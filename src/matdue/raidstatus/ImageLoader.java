package matdue.raidstatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import matdue.raidstatus.helper.HttpClientHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class ImageLoader implements Runnable {

	public static final int OK = 2000;
	
	private final static String USER_AGENT = "DKP Raidstatus";
	
	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	
	private String url;
	private Handler handler;
	private File cacheDir;
	private int widgetID;
	
	public ImageLoader(String url, Handler handler, File cacheDir, int widgetID) {
		this.url = url;
		this.handler = handler;
		this.cacheDir = cacheDir;
		this.widgetID = widgetID;
	}
	
	@Override
	public void run() {
		// Hash tag of URL
		String md5HashTag;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(url.getBytes());
			BigInteger md5 = new BigInteger(1, digest.digest());
			md5HashTag = String.format("%1$032X", md5);
		} catch (NoSuchAlgorithmException e) {
			// Fallback String.hashCode()
			md5HashTag = Integer.toHexString(url.hashCode());
		}
		
		// Check if image is in memory cache
		SoftReference<Bitmap> cachedImageReference = imageCache.get(md5HashTag);
		if (cachedImageReference != null) {
			Bitmap cachedImage = cachedImageReference.get();
			if (cachedImage != null) {
				Log.v("ImageLoader", "In memory cache: " + url);
				handler.obtainMessage(OK, widgetID, 0, cachedImage).sendToTarget();
				return;
			}
		}
		
		// Check if image is on disk
		File cachedImageFile = new File(cacheDir, md5HashTag);
		if (cachedImageFile.exists()) {
			Bitmap cachedImage = BitmapFactory.decodeFile(cachedImageFile.getAbsolutePath());
			if (cachedImage != null) {
				// Cache image in memory
				imageCache.put(md5HashTag, new SoftReference<Bitmap>(cachedImage));
				
				Log.v("ImageLoader", "In file cache: " + url);
				handler.obtainMessage(OK, widgetID, 0, cachedImage).sendToTarget();
				return;
			}
		}
		
		// Download image
		Log.v("ImageLoader", "Downloading " + url);
		HttpClient httpClient = HttpClientHelper.newInstance(USER_AGENT);
		try {
			HttpGet httpRequest = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
			if (responseStatusCode == 200) {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
		        	InputStream stream = entity.getContent();
		        	
					// Cache image on disk
		        	FileOutputStream cacheFileStream = new FileOutputStream(cachedImageFile);
		        	copyStream(stream, cacheFileStream);
		        	cacheFileStream.flush();
		        	cacheFileStream.close();
					stream.close();
					
					// Decode bitmap from file
		        	Bitmap cachedImage = BitmapFactory.decodeFile(cachedImageFile.getAbsolutePath());
		        	if (cachedImage != null) {
		        		// Cache image in memory
		        		imageCache.put(md5HashTag, new SoftReference<Bitmap>(cachedImage));
		        		
		        		handler.obtainMessage(OK, widgetID, 0, cachedImage).sendToTarget();
						return;
		        	}
				} 
			} 
		} catch (IOException e) {
			// Ignore any error
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		// This code is reached if we weren't able to decode the bitmap.
		// To be sure to reload the image next time, delete all caches for that image.
		imageCache.remove(md5HashTag);
		cachedImageFile.delete();
	}

	private void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) > 0) {
			out.write(buffer, 0, bytesRead);
		}
	}
	
}

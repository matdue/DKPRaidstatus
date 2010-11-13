package matdue.raidstatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import matdue.raidstatus.helper.HttpClientHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.util.Log;

public class ConcurrentUpdater implements Runnable {

	public final static int OK = 1000;
	public final static int NETWORK_ERROR = 1001;
	public final static int NO_DATA = 1002;
	
	private String url;
	private Handler handler;
	
	private final static String USER_AGENT = "DKP Raidstatus";

	public ConcurrentUpdater(String url, Handler handler) {
		this.url = url;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		HttpClient httpClient = HttpClientHelper.newInstance(USER_AGENT);
		try {
			HttpGet httpRequest = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
			if (responseStatusCode == 200) {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					String contentCharset = EntityUtils.getContentCharSet(entity);
		        	if (contentCharset == null) {
		        		contentCharset = "ISO-8859-1";
		        	}
		        	
		        	InputStream stream = entity.getContent();
		        	BufferedReader reader = new BufferedReader(new InputStreamReader(stream, contentCharset));
		        	int linesRead = 0;
					while ((reader.readLine()) != null) {
						++linesRead;
					}
					Log.v("xxx", Integer.toString(linesRead));
					stream.close();
				} else {
					handler.sendEmptyMessage(NO_DATA);
					return;
				}
			} else {
				handler.sendEmptyMessage(NO_DATA);
				return;
			}
		} catch (IOException e) {
			Log.v("xxx", "update failed", e);
			handler.obtainMessage(NETWORK_ERROR, e.getMessage()).sendToTarget();
			return;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		handler.sendEmptyMessage(OK);
	}

}

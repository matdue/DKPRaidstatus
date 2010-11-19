package matdue.raidstatus;

import java.text.NumberFormat;

import matdue.raidstatus.data.Raid;
import matdue.raidstatus.data.RaidMember;
import matdue.raidstatus.data.database.RaidDatabase;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ConcurrentUpdater.OK:
				setProgressBarIndeterminateVisibility(false);
				updateView();
				break;
				
			case ConcurrentUpdater.NETWORK_ERROR:
				setProgressBarIndeterminateVisibility(false);
				String message = getResources().getString(R.string.message_dkp_network_error, msg.obj);
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				break;
			
			case ConcurrentUpdater.NO_DATA:
				setProgressBarIndeterminateVisibility(false);
				Toast.makeText(MainActivity.this, R.string.message_dkp_no_data, Toast.LENGTH_LONG).show();
				break;
				
			case ImageLoader.OK:
				ImageView image = (ImageView) findViewById(msg.arg1);
	    		image.setImageBitmap((Bitmap) msg.obj);
	    		break;
			}
		}
	};
	
	private boolean preferencesHaveChanged = false;
	private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			preferencesHaveChanged = true;
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSharedPreferences().registerOnSharedPreferenceChangeListener(preferencesListener);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        updateView();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	if (preferencesHaveChanged) {
    		preferencesHaveChanged = false;
    		updateRaidData();
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferencesListener);
    }
    
    private void updateView() {
    	RaidDatabase db = new RaidDatabase(this);
    	Raid nextRaid = db.loadRaid();
    	if (nextRaid != null) {
    		findViewById(R.id.NoDataLayout).setVisibility(View.GONE);
    		findViewById(R.id.RaidLayout).setVisibility(View.VISIBLE);
    		
    		// Raid information
    		String message = getResources().getString(R.string.main_raid_title, nextRaid.name);
    		TextView view = (TextView) findViewById(R.id.main_raid_title);
    		view.setText(message);
    		
    		message = getResources().getString(R.string.main_raid_datetime, 
    				DateFormat.format(getResources().getString(R.string.main_raid_date_format), nextRaid.start),
    				DateFormat.format(getResources().getString(R.string.main_raid_time_format), nextRaid.start));
    		view = (TextView) findViewById(R.id.main_raid_datetime);
    		view.setText(message);
    		
    		String url = getSharedPreferences().getString("url", "");
    		if (!url.endsWith("/")) {
    			url = url + "/";
    		}
    		url = url + "games/WoW/events/" + nextRaid.icon;
    		ImageLoader imageLoader = new ImageLoader(url, handler, getCacheDir(), R.id.RaidLogo);
    		new Thread(imageLoader).run();
    		
    		// Player information
    		String playerName = getSharedPreferences().getString("charname", null);
    		if (playerName != null) {
				// Lookup player in members
				for (RaidMember member : nextRaid.raidMembers) {
					if (playerName.equals(member.player.name)) {
						// Got it, display information
						String subscription = getResources().getStringArray(R.array.subscription)[member.subscribed];
						
						if (member.role == null) {
							message = getResources().getString(R.string.main_raid_player, 
									playerName, subscription);
						} else {
							String role = "???";
							if ("tank".equals(member.role)) {
								role = getResources().getStringArray(R.array.role)[0];
							} else if ("healer".equals(member.role)) {
								role = getResources().getStringArray(R.array.role)[1];
							} else if ("melee".equals(member.role)) {
								role = getResources().getStringArray(R.array.role)[2];
							} else if ("range".equals(member.role)) {
								role = getResources().getStringArray(R.array.role)[3];
							}
							
							message = getResources().getString(R.string.main_raid_player_role, 
									playerName, subscription, role);
						}
						view = (TextView) findViewById(R.id.main_raid_player);
						view.setText(message);
						view.setVisibility(View.VISIBLE);
						
						// Note
						if (member.note != null && member.note.length() != 0) {
							message = getResources().getString(R.string.main_raid_player_note, member.note);
							view = (TextView) findViewById(R.id.main_raid_player_note);
							view.setText(message);
							view.setVisibility(View.VISIBLE);
						}
						
						// DKP
						message = getResources().getString(R.string.main_raid_player_dkp, 
								NumberFormat.getInstance().format(member.player.currentDkp));
						view = (TextView) findViewById(R.id.main_raid_player_dkp);
						view.setText(message);
						view.setVisibility(View.VISIBLE);
						
						break;
					}
				}
    		}
    	} else {
    		findViewById(R.id.RaidLayout).setVisibility(View.GONE);
    		findViewById(R.id.NoDataLayout).setVisibility(View.VISIBLE);
    	}
    	db.close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_main, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_main_preferences:
    		startActivity(new Intent(this, PreferencesActivity.class));
    		return true;
    		
    	case R.id.menu_main_update:
    		updateRaidData();
    		return true;
    		
    	default:
        	return super.onOptionsItemSelected(item);
    	}
    }
    
    private void updateRaidData() {
		String url = getSharedPreferences().getString("url", null);
		if (url == null) {
			Toast.makeText(this, R.string.message_not_configured_yet, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!url.endsWith("/")) {
			url = url + "/";
		}
		url = url + "getdkp.php";

        setProgressBarIndeterminateVisibility(true);
        RaidDatabase db = new RaidDatabase(this);
        ConcurrentUpdater updater = new ConcurrentUpdater(url, handler, db);
        new Thread(updater).start();
    }
    
    private SharedPreferences getSharedPreferences() {
    	return getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
    }
}
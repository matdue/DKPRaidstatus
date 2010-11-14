package matdue.raidstatus;

import matdue.raidstatus.data.Raid;
import matdue.raidstatus.data.database.RaidDatabase;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ConcurrentUpdater.OK:
				setProgressBarIndeterminateVisibility(false);
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
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        updateView();
    }
    
    private void updateView() {
    	RaidDatabase db = new RaidDatabase(this);
    	Raid nextRaid = db.loadRaid();
    	if (nextRaid != null) {
    		Toast.makeText(this, nextRaid.name, Toast.LENGTH_SHORT).show();
    	}
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
		SharedPreferences preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
		String url = preferences.getString("url", null);
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
}
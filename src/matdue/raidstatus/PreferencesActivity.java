package matdue.raidstatus;

import matdue.raidstatus.data.database.RaidDatabase;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);

		// Get list of players from database
		RaidDatabase db = new RaidDatabase(this);
		String[] playerNames = db.loadPlayerNames();
		db.close();
		
		ListPreference charnamePreference = (ListPreference) findPreference("charname");
		charnamePreference.setEntries(playerNames);
		charnamePreference.setEntryValues(playerNames);
		charnamePreference.setEnabled(playerNames.length != 0);
	}
	
	public static final SharedPreferences getApplicationPreferences(final ContextWrapper ctx) {
		return ctx.getSharedPreferences(ctx.getPackageName() + "_preferences", MODE_PRIVATE);
	}

}

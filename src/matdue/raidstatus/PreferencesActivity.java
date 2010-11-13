package matdue.raidstatus;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		Preference charnamePreference = findPreference("charname");
		charnamePreference.setEnabled(false);
	}

}

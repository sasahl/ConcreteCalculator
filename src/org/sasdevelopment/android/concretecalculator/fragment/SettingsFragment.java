package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This fragment displays the settings menu
 */

//import org.sasdevelopment.android.concretecalculator.*;
import org.sasdevelopment.android.concretecalculator.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	public static final String STANDARD_CONCRETE_PRICE_PREFS_KEY = "prefs_standard_price";
	//public static final String TAG = "settingsFragment";
	
	private EditTextPreference mStandardConcretePricePref;
	private String standardConcretePriceStr;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs_slab);
		
		//Get the preference values from sharedPreferences
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		standardConcretePriceStr = sharedPrefs.getString(SettingsFragment.STANDARD_CONCRETE_PRICE_PREFS_KEY, "0");
		
		
		//Set the summary of the standard concrete price edittextPreference widget
		mStandardConcretePricePref = (EditTextPreference)findPreference(STANDARD_CONCRETE_PRICE_PREFS_KEY);
		mStandardConcretePricePref.setSummary("$" + standardConcretePriceStr);
		
			
	}


	//Listen for changes to the shared preferences values
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		//Reset the summary of the concrete price EditTextPreferences when the user inputs a new value
		standardConcretePriceStr = sharedPreferences.getString(STANDARD_CONCRETE_PRICE_PREFS_KEY, "0");
		mStandardConcretePricePref.setSummary("$" + standardConcretePriceStr);
		
		
	}
	
	//Register the shared preference listener for proper life cycle management
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	//Unregister the shared preference listener
	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}		
}	



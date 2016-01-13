package org.sasdevelopment.android.concretecalculator.activity;
/*
 * This activity will host the settings fragment that displays the settings menu
 */

import org.sasdevelopment.android.concretecalculator.fragment.SettingsFragment;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Display the settings fragment
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment()).commit();
		
	
	}
}

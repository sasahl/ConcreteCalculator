package org.sasdevelopment.android.concretecalculator.activity;

import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.fragment.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BackfillActivity extends ActionBarActivity {
	
	public static final String FRAGMENT_TO_HOST = "FragmentToHost";
	public static final int DIRT = 0;
	public static final int BACKFILL = 1;
	public static final int HAUL_OFF = 2;
	
	private int mFragmentChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_fragment);
		getWindow().setBackgroundDrawable(null); //helps prevent overdraw
		
		//Force portrait on small devices
		if(getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		mFragmentChoice = getIntent().getIntExtra(FRAGMENT_TO_HOST, 0);
		
		switch(mFragmentChoice) {
		case DIRT: {
							
							
							if(fragment == null) {
								fragment = new DirtExcavationFragment();
								fm.beginTransaction().add(R.id.fragmentContainer, fragment)
								.commit();
							}
							break;
						}
		case BACKFILL: {
							
							
							if(fragment == null) {
								fragment = new BackfillFragment();
								fm.beginTransaction().add(R.id.fragmentContainer, fragment)
								.commit();
							}
							break;
						}
		case HAUL_OFF: {
							
							
							if(fragment == null) {
								fragment = new ConcreteHaulOffFragment();
								fm.beginTransaction().add(R.id.fragmentContainer, fragment)
								.commit();
							}
							break;
						 }
		
		
		}
		//display the "up caret" by the app icon
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/*
	 * Menu related methods
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//Start the settings activity when settings is clicked
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		if(id == R.id.action_app_info) {
        	//Start the app info activity when app info is clicked
        	Intent i = new Intent(this, AppInfoActivity.class);
        	startActivity(i);
        	return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

}

package org.sasdevelopment.android.concretecalculator.activity;

import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.fragment.CMUCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.CMUResultFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class CMUActivity extends ActionBarActivity implements CMUCalculatorFragment.CallbacksBlock ,
			CMUResultFragment.CallbacksBlockResult {
	
	private LinearLayout mLayout;
	
	private int mNumBlocks;
	private int mNumBagsOfMortar;
	private float mConcreteFill;
	private int mRebarLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculations);
		getWindow().setBackgroundDrawable(null);
		
		//Force portrait on small devices
		if(getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		mLayout = (LinearLayout)findViewById(R.id.activity_layout);
		mLayout.setBackgroundResource(R.drawable.wall_background_yellow);
		
		FragmentManager fm = getFragmentManager(); 
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_top);
		
		if(fragment == null) {
			fragment = new CMUCalculatorFragment();
			fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
			.commit();
		}
		
		//display the "up caret" by the app icon
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}//End onCreate()
	
	
	/*
	 * This method is from the CallbacksBlock interface. It must be defined in this activity because it implements the 
	 *   CallbacksBlock interface.
	 *   This method is adding the 'CalculationResultFragment' to this activity when the 'Calculation' button
	 *   is clicked in CMUCalculatorFragment
	 *   
	 *   When this method is called in the calculation button listener of the CMUCalculatorFragment. it delegates the implementation
	 *   of that method for here, so the hosting activity can perform the communication between the 2 fragments. 
	 *   
	 *   @param: numBlocks is the amount of blocks needed for the wall
	 *   @param: numMortarBags is the number of 80# bags of mortar needed for the blocks
	 *   @param: fillCubicYards is the amount of concrete needed to fill in the blocks if so desired
	 *   @param: rebar is the total length of rebar needed for the wall. this can be 0 if not selected in the CMUCalculatorFragment
	 */
	@Override
	public void onCalculationClick(int numBlocks, int numMortarBags,
			float fillCubicYards, int rebar) {
		
		mNumBlocks = numBlocks;
		mNumBagsOfMortar = numMortarBags;
		mConcreteFill = fillCubicYards;
		mRebarLength = rebar;
		
		
		
		// The FragmentManager is in charge of adding, removing, or replacing
		// fragments in the Activity.
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_bottom);
			
		// Add an instance of CalculationResultFragment to this Activity
		if (fragment == null) {
			fragment = new CMUResultFragment();
			fm.beginTransaction().add(R.id.fragmentContainer_bottom, fragment)
					.commit();
		}
		// Update the fragment if the calculation button is clicked after the
		// CalculationResultFragment has
		// already been added to the activity once. This is if the user
		// re-enters values in the input boxes.
		else {
			fragment = new CMUResultFragment();
			fm.beginTransaction()
					.replace(R.id.fragmentContainer_bottom, fragment).commit();
		}
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


	@Override
	public int getBlocks() {
		return mNumBlocks;
	}


	@Override
	public int getBagsOfMortar() {
		return mNumBagsOfMortar;
	}


	@Override
	public float getConcreteFill() {
		return mConcreteFill;
	}


	@Override
	public int getRebar() {
		return mRebarLength;
	}


}

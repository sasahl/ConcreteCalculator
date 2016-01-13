package org.sasdevelopment.android.concretecalculator.activity;

/*
 * This Activity will host SlabCalculatorFragment and CalculationsResultFragment
 * CalculationsResultFragment will be added to this activity when the user clicks the "calculation"
 * button in the slabCalculatorfragment.
 * 
 * It implements the SlabcalculatorFragment.Callbacks and CalculationResultFragment.CallbacksResult interfaces
 * It must implement those in order for the 2 fragments to communicate independently together.
 */

import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.CalculationsResultList;
import org.sasdevelopment.android.concretecalculator.ConcreteCalculationsResult;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;
import org.sasdevelopment.android.concretecalculator.fragment.CalculationResultFragment;
import org.sasdevelopment.android.concretecalculator.fragment.SlabCalculatorFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class SlabCalculatorActivity extends ActionBarActivity 
		implements SlabCalculatorFragment.Callbacks, CalculationResultFragment.CallbacksResult {
	
	//private static final String TAG = "slabCalculatorActivity";
	private static final String SQUARE_FEET_KEY = "totalSquareFeet";
	
	private int mTotalSquareFeet;
	private int mThickness;
	private LinearLayout layout;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(SQUARE_FEET_KEY, mTotalSquareFeet);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!AddCalculationDialog.isOpen()) {
		   //Clear out any added up results if there are any.
		   CalculationsResultList.get(this).clear();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculations);
		getWindow().setBackgroundDrawable(null);
		
		//Force portrait on small devices
		if(getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		//Set the background layout of this activity. This activity is hosting 2 fragments and have a border around both fragments
		//	therefore I have set the background in this activity instead of the fragments.
		//The reason I did not do it in xml is that the xml layout will be used by several activities, and each will have a different color border.
		layout = (LinearLayout)findViewById(R.id.activity_layout);
		layout.setBackgroundResource(R.drawable.slab_background_blue);
		
		
		if(savedInstanceState != null) {
			mTotalSquareFeet = savedInstanceState.getInt(SQUARE_FEET_KEY);
		}
		

		// The FragmentManager is in charge of adding, removing, or replacing
		// fragments in the Activity.
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_top);

		// Add an instance of SlabCalculatorFragment to this Activity
		if (fragment == null) {
			fragment = new SlabCalculatorFragment();
			fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
					.commit();
		}
		
		//display the "up caret" by the app icon
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}//End onCreate

	/*
	 * This method is from the Callback interface. It must be defined in this activity because it implements the 
	 *   SlabCalculatorFragment.Callbacks interface.
	 *   This method is supposed to add the 'CalculationResultFragment' to this activity when the 'Calculation' button
	 *   is clicked in SlabCalculatorFragment.
	 *   
	 *   When this method is called in the calculation button listener of the SlabCalculatorFragment. it delegates the implementation
	 *   of that method for here, so the hosting activity can perform the communication between 2 fragments. 
	 */
	@Override
	public void onCalculationClick(int totalSquareFeet, double width, double length, int thickness) {
		
		//Save the transferred data in the instance fields
		if(totalSquareFeet != 0) {
			mTotalSquareFeet = totalSquareFeet;
		}
		else {
			mTotalSquareFeet = (int)(Math.ceil(width * length));
		}
		mThickness = thickness;
		
		// The FragmentManager is in charge of adding, removing, or replacing
		// fragments in the Activity.
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_bottom);
		

		// Add an instance of CalculationResultFragment to this Activity
		if (fragment == null) {
			fragment = new CalculationResultFragment();
			fm.beginTransaction().add(R.id.fragmentContainer_bottom, fragment)
					.commit();
		}
		//Update the fragment if the calculation button is clicked after the CalculationResultFragment has 
		//already been added to the activity once. This is if the user re-enters values in the input boxes.
		else {
			fragment = new CalculationResultFragment();
			fm.beginTransaction().replace(R.id.fragmentContainer_bottom, fragment)
			.commit();
		}
		
		
	}
	
	
	/*
	 * This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
	 * it implements the interface
	 * This method is supposed to make the different calculations needed to fill out the result fields of 'CalculationResultFragment',
	 * 	and return a 'ConcreteCalculationResults' object, that has all the information that 'CalculationResultFragment' needs to fill out it's fields.
	 * 
	 */
	@Override
	public ConcreteCalculationsResult updateResult() {
		
		//Create a ConcreteCalculationsResult object that can store the different values that CalculationResultFragment needs to set it's fields.
		ConcreteCalculationsResult ccr = new ConcreteCalculationsResult();
		double cubicYards = Calculations.concreteSlabCubicYards(mTotalSquareFeet, mThickness);
		
		//Set the ccr's instance fields
		ccr.setmCubicYards(cubicYards);
		ccr.setM80PoundBags(Calculations.calculate80PoundBags(cubicYards));
		ccr.setM60PoundBags(Calculations.calculate60PoundBags(cubicYards));
		ccr.setM40PoundBags(Calculations.calculate40PoundBags(cubicYards));
		
		return ccr;
	}
	
	/*
	 ** This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
	 * it implements the interface.
	 *	It is called when updating rebar calculations from the spinner.
	 */
	@Override
	public int updateRebarLenght(int rebarSpacing) {
		if(CalculationsResultList.get(this).isEmpty()) {
			return Calculations.calculateRebar(mTotalSquareFeet, rebarSpacing);
		}else {
			return Calculations.calculateRebar(mTotalSquareFeet, rebarSpacing) + CalculationsResultList.get(this).getCumulatedResult().getRebarLength();
		}
	}

	 /* This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
	 * it implements the interface
	 * It is called when updating gravel calculations from the spinner
	 */
	@Override
	public double updateGravelDepth(int depth) {
		if(CalculationsResultList.get(this).isEmpty()) {
			return Calculations.calculateGravel(mTotalSquareFeet, depth);
		}else {
			return Calculations.calculateGravel(mTotalSquareFeet, depth) + CalculationsResultList.get(this).getCumulatedResult().getmGravelTons();
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

	
	
}

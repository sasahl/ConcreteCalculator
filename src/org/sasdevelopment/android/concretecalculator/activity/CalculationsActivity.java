package org.sasdevelopment.android.concretecalculator.activity;

/*
 * This activity will host most of the concrete calculations fragments, and the CalculationResultFragment. It will handle the 
 * communication between the 2 fragments
 */


import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.CalculationsResultList;
import org.sasdevelopment.android.concretecalculator.ConcreteCalculationsResult;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;
import org.sasdevelopment.android.concretecalculator.fragment.CalculationResultFragment;
import org.sasdevelopment.android.concretecalculator.fragment.CalculationResultFragment.CallbacksResult;
import org.sasdevelopment.android.concretecalculator.fragment.ColumnCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.FootingCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.StepsPlatformCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.StepsSlopeCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.WallCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.interfaces.Callback;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class CalculationsActivity extends ActionBarActivity 
							implements  CallbacksResult, Callback{
	//private static final String TAG = "calculationsActivity";
	
	//These are used in onSaveInstanceState()
	private static final String REBAR_INPUT = "rebarInput";
	private static final String HAS_GRAVEL_CALC = "hasGravelCalc";
	private static final String SQUARE_FEET = "squareFeet";
	
	//These are used to determine what fragments to host.
	public static final String FRAGMENT_TO_HOST = "FragmentToHost";
	public static final int WALL = 0;
	public static final int FOOTING = 1;
	public static final int COLUMN = 2;
	public static final int STEP_SLOPE = 3;
	public static final int STEP_PLATFORM = 4;
	
	private LinearLayout mLayout;
	private double mCubicYards;
	private int mRebarLength;
	private int mTotalSquareFeet;
	private boolean mHasGravelCalculation;
	private int mFragmentChoice;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(REBAR_INPUT, mRebarLength);
		outState.putBoolean(HAS_GRAVEL_CALC, mHasGravelCalculation);
		outState.putInt(SQUARE_FEET, mTotalSquareFeet);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(!AddCalculationDialog.isOpen()) {
			//Clear out any added up results if there are any and the AddCalculationDialog is not open.
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
		
		if(savedInstanceState != null) {
			mRebarLength = savedInstanceState.getInt(REBAR_INPUT);
			mHasGravelCalculation = savedInstanceState.getBoolean(HAS_GRAVEL_CALC);
			mTotalSquareFeet = savedInstanceState.getInt(SQUARE_FEET);
		}
		
		/* The FragmentManager is in charge of adding, removing, or replacing
		* fragments in the Activity.
		* 
		* The reason I check the fragment for null in the switch statement, is because the call to onCreate()
		* could be in response to CalculationsActivity being 'recreated' after being destroyed on rotation change.
		* When an activity is destroyed, its FragmentManager saves out its list of fragments. When the activity is
		* recreated, the new FragmentManager retrieves the list and recreates the listed fragments to make
		* everything as it was before, if there are any. Therefore if 'fragment' is not null, then it uses the old fragment with all its
		* values intact, but if fragment is null, then we need to create a new one.
		* The fragment manager I believe is the reason why values in Edittext boxes can stay intact on rotation change.
		*/
		FragmentManager fm = getFragmentManager();
		//This call will return a fragment if a fragment is in the list with this container view ID.
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_top); 
		
		//Get the intent extra in order to find out which fragment to host. 0 is the default value.
		mFragmentChoice = getIntent().getIntExtra(FRAGMENT_TO_HOST, 0);
		
		
		switch(mFragmentChoice) {
		case WALL: {
							mLayout = (LinearLayout)findViewById(R.id.activity_layout);
							mLayout.setBackgroundResource(R.drawable.wall_background_yellow);
							
							if(fragment == null) {
								fragment = new WallCalculatorFragment();
								fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
								.commit();
							}
							break;
						}
		case FOOTING: {
							mLayout = (LinearLayout)findViewById(R.id.activity_layout);
							mLayout.setBackgroundResource(R.drawable.footing_background_red);
							
							if(fragment == null) {
								fragment = new FootingCalculatorFragment();
								fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
								.commit();
							}
							break;
						}
		case COLUMN: {
							mLayout = (LinearLayout)findViewById(R.id.activity_layout);
							mLayout.setBackgroundResource(R.drawable.column_background_green);
							
							if(fragment == null) {
								fragment = new ColumnCalculatorFragment();
								fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
								.commit();
							}
							break;
						 }
		case STEP_SLOPE: {
							mLayout = (LinearLayout)findViewById(R.id.activity_layout);
							mLayout.setBackgroundResource(R.drawable.background_orange_border);
							
							if(fragment == null) {
								fragment = new StepsSlopeCalculatorFragment();
								fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
								.commit();
							}
							break;
						 }
		case STEP_PLATFORM: {
							mLayout = (LinearLayout)findViewById(R.id.activity_layout);
							mLayout.setBackgroundResource(R.drawable.slab_background_blue);
							
							if(fragment == null) {
								fragment = new StepsPlatformCalculatorFragment();
								fm.beginTransaction().add(R.id.fragmentContainer_top, fragment)
								.commit();
							}
							break;
						 }
		
		}//End switch
		
		
		//display the "up caret" by the app icon
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	//Inflate the action bar menu. In my case I have only 3 items in the overflow menu. They are "settings" and "app info"
		//	and the "home" caret by the app name.
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
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
				// Start the settings activity when settings is clicked
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				return true;
			}
			if (id == R.id.action_app_info) {
				// Start the app info activity when app info is clicked
				Intent i = new Intent(this, AppInfoActivity.class);
				startActivity(i);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		/*
		 * This method is from the Callback interface. It must be defined in this activity because it implements the 
		 *   Callback interface.
		 *   This method is adding the 'CalculationResultFragment' to this activity when the 'Calculation' button
		 *   is clicked in StepsCalculatorFragment.
		 *   
		 *   When this method is called in the calculation button listener of the StepsCalculatorFragment. it delegates the implementation
		 *   of that method for here, so the hosting activity can perform the communication between the 2 fragments. 
		 *   
		 *   @param: cubicYards is the calculated cubic yards needed
		 *   @param: rebarLengthInFeet is the total calculated rebars needed.
		 *   @param: totalSquareFeet is used to calculate the amount of gravel needed.
		 *   @param: hasGravelCalculation returns a boolean. if true, then gravel calculations are included, otherwise they are not.
		 */
		@Override
		public void onCalculationClick(double cubicYards,int rebarLengthInFeet, int totalSquareFeet,
				boolean hasGravelCalculation) {
			
			mCubicYards = cubicYards;
			mRebarLength = rebarLengthInFeet;
			mTotalSquareFeet = totalSquareFeet;
			mHasGravelCalculation = hasGravelCalculation;
			
			
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
			// Update the fragment if the calculation button is clicked after the
			// CalculationResultFragment has
			// already been added to the activity once. This is if the user
			// re-enters values in the input boxes.
			else {
				fragment = new CalculationResultFragment();
				fm.beginTransaction()
						.replace(R.id.fragmentContainer_bottom, fragment).commit();
			}
		}


		/*
		 * This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
		 * it implements the interface
		 * This method is doing the different calculations needed to fill out the concrete related result fields of 'CalculationResultFragment',
		 * 	and return a 'ConcreteCalculationResults' object, that has all the information that 'CalculationResultFragment' needs to fill out it's fields.
		 * 
		 */
		@Override
		public ConcreteCalculationsResult updateResult() {
			//Create a ConcreteCalculationsResult object that can store the different values that CalculationResultFragment needs to set it's fields.
			ConcreteCalculationsResult ccr = new ConcreteCalculationsResult();
			
			//Set the ccr's instance fields
			ccr.setmCubicYards(mCubicYards);
			ccr.setM80PoundBags(Calculations.calculate80PoundBags(mCubicYards));
			ccr.setM60PoundBags(Calculations.calculate60PoundBags(mCubicYards));
			ccr.setM40PoundBags(Calculations.calculate40PoundBags(mCubicYards));
			
			return ccr;
		}

		
		/*
		 ** This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
		 * it implements the interface.
		 * 	It is called when updating rebar calculations. In the step calculations the spinner is not used, so the parameter is not used here either.
		 * If the rebar spacing is 0, then this method will return 0, and therefore not be shown in the result fragment.
		 */
		@Override
		public int updateRebarLenght(int rebarSpacing) {
			if(CalculationsResultList.get(this).isEmpty()) {
				return mRebarLength;
			}else {
				return mRebarLength + CalculationsResultList.get(this).getCumulatedResult().getRebarLength();
			}
		}

		
		 /* This method is from 'CalculationResultFragment.Callbacks interface. It must be defined in this activity because
		 * it implements the interface
		 * 
		 * It will return 0 if mHasGravelCalculation is false. That way if 0 is returned, then the result fragment can hide the gravel calculations.
		 */
		@Override
		public double updateGravelDepth(int depth) {
			if(CalculationsResultList.get(this).isEmpty()) {
				if (mHasGravelCalculation) {
					return Calculations.calculateGravel(mTotalSquareFeet, depth);
				}else
					return 0;
			}else if(mHasGravelCalculation) {
				return Calculations.calculateGravel(mTotalSquareFeet, depth) + CalculationsResultList.get(this).getCumulatedResult().getmGravelTons();
			}else {
				return CalculationsResultList.get(this).getCumulatedResult().getmGravelTons();
			}
		}

	
	

}

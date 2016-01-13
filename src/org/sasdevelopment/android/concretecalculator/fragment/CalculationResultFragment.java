package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This fragment is built so it can be hosted by several different Activities.
 * It displays the result of the different calculations being done as a result
 * 	of the user input in the different concrete Calculation Fragments.
 * 
 * It defines an Interface "CallBacksResult" that must be implemented by the hosting Activity.
 */

import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.CalculationsResultList;
import org.sasdevelopment.android.concretecalculator.ConcreteCalculationsResult;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.activity.SlabCalculatorActivity;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;
import org.sasdevelopment.android.concretecalculator.dialogs.SendResultDialog;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CalculationResultFragment extends Fragment {
	
	//private static final String TAG = "CalculationResultFragment";
	
	//Define constants to represent the preference keys needed to retrieve the preference values
	public static final String REBAR_GRIDSIZE_PREFS_KEY = "prefs_rebar_gridsize";
	public static final String GRAVEL_DEPTH_PREFS_KEY = "prefs_gravel_depth";
	private static final String BAG_CALC_VISIBLE_PREFS_KEY = "prefs_bag_calculations";
	private static final String REBAR_CALC_VISIBLE_PREFS_KEY = "prefs_rebar_calculation";
	private static final String GRAVEL_CALC_VISIBLE_PREFS_KEY = "prefs_gravel_calculation";
	private static final String COST_CALC_VISIBLE_PREFS_KEY = "prefs_cost_calculation";
	
	
	//This constant is used to save the ccr object in the bundle
	private static final String CCR = "ccr";
	private static final String REBAR_SPINNER_SELECTION = "spinnerSelection";
	private static final String GRAVEL_SPINNER_SELECTION = "gravelSelection";
	
	private Spinner mRebarSpinner;
	private Spinner mGravelSpinner;
	private TextView mCubicYardsTextView;
	private TextView mCostTextView;
	private TextView m80PoundBagsTextView;
	private TextView m60PoundBagsTextView;
	private TextView m40PoundBagsTextView;
	private TextView mRebarTextView;
	private TextView mGravelTextView;
	private CallbacksResult mCallbacks;
	private ConcreteCalculationsResult ccr; //This object holds the values needed to fill out the different text fields.
	private int mRebarSpacing;
	private int mRebarSpinnerSelection = -1;
	private int mGravelSpinnerSelection = -1;
	private int mGravelDepth;
	private SharedPreferences sharedPrefs;
	private boolean hasBagCalculationsVisible;
	private boolean hasGravelCalculationVisible;
	private boolean hasRebarCalculationVisible;
	private boolean hasCostCalculationVisible;
	private String mStandardConcretePriceStr;
	private double mStandardConcretePrice;
	private RelativeLayout.LayoutParams params; //Used to dynamically adjust the layout
	private Button mSendButton;
	private Button mAddCalculationButton;
	
	//These are used just for visibility setting purposes
	private TextView mRebarLabel;
	private TextView mRebarLinearFeetLabel;
	private TextView mGravelLabel;
	private TextView mGravelTonsLabel;
	private TextView m80PoundBagLabel;
	private TextView m60PoundBagLabel;
	private TextView m40PoundBagLabel;
	private TextView mCostLabel;
	private View mDividerLine;
	
	/**
	 * Required interface for hosting activities
	 */
	public interface CallbacksResult {
		
		/*
		 * These methods needs to be defined by the hosting activity
		 * The purpose of these methods is to update and set the fields in the 'CalculationResultFragment'
		 */
		public ConcreteCalculationsResult updateResult();
		public int updateRebarLenght(int rebarSpacing);
		public double updateGravelDepth(int depth);
	}
	
	// This method is run when the fragment is first attached to the hosting
	// activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// The hosting activity is cast to Callbacks, and is assigned to
		// mCallbacks
		// Note ** If the activity doesn't implement the Callbacks interface an
		// exception will be thrown
		mCallbacks = (CallbacksResult) activity;
	}

	// This method is run when the fragment is detached from the hosting
	// activity
	@Override
	public void onDetach() {
		super.onDetach();
		// mCallbacks is assigned back to null when the fragment is detached
		// from the hosting activity
		mCallbacks = null;
	}
	
	//Save the ccr object to the bundle
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CCR, ccr);
		outState.putInt(REBAR_SPINNER_SELECTION, mRebarSpinner.getSelectedItemPosition());
		outState.putInt(GRAVEL_SPINNER_SELECTION, mGravelSpinner.getSelectedItemPosition());
	}
	
	

	/*
	 * Make settings changes here so the changes will be immediately visible to the user, if the settings are changed while
	 * 	this fragment is used/visible. onCreateView() does not get called when the user returns from the settings menu.
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		Activity hostActivity = getActivity();
		
		// Get visibility user choices from preferences
		hasBagCalculationsVisible = sharedPrefs.getBoolean(BAG_CALC_VISIBLE_PREFS_KEY, true);
		hasCostCalculationVisible = sharedPrefs.getBoolean(COST_CALC_VISIBLE_PREFS_KEY, true);
		
		//These 2 are always set to true, except for the slab calculator, because the user can decide whether to include them or not directly 
		//	from the wall, and footing fragments. So if they were unchecked in settings, then they would not be visible in the wall and footing 
		//	calculation result even if the user wanted them to. The user would have to go to the settings menu to enable them first, and that 
		//	would not be an ideal user experience.
		hasGravelCalculationVisible = true; 
		hasRebarCalculationVisible = true;
		
		
		
		//The preference values for these 2 are only relevant for the slab calculator
		if(hostActivity instanceof SlabCalculatorActivity) {
			hasGravelCalculationVisible = sharedPrefs.getBoolean(GRAVEL_CALC_VISIBLE_PREFS_KEY, true);
			hasRebarCalculationVisible = sharedPrefs.getBoolean(REBAR_CALC_VISIBLE_PREFS_KEY, true);
			
			mRebarSpacing = getRebarSpacingFromPreferences(); 
			updateRebarSpinnerSelection(); 
		}
		
		mGravelDepth = getGravelDepthFromPreferences();
		updateGravelSpinnerSelection();
		
		
		// Show or hide the rebar calculations based on the users choice in settings, or if the rebar length is 0.
		// In the slab calculator the rebar calculations are always shown except if the user has unchecked it in the settings menu.
		// For wall and footing the rebar calculations are not shown if the returned rebar length is 0. because this indicates that the 
		//	user has not filled out the rebar spacing input boxes, and therefore are not interested in getting that information.
		if (!hasRebarCalculationVisible || ccr.getRebarLength() == 0) {
			mRebarLabel.setVisibility(View.GONE);
			mRebarLinearFeetLabel.setVisibility(View.GONE);
			mRebarTextView.setVisibility(View.GONE);
			mRebarSpinner.setVisibility(View.GONE);
		} else {
			mRebarLabel.setVisibility(View.VISIBLE);
			mRebarLinearFeetLabel.setVisibility(View.VISIBLE);
			mRebarTextView.setVisibility(View.VISIBLE);
			if(hostActivity instanceof SlabCalculatorActivity) //The rebar spinner is only used in the slab calculations.
				mRebarSpinner.setVisibility(View.VISIBLE);
			else
				mRebarSpinner.setVisibility(View.GONE);
		}
		

		// Show or hide the gravel calculations based on the users choice
		// Hide gravel calculations if the hosting activity is WallCalculatorActivity
		// WallCalculatorActivity does not include gravel calculations, therefore it should always return 0 for gravel.
		if (!hasGravelCalculationVisible || ccr.getmGravelTons() == 0) {
			mGravelLabel.setVisibility(View.GONE);
			mGravelTonsLabel.setVisibility(View.GONE);
			mGravelTextView.setVisibility(View.GONE);
			mGravelSpinner.setVisibility(View.GONE);
		} else {
			mGravelLabel.setVisibility(View.VISIBLE);
			mGravelTonsLabel.setVisibility(View.VISIBLE);
			mGravelTextView.setVisibility(View.VISIBLE);
			mGravelSpinner.setVisibility(View.VISIBLE);
		}
		
		//Show or hide the cost calculations based on the users choice
		if (!hasCostCalculationVisible) {
			mCostLabel.setVisibility(View.GONE);
			mCostTextView.setVisibility(View.GONE);
			params.addRule(RelativeLayout.BELOW, mCubicYardsTextView.getId()); //define a rule used by the divider line
		} else {
			mCostLabel.setVisibility(View.VISIBLE);
			mCostTextView.setVisibility(View.VISIBLE);
			params.addRule(RelativeLayout.BELOW, mCostTextView.getId()); //define a rule used by the divider line
			
			//update the total cost of concrete. This is only necessary if it is actually visible
			mStandardConcretePrice = getStandardConcretePriceFromPreference();
			
			ccr.setmConcretePrice((Math.round(Calculations.concretePrice(mStandardConcretePrice, ccr.getmCubicYards()) * 100) / 100.0));	
			if(ccr.getmConcretePrice() == 0.0 && mStandardConcretePrice == 0) {
				mCostTextView.setText(getResources().getText(R.string.cost_hint));
			}else {
				mCostTextView.setText("$" + ((Double)ccr.getmConcretePrice()).toString());
			}
		}
		
		//Adjust the layout of the divider line based on the cost calculations being there or not
		mDividerLine.setLayoutParams(params);
		mDividerLine.getLayoutParams().height = 1;

		// Show or hide the gravel calculations based on the users choice
		if (!hasBagCalculationsVisible) {
			m80PoundBagLabel.setVisibility(View.GONE);
			m80PoundBagsTextView.setVisibility(View.GONE);
			m60PoundBagLabel.setVisibility(View.GONE);
			m60PoundBagsTextView.setVisibility(View.GONE);
			m40PoundBagLabel.setVisibility(View.GONE);
			m40PoundBagsTextView.setVisibility(View.GONE);
		} else {
			m80PoundBagLabel.setVisibility(View.VISIBLE);
			m80PoundBagsTextView.setVisibility(View.VISIBLE);
			m60PoundBagLabel.setVisibility(View.VISIBLE);
			m60PoundBagsTextView.setVisibility(View.VISIBLE);
			m40PoundBagLabel.setVisibility(View.VISIBLE);
			m40PoundBagsTextView.setVisibility(View.VISIBLE);
		}
		
		
	}//End onResume()	
 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.calculations_result, container, false);
		
		//Initialize the shared preferences instance field, and get the values from shared Preferences.
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		getPreferenceValues();
		
		
		//Handle rotation change, and retrieve the old values for the ccr object if they have been saved
		if(savedInstanceState != null) {
			ccr = (ConcreteCalculationsResult)savedInstanceState.getSerializable(CCR);
			mRebarSpinnerSelection = savedInstanceState.getInt(REBAR_SPINNER_SELECTION);
			mGravelSpinnerSelection = savedInstanceState.getInt(GRAVEL_SPINNER_SELECTION);
		}
		else {
			//Retrieve the calculations back from the activity
			//This is run every time a new fragment without any savedInstanceState value is created.
			ccr = mCallbacks.updateResult();
			
			//Check for any value in CalculationsResultList in order to find out if it has any saved data.
			//If so, this indicates the user are adding this calculation to another.
			if(!CalculationsResultList.get(getActivity()).isEmpty()) {
				ccr.add(CalculationsResultList.get(getActivity()).getCumulatedResult());
			}
		}
		
		
		
		setUpSpinners(v);

		
		//Set the onItemSelectedListener for the rebar spinner
		mRebarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Find out what selection the user pressed, by checking the position returned in the onItemSelected() method
				//Set the rebar spacing according the selection
				switch(position) {
					case 0:	mRebarSpacing = 12; break;
					case 1: mRebarSpacing = 18; break;
					case 2: mRebarSpacing = 24; break;
					case 3: mRebarSpacing = 30; break;
					case 4: mRebarSpacing = 36; break;
					default: mRebarSpacing = 24; break;
				}//end switch
				updateRebarCalculation();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		
		
		//Set the onItemSelectedListener for the gravel spinner
		mGravelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				//Find out what selection the user pressed, by checking the position returned in the onItemSelected() method
				//Set the gravel layer depth according the selection
				switch(position) {
				case 0: mGravelDepth = 1; break;
				case 1: mGravelDepth = 2; break;
				case 2: mGravelDepth = 3; break;
				case 3: mGravelDepth = 4; break;
				default: mGravelDepth = 2; break;
				}
				
				updateGravelCalculation();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
				
		//Set all the textView output boxes
		mCubicYardsTextView = (TextView)v.findViewById(R.id.cubic_yards_result_textview);
		double cubicYardsFormatted = (Math.round(ccr.getmCubicYards() * 100) / 100.0); //format to 2 decimals
		mCubicYardsTextView.setText(((Double)cubicYardsFormatted).toString());
		
		mCostTextView = (TextView)v.findViewById(R.id.cost_result_textview); //The value of the cost, rebar, and gravel textView are set in onResume()
		mRebarTextView = (TextView)v.findViewById(R.id.rebar_result_textview);
		mGravelTextView = (TextView)v.findViewById(R.id.gravel_result_textview);
		
		m80PoundBagsTextView = (TextView)v.findViewById(R.id.eighty_pound_bag_result_textview);
		m80PoundBagsTextView.setText(((Integer)ccr.getM80PoundBags()).toString());
		
		m60PoundBagsTextView = (TextView)v.findViewById(R.id.sixty_pound_bag_result_textview);
		m60PoundBagsTextView.setText(((Integer)ccr.getM60PoundBags()).toString());
		
		m40PoundBagsTextView = (TextView)v.findViewById(R.id.fourty_pound_bag_result_textview);
		m40PoundBagsTextView.setText(((Integer)ccr.getM40PoundBags()).toString());
		
		
		//These are used just for visibility purposes. They can be set to visible or invisible by the user
		mRebarLabel = (TextView)v.findViewById(R.id.rebar_textview);
		mRebarLinearFeetLabel = (TextView)v.findViewById(R.id.rebar_linearfeet_textview);
		mGravelLabel = (TextView)v.findViewById(R.id.gravel_textview);
		mGravelTonsLabel = (TextView)v.findViewById(R.id.gravel_tons_textview);
		m80PoundBagLabel = (TextView)v.findViewById(R.id.eighty_pound_bag_textview);
		m60PoundBagLabel = (TextView)v.findViewById(R.id.sixty_pound_bag_textview);
		m40PoundBagLabel = (TextView)v.findViewById(R.id.fourty_pound_bag_textview);
		mCostLabel = (TextView)v.findViewById(R.id.cost_textview);
		mDividerLine = v.findViewById(R.id.divider1_view);
		
		//Initialize the relative layout parameters variable. It is used in onResume()
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		
		updateRebarCalculation();
		updateGravelCalculation();
		
		
		//Setup the send button
		//This will give the user an option to send the calculated result through an email or text message.
		mSendButton = (Button)v.findViewById(R.id.send_result_button);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Format the cubic yards to 2 decimals
				ccr.setmCubicYards((Math.round(ccr.getmCubicYards() * 100) / 100.0));
				
				
				//Reset values to 0 if they are not visible. This will prevent their corresponding check boxes
				//	from appearing in the send dialog.
				if(!hasBagCalculationsVisible) {
					ccr.setM40PoundBags(0);
					ccr.setM60PoundBags(0);
					ccr.setM80PoundBags(0);
				}
				if(!hasCostCalculationVisible) {
					ccr.setmConcretePrice(0);
				}
				if(!hasGravelCalculationVisible) {
					ccr.setmGravelTons(0);
				}
				if(!hasRebarCalculationVisible) {
					ccr.setRebarLength(0);
				}
				
				//Open a dialog to let the user decide what to include in the message
				SendResultDialog dialog = SendResultDialog.newInstance(ccr);
				dialog.show(getFragmentManager(), "Send result dialog");	
			}
		});
		
		//Setup the 'add calculation' button. This will allow the user to add two or more calculations together.
		mAddCalculationButton  = (Button)v.findViewById(R.id.add_calculation_button);
		mAddCalculationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//check which values are visible, if they are not visible, then reset them to 0. 
				//because the user is not interested in them.
				if(!hasBagCalculationsVisible) {
					ccr.setM40PoundBags(0);
					ccr.setM60PoundBags(0);
					ccr.setM80PoundBags(0);
				}
				if(!hasCostCalculationVisible) {
					ccr.setmConcretePrice(0);
				}
				if(!hasGravelCalculationVisible) {
					ccr.setmGravelTons(0);
				}
				if(!hasRebarCalculationVisible) {
					ccr.setRebarLength(0);
				}
				
				//save the data to a ccr object and then open a dialog asking the user what 
				// type of concrete calculation to add (slab, wall, footing etc).
				CalculationsResultList.get(getActivity()).setCumulatedResult(ccr);
				
				AddCalculationDialog dialog = new AddCalculationDialog();
				dialog.show(getFragmentManager(), "Add calculation");
			}
		});
		
		
		return v;
	}//End onCreateView
	
	/*
	 * This  method gets the spinners set up and ready to use
	 */
	private void setUpSpinners(View v) {

		// define a spinner for the rebar calculations
		mRebarSpinner = (Spinner) v.findViewById(R.id.rebar_spinner);
		// Create an ArrayAdapter using the slab_rebar string array and a
		// default layout
		ArrayAdapter<CharSequence> rebarSpinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.slab_rebar_spacing,
						R.layout.spinner_layout);
		// Specify the layout to use when the list of choices appears
		rebarSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		// Apply the adapter to the spinner
		mRebarSpinner.setAdapter(rebarSpinnerAdapter);
		

		// Define a spinner for the gravel input box. The rest is the same as
		// above
		mGravelSpinner = (Spinner) v.findViewById(R.id.gravel_spinner);
		ArrayAdapter<CharSequence> gravelSpinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.gravel_thickness,
						R.layout.spinner_layout);

		gravelSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mGravelSpinner.setAdapter(gravelSpinnerAdapter);
		
	}//End setUpSpinners
	
	/*
	 * Helper method to get the users default choice from the settings menu
	 * Note** This can only be called after sharedPrefs have been initialized
	 * 		otherwise it will throw an exception
	 * This is for  concrete slab calculations
	 */
	private int getRebarSpacingFromPreferences() {
		String RebarSpacingStr = sharedPrefs.getString(REBAR_GRIDSIZE_PREFS_KEY, "2");
		return Integer.parseInt(RebarSpacingStr);
	}
	
	/*
	 * Helper method to get the users default choice from the settings menu
	 * Note** This can only be called after sharedPrefs have been initialized
	 * 		otherwise it will throw an exception
	 */
	private int getGravelDepthFromPreferences() {
		String GravelDepthStr = sharedPrefs.getString(GRAVEL_DEPTH_PREFS_KEY, "1");
		return Integer.parseInt(GravelDepthStr);
	}
	
	/*
	 * This returns the standard price of concrete if the user has set it in settings,
	 * otherwise it returns 0.0.
	 * 
	 * Note** sharedPrefs needs to have been initialized before this method gets called
	 */
	private double getStandardConcretePriceFromPreference() {
		//Get the price of standard concrete from the shared preferences as a string		
		mStandardConcretePriceStr = sharedPrefs.getString(SettingsFragment.STANDARD_CONCRETE_PRICE_PREFS_KEY, "0");
		//Convert the String value to a double. mStandardConcretePrice is set to 0.0 if no price is set in preferences
		if(!(mStandardConcretePriceStr.equals(""))) {
			 return Double.parseDouble(mStandardConcretePriceStr);
		}else
			return 0.0;
	}
	
	
	/*
	 * Helper method that displays the rebar calculations.
	 *  Note** This can only be called after mRebarTextView and ccr has been initialized
	 *  		otherwise it will throw an exception. They are initialized in onCreateView()
	 */
	private void updateRebarCalculation() {
		//Retrieve the calculations back from the activity 
		int rebar = mCallbacks.updateRebarLenght(mRebarSpacing);
		ccr.setRebarLength(rebar);
		
		//Update the rebar textView with the new rebar spacing selection
		mRebarTextView.setText(((Integer)ccr.getRebarLength()).toString());
	}
	
	/*
	 * Helper method that displays the gravel calculations.
	 *  Note** This can only be called after mGravelTextView and ccr has been initialized
	 *  		otherwise it will throw an exception. They are initialized in onCreateView()
	 */
	private void updateGravelCalculation() {
		//Retrieve the calculations back from the activity, and format the output
		double tons = Math.round(mCallbacks.updateGravelDepth(mGravelDepth) * 100) / 100.0;
		ccr.setmGravelTons(tons );
		
		//Update the gravel textView based on the new gravel layer selection
		mGravelTextView.setText(((Double)ccr.getmGravelTons()).toString());
	}
	
	/*
	 * Set up rebar spinner selection based on the shared preference value
	 * e.g. If mRebarSpacing is 24, then the spinner should be set to array element 2 etc.
	 * 
	 * Note** This can only be called after the mRebarSpinner has been initialized
	 *  		otherwise it will throw an exception. It is initialized in onCreateView()
	 */
	private void updateRebarSpinnerSelection() {
		
		int rebarArrayElement = 0;
		if(mRebarSpinnerSelection == -1) {
			switch(mRebarSpacing) {
			case 12: rebarArrayElement = 0; break;
			case 18: rebarArrayElement = 1; break;
			case 24: rebarArrayElement = 2; break;
			case 30: rebarArrayElement = 3; break;
			case 36: rebarArrayElement = 4; break;
			}
		}else
			rebarArrayElement = mRebarSpinnerSelection;
		mRebarSpinner.setSelection(rebarArrayElement); //Set default value for the rebar spinner.
	}
	
	/*
	 * Setup the gravel spinner selection based on the shared preference value
	 * 
	 *  Note** This can only be called after the mGravelSpinner has been initialized
	 *  		otherwise it will throw an exception. It is initialized in onCreateView()
	 */
	private void updateGravelSpinnerSelection() {
		
		int gravelArrayElement = 0;
		if(mGravelSpinnerSelection == -1) {
			switch(mGravelDepth) {
			case 1: gravelArrayElement = 0; break;
			case 2: gravelArrayElement = 1; break;
			case 3: gravelArrayElement = 2; break;
			case 4: gravelArrayElement = 3; break;
			}
		}else
			gravelArrayElement = mGravelSpinnerSelection;
		mGravelSpinner.setSelection(gravelArrayElement); // Set default value  of the gravel spinner.
	}
	
	/*
	 * In this method I retrieve all the default/preference values from the sharedPreference object
	 * 
	 **** Note**** This method needs to be called AFTER the SharedPrefs instance variable has been initialized.
	 * or it will throw an exception.
	 */
	private void getPreferenceValues() {
		//Set the rebar spacing and gravel depth default choices
		mRebarSpacing = getRebarSpacingFromPreferences();
		mGravelDepth = getGravelDepthFromPreferences();
		
		mStandardConcretePrice = getStandardConcretePriceFromPreference();
	}
	
}//End CalculationResultFragment

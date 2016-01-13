package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This fragment displays the concrete footing input boxes. 
 *** NOTE: Any Activity that host's this fragment must implement the Callback interface
 */
import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;
import org.sasdevelopment.android.concretecalculator.interfaces.Callback;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FootingCalculatorFragment extends Fragment {
	
	//public static final String TAG = "FootingCalculatorFragment";
	private static final String REBAR_LAYOUT_OPEN = "rebarLayoutOpen";
	
	private TextView mRebarSpacingLabel;
	private TextView mHeaderTextView;
	private EditText mPerpendicularSpacingEditText;
	private EditText mWidthEditText;
	private EditText mLengthEditText;
	private EditText mThicknessEditText;
	private Spinner mWidthSpinner;
	private Spinner mLengthSpinner;
	private Spinner mThicknessSpinner;
	private Spinner mNumRebarsSpinner;
	private Button mCalculateButton;
	private ImageView mIncludeRebarInputIcon;
	private ImageView mCloseRebarInputIcon;
	private LinearLayout mOptionalRebarLayout;
	private CheckBox mGravelInputCheckbox;
	private Callback mCallback; //******* The hosting Activity most implement this interface ***********
	private ViewStub viewStub;
	private View mRebarLayoutinflated;
	
	private double mWidth;
	private double mLength;
	private int mThickness;
	private int mPerpendicularSpacing;
	private int mNumRebars;
	private boolean mHasGravelCalculation;
	private boolean mHasRebarInput;

	
	//This method is run when the fragment is first attached to the hosting activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//The hosting activity is cast to Callbacks, and is assigned to mCallbacks
		//Note ** If the activity doesn't implement the Callbacks interface an exception will be thrown
		mCallback = (Callback)activity;
	}

	//This method is run when the fragment is detached from the hosting activity
	@Override
	public void onDetach() {
		super.onDetach();
		//mCallbacks is assigned back to null when the fragment is detached from the hosting activity
		mCallback = null;
	}

	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(REBAR_LAYOUT_OPEN, mHasRebarInput);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Inflate the layout
		//See MainFragment onCreateView() for more comments about the parameters of this method
		View v = inflater.inflate(R.layout.footing_calculator, container, false);
		
		//Keep the optional rebar layout open on rotation change
		if(savedInstanceState != null) {
			mHasRebarInput = savedInstanceState.getBoolean(REBAR_LAYOUT_OPEN);
		}
		
		
		//Setup the input boxes
		mWidthEditText = (EditText)v.findViewById(R.id.width_edittext);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edittext);
		mThicknessEditText = (EditText)v.findViewById(R.id.thickness_edittext);
		mIncludeRebarInputIcon = (ImageView)v.findViewById(R.id.rebar_spacing_down_arrow);
		mOptionalRebarLayout = (LinearLayout)v.findViewById(R.id.optional_rebar_layout);
		mGravelInputCheckbox = (CheckBox)v.findViewById(R.id.include_gravel_checkbox);
		viewStub = (ViewStub)v.findViewById(R.id.footing_rebar_stub);
		
		//Handles the click event on the check box. mHasGravelCalculation will be passed on to the 
		//	hosting activity and from there on to the calculations result fragment. 
		//	Then the gravel results will be shown if the user has checked the box, otherwise it will not.
		mGravelInputCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mHasGravelCalculation = isChecked;	
			}
		});
		
		
		//Setup the spinners
		mWidthSpinner = (Spinner)v.findViewById(R.id.width_spinner);
		// Create an ArrayAdapter using the feet/inches string array and a default layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.feet_inches,
				R.layout.spinner_layout);
		// Specify the layout to use when the drop down list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mWidthSpinner.setAdapter(adapter);
		mWidthSpinner.setSelection(1);
		
		mLengthSpinner = (Spinner)v.findViewById(R.id.length_spinner);
		mLengthSpinner.setAdapter(adapter);
		mThicknessSpinner = (Spinner)v.findViewById(R.id.thickness_spinner);
		mThicknessSpinner.setAdapter(adapter);
		mThicknessSpinner.setSelection(1); //Set selection to Inches.
		
		//Keep the rebar layout open on rotation change
		if(mHasRebarInput) {
			openRebarLayout();
		}
		
		
		
		//open the rebar calculation layout if the user wants to include it.
		mIncludeRebarInputIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openRebarLayout();
			}
		});
		
		//Set up the calculation button
		//It will get the values from the input boxes and then use the callback method to transfer
		//	the values to the hosting activity, so they can be used by the CalculationResultFragment.
		//CalculationResultFragment is then added to the hosting activity.
		mCalculateButton = (Button)v.findViewById(R.id.footing_calculator_button);
		mCalculateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Get the input from the width, length, and thickness boxes
				
				//Get the width data in inches
				if(mWidthEditText.getText().length() == 0) { //Check for no input
					mWidth = 0.0;
				}else {
					if(mWidthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
						mWidth = Double.valueOf(mWidthEditText.getText().toString());
					}else
						mWidth = (Double.valueOf(mWidthEditText.getText().toString()) * 12);
				}
				
				//Get the length data in feet
				if(mLengthEditText.getText().length() == 0) {
					mLength = 0.0;
				}else {
					if(mLengthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
						mLength = (Double.valueOf(mLengthEditText.getText().toString()) / 12);
					}else
						mLength = Double.valueOf(mLengthEditText.getText().toString());
				}
				
				
				//Get the thickness data
				//It is measured in inches
				if(mThicknessEditText.getText().length() == 0) {
					mThickness = 0;
				}else {
					if(mThicknessSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
						mThickness = Integer.valueOf(mThicknessEditText.getText().toString());
					}else
						mThickness = (Integer.valueOf(mThicknessEditText.getText().toString()) * 12);
				}
				
				//Get perpendicular rebar spacing in inches
				if(mHasRebarInput) {
					if(mPerpendicularSpacingEditText.getText().length() == 0) {
						mPerpendicularSpacing = 0;
					}else {
						mPerpendicularSpacing = Integer.valueOf(mPerpendicularSpacingEditText.getText().toString());
					}
					
					//Get the number of lengthwise running rebars
					mNumRebars = Integer.valueOf(mNumRebarsSpinner.getSelectedItem().toString());
				}
				
				int totalSquareFeet = (int)(Math.ceil((mWidth / 12) * mLength));
				
				//Get the rebar length and cubic yards
				int rebarLength = (mHasRebarInput) ? Calculations.calculateFootingRebar(mWidth, mLength, mNumRebars, mPerpendicularSpacing) : 0;
				double cubicYards = Calculations.concreteSlabCubicYards(totalSquareFeet, mThickness);
				
				//Use the Callback interface to get the result to the hosting activity
				mCallback.onCalculationClick(cubicYards, rebarLength, totalSquareFeet, mHasGravelCalculation);
				
				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				//**NOTE** It's important to make sure the activity always have a view with focus, otherwise getCurrentFocus()
				//		will return null and the app will crash.
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				
			}
		});
		
		if(AddCalculationDialog.isOpen()) {
			//make the plus sign textview visible when adding calculations together.
			mHeaderTextView = (TextView)v.findViewById(R.id.footing_header_text_view);
			mHeaderTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_content_add_circle_outline), null);
			
			mHeaderTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), R.string.adding_calculations_toast, Toast.LENGTH_SHORT).show();
					
				}
			});
		}
		
		return v;
	}//End onCreateView
	
	private void openRebarLayout() {
		//if viewStub is null, then mRebarLayoutinflated has already been inflated
		if(viewStub != null) {
			mRebarLayoutinflated = viewStub.inflate();
			mPerpendicularSpacingEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.perpendicular_spacing_edittext);
			mCloseRebarInputIcon = (ImageView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_up_arrow);
			mRebarSpacingLabel = (TextView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_label);
			
			mNumRebarsSpinner = (Spinner)mRebarLayoutinflated.findViewById(R.id.lengthwise_running_bars_spinner);
			//Reuse the column_vertical_rebars array
			ArrayAdapter<CharSequence> numRebarAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.column_vertical_rebars,
					R.layout.spinner_layout);
			numRebarAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
			mNumRebarsSpinner.setAdapter(numRebarAdapter);
			viewStub = null;
		}else {
			mRebarLayoutinflated.setVisibility(View.VISIBLE);
		}
		
		mRebarSpacingLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mOptionalRebarLayout.setVisibility(View.GONE);
		mPerpendicularSpacingEditText.requestFocus();
		mHasRebarInput = true;
		
		//The upArrowIcon listener is defined here because the upArrowIcon is only inflated
		//	if the rebar layout has been opened.
		mCloseRebarInputIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRebarLayoutinflated.setVisibility(View.GONE);
				mOptionalRebarLayout.setVisibility(View.VISIBLE);
				mThicknessEditText.requestFocus();
				mHasRebarInput = false;
			}
		});
	}
	

}

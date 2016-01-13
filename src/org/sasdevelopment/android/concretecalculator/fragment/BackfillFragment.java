package org.sasdevelopment.android.concretecalculator.fragment;

/*
 * This class extends BaseFragment. That is a Fragment that includes variables and methods
 * that BackfillFragment, ConcreteHaulOffFragment, and DirtExcavationFragment have in common.
 */

import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class BackfillFragment extends BaseFragment {
	
	//private static final String TAG = "BackfillFragment";
	private static final String AGGREGATE_STR = "aggregateStr";
	private static final String TONS_NEEDED = "tonsNeeded";
	private static final String DEPTH = "depth";
	
	private Spinner mDepthSpinner;
	private Spinner mAggregateSpinner;
	private TextView mBackfillResultTextView;
	private TextView mMaterialsTextview;
	private String mAggregateStr;
	private float mAggregateTonsNeeded;
	private boolean hasSavedInstanceState; //This variable is needed because I cannot check the savedInstanceState in the listeners in onCreateView
	
	//This is just used for visibility purposes
	private TextView mTonsOfLabel;
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(AGGREGATE_STR, mAggregateStr);
		outState.putFloat(TONS_NEEDED, mAggregateTonsNeeded);
		outState.putFloat(DEPTH, mDepth);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.backfill_calculator, container, false);
		final InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		
				
		mWidthEditText = (EditText)v.findViewById(R.id.width_edit_text);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edit_text);
		mSquarefeetEditText = (EditText)v.findViewById(R.id.total_squarefoot_edit_text);
		mDepthEditText = (EditText)v.findViewById(R.id.depth_edit_text);
		mWidthSpinner = (Spinner)v.findViewById(R.id.width_spinner);
		mLengthSpinner = (Spinner)v.findViewById(R.id.length_spinner);
		mDepthSpinner= (Spinner)v.findViewById(R.id.depth_spinner);
		mAggregateSpinner = (Spinner)v.findViewById(R.id.aggregate_spinner);
		mCalculateButton = (Button)v.findViewById(R.id.calculator_button);
		mBackfillResultTextView = (TextView)v.findViewById(R.id.backfill_result_textview);
		mTonsOfLabel = (TextView)v.findViewById(R.id.tons_of_textview);
		mMaterialsTextview = (TextView)v.findViewById(R.id.materials_textview);
		
		//Hide the result views initially
		mBackfillResultTextView.setVisibility(View.GONE);
		mTonsOfLabel.setVisibility(View.GONE);
		mMaterialsTextview.setVisibility(View.GONE);
		
		//These values are saved in order to disable the right input boxes on rotation
		if(savedInstanceState != null) {
			mTotalSquarefeet = savedInstanceState.getInt(TOTAL_SQUARE_FEET);
			mWidth = savedInstanceState.getFloat(WIDTH);
			mLength = savedInstanceState.getFloat(LENGTH);
			mDepth = savedInstanceState.getFloat(DEPTH);
			mAggregateStr = savedInstanceState.getString(AGGREGATE_STR);
			mAggregateTonsNeeded = savedInstanceState.getFloat(TONS_NEEDED);
			mBackfillResultTextView.setText(String.valueOf(mAggregateTonsNeeded));
			mMaterialsTextview.setText(mAggregateStr);
			hasSavedInstanceState = true;
			
			if(mAggregateTonsNeeded != 0) {
				mBackfillResultTextView.setVisibility(View.VISIBLE);
				mTonsOfLabel.setVisibility(View.VISIBLE);
				mMaterialsTextview.setVisibility(View.VISIBLE);
			}
		}
		
		//Setup the spinners
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
				,R.array.feet_inches, R.layout.spinner_layout);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
				
		mWidthSpinner.setAdapter(adapter);
		mLengthSpinner.setAdapter(adapter);
		mDepthSpinner.setAdapter(adapter);
		mDepthSpinner.setSelection(1);
		
		ArrayAdapter<CharSequence> aggregateAdapter = ArrayAdapter.createFromResource(getActivity()
				,R.array.aggregates, R.layout.spinner_layout);
		aggregateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mAggregateSpinner.setAdapter(aggregateAdapter);
		
		//Set a listener for the aggregate spinner so the calculations will be updated when the user selects a new material.
		mAggregateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				setCalculationResult();	
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		// This will take care of the right boxes stays disabled and enabled on
		// rotation change
		// The mDepthEditText onFocusChange listener is implemented for this
		// purpose
		if(mTotalSquarefeet != 0) {
			disable(mWidthEditText);
			disable(mLengthEditText);
		}
		else if(mWidth != 0 || mLength != 0) {
			disable(mSquarefeetEditText);
		}

		// Set up the onFocusChange listener for the total square feet input
		// box.
		mSquarefeetEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						setFocus();
					}
				});// End totalSquarefeet box listener

		// Set up the onFocusChange listener for the width input box
		mWidthEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						setFocus();
					}
				});// End width box listener

		// Set up the onFocusChange listener for the length input box
		mLengthEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						setFocus();

					}
				});// End length box listener

		// The purpose of this listener is to give the 3 other input boxes the
		// right values on a rotation change
		// happening when this editText box has focus. The purpose of giving the
		// other 3 boxes the right values
		// is to "enabled" and "disabled" the right boxes on a rotation change.
		// These values are not the actual values used in the calculations. that
		// part is done in the button onClickListener.
		mDepthEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// check for input in the total square feet box. If
						// there is input, then totalSquareFeet is assigned 1
						// and width and length is assigned to 0. If no input,
						// then vice-versa.
						if(!hasSavedInstanceState) {
							if (mSquarefeetEditText.getText().length() == 0) {
									mTotalSquarefeet = 0;
									mWidth = 1;
									mLength = 1;
							}
							else {
								mTotalSquarefeet = 1;
								mWidth = 0;
								mLength = 0;
							}
						}
					}
				});
		
		mCalculateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Get data from the input boxes 
				
				//Check for input in the total square feet box. An EditText box will return a charSequence
				// so checking its length is better than checking for an empty string. An EditText won't return null.
				if(mSquarefeetEditText.getText().length() == 0) {
					mTotalSquarefeet = 0;
				}
				else {
					mTotalSquarefeet = Integer.valueOf(mSquarefeetEditText.getText().toString());
				}
				
				//Check for input in the width box in feet.
				if(mWidthEditText.getText().length() == 0) {
					mWidth = 0.0f;
				}
				else {
					//Get the selection from the width spinner
					if(mWidthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) 
						mWidth =  (Float.valueOf(mWidthEditText.getText().toString()) / 12);
					else
						mWidth = Float.valueOf(mWidthEditText.getText().toString());
				}
				
				//Check for input in the length box in feet
				if(mLengthEditText.getText().length() == 0) {
					mLength = 0.0f;
				}
				else {
					//Get the selection from the length spinner
					if(mLengthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches)))
						mLength = (Float.valueOf(mLengthEditText.getText().toString()) / 12);
					else
						mLength = Float.valueOf(mLengthEditText.getText().toString());
				}
				
				//Check for input in the depth box in feet
				if(mDepthEditText.getText().length() == 0) {
					mDepth = 0.0f;
				}
				else {
					//Get the selection from the depth spinner
					if(mDepthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches)))
						mDepth = (Float.valueOf(mDepthEditText.getText().toString()) / 12);
					else
						mDepth = Float.valueOf(mDepthEditText.getText().toString());
				}
				
				//calculate the tons needed and set the result boxes to be visible
				setCalculationResult();
				mBackfillResultTextView.setVisibility(View.VISIBLE);
				mMaterialsTextview.setVisibility(View.VISIBLE);
				mTonsOfLabel.setVisibility(View.VISIBLE);
				
			    
				
				
				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the Calculation Result
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		
		return v;
	}//End onCreateView
	
	
	/*
	 * This is a helper method that sets the focus on either the total square feet box,
	 * or the width and length boxes. 
	 * If the user clicks total square feet box, then the width and length boxes will be cleared 
	 * of input and the opacity changed in order to let the user know that not all 3 boxes can have input at the same time.
	 * If the user clicks on either the width or length box, then input in the total square feet box 
	 * will be cleared out, and have the opacity changed.
	 */
	

	//Helper method to prevent code duplication
	private void setCalculationResult() {
		int aggregateSelection = mAggregateSpinner.getSelectedItemPosition();
		mAggregateStr = mAggregateSpinner.getSelectedItem().toString();
		
		//calculate the amount of backfill needed
		int squarefeet = (mTotalSquarefeet != 0) ? mTotalSquarefeet : (int)(mWidth * mLength);
		float cubicYards = (squarefeet * mDepth) / Calculations.CUBIC_FEET_PER_CUBIC_YARD;
		
		//calculate aggregateTonsNeeded and format it to 2 decimals
		mAggregateTonsNeeded = (Math.round(Calculations.calculateBackfill(aggregateSelection, cubicYards) * 100)) / 100.0f;
		
		mBackfillResultTextView.setText(String.valueOf(mAggregateTonsNeeded));
		mMaterialsTextview.setText(mAggregateStr);
	}
	
	
}

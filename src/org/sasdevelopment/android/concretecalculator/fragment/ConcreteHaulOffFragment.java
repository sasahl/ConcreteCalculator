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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ConcreteHaulOffFragment extends BaseFragment {

	private static final String TONS_OF_CONCRETE = "tonsOfConcrete";
	
	private Spinner mDepthSpinner;
	private TextView mResultTextView;
	private TextView mHeaderTextView;
	private float mTonsOfConcrete;
	
	//This is just used for visibility purposes
	private TextView mTonsLabel;
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putFloat(TONS_OF_CONCRETE, mTonsOfConcrete);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.concrete_haul_off, container, false);
		
		//These values are saved in order to disable the right input boxes on rotation
		
		
		mWidthEditText = (EditText)v.findViewById(R.id.width_edit_text);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edit_text);
		mSquarefeetEditText = (EditText)v.findViewById(R.id.total_squarefoot_edit_text);
		mDepthEditText = (EditText)v.findViewById(R.id.depth_edit_text);
		mWidthSpinner = (Spinner)v.findViewById(R.id.width_spinner);
		mLengthSpinner = (Spinner)v.findViewById(R.id.length_spinner);
		mDepthSpinner= (Spinner)v.findViewById(R.id.depth_spinner);
		mCalculateButton = (Button)v.findViewById(R.id.calculator_button);
		mResultTextView = (TextView)v.findViewById(R.id.concrete_hauloff_result_textview);
		mHeaderTextView = (TextView)v.findViewById(R.id.header_text_view);
		mHeaderTextView.setText(R.string.concrete_tear_out);
		mTonsLabel = (TextView)v.findViewById(R.id.tons_label);
		
		//Hide the result textviews
		mResultTextView.setVisibility(View.GONE);
		mTonsLabel.setVisibility(View.GONE);
		
		if(savedInstanceState != null) {
			mTotalSquarefeet = savedInstanceState.getInt(TOTAL_SQUARE_FEET);
			mWidth = savedInstanceState.getFloat(WIDTH);
			mLength = savedInstanceState.getFloat(LENGTH);
			mTonsOfConcrete = savedInstanceState.getFloat(TONS_OF_CONCRETE);
			mResultTextView.setText(String.valueOf(mTonsOfConcrete));
			
			if(mTonsOfConcrete != 0) {
				mResultTextView.setVisibility(View.VISIBLE);
				mTonsLabel.setVisibility(View.VISIBLE);
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
		
		//This will take care of the right boxes stays disabled and enabled on rotation change
		//The mDepthEditText onFocusChange listener is implemented for this purpose
		if(mTotalSquarefeet != 0) {
			disable(mWidthEditText);
			disable(mLengthEditText);
		}
		else if(mWidth != 0 || mLength != 0) {
			disable(mSquarefeetEditText);
		}
						
					
		//Set up the onFocusChange listener for the total square feet input box.
		mSquarefeetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
							
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				setFocus();
			}
		});//End totalSquarefeet box listener
						
						
		//Set up the onFocusChange listener for the width input box
		mWidthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
							
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				setFocus();
			}
		});//End width box listener
						
		// Set up the onFocusChange listener for the length input box
		mLengthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			 @Override
			 public void onFocusChange(View v, boolean hasFocus) {
				 setFocus();
								 
			}
		});//End length box listener
						
		//The purpose of this listener is to give the 3 other input boxes the right values on a rotation change
		// happening when this editText box has focus. The purpose of giving the other 3 boxes the right values
		// is to "enabled" and "disabled" the right boxes on a rotation change.
		// These values are not the actual values used in the calculations. that part is done in the button onClickListener.
		mDepthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
							
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//check for input in the total square feet box. If there is input, then totalSquareFeet is assigned 1
				// and width and length is assigned to 0. If no input, then vice-versa.
				if(mSquarefeetEditText.getText().length() == 0) {
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
		});
		
		mCalculateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Get data from the input boxes

				// Check for input in the total square feet box. An EditText box
				// will return a charSequence
				// so checking its length is better than checking for an empty
				// string. An EditText won't return null.
				if (mSquarefeetEditText.getText().length() == 0) {
					mTotalSquarefeet = 0;
				} else {
					mTotalSquarefeet = Integer.valueOf(mSquarefeetEditText
							.getText().toString());
				}

				// Check for input in the width box in feet.
				if (mWidthEditText.getText().length() == 0) {
					mWidth = 0.0f;
				} else {
					// Get the selection from the width spinner
					if (mWidthSpinner.getSelectedItem().toString()
							.equals(getResources().getString(R.string.inches)))
						mWidth = (Float.valueOf(mWidthEditText.getText()
								.toString()) / 12);
					else
						mWidth = Float.valueOf(mWidthEditText.getText()
								.toString());
				}

				// Check for input in the length box in feet
				if (mLengthEditText.getText().length() == 0) {
					mLength = 0.0f;
				} else {
					// Get the selection from the length spinner
					if (mLengthSpinner.getSelectedItem().toString()
							.equals(getResources().getString(R.string.inches)))
						mLength = (Float.valueOf(mLengthEditText.getText()
								.toString()) / 12);
					else
						mLength = Float.valueOf(mLengthEditText.getText()
								.toString());
				}

				// Check for input in the depth box in feet
				if (mDepthEditText.getText().length() == 0) {
					mDepth = 0.0f;
				} else {
					// Get the selection from the depth spinner
					if (mDepthSpinner.getSelectedItem().toString()
							.equals(getResources().getString(R.string.inches)))
						mDepth = (Float.valueOf(mDepthEditText.getText()
								.toString()) / 12);
					else
						mDepth = Float.valueOf(mDepthEditText.getText()
								.toString());
				}

				// calculate the tons of concrete to haul off.
				int squarefeet = (mTotalSquarefeet != 0) ? mTotalSquarefeet
						: (int) (mWidth * mLength);
				mTonsOfConcrete = Calculations.CalculateConcreteTonsToHaulOff(
						squarefeet, mDepth);

				// format the dirtAmount to 2 decimals
				mTonsOfConcrete = Math.round(mTonsOfConcrete * 100) / 100.0f;

				mResultTextView.setText(String.valueOf(mTonsOfConcrete));
				mResultTextView.setVisibility(View.VISIBLE);
				mTonsLabel.setVisibility(View.VISIBLE);

				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the Calculation Result
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		return v;
	}




	
}

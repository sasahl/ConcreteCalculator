package org.sasdevelopment.android.concretecalculator.fragment;

/*
 * This fragment display the Column Calculator input layout.
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ColumnCalculatorFragment extends Fragment {
	
	private static final String REBAR_LAYOUT_OPEN = "rebarLayoutOpen";
	
	private TextView mRebarSpacingLabel;
	private TextView mHeaderTextView;
	private EditText mHeightEditText;
	private EditText mDiameterEditText;
	private EditText mNumColumnsEditText;
	private EditText mHorizontalRebarSpacingEditText;
	private Spinner mHeightSpinner;
	private Spinner mDiameterSpinner;
	private Spinner mVerticalRebarSpinner;
	private ImageView mDownArrowIcon;
	private ImageView mUpArrowIcon;
	private Button mCalculationButton;
	private LinearLayout mOptionalRebarLayout;
	private Callback mCallback; //******* The hosting Activity most implement this interface ***********
	private ViewStub viewStub;
	private View mRebarLayoutinflated;
	
	private double mHeight; 
	private double mDiameter; 
	private int mNumberOfColumns;
	private int mNumberOfVerticalRebars;
	private int mHorizontalRebarSpacing;
	private boolean mHasRebarInput;
	
	
	//This method is run when the fragment is first attached to the hosting activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//The hosting activity is cast to Callback, and is assigned to mCallback
		//Note ** If the activity doesn't implement the Callback interface an exception will be thrown
		mCallback = (Callback)activity;
	}

	//This method is run when the fragment is detached from the hosting activity
	@Override
	public void onDetach() {
		super.onDetach();
		//mCallbacks is assigned back to null when the fragment is detached from the hosting activity
		mCallback = null;
	}
	
	//Save the rebar layout if it is open
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(REBAR_LAYOUT_OPEN, mHasRebarInput);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.column_calculator, container, false);
		
		//Keep the optional rebar layout open on rotation change
		if(savedInstanceState != null) {
			mHasRebarInput = savedInstanceState.getBoolean(REBAR_LAYOUT_OPEN);
		}
		
		//Setup the spinners
		mHeightSpinner = (Spinner)v.findViewById(R.id.height_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), 
				R.array.feet_inches, R.layout.spinner_layout);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mHeightSpinner.setAdapter(adapter);
		
		mDiameterSpinner = (Spinner)v.findViewById(R.id.diameter_spinner);
		mDiameterSpinner.setAdapter(adapter);
		mDiameterSpinner.setSelection(1); //Set the default selection to inches
		
		
		//setup all the widgets
		mHeightEditText = (EditText)v.findViewById(R.id.height_edittext);
		mDiameterEditText = (EditText)v.findViewById(R.id.diameter_edittext);
		mNumColumnsEditText = (EditText)v.findViewById(R.id.number_of_columns_edittext);
		mDownArrowIcon = (ImageView)v.findViewById(R.id.rebar_spacing_down_arrow);
		mOptionalRebarLayout = (LinearLayout)v.findViewById(R.id.optional_rebar_layout);
		viewStub = (ViewStub)v.findViewById(R.id.column_rebar_stub);
		
		//Keep the rebar layout open on rotation change
		if(mHasRebarInput) {
			openRebarLayout();
		}

		
		//Show the hidden rebar layout
		mDownArrowIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openRebarLayout();
			}
		});
		
		
		// Set up the calculation button
		// It will get the values from the input boxes and then use the callback
		// method to transfer
		// the values to the hosting activity, so they can be used by the
		// CalculationResultFragment.
		// CalculationResultFragment is then added to the hosting activity.
		mCalculationButton = (Button) v.findViewById(R.id.wall_calculator_button);
		mCalculationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Get data from the input boxes

				// Get the height data. it is in feet
				if (mHeightEditText.getText().length() == 0) {
					mHeight = 0.0;
				} else if (mHeightSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
					mHeight = (Double.valueOf(mHeightEditText.getText().toString()) / 12);
				} else
					mHeight = Double.valueOf(mHeightEditText.getText().toString());
				
				//Get the diameter data. this data should be in inches
				if (mDiameterEditText.getText().length() == 0) {
					mDiameter = 0.0;
				}else if (mDiameterSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
					mDiameter = Double.valueOf(mDiameterEditText.getText().toString());
				}else
					mDiameter = Double.valueOf(mDiameterEditText.getText().toString()) * 12; //mDiameter should be in inches
				
				//Get the data for the number of columns 
				if(mNumColumnsEditText.getText().length() == 0) {
					mNumberOfColumns = 0;
				}else
					mNumberOfColumns = Integer.valueOf(mNumColumnsEditText.getText().toString());
				
				if(mHasRebarInput) {
					//Get the vertical rebar spinner selection.
					mNumberOfVerticalRebars = Integer.valueOf(mVerticalRebarSpinner.getSelectedItem().toString());
					
					//Get the horizontal rebar spacing. It is in inches, if the rebar layout is open
					if(mHorizontalRebarSpacingEditText.getText().length() == 0) {
						mHorizontalRebarSpacing = 0;
					}else
						mHorizontalRebarSpacing = Integer.valueOf(mHorizontalRebarSpacingEditText.getText().toString());
				}
				
				//Calculated the cubic yards, rebar length. gravel is not include for columns
				double cubicYards = Calculations.calculateColumnCubicYards(mHeight, mDiameter) * mNumberOfColumns;
				int rebarLength = (mHasRebarInput) ? Calculations.calculateRebarForColumn(mHeight, mDiameter, mNumberOfVerticalRebars, mHorizontalRebarSpacing) * mNumberOfColumns : 0;
				
				// Call the interface method. This is adding the CalculationResultFragment to the hosting activity
				//	and transferring the values from the input boxes to the hosting activity.
				//Gravel calculations are not used in this fragment, therefore it returns O for the totalSquarefeet parameter, and false for has gravel calculations
				mCallback.onCalculationClick(cubicYards, rebarLength, 0, false);

				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

				
			}//End onClick
		});
		
		if(AddCalculationDialog.isOpen()) {
			//make the plus sign textview visible when adding calculations together.
			mHeaderTextView = (TextView)v.findViewById(R.id.column_header_text_view);
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
			mHorizontalRebarSpacingEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.horizontal_edittext);
			mUpArrowIcon = (ImageView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_up_arrow);
			mRebarSpacingLabel = (TextView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_label);
			
			mVerticalRebarSpinner = (Spinner)mRebarLayoutinflated.findViewById(R.id.vertical_bars_spinner);
			ArrayAdapter<CharSequence> verticalRebarAdapter = ArrayAdapter.createFromResource(getActivity(), 
					R.array.column_vertical_rebars, R.layout.spinner_layout);
			verticalRebarAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
			mVerticalRebarSpinner.setAdapter(verticalRebarAdapter);
			mVerticalRebarSpinner.setSelection(0); //Set the default to 0.
			viewStub = null;
		}else {
			mRebarLayoutinflated.setVisibility(View.VISIBLE);
		}
		
		mRebarSpacingLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mOptionalRebarLayout.setVisibility(View.GONE);
		mHorizontalRebarSpacingEditText.requestFocus();
		mHasRebarInput = true;
		
		//The upArrowIcon listener is defined here because the upArrowIcon is only inflated
		//	if the rebar layout has been opened.
		mUpArrowIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRebarLayoutinflated.setVisibility(View.GONE);
				mOptionalRebarLayout.setVisibility(View.VISIBLE);
				mNumColumnsEditText.requestFocus();
				mHasRebarInput = false;
				
			}
		});
	
	}
}

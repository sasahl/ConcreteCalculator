package org.sasdevelopment.android.concretecalculator.fragment;

/*
 * This fragment display the Steps on slope Calculator input layout.
 *** NOTE: Any Activity that host's this fragment must implement the Callback interface
 */
import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;
import org.sasdevelopment.android.concretecalculator.dialogs.StepsDiagram;
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

public class StepsSlopeCalculatorFragment extends Fragment {
	
	private static final String REBAR_LAYOUT_OPEN = "rebarLayoutOpen";
	private static final double ONE_FOOT = 12.0;
	public static final int STEPS_SLOPE = 1;
	
	private TextView mRebarSpacingLabel;
	private TextView mHeaderTextView;
	private EditText mWidthEditText;
	private EditText mRiseEditText;
	private EditText mRunEditText;
	private EditText mDepthEditText;
	private EditText mRebarWidthSpacingEditText;
	private EditText mRebarLengthSpacingEditText;
	private EditText mNumberOfStepsEditText;
	private Spinner mWidthSpinner;
	private CheckBox mIncludeGravelCheckBox;
	private ImageView mDownArrowIcon;
	private ImageView mUpArrowIcon;
	private ImageView mStepsDiagram;
	private Button mCalculationButton;
	private LinearLayout mOptionalRebarLayout;
	private Callback mCallback;  //******* The hosting Activity most implement this interface ***********
	private ViewStub viewStub;
	private View mRebarLayoutinflated;
	
	private boolean mHasRebarInput;
	private boolean mHasGravelInput;
	private double mWidth;
	private double mRise;
	private double mRun;
	private double mDepth; //This is the measurement from top/front of an individual step to the bottom of the concrete sloping up.
	private double mCubicYards;
	private int mWidthRebarSpacing;
	private int mLengthRebarSpacing;
	private int mNumberOfSteps;
	private int mTotalRebarLength;
	
	//This method is run when the fragment is first attached to the hosting activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// The hosting activity is cast to Callback, and is assigned to
		// mCallback
		// Note ** If the activity doesn't implement the Callback interface an
		// exception will be thrown
		mCallback = (Callback) activity;
	}

	// This method is run when the fragment is detached from the hosting
	// activity
	@Override
	public void onDetach() {
		super.onDetach();
		// mCallbacks is assigned back to null when the fragment is detached
		// from the hosting activity
		mCallback = null;
	}

	// Save the rebar layout if it is open
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(REBAR_LAYOUT_OPEN, mHasRebarInput);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.steps_slope_calculator, container, false);
		
		//Keep the optional rebar layout open on rotation change
		if(savedInstanceState != null) {
			mHasRebarInput = savedInstanceState.getBoolean(REBAR_LAYOUT_OPEN);
		}
		
		//Setup the width spinner
		mWidthSpinner = (Spinner)v.findViewById(R.id.width_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), 
				R.array.feet_inches, R.layout.spinner_layout);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mWidthSpinner.setAdapter(adapter);
		
		mWidthEditText = (EditText)v.findViewById(R.id.width_edittext);
		mRiseEditText = (EditText)v.findViewById(R.id.rise_edittext);
		mRunEditText = (EditText)v.findViewById(R.id.run_edittext);
		mDepthEditText = (EditText)v.findViewById(R.id.depth_edittext);
		mNumberOfStepsEditText = (EditText)v.findViewById(R.id.number_of_steps_edittext);
		mIncludeGravelCheckBox = (CheckBox) v.findViewById(R.id.include_gravel_checkbox);
		mOptionalRebarLayout = (LinearLayout)v.findViewById(R.id.optional_rebar_layout);
		mDownArrowIcon = (ImageView)v.findViewById(R.id.rebar_spacing_down_arrow);
		mStepsDiagram = (ImageView)v.findViewById(R.id.steps_diagram);
		viewStub = (ViewStub)v.findViewById(R.id.steps_rebar_stub);
		
		if(mHasRebarInput) {
			openRebarLayout();
		}
		
		//Handles the click event on the check box. mHasGravelCalculation will be passed on to the 
		//	hosting activity and from there on to the calculations result fragment. 
		//	Then the gravel results will be shown if the user has checked the box, otherwise it will not.
		mIncludeGravelCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mHasGravelInput = isChecked;	
			}
		});
		
		
		// Show the hidden rebar layout
		mDownArrowIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openRebarLayout();
			}
		});

		
		
		
        mStepsDiagram.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putInt(StepsDiagram.DIAGRAM, STEPS_SLOPE); 
				StepsDiagram diagram = new StepsDiagram();
				diagram.setArguments(args); //Set a bundle with data
				diagram.show(getFragmentManager(), "steps dialog");
			}
		});
		
		
		// Set up the calculation button
		// It will get the values from the input boxes and then use the callback
		// method to transfer
		// the values to the hosting activity, so they can be used by the
		// CalculationResultFragment.
		// CalculationResultFragment is then added to the hosting activity.
		mCalculationButton = (Button)v.findViewById(R.id.steps_calculator_button);
		mCalculationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Get the data from the input boxes
				
				// Get the width data. it is in feet
				if (mWidthEditText.getText().length() == 0) {
					mWidth = 0.0;
				} else if (mWidthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
					mWidth = Double.valueOf(mWidthEditText.getText().toString());
				} else
					mWidth = (Double.valueOf(mWidthEditText.getText().toString()) * ONE_FOOT); //mWidth should be in inches
				
				//Get the rise
				if(mRiseEditText.getText().length() == 0) {
					mRise = 0;
				}else {
					mRise = Double.valueOf(mRiseEditText.getText().toString());
				}
				
				//Get the run
				if(mRunEditText.getText().length() == 0) {
					mRun = 0;
				}else {
					mRun = Double.valueOf(mRunEditText.getText().toString());
				}
				
				//Get the depth
				if(mDepthEditText.getText().length() == 0) {
					mDepth = 0;
				}else {
					mDepth = Double.valueOf(mDepthEditText.getText().toString());
				}
				
				//Get the number of steps
				if(mNumberOfStepsEditText.getText().length() == 0) {
					mNumberOfSteps = 0;
				}else {
					mNumberOfSteps = Integer.valueOf(mNumberOfStepsEditText.getText().toString());
				}
				
				
				//Get The rebar spacing is measured in inches
				if(mHasRebarInput) {
					if(mRebarWidthSpacingEditText.getText().length() == 0) {
						mWidthRebarSpacing = 0;
					}else {
						mWidthRebarSpacing = Integer.valueOf(mRebarWidthSpacingEditText.getText().toString());
					}
						
					if(mRebarLengthSpacingEditText.getText().length() == 0) {
						mLengthRebarSpacing = 0;
					}else {
						mLengthRebarSpacing = Integer.valueOf(mRebarLengthSpacingEditText.getText().toString());
					}
				}
				
				//Calculate the different values needed to for the mCallback method call.
				//calculateWallRebar can also be used here for these calculations
				mTotalRebarLength = (mHasRebarInput) ? mTotalRebarLength = Calculations.calculateWallRebar(mWidth / ONE_FOOT, (mRun * mNumberOfSteps) / ONE_FOOT, 
							mWidthRebarSpacing, mLengthRebarSpacing) : 0;
				
				int totalSquareFeet = (int)(((mRun * mNumberOfSteps) / ONE_FOOT) * (mWidth / ONE_FOOT)); 
				mCubicYards = Calculations.calculateStepsSlopeCubicYards(mWidth, mRise, mRun, mDepth, mNumberOfSteps);
				
				mCallback.onCalculationClick(mCubicYards, mTotalRebarLength, totalSquareFeet, mHasGravelInput);
				
	
				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				
			}
		});
		
		if(AddCalculationDialog.isOpen()) {
			//make the plus sign textview visible when adding calculations together.
			mHeaderTextView = (TextView)v.findViewById(R.id.steps_header_text_view);
			mHeaderTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_content_add_circle_outline), null);
			
			mHeaderTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), R.string.adding_calculations_toast, Toast.LENGTH_SHORT).show();
					
				}
			});
		}

		return v;
	}
	
	private void openRebarLayout() {
		if(viewStub != null) {
			mRebarLayoutinflated = viewStub.inflate();
			mRebarWidthSpacingEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.width_spacing_edittext);
			mRebarLengthSpacingEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.length_spacing_edittext);
			mUpArrowIcon = (ImageView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_up_arrow);
			mRebarSpacingLabel = (TextView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_label);
			viewStub = null;
		}else {
			mRebarLayoutinflated.setVisibility(View.VISIBLE);
		}
		
		mRebarSpacingLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mOptionalRebarLayout.setVisibility(View.GONE);
		mRebarWidthSpacingEditText.requestFocus();
		mHasRebarInput = true;
		
		
		mUpArrowIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRebarLayoutinflated.setVisibility(View.GONE);
				mOptionalRebarLayout.setVisibility(View.VISIBLE);
				mNumberOfStepsEditText.requestFocus();
				mHasRebarInput = false;
			}
		});
	}

}

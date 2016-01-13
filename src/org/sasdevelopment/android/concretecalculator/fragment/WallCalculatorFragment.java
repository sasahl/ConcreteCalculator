package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This fragment displays the concrete wall input boxes. 
 * 	The Interface "AdapterView.OnItemSelectedListener" is needed to get feedback from the spinners
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

public class WallCalculatorFragment extends Fragment {
	
	private static final String REBAR_LAYOUT_OPEN = "rebarLayoutOpen";
	
	private TextView mRebarSpacingLabel;
	private TextView mHeaderTextView;
	private Spinner mHeightSpinner;
	private Spinner mLengthSpinner;
	private Spinner mThicknessSpinner;
	private EditText mHeightEditText;
	private EditText mLengthEditText;
	private EditText mThicknessEditText;
	private EditText mRebarVerticalEditText;
	private EditText mRebarHorizontalEditText;
	private Button mCalculationButton;
	private LinearLayout mOptionalInputLayout;
	private ImageView mDownArrowIcon;
	private ImageView mUpArrowIcon;
	private Callback mCallback;  //******* The hosting Activity most implement this interface ***********
	private ViewStub viewStub;
	private View mRebarLayoutinflated;
	
	private double mHeight;
	private double mLength;
	private int mThickness;
	private int mRebarVerticalSpacing;
	private int mRebarHorizontalSpacing;
	private boolean mIsRebarLayoutOpen;
	
	
	
	
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
		outState.putBoolean(REBAR_LAYOUT_OPEN, mIsRebarLayoutOpen);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Inflate the layout
		//See MainFragment onCreateView() for more comments about the parameters of this method
		View v = inflater.inflate(R.layout.wall_calculator, container, false);
		
		if(savedInstanceState != null) {
			mIsRebarLayoutOpen = savedInstanceState.getBoolean(REBAR_LAYOUT_OPEN);
		}
		
		
		
		//Set up the spinners.
		//The spinners take their input from an ArrayAdapter, and here the adapters gets their values from a resource string array
		mHeightSpinner = (Spinner)v.findViewById(R.id.height_spinner);
		// Create an ArrayAdapter using the feet/inches string array and a default layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.feet_inches,
						R.layout.spinner_layout);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		// Apply the adapter to the spinner
		mHeightSpinner.setAdapter(adapter);
		//mHeightSpinner.setOnItemSelectedListener(this); // Set the spinner to listen to the onItemSelectedListener

		// Define a spinner for the length input box. The rest is the same as above
		mLengthSpinner = (Spinner) v.findViewById(R.id.length_spinner);
		mLengthSpinner.setAdapter(adapter);
		
		mThicknessSpinner = (Spinner)v.findViewById(R.id.thickness_spinner);
		mThicknessSpinner.setAdapter(adapter);
		mThicknessSpinner.setSelection(1); 
		
		//Attach the widgets to their input boxes
		mHeightEditText = (EditText)v.findViewById(R.id.height_edittext);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edittext);
		mThicknessEditText= (EditText)v.findViewById(R.id.thickness_edittext);
		mOptionalInputLayout = (LinearLayout)v.findViewById(R.id.optional_rebar_layout);
		mDownArrowIcon = (ImageView)v.findViewById(R.id.rebar_spacing_down_arrow);
		viewStub = (ViewStub)v.findViewById(R.id.wall_rebar_stub);
		
		//Keep the rebar layout open on rotation change
		if(mIsRebarLayoutOpen) {
			openRebarLayout();
		}
		
		
		//Set the listener for the down arrow icon.
		//If clicked, then the rebar layout should become visible
		mDownArrowIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openRebarLayout();
			}
		});
		
		
		//Set up the calculation button
		//It will get the values from the input boxes and then use the callback method to transfer
		//	the values to the hosting activity, so they can be used by the CalculationResultFragment.
		//CalculationResultFragment is then added to the hosting activity.
		mCalculationButton = (Button)v.findViewById(R.id.wall_calculator_button);
		mCalculationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				//Get data from the input boxes
				
				//Get the height data
				if(mHeightEditText.getText().length() == 0) {
					mHeight = 0.0;
				}else
					if(mHeightSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
						mHeight = (Double.valueOf(mHeightEditText.getText().toString()) / 12);
					}else
						mHeight = Double.valueOf(mHeightEditText.getText().toString());
					
				
				//Get the length data
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
				
				
				//Get the vertical rebar spacing data
				if(mIsRebarLayoutOpen) {
					if( mRebarVerticalEditText.getText().length() == 0) {
						mRebarVerticalSpacing = 0;
					}else {
						mRebarVerticalSpacing = Integer.valueOf(mRebarVerticalEditText.getText().toString());
					}
					
					
					//Get the horizontal rebar spacing data
					if(mRebarHorizontalEditText.getText().length() == 0) {
						mRebarHorizontalSpacing = 0;
					}else {
						mRebarHorizontalSpacing = Integer.valueOf(mRebarHorizontalEditText.getText().toString());
					}
				}
				
				
				int totalSquareFeet = (int)(Math.ceil(mHeight * mLength));
				double cubicYards = Calculations.concreteSlabCubicYards(totalSquareFeet, mThickness);
				int rebarLength = (mIsRebarLayoutOpen) ? Calculations.calculateWallRebar(mHeight, mLength, mRebarVerticalSpacing, mRebarHorizontalSpacing) : 0;
				
				// Call the interface method. This is adding the CalculationResultFragment to the hosting activity
				//	and transferring the values from the input boxes to the hosting activity
				// 0 is returned for the totalSquareFeet parameter, because gravel calculations are not used for walls.
				mCallback.onCalculationClick(cubicYards, rebarLength, 0, false);

				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				
			}
		});//End buttonListener 
		
		
		if(AddCalculationDialog.isOpen()) {
			//make the plus sign textview visible when adding calculations together.
			mHeaderTextView = (TextView)v.findViewById(R.id.wall_header_text_view);
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
		//if viewStub is null, then mRebarLayoutinflated has already been inflated
		if(viewStub != null) {
			mRebarLayoutinflated = viewStub.inflate();
			mRebarHorizontalEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.horizontal_edittext);
			mRebarVerticalEditText = (EditText)mRebarLayoutinflated.findViewById(R.id.vertical_edittext);
			mRebarSpacingLabel = (TextView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_label);
			mUpArrowIcon = (ImageView)mRebarLayoutinflated.findViewById(R.id.rebar_spacing_up_arrow);
			viewStub = null;
		}else {
			mRebarLayoutinflated.setVisibility(View.VISIBLE);
		}
		
		mOptionalInputLayout.setVisibility(View.GONE);
		mRebarVerticalEditText.requestFocus();
		mRebarSpacingLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mIsRebarLayoutOpen = true;

		
		//The upArrowIcon listener is defined here because the upArrowIcon is only inflated
		//	if the rebar layout has been opened.
		mUpArrowIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRebarLayoutinflated.setVisibility(View.GONE);
				mOptionalInputLayout.setVisibility(View.VISIBLE);
			//	mThicknessEditText.requestFocus();
				mIsRebarLayoutOpen = false;
			}
		});
	}
	

}

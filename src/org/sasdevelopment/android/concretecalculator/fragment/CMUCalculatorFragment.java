package org.sasdevelopment.android.concretecalculator.fragment;

import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.R;

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

public class CMUCalculatorFragment extends Fragment {
	
	private static final float ONE_FOOT = 12.0f;
	private static final String REBAR_LAYOUT_OPEN = "rebarLayoutOpen";
	
	private Spinner mBlockSizeSpinner;
	private Spinner mHeightSpinner;
	private EditText mHeightEditText;
	private EditText mLengthEditText;
	private EditText mRebarVerticalEditText;
	private EditText mRebarHorizontalEditText;
	private ImageView mUpArrowIcon;
	private ImageView mDownArrowIcon;
	private Button mCalculationButton;
	private LinearLayout mOptionalRebarLayout;
	private CallbacksBlock mCallback; 
	private ViewStub mViewStub;
	private View mRebarLayoutinflated;
	private TextView mRebarSpacingLabel;
	
	private float mHeight;
	private float mLength;
	private int mBlockSize;
	private int mRebarVerticalSpacing;
	private int mRebarHorizontalSpacing;
	private boolean mIsRebarLayoutOpen;
	
	
	/**
	 * Required interface for hosting activities
	 */
	public interface CallbacksBlock {
		
		/*
		 * This method needs to be defined by the hosting activity
		 * The purpose of this method is to start and attach the "CalculationResultFragment"
		 * 	to the hosting activity. CalculationResultFragment will display the result of 
		 * the user input from this fragment.
		 * This method also transfers the values from the input fields 
		 */
		void onCalculationClick(int numBlocks, int numMortarBags, float fillCubicYards, int rebar);
	}
	
	
	//This method is run when the fragment is first attached to the hosting activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//The hosting activity is cast to CallbacksBlocks, and is assigned to mCallbacks
		//Note ** If the activity doesn't implement the CallbacksBlocks interface an exception will be thrown
		mCallback = (CallbacksBlock)activity;
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
		
		View v = inflater.inflate(R.layout.cmu_calculator, container, false);
		
		//Keep the optional rebar layout open on rotation change
		if(savedInstanceState != null) {
			mIsRebarLayoutOpen = savedInstanceState.getBoolean(REBAR_LAYOUT_OPEN);
		}
		
		
		mBlockSizeSpinner = (Spinner)v.findViewById(R.id.block_size_spinner);
		mHeightSpinner = (Spinner)v.findViewById(R.id.height_spinner);
		
		ArrayAdapter<CharSequence> blockAdapter = ArrayAdapter.createFromResource(getActivity(), 
				R.array.cmu_size, R.layout.spinner_layout);
		blockAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mBlockSizeSpinner.setAdapter(blockAdapter);
		mBlockSizeSpinner.setSelection(1);
		
		ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(getActivity(), 
				R.array.feet_inches, R.layout.spinner_layout);
		heightAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mHeightSpinner.setAdapter(heightAdapter);
		
		mHeightEditText = (EditText)v.findViewById(R.id.height_edittext);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edittext);
		mDownArrowIcon = (ImageView)v.findViewById(R.id.rebar_spacing_down_arrow);
		mOptionalRebarLayout = (LinearLayout)v.findViewById(R.id.optional_rebar_layout);
		mViewStub = (ViewStub)v.findViewById(R.id.cmu_rebar_stub);
		
		if(mIsRebarLayoutOpen) {
			openRebarLayout();
		}
		
		
		// Set the listener for the down arrow icon.
		// If clicked, then the rebar layout should become visible
		mDownArrowIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openRebarLayout();
			}
		});
		
		mCalculationButton = (Button)v.findViewById(R.id.cmu_calculator_button);
		mCalculationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Get the block size selection
				mBlockSize = getBlockSize();
				
				//Get the height data in inches
				if(mHeightEditText.getText().length() == 0) {
					mHeight = 0.0f;
				}else
					if(mHeightSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) {
						mHeight = (Float.valueOf(mHeightEditText.getText().toString()));
					}else
						mHeight = Float.valueOf(mHeightEditText.getText().toString()) * 12;
					
				
				//Get the length data in inches
				if(mLengthEditText.getText().length() == 0) {
					mLength = 0.0f;
				}else {
					mLength = Float.valueOf(mLengthEditText.getText().toString()) * 12;
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
				
				int blocks = Calculations.calculateBlocks(mHeight, mLength);
				int mortarBags = Calculations.calculateMortarForBlocks(blocks, mBlockSize);
				float fill = Calculations.calculateFillForBlocks(blocks, mBlockSize); //fill is cubic yards of concrete
				int rebarLength = (mIsRebarLayoutOpen) ? Calculations.calculateWallRebar(mHeight / ONE_FOOT, mLength / ONE_FOOT, mRebarVerticalSpacing, mRebarHorizontalSpacing) : 0;
					
				// Call the interface method. This is adding the CalculationResultFragment to the hosting activity
				//	and transferring the values from the input boxes to the hosting activity
				mCallback.onCalculationClick(blocks, mortarBags, fill, rebarLength);
					
				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				
			}
		});//End button listener
		
		
		
		return v;
	}//End onCreateView
	
	private int getBlockSize() {
		int size = 0;
		int selection = mBlockSizeSpinner.getSelectedItemPosition();
		switch(selection) {
		case 0: size = 6; break;
		case 1: size = 8; break;
		case 2: size = 12; break;
		default: size = 8; break;
		}
		return size;
	}
	
	private void openRebarLayout() {
		// if viewStub is null, then mRebarLayoutinflated has already been inflated
		if (mViewStub != null) {
			mRebarLayoutinflated = mViewStub.inflate();
			mRebarHorizontalEditText = (EditText) mRebarLayoutinflated.findViewById(R.id.horizontal_edittext);
			mRebarVerticalEditText = (EditText) mRebarLayoutinflated.findViewById(R.id.vertical_edittext);
			mRebarSpacingLabel = (TextView) mRebarLayoutinflated.findViewById(R.id.rebar_spacing_label);
			mUpArrowIcon = (ImageView) mRebarLayoutinflated.findViewById(R.id.rebar_spacing_up_arrow);
			mViewStub = null;
		} else {
			mRebarLayoutinflated.setVisibility(View.VISIBLE);
		}

		mOptionalRebarLayout.setVisibility(View.GONE);
		mRebarVerticalEditText.requestFocus();
		mRebarSpacingLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mIsRebarLayoutOpen = true;

		// The upArrowIcon listener is defined here because the
		// upArrowIcon is only inflated
		// if the rebar layout has been opened.
		mUpArrowIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mRebarLayoutinflated.setVisibility(View.GONE);
				mOptionalRebarLayout.setVisibility(View.VISIBLE);
				mLengthEditText.requestFocus();
				mIsRebarLayoutOpen = false;
			}
		});
	}
}

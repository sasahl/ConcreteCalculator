package org.sasdevelopment.android.concretecalculator.fragment;

import org.sasdevelopment.android.concretecalculator.Calculations;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.SendCmuResultDialog;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class CMUResultFragment extends Fragment {
	
	private static final String NUM_BLOCKS = "numBlocks";
	private static final String MORTAR_BAGS = "mortarBags";
	private static final String CONCRETE_FILL = "concreteFill";
	private static final String REBAR_LENGTH = "rebarLength";
	private static final String FILL_SELECTION = "fillSelection";
	
	private TextView mBlocksTextview;
	private TextView mMortarMixTextview;
	private TextView mFillResultTextview;
	private TextView mRebarResultTextview;
	private CallbacksBlockResult mCallbacks;
	private Button mSendButton;
	private Spinner mResultSelectionSpinner;
	
	
	private int mNumBlocks;
	private int mNumBagsOfMortar;
	private float mConcreteFill;
	private int m80ConcreteBagsFill;
	private int m60ConcreteBagsFill;
	private int m40ConcreteBagsFill;
	private int mRebarLength;
	private float mFormattedFill;
	
	
	/**
	 * Required interface for hosting activities
	 */
	public interface CallbacksBlockResult {
		
		/*
		 * These methods needs to be defined by the hosting activity
		 * The purpose of these methods is to update the information coming from the hosting activity
		 */
		 int getBlocks();
		 int getBagsOfMortar();
		 float getConcreteFill();
		 int getRebar();
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
		mCallbacks = (CallbacksBlockResult) activity;
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
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(NUM_BLOCKS, mNumBlocks);
		outState.putInt(MORTAR_BAGS, mNumBagsOfMortar);
		outState.putFloat(CONCRETE_FILL, mConcreteFill);
		outState.putInt(REBAR_LENGTH, mRebarLength);
		outState.putInt(FILL_SELECTION, mResultSelectionSpinner.getSelectedItemPosition());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.cmu_result, container, false);
		
		
		
		mBlocksTextview = (TextView)v.findViewById(R.id.blocks_result_textview);
		mMortarMixTextview = (TextView)v.findViewById(R.id.mortar_result_textview);
		mFillResultTextview = (TextView)v.findViewById(R.id.cubic_yards_result_textview);
		mRebarResultTextview = (TextView)v.findViewById(R.id.rebar_result_textview);
		mResultSelectionSpinner = (Spinner)v.findViewById(R.id.result_selection_spinner);
		
		ArrayAdapter<CharSequence> fillAdapter = ArrayAdapter.createFromResource(getActivity(), 
				R.array.block_fill_selection, R.layout.spinner_layout);
		fillAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		mResultSelectionSpinner.setAdapter(fillAdapter);
		
		if(savedInstanceState != null) {
			mNumBlocks = savedInstanceState.getInt(NUM_BLOCKS);
			mNumBagsOfMortar = savedInstanceState.getInt(MORTAR_BAGS);
			mConcreteFill = savedInstanceState.getFloat(CONCRETE_FILL);
			mRebarLength = savedInstanceState.getInt(REBAR_LENGTH);
			mResultSelectionSpinner.setSelection(savedInstanceState.getInt(FILL_SELECTION));
		}else {
			mNumBlocks = mCallbacks.getBlocks();
			mNumBagsOfMortar = mCallbacks.getBagsOfMortar();
			mConcreteFill = mCallbacks.getConcreteFill();
			mRebarLength = mCallbacks.getRebar();
			mResultSelectionSpinner.setSelection(0);
		}
		
		
		
		//Hide rebar result if the total rebar length is 0.
		if(mRebarLength == 0) {
			TextView rebarLabel = (TextView)v.findViewById(R.id.rebar_textview);
			TextView feetLabel = (TextView)v.findViewById(R.id.feet_textview);
			mRebarResultTextview.setVisibility(View.GONE);
			rebarLabel.setVisibility(View.GONE);
			feetLabel.setVisibility(View.GONE);
		}
		
		mBlocksTextview.setText(String.valueOf(mNumBlocks));
		mMortarMixTextview.setText(String.valueOf(mNumBagsOfMortar));
		mFormattedFill = Math.round(mConcreteFill * 100) / 100.0f; //format to 2 decimals
		mFillResultTextview.setText(String.valueOf(mFormattedFill));
		mRebarResultTextview.setText(String.valueOf(mRebarLength));
		
		
		mSendButton = (Button)v.findViewById(R.id.send_result_button);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String spinnerSelection = (String)mResultSelectionSpinner.getSelectedItem();
				
				//determine what is selected in the spinner.
				if(spinnerSelection.equals(getResources().getString(R.string.pound_80_bags_label))) {
					m80ConcreteBagsFill = Calculations.calculate80PoundBags(mConcreteFill);
					mFormattedFill = 0.0f; //Reset mFormattedFill to 0, so it don't show in the send dialog
					m60ConcreteBagsFill = 0; //Assign 60 and 40 pound bags to 0 in case they already have been calculated, which they would be, if the user goes back to re-select the spinner several times
					m40ConcreteBagsFill = 0;
				}
				else if(spinnerSelection.equals(getResources().getString(R.string.pound_60_bags_label))) {
					m60ConcreteBagsFill = Calculations.calculate60PoundBags(mConcreteFill);
					mFormattedFill = 0.0f;
					m80ConcreteBagsFill = 0;
					m40ConcreteBagsFill = 0;
				}
				else if(spinnerSelection.equals(getResources().getString(R.string.pound_40_bags_label))) {
					m40ConcreteBagsFill = Calculations.calculate40PoundBags(mConcreteFill);
					mFormattedFill = 0.0f;
					m80ConcreteBagsFill = 0;
					m60ConcreteBagsFill = 0;
				}else {
					mFormattedFill = Math.round(mConcreteFill * 100) / 100.0f; //format to 2 decimals
					m80ConcreteBagsFill = 0;
					m60ConcreteBagsFill = 0;
					m40ConcreteBagsFill = 0;
				}
				
				
				//Open a dialog to let the user decide what to include in the message
				SendCmuResultDialog dialog = SendCmuResultDialog.newInstance(mNumBlocks, mNumBagsOfMortar, mRebarLength,
						m80ConcreteBagsFill, m60ConcreteBagsFill, m40ConcreteBagsFill, mFormattedFill);
				dialog.show(getFragmentManager(), "Send result dialog");
				
			}
		});
		
		//Update the concrete fill result textview when user selects a different option in the spinner
		mResultSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
				case 0: float formattedFill = Math.round(mConcreteFill * 100) / 100.0f; //format to 2 decimals
						mFillResultTextview.setText(String.valueOf(formattedFill));
						break;
				case 1: mFillResultTextview.setText(String.valueOf(Calculations.calculate80PoundBags(mConcreteFill)));
						break;
				case 2: mFillResultTextview.setText(String.valueOf(Calculations.calculate60PoundBags(mConcreteFill)));
						break;
				case 3: mFillResultTextview.setText(String.valueOf(Calculations.calculate40PoundBags(mConcreteFill)));
						break;
				default: float formattedFill1 = Math.round(mConcreteFill * 100) / 100.0f; //format to 2 decimals
						 mFillResultTextview.setText(String.valueOf(formattedFill1));
						 break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	
		return v;
	}

	
	

}

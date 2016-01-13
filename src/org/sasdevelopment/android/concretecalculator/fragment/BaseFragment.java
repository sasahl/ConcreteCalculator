package org.sasdevelopment.android.concretecalculator.fragment;

/*
 * This class servers as a super class to DirtExcavationFragment, BackfillFragment, and ConcreteHaulOffFragment
 * It holds common components to these classes.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class BaseFragment extends Fragment {

	protected static final String TOTAL_SQUARE_FEET = "totalSquareFeet";
	protected static final String WIDTH = "width";
	protected static final String LENGTH = "length";
	
	protected EditText mSquarefeetEditText;
	protected EditText mWidthEditText;
	protected EditText mLengthEditText;
	protected EditText mDepthEditText;
	protected Spinner mWidthSpinner;
	protected Spinner mLengthSpinner;
	protected Button mCalculateButton;
	
	
	protected int mTotalSquarefeet = 0;
	protected float mWidth = 0.0f;
	protected float mLength = 0.0f;
	protected float mDepth = 0.0f;
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(TOTAL_SQUARE_FEET, mTotalSquarefeet);
		outState.putFloat(WIDTH, mWidth);
		outState.putFloat(LENGTH, mLength);
	}
	
	/*
	 * This is a helper method that sets the focus on either the total square feet box,
	 * or the width and length boxes. 
	 * If the user clicks total square feet box, then the width and length boxes will be cleared 
	 * of input and the opacity changed in order to let the user know that not all 3 boxes can have input at the same time.
	 * If the user clicks on either the width or length box, then input in the total square feet box 
	 * will be cleared out, and have the opacity changed.
	 */
	public void setFocus() {
		
		if(mSquarefeetEditText.hasFocus()) {
			mSquarefeetEditText.getBackground().setAlpha(255);
			//If input is entered in the total square foot box, then potential width and length input should be cleared out.
			//Change the opacity of the width and length boxes
			disable(mWidthEditText);
			disable(mLengthEditText);
		}	
		else if(mWidthEditText.hasFocus() || mLengthEditText.hasFocus()) {
			//Set to full opacity
			mWidthEditText.getBackground().setAlpha(255);
			mLengthEditText.getBackground().setAlpha(255);
			//if input is entered in the width box, then potential total square foot input should be cleared out
			//Change the opacity of the total square feet box
			disable(mSquarefeetEditText);
		}
		
	}
	
	
	//Input is cleared out of the EditText box
	//The opacity is changed in order to let the user know that the box is "disabled"
	public void disable(EditText et) {
		et.setText("");
		et.getBackground().setAlpha(180);
	}
}

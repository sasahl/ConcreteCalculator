package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This class is where the user enter the information needed to make the slab calculations. (e.g width, height, thickness)
 */

import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.dialogs.AddCalculationDialog;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.Toast;

public class SlabCalculatorFragment extends Fragment implements AdapterView.OnItemSelectedListener{
	
	//private static final String TAG = "SlabCalculatorFragment";
	
	private static final String TOTAL_SQUARE_FEET = "totalSquareFeet";
	private static final String WIDTH = "width";
	private static final String LENGTH = "length";
	
	private Spinner mWidthSpinner;
	private Spinner mLengthSpinner;
	private Callbacks mCallbacks;
	private Button mCalculationButton;
	private EditText mWidthEditText;
	private EditText mLengthEditText;
	private EditText mTotalSquarefeetEditText;
	private EditText mThicknessEditText;
	private TextView mHeaderTextView;
	
	//Declare integers that is used in the mCallbacks method
	private int mTotalSquarefeet = 0;
	private double mWidth = 0.0;
	private double mLength = 0.0;
	private int mThickness = 0;
	
	/**
	 * Required interface for hosting activities
	 */
	public interface Callbacks {
		
		/*
		 * This method needs to be defined by the hosting activity
		 * The purpose of this method is to start and attach the "CalculationResultFragment"
		 * 	to the hosting activity. CalculationResultFragment will display the result of 
		 * the user input from this fragment.
		 * This method also transfers the values from the input fields 
		 */
		void onCalculationClick(int totalSquarefeet, double width, double length, int thickness);
	}
	
	//This method is run when the fragment is first attached to the hosting activity
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//The hosting activity is cast to Callbacks, and is assigned to mCallbacks
		//Note ** If the activity doesn't implement the Callbacks interface an exception will be thrown
		mCallbacks = (Callbacks)activity;
	}

	//This method is run when the fragment is detached from the hosting activity
	@Override
	public void onDetach() {
		super.onDetach();
		//mCallbacks is assigned back to null when the fragment is detached from the hosting activity
		mCallbacks = null;
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(TOTAL_SQUARE_FEET, mTotalSquarefeet);
		outState.putDouble(WIDTH, mWidth);
		outState.putDouble(LENGTH, mLength);
	}
	
	
	

	/*The system calls this when it's time for the fragment to draw its user interface for the first time. 
     * To draw a UI for your fragment, you must return a View from this method that is the root of your fragment's layout. 
     * You can return null if the fragment does not provide a UI.
     *
	 *This method returns the inflated View back to the hosting Activity. (in this case SlabCalculatorActivity). 
	 *The LayoutInflater and ViewGroup parameters are necessary to inflate the layout.
	 *The Bundle will contain data that this method can use to recreate the view from a saved state.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.slab_calculator, container, false);
		
		//These values are saved in order to disable the right input boxes on rotation
		if(savedInstanceState != null) {
			mTotalSquarefeet = savedInstanceState.getInt(TOTAL_SQUARE_FEET);
			mWidth = savedInstanceState.getDouble(WIDTH);
			mLength = savedInstanceState.getDouble(LENGTH);
		}
		
		
		//define a spinner for the width input box
		mWidthSpinner = (Spinner)v.findViewById(R.id.width_spinner);
		//Create an ArrayAdapter using the feet/inches string array and a default layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.feet_inches, 
				R.layout.spinner_layout);
		//Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		//Apply the adapter to the spinner
		mWidthSpinner.setAdapter(adapter); 
		mWidthSpinner.setOnItemSelectedListener(this); //Set the spinner to listen to the onItemSelectedListener 
		
		//Define a spinner for the length input box. The rest is the same as above
		mLengthSpinner = (Spinner)v.findViewById(R.id.length_spinner);
		mLengthSpinner.setAdapter(adapter);
		mLengthSpinner.setOnItemSelectedListener(this); //Set the spinner to listen to the onItemSelectedListener
		
		
		mWidthEditText = (EditText)v.findViewById(R.id.width_edit_text);
		mLengthEditText = (EditText)v.findViewById(R.id.length_edit_text);
		mTotalSquarefeetEditText = (EditText)v.findViewById(R.id.total_squarefoot_edit_text);
		mThicknessEditText = (EditText)v.findViewById(R.id.thickness_edit_text);
		
		
		//This will take care of the right boxes stays disabled and enabled on rotation change
		//The mThicknessEditText onFocusChange listener is implemented for this purpose
		if(mTotalSquarefeet != 0) {
			disable(mWidthEditText);
			disable(mLengthEditText);
		}
		else if(mWidth != 0 || mLength != 0) {
			disable(mTotalSquarefeetEditText);
		}
		
	
		//Set up the onFocusChange listener for the total square feet input box.
		mTotalSquarefeetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
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
		mThicknessEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//check for input in the total square feet box. If there is input, then totalSquareFeet is assigned 1
				// and width and length is assigned to 0. If no input, then vice-versa.
				if(mTotalSquarefeetEditText.getText().length() == 0) {
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
		
		
		
		
		// Set up the calculation button
		mCalculationButton = (Button) v.findViewById(R.id.slab_calculator_button);
		mCalculationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//Get data from the input boxes 
				
				//Check for input in the total square feet box. An EditText box will return a charSequence
				// so checking its length is better than checking for an empty string. An EditText won't return null.
				if(mTotalSquarefeetEditText.getText().length() == 0) {
					mTotalSquarefeet = 0;
				}
				else {
					mTotalSquarefeet = Integer.valueOf(mTotalSquarefeetEditText.getText().toString());
				}
				
				//Check for input in the width box.
				if(mWidthEditText.getText().length() == 0) {
					mWidth = 0.0;
				}
				else {
					//Get the selection from the width spinner
					if(mWidthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches))) 
						mWidth =  (Double.valueOf(mWidthEditText.getText().toString()) / 12);
					else
						mWidth = Double.valueOf(mWidthEditText.getText().toString());
				}
				
				//Check for input in the length box
				if(mLengthEditText.getText().length() == 0) {
					mLength = 0.0;
				}
				else {
					//Get the selection from the length spinner
					if(mLengthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.inches)))
						mLength = (Double.valueOf(mLengthEditText.getText().toString()) / 12);
					else
						mLength = Double.valueOf(mLengthEditText.getText().toString());
				}
				
				//Check for input in the thickness box
				if(mThicknessEditText.getText().length() == 0) {
					mThickness = 0;
				}
				else {
					mThickness = Integer.valueOf(mThicknessEditText.getText().toString());
				}
				
				// Call the interface method. This is adding the CalculationResultFragment to the hosting activity
				//	and transferring the values from the input boxes to the hosting activity
				mCallbacks.onCalculationClick(mTotalSquarefeet , mWidth, mLength, mThickness);

				// Close the soft keyboard when the button is clicked, so it
				// doesn't cover the CalculationResultFragment
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});// End button listener
		
		if(AddCalculationDialog.isOpen()) {
			//make the plus sign textview visible when adding calculations together.
			mHeaderTextView = (TextView)v.findViewById(R.id.slab_header_text_view);
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
	

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 * 
	 * These 2 methods below is needed to implement the AdapterView.OnItemSelectedListener. This interface is needed for the spinners.
	 * The spinner object will receive the user selection in the 'inItemSelected()' method
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		 parent.getItemAtPosition(position).toString();
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * This is a helper method that sets the focus on either the total square feet box,
	 * or the width and length boxes. 
	 * If the user clicks total square feet box, then the width and length boxes will be cleared 
	 * of input and the opacity changed in order to let the user know that not all 3 boxes can have input at the same time.
	 * If the user clicks on either the width or length box, then input in the total square feet box 
	 * will be cleared out, and have the opacity changed.
	 */
	private void setFocus() {
		
		if(mTotalSquarefeetEditText.hasFocus()) {
			mTotalSquarefeetEditText.getBackground().setAlpha(255);
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
			disable(mTotalSquarefeetEditText);
		}
		
	}
	
	
	//Input is cleared out of the EditText box
	//The opacity is changed in order to let the user know that the box is "disabled"
	private void disable(EditText et) {
		et.setText("");
		et.getBackground().setAlpha(180);
	}
	
	

}//End  SlabCalculatorFragment class

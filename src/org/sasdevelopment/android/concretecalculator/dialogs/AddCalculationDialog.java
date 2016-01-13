package org.sasdevelopment.android.concretecalculator.dialogs;

/*
 * This dialog will show when the user clicks the 'Add Calculations' button in the CalculationsResultFragment.
 * It lets the user decide what new calculation he/she would like to add to the current one.
 */
import org.sasdevelopment.android.concretecalculator.CalculationsResultList;
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.activity.CalculationsActivity;
import org.sasdevelopment.android.concretecalculator.activity.SlabCalculatorActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class AddCalculationDialog extends DialogFragment {
	
	private static final String TAG = "AddCalculationDialog";
	
	private TextView mSlabTextView;
	private TextView mWallTextView;
	private TextView mFootingTextView;
	private TextView mColumnTextView;
	private TextView mStepTextView;
	private static boolean sIsDialogOpen;
	
	/*
	 * This static method indicates whether this dialog is open or not.
	 * It is called in CalculationActivity and SlabCalculationActivity in the onDestroy() method.
	 *  This is used to determine if the saved result, from the Activity where the "Add Calculation" 
	 *  button was clicked, should be cleared out or not. 
	 *  The saved result is cleared out in the activity's onDestroy() method, so when the user clicks the 
	 *  "back button" the saved result is reset to 0. Unless this dialog is still open. That would indicate
	 *  that the user might just have selected the wrong choice in this dialog, and wants to go back and 
	 *  reselect another option. 
	 *  e.g. The user has done a calculation in the Slab Activity. Now he wants to add steps to this result. He
	 *  clicks the "Add Calculation" button. This dialog pops up, and he accidentally clicks the column choice 
	 *  instead of the step choice. Now he wants to go back here to reselect the steps. In that case we still
	 *  want to keep the saved calculation, and not have it cleared out by the onDestroy() method.
	 */
	public static boolean isOpen() {
		return sIsDialogOpen;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		sIsDialogOpen = true;
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.add_calculation, null);
		
		mSlabTextView = (TextView)v.findViewById(R.id.slab_textview);
		mSlabTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts SlabCalculatorFragment
				Intent i = new Intent(getActivity(), SlabCalculatorActivity.class);
				startActivity(i);
			}
		});
		
		mWallTextView = (TextView)v.findViewById(R.id.wall_textview);
		mWallTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts WallCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.WALL);
				startActivity(i);
			}
		});
		
		mFootingTextView = (TextView)v.findViewById(R.id.footing_textview);
		mFootingTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts FootingCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.FOOTING);
				startActivity(i);
			}
		});
		
		mColumnTextView = (TextView)v.findViewById(R.id.columns_textview);
		mColumnTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts ColumnCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.COLUMN);
				startActivity(i);
			}
		});
		
		mStepTextView = (TextView)v.findViewById(R.id.steps_textview);
		mStepTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StepsDialog dialog = new StepsDialog();
				dialog.show(getFragmentManager(), "steps dialog");
			}
		});
		
		
		//Build the dialog by using an AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.add_calculation_dialog_title);
		builder.setView(v);
						
		return builder.create();
	}

	/*
	 * This method is invoked by the system and is called when a user
	 * clicks the back button, or outside the dialog without doing anything with the 
	 * the dialog.
	 * This method is similar to onCancel(), but onDismiss() is also called after
	 * a user has chosen to add another calculation, but then regrets the decision and
	 * clicks the "< "Home enable button.
	 * 
	 * Clear out the CalcualtionsResultList if the user cancels the dialog.
	 */
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		Log.d(TAG, "onDismiss()");
		CalculationsResultList.get(getActivity()).clear();
		sIsDialogOpen = false;
	}

	
	
	

	
	
	
	
	

}

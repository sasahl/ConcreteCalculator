package org.sasdevelopment.android.concretecalculator.dialogs;

/*
 * This dialog shows a large image of the steps diagram if the user
 * clicks the small image included in the 2 step calculator fragments next to the title.
 * 
 * information is being sent from the fragments via the arguments bundle, in order to 
 * determine what fragment is opening this dialog, and therefore determine what image to show. 
 */
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.fragment.StepsPlatformCalculatorFragment;
import org.sasdevelopment.android.concretecalculator.fragment.StepsSlopeCalculatorFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class StepsDiagram extends DialogFragment {
	
	public static final String DIAGRAM = "Diagram";
	
	private ImageView mStepsDiagram;
	private int mParentFragment;

	@SuppressLint("InflateParams") 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.steps_diagram, null);
		
		//Get the information sent from the fragment in order to determine which image to show.
		Bundle args = getArguments();
		mParentFragment = args.getInt(DIAGRAM);
		
		mStepsDiagram = (ImageView)v.findViewById(R.id.steps_diagram_imageView);
		if(mParentFragment == StepsPlatformCalculatorFragment.STEPS_PLATFORM) {
			mStepsDiagram.setBackgroundResource(R.drawable.steps_platform1_3);
		}else if(mParentFragment == StepsSlopeCalculatorFragment.STEPS_SLOPE) {
			mStepsDiagram.setBackgroundResource(R.drawable.steps1_4);
		}
		
		//Build the dialog by using an AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		
		
		return builder.create();
	}

	
}

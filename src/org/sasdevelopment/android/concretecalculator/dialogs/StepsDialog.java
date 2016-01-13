package org.sasdevelopment.android.concretecalculator.dialogs;

/*
 * This dialog displays 2 kinds/styles of steps the user can choose from when clicking the 
 * steps calculator button on the front page. It is implemented by a dialog with a title
 * and 2 ImageView widgets. The top one displaying a slope style set of steps, and the bottom one
 * displaying a set of steps with a platform as the top step.
 */
import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.activity.CalculationsActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class StepsDialog extends DialogFragment {

	
	private ImageView mStepsSlopeImageView;
	private ImageView mStepsPlatformImageView;

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.steps_dialog, null);
		
		mStepsSlopeImageView = (ImageView)v.findViewById(R.id.slope_steps_imageView);
		mStepsPlatformImageView = (ImageView)v.findViewById(R.id.platform_steps_imageView);
		
		mStepsSlopeImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.STEP_SLOPE);
				startActivity(i);	
			}
		});
		
		mStepsPlatformImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.STEP_PLATFORM);
				startActivity(i);	
			}
		});
		
		//Build the dialog by using an AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.steps_dialog_title);
		builder.setView(v);
		
		
		return builder.create();
		
	}

}

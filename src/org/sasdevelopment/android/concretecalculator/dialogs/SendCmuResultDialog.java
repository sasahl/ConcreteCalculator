package org.sasdevelopment.android.concretecalculator.dialogs;

import org.sasdevelopment.android.concretecalculator.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SendCmuResultDialog extends DialogFragment {
	
	private EditText mJobNameEditText;
	private CheckBox mBlocksCheckbox;
	private CheckBox mMortarMixCheckbox;
	private CheckBox mFillCubicYardsCheckbox;
	private CheckBox mFill80PoundBagCheckbox;
	private CheckBox mFill60PoundBagCheckbox;
	private CheckBox mFill40PoundBagCheckbox;
	private CheckBox mRebarCheckbox;
	
	//Used for visibility purposes only
	private TextView mFillCubicYardsTextView;
	private TextView mFill80PoundBagsTextView;
	private TextView mFill60PoundBagsTextView;
	private TextView mFill40PoundBagsTextView;
	private TextView mRebarTextView;
	
	
	private int mNumBlocks;
	private int mNumMortarBags;
	private int mRebar;
	private int mFill80PoundBags;
	private int mFill60PoundBags;
	private int mFill40PoundBags;
	private float mFillCubicYards;
	
	private void setResultValues(int blocks, int mortarBags, int rebar, int fill80Poundbags, int fill60Poundbags, int fill40Poundbags, float fillCubicYards) {
		mNumBlocks = blocks;
		mNumMortarBags = mortarBags;
		mRebar = rebar;
		mFill80PoundBags = fill80Poundbags;
		mFill60PoundBags = fill60Poundbags;
		mFill40PoundBags = fill40Poundbags;
		mFillCubicYards = fillCubicYards;
	}
	
	
	//Empty constructor
	public SendCmuResultDialog() {}
		
	//newInstance method
	public static SendCmuResultDialog newInstance(int blocks, int mortar, int rebar, int fill80Poundbags, int fill60Poundbags, int fill40Poundbags, float fillCubicYards) {
			
		SendCmuResultDialog s = new SendCmuResultDialog();
		s.setResultValues(blocks, mortar, rebar, fill80Poundbags, fill60Poundbags, fill40Poundbags, fillCubicYards);
		return s;
	}
	
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View v = inflater.inflate(R.layout.send_cmu_dialog, null);
		
		mJobNameEditText = (EditText)v.findViewById(R.id.jobname_edittext);
		mBlocksCheckbox = (CheckBox)v.findViewById(R.id.blocks_checkbox);
		mMortarMixCheckbox = (CheckBox)v.findViewById(R.id.mortar_mix_checkbox);
		mFillCubicYardsCheckbox = (CheckBox)v.findViewById(R.id.fill_cubic_yards_checkbox);
		mFill80PoundBagCheckbox = (CheckBox)v.findViewById(R.id.fill_80_pound_bag_checkbox);
		mFill60PoundBagCheckbox = (CheckBox)v.findViewById(R.id.fill_60_pound_bag_checkbox);
		mFill40PoundBagCheckbox = (CheckBox)v.findViewById(R.id.fill_40_pound_bag_checkbox);
		mRebarCheckbox = (CheckBox)v.findViewById(R.id.rebar_checkbox);
		
		mFillCubicYardsTextView = (TextView)v.findViewById(R.id.fill_cubic_yards_textview);
		mFill80PoundBagsTextView = (TextView)v.findViewById(R.id.fill_80_pound_bag_textview);
		mFill60PoundBagsTextView = (TextView)v.findViewById(R.id.fill_60_pound_bag_textview);
		mFill40PoundBagsTextView = (TextView)v.findViewById(R.id.fill_40_pound_bag_textview);
		mRebarTextView = (TextView)v.findViewById(R.id.rebar_textview);
		
		//Hide views if they have a value of 0. Because this would indicate that the user don't care about that value.
		// and therefore there is no reason to show the check-box to the user.
		if(mRebar == 0) {
			mRebarCheckbox.setVisibility(View.GONE);
			mRebarTextView.setVisibility(View.GONE);
		}
		if(mFillCubicYards == 0) {
			mFillCubicYardsCheckbox.setVisibility(View.GONE);
			mFillCubicYardsTextView.setVisibility(View.GONE);
		}
		if(mFill80PoundBags == 0) {
			mFill80PoundBagCheckbox.setVisibility(View.GONE);
			mFill80PoundBagsTextView.setVisibility(View.GONE);
		}
		if(mFill60PoundBags == 0) {
			mFill60PoundBagCheckbox.setVisibility(View.GONE);
			mFill60PoundBagsTextView.setVisibility(View.GONE);
		}
		if(mFill40PoundBags == 0) {
			mFill40PoundBagCheckbox.setVisibility(View.GONE);
			mFill40PoundBagsTextView.setVisibility(View.GONE);
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.send_dialog_title);
		builder.setView(v);
		builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//Implicit intent that always gives the user an option of how the message should be sent 
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, buildMessage());
				i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.send_cmu_dialog_subject));
				i = Intent.createChooser(i, getResources().getString(R.string.send_report));
				startActivity(i);
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		
		return builder.create();
	}
	
	
	// This helper method should be called in the send button listener
	// returns a string representing the message to be sent.
	private String buildMessage() {

		StringBuilder builder = new StringBuilder();

		// Include the job name if it is filled in
		if (mJobNameEditText.getText().length() != 0) {
			builder.append(getResources().getString(R.string.jobname_str))
			     	.append(" ")
			     	.append(mJobNameEditText.getText())
			     	.append("\n");
		}

		// Include the selected check box items
		if (mBlocksCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.blocks))
			       .append(" ")
			       .append(String.valueOf(mNumBlocks))
			       .append("\n");
		}
		if (mMortarMixCheckbox.isChecked()) {
			
			builder.append(getResources().getString(R.string.mortar_mix))
			       .append(" ")
			       .append(String.valueOf(mNumMortarBags))
			       .append(" ")
			       .append(getResources().getString(R.string.bags))
			       .append("\n");
		}
		if (mRebarCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.rebar))
			       .append(" ")
			       .append(String.valueOf(mRebar))
			       .append(" ")
			       .append(getResources().getString(R.string.feet))
			       .append("\n");
		}
		if (mFillCubicYardsCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.fill_cubic_yards))
			       .append(" ")
			       .append(String.valueOf(mFillCubicYards))
			       .append("\n");
		}
		if (mFill80PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.fill_80_pound_bags))
			       .append(" ")
			       .append(String.valueOf(mFill80PoundBags))
			       .append("\n");
		}
		if (mFill60PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.fill_60_pound_bags))
			       .append(" ")
			       .append(String.valueOf(mFill60PoundBags))
			       .append("\n");
		}
		if (mFill40PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.fill_40_pound_bags))
			       .append(" ")
			       .append(String.valueOf(mFill40PoundBags))
			       .append("\n");
		}

		return builder.toString();
	}
	
	
}

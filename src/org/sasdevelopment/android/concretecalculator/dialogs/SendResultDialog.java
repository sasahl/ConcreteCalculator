package org.sasdevelopment.android.concretecalculator.dialogs;

/*
 * This fragment gets shown when the user clicks the "Send Result" button in the CalculationsResultFragment
 * It opens a dialog asking the user what information from the calculated result he/she would like to include and send
 *  to the recipient of the  message.
 *  
 *  The message could for example be:
 *  
 *  Job Name: Smith
 *  Cubic Yards: 1.5
 *  80 Pound Bags: 64
 *  Rebar: 224 feet
 *  Gravel: 2.5 tons
 */

import org.sasdevelopment.android.concretecalculator.ConcreteCalculationsResult;
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

public class SendResultDialog extends DialogFragment {
	
	public static final String TAG = "SendResultDialog";
	
	private EditText mJobNameEditText;
	private CheckBox mCubicYardsCheckbox;
	private CheckBox mCostCheckbox;
	private CheckBox m80PoundBagCheckbox;
	private CheckBox m60PoundBagCheckbox;
	private CheckBox m40PoundBagCheckbox;
	private CheckBox mRebarCheckbox;
	private CheckBox mGravelCheckbox;
	
	//These are used just for visibility purposes
	private TextView mCostTextView;
	private TextView m80PoundBagTextView;
	private TextView m60PoundBagTextView;
	private TextView m40PoundBagTextView;
	private TextView mGravelTextView;
	private TextView mRebarTextView;
	
	ConcreteCalculationsResult mCcr; //Holds the values needed for the message being sent.
	
	//This method gets the result values from the ConcreteCalculationsResult object, 
	//It is called in the static newInstance method
	private void setResultValues(ConcreteCalculationsResult ccr) {
		mCcr = ccr;
	}
	
	//Empty constructor
	public SendResultDialog() {}
	
	//newInstance method
	public static SendResultDialog newInstance(ConcreteCalculationsResult ccr) {
		
		SendResultDialog s = new SendResultDialog();
		s.setResultValues(ccr);
		return s;
	}
	

	//Build a custom AlertDialog. This is called after onCreate()
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		@SuppressLint("InflateParams")
		View v = inflater.inflate(R.layout.send_dialog, null);
		
		mJobNameEditText = (EditText)v.findViewById(R.id.jobname_edittext);
		mCubicYardsCheckbox = (CheckBox)v.findViewById(R.id.cubic_yards_checkbox);
		mCostCheckbox = (CheckBox)v.findViewById(R.id.cost_checkbox);
		m80PoundBagCheckbox = (CheckBox)v.findViewById(R.id.eighty_pound_bag_checkbox);
		m60PoundBagCheckbox = (CheckBox)v.findViewById(R.id.sixty_pound_bag_checkbox);
		m40PoundBagCheckbox = (CheckBox)v.findViewById(R.id.fourty_pound_bag_checkbox);
		mRebarCheckbox = (CheckBox)v.findViewById(R.id.rebar_checkbox);
		mGravelCheckbox = (CheckBox)v.findViewById(R.id.gravel_checkbox);
		
		mCostTextView = (TextView)v.findViewById(R.id.cost_textview);
		m80PoundBagTextView = (TextView)v.findViewById(R.id.eighty_pound_bag_textview);
		m60PoundBagTextView = (TextView)v.findViewById(R.id.sixty_pound_bag_textview);
		m40PoundBagTextView = (TextView)v.findViewById(R.id.fourty_pound_bag_textview);
		mGravelTextView = (TextView)v.findViewById(R.id.gravel_textview);
		mRebarTextView = (TextView)v.findViewById(R.id.rebar_textview);
		
		//Prevent these check boxes to appear in the dialog if their corresponding values equal 0.
		//if 80 pound bag is 0, then 60 and 40 pound bags will also be 0.
		if(mCcr.getM80PoundBags() == 0) {
			m80PoundBagCheckbox.setVisibility(View.GONE);
			m80PoundBagTextView.setVisibility(View.GONE);
			m60PoundBagCheckbox.setVisibility(View.GONE);
			m60PoundBagTextView.setVisibility(View.GONE);
			m40PoundBagCheckbox.setVisibility(View.GONE);
			m40PoundBagTextView.setVisibility(View.GONE);
		}
		if(mCcr.getmConcretePrice() == 0) {
			mCostCheckbox.setVisibility(View.GONE);
			mCostTextView.setVisibility(View.GONE);
		}
		if(mCcr.getmGravelTons() == 0) {
			mGravelCheckbox.setVisibility(View.GONE);
			mGravelTextView.setVisibility(View.GONE);
		}
		if(mCcr.getRebarLength() == 0) {
			mRebarCheckbox.setVisibility(View.GONE);
			mRebarTextView.setVisibility(View.GONE);
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
				i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.send_dialog_subject));
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
	
	
	//This helper method should be called in the send button listener
	//returns a string representing the message to be sent.
	private String buildMessage() {
		
		StringBuilder builder = new StringBuilder();

		// Include the job name if it is filled in
		if (mJobNameEditText.getText().length() != 0) {
			builder.append(getResources().getString(R.string.jobname_str))
			     	.append(" ")
			     	.append(mJobNameEditText.getText())
			     	.append("\n");
		}
		
		//Include the selected check box items
		if(mCubicYardsCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.cubic_yards))
			       .append(" ")
			       .append(String.valueOf(mCcr.getmCubicYards()))
			       .append("\n");
			
		}
		if(mCostCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.cost))
			       .append(" ")
			       .append(String.valueOf(mCcr.getmConcretePrice()))
			       .append("\n");
		}
		if(m80PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.pound_80_bags))
			       .append(" ")
			       .append(String.valueOf(mCcr.getM80PoundBags()))
			       .append("\n");
		}
		if(m60PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.pound_60_bags))
			       .append(" ")
			       .append(String.valueOf(mCcr.getM60PoundBags()))
			       .append("\n");
		}
		if(m40PoundBagCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.pound_40_bags))
			       .append(" ")
			       .append(String.valueOf(mCcr.getM40PoundBags()))
			       .append("\n");
		}
		if(mRebarCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.rebar))
			       .append(" ")
			       .append(String.valueOf(mCcr.getRebarLength()))
			       .append(" ")
			       .append(getResources().getString(R.string.feet))
			       .append("\n");
		}
		if(mGravelCheckbox.isChecked()) {
			builder.append(getResources().getString(R.string.gravel))
			       .append(" ")
			       .append(String.valueOf(mCcr.getmGravelTons()))
			       .append(" ")
			       .append(getResources().getString(R.string.tons))
			       .append("\n");
		}

		
		return builder.toString();
	}

}

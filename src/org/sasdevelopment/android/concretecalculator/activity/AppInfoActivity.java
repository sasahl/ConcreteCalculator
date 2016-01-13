package org.sasdevelopment.android.concretecalculator.activity;

/*
 * This class displays information about the app. The user can useful information about how the app is 
 * designed, and how it should be used. 
 */
import org.sasdevelopment.android.concretecalculator.R;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class AppInfoActivity extends ActionBarActivity {
	
	private TextView mAppInfoTextview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.app_info);
		
		//Underline the section headers
		//This can also be done in the strings file by adding <u>...</u>, but I have
		//seen a lot of comments saying it does'nt always work.
		mAppInfoTextview = (TextView)findViewById(R.id.app_info);
		mAppInfoTextview.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
	}

}

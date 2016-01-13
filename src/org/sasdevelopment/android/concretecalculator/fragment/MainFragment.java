package org.sasdevelopment.android.concretecalculator.fragment;
/*
 * This fragment is hosted by the MainActivity. It inflates the main page layout and handles the button clicks and interactions
 */

import org.sasdevelopment.android.concretecalculator.R;
import org.sasdevelopment.android.concretecalculator.activity.*;
import org.sasdevelopment.android.concretecalculator.dialogs.StepsDialog;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainFragment extends Fragment{
	
	private Button mSlabCalculatorButton;
	private Button mWallCalculatorButton;
	private Button mFootingCalculatorButton;
	private Button mColumnCalculatorButton;
	private Button mStepsCalculatorButton;
	private Button mBlockCalculatorButton;
	private Button mDirtExcavationButton;
	private Button mBackfillButton;
	private Button mConcreteHaulOffButton;
	/*
	 * The system calls this when creating the fragment. Within your implementation, 
	 * you should initialize essential components of the fragment that you want to retain when the fragment is paused or stopped, 
	 * then resumed.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


    /*The system calls this when it's time for the fragment to draw its user interface for the first time. 
     * To draw a UI for your fragment, you must return a View from this method that is the root of your fragment's layout. 
     * You can return null if the fragment does not provide a UI.
     *
	 *This method returns the inflated View back to the hosting Activity. (in this case MainActivity). 
	 *The LayoutInflater and ViewGroup parameters are necessary to inflate the layout.
	 *The Bundle will contain data that this method can use to recreate the view from a saved state.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//container is needed to configure the widgets properly.
		//container is the root ViewGroup of this particular View, and the attributes of 
		//the container is needed in order to arrange the view properly.
		//eg. rootView could be LinearLayout, and specific view a TextView
		//false is passed to prevent the system from attaching the view to the root element. The system will throw an
		//	exception if that happens, but the rootViews attributes are still needed. 
		//More good information here: http://possiblemobile.com/2013/05/layout-inflation-as-intended/
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		
		mSlabCalculatorButton = (Button)v.findViewById(R.id.concrete_slab_button);
		mSlabCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts SlabCalculatorFragment
				Intent i = new Intent(getActivity(), SlabCalculatorActivity.class);
				startActivity(i);
			}
		});
		
		mWallCalculatorButton = (Button)v.findViewById(R.id.concrete_wall_button);
		mWallCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts WallCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.WALL);
				startActivity(i);
			}
		});
		
		mFootingCalculatorButton = (Button)v.findViewById(R.id.concrete_footing_button);
		mFootingCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts FootingCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.FOOTING);
				startActivity(i);
			}
		});
		
		mColumnCalculatorButton = (Button)v.findViewById(R.id.concrete_column_button);
		mColumnCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start the activity that hosts ColumnCalculatorFragment
				Intent i = new Intent(getActivity(), CalculationsActivity.class);
				i.putExtra(CalculationsActivity.FRAGMENT_TO_HOST, CalculationsActivity.COLUMN);
				startActivity(i);
			}
		});
		
		mStepsCalculatorButton = (Button)v.findViewById(R.id.concrete_steps_button);
		mStepsCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StepsDialog dialog = new StepsDialog();
				dialog.show(getFragmentManager(), "steps dialog");
				
			}
		});
		
		mBlockCalculatorButton = (Button)v.findViewById(R.id.cmu_block_button);
		mBlockCalculatorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CMUActivity.class);
				startActivity(i);
				
			}
		});
		
		mDirtExcavationButton = (Button)v.findViewById(R.id.excavate_dirt_button);
		mDirtExcavationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), BackfillActivity.class);
				i.putExtra(BackfillActivity.FRAGMENT_TO_HOST, BackfillActivity.DIRT);
				startActivity(i);
			}
		});
		
		mBackfillButton = (Button)v.findViewById(R.id.backfill_button);
		mBackfillButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), BackfillActivity.class);
				i.putExtra(BackfillActivity.FRAGMENT_TO_HOST, BackfillActivity.BACKFILL);
				startActivity(i);
				
			}
		});
		
		mConcreteHaulOffButton = (Button)v.findViewById(R.id.concrete_tear_out_button);
		mConcreteHaulOffButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), BackfillActivity.class);
				i.putExtra(BackfillActivity.FRAGMENT_TO_HOST, BackfillActivity.HAUL_OFF);
				startActivity(i);
				
			}
		});
		
		return v;	
		
	}


	/*
	 * The system calls this method as the first indication that the user is leaving the fragment 
	 * (though it does not always mean the fragment is being destroyed). 
	 * This is usually where you should commit any changes that should be persisted beyond the current user session 
	 * (because the user might not come back).
	 */
	@Override
	public void onPause() {
		super.onPause();
	}	

}

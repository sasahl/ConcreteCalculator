package org.sasdevelopment.android.concretecalculator;

import android.content.Context;
/*
 * This class holds a list of a calculated result. It is stored in an ArrayList of
 * ConcreteCalculationsResult objects.
 * It is designed as a singleton, so there is only one instance of this class which
 * will return the arrayList if it is not null.
 * 
 * This class is used to store the result outcome from the CalculationsResultFragment when the 
 * user hits the 'Add Calculation' button. Then it can be retrieved if the user wants to add several
 * calculations together.
 */

public class CalculationsResultList {
	
	private ConcreteCalculationsResult mCumulatedResult;
	private static CalculationsResultList sResult; //'s' indicates it is a static instance variable
	@SuppressWarnings("unused")
	private Context mAppContext;
	private boolean mIsEmpty;
	
	/*
	 * private constructor
	 */
	private CalculationsResultList(Context appContext) {
		mAppContext = appContext;
		mCumulatedResult = new ConcreteCalculationsResult();
		mIsEmpty = true;
	}
	
	/*
	 * Returns the CalculationsResultList object if one has been created, else
	 * it will create a new one and return that object.
	 * 
	 * It is common in Android to include a Context parameter. That allows the singleton
	 * to start activities, access project resources, find the applications private storage etc.
	 */
	public static CalculationsResultList get(Context c) {
		if(sResult == null) {
			sResult = new CalculationsResultList(c.getApplicationContext());
		}
		return sResult;
	}
	
	/*
	 * Returns ConcreteCalculationsResult object
	 */
	public ConcreteCalculationsResult getCumulatedResult() {
		return mCumulatedResult;
	}
	
	//reset mCumulatedresult to null
	public void clear() {
		mCumulatedResult = null;
		mIsEmpty = true;
	}
	
	public void setCumulatedResult(ConcreteCalculationsResult ccr) {
		mCumulatedResult = ccr;
		mIsEmpty = false;
	}
	
	/*
	 * Returns true if empty, otherwise false
	 */
	public boolean isEmpty() {
		return mIsEmpty;
	}
	
	

}

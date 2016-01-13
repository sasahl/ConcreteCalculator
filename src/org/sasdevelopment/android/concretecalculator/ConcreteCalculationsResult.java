package org.sasdevelopment.android.concretecalculator;

import java.io.Serializable;

/*
 * This class is used by 'CalculationResultFragment' to fill out all it's fields
 * An object of this class will be passed to it by the hosting activity.
 * 
 * It implements Serializable so an instance object of this class can be saved in a Bundle.
 */

public class ConcreteCalculationsResult implements Serializable {
	
	private static final long serialVersionUID = 5648796521456l;
	private double mCubicYards;
	private double mConcretePrice;
	private int m80PoundBags;
	private int m60PoundBags;
	private int m40PoundBags;
	private int mRebarLength;
	private double mGravelTons;
	
	//Default constructor
	public ConcreteCalculationsResult() {
		mCubicYards = 0.0;
		mConcretePrice = 0.0;
		m80PoundBags = 0;
		m60PoundBags = 0;
		m40PoundBags = 0;
		mRebarLength = 0;
		mGravelTons = 0.0;
	}
	
	public double getmGravelTons() {
		return mGravelTons;
	}

	public void setmGravelTons(double mGravelTons) {
		
		//Format to 2 decimals
		double temp = Math.round(mGravelTons * 100) / 100.0;
		this.mGravelTons = temp;
	}

	public int getRebarLength() {
		return mRebarLength;
	}

	public void setRebarLength(int rebarLength) {
		this.mRebarLength = rebarLength;
	}

	public double getmCubicYards() {
		return mCubicYards;
	}
	public void setmCubicYards(double mCubicYards) {
		this.mCubicYards = mCubicYards;
	}
	public int getM80PoundBags() {
		return m80PoundBags;
	}
	public void setM80PoundBags(int m80PoundBags) {
		this.m80PoundBags = m80PoundBags;
	}
	public int getM60PoundBags() {
		return m60PoundBags;
	}
	public void setM60PoundBags(int m60PoundBags) {
		this.m60PoundBags = m60PoundBags;
	}
	public int getM40PoundBags() {
		return m40PoundBags;
	}
	public void setM40PoundBags(int m40PoundBags) {
		this.m40PoundBags = m40PoundBags;
	}
	public double getmConcretePrice() {
		return mConcretePrice;
	}

	public void setmConcretePrice(double mConcretePrice) {
		this.mConcretePrice = mConcretePrice;
	}
	
	public void add(ConcreteCalculationsResult c) {
		 this.mCubicYards += c.getmCubicYards();
		 this.mConcretePrice += c.getmConcretePrice();
		 this.m80PoundBags += c.getM80PoundBags();
		 this.m60PoundBags += c.getM60PoundBags();
		 this.m40PoundBags += c.getM40PoundBags();
		 this.mRebarLength += c.getRebarLength();
		 this.mGravelTons += c.getmGravelTons();
	}
	

}

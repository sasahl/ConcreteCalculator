package org.sasdevelopment.android.concretecalculator.interfaces;
/*
 * This interface is used by the different activities that serves as communication between the fragments 
 * 	that hold the user input values, and the fragment that displays the calculated result of those inputs.
 * 
 * Each of the fragments that serves as the user input fragments, have an instance variable of this interface. that 
 * instance variable is cast into the hosting activity in the onAttach() method. The hosting activity will have to 
 * implement this interface. 
 */

public interface Callback {
	
	void onCalculationClick(double cubicYards, int rebarLengthInFeet, int totalSquareFeet, boolean hasGravelCalculation);

}

package org.sasdevelopment.android.concretecalculator;


/*
 * This is a utility class that is holding all the different calculations needed to do the concrete calculations
 * It consist of all static methods, and no instance of this class can be made.
 * This class is declared final and therefore cannot be used as a super class.
 */
public final class Calculations {
	
	private static final double PI = 3.14159265359;
	private static final double RIGHT_ANGLE = 90.0;
	private static final double CUBIC_INCHES_PER_CUBIC_FOOT = 1728.0;
	private static final double ONE_FOOT = 12.0;
	public static final int CUBIC_FEET_PER_CUBIC_YARD = 27;
	private static final double PERCENTAGE_ADDED = 1.1; //Add 10%
	private static final int GRAVEL_POUND_PER_CUBIC_FOOT = 105;
	private static final int POUNDS_IN_A_TON = 2000;
	private static final int CMU_HEIGHT = 8; //in inches
	private static final int CMU_LENGTH = 16; //in inches
	private static final float MORTAR_PER_BLOCK_6 = 0.08f;
	private static final float MORTAR_PER_BLOCK_8 = 0.09f; //Estimate of 80# bags of mortar needed per block.
	private static final float MORTAR_PER_BLOCK_12 = 0.12f;
	private static final float CUBIC_YARDS_PER_BLOCK_6 = 0.010111f;
	private static final float CUBIC_YARDS_PER_BLOCK_8 = 0.010534f; //Estimate of cubic yards of concrete needed to fill a block.
	private static final float CUBIC_YARDS_PER_BLOCK_12 = 0.014389f;
	private static final float EXCAVATED_CLAY_VOLUME = 1.3f; //this represents the excavated volume compared to compacted volume of the soil
	private static final float GRAVEL_TONS_PER_CUBIC_YARD = 1.4F;
	private static final float CONCRETE_SAND_TONS_PER_CUBIC_YARD = 1.3F;
	private static final float MASONRY_SAND_TONS_PER_CUBIC_YARD = 1.2F;
	private static final float BASE_TONS_PER_CUBIC_YARD = 1.5F;
	private static final int CONCRETE_POUNDS_PER_CUBIC_FOOT = 150;

	
	//Prevent anyone from instantiating this class
	private Calculations() {}
	
	/*
	 * Returns a value of type double  representing the cubic yards of concrete needed.
	 * thickness must be in inches
	 */
	public static double concreteSlabCubicYards(int totalSquarefeet, int thickness) {
		
		//Calculate cubic yards
		return (totalSquarefeet * (thickness / ONE_FOOT)) / CUBIC_FEET_PER_CUBIC_YARD;
	}
	
	public static double concretePrice(double priceOfConcrete, double cubicYards) {
		return priceOfConcrete * cubicYards;
	}
	
	
	/*
	 * @param: cubicYards represents the total amount of concrete needed.
	 * return: The number of 80# bags of concrete mix needed given the cubicYards amount.
	 */
	public static int calculate80PoundBags(double cubicYards) {
		
		//Standard estimate of cubic feet in a 80# bag of concrete mix
		double cubicFeetPerBag = 0.6;
		
		return (int)(Math.ceil((cubicYards * CUBIC_FEET_PER_CUBIC_YARD) / cubicFeetPerBag));
	}
	
	/*
	 * @param: cubicYards represents the total amount of concrete needed.
	 * return: The number of 60# bags of concrete mix needed given the cubicYards amount.
	 */
	public static int calculate60PoundBags(double cubicYards) {
		
		//Standard estimate of cubic feet in a 60# bag of concrete mix
		double cubicFeetPerBag = 0.45;
		
		return (int)(Math.ceil((cubicYards * CUBIC_FEET_PER_CUBIC_YARD) / cubicFeetPerBag));
	}
	
	/*
	 * @param: cubicYards represents the total amount of concrete needed.
	 * return: The number of 40# bags of concrete mix needed given the cubicYards amount.
	 */
	public static int calculate40PoundBags(double cubicYards) {
		
		//Double the 80# bag calculation
		return calculate80PoundBags(cubicYards) * 2;
	}
	
	/*
	 * @param: totalSquareFeet represents the area that needs rebar
	 * @param: spacing represents how close the rebar should be spaced. 
	 * 	spacing is in inches.
	 * return: the total linear length of rebar (in feet) needed to fill the area with 10% added.
	 */
	public static int calculateRebar(int totalSquareFeet, double spacing) {
		
		//Convert spacing to feet
		spacing = spacing / ONE_FOOT;
		
		//Calculate the side length of a square whose area is equal to the size of totalSquareFeet.
		double areaSideLength = Math.ceil(Math.sqrt((double)totalSquareFeet));
		
		//Return the rebar needed with 10% added
		return (int)((Math.ceil(areaSideLength * (( areaSideLength / spacing) + 1)) * 2) * PERCENTAGE_ADDED);
	}
	
	
	/*
	 * @param: height represents the height of the wall in feet
	 * @param: lenght represents the length of the wall in feet
	 * @param: rebarSpacingVertical represents the vertical spacing of rebar in inches
	 * @param: rebarSpacingHorizontal represents the horizontal spacing of rebar in inches
	 * 
	 * return: the total linear length of rebar (in feet) needed for the wall with 10% added
	 * 
	 */
	public static int calculateWallRebar(double height, double length, int rebarSpacingVertical, int rebarSpacingHorizontal) {
		
		int totalRebarLength = 0;
		
		//convert rebar spacings to feet
		double verticalSpacing = rebarSpacingVertical / ONE_FOOT;
		double horizontalSpacing = rebarSpacingHorizontal / ONE_FOOT;
		
		//Make sure I don't divide by zero
		if(verticalSpacing > 0) {
			//Calculate the linear feet needed for vertical rebar
			totalRebarLength = (int)(Math.ceil(length / verticalSpacing) * height);
		}
		
		if(horizontalSpacing > 0) {
			//Calculate the linear feet needed for the horizontal rebar and add it to totalRebarLength
			totalRebarLength += (int)(Math.ceil(height / horizontalSpacing) * length);
		}
		
		return (int)(totalRebarLength * PERCENTAGE_ADDED); //Add 10%
	}
	
	 /*
	  * returns the total linear length of rebar (in feet) needed for the footing with 10% added
	  * 
	 * @param: width is in inches
	 * @param: length is in feet
	 * @param: numOfLengthwiseRebar is how many rebars are running lengthwise in the footing
	 * @param: perpendicularSpacing is the spacing in inches. 
	 */
	public static int calculateFootingRebar(double width, double length, int numOfLengthwiseRebars, int perpendicularSpacing) {
		
		int totalRebarLength = (int)(length * numOfLengthwiseRebars);
		 
		if(perpendicularSpacing == 0) return (int)(totalRebarLength * PERCENTAGE_ADDED);
		
		//subtract for inset (3" in each side), and convert to feet
		width = (width - 6) / ONE_FOOT;
		
		//convert to feet
		double barSpacing = perpendicularSpacing / ONE_FOOT;
		int numOfPerpendicularRebars = (int)(length / barSpacing + 1);
		
		return (int)((numOfPerpendicularRebars * width + totalRebarLength) * PERCENTAGE_ADDED);
	}
	
	
	
	/*
	 * @param: totalSquareFeet refers to the area that needs covered with gravel
	 * @param: depth refers to the depth in inches.
	 * 
	 * return: The total tons of gravel needed
	 */
	public static double calculateGravel(int totalSquareFeet, double depth) {
		
		//Convert depth to feet
		depth = depth / ONE_FOOT;
		
		return (totalSquareFeet * depth * GRAVEL_POUND_PER_CUBIC_FOOT) / POUNDS_IN_A_TON;
	}
	
	/*
	 * @param: height of the column in feet.
	 * @param: diameter of the column. It is in inches.
	 * 
	 * return: cubic yards of concrete needed for this column
	 */
	public static double calculateColumnCubicYards(double heigth, double diameter) {
		
		//Convert diameter to feet and get a radius for the column
		double radius = (diameter / ONE_FOOT) / 2.0;
		
		//Formula for column is ( PI * r^2 * H )
		return (PI * (Math.pow(radius, 2)) * heigth) / CUBIC_FEET_PER_CUBIC_YARD;	
	}
	
	/*
	 * The rebar for a column is calculated with "n" number of vertical rebars in the column, and a 
	 * 	horizontal rebar tied every so often up along the vertical bars. Each horizontal rebar form a circle 
	 * 	around the vertical ones.
	 * 
	 * @param: height of the column in feet
	 * @param: diameter of the column in inches.
	 * @param: numberOfVerticalBars refer to vertical rebars in the column
	 * @param: horizontalSpacing refers to the spacing between the horizontal rebars. For example,
	 * 		you could tie a horizontal rebar around the vertical rebars every 6 inches going up in the column.
	 * 
	 * Returns the linear feet of rebar needed for a column as an integer.
	 */
	public static int calculateRebarForColumn(double height, double diameter, int numberOfVerticalBars, int horizontalSpacing) {
		
		//calculate the vertical rebars needed
		double totalRebarLenght = height * numberOfVerticalBars;
		
		//Calculate the circumference of the column diameter - 4".
		//I subtract 4" to adjust for the space there needs to be between the rebar and the column perimeter
		double r = (diameter - 4) / 2.0;
		double circumferenceInFeet  = (2 * PI * r) / ONE_FOOT;
		
		int numberOfHorinzontalBars = 0;
		//Make sure I don't divide by 0
		if(horizontalSpacing > 0) {
			numberOfHorinzontalBars = (int)Math.ceil((height * ONE_FOOT) / horizontalSpacing);
		}
		
		return (int)((numberOfHorinzontalBars * circumferenceInFeet + totalRebarLenght) * PERCENTAGE_ADDED);
	}
	
	/*
	 * In these calculations I have divide the steps up in a top area, and a bottom area.
	 * The top area is just the top triangle area of each thread. As taking the (rise * run / 2).
	 * Anything below that I have treated as just a regular concrete slab area, 
	 * with each end ending in another triangle area.
	 *
	 * Except numSteps, All parameters are in inches
	 * @param: width is the width of the steps.
	 * @param: rise is the height of each individual step
	 * @param: run is the "depth" of each individual step
	 * @param: depth is the height from the 'inside corner' of an individual step down to the bottom of the concrete
	 * @param: numSteps is the total number of steps.
	 * 
	 * Returns the cubic yards of concrete needed for the steps.
	 * 
	 * it's helpful to have a picture of the sloping steps handy.
	 */
	public static double calculateStepsSlopeCubicYards(double width, double rise, double run, double depth, int numSteps)  {
		
		double totalCubicInches = 0;
		//Prevent divide by 0
		if(run == 0 || rise == 0) return 0;
		
		double angleA = Math.toDegrees(Math.atan(rise / run));
		double angleB = RIGHT_ANGLE - angleA;
		double topTriangleAreaInCubicInches = ((rise * run * numSteps) / 2) * width; //cubic inches of step top triangle are 
		
		//Find the top length of the bottom area.
		double topLengthOfBottomArea = (Math.sqrt((Math.pow(rise, 2)) + (Math.pow(run, 2))) * numSteps); //Find hypotenuse of each step and multiply it with the number of steps.
		double l = (depth / (Math.sin(Math.toRadians(angleA)))); //Find the length of the bottom area lower triangle
		double h = (depth / (Math.sin(Math.toRadians(angleB)))); //Find the height of the bottom area lower triangle
		double r = Math.sqrt((Math.pow(l, 2) + (Math.pow(h, 2)))); //r = the hypotenuse of the lower triangle.
		double remainingLengthOfBottomArea = topLengthOfBottomArea - r;
		double bottomAreaInCubicInches = remainingLengthOfBottomArea * depth * width;
		double bottomAreaLowerTriangleInCubicInhes = (h * l / 2) * width;
		
		totalCubicInches = topTriangleAreaInCubicInches + bottomAreaInCubicInches + bottomAreaLowerTriangleInCubicInhes;
		
		return (totalCubicInches / (CUBIC_INCHES_PER_CUBIC_FOOT * CUBIC_FEET_PER_CUBIC_YARD));	
	}
	
	
	/*
	 * Except numSteps, All parameters are in inches
	 * @param: platformLength is the distance from the front of the top step to the back of the concrete
	 * @param: width is the width of the steps.
	 * @param: rise is the height of each individual step
	 * @param: run is the "depth" of each individual step
	 * @param: numSteps is the total number of steps.
	 * 
	 * Returns the cubic yards of concrete needed for the steps.
	 */
	public static double calculateStepsPlatformCubicYards(double platformLength, double rise, double run, double width, int numSteps) {
		
		double totalCubicInches = 0;
		if(run == 0 || rise == 0) return 0;
		
		numSteps -= 1; //Subtract 1 in order not to calculate the platform
		
		double stepsArea = ((rise * run) * ((numSteps * (numSteps + 1)) / 2) * width);
		double platformArea = (rise * (numSteps + 1)) * platformLength * width;
		
		totalCubicInches = stepsArea + platformArea;
		
		return (totalCubicInches / (CUBIC_INCHES_PER_CUBIC_FOOT * CUBIC_FEET_PER_CUBIC_YARD));
	}
	
	/*
	 * @param: height is measured in inches
	 * @param: length is measured in inches.
	 * 
	 * Returns the number of 8x8x16 inch blocks needed for a wall
	 */
	public static int calculateBlocks(float height, float length) {
		
		float verticalBlocks = height / CMU_HEIGHT;
		float lengthBlocks = length / CMU_LENGTH;
		
		return (int)Math.ceil(verticalBlocks * lengthBlocks * 1.05); //5% added
	}
	
	/*
	 * @param: blocks is the amount of 8x8x16 inch blocks in the wall.
	 * @param: size refers to the width of the blocks. ( 6x8x16, 8x8x16, or 12x8x16 ).
	 * 
	 * Return the number of 80# bags of mortar mix needed for the blocks.
	 */
	public static int calculateMortarForBlocks(int blocks, int size) {
		
		int mortarBagsNeeded = 0;
		switch(size) {
		case 6: mortarBagsNeeded = (int)Math.ceil(MORTAR_PER_BLOCK_6 * blocks * PERCENTAGE_ADDED); break; //10% added
		case 8:  mortarBagsNeeded = (int)Math.ceil(MORTAR_PER_BLOCK_8 * blocks * PERCENTAGE_ADDED); break;
		case 12:  mortarBagsNeeded = (int)Math.ceil(MORTAR_PER_BLOCK_12 * blocks * PERCENTAGE_ADDED); break;
		default:  mortarBagsNeeded = (int)Math.ceil(MORTAR_PER_BLOCK_8 * blocks * PERCENTAGE_ADDED); break;
		}
		
		return mortarBagsNeeded;
	 }
	
	/*
	 * @param: blocks is the amount of 8x8x16 inch blocks in the wall.
	 * @param: size refers to the width of the blocks. ( 6x8x16, 8x8x16, or 12x8x16 ).
	 *  
	 * Return the cubic yards needed to fill the block wall with concrete.
	 */
	public static float calculateFillForBlocks(int blocks, int size) {
		
		float cubicYards = 0;
		
		switch(size) {
		//6, 8, and 12 refer to the width of a block
		case 6: cubicYards = CUBIC_YARDS_PER_BLOCK_6 * blocks; break;
		case 8: cubicYards = CUBIC_YARDS_PER_BLOCK_8 * blocks; break;
		case 12: cubicYards = CUBIC_YARDS_PER_BLOCK_12 * blocks; break;
		default: cubicYards = CUBIC_YARDS_PER_BLOCK_8 * blocks; break;
		}
		
		return cubicYards;
	}
	
	/*
	 * returns the amount of calculated dirt in cubic yards.
	 *  Accounts for the extra space dirt takes up when excavated compared to when it's compacted.
	 * 
	 * @param: squarefeet is the total area to be excavated
	 * @param: depth of the area to be excavated. it is in feet
	 */
	public static float calculateExcavatedDirt(int squarefeet, float depth) {
		
		float  area = squarefeet * depth;
		return (area * EXCAVATED_CLAY_VOLUME) / CUBIC_FEET_PER_CUBIC_YARD;	
	}
	
	/*
	 * returns the total calculated tons of the back-fill needed
	 * 
	 * @param: aggregateSelection. This integer comes from the user selection of the aggregate spinner in the 
	 * 			back-fill fragment. The default selection is 1/2" gravel. But all the listed gravel choices have the same
	 * 			tons-per-cubic-yard weight, so therefore the first 3 cases in the switch statement are empty.
	 * @param: cubicYards is the area to be back-filled.
	 */
	public static float calculateBackfill(int aggregateSelection, float cubicYards) {
		
		 float result = 0f;
		 switch(aggregateSelection) {
		 case 0: //case 0 through 3 does the same calculation
		 case 1:
		 case 2:
		 case 3: result = GRAVEL_TONS_PER_CUBIC_YARD * cubicYards; break;
		 case 4: result = CONCRETE_SAND_TONS_PER_CUBIC_YARD * cubicYards; break;
		 case 5: result = MASONRY_SAND_TONS_PER_CUBIC_YARD * cubicYards; break;
		 case 6: result = BASE_TONS_PER_CUBIC_YARD * cubicYards; break;
		 default: result = GRAVEL_TONS_PER_CUBIC_YARD * cubicYards; break;
		 }
		 
		 return result;
	}
	
	/*
	 * returns tons of concrete needed to be hauled off.
	 * 
	 * @param: squarefeet is the total area of concrete that needs demolition
	 * @param: depth is the thickness of the concrete in feet.
	 */
	public static float CalculateConcreteTonsToHaulOff(int squarefeet, float depth) {
		
		float poundsOfConcrete = (squarefeet * depth) * CONCRETE_POUNDS_PER_CUBIC_FOOT;
		return  (poundsOfConcrete / POUNDS_IN_A_TON);
	}

}//End Calculations class

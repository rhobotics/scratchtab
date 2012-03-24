package com.binaryme.tools;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;

import com.binaryme.ScratchTab.InterfaceStaticInitializable;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;

public class ColorPalette implements InterfaceStaticInitializable {

	//colors of dummies, colors used to signalize reaction of a slot dummy to the user
//	public static int colorSlotEmptyFill;	//!! there is an individual fill color for each slot, depending on where it is positioned - it should be pulled by getBodyFillColor()  
    public static int colorSlotEmptyStroke; //general color for all Slot Dummies
	public static int colorSlotActiveFill; 	//general color for all Slot Dummies
	public static int colorSlotActiveStroke; //general color for all Slot Dummies
	public static int colorSlotHoverFill;  	//general color for all Slot Dummies
	public static int colorSlotHoverStroke;  //general color for all Slot Dummies
	
	//concrete ColorPalettes colors
	public static int colorOfControl;
	public static int colorOfLogic	;
	public static int colorOfNumbers;
	public static int colorOfRobot	;
	public static int colorOfTablet;
	public static int colorOfVariables;
	
	//block bodies - the Fill color is pulled from the nextBody pointers
	public static int colorBodyStroke;
	public static int colorSlotStroke;
	public static int colorAppError;


	@Override
	public void onApplicationStart(Activity context) {
		// init the colors
		/** Loads the drawing colors from android's resource files in \res\values\ */

		Resources res = context.getResources();
		
		//dummies
		ColorPalette.colorSlotEmptyStroke	=res.getColor(R.color.colorSlotEmptyStroke);
		ColorPalette.colorSlotActiveFill	=res.getColor(R.color.colorSlotActiveFill);
		ColorPalette.colorSlotActiveStroke	=res.getColor(R.color.colorSlotActiveStroke);
		ColorPalette.colorSlotHoverFill		=res.getColor(R.color.colorSlotHoverFill);
		ColorPalette.colorSlotHoverStroke	=res.getColor(R.color.colorSlotHoverStroke);

		
		//concrete ColorPalettes colors
		ColorPalette.colorOfControl=res.getColor(R.color.control);
		ColorPalette.colorOfLogic=res.getColor(R.color.logic);
		ColorPalette.colorOfNumbers=res.getColor(R.color.numbers);
		ColorPalette.colorOfRobot=res.getColor(R.color.robot);
		ColorPalette.colorOfTablet=res.getColor(R.color.tablet);
		ColorPalette.colorOfVariables=res.getColor(R.color.variables);
		
		//drawing details
		ColorPalette.colorBodyStroke=res.getColor(R.color.colorBodyStroke);
		ColorPalette.colorSlotStroke=res.getColor(R.color.colorSlotStroke);
		ColorPalette.colorAppError=res.getColor(R.color.colorAppError);
	}
	
	
//COLORS AS METHODS
	
	/*
	 * Slot dummies and Shapes with Slots find the body color in a different way.
	 * Slot dummies search for the next body above in the hierarchy and 
	 *  
	 */
	
	/** Method returns the fill color of the given ShapeWithColor, which represents a body of a programming block */
	public static int getBodyFillColor(ShapeWithSlots s){
		try{	
			return s.getBodyColor();
		}catch(Exception e){
			return ColorPalette.colorAppError;
		}
	}
	/** Method finds the body Block, and returns the DEFAULT fill color of it's shape (independant of the blocks state, error) */
	public static int getBodyFillColor(ShapeSlotDummy d){
		try{	
			ShapeWithSlots bodyShape = (ShapeWithSlots) d.getAssociatedBlock().getNextBodyBlock().getShape();
			return bodyShape.getBodyColor();
		}catch(Exception e){
			return ColorPalette.colorAppError;
		}
	}
	/** Method finds the body Block, and returns the CURRENT fill color of it's shape, state dependant */
	public static int getCurrentBodyColor(ShapeSlotDummy d){
		try{	
			ShapeWithSlots bodyShape = (ShapeWithSlots) d.getAssociatedBlock().getNextBodyBlock().getShape();
			return bodyShape.getCurrentFillColor();
		}catch(Exception e){
			return ColorPalette.colorAppError;
		}
	}
	/** Method returns the stroke color of the given SHapeWithColor, which represents a body of a programming block */
	public static int getBodyStrokeColor(ShapeWithSlots s){
		try{	
			return s.getBodyStrokeColor();
		}catch(Exception e){
			return ColorPalette.colorAppError;
		}
	}
	/** Method finds the body Block, and returns the stroke color of it's shape */
	public static int getBodyStrokeColor(ShapeSlotDummy d){
		try{
			ShapeWithSlots bodyShape = (ShapeWithSlots) d.getAssociatedBlock().getNextBodyBlock().getShape();
			return bodyShape.getBodyStrokeColor();
		}catch(Exception e){
			return ColorPalette.colorAppError;
		}
	}
	
	
	
//HELPER METHODS
	
	/** 
	 * Returns an Android color-code of a color, which is darker as paramater color excactly to the given amount.
	 * The darkest color is black (255,255,255).
	 * @param color - a color sample in android color system
	 * @param amount - amount to which the resulting color should be darker than the sample 
	 * @return the darker color
	 */
	public static int makeDarker(int color, float amount){
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		int darkerColor = Color.argb(Color.alpha(color),
		(int) Math.max(0f, red - 255 * amount),
	    (int) Math.max(0f, green - 255 * amount),
	    (int) Math.max(0f, blue - 255 * amount) );
		
		return darkerColor;
	}
	
	/** 
	 * Returns an Android color-code of a color, which is brider as paramater color, exactly to the given amount.
	 * The bridest color is white (255,255,255).
	 * @param color - a color sample in android color system
	 * @param amount - amount to which the resulting color should be darker than the sample 
	 * @return the darker color
	 */
	public static int makeBrider(int color, float amount){
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		int briderColor = Color.argb(Color.alpha(color),
		(int) Math.min(255f, red + 255 * amount),
	    (int) Math.min(255f, green + 255 * amount),
	    (int) Math.min(255f, blue + 255 * amount) );
		
		return briderColor;
	}
}

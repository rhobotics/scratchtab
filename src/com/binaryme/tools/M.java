package com.binaryme.tools;

import icommand.scratchtab.LegoNXTHandler.HowtoStop;

import java.text.DecimalFormat;
import java.util.Arrays;


import com.binaryme.DragDrop.DragHandler;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


/**
 * @author Alexander Friesen
 * This class contains custom Metric convention methods and other tools, which are not class specific.
 *
 */
public class M {
	
	private static int uniqueId = 10;
	private static WindowManager wm;
	private static float CMperINCH = 2.54f;
	
	
	/*
	 * this will replace androids density independent pixels. Android's default Density-independent-pixel are always mapped to one of the following cases
	 *  	0.75 * 1px = 1dp  (ldpi)
	 * 	 	1    * 1px = 1dp  (baseline)
	 *  	1.5  * 1px = 1dp  (hdpi)
	 *  	2    * 1px = 1dp  (xhdpi)
	 *  which makes figures to look not exactly the same on all screens. (probably this is done to reduce possible resource sizes to 4 cases)
	 *  Since we do not use any drawables in this project, we will calculate the exactly dencity-independant-pixels based on physical xdpi/ydpi of the screen.
	 *  
	 *  Further, because of personal preferences of project's author we will calculate the number of pixels per mm, 
	 *  so that the sizes of blocks can be defined in cm and mm.  
	 */
	public static float MMinPx; //1 mm in px on current display
	public static float CMinPx; //1 cm in px on current display
	public static float screenWpx; 
	public static float screenHpx; 
	
	
	public static void initMetrics(WindowManager wm){
		M.wm = wm;
		calculatePixelsPerCM();
	}
	
	
	private static void calculatePixelsPerCM(){
		DisplayMetrics dMetrics = new DisplayMetrics();
		M.wm.getDefaultDisplay().getMetrics(dMetrics);

		int widthPx = dMetrics.widthPixels;
		int heightPx = dMetrics.heightPixels;
		
//		float androidDensityMultiplyer = dMetrics.density;
//		float androidDensityDpi = dMetrics.densityDpi;
//		
		float physicalXPixelsPerInch = dMetrics.xdpi; //149dpi on Xoom,  169dpi on Galaxy
		float physicalYPixelsPerInch = dMetrics.ydpi;
		
		//CALCULATIONS
		//assumed the xdpi rawly equals ydpi, so calculate one common value for density-independent xdpi/ydpi based on xdpi
		CMinPx =  physicalXPixelsPerInch / CMperINCH ; 
		MMinPx = CMinPx / 10;
		
		screenWpx = widthPx;
		screenHpx = heightPx;
		
//		Log.d("MyActivity","heightPx: "+heightPx+" , widthPx: "+widthPx);
//		Log.d("MyActivity","androidDensityMultiplyer: "+androidDensityMultiplyer);
//		Log.d("MyActivity","androidDensityDpi: "+androidDensityDpi);
//		Log.d("MyActivity","physicalXPixelsPerInch: "+physicalXPixelsPerInch + " , physicalYPixelsPerInch: "+physicalYPixelsPerInch);
//		
//		Log.d("MyActivity", "One cm equals so much px: "+cm);
//		Log.d("MyActivity", "One mm equals so much px: "+mm);
//		Log.d("MyActivity", "5 cm equals so much px: "+5*cm);
		
		
	}
	
	public static int mm2px(int mm){
		return Math.round(MMinPx *mm);
	}
	
	public static int cm2px(int cm){
		return Math.round(CMinPx *cm);
	}
	
	public static int max(int[] vals){
		if(vals.length <= 0){
			return 0;
		}
		Arrays.sort(vals);
		return vals[vals.length-1];
	}
	
	/** function for logging. Fills the input string with empty spaces, if it is too short. Makes text look pretty in log. */
	public static String unifyLength(String str, int minlength){
		while(str.length() < minlength){
			str= str+" ";
		}
		return str;
	}
	
	/** generates unique ids */
	public static int getuniqueId(){
		uniqueId= uniqueId+1;
		return uniqueId;
	}
	
	/** retrieves the screen center as a point */
	public static Point getScreenCenter(){
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		return new Point(width/2, height/2);
	}
	
	/** cuts the name of the class from a full class path string */
	public static String getClassName(String fullClassPath){
		try{
			String[] res = fullClassPath.split(".");
			return res[res.length-1];
		}catch(Exception e){
			return "";
		}
	}
	
	/** can turn a MotionEvent action id into readable text */
	public static String motionEventResolve(int id){
		String result = "UNKNOWN";
		switch(id){
			case 0: result ="ACTION_DOWN"; break;
			case 1: result ="ACTION_UP "; break;
			case 2: result ="ACTION_MOVE "; break;
			case 3: result ="ACTION_CANCEL "; break;
			case 4: result ="ACTION_OUTSIDE "; break;
			case 5: result ="ACTION_POINTER_1_DOWN "; break;
			case 6: result ="ACTION_POINTER_UP "; break;
			case 7: result ="ACTION_HOVER_MOVE "; break;
			case 8: result ="ACTION_SCROLL "; break;
			case 9: result ="ACTION_HOVER_ENTER "; break;
			case 10: result ="ACTION_HOVER_EXIT "; break;
		}
		return result;
	}
	/** can turn a MotionEvent action id into readable text */
	public static String dragEventResolve(int id){
		String result = "UNKNOWN";
		switch(id){
			case 1: result ="ACTION_DRAG_STARTED  "; break;
			case 2: result ="ACTION_DRAG_LOCATION "; break;
			case 3: result ="ACTION_DROP  "; break;
			case 4: result ="ACTION_DRAG_ENDED "; break;
			case 5: result ="ACTION_DRAG_ENTERED  "; break;
			case 6: result ="ACTION_DRAG_EXITED  "; break;
		}
		return result;
	}
	
//CONVERT THE DATA TO STRING
	
	/** Converts a number to string, e.g. for sending numbers as system messages. */
	public static String parseDouble(Double num){
		//first cut off all the 0 digits after the decimal point, 
		// because if someone will compare a String with a number, he only write down some digits after teh decimal point, if they are not 0
		// if there are some digits, which are not zero - they will remain
		// 4.0 -> 4
		// 4.2 -> 4.2
		String result = ""; 
		if(((num*10) % 10) != 0){
			//the number had some digits after teh decimal point, which are not 0
			result = num.toString();
		}else{
			//the number had only zeros after the decimal
			DecimalFormat zeroPlaces = new DecimalFormat("0");	
			result = zeroPlaces.format(num);
		}
		return result;
	}
	
	
	
//SOFT KEYBOARD METHODS
	
	/** Hides the virtual keyboard. */
	public static void hideKeyboard(View currentView){
		InputMethodManager imm = (InputMethodManager)AppRessources.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
	}
	
	
//CONTROL
	/** the safer place in the app. Stopps all processes. The user can touch this if something goes wrong. */
	public static void stopAll(){
		try{
			//stop the NXT robot if exists 
			AppRessources.legoNXTHandler.stopAllMotors(HowtoStop.IMMEDIATELY);
		}catch(NullPointerException e){
			//if it does not exist a NullPointerException will be thrown. Dont need to do anything.
		}
		
		//run the method which will go through all stack threads and interrupt every thread.
		ExecutionHandler.stopEverything();
		
		//additionally cancel Drag. It's an emergency solution, for the case, when dragging flag is set to true and th block disappears somewhere, so that no other dragging process can be started.
		DragHandler.undoCurrentDrag();
	}

}

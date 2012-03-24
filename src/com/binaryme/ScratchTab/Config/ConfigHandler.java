package com.binaryme.ScratchTab.Config;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.binaryme.ScratchTab.InterfaceStaticInitializable;
import com.binaryme.ScratchTab.R;
import com.binaryme.tools.M;


public class ConfigHandler implements InterfaceStaticInitializable {
	
	/** The speed of command execution. If the command is executed faster than this minimum time - it will wait until the next command is executed. Useful for command execution visualization by lighting the blocks. */
	public static int minimalExecutionTime=0;
	
	/** This Variable sets the scale level, which determines the size of the Blocks in the BlockPane. The scala is the same as for the global zoom.  */
	public static float scaleLevelForBlocksInPane=1f;
	
	/** Configure the text style for the labels */
	public static Paint textPaintLabel = new Paint(); 

	/** Configure the text style for the labels */
	public static Paint textPaintBlockTypeNavigation = new Paint(); 
	
	/** Default Slot Dummy Values */
	public static String DEFAULT_VALUE_TEXTFIELD ="";
	public static int 	 DEFAULT_VALUE_NUMFIELD =0;
	public static double DEFAULT_VALUE_NUMDOUBLEFIELD =0;
	public static boolean DEFAULT_VALUE_BOOLEAN =false;
	public static Object DEFAULT_VALUE_COMMAND =null;
	
	
	@Override
	public void onApplicationStart(Activity context) {
		
		Resources res = context.getResources();
		minimalExecutionTime=res.getInteger(R.integer.executionTimeMiddle);
		
		scaleLevelForBlocksInPane=1f;
		
		
		//textpaint in labels
		float TEXT_SIZE = M.cm2px(1)/3.26f;
		textPaintLabel.setAntiAlias(true);
		textPaintLabel.setStrokeWidth(2);
		textPaintLabel.setTextSize(TEXT_SIZE);
		textPaintLabel.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		textPaintLabel.setColor(Color.WHITE);
		
		//textpaint in block pane navigation
		float TEXT_SIZE_NAV = M.cm2px(1)/4f;
		textPaintBlockTypeNavigation.setAntiAlias(true);
		textPaintBlockTypeNavigation.setStrokeWidth(2);
		textPaintBlockTypeNavigation.setTextSize(TEXT_SIZE_NAV);
		textPaintBlockTypeNavigation.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		textPaintBlockTypeNavigation.setFakeBoldText(true);
		textPaintBlockTypeNavigation.setColor(Color.WHITE);
		
	} 

}

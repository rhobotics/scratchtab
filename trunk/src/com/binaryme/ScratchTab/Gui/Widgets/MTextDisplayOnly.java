package com.binaryme.ScratchTab.Gui.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

/** Class to display some text, which should not be editable by the user. 
 *  Instances of this class do not position themselves, instead they are dropped into Widgets, which handle positioning and drop events.  
 *  Here all the settings and drawing, which differ from android's native implementation of the spinner should be done */
public class MTextDisplayOnly extends MTextField {
	
	String defaultValue = "";
	
	public MTextDisplayOnly(Context context) {
		super(context);
		
		//disallow user input
		this.setFocusable(false);
		
		//change the look
		this.setTextColor(Color.WHITE);
		this.setBackgroundColor(Color.BLACK);
		
		//default text is an empty string
		this.setText("");
	}
	
	public String getValueAsString() {
		String result =defaultValue; 
		if(super.getValueAsString()!="") result = super.getValueAsString();
		return result;
	}
	
	/** do not handle user input at all */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

}

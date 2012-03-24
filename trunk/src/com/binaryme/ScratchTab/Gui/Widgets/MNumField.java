package com.binaryme.ScratchTab.Gui.Widgets;

import com.binaryme.ScratchTab.Config.ConfigHandler;

import android.content.Context;
import android.text.InputType;

/** Class to represent an NumField.
 *  Instances of this class do not position themselves, instead they are dropped into Widgets, which handle positioning and drop events.  
 *  Here all the settings and drawing, which differ from android's native implementation of the spinner should be done */
public class MNumField extends MTextField {
	
	private int defaultValue = ConfigHandler.DEFAULT_VALUE_NUMFIELD;

	public MNumField(Context context) {
		super(context);
		
		//hint
		setHint(""+defaultValue);
		
		//allow only numbers as input
		this.setInputType(InputType.TYPE_CLASS_NUMBER);				//keyboard allows decimal numbers with a point as a dlimiter
	}
	
	public Double getValueAsDouble() {
		double result =defaultValue; 
		if(super.getValueAsString()!="") result = Double.parseDouble(super.getValueAsString());
		return result;
	}

}

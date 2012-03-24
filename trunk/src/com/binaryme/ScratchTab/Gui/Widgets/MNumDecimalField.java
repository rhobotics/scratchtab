package com.binaryme.ScratchTab.Gui.Widgets;

import android.content.Context;
import android.text.method.DigitsKeyListener;

import com.binaryme.ScratchTab.Config.ConfigHandler;

public class MNumDecimalField extends MTextField {
	
	private double defaultValue = ConfigHandler.DEFAULT_VALUE_NUMDOUBLEFIELD;

	public MNumDecimalField(Context context) {
		super(context);
		
		
		//hint
		setHint(""+defaultValue);

		//make the input decimal
		DigitsKeyListener MyDigitKeyListener =	new DigitsKeyListener(true, true); // first true : is signed, second one : is decimal
		this.setKeyListener( MyDigitKeyListener );
		
		//Raw input type
//		this.setRawInputType(Configuration.KEYBOARD_12KEY);   // NUMBERS displays the keyboard, but same problem
//		this.setRawInputType(Configuration.KEYBOARD_NOKEYS);  // all chars
//		this.setRawInputType(Configuration.KEYBOARD_UNDEFINED);  //no keyboard
//		this.setRawInputType(Configuration.KEYBOARD_QWERTY);  // - . gehen. Sobald das widget einmal neu gezeichnet wird, geht alles.
		

	}
	
	public double getValueAsDouble() {
		double result =defaultValue; 
		if(super.getValueAsString().length() > 0){ 
			result = Double.parseDouble(super.getValueAsString()); 
		}
		return result;
	}
	
}
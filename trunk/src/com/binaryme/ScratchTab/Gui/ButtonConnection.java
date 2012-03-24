package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class ButtonConnection extends ToggleButton {
	
		private Drawable offLight;
		private Drawable onLight;
		private Drawable nxtOn;
		private Drawable nxt;
		private LayerDrawable buttonOnBg;
		private LayerDrawable buttonOffBg;
		
		private Drawable beforeBlinking;
		
		private BlinkingAnimation blinkingAnimation;
	

		public ButtonConnection(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public ButtonConnection(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public ButtonConnection(Context context) {
			super(context);						
			init();
		}
		private void init(){
			//get drawables
			Resources res = getResources();
			nxtOn				= 	res.getDrawable(com.binaryme.ScratchTab.R.drawable.nxt_on);
			nxt					= 	res.getDrawable(com.binaryme.ScratchTab.R.drawable.nxt);
			onLight 			= 	res.getDrawable(com.binaryme.ScratchTab.R.drawable.btn_toggle_on);
			offLight			= 	res.getDrawable(com.binaryme.ScratchTab.R.drawable.btn_toggle_off);
			Drawable buttonBg	= 	res.getDrawable(android.R.drawable.btn_default_small);

			
			Drawable[] layersoff = new Drawable[3];
			layersoff[0] = buttonBg;
			layersoff[1] = onLight;
			layersoff[2] = nxtOn;
			buttonOnBg = new LayerDrawable(layersoff); //contains combined toggle button bg with light off
			
			Drawable[] layerson = new Drawable[3];
			layerson[0] = buttonBg;
			layerson[1] = offLight;
			layerson[2] = nxt;
			buttonOffBg = new LayerDrawable(layerson); //contains combined toggle button bg with light on
			
			
			//need no text
			this.setTextOff("");
			this.setTextOn("");
			this.setText("");

			this.setBackgroundDrawable(buttonOffBg);

			//custom red background-color
//			this.getBackground().setColorFilter(0xFFFF0000, Mode.MULTIPLY);
		}
		
		//blinking animation
		private class BlinkingAnimation extends AnimationDrawable{
			BlinkingAnimation(){
				addFrame(buttonOnBg, 500);
				addFrame(buttonOffBg, 500);
				setOneShot(false);
			}
		}
		
		@Override
		public void setChecked(boolean checked) {
			super.setChecked(checked);
			if(isChecked()){
				this.setBackgroundDrawable(buttonOnBg);
			}else{
				this.setBackgroundDrawable(buttonOffBg);
			}
		}
		
		/** makes the button blinking forever */
		public void setBlinkingOn(){
			beforeBlinking=this.getBackground();
			blinkingAnimation = new BlinkingAnimation();
			//because of an android bug the animation has to be starte as following:
			this.post(new Runnable() {
				@Override
				public void run() {
					blinkingAnimation.start();
				}
			});
			
			this.setBackgroundDrawable(blinkingAnimation);
		}
		/** returns the last button background */
		public void setBlinkingOff(){
			this.setBackgroundDrawable(beforeBlinking);
		}
		
}

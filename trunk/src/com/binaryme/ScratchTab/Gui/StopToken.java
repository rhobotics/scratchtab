package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.binaryme.tools.M;

public class StopToken extends Button {

		public StopToken(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public StopToken(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public StopToken(Context context) {
			super(context);						
			init();
		}
		
		private void init(){
			//size
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(M.cm2px(1),M.cm2px(1));
//			this.setLayoutParams(lp);
			
			//text
			this.setText("stop");
			
			//color
//			this.setBackgroundColor(Color.RED);
//			this.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);	//green
			this.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);	//red
			
			//size
			this.setWidth(M.cm2px(6));
			this.setHeight(M.cm2px(1));
		}
		
		
//TOUCH		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			M.stopAll();
			return true;
		}
}

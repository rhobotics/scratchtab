package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.binaryme.ScratchTab.R;
import com.binaryme.tools.M;

public class CopyOfStopToken extends ImageView implements OnTouchListener{

		public CopyOfStopToken(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public CopyOfStopToken(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public CopyOfStopToken(Context context) {
			super(context);						
			init();
		}
		
		private void init(){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.stop);
			this.setImageDrawable(drawable);

			//size
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(M.cm2px(1),M.cm2px(1));
			this.setLayoutParams(lp);
			
			//register listeners
			this.setOnTouchListener(this); //to react on touch and drag of the token
		}
		
		
//DRAG AND DROP
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			M.stopAll();
			return true;
		}
}

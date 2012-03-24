package com.binaryme.ScratchTab.Gui;

import com.binaryme.tools.M;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/** Wraps the BlockPane to reduce it's width. 
 *  This subclass of HorizontalScrollView wont interrupt the touch events, but it will still use them by rewriting the {@link #dispatchTouchEvent(MotionEvent)}  */ 
public class BlockPaneHorizontalScrollView extends HorizontalScrollView {

	public BlockPaneHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
	}
	public BlockPaneHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public BlockPaneHorizontalScrollView(Context context) {
		super(context);
		init();
	}
	
	void init(){
		//disable blue shining which appear when scrolling to the end is done
		setOverScrollMode(OVER_SCROLL_NEVER);
	}
	
	
	
//TOUCH
	
	/** The pass useful TouchEvent manually to the OnTouch Method */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d("pane","HorizontalScroll dispatch get a touch event "+M.motionEventResolve(ev.getAction()) );
		boolean sup = super.dispatchTouchEvent(ev);
		boolean result = sup;
		
		//TEST - try passing Motion events to onTouch
		this.onTouchEvent(ev);
		
		return result;
	}

	/** Do not intercept the Touch events so that the touch events can reach the BlockPane(subclass of ScrollView) inside of this container and
	 *  horizontal and vertical scrolling is possible at the same time */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    //Call super first because it does some hidden motion event handling
	    super.onInterceptTouchEvent(ev);
	    boolean result = false;
	    return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = super.onTouchEvent(ev);
		
		switch(ev.getAction()){
			case MotionEvent.ACTION_UP:
				this.smoothScrollTo(0, 0);
				Log.d("pane","HorizontalScroll ACTION_UP received. Scroll to the left." );
			break;
		}
		Log.d("pane","HorizontalScroll get a touch event "+M.motionEventResolve(ev.getAction()) );
		return result;
	}
	
}

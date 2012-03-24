package com.binaryme.ScrollViewDual;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class mScrollView extends ScrollView {
		

	private VelocityTracker mVelocityTracker;
	private int  mActivePointerId;
	final ViewConfiguration configuration = ViewConfiguration.get(getContext());
	private final int mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	private final int mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
	private float oldTouchY;
	private boolean isDragging = false;
	
	public mScrollView(Context context) {
		super(context);
		init();
	}
	
	public mScrollView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}

	public mScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	//TODO debugging loop
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("MyActivity","draw ScrollView");
		super.onDraw(canvas);
	}
	//TODO debugging loop
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d("MyActivity","dispatchdraw ScrollView");
		super.dispatchDraw(canvas);
	}
	
	
	void init(){
		setFadingEdgeLength(0);
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		
		//add scrollbars
		setHorizontalScrollBarEnabled(true);
		setVerticalScrollBarEnabled(true);
		setScrollbarFadingEnabled(false);
		awakenScrollBars(0,true);
	}


	public boolean onTouchEvent(MotionEvent ev) {
    	int maxScrollRange = computeVerticalScrollRange();    

		if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
			// Don't handle edge touches immediately -- they may actually belong to one of our
			// descendants.
			return false;
		}
		
	      
	      //for fling
	      if (mVelocityTracker == null) {
	    	  mVelocityTracker = VelocityTracker.obtain();
	      }
	      mVelocityTracker.addMovement(ev);

    	
		final int action = ev.getAction();
		
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
		
		  	  /* If being flinged and user touches, stop the fling. isFinished
		       * will be false if being flinged.
		       */
		      if (getAnimation() != null) {
		        clearAnimation();
		      }
		
		      // Remember where the motion event started
		      oldTouchY = ev.getY();
		      this.isDragging = true;
		      break;
		}
		case MotionEvent.ACTION_MOVE:
			if(isDragging){
				float newTouchY = ev.getY();
				float scrollDelta = oldTouchY-newTouchY;
				
				float currentPosY = this.getScrollY();
				if( (currentPosY+scrollDelta)<0) scrollDelta=0;
				if( (currentPosY+scrollDelta)>maxScrollRange) scrollDelta=maxScrollRange;
				
				smoothScrollBy(0,Math.round(scrollDelta));
				
				oldTouchY = ev.getY();
			}
		break;
        case MotionEvent.ACTION_UP:
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

            Log.d("mScrollView","getChildCount(): "+getChildCount() +", Math.abs(initialVelocity) :"+Math.abs(initialVelocity) + ", mMinimumVelocity: "+mMinimumVelocity);
            if (getChildCount() > 0) {
            	
            	
                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    fling(-initialVelocity);
                } 
	
	            if (mVelocityTracker != null) {
	                mVelocityTracker.recycle();
	                mVelocityTracker = null;
	            }
	            
	            this.isDragging = false;
            }
        break;
		
		}
		return true;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    //Call super first because it does some hidden motion event handling
	    super.onInterceptTouchEvent(ev);
	    boolean result = false;
	    
	    Log.d("Order","ScrollView           : "+ev.getEventTime()+" onInterception. Event "+ev.getAction() + " Return "+result);
	    return result;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean sup = super.dispatchTouchEvent(ev);
		boolean result = sup;
		
		switch(ev.getAction()){
			case -1: 
				//this event was already handled in the tree hierarchy below this view.
			break;
			
			case MotionEvent.ACTION_DOWN: case MotionEvent.ACTION_MOVE: case MotionEvent.ACTION_UP:
				//ACTION_DOWN - for initiating the touch event
				//ACTION_MOVE - for scrolling
				//ACTION_UP -   for fling^
				
				//hijack this Event because it was not handled below this view in the hierarchy
				onTouchEvent(ev);
				result = true;
			break;
		}
		
		Log.d("Order","ScrollView           : "+ev.getEventTime()+" dispatchTouchEvent. Event "+ev.getAction());
		return result;
	}
	
//FOCUS
	//FLING-FOCUS PATCH: returning true here prevents the focus from jumping around between different text forms, when scrolling.
	//EDIT: dont touch the focus, or the Texfields will not work right
//	@Override
//	public View findFocus() {
//		return this;
//	}
}

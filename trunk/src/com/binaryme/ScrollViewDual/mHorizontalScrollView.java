package com.binaryme.ScrollViewDual;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;

public class mHorizontalScrollView extends HorizontalScrollView {
	
	private ExternalScrollbar eScroller;
	
	private VelocityTracker mVelocityTracker;
	private int  mActivePointerId;
	final ViewConfiguration configuration = ViewConfiguration.get(getContext());
	private final int mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	private final int mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
	private float oldTouchX;
	private boolean isDragging = false;
	
	public mHorizontalScrollView(Context context) {
		super(context);
		init();
	}
	public mHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    init();
	}
	public mHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
//INTRODUCING AN EXTERNAL SCROLL BAR
	//because HorizontalScrollView's scroll bar is not always visible, when horizontalscrollview's is wrapped by a ScrollView
	
	void init(){
		this.setScrollbarFadingEnabled(false);
		setFadingEdgeLength(0);
	}
	
	/**
	 * This Method must be called from the Activity in order to add an External scroll bar to this View
	 * @param e
	 */
	public void setExternalScrollbar(ExternalScrollbar e){
		this.eScroller = e;
	}
	
	@Override
	protected int computeHorizontalScrollRange() {
		int sup = super.computeHorizontalScrollRange();
		 
		 //Computes the full scroll range which is equal to the sum of container's content width.
		 //This method will set the external scroll bar width (by setting external dummy's width) 
		if(this.eScroller!=null ){
			//call child's computeHorizontalScrollRange
			eScroller.computeHorizontalScrollRange(sup);
		}
		//by returning 0 this method makes the horizontal scroll bar invisible. External scroll bar will represent the horizontal movement for this view.
		return 0;
	}
	
	@Override
	protected int computeHorizontalScrollOffset() {
		int sup = super.computeHorizontalScrollOffset();
		 if(this.eScroller!=null){
			 this.eScroller.scrollTo(sup, 0);
		 }
		 return sup;
	}
	
	@Override
	protected boolean awakenScrollBars() {
		boolean result = false;
		if(this.eScroller!=null){
			result = eScroller.awakenScrollBars();
		}
		return result;
	}
	@Override
	protected boolean awakenScrollBars(int startDelay) { 
		boolean result = false;
		if(this.eScroller!=null){
			result = eScroller.awakenScrollBars(startDelay);
		}
		return result;
	}
	@Override
	protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
		boolean result = false;
		if(this.eScroller!=null){
			result = eScroller.awakenScrollBars(startDelay, invalidate);
		}
		return result;
	}
	
	
	
//HANDLING TOUCH EVENTS
	
	/*
	 * IMPORTANTE!!! 
	 * Do not use super.onTouch(MotionEvent) because:
	 * when super.onTouch is called from dispatchTouchEvent 
	 * an endless loop of drawing the whole View tree again and again is started 
	 * once the scroll bars bounce against the end of screen.
	 * 
	 * Do not know where drawing is called in the super.onTouch method... Maybe a Bug?
	 */
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
		
		  	/*
		       * If being flinged and user touches, stop the fling. isFinished
		       * will be false if being flinged.
		       */
		      if (getAnimation() != null) {
		        clearAnimation();
		      }
		
		      // Remember where the motion event started
		      oldTouchX = ev.getX();
		      this.isDragging = true;
		      break;
		}
		case MotionEvent.ACTION_MOVE:
			if(isDragging){
				float newTouchX = ev.getX();
				float scrollDelta = oldTouchX-newTouchX;
				
				float currentPosX = this.getScrollX();
				if( (currentPosX+scrollDelta)<0) scrollDelta=0;
				if( (currentPosX+scrollDelta)>maxScrollRange) scrollDelta=maxScrollRange;
				
				smoothScrollBy(Math.round(scrollDelta),0);
				oldTouchX = ev.getX();
			}
		break;
        case MotionEvent.ACTION_UP:
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);

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
		
		Log.d("Order","HorizontalScrollView : "+ev.getEventTime()+" dispatchTouchEvent. Event "+ev.getAction());
		return result;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    //Call super first because it does some hidden motion event handling
	    super.onInterceptTouchEvent(ev);
	    
	    //do not INTERCEPT any event, because the only action which this view should handle is MOVE for scrolling, and every MOVE action could be a movement, occurring accidently on the first-touch, when performing a multi-touch gesture.
	    //and multi-touch is handled below in the view hierarchy - there is no situation in which an event should belong to this view with a probability of 100% (and not to views below in the view hierarchy).
	    boolean result = false;
	    	    
		Log.d("Order","HorizontalScrollView : "+ev.getEventTime()+" onInterception. Event "+ev.getAction()+ " Return " + result);
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
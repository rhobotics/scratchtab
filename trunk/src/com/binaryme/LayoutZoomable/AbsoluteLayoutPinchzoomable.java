package com.binaryme.LayoutZoomable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;


/**
 * 
 * @author skip 
 * Based on http://code.google.com/p/android-pinch
 *
 */


@SuppressWarnings("deprecation")
public class AbsoluteLayoutPinchzoomable extends AbsoluteLayout implements ScaleEventListener {
	
	public AbsoluteLayoutPinchzoomable(Context context, AttributeSet attrs,
		int defStyle) {
		super(context, attrs, defStyle);
        init();
	}

	public AbsoluteLayoutPinchzoomable(Context context, AttributeSet attrs) {
		super(context, attrs);
        init();
	}
	public AbsoluteLayoutPinchzoomable(Context context) {
		super(context);
        init();
	}
	

//DSPATCH TOUCH EVENTS
	
	/**
	 * Use this method, if an event should not be handled in the view-hierarchy above this view,
	 * for example if a motion event was handled and ScrollView which wrap all the other Views should not react to this event's actions.
	 */
	public void hijackEvent(MotionEvent ev){
		//set this Event's action to -1 which has no meaning by default
		Log.d("Touch","would hijack an event");
		ev.setAction(-1);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);

		boolean result = onTouchEvent(ev);
		if(result){ 
			/*
			 * Hijack all event's actions, which onTouchEvent wants to handle, except of the MotionEvent.ACTION_DOWN 
			 * because depending on what the user does next - following actions will be handled by this view (pinch gesture)
			 * or by the views above in the hierarchy (scrolling gesture)
			 */
			if(MotionEvent.ACTION_DOWN != ev.getAction()){
				hijackEvent(ev);
			}
		}
		return result;
	}

	
//IMPLEMENT PINCH GESTURE ZOOM

	// actions
    public static final int GROW = 0;
    public static final int SHRINK = 1;
    
    // intervals
    public static final int DURATION = 150;
    public static float ZOOM_FACTOR = 0.04f;
    
    
    public int 
    		mAbsPixelHeight=this.getContext().getResources().getDisplayMetrics().heightPixels,
    		mAbsPixelWidth=this.getContext().getResources().getDisplayMetrics().widthPixels,
    		mDefaultWidth = -1, mDefaultHeight = -1,
            mOldWidth = -1, 
            mOldHeight = -1,
            mTouchSlop = 6;
    public float mOldScale = 1.0f, mMinScale = 0.5f, mMaxScale = 2f;
    
    protected static float x1, 
            x2, 
            y1, 
            y2, 
            x1_pre,
            y1_pre,
            dist_delta = 0,
            dist_curr = -1, 
            dist_pre = -1;
    
    private boolean mDragging = false;
    /*
     * if redrawing the whole system might become too expensive - caching of the View's canvas can be introduced.
     * the picture from cache will be scaled on the fly then, redrawing will happen once, on gesture end.
     * for not mPicture is useless.  
     */
    private Picture mPicture = null;

    
    
	@Override
	public void onScaleEvent(float newscale, Point pivot) {
		ViewGroup.LayoutParams l = this.getLayoutParams();
		
		//update the data after scaling the view
		this.mOldHeight = l.height;
		this.mOldWidth =  l.width;
		
		l.width =  Math.round(this.mDefaultWidth*newscale);
		l.height = Math.round(this.mDefaultHeight*newscale);
		this.setLayoutParams(l);
	}
	
	
// DISABLE ANIMATION
// there is no need in animating the workspace
//		
//		ScaleAnimation animation = new ScaleAnimation(
//				1, 
//				mScale, 
//				1, 
//				mScale, 
//				ScaleAnimation.RELATIVE_TO_SELF, 
//				0, 
//				ScaleAnimation.RELATIVE_TO_SELF, 
//				0);
//
//		
//		animation.setDuration(DURATION);
//		animation.setFillEnabled(true);
//		animation.setInterpolator(this.getContext(), android.R.anim.linear_interpolator);
//		animation.setAnimationListener(new mAnimationListener(this, mScale));
//        this.startAnimation(animation);
//        Log.d("MyActivity","Width: "+this.getWidth()+", height "+this.getHeight());
//
//	}	//onScaleEvent End
//	
//	/**
//	 *	Class for changing the Height and Width after the animation has ended.
//	 *	Saves the pointer to the View, which it should change. 
//	 */
//	private class mAnimationListener implements AnimationListener{
//		private AbsoluteLayoutPinchzoomable scaleView;
//		private float newScale; 
//		
//		mAnimationListener(AbsoluteLayoutPinchzoomable l, float newScale){
//			this.newScale  = newScale;
//			this.scaleView = l;
//		}
//
//		@Override
//		public void onAnimationEnd(Animation animation) {
//    		ViewGroup.LayoutParams l = scaleView.getLayoutParams();
//    		
//    		//update the data after scaling the view
//    		scaleView.mOldHeight = l.height;
//    		scaleView.mOldWidth =  l.width;
//    		
//    		l.width =  Math.round(scaleView.mDefaultWidth*newScale);
//    		l.height = Math.round(scaleView.mDefaultHeight*newScale);
//
//    		//temp
//    		scaleView.setLayoutParams(l);
//
//    		//redraw
//    		scaleView.invalidate();
//    		
//		}
//
//		@Override
//		public void onAnimationRepeat(Animation animation) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void onAnimationStart(Animation animation) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}
// DISABLE ANIMATION END
	    
    private void init() {
        setWillNotDraw(false);
        
        //register for zoom events
        ScaleHandler.addScaleEventListener(this);
    } 
    
    
    public boolean onTouchEvent(MotionEvent event) {  
    	boolean result = false;
    	
        if (!mDragging) {
                super.onTouchEvent(event);
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK, 
                p_count = event.getPointerCount();
        
        switch (action) {
        case MotionEvent.ACTION_MOVE:
        	
            // point 1 coords
            x1 = event.getX(0);
            y1 = event.getY(0);
            
            int rx1 = Math.round(event.getRawX());
            int ry1 = Math.round(event.getRawY());
            Point rawPoint1 = new Point(rx1, ry1);
            
            // if it's a multi-touch gesture
            if (p_count > 1) {   
            			//send a signal, that we will handle this event. It will be hijacked by this View, so that it wont be handled in the view tree above us.
            			result=true;
            	
                        // point 2 coords
                        x2 = event.getX(1);
                        y2 = event.getY(1);
                        
                        // distance between 2 touch events
                        dist_curr = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                        
                        
                        if(mDragging == false){
                        	//at the first double tap initiate the dist_pre
                        	dist_pre = dist_curr;
                        	mDragging = true;
                        }

                        	// the intervall of dist_delta is about -30 .. +30 
                        	dist_delta = dist_curr - dist_pre;
                        	
                        	// the scale level should depend on the range of user's movement 
                        	// if we assume the range of dist_delta as -30 .. +30 then a factor of dist_delta/30 will positively reflect intention
                        	float fingerDistanceFactor = 1+ Math.abs(dist_delta)/30;
                        	//to avoid extreme values, we set this factor's maximum as 2
                        	fingerDistanceFactor = Math.min( fingerDistanceFactor, 2f);
                        	

                    	if (Math.abs(dist_delta) > mTouchSlop) { 
                    		
                        	Log.d("scale","-----");
                        	Log.d("scale","dist_delta: "+dist_delta);
                        	Log.d("scale","fingerDistanceFactor: "+fingerDistanceFactor);


                        	float mScale = ScaleHandler.getScale();
                                
	                        int mode = dist_delta > 0 ? GROW : (dist_curr == dist_pre ? 2 : SHRINK);
	                        switch (mode) {
	                        case GROW: // grow
                                    mOldScale = mScale;
                                    mScale += (ZOOM_FACTOR*fingerDistanceFactor);
                                    ScaleHandler.setScale(mScale, rawPoint1, this);
	                        break;
	                        case SHRINK: // shrink
                                    mOldScale = mScale;
                                    mScale -= (ZOOM_FACTOR*fingerDistanceFactor);
                                    ScaleHandler.setScale(mScale, rawPoint1, this);
	                        break;
	                        }
                    }
                    
                    x1_pre = x1;
                    y1_pre = y1;
                    dist_pre = dist_curr;
            }
            else {

            	// point 1 coords
                x1_pre = event.getX(0);
                y1_pre = event.getY(0);
            }
        break;
        case MotionEvent.ACTION_DOWN:
            
            // point 1 coords
            x1_pre = event.getX(0);
            y1_pre = event.getY(0);
            
            // handle the down action
            result = true;
        break;
        case MotionEvent.ACTION_UP:
            if (mDragging) { mDragging = false; }
        break;
        
	    }
//	    return true;
		Log.d("Order","AbsoluteLayoutExtend : "+event.getEventTime()+" onTouchEvent. Event "+event.getAction() + " returns: "+result);
	    return result;
    }
    
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            //computeScroll();
    }
    
    protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
    }
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    		
    		//remember the Default Size of this View for scrolling relative to the default size
    		if(this.mDefaultHeight<0){this.mDefaultHeight=this.getMeasuredHeight();}
    		if(this.mDefaultWidth<0){this.mDefaultWidth=this.getMeasuredWidth();}
}
    
    protected void onDraw(Canvas canvas) {
//            canvas.setDrawFilter(sZoomFilter);
//            canvas.scale(mScale, mScale);
//            canvas.translate(-getScrollX(), -getScrollY());
//            
//            if (mPicture != null) {
//                    canvas.save();
//                    canvas.drawPicture(mPicture);
//                    canvas.restore();
//                    mPicture = null;
//            }
            
            super.onDraw(canvas);
            
    }
    
    

    
}
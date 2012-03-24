package com.binaryme.LayoutZoomable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.graphics.Point;
import android.util.Log;

public class ScaleHandler {
	
	
	//TODO set default scale from a resource
	private static float startScale = 1f, mMaxScale = 3f, mMinScale=0.5f;
	//pivot Points. Scaling must be done so that these points stay on the same place on the display.
	private static float pivotX=0,pivotY=0;
	
	public static float getScale() {
		return startScale;
	}

	/**
	 * used by all classes to change the scale level (zoom is called scale in this app)
	 * @param scale - the new zoom level. The minimum level is {@link #getMinScale()}, maximum is {@link #getMaxScale()}
	 * @param pivot - the Point which should be handled as the center of the scaling
	 * @param caller - the object which has triggered the scaling, so that this object will not receive a scale event. Otherwise an endless loop could be created. Called reacts on ScaleEvent and receives a new ScaleEvent.
	 */
	public synchronized static void setScale(float scale, Point pivot, Object caller) {

		//rounding scale level
		scale = Math.round(scale*10)/10f;
		
		if(ScaleHandler.startScale != scale){
			if( mMinScale<=scale && scale<=mMaxScale ){
				
				Log.d("scaleHandler", "Scale: "+scale);
				ScaleHandler.startScale = scale;
				ScaleHandler.pivotX = pivot.x;
				ScaleHandler.pivotY = pivot.y;
				fireScaleEvent(scale, pivot, caller);
			}
		}
	}

	
	//ArrayList with weak references to ScaleEventListeners inside
	private static ArrayList<WeakReference<ScaleEventListener>> scaleListeners = new ArrayList<WeakReference<ScaleEventListener>>();
	
	
	public synchronized static void addScaleEventListener(ScaleEventListener listener){
		ScaleHandler.scaleListeners.add(new WeakReference<ScaleEventListener>(listener));
	}
	
	public synchronized static void removeScaleEventListener(ScaleEventListener listener){
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<ScaleEventListener> r : ScaleHandler.scaleListeners){
			ScaleEventListener s = r.get();
			if(s==null || s == listener){ 
				badReferences.add(r);					//remember bad reference
			}
		}
		ScaleHandler.scaleListeners.removeAll(badReferences);
	}
	
	// synchronized - is important here to avoid conflicts, when many zoom gestures are done consiqutively 
	//Event is fired, if scale level is changed
	private synchronized static void fireScaleEvent(float newscale, Point pivot, Object caller){
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<ScaleEventListener> r : ScaleHandler.scaleListeners){
			ScaleEventListener s = r.get();
			if(s==null){ 
//				ScaleHandler.scaleListeners.remove(r);  // CANT REMOVE FROM ArrayList WHILE ITERATING - ConcurrentModificationException IS THROWN
				badReferences.add(r);					//remember bad reference
			}else{
				if(s == caller) continue;
				s.onScaleEvent(newscale, pivot);
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		ScaleHandler.scaleListeners.removeAll(badReferences);
	}
	
	public static float getMinScale(){
		return mMinScale;
	}
	public static float getMaxScale(){
		return mMaxScale;
	}
	
	public static int scale(int size){
		return Math.round(size*startScale);
	}
	public static float scale(float size){
		return size*startScale;
	}
	public static int unscale(int size){
		return Math.round(size/startScale);
	}
	public static float unscale(float size){
		return size/startScale;
	}
	
	

}

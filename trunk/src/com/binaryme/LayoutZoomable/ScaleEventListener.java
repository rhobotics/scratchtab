package com.binaryme.LayoutZoomable;

import java.util.ArrayList;

import android.graphics.Point;

public interface ScaleEventListener {
	
	public ArrayList<ScaleEventListener> sublisteners=new ArrayList<ScaleEventListener>();
	
	/**
	 * This method handles ScaleEvents which occurs, when user does a pinch gesture. 
	 * oldscale ist the scale level before the event.
	 * newscale is the new scale level which has cause the event.
	 * scaleFactor is a factor, which will be used to calculate the new shape sizes, e.g. newscale/oldscale  
	 */
	void onScaleEvent(float newscale, Point pivot);
	

}

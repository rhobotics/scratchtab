package com.binaryme.DragDrop;

import android.view.View;

/**
 *	This class encapsulates the dragging infos, about the object, which is currently dragged. 
 *  It can be passed to View's {@link View#startDrag(android.content.ClipData, android.view.View.DragShadowBuilder, Object, int)} as the third parameter.
 *  It should encapsulate all the infos, which could be useful after the drop occured.  
 */
public class DragStateData {
	public Class<?> classOfTheDraggable= null;
}

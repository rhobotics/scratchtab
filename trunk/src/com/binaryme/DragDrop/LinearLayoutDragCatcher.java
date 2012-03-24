package com.binaryme.DragDrop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.LinearLayout;

/**
 *	Class, created to catch the dragEvents, which were not handled. The outer layout container in the app is an instance of this class.  
 */
public class LinearLayoutDragCatcher extends LinearLayout  {
	
	static int i=1;
	
	public LinearLayoutDragCatcher(Context context) {
		super(context);
		init();
	}
	public LinearLayoutDragCatcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public LinearLayoutDragCatcher(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		//listen for touch events to know, when user takes the finger up, to cancel failed drop
		//this is done here, because DragHandler can not listen for Touch Events, because it is not a part of the android infrastructure, and not part of the UI.
//		this.setOnTouchListener(this);
	}
	
	@Override
	public boolean onDragEvent(DragEvent event) {
		//handle the drop		
		switch(event.getAction()){
			case DragEvent.ACTION_DROP:
				//Log.d("drag", "DragHandler receives action"+M.dragEventResolve(event.getAction()));
				DragHandler.undoCurrentDrag();	//stop the drag process now
				break;
		}
		return true;
	}


//	@Override
//	public boolean dispatchDragEvent(DragEvent event) {
//		boolean result = super.dispatchDragEvent(event);
//		
//		Log.d("howmany","LinearLayoutDragCatcher call "+i);
//		Log.d("howmany",M.dragEventResolve(event.getAction()));
//		i++;
//		
////		switch(event.getAction()){
////			case DragEvent.ACTION_DRAG_ENDED:
////				if(event.getResult() == false && DragHandler.isDragging() ){
////					DragHandler.undoCurrentDrag();
////					//TODO try catching the drag
////					result = true;
////				}
////			break;
////		}
//		return result;
//
//	}
}

//		
//		
//		//native behave of dispatchDragEvent
//		switch(event.getAction()){
//			case DragEvent.ACTION_DRAG_ENDED:
//				Log.d("dispatchDragEventLinView","ACTION_DRAG_ENDED, returns "+result);
////				result = true; //catch, if no one did until now
//				break;
//			case DragEvent.ACTION_DRAG_ENTERED:
//				Log.d("dispatchDragEventLinView","ACTION_DRAG_ENTERED, returns "+result);				
//				break;
//			case DragEvent.ACTION_DRAG_EXITED:
//				Log.d("dispatchDragEventLinView","ACTION_DRAG_EXITED, returns "+result);
//				break;
//			case DragEvent.ACTION_DRAG_LOCATION:
//				Log.d("dispatchDragEventLinView","ACTION_DRAG_LOCATION, returns "+result);
//				break;
//			case DragEvent.ACTION_DRAG_STARTED:
//				Log.d("dispatchDragEventLinView","ACTION_DRAG_STARTED, returns "+result);
//				result =  true; //subscribe for the drag events
//				break;
//			case DragEvent.ACTION_DROP:
//				Log.d("dispatchDragEventLinView","ACTION_DROP, returns "+result);
//				result =  true; //catch, if no one did until now
//				break;
//			
//		}
//		return result;
//	}
//	
//	//TODO try catching event
//	@Override
//	public boolean onDragEvent(DragEvent event) {
//		return true; //catch, if no one did until now
//	}



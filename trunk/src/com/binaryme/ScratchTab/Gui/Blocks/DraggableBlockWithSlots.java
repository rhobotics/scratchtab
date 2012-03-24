package com.binaryme.ScratchTab.Gui.Blocks;

import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.binaryme.DragDrop.DragHandler;
import com.binaryme.tools.M;

public abstract class DraggableBlockWithSlots extends BlockWithSlots {
	
	public DraggableBlockWithSlots(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public DraggableBlockWithSlots(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public DraggableBlockWithSlots(Activity context) {
			super(context);	
			init();
		}
		
		private void init(){}
		
		
		
//GETTER AND SETTER
		/** Returns the color of the block-body, which this DraggableBlockWithSlots represents. 
		 * In TabScratch the body color depends on the logical group of the block, which is derived from block's aim e.g. LOGIC,NUMBERS,ROBOT,SENSORS,VARIABLES.  
		 * Do not mix up with {@link Block.BlockType} which determines the shape of the block and have fields HEAD, COMMAND, BOOLEAN, DATA, LABEL */
		public int getBodyColor(){
			return this.img.getBodyColor();
		}
		
		
		//USER INTERACTION
			@Override
			public boolean onInterceptTouchEvent(MotionEvent ev) {
				boolean result=super.onInterceptTouchEvent(ev); 
				Log.d("Touch","Block Interception action: "+ev.getAction() + " returns "+result );
				return result;
			}
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				boolean result = false;
				
				/* Phase 1. The user moves the Blocks  - hide the keyboard. */
				M.hideKeyboard(this);

				/*
				 * first check the viewGroup, whether the touch was inside of my Shape. If not - do not use it.
				 * touch events , which are not inside the shape can be dismissed by returning true and doing nothing, 
				 * because event passing to the top visible view is asllready done by android and the current view is the right one for this event,
				 * so if no shape is found under the current touch event - then an emty space was touched which belongs to this view, and this event should stay unhandled.
				 */
				int x = Math.round( event.getX());
				int y = Math.round( event.getY());
				if(	(this.img!=null) && 	( this.img.contains( x,y ) )   &&  (event.getAction()==0)  ){
					//now we can be sure that the touch occured within the shape
					super.onTouchEvent(event); 
					
					//passing the event to the gesture detector
					result= DragHandler.startDrag(this, event, null);
					
				}
				return result;
			}
			
//HOOKS TO IMPLEMENT
	/** Called, when a draggable block is deleted. Implement this hook, to recycle the resources. This method currently does nothing. */
	public void onDelete(){}
				
}

package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewParent;

import com.binaryme.DragDrop.DragHandler;
import com.binaryme.LayoutZoomable.AbsoluteLayoutPinchzoomable;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.tools.M;

public class WorkspacePanel extends AbsoluteLayoutPinchzoomable implements  InterfaceBlockContainer, OnDragListener{

		public WorkspacePanel(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}

		public WorkspacePanel(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public WorkspacePanel(Context context) {
			super(context);
			init();
		}
		
		private void init(){
			//register this as it's own drag listener
			this.setOnDragListener(this);
			
			 //disable focus on all GUI elements to avoid weird android's "android.view.ViewGroup.focusableViewAvailable" stack overflows
			this.setFocusable(false);
			this.setFocusableInTouchMode(false);
			
		}

		@Override
		public void add(Block<? extends Shape> b, int x, int y) {
			//remove the block from it's old parent
			
			ViewParent vp = null;
			try{
				vp = b.getParent();
			}catch(Exception e){
				Log.d("drag", "Exception when getting parent");
			}
			
			if(vp != null ){
				if(vp instanceof InterfaceBlockContainer){
					((InterfaceBlockContainer)vp).remove(b);
				}else{
					throw new IllegalArgumentException("The Parent of the Block, which system was trying to add to a Slot was not an InterfaceBlockContainer. Cant remove the Block from its old parent.");
				}
			}
			
			//add the block to the workspace
			this.addView(b);
			
			//block should be positioned on the workspace
			b.setPosition(x, y);
		}

		@Override
		public void remove(Block<? extends Shape> b) {
			this.removeView(b);
		}
		
		@Override
		public void addView(View child) {
			super.addView(child);
			//set the isRoot flag to true
			if(child instanceof Block){
				((Block)child).setRoot(true);
			}
		}

//HANDLING DROP
		
		/**
		 * pass the dragEvent to the OnDrag. For some reason the native implementation of dispatchDragEvent() doesn't pass all Actions, ACTION_DRAG_STARTED and ACTION_DRAG_ENDED are not passed.
		 */
		@Override
		public boolean dispatchDragEvent(DragEvent event) {
			//the drag event is dispatched inside out. Try to send the drag event to the children first, by using the super.dispatchDragEvent() method.
			boolean result = super.dispatchDragEvent(event);
			
			//if the super.dispatchDragEvent() returned true, then some child already handled the drop.
			//if the super.dispatchDragEvent() returned false, then children did not handle the drop, so we can try.
			if(result==false){
				result = this.onDrag(DragHandler.getBlockDragging(), event);
			}
			return result;
			
		}
		
		
		//what to do with the block, which was dropped to the workspace?
		/*
		 * ATTENTION! ATTENTION! ATTENTION! ATTENTION! ATTENTION! 
		 * Returning false from onDrag on ACTION_DRAG_STARTED does not cut the further drag actions off.
		 * The View will still receive the ACTION_DRAG_ENTERED, ACTION_DRAG_EXITED actions.
		 */
		@Override
		public boolean onDrag(View v, DragEvent event) {
			boolean result = false;
			Log.d("drag","Workspace gets a drag event "+event.getAction()+", the result is "+result);
			
			//TODO
			switch(event.getAction()){
				case DragEvent.ACTION_DRAG_STARTED :
					//TODO debug actions
					Log.d("dragStartStop","WORKSPACE says: ACTION_DRAG_STARTED");
					result=true; //workspace accepts all blocks, independently of its type.
					break;
				
				case DragEvent.ACTION_DRAG_EXITED :
					break;//no reaction
					
				case DragEvent.ACTION_DRAG_ENTERED :
					break;//no reaction
					
				case DragEvent.ACTION_DRAG_LOCATION :
					break; //no reaction
					
				case DragEvent.ACTION_DROP :
					int newx = Math.max(0,  Math.round(event.getX()- DragHandler.getBlockTouchDeltaX())  );
					int newy = Math.max(0,  Math.round(event.getY() - DragHandler.getBlockTouchDeltaY())  );
					
					DragHandler.executeDropTo(this , newx, newy);
					
					result=true;
					break;
					
				case DragEvent.ACTION_DRAG_ENDED :
					break; //no reaction
					
			}
			return result; //default result is false
		}
		
		
		
//TOUCH
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				M.hideKeyboard(this);
			}
			return super.onTouchEvent(event);
		}
		
		//KEYBOARD EVENTS
		/** Overriding this method is necessary, because the KeyEveents sometimes do not reach the focused View, when using the default implementation.
		 *  In Android the SoftwareKeyboard-Events are normally passed through the View hierarchy to the View, which is currently focused. 
		 *  For some reason there is a problem in passing Soft-Keyboard events to the focused TextFields through the hierarchy. 
		 *  For this reason the keyEvents are passed directly to the focused view here.*/
		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			//retrieve the focused view
			View focusedView = AppRessources.context.getCurrentFocus();
			//pass the keyEvent to the focused view
			return focusedView.dispatchKeyEvent(event);
//			return super.dispatchKeyEvent(event);
		}
	
}

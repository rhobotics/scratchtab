package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeDoUntil;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class DoUntil extends ExecutableDraggableBlockWithSlots<ShapeDoUntil, Object>{  
	
	private SlotBoolean slotBoolean;
	private SlotCommand slotInnerChild;
	
	
	public DoUntil(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public DoUntil(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public DoUntil(Activity context) {
			super(context);
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeDoUntil.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotBoolean = label.findFirstOccurenceOfSlot(SlotBoolean.class);
			slotInnerChild = (SlotCommand) this.getShape().getSlot(ShapeDoUntil.CHILD_INNER);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeDoUntil initiateShapeHere() {
			return new ShapeDoUntil(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(final ExecutionHandler<?> executionHandler) {
			
			//initial read of the head boolean slot
			Boolean breakLoop = slotBoolean.executeForValue(executionHandler);
			
			
			//the loop breaks, when the boolean slot becomes ==true
			while(!executionHandler.isInterrupted() && !breakLoop){
				//execute the child
				slotInnerChild.executeForValue(executionHandler);
				//check if we already have a breakLoop == true
				breakLoop = slotBoolean.executeForValue(executionHandler);
				
				//TODO:
				AppRessources.context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("stop","stop");
						Log.d("stop","is executionHandler interrupted: "+executionHandler.isInterrupted());
					}
				});
			}
			//conditional control blocks never return values, just execute children
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDoUntil.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeWaitUntilTrue;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class WaitUntilTrue extends ExecutableDraggableBlockWithSlots<ShapeWaitUntilTrue, Object>{  
	
	private SlotBoolean slotBoolean;
	
	
	public WaitUntilTrue(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public WaitUntilTrue(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public WaitUntilTrue(Activity context) {
			super(context);
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeWaitUntilTrue.LABEL);
			Label label = (Label) slotLabel.getInfill();
			slotBoolean = label.findFirstOccurenceOfSlot(SlotBoolean.class);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeWaitUntilTrue initiateShapeHere() {
			return new ShapeWaitUntilTrue(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			Boolean result = slotBoolean.executeForValue(executionHandler);

			//sleep, untill result becomes true
			while(!result && !executionHandler.isInterrupted()){
				try {
					executionHandler.sleep(100);
				} catch (InterruptedException e) {	}
					//check the boolean slot again
					result = slotBoolean.executeForValue(executionHandler);
			}
			//conditional control blocks never return values, just execute children
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeWaitUntilTrue.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

package com.binaryme.ScratchTab.Gui.Blocks.Logic;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Logic.ShapeNegate;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;

public class Negate extends ExecutableDraggableBlockWithSlots<ShapeNegate, Boolean> {
	
	SlotBoolean slotBoolean;
	
	public Negate(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public Negate(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public Negate(Activity context) {
			super(context);	
			init();
		}
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeNegate.LABEL);
			Label label = (Label) slotLabel.getInfill();
			slotBoolean  = label.findFirstOccurenceOfSlot(SlotBoolean.class);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Boolean executeForValue(ExecutionHandler executionHandler) {
			Boolean slotValue 	= slotBoolean.executeForValue(executionHandler);
			return (!slotValue);
		}
		@Override
		public Slot getSuccessorSlot() {
			// no successors. This block may only trigger the executon of other blocks if they are pasted into it.
			return null;
		}
		@Override
		protected ShapeNegate initiateShapeHere() {
			return new ShapeNegate(getContextActivity(), this);
		}
}

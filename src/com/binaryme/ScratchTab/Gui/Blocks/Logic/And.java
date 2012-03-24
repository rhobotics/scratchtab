package com.binaryme.ScratchTab.Gui.Blocks.Logic;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Logic.ShapeAnd;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNumDecimal;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;

public class And extends ExecutableDraggableBlockWithSlots<ShapeAnd, Boolean> {
	
	SlotBoolean slotBooleanLeft;
	SlotBoolean slotBooleanRight;
	
	public And(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public And(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public And(Activity context) {
			super(context);	
			init();
		}
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeAnd.LABEL);
			Label label = (Label) slotLabel.getInfill();
			slotBooleanLeft  = label.findSlot(SlotBoolean.class,1);
			slotBooleanRight = label.findSlot(SlotBoolean.class,2);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Boolean executeForValue(ExecutionHandler executionHandler) {
			Boolean left 	= slotBooleanLeft.executeForValue(executionHandler);
			Boolean right 	= slotBooleanRight.executeForValue(executionHandler);
			return (left && right);
		}
		@Override
		public Slot getSuccessorSlot() {
			// no successors. This block may only trigger the executon of other blocks if they are pasted into it.
			return null;
		}
		@Override
		protected ShapeAnd initiateShapeHere() {
			return new ShapeAnd(getContextActivity(), this);
		}
}

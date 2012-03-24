package com.binaryme.ScratchTab.Gui.Blocks.Logic;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Logic.ShapeAnd;
import com.binaryme.ScratchTab.Gui.Shapes.Logic.ShapeLess;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNumDecimal;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;

public class Less extends ExecutableDraggableBlockWithSlots<ShapeLess, Boolean> {
	
	SlotDataNumDecimal slotDataNumDecimalLeft;
	SlotDataNumDecimal slotDataNumDecimalRight;
	
	public Less(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public Less(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public Less(Activity context) {
			super(context);	
			init();
		}
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeAnd.LABEL);
			Label label = (Label) slotLabel.getInfill();
			slotDataNumDecimalLeft = label.findSlot(SlotDataNumDecimal.class,1);
			slotDataNumDecimalRight = label.findSlot(SlotDataNumDecimal.class,2);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Boolean executeForValue(ExecutionHandler executionHandler) {
			Double left 	= slotDataNumDecimalLeft.executeForValue(executionHandler);
			Double right 	= slotDataNumDecimalRight.executeForValue(executionHandler);
			return (left<right);
		}
		@Override
		public Slot getSuccessorSlot() {
			// no successors. This block may only trigger the executon of other blocks if they are pasted into it.
			return null;
		}
		@Override
		protected ShapeLess initiateShapeHere() {
			return new ShapeLess(getContextActivity(), this);
		}
}

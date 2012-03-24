package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeIf;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class If extends ExecutableDraggableBlockWithSlots<ShapeIf, Object>{  
	
	private SlotBoolean slotBoolean;
	private SlotCommand slotInnerChild;
	
	
	public If(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public If(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public If(Activity context) {
			super(context);
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeIf.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotBoolean = label.findFirstOccurenceOfSlot(SlotBoolean.class);
			slotInnerChild = (SlotCommand) this.getShape().getSlot(ShapeIf.CHILD_INNER);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeIf initiateShapeHere() {
			return new ShapeIf(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			Boolean result = slotBoolean.executeForValue(executionHandler);

			//execute the child
			if(result){
				slotInnerChild.executeForValue(executionHandler);
			}
			//conditional control blocks never return values, just execute children
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeIf.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

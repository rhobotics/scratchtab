package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeIfElse;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class IfElse extends ExecutableDraggableBlockWithSlots<ShapeIfElse, Object>{  
	
	private SlotBoolean slotBoolean;
	private SlotCommand slotInnerChildIf;
	private SlotCommand slotInnerChildElse;
	
	
	public IfElse(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public IfElse(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public IfElse(Activity context) {
			super(context);
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeIfElse.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotBoolean = label.findFirstOccurenceOfSlot(SlotBoolean.class);
			slotInnerChildIf = (SlotCommand) this.getShape().getSlot(ShapeIfElse.CHILD_INNER);
			slotInnerChildElse = (SlotCommand) this.getShape().getSlot(ShapeIfElse.CHILD_INNER_SECOND);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeIfElse initiateShapeHere() {
			return new ShapeIfElse(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			Boolean result = slotBoolean.executeForValue(executionHandler);

			//execute the child
			if(result){
				slotInnerChildIf.executeForValue(executionHandler);
			}else{
				slotInnerChildElse.executeForValue(executionHandler);
			}
			//conditional control blocks never return values, just execute children
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeIfElse.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

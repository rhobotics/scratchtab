package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeDoXTimes;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class DoXTimes extends ExecutableDraggableBlockWithSlots<ShapeDoXTimes, Object>{  
	
	private SlotDataNum slotDataNum;
	private SlotCommand slotInnerChild;
	
	
	public DoXTimes(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public DoXTimes(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public DoXTimes(Activity context) {
			super(context);
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeDoXTimes.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotDataNum = label.findFirstOccurenceOfSlot(SlotDataNum.class);
			slotInnerChild = (SlotCommand) this.getShape().getSlot(ShapeDoXTimes.CHILD_INNER);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeDoXTimes initiateShapeHere() {
			return new ShapeDoXTimes(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
//			Double frequency = (Double) executionHandler.executeExecutable( slotDataNum );
			Double frequency = slotDataNum.executeForValue(executionHandler);

			//execute the child
			for(int i=0; i<frequency; i++){
				executionHandler.executeExecutable( slotInnerChild );
				if(executionHandler.isInterrupted()){
					break;
				}
			}
			//conditional control blocks never return values, just execute children
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDoXTimes.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.movingDirection;
import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeDoXTimes;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeDriveForwardLimited;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class DriveForwardLimited extends ExecutableDraggableBlockWithSlots<ShapeDriveForwardLimited, Object>{  
	
	private SlotDataNum slotDataNum;
	
	public DriveForwardLimited(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public DriveForwardLimited(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public DriveForwardLimited(Activity context) {
			super(context);	
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeDoXTimes.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotDataNum = label.findFirstOccurenceOfSlot(SlotDataNum.class);
		}
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeDriveForwardLimited initiateShapeHere() {
			return new ShapeDriveForwardLimited(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler<?> executionHandler) {
			double distanceD = (Double) executionHandler.executeExecutable( slotDataNum );
			int distance = (int)distanceD;
			
			Log.d("MyApplication","Distance "+distance);
			
			AppRessources.legoNXTHandler.moveLimited(movingDirection.FORWARD, distance);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDriveForwardLimited.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


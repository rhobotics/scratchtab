package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.movingDirection;
import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeDriveForward;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class DriveForward extends ExecutableDraggableBlockWithSlots<ShapeDriveForward, Object>{  
	
	public DriveForward(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public DriveForward(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public DriveForward(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeDriveForward initiateShapeHere() {
			return new ShapeDriveForward(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			//TODO change to forever
			AppRessources.legoNXTHandler.moveForever(movingDirection.FORWARD);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDriveForward.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


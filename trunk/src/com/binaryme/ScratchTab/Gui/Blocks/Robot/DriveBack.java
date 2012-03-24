package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.movingDirection;
import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeDriveBack;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class DriveBack extends ExecutableDraggableBlockWithSlots<ShapeDriveBack, Object>{  
	
	public DriveBack(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public DriveBack(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public DriveBack(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeDriveBack initiateShapeHere() {
			return new ShapeDriveBack(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			//TODO change to forever
			AppRessources.legoNXTHandler.moveForever(movingDirection.BACKWARD);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDriveBack.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


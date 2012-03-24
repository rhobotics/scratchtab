package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.HowtoStop;
import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeStopAllMotors;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class StopAllMotors extends ExecutableDraggableBlockWithSlots<ShapeStopAllMotors, Object>{  
	
	public StopAllMotors(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public StopAllMotors(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public StopAllMotors(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeStopAllMotors initiateShapeHere() {
			return new ShapeStopAllMotors(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			AppRessources.legoNXTHandler.stopAllMotors(HowtoStop.IMMEDIATELY);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeStopAllMotors.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


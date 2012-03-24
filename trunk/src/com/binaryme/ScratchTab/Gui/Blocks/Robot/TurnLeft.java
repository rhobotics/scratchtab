package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.TurnDirection;
import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeTurnLeft;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class TurnLeft extends ExecutableDraggableBlockWithSlots<ShapeTurnLeft, Object>{  
	
	public TurnLeft(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public TurnLeft(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public TurnLeft(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeTurnLeft initiateShapeHere() {
			return new ShapeTurnLeft(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			AppRessources.legoNXTHandler.turn(TurnDirection.LEFT);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeTurnLeft.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


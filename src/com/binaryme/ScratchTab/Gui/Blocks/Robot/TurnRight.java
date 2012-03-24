package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.TurnDirection;
import icommand.scratchtab.LegoNXTHandler.movingDirection;
import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeTurnRight;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class TurnRight extends ExecutableDraggableBlockWithSlots<ShapeTurnRight, Object>{  
	
	public TurnRight(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public TurnRight(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public TurnRight(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeTurnRight initiateShapeHere() {
			return new ShapeTurnRight(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			AppRessources.legoNXTHandler.turn(TurnDirection.RIGHT);
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeTurnRight.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


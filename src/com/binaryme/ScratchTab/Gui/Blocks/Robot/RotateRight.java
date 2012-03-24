package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeRotateLeft;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class RotateRight extends ExecutableDraggableBlockWithSlots<ShapeRotateLeft, Object>{  
	
	public RotateRight(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public RotateRight(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public RotateRight(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeRotateLeft initiateShapeHere() {
			return new ShapeRotateLeft(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			AppRessources.legoNXTHandler.rotateRight();
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeRotateLeft.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


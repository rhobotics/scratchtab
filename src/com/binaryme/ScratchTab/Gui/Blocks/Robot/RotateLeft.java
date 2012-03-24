package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeRotateRight;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class RotateLeft extends ExecutableDraggableBlockWithSlots<ShapeRotateRight, Object>{  
	
	public RotateLeft(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public RotateLeft(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public RotateLeft(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeRotateRight initiateShapeHere() {
			return new ShapeRotateRight(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			AppRessources.legoNXTHandler.rotateLeft();
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeRotateRight.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}


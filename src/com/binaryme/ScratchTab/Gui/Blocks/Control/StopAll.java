package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeStopAll;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.tools.M;


public class StopAll extends ExecutableDraggableBlockWithSlots<ShapeStopAll, Object>{  
	
	public StopAll(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public StopAll(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public StopAll(Activity context) {
			super(context);	
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeStopAll initiateShapeHere() {
			return new ShapeStopAll(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			M.stopAll();
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			return null;
		}
}

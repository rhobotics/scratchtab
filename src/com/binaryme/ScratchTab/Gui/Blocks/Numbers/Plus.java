package com.binaryme.ScratchTab.Gui.Blocks.Numbers;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Shapes.Numbers.ShapePlus;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.tools.M;

public class Plus extends ExecutableDraggableBlockWithSlots<ShapePlus, Double>  implements InterfaceNumDataContainer  {
	public Plus(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public Plus(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public Plus(Activity context) {
			super(context);	
		}
		@Override
		public Double executeForValue(ExecutionHandler executionHandler) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			// no successors. This block may only trigger the executon of other blocks if they are pasted into it.
			return null;
		}
		@Override
		protected ShapePlus initiateShapeHere() {
			return new ShapePlus(getContextActivity(), this);
		}
		@Override
		public double getNum(ExecutionHandler handler) {
			return executeForValue(handler);
		}
		@Override
		public String parseString(ExecutionHandler handler) {
			Double res = executeForValue(handler);  
			String str = M.parseDouble(res);
			return str;
		}
}

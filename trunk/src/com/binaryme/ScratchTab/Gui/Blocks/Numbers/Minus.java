package com.binaryme.ScratchTab.Gui.Blocks.Numbers;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Numbers.ShapeDivide;
import com.binaryme.ScratchTab.Gui.Shapes.Numbers.ShapeMinus;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNumDecimal;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.tools.M;

public class Minus extends ExecutableDraggableBlockWithSlots<ShapeMinus, Double>  implements InterfaceNumDataContainer  {
	public Minus(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public Minus(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public Minus(Activity context) {
			super(context);	
		}
		@Override
		public Double executeForValue(ExecutionHandler<?> executionHandler) {
			//first the the label slot, with the label. This SLot contains the label. The label contains 2 input fields which we need for calculation.
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeDivide.LABEL);
			Label lbl = slotLabel.getLabel();
			
			//look for SlotDataNumDecimal which are inserted inside of  ShapeDivide
			SlotDataNumDecimal slotnum1 = lbl.findSlot(SlotDataNumDecimal.class, 1);			
			SlotDataNumDecimal slotnum2 = lbl.findSlot(SlotDataNumDecimal.class, 2);	
			
			//do the math which this block is created for.
			double result = slotnum1.executeForValue(executionHandler) - slotnum2.executeForValue(executionHandler);
			
			return result;
		}
		@Override
		public Slot getSuccessorSlot() {
			// no successors. This block may only trigger the executon of other blocks if they are pasted into it.
			return null;
		}
		@Override
		protected ShapeMinus initiateShapeHere() {
			return new ShapeMinus(getContextActivity(), this);
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

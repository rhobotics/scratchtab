package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummyLabel;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class BlockSlotDummyLabel extends BlockSlotDummy<Object> {
	public BlockSlotDummyLabel(Activity context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
	}
	public BlockSlotDummyLabel(Activity context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BlockSlotDummyLabel(Activity context) {
		super(context);
	}
	@Override
	protected ShapeSlotDummy initiateShapeHere() {
		return new ShapeSlotDummyLabel(getContextActivity(), this);	
	}
	@Override
	public Object getValue() {
		//will never call getValue on a Label Slot, because Label do not encapsulates Values on its own.
		return null;
	}
	
	
//EXECTUION
	@Override
	public Object executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}
	@Override
	public Slot getSuccessorSlot() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void interruptExecution() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Boolean isInterrupted() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void feedbackExecutionError() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void feedbackExecutionProcessRunning() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void feedbackDisable() {
		// TODO Auto-generated method stub
		
	}
}
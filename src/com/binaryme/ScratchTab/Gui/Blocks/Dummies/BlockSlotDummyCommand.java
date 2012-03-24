package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummyCommand;
import com.binaryme.ScratchTab.Gui.Slots.Slot;

public class BlockSlotDummyCommand extends BlockSlotDummy<Object> {
	
	private Object defaultValue = ConfigHandler.DEFAULT_VALUE_COMMAND;
	
	public BlockSlotDummyCommand(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
	}
	public BlockSlotDummyCommand(Activity context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BlockSlotDummyCommand(Activity context) {
		super(context);
	}
	@Override
	protected ShapeSlotDummy initiateShapeHere() {
		return new ShapeSlotDummyCommand(getContextActivity(), this);	
	}
	@Override
	public Object getValue() {
		//Command Dummies are asked for values, when the command Slot is empty, so they always return null since empty commandSlots are supposed to return null. 
		return defaultValue;
	}
	
	
//EXECTUION
	@Override
	public Object executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}
	
}

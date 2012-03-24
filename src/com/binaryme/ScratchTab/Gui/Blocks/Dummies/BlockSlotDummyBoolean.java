package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceBooleanDataContainer;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummyBoolean;

public class BlockSlotDummyBoolean extends BlockSlotDummy<Boolean> implements InterfaceBooleanDataContainer{
	
	private boolean defaultValue = ConfigHandler.DEFAULT_VALUE_BOOLEAN;
	
	public BlockSlotDummyBoolean(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
	}
	public BlockSlotDummyBoolean(Activity context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BlockSlotDummyBoolean(Activity context) {
		super(context);
	}
	@Override
	protected ShapeSlotDummy initiateShapeHere() {
		return new ShapeSlotDummyBoolean(getContextActivity(), this);	
	}
	@Override
	public Boolean getValue() {
		return defaultValue;
	}
	@Override
	public Boolean getBoolean() {
		return getValue();
	}
	@Override
	public String parseString(ExecutionHandler handler) {
		return getValue().toString();
	}
	
//EXECTUION
	@Override
	public Boolean executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}
}

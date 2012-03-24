package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;

import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Widgets.MNumField;

public class BlockSlotDummyDataNum extends BlockSlotDummyData<MNumField, Double> implements InterfaceNumDataContainer {

	private int defaultValue = ConfigHandler.DEFAULT_VALUE_NUMFIELD;
	
	public BlockSlotDummyDataNum(Activity context, MNumField widget) {
		super(context, widget);
	}

	@Override
	public Double getValue() {
		return this.getWidget().getValueAsDouble();
	}

	@Override
	public double getNum(ExecutionHandler handler) {
		return getValue();
	}

	@Override
	public String parseString(ExecutionHandler handler) {
		return getValue().toString();
	}
	

//EXECTUION

	@Override
	public Double executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}

}

package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Widgets.MNumDecimalField;

public class BlockSlotDummyDataDecimal extends BlockSlotDummyData<MNumDecimalField,Double>  implements InterfaceNumDataContainer {

//	private double defaultValue = ConfigHandler.DEFAULT_VALUE_NUMDOUBLEFIELD;
	
	public BlockSlotDummyDataDecimal(Activity context, MNumDecimalField widget) {
		super(context, widget);
	}

	@Override
	public Double getValue() {
		return new Double(this.getWidget().getValueAsDouble());
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

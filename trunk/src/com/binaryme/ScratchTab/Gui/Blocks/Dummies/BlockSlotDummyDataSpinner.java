package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceStringDataContainer;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;

public class BlockSlotDummyDataSpinner extends BlockSlotDummyData<MSpinner, String> implements InterfaceStringDataContainer {
	public BlockSlotDummyDataSpinner(Activity context, MSpinner widget) {
		super(context, widget);
	}
	
	@Override
	public String getValue() {
		return this.getWidget().getValue();
	}
	
	@Override
	public String parseString(ExecutionHandler handler) {
		return getValue().toString();
	}
	
//EXECTUION

	@Override
	public String executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}
}

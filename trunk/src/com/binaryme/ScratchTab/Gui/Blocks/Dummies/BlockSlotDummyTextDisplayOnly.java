package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceStringDataContainer;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Widgets.MTextDisplayOnly;

public class BlockSlotDummyTextDisplayOnly extends BlockSlotDummyData<MTextDisplayOnly, String> implements InterfaceStringDataContainer {

	public BlockSlotDummyTextDisplayOnly(Activity context, MTextDisplayOnly widget) {
		super(context, widget);
	}

	@Override
	public String getValue() {
		return this.getWidget().getValueAsString();
	}
	
	@Override
	public String parseString(ExecutionHandler handler) {
		return getValue();
	}
	
//EXECTUION
	@Override
	public String executeForValue(ExecutionHandler<?> executionHandler) {
		return getValue();
	}

}
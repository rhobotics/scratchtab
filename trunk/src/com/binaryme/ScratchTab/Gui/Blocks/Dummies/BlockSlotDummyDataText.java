package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceStringDataContainer;
import com.binaryme.ScratchTab.Gui.Widgets.MTextField;

public class BlockSlotDummyDataText extends BlockSlotDummyData<MTextField, String> implements InterfaceStringDataContainer {

	public BlockSlotDummyDataText(Activity context, MTextField widget) {
		super(context, widget);
	}

	@Override
	public String getValue() {
		return this.getWidget().getValueAsString();
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
	
	
	//override the feedback methods, because the Text dummies do not 
	@Override
	public void feedbackDisable() {
		super.feedbackDisable();
	}

}

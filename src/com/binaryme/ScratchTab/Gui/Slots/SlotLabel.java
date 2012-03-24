package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;
import android.view.View;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyLabel;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

public class SlotLabel extends Slot<BlockSlotDummyLabel, Object> {
	public SlotLabel(Activity context, Shape shape) {
		super(context);
	}

	@Override
	public BlockType getType() {
		return BlockType.LABEL;
	}

	@Override
	protected BlockSlotDummyLabel initiateBlockSlotDummy() {
		return new BlockSlotDummyLabel(getContextActivity()); //SlotLabel doesn't have no Dummy to draw, because SlotLabel is never empty.
	}

	@Override
	public Object executeForValue(ExecutionHandler executionHandler) {
		//no own value for the label. Label retrieves the values within the label, but there are some methods to retrieves values from embedded slots 
		return null;
	} 
	
//OVERRIDE
	/** SlotLabel will only accept Blocks with a {@link BlockType} "LABEL". There is always a Label in the SlotLabel, and this Label can not be removed from the SlotLabel, so we can cast the infill to Label relative safe.  */
	public Label getLabel() {
		Label result=null; 
		Block<?> b = super.getInfill();
		if(b instanceof Label){
			result= (Label) b;
		}
		return result;
	}
	
	
//METHODS TO RETRIEVE DATA FROM EMBEDDED SLOTS - SlotLabel just passes all requests to the Label
	
	public <T extends View> T findFirstOccurenceOfSlot(Class<T> slotclass){
		return getLabel().findFirstOccurenceOfSlot(slotclass);
	}
	public <T extends View> T findSlot(Class<T> slotclass, int occurrence){
		return getLabel().findSlot(slotclass, occurrence);
	}
	
	
	//THE WHOLE SEQUENCE JUST PASSES THE REQUEST TO THE LABEL. METHODS ARE CREATED TO ENABLE COMFORTABLE ASKING THE LABELSLOT FOR LABEL DATA
	
	public String executeForTextValue(ExecutionHandler handler){
		return getLabel().executeForTextValue(handler);
	}
	public String executeForTextValue(ExecutionHandler handler, int occurence){
		return getLabel().executeForTextValue(handler);
	}
	public double executeForNumDecimal(ExecutionHandler handler){
		return getLabel().executeForNum(handler);
	}
	public double executeForNumDecimal(ExecutionHandler handler, int occurence){
		return getLabel().executeForNum(handler,occurence);
	}
	public String executeForSpinner(ExecutionHandler handler){
		return getLabel().executeForSpinner(handler);
	}
	public String executeForSpinner(ExecutionHandler handler, int occurence){
		return getLabel().executeForSpinner(handler,occurence);
	}
	public boolean executeForBoolean(ExecutionHandler handler){
		return getLabel().executeForBoolean(handler);
	}
	public boolean executeForBoolean(ExecutionHandler handler, int occurence){
		return getLabel().executeForBoolean(handler,occurence);
	}
}
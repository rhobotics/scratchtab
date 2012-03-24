package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.Slot.SlotMode;
import com.binaryme.tools.ColorPalette;

/** 
 * 	Instances of this class are displayed in Slots, if the Slots are empty. 
 *  They encapsulate the default Look and feel of the slots, e.g. the picture of an empty slot. 
 *  
 * @param <T> - the Type of Object, which the current dummy will return during the execution. E.g. a {@link BlockSlotDummyDataNum} should return a Double when it is executed. 
 */
public abstract class BlockSlotDummy<T extends Object> extends Block<ShapeSlotDummy> implements Executable<T> {
	private boolean isInterrupted=false;
	
	public BlockSlotDummy(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
	}
	public BlockSlotDummy(Activity context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BlockSlotDummy(Activity context) {
		super(context);
	}
	
	/** Method will be used to switch a Dummy into the right mode, so that it will draw with respect to to hover, drop, drag  */
	public void setSlotMode(SlotMode mode){
		this.img.setMode(mode);
	}
	
	@Override
	public ShapeSlotDummy getShape() {
		return this.img;
	}
	
	/** Should return the Value of an empty Slot, where the current dummy will be used.  
	 *  The Data to fill the returned object can be retrieved from the Widget within the Object.
	 * 	This Method's signature should be redefined to return a more concrete Object. E.g. the BlockSlotDummyNumDouble should return a Double object. 
	 */
	public abstract Object getValue();
	
//EXECUTION
	@Override
	public Slot getSuccessorSlot() {
		//no successors for dummies
		return null;
	}
	@Override
	public void interruptExecution() {
		this.isInterrupted=true;
	}
	@Override
	public Boolean isInterrupted() {
		return isInterrupted;
	}
	@Override
	public void feedbackExecutionError() {
		this.getShape().setCurrentFillColor( ColorPalette.colorAppError);
	}
	@Override
	public void feedbackExecutionProcessRunning() {
		ShapeSlotDummy s = this.getShape();
		s.setCurrentFillColor(s.fillColorActive());
	}
	@Override
	public void feedbackDisable() {
		ShapeSlotDummy s = this.getShape();
		s.setCurrentFillColor(s.fillColorEmpty());
	}
}
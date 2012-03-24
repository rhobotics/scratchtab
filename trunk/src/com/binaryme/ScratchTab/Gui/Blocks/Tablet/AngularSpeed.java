package com.binaryme.ScratchTab.Gui.Blocks.Tablet;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyTextDisplayOnly;
import com.binaryme.ScratchTab.Gui.Shapes.Tablet.ShapeAngularSpeed;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;

public class AngularSpeed extends AbstractSensorBlock<ShapeAngularSpeed> {

	public AngularSpeed(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
	}
	public AngularSpeed(Activity context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AngularSpeed(Activity context) {
		super(context);	
	}
	
	
	@Override
	protected void init() {
		
		
		//find the Label
		SlotLabel slotlabel = (SlotLabel) this.getShape().getSlot(ShapeAngularSpeed.LABEL);
			
		//text display
			//get the slot
			SlotTextDisplayOnly slot = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotTextDisplayOnly.class);
			
			//getting the dummy
			BlockSlotDummyTextDisplayOnly dummy = slot.getInfill();
			
			//getting the Textdisplay-widget
			this.mTextDisplayWidget = dummy.getWidget();
		
		//spinner
			//get the slot
			SlotDataSpinner slotspinner = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotDataSpinner.class);
			
			//get the spinner
			this.mSpinnerWidget = slotspinner.getSpinner();
			
			//listen for Item selection
			mSpinnerWidget.setOnItemSelectedListener(this);
		
			
		//SETTINGS
			this.onSelection(currentSensorDimension);
			
			//set the Popup title
			mSpinnerWidget.setPrompt("Measure acceleration");
		
	}

	@Override
	protected ShapeAngularSpeed initiateShapeHere() {
		return new ShapeAngularSpeed(getContextActivity(),this);
	}
	@Override
	protected MySensor getMySensorFromShape() {
		return ShapeAngularSpeed.sensor;
	}

}

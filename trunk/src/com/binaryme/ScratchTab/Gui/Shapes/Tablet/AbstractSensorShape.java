package com.binaryme.ScratchTab.Gui.Shapes.Tablet;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsRectangle;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;
import com.binaryme.tools.ColorPalette;

public abstract class AbstractSensorShape extends ShapeWithSlotsRectangle {
	
	public AbstractSensorShape(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}
	
	/** Hook to implement for each sensor-shape */
	public abstract MySensor getSensor();

	@Override
	protected void initLabels() {
		MySensor sensor = getSensor();
		
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent(sensor.getName());
		
		//Widget 2 - Spinner with all possible NXT Sensors
		ArrayList<String> contentArrayList = new ArrayList<String>();
		//retrieve all Dimensions for the dropdown
		for(String sensorDimension : sensor.getDimensionsForCurrentSensor()){
			contentArrayList.add(sensorDimension);
		}
		SlotDataSpinner spinnerSlot = new SlotDataSpinner(getContextActivity(), contentArrayList);
		label.appendContent( spinnerSlot );
		
		//Widget 3 - TextDisplayOnly
		SlotTextDisplayOnly slotText =  new SlotTextDisplayOnly(AppRessources.context); 
		label.appendContent(slotText);
		
		this.getSlot(LABEL).add(label,0,0);
	}

	@Override
	protected int bodyColor() {
		return ColorPalette.colorOfTablet;
	}

	@Override
	protected int bodyStrokeColor() {
		return ColorPalette.colorBodyStroke;
	}

}
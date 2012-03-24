package com.binaryme.ScratchTab.Gui.Shapes.Robot;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Robot.SensorData;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsRectangle;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;
import com.binaryme.tools.ColorPalette;

public class ShapeSensorData extends ShapeWithSlotsRectangle {

	public ShapeSensorData(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Spinner with all possible NXT Sensors
		ArrayList<String> contentArrayList = new ArrayList<String>();
		contentArrayList.add(SensorData.SensorType.NO_SENSOR.toString());
		contentArrayList.add(SensorData.SensorType.DISTANCE.toString());
		contentArrayList.add(SensorData.SensorType.BUTTON.toString());
		contentArrayList.add(SensorData.SensorType.LIGHT.toString());
		contentArrayList.add(SensorData.SensorType.SOUND.toString());
		SlotDataSpinner spinnerSlot = new SlotDataSpinner(getContextActivity(), contentArrayList);
		label.appendContent( spinnerSlot );
		
		//Widget 2 - TextDisplayOnly
		SlotTextDisplayOnly slotText =  new SlotTextDisplayOnly(AppRessources.context); 
		label.appendContent(slotText);
				
		this.getSlot(LABEL).add(label,0,0);
	}

	@Override
	protected int bodyColor() {
		return ColorPalette.colorOfRobot;
	}

	@Override
	protected int bodyStrokeColor() {
		return ColorPalette.colorBodyStroke;
	}

}
package com.binaryme.ScratchTab.Gui.Shapes.Control;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Robot.SensorData;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsHead;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;
import com.binaryme.tools.ColorPalette;

public class ShapeHeadNXTSensorDataLarger extends ShapeWithSlotsHead {

	public ShapeHeadNXTSensorDataLarger(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent( "If current" );
		
		//Widget 2 - Spinner with all possible NXT Sensors
		ArrayList<String> contentArrayList = new ArrayList<String>();
		contentArrayList.add(SensorData.SensorType.NO_SENSOR.toString());
		contentArrayList.add(SensorData.SensorType.DISTANCE.toString());
		contentArrayList.add(SensorData.SensorType.BUTTON.toString());
		contentArrayList.add(SensorData.SensorType.LIGHT.toString());
		contentArrayList.add(SensorData.SensorType.SOUND.toString());
		SlotDataSpinner spinnerSlot = new SlotDataSpinner(getContextActivity(), contentArrayList);
		label.appendContent( spinnerSlot );
		
		//Widget 3 - TextDisplayOnly
		SlotTextDisplayOnly slotText =  new SlotTextDisplayOnly(AppRessources.context); 
		label.appendContent(slotText);
		
		//Widget 4 - Text
		label.appendContent( ">" );
		
		//Widget 5 - SlotDataText for textinput
		SlotDataNum slot = new SlotDataNum(AppRessources.context);
		label.appendContent( slot );
		
		this.getSlot(this.LABEL).add(label,0,0);
	}

	@Override
	protected int bodyColor() {
		return ColorPalette.colorOfControl;
	}

	@Override
	protected int bodyStrokeColor() {
		return ColorPalette.colorBodyStroke;
	}

}
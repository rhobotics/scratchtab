package com.binaryme.ScratchTab.Gui.Shapes.Tablet;

import android.app.Activity;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

public class ShapeAcceleration extends AbstractSensorShape {

	public static MySensor sensor = new MySensor(MySensor.ACCELERATION);	
	
	public ShapeAcceleration(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	public MySensor getSensor() {
		return sensor;
	};

}
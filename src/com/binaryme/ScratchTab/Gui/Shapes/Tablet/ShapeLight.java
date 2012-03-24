package com.binaryme.ScratchTab.Gui.Shapes.Tablet;

import android.app.Activity;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

public class ShapeLight extends AbstractSensorShape {

	public static MySensor sensor = new MySensor(MySensor.LIGHT);	
	
	public ShapeLight(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	public MySensor getSensor() {
		return sensor;
	};

}
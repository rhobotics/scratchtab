package com.binaryme.ScratchTab.Gui.Shapes.Tablet;

import android.app.Activity;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

public class ShapeOrientation extends AbstractSensorShape {

	public static MySensor sensor = new MySensor(MySensor.ORIENTATION);	
	
	public ShapeOrientation(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	public MySensor getSensor() {
		return sensor;
	};

}
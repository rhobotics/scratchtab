package com.binaryme.ScratchTab.Gui.Shapes.Robot;

import android.app.Activity;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsSingleLevel;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.tools.ColorPalette;

public class ShapeDriveForwardLimited extends ShapeWithSlotsSingleLevel {

	public ShapeDriveForwardLimited(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent("Drive");
		
		//Widget 2 - Int Number
		SlotDataNum slotnum = new SlotDataNum(getContext());
		label.appendContent(slotnum);
		
		//Widget 3 - Text
		label.appendContent("cm");
		
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
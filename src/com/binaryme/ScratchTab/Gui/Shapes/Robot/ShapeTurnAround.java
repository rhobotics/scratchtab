package com.binaryme.ScratchTab.Gui.Shapes.Robot;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Robot.TurnAround;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsSingleLevel;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.tools.ColorPalette;

public class ShapeTurnAround extends ShapeWithSlotsSingleLevel {

	public ShapeTurnAround(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent( "Turn around " );
		
		//Widget 2 - Spinner with all possible NXT Sensors
		ArrayList<String> contentArrayList = new ArrayList<String>();
		contentArrayList.add(TurnAround.Direction.RIGHT.toString());
		contentArrayList.add(TurnAround.Direction.LEFT.toString());
		SlotDataSpinner spinnerSlot = new SlotDataSpinner(getContextActivity(), contentArrayList);
		label.appendContent( spinnerSlot );
		
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
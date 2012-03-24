package com.binaryme.ScratchTab.Gui.Shapes.Control;

import android.app.Activity;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsDoubleLevel;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.tools.ColorPalette;

public class ShapeDoUntil extends ShapeWithSlotsDoubleLevel {

	public ShapeDoUntil(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		label.appendContent("Repeat until");
		
		//Widget 1 - Boolean
		SlotBoolean slotBoolean = new SlotBoolean(getContext());
		label.appendContent(slotBoolean);
		
		this.getSlot(this.LABEL_TOP).add(label,0,0);
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
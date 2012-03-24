package com.binaryme.ScratchTab.Gui.Shapes.Control;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsTrippleLevel;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.tools.ColorPalette;

public class ShapeIfElse extends ShapeWithSlotsTrippleLevel {

	public ShapeIfElse(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		
		//LABEL_TOP
		Label labelTop = new Label(this.getContext());
		
			//Widget 1. Text
			labelTop.appendContent("If");

			//Widget 2. Boolean
			SlotBoolean slotBoolean = new SlotBoolean(getContext());
			labelTop.appendContent(slotBoolean);
			
			this.getSlot(this.LABEL_TOP).add(labelTop,0,0);

			
		//LABEL_TOP
		Label labelMiddle = new Label(this.getContext());

			//Widget 1. Text
			labelMiddle.appendContent("Else");
		
			this.getSlot(this.LABEL_MIDDLE).add(labelMiddle,0,0);
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

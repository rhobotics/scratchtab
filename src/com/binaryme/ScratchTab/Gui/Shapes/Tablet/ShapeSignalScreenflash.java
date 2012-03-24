package com.binaryme.ScratchTab.Gui.Shapes.Tablet;

import android.app.Activity;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsSingleLevel;
import com.binaryme.tools.ColorPalette;

public class ShapeSignalScreenflash extends ShapeWithSlotsSingleLevel {

	public ShapeSignalScreenflash(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent("Make a screenflash");
		
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
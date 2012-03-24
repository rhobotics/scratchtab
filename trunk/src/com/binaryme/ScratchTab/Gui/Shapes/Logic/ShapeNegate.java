package com.binaryme.ScratchTab.Gui.Shapes.Logic;

import android.app.Activity;
import android.graphics.Color;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsDiamond;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNumDecimal;
import com.binaryme.tools.ColorPalette;

public class ShapeNegate extends ShapeWithSlotsDiamond{

	public ShapeNegate(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent( "not" );
		
		//Widget 2 - Boolean
		label.appendContent( new SlotBoolean(getContextActivity()) );
		
		this.getSlot(this.LABEL).add(label,0,0);
	}
	
	@Override
	protected int bodyColor() {
		return ColorPalette.colorOfLogic;
	}

	@Override
	protected int bodyStrokeColor() {
		return ColorPalette.colorBodyStroke;
	}

}

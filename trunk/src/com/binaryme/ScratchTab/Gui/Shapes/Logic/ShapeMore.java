package com.binaryme.ScratchTab.Gui.Shapes.Logic;

import android.app.Activity;
import android.graphics.Color;

import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsDiamond;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNumDecimal;
import com.binaryme.tools.ColorPalette;

public class ShapeMore extends ShapeWithSlotsDiamond{

	public ShapeMore(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Double field
		label.appendContent( new SlotDataNumDecimal(getContextActivity()) );
		
		//Widget 2 - Text
		label.appendContent( ">" );
		
		//Widget 3 - Double field
		label.appendContent( new SlotDataNumDecimal(getContextActivity()) );
		
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

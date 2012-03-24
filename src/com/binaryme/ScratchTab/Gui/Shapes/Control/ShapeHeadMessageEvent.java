package com.binaryme.ScratchTab.Gui.Shapes.Control;

import android.app.Activity;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlotsHead;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataText;
import com.binaryme.tools.ColorPalette;

public class ShapeHeadMessageEvent extends ShapeWithSlotsHead {

	public ShapeHeadMessageEvent(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void initLabels() {
		Label label = new Label(this.getContext());
		
		//Widget 1 - Text
		label.appendContent( "If receive message " );
		
		//Widget 2 - SlotDataText for textinput
		SlotDataText slot = new SlotDataText(AppRessources.context);
		label.appendContent( slot );
		
		this.getSlot(this.LABEL).add(label,0,0);
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
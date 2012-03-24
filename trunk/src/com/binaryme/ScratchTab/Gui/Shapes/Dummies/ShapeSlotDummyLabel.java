package com.binaryme.ScratchTab.Gui.Shapes.Dummies;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;

import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

public class ShapeSlotDummyLabel extends ShapeSlotDummy {

	public ShapeSlotDummyLabel(Activity context, Block<? extends Shape> associatedBlock) {
		super(context,associatedBlock);
	}
	/** Label Slots always have a label in them, so they do not need to be drawn. */
	@Override
	public Path drawPath() {
		return new Path();
	}
	@Override
	public BlockType getType() {
		return BlockType.LABEL;
	}
	
	
	@Override
	public Path drawPathEmpty() {
		return new Path();
	}
	@Override
	public Path drawPathActive() {
		return new Path();
	}
	@Override
	public Path drawPathHover() {
		return new Path();
	}
	
	//COLORS
	@Override
	public int fillColorEmpty() {
		return Color.TRANSPARENT;
	}
	@Override
	public int strokeColorEmpty() {
		return Color.TRANSPARENT;
	}
	@Override
	public int fillColorActive() {
		return Color.TRANSPARENT;
	}
	@Override
	public int strokeColorActive() {
		return Color.TRANSPARENT;
	}
	@Override
	public int fillColorHover() {
		return Color.TRANSPARENT;
	}
	@Override
	public int strokeColorHover() {
		return Color.TRANSPARENT;
	}

}
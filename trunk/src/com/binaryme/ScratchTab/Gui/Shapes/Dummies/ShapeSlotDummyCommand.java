package com.binaryme.ScratchTab.Gui.Shapes.Dummies;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Path.Direction;

import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.tools.ColorPalette;

public class ShapeSlotDummyCommand extends ShapeSlotDummy {

	public ShapeSlotDummyCommand(Activity context, Block<? extends Shape> associatedBlock) {
		super(context,associatedBlock);
	}
	
//DRAWING HELPER METHODS
	
	//the shape ShapeSlotDummyCommandBottom overrides ths, to draw a longer rectangle
	protected Path drawSlotCommand() {
		//draw a new Path
		Path path = new Path();
		
		//TODO here should be the size of the slot
		Block<?> bl = this.getAssociatedBlock().getNextBodyBlock();
		Shape s = this.getAssociatedBlock().getNextBodyBlock().getShape();
		int i = this.getAssociatedBlock().getNextBodyBlock().getShape().labelWidth;
		
//		@SuppressWarnings("unused")
//		int b = this.getAssociatedBlock().getNextBodyBlock().getShape().labelWidth-this.blockSlotWidth;
//		int a = this.minInnerChildHeight;
				
		//draw a rectangle		
		path.addRect(0, 0, (this.getAssociatedBlock().getNextBodyBlock().getShape().labelWidth-this.blockSlotWidth), this.minInnerChildHeight, Direction.CCW);
		
		path.close();
		return path;
	}

//HOOKS
	
	//PATHS	
	@Override
	public Path drawPathEmpty() {
		//TODO: should return an empty path
		//return new Path();		//because the Command Slot in invisible, we do not need to draw any
		return drawSlotCommand();	//draw a command shape path, which is common for all states
	}

	@Override
	public Path drawPathActive() {
		return drawSlotCommand();	//draw a command shape path, which is common for all states
	}

	@Override
	public Path drawPathHover() {
		return drawSlotCommand();	//draw a command shape path, which is common for all states
	}

	@Override
	public BlockType getType() {
		return Block.BlockType.COMMAND;
	}	
	
	//COLORS
	@Override
	public int fillColorEmpty() {
		return Color.TRANSPARENT;					//command slot is transparent when empty
	}
	@Override
	public int strokeColorEmpty() {
		return ColorPalette.colorSlotEmptyStroke;	//command slot is transparent when empty
	}
	@Override
	public int fillColorActive() {
		return ColorPalette.colorSlotActiveFill;
	}
	@Override
	public int strokeColorActive() {
		return ColorPalette.colorSlotActiveStroke;
	}
	@Override
	public int fillColorHover() {
		return ColorPalette.colorSlotHoverFill;
	}
	@Override
	public int strokeColorHover() {
		return ColorPalette.colorSlotHoverStroke;
	}
	
	//SIZE
	//makes recursive measurement of inSlot and complete dimensions possible
	@Override
	public boolean calculateBlockSizeHook(ShapeDimensions shapeDimensions){
		shapeDimensions.unscaledCompleteHeight = shapeDimensions.getUnscaledShapeBoundsHeight();
		shapeDimensions.unscaledCompleteWidth  = shapeDimensions.getUnscaledShapeBoundsWidth();
		shapeDimensions.unscaledHeightInSlot  = 0;
		shapeDimensions.unscaledWidthInSlot = 0;
		return true; 
	}
	
}

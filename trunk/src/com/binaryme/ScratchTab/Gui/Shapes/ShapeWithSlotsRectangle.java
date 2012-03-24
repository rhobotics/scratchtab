package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Shapes.Shape.ShapeDimensions;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;

public abstract class ShapeWithSlotsRectangle  extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL = 1;

	public ShapeWithSlotsRectangle(Activity context,
			Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}

	@Override
	protected void fillTheSlotManager() {
		//LABEL
		SlotLabel theonlylabel = new SlotLabel(this.getContext(), this);
		this.mSlotManager.addSlot(LABEL, theonlylabel);
	}

	@Override
	protected void positionSlots() {
		//set the position of LABEL Slot
		int posXsm=ScaleHandler.scale(this.labelMargin);
		int posYsm=ScaleHandler.scale(this.labelMargin);
		this.mSlotManager.getSlot(LABEL).setPosition( posXsm , posYsm);
		
	}

	@Override
	public void extractUNSCALEDdataFromSlotManager() {
		this.labelWidth = Math.round(this.getSlot(LABEL).getUnscaledWidth());
		this.labelHeight = Math.round(this.getSlot(LABEL).getUnscaledHeight());
	}

	@Override
	public boolean calculateBlockSizeHook(ShapeDimensions shapeDimensions) {
		//no children should exceed square shape's size
		return false;
	}

	@Override
	public Path drawPath() {
		float currentheight = this.labelMargin+labelHeight+this.labelMargin; 		//labelHeight should contain the label's height
		float currentwidth = this.labelMargin+labelWidth+this.labelMargin; 		//labelHeight should contain the label's height
		
		//draw a new Path
		Path path = new Path();
//		path.moveTo(0, 0);
//		
//		path.rLineTo(currentwidth,0 );			//right
//		path.rLineTo(0, currentheight); 		//down and right
//		path.rLineTo(-currentwidth, 0);			//left
//		path.rLineTo(0, -currentheight);		//up
		
//		path.close();
		
		RectF rectf = new RectF(0, 0, currentwidth, currentheight);
		path.addRoundRect(rectf, 10, 10, Direction.CCW);
		
		return path;
	}
	

	@Override
	public BlockType getType() {
		return BlockType.DATA;
	}

}

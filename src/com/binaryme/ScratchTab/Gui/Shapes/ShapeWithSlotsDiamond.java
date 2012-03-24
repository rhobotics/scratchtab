package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Path;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;

public abstract class ShapeWithSlotsDiamond extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL = 1;

	public ShapeWithSlotsDiamond(Activity context,
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
		int posXsm=ScaleHandler.scale(booleanSlotPike);
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
		//no children should exceed diamond shape's size
		return false;
	}

	@Override
	public Path drawPath() {
		float thislabelshalfheight = labelHeight/2; 		//labelHeight should contain the label's height
		thislabelshalfheight += this.labelMargin; 	//make the block a little bit higher, depending on the padding
		
		//draw a new Path
		Path path = new Path();
		path.moveTo(0, thislabelshalfheight);
		
		path.rLineTo(booleanSlotPike, -thislabelshalfheight);		//up and right
		path.rLineTo(this.labelWidth,0 );							//right
		path.rLineTo(booleanSlotPike, thislabelshalfheight); 		//down and right
		path.rLineTo(-booleanSlotPike, thislabelshalfheight); 		//down and left
		path.rLineTo(-this.labelWidth, 0);							//left
		path.rLineTo(-booleanSlotPike, -thislabelshalfheight);		//up left
		
		path.close();
		return path;
	}
	

	@Override
	public BlockType getType() {
		return BlockType.BOOLEAN;
	}

}

package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Path;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.tools.M;

public abstract class ShapeWithSlotsSingleLevelLast extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL = 1;
	
	public ShapeWithSlotsSingleLevelLast(Activity context, Block<? extends Shape> associatedBlock){
		super(context,associatedBlock);
	}
	
	
	//DRAWING HOOK
	@Override
	public Path drawPath() {
		int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth+labelMargin;
		
		
		//draw a new Path
		Path path = new Path();
		
		path.moveTo(0, 0);		

		//right
		path.rLineTo((blockBackWidth-blockSlotWidth), 0);
		drawNotch(path, DIRECTION.RIGHT);
		path.rLineTo(blockProtrusion, 0 );
		//down
		path.rLineTo(0, Math.max(minLabelHeight,blockTopHeight) );
		//left
		path.rLineTo(-(blockProtrusion), 0 );
		path.rLineTo( -blockBackWidth, 0);
		//up
		path.close();										
		
		return path;
	}
	
	/**
	 * set {@link Shape#unscaledCompleteHeight}
	 * set {@link Shape#unscaledCompleteWidth}
	 * consider shape's overflow and shape's obstacle 
	 * 
	 * set {@link Shape#unscaledHeightInSlot}
	 * set {@link Shape#unscaledWidthInSlot} 
	 */
	@Override
	public boolean calculateBlockSizeHook(ShapeDimensions dimensions) {
		/**
		 * IMPORTANT: lookout for scaled and unscaled values.
		 * Slots' measured Values are scaled.
		 */
		
		int unscaledShapeBoundsHeight 			= dimensions.getUnscaledShapeBoundsHeight();
		int unscaledShapeBoundsHeightNoNotch 	= unscaledShapeBoundsHeight - this.blockSlotHeight;
		
		int unscaledWidthInSlot;
		int unscaledHeightInSlot;
		
		int unscaledCompleteWidth;
		int unscaledCompleteHeight;
		
//1.	unscaledWidthInSlot, unscaledHeightInSlot - recursive, no obstacles, for drawing of shapes in slots
	//width
		int[] arr = {
				(this.getSlot(LABEL).getMeasuredWidth()+this.blockBackWidth-this.blockSlotWidth+this.labelMargin),
			};
		unscaledWidthInSlot = ScaleHandler.unscale( M.max(arr) );

	//height
		unscaledHeightInSlot = unscaledShapeBoundsHeightNoNotch; 
		
		
		
//2.	unscaledCompleteHeight, unscaledCompleteWidth	- recursive, with obstacles, for calculating the total size of block
	//width
		unscaledCompleteWidth = unscaledWidthInSlot; //the block-width and inside-slot-width are the same

	//height
		unscaledCompleteHeight = unscaledShapeBoundsHeightNoNotch;  //already contains the obstacle
		
		
		//save the data back to the ShapeDimensions object which was passed by referense
		dimensions.unscaledCompleteWidth=unscaledCompleteWidth;
		dimensions.unscaledCompleteHeight=unscaledCompleteHeight;
		
		dimensions.unscaledWidthInSlot=unscaledWidthInSlot;
		dimensions.unscaledHeightInSlot=unscaledHeightInSlot;

		return true;
	}

	@Override
	public BlockType getType() {
		return BlockType.COMMAND;
	}

	@Override
	public void fillTheSlotManager() {
		//LABEL
		SlotLabel label = new SlotLabel(this.getContext(), this);
		this.mSlotManager.addSlot(LABEL, label);
	}
	
	@Override
	public void extractUNSCALEDdataFromSlotManager() {
		
		this.labelWidth = Math.round(this.getSlot(LABEL).getUnscaledWidth());
		this.labelHeight = Math.round(this.getSlot(LABEL).getUnscaledHeight());
		
		int minBlockTopHeight = labelMargin+Shape.minLabelHeight+labelMargin;
		this.blockTopHeight = Math.round( Math.max( minBlockTopHeight, labelHeight+2*labelMargin));
		
	}
	
	@Override
	protected void positionSlots() {
		/**
		 * REMEMBER: Every variable which you use to draw should be set in extractUNSCALEDdataFromSlotManager
		 * use UNSCALED data here
		 */
				
		//set the position of LABEL_TOP Slot
		int posXsm=ScaleHandler.scale(this.blockBackWidth-this.blockSlotWidth);
		int posYsm=ScaleHandler.scale(this.labelMargin+this.blockSlotHeight);
		this.mSlotManager.getSlot(LABEL).setPosition( posXsm , posYsm);
	}
	
}

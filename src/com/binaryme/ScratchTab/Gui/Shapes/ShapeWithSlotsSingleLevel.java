package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.tools.M;

public abstract class ShapeWithSlotsSingleLevel extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL = 1;
	public final static int CHILD_BELOW = 2;
	
	//define some variables, used during the drawing and positioning
	int blockHeightInclMargins;
	
	public ShapeWithSlotsSingleLevel(Activity context, Block<? extends Shape> associatedBlock){
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
		path.rLineTo(0, Math.max(minLabelHeight,this.blockHeightInclMargins) );
		//left
		path.rLineTo(-(blockProtrusion), 0 );
		drawNotch(path, DIRECTION.LEFT);
		path.rLineTo( -( blockBackWidth-blockSlotWidth ), 0);
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
				(this.getSlot(CHILD_BELOW).getMeasuredWidth() )
			};
		unscaledWidthInSlot = ScaleHandler.unscale( M.max(arr) );

	//height
		unscaledHeightInSlot = unscaledShapeBoundsHeightNoNotch; 
		//recursion
		unscaledHeightInSlot += this.mSlotManager.getSlot(CHILD_BELOW).getShape().unscaledHeightInSlot;
		
		
		
		
		
//2.	unscaledCompleteHeight, unscaledCompleteWidth	- recursive, with obstacles, for calculating the total size of block
	//width
		unscaledCompleteWidth = unscaledWidthInSlot; //the block-width and inside-slot-width are the same

	//height
		unscaledCompleteHeight = unscaledShapeBoundsHeightNoNotch;  //already contains the obstacle
		//recursion
		unscaledCompleteHeight += this.mSlotManager.getSlot(CHILD_BELOW).getShape().unscaledCompleteHeight;
		
		
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
		//UPPER_LABEL
		SlotLabel label = new SlotLabel(this.getContext(), this);
		this.mSlotManager.addSlot(LABEL, label);
				
		//CHILD_BELOW
		SlotCommand belowslot = new SlotCommand(this.getContext());
		this.mSlotManager.addSlot(CHILD_BELOW, belowslot);
	}
	
	@Override
	public void extractUNSCALEDdataFromSlotManager() {
		
		this.labelWidth = Math.round(this.getSlot(LABEL).getUnscaledWidth());
		this.labelHeight = Math.round(this.getSlot(LABEL).getUnscaledHeight());
		this.blockHeightInclMargins = blockSlotHeight+labelHeight+labelMargin;
		
	}
	
	@Override
	protected void positionSlots() {
		/**
		 * REMEMBER: Every variable which you use to draw should be set in extractUNSCALEDdataFromSlotManager
		 * use UNSCALED data here
		 */
				
		//set the position of LABEL_TOP Slot
		int posXsm=ScaleHandler.scale(this.blockBackWidth-this.blockSlotWidth);
		int posYsm=ScaleHandler.scale(this.blockSlotHeight);
		this.mSlotManager.getSlot(LABEL).setPosition( posXsm , posYsm);
		
		//set the position of CHILD_BOTTOM Slot
		int posXbc=ScaleHandler.scale(0);
		int posYbc=ScaleHandler.scale( blockSlotHeight+labelHeight+labelMargin );
		
		this.mSlotManager.getSlot(CHILD_BELOW).setPosition( posXbc ,  posYbc );
	}
	
}

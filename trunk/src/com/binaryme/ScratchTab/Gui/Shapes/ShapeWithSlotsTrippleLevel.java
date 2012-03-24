package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Path;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.tools.M;

public abstract class ShapeWithSlotsTrippleLevel extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL_TOP 			= 1;
	public final static int LABEL_MIDDLE 		= 2;
	public final static int LABEL_BOTTOM 		= 3;
	public final static int CHILD_INNER 		= 4;
	public final static int CHILD_INNER_SECOND 	= 5;
	public final static int CHILD_BELOW 		= 6;
	
	//inner child
	public int innerChildWidth;
	public int innerChildHeight;
	
	//inner child second
	public int innerChildSecondWidth;
	public int innerChildSecondHeight;
	
	//labels
	public int labelHeightTop;
	public int labelHeightMiddle;
	public int labelHeightBottom;
	
	public int blockMiddleHeight;
	
	public ShapeWithSlotsTrippleLevel(Activity context, Block<? extends Shape> associatedBlock){
		super(context,associatedBlock);
	}
	
	
	//DRAWING HOOK
	@Override
	public Path drawPath() {
		//draw a new Path
		Path path = new Path();
		path.moveTo(0, 0);
		
		//draw a BlockHead
		drawBlockHead(path, labelWidth, labelHeightTop);
		
		//down
		path.rLineTo(0, Math.max(innerChildHeight,minInnerChildHeight) );

		//block midde
		drawBlockMiddle(path,this.blockMiddleHeight);
		
		//down
		path.rLineTo(0, Math.max(innerChildSecondHeight,minInnerChildHeight) );
		
		//draw a BlockBottom
		drawBlockBottomWithSlot(path);
		
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
				(this.getSlot(LABEL_TOP).getMeasuredWidth() + ScaleHandler.scale(this.blockSlotWidth)),
				(this.getSlot(LABEL_MIDDLE).getMeasuredWidth() + ScaleHandler.scale(this.blockSlotWidth)),
				(this.getSlot(LABEL_BOTTOM).getMeasuredWidth() + ScaleHandler.scale(this.blockSlotWidth)),
				(this.getSlot(CHILD_INNER).getMeasuredWidth() + ScaleHandler.scale(this.blockBackWidth)),
				(this.getSlot(CHILD_INNER_SECOND).getMeasuredWidth() + ScaleHandler.scale(this.blockBackWidth)),
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
		SlotLabel upperlabel = new SlotLabel(this.getContext(), this);
		this.mSlotManager.addSlot(LABEL_TOP, upperlabel);
				
		//INNER_CHILD
		SlotCommand innerslot = new SlotCommand(this.getContext() );
		this.mSlotManager.addSlot(CHILD_INNER, innerslot);

		//MIDDLE_LABEL
		SlotLabel middleLabel = new SlotLabel(this.getContext(),this);
		this.mSlotManager.addSlot(LABEL_MIDDLE, middleLabel);
		
		//INNER_CHILD_SECONG
		SlotCommand innerslotSecond = new SlotCommand(this.getContext() );
		this.mSlotManager.addSlot(CHILD_INNER_SECOND, innerslotSecond);
		
		//BOTTOM_LABEL
		SlotLabel bottomlabel = new SlotLabel(this.getContext(),this);
		this.mSlotManager.addSlot(LABEL_BOTTOM, bottomlabel);
		
		//CHILD_BELOW
		SlotCommand belowslot = new SlotCommand(this.getContext());
		this.mSlotManager.addSlot(CHILD_BELOW, belowslot);
	}
	
	@Override
	public void extractUNSCALEDdataFromSlotManager() {
		
		this.labelWidth = Math.max(		Math.round(this.getSlot(LABEL_TOP).getUnscaledWidth()),
										Math.round(this.getSlot(LABEL_MIDDLE).getUnscaledWidth()) );
		
		this.labelHeightTop 	= Math.round(this.getSlot(LABEL_TOP).getUnscaledHeight());
		
		this.labelHeightTop 	= Math.round(this.getSlot(LABEL_TOP).getUnscaledHeight());
		this.labelHeightMiddle 	= Math.round(this.getSlot(LABEL_MIDDLE).getUnscaledHeight());
		this.labelHeightBottom 	= Math.round(this.getSlot(LABEL_BOTTOM).getUnscaledHeight());
	
		this.innerChildHeight 		= Math.max( Math.round(this.getSlot(CHILD_INNER).getShape().unscaledHeightInSlot), this.minInnerChildHeight);
		this.innerChildSecondHeight = Math.max( Math.round(this.getSlot(CHILD_INNER_SECOND).getShape().unscaledHeightInSlot), this.minInnerChildHeight);
		
		this.blockTopHeight 	= Math.round( Math.max( this.minBlockTopHeight, this.labelHeightTop		+2*this.blockSlotHeight));
		this.blockMiddleHeight 	= Math.round( Math.max( this.minBlockTopHeight, this.labelHeightMiddle	+2*this.blockSlotHeight));
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
		this.mSlotManager.getSlot(LABEL_TOP).setPosition( posXsm , posYsm);
		
		//set the position of CHILD_INNER Slot
		int posXic=ScaleHandler.scale( this.blockBackWidth);
		int posYic=ScaleHandler.scale( Math.max(this.minBlockTopHeight, Math.round (this.mSlotManager.getSlot(LABEL_TOP).getUnscaledHeight()) + 2*this.blockSlotHeight )  );
		this.mSlotManager.getSlot(CHILD_INNER).setPosition( posXic , posYic);
		
		//set the position of LABEL_MIDDLE Slot
		int posXsm2=ScaleHandler.scale(this.blockBackWidth-this.blockSlotWidth);
		int posYsm2=ScaleHandler.scale(	 this.blockTopHeight
										+this.innerChildHeight
										+blockSlotHeight);
		this.mSlotManager.getSlot(LABEL_MIDDLE).setPosition( posXsm2 , posYsm2);
		
		//set the position of CHILD_INNER_SECOND Slot
		int posXicm=ScaleHandler.scale( this.blockBackWidth);
		int posYicm=ScaleHandler.scale( this.blockTopHeight
										+this.innerChildHeight
										+this.blockMiddleHeight);		
		this.mSlotManager.getSlot(CHILD_INNER_SECOND).setPosition( posXicm , posYicm);
		
		//set the position of BOTTOM_LABEL Slot
		int posXbl=ScaleHandler.scale(this.blockBackWidth-this.blockSlotWidth);
		int posYbl= Math.round( ScaleHandler.scale( 
											Math.max(
												this.minInnerChildHeight, 
												(this.innerChildHeight + posYicm + this.mSlotManager.getSlot(CHILD_INNER_SECOND).getUnscaledHeight() ) 
											)
								)
					);
		this.mSlotManager.getSlot(LABEL_BOTTOM).setPosition( posXbl ,  posYbl );
		
		//set the position of CHILD_BOTTOM Slot
		int posXbc=ScaleHandler.scale(0);
		int posYbc=ScaleHandler.scale( Math.round(this.blockTopHeight + this.innerChildHeight + this.blockMiddleHeight + this.innerChildSecondHeight + this.blockBottomHeight ) );
		
		this.mSlotManager.getSlot(CHILD_BELOW).setPosition( posXbc ,  posYbc );
	}
	
}

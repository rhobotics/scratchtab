package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Slots.SlotCommand;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.tools.M;

public abstract class ShapeWithSlotsHead extends ShapeWithSlots{
	
	// Slot IDs, to use inside of the SlotManager
	public final static int LABEL = 1;
	public final static int CHILD_BELOW = 2;
	
	public ShapeWithSlotsHead(Activity context, Block<? extends Shape> associatedBlock){
		super(context,associatedBlock);
	}
	
	
	//DRAWING HOOK
	@Override
	public Path drawPath() {
		int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth+labelMargin;
		
		int bezierX1	= M.cm2px(1);
		int bezierY1	= -headHeight;
		
		int bezierX2	= M.cm2px(1)+bezierX1;
		int bezierY2	= headHeight;
		
		int bezierX3	= blockProtrusion+this.blockBackWidth ;
		int bezierY3	= headHeight;
		
		
		//draw a new Path
		Path path = new Path();
		
		//start a little bit under the 0,0
		path.moveTo(0, headHeight);	 

		//bezier curve to the right
		path.cubicTo(bezierX1, bezierY1,    bezierX2, bezierY2,    bezierX3, bezierY3);
		
		
		//down
		path.rLineTo(0, Math.max(minLabelHeight, this.blockTopHeight) );
		//left
		path.rLineTo(-(blockProtrusion), 0 );
		drawNotch(path, DIRECTION.LEFT);
		path.rLineTo( -( blockBackWidth-blockSlotWidth ), 0);
		//up
		path.rLineTo(0, -Math.max(minLabelHeight,this.blockTopHeight) );
		path.close();		
		
		
//		//TODO
//		//down
//		path.rLineTo(0, 40 );
//		//left
//		path.rLineTo( -40, 0);
		//up
		path.close();
		
		//TODO
		RectF test = new RectF();
		path.computeBounds(test, true);
		Log.d("height",""+test.height());
		
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
		
		//for some reason "head"-shape's path height, with a bezier curve on the top, is measured wrong - calculate height manually
//		int unscaledShapeBoundsHeight 			= dimensions.getUnscaledShapeBoundsHeight();
		int unscaledShapeBoundsHeight 			= blockTopHeight+headHeight+this.blockSlotHeight;
		int unscaledShapeBoundsHeightNoNotch 	= unscaledShapeBoundsHeight - this.blockSlotHeight;
		
		int unscaledWidthInSlot;
		int unscaledHeightInSlot;
		
		int unscaledCompleteWidth;
		int unscaledCompleteHeight;
		
		Log.d("height",""+unscaledShapeBoundsHeight);
		
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
		unscaledCompleteHeight = unscaledShapeBoundsHeightNoNotch;
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
		return BlockType.HEAD;
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
		this.blockTopHeight=Math.max(labelHeight+2*labelMargin, minLabelHeight);
	}
	
	@Override
	protected void positionSlots() {
		/**
		 * REMEMBER: Every variable which you use to draw should be set in extractUNSCALEDdataFromSlotManager
		 * use UNSCALED data here
		 */
				
		//TODO
		//set the position of LABEL Slot
		int posXsm=ScaleHandler.scale(this.blockBackWidth-this.blockSlotWidth);
		int posYsm=ScaleHandler.scale(this.labelMargin + this.headHeight) ;
		this.mSlotManager.getSlot(LABEL).setPosition( posXsm , posYsm);
		
		//set the position of CHILD_BOTTOM Slot
		int posXbc=ScaleHandler.scale(0);
		int posYbc=ScaleHandler.scale( this.blockTopHeight + this.headHeight);
		
		this.mSlotManager.getSlot(CHILD_BELOW).setPosition( posXbc ,  posYbc );
	}
}

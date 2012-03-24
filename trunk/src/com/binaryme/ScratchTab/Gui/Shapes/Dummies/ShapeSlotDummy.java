package com.binaryme.ScratchTab.Gui.Shapes.Dummies;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Path;

import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.Slot.SlotMode;

/** ShapeSlotDummy's descendants are called Dummies. A Dummy draws an images, which represents empty slots. Dummies never have children. */
public abstract class ShapeSlotDummy extends Shape {

	/* 
	 * According to the mode of the Dummy, it is drawn in a different manner - with a frame or highlighted completely
	 * possible modes are Slot EMPTY,ACTIVE,HOVER,FILLED. When the Dummy is in FILLED mode - it should be invisible. The filled child will take over the drawing. */	    
	protected Slot.SlotMode mode;  
	
	public ShapeSlotDummy(Activity context, Block<? extends Shape> associatedBlock) {
		super(context, associatedBlock);
	}
	
//GETTER AND SETTER
	public void setMode(Slot.SlotMode mode){
		if(this.mode!=mode){
			this.mode = mode;
			this.clearCache();		//destroy the cache.
		}
	}
	public Slot.SlotMode getMode(){
		return this.mode;
	}
	
//DRAWING
	/** Implementing the hook, which is responsible for drawing the SLot in the right manner. 
	 *  Called every time, when cached was cleared and drawing is possible. */
	@Override
	public Path drawPath(){
		Path result = null;
		
		//before drawing the path - choose the right color, according to the rightmode
		this.chooseColor(this.mode);
		
		switch (this.mode){
			case EMPTY :
				result= this.drawPathEmpty();
				break;
			case ACTIVE :
				result= this.drawPathActive();
				break;
			case HOVER :
				result= this.drawPathHover();
				break;
			case FILLED :
				result = new Path(); //if the slot, which this dummy represents is filled, dummy should be invisible
				break;
		}
		return result;
	}

	/** Method to load the right drawing colors, according to the current mode. Called Every time, when the mode changes by the method {@link #setMode(SlotMode)}. */
	private void chooseColor(SlotMode mode){
		switch (mode){
		case EMPTY :
			this.setCurrentFillColor(this.fillColorEmpty());
			this.setCurrentStrokeColor(this.strokeColorEmpty());
			break;
		case ACTIVE :
			this.setCurrentFillColor(this.fillColorActive());
			this.setCurrentStrokeColor(this.strokeColorActive());
			break;
		case HOVER :
			this.setCurrentFillColor(this.fillColorHover());
			this.setCurrentStrokeColor(this.strokeColorHover());
			break;
		}
	}
	

	
//IMPLEMENT HOOKS
	
	@Override
	protected void initStrokeColor(Paint strokeColor) {
		//Dummies have a Stroke Width of 2
		strokeColor.setStrokeMiter(10);
		strokeColor.setStrokeWidth(2);
	}
	@Override
	protected void initFillColor(Paint fillColor) {
		//because dummie's color initiation happens, when the initial mode is set - this hook is not needed
	}
	@Override
	public boolean calculateBlockSizeHook(ShapeDimensions shapeDimensions){
		return false; //the dummies size is calculated as default, by measuring it's shape. By returning false this method passes the measuring to {@link Block#onMeasure() }
	}
	
	
//HOOKS
	
	/*
	 * This class ShapeSlotDummy introduces drawing of slots, depending on the current SlotMode: EMPTY,ACTIVE,HOVER
	 * This mode dependent drawing will be implemented in the following hooks. They will be then called by TabScratch in the right mode.  
	 */
	public abstract Path drawPathEmpty();
	public abstract Path drawPathActive();
	public abstract Path drawPathHover();
	
	//Hooks, to choose the stroke and fill color for every slot mode 
	public abstract int fillColorEmpty();
	public abstract int strokeColorEmpty();
	public abstract int fillColorActive();
	public abstract int strokeColorActive();
	public abstract int fillColorHover();
	public abstract int strokeColorHover();
	
}
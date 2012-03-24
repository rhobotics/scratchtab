package com.binaryme.ScratchTab.Gui.Blocks;

import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.ScratchTab.Gui.Slots.Slot;



/** Class BlockWithSlots encapsulates the decision about the type of the variable {@link #img} by passing the ShapeWithSlots.class as generic parameter to the parent class.*/
public abstract class BlockWithSlots extends Block<ShapeWithSlots>{
	
	protected ShapeWithSlots.SlotManager mSlotManager;
	
	public BlockWithSlots(Activity context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public BlockWithSlots(Activity context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public BlockWithSlots(Activity context) {
		super(context);
		init();
	}
	
	private void init(){
		addSlotsToViewTree();	
	}
	
	/** Here Shape.SlotManager inserts the slots, defined in the concrete Shape {@link #img}. */
	protected void addSlotsToViewTree(){
		img.mSlotManager.addSlotsTo(this);

		//remember the SlotManager. It will be used for redrawing the Shape Stack
		this.mSlotManager = img.mSlotManager;
	}
	
	
//GETTER AND SETTER
	/** All BlockWithSlots are created with a ShapeWithSlots as a generic parameter. ({@link ShapeWithSlots} inherits from {@link Shape})
	 *  That means, that we can change the getter to return ShapeWithSlots. */
	@Override
	public ShapeWithSlots getShape() {
		return this.img;
	}
	
//DRAWING
	/** onMeasure is overridden, to have the possibility of measuring the Slots in the predefined order, and calling extractUNSCALEDdataFromSlotManager every time. */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		HashMap<Integer, Slot> slots = null;
		
		/** Measure children
		 *  First measuring of the Slots with smaller key is important. 
		 *  It allows defining of measurement order of the slot, when defining them in the Shape.class descendant.
		 */
		if(this instanceof BlockWithSlots){
			slots = ((BlockWithSlots)this).mSlotManager.slots;

			//first measuring of the Slots with smaller key is important. It allows defining of measurement order of the slot, when defining them in the Shape class descendant.
			Integer[] keys= new Integer[slots.size()];
			slots.keySet().toArray(keys);
			Arrays.sort(keys);
			
			for (Integer key : keys) {
				
				//clearing cache before measuring the slot
				Shape s = slots.get(key).getInfill().getShape();
				if(s != null){ s.clearCache(); }
				
				//measureChild doesn't call the onMeasure properly - use onMeasure 
			    slots.get(key).onMeasure(MeasureSpec.UNSPECIFIED , MeasureSpec.UNSPECIFIED);

			    /** after measuring a slot from the slot manager is done-extract the data    */
			    ((ShapeWithSlots)this.img).extractUNSCALEDdataFromSlotManager();
			    ((ShapeWithSlots)this.img).clearCache();  		//force the Shape to redraw the cached image
			}
			
		}
		
		//now, after measuring all slots is done, slots should contain valid size information, now we can draw and measure any shape
		this.img.onMeasure();
		
		setMeasuredDimensionsPublic( ScaleHandler.scale(this.img.unscaledCompleteWidth),ScaleHandler.scale(this.img.unscaledCompleteHeight), 
										 this.img.unscaledCompleteWidth, this.img.unscaledCompleteHeight );
	}
	
	
}

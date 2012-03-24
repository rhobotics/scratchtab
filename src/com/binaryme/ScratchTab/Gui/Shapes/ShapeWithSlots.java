package com.binaryme.ScratchTab.Gui.Shapes;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;

import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.tools.ColorPalette;

public abstract class ShapeWithSlots extends Shape{
	
	//will manage the data about the slot positions, changing onLayout
	public SlotManager mSlotManager;

	public ShapeWithSlots(Activity context, Block<? extends Shape> associatedBlock) {
		super(context,associatedBlock);
		this.mSlotManager = new SlotManager(); 
		
		fillTheSlotManager(); //Hook to set the Slots inside the shape, and their positions
		initLabels();		  //Hook to initialise the LabelSlots
	}
	
	
//GETTER AND SETTER
	public Slot getSlot(int index){
		return this.mSlotManager.getSlot(index);
	}
	public int getBodyColor(){
		return this.bodyColor();
	}
	public int getBodyStrokeColor(){
		return this.bodyStrokeColor();
	}
	
	
//INNER CLASS
	/** The structure to store the slots of a concrete shape. 
	 *  Slots are saved by using {@link #addSlot(int, Slot)} where int - is a unique slot-id, by which the slot can be called later. 
	 *  
	 *  <p>
	 *  <h1>ATTENTION</h1> 
	 *  The slots with the smaller ids will be measured first.
	 *  This can be used, if drawing of one Slot A depends on another Slot B's parameters. 
	 *  When drawing a dependent Slot A - the Slot B parameters can be found in variables, which were previously defined in {@link Shape}, and the filling of which is defined in {@link ShapeWithSlots#extractUNSCALEDdataFromSlotManager() }.   
	 *  </p>
	 *  
	 *  <p>
	 *  <h1>How it works</h1>
	 *  First the Slot B is measured (if it's id is smaller in {@link SlotManager#addSlot(int, Slot)}). 
	 *  Then all relevant parameters are extracted as specified in the hook {@link ShapeWithSlots#extractUNSCALEDdataFromSlotManager() } and stored in the variables, predefined in {@link Shape}.
	 *  Then the Slot A is measured, which now can use the predefined variables from the class {@link Shape}. 
	 *  </p>
	 *  */
	public class SlotManager {
		
		public HashMap<Integer,Slot> slots = new HashMap<Integer,Slot>();
		
		/** Method will be used by descending Shapes to define new slots in them. */	
		public void addSlot(int slotid, Slot s){
			if(slots.get(slotid)!=null){throw new UnsupportedOperationException("A Slot with the index "+slotid+" allready exists. Slot cannot be overridden.");}
			
			if(s != null){ 
				slots.put(slotid, s);
			}else{
				throw new NullPointerException("Attempt of adding null as a slot failed.");
			}
		}
		
		public Slot getSlot(int index){
			return slots.get(index);
		}
		
		/** SlotManager knows how to add the slots to the View tree. Concrete Block will ask a concrete Shape to do so. */
		public void addSlotsTo(Block<?> block){
			for( Slot s : slots.values() ){
				block.addView(s);
			}
		}
	}
	
	
//DRAWING
	@Override
	public void onLayout() {
		//positioning of all slots is done, after the onMeasure pass. OnMeasure, onLayout is triggered by the block hierarchy.
		this.positionSlots();
	}
	
//IMPLEMENT HOOKS
	//Configuring the active colors, StrokeWidths, etc for PROGRAMMING BLOCK BODIES
	@Override
	protected void initStrokeColor(Paint strokeColor){
		//Block Bodies have a Stroke Width of 2
		strokeColor.setStrokeMiter(10);
		strokeColor.setStrokeWidth(2);
		strokeColor.setColor( this.bodyStrokeColor() );
	}
	@Override
	protected void initFillColor(Paint fillColor){
		fillColor.setColor( this.bodyColor() );
	}
	
//HOOKS
	
	/** Initiation of the Slots should be done in this hook. 
	 *  Choose the Labels and Slots, which are present in this shape, add them to the {@link #mSlotManager}
	 *  E.g. a DoubleLayer Shape should add a first level Label and a second level Label and an inner slot of type COMMAND */
	protected abstract void fillTheSlotManager();
	
	/**<p>Here the Slots should be positioned inside of the ShapeWithSlots (represents the programming-slot-body).
	 * <b>IMPORTANT: </b>
	 * The positions should be set in a scaled form. 
	 * Positioning is separated from Slot instantiation, because slots will be repositioned, every time the structure of the block changes, e.g. if the block infill has changed, because this may influence the position of other slots.
	 * </p>
	 * 
	 * <p>
	 * For slot positioning you should use the variables, which were defined in the shape. All variables, on which this class depends, like slot sizes, should have been updated during the onMeasure pass.
	 * E.g. {@link Shape#blockBackWidth}, {@link Shape#blockTopHeight}, {@link Shape#labelHeight}, {@link Shape#labelWidth}... and much more. There is a sketch, which should help to learn, what variable contains what GUI-element measure: TODO:insert the Block-sketch from illustrator!
	 * Additionally you can get every slot, by passing the right slot id (e.g. {@link ShapeWithSlotsDoubleLevel#LABEL_TOP} to the {@link #mSlotManager}) to get the right Slot 
	 * and by using the {@link #getUnscaledHeight()}, {@link #getUnscaledWidth()()} or {@link #getMeasuredHeight()}, {@link #getMeasuredHeight()} - <b>not the {@link View#getHeight()}, {@link View#getWidth()} methods because they are not allways up to date. </b>
	 * </p>
	 * 
	 * <p>
	 * <b>CONTRACT</b>: when you do the slot positioning - consider the Scale level, by using {@link ScaleHandler#scale(float)}, {@link ScaleHandler#scale(int)} methods. 
	 * This means, that all coordinates and sizes, which are used to position the slots, should be scaled by using the upper scale methods.  
	 * </p>
	 */
	protected abstract void positionSlots();
	
	/** Method works together with {@link #fillTheSlotManager()}. In this Hook the Data like <b>height</b> and <b>width</b> from Slots in the SlotManager should be assigned to the drawing variables.
	 *  <b>IMPORTANT: </b> 
	 *  <ul>
	 *  	<li>The data should be extracted in the unscaled form. Use {@link ScaleHandler#unscale(int)}.
	 *  	<li>You should set the predefined {@link Shape#labelHeight} and {@link Shape#labelWidth} variables which are implicitely used for drawing of commands Slot dummies. 
	 *  </ul>
	 * 	This method will be automatically called, if the slot size or position do change. */ 
	public abstract void extractUNSCALEDdataFromSlotManager();
	
	/** This hook will be implemented by the concrete Shpe, which knows the content of it's labels. 
	 *  The Label should be filled with Text, Slots and whatever the new Block should contain. */
	protected abstract void initLabels();
	
	/**Here the color for of the current block body is chosen. 
	 * This Method should return one of the static color, contained in {@link ColorPalette}, like:
	 *  <ul>
	 *  	<li>{@link ColorPalette#colorOfControl}</li>
	 *  	<li>{@link ColorPalette#colorOfLogic}</li>
	 *  	<li>{@link ColorPalette#colorOfNumbers}</li>
	 *  	<li>{@link ColorPalette#colorOfRobot}</li>
	 *  	<li>{@link ColorPalette#colorOfSensors}</li>
	 *  	<li>{@link ColorPalette#colorOfVariables}</li>
	 *  </ul>
	 *  The Programming-block-color helps the ScratchTab programmer to understand, what the current block is made for, does it represent a command or a number etc.*/
	protected abstract int bodyColor();
	
	/**Here the STROKE COLOR for of the current block body is chosen. 
	 * This Method should return one of the static color, contained in {@link ColorPalette}, like:
	 *  <ul>
	 *  	<li>{@link ColorPalette#colorOfControl}</li>
	 *  	<li>{@link ColorPalette#colorOfLogic}</li>
	 *  	<li>{@link ColorPalette#colorOfNumbers}</li>
	 *  	<li>{@link ColorPalette#colorOfRobot}</li>
	 *  	<li>{@link ColorPalette#colorOfSensors}</li>
	 *  	<li>{@link ColorPalette#colorOfVariables}</li>
	 *  </ul>
	 *  The Programming-block-color helps the ScratchTab programmer to understand, what the current block is made for, does it represent a command or a number etc.*/
	protected abstract int bodyStrokeColor();


}

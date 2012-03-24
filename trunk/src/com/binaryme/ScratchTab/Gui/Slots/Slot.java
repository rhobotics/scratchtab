package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;

import com.binaryme.DragDrop.DragHandler;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Gui.InterfaceBlockContainer;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummy;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;


/**
 * Performs as a filter which accepts only Blocks of the appropriate Type, which do have the right shape.
 * Performs as a Facade for measuring this Block's content (passes the Measuring to the content. Saves child's width and height.)
 *
 * @param <T> - the concrete dummy, which the current slot will use
 * @param <O> - the Object type, which the current slot will return on execution. Used in successors, when each of them implements its own executeForValue() 
 */
//TODO: allow adding some chosen Android Widgets to the Labels, like TextFields, DropDowns
public abstract class Slot<T extends BlockSlotDummy, O extends Object> extends Block<Shape> implements  InterfaceBlockContainer, OnDragListener, OnTouchListener, Executable<O> {

	private Block<? extends Shape> mContent;
	protected T blockSlotDummy;					//this will draw the image of an empty slot
	private SlotMode mode;						//slot presentation, e.g. frame and background depend on this
	
	private Boolean isInterrupted = false;		//if this executable will be interrupted - it will be set to true
	
	/**
	 * Because positioning of Slots will be done in a separate hook, to have the possibility of calling the positioning method
	 * again and again - this constructor won't be used in descending Classes, because here the position is set as constructor parameters.
	 * @param context
	 */
	private Slot(Activity context, int x, int y) {
		super(context);
		this.setPosition(x, y);
		
		initiateBlockSlotDummy_addToView();		//Hook, to set and position the slot dummy. Dummy is drawn, when the slot is empty.
		
		this.setMode(Slot.SlotMode.EMPTY); 		//On default slot is empty
		
		//listeners
		this.setOnDragListener(this);			//register the listener, to handle drag and drop
		this.setOnTouchListener(this);
	}
	
	/**
	 * Constructor, to use in descending Slot classes. The default position of a slot is 0,0
	 * @param context
	 */
	public Slot(Activity context ) {
		//call the (context, int, int) constructor
		this(context, 0,0);
	}
	
	/** Reflects the current state of the Slot. Changes on Hover etc. events */
	public static enum SlotMode{EMPTY,ACTIVE,HOVER,FILLED};
	
	/** Drawing of the Block class is overridden. Slots do not have own Shapes any more, they use dummy blocks to represent empty slots - so no Shape initialization */
	@Override
	protected Shape initiateShapeHere() {
		return null;
	}


//GETTER AND SETTER
	public void setMode(SlotMode mode){
		this.mode = mode;
		this.blockSlotDummy.setSlotMode(mode); //something has happened to the Slot, e.g. there is a block dragged above it etc.
//		if(mode == SlotMode.FILLED){
//			this.blockSlotDummy.setVisibility(GONE); 			//hide the dummy, when something has occupies the slot
//		}else{
//			this.blockSlotDummy.setVisibility(VISIBLE);
//		}
		
		this.blockSlotDummy.clearShapeCache(); //the Slot should be drawn in a different manner, so clear Shapes cache.	
	}
	public SlotMode getMode(){
		return this.mode;
	}
	/** This is a facade to use within this class, to get the data within of this slot, or the dummy if the Slot is empty at the moment */  
	public Block<?> getInfill(){
		Block<?> result = this.blockSlotDummy;
		if( (this.mode!=SlotMode.EMPTY) && (this.mContent != null)){
			if(this.mContent.isVisible()){
				result = this.mContent;
			}
		}
		return result;
	}
	public boolean isEmpty(){
		if(this.mContent == null){ return true;}
		else{ return false; }
	}
	protected T getBlockSlotDummy(){
		return this.blockSlotDummy;
	}
	
	


//EXECUTION
	@Override
	public void interruptExecution() {
		this.isInterrupted = true;
	}
	@Override
	public Boolean isInterrupted() {
		return this.isInterrupted; //interrupt the execution
	}
	

	
	/** comes from Executable */
	public Slot getSuccessorSlot(){
		//successors are handled by the blocks - no successors for the slots.
		return null;
	}
	@Override
	public void feedbackExecutionError() {
		// TODO Auto-generated method stub
	}

	@Override
	public void feedbackExecutionProcessRunning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void feedbackDisable() {
		// TODO Auto-generated method stub
	}
	
	

	/** Slot returns it's infills shape because it doesn't not have its own */
	@Override
	public Shape getShape() {
		return this.getInfill().getShape();
	}
	
//SLOT INTERACTION METHODS
	public void add(Block<? extends Shape> b, int x, int y) {
		/*
		 *  in Slots the coordinates x,y are irrelevant. Content is always positioned in 0,0
		 *  x,y are used when adding Blocks to the workspace
		 */
		
		//remove the block from it's old parent
		ViewParent vp = b.getParent();
		if(vp != null ){
			if(vp instanceof InterfaceBlockContainer){
				((InterfaceBlockContainer)vp).remove(b);
			}else{
				throw new IllegalArgumentException("The Parent of the Block, which system was trying to add to a Slot was not an InterfaceBlockContainer. Cant remove the Block from its old parent.");
			}
		}
		
		//add the block to this Slot
		this.add(b);
		b.setPosition(0, 0); //always add blocks at 0,0 into the slot
	}
	
	@Override
	public void remove(Block<? extends Shape> b) {
		this.clear();
	}
	
	/** Used to add Blocks into Slots, e.g. if the Block was dropped into a Slot. Coordinates x,y are not needed, because the dropped Block always fills the slot completely. */
	private void add(Block<? extends Shape> content){
		if(content.getType() == this.getType()){
			this.mContent = content;
			this.addView(content); 			//add the content to the ViewGroup hierarchy, so that content's onMeasure, onLayout, onDraw methods are called
			this.setMode(SlotMode.FILLED);

		}else{
			throw new UnsupportedOperationException("Can not add block of type "+content.getType()+" to Slot of type "+this.getType());
		}
	}

	/** Method to switch Content visibility inside of the Slot	 */
	public void setContentVisible(boolean b){
		if(this.mContent != null){this.mContent.setVisible(false);}
	}
	
	/** Empty the slot, e.g. if a block was dragged away from slot. */
	public void clear(){
		//delete the content. Do not do removeAllViews() ot the dummy will be deleted too.
		this.removeView(this.mContent);
		this.mContent = null;
		this.setMode(SlotMode.EMPTY);
	}

	
//HANDLING DROP   
	//  this methods name should be "onDrop" since slots receive draggables, can't be dragged them selfs  :)
	/*
	 * ATTENTION! ATTENTION! ATTENTION! ATTENTION! ATTENTION! 
	 * Returning false from onDrag on ACTION_DRAG_STARTED does not cut the further drag actions off.
	 * The View will still receive the ACTION_DRAG_ENTERED, ACTION_DRAG_EXITED actions.
	 */
	@Override
	public boolean onDrag(View v, DragEvent event) {
		boolean result = false; // on default do not handle any drop events
				
		//filter out everything, what is not a block
		if(!(v instanceof Block<?> )){
			return false;
		}
		if(  DragHandler.isDragging() &&											//actions ACTION_DRAG_STARTED,ACTION_DRAG_EXITED, ACTION_DRAG_ENTERED require that drop has not happened yet, since they use getBlockDragging()  -  ACTION_DRAG_ENDED does not, so it is handled separately.
			(this.getNextBodyBlock() != DragHandler.getBlockDragging()) &&			//currently dragged block's slots should not react on drag
			(this.getType() == DragHandler.getBlockDragging().getType())  	){ 		//slot only accepts a block, if block's type match slot's type
			
			switch(event.getAction()){
			case DragEvent.ACTION_DRAG_STARTED :
				
					//slot only accepts a block if the Slot is empty
					if( this.mContent == null ){
						
						this.setMode(SlotMode.ACTIVE);
						result=true; //returning true signalizes to the system : "we will handle the events", means this object will get future drag action if this events
						break;
					}
					
			
			case DragEvent.ACTION_DRAG_EXITED :
					
					if( this.mContent == null ){
						this.setMode(SlotMode.ACTIVE);
						result=true;
						break;
					}
			
				
			case DragEvent.ACTION_DRAG_ENTERED :
					
					if (this.mContent == null){
						this.setMode(SlotMode.HOVER);
						
						result=true;
						break;
					}

				
			case DragEvent.ACTION_DRAG_LOCATION :
					break; //no reaction
				
			case DragEvent.ACTION_DROP :
				//here we define that a slot can only receive something, if there it has no content in it yet. To change this behavior - override onDrag 
					if (this.mContent == null){
						DragHandler.executeDropTo(this, 0, 0);
						result=true;
						break;
					}
				
			default:
					Log.d("MyActivity", "Action not catched: "+ event.getAction());
					break;
		}
	} // end of check, whether we are dragging the right block now
		
		
	// handling ACTION_DRAG_ENDED since it does not require that something is currently dragged now
	if(event.getAction() == DragEvent.ACTION_DRAG_ENDED){
		Log.d("dragStartStop","slot says: ACTION_DRAG_ENDED, result: "+event.getResult());
		if(this.mContent!=null){
			this.setMode(SlotMode.FILLED); }
		else{
			this.setMode(SlotMode.EMPTY); } 
		result=true;
	}
	
	this.invalidate();
		
	return result; 
}
	
	
//DRAWING METHODS
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
		Log.d("slot","onMeasure");
		
		//DO not call super.onMeasure, since Block normally measures it's size by measuring it's Shape {@link Block#img} and Slots do not have own Shapes.
		
		//passing the measurement event
		Block<?> infill = this.getInfill();
		infill.measure(widthMeasureSpec, heightMeasureSpec);
		
		//update scaled and unscaled size variables
		this.setMeasuredDimensionsPublic(infill.getMeasuredWidth(),infill.getMeasuredHeight(), Math.round(infill.getUnscaledWidth()), Math.round(infill.getUnscaledHeight()) );
	}
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Log.d("slot","--- onLayout");
		
//		//update the position of the Slot
//		this.setPosition(left, top);
		
		//implement passing the layout event
		Block<?> infill = this.getInfill();
		infill.onLayout(changed,left, top, right, bottom);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("slot","onDraw");
		
		//implement passing the layout event
		Block<?> infill = this.getInfill();
		infill.draw(canvas);
	}
	@Override
	public void invalidate() {
		//remove the cached shape
		if(img!=null)img.clearCache();
		
		Log.d("invalidate","SLOT: Invalidate called.");
		super.invalidate();
	}

	
//TOUCH 
	//redirect the touch events to the infill
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return this.getInfill().onTouchEvent(event);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d("twidget","Slot dispatchKeyEvent");
		return super.dispatchKeyEvent(event);
	}
	
//OTHER
	/** uses the hook {@link #initiateBlockSlotDummy()} and inserts the BlockSlotDummy into the Block-View Tree  */
	private void initiateBlockSlotDummy_addToView(){
		this.blockSlotDummy = initiateBlockSlotDummy(); // remember the SlotDummy chosen by user
		
		/*
		 * in some blocks, e.g. Labels there is no blockSlotDummy because those Slots are always filled,
		 * so initiateBlockSlotDummy() returned null we do not need to add it to the hierarchy  
		 */
		if(this.blockSlotDummy!=null){
			this.addView(this.blockSlotDummy); 				// add the SlotDummy to the View hierarchy
		}
	}
	
	
//HOOKS
	/** Choose which Type of blocks to accept in the current slot */
	public abstract BlockType getType();
	
	/** here the {@link #blockSlotDummy} should be instantiated in each slot */
	protected abstract T initiateBlockSlotDummy();

}

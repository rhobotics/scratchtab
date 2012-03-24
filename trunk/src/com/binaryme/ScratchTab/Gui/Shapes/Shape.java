package com.binaryme.ScratchTab.Gui.Shapes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.tools.M;


/**
 *  Shape represents the visual component of TabScratche's programming blocks.
 *  Uses two caches:
 *  <ol>
 *   <li>one for the unscaled {@link #cachedPath} - to have the possibility of scaling the shape to the right size, just by changing this caches size
 *   <li>one for the scaled {@link #scaledCachedPath} - to have the possibility of redrawing a shape, without calculating operations, to adopt shape to the current scale
 *  </ol> 
 *	Shape size management: 
 *		onScaleEvent()		- invalidates the cached Path, after the scale level has changed. 
 *		onMeasure()   		- measures the Path, for later drawing
 *		draw()				- draws the path on the canvas
 *
 *		clearCache()		- call this method, if the proportions of the Shape change (not on Scale), e.g. if a child block was resized.
 */
public abstract class Shape implements ScaleEventListener{
	
	
	public int unscaledCompleteWidth;			//these dimensions should wrap the whole associated BLOCK, incl. all the children which may exceed the Shape size   
	public int unscaledCompleteHeight; 
	protected int unscaledWidthInSlot;			//this dimensions should define the size of the associated BLOCK, occupied in a command slot. (reduce unscaledCompleteWidth by the obstacles at the bottom)
	protected int unscaledHeightInSlot;
	
	public float unscaledDrawableShapeBoundWidth;			//this dimensions should wrap this Shape only, without any children. Used to check, whether a touch event occured within the Shape or not, to skip events outside the shape.
	public float unscaledDrawableShapeBoundHeight;
	
	//drawing tools
	protected Path  cachedPath;
	protected Path  scaledCachedPath;
	
	// Region is an Android object, which has a "contains(x,y)" method. Keeping this object in cache enables checking whether a touch occurred within a given path.
	protected Region scaledCachedRegion;

	
	
//ALL COLORS, WHICH ARE USED TO DRAW THE SHAPES ARE DEFINED HERE.

	//if the Color wants to change it's color - it should change colorCurrentFill, colorCurrentStroke
	protected Paint colorCurrentStrokeColor = new Paint();
	protected Paint colorCurrentFillColor	= new Paint();
	
	//all shapes know the color of the next body, which is the color of itself if it represents the body, or the color of the next parent body
	protected int colorOfNextBodyStroke;
	protected int colorOfNextBodyFill;
	
	protected int MeasuredScaledHeight;
	protected int MeasuredScaledWidth;
	
	private Activity mContext;
	private Block<? extends Shape> mBlock;
	
	
	/**
	 * DRAWING CONSTANTS, DEFINED IN THE SKETCH, SEE {@link #}} //TODO:link_the_png 	
	 */
	//inner child
	protected final int minInnerChildHeight=M.mm2px(5);

	//block
	protected final int  blockBackWidth=M.mm2px(10);
	protected final int  blockBottomHeight=M.mm2px(5);
	protected final int  minBlockTopHeight=M.mm2px(10);
	protected int blockTopHeight=0;
	
	//blockslot
	protected final int  blockSlotHeight=M.mm2px(2);
	protected final int  blockSlotWidth=M.mm2px(5);
	
	//label
	public int labelWidth=0;	//will be set externally, depending on the concrete label
	public int labelHeight=0;	//will be set externally, depending on the concrete label
	public static final int  minLabelHeight=M.mm2px(10);
	public static final int  minLabelWidth=M.mm2px(25);
	protected final int  labelMargin = M.mm2px(2);
	
	//boolean Slot
	protected final int booleanSlotHalfHeight 	= M.mm2px(4);
	protected final int booleanSlotPike   		= M.mm2px(5);
	protected final int booleanSlotMinWidth  	= M.mm2px(15);
	
	//head
	protected int headHeight 				= M.mm2px(7);
	
	
	public Shape(Activity context, Block<? extends Shape> associatedBlock){
			//save the shape. needed for creation of Slots in the SLotManager
			this.mContext = context;
			this.mBlock = associatedBlock;
		
			//register for the Scale Events
			ScaleHandler.addScaleEventListener(this);
			
			//init drawing colors
			this.colorCurrentStrokeColor.setStyle(Paint.Style.STROKE);
			this.colorCurrentFillColor.setStyle(Paint.Style.FILL);
			//use color initiation hook
			this.initFillColor(colorCurrentFillColor); 
			this.initStrokeColor(colorCurrentStrokeColor);				
	}

	
	
//SETTER AND GETTER
	/**
	 * Sets an argb color packed as int.
	 * @param bgcolorARGB
	 */
	public void setCurrentFillColor(int fillColor){
		this.colorCurrentFillColor.setColor(fillColor);
	}
	public int getCurrentFillColor(){
		return this.colorCurrentFillColor.getColor();
	}
	public void setCurrentStrokeColor(int strokeColor){
		this.colorCurrentStrokeColor.setColor(strokeColor);
	}
	
	public void setColorOfNextBodyFill(int color ){
		this.colorOfNextBodyFill=color;
	}
	public void setColorOfNextBodyStroke(int color){
		this.colorOfNextBodyStroke= color;
	}
	public void setContext(Activity c){
		this.mContext = c;
	}
	public Activity getContext(){
		return this.mContext;
	}
	public Activity getContextActivity(){
		return this.mContext;
	}
	public void setAssociatedBlock(Block<? extends Shape> b){
		this.mBlock = b;
	}
	public Block<? extends Shape> getAssociatedBlock(){
		return this.mBlock;
	}
	
	
	/** 
	 * Returns the <b>UNSCALED</b> Path if it already exists(is cached) or creates a new one using the current Data in this class.
	 * Updates the cache, if necessary. 
	 * Generating of shape's path happens here. 
	 * The drawing framework should be used here. The drawing framework is described in the documentation \\TODO:link_and_add_picture_from_illustrator.
	 * 
	 */
	private Path getCachedPathOrCreateCachedPath(){
		if(this.cachedPath!=null){
			//cached path
			return this.cachedPath;
		}
		
		//UPDATE THE LOCAL PATH FIELD BECAUSE IT WAS NOT CACHED YET
		this.cachedPath = this.drawPath();
		//the complete Shape Size
		RectF bounds = new RectF();
		this.cachedPath.computeBounds(bounds, true);
		
		//try retrieving the data from the size calculation hook
		ShapeDimensions dimensions = new ShapeDimensions(bounds.width(), bounds.height());

		//update the shape dimensions
		this.unscaledDrawableShapeBoundHeight = bounds.height();
		this.unscaledDrawableShapeBoundWidth = bounds.width();
		
		//use the calculated values from the hook
		dimensions.unscaledCompleteWidth =  unscaledCompleteWidth;
		dimensions.unscaledCompleteWidth =  unscaledCompleteWidth;
		dimensions.unscaledCompleteHeight = unscaledCompleteHeight;
		
		boolean isImplemented = calculateBlockSizeHook(dimensions); //will change dimensions, if "isImplemented" is true
		if(isImplemented){
			//use the calculated values from the hook
			this.unscaledCompleteWidth=	dimensions.unscaledCompleteWidth;
			this.unscaledCompleteHeight=	dimensions.unscaledCompleteHeight;
			
			//on default the Shape requires it's complete as much space in slots, as measured 
			this.unscaledWidthInSlot= 	dimensions.unscaledWidthInSlot;
			this.unscaledHeightInSlot=	dimensions.unscaledHeightInSlot;
			
		}else{
			
			//these values should wrap the whole Shape.
			this.unscaledCompleteWidth=	Math.round( bounds.width() );
			this.unscaledCompleteHeight=	Math.round( bounds.height() );
			
			//on default the Shape requires it's complete as much space in slots, as measured 
			this.unscaledWidthInSlot= 	Math.round( bounds.width() );
			this.unscaledHeightInSlot=	Math.round( bounds.height() );
		}
		
		return this.cachedPath;
	}
	
	/** Call to update the Height and Width of the Shape. All parameter on which this class depends like labelHeight or innerChildHeight should be set at this time. */
	public int getMeasuredHeight(){ return this.MeasuredScaledHeight; }
	public int getMeasuredWidth(){ return this.MeasuredScaledWidth; }
	
	protected void setMeasuredHeight(int measuredHeight) {
		MeasuredScaledHeight = measuredHeight;
	}
	protected void setMeasuredWidth(int measuredWidth) {
		MeasuredScaledWidth = measuredWidth;
	}

	
	
	
//DRAWING METHODS - are triggered by the block hierarchy: 1) onMeasure-measure slots first, 2)onLayout-position slots here 3)draw
	
	/**
	 * This method has to be called before drawing happens (as usual in Android)
	 * Method generates the Path(or takes the cache), 
	 * scales the path to the necessary amount,
	 * measures the bounds.
	 */
	public void onMeasure() {
		
		Log.d("onmeasure","Measuring "+this.getClass().toString());

		/* ShapesWithSlots need additional Information about their slots, to measure themselves.
		 * This information can be retrieved by the special hook: ShapesWithSlots.extractUNSCALEDdataFromSlotManager.
		 * ShapeWithSlots should override the onMeasure() method and call ShapesWithSlots.extractUNSCALEDdataFromSlotManager()
		 * before calling the current version of onMeasure() by using super.onMeasure() */
		
		//if the cache of this Shape is empty (was cleared?) generate it
		if((this.scaledCachedPath==null) || (this.cachedPath==null)){
			//Implicitly generated the path for this shape if necessary.
			// generating path is OK here, because the children should already be measured, because the Block.onMeasure first measures all Slots from SlotManager
			// for generating the path, the slot objects to not have to contain it's positions. The slots will be positioned in onLayout, in ShapeWithSots 
			Path p = this.getCachedPathOrCreateCachedPath();
			updateMyScaledCachedPath(p, ScaleHandler.getScale()); 
		}
		
		//the size is calculated in getCachedPathOrCreateCachedPath and saved into the size variables. use them to measure
		this.setMeasuredHeight( Math.round(
					ScaleHandler.scale(this.unscaledCompleteHeight)) 
					); 
		this.setMeasuredWidth( Math.round(
				ScaleHandler.scale(this.unscaledCompleteWidth)) 
		); 

	}
	/** Helper Class, used inside of {@link Shape#calculateBlockSizeHook() to save the calculated data.} */
	public class ShapeDimensions{
		private int unscaledShapeBoundsWidth;
		private int unscaledShapeBoundsHeight;
		
		public int unscaledCompleteWidth;
		public int unscaledCompleteHeight;
		public int unscaledWidthInSlot;
		public int unscaledHeightInSlot;
		
		public ShapeDimensions(int unscaledShapeBoundsWidth, int unscaledShapeBoundsHeight){
			this.unscaledShapeBoundsWidth=unscaledShapeBoundsWidth; 
			this.unscaledShapeBoundsHeight=unscaledShapeBoundsHeight; 
		}
		public ShapeDimensions(float unscaledShapeBoundsWidth, float unscaledShapeBoundsHeight){
			this.unscaledShapeBoundsWidth=Math.round(unscaledShapeBoundsWidth); 
			this.unscaledShapeBoundsHeight=Math.round(unscaledShapeBoundsHeight); 
		}
		public int getUnscaledShapeBoundsWidth(){return this.unscaledShapeBoundsWidth;}
		public int getUnscaledShapeBoundsHeight(){return this.unscaledShapeBoundsHeight;}
	}
	
	
	public boolean cacheExists(){
		//the cache is empty, the Shape Stack should be measured again
		if(this.cachedPath != null){ 
			return true;
		}
		return false; 
	}
	
	/**
	 * This method has to be called after onMeasure (as usual in Android)
	 * Method does the positioning of Slots or other actions, 
	 * which depend on the knowledge about the sizes of Slots, inside of the current Shape. 
	 * Sizes of Slots are only available after onMeasure was done for the whole Shape-tree. 	 */
	public void onLayout() {
		// ShapeWithSlots will override this to behave as following:
		// 1. reposition the Slots
	}
	
	/** The Block class will draw a shape by using this method. 
	 *  CONTRACT: before drawing this shape
	 *  <ul> 
	 *  <li> onMeasure() should be called, to make sure, that the shape and it's children are measured correctly. The children, e.g. slots are measured first.
	 *  <li> onLayout() should be called, to take advantage of infos, which were collected during the onMeasure pass. OnLayout is the right place to position the slots. */
	public void draw(Canvas canvas) {
//		canvas.drawColor(Color.GRAY);
		
		//Should already exist, after measurement. but if it doesn't - generate it and adjust to the scale level
		if(this.scaledCachedPath == null){
			updateMyScaledCachedPath(getCachedPathOrCreateCachedPath(), ScaleHandler.getScale());
		}
		
		//getting the SCALED path from cache. 
		Path path = this.scaledCachedPath; 
		
		canvas.drawPath(path, this.colorCurrentFillColor);
		canvas.drawPath(path, this.colorCurrentStrokeColor);
	}
	
	
	//DRAWING FRAMEWORK
			/** A part of the drawing framework. Draws a Block connector Slot on the given Path. The Data are taken from {@link #blockSlotWidth} and {@link #blockSlotHeight}. If you are drawing the path from left to right - pass DIRECTION.RIGHT, otherwise DIRECTION.LEFT */
			protected Path drawNotch(Path path, DIRECTION dir){
				path.rLineTo(0, blockSlotHeight);
				if(dir == DIRECTION.RIGHT){	
					path.rLineTo(blockSlotWidth, 0); }
				else if(dir == DIRECTION.LEFT){
					path.rLineTo(-blockSlotWidth, 0); }
				path.rLineTo(0, -blockSlotHeight);
				return path;
			}
			public enum DIRECTION{	LEFT,RIGHT;	}
			
			/** A part of the drawing framework. Draws a BlockHead. 
			 *	Depends on local fields {@link #minLabelWidth} {@link #labelWidth} {@link #blockSlotWidth} {@link #blockTopHeight} {@link #blockBackWidth}
			 */
			protected Path drawBlockHead(Path path, int labelWidth, int labelHeight){
				int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth;
				//top upper Block Site
				path.rLineTo((blockBackWidth-blockSlotWidth), 0);
				drawNotch(path, DIRECTION.RIGHT);
				path.rLineTo(blockProtrusion, 0 );
				//top right Block Site
				path.rLineTo(0, Math.max(minLabelHeight,(labelHeight+2*blockSlotHeight)) );
				//top lower Block Site
				path.rLineTo(-(blockProtrusion-blockBackWidth), 0 );
				drawNotch(path, DIRECTION.LEFT);
				path.rLineTo( -( blockBackWidth-blockSlotWidth ), 0);
				return path;
			}
			/** A part of the drawing framework. Draws a BlockMiddle. 
			 *  Depends on local fields {@link #minLabelWidth} {@link #labelWidth} {@link #blockSlotWidth} {@link #blockBottomHeight} {@link #blockBackWidth}
			 */
			protected Path drawBlockMiddle(Path path, int blockheight){
				//this variable contains the length, by which block exceeds its blockBack 
				int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth;
				//block protrusion to the right
				path.rLineTo(blockProtrusion, 0);
				//block height
				path.rLineTo(0, blockheight);
				//blockProtrusion-blockBack to the left
				path.rLineTo(-(blockProtrusion-blockBackWidth), 0);
				//draw the Slot
				drawNotch(path, DIRECTION.LEFT);
				//draw the line as long as the rest of the blockBack
				path.rLineTo( -(blockBackWidth-blockSlotWidth), 0);
				return path;
			}
			/** A part of the drawing framework. Draws a BlockBottomWithSlot. 
			 * 	Depends on local fields {@link #minLabelWidth} {@link #labelWidth} {@link #blockSlotWidth} {@link #blockBottomHeight} {@link #blockBackWidth}
			 */
			protected Path drawBlockBottomWithSlot(Path path){
				int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth;
				//draw the top upper horizontal line 
				path.rLineTo(blockProtrusion, 0);
				//draw the right vertical line
				path.rLineTo(0, blockBottomHeight);
				//draw the lower horizontal line till slot
				path.rLineTo(-blockProtrusion, 0);
				//draw the slot
				drawNotch(path, DIRECTION.LEFT);
				//draw the rest
				path.rLineTo(-(blockBackWidth-blockSlotWidth), 0);
				return path;
			}
			/** A part of the drawing framework. Draws a BlockBottom. 
			 *  Depends on local fields {@link #minLabelWidth} {@link #labelWidth} {@link #blockBottomHeight} {@link #blockBackWidth}
			 */
			protected Path drawBlockBottom(Path path){
				int blockProtrusion= Math.max(minLabelWidth, labelWidth)-blockSlotWidth;
				path.rLineTo(blockProtrusion, 0);
				path.rLineTo(0, blockBottomHeight);
				path.rLineTo(-(blockProtrusion+blockBackWidth) ,0 );
				return path;
			}
	
	
//OTHER
  
	/**
	 * Method adopts recreates the scaled path by using the scale level and the cached basic path taken from {@link #cachedPath}.
	 */
	@Override
	public void onScaleEvent(float newscale, Point pivot) {
		// do not change the path or colour. Just scale the available cached path.		
		updateMyScaledCachedPath(this.getCachedPathOrCreateCachedPath(), newscale);
	}
	
	/** A helper method for scaling the path */
	private void updateMyScaledCachedPath(Path path, float newscale){
		Matrix m = new Matrix();
		m.setScale(newscale, newscale);
		this.scaledCachedPath= new Path(path);
		//scale the path
		this.scaledCachedPath.transform(m);
		
		//now save the region
		RectF rectF = new RectF();
		scaledCachedPath.computeBounds(rectF, false);
		this.scaledCachedRegion = new Region();
		this.scaledCachedRegion.setPath(scaledCachedPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
	}
	
	/** The current shape is not up to date. Maybe the child of the block has grown and changed this shape?	 */
	public void clearCache(){
		this.cachedPath = null;
		this.scaledCachedPath = null;
		this.scaledCachedRegion = null;
	}
	
	/** Checks, whether a point is located within of the current shapes outline */
	public boolean contains(int x, int y){
		boolean result = false;
		if(this.scaledCachedRegion != null){
			result = this.scaledCachedRegion.contains(x, y);
		}
		return result;
	}
	
	
//HOOKS - OVERRIDE THE FOLLOWING METHODS TO IMPLEMENT CONCRETE SHAPES

	
	/**
	 * <p>
	 * This hook is needed to calculate the associated block's size, if the size differs from the shape's measured bounds. 
	 * Return true, if you wish to calculate the size of the associated block in a special manner, e.g. if you know that children in the slots of the current shape will become bigger than this shape. 
	 * If you return false, the default implementation will calculate the Block's dimensions as equal to the Shapes bounds.
	 * 
	 * If you use this hook you have the chance to calculate two different block's dimensions
	 * <ol>
	 * 	<li> {@link ShapeDimensions#unscaledCompleteHeight}, {@link ShapeDimensions#unscaledCompleteWidth} - which should at least wrap the whole Shape - or not the entie Shape will be visible,
	 * 			but it can exceed the Shape dimensions too.</li>
	 *  <li> {@link ShapeDimensions#unscaledWidthInSlot}, {@link ShapeDimensions#unscaledHeightInSlot} - which will be used, when the block is located in a slot. This dimensions are introduced, to provide the possibility of ignoring some obstacles at block's bottom, 
	 *  		which still should be considered in the  {@link ShapeDimensions#unscaledCompleteHeight}, {@link ShapeDimensions#unscaledCompleteWidth} - or they will be cut off when drawing.</li>   
	 * </p>
	 * 
	 * <br>
	 * <p>
	 * <b>If implemented, this method should:</b> 
	 * <ol>
	 * <li>Somehow calculate the size for the associated block.</li> 
	 * <ul>
	 *    <li> you can use the Shape bounds, which are available in passed {@link ShapeDimensions#getUnscaledShapeBoundsHeight()}, {@link ShapeDimensions#getUnscaledShapeBoundsWidth()}
	 *    <li> current Shape should have been measured either, use it's size like <b>this.getMeasuredWidth(), this.getMeasuredHeight()</b>
	 * </ul>
	 * <li>Save the calculated dimensions back to the {@link ShapeDimensions} object passed in the parameters.
	 * <li>return true</li>
	 * </ol>
	 * 
	 * </p>
	 * 
	 * @return true if this method is implemented and will calculate the block size. The size won't be calculated elsewhere, if this method returns true. 
	 */
	public abstract boolean calculateBlockSizeHook(ShapeDimensions shapeDimensions);
	
	/** The hook for concrete shapes to implement the drawing of the concrete block's shape. You can use the drawing Framework provided by the Shape class.
	 *  Drawing Framework contains following methods:
	 *  <ul>
	 *		<li>{@link #drawBlockHead(Path)}</li>
	 *		<li>{@link #drawBlockMiddle(Path)}</li>
	 *		<li>{@link #drawBlockBottomWithSlot(Path)}</li>
	 *		<li>{@link #drawBlockBottom(Path)}</li>
	 *		<li>{@link #drawNotch(Path, DIRECTION)}</li>
	 *  </ul>
	 */
	public abstract Path drawPath();
		
	
	/** When creating a new Shape - return a field from {@link Block.BlockType}, which corresponds to the type of this Shape */
	public abstract BlockType getType();
	
	/** configure the stroke Color. Here the stroke Color should be initiated with the default color for the current shape, width of the stroke etc. should be set  */
	protected abstract void initStrokeColor(Paint strokeColor);
	
	/** configure the fill Color. Here the stroke Color should be initiated with the default color for the current shape, width of the stroke etc. should be set   */
	protected abstract void initFillColor(Paint fillColor);


}

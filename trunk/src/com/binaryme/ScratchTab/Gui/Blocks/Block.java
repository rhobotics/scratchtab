package com.binaryme.ScratchTab.Gui.Blocks;

import java.util.Stack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;

import com.binaryme.DragDrop.DragStateData;
import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.StartToken;
import com.binaryme.ScratchTab.Gui.WorkspacePanel;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.tools.M;


/**
 * Represents a Block. Its the basic GUI unit in this framework. All draggable and droppable blocks, labels and slots inside of Blocks do inherit from Block.
 * <ul>
 * 	<li><b>On default Block manages it's size by looking at the Shape</b> {@link #img}. Shapes are aware of scale level, so looking at Shapes size will consider the scale level. Use {@link #initPosition(int, int)} to initiate Block's position and {@link #updatePosition(int, int)} to change the position after initialisation.</li>
 * 	<li><b>Block can manage it's size on its own too</b> by overriding {@link #calculateBlockSizeHook()}. Use {@link #initDimensionAndPosition(int, int, int, int)} to initiate Block's position and {@link #updatePosition(int, int)} to change the position after initialisation. {@link #getType()} should be overridden, if the </li>
 * </ul>
 * Block's size in determined by default by measuring {@link #img}. If you do not change the measuring mechanism by overriding {@link #calculateBlockSizeHook} - 
 * 
 * Should be used as following:
 * 		1. extend the Block class
 * 		2. implement the Hooks 
 * 		3. Override Drawing Methods for drawing the shadow and for drawing the block. Do not forget to look at the RedFrame boolean, whiteFrame boolean and draw the Frame accordingly to these Variables.
 * 		
 * 
 *  <p>
 *  <h1>REMARKS:</h1>
 *  <ul> 
 *  <li>Use AbsoluteLayout.LayoutParams if your use setLayoutParameter, not the ViewGroup.LayoutParams because it doesn't contain x,y coordinates</li>
 *  <li>Add children of type Block only - to ensure that children are layed out by an AbsoluteLayout</li>
 *  <li>Do not use the {@link #getHeight()}, {@link #getWidth()} methods - they does not always return a value, which is up to date, especially they are not immediately up to date after measuring the View tree with {@link #onMeasure(int, int)}. Use the methods {@link #getUnscaledHeight()}, {@link #getUnscaledWidth()} and {@link #getMeasuredHeight()}, {@link #getMeasuredWidth()} instead</li>
 *  </ul>
 *  </p>
 * @author Alexander Friesen
 *
 */
@SuppressWarnings("deprecation")
public abstract class Block<T extends Shape> extends AbsoluteLayout implements ScaleEventListener, OnDragListener {
	
	//this flag is set after each drop event. It is true, when the current block's parent is a workspace.  
	private boolean isRoot = false;
	/* this cache is updated by root blocks only, when a root block is drawn.
	 * It is used to draw the stack on scroll to increase the performance.	 */
	protected Bitmap bitmapImgCache; //TODO check, whether it increases the performance
	
	
	private Activity mContextActivity;
	
	public boolean DRAWSHADOW = true;
	public boolean DRAWFRAME = false;

	//if blocks position will be adjusted to the scale level externally, e.g. in a label to respect the  
//	public boolean ADJUST_POSITION_ON_SCALE = true; 
	
	protected T img; //class encapsulates the Block Picture
	
	private float mUnscaledWidth; 		//mUnscaledWidth, mUnscaledHeight save the dimension of this Block, without scaling.
	private float mUnscaledHeight; 
	private float mScaledWidth;	//mScaledWidth, mScaledHeight save the dimension of this Block with respect to the scale level 
	private float mScaledHeight;
	private int mUnscaledX; 
	private int mUnscaledY;
	private int mScaledX;
	private int mScaledY;
	
	private int id;
	
	// Pointer to the Block, above this in the Tree hierarchy, which represents a programming block body. Pointer is updated for the whole child tree, when child is added to a block by addView()
	private DraggableBlockWithSlots nextBodyBlock;  //#CACHE
	
		public Block(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle); 	//super() calls requestLayout()
			init(context);
		}
		public Block(Activity context, AttributeSet attrs) {
			super(context, attrs); 				//super() calls requestLayout()
			init(context);
		}
		public Block(Activity context) {
			super(context);						//super() calls requestLayout()
			init(context);
		}
		
		private void init(Activity context){
			this.mContextActivity=context;
			
			ScaleHandler.addScaleEventListener(this);
			
			this.img = initiateShapeHere(); //use the hook to set the shape for the current block.			
			initDimensionAndPosition(0,0,0,0); //initiate Android's LayoutParams etc. They can be set by using setDimensions(), setPosition()
			
			//make every GUI detail in ScratchTab focusable, so that focus can on textFields can be disabled by touching something else.
			this.setFocusable(true);
			this.setFocusableInTouchMode(true);
			
			//listeners
			 /**
			  *  Block.class implements the OnDrag, because it has to handle the drop of StartTokens, to execute commands in the stack. 
			  *  Dtag handling is implemented on Block.class level, because it is the class from which all the other classses in this app inherit,
			  *  so all the classes will be capable to receive the drop and pass it to the top most executable container.
			  */
			 this.setOnDragListener(this); //
			 
			 //Id
			 this.id = M.getuniqueId();
			 this.setId(id);
		}
		
		
//GETTER AND SETTER
		public T getShape(){ 
			return this.img;
		}
		protected void setShape(T s){
			this.img = s;
		}

		/** On default the type will be red from the shape */
		public BlockType getType(){
			if(this.img != null){
				return this.img.getType();
			}
			return null;
		}
		public void setVisible(boolean visible) {
			if(visible){
				this.setVisibility(VISIBLE);
			}else{
				this.setVisibility(GONE);
			}
		}
		public boolean isVisible() {
			if(this.getVisibility() == VISIBLE){
				return true;
			}else{
				return false;
			}
		}
		public boolean isRoot()  {		return isRoot;		}
		public void setRoot(boolean isroot) {
			this.isRoot=isroot;		
		}
		
		//use unscaled size for drawing calculations to reduce rounding errors
		public float getUnscaledWidth(){
			return this.mUnscaledWidth;
		}
		public float getUnscaledHeight(){
			return this.mUnscaledHeight;
		}
		
		/** we know, that instances of {@link DraggableBlockWithSlots} represent the bodies, so we search for the first occurrence. Use the cached value {@link #nextBodyBlock} if it exists  */
		public DraggableBlockWithSlots getNextBodyBlock(){
			if(this.nextBodyBlock != null){
				return this.nextBodyBlock;
			}
			this.nextBodyBlock = rekFindNextBodyBlock(this);
			return this.nextBodyBlock;
		}
		protected DraggableBlockWithSlots rekFindNextBodyBlock(Block<?> b){
			if(b==null) return null;
			if(b instanceof DraggableBlockWithSlots){
				return (DraggableBlockWithSlots) b;
			}
			return rekFindNextBodyBlock(b.getParentBlock());
		}
		
		@Override
		public void removeView(View view) {
			super.removeView(view);
			if(view instanceof Block){ 
				//on remove of a block from the structure - make the nextBodyPointer invalid
				((Block<?>)view).nextBodyBlock=null; }
		}
		@Override
		public void removeViewAt(int index) {
			super.removeViewAt(index);
			if(getChildAt(index) instanceof Block){ 
				//on remove of a block from the structure - make the nextBodyPointer invalid
				((Block<?>)getChildAt(index)).nextBodyBlock=null; }
		}
		
		
		/** Method to return the pointer to the parent in the Block hierarchy. If there is no Parent at all or it is not an instance of {@link Block} - returns null */
		public Block<?> getParentBlock(){
			ViewParent parent = this.getParent();
			if((parent!=null) && (this.getParent() instanceof Block<?>)){
				return (Block<?>) this.getParent();
			}
			return null;
		}
		/** this little method allows us to get the Activity (which inherits from Context, thats why getContext) from anywhere in this app.
		 *  The main activity is needed, to execute something on a UI thread */
		public Activity getContextActivity(){
			return this.mContextActivity;
		}
		

		
//SIZE AND POSITION CHANGING METHODS

		//By default onMeasure is implemented, that the Size of this Block will be equal to the size of this block's Shape
		/**
		 * This Method should be used, if the size of the Block is NOT determined automatically from {@link #img} in other words: use this method if you override onMeasure to use {@link #mScaledWidth} {@link #mScaledHeight} for {@link #setMeasuredDimension(int, int)} and want to set the initial Blocksize manually.
		 * CONTRACT: This method or {@link #initPosition(int, int)} has to be called before this Block will be Measured and made visible.
		 * Call to initially set the dimensions and the position of the Block for the first time. 
		 * Method is introduced instead of the LayoutParams object.
		 * @param unscaledWidth  - width in pixel, not scaled yet
		 * @param unscaledHeight - height in pixel, not scaled yet
		 * @param unscaledX      - x coordinate in pixel, not scaled yet
		 * @param unscaledY      - y coordinate in pixel, not scaled yet
		 */
		private void initDimensionAndPosition(float unscaledWidth, float unscaledHeight, int unscaledX, int unscaledY){
			
			mUnscaledWidth = unscaledWidth; 
			mUnscaledHeight = unscaledHeight; 
			mUnscaledX = unscaledX;
			mUnscaledY = unscaledY; 
			
			//adopt to scale
			mScaledWidth = ScaleHandler.scale(mUnscaledWidth); 
			mScaledHeight = ScaleHandler.scale(mUnscaledHeight);
			mScaledX = ScaleHandler.scale(mUnscaledX); 
			mScaledY = ScaleHandler.scale(mUnscaledY);
			
			
			AbsoluteLayout.LayoutParams mLayoutParams = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
			//save the Position into mUnscaledY LayoutParams
			mLayoutParams.width=Math.round(mScaledWidth); 
			mLayoutParams.height=Math.round(mScaledHeight);
			mLayoutParams.x=mScaledX; 
			mLayoutParams.y=mScaledY;
			super.setLayoutParams(mLayoutParams); //calling super method, so that no endless loop is created
		}
		
		
		/**
		 * Call when block changes its position, e.g. after a drag operation.
		 * Normally block's position is updated when workspace is scaled.
		 * @param scaledX - x coordinate, scaled
		 * @param scaledY - y coordinate, scaled
		 */
		public void setPosition(int scaledX,int scaledY){
			//new position
			mUnscaledX = ScaleHandler.unscale(scaledX);
			mUnscaledY = ScaleHandler.unscale(scaledY); 
			
			//new scaled position
			mScaledX = scaledX; 
			mScaledY = scaledY;

			//here the Android framework gets the position and size of the view from when drawing the view
			AbsoluteLayout.LayoutParams mLayoutParams = (AbsoluteLayout.LayoutParams) this.getLayoutParams();
			//save the Position into mUnscaledY LayoutParams
			mLayoutParams.width=Math.round(mScaledWidth); 
			mLayoutParams.height=Math.round(mScaledHeight);
			mLayoutParams.x=mScaledX; 
			mLayoutParams.y=mScaledY;
			super.setLayoutParams(mLayoutParams); //calling super method, so that no endless loop is created, because onLayout is overridden
			
			/* IMPORTANT - 
			 * setLayoutParams(AbsoluteLayout.LayoutParams) sometimes lets the View.mLeft, View.mRight variables unchanged,
			 * although those variables save the position of the Block. Set them explicitly. 
			 */
			this.setLeft(mScaledX);
			this.setTop(mScaledY);
		}
		
		/**
		 * Call when block changes it's size, e.g because of child drop.
		 * @param unscaledWidth -  the new Width of the Block, not scaled yet
		 * @param unscaledHeight - the new Height of the Block, not scaled yet
		 */
		private void setDimensions(float unscaledWidth,float unscaledHeight){
			//new dimension and Dimension
			mUnscaledWidth = unscaledWidth; 
			mUnscaledHeight = unscaledHeight; 

			//adopt to scale
			mScaledWidth = ScaleHandler.scale(mUnscaledWidth); 
			mScaledHeight = ScaleHandler.scale(mUnscaledHeight);

			//here the Android framework gets the position and size of the view from when drawing the view
			AbsoluteLayout.LayoutParams mLayoutParams = (LayoutParams) this.getLayoutParams();
			//save the Position into mUnscaledY LayoutParams
			mLayoutParams.width=Math.round(mScaledWidth); 
			mLayoutParams.height=Math.round(mScaledHeight);
			super.setLayoutParams(mLayoutParams); //calling super method, so that no endless loop is created
		}

		
		
//DRAWING
		/**
		 * Drawing of the Block is done here, with respect to the child Views.
		 * Drawing a block is a two pass process:
		 * Pass 1: first the shadows for all blocks are drawn recursively by calling onDrawShadow() for current block and all children,
		 * Pass 2: then the blocks themselves are drawn recursively by calling onDrawBlock() for current block and all children
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			
//			//TODO: artifact hunt
//			this.rekArtifactHunter(1);
			
			//PASS 1: draw the shadow for this block here
			// EDIT: das sollte nur einmal passieren, innerhalb des Root Blocks.
			// this.onDrawShadow(canvas);
			
//			//draw the shadow for the children
//	        final int size = this.getChildCount();
//	        for (int i = 0; i < size; ++i) {
//	        	//dangerous casting, but the Class is checked inside of addView()
//	            final Block child = (Block)this.getChildAt(i);
//	            if ((child.getVisibility() ) != GONE) {
//	                child.onDrawShadow(canvas);
//	            }
//	        }
			
	        
			//PASS 2: draw this block
//			if(this.isRoot){
//				this.bitmapImgCache = Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(), Config.ARGB_8888);
//				Canvas c = new Canvas(this.bitmapImgCache);
//				this.onDrawBlock(c);
//				
//				//draw the cache onto the view canvas now
//				canvas.drawBitmap(bitmapImgCache, getMatrix(), null);
//			}else{
				//draw the path directly to the view canvas
				this.onDrawBlock(canvas);
//			}
		}
		
		/** Method invalidates the current Block, and all it's children. Clears the cache of the child blocks. */
		public void invalidateTree(){
			//remove the cached shape
			if(img!=null)img.clearCache();
			//invalidate
			this.invalidate();
			int cnt=this.getChildCount();
			for(int i=0; i<cnt; i++){
				View child = this.getChildAt(i);
				if(child instanceof Block) ((Block<?>)child).invalidateTree();
				else child.invalidate();
			}
		}
		
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			//now, after measuring all slots is done, slots should contain valid size information, now we can draw and measure any shape
			this.img.onMeasure();
			
			setMeasuredDimensionsPublic( ScaleHandler.scale(this.img.unscaledCompleteWidth),ScaleHandler.scale(this.img.unscaledCompleteHeight), 
											 this.img.unscaledCompleteWidth, this.img.unscaledCompleteHeight );
			
		}
		
		
		/** Helper method for setting the measured dimensions of the block from outside of this class, 
		 *  e.g. for the shape when implementing the {@link Shape#calculateBlockSizeHook()}  
		 *  Here the scaled dimensions can be passed explicitly as parameter 3 and 4, maybe if they were computed using float values to reduce the rounding error.s */
		public void setMeasuredDimensionsPublic(int scaledMeasuredWidth, int scaledMeasuredHeight, int unscaledMeasuredWidth, int unscaledMeasuredHeight){
			//first use android's method to set frameworks width and height 
			this.setMeasuredDimension(scaledMeasuredWidth,scaledMeasuredHeight);
			
			//update block's inner size variables
			this.setDimensions(unscaledMeasuredWidth, unscaledMeasuredHeight);
		}
		
		/** Helper method for setting the measured dimensions of the block from outside of this class, 
		 *  e.g. for the shape when implementing the {@link Shape#calculateBlockSizeHook()}  
		 *  Here the scaled dimensions will be computed by using unscaled dimension parameters 1 and 2. */
		public void setMeasuredDimensionsPublic(int unscaledMeasuredWidth, int unscaledMeasuredHeight){
			//first use android's method to set frameworks width and height 
			this.setMeasuredDimension(	ScaleHandler.scale(unscaledMeasuredWidth),
										ScaleHandler.scale(unscaledMeasuredHeight));
			
			//update block's inner size variables
			this.setDimensions(unscaledMeasuredWidth, unscaledMeasuredHeight);
		}
		
		
		@Override
		public void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			
//				if(changed){
					
					//reposition Slots here by calling onLayout in shape
					if(this.img != null){
						//give the Shape a possibility to reposition itself, e.g. to reposition the Slots
						this.img.onLayout();
					}
					
					int uLeft =  this.mScaledX;
					int uTop =   this.mScaledY;
					int uRight =  uLeft+ Math.round(this.mScaledWidth);
					int uBottom = uTop+ Math.round(this.mScaledHeight);
					
					//Synchronise the local size variables with the LayoutParams
					AbsoluteLayout.LayoutParams lp = (LayoutParams) this.getLayoutParams();
					lp.x = uLeft;
					lp.y = uTop;
					this.setLayoutParams(lp);
					
					/* IMPORTANT - 
					 * setLayoutParams(AbsoluteLayout.LayoutParams) sometimes lets the View.mLeft, View.mRight variables unchanged,
					 * although those variables save the position of the Block. Set them explicitly. 
					 */
					this.setLeft(uLeft);
					this.setTop(uTop);
					this.setRight(uRight);
					this.setBottom(uBottom);
					
					//scaling should already be done in onScaleEvent() so that we can use mScaledX, mScaledWidth , mScaledY, mScaledHeight
					super.onLayout(true, uLeft, uTop, uRight, uBottom);
//				}
		}
		/** replaces requestLayout which does not allways work */
		public void onLayout(){ 
			onLayout(true, mScaledX, mScaledY, Math.round(mScaledX+mScaledWidth), Math.round(mScaledY+mScaledHeight) );
			//layout the children
			int cnt = this.getChildCount();
			for(int i=0; i<cnt; i++){
					View child = this.getChildAt(i);
					if(child instanceof Block){ ((Block<?>) child).onLayout(); }
					else{ child.requestLayout(); }
			}	
		}
		
		
		
		/** clears the cached path of the current Block */
		public void clearShapeCache(){
			if(this.img != null){
				this.img.clearCache();
			}
		}
		
		/**
		 * In AbsoluteLayout dispatchDraw() does't pass call onDraw. Here we first call onDraw and then draw the children by calling super.dispatchDraw.
		 */
		//working version without rootCache
		@Override
		protected void dispatchDraw(Canvas canvas) {
			this.onDraw(canvas);
			super.dispatchDraw(canvas);
		}
		
		
		/** Use the variable {@link #img} in here to draw the block. Can be overridden by children. */
		protected void onDrawBlock(Canvas canvas){
			this.img.draw(canvas);
		}
		

//REKURSIVE
		
		//REK CACHE CLEAR, ALONG THE PATH TO THE ROOT
		/** Shortcut to clear block tree from this block on */
		public void clearCacheForWholeStackAboveCurrentBlock(){ clearCacheForWholeStackAboveCurrentBlock(this);}
		
		/** If something changes inside the Programming-Block, which influences the whole Tree-Structure, 
		 * this method will make the Shapes of all Blocks, which are above the current block and so are maybe
		 * influenced by this change, to clear their cached pictures. 
		 * 
		 * Normally cache clearing is followed by recursive invalidating and redrawing of the blocks, with uncached shapes.
		 * For that method {@link #redrawUncachedStackFromRoot()} is used. */
		protected void clearCacheForWholeStackAboveCurrentBlock(ViewParent viewParent){

			//clearing the cache for the current level of recursion
			if(viewParent instanceof Block<?>){
				//Invalidate the shape in each block, 
				Shape s = ((Block)viewParent).getShape();
				if(s != null){s.clearCache();}
			}else{
				throw new NullPointerException("Left the Block hierarchy and did not found the Stack-Root when tried to perform invalidateStack() on a "+this.getClass());
			}

			//dig deeper, if necessary
			ViewParent parent = viewParent.getParent();
			if(parent instanceof WorkspacePanel) {
				//we have found the root - stop here
			}else{
				//another recursion loop
				clearCacheForWholeStackAboveCurrentBlock(parent);	//if we are still inside of the Block hierarchy and still did not find the root - search recursively
			}
		}
		
		//REK REDRAW UNCACHED BLOCKS, ALONG THE PATH TO THE ROOT
		/** Method redraws the cache, beginning from root block.
		 *  Method will be probably used as  */
		public void redrawUncachedStackFromRoot(){
			//find root Block
			Block<?> rootBlock = findRootBlock();
			//redraw all caches, beginning from the root
			
//			rootBlock.invalidate(); //it doesn't work - call the invalidation cycle manually, as below
			//Manually call the whole cycle - don't trust the automatics
			rootBlock.onMeasure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			rootBlock.onLayout(true, 0, 0, 0, 0);
			rootBlock.invalidate();
		}
		
		/** shortcut of findRootBlock(ViewParent) */
		public Block<?> findRootBlock(){	return findRootBlock(this, new Stack<Block<?>>()).pop();	}
		
		//REK FIND ROOT BLOCK
		/** searches for the first block , which is lying on the workspace and contains any other blocks.
		 *  returns a stack with all the blocks, which occured between this block and the root block. */
		private Stack<Block<?>> findRootBlock(ViewParent vp, Stack<Block<?>> stack){
			//push the root onto the stack
			stack.push((Block<?>)vp);
			
			ViewParent newParent = vp.getParent();
			
			if(newParent==null){
				return stack;
			}else if(newParent instanceof WorkspacePanel){
				//we have found the root, its vp because it's parent is a workspace
				//and return the stack
				return stack;
			}else{
				return findRootBlock(newParent, stack);
			}
		}
		
		//REK FIND ROOT EXECUTABLE
		private ExecutableDraggableBlockWithSlots<?,?> getTopMostExecutable(){
			//first get a stack with all blocks, between this block and the root (incl. the root itselfe)
			Stack<Block<?>> allBlocksInbetweenStack =  findRootBlock(this, new Stack<Block<?>>());

			//now check all blocks, and return the first executable, which is found
			ExecutableDraggableBlockWithSlots<?,?> result = null;
			while(!allBlocksInbetweenStack.empty()){
				Block<?> b = allBlocksInbetweenStack.pop();
				if(b instanceof ExecutableDraggableBlockWithSlots){
					result=(ExecutableDraggableBlockWithSlots<?,?>) b;
					break;
				}
			}
			return result;
		}
		
		
		
		
//DROP HANDLING
		/**
		  *  Block.class implements the OnDrag, because it has to handle the drop of StartTokens, to execute commands in the stack. 
		  *  User should be able to drop the StartToken on any part of the Interface, not only on an executable block. 
		  *  Drag handling is implemented on Block.class level, because it is the class from which all the other classses in this app inherit,
		  *  so all the classes will be capable to receive the drop and pass it to the top most executable container.
		*/
		@Override
		public boolean onDrag(View v, DragEvent event) {
			boolean result = false;

			//in this app we only pass DragStateData as LocalStateData, so we can cast it here
			DragStateData dd = (DragStateData) event.getLocalState();
			
			//implement on drag listener, so that if you drop myself to me - its like a click
			if(dd!=null && dd.classOfTheDraggable==StartToken.class){
				switch(event.getAction()){
				//yes, we want to receive the drag events
				case DragEvent.ACTION_DRAG_STARTED :
					result= true;
					break;
				
				//dropped a startToken into a scratchTab block	
				case DragEvent.ACTION_DROP :
					//find the top executable in this stack
					ExecutableDraggableBlockWithSlots<?,?> topExec = getTopMostExecutable();
					//create a new thread and run the recursive execution cascade by creating the Handler 
					new ExecutionHandler(topExec, this.mContextActivity); //here we don't bother about what the executable will return on execution, so we dont bother about the parameters of ExecutionHandler 
					result= true;
					break;
					
				case DragEvent.ACTION_DRAG_ENDED :
					result= true;
					break;
				}
			}
			return result;
		}
		
		
//OTHER
		
		/**
		 * Enum, to set the type of the slots and blocks which can be inserted into slots. 
		 * there will be no SLOTS of type head , because Head should always  be the root of the Stack.
		 */
		public static enum BlockType{HEAD, COMMAND, BOOLEAN, LABEL, DATA };
		
		
		/**
		 * Method responsible for adaptation to the new scale level,
		 * ONLY scaling is done here, neither the position changing, or proportions  
		 */
		@Override
		public void onScaleEvent(float newscale, Point pivot) {
			//adopt to scale
			this.mScaledX = ScaleHandler.scale(this.mUnscaledX);
			this.mScaledY = ScaleHandler.scale(this.mUnscaledY);
			this.mScaledWidth = ScaleHandler.scale(this.mUnscaledWidth);
			this.mScaledHeight = ScaleHandler.scale(this.mUnscaledHeight);
			
//			//DO NOT CALL requestLayout()! It makes the layout jumping, maybe because of the padding between the children. Call onLayout instead.
////			requestLayout();
			onLayout(true, mScaledX, mScaledY, Math.round(mScaledX+mScaledWidth), Math.round(mScaledY+mScaledHeight) );
		}
		
		@Override
		public void addView(View child) {
			super.addView(child);
			//the workspace sets the Block flag isRoot to true when executing the add() Method. Block will set this flag to false.
			
			//set the isRoot flag to false
			if(child instanceof Block){
				((Block)child).setRoot(false);
			}
		}
		
		
		
//HOOKS TO OVERRIDE

		
		/** Override this method to implement shadow drawing. This method will be called by the root block for all children. */
		protected void onDrawShadow(Canvas canvas){}
		
		/** In this method the descendants should initialise the {@link #img} with the right shape 
		 *  Can not automatically initialise, because there are descendants without own visual representations(Slots)*/
		protected abstract T initiateShapeHere();
		
		
		
//FOCUS
		//FLING-FOCUS PATCH: returning true here prevents the focus from jumping around between different text forms, when scrolling.
		@Override
		public View findFocus() {
			return this;
		}
}

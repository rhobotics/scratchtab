package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.binaryme.DragDrop.DragHandler;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.Blocks.DraggableBlockWithSlots;
import com.binaryme.tools.M;

public class BlockPaneImageView extends ImageView {
	
	BlockPane.BlockRepresentationInBlockpane mBlockRepresentationPointer;

	
	//MotionTouch ACTION_DOWN event data. Will be set on initial event to remember the inital event data
	private long mTouchInitialTime ;
	private float mTouchInitialX ;
	private float mTouchInitialY ;
	private boolean isPotentialDragGesture = false;
	
	
	public BlockPaneImageView(Context context) {
		super(context);
		init();
	}
	public BlockPaneImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public BlockPaneImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	void init(){

	}

	
	/** Method used by the ArrayAdapter in BlockPane, to set the pointers for each BlockRepresenation in BlockPane.
	 *  This pointer is set, when the adapter processes the BlockRepresentationInBlockpane in {@link ArrayAdapter#getView(int, View, android.view.ViewGroup)} so that they can be displayed in the ListView,   */
	public void setBlockRepresentationInBlockpane(BlockPane.BlockRepresentationInBlockpane blockrepresentation){
		this.mBlockRepresentationPointer = blockrepresentation;
	}
	/** This imageView encapsulates an Image of a BLock. This method returns a pointer to this Block's representation, 
	 *  so that the class of the block can be retrieved and a ne instance of the block created on doubleclick. */
	public BlockPane.BlockRepresentationInBlockpane getBlockRepresentationInBlockpane(){
		return this.mBlockRepresentationPointer;
	}
	
	
	//INSTANTIALIZATION AND DRAGSTART WHICH WILL OCCUR ON A PREDEFINED GESTURE
	public void startDraggingBlock(){
		//retrieve the name of teh matching block
		String classToInstantiate = this.getBlockRepresentationInBlockpane().getBlockRepresentationInBlockpaneClassPath();
		try {
			
//			 unchecked cast here, because it depends on the user of the ScratchTab framework, that only instances of  DraggableBlockWithSlots are put into the ArrayList inside of BlockList.getAllAvailableBlocksAsGroups()
			@SuppressWarnings("unchecked")
			Class<? extends DraggableBlockWithSlots> c = (Class<? extends DraggableBlockWithSlots>) Class.forName(classToInstantiate);
			DragHandler.startDrag(c, null, this);
			
		} catch (Exception e1) {
			AppRessources.popupHandler.popError("Error, while a new block was created in the Block Pane.");
			e1.printStackTrace();
		} 
	}
	
	
	/** The pass useful TouchEvent manually to the OnTouch Method */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		Log.d("pane","--ImageView dispatch get a touch event "+M.motionEventResolve(ev.getAction()) );
		boolean sup = super.dispatchTouchEvent(ev);
		boolean result = sup;
			
		//always pass the Touch event manually
		onTouchEvent(ev);
		
		return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = super.onTouchEvent(ev); 
		result = true;
		
		switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				mTouchInitialTime = ev.getDownTime();
				mTouchInitialX = ev.getX();
				mTouchInitialY = ev.getY();
				
				isPotentialDragGesture = true;
			break;
			
			case MotionEvent.ACTION_MOVE:
				
				/*
				 * DO THE CHECKS FOR THE DRAG GESTURE. START THE GESTURE IF:
				 *  - the  abs(delta y) is smaller than delta x
				 *  - the delta x is min. 70px (movement to the right is positive)
				 *  To sum up the drag event is triggered, when the user moves his finger to the right fast enought in the first 200 ms 
				 */
				
				int dragGestureDeltaX = 70;		//delta x to the right in px 
				
				//skip the movement, because current gesture cant become a drag gesture any more. Some conditions are violated.
				if(isPotentialDragGesture == false){
					break;
				}
				
				float touchCurrentX = ev.getX();
				float touchCurrentY = ev.getY();
				
				//if the finger was not moved into the y direction more than into the x direction. 
				//This happens 
				//				- when the finger is moved more along the y axis.
				//				- when the finger is moved to the right along the x axis only, since it results in a negative delta x 
				if(Math.abs(touchCurrentY-mTouchInitialY) > (touchCurrentX-mTouchInitialX)){
					isPotentialDragGesture=false;
					break;
				}
				
				//if the predefined delta x of 200px was reached on time start the gesture
				if((touchCurrentX-mTouchInitialX)>dragGestureDeltaX){
					//start the block dragging here, if all tests are passed
					startDragging(ev);
					isPotentialDragGesture=false;
				}
			break;
		}
		
		//TODO: delete the logs
//		
//		Log.d("imageview","Touch event "+M.motionEventResolve(ev.getAction()) );
//		Log.d("pane","-- ImageView touch event "+M.motionEventResolve(ev.getAction()) );
//		
//		//TODO experimenting
//		Log.d("imagev","--------------" );
//		Log.d("imagev","ImageView dispatch get a touch event "+M.motionEventResolve(ev.getAction()) );
////		Log.d("imagev",M.unifyLength("HistoricalX", 30)+ev.getHistoricalX(0));
////		Log.d("imagev",M.unifyLength("HistoricalOrientation", 30)+ev.getHistoricalOrientation(0));
//		Log.d("imagev",M.unifyLength("x axis", 30)+ev.getX(0));
//		Log.d("imagev",M.unifyLength("orientation", 30)+ev.getOrientation(0) );
//		Log.d("imagev",M.unifyLength("pressure", 30)+ev.getPressure(0) );
//		Log.d("imagev",M.unifyLength("edgeflags", 30)+ev.getEdgeFlags() );
//		Log.d("imagev",M.unifyLength("size", 30)+ev.getSize(0) );
//		Log.d("imagev",M.unifyLength("getToolMajor", 30)+ev.getToolMajor(0) );
//		Log.d("imagev",M.unifyLength("getEventTime", 30)+ev.getEventTime() );
//		Log.d("imagev",M.unifyLength("velocity", 30)+mVelocityTracker.getXVelocity(0) );
//		
//		
//		Log.d("imagev",M.unifyLength("blockwidth", 30)+mBlockwidth );
//		Log.d("imagev",M.unifyLength("touchInitialTime", 30)+mTouchInitialTime );
//		Log.d("imagev",M.unifyLength("touchCurrentTime", 30)+ev.getEventTime() );
//		Log.d("imagev",M.unifyLength("delta time", 30)+(ev.getEventTime()-mTouchInitialTime) );
//		Log.d("imagev",M.unifyLength("delta y", 30)+(ev.getY()-mTouchInitialY) );
//		Log.d("imagev",M.unifyLength("delta x", 30)+(ev.getX()-mTouchInitialX) ); //need to know about movement to the left. To the left Y gets bigger
		
		return result;
	}

	
	
	
//DRAG
		public void startDragging(MotionEvent e) {
			//react on double Tap of the Image of a BlockRepresentationInBlockpane
			String classToInstantiate = this.getBlockRepresentationInBlockpane().getBlockRepresentationInBlockpaneClassPath();
			try {
//				unchecked cast here, because it depends on the user of the ScratchTab framework, that only instances of  DraggableBlockWithSlots are put into the ArrayList inside of BlockList.getAllAvailableBlocksAsGroups()
				@SuppressWarnings("unchecked")
				Class<? extends DraggableBlockWithSlots> c = (Class<? extends DraggableBlockWithSlots>) Class.forName(classToInstantiate);
				DragHandler.startDrag(c, e, this);
				
			} catch (Exception e1) {
				AppRessources.popupHandler.popError("Error, while a new block was created in the Block Pane.");
				e1.printStackTrace();
			} 
		}



		
}

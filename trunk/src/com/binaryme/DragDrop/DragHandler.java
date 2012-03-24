package com.binaryme.DragDrop;

import java.lang.reflect.Constructor;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewParent;

import com.binaryme.ScratchTab.InterfaceStaticInitializable;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.InterfaceBlockContainer;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.DraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;

/**
 * This class is for handling dragging and dropping of draggable ScratchTagBlocks, which are represented by {@link DraggableBlockWithSlots}. 
 * All related logic is implemented here. 
 * Drag should be started by using {@link #startDrag(DraggableBlockWithSlots, MotionEvent)
 * and drop should be made by using {@link #executeDropTo(InterfaceBlockContainer, int, int)}. */
public class DragHandler implements InterfaceStaticInitializable{

	private static Activity mContext;
	
	private static boolean isDragging = false;
	private static DraggableBlockWithSlots blockCurrentlyDragging = null;
	
	private static float blockTouchDeltaX = 0;
	private static float blockTouchDeltaY = 0;
	
	public static boolean isDragging(){
		return isDragging;
	}
	
	@SuppressWarnings("unused")
	private static OnDragListener mDragListener; //will be initialized on application start, by my creation - the static initializer :)
	
//GETTER
	public static synchronized float getBlockTouchDeltaX(){
		return DragHandler.blockTouchDeltaX;
	}
	public static synchronized float getBlockTouchDeltaY(){
		return DragHandler.blockTouchDeltaY;
	}
	
	
	public static synchronized void stoppingBlockDragging(){
		DragHandler.isDragging = false;
		DragHandler.blockCurrentlyDragging = null;
	}
	
	public static synchronized DraggableBlockWithSlots getBlockDragging(){
		return DragHandler.blockCurrentlyDragging;
	}
	
//DRAG AND DROP
	
	public static synchronized void undoCurrentDrag(){
		if(DragHandler.blockCurrentlyDragging != null){
			//make the dragged visible at it's old place 
			DragHandler.blockCurrentlyDragging.setVisible(true);
		}
		
		
		//we are responsible for stopping the dragging process, since in TabScratch the Drop handling container is responsible for stopping the drag.
		DragHandler.stoppingBlockDragging();
	}
	
	/**
	 * used by draggable objects, to initiate the drag process.
	 * <b>ATTENTION!</b> if the block, which should be dragged was never drawn, e.g. if its a new instance, then you should pass a new, measured, layedout, drawn view as a dragShadowView. Otherwise android.view.View.startDrag() wont work.  
	 * @param block - the block, which we are dragging
	 * @param event - the event, which was created, when the block dragging was initiated
	 * @return		- true, if the starting process was successfull
	 * @param dragShadowViewview- if you like you can pass the dragshadowView explicitly, otherwise the blockclass will be used for generating the Shadow
	 */
	public static synchronized boolean startDrag(DraggableBlockWithSlots block, MotionEvent event, View dragShadowView){
		boolean result = false;
		
		if(DragHandler.isDragging == false){//do not start new dragging process, when old process is not done
			result = true;
		
			//dragging process started now
			DragHandler.isDragging = true;
			
			//remember the block, to make it available if necessary
			DragHandler.blockCurrentlyDragging = block;
			
			if(event != null){
				//remember the touch position
				blockTouchDeltaX = event.getX();
				blockTouchDeltaY = event.getY();
			}
			
			//hide this block in it's old place
			block.setVisible(false);
			
			//try to stop the execution, if this block's tree is executed now
			ExecutionHandler.stopExecutableTree( (ExecutableDraggableBlockWithSlots<?, ?>) blockCurrentlyDragging);
			
			//start dragging by using Android Framework
			if(dragShadowView != null){
//				dragShadowView.startDrag(null, new DragShadowBuilder(dragShadowView), null, 0); // use the own shadow builder
				dragShadowView.startDrag(null, new MyDragShadowBuilder(dragShadowView), null, 0);
			}else{
//				block.startDrag(null, new DragShadowBuilder(block), null, 0);
				block.startDrag(null, new MyDragShadowBuilder(block), null, 0);
			}
		}
		
		return result;
	}
	/**
	 * Class used to start drag and drop process for a new block, dragged from the block pane
	 * @param blockclass		- this class will be instantiated
	 * @param dragShadowView	- this class will be used to create the drag shadow
	 * @param event				- the event, which was created, when the block dragging was initiated
	 * @param dragShadowViewview- if you like you can pass the dragshadowView explicitly, otherwise the blockclass will be used for generating the Shadow
	 * @return
	 */
	public static synchronized boolean startDrag(Class<? extends DraggableBlockWithSlots> blockclass, MotionEvent event, View dragShadowViewview){
		boolean result = false;
		if(DragHandler.isDragging == true){
			//do not start new dragging process, when old process is not done, cancel the old process first
			undoCurrentDrag();
		}try {
			
			//get the constructor, parametrized with the Activity 
			Constructor<?> activityConstructor = blockclass.getConstructor(new Class[]{Activity.class});
			
			//use the constructor BlockClass(Activity activity) to instantiate the block
			Object nextBlockObject = activityConstructor.newInstance(new Object[]{mContext});
			
			//every object in the block pane is a draggable block, we will insert nothing else
			DraggableBlockWithSlots nextBlock = (DraggableBlockWithSlots)nextBlockObject;
			
			result= startDrag(nextBlock, event, dragShadowViewview);
			
		} catch (Exception e) {
			AppRessources.popupHandler.popError("Exception occured, while a new block was created.");
		}
		
		return result;
	}
	
	/** used by objects, where Drop ended to receive the dropped block */
	public static synchronized void executeDropTo( InterfaceBlockContainer dropContainer, int posX, int posY){

		if(isDragging == true){
				
			//remove the dragged vie from parent, so that we can assign it to its new parent
			ViewParent oldParent = blockCurrentlyDragging.getParent();
			if(oldParent != null)((InterfaceBlockContainer)oldParent).remove(blockCurrentlyDragging);  // old parent may be empty, if we are dragging a new block
			
			//add the block to the new parent
			dropContainer.add(blockCurrentlyDragging, posX, posY);
			
			//block was hidden during the dragging process. make the block visible again.
			blockCurrentlyDragging.setVisible(true);
	
			//redraw the block from the inserted block till the root block in its NEW Position
			blockCurrentlyDragging.clearCacheForWholeStackAboveCurrentBlock();
			blockCurrentlyDragging.redrawUncachedStackFromRoot();
			
			//redraw the old parent till root, if its a block and not the workspace. old parent may be empty, if we are dragging a new block
			if((oldParent != null) && (oldParent instanceof Block)){
				Block<?> blockParent= (Block<?>)oldParent;
				blockParent.clearCacheForWholeStackAboveCurrentBlock();
				blockParent.redrawUncachedStackFromRoot();
			}
			
			// stop dragging. In my application the OnDragListener, which handles the drop stops the dragging process.
			// the dragging process in TabScratch is started by the instances of the DraggableBlockWithSlots class.
			stoppingBlockDragging();
		}
	}
	
	/** used by objects, where Drop ended to remove the dropped block */
	public static synchronized void executeBlockDestruction(){

		if(isDragging == true){
			
			//remove the dragged view from parent, so that we can assign it to its new parent
			ViewParent oldParent = blockCurrentlyDragging.getParent();
			if(oldParent != null)((InterfaceBlockContainer)oldParent).remove(blockCurrentlyDragging);  // old parent may be empty, if we are dragging a new block
						
			//block was hidden during the dragging process. Set Visibility to GONE, so that it wont occupy any space until it is destroyed.
			blockCurrentlyDragging.setVisibility(View.GONE);
			
			//redraw the old parent till root, if its a block and not the workspace. old parent may be empty, if we are dragging a new block
			if((oldParent != null) && (oldParent instanceof Block)){
				Block<?> blockParent= (Block<?>)oldParent;
				blockParent.clearCacheForWholeStackAboveCurrentBlock();
				blockParent.redrawUncachedStackFromRoot();
			}
			
			//call the onDelete hook to let the block do something, e.g. releasing resources or unregister sensor listener
			blockCurrentlyDragging.onDelete();
			
			// stop dragging. In my application the OnDragListener, which handles the drop stops the dragging process.
			// the dragging process in TabScratch is started by the instances of the DraggableBlockWithSlots class.
			stoppingBlockDragging();
		}
	}
	
	@Override
	public void onApplicationStart(Activity context) {
		mContext = context;	
	}
}

package com.binaryme.ScratchTab.Gui.Blocks;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.tools.ColorPalette;

/** Class represents blocks with executable app logic inside. 
 *  @param <S> - should be a concrete ShapeWithSlots, associated with the current block.
 *  @param <E> - should be the class, which the current executable block will return after the execution. */
public abstract class ExecutableDraggableBlockWithSlots<S extends ShapeWithSlots, E extends Object> extends DraggableBlockWithSlots implements Executable<E> {
	
	private boolean isInterrupted = false;	//set to true if the current executable is interrupted
	
	public ExecutableDraggableBlockWithSlots(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
		}
		public ExecutableDraggableBlockWithSlots(Activity context, AttributeSet attrs) {
			super(context, attrs);
		}
		public ExecutableDraggableBlockWithSlots(Activity context) {
			super(context);	
		}
		
		
//IMPLEMENT INTERFACES
		
		//EXECUTABLE BLOCKS SIGNALIZE EXECUTION AND ERRORS BY CHANING THE BLOCKS BODY COLOR
		@Override
		public void feedbackExecutionError() {
			// change the color of my body
			this.getShape().setCurrentFillColor(ColorPalette.colorAppError);
			this.invalidateTree();
		}
		@Override
		public void feedbackExecutionProcessRunning() {
			// change the color of my body
			this.getShape().setCurrentFillColor(ColorPalette.makeBrider(getBodyColor(), 0.3f ));
			this.invalidateTree();
		}
		@Override
		public void feedbackDisable() {
			// reset the body color of my shape
			this.getShape().setCurrentFillColor(this.getBodyColor());
			this.invalidateTree();
		}	
		
//OVERRIDE
		@Override
		protected abstract S initiateShapeHere();
		
		@SuppressWarnings("unchecked")
		@Override
		public S getShape() {
			return (S) super.getShape();
		}
		
		@Override
		public void interruptExecution() {
			this.isInterrupted = true;
		}
		public void setInterrupted(boolean interrupted) {
			this.isInterrupted = interrupted;
		}
		@Override
		public Boolean isInterrupted() {
			return this.isInterrupted;
		}
}

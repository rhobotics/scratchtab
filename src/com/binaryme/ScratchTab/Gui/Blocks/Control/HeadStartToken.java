package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Events.HeadStartTokenHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeHeadStartToken;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class HeadStartToken extends ExecutableDraggableBlockWithSlots<ShapeHeadStartToken, Object>{  
	
	public HeadStartToken(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public HeadStartToken(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public HeadStartToken(Activity context) {
			super(context);	
			init();
		}
		void init(){
			//register the current block to be started, when start token is pushed
			HeadStartTokenHandler.registerStartTokenHead(this);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeHeadStartToken initiateShapeHere() {
			return new ShapeHeadStartToken(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			//TODO
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeHeadStartToken.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}

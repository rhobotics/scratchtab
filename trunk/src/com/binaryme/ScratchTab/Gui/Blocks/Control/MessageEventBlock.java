package com.binaryme.ScratchTab.Gui.Blocks.Control;

import android.app.Activity;
import android.util.AttributeSet;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Events.HeadMessageEventHandler;
import com.binaryme.ScratchTab.Events.HeadStartTokenHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeMessageEventBlock;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataText;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class MessageEventBlock extends ExecutableDraggableBlockWithSlots<ShapeMessageEventBlock, Object>{  
	
	public MessageEventBlock(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public MessageEventBlock(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public MessageEventBlock(Activity context) {
			super(context);	
			init();
		}
		void init(){
		}
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeMessageEventBlock initiateShapeHere() {
			return new ShapeMessageEventBlock(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			
			//retrieve the SlotLabel from this block's Shape
			SlotLabel slotlabel = (SlotLabel) this.getShape().getSlot(ShapeMessageEventBlock.LABEL);
			
			//retrieve the DataText from the Label. The first DataTextSlot inside of the Label is found here, and checked for the Value. 
			//If no such a Slot is found inside of the label the ConfigHandler.DEFAULT_VALUE_TEXTFIELD is returned which is "" 
			String newmessage = slotlabel.executeForTextValue(executionHandler);
//			String newmessage = (String) executionHandler.executeExecutable(slotlabel);
//			SlotDataText slotDataText = slotlabel.findFirstOccurenceOfSlot(SlotDataText.class);
//			String newmessage = (String) executionHandler.executeExecutable(slotDataText);
			
			//TODO var to check if interrupted delete it
			boolean isInterrupted = executionHandler.isInterrupted();
			
			//start a message event now
			HeadMessageEventHandler.fireMessageEvent(newmessage);
			
			//notify about a new Message
			//KEEP NOTIFICATION IN FINAL RELEASE - USEFUL
			AppRessources.popupHandler.pop("Send Message: "+newmessage);
			
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeMessageEventBlock.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}

}

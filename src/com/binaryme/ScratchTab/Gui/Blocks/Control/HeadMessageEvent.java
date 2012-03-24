package com.binaryme.ScratchTab.Gui.Blocks.Control;

import java.util.ArrayList;

import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Events.HeadMessageEventHandler;
import com.binaryme.ScratchTab.Events.InterfaceMessageEventReceiver;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeHeadMessageEvent;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataText;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class HeadMessageEvent extends ExecutableDraggableBlockWithSlots<ShapeHeadMessageEvent, Object> implements InterfaceMessageEventReceiver{  
	
	/** all the messages, which are retrieved by this block are saved here. 
	 *  After a message is received - this executable will try to execute itself. 
	 *   The execution trial will be started successful if the message in the head of this block is found in this pool.
	 *  After each execution trial this pool will be cleared. */
	private ArrayList<String> messagePool = new ArrayList<String>();
	
	public HeadMessageEvent(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public HeadMessageEvent(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public HeadMessageEvent(Activity context) {
			super(context);	
			init();
		}
		void init(){
			//register the current block to be started, when start token is pushed
			HeadMessageEventHandler.registerMessageEventReceiver(this);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeHeadMessageEvent initiateShapeHere() {
			return new ShapeHeadMessageEvent(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler<?> executionHandler) {
			//on initially execution the head should not be iterrupted, even if it was interrupted in the past
			this.setInterrupted(false);
			
			//first retrieve the value, which this head block contains in its text field
			SlotLabel slotlabel 	= (SlotLabel) this.getShape().getSlot(ShapeHeadMessageEvent.LABEL);
			SlotDataText slotText 	= slotlabel.findFirstOccurenceOfSlot(SlotDataText.class); 
			String str = slotText.executeForValue(executionHandler);
			
			//check if we have the given message in the queue
			boolean messageReceived = this.hasReceivedMessage(str.trim());
			if( !messageReceived){
				this.interruptExecution();
			}
			
			//empty the message pool now.
			this.emptyMessagePool();
			
			//now the ExecutionHandler will execute the successor block, if a matching message was found inside of the pool and the execution was not interrupted
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				//Todo test for Nullpointer 
				ShapeHeadMessageEvent s = this.getShape();
				Slot slot = s.getSlot(ShapeHeadMessageEvent.CHILD_BELOW);
				return slot;
			}catch(NullPointerException e){
				return null;
			}
		}
		
		
//MESSAGING
		@Override
		public void onMessageEvent(String message) {
			//add the message to the pool
			this.messagePool.add(message);
			
			//try to execute the current executable. It will check it's head value and interrupt the execution, if the message is not contained by the pool			
			new ExecutionHandler<Object>(this, AppRessources.context);
		}
		private synchronized void addMessageToPool(String message){
			this.messagePool.add(message.trim());
		}
		private synchronized boolean hasReceivedMessage(String message){
			boolean result = false;
			for(String poolmessage:this.messagePool){
				Log.d("message","Poolmessage "+poolmessage+" compared with "+message);
				if(message.equals(poolmessage)){
					result = true;
					break;
				}
			}
			
			return result;
		}
		private void emptyMessagePool(){
			this.messagePool.clear();
		}
}

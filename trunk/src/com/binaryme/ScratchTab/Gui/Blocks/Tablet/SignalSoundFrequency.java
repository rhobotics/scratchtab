package com.binaryme.ScratchTab.Gui.Blocks.Tablet;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.WindowManager;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Label;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeDoXTimes;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeDriveForwardLimited;
import com.binaryme.ScratchTab.Gui.Shapes.Tablet.ShapeSignalSoundFrequency;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;


public class SignalSoundFrequency extends ExecutableDraggableBlockWithSlots<ShapeSignalSoundFrequency, Object>{  
	
	private SlotDataNum slotDataNum;
	
	public SignalSoundFrequency(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public SignalSoundFrequency(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public SignalSoundFrequency(Activity context) {
			super(context);	
			init();
		}
		
		private void init(){
			//finding the slots
			SlotLabel slotLabel = (SlotLabel) this.getShape().getSlot(ShapeDoXTimes.LABEL_TOP);
			Label label = (Label) slotLabel.getInfill();
			slotDataNum = label.findFirstOccurenceOfSlot(SlotDataNum.class);
			
			slotDataNum.getNumField().setText("440"); //deafault value
		}
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeSignalSoundFrequency initiateShapeHere() {
			return new ShapeSignalSoundFrequency(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler<?> executionHandler) {
			double frequencyD = (Double) executionHandler.executeExecutable( slotDataNum );
			
			//play a Sound
			AppRessources.soundHandler.setFrequency(frequencyD);
			AppRessources.soundHandler.genTone();
			AppRessources.soundHandler.playSound();
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeDriveForwardLimited.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
}



package com.binaryme.ScratchTab.Gui.Blocks.Tablet;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.WindowManager;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeDriveForwardLimited;
import com.binaryme.ScratchTab.Gui.Shapes.Tablet.ShapeSignalScreenflash;
import com.binaryme.ScratchTab.Gui.Slots.Slot;


public class SignalScreenflash extends ExecutableDraggableBlockWithSlots<ShapeSignalScreenflash, Object>{  
	
	
	public SignalScreenflash(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public SignalScreenflash(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public SignalScreenflash(Activity context) {
			super(context);	
			init();
		}
		
		private void init(){
			//finding the slots
		}
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeSignalScreenflash initiateShapeHere() {
			return new ShapeSignalScreenflash(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler<?> executionHandler) {
			
			//playing with the brightness of the screen 
			final WindowManager.LayoutParams layout = AppRessources.context.getWindow().getAttributes();
			final float oldBrightness = layout.screenBrightness;
			
			AppRessources.context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//bright
					layout.screenBrightness = 1F;
					AppRessources.context.getWindow().setAttributes(layout);
				}
			});
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//sleeping failed
			}
			
			AppRessources.context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//dark
					layout.screenBrightness = 0;
					AppRessources.context.getWindow().setAttributes(layout);
				}
			});
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//sleeping failed
			}
			
			AppRessources.context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//normal
					layout.screenBrightness = oldBrightness;
					AppRessources.context.getWindow().setAttributes(layout);
				}
			});
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



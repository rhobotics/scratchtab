package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTHandler.TurnDirection;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeHeadNXTSensorDataLarger;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeTurnAround;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;


public class TurnAround extends ExecutableDraggableBlockWithSlots<ShapeTurnAround, Object>  implements OnItemSelectedListener {  
	
	//widgets
	private MSpinner mSpinnerWidget;
	
	private Direction currentDirection = Direction.RIGHT;
	
	public static enum Direction{ RIGHT, LEFT }
	
	public TurnAround(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public TurnAround(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public TurnAround(Activity context) {
			super(context);
			init();
		}
		private void init(){
			//find the Label
				SlotLabel slotlabel = (SlotLabel) this.getShape().getSlot(ShapeHeadNXTSensorDataLarger.LABEL);
			
			//spinner
				//get the slot
				SlotDataSpinner slotspinner = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotDataSpinner.class);
				
				//get the spinner
				this.mSpinnerWidget = slotspinner.getSpinner();
				
				//listen for Item selection
				mSpinnerWidget.setOnItemSelectedListener(this);
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeTurnAround initiateShapeHere() {
			return new ShapeTurnAround(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			if(currentDirection == Direction.RIGHT ){
				AppRessources.legoNXTHandler.turnAround(TurnDirection.RIGHT);
			}else{
				AppRessources.legoNXTHandler.turnAround(TurnDirection.LEFT);
			}
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeTurnAround.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
		
//HANDLERS FOR SPINNER
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String selected = ((TextView)arg1).getText().toString();
			
			//parse enum and select a new sensor
			this.currentDirection = Direction.valueOf(selected);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
}


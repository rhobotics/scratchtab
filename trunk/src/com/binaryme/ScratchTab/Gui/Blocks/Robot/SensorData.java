package com.binaryme.ScratchTab.Gui.Blocks.Robot;

import icommand.scratchtab.LegoNXTSensorEventListener;
import icommand.scratchtab.LegoNXTHandler.LegoNXTSensorData;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyTextDisplayOnly;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeHeadNXTSensorDataLarger;
import com.binaryme.ScratchTab.Gui.Shapes.Robot.ShapeSensorData;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;
import com.binaryme.ScratchTab.Gui.Widgets.MTextDisplayOnly;


public class SensorData extends ExecutableDraggableBlockWithSlots<ShapeSensorData, Double>  implements LegoNXTSensorEventListener, OnItemSelectedListener, InterfaceNumDataContainer{  
	
	//widgets
	private MTextDisplayOnly mTextDisplayWidget;
	private MSpinner mSpinnerWidget;
	
	private SensorType currentSensor = SensorType.NO_SENSOR; //default sensor is distance
	private int currentSensorValue;
	
	public static enum SensorType{ NO_SENSOR, DISTANCE, BUTTON, SOUND, LIGHT }
	
	public SensorData(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public SensorData(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public SensorData(Activity context) {
			super(context);	
			init();
		}
		
		private void init(){
			
		//find the Label
		SlotLabel slotlabel = (SlotLabel) this.getShape().getSlot(ShapeHeadNXTSensorDataLarger.LABEL);
			
		//text display
			//get the slot
			SlotTextDisplayOnly slot = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotTextDisplayOnly.class);
			
			//getting the dummy
			BlockSlotDummyTextDisplayOnly dummy = slot.getInfill();
			
			//getting the Textdisplay-widget
			this.mTextDisplayWidget = dummy.getWidget();
		
		//spinner
			//get the slot
			SlotDataSpinner slotspinner = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotDataSpinner.class);
			
			//get the spinner
			this.mSpinnerWidget = slotspinner.getSpinner();
			
			//listen for Item selection
			mSpinnerWidget.setOnItemSelectedListener(this);
		
			
		//SETTINGS
		//register the current block to listen for sensor data
		AppRessources.legoNXTHandler.addLegoNXTSensorEventListener(this);
		
		//set the Popup title
		mSpinnerWidget.setPrompt("NXT sensors");
		}
		

		
//HANDLERS FOR SPINNER
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String selected = ((TextView)arg1).getText().toString();
			
			//parse enum and select a new sensor
			this.currentSensor = SensorType.valueOf(selected);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeSensorData initiateShapeHere() {
			return new ShapeSensorData(getContextActivity(),this);
		}
		@Override
		public Double executeForValue(ExecutionHandler executionHandler) {
			//just return the sensor value
			return (double) this.currentSensorValue;
		}
		@Override
		public Slot getSuccessorSlot() {
			return null;
		}
		@Override
		public double getNum(ExecutionHandler handler) {
			return currentSensorValue;
		}
		@Override
		public String parseString(ExecutionHandler handler) {
			return ""+currentSensorValue;
		}
		
//NXT
		@Override
		public void OnNewSensorData(final LegoNXTSensorData data) {
			
			//check if some sensor is selected 
			if( this.currentSensor != SensorType.NO_SENSOR){
			
				AppRessources.context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						currentSensorValue = filterData(data, currentSensor);
						//setting the display
						mTextDisplayWidget.setText(String.valueOf( currentSensorValue) );
					}
				});
				
			}
		}
		
		
		//HELER		
		public static int filterData(LegoNXTSensorData data, SensorType currentSensor ){
			int result = 0;
			
			switch (currentSensor){
				case DISTANCE:
					if(data.distance<=0){
						result = 100; // the default sensor value should be max, or we will get problems, when we want to react on concrete distance. The value will jump from -1 to 100(max sensor value) and back.
					}else{
						result= data.distance;
					}
					break;
					
				case BUTTON:
					if(data.button){
						result = 1;
					}else{
						result=0;
					}
					break;	
					
				case LIGHT:
					result = data.light;
					break;	
					
				case SOUND:
					result = data.db;
					break;	
			}
			return result;
	}

}

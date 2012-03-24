package com.binaryme.ScratchTab.Gui.Blocks.Control;

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
import com.binaryme.ScratchTab.Gui.Blocks.Robot.SensorData;
import com.binaryme.ScratchTab.Gui.Blocks.Robot.SensorData.SensorType;
import com.binaryme.ScratchTab.Gui.Shapes.Control.ShapeHeadNXTSensorDataSmaller;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotLabel;
import com.binaryme.ScratchTab.Gui.Slots.SlotTextDisplayOnly;
import com.binaryme.ScratchTab.Gui.Widgets.MNumField;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;
import com.binaryme.ScratchTab.Gui.Widgets.MTextDisplayOnly;


public class HeadNXTSensorDataSmaller extends ExecutableDraggableBlockWithSlots<ShapeHeadNXTSensorDataSmaller, Object> implements LegoNXTSensorEventListener, OnItemSelectedListener{  
	
	//pointer for the runnable
	private final HeadNXTSensorDataSmaller thisObject = this;
	
	//widgets
	private MTextDisplayOnly mTextDisplayWidget;
	private MSpinner mSpinnerWidget;
	private MNumField mNumbersInputField;
	
	private SensorData.SensorType currentSensor = SensorData.SensorType.NO_SENSOR; //default sensor is distance
	private int currentSensorValue;
	
	private ExecutionHandler executionHandler;
	
	
	
	
	public HeadNXTSensorDataSmaller(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public HeadNXTSensorDataSmaller(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public HeadNXTSensorDataSmaller(Activity context) {
			super(context);	
			init();
		}
		void init(){
			
			//finding my text display widget of class MTextDisplayOnly.class
			//find the Label
			SlotLabel slotlabel = (SlotLabel) this.getShape().getSlot(ShapeHeadNXTSensorDataSmaller.LABEL);
			
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
			
			//numbers input field
				//get the slot
				SlotDataNum slotDataNum = slotlabel.getLabel().findFirstOccurenceOfSlot(SlotDataNum.class);
				
				//remember the num widget
				this.mNumbersInputField = slotDataNum.getNumField();
				
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
			currentSensor = SensorData.SensorType.valueOf(SensorData.SensorType.class, selected);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
//IMPLEMENT INTERFACES
		@Override
		protected ShapeHeadNXTSensorDataSmaller initiateShapeHere() {
			return new ShapeHeadNXTSensorDataSmaller(getContextActivity(),this);
		}
		@Override
		public Object executeForValue(ExecutionHandler executionHandler) {
			this.executionHandler = executionHandler;
			return null;
		}
		@Override
		public Slot getSuccessorSlot() {
			try{
				return this.getShape().getSlot(ShapeHeadNXTSensorDataSmaller.CHILD_BELOW);
			}catch(NullPointerException e){
				return null;
			}
		}
		
//NXT
		@Override
		public void OnNewSensorData(final LegoNXTSensorData data) {
			
			//check if we are already running, by checking if the thread is alive AND if some sensor is selected 
			if(  
				( (executionHandler==null) || !executionHandler.isAlive() ) &&
				  (this.currentSensor != SensorType.NO_SENSOR)	){
			
				AppRessources.context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						currentSensorValue = SensorData.filterData(data, currentSensor);
						//setting the display
						mTextDisplayWidget.setText(String.valueOf( currentSensorValue) );
						
						//APP LOGIC: compare the value and execute if needed
						if( currentSensorValue < mNumbersInputField.getValueAsDouble()){
							new ExecutionHandler(thisObject, AppRessources.context);
						}
					}
				});
				
			}
		}
		
		
}

package com.binaryme.ScratchTab.Gui.Blocks.Tablet;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.binaryme.AndroidSensors.MySensor;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;
import com.binaryme.ScratchTab.Gui.Widgets.MTextDisplayOnly;


public abstract class AbstractSensorBlock<T  extends ShapeWithSlots> extends ExecutableDraggableBlockWithSlots<T, Double>  implements SensorEventListener, OnItemSelectedListener, InterfaceNumDataContainer{  
	
	//widgets
	protected MTextDisplayOnly mTextDisplayWidget;
	protected MSpinner 		 mSpinnerWidget;
	
	protected MySensor 	currentSensor;
	protected int 		currentSensorValue = 0;
	//All sensors except the light sensor got additional dimension, like X,Y,Z axis. This the the current axis.
	protected String 		currentSensorDimension = MySensor.OFF;  //OFF is the default value 
	
	protected boolean isOn = false;
	
	public AbstractSensorBlock(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			this.currentSensor = getMySensorFromShape();
			init();
	}
	public AbstractSensorBlock(Activity context, AttributeSet attrs) {
		super(context, attrs);
		this.currentSensor = getMySensorFromShape();
		init();
	}
	public AbstractSensorBlock(Activity context) {
		super(context);	
		this.currentSensor = getMySensorFromShape();
		init();
	}
	
	/** initiate the Sensorblock here */
	protected abstract void init();
	
	/** get the sensor from the current shape */
	protected abstract MySensor getMySensorFromShape();
		

		
//SELECTION HANDLER FOR SPINNER
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String selected = ((TextView)arg1).getText().toString();
			onSelection(selected);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
		
//SENSOR EVENT
		@Override
		public void onSensorChanged(SensorEvent event) {
			currentSensorValue = this.currentSensor.getValueForDimension(currentSensorDimension, event);
			AppRessources.context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//setting the display
					mTextDisplayWidget.setText(String.valueOf(currentSensorValue) );
				}
			});
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
		
//IMPLEMENT INTERFACES
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
		public void onDelete() {
			//unregister this block as listener
			this.currentSensor.unregisterListener(this);
		}
		@Override
		public double getNum(ExecutionHandler handler) {
			return currentSensorValue;
		}
		@Override
		public String parseString(ExecutionHandler handler) {
			return ""+currentSensorValue;
		}
		
		
//HELER
		public void onSelection(String theSensorDimension) {
			this.currentSensorDimension = theSensorDimension;
			//now register or unregister this block as listener
			
			if(currentSensorDimension == MySensor.OFF){
				//some sensors have an artificial OFF dimension, which should turn the sensor off.
				//unregister the listener to save the battery
				this.currentSensor.unregisterListener(this);
				this.isOn = false;
				
			}else 	if(currentSensorDimension == MySensor.ON){
				//some sensors have an artificial ON dimension, which should turn the sensor off.
				this.currentSensor.registerListener(this);
				this.isOn = true;
				
			}else{
				//other dimensions should turn the sensor on, if it is not on yet
				if(!this.isOn){
					this.currentSensor.registerListener(this);
					this.isOn = true;
				}
			}
		}

}

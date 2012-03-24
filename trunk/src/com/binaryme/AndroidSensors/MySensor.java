package com.binaryme.AndroidSensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.binaryme.ScratchTab.Config.AppRessources;

/** This class encapsulates an android sensor. 
 *  It can handle sensor names as string. Useful because only Strings are known, when the user chooses something in a dropdown. */
public class MySensor {
	
	//sensor names as they will be displayed
		public final static String LIGHT 			= "Light";
		public final static String ACCELERATION 	= "Acceleration"; 			// x-axis, y-axis, z-axis
		public final static String MAGNETIC_FIELD 	= "Magnetic field"; 		// x-axis, y-axis, z-axis
		public final static String ORIENTATION 		= "Orientation"; 			// Azimuth, Pitch, Roll
		public final static String ANGULAR_SPEED 	= "Angular speed";			// x-axis, y-axis, z-axis
	
	//sensor dimensions
		public final static String ON 		= "ON";
		public final static String OFF 		= "OFF";
		public final static String XAXIS 	= "x-axis";
		public final static String YAXIS 	= "y-axis";
		public final static String ZAXIS 	= "z-axis";
		public final static String AZIMUTH 	= "Azimuth";
		public final static String PITCH 	= "Pitch";
		public final static String ROLL 	= "Roll";
	
	private final SensorManager mSensorManager;
	private Sensor mAndroidSensor;				//the android sensor object itself
	private int mSensorType;					//used to instantiate AndroidSensors. Android uses int constants.
	private String mSensorTypeAsString;			//used to navigate through HashMaps
	private TreeMap<String, Integer> mSensorDimensions; //cached for the current sensor
	
	// place to save the sensor-id mapping, so that we can handle the sensors by name
	private static HashMap<String,Integer> sensors = new HashMap<String,Integer>();
	
	// place to save the mapping between the Sensor and Its different value-dimensions: [ "Orientation" -> [Azimuth->0, Pitch->1, Roll->2]  ] 
	private HashMap<String,TreeMap<String, Integer>> sensorDimensions = new HashMap<String, TreeMap<String, Integer>>();
	
	
	/**
	 * @param TYPE_OF_SENSOR - the type of the sensor, which will be represented by the current instance. Use one of the static constants to choose the sensor.
	 */
	public MySensor(String TYPE_OF_SENSOR) {
		
		//instantiate the HashMap with all sensors
			initSensorNames();

			mSensorTypeAsString = TYPE_OF_SENSOR;
			mSensorManager = (SensorManager)AppRessources.context.getSystemService(Activity.SENSOR_SERVICE);
			mSensorType = sensors.get(TYPE_OF_SENSOR); 	//resolve the String to int
			mAndroidSensor = mSensorManager.getDefaultSensor(mSensorType);
			
		//instantiate the HashMap with all possible SensorTypes dimensions
			//LIGHT
			TreeMap<String, Integer> light = new TreeMap<String, Integer>();
			light.put(OFF,	  -2);
			light.put(ON,	  -1);
			sensorDimensions.put(LIGHT, light);
			
			//ACCELERATION
			TreeMap<String, Integer> acceleration = new TreeMap<String, Integer>();
			acceleration.put(OFF,	  -2);
			acceleration.put(XAXIS, 0);
			acceleration.put(YAXIS, 1);
			acceleration.put(ZAXIS, 2);
			sensorDimensions.put(ACCELERATION, acceleration);

			//MAGNETIC_FIELD
			TreeMap<String, Integer> magnetic = new TreeMap<String, Integer>();
			magnetic.put(OFF,	  -2);
			magnetic.put(XAXIS, 0);
			magnetic.put(YAXIS, 1);
			magnetic.put(ZAXIS, 2);
			sensorDimensions.put(MAGNETIC_FIELD, magnetic);
			
			//ORIENTATION
			TreeMap<String, Integer> orientation = new TreeMap<String, Integer>();
			orientation.put(OFF,	-2);
			orientation.put(AZIMUTH, 0);
			orientation.put(PITCH, 	1);
			orientation.put(ROLL, 	2);
			sensorDimensions.put(ORIENTATION, orientation);
			
			//ANGULAR_SPEED
			TreeMap<String, Integer> angularSpeed = new TreeMap<String, Integer>();
			angularSpeed.put(OFF,	-2);
			angularSpeed.put(XAXIS, 0);
			angularSpeed.put(YAXIS, 1);
			angularSpeed.put(ZAXIS, 2);
			sensorDimensions.put(ANGULAR_SPEED, angularSpeed);
			
			
			//remember the dimensions
			mSensorDimensions = this.sensorDimensions.get(this.mSensorTypeAsString);
	}
	
//INTERACTION
	/** gets the Sensorname */
	public String getName(){
		return mSensorTypeAsString;
	}
	
	/** Retrieves all the dimensions for the current sensor by its name. E.g. {"Azimuth", "Pitch" ,"Roll"} for TYPE_ORIENTATION sensor */
	public String[] getDimensionsForCurrentSensor(){
		//get the Hashmap with the second dimensions from the Hashmap with Sensor-names
		Set<String> keyset = this.mSensorDimensions.keySet();

		//Im too lazy to implement sortByValue for Hashmapts, so I implement custom sort to keep ON and OFF at the top.
		ArrayList<String> result = new ArrayList<String>();
		for(String str: keyset){
			result.add(str);
		}
		
		if(keyset.contains(OFF)){
			result.remove(OFF);
			result.add( 0, OFF );	//prepend
		}else if(keyset.contains(ON)){
			result.remove(ON);
			result.add( 1, ON );	//prepend
		}
		return result.toArray(new String[result.size()] );
	}

	
	/** Can choose the right value from a sensorEvent for the dimension, given as String.  
	 *  On default, if ON or OFF are selected - return values[0]
	 *  
	 * @param currentDimension - what value should we retrieve from the SensorEvent.  If a sensor doesn't have any dimensions - any value can be passed as currentDimension.
	 * @param sensorEvent
	 * @return
	 */
	public int getValueForDimension(String currentDimension , SensorEvent sensorEvent){
		int result=0;
		
		//the current sensor has some dimensions. Choose the right one, young padawan.
		int arraykey = this.mSensorDimensions.get(currentDimension);

		if((arraykey >= 0) && (arraykey<sensorEvent.values.length )){
			result = (int) sensorEvent.values[arraykey];
		}else{
			//if its ON or OFF return the default Sensor value from the array position 0 
			result = (int) sensorEvent.values[0];
		}
		return result;
	}
	
	/** Adds a sensor to the list. The sensor is turned on here, if it the first registered sensorListener. Battery consumption begins. */
	public void registerListener(SensorEventListener sensorEventListener){
		mSensorManager.registerListener(sensorEventListener, mAndroidSensor,  SensorManager.SENSOR_DELAY_UI);
	}
	
	/** Removes a sensor from the list. The sensor is turned off here, if it the last registered sensorListener. Battery consumption by the sensor stops. */
	public void unregisterListener(SensorEventListener sensorEventListener){
		mSensorManager.unregisterListener(sensorEventListener);
	}
	
//GENERAL METHODS FOR ALL SENSORS
	/** gets all the Sensor names */
	public static String[] getAllPossibleSensors(){
		if(sensors.isEmpty()){
			initSensorNames();
		}
		String[] result = (String[]) sensors.keySet().toArray();
		return result;
	}
	//instantiate the HashMap with all sensors
	private static void initSensorNames(){
		sensors.put(LIGHT, Sensor.TYPE_LIGHT);
		sensors.put(ACCELERATION, Sensor.TYPE_ACCELEROMETER);
		sensors.put(MAGNETIC_FIELD, Sensor.TYPE_MAGNETIC_FIELD);
		sensors.put(ORIENTATION, Sensor.TYPE_ORIENTATION);
		sensors.put(ANGULAR_SPEED, Sensor.TYPE_GYROSCOPE);
	}
	
}

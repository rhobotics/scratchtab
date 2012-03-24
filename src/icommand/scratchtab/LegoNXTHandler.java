package icommand.scratchtab;

import icommand.navigation.TachoNavigator;
import icommand.nxt.LightSensor;
import icommand.nxt.Motor;
import icommand.nxt.SensorPort;
import icommand.nxt.SoundSensor;
import icommand.nxt.TouchSensor;
import icommand.nxt.UltrasonicSensor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.util.Log;

import com.binaryme.Bluetooth.BluetoothHandler;
import com.binaryme.Bluetooth.BluetoothHandler.ConnectionStatus;
import com.binaryme.Bluetooth.InterfaceConnectionRequestListener;
import com.binaryme.Bluetooth.InterfaceGeneralConnectionListener;



/** This class handles a concrete device - a lego NXT Robot, including the bluetooth connection to this robot.
 *  The connection might not exist, but this class ALLWAYS can receive commands for the robot.
 *  It can do something, when there is no connection and a robot command was received, e.g. request a connection from the BluetoothHandler
 *  
 *  To send commands to the robot the diablu classes are used from http://code.google.com/p/diablu created by Jorge Cardoso */
public class LegoNXTHandler implements InterfaceConnectionRequestListener, InterfaceGeneralConnectionListener {
	
	private Activity contextActivity;
	
	private static BluetoothDevice mDevice;
	private static BluetoothSocket mySocket;

	//NXT control Objects from icommand
		//Object to control the wheels
		private static TachoNavigator robot;
		
		//Sensor Objects
//		ColorSensor colorSensor;  		//don't have this sensor onboard
//		CompassSensor compassSensor;  	//don't have this sensor onboard
		LightSensor lightSensor;
		SoundSensor soundSensor;
//		TiltSensor tiltSensor; 			//don't have this sensor onboard
		TouchSensor touchSensor;
		UltrasonicSensor ultrasonicSensor;
	
	/** started, when a new listener is added or when a connection is established. It will stop it self if needed */
	private static SensorPollingThread sensorPollingThread;
	
	private static ArrayList<WeakReference<LegoNXTSensorEventListener>> LegoNXTSensorEventListenerList = new ArrayList<WeakReference<LegoNXTSensorEventListener>>();
	
    //TODO: move config to a separate file, set config in a menu
	//config
	public static final int config_PORT_MOTOR_ARM=0;
	public static final int config_PORT_MOTOR_RIGHTWHEEL=1;		//motor B in my configuration
	public static final int config_PORT_MOTOR_LEFTWHEEL=2;		//motor C in my configuration
	
	public static final int config_PORT_SENSOR_BUTTON=0;
	public static final int config_PORT_SENSOR_SOUND=1;
	public static final int config_PORT_SENSOR_LIGHT=2;
	public static final int config_PORT_SENSOR_DISTANCE=3;
	
	public static final int config_MOTOR_POWER=75; 					//-100 for backward, till 100 for forward
	
	
	//TODO: if the limit command doesnt work - use this command
//	private NXTCommandSetOutputState robotSuccessSignalCommand = new NXTCommandSetOutputState((byte)0, 
//            (byte)50, 
//            (byte) (NXTCommandSetOutputState.MODE_MOTOR_ON| NXTCommandSetOutputState.MODE_REGULATED), 
//            NXTCommandSetOutputState.REGULATION_MODE_MOTOR_SPEED, 
//            (byte)0, 
//            NXTCommandSetOutputState.RUN_STATE_RUNNING, 100);
	
	public static final int legoNXTBluetoothDeviceClass = 2052; 											//this is a general bluetooth device class of a lego nxt robot. Used to recognize the robot
	public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 	//universally unique identifier - used to create a connection socket to the device. This id is the same for all NXT robots.
	
	
	public LegoNXTHandler(Activity context) {
		contextActivity=context;
		
		//register for listeners
		BluetoothHandler.addConnectionRequestListener(this);
		BluetoothHandler.addGeneralConnectionListener(this); // need general connection info to know, about connection with the device
		
		//init Thread
		sensorPollingThread = new SensorPollingThread();
	}
	
	
//INTERACTION
	/** used by GUI objects, who needs the actual sensor data from robot */
	public void addLegoNXTSensorEventListener(LegoNXTSensorEventListener l){
		LegoNXTSensorEventListenerList.add(new WeakReference<LegoNXTSensorEventListener>(l));
		if(!sensorPollingThread.isRunning()){
			sensorPollingThread = new SensorPollingThread();
			sensorPollingThread.start(); //start the polling thread, it will stop when necessary
		}
	}
	public TachoNavigator getRobot(){
		return robot;
	}
	
//FIRE EVENTS
	private void fireLegoNXTSensorEventListener(LegoNXTSensorData data){
		if(data == null) return;
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<LegoNXTSensorEventListener> r : LegoNXTSensorEventListenerList){
			LegoNXTSensorEventListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException
			}else{
				listener.OnNewSensorData(data);
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		LegoNXTSensorEventListenerList.removeAll(badReferences);
	}
	
	
//IMPLEMENT INTERFACE
	@Override
	public void onRequestConnection(BluetoothDevice device, int deviceClass) {
		//check if it a robot, which a bluetooth connection was started on
		if((device!= null) && (deviceClass == legoNXTBluetoothDeviceClass)){
			mDevice=device;
			new connectionEstablishingThread().start(); //connect to the legoNXT. On success a ConnectedEvent will be fired 
		}
	}
	@Override
	public void onRequestDisconnectionAll() {
		try {
			disconnect();
		} catch (Exception e) {
			Log.d("nxt","Error when diconnecting a device");
			Log.d("nxt",Log.getStackTraceString(e));
		}
	}
	@Override
	public void onRequestDisconnection(BluetoothDevice device, int deviceClass) {
		if(deviceClass == legoNXTBluetoothDeviceClass){
			try {
				disconnect();
			} catch (Exception e) {
				Log.d("nxt","Error when diconnecting a device");
				Log.d("nxt",Log.getStackTraceString(e));
			}
		}
	}
	@Override
	public void onConnected(BluetoothDevice device) {
		// try starting a sensor polling thread, it will stop if there are no listeners.
		if(!sensorPollingThread.isRunning()){
			sensorPollingThread = new SensorPollingThread();
			sensorPollingThread.start(); //start the polling thread, it will stop when necessary
		}
	}
	@Override
	public void onConnectionProcessStarted(BluetoothDevice device) {
		//don't bother
	}
	@Override
	public void onDisconnected(BluetoothDevice device) {
		//don't bother
	}

	
	
	
	
	
	
	
	
	
	
	

//ROBO COMMANDS
	
	/** block: all motors off */
	public void stopAllMotors(HowtoStop howtoStop){
		robot.stop();
	}
	public static enum HowtoStop{SMOOTHLY, IMMEDIATELY}; 
	
	/** block: motor [X] on. 
	 *  use {@link #config_PORT_MOTOR_ARM}, ... variables, they will be loaded from a app config */
	public void startMotorForever(int motorPort){
		//TODO
	}
	
	/** block: let the motor [X] turn by [Y] degrees */
	public void startMotorLimited(int motorPort, int degrees){
		//TODO
	}	
	
	/** block: motor [X] off. 
	 *  use {@link #config_PORT_MOTOR_ARM}, ... variables, they will be loaded from a app config */
	public void stopMotor(int motorPort){
		//TODO
	}	
	
	/** block: motor [X] reverse. 
	 *  use {@link #config_PORT_MOTOR_ARM}, ... variables, they will be loaded from a app config */
	public void reverseMotor(int motorPort){
		//TODO
	}
	
	/** block: rotate left forever */
	public void rotateLeft(){
		robot.setSpeed(100);
		robot.rotateLeft();
		robot.setSpeed(360);
	}
	
	/** block: rotate right forever */
	public void rotateRight(){
		robot.setSpeed(100);
		robot.rotateRight();
		robot.setSpeed(360);
	}
	
	

	/** block: move [X].  */
	public void moveForever(movingDirection direction){
		if(movingDirection.FORWARD == direction){
			robot.forward();
		}else{
			robot.backward();
		}
	}
	/** block: let both wheels rotate by X degrees into the given direction */
	public void moveLimited(movingDirection direction, int degrees){
		if(movingDirection.FORWARD == direction){
			robot.travel(degrees);
		}else{
			robot.travel(-degrees);
		}
	}
	public static enum movingDirection {FORWARD, BACKWARD}; 

	
	/** block: stop both wheels of the robot immediately.  */
	public void stopImmediately(){
		robot.stop();
	}
	
	/** block: stop both wheels smoothly.  */
	public void stopSmoothly(){
		//TODO
	}
	
	/** block: turn [X] by 45 degrees. Turn into given direction */
	public void turn(TurnDirection turnDirection){
		if(TurnDirection.LEFT == turnDirection){
			robot.rotate(90);
		}else{
			robot.rotate(-90);
		}
	}
	/** block: turn [X] by 45 degrees. Turn into given direction */
	public void turnAround(TurnDirection turnDirection){
		if(TurnDirection.LEFT == turnDirection){
			robot.rotate(180);
		}else{
			robot.rotate(180);
		}
	}
	public static enum TurnDirection {LEFT,RIGHT}; 


	
	
	
	
	
	
	
	
	
//HELPER METHODS
	private void disconnect() throws IOException{
		if(robot!=null){ this.stopAllMotors(HowtoStop.SMOOTHLY); }
		if(mDevice!=null){ BluetoothHandler.fireDisconnectedEvent(mDevice); }
		robot = null;
		mySocket=null;
		mDevice=null;
	}
	
	
	
	
	
	
	
//HELPER CLASSES
	/** This thread establishes a connection to the lego NXT robot on socket {@link LegoNXTHandler#mySocket} 
	 *  and ends, whether the connection was established successfully or not.
	 *  Related {@link BluetoothHandler} Events are fired by this thread. */
	private class connectionEstablishingThread extends Thread{
		@Override
		public void run() {
			//TODO - remove. Without this call sending Log messages is not possible 
			Looper.prepare();
			
			Log.d("nxt","ConnectionThread started");
			try {
	            //try to connect to the given device, show a spinning wheel or something. Should run on UI Thread.
				contextActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						BluetoothHandler.fireConnectionProcessStartedEvent(mDevice);
					}
				});
				
				
				Log.d("nxt","ConnectionProcessStartedEvent fired");
				
	            mySocket = mDevice.createRfcommSocketToServiceRecord(uuid);
	            mySocket.connect();

	            
	            //first use the hook, which was inserted into icommand to set the socket
	            icommand.nxt.comm.NXTCommFactory.setSocket(mySocket);
	            
	            //Let the Connection open itself
	            icommand.nxt.comm.NXTCommand.open();
	            
	            //then create a robot representation, if the connection was successfull
	            Motor motorLEFTWHEEL 	= new Motor(config_PORT_MOTOR_LEFTWHEEL);
	            Motor motorRIGHTWHEEL	= new Motor(config_PORT_MOTOR_RIGHTWHEEL);
	            Motor motorARM 			= new Motor(config_PORT_MOTOR_ARM);
	            
	            robot = new TachoNavigator(4.96, 9 , motorLEFTWHEEL, motorRIGHTWHEEL);
	            
	    		lightSensor 		= new LightSensor(new SensorPort(config_PORT_SENSOR_LIGHT));
	    		lightSensor.passivate();
	    		
	    		soundSensor 		= new SoundSensor(new SensorPort(config_PORT_SENSOR_SOUND));
	    		touchSensor			= new TouchSensor(new SensorPort(config_PORT_SENSOR_BUTTON));
	    		ultrasonicSensor	= new UltrasonicSensor(new SensorPort(config_PORT_SENSOR_DISTANCE));
	            
	            //send a success command to the robot, for users to make the connection visible
	            //TODO - blink with the led, instead of moving the arm
	            motorARM.rotate(200);
	            
	            //fire a connection success event, for all GUI to set itself to the disconnected mode. Should run on UI Thread.
				contextActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
			            BluetoothHandler.fireConnectedEvent(mDevice);
					}
				});
	            
	            return;
	            
	        }catch (Exception e) {
	        	//could not connect. let everybody know, because maybe some class still relies on this connection
				Log.d("nxt","Couldnt connect. Run firedisconnect(). Exception occured: "+e.getClass()+" Message: "+e.getMessage());
				Log.d("nxt",Log.getStackTraceString(e));
				
				//fire a disconnection event, for all GUI to set itself to the disconnected mode. Should run on UI Thread.
				contextActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
			            BluetoothHandler.fireDisconnectedEvent(mDevice);
					}
				});

	            return;
			}
		}
	}//end connectionEstablishingThread
	
	

	
	/** responsible for polling the Robot for data. 
	 *  Started when the first {@link LegoNXTSensorEventListener} was added or on establishing a robot connection */
	private class SensorPollingThread extends Thread{
		private boolean isRunning=false;
		
		//isAlive() doesnt work, dont know why
		public synchronized boolean isRunning(){ return isRunning;	}
		
		private void quit(){
			isRunning=false;
		}
		
		@Override
		public void run() {
			isRunning=true;
			while(isRunning){
				if( (BluetoothHandler.getStatus() == ConnectionStatus.CONNECTED) && //stop if there is no connection
						(LegoNXTSensorEventListenerList.toArray().length > 0) && //stop if there are no listeners
							(getRobot() != null)) //stop if there is no robot instantiated
				{
					//retrieve robots data and fire an event
					fireLegoNXTSensorEventListener(new LegoNXTSensorData());
					
					//TODO start testing step motor infos LegoNXT
					
//					Log.d("robot", "getMotorRotationCount "+robot.getMotorBlockTachoCount(LegoNXTHandler.config_PORT_MOTOR_LEFTWHEEL));
//					Log.d("robot", "getMotorRotationCount "+robot.getMotorRotationCount(LegoNXTHandler.config_PORT_MOTOR_LEFTWHEEL));
//					Log.d("robot", "getMotorTachoCount "+robot.getMotorTachoCount(LegoNXTHandler.config_PORT_MOTOR_LEFTWHEEL));
//					Log.d("robot", "getMotorTachoLimit "+robot.getMotorTachoLimit(LegoNXTHandler.config_PORT_MOTOR_LEFTWHEEL));
					
					//TODO stop testing LegoNXT
					
					//wait a little till we ask the next time
					try { Thread.sleep(100);} 
					catch (InterruptedException e) { e.printStackTrace(); }
					catch(ArrayIndexOutOfBoundsException e){
		        		//diablu sometimes returns Index out of bounds exceptions, during sensor reads, ignore them.
						Log.e("MyApplication","Array out of bounds Exception when we were reading NXT sensor data",e);
		        	}
				
				}else{
					quit();
				}
			}
		}
	}//end SensorPollingThread
	
	/** shows empty or false if there is no robot connection yet */
	public class LegoNXTSensorData{
		
//        public int battery=0;
        public boolean button=false;
        public int db=0;
        public int distance=0;
        public int light=0;
        
        LegoNXTSensorData(){
        	try{        
        		//TODO
//	            battery = robot.getBatteryLevel();
//	            button = robot.getButtonState(config_PORT_SENSOR_BUTTON);
//	            db = robot.getDB(config_PORT_SENSOR_SOUND);
//	            distance = robot.getDistance(config_PORT_SENSOR_DISTANCE);
//	            light = robot.getLight(config_PORT_SENSOR_LIGHT);
	            button 		= touchSensor.isPressed();
	            db 			= soundSensor.getdB();
	            distance 	= ultrasonicSensor.getDistance();
	            light 		= lightSensor.getLightPercent();
	        	
	        	Log.d("nxt","----");
//	        	Log.d("nxt","battery "+battery);
	        	Log.d("nxt","button "+button);
	        	Log.d("nxt","db "+db);
	        	Log.d("nxt","distance "+distance);
	        	Log.d("nxt","light "+light);
        	}catch(NullPointerException e){
        		//robot disconnected during the poll or is not connected yet, no problem.
        	}
        }
        
	}//end LegoNXTSensorData

}

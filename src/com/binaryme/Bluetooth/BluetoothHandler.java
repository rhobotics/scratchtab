package com.binaryme.Bluetooth;

import icommand.scratchtab.LegoNXTHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.binaryme.ScratchTab.Config.AppRessources;

/** 
 * 	<p>
 * 	This class is a central place, where connected bluetooth devices of all kinds come together. 
 *  This class doesn't establish connections itself, but passes the connection establishing job to special handlers, which should have registered them selves as {@link InterfaceConnectionRequestListener} by using {@link #addConnectionRequestListener(InterfaceConnectionRequestListener)}.
 *  </p>
 *  
 *  <p>
 *  To establish a connection to a bluetooth device, you have to know this device's class (int deviceClass) e.g. the NXT robot has the deviceClass=2052. Use Method  {@link #requestConnectionTo(BluetoothDevice)} with the deviceClass number.
 *  This class will then pass the deviceClass number to all handlers and if there is a handler for this kind of devices - the {@link InterfaceGeneralConnectionListener#onConnected(BluetoothDevice)} event will be fired for all registered GeneralConnectionListeners  when the connection has been established.
 *  </p>
 *  
 *  <p>
 *  This class is needed, to have the possibility of reacting to all bluetooth events, e.g. by showing a spinning wheel on every connection. 
 *  </p>
 *  */
public class BluetoothHandler{

	private static BluetoothAdapter myBluetoothAdapter;
	private static BluetoothDevice selectedDevice;
	
	public static enum ConnectionStatus {DISCONNECTED, CONNECTING, CONNECTED}
	public static ConnectionStatus status = ConnectionStatus.DISCONNECTED; //updated on connection establishing, disconnecting
	
	private static ArrayList<WeakReference<InterfaceConnectionRequestListener>> ConnectionRequestListenerList = new ArrayList<WeakReference<InterfaceConnectionRequestListener>>();
	private static ArrayList<WeakReference<InterfaceGeneralConnectionListener>> GeneralConnectionListenerList = new ArrayList<WeakReference<InterfaceGeneralConnectionListener>>();
	
	//config
	private static boolean configTryReconnectLastDevice=true;
	private static int configLastDeviceId = 2052;
	
	//init here
	public BluetoothHandler(){
		myBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
		
        /* get default bluetooth adapter */
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
        	AppRessources.popupHandler.pop( "This device does not support bluetooth");
        }else if (!myBluetoothAdapter.isEnabled()) {
        	AppRessources.popupHandler.pop( "Please enable your bluetooth adapter");
		}else if(configTryReconnectLastDevice){
			//try select the last used device
			selectLastConnectedDeviceIfAvailable();
		}
	}

	
//INTERACTION
	public static BluetoothDevice getSelectedDevice(){
		return selectedDevice;
	}
	public static void setSelectedDevice(BluetoothDevice device){
		selectedDevice=device;
	}
	public static ConnectionStatus getStatus(){
		return status;
	}
	/** used by concrete bluetooth device Handlers e.g. LegoNXTHandler */
	public static void addConnectionRequestListener(InterfaceConnectionRequestListener l){
		ConnectionRequestListenerList.add(new WeakReference<InterfaceConnectionRequestListener>(l));
	}
	/** used by general bluetooth connection listeners, e.g. GUI with the spinning wheel */
	public static void addGeneralConnectionListener(InterfaceGeneralConnectionListener l){
		GeneralConnectionListenerList.add(new WeakReference<InterfaceGeneralConnectionListener>(l));
	}
	public static Set<BluetoothDevice> getPairedDevices(){
    	/* query paired devices (no discovery to prevent hijacking of devices) */
		return myBluetoothAdapter.getBondedDevices();
	}
	

	/** a connection method. If the right Device Handler exists, it will establish the connection and
	 *  fire events the {@link #fireConnectionProcessStartedEvent(int)} and {@link #fireConnectedEvent(int)}*/
	public void requestConnectionTo(BluetoothDevice device){
		fireRequestBluetoothDeviceConnection(device, device.getBluetoothClass().getDeviceClass());
	}
	/** disconnect all bluetooth devices of all kind */
	public void requestDisconnectAll(){
		fireRequestBluetoothDeviceDisconnection();
	}
	/** disconnect all bluetooth devices of all kind */
	public void requestDisconnectDevice(BluetoothDevice device){
		fireRequestBluetoothDeviceDisconnection(device);
	}
	
	/** find a device with a predefined device class and connect to the first found device. 
	 * Used, when a special bluetooth handler needs a device connection to the device of it's special class, e.g. {@link LegoNXTHandler} needs connection to a 2052 device */
	public void requestConnectionTo(int deviceClass){
		//check if the selected bluetooth device has the right class
		if((selectedDevice!= null) && (selectedDevice.getBluetoothClass().getDeviceClass() == deviceClass)){
			requestConnectionTo(selectedDevice);
		}
		
		//otherwise search for a device of a given device-class, among the paired devices
		else {
			for( BluetoothDevice device : getPairedDevices()){
				if(device.getBluetoothClass().getDeviceClass() == deviceClass){
					requestConnectionTo(device);
					break;
				}
			}
		}
		
	}
	
	
	
//INTERNAL
	private void selectLastConnectedDeviceIfAvailable(){

		//TODO load name and check it either
		for(BluetoothDevice d:getPairedDevices()){
			if(d.getBluetoothClass().getDeviceClass() == configLastDeviceId ){
				selectedDevice = d;
				requestDisconnectDevice(selectedDevice);
				requestConnectionTo(selectedDevice);
			}
		}
	}


	
//FIRE EVENTS
	//request event: started to let a concrete handler establish the connection
	private static void fireRequestBluetoothDeviceConnection(BluetoothDevice device, int deviceClass){
		if(device == null) return;
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceConnectionRequestListener> r : ConnectionRequestListenerList){
			InterfaceConnectionRequestListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException 
			}else{
				if(selectedDevice!=null){
					listener.onRequestConnection(device, deviceClass);
				}
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		ConnectionRequestListenerList.removeAll(badReferences);
	}
	//request event: started to let all concrete handlers disconnect their devices
	private static void fireRequestBluetoothDeviceDisconnection(){
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceConnectionRequestListener> r : ConnectionRequestListenerList){
			InterfaceConnectionRequestListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException 
			}else{
				listener.onRequestDisconnectionAll();
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		ConnectionRequestListenerList.removeAll(badReferences);
	}
	//request event: started to let a concrete handlers disconnect its device
	private static void fireRequestBluetoothDeviceDisconnection(BluetoothDevice device){
		if(device == null) return;
		status = ConnectionStatus.DISCONNECTED; // update the status here, before anyobdy know about the connection
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceConnectionRequestListener> r : ConnectionRequestListenerList){
			InterfaceConnectionRequestListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException 
			}else{
				listener.onRequestDisconnection(device, device.getBluetoothClass().getDeviceClass());
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		ConnectionRequestListenerList.removeAll(badReferences);
	}
	//public general connection event: used by handler
	public static void fireConnectionProcessStartedEvent(BluetoothDevice device){
		if(device == null) return;
		status = ConnectionStatus.CONNECTING; // update the status here, before anyobdy know about the connection
		BluetoothHandler.selectedDevice = device;
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceGeneralConnectionListener> r : GeneralConnectionListenerList){
			InterfaceGeneralConnectionListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException
			}else{
				listener.onConnectionProcessStarted(device);
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		GeneralConnectionListenerList.removeAll(badReferences);
		
		AppRessources.popupHandler.pop("Trying to establish a bluetooth connection to "+device.getName());	//let the GUI display a message
	}
	//public general connection event: used by handler
	public static void fireConnectedEvent(BluetoothDevice device){
		if(device == null) return;
		status = ConnectionStatus.CONNECTED; // update the status here, before anyobdy know about the connection
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceGeneralConnectionListener> r : GeneralConnectionListenerList){
			InterfaceGeneralConnectionListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException
			}else{
				listener.onConnected(device);
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		GeneralConnectionListenerList.removeAll(badReferences);
		
		AppRessources.popupHandler.pop("Bluetooth connection established to "+device.getName());	//let the GUI display a message
	}
	//public general connection event: used by handler
	public static void fireDisconnectedEvent(BluetoothDevice device){
		if(device == null) return;
		ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
		for(WeakReference<InterfaceGeneralConnectionListener> r : GeneralConnectionListenerList){
			InterfaceGeneralConnectionListener listener = r.get();
			if(listener==null){ 
				badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException
			}else{
				listener.onDisconnected(device);
			}
		}
		//Now when iterating ArrayList is over remove bad references from it
		GeneralConnectionListenerList.removeAll(badReferences);
		
		AppRessources.popupHandler.pop("Closed the bluetooth connection with "+device.getName());	//let the GUI display a message
	}
	
}

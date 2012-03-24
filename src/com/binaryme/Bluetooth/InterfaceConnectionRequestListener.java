package com.binaryme.Bluetooth;

import android.bluetooth.BluetoothDevice;

/** Interface should be implemented by a class, which wants to know about general bluetooth connection events. 
 *  At this stage it is not important, which kind of device is connected disconnected.
 *  If a class wants to know about specific bluetooth device connection events - implement a more specific interface, e.g. LegoNXTConnectionListener */
public interface InterfaceConnectionRequestListener {
	
	/** called when the system requests a connection to a bluetooth device. The connection should happen inside this method. 
	 * The connection should be implemented by a specific handler, which handles the specific device. 
	 * The specific handler will recognize it's device by the deviceClass and do the connection */
	public void onRequestConnection(BluetoothDevice device, int deviceClass);
	
	/** called when the system requests a disconnection to a bluetooth device of a given class, e.g. to connect to another device */
	public void onRequestDisconnection(BluetoothDevice device, int deviceClass);
	
	/** called by the general connection manager, to let more special connection manager close their connections */
	public void onRequestDisconnectionAll();
	
	
}

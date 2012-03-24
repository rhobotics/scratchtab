package com.binaryme.Bluetooth;

import android.bluetooth.BluetoothDevice;

public interface InterfaceGeneralConnectionListener {
	public void onConnected(BluetoothDevice device);
	public void onConnectionProcessStarted(BluetoothDevice device);
	public void onDisconnected(BluetoothDevice device);
}

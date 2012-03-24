package com.binaryme.ScratchTab.Config;

import icommand.scratchtab.LegoNXTHandler;
import android.app.Activity;

import com.binaryme.AndroidSensors.AndroidSensorsHandler;
import com.binaryme.Bluetooth.BluetoothHandler;
import com.binaryme.ScratchTab.InterfaceStaticInitializable;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Gui.BlockPane;
import com.binaryme.ScratchTab.Gui.PopupHandler;
import com.binaryme.ScratchTab.Sound.SoundHandler;

/** Class puts all resources together, which should be shared inside the application */
public class AppRessources implements InterfaceStaticInitializable {

	public static Activity context;
	public static BluetoothHandler bluetoothHandler;
	public static AndroidSensorsHandler androidSensorsHandler;
	public static LegoNXTHandler legoNXTHandler;  
	public static PopupHandler popupHandler;
	public static BlockPane blockPane;
	public static SoundHandler soundHandler;
	
	
	@Override
	public void onApplicationStart(Activity appcontext) {
		context = appcontext;

		//responsible for displaying information. Should be initialized first to make it possible to use it in other initializations
		popupHandler = new PopupHandler(context);
		
		//handles requests bluetooth connection establishment by the bluetooth device handlers
		bluetoothHandler = new BluetoothHandler();
		
		//handles all the android sensor data listeners
		androidSensorsHandler = new AndroidSensorsHandler();

		//handler manages the lego NXT robot, needs to know about requests about the nxt robot, which are send by the bluetoothHandler
		legoNXTHandler = new LegoNXTHandler(context);
		
		blockPane = ((BlockPane) context.findViewById(R.id.theBlockPanel)).init(appcontext);
		
		//kann make beep sounds
		soundHandler = new SoundHandler();
	}  
	
}

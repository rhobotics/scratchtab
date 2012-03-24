package icommand.scratchtab;

import icommand.scratchtab.LegoNXTHandler.LegoNXTSensorData;

import com.binaryme.ScratchTab.Config.AppRessources;


/** Everyone who needs to know about robot's sensor data - register here. The data renew regularly.
 *  LegoNXTSensorEventListeners should register them selves at {@link AppRessources#legoNXTHandler} by using {@link LegoNXTHandler#addLegoNXTSensorEventListener(LegoNXTSensorEventListener)}
 *  */
public interface LegoNXTSensorEventListener {
	public void OnNewSensorData(LegoNXTSensorData data);
}

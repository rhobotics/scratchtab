package com.binaryme.ScratchTab.Events;

public interface InterfaceMessageEventReceiver {
	
	/** Executed by the {@link HeadMessageEventHandler} on every registered {@link InterfaceMessageEventReceiver}, when a new message event is triggered.  */
	public void onMessageEvent(String message);
}

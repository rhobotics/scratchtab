package com.binaryme.ScratchTab;

import android.util.Log;

/** Method to handle system exceptions, which can not be catched or patched in other way. */
public class ScratchTabDefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler  {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if( (ex instanceof java.lang.IllegalStateException) && (ex.getMessage().equals("reportDropResult() by non-recipient")) ){
			Log.d("MyApplication", "getCause" + ex.getCause());
			Log.d("MyApplication", "uncaughtException" + ex.getClass());
			Log.d("MyApplication", "uncaughtException" + ex.getLocalizedMessage());
			Log.d("MyApplication", "uncaughtException" + ex.getMessage());
			//throw a new exception to prevent android from exiting on the IllegalStateException
		}
		
	}

}

package com.binaryme.ScratchTab;

import android.app.Activity;

public interface InterfaceStaticInitializable {
	
	/** If a method, which is a library with static methods only, needs information at runtime - it should implement this interface 
	 * AND   !IMPORTANT! Its Name should be present in StaticInitializer's {@link StaticInitializer#initializables} list !IMPORTANT! */
	public void onApplicationStart(Activity context);
}

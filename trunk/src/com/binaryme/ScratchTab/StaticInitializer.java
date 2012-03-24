package com.binaryme.ScratchTab;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;

/** This class contains the list of classes which mainly contain STATIC properties, which need to be initialized once on Application start.
 *  It creates an instance of each class in the list and calls it's method onApplicationStart() which should contain the initialization logic. 
 *  */
public class StaticInitializer {
	
	//List with the static class, which need initialization and implement InterfaceStaticInitializer 
	protected static String[] initializables = {
			"com.binaryme.tools.ColorPalette",
			"com.binaryme.DragDrop.DragHandler",
			"com.binaryme.ScratchTab.Config.ConfigHandler",
			"com.binaryme.ScratchTab.Config.AppRessources"
	};
	
	
	/** CONTRACT: call this method from the Application controller */
	public static void startStaticInitialization(Activity contextactivity){
		for(String initializable:StaticInitializer.initializables){
			
			//get the class by its name
			try {
				Class<?> c = Class.forName(initializable);				//get an Object of Type "Class" by full name
				Class<?>[] argTypes = new Class[] { Activity.class }; 	//arguments for instantiation of the class

				//find the method to call with the arguments: argTypes 
				Method onApplicationStart = c.getDeclaredMethod("onApplicationStart", argTypes);

				//create an instance, on which we can start the method
				InterfaceStaticInitializable interf = (InterfaceStaticInitializable)c.newInstance();
			
				//invoke the method on a InterfaceStaticInitializable instance
				onApplicationStart.invoke(interf, new Object[] {contextactivity});
					
			}catch (InvocationTargetException e) {
				throw new InternalError("The initialization of a static method has failed with error: "+e+"\n"+
						"the Target Exception is:"+e.getTargetException());										
			}catch (Exception e) {
				throw new InternalError("The initialization of a static method has failed with error: "+e);
			}
		
		}
	}
}

package com.binaryme.ScratchTab.Events;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;

public class HeadStartTokenHandler {

	
	/** put all the head blocks which should be started on "go" click into this list.  */
	private static LinkedList<WeakReference<Executable>> goHeadBlocks= new LinkedList<WeakReference<Executable>>();
	

	//INTERACTION
		/** Head blocks, which are executed on start token touch will register themselves using this method. */
		public static synchronized void registerStartTokenHead(Executable exec){
			goHeadBlocks.add(new WeakReference<Executable>(exec));
		}
		/** Method executes all registered start-token-heads. Method is used when a start token is touched. */
		public static void fireExecutionOfStartTokenHeads(){
			ArrayList<WeakReference<?>> badReferences = new ArrayList<WeakReference<?>>();
			for(WeakReference<Executable> r : goHeadBlocks){
				Executable exec = r.get();
				if(exec==null){ 
					badReferences.add(r);					//remember bad reference, can't remove from ArrayList while iterating - ConcurrentModificationException 
				}else{
					exec.executeForValue(new ExecutionHandler(exec,AppRessources.context));
				}
			}
			//Now when iterating ArrayList is over remove bad references from it
			goHeadBlocks.removeAll(badReferences);
		}
	
}

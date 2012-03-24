package com.binaryme.ScratchTab.Exec;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Slots.Slot;

/** This class encapsulates the method run(Executable), 
 *  which knows to use the data inside of the Executable, e.g. to start a new thread, 
 *  to handle exceptions, happened during the execution,
 *  to change the background color of the executed block. 
 *  
 *  Another job of this Class is to enable and disable the signals feedback signals,
 *  which are needed to demonstrate the execution state to the user.
 *  Every Executable implements it's own feedback signals in the methods  
 *   
 *  @param <E> - the recursive Execution of an executable may return a result. Here you can pass the type of the returned result. E.g. executing a SlotDataNum should return a double.
 *  */
public class ExecutionHandler<E extends Object> extends Thread {
	
	/** when an ExecutionHandler is created - the first Executable which was passed to the constructor will be saved here */
	private final Executable<E> rootExecutable; 
	
	/** here the result from the execution of the rootExecutable are saved */
	private E result = null;
	
	Activity guiThreadActivity=null;
	
	private boolean isInterrupted=false;
	

	/** 
	 * Executables from the current stack.
	 * Here the Executables are pushed, which's execute Method is currently in process. 
	 * Every time, when the {@link ExecutionHandler#execute(Executable)} method is started on an executable X, the executable X is pushed on this List.
	 * Every time, when the execution of an executable X successfully ends, the executable X is popped from the list.
	 * If an exception occurs, the last executable in the row is blamed and it's feedback feedbackExecutionError is triggered.
	 */
	LinkedList<Executable<?>> executablesInProcess= new LinkedList<Executable<?>>();
	/** like executablesInProcess, but contains ALL running executables from ALL stacks (don't miss the "static" buzzword) */
	static ConcurrentLinkedQueue<Executable<?>> allExecutablesInProcess= new ConcurrentLinkedQueue<Executable<?>>();
	
	
	/** mapping: root executables, where the execution began to their execution threads. This HashMap gives us the possibility, to kill the thread by Executable.
	 * HashTable is synchronized on default in Java implementation. */
	static Hashtable<Block<?>, ExecutionHandler<?>> rootBlockToThread = new Hashtable<Block<?>, ExecutionHandler<?>>(); 
	/** all ExecutionHandler Threads. use this list if you need to stop all ExecutionHandlers */
	static ConcurrentLinkedQueue<ExecutionHandler<?>> allThreads = new ConcurrentLinkedQueue<ExecutionHandler<?>>();
	
	
	
	
	/** This constructor will be used to start the execution of a ScratchTab stack.
	 *  To start the execution of an Executable and it's successors just create an ExecutionHandler.
	 *  The GUI Thread will create the ExecutionHander as answer to an event, which is defined to start the stack execution, e.g. double tap. */
	public ExecutionHandler(final Executable<E> executable, Activity guiThreadActivity){
		//save the root executable
		rootExecutable=executable;
		
		//check if already running
		if(allExecutablesInProcess.contains(executable)){
			return;
		}
		
		//remember the gui thread activity to run UI operations on it's thread
		this.guiThreadActivity=guiThreadActivity;
		//run feedback methods on the UI thread
		this.guiThreadActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				//first reset the stack of executables, so their signalization is set back, if the stack was executed earlier and failed
				rootExecutable.feedbackDisable();
				if(executable instanceof ViewGroup){recursivelyResetExecutables((ViewGroup)rootExecutable);}
			}
		});

		//add myself to the ExecutionHandler list, so that this Thread can be interrupted if needed (e.g. on stop Button touch)
		allThreads.add(this);
		
		//find the root block for the current executable and add it to the mapping. Executable can be relatively safe casted to Block, because executable Interface is only implemented by blocks.
		Block<?> stackRoot = ((Block<?>)executable).findRootBlock();
		ExecutionHandler.rootBlockToThread.put(stackRoot, this);
		
		//now start this new thread to execute the app logic for the given rootExecutable and it's stack - will execute this method's run() method
		this.start();
	}
	
	
	/** This methods is executed by this.start().
	 *  It creates the new Thread and starts executing the {@link ExecutionHandler#rootExecutable} */
	@Override
	public void run() {
		//execute the logic for each executable in the stack now
		//start the execution chain, Executables will start another executables by using the same method ExecutionHandler.execute(Executable)
		this.result = this.executeExecutable(rootExecutable);
		
		//clean up the current thread from the global lists
		clearExecutionHandlerPointers(this);
	}
	
	
	/** This is the most important execution method, 
	 * <ol>
	 * 	<li>it looks if an Executable wants to be executed in an own local thread, and runs a local thread, waits for results and updates the GUI
	 * 		EDIT: is this useful to run local Threads and wait for them? No it's not...
	 * 	<li>it manages occurred Exceptions and feedback.
	 * </ol>
	 * 
	 * @param executable - which this object will execute as next, additionally to all the others in the list {@link ExecutionHandler#executablesInProcess}}
	 * @return - whatever the executable wants us to return
	 */
	public <T> T executeExecutable(final Executable<T> executable){
		
		//push the current onto the Executables stack, if an error occurs, we will take this last Executable from stack and let it signalize an error
		this.executablesInProcess.push(executable);
		//add theexecutable to the global stack
		allExecutablesInProcess.add(executable);
		
		//run feedback methods on the UI thread, give feedback about Executable's state 
		this.guiThreadActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				//give feedback about Executable's state 
				executable.feedbackExecutionProcessRunning();
			}
		});

		//the default executable result, which is used when an Exception occurs, is null
		T executableResult = null;
		try{
			//if the Handler is interrupted - do not move on
			if(isInterrupted()){
				throw new InterruptedException();
			}
			
			//remember the start time
			long startedAt =System.currentTimeMillis();
			
			//APP LOGIC EXECUTION HERE: Recursive call to ExecutionHandler.executeExecutable may be embedded in Executable.executeLogic, if it needs to run other executables - it should do so by using this method: executeExecutable, so that it can handle exceptions
			executableResult = executable.executeForValue(this);
			
			//make a brake with respect to the minimum execution time, if needed
			long endedAt =System.currentTimeMillis();
			long timegap = endedAt-startedAt;
			//do not try to sleep, if theThread is allready interrupted. You will get an InterruptedException
			if(ConfigHandler.minimalExecutionTime>timegap){
				sleep(ConfigHandler.minimalExecutionTime-timegap);
			}
			
			//take it from the executables queue
			executablesInProcess.poll(); //remove the top element from queue, without throwing the NoSuchElementException
			//remove the executable from the global set, when the whole scratchblock stack is executed
			allExecutablesInProcess.remove(executable);
			
			//run feedback methods on the UI thread, if we are here, than no Exception occurred during the execution - yuppiaieee!
			this.guiThreadActivity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					//if we are here, than no Exception occurred during the execution - yuppiaieee!
					executable.feedbackDisable();
				}
			});
			
			//TODO testvariable for debugging - del isinterrupted
			boolean isinterrupted = executable.isInterrupted();
			
			if(!executable.isInterrupted()){
				//now start the Successor execution. No one will use its result, so we can Cast the slot to Executable<?> with a ? as parameter and return type.
				//before returning some value the successor slot's value. Use our special executeExecutable(Slot) method, which does all the checks
				executeExecutable( executable.getSuccessorSlot() );
			}else{
				interrupt();
			}
			
		}catch(Exception e){

			//do something against the Exception
			if(e instanceof InterruptedException){
				//sometimes occurs, when the executable is interrupted
				//it is ok, do not have to handle this exception
			}else{
				//interrupt the thread on unknown exception
				interrupt();
			
				//remove the executable from the global Set
				clearExecutionHandlerPointers(this);
				
				Log.e("MyApplication","Exeption during execution",e);
				
				//run feedback methods on the UI thread, if we are here, than no Exception occurred during the execution - yuppiaieee!
				this.guiThreadActivity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						//if we are here, than an Exception has occurred during the execution!
						executable.feedbackExecutionError();
					}
				});
			}
		}//end of catch()
		
		//and now return the result, which we obtained after the execution of the Executable and let the thread run out ....
		return (T) executableResult;
	}

	
	/** This is the second most important execution method :)
	 *  This method will be called by an Executable, when it is ready with whatever it was doing and wants to pass the execution process to the next executable
	 *  This method will check do all the necessary checks about the Slot, if it's empty, if it contains an executable...
	 * 
	 * @param executable
	 * @return
	 */
	public <T> T executeExecutable(Slot<?,T> slotContainsNextExecutable){
		//if slot doesn't exist - return
		if(slotContainsNextExecutable == null) return null;
		else{
			//otherwise try to execute the slot as next executable
			Executable<T> nextExecutable = (Executable<T>)slotContainsNextExecutable;
			return this.executeExecutable(nextExecutable);
		}
	}

	
	/** used to stop a concrete given thread */
	public static void stopExecutionHandler(final ExecutionHandler<?> execHandler){
		//first interrupt the ExecutionHandler, which is a thread
		execHandler.interrupt();
		
		//and now let every executable, which was executed on this thread disable the "i'm running" signal
		AppRessources.context.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				for( Executable<?> exec : execHandler.executablesInProcess){
					exec.feedbackDisable();
				}
			}
		});
		clearExecutionHandlerPointers(execHandler);
	}
	/** used to stop an execution stack by one concrete executable. For that the executable is handled as a block, and
	 *  <ol>
	 *  	<li> the root block of the given execution is found.
	 *  	<li> through the mapping block-thread the right thread is found, and stopped 
	 *  </ol>
	 *   */
	public static void stopExecutableTree(ExecutableDraggableBlockWithSlots<?,?> executableBlock){
		//handle the executable as a block, to retrieve its root
		Block root = executableBlock.findRootBlock();
		//use the block-thread mapping
		ExecutionHandler<?> execHandler = rootBlockToThread.get(root);
		
		//delete the root from the hashMap
		rootBlockToThread.remove(root);

		//if such a block was found in the Block-Thread HashMap, stop the matching thread  
		if(execHandler!=null){
			stopExecutionHandler(execHandler);
		}
	}
	/** used to stop all ExecutionHandlers which are currently executed. */
	public static void stopEverything(){
		for(final ExecutionHandler<?> execHandler:allThreads){
			//interrupt the stack execution, so that it wont be further executed 
			stopExecutionHandler(execHandler);
		}
		//Now when all ExecutionHandlers should already be empty, but for sure - clear all global lists explicitly
		allThreads.clear(); 
		allExecutablesInProcess.clear();
		rootBlockToThread.clear();
	}


	
	/** If execution of the root executable has given any Results - if will be returned here. */
	public E getResult(){
		return this.result;
	}
	
	
//OWN interrupt FLAG
	@Override
	synchronized public void interrupt() {
		this.isInterrupted=true;
	}
	@Override
	synchronized public boolean isInterrupted() {
		return this.isInterrupted;
	}

//HELPER
	/** Checks every child in the hierarchy the {@link Executable#feedbackDisable()} for the Executable and all it's successors.
	 *  Successors are successor executables, as  */
	private void recursivelyResetExecutables(ViewGroup viewGroup){
		int cnt = viewGroup.getChildCount();
		for(int i=0; i<cnt; i++){
			View child = viewGroup.getChildAt(i);
			Log.d("reset", ""+child.getClass());
			//reset the children
			if(child instanceof Executable){ ((Executable)child).feedbackDisable(); }
			//recursion
			if(child instanceof ViewGroup){ recursivelyResetExecutables((ViewGroup)child); }
		}
	}
	/** method to clear all the local thread pointers to executables and the executionHandler pointers from all static lists  */
	private static void clearExecutionHandlerPointers(ExecutionHandler<?> execHandler){
		
		//now iterate the executables on the given ExecutionHandler Thread again, now not on the UI thread and delete them from global lists
		for( Executable<?> exec : execHandler.executablesInProcess){
			allExecutablesInProcess.remove(exec); 
		}
		
		//empty the local lists to release the objects
		execHandler.executablesInProcess.clear();
		
		//and remove the ExecutionHandler from the global list
		allThreads.remove(execHandler);
	}
	
	

}

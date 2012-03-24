package com.binaryme.ScratchTab.Exec;

import com.binaryme.ScratchTab.Gui.Blocks.DraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Blocks.ExecutableDraggableBlockWithSlots;
import com.binaryme.ScratchTab.Gui.Shapes.ShapeWithSlots;
import com.binaryme.ScratchTab.Gui.Slots.Slot;

/** Interface defines methods, which are used to define commands for executable blocks.
 *  Interface is implemented by {@link ExecutableDraggableBlockWithSlots} 
 *  @param <T> is the type, which the current object will return after execution.*/
public interface Executable<T extends Object> {
	
	/** 
	 * This Interface is used, to implement the application logic, which concrete Blocks should represent. 
	 * If necessary (e.g if the semantical calculation is a mathematical operation with a result) the result should be returned as an object.
	 * The result will be used by wrapping blocks. Because the wrapping blocks only accept predefined block types in their slots - 
	 * the result returned by blocks in these slots can be safely casted from object to the type, which those predefined blocks are awaited to return.
	 * 
	 * {@link DraggableBlockWithSlots} and {@link Slot} will implement this interface, 
	 * since DraggableBlockWithSlots is the root class of all executable blocks with embedded semantics
	 * and Slot is the class, which will maintain those executable blocks, so Slots just call execute on their infill.
	 * 
	 * <ol>
	 *  <li> Choose the Slot, which's infill should be executed next by using {@link #setSuccessorSlot(Slot)}
	 *  <li> Define the applications logic in this method. 
	 *  		If you need to execute nested Executables for results - find the needed Sot and call execute Slot.executeForValue(executionHandler).
	 *  		The Slot will use the execution Handler to run the Object in the Slot inside of the executionHandler Slot.
	 *  		The ExecutionHandler will trigger feedback on executables, and stop the Thread if necessary.
	 *  <li> <b>DO NOT</b> call executeForValue on other Executables except of the Slots - only Threads use the ExecutionHandler correctly to start an execution handled by the ExecutionHandler.
	 * </ol>
	 * */
	abstract T executeForValue(ExecutionHandler<?> executionHandler);
	
	
	
	/** Determines which Slot will be executed as next, it the current executable is ready.
	 * <ol>
	 * 	<li> Retrieve the Blocks {@link ShapeWithSlots} by using method {@link ExecutableDraggableBlockWithSlots#getShape()}
	 *  <li> Retrieve the needed Slot from the ShapeWithSlots by using Method {@link ShapeWithSlots#getSlot(int)}. 
	 *  	 Use current Shapes's predefined constants to pass them to {@link ShapeWithSlots#getSlot(int)}
	 *  <li> Pass the Slot to this method. 
	 * </ol>
	 *  */
	public Slot getSuccessorSlot();
	
	
	/** Determines which Slot will be executed as next, it the current executable is ready.
	 *  If execution of an executable is running and the next Executable in the stack should not be executed - run this method.
	 *  */
	public void interruptExecution();
	public Boolean isInterrupted();
	
	
//	/**
//	 * If this method returns true - the executable will run it's command in an own thread.
//	 * All commands, which are not basic like mathematical operations, should run in an own thread (e.g. remote communicaton etc.)  
//	 * 
//	 * <ol>
//	 * <li>Use method {@link #setMaxExecutionTime(int)} to determine the maximum time for the execution thread.</b>
//	 * <li>Return true, if you want the semantics defined in {@link #execute()} to be executed in an own thread
//	 * </ol>
//	 */
//	abstract boolean runExecutableInOwnThread();
//	
//	/** sets the maximal time, after which the thread with the executable should be stopped. 
//	 * This method matters only, if {@link #runExecutableInOwnThread()} returns true. 
//	 * Use this method in {@link #runExecutableInOwnThread()} */
//	public void setMaxExecutionTime(int milliseconds);
	
	
	/** Method is started, when an Error happens during the execution.
	 *  Signalize the Error to the user, e.g. by changing blocks body color. */
	public void feedbackExecutionError();

	/** Method is started, when the execution process for the current Executable is running, 
	 *  Signalize the process to the user, e.g. by making the block color white or by making the block color lighter. */
	public void feedbackExecutionProcessRunning();
	
	/** Method is started, when the signalization should be undone, e.g. when the executable is re- executed again, and feedback signals should be reset. 
	 *  Reverse all the previous signals here. */
	public void feedbackDisable();
}

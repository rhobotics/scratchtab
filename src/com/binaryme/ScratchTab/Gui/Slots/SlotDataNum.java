package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Exec.WrongDataException;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyDataNum;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceNumDataContainer;
import com.binaryme.ScratchTab.Gui.Widgets.MNumField;

public class SlotDataNum extends Slot<BlockSlotDummyDataNum, Double> {
	
	public SlotDataNum(Activity context) {
		super(context);
	}

	@Override
	public BlockType getType() {
		return BlockType.DATA;
	}

	@Override
	protected BlockSlotDummyDataNum initiateBlockSlotDummy() {
		return new BlockSlotDummyDataNum(getContextActivity(), new MNumField(getContextActivity()));
	}
	
	public MNumField getNumField(){
		return getBlockSlotDummy().getWidget();
	}
	
	@Override
	public Double executeForValue(ExecutionHandler<?> executionHandler) {
		if(this.isEmpty() || !(getInfill() instanceof Executable ) ){
			return executionHandler.executeExecutable( this.getBlockSlotDummy() );
		}else{
			/*
			 * what happens here is the recursive usage of the ExecutionHandler.
			 * ExecutionHandler.executeExecutable(Executable) and calls Executable.executeForValue() and returns, what the Executable.executeForValue returns.
			 * 
			 * If it would do nothing more - ExecutionHandler.executeExecutable(Executable) would be useless, because then we could call Executable.executeForValue directly.
			 * ExecutionHandler.executeExecutable(Executable) is not useless, because it handles errors and makes a break between executions of each Executable, to respect the minimal execution speed. 
			 */

			//the infill in the current slot - is of BlockType DATA. This means it will return wether a Double or a String after the execution.
			//check here, what it did return. Throw an exception, it the returned type is wron in this case.
			Executable<?> exec = (Executable<?>)getInfill();
			Object executionResult = executionHandler.executeExecutable(exec);
			
			//here we only accept the execution of type double. It we have a String here - throw an exception, which will be handled by the ExecutionHandler
			try{
				return (Double)executionResult;
			}catch(Exception e){
				throw new WrongDataException("Can not treat this slots content as a number");
			}
		}
	}
	
	
//HELPER
	/** ATTENTION: 
	 *  may throw a casting exception, if a block, which is dropped into this slot do not implement the Interface InterfaceNumDataContainer.
	 *  This is a desired behavior. This error should occur during the execution. It should be catched and handled by the ExecutionHandler, by displaying it to the user in a suitable manner,
	 *  e.g. by highlighting the block, where the error occurred.  
	 *    */
	private InterfaceNumDataContainer getContainer(){
		try{
			return ((InterfaceNumDataContainer)this.getInfill());
		}catch(Exception e){
			throw new InternalError("The block input can not no be treated as a number.");
		}
	}

}

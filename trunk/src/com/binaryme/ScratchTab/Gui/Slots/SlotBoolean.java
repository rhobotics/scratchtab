package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Exec.WrongDataException;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyBoolean;

public class SlotBoolean extends Slot<BlockSlotDummyBoolean, Boolean> {

	public SlotBoolean(Activity context) {
		super(context);
	}

	@Override
	public BlockType getType() {
		return BlockType.BOOLEAN;
	}

	
	@Override
	protected BlockSlotDummyBoolean initiateBlockSlotDummy() {
		return new BlockSlotDummyBoolean(getContextActivity());
	}
	
//TODO delete
//	@Override
//	public Boolean executeForValue(ExecutionHandler executionHandler) {
//		boolean result=this.getBlockSlotDummy().getValue();
//		if(!isEmpty() &&   (this.getInfill() instanceof ExecutableDraggableBlockWithSlots ) ){
//			result = ((ExecutableDraggableBlockWithSlots<ShapeWithSlots, Boolean>)this.getInfill()).executeForValue(executionHandler);
//		}
//		return new Boolean(result);
//	}
	
	@Override
	public Boolean executeForValue(ExecutionHandler<?> executionHandler) {
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
				return (Boolean)executionResult;
			}catch(Exception e){
				throw new WrongDataException("Can not treat this slots content as true or false.");
			}
		}
	}

	@Override
	public void feedbackExecutionError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void feedbackExecutionProcessRunning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void feedbackDisable() {
		// TODO Auto-generated method stub
		
	}

}

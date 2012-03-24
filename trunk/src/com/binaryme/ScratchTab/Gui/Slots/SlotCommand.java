package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyCommand;

public class SlotCommand extends Slot<BlockSlotDummyCommand, Object> {
	
	public SlotCommand(Activity context) {
		super(context);
	}

	@Override
	public BlockType getType() {
		return BlockType.COMMAND;
	}

	@Override
	protected BlockSlotDummyCommand initiateBlockSlotDummy() {
		return new BlockSlotDummyCommand(getContextActivity());
	}

	
	@Override
	public Object executeForValue(ExecutionHandler<?> executionHandler) {
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
			
			//here we accept every object, because the command slot does not really have any predefined values to return
			return executionResult;
		}
	}
	
	
	//TODO: override onDrag, for the SlotCommand in FILLED-mode to react, on ACTION_DRAG_ENTER/ACTION_DRAG_DROP depending on the hover coordinates.
	//	ACTION_DRAG_ENTER -> drawing decorations above the slot infill
	//	ACTION_DRAG_DROP  -> rearranging parent/child, adding them in each other
}

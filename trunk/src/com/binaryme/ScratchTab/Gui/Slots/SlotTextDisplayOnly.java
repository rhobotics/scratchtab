package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Exec.WrongDataException;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyTextDisplayOnly;
import com.binaryme.ScratchTab.Gui.Widgets.MTextDisplayOnly;


public class SlotTextDisplayOnly extends Slot<BlockSlotDummyTextDisplayOnly, String> {
	
	public SlotTextDisplayOnly(Activity context) {
		super(context);
	}

	@Override
	public BlockType getType() {
		//DisplayOnly Text should not be replaceable by any draggable block. Returning null here makes the current slot reject every block of every BlockType.
		return null;
	}

	@Override
	protected BlockSlotDummyTextDisplayOnly initiateBlockSlotDummy() {
		return new BlockSlotDummyTextDisplayOnly(getContextActivity(), new MTextDisplayOnly(getContextActivity()));
	}
	
	//because this slot will not accept any BlockType, getInfill allways returns null
	@Override
	public BlockSlotDummyTextDisplayOnly getInfill() {
		return (BlockSlotDummyTextDisplayOnly) super.getInfill();
	}
	
	public void setValue(String str) {
		this.getInfill().getWidget().setText(str);
	}
	
	/** Because the SlotTextDisplayOnly will never have any content in it - the data can be retrieved directly from the Dummy. */
	@Override
	public String executeForValue(ExecutionHandler<?> executionHandler) {
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
				return (String)executionResult;
			}catch(Exception e){
				throw new WrongDataException("Can not treat this slots content as text.");
			}
		}
	}
}

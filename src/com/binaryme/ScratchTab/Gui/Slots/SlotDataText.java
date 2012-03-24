package com.binaryme.ScratchTab.Gui.Slots;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Exec.WrongDataException;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyDataText;
import com.binaryme.ScratchTab.Gui.Datainterfaces.InterfaceStringDataContainer;
import com.binaryme.ScratchTab.Gui.Widgets.MTextField;
import com.binaryme.tools.M;


public class SlotDataText extends Slot<BlockSlotDummyDataText, String> {
	
	public SlotDataText(Activity context) {
		super(context);
	}

	@Override
	public BlockType getType() {
		return BlockType.DATA;
	}

	@Override
	protected BlockSlotDummyDataText initiateBlockSlotDummy() {
		return new BlockSlotDummyDataText(getContextActivity(), new MTextField(getContextActivity()));
	}

	@Override
	public String executeForValue(ExecutionHandler<?> executionHandler) {
		if(this.isEmpty() || !(getInfill() instanceof Executable ) ){
			return this.getBlockSlotDummy().getValue();
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
			
			//here we accept double or String, but double should be converted first
			String result = "";
			if(executionResult instanceof Double){
				result = M.parseDouble((Double)executionResult);
			}else if(executionResult instanceof String){
				result = (String) executionResult;
			}else{
				throw new WrongDataException("Can not treat this slots content as a text.");	
			}
			return result;
		}
	}
	
	
//HELPER
	/** ATTENTION: 
	 *  may throw a casting exception, if a block, which is dropped into this slot do not implement the Interface InterfaceNumDataContainer.
	 *  This is a desired behavior. This error should occur during the execution. It should be catched and handled by the ExecutionHandler, by displaying it to the user in a suitable manner,
	 *  e.g. by highlighting the block, where the error occurred.  
	 *    */
	private InterfaceStringDataContainer getContainer(){
		try{
			return ((InterfaceStringDataContainer)this.getInfill());
		}catch(Exception e){
			throw new InternalError("The block input cannot no be treated as text.");
		}
	}
	
}

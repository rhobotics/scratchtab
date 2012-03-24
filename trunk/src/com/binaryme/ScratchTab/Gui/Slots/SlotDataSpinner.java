package com.binaryme.ScratchTab.Gui.Slots;

import java.util.ArrayList;

import android.app.Activity;

import com.binaryme.ScratchTab.Exec.Executable;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Dummies.BlockSlotDummyDataSpinner;
import com.binaryme.ScratchTab.Gui.Widgets.MSpinner;


public class SlotDataSpinner extends Slot<BlockSlotDummyDataSpinner, String> {
	
	private MSpinner mSpinner;
	
	public SlotDataSpinner(Activity context, ArrayList<String> spinnerArray) {
		super(context);
		this.mSpinner.setAdapter(spinnerArray);
	}

	@Override
	public BlockType getType() {
		//Spinner should not be replaceable by any draggable block. Returning null here makes the current slot reject every block of every BlockType.		
		return null;
	}

	@Override
	protected BlockSlotDummyDataSpinner initiateBlockSlotDummy() {
		/**
		 * There is no chance to initiate the mSpinnerContentArray before this place,
		 * because super(Context) in this method constructor is called first, before everything else is done and 
		 * 
		 *  the super(Context) constructor calls the method initiateBlockSlotDummy() implicitly, 
		 *  which uses mSpinnerContentArray
		 *  so there is no chance to pass the  right spinnerArray to the BlockSlotDummyData to avoid a nullPointerException.
		 *  
		 *  As a solution we create a BlockSlotDummy with an empty SpinnerContentArray, and pass the right SpinnerContentArray later, 
		 *  by using MSpinnersetAdapter(spinnerArray)
		 */
		ArrayList<String> mSpinnerContentArray=new ArrayList<String>();
		mSpinnerContentArray.add("empty");
		this.mSpinner = new MSpinner(getContextActivity(), mSpinnerContentArray);
		return new BlockSlotDummyDataSpinner(getContextActivity(), mSpinner);
	}

	@Override
	public BlockSlotDummyDataSpinner getInfill() {
		return getBlockSlotDummy();
	}
	
	public MSpinner getSpinner(){
		return getBlockSlotDummy().getWidget();
	}
	
	/** Because the SlotDataSpinner will never have any content in it - the data can be retrieved directly from the Dummy. */
	@Override
	public String executeForValue(ExecutionHandler<?> executionHandler) {
		return executionHandler.executeExecutable( this.getBlockSlotDummy() );
	}
}

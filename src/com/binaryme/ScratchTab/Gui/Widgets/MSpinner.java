package com.binaryme.ScratchTab.Gui.Widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Gui.Blocks.Block;

/** Class to represent a dropdown.
 *  Instances of this class do not position themselves, instead they are embedded into Dummies which are embedded into Slots. Slots are the objects, where positioning and drop events are handled.  
 *  Instances of this class contain the settings and drawing, which differ from android's native implementation of the spinner should be done 
 *  */
@SuppressWarnings("deprecation")
public class MSpinner extends Spinner implements  ScaleEventListener{
	
	/*
	 * SPINNER CONSTRUCTION:
	 * Spinner is made of X views
	 * 
	 *		- View which represents the closed dropdown widget (normally a square-TextView with an arrow on the right side).
	 *		  This View is maintained by the Adapter with the Content-Data. For each Adapter entry there is an instance of such a view. Those views can be retrieved as following:
	 * 			this.getSelectedView()
	 * 			this.getAdapter().getView(1, null, null); //0 is number of the view to retrieve
	 * 
	 * 		- View which represents the Dropdown popup layer.
	 * 		  This View is passed to the Adapter too, curiously there is an instance of it for each entry in the Adapter too. This view can be retrieved as following:
	 *			 this.getAdapter().getDropDownViewResource(1, null, null); //0 is number of the view to retrieve
	 */
	
	private float textSize=20f; 
	
//	private int mUnscaledX=0;
//	private int mUnscaledY=0;
	
	private int mPaddingLeftUnscaled;
	private int mPaddingTopUnscaled;
	private int mPaddingRightUnscaled;
	private int mPaddingBottomUnscaled;
	
	

	public MSpinner(Context context, ArrayList<String> contentArrayList) {
		super(context);
		
		//so that we won't get nullPointerExceptions 
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(0,0,0,0));		
		
		//avoid padding, to avoid unnecessary complications on scrolling
		this.mPaddingTopUnscaled = 10;
		this.mPaddingLeftUnscaled = 10;
		this.mPaddingRightUnscaled=20;
		this.mPaddingBottomUnscaled=0;
		
		//listener
		ScaleHandler.addScaleEventListener(this);
		
		//setting the Adapter with content  
		setAdapter(contentArrayList);
		
	}
	
//SETTER AND GETTER
	public void setAdapter(ArrayList<String> contentArrayList) {
		ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this.getContext(), R.layout.spinnerlayout, contentArrayList);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		super.setAdapter(spinnerArrayAdapter);
		
		//initiate the size
		this.onScaleEvent(ScaleHandler.getScale(), null);
		
		
		//redraw the spinner after the first, automatical selection
		myRedrawSpinner();
	}
	/** Method to retrieve this widgets Value. */
	public String getValue(){
		return this.getCurrentTextView().getText().toString();
	}
	
//	/** Use this method to set position of this widget*/
//	public void setPosition(int xUnscaled, int yUnscaled){
//		this.mUnscaledX = xUnscaled;
//		this.mUnscaledY = yUnscaled;
//		
//		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) this.getLayoutParams();
//		lp.x = ScaleHandler.scale(xUnscaled);
//		lp.y =  ScaleHandler.scale(yUnscaled);
//		this.setLayoutParams(lp);
//	}
		
	
//DRAWING
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		SpinnerTextView mtv = this.getCurrentTextView();
		mtv.setTextSize(ScaleHandler.getScale()*textSize);
		
		int customWidthOffset = ScaleHandler.scale(50);
		int customHeightOffset = ScaleHandler.scale(25);
		
		setMeasuredDimension(mtv.getWidthScaled()+customWidthOffset, mtv.getHeightScaled()+customHeightOffset);
	}
	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		SpinnerTextView mtv = this.getCurrentTextView();

		this.getCurrentTextView().onLayout(true,
				this.getPaddingLeft(), 
				this.getPaddingTop(), 
				mtv.getWidthScaled()+this.getPaddingLeft(), 
				mtv.getHeightScaled()+this.getPaddingTop()
		);
		
		this.getCurrentTextView().requestLayout();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	
//HELPER
	/** use this method everywhere, except of the constructor because in constructor the adapter is not yet initialized */
	private SpinnerTextView getCurrentTextView(){
		SpinnerTextView mv = (SpinnerTextView) this.getSelectedView();
		//return the first view from adapter, if none is selected yet
		if(mv == null){
			mv = (SpinnerTextView) this.getAdapter().getView(0, null, null);
		}
		return mv;
	}

	
	@Override
	public void onScaleEvent(float newscale, Point pivot) {
		//Textview
		SpinnerTextView mtv = this.getCurrentTextView();
		mtv.setTextSize(textSize*newscale);
		
		this.setPadding( Math.round(this.mPaddingLeftUnscaled *newscale), 
				Math.round(this.mPaddingTopUnscaled *newscale),  
				Math.round(this.mPaddingRightUnscaled *newscale),  
				Math.round(this.mPaddingBottomUnscaled *newscale) 
		);
		
		//make the layout changes visible
		this.invalidate();
	}
	
	
//POPUP HOOK
//	@Override
//	public void setSelected(boolean selected) {
//		Log.d("spinnerevent","setSelected(boolean selected)");
//		super.setSelected(selected);
//	}
	//repair onItemSelected listener
	@Override
	public void setSelection(int position) {
		Log.d("spinnerevent","setSelection(int position)");
		super.setSelection(position);
		myRedrawSpinner();
	}
//	@Override
//	public void setSelection(int position, boolean animate) {
//		Log.d("spinnerevent","setSelection(int position, boolean animate)");
//		super.setSelection(position, animate);
//	}
	
	
	//sometimes event doesnt trigger, dont know why, so onItemSelectedListener doesn't work with the spinner
//	@Override
//	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	}
	
	
	/** method called, when a new item is selected in the popup.
	 *  method updates the layout of the stack and redraws it. */
	protected void myRedrawSpinner(){
		//redraw this, no need to measure
		this.onLayout(true,0,0,0,0); //requestLayout doesnt work
		this.invalidate();
		
		//start rekursive method to readraw the stack
		rekLayoutRedrawStack(this.getParent());
	}
	/** Update the layout and redraw the whole stack.
	 *  Invalidates the whole ScratchTab stack, because invalidate() doesn't work - invalidate() redraws the current view, 
	 *  and the current view is drawn on the canvas by many wrapping classes, beginning from the top, so we invalidate thetop most, invalidation is then passed to the bottom.
	 *   
	 *  Used when something is typed into the text field, because textfield can change it's size and so change the layout of the whole stack */
	private void rekLayoutRedrawStack(ViewParent parent){
		ViewParent par = parent;
		if(par == null) return;
		//invalidate if if as Slot was found
		if(par instanceof Block){
			Block<?> root = ((Block<?>)par).findRootBlock();
			root.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
			root.onLayout();
			root.invalidateTree();
			return;
		}
		rekLayoutRedrawStack(parent.getParent());
	}



}

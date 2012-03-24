package com.binaryme.ScratchTab.Gui.Widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.DebugMode;
import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.tools.M;

/** Class to represent an editable textField.
 *  Instances of this class do not position themselves, instead they are dropped into Widgets, which handle positioning and drop events.  
 *  Here all the settings and drawing, which differ from android's native implementation of the spinner should be done */
@SuppressWarnings("deprecation")
public class MTextField extends EditText implements  ScaleEventListener, TextWatcher, OnEditorActionListener{
	
	private String defaultValue = ConfigHandler.DEFAULT_VALUE_TEXTFIELD;
	
	private Context mContext;
	
	private int unscaledHeight;
	private int unscaledMinWidth=40;
	
	private int mUnscaledX;
	private int mUnscaledY;
	
	private int textSize=20; 
	

	public MTextField(Context context) {
		super(context);
		mContext=context;
		
		//set default text
		this.setHint(defaultValue);
		
		//set Text size
		this.setTextSize(textSize);
		
		//frame
		this.setFrame(0, 0, 0, 0);
		
		//padding - disabled, doesnt work
		this.setPadding(10, 0, 10, 0);
		
		//gravity
//		this.setGravity(Gravity.TOP);
//		this.setGravity(Gravity.LEFT);
		this.setGravity(Gravity.CENTER);
		
    	//enable or keyboard wont appear
		/*
		 * There is a default behavior of gaining the focus by a textview, when it comes to the screen, which includes opening the soft keyboard.
		 * This is not acceptable, because during our navigation through the workspace the focus would jump and keyboard popup every time.
		 * 
		 *  because its not possible to disable autofocus we disable the focus here,
		 *  activate focus on touch,
		 *  deactivate focus on focus leaving.
		 */
		//
//		this.setFocusable(false); 
//		this.setFocusableInTouchMode(false);
    	this.setFocusable(true);
    	
    	this.setNextFocusDownId(this.getId());
    	this.setNextFocusForwardId(this.getId());
    	this.setNextFocusLeftId(this.getId());
    	this.setNextFocusRightId(this.getId());
    	this.setNextFocusUpId(this.getId());
		
		this.setSelectAllOnFocus(false); //select text on focus
		
		this.setFadingEdgeLength(0);
    	
    	
    	//to avoid null pointer exceptions
    	this.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,unscaledMinWidth,0,0));	
    	
		//size
    	this.setMinWidth(unscaledMinWidth);
//    	this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//    	this.unscaledHeight = this.getMeasuredHeight();

    	//somehow android's native widget is measured, so that there is allways a little top/bottom padding left 
    	this.unscaledHeight = this.textSize+20; 
    	
    	
    	//listeners
		ScaleHandler.addScaleEventListener(this);
		
		//allow text as input
//		this.setInputType(InputType.TYPE_CLASS_TEXT);
																	//EVERYTHING BELOW DOESNT WORK UNTIL A SCALE EVENT OCCURS, OR NTIL THE WIDGET IS RSIZED ONCE
//		this.setInputType(InputType.TYPE_CLASS_PHONE); 				//keyboard allows + ( ) .
//		this.setInputType(InputType.TYPE_CLASS_NUMBER);				//keyboard allows decimal numbers with a point as a dlimiter
		
//		this.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);	//doesn't work
//		this.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);		//doesn't work
		
		//limiting the length - works!
//		InputFilter[] FilterArray = new InputFilter[1];
//		FilterArray[0] = new InputFilter.
//		this.setFilters(FilterArray);
		
		//make the input decimal - works!
//		DigitsKeyListener MyDigitKeyListener =	new DigitsKeyListener(true, true); // first true : is signed, second one : is decimal
//		this.setKeyListener( MyDigitKeyListener );

		//listen for key events, so that we can hide the keyboard on enter
		this.setOnEditorActionListener(this);
		
		//enable
		this.setEnabled(true);
		
	}
	
//SETTER AND GETTER
	/** Use this method to set position of this widget*/
	public void setPosition(int xUnscaled, int yUnscaled){
		this.mUnscaledX = xUnscaled;
		this.mUnscaledY = yUnscaled;
		
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) this.getLayoutParams();
		lp.x = ScaleHandler.scale(xUnscaled);
		lp.y =  ScaleHandler.scale(yUnscaled);
		this.setLayoutParams(lp);
	}
	/** Method to retrieve this widgets Value. */
	public String getValueAsString(){
		String result =defaultValue; 
		if(this.getText().toString().length()>0){
			result = this.getText().toString();
		}
		return result;
	}
	
//DRAW
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), (Math.round(this.unscaledHeight*ScaleHandler.getScale())));
	}
	@Override
	public void onScaleEvent(float newscale, Point pivot) {
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) this.getLayoutParams();
		
//		//position
		lp.x = ScaleHandler.scale(this.mUnscaledX);
		lp.y =  ScaleHandler.scale(this.mUnscaledY);

		//size
		this.setMinWidth(Math.round(unscaledMinWidth*newscale));	//width
		lp.height = (Math.round(this.unscaledHeight*newscale)); 	//height
		this.setMinHeight(lp.height);
		
		this.setTextSize(textSize*newscale);						//textsize 
		this.setLayoutParams(lp);
		
		//redraw
//		this.onMeasure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		invalidate();
		
		if(DebugMode.on){
			Log.d("widget","-----");
			Log.d("widget","unscaledMinWidth: "+Math.round(unscaledMinWidth*newscale));
			Log.d("widget","lp.height: "+lp.height);
			Log.d("widget","MinHeight: "+lp.height);
			Log.d("widget","MeasuredHeight: "+this.getMeasuredHeight());
			Log.d("widget","TextSize: "+(textSize*newscale));
			Log.d("widget","getCompoundDrawablePadding : "+getCompoundDrawablePadding());
			Log.d("widget","getCompoundPaddingTop  : "+getCompoundPaddingTop());
			Log.d("widget","getCompoundPaddingBottom  : "+getCompoundPaddingBottom());
		}
	}


//FOCUS
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		this.setEnabled(true);
//		this.setFocusableInTouchMode(true);
//		this.setFocusable(true);
		
		this.requestFocus();
				
		//open the keyboard if a textfield was touched
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInputFromInputMethod(this.getWindowToken(), 0);
		
		
		//BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG!
		// Without the code below EditText does not receive numbers, until the first scale event or until enough text is entered, to resize the EditText.
		// don't know about the reason for this behavior, the textedit events just don't trigger, numbers just do not appear. 
		float constant = ScaleHandler.getScale();
		this.onScaleEvent(constant+0.1f, new Point(0,0));
		this.onMeasure(100, 20);
		this.onLayout(true, 0, 0, 100, 100);
		this.layoutRedrawStack(this.getParent());
		this.invalidate();
		//BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG! BUG!

		return super.onTouchEvent(event); 
	}
	
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
//		this.setFocusable(false);
//		this.setFocusableInTouchMode(false);

		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		
		//if current textfield is focused on change, then it was not focused before
		if(focused){
			//close the keyboard, if keyboard opening is triggered on autofocus - it should open on touch, not automatically
//			M.HideKeyboard(this);
		}
	}

	//react on Enter Button by hiding the keyboard
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean result = false;
		//Close Keyboard, when
		// IME_ACTION_NEXT is triggered, which is the "next" button
		// event != null is not null, which is the case when enter was pushed
		if((actionId == EditorInfo.IME_ACTION_NEXT) || (event!=null)){
			result = true;
			//hide the keyboard on enter
			M.hideKeyboard(this);
		}
		return result;
	}
	
	
//INTERFACE IMPLEMENTATION
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		layoutRedrawStack(getParent());
	}
	
	
	
//HELPER
	/** Update the layout and redraw the whole stack.
	 *  Invalidates the whole ScratchTab stack, because invalidate() doesn't work - invalidate() redraws the current view, 
	 *  and the current view is drawn on the canvas by many wrapping classes, beginning from the top, so we invalidate thetop most, invalidation is then passed to the bottom.
	 *   
	 *  Used when something is typed into the text field, because textfield can change it's size and so change the layout of the whole stack */
	protected void layoutRedrawStack(ViewParent parent){
		ViewParent par = parent;
		if(par == null) return;
		//invalidate if if as Slot was found
		if(par instanceof Block){
			Block<?> root = ((Block<?>)par).findRootBlock();
			root.onLayout();
			root.invalidate();
			return;
		}
		layoutRedrawStack(parent.getParent());
	}
	
	
//DRAG
	@Override
	public boolean onDragEvent(DragEvent event) {
		//do not accept drag events. Slots will handle drag&drop.
		return false;
	}
	
	//TODO
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		Log.d("twidget","Workspace  dispatchKeyEventPreIme");
		return super.dispatchKeyEventPreIme(event);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d("twidget","Workspace  dispatchKeyEvent");
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		Log.d("twidget","Workspace  onKeyPreIme");
		return super.onKeyPreIme(keyCode, event);
	}
	
}

package com.binaryme.ScratchTab.Gui.Widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.TextView;


/** Class is used to construct a spinner. This View - is the view which is used implicitly to display a dropdown. 
 *  DO NOT MISTAKE MTextView for the MTextField !!!
 *  MTextView  - this view, impliocetily used in spinner
 *  MTextField - view, which can handle text input
 * */
@SuppressWarnings("deprecation")
public class SpinnerTextView extends TextView {
	
	private int mTextHeightScaled;
	private int mTextWidthScaled;
	
	// some space is needed, for android, to draw the arrow down above this view, when it will draw the dropdown
	private int rightSpaceForArrow =30;
	
	public SpinnerTextView(Context context) {
		super(context);
		init();
	}
	public SpinnerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SpinnerTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init(){
		this.setPadding(0,0,0,0);

		//to avoid null pointer exceptions
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(0,0,0,0));	
	}
	
	
	
//GETTER AND SETTER
	public int getHeightScaled(){
		return this.mTextHeightScaled;
	}
	public int getWidthScaled(){
		return this.mTextWidthScaled;
	}
	
	
	
//DRAWING
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mTextWidthScaled+this.rightSpaceForArrow, mTextHeightScaled);
		Log.d("SpinnerTexView","onMeasure");
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Log.d("SpinnerTexView","onLayout");
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.drawColor(Color.MAGENTA);
		Log.d("SpinnerTexView","onDraw");
		
		String str = this.getText().toString();

		//vertical centering
		int canvasheight = canvas.getClipBounds().height();
		int topPadding = Math.max(0, (canvasheight-this.mTextHeightScaled)/2);
		
		canvas.drawText(str, 0, this.mTextHeightScaled+topPadding, this.getPaint());
	}
	
	
	
//OTHER
	
	/** Method is called by android on TextView initiation, so we can define this View's size here */
	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
		
		//update this view's size on Textsize change
		String str = this.getText().toString();
		
		//measure Text
		Rect bounds = new Rect();
		this.getPaint().getTextBounds(str, 0, str.length(), bounds); //saves the data in bounds
		
		this.mTextWidthScaled= bounds.width()+2; // +2 to avoid rounding errors and cutting a bit of the last letter
		this.mTextHeightScaled= bounds.height();
		
		setMeasuredDimension(mTextWidthScaled, mTextHeightScaled);
	}
	
}

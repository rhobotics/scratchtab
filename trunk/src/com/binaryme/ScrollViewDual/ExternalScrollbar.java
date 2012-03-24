package com.binaryme.ScrollViewDual;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


/*
 * IMPORTANT: The width of the External scroll bar should be the same as the width of the horizontal bar, which it should represent!
 */
public class ExternalScrollbar extends HorizontalScrollView {

	private View widthStretchDummy;
	private final static int MINHEIGHT =6;
	private int mScrollOffsetX=0;
	private boolean ScrollbarFadingEnabled = false;
	
	public ExternalScrollbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public ExternalScrollbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ExternalScrollbar(Context context) {
		super(context);
		init();
	}

	void init(){
		this.setScrollbarFadingEnabled(ScrollbarFadingEnabled);
		
		//add LinearLayout
		LinearLayout linLay = new LinearLayout(this.getContext());
		android.view.ViewGroup.LayoutParams lpLinLay = new android.view.ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linLay.setLayoutParams(lpLinLay);
		linLay.setBackgroundColor(Color.TRANSPARENT);
		
		//add a dummy view to stretch the horizontal Scroll Layout
		View dummy = new View(this.getContext());
		android.view.ViewGroup.LayoutParams lpDummy = new android.view.ViewGroup.LayoutParams(0, MINHEIGHT);
		dummy.setLayoutParams(lpDummy);
		
		this.widthStretchDummy = dummy;
		//wrap the dummy in a Linear Layout, because otherwise it wont display the width and height of the dummy right
		linLay.addView(dummy);
		this.addView(linLay);
	}

	private void setWidth( int w ){
		android.view.ViewGroup.LayoutParams lp = this.widthStretchDummy.getLayoutParams();
		lp.width=w;
		this.widthStretchDummy.setLayoutParams(lp);
	}
	
	protected int computeHorizontalScrollRange(int width) {
		this.setWidth(width);
		return super.computeHorizontalScrollRange();
	}
	
	@Override
	protected int computeHorizontalScrollOffset() {
		return this.mScrollOffsetX;
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		//make the external bar not dragable
//		return false;
//	}
	
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		this.mScrollOffsetX = x;
	}
	

	//make awakenScrollBars methods available in this package
	@Override
	protected boolean awakenScrollBars() {
		return super.awakenScrollBars();
	}
	
	@Override
	protected boolean awakenScrollBars(int startDelay) {
		return super.awakenScrollBars(startDelay);
	}
	
	@Override
	protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
		return super.awakenScrollBars(startDelay, invalidate);
	}

}


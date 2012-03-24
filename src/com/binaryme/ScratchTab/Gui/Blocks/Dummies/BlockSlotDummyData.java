package com.binaryme.ScratchTab.Gui.Blocks.Dummies;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Gui.Shapes.Dummies.ShapeSlotDummy;
import com.binaryme.ScratchTab.Gui.Slots.Slot.SlotMode;
import com.binaryme.ScratchTab.Gui.Widgets.MNumField;


/** this class wraps android's widgets. 
 *  It will (re-) implement the drawing hooks, to draw the widgets instead of the paths. 
 *  
 *  <br>
 *  @param <T> - is the class of the android's widget, which will be wrapped by the instance of the current class, e.g. {@link MNumField}
 *  @param <O> - is the Object Type which the current Dummy will return 
 *  */
@SuppressWarnings("deprecation")
public abstract class BlockSlotDummyData<T extends View, O extends Object> extends BlockSlotDummy<O> implements ScaleEventListener {

	protected T androidWidget;
	
	private int mUnscaledX;
	private int mUnscaledY;

	public BlockSlotDummyData(Activity context, T widget) {
		super(context);
		//remember the widget, which will do all the action and add it to the current view
		androidWidget = widget;
		this.addView(widget);
		
		//to avoid NullPoinster exceptions
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
		
		//need to know about scale events to adopt the position. The size will be adopted to the scale by MSpinner and MTextField 
		ScaleHandler.addScaleEventListener(this);
	}
	
	
	
//GETTER AND SETTER
	/** TODO method will return the dummy if nothing was dropped into the widget, otherwise it will return the dropped in View. */
	public T getWidget(){
		return this.androidWidget;
	}
	
	/**
	 * Used to set the initial position of this widget, when it is inserted somewhere.
	 * @param xUnscaled
	 * @param yUnscaled
	 */
	public void setUnscaledPosition(int xUnscaled, int yUnscaled){
		this.mUnscaledX = xUnscaled;
		this.mUnscaledY = yUnscaled;
		
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) this.getLayoutParams();
		lp.x = ScaleHandler.scale(xUnscaled);
		lp.y = ScaleHandler.scale(yUnscaled);
		this.setLayoutParams(lp);
	}
		
	
	

//DRAW - pass everything to teh infill, which will handle the scaling itself
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("dummy","onMeasure");
		View infill = getWidget();
		infill.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		this.setMeasuredDimension(infill.getMeasuredWidth(), infill.getMeasuredHeight());
		
		int scaledWidth=infill.getMeasuredWidth();
		int scaledHeight=infill.getMeasuredHeight();
		int unscaledWidth= ScaleHandler.unscale(scaledWidth);
		int unscaledHeight=ScaleHandler.unscale(scaledHeight);
		
		//important is using setMeasuredDimensionsPublic instead of setMeasureDimensions, because only setMeasuredDimensionsPublic updates the unscaled dimensions
		this.setMeasuredDimensionsPublic(scaledWidth, scaledHeight, unscaledWidth, unscaledHeight);
//		Log.d("widget",M.unifyLength(this.getClass() + "  onMeasure", 70) + " : "+infill.getMeasuredWidth() + ", "+infill.getMeasuredHeight() );
//		Log.d("field","Dummy onMeasure "+this.getClass());
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Log.d("dummy","onLayout");
	}
		
	
	//TODO: check those 2 methods, which of them is necessary
	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.drawColor(Color.RED); //TODO what is scaled wrong - distance or position? position!
		getWidget().draw(canvas);
	}

	
	//scale
	@Override
	public void onScaleEvent(float newscale, Point pivot) {
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) this.getLayoutParams();
		
		//position
		lp.x = ScaleHandler.scale(this.mUnscaledX);
		lp.y = ScaleHandler.scale(this.mUnscaledY);
		
		this.setLayoutParams(lp);		
	}
	
	//touch
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("field","Touchfeld BlockSlotDummyData gedrueckt "+this.getClass());
		return getWidget().onTouchEvent(event);
	}

	@Override
	protected ShapeSlotDummy initiateShapeHere() {
		// the Data dummy will draw itself by drawing android's widgets - do not need to initialize the Shape
		return null;
	}
	
	
//OVERRIDE METHODS FROM BlockSlotDummy, because the Data Dummies should somehow use their widgets to give feedback, to draw themselves
	@Override
	public void setSlotMode(SlotMode mode){
		//TODO handle the slot mode here, implement it somehow in the widget first.
	}
	
	@Override
	public ShapeSlotDummy getShape() {
		//TODO: normally return null. Should we return an empty shape to avoid nullpointerexceptions?
		return null;
	}
	
	@Override
	public void feedbackExecutionError() {
		//TODO: use the widget to give some feedback. Define a feedbackExecutionError interface first, which all widgets will implement
	}
	@Override
	public void feedbackExecutionProcessRunning() {
		//TODO: use the widget to give some feedback. Define a feedbackExecutionError interface first, which all widgets will implement
	}
	@Override
	public void feedbackDisable() {
		//TODO: use the widget to give some feedback. Define a feedbackExecutionError interface first, which all widgets will implement
	}
	
}

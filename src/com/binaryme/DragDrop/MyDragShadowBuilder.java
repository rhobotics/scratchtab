package com.binaryme.DragDrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

import com.binaryme.tools.M;

/** Creates an own drag shadow. More transparent, positioned with the right top corner under the finger */
public class MyDragShadowBuilder extends View.DragShadowBuilder {

	private View shadowView; 
	
	MyDragShadowBuilder(View v){
		super(); //call super constructor without View, cause we will override the draw methods to draw the shadow on our own.
		shadowView = v;
	}
	
	@Override
	public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
		shadowSize.x = shadowView.getMeasuredWidth();
		shadowSize.y = shadowView.getMeasuredHeight();
		
		shadowTouchPoint.x = M.mm2px(10) + M.mm2px(2); //block back width + 1/2 block nudge
		shadowTouchPoint.y = 0;
	}
	
	@Override
	public void onDrawShadow(Canvas canvas) {
		
		//converting the view to a bitmap
		Bitmap b = Bitmap.createBitmap( shadowView.getMeasuredWidth(), shadowView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);                
	    Canvas c = new Canvas(b);
	    shadowView.draw(c);

	    //painting the bitmap with a transparency of 100 to the canvas 
	    Paint p = new Paint();
	    p.setAlpha(100);
		canvas.drawBitmap(b, 0, 0, p);
	}
}

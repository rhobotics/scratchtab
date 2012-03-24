package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.binaryme.DragDrop.DragStateData;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Events.HeadStartTokenHandler;
import com.binaryme.tools.M;

public class StartToken extends View implements OnTouchListener, OnDragListener{
	
		private String mText = "Drag to start";
		private int mWidth = M.cm2px(8);
		private int mHeight = M.cm2px(1);
		private int mPadding = 8;
		

		public StartToken(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public StartToken(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public StartToken(Context context) {
			super(context);						
			init();
		}
		
		private void init(){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.go);

//			//size
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth,mHeight);
//			this.setLayoutParams(lp);

			//register listeners
			this.setOnTouchListener(this); //to react on touch and drag of the token
			this.setOnDragListener(this);  //to react on drop of the token into itself (as if a click was preformed)
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension(mWidth, mHeight);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
//			canvas.drawColor(Color.RED);
			int strokeWidth = 8;
			
			Paint p = new Paint();
			p.setStrokeWidth(8);
			p.setColor(Color.rgb(114, 114, 116));
			p.setPathEffect(new DashPathEffect(new float[] {20,20}, 0));
			p.setAntiAlias(true);
			p.setStrokeMiter(5);
			p.setStrokeCap(Paint.Cap.ROUND);
			p.setStrokeJoin(Paint.Join.ROUND);
			
			RectF rect = new RectF();
			rect.left = mPadding;
			rect.top = strokeWidth/2;
			rect.right = mWidth-mPadding;
			rect.bottom = mHeight-mPadding;

			
			p.setStyle(Style.FILL);
//			p.setColor(Color.rgb(220, 208, 218));	//grey
			p.setColor(Color.rgb(0, 104, 70));		//green
			canvas.drawRoundRect(rect, 3, 3, p);
			
			p.setStyle(Style.STROKE);
			p.setColor(Color.rgb(114, 114, 116));
			canvas.drawRoundRect(rect, 3, 3, p);
			
			
			
			//text
			Rect textBounds = new Rect();
			Paint tp = new Paint();
			tp.setColor(Color.BLACK);
			tp.getTextBounds(mText, 0, mText.length(), textBounds);
			int textX = (mWidth/2) - textBounds.width()/2;
			int textY = (int) ((mHeight/2) + 3);
			canvas.drawText(mText, textX, textY, tp);
			
			//picture
//			Resources res = getResources();
//			Drawable drawable = res.getDrawable(R.drawable.go);
//			drawable.draw(canvas);
//			
//			int iconHeight = mHeight - 3*mPadding;
//			int icon1X = mWidth/2 + textBounds.width();
//			int icon2X = mWidth/2 - textBounds.width() - iconHeight;
//			int iconY = 5;
//			Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.go);
//			Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, iconHeight, iconHeight, false);
//			canvas.drawBitmap(resizedIcon, icon1X, iconY, new Paint());
//			canvas.drawBitmap(resizedIcon, icon2X, iconY, new Paint());
			
		}
		
		
//DRAG AND DROP
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//TODO: introduce treshhold,  start dragging only if a movement of x mm happened, after touching the button
			
			DragStateData dd = new DragStateData();
			dd.classOfTheDraggable = this.getClass();
			
			//new Token shadow
			Resources res = getResources();
			Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.go);
			ImageView iv = new ImageView(getContext());
			iv.setImageBitmap(icon);
			iv.setLayoutParams(new LayoutParams(100, 100));
			
		    //6. 
//	        StartToken.MTokenView token =  new StartToken.MTokenView(getContext());
//	        token.setX(100);
//	        token.setY(100);
//	        token.setVisibility(VISIBLE);
//	        token.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
//	        token.setMinimumHeight(100);
//	        token.setMinimumWidth(100);
			
			this.startDrag(null, new MShadow(getContext(), icon), dd, 0);
			return true;
		}
		@Override
		public boolean onDrag(View v, DragEvent event) {
			//implement on drag listener, so that if you drop myself to me - its like a click
			boolean result= false;

			//in this app we only pass DragStateData as LocalStateData, so we can cast it here
			DragStateData dd = (DragStateData) event.getLocalState();
			
			try{
				//implement on drag listener, so that if you drop myself to me - its like a click
				if(dd!= null && dd.classOfTheDraggable==StartToken.class){
					switch(event.getAction()){
					//yes, we want to receive the drag events
					case DragEvent.ACTION_DRAG_STARTED :
						result= true;
						break;
						
					//dropped a startToken into a scratchTab block	
					case DragEvent.ACTION_DROP :
						HeadStartTokenHandler.fireExecutionOfStartTokenHeads();
						result= true;
						break;
						
					case DragEvent.ACTION_DRAG_ENDED :
						result= true;
						break;
					}
				}
			}catch(Exception e){
				Log.d("Thread","Exception");
			}
			
			return result;
		}
		
		
		
		public static class MShadow extends View.DragShadowBuilder{
			
			Bitmap bm;

			public MShadow(Context context, Bitmap bitmap) {
				bm = bitmap;
			}
			
//			@Override
//			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//				setMeasuredDimension(bm.getWidth(), bm.getHeight());
//			}
//			
//			@Override
//			protected void onDraw(Canvas canvas) {
//				super.onDraw(canvas);
//				canvas.drawColor(Color.RED);
//				canvas.drawBitmap(bm, new Matrix(), new Paint());
//			}
			
			@Override
			public void onDrawShadow(Canvas canvas) {
				canvas.drawBitmap(bm, new Matrix(), new Paint());
			}
			
			@Override
			public void onProvideShadowMetrics(Point shadowSize,
					Point shadowTouchPoint) {
				super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
				
				shadowSize.x = bm.getWidth();
				shadowSize.y = bm.getHeight();
				
				shadowTouchPoint.x = bm.getWidth();
				shadowTouchPoint.y = bm.getHeight();
			}
		}
}

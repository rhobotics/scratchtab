package com.binaryme.ScratchTab.Gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.binaryme.DragDrop.DragStateData;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Events.HeadStartTokenHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.tools.M;

public class CopyOfStartToken extends ImageView implements OnTouchListener, OnDragListener{

		public CopyOfStartToken(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public CopyOfStartToken(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public CopyOfStartToken(Context context) {
			super(context);						
			init();
		}
		
		private void init(){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.go);
			this.setImageDrawable(drawable);

			//size
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(M.cm2px(1),M.cm2px(1));
			this.setLayoutParams(lp);
			
			//register listeners
			this.setOnTouchListener(this); //to react on touch and drag of the token
			this.setOnDragListener(this);  //to react on drop of the token into itself (as if a click was preformed)
		}
		
		
//DRAG AND DROP
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//TODO: introduce treshhold,  start dragging only if a movement of x mm happened, after touching the button
			
			DragStateData dd = new DragStateData();
			dd.classOfTheDraggable = this.getClass();
			
			this.startDrag(null, new DragShadowBuilder(this), dd, 0);
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
				if(dd!= null && dd.classOfTheDraggable==CopyOfStartToken.class){
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
		
}

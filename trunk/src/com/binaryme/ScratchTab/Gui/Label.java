package com.binaryme.ScratchTab.Gui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.DebugMode;
import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Exec.ExecutionHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.ScratchTab.Gui.Slots.Slot;
import com.binaryme.ScratchTab.Gui.Slots.SlotBoolean;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataNum;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataSpinner;
import com.binaryme.ScratchTab.Gui.Slots.SlotDataText;

/** Label is a very special Block. It can contain Views, text, other Blocks. 
 *  Labels are designed to equip the Block shapes with text, entry-fields, boolean slots and other decorations.
 *  For that the measurement and scaling mechanisms are redesigned here to wrap all the label content.
 */
public class Label extends Block {
	
		private int CONTENT_SPACING = 5;
		private final float TEXT_SIZE = ConfigHandler.textPaintLabel.getTextSize();
		
		private ArrayList<Object> content;
		
		protected Paint textPaint;
		private int scaled_CONTENT_SPACING = ScaleHandler.scale(CONTENT_SPACING);
		
		public Label(Activity context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}
		public Label(Activity context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public Label(Activity context) {
			super(context);						
			init();
		}
		
		private void init(){
			//initiate the storage structures
			this.content = new ArrayList<Object>();
			
			//initiate the paint for the text
			textPaint = new Paint(ConfigHandler.textPaintLabel);
			
			
			//TODO : comment cause layout of label breaks 
			//try laying out whole stack
			//update the textSize
//			this.findRootBlock().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//			this.findRootBlock().onLayout();
			textPaint.setTextSize(TEXT_SIZE*ScaleHandler.getScale());
//			invalidate();
		}
		
		
		public void appendContent(String str){	
			//wrap string as a TextContainer
			this.content.add(new TextContainer(str));
		}
		public void appendContent(Slot slot){
			this.content.add(slot);
			
			//additionally add block to this ViewGroup. It will measured and positioned later, on a onMeasure-onLayout-onDraw cycle
			this.addView(slot);
		}
		
//GETTER AND SETTER
		@Override
		public BlockType getType() {
			return BlockType.LABEL;
		}
		
		
		
//DRAWING METHODS
		
		@SuppressWarnings({ "deprecation"})
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			
			Log.d("onmeasure","Measuring "+this.getClass().toString());
			for(Object obj: content){
				if(obj instanceof TextContainer){
					TextContainer t = (TextContainer) obj;
					Log.d("onmeasure","Measuring "+t.text);
					if(t.equals("else")){
						Log.d("onmeasure","Pause");
					}
					break;
				}
			}

			float mScaledWidth=0;
			float mScaledHeight=0;
			float mUNSCALEDWidth=0;
			float mUNSCALEDHeight=0;
			
			int count = 0; //counter of inserted content objects. Needed to know, how many content spaces will be insterted.
			
			
			for( Object obj :this.content){
				if(obj instanceof TextContainer){
					TextContainer txt = (TextContainer)obj;
					//consider TextContainer's width for scaled
					mScaledWidth += txt.scaledWidth; 								//content is horizontally positioned in a row, 
					mScaledHeight = Math.max(mScaledHeight, txt.scaledHeight);		//label height equals to the height of the tallest object

					//and unscaled values
					mUNSCALEDWidth += txt.unscaledWidth;								//content is horizontally positioned in a row
					mUNSCALEDHeight = Math.max(mUNSCALEDHeight, txt.unscaledHeight); //label height equals to the height of the tallest object;
					
					count++;
					
				}else if(obj instanceof Slot){
										
					Slot s = (Slot)obj;
					s.onMeasure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					
					//ATTENTION: inside of the onMeasure method the getHeight and getWidth returns 0. Manual measuring and getMEASUREDHeight should be used.
					mScaledWidth+=s.getMeasuredWidth();
					mScaledHeight = Math.max(mScaledHeight, s.getMeasuredHeight()); //content is horizontally positioned in a row, label height equals to the height of the tallest object
					
					//update unscaled Values
					mUNSCALEDWidth+=s.getUnscaledWidth();							//content is horizontally positioned in a row
					mUNSCALEDHeight= Math.max(mUNSCALEDHeight, s.getUnscaledHeight()); //label height equals to the height o
					
					count++;
				}
				
				//add a space, so that next content will not touch the current 
				mScaledWidth 	+=  scaled_CONTENT_SPACING ;
				mUNSCALEDWidth 	+=  CONTENT_SPACING ;
			}
			

			//round the scaled width and scaled height for setMeasuredDimension
			int mScaledWidthRounded =Math.round(mScaledWidth);
			int mScaledHeightRounded=Math.round(mScaledHeight);
			
			int mUNSCALEDWidthRounded =Math.round( mUNSCALEDWidth );
			int mUNSCALEDHeightRounded=Math.round( mUNSCALEDHeight );
			
			
			setMeasuredDimensionsPublic(mScaledWidthRounded, mScaledHeightRounded, mUNSCALEDWidthRounded, mUNSCALEDHeightRounded);
		}
		
		@Override
		public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
			
			Log.d("Label","onLayout");
			
			float currentScaledX=0; //set content position, by traversing it from left to right. For that remember the current X position. 
			
			for( Object obj :this.content){
				if(obj instanceof TextContainer){
					TextContainer txt = (TextContainer)obj;
					
					txt.scaledX = currentScaledX; //set the current X 
					currentScaledX += txt.scaledWidth + this.scaled_CONTENT_SPACING; //increase current scaled X Position by width and the content space
					
					int labelMiddle = this.getMeasuredHeight()/2;
					int textHalf = txt.scaledHeight/2;
					txt.scaledY = (labelMiddle - textHalf); //centring the text vertically. Its Y position equals to middle - half_txt.height
					
				}else if(obj instanceof Slot){
					Slot s = (Slot)obj;
					
					int sHeight = s.getMeasuredHeight();
					int sWidth = s.getMeasuredWidth();
					int yPositionCentered = (this.getMeasuredHeight()/2) - (sHeight/2);
					
					s.setPosition(Math.round(currentScaledX), yPositionCentered );	//setting block's position with respect to the previous label content
					currentScaledX += sWidth + this.scaled_CONTENT_SPACING;			//increase current scaled X Position by width and the content space
				}
			}
			
			super.onLayout(changed, left, top, right, bottom);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			if(DebugMode.on){
				canvas.drawColor(Color.BLACK);
			}
			//TODO
			Log.d("Label","onDraw");
			
			//use Padding
			canvas.save();
			
			for( Object obj :this.content){
				if(obj instanceof TextContainer){
					TextContainer txt = (TextContainer)obj;
					
					float ascent  = textPaint.ascent();		//space above the baseline
					float descent = textPaint.descent();	//space under the baseline, g, y use it
					canvas.drawText(txt.text, txt.scaledX, txt.scaledY-ascent, textPaint); //text is already positioned and measured with respect to other content. Draw it.  
					
				}else if(obj instanceof Slot){
					// we don't have to draw Slots explicitly because they are Views and are drawn automatically
					//	Slots only have to be added into the View structure - done in appendContent() 
					//	and the drawing will be automatically dispatched to them by Android's dispatchDraw()
				}
			}
			
		}
		
//LABEL INTERACTION METHODS
		//TODO: test both find methods
		
		/** used during the command execution in a specific programming block with a label in it.
		 *  The concrete programming block will look for a concrete slot, which have to be there as the block knows.
		 *  E.g. in an "If" block there is always a boolean slot in its label. The "If" block knows about it, so it can search for the boolean slot to execute its semantics. 
		 *  The first occurrence of the slotclass will be found by this method. 
		 *  
		 *  The slotclass can be everything, what can be appended to the label by using the appendContent method, 
		 *  see {@link #appendContent(Slot)}, {@link #appendContent(String)}, {@link #appendContent(Widget)}
		 */
		@SuppressWarnings("unchecked")
		public <T extends View> T findFirstOccurenceOfSlot(Class<T> slotclass){
			
			T result = null;
			for(Object obj: this.content){
				if (obj.getClass() == slotclass){
					result = (T)obj;
					break;
				}
			}
			return result;
		}
		
		/**
		 * 	used during the command execution in a specific programming block with a label in it.
		 *  The concrete programming block will look for a concrete slot, which have to be there as the block knows.
		 *  E.g. in an "If" block there is always a boolean slot in its label. The "If" block knows about it, so it can search for the boolean slot to execute its semantics. 
		 *  The occurrence is the occurrence of the given Slot to return. Occurrence starts with 1.
		 *  If no Slot of the right class with the right occurrence number is found - returns null.
		 *  
		 *  The slotclass can be everything, what can be appended to the label by using the appendContent method, 
		 *  see {@link #appendContent(Slot)}, {@link #appendContent(String)}, {@link #appendContent(Widget)}
		 *  
		 * @param slotclass		-  the class of the slot
		 * @param occurrence	-  if a number X is passed as parameter here, the method will look for the Xs occurrence of the Class. The counting is started with 1.
		 * @return
		 */
		public <T extends View> T findSlot(Class<T> slotclass, int occurrence){
			int cnt = 1;
			T result = null;
			for(Object obj: this.content){
				if (obj.getClass() == slotclass){
					
					//check if this is the right occurence
					if(cnt == occurrence){
						result = (T)obj;
						break;	
					}
					cnt++;
				}
			}
			return result;
		}
		
		
		
		/*
		 * IMPORTANT: 
		 * each method in the following sequence does the same thing:
		 * - finds the right slot in the current label
		 * - executes the slot to retrieve the value
		 */
		
		public String executeForTextValue(ExecutionHandler<?> handler){
			String result;
			try{
				result = this.findFirstOccurenceOfSlot(SlotDataText.class).executeForValue(handler);
			}catch(NullPointerException e){
				result = ConfigHandler.DEFAULT_VALUE_TEXTFIELD;
			}
			return result;
		}
		public String executeForTextValue(ExecutionHandler handler, int occurence){
			try{
				return this.findSlot(SlotDataText.class, occurence).executeForValue(handler);
			}catch(NullPointerException e){
				return ConfigHandler.DEFAULT_VALUE_TEXTFIELD;
			}
		}
		public double executeForNum(ExecutionHandler handler){
			try{
				return this.findFirstOccurenceOfSlot(SlotDataNum.class).executeForValue(handler);
			}catch(NullPointerException e){
				return ConfigHandler.DEFAULT_VALUE_NUMFIELD;
			}
		}
		public double executeForNum(ExecutionHandler handler, int occurence){
			try{
				return this.findSlot(SlotDataNum.class, occurence).executeForValue(handler);
			}catch(NullPointerException e){
				return ConfigHandler.DEFAULT_VALUE_NUMFIELD;
			}
		}
		public String executeForSpinner(ExecutionHandler handler){
			try{
				return this.findFirstOccurenceOfSlot(SlotDataSpinner.class).executeForValue(handler);
			}catch(NullPointerException e){
				return "";
			}
		}
		public String executeForSpinner(ExecutionHandler handler, int occurence){
			try{
				return this.findSlot(SlotDataSpinner.class, occurence).executeForValue(handler);
			}catch(NullPointerException e){
				return "";
			}
		}
		public boolean executeForBoolean(ExecutionHandler handler){
			try{
				return this.findFirstOccurenceOfSlot(SlotBoolean.class).executeForValue(handler);
			}catch(NullPointerException e){
				return ConfigHandler.DEFAULT_VALUE_BOOLEAN;
			}
		}
		public boolean executeForBoolean(ExecutionHandler handler, int occurence){
			try{
				return this.findSlot(SlotBoolean.class, occurence).executeForValue(handler);
			}catch(NullPointerException e){
				return ConfigHandler.DEFAULT_VALUE_BOOLEAN;
			}
		}

		
		
		
		
//IMPLEMENT HOOKS
		/** Labels have no own shapes, so nothing to initialise. By overriding this we ensure that Block doesn't complain about missing Shape {@link #img} */
		@Override
		protected Shape initiateShapeHere() {
			return null;
		}
		

//EVENTS
		/** A class to encapsulate the Text and the corresponding information, like size and position on the label */
		private class TextContainer implements ScaleEventListener{

			private String text;
			private int unscaledWidth;
			private int unscaledHeight;
			
			//scaledWidth and Height are always up to date because TextContainer listens for ScaleEvents and adopts the scaledWidth and scaledHeight
			protected int scaledWidth;
			protected int scaledHeight;
			
			//scaled position. Text only has scaled positions because it is computed on every onMeasure-onLayout-onDraw cycle.
			protected float scaledX;
			protected float scaledY;
			
			
			protected TextContainer(String str){
				this.text = str;
				
				//measure Text by using the stable Paint, DONT USE textPaint to measure, it changes its textsize regulary
				Rect bounds = new Rect();
				
				//use static Paint to measure, cause we need unscaled values
				ConfigHandler.textPaintLabel.getTextBounds(str, 0, str.length(), bounds); //saves the data in bounds
//				textPaint.getTextBounds(str, 0, str.length(), bounds); //saves the data in bounds
				
				//set width and height
				this.unscaledWidth = bounds.width();
				//TODO: SOMEHOW THE LABEL WITH TEXT ONLY IS MEASURED WRON, SEE SHAPESINGLE. ADDING SOME STATIC OFFSET HELPS, BUT FINDOUT WHY ITS MEASURED WRONG.
//				this.unscaledHeight = Math.round( Math.abs(ConfigHandler.textPaintLabel.ascent()) + ConfigHandler.textPaintLabel.descent() );
				this.unscaledHeight = Math.round( Math.abs(ConfigHandler.textPaintLabel.ascent()) + 4*ConfigHandler.textPaintLabel.descent() );
				
				this.scaledWidth = ScaleHandler.scale(this.unscaledWidth);
				this.scaledHeight = ScaleHandler.scale(this.unscaledHeight);
				
				//if there will be problems with scale synchronisation of text inside of labels - label could call	those methods when ScaleEvent occures
				ScaleHandler.addScaleEventListener(this);
			}
			
			@Override
			public void onScaleEvent(float newscale, Point pivot) {
				scaledWidth = Math.round(unscaledWidth * newscale);
				scaledHeight = Math.round(unscaledHeight * newscale);
				textPaint.setTextSize(TEXT_SIZE*newscale);
			}
		}


		
}

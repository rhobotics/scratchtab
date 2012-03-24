package com.binaryme.ScratchTab.Gui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.binaryme.DragDrop.DragHandler;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.DebugMode;
import com.binaryme.ScratchTab.R;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Config.ConfigHandler;
import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.tools.ColorPalette;
import com.binaryme.tools.M;

/** Can not change the constructor parameters, without loosing the possibility of defining this View in XML. 
 *  For that someone (in this application it's {@link AppRessources}) has to add the needed Activity information, by calling the method {@link BlockPane#init(Activity)}
 *  
 *  OnDragListener - implemented to do block deletetion, when they are dropped into the pane
 *  
 *  */
public class BlockPane extends ListView implements OnDragListener {
	
	/** This Variable sets the scale level, which determines the size of the Blocks in the BlockPane. The scale is the same as for the global zoom.  */
	private float scaleToDrawBlocksInPane = ConfigHandler.scaleLevelForBlocksInPane;
	
	private Activity mContextActivity;
	
	private boolean panelNavGenerated = false;
	
	
	/** The last blocks on the block pane which was touched, should be initialized, when the finger leaves the block pane. 
	 *  This variable will remember the block, which was touched last.  */
//	private static ImageViewAndBlockRepresentation blockForDelayedInitialization;
	
	
	/** This ArrayList contains all BlockGroups to display in the BlockPane. 
	 *  Each BlockGroup is contains instances of {@link BlockRepresentationInBlockpane} which encapsulate all information we may need to draw the Blocks in the BlockPane. */
	private ArrayList<BlockGroup> blockgroups = new ArrayList<BlockGroup>();
	private ArrayList<BlockRepresentationInBlockpane> blockRepresentations = new ArrayList<BlockRepresentationInBlockpane>();
	
	public BlockPane(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
	}
	public BlockPane(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BlockPane(Context context) {
		super(context);
	}
	
	public BlockPane init(Activity contextActivity){
		mContextActivity = contextActivity;
		
		//hide deviders
		this.setDividerHeight(0);
		
		//hide scrollbar
		this.setVerticalScrollBarEnabled(false);
		
		//disable orange selection background on item touch
		//EDIT: orange selection background is useful to visualize which block was chosen
//		this.setSelector(android.R.color.transparent);
				
		//disable black listview bg when scrolling
		this.setCacheColorHint(Color.TRANSPARENT);
				
		//retrieve all available Blocks divided into groups
		this.blockgroups = getAllAvailableBlocksAsGroups();
		
		//collect blocks from all groups into one blockRepresentations ArrayList
		for(BlockGroup blockgroup:blockgroups){
			blockRepresentations.addAll( blockgroup.getBlocks() );
		}
		BlockRepresentationInBlockpane[] blockRepresentationsArray = blockRepresentations.toArray(new BlockRepresentationInBlockpane[]{});
		
		//create an adapter which pulls processes the BlockRepresenations into ListView usable form
		this.setAdapter(new BlockRepresentationAdapter(getContext(), com.binaryme.ScratchTab.R.layout.blockpane_item, blockRepresentationsArray));
		
		//listeners
		this.setOnDragListener(this);

		return this; //needed for the chaining pattern bla(arg).blub(arg2).brr(): BlockPane mypane = new BlockPane().init();
	}
	
	
/*
 * HOOK IN INTO THE MEASUREMENT CYCLE TO INIT THE INSTANCE OF MYSELF WHICH IS SAVED IN APPRESOURCES. THIS HACK IS NEEDED, BECAUSE:
 *  - THIS CLASS NEEDS MEASURED HEIGHT TO GENERATE TYPES NAV, SO GENERATE THE THE MEASUREMENT IS 100% DONE - onMeasure
 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//TODO try getting the right width
		android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
		setMeasuredDimension(lp.width, getMeasuredHeight());
		
		//use the width from the layout parameters. For some reason this value is overridden.  
//		this.setMeasuredDimension(500, getMeasuredHeight());
		if(!panelNavGenerated){
			//generate the navigation for the blockPane
			createBlockTypesNavigation(blockgroups, blockRepresentations.toArray().length);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//because onMeasure is called many times, we will set the flag now, in onDraw, after all measurement is done. Set the flag once and for all times, so that the panel nav is not generated again
		this.panelNavGenerated=true;
	}
	
	
//TOUCH
	/** The pass useful TouchEvent manually to the OnTouch Method */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d("pane","BlockPane dispatch get a touch event "+M.motionEventResolve(ev.getAction()) );
		boolean sup = super.dispatchTouchEvent(ev);
		boolean result = sup;
				
		//the events are passed to the topmost container anyway
		this.onTouchEvent(ev);
		
		return result;
	}
	
	/** Do not intercept the Touch events so that the touch events can reach the BlockPane(subclass of ScrollView) inside of this container and
	 *  horizontal and vertical scrolling is possible at the same time */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    //Call super first because it does some hidden motion event handling
	    super.onInterceptTouchEvent(ev);
	    boolean result = false;
	    return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = super.onTouchEvent(ev);	
		Log.d("pane","BlockPane get a touch event "+M.motionEventResolve(ev.getAction())+" Width is "+getMeasuredWidth() );
		switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				//move to phase 2 again. hide the keyboard.
				M.hideKeyboard(this);
			break;
		}
		return result;
	}
	
	
//HELPER METHODS
	/** Method lists all blocks, which will be displayed in the blockPane */
	private ArrayList<BlockGroup> getAllAvailableBlocksAsGroups(){
		
		ArrayList<BlockGroup> myBlockgroups = new ArrayList<BlockGroup>();
		
		//COMMAND GROUP
			String[] controlBlockClasses={
					"com.binaryme.ScratchTab.Gui.Blocks.Control.HeadStartToken",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.HeadMessageEvent",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.HeadNXTSensorDataLarger",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.HeadNXTSensorDataSmaller",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.If",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.IfElse",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.DoXTimes",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.DoUntil",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.WaitUntilTrue",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.StopAll",
					"com.binaryme.ScratchTab.Gui.Blocks.Control.MessageEventBlock",
			};
			BlockGroup groupOfControlBlocks = new BlockGroup("Control", ColorPalette.colorOfControl, controlBlockClasses);
			myBlockgroups.add(groupOfControlBlocks); //add the new Group to the Groups ArrayList
			
		//TABLET GROUP
			String[] sensorsBlockClasses={
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.Acceleration",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.AngularSpeed",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.Light",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.MagneticField",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.Orientation",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.SignalSoundFrequency",
					"com.binaryme.ScratchTab.Gui.Blocks.Tablet.SignalScreenflash",
			};
			BlockGroup groupOfSensorsBlocks = new BlockGroup("Tablet", ColorPalette.colorOfTablet, sensorsBlockClasses);
			myBlockgroups.add(groupOfSensorsBlocks); //add the new Group to the Groups ArrayList
			
		//ROBOT GROUP
			String[] robotBlockClasses={
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.SensorData",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.DriveForward",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.DriveForwardLimited",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.DriveBack",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.DriveBackLimited",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.TurnRight",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.TurnLeft",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.RotateLeft",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.RotateRight",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.TurnAround",
					"com.binaryme.ScratchTab.Gui.Blocks.Robot.StopAllMotors",
			};
			BlockGroup groupOfRobotBlocks = new BlockGroup("Robot", ColorPalette.colorOfRobot, robotBlockClasses);
			myBlockgroups.add(groupOfRobotBlocks); //add the new Group to the Groups ArrayList
			
			//LOGIC GROUP
			String[] logicBlockClasses={
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.And",
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.Or",
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.Negate",
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.More",
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.Equals",
					"com.binaryme.ScratchTab.Gui.Blocks.Logic.Less",
			};
			BlockGroup logicBlockBlocks = new BlockGroup("Logic", ColorPalette.colorOfLogic, logicBlockClasses);
			myBlockgroups.add(logicBlockBlocks); //add the new Group to the Groups ArrayList
			
			//NUMBERS GROUP
			String[] numbersBlockClasses={
					"com.binaryme.ScratchTab.Gui.Blocks.Numbers.Multiply",
					"com.binaryme.ScratchTab.Gui.Blocks.Numbers.Divide",
					"com.binaryme.ScratchTab.Gui.Blocks.Numbers.Plus",
					"com.binaryme.ScratchTab.Gui.Blocks.Numbers.Minus",
			};
			BlockGroup numbersBlockBlocks = new BlockGroup("Numbers", ColorPalette.colorOfNumbers, numbersBlockClasses);
			myBlockgroups.add(numbersBlockBlocks); //add the new Group to the Groups ArrayList
		
		return myBlockgroups;
	}
	
	
	/**  Creates a block group navigation left to the block pane, made of many objects of type {@link NavSegment}. 
	 *   This navigation is for faster choosing of blocks in the block pane. */
	private void createBlockTypesNavigation(ArrayList<BlockGroup> blockgroups, final float totalBlockCount){
		LinearLayout wrapper = (LinearLayout) mContextActivity.findViewById(R.id.blockTypesNavigation);
		
		//remove the old navigation fragments, if some of them were inserted during the last measuring cycles.
		wrapper.removeAllViews();
		
		//retrieve the available space, which we will divide among the blockGouprs
		final int wrapperwidth  = wrapper.getMeasuredWidth();
		final int wrapperheight = wrapper.getMeasuredHeight();
		
		//how much vertical space should represent one block?
		final float spaceForOneBlock = ((float)wrapperheight)/(totalBlockCount);
		
		
		//now create a navigation area for each blockgroup 
		int firstGroupItemInGlobalList = 0;
		for(BlockGroup blockgroup:blockgroups){
			int fragmentheight = Math.round(blockgroup.count*spaceForOneBlock);
			int fragmentwidth = wrapperwidth;
			
			View navSegment = new NavSegment(mContextActivity,
												this, 
												firstGroupItemInGlobalList, 
												blockgroup.count,
												fragmentwidth, 
												fragmentheight,
												blockgroup.blockGroupColor,
												blockgroup.blockGroupName);
			firstGroupItemInGlobalList=firstGroupItemInGlobalList + blockgroup.count;
			wrapper.addView(navSegment);
		}
		
		//redraw the navigation
		wrapper.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		wrapper.invalidate();
	}
	
	
	
//IMPLEMENT INTERFACES
	@Override
	public boolean onDrag(View v, DragEvent event) {
		boolean result = false;
		
		Log.d("blockpane","Drag event "+M.dragEventResolve(event.getAction()) );
		
		switch(event.getAction()){
			case DragEvent.ACTION_DRAG_STARTED :
				result=true; //BlockPane accepts all blocks, independently of its type.
				break;
			
			case DragEvent.ACTION_DRAG_EXITED :
				break;//no reaction
				
			case DragEvent.ACTION_DRAG_ENTERED :
				break;//no reaction
				
			case DragEvent.ACTION_DRAG_LOCATION :
				break; //no reaction
				
			case DragEvent.ACTION_DROP :
				//destroy the block on Panel drop
				DragHandler.executeBlockDestruction();
				result=true;
				break;
				
			case DragEvent.ACTION_DRAG_ENDED :
				break; //no reaction
		}
		return result; //default result is false
		
	}
	

	
	
	
	
	
//////////////////////////////////////////////////////////////////////////	
////////////////INNER CLASSES/////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
	
	/////////////////////
	///////CLASS/////////
	/////////////////////
		/** BlockRepresentationAdapter is responsible for processing of data, inside of the {@link BlockPane#blockgroups} Array, in order to display it in a ListView */  
		private class BlockRepresentationAdapter extends ArrayAdapter<BlockRepresentationInBlockpane>{
			private BlockRepresentationInBlockpane[] myBlockRepresentationsArray;
			private LayoutInflater mInflater;
			private int mListViewItemLayoutResourceId;
			
			public BlockRepresentationAdapter(Context context, int listViewItemLayoutResourceId, BlockRepresentationInBlockpane[] blockrepresentations) {
				super(context, listViewItemLayoutResourceId, blockrepresentations);
				
				//remember the array with BlockGroups, which is the information source for this Adapter
				myBlockRepresentationsArray=blockrepresentations;
				
				//remember the Inflater to create ListViewItems-Layouts from XML in getView
				mInflater = (LayoutInflater)context.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				
				//remember the ListViewItems-Layout id			
				mListViewItemLayoutResourceId = listViewItemLayoutResourceId;
			}
			
			@Override
			public int getCount() {
				return myBlockRepresentationsArray.length;
			}
	
			@Override
			public BlockRepresentationInBlockpane getItem(int position) {
				return myBlockRepresentationsArray[position];
			}
	
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			/*
			 * This method should create a new View 
			 * 	- of the Type as defined in generic scopes of the ArrayAdapter, which this class is extending
			 *  - by inflating the recourse with the id, which was passed to this classes constructor 
			 */
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				//create a view by using the given XML resource id
				convertView = mInflater.inflate(mListViewItemLayoutResourceId, null);
				
				//find the imageView, nested inside the layout with the given resource 
				BlockPaneImageView iv = (BlockPaneImageView)convertView.findViewById(R.id.blockPaneImageView);
				iv.setImageBitmap(this.getItem(position).blockimage);
				
				//saving a pointer to the current BlockRepresentationInBlockPane into the given ImageView ImageViewAndBlockRepresentation, so that it can instantiate the Block on dopbleTap
				iv.setBlockRepresentationInBlockpane(this.getItem(position));
				
				return convertView;
			}
		}
	
	/////////////////////
	///////CLASS/////////
	/////////////////////
		/** Class represents a Block and contains all necessary information to draw a Block on a Block Pane. 
		 *  {@link BlockRepresentationInBlockpane} Objects are grouped in containers of type {@link BlockGroup} */
		public class BlockRepresentationInBlockpane extends View {
			public String classpath;
			public Bitmap blockimage;
			
			public BlockRepresentationInBlockpane(Context context, String classpath, Bitmap blockimage) {
				super(context);
				this.blockimage = blockimage;
				this.classpath = classpath;
			}
			public String getBlockRepresentationInBlockpaneClassPath(){
				return classpath;
			}
		}//end of class BlockRepresentationInBlockpane
	
	
	/////////////////////
	///////CLASS/////////
	/////////////////////
		/** Class represents a ScratchTab group of Blocks which are visually grouped by having the same background color. */
		private class BlockGroup{
			private String blockGroupName;
			private int blockGroupColor;
			private ArrayList<BlockRepresentationInBlockpane> blocksInGroup = new ArrayList<BlockRepresentationInBlockpane>();
			private int count=0;
			
			BlockGroup(String blockGroupName, int blockGroupColor, String[] blockClasses){
				this.blockGroupColor = blockGroupColor;
				this.blockGroupName = blockGroupName;
				addBlocks(blockClasses);
			}
	
			//GETTER
				protected ArrayList<BlockRepresentationInBlockpane> getBlocks(){
					return this.blocksInGroup;
				}
			
			
			//INTERACTION
			/** Takes an array with block Class names as Strings, adds a representation of each Block class to the BlockGroup. */
			protected void addBlocks(String[] blockClasses){
				
				//TODO: set the right Scale
				float oldScale = ScaleHandler.getScale();
				ScaleHandler.setScale(scaleToDrawBlocksInPane, new Point(0,0), this);
				
				for(String blockClass:blockClasses){
					try{
						//get an Object of Type "Class" by full name
						Class<?> c = Class.forName(blockClass);		
						
						//get the constructor, parametrized with the Activity 
						Constructor<?> activityConstructor = c.getConstructor(new Class[]{Activity.class});
						
						//use the constructor BlockClass(Activity activity) to instantiate the block
						Object nextBlockObject = activityConstructor.newInstance(new Object[]{mContextActivity});
						
						if(!(nextBlockObject instanceof Block<?>))continue;
						
						//every object in the block pane is a draggable block, we will insert nothing else
						Block<Shape> nextBlock = (Block<Shape>)nextBlockObject;
	
						//do the block drawing cycle to configure make the block drawable. 
						nextBlock.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
						nextBlock.onLayout();
						nextBlock.invalidateTree();
						
						int width = nextBlock.getMeasuredWidth();
						int height = nextBlock.getMeasuredHeight();
						
						//create a new Bitmap, where we can store the new Block represenation.
						Bitmap nextBlockBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
						Canvas nextBlockCanvas = new Canvas(nextBlockBitmap);
						nextBlock.draw(nextBlockCanvas); //now nextBlockBitmap has the Block's Shape on it
						
						
						//create the new representation of the block
						BlockRepresentationInBlockpane nextBlockPaneRepresentation = new BlockRepresentationInBlockpane(mContextActivity, blockClass, nextBlockBitmap);
						
						//add the block representation to the current group
						blocksInGroup.add(nextBlockPaneRepresentation);
						
						count++; // add 1 more Block to this group
					
					}catch(Exception e){
						Log.e("MyApplication", "exception", e);
						if(DebugMode.userErrorsOn){
							AppRessources.popupHandler.popError("Could not retrieve the Block "+M.getClassName(blockClass)+" to insert append it to the block pane.");
						}
					}
				}//end of for loop
				
				//restore the scale
				ScaleHandler.setScale(oldScale, new Point(0,0), this);
			}//end of addBlocks()
			
		} //end of class BlockGroup
	
		
		
		
		
		
		
		
		
		
	/////////////////////
	///////CLASS/////////
	/////////////////////
		/** Draws a segment of blockTypesNavigation left to the Block Pane, which scrolls the ListView to the right group on touch. */
		private class NavSegment extends View implements OnTouchListener{
			
			private ListView willScrollThat;
			private int firstGroupItemNumber;
			private int blockCountInGroup;
			
			private float spacePerItem;
			
			private int width;
			private int height;
			
			private int fillcolor;
			private String text;
			private int textpadding = 4;
			
			private Paint textPaint = new Paint();
	
			/** 
			 * Use to create a navigation fragment for scrolling directly to some predefined group of items on touch.
			 * 
			 * @param context - Application Context.
			 * @param listView - The scrollView, which this navigationFragment will control
			 * @param firstGroupItemNumber - In the ListView All items are listed in one Row. This navigation fragment will let the given ListView scroll to some predefined Items in the ListView. firstGroupItemNumber is the <b>first</b> item's position in total list of item.  
			 * @param blockCountInGroup    - How man< blocks are in the current group?
			 * @param width  - given width
			 * @param height - given height
			 * @param fillcolor - background color, normally equals to the BlockType color, e.g. Control Blocks are orange
			 * @param text - the text, which should be written on this navigation fragment, normally equals to the group name, e.g. "control"
			 */
			public NavSegment(Context context, ListView listView, int firstGroupItemNumber, int blockCountInGroup, int width, int height, int fillcolor, String text) {
				super(context);
				this.willScrollThat = listView;
				this.firstGroupItemNumber = firstGroupItemNumber;
				this.blockCountInGroup = blockCountInGroup;
				this.width=width;
				this.height=height;
				this.fillcolor=fillcolor;
				this.text=text;
				
				//paint
				this.textPaint = new Paint(ConfigHandler.textPaintBlockTypeNavigation);
				
				//calculate
				if(blockCountInGroup!=0) 	this.spacePerItem = height/blockCountInGroup;
				else 						this.spacePerItem = 0;
				
				//listeners
				this.setOnTouchListener(this);
			}
			

			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				setMeasuredDimension(this.width, this.height);
			}
			
			@Override
			protected void onDraw(Canvas canvas) {
				canvas.drawColor(this.fillcolor);
				
				if(DebugMode.on){
					Paint redpaint = new Paint();
					redpaint.setColor(Color.RED);
					redpaint.setStrokeWidth(5);
					for(int i=1; i<=blockCountInGroup; i++ ){
						int myY = Math.round(i*spacePerItem);
						canvas.drawLine(1, myY, width, myY, redpaint);
					}
				}
	
				//measure the text
				Rect bounds = new Rect();
				this.textPaint.getTextBounds(text, 0, text.length(), bounds);
				//add padding
				bounds.set(bounds.left, bounds.top, bounds.right+this.textpadding, bounds.bottom);
				
				//TEXTSTYLE
					//make text darker
					int darkerFillColor = ColorPalette.makeDarker(fillcolor, 0.6f);
					textPaint.setColor(darkerFillColor);
				
					//centering the text
					int centeredXInFragment = (width+bounds.height())/2;
					int textMoveDownY = bounds.width();
					
					//add gradient, which will make text disappear slowly  to the text, if the text is too long for the current fragment
					if(height<bounds.width()){
					    Shader textShader=new LinearGradient(0, 0, -bounds.width(), 0,
					    		new int[]{fillcolor,darkerFillColor},
					            new float[]{0, 1}, TileMode.CLAMP);
					    textPaint.setShader(textShader);
					    
					    //adopt the text-move-down offset, so that text wont bounce against the bottom border
					    textMoveDownY=height-textpadding;
					}
					
				//write the text, rotated by 90 degrees on the canvas
				canvas.rotate(-90);
				canvas.drawText(text, -textMoveDownY, centeredXInFragment, textPaint);
			}
			
	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//the coordinates are relative to the current fragment view
				float x =event.getX();
				float y =event.getY();
				
				//calculate to which Item we gonna scroll
				int item = Math.round(y/spacePerItem)+this.firstGroupItemNumber;
				
				this.willScrollThat.smoothScrollToPosition(item);
				this.willScrollThat.smoothScrollToPositionFromTop(item, 10, 100);
				return true;
			}
			
		} //end of NavSegment



}

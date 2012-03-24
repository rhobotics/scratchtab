package com.binaryme.ScratchTab;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.binaryme.LayoutZoomable.AbsoluteLayoutPinchzoomable;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.ScratchTab.Gui.BlockPane;
import com.binaryme.ScrollViewDual.ExternalScrollbar;
import com.binaryme.ScrollViewDual.mHorizontalScrollView;
import com.binaryme.tools.M;



public class ApplicationController extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //register a handler for uncaught exceptions first
        Thread.setDefaultUncaughtExceptionHandler(new ScratchTabDefaultUncaughtExceptionHandler());
        
        //initiate the CM to Pixel proportions for this device. Should happen before seting the contentview, because Views will use the initiated Metrics.
        com.binaryme.tools.M.initMetrics(this.getWindowManager());
        
        setContentView(R.layout.main);
        
        //TODO commented: start the artificial initialization of static methods
        //StaticInitializer.startStaticInitialization(this);
        //initialize everything, what needs initialization on app start. The order of initialization is important. 
		new com.binaryme.tools.ColorPalette().onApplicationStart(this);
		new com.binaryme.DragDrop.DragHandler().onApplicationStart(this);
		new com.binaryme.ScratchTab.Config.ConfigHandler().onApplicationStart(this);
		new com.binaryme.ScratchTab.Config.AppRessources().onApplicationStart(this);
        
        //register an external scroll bar for the horizontal scroll view
        mHorizontalScrollView horizontalScrollV = (mHorizontalScrollView) findViewById(R.id.horizontalScrollView);
        ExternalScrollbar externalScrollBar = (ExternalScrollbar) findViewById(R.id.horizontalScrollViewExternalBar);
        horizontalScrollV.setExternalScrollbar(externalScrollBar);
        
        //TODO test hide the keyboard till an editview is touched
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
        //calculate the default Panel proportions, depending on resolution and screen size
        initGuiSizes();
        
        //TODO - delete this widget testing method call
        addmywidget();
        
        //TODO temporary run this, for demo in the lab
        addTestBlocksToWorkspace();
    }
    
    @Override
    protected void onPause() {
    	AppRessources.bluetoothHandler.requestDisconnectAll();
    	super.onPause();
    }
    
    
//APPLICATION LOGIC
    
    
    //TODO - delete this widget method
    private void addmywidget(){
    	com.binaryme.LayoutZoomable.AbsoluteLayoutPinchzoomable workspace = (AbsoluteLayoutPinchzoomable) findViewById(R.id.workspace);
    	
    	
    	//FUNKTIONIERT EINIGERMASSEN
		
//		1. TextField Widget
//		WidgetTextField widgetTextField = new WidgetTextField(this);
//		widgetTextField.setUnscaledPosition(0, 0);
//		
//		workspace.addView(widgetTextField);
    	
    	
    	
//    	2. SlotData, with a textfield
//    	SlotDataText slotDataNum = new SlotDataText(this);
//    	slotDataNum.setPosition(10, 0);
//    	
//    	workspace.addView(slotDataNum);
    	
    	
    	
//    	3. Spinner
//		ArrayList<String> spinnerArray = new ArrayList<String>();
//		spinnerArray.add("90 Grad aaaaa");
//		spinnerArray.add("180 Grad");
//		
//		SlotDataSpinner spinnerslot = new SlotDataSpinner(this, spinnerArray);
//		workspace.addView( spinnerslot );

    	
    }
    
    
    //TODO temporary let an 
    private void addTestBlocksToWorkspace(){
    	
//    	ListView lv = (ListView) findViewById(R.id.blockPane);
    	
//    	View block1 = new tempRect(this);
//		AbsoluteLayout.LayoutParams lp1 = new AbsoluteLayout.LayoutParams(M.cm2px(2), M.cm2px(2), M.cm2px(2), M.cm2px(2));
//    	block1.setLayoutParams(lp1);

//    	View block2 = new tempRect(this);
//    	AbsoluteLayout.LayoutParams lp2 = new AbsoluteLayout.LayoutParams(200, 200, 300, 500);
//    	block2.setLayoutParams(lp2);
    	
//    	Block block3 = new Block(this);
//    	block3.initDimensionAndPosition( M.cm2px(2), M.cm2px(2), M.cm2px(3), M.cm2px(3));

//    	Block blockIfElse = new IfElse(this);
//    	blockIfElse.initDimensionAndPosition( M.cm2px(4), M.cm2px(4), M.cm2px(3), M.cm2px(3));
    	
//    	Block<?> blockIf = new If(this);
//    	blockIf.setPosition(M.cm2px(3), M.cm2px(3));
//    	
//    	Block<?> blockIf2 = new IfTestDBlue(this);
//    	blockIf2.setPosition(M.cm2px(1), M.cm2px(5));
//
//    	Block<?> blockIf3 = new IfTestLBlue(this);
//    	blockIf3.setPosition(M.cm2px(9), M.cm2px(7));
//    	
//    	Block<?> blockRobot = new DriveForward(this);
//    	blockRobot.setPosition(M.cm2px(3), M.cm2px(1));
    	
//    	Label blockLabel = new Label(this);
//    	blockLabel.appendContent("TestString ein sehr sehr sehr sehr sher sehr langer String");
//    	blockLabel.appendContent("Zweiter String!");
//    	blockLabel.initPosition(M.cm2px(4), M.cm2px(2));
    	
    	
    	com.binaryme.LayoutZoomable.AbsoluteLayoutPinchzoomable workspace = (AbsoluteLayoutPinchzoomable) findViewById(R.id.workspace);
//    	workspace.addView(block1);
//    	workspace.addView(block2);
//    	workspace.addView(block3);
//    	workspace.addView(blockIfElse);
//    	workspace.addView(blockIf);
//    	workspace.addView(blockIf2);
//    	workspace.addView(blockIf3);
//    	workspace.addView(blockLabel);
//    	workspace.addView(blockRobot);
    	
    	
    	workspace.invalidate();
    }
    
    
    /**
     * Initialises custom metric units and resizes the GUI Elements
     */
    private void initGuiSizes(){
    	//BLOCKANE
	        //set width of the block pane Wraper - the Relative layout - to 5cm
	        int widthBlockPaneWraper = Math.round( 5.5f*M.CMinPx ); //325 px
	        View blockPaneWraper = findViewById(R.id.blockPaneWraper);
	        ViewGroup.LayoutParams lpblockPaneWraper = blockPaneWraper.getLayoutParams();
	        lpblockPaneWraper.width = widthBlockPaneWraper;
	        blockPaneWraper.setLayoutParams(lpblockPaneWraper);
	
	        //set width of the block group navigation to 0.5cm
	        int widthBlockGroupNav = Math.round( 0.5f*M.CMinPx ); //30px
	        View blockTypesOverview = findViewById(R.id.blockTypesNavigation);
	        ViewGroup.LayoutParams lpBlockGroupNav = blockTypesOverview.getLayoutParams();
	        lpBlockGroupNav.width = widthBlockGroupNav;
	        blockTypesOverview.setLayoutParams(lpBlockGroupNav);
	        
	        //TODO: use onMeasure in BlockPane
	        //set width of the Block Pane container - the com.binaryme.ScratchTab.Gui.BlockPane - to 8cm
	        int widthBlockPane = Math.round( 7.5f*M.CMinPx );
	        BlockPane blockPane = (BlockPane) findViewById(R.id.theBlockPanel);
	        android.view.ViewGroup.LayoutParams lpbBlockPane =  blockPane.getLayoutParams();
	        lpbBlockPane.width = widthBlockPane;
	        blockPane.setLayoutParams(lpbBlockPane);
	        
	        //set width of the horizontal scroller which wraps the BlockPane
	        int widthBlockContainer = Math.round( 5f*M.CMinPx );  //295 px
	        View blockPaneHorizontalScroller = findViewById(R.id.blockPaneHorizontalScrollview);
	        ViewGroup.LayoutParams lpHorScroller = blockPaneHorizontalScroller.getLayoutParams();
	        lpHorScroller.width = widthBlockContainer;
	        blockPaneHorizontalScroller.setLayoutParams(lpHorScroller); 
        
        
        //set height of the title bar to 1 cm
        int heighttitleBar = Math.round( 1f*M.CMinPx );
        View titleBar = findViewById(R.id.titleBar);
        ViewGroup.LayoutParams lpTitleBar = titleBar.getLayoutParams();
        lpTitleBar.height = heighttitleBar;
        titleBar.setLayoutParams(lpTitleBar);
        
        //set Workspace Height to 1x Screnwidth / minimum scale level, so that minimum scaled screen is fills the screen
        int workspaceWidth = Math.round( M.screenWpx / ScaleHandler.getMinScale() );
        View workspace = findViewById(R.id.workspace);
        ViewGroup.LayoutParams lpworkspace = workspace.getLayoutParams();
        lpworkspace.width = workspaceWidth;
        workspace.setLayoutParams(lpworkspace);
        
    }
}
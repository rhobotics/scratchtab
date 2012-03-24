package com.binaryme.ScratchTab.Gui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.binaryme.Bluetooth.InterfaceGeneralConnectionListener;
import com.binaryme.LayoutZoomable.ScaleEventListener;
import com.binaryme.LayoutZoomable.ScaleHandler;
import com.binaryme.ScratchTab.Config.AppRessources;
import com.binaryme.tools.M;

public class TitleBar extends RelativeLayout implements InterfaceGeneralConnectionListener {
	
	private SeekBar zoomSlider;
	private StartToken startbutton;
	private StopToken stopbutton;
	private ButtonConnection bluetoothbutton;
	private ImageButton closekeyboardbutton;
	
	private int legoNXTBluetoothDeviceClass=2052; // id needed to connect the bluetooth device

	public TitleBar(Context context, AttributeSet attrs,
			int defStyle) {
			super(context, attrs, defStyle);
			init();
		}

		public TitleBar(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}
		public TitleBar(Context context) {
			super(context);
			init();
		}
		private void init(){
			
			//SET SIZE
		        int heighttitleBar = Math.round( 1f*M.CMinPx );
		        ViewGroup.LayoutParams lpTitleBar = this.getLayoutParams();
		        if(lpTitleBar==null){
		        	lpTitleBar=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heighttitleBar);
		        }
		        lpTitleBar.height = heighttitleBar;
		        this.setLayoutParams(lpTitleBar);

		        
		        
		    //0. ADD ZOOM SLIDER
		        zoomSlider = new MySeekbar(getContext());
		        zoomSlider.setId(M.getuniqueId());
		        
		        // Defining the layout parameters of the SeekBar
		        RelativeLayout.LayoutParams lpzoomSlider = new RelativeLayout.LayoutParams(
		                RelativeLayout.LayoutParams.WRAP_CONTENT,
		                RelativeLayout.LayoutParams.WRAP_CONTENT);
		        lpzoomSlider.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        lpzoomSlider.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		        lpzoomSlider.height = heighttitleBar;
		        lpzoomSlider.width =  Math.round( 5.5f*M.CMinPx );
		        
		        // Setting the parameters on the StartToken
		        zoomSlider.setLayoutParams(lpzoomSlider);
	
		        // Adding the TextView to the RelativeLayout as a child
		        this.addView(zoomSlider);
		        
		        
			
	        //1. ADD START BUTTON
		        startbutton = new StartToken(getContext());
		        startbutton.setId(M.getuniqueId());
	
		        // Defining the layout parameters of the TextView
		        RelativeLayout.LayoutParams lpstartbutton = new RelativeLayout.LayoutParams(
		                RelativeLayout.LayoutParams.WRAP_CONTENT,
		                RelativeLayout.LayoutParams.WRAP_CONTENT);
		        lpstartbutton.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        lpstartbutton.addRule(RelativeLayout.RIGHT_OF, zoomSlider.getId());
	
		        // Setting the parameters on the StartToken
		        startbutton.setLayoutParams(lpstartbutton);
	
		        // Adding the TextView to the RelativeLayout as a child
		        this.addView(startbutton);

		        
		        
		        
		    //3. ADD KEYBOARD CLOSING BUTTON TO THE CORNER
//		        closekeyboardbutton = new ImageButton(getContext());
//		        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
//		        		com.binaryme.ScratchTab.R.drawable.hidekeyboard);
//		        closekeyboardbutton.setImageBitmap(icon);
//		        closekeyboardbutton.setId(M.getuniqueId());
//		        
//		        // Defining the layout parameters of the TextView
//		        RelativeLayout.LayoutParams lpclosekeyboardbutton = new RelativeLayout.LayoutParams(
//		                RelativeLayout.LayoutParams.WRAP_CONTENT,
//		                RelativeLayout.LayoutParams.WRAP_CONTENT);
//		        lpclosekeyboardbutton.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		        lpclosekeyboardbutton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		        lpclosekeyboardbutton.height=heighttitleBar;
//		        lpclosekeyboardbutton.width=heighttitleBar*2;
		        
//		        // Setting the parameters on the StartToken
//		        closekeyboardbutton.setLayoutParams(lpclosekeyboardbutton);
//		        
//		        // Adding the TextView to the RelativeLayout as a child
//		        this.addView(closekeyboardbutton);
//		        
//		        //add button functionality
//		        closekeyboardbutton.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						//close send the Keyboardclosing event
//						M.hideKeyboard(closekeyboardbutton);
//					}
//				});
//		        
//			//LISTENERS
//		        AppRessources.bluetoothHandler.addGeneralConnectionListener(this);  
		      
		        
		        
		    //4. ADD BLUETOOTH BUTTON
		        bluetoothbutton = new ButtonConnection(getContext());
		        bluetoothbutton.setId(M.getuniqueId());
		        
		        // Defining the layout parameters of the TextView
		        RelativeLayout.LayoutParams lpbluetoothbutton = new RelativeLayout.LayoutParams(
		                RelativeLayout.LayoutParams.WRAP_CONTENT,
		                RelativeLayout.LayoutParams.WRAP_CONTENT);
		        lpbluetoothbutton.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        lpbluetoothbutton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		        lpbluetoothbutton.addRule(RelativeLayout.LEFT_OF, closekeyboardbutton.getId());
		        lpbluetoothbutton.height=heighttitleBar;
		        lpbluetoothbutton.width=heighttitleBar*2;
		        
		        // Setting the parameters on the StartToken
		        bluetoothbutton.setLayoutParams(lpbluetoothbutton);
		        
	
		        // Adding the TextView to the RelativeLayout as a child
		        this.addView(bluetoothbutton);
		        
		        //add button functionality
		        bluetoothbutton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(bluetoothbutton.isChecked()){
							//on - connection is now enabled, will disable it
							AppRessources.bluetoothHandler.requestConnectionTo(legoNXTBluetoothDeviceClass);
						}else{
							//off - connection is now disabled, will enable it
							AppRessources.bluetoothHandler.requestDisconnectAll();
						}
					}
				});
		        
			//LISTENERS
		        AppRessources.bluetoothHandler.addGeneralConnectionListener(this);
		        
		        
			//5. ADD STOP BUTTON
		        stopbutton = new StopToken(this.getContext());
		        stopbutton.setId(M.getuniqueId());
		        
		        // Defining the layout parameters of the TextView
		        RelativeLayout.LayoutParams lpsstopbutton = new RelativeLayout.LayoutParams(
		        		RelativeLayout.LayoutParams.WRAP_CONTENT,
		        		RelativeLayout.LayoutParams.WRAP_CONTENT);
		        lpsstopbutton.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		        lpsstopbutton.addRule(RelativeLayout.LEFT_OF, bluetoothbutton.getId());
		        
		        // Setting the parameters on the StartToken
		        stopbutton.setLayoutParams(lpsstopbutton);
		        
		        // Adding the TextView to the RelativeLayout as a child
		        this.addView(stopbutton);
		        
		        

		}
		

        
		@Override
		public void onConnected(BluetoothDevice device) {
			bluetoothbutton.setChecked(true);
			bluetoothbutton.setClickable(true);
		}

		@Override
		public void onConnectionProcessStarted(BluetoothDevice device) {
			bluetoothbutton.setClickable(false);
			bluetoothbutton.setBlinkingOn();
		}

		@Override
		public void onDisconnected(BluetoothDevice device) {
			bluetoothbutton.setChecked(false);
			bluetoothbutton.setClickable(true);
		}
		
		
		class MySeekbar extends SeekBar implements OnSeekBarChangeListener, ScaleEventListener{
			
			private boolean onProcessChangedLocked=false;
			
			public MySeekbar(Context context) {
				super(context);

				//listeners
				this.setOnSeekBarChangeListener(this);	 	//read progress values
				ScaleHandler.addScaleEventListener(this);	//update value if scale is triggered by somethin else
				
				/* seekbar should start with ScaleHandler.getMinScale() but android seekbar supports only 0 as min value,
				 * 	so we shift the scala to the left by reducing the maxScale by minScale.
				 * 
				 * seekbar supports integer only, so we multiply the scala by 100 to get 2 decimal digits precision
				 * 	on scala reading, we will divide the scala value by 100
				 */
				this.setMax((int) (ScaleHandler.getMaxScale() - ScaleHandler.getMinScale()) *100  );
				
				//set the start value, multiply by 100 to get the 2 decimal digits precision
				this.setProgress(Math.round(ScaleHandler.getScale()*100));
			}
			
			private float progress2Scale(int androidprogress){
				//get the 2 decimal digits precision
				float scaleProgress = ((float)androidprogress)/100f;
				//move the scala, to set ScaleHandler.getMinScale() as a minimum
				scaleProgress += ScaleHandler.getMinScale(); 
				return scaleProgress;
			}
			private int scale2Progress(float scale){
				//move the scala, to set ScaleHandler.getMinScale() as a minimum
				float androidProgressSmall = scale-ScaleHandler.getMinScale() ;
				//get the 2 decimal digits precision
				int androidProgress = Math.round( androidProgressSmall*100 );
				return androidProgress;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(!onProcessChangedLocked){
					ScaleHandler.setScale(progress2Scale(progress), M.getScreenCenter(), this);
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScaleEvent(float newscale, Point pivot) {
				//do not trigger on processChanged
				this.onProcessChangedLocked=true;
				this.setProgress(scale2Progress(newscale));
				this.onProcessChangedLocked=false;
			}
		}
		
		
}

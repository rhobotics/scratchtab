package com.binaryme.ScratchTab.Gui;

import android.app.Activity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.binaryme.ScratchTab.DebugMode;
import com.binaryme.ScratchTab.R;
import com.binaryme.tools.M;

public class PopupHandler {

	private static Activity mContext=null;
	
	public PopupHandler(Activity context){
		mContext = context;
	}
	
	/**
	 * Method  to display a popup message.
	 * @param message - The message as String.
	 */
	public void pop(final String message){
		//TODO: temporary use the toast for displaying infos
		//run feedback methods on the UI thread, if we are here, than no Exception occurred during the execution - yuppiaieee!
		this.mContext.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				//if we are here, than an Exception has occurred during the execution!
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	/**
	 * Method  to display a popup message.
	 * @param message - The message as String.
	 * @param x - the X coordinate on the Workspace.
	 * @param y - the Y coordinate on the Workspace.
	 */
	public void pop(final String message, int x, int y){
		//TODO: temporary use the toast for displaying infos
		//run feedback methods on the UI thread, if we are here, than no Exception occurred during the execution - yuppiaieee!
		this.mContext.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				//if we are here, than an Exception has occurred during the execution!
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
//	/** Method  to display an Error message. */
//	public synchronized static void popError(String message){
////		Dialog dialog = new Dialog(mContext);
////
////		dialog.setContentView(R.layout.toast_error);
////		dialog.setTitle(R.string.error_dialogue_title);
//		
//		LayoutInflater inflater = mContext.getLayoutInflater();
//		View toastRoot = inflater.inflate(R.layout.toast_error, null);
//		
//		//setting the right message
//		TextView textview = (TextView) toastRoot.findViewById(R.id.toast_error_message);
//		InputFilter[] fArray = new InputFilter[1];
//		fArray[0] = new InputFilter.LengthFilter(M.getScreenCenter().x); //half of the screen should be the max width
//		textview.setFilters(fArray);
//		
//		textview.setText(message);
//
////		TextView text = (TextView) dialog.findViewById(R.id.toast_error_message);
////		text.setText(message);
//		
//		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//		builder.setView(toastRoot);
//		AlertDialog alertDialog = builder.create();
//		alertDialog.setCancelable(true);
//		alertDialog.show();
//		
//	}
	
	/** Method  to display an Error message. */
	public synchronized void popError(String message){
		if(DebugMode.userErrorsOn){
			LayoutInflater inflater = mContext.getLayoutInflater();
			View toastRoot = inflater.inflate(R.layout.toast_error, null);
			
			Toast toast = new Toast(mContext);
			toast.setView(toastRoot);
			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			
			//setting the right message
			TextView textview = (TextView) toastRoot.findViewById(R.id.toast_error_message);
			InputFilter[] fArray = new InputFilter[1];
			fArray[0] = new InputFilter.LengthFilter(M.getScreenCenter().x); //half of the screen should be the max width
			textview.setFilters(fArray);
			
			textview.setText(message);
			fireLongToast(toast);
		}
	}
	
	private static void fireLongToast(final Toast toast) {
        Thread t = new Thread() {
            public void run() {
                int count = 0;
                try {
                    while (true && count < 3) {
                        toast.show();
                        sleep(1850);
                        count++;

                        // do some logic that breaks out of the while loop
                    }
                } catch (Exception e) {
                    //nothing to do
                }
            }
        };
        t.start();
    }

}

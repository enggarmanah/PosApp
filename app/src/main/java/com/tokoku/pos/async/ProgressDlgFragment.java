package com.tokoku.pos.async;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.NotificationUtil;

public class ProgressDlgFragment extends DialogFragment {
	
	private static final String PROGRESS_PERCENTAGE = "PROGRESS_PERCENTAGE";
	private static final String PROGRESS_MESSAGE = "PROGRESS_MESSAGE";
	
	ProgressBar mDataSyncPb;
	TextView mSyncMessage;
	
	String mMessage = Constant.EMPTY_STRING;
	int mProgress = 0;
	
	private boolean miShowCancellationMessage = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		
		if (savedInstanceState != null) {
			
			mProgress = savedInstanceState.getInt(PROGRESS_PERCENTAGE);
			mMessage = savedInstanceState.getString(PROGRESS_MESSAGE);
		}
		
		setCancelable(false);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putInt(PROGRESS_PERCENTAGE, mProgress);
		outState.putString(PROGRESS_MESSAGE, mMessage);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.data_sync_progress_fragment, container, false);
		
		return view;
	}
	
	@Override
	public void onStart() {

		super.onStart();
		
		mDataSyncPb = (ProgressBar) getView().findViewById(R.id.dataSyncPb);
		mSyncMessage = (TextView) getView().findViewById(R.id.syncMessageText);
		
		setProgress(mProgress);
		mSyncMessage.setText(mMessage);
		
		if (getDialog() != null) {
			
			getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	
					if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
						
						HttpAsyncManager.stopSyncTask();
						dismiss();
						
						if (miShowCancellationMessage) {
							NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_sync_cancellation));
						}
						
						return true;
			
					} else {
						return false;
					}
				}
			});
		}
	}
	
	public void setProgress(int progress) {
		
		mProgress = progress;
		
		if (mDataSyncPb == null) {
			return;
		}
				
		mDataSyncPb.setIndeterminate(progress == 0);
		mDataSyncPb.setProgress(progress);
	}
	
	public void setMessage(String message) {
		
		mMessage = message;
		
		if (mSyncMessage == null) {
			return;
		}
		
		mSyncMessage.setText(message);
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		
	    if (HttpAsyncManager.stopSyncTask()) {
			super.onCancel(dialog);
	    }
	    
	    if (miShowCancellationMessage) {
	    	NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_sync_cancellation));
	    }
	}
	
	public void setCancellationMessage(boolean isShowCancellationMessage) {
		
		miShowCancellationMessage = isShowCancellationMessage;
	}
}
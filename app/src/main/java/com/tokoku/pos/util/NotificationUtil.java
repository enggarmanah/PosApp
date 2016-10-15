package com.tokoku.pos.util;

import android.app.FragmentManager;

import com.tokoku.pos.common.AlertDlgFragment;

public class NotificationUtil {
	
	private static String ALERT_DIALOG_FRAGMENT_TAG = "alertDialogFragment";
	
	private static AlertDlgFragment mAlertDialog = new AlertDlgFragment();
	
	private static AlertDlgFragment getAlertDialogInstance() {
		
		return mAlertDialog;
	}
	
	public static synchronized void setAlertMessage(FragmentManager fragmentManager, String message) {
		
		AlertDlgFragment mAlertDialog = getAlertDialogInstance();
		
		if (mAlertDialog.isAdded()) {
			return;
		}
		
		try {
		
			mAlertDialog.show(fragmentManager, ALERT_DIALOG_FRAGMENT_TAG);
			mAlertDialog.setAlertMessage(message);
		
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
}

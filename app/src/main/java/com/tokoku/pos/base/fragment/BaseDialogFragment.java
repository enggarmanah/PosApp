package com.tokoku.pos.base.fragment;

import android.app.DialogFragment;
import android.view.Gravity;
import android.widget.Toast;

public abstract class BaseDialogFragment extends DialogFragment {
	
	public void showMessage(int resourceId) {
		
		Toast toast = Toast.makeText(getActivity(), getResources().getString(resourceId), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	public void showMessage(String message) {
		
		try {
			Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

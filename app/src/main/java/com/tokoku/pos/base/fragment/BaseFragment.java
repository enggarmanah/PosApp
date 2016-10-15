package com.tokoku.pos.base.fragment;

import android.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
	
	public void onStart() {
		super.onStart();
	}
		
	public void showMessage(int resourceId) {
		
		Toast toast = Toast.makeText(getActivity(), getResources().getString(resourceId), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	public void showMessage(String message) {
		
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	protected boolean isViewInitialized() {
		
		if (getView() != null && isAdded()) {
			return true;
		} else {
			return false;
		}
	}
}

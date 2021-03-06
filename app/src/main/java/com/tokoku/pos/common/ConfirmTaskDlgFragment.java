package com.tokoku.pos.common;

import com.tokoku.pos.R;
import com.tokoku.pos.base.fragment.BaseDialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmTaskDlgFragment extends BaseDialogFragment {
	
	ConfirmListener mListener;
	
	String mConfirmMessage;
	String mTask;
	
	TextView mConfirmationText;
	
	Button mOkBtn;
	Button mCancelBtn;
	
	private static String LABEL = "LABEL";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

		setCancelable(false);

		if (savedInstanceState != null) {
			
			mConfirmMessage = (String) savedInstanceState.getSerializable(LABEL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.cmn_confirm_task_fragment, container, false);

		return view;
	}

	@Override
	public void onStart() {

		super.onStart();

		mConfirmationText = (TextView) getView().findViewById(R.id.confirmationText);
		
		mOkBtn = (Button) getView().findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		mCancelBtn = (Button) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		refreshContent();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(LABEL, mConfirmMessage);
	}
	
	private void refreshContent() {
		
		if (getView() == null) {
			return;
		}
		
		mConfirmationText.setText(mConfirmMessage);
	}
	
	public void setConfirm(ConfirmListener listener, String task, String confirmMessage) {
		
		mListener = listener;
		mTask = task;
		mConfirmMessage = confirmMessage;
		
		refreshContent();
	}

	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				mListener.onConfirm(mTask);
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
}

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

public class AlertDlgFragment extends BaseDialogFragment {
	
	String mAlertMessage;
	
	TextView mConfirmationText;
	
	Button mOkBtn;
	
	private static String LABEL = "LABEL";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

		setCancelable(false);

		if (savedInstanceState != null) {
			
			mAlertMessage = (String) savedInstanceState.getSerializable(LABEL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.cmn_alert_fragment, container, false);

		return view;
	}

	@Override
	public void onStart() {

		super.onStart();

		mConfirmationText = (TextView) getView().findViewById(R.id.confirmationText);
		
		mOkBtn = (Button) getView().findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		refreshContent();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(LABEL, mAlertMessage);
	}
	
	private void refreshContent() {
		
		if (getView() == null) {
			return;
		}
		
		mConfirmationText.setText(mAlertMessage);
	}
	
	public void setAlertMessage(String alertMessage) {
		
		mAlertMessage = alertMessage;
		
		refreshContent();
	}

	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
}

package com.tokoku.pos.report.transaction;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.base.fragment.BaseDialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TransactionDeleteDlgFragment extends BaseDialogFragment {
	
	String mLabel;
	Transactions mTransactionsToBeDeleted;
	
	TextView mConfirmationText;
	
	Button mOkBtn;
	Button mCancelBtn;
	
	TransactionActionListener mTransactionActionListener;
	
	private static String ITEM = "ITEM";
	private static String LABEL = "LABEL";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

		setCancelable(false);

		if (savedInstanceState != null) {
			
			mTransactionsToBeDeleted = (Transactions) savedInstanceState.getSerializable(ITEM);
			mLabel = (String) savedInstanceState.getSerializable(LABEL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.cmn_confirm_delete_fragment, container, false);

		return view;
	}

	@Override
	public void onStart() {

		super.onStart();

		mConfirmationText = (TextView) getView().findViewById(R.id.confirmationText);
		
		mOkBtn = (Button) getView().findViewById(R.id.okBtn);
		mCancelBtn = (Button) getView().findViewById(R.id.cancelBtn);

		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		refreshContent();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mTransactionActionListener = (TransactionActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TransactionActionListener");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(ITEM, (Serializable) mTransactionsToBeDeleted);
		outState.putSerializable(LABEL, mLabel);
	}
	
	private void refreshContent() {
		
		if (getView() == null) {
			return;
		}
		
		mConfirmationText.setText(getString(R.string.confirm_delete_transaction));
	}
	
	public void setTransactionToBeDeleted(Transactions item, String label) {
		
		mTransactionsToBeDeleted = item;
		mLabel = label;
		
		refreshContent();
	}

	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				mTransactionActionListener.onTransactionDeleted(mTransactionsToBeDeleted);
				
				dismiss();
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

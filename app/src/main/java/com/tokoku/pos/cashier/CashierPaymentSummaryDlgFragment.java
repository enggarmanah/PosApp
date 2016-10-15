package com.tokoku.pos.cashier;

import java.io.Serializable;
import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Transactions;
import com.android.pos.dao.User;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseDialogFragment;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PrintUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CashierPaymentSummaryDlgFragment extends BaseDialogFragment {

	LinearLayout mCustomerPanel;

	TextView mCustomerText;
	TextView mPaymentTypeText;
	TextView mTotalBillText;
	TextView mPaymentText;
	TextView mChangeLabelText;
	TextView mChangeText;

	Button mOkBtn;
	Button mCancelBtn;
	
	Customer mCustomer;
	String mPaymentType;
	Float mTotalBill;
	Float mPayment;
	Float mChange;

	CashierActionListener mActionListener;

	CodeSpinnerArrayAdapter paymentTypeArrayAdapter;

	private static String CUSTOMER = "CUSTOMER";
	private static String TOTAL_BILL = "TOTAL_BILL";
	private static String PAYMENT_TYPE = "PAYMENT_TYPE";
	private static String PAYMENT = "PAYMENT";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

		setCancelable(false);

		if (savedInstanceState != null) {
			mCustomer = (Customer) savedInstanceState.getSerializable(CUSTOMER);
			mTotalBill = (Float) savedInstanceState.getSerializable(TOTAL_BILL);
			mPaymentType = (String) savedInstanceState.getSerializable(PAYMENT_TYPE);
			mPayment = (Float) savedInstanceState.getSerializable(PAYMENT);
			mChange = mPayment - mTotalBill;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.cashier_payment_summary_fragment, container, false);

		return view;
	}

	@Override
	public void onStart() {

		super.onStart();

		mCustomerPanel = (LinearLayout) getView().findViewById(R.id.customerPanel);

		mCustomerText = (TextView) getView().findViewById(R.id.customerText);
		mPaymentTypeText = (TextView) getView().findViewById(R.id.paymentTypeText);
		mTotalBillText = (TextView) getView().findViewById(R.id.totalBillText);
		mPaymentText = (TextView) getView().findViewById(R.id.paymentText);
		mChangeLabelText = (TextView) getView().findViewById(R.id.changeLabelText);
		mChangeText = (TextView) getView().findViewById(R.id.changeText);
		mOkBtn = (Button) getView().findViewById(R.id.okBtn);
		mCancelBtn = (Button) getView().findViewById(R.id.cancelBtn);

		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());

		refreshDisplay();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mActionListener = (CashierActionListener) activity;
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement CashierActionListener");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putSerializable(CUSTOMER, (Serializable) mCustomer);
		outState.putSerializable(TOTAL_BILL, (Serializable) mTotalBill);
		outState.putSerializable(PAYMENT_TYPE, (Serializable) mPaymentType);
		outState.putSerializable(PAYMENT, (Serializable) mPayment);
	}

	private void refreshDisplay() {
		
		if (getView() == null) {
			return;
		}

		if (mCustomer != null) {
			mCustomerText.setText(mCustomer.getName());
			mCustomerPanel.setVisibility(View.VISIBLE);
			
		} else {
			mCustomerPanel.setVisibility(View.GONE);
		}
		
		String paymentTypeLabel = Constant.EMPTY_STRING;
		String returnLabel = Constant.EMPTY_STRING;
		
		if (Constant.PAYMENT_TYPE_CREDIT.equals(mPaymentType)) {
			paymentTypeLabel = getString(R.string.payment);
			returnLabel = CodeUtil.getPaymentTypeLabel(mPaymentType);
		} else {
			paymentTypeLabel = CodeUtil.getPaymentTypeLabel(mPaymentType);
			returnLabel = getString(R.string.change);
		}
		
		mPaymentTypeText.setText(paymentTypeLabel);
		mChangeLabelText.setText(returnLabel);
		
		mTotalBillText.setText(CommonUtil.formatCurrency(mTotalBill));
		mPaymentText.setText(CommonUtil.formatCurrency(mPayment));
		mChangeText.setText(CommonUtil.formatCurrency(Math.abs(mChange)));
	}

	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//CommonUtil.setEvent(getString(R.string.event_cat_transaction), getString(R.string.event_act_payment));
				
				Transactions transaction = getTransaction();
				
				mActionListener.onPaymentCompleted(transaction);
				
				if (PrintUtil.isPrinterActive()) {
					mActionListener.onPrintReceipt(transaction);
				}
				
				mActionListener.onClearTransaction();
				
				CommonUtil.sendEvent(getString(R.string.event_cat_transaction), getString(R.string.event_act_payment));
				
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

	public void setPaymentInfo(Customer customer, String paymentType, Float totalBill, Float payment) {

		mCustomer = customer;
		mPaymentType = paymentType;
		mTotalBill = totalBill;
		mPayment = payment;
		mChange = payment - totalBill;

		refreshDisplay();
	}

	private Transactions getTransaction() {

		Transactions transaction = new Transactions();

		transaction.setTransactionNo(CommonUtil.getTransactionNo());
		transaction.setTransactionDate(new Date());

		if (mCustomer != null) {
			transaction.setCustomerId(mCustomer.getId());
			transaction.setCustomerName(mCustomer.getName());
		}
		
		User user = UserUtil.getUser();
		
		transaction.setCashierId(user.getId());
		transaction.setCashierName(user.getName());
		
		transaction.setPaymentType(mPaymentType);
		transaction.setTotalAmount(mTotalBill);
		transaction.setPaymentAmount(mPayment);
		transaction.setReturnAmount(mChange);
		
		transaction.setStatus(Constant.STATUS_ACTIVE);

		return transaction;
	}
}

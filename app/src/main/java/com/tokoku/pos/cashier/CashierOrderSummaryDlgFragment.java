package com.tokoku.pos.cashier;

import java.io.Serializable;
import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Orders;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseDialogFragment;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
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

public class CashierOrderSummaryDlgFragment extends BaseDialogFragment {

	LinearLayout mOrderTypePanel;
	LinearLayout mWaitressPanel;
	LinearLayout mCustomerPanel;
	
	TextView mOrderReferenceLabel;

	TextView mOrderTypeText;
	TextView mOrderReferenceText;
	TextView mWaitressText;

	Button mOkBtn;
	Button mCancelBtn;
	
	String mOrderReference;
	String mOrderType;
	Employee mWaitress;
	Customer mCustomer;
	int mTotalItem;

	CashierActionListener mActionListener;

	private static String ORDER_REFERENCE = "ORDER_REFERENCE";
	private static String ORDER_TYPE = "ORDER_TYPE";
	private static String TOTAL_ITEM = "TOTAL_ITEM";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

		setCancelable(false);

		if (savedInstanceState != null) {
			mOrderReference = (String) savedInstanceState.getSerializable(ORDER_REFERENCE);
			mOrderType = (String) savedInstanceState.getSerializable(ORDER_TYPE);
			mTotalItem = (Integer) savedInstanceState.getSerializable(TOTAL_ITEM);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.cashier_order_summary_dlg_fragment, container, false);

		return view;
	}

	@Override
	public void onStart() {

		super.onStart();

		mOrderTypePanel = (LinearLayout) getView().findViewById(R.id.orderTypePanel);
		mWaitressPanel = (LinearLayout) getView().findViewById(R.id.waitressPanel);
		mCustomerPanel = (LinearLayout) getView().findViewById(R.id.customerPanel);
		
		mOrderReferenceLabel = (TextView) getView().findViewById(R.id.orderReferenceLabel);
		mOrderReferenceText = (TextView) getView().findViewById(R.id.orderReferenceText);
		mOrderTypeText = (TextView) getView().findViewById(R.id.orderTypeText);
		mWaitressText = (TextView) getView().findViewById(R.id.waitressText);
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

		outState.putSerializable(ORDER_REFERENCE, (Serializable) mOrderReference);
		outState.putSerializable(ORDER_TYPE, (Serializable) mTotalItem);
		outState.putSerializable(TOTAL_ITEM, (Serializable) mOrderType);
	}

	private void refreshDisplay() {

		if (getView() == null) {
			return;
		}
		
		if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mOrderType)) {
			mOrderTypePanel.setVisibility(View.VISIBLE);
			mOrderReferenceLabel.setText(getString(R.string.reservation_no));
		
		} else if (Constant.TXN_ORDER_TYPE_TAKEWAY.equals(mOrderType)) {
			mOrderTypePanel.setVisibility(View.VISIBLE);
			mOrderReferenceLabel.setText(getString(R.string.customer));
		
		} else if (Constant.TXN_ORDER_TYPE_SERVICE.equals(mOrderType)) {
			mOrderTypePanel.setVisibility(View.GONE);
			mOrderReferenceLabel.setText(getString(R.string.customer));
			mOrderReferenceLabel.setMinHeight(CommonUtil.convertDpToPix(60));
			mOrderReferenceText.setMinHeight(CommonUtil.convertDpToPix(60));
		}

		mOrderReferenceText.setText(mOrderReference);
		mOrderTypeText.setText(CodeUtil.getOrderTypeLabel(mOrderType));
		
		if (mWaitress != null) {
			mWaitressPanel.setVisibility(View.VISIBLE);
			mWaitressText.setText(mWaitress.getName());
			
		} else {
			mWaitressPanel.setVisibility(View.GONE);
		}
	}

	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Orders order = getOrder();
				
				mActionListener.onOrderConfirmed(order);
				
				if (!UserUtil.isWaitress()) {
					
					if (PrintUtil.isPrinterActive()) {
						mActionListener.onPrintOrder(order);
					}
					mActionListener.onClearTransaction();
				}
					
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

	public void setOrderInfo(String orderReference, String orderType, Employee waitress, Customer customer) {

		mOrderReference = orderReference;
		mOrderType = orderType;
		mWaitress = waitress;
		mCustomer = customer;
		
		refreshDisplay();
	}

	private Orders getOrder() {

		Orders order = new Orders();
		
		order.setMerchantId(MerchantUtil.getMerchantId());
		
		order.setOrderNo(CommonUtil.getTransactionNo());
		order.setOrderDate(new Date());
		order.setOrderType(mOrderType);
		order.setOrderReference(mOrderReference);
		
		if (mCustomer != null) {
			order.setCustomer(mCustomer);
			order.setCustomerName(mOrderReference);
		}
		
		if (mWaitress != null) {
			order.setEmployee(mWaitress);
			order.setWaitressName(mWaitress.getName());
		}
		
		order.setStatus(Constant.STATUS_ACTIVE);
		
		return order;
	}
}

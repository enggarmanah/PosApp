package com.tokoku.pos.cashier;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Employee;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CashierOrderDlgFragment extends DialogFragment {
	
	TextView mTotalItemText;
	Spinner mOrderTypeSp;
	EditText mWaitressText;
	EditText mCustomerText;
	TextView mReservationNoText;
	
	ImageButton mCustomerSearchBtn;
	
	LinearLayout mWaitressPanel;
	LinearLayout mCustomerPanel;
	
	Button okBtn;
	Button cancelBtn;
	
	Employee mWaitress;
	Customer mCustomer;
	
	float mTotalItem;
	String mOrderType;
	String mWaitressName;
	String mCustomerName;
	String mReservationNo;
	
	CashierActionListener mActionListener;
	
	CodeSpinnerArrayAdapter orderTypeArrayAdapter;
	
	private static String TOTAL_ITEM = "TOTAL_ITEM";
	private static String ORDER_TYPE = "ORDER_TYPE";
	private static String CUSTOMER = "CUSTOMER";
	private static String WAITRESS = "WAITRESS";
	private static String RESERVATION_NO = "RESERVATION_NO";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        if (savedInstanceState != null) {
        	
			mTotalItem = (Float) savedInstanceState.getSerializable(TOTAL_ITEM);
			mOrderType = (String) savedInstanceState.getSerializable(ORDER_TYPE);
			mReservationNo = (String) savedInstanceState.getSerializable(RESERVATION_NO);
			mCustomer = (Customer) savedInstanceState.getSerializable(CUSTOMER);
			mWaitress = (Employee) savedInstanceState.getSerializable(WAITRESS);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.cashier_order_dlg_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mTotalItemText = (TextView) getView().findViewById(R.id.totalItemText);
		mOrderTypeSp = (Spinner) getView().findViewById(R.id.orderTypeSp);
		mWaitressText = (EditText) getView().findViewById(R.id.waitressText);
		mCustomerText = (EditText) getView().findViewById(R.id.customerText);
		mReservationNoText = (TextView) getView().findViewById(R.id.reservationNoText);
		
		mWaitressText.setOnClickListener(getWaitressSearchBtnOnClickListener());
		
		mOrderTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
		    	mOrderType = CodeBean.getNvlCode((CodeBean) mOrderTypeSp.getSelectedItem());
		    	refreshDisplay();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		mCustomerSearchBtn = (ImageButton) getView().findViewById(R.id.customerSearchBtn);
		mCustomerSearchBtn.setOnClickListener(getCustomerSearchBtnOnClickListener());
		
		okBtn = (Button) getView().findViewById(R.id.okBtn);
		cancelBtn = (Button) getView().findViewById(R.id.cancelBtn);
		
		okBtn.setOnClickListener(getOkBtnOnClickListener());
		cancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		orderTypeArrayAdapter = new CodeSpinnerArrayAdapter(mOrderTypeSp, getActivity(), CodeUtil.getFnBOrderTypes(),
											R.layout.cashier_spinner_items, 
											R.layout.cashier_spinner_items_selected, 
											R.layout.cashier_spinner_selected_item);
		
		mOrderTypeSp.setAdapter(orderTypeArrayAdapter);
		
		mWaitressPanel = (LinearLayout) getView().findViewById(R.id.waitressPanel);
		mCustomerPanel = (LinearLayout) getView().findViewById(R.id.customerPanel);
		
		refreshDisplay();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CashierActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		saveDataFromView();
		
		outState.putSerializable(TOTAL_ITEM, (Serializable) mTotalItem);
		outState.putSerializable(ORDER_TYPE, (Serializable) mOrderType);
		outState.putSerializable(RESERVATION_NO, (Serializable) mReservationNo);
		outState.putSerializable(CUSTOMER, (Serializable) mCustomer);
		outState.putSerializable(WAITRESS, (Serializable) mWaitress);
	}
	
	public void setWaitress(Employee employee) {
		
		mWaitress = employee;
		
		if (mWaitress != null) {
			mWaitressText.setText(mWaitress.getName());
		}
	}
	
	public void setCustomer(Customer customer) {
		
		mCustomer = customer;
		
		if (mCustomer != null) {
			mCustomerText.setText(mCustomer.getName());
		}
	}
	
	private void saveDataFromView() {
		
		mTotalItem = CommonUtil.parseIntNumber(mTotalItemText.getText().toString());
		
		if (!Constant.TXN_ORDER_TYPE_SERVICE.equals(mOrderType)) {
			mOrderType = CodeBean.getNvlCode((CodeBean) mOrderTypeSp.getSelectedItem());
		}
			
		mReservationNo = mReservationNoText.getText().toString();
		mCustomerName = mCustomerText.getText().toString();
    	
    	if (mCustomer == null) {
    		mCustomer = new Customer();
    		mCustomer.setName(mCustomerName);
    	}
	}
	
	private void reset() {
		
		mWaitress = null;
		mCustomer = null;
		
		mWaitressName = Constant.EMPTY_STRING;
		mCustomerName = Constant.EMPTY_STRING;
		mReservationNo = Constant.EMPTY_STRING;
	}
	
	private View.OnClickListener getWaitressSearchBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActionListener.onSelectEmployee();
			}
		};
	}
	
	private View.OnClickListener getCustomerSearchBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActionListener.onSelectCustomer();
			}
		};
	}
	
	private void refreshDisplay() {
		
		if (getView() == null) {
			return;
		}
		
		int orderTypeIndex = orderTypeArrayAdapter.getPosition(mOrderType);
		
		mTotalItemText.setText(CommonUtil.formatNumber(mTotalItem));
		mOrderTypeSp.setSelection(orderTypeIndex);
		mCustomerText.setText(mCustomerName);
		mReservationNoText.setText(mReservationNo);

		if (mOrderType == null) {
			
			if (Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(MerchantUtil.getMerchantType())) {
				mOrderType = Constant.TXN_ORDER_TYPE_DINE_IN;
				
			} else if (Constant.MERCHANT_TYPE_GOODS_N_SERVICES.equals(MerchantUtil.getMerchantType())) {
				mOrderType = Constant.TXN_ORDER_TYPE_SERVICE;
			}
		}
		
		if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mOrderType)) {
			
			mCustomerPanel.setVisibility(View.GONE);
			mReservationNoText.setVisibility(View.VISIBLE);
			mOrderTypeSp.setVisibility(View.VISIBLE);
			
			mReservationNoText.requestFocus();
			
		} else if (Constant.TXN_ORDER_TYPE_TAKEWAY.equals(mOrderType)) {
			
			mCustomerPanel.setVisibility(View.VISIBLE);
			mReservationNoText.setVisibility(View.GONE);
			mOrderTypeSp.setVisibility(View.VISIBLE);
			
			mCustomerText.requestFocus();
		
		} else if (Constant.TXN_ORDER_TYPE_SERVICE.equals(mOrderType)) {
			
			mCustomerText.setMinHeight(CommonUtil.convertDpToPix(60));
			mCustomerPanel.setVisibility(View.VISIBLE);
			mReservationNoText.setVisibility(View.GONE);
			mOrderTypeSp.setVisibility(View.GONE);
			
			mCustomerText.requestFocus();
		}
		
		if (Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(MerchantUtil.getMerchantType())) {
			mWaitressPanel.setVisibility(View.VISIBLE);
		} else {
			mWaitressPanel.setVisibility(View.GONE);
		}
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveDataFromView();
				
				String orderReference = Constant.EMPTY_STRING;
				
				if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mOrderType)) {
					orderReference = CommonUtil.formatReservationNo(mReservationNo);
				} else {
					orderReference = mCustomerName;
				}
				
				if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mOrderType) && CommonUtil.isEmpty(orderReference)) {
				
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_please_choose_table_no));
	    			
	    			return;
	    			
				} else if (Constant.TXN_ORDER_TYPE_TAKEWAY.equals(mOrderType) && CommonUtil.isEmpty(orderReference)) {
				
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_customer_name_empty));
	    			
	    			return;
				}
				
				mActionListener.onOrderInfoProvided(orderReference, mOrderType, mWaitress, mCustomer);
				
				mReservationNo = Constant.EMPTY_STRING;
				
				reset();
				
				dismiss();
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mReservationNo = Constant.EMPTY_STRING;
				
				reset();
				
				dismiss();
			}
		};
	}
	
	public void setTotalOrder(int totalOrder) {
		
		mTotalItem = totalOrder;
		
		refreshDisplay();
	}
}

package com.tokoku.pos.cashier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class CashierPaymentDlgFragment extends DialogFragment {
	
	TextView mTotalBillText;
	Spinner mPaymentTypeSp;
	TextView mCustomerText;
	TextView mPaymentText;
	
	Button number0Btn;
	Button number1Btn;
	Button number2Btn;
	Button number3Btn;
	Button number4Btn;
	Button number5Btn;
	Button number6Btn;
	Button number7Btn;
	Button number8Btn;
	Button number9Btn;
	
	Button actionCBtn;
	ImageButton actionXBtn;
	
	Button okBtn;
	Button cancelBtn;
	
	Float mTotalBill;
	String mPaymentType;
	Customer mCustomer;
	Float mPayment;
	
	CashierActionListener mActionListener;
	
	CodeSpinnerArrayAdapter paymentTypeArrayAdapter;
	
	private static String TOTAL_BILL = "TOTAL_BILL";
	private static String PAYMENT_TYPE = "PAYMENT_TYPE";
	private static String CUSTOMER = "CUSTOMER";
	private static String PAYMENT = "PAYMENT";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mPayment = Float.valueOf(0);
        		
        if (savedInstanceState != null) {
        	
			mTotalBill = (Float) savedInstanceState.getSerializable(TOTAL_BILL);
			mPaymentType = (String) savedInstanceState.getSerializable(PAYMENT_TYPE);
			mCustomer = (Customer) savedInstanceState.getSerializable(CUSTOMER);
			mPayment = (Float) savedInstanceState.getSerializable(PAYMENT);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.cashier_payment_dlg_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mTotalBillText = (TextView) getView().findViewById(R.id.totalBillText);
		mPaymentTypeSp = (Spinner) getView().findViewById(R.id.paymentTypeSp);
		mCustomerText = (TextView) getView().findViewById(R.id.customerText);
		mPaymentText = (TextView) getView().findViewById(R.id.paymentText);
		
		number0Btn = (Button) getView().findViewById(R.id.number0Btn);
		number1Btn = (Button) getView().findViewById(R.id.number1Btn);
		number2Btn = (Button) getView().findViewById(R.id.number2Btn);
		number3Btn = (Button) getView().findViewById(R.id.number3Btn);
		number4Btn = (Button) getView().findViewById(R.id.number4Btn);
		number5Btn = (Button) getView().findViewById(R.id.number5Btn);
		number6Btn = (Button) getView().findViewById(R.id.number6Btn);
		number7Btn = (Button) getView().findViewById(R.id.number7Btn);
		number8Btn = (Button) getView().findViewById(R.id.number8Btn);
		number9Btn = (Button) getView().findViewById(R.id.number9Btn);
		
		actionCBtn = (Button) getView().findViewById(R.id.actionCBtn);
		actionXBtn = (ImageButton) getView().findViewById(R.id.actionXBtn);
		
		okBtn = (Button) getView().findViewById(R.id.okBtn);
		cancelBtn = (Button) getView().findViewById(R.id.cancelBtn);
		
		number0Btn.setOnClickListener(getNumberBtnOnClickListener("0"));
		number1Btn.setOnClickListener(getNumberBtnOnClickListener("1"));
		number2Btn.setOnClickListener(getNumberBtnOnClickListener("2"));
		number3Btn.setOnClickListener(getNumberBtnOnClickListener("3"));
		number4Btn.setOnClickListener(getNumberBtnOnClickListener("4"));
		number5Btn.setOnClickListener(getNumberBtnOnClickListener("5"));
		number6Btn.setOnClickListener(getNumberBtnOnClickListener("6"));
		number7Btn.setOnClickListener(getNumberBtnOnClickListener("7"));
		number8Btn.setOnClickListener(getNumberBtnOnClickListener("8"));
		number9Btn.setOnClickListener(getNumberBtnOnClickListener("9"));
		
		actionXBtn.setOnClickListener(getDeleteBtnOnClickListener());
		
		mCustomerText.setOnClickListener(getCustomerTextOnClickListener());
		
		okBtn.setOnClickListener(getOkBtnOnClickListener());
		cancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		paymentTypeArrayAdapter = new CodeSpinnerArrayAdapter(mPaymentTypeSp, getActivity(), getPaymentTypes(),
											R.layout.cashier_spinner_items, 
											R.layout.cashier_spinner_items_selected, 
											R.layout.cashier_spinner_selected_item);
		
		mPaymentTypeSp.setAdapter(paymentTypeArrayAdapter);
		
		if (CommonUtil.isDecimalCurrency()) {
			actionCBtn.setText(CommonUtil.getCurrencyDecimalSeparator());
			actionCBtn.setOnClickListener(getCommaBtnOnClickListener());
		} else {
			actionCBtn.setOnClickListener(getClearBtnOnClickListener());
		}
		
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
		
		outState.putSerializable(TOTAL_BILL, (Serializable) mTotalBill);
		outState.putSerializable(PAYMENT_TYPE, (Serializable) mPaymentType);
		outState.putSerializable(CUSTOMER, (Serializable) mCustomer);
		outState.putSerializable(PAYMENT, (Serializable) mPayment);
	}
	
	private void saveDataFromView() {
		
		mTotalBill = CommonUtil.parseFloatCurrency(mTotalBillText.getText().toString());
    	mPaymentType = CodeBean.getNvlCode((CodeBean) mPaymentTypeSp.getSelectedItem());
    	mPayment = CommonUtil.parseFloatCurrency(mPaymentText.getText().toString());
	}
	
	public void setCustomer(Customer customer) {
		
		mCustomer = customer;
		
		refreshDisplay();
	}
	
	private void refreshDisplay() {
		
		if (getView() == null) {
			return;
		}
		
		int picPaymentTypeIndex = paymentTypeArrayAdapter.getPosition(mPaymentType);
		
		mTotalBillText.setText(CommonUtil.formatCurrency(mTotalBill));
		mPaymentTypeSp.setSelection(picPaymentTypeIndex);
		mPaymentText.setText(CommonUtil.formatNumber(mPayment));
		
		if (mCustomer != null) {
			mCustomerText.setText(mCustomer.getName());
		} else {
			mCustomerText.setText(getString(R.string.track_customer));
		}
	}
	
	private View.OnClickListener getNumberBtnOnClickListener(final String numberText) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String paymentText = mPaymentText.getText().toString();
				String decimalSeparator = CommonUtil.getCurrencyDecimalSeparator(); 
				
				if (decimalSeparator != null && paymentText.contains(decimalSeparator)) {
					mPaymentText.setText(mPaymentText.getText() + numberText);
					
				} else {
					float payment = CommonUtil.parseFloatNumber(mPaymentText.getText().toString());
					float number = CommonUtil.parseFloatNumber(numberText);
					
					float total = payment * 10 + number;
					
					mPaymentText.setText(CommonUtil.formatNumber(total));
				}
			}
		};
	}
	
	private View.OnClickListener getClearBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mPaymentText.setText("0");
			}
		};
	}
	
	private View.OnClickListener getCommaBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String paymentText = mPaymentText.getText().toString();
				String decimalSeparator = CommonUtil.getCurrencyDecimalSeparator(); 
				
				if (!paymentText.contains(decimalSeparator)) {
					mPaymentText.setText(mPaymentText.getText() + decimalSeparator);
				}
			}
		};
	}
	
	private View.OnClickListener getDeleteBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				float payment = CommonUtil.parseFloatNumber(mPaymentText.getText().toString());
				String number = CommonUtil.isRound(payment) ? String.valueOf((int) payment) : String.valueOf(payment);
				
				if (number.length() == 1) {
					mPaymentText.setText("0");
				} else {
					number = CommonUtil.formatNumber(number.substring(0, number.length()-1));
					mPaymentText.setText(number);
				}
			}
		};
	}
	
	private View.OnClickListener getCustomerTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveDataFromView();
				mActionListener.onSelectCustomer();
			}
		};
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mPayment = CommonUtil.parseFloatCurrency(mPaymentText.getText().toString()); 
				mPaymentType = CodeBean.getNvlCode((CodeBean) mPaymentTypeSp.getSelectedItem());
				
				if (Constant.PAYMENT_TYPE_CREDIT.equals(mPaymentType) && mCustomer == null) {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.payment_credit_without_customer));
					return;
				}
				
				if (Constant.PAYMENT_TYPE_CREDIT.equals(mPaymentType) && mPayment >= mTotalBill) {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.payment_should_not_be_credit));
					return;
				}
				
				if (Constant.PAYMENT_TYPE_CREDIT.equals(mPaymentType) || mPayment >= mTotalBill) {
				
					mActionListener.onPaymentInfoProvided(mCustomer, mPaymentType, mTotalBill, mPayment);
					
					mPayment = Float.valueOf(0);
					mPaymentType = null;
					
					dismiss();
					
				} else {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.payment_insufficient));
				}
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mPayment = Float.valueOf(0);
				
				dismiss();
			}
		};
	}
	
	public void setBillInfo(Float amount, Customer customer) {
		
		mTotalBill = amount;
		mCustomer = customer;
		
		refreshDisplay();
	}
	
	private CodeBean[] getPaymentTypes() {
		
		HashMap<String, String> merchantPaymentTypes = new HashMap<String, String>();
		String[] merchantPaymentTypeArray = null;
		
		if (MerchantUtil.getMerchant().getPaymentType() != null) {
			merchantPaymentTypeArray = MerchantUtil.getMerchant().getPaymentType().split(Constant.DATA_SEPARATOR);
		}
		
		if (merchantPaymentTypeArray != null) {
			
			for (String paymentType : merchantPaymentTypeArray) {
				merchantPaymentTypes.put(paymentType, Constant.STATUS_YES);
			}
		}
		
		List<CodeBean> paymentTypes = new ArrayList<CodeBean>();
		
		for (CodeBean codeBean : CodeUtil.getPaymentTypes()) {
			
			if (merchantPaymentTypes.get(codeBean.getCode()) != null) {
				paymentTypes.add(codeBean);
			}
		}
		
		return paymentTypes.toArray(new CodeBean[paymentTypes.size()]);
	}
}

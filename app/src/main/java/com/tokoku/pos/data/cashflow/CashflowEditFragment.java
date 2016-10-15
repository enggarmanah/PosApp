package com.tokoku.pos.data.cashflow;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CashflowEditFragment extends BaseEditFragment<Cashflow> {
    
	Spinner mTypeSp;
	EditText mBillReferenceNoText;
    EditText mBillSupplierText;
	EditText mTransactionNoText;
	EditText mTransactionCustomerText;
	EditText mCashDateText;
    EditText mCashAmountText;
    EditText mRemarksText;
    
    LinearLayout mBillReferenceNoPanel;
    LinearLayout mBillSupplierPanel;
    LinearLayout mTransactionNoPanel;
    LinearLayout mTransactionCustomerPanel;
    	
    CodeSpinnerArrayAdapter typeArrayAdapter;
    
    BaseItemListener<Cashflow> mCashflowItemListener;
    
    String mCashflowType;
    
    CashflowDaoService mCashflowDaoService = new CashflowDaoService();
    BillsDaoService mBillDaoService = new BillsDaoService();
    TransactionsDaoService mTransactionDaoService = new TransactionsDaoService();
    
    @Override
    protected void hideKeyboard() {
    	
    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTransactionNoText.getWindowToken(), 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_cashflow_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mCashflowItemListener = (BaseItemListener<Cashflow>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<T>");
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void initViewReference(View view) {
        
    	super.initViewReference(view);
    	
    	mTypeSp = (Spinner) view.findViewById(R.id.typeSp);
    	mBillReferenceNoText = (EditText) view.findViewById(R.id.billReferenceNoText);
    	mBillSupplierText = (EditText) view.findViewById(R.id.billSupplierText);
    	mTransactionNoText = (EditText) view.findViewById(R.id.transactionNoText);
    	mTransactionCustomerText = (EditText) view.findViewById(R.id.transactionCustomerText);
    	mCashDateText = (EditText) view.findViewById(R.id.cashDateText);
    	mCashAmountText = (EditText) view.findViewById(R.id.cashAmountText);
    	mRemarksText = (EditText) view.findViewById(R.id.remarksText);
    	
    	mBillReferenceNoPanel = (LinearLayout) view.findViewById(R.id.billReferenceNoPanel);
        mBillSupplierPanel = (LinearLayout) view.findViewById(R.id.billSupplierPanel);
        mTransactionNoPanel = (LinearLayout) view.findViewById(R.id.transactionNoPanel);
        mTransactionCustomerPanel = (LinearLayout) view.findViewById(R.id.transactionCustomerPanel);
        
        mTypeSp.setOnItemSelectedListener(getTypeOnItemSelectedListener());
    	
    	registerField(mTypeSp);
    	registerField(mBillReferenceNoText);
    	registerField(mTransactionNoText);
    	registerField(mCashDateText);
    	registerField(mCashAmountText);
    	registerField(mRemarksText);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mCashDateText, R.string.field_date));
    	mandatoryFields.add(new FormFieldBean(mCashAmountText, R.string.field_amount));
    	
    	mCashAmountText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	
    	typeArrayAdapter = new CodeSpinnerArrayAdapter(mTypeSp, getActivity(), CodeUtil.getCashflowTypes());
    	mTypeSp.setAdapter(typeArrayAdapter);
    	
    	mCashDateText.setOnClickListener(getDateFieldOnClickListener("cashDatePicker"));
    	
    	linkDatePickerWithInputField("cashDatePicker", mCashDateText);
    	
    	mBillReferenceNoText.setFocusable(false);
    	mBillReferenceNoText.setOnClickListener(getBillOnClickListener());
    	
    	mTransactionNoText.setFocusable(false);
    	mTransactionNoText.setOnClickListener(getTransactionOnClickListener());
    }
    
    @Override
    protected void updateView(Cashflow cashflow) {
    	
    	if (cashflow != null) {
    		
    		int statusIndex = typeArrayAdapter.getPosition(cashflow.getType());
    		mTypeSp.setSelection(statusIndex);
    		
    		mBillReferenceNoText.setText(Constant.EMPTY_STRING);
    		mBillSupplierText.setText(Constant.EMPTY_STRING);
    		
    		if (cashflow.getBillId() != null) {
    			Bills bill = mBillDaoService.getBills(cashflow.getBillId());
    			mBillReferenceNoText.setText(bill.getBillReferenceNo());
				mBillSupplierText.setText(bill.getSupplierName());
    		}
    		
    		mTransactionNoText.setText(Constant.EMPTY_STRING);
    		mTransactionCustomerText.setText(Constant.EMPTY_STRING);
    		
    		if (cashflow.getTransactionId() != null) {
    			Transactions transaction = mTransactionDaoService.getTransactions(cashflow.getTransactionId());
    			mTransactionNoText.setText(transaction.getTransactionNo());
    			mTransactionCustomerText.setText(transaction.getCustomerName());
    		}
    		
    		mCashDateText.setText(CommonUtil.formatDate(cashflow.getCashDate()));
    		mCashAmountText.setText(cashflow.getCashAmount() == null ? Constant.EMPTY_STRING : CommonUtil.formatCurrency(Math.abs(cashflow.getCashAmount())));
    		mRemarksText.setText(cashflow.getRemarks());
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    	
        mCashflowType = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
        refreshVisibleField();
    }
    
    @Override
    protected void saveItem() {
    	
    	String type = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
    	Date cashDate = CommonUtil.parseDate(mCashDateText.getText().toString());
    	Float cashAmount = CommonUtil.parseFloatCurrency(mCashAmountText.getText().toString());
    	String remarks = mRemarksText.getText().toString();
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setType(type);
    		
    		if (Constant.CASHFLOW_TYPE_CAPITAL_IN.equals(type) ||
				Constant.CASHFLOW_TYPE_BANK_WITHDRAWAL.equals(type) ||
				Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(type)) {
    			
    			cashAmount = cashAmount != null ? cashAmount : 0f;
				mItem.setCashAmount(cashAmount);
				
			} else {
				Float negativeAmount = cashAmount != null ? -cashAmount : 0f;
				mItem.setCashAmount(negativeAmount);
			}
    		
    		if (!Constant.CASHFLOW_TYPE_BILL_PAYMENT.equals(type)) {
    			mItem.setBills(null);
    		}
    		
    		if (!Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(type)) {
    			mItem.setTransactions(null);
    		}
		
    		mItem.setCashDate(cashDate);
    		mItem.setRemarks(remarks);
    		
    		mItem.setStatus(Constant.STATUS_ACTIVE);
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		
    		String loginId = UserUtil.getUser().getUserId();
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(loginId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(loginId);
    		mItem.setUpdateDate(new Date());
    	}
    }
    
    @Override
    protected Long getItemId(Cashflow cashflow) {
    	
    	return cashflow.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
        mCashflowDaoService.addCashflow(mItem);
        
        if (mItem.getBillId() != null) {
        	mBillDaoService.updatePaymentDetails(mItem.getBillId());
        }
        
        mBillReferenceNoText.getText().clear();
        mBillSupplierText.getText().clear();
        mTransactionNoText.getText().clear();
        mTransactionCustomerText.getText().clear();
        mCashDateText.getText().clear();
        mCashAmountText.getText().clear();
        mRemarksText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mCashflowDaoService.updateCashflow(mItem);
    	
    	if (mItem.getBillId() != null) {
        	mBillDaoService.updatePaymentDetails(mItem.getBillId());
        }
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return null;
    }
    
    @Override
    public Cashflow updateItem(Cashflow cashflow) {

    	if (cashflow.getId() != null) {
    		cashflow = mCashflowDaoService.getCashflow(cashflow.getId());
    	}
    	
    	this.mItem = cashflow;
    	
    	return cashflow;
    }
    
    public void setTransaction(Transactions transactions) {
		
    	if (mItem != null) {
    		
    		mItem.setTransactions(transactions);
    		
    		updateView(mItem);
    	}
	}
    
    public void setBill(Bills bill) {
		
    	if (mItem != null) {
    		
    		mItem.setBills(bill);
    		
    		updateView(mItem);
    	}
	}
    
    private void refreshVisibleField() {
    	
    	mBillReferenceNoPanel.setVisibility(View.GONE);
		mBillSupplierPanel.setVisibility(View.GONE);
		mTransactionNoPanel.setVisibility(View.GONE);
		mTransactionCustomerPanel.setVisibility(View.GONE);
    	
    	if (Constant.CASHFLOW_TYPE_BILL_PAYMENT.equals(mCashflowType)) {
    		mBillReferenceNoPanel.setVisibility(View.VISIBLE);
			mBillSupplierPanel.setVisibility(View.VISIBLE);
			
			if (mItem != null && mItem.getBillId() != null && mItem.getBills().getSupplierId() != null) {
				mBillSupplierPanel.setVisibility(View.VISIBLE);
			} else {
				mBillSupplierPanel.setVisibility(View.GONE);
			}
    	} else if (Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(mCashflowType)) {
			mTransactionNoPanel.setVisibility(View.VISIBLE);
			mTransactionCustomerPanel.setVisibility(View.VISIBLE);
    	}
    }
    
    public View.OnClickListener getTransactionOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mTransactionNoText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mCashflowItemListener.onSelectTransaction(isMandatory);
					}
				}
			}
		};
    }
    
    public View.OnClickListener getBillOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mBillReferenceNoText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mCashflowItemListener.onSelectBill(isMandatory);
					}
				}
			}
		};
    }
    		
    private AdapterView.OnItemSelectedListener getTypeOnItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
			
        	@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
        		mCashflowType = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
		    	
		    	refreshVisibleField();
		    }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
}
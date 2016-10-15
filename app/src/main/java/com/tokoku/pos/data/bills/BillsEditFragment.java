package com.tokoku.pos.data.bills;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.BillsDaoService;
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

public class BillsEditFragment extends BaseEditFragment<Bills> {
    
	Spinner mTypeSp;
    EditText mSupplierNameText;
    EditText mBillReferenceNoText;
    EditText mBillDate;
    EditText mBillDueDate;
    EditText mBillAmountText;
    EditText mDeliveryDate;
    EditText mRemarksText;
    
    LinearLayout mSupplierPanel;
    LinearLayout mBillReferenceNoPanel;
    LinearLayout mBillDatePanel;
    LinearLayout mBillDueDatePanel;
    LinearLayout mBillAmountPanel;
    LinearLayout mDeliveryDatePanel;
    
    CodeSpinnerArrayAdapter typeArrayAdapter;
    
    private BillsDaoService mBillsDaoService = new BillsDaoService();
    
    BaseItemListener<Bills> mBillsItemListener;
    
    String mBillType;
    String mBillStatus;
    
    @Override
    protected void hideKeyboard() {
    	
    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRemarksText.getWindowToken(), 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_bills_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mBillsItemListener = (BaseItemListener<Bills>) activity;
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
    	
    	mSupplierNameText = (EditText) view.findViewById(R.id.supplierNameText);
    	mBillReferenceNoText = (EditText) view.findViewById(R.id.billsReferenceNoText);
    	mBillDate = (EditText) view.findViewById(R.id.billsDate);
    	mBillDueDate = (EditText) view.findViewById(R.id.billsDueDate);
    	mBillAmountText = (EditText) view.findViewById(R.id.billsAmountText);
    	mDeliveryDate = (EditText) view.findViewById(R.id.deliveryDate);
    	mRemarksText = (EditText) view.findViewById(R.id.remarksText);
    	
    	registerField(mTypeSp);
    	registerField(mSupplierNameText);
    	registerField(mBillReferenceNoText);
    	registerField(mBillDate);
    	registerField(mBillDueDate);
    	registerField(mBillAmountText);
    	registerField(mDeliveryDate);
    	registerField(mRemarksText);
    	
    	enableInputFields(false);
    	
    	mBillAmountText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	
    	typeArrayAdapter = new CodeSpinnerArrayAdapter(mTypeSp, getActivity(), CodeUtil.getBillTypes());
    	mTypeSp.setAdapter(typeArrayAdapter);
    	
    	mBillDate.setOnClickListener(getDateFieldOnClickListener("billsDatePicker"));
    	mBillDueDate.setOnClickListener(getDateFieldOnClickListener("billsDueDatePicker"));
    	mDeliveryDate.setOnClickListener(getDateFieldOnClickListener("deliveryDatePicker"));
    	
    	linkDatePickerWithInputField("billsDatePicker", mBillDate);
    	linkDatePickerWithInputField("billsDueDatePicker", mBillDueDate);
    	linkDatePickerWithInputField("deliveryDatePicker", mDeliveryDate);
    	
    	mSupplierNameText.setFocusable(false);
    	mSupplierNameText.setOnClickListener(getSupplierOnClickListener());
    	
    	mSupplierPanel = (LinearLayout) view.findViewById(R.id.supplierPanel);
        mBillReferenceNoPanel = (LinearLayout) view.findViewById(R.id.billReferenceNoPanel);
        mBillDatePanel = (LinearLayout) view.findViewById(R.id.billDatePanel);
        mBillDueDatePanel = (LinearLayout) view.findViewById(R.id.billDueDatePanel);
        mBillAmountPanel = (LinearLayout) view.findViewById(R.id.billAmountPanel);
        mDeliveryDatePanel = (LinearLayout) view.findViewById(R.id.deliveryDatePanel);
        
        mTypeSp.setOnItemSelectedListener(getTypeOnItemSelectedListener());
        
        refreshVisibleField();
    }
    
    @Override
    protected void updateView(Bills bills) {
    	
    	if (bills != null) {
    		
    		int typeIndex = typeArrayAdapter.getPosition(bills.getBillType());
    		mTypeSp.setSelection(typeIndex);
    		
    		mSupplierNameText.setText(bills.getSupplierName());
    		mBillReferenceNoText.setText(bills.getBillReferenceNo());
    		mBillDate.setText(CommonUtil.formatDate(bills.getBillDate()));
    		mBillDueDate.setText(CommonUtil.formatDate(bills.getBillDueDate()));
    		mBillAmountText.setText(CommonUtil.formatCurrency(bills.getBillAmount()));
    		mDeliveryDate.setText(CommonUtil.formatDate(bills.getDeliveryDate()));
    		mRemarksText.setText(bills.getRemarks());
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    	
    	mBillType = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
        
        refreshVisibleField();
    }
    
    @Override
    protected void saveItem() {
    	
    	String type = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
    	String supplierName = mSupplierNameText.getText().toString();
    	String billReferenceNo = mBillReferenceNoText.getText().toString();
    	Date billDate = CommonUtil.parseDate(mBillDate.getText().toString());
    	Date billDueDate = CommonUtil.parseDate(mBillDueDate.getText().toString());
    	Float billAmount = CommonUtil.parseFloatCurrency(mBillAmountText.getText().toString());
    	Date deliveryDate = CommonUtil.parseDate(mDeliveryDate.getText().toString());
    	String remarks = mRemarksText.getText().toString();
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setBillType(type);
    		mItem.setSupplierName(supplierName);
    		mItem.setBillReferenceNo(billReferenceNo);
    		mItem.setBillDate(billDate);
    		mItem.setBillDueDate(billDueDate);
    		mItem.setBillAmount(billAmount);
    		mItem.setDeliveryDate(deliveryDate);
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
    protected Long getItemId(Bills bills) {
    	
    	return bills.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
        mBillsDaoService.addBills(mItem);
        
        mSupplierNameText.getText().clear();
        mBillReferenceNoText.getText().clear();
        mBillDate.getText().clear();
        mBillDueDate.getText().clear();
        mBillAmountText.getText().clear();
        mDeliveryDate.getText().clear();
        mRemarksText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	float payment = CommonUtil.getNvlFloat(mItem.getPayment());
		float billAmount = CommonUtil.getNvlFloat(mItem.getBillAmount());
		
		mItem.setPayment(payment);
		mItem.setBillAmount(billAmount);
    	
    	mBillsDaoService.updateBills(mItem);
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return null;
    }
    
    @Override
    public Bills updateItem(Bills bills) {

    	bills = mBillsDaoService.getBills(bills.getId());
    	
    	this.mItem = bills;
    	
    	return bills;
    }
    
    public void setSupplier(Supplier supplier) {
		
    	if (mItem != null) {
    		
    		if (supplier != null) {
    			
    			mItem.setSupplierId(supplier.getId());
    			mItem.setSupplierName(supplier.getName());
    			
    		} else {
    			
    			mItem.setSupplier(null);
    			mItem.setSupplierName(Constant.EMPTY_STRING);	
    		}
    		
    		updateView(mItem);
    	}
	}
    
    private void refreshVisibleField() {
    	
    	mSupplierPanel.setVisibility(View.VISIBLE);
		mBillReferenceNoPanel.setVisibility(View.VISIBLE);
		mBillDatePanel.setVisibility(View.VISIBLE);
		mBillDueDatePanel.setVisibility(View.VISIBLE);
		mBillAmountPanel.setVisibility(View.VISIBLE);
		mDeliveryDatePanel.setVisibility(View.VISIBLE);
		
		mandatoryFields.clear();
		
    	if (Constant.BILL_TYPE_PRODUCT_PURCHASE.equals(mBillType)) {
    		
    		mandatoryFields.add(new FormFieldBean(mBillDate, R.string.field_bills_date));
        	mandatoryFields.add(new FormFieldBean(mBillAmountText, R.string.field_total));
        	mandatoryFields.add(new FormFieldBean(mDeliveryDate, R.string.field_delivery_date));
        	mandatoryFields.add(new FormFieldBean(mRemarksText, R.string.field_remarks));
    		
    	} else if (Constant.BILL_TYPE_EXPENSE.equals(mBillType)) {
    		
    		mSupplierPanel.setVisibility(View.GONE);
    		mDeliveryDatePanel.setVisibility(View.GONE);
    		
    		mandatoryFields.add(new FormFieldBean(mBillDate, R.string.field_bills_date));
        	mandatoryFields.add(new FormFieldBean(mBillAmountText, R.string.field_total));
        	mandatoryFields.add(new FormFieldBean(mRemarksText, R.string.field_remarks));
    	}
    	
    	highlightMandatoryFields();
    }
    
    private View.OnClickListener getSupplierOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mSupplierNameText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mBillsItemListener.onSelectSupplier(isMandatory);
					}
				}
			}
		};
    }
    
    private AdapterView.OnItemSelectedListener getTypeOnItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
			
        	@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
		    	mBillType = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
		    	
		    	refreshVisibleField();
		    }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
}
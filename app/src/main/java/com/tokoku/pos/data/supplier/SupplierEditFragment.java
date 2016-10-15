package com.tokoku.pos.data.supplier;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SupplierEditFragment extends BaseEditFragment<Supplier> {
    
	EditText mNameText;
	EditText mTelephoneText;
    EditText mAddressText;
    EditText mPicNameText;
    EditText mPicTelephoneText;
    EditText mRemarksText;
    
    private SupplierDaoService mSupplierDaoService = new SupplierDaoService();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_supplier_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void initViewReference(View view) {
        
    	super.initViewReference(view);
    	
    	mNameText = (EditText) view.findViewById(R.id.nameText);
    	mTelephoneText = (EditText) view.findViewById(R.id.telephoneText);
    	mAddressText = (EditText) view.findViewById(R.id.addressText);
    	mPicNameText = (EditText) view.findViewById(R.id.picNameText);
    	mPicTelephoneText = (EditText) view.findViewById(R.id.picTelephoneText);
    	mRemarksText = (EditText) view.findViewById(R.id.remarksText);
    	
    	registerField(mNameText);
    	registerField(mTelephoneText);
    	registerField(mAddressText);
    	registerField(mPicNameText);
    	registerField(mPicTelephoneText);
    	registerField(mRemarksText);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	mandatoryFields.add(new FormFieldBean(mTelephoneText, R.string.field_telephone));
    	mandatoryFields.add(new FormFieldBean(mAddressText, R.string.field_address));
    	mandatoryFields.add(new FormFieldBean(mPicNameText, R.string.field_pic_name));
    	mandatoryFields.add(new FormFieldBean(mPicTelephoneText, R.string.field_pic_telephone));
    }
    
    @Override
    protected void updateView(Supplier supplier) {
    	
    	if (supplier != null) {
    		
    		mNameText.setText(supplier.getName());
    		mTelephoneText.setText(supplier.getTelephone());
    		mAddressText.setText(supplier.getAddress());
    		mPicNameText.setText(supplier.getPicName());
    		mPicTelephoneText.setText(supplier.getPicTelephone());
    		mRemarksText.setText(supplier.getRemarks());
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	String telephone = mTelephoneText.getText().toString();
    	String address = mAddressText.getText().toString();
    	String picName = mPicNameText.getText().toString();
    	String picTelephone = mPicTelephoneText.getText().toString();
    	String remarks = mRemarksText.getText().toString();
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setName(name);
    		mItem.setTelephone(telephone);
    		mItem.setAddress(address);
    		mItem.setPicName(picName);
    		mItem.setPicTelephone(picTelephone);
    		mItem.setRemarks(remarks);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		mItem.setStatus(Constant.STATUS_ACTIVE);
    		
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
    protected Long getItemId(Supplier supplier) {
    	
    	return supplier.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
        mSupplierDaoService.addSupplier(mItem);
        
        mNameText.getText().clear();
        mTelephoneText.getText().clear();
        mAddressText.getText().clear();
        mPicNameText.getText().clear();
        mPicTelephoneText.getText().clear();
        mRemarksText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mSupplierDaoService.updateSupplier(mItem);
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public Supplier updateItem(Supplier supplier) {

    	supplier = mSupplierDaoService.getSupplier(supplier.getId());
    	
    	this.mItem = supplier;
    	
    	return supplier;
    }
}
package com.tokoku.pos.data.discount;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Discount;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.dao.DiscountDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class DiscountEditFragment extends BaseEditFragment<Discount> {
    
	EditText mNameText;
	EditText mPercentageText;
	
    private DiscountDaoService mDiscountDaoService = new DiscountDaoService();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_discount_fragment, container, false);
    	
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
        mPercentageText = (EditText) view.findViewById(R.id.percentageText);	
    	
        registerField(mNameText);
        registerField(mPercentageText);
        
        enableInputFields(false);
        
        mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	mandatoryFields.add(new FormFieldBean(mPercentageText, R.string.field_percentage));
    }
    
    @Override
    protected void updateView(Discount discount) {
    	
    	if (discount != null) {
    		
    		mNameText.setText(discount.getName());
    		mPercentageText.setText(CommonUtil.floatToStr(discount.getPercentage()));
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	Float percentage = CommonUtil.strToFloat(mPercentageText.getText().toString());
    	
    	if (mItem != null) {
    		
    		Long merchantId = MerchantUtil.getMerchant().getId();
    		
    		mItem.setMerchantId(merchantId);
    		mItem.setName(name);
    		mItem.setPercentage(percentage);
    		mItem.setAmount(Float.valueOf(0));
    		mItem.setStatus(Constant.STATUS_ACTIVE);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		
    		String userId = UserUtil.getUser().getUserId();
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(userId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(userId);
    		mItem.setUpdateDate(new Date());
    	}
    }
    
    @Override
    protected Long getItemId(Discount discount) {
    	
    	return discount.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
    	mDiscountDaoService.addDiscount(mItem);
    	
        mNameText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mDiscountDaoService.updateDiscount(mItem);
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public Discount updateItem(Discount discount) {

    	discount = mDiscountDaoService.getDiscount(discount.getId());
    	
    	this.mItem = discount;
    	
    	return discount;
    }
}
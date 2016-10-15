package com.tokoku.pos.data.productGrp;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.ProductGroup;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.dao.ProductGroupDaoService;
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

public class ProductGrpEditFragment extends BaseEditFragment<ProductGroup> {
    
	EditText mNameText;
    
    private ProductGroupDaoService mProductGroupDaoService = new ProductGroupDaoService();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_product_grp_fragment, container, false);
    	
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
    	
    	registerField(mNameText);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    }
    
    @Override
    protected void updateView(ProductGroup productGroup) {
    	
    	if (productGroup != null) {
    		
    		mNameText.setText(productGroup.getName());
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	
    	if (mItem != null) {
    		
    		Long merchantId = MerchantUtil.getMerchantId();
    		
    		mItem.setMerchantId(merchantId);
    		mItem.setName(name);
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
    protected Long getItemId(ProductGroup productGroup) {
    	
    	return productGroup.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
        mProductGroupDaoService.addProductGroup(mItem);
        
        mNameText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mProductGroupDaoService.updateProductGroup(mItem);
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public ProductGroup updateItem(ProductGroup productGroup) {

    	productGroup = mProductGroupDaoService.getProductGroup(productGroup.getId()); 
    			
    	this.mItem = productGroup;
    	
    	return productGroup;
    }
}
package com.tokoku.pos.data.discount;

import java.util.Date;

import com.tokoku.pos.CodeBean;
import com.tokoku.pos.R;
import com.android.pos.dao.Discount;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.dao.DiscountDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DiscountEditFragment extends BaseEditFragment<Discount> {

	EditText mNameText;
	EditText mPercentageText;
    EditText mAmountText;

    Spinner mDiscountTypeSp;

    LinearLayout mPercentagePanel;
    LinearLayout mAmountPanel;

    CodeSpinnerArrayAdapter discountTypeArrayAdapter;
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
        mAmountText = (EditText) view.findViewById(R.id.amountText);

        mDiscountTypeSp = (Spinner) view.findViewById(R.id.discountTypeSp);

        mPercentagePanel = (LinearLayout) view.findViewById(R.id.percentagePanel);
        mAmountPanel = (LinearLayout) view.findViewById(R.id.amountPanel);

        registerField(mNameText);
        registerField(mPercentageText);
        registerField(mAmountText);
        registerField(mDiscountTypeSp);
        
        enableInputFields(false);

        mAmountText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());

        discountTypeArrayAdapter = new CodeSpinnerArrayAdapter(mDiscountTypeSp, getActivity(), CodeUtil.getDiscountTypes());
        mDiscountTypeSp.setAdapter(discountTypeArrayAdapter);

        mDiscountTypeSp.setOnItemSelectedListener(getTypeOnItemSelectedListener());

        refresh();
    }
    
    @Override
    protected void updateView(Discount discount) {
    	
    	if (discount != null) {

            int discountTypeIndex = discountTypeArrayAdapter.getPosition(discount.getType());

    		mNameText.setText(discount.getName());
    		mPercentageText.setText(CommonUtil.floatToStr(discount.getPercentage()));
            mAmountText.setText(CommonUtil.formatCurrency(discount.getAmount()));

            mDiscountTypeSp.setSelection(discountTypeIndex);
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	Float percentage = CommonUtil.strToFloat(mPercentageText.getText().toString());
        Float amount = CommonUtil.parseFloatCurrency(mAmountText.getText().toString());

        String discountType = CodeBean.getNvlCode((CodeBean) mDiscountTypeSp.getSelectedItem());

    	if (mItem != null) {
    		
    		Long merchantId = MerchantUtil.getMerchant().getId();
    		
    		mItem.setMerchantId(merchantId);
    		mItem.setName(name);
            mItem.setType(discountType);
    		mItem.setPercentage(percentage);
    		mItem.setAmount(amount);
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

    private AdapterView.OnItemSelectedListener getTypeOnItemSelectedListener() {

        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void refresh() {

        String discountType = CodeBean.getNvlCode((CodeBean) mDiscountTypeSp.getSelectedItem());

        if (Constant.DISCOUNT_TYPE_AMOUNT.equals(discountType)) {

            mAmountPanel.setVisibility(View.VISIBLE);
            mPercentagePanel.setVisibility(View.GONE);

            mandatoryFields.clear();
            mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
            mandatoryFields.add(new FormFieldBean(mAmountText, R.string.field_amount));

        } else {

            mAmountPanel.setVisibility(View.GONE);
            mPercentagePanel.setVisibility(View.VISIBLE);

            mandatoryFields.clear();
            mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
            mandatoryFields.add(new FormFieldBean(mPercentageText, R.string.field_percentage));
        }
    }
}
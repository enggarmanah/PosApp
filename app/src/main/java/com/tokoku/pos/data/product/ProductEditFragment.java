package com.tokoku.pos.data.product;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ProductEditFragment extends BaseEditFragment<Product> {
    
	EditText mCodeText;
	EditText mNameText;
	Spinner mTypeSp;
	EditText mProductGroupText;
    
	LinearLayout mPrice1Panel;
	LinearLayout mPrice2Panel;
	LinearLayout mPrice3Panel;
	
	TextView mPrice1Label;
	TextView mPrice2Label;
	TextView mPrice3Label;
	
	EditText mPrice1Text;
	EditText mPrice2Text;
	EditText mPrice3Text;
    
	EditText mCostPriceText;
    Spinner mPicRequiredSp;
    EditText mCommisionText;
    EditText mPromoPriceText;
    EditText mPromoStartDate;
    EditText mPromoEndDate;
    Spinner mQuantityTypeSp;
    EditText mStockText;
    EditText mMinStockText;
    Spinner mStatusSp;
    
    CodeSpinnerArrayAdapter statusArrayAdapter;
    CodeSpinnerArrayAdapter typeArrayAdapter;
    CodeSpinnerArrayAdapter picRequiredArrayAdapter;
    CodeSpinnerArrayAdapter quantityTypeArrayAdapter;
    
    private ProductDaoService mProductDaoService = new ProductDaoService();
    private ProductGroupDaoService mProductGroupDaoService = new ProductGroupDaoService();
    
    BaseItemListener<Product> mProductItemListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_product_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mProductItemListener = (BaseItemListener<Product>) activity;
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
    	
    	mPrice1Panel = (LinearLayout) view.findViewById(R.id.price1Panel);
    	mPrice2Panel = (LinearLayout) view.findViewById(R.id.price2Panel);
    	mPrice3Panel = (LinearLayout) view.findViewById(R.id.price3Panel);
    	
    	mCodeText = (EditText) view.findViewById(R.id.codeText);
    	mNameText = (EditText) view.findViewById(R.id.nameText);
    	mTypeSp = (Spinner) view.findViewById(R.id.typeSp);
    	mProductGroupText = (EditText) view.findViewById(R.id.productGroupText);
    	mPrice1Label = (TextView) view.findViewById(R.id.priceLabel1Text);
    	mPrice1Text = (EditText) view.findViewById(R.id.price1Text);
    	mPrice2Label = (TextView) view.findViewById(R.id.priceLabel2Text);
    	mPrice2Text = (EditText) view.findViewById(R.id.price2Text);
    	mPrice3Label = (TextView) view.findViewById(R.id.priceLabel3Text);
    	mPrice3Text = (EditText) view.findViewById(R.id.price3Text);
    	mCostPriceText = (EditText) view.findViewById(R.id.costPriceText);
    	mPicRequiredSp = (Spinner) view.findViewById(R.id.picRequiredSp);
    	mCommisionText = (EditText) view.findViewById(R.id.commisionText);
    	mPromoPriceText = (EditText) view.findViewById(R.id.promoPriceText);
    	mPromoStartDate = (EditText) view.findViewById(R.id.promoStartDate);
    	mPromoEndDate = (EditText) view.findViewById(R.id.promoEndDate);
    	mQuantityTypeSp = (Spinner) view.findViewById(R.id.quantityTypeSp);
    	mStockText = (EditText) view.findViewById(R.id.stockText);
    	mMinStockText = (EditText) view.findViewById(R.id.minStockText);
    	mStatusSp = (Spinner) view.findViewById(R.id.statusSp);
    	
    	mProductGroupText.setFocusable(false);
    	mProductGroupText.setOnClickListener(getProductGroupOnClickListener());
    	
    	registerField(mCodeText);
    	registerField(mNameText);
    	registerField(mTypeSp);
    	registerField(mProductGroupText);
    	registerField(mPrice1Text);
    	registerField(mPrice2Text);
    	registerField(mPrice3Text);
    	registerField(mCostPriceText);
    	registerField(mPicRequiredSp);
    	registerField(mCommisionText);
    	registerField(mPromoPriceText);
    	registerField(mPromoStartDate);
    	registerField(mPromoEndDate);
    	registerField(mQuantityTypeSp);
    	registerField(mStockText);
    	registerField(mMinStockText);
    	registerField(mStatusSp);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	mandatoryFields.add(new FormFieldBean(mPrice1Text, R.string.field_price));
    	
    	mPrice1Text.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mPrice2Text.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mPrice3Text.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	
    	mCostPriceText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mCommisionText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mPromoPriceText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mStockText.setOnFocusChangeListener(getNumberFieldOnFocusChangeListener());
    	mMinStockText.setOnFocusChangeListener(getNumberFieldOnFocusChangeListener());
    	
    	mPromoStartDate.setOnClickListener(getDateFieldOnClickListener("startDatePicker"));
    	mPromoEndDate.setOnClickListener(getDateFieldOnClickListener("endDatePicker"));
    	
    	linkDatePickerWithInputField("startDatePicker", mPromoStartDate);
    	linkDatePickerWithInputField("endDatePicker", mPromoEndDate);
    	
    	boolean isFnBMerchant = Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(MerchantUtil.getMerchant().getType());
    	
    	typeArrayAdapter = new CodeSpinnerArrayAdapter(mTypeSp, getActivity(), isFnBMerchant ? CodeUtil.getFnBProductTypes() : CodeUtil.getProductTypes());
    	mTypeSp.setAdapter(typeArrayAdapter);
    	
    	picRequiredArrayAdapter = new CodeSpinnerArrayAdapter(mPicRequiredSp, getActivity(), CodeUtil.getBooleans());
    	mPicRequiredSp.setAdapter(picRequiredArrayAdapter);
    	
    	statusArrayAdapter = new CodeSpinnerArrayAdapter(mStatusSp, getActivity(), CodeUtil.getProductStatus());
    	mStatusSp.setAdapter(statusArrayAdapter);
    	
    	quantityTypeArrayAdapter = new CodeSpinnerArrayAdapter(mQuantityTypeSp, getActivity(), CodeUtil.getQuantityTypes());
    	mQuantityTypeSp.setAdapter(quantityTypeArrayAdapter);
    	
    	Merchant merchant = MerchantUtil.getMerchant();
    	
    	mPrice1Label.setText(getString(R.string.field_price) + " " + CommonUtil.getNvlString(merchant.getPriceLabel1()));
    	mPrice2Label.setText(getString(R.string.field_price) + " " + CommonUtil.getNvlString(merchant.getPriceLabel2()));
    	mPrice3Label.setText(getString(R.string.field_price) + " " + CommonUtil.getNvlString(merchant.getPriceLabel3()));
    	
    	mPrice2Panel.setVisibility(View.VISIBLE);
    	mPrice3Panel.setVisibility(View.VISIBLE);
    	
    	if (merchant.getPriceTypeCount() < 2) {
    		mPrice2Panel.setVisibility(View.GONE);
    	}
    	
    	if (merchant.getPriceTypeCount() < 3) {
    		mPrice3Panel.setVisibility(View.GONE);
    	}
    }
    
    @Override
    protected void updateView(Product product) {
    	
    	if (product != null) {
    		
    		if (product.getPicRequired() == null) {
    			product.setPicRequired(Constant.STATUS_NO);
    		}
    		
    		int typeIndex = typeArrayAdapter.getPosition(product.getType());
    		int statusIndex = statusArrayAdapter.getPosition(product.getStatus());
    		int picRequiredIndex = picRequiredArrayAdapter.getPosition(product.getPicRequired());
    		int quantityTypeIndex = quantityTypeArrayAdapter.getPosition(product.getQuantityType());
    		
    		if (product.getProductGroupId() != null) {
    			
    			String productGroupName = mProductGroupDaoService.getProductGroup(product.getProductGroupId()).getName();
    			mProductGroupText.setText(productGroupName);
    			
    		} else {
    			
    			mProductGroupText.setText(Constant.EMPTY_STRING);
    		}
    		
    		mCodeText.setText(product.getCode());
    		mNameText.setText(product.getName());
    		mPrice1Text.setText(CommonUtil.formatCurrency(product.getPrice1()));
    		mPrice2Text.setText(CommonUtil.formatCurrency(product.getPrice2()));
    		mPrice3Text.setText(CommonUtil.formatCurrency(product.getPrice3()));
    		mCostPriceText.setText(CommonUtil.formatCurrency(product.getCostPrice()));
    		mCommisionText.setText(CommonUtil.formatCurrency(product.getCommision()));
    		mPromoPriceText.setText(CommonUtil.formatCurrency(product.getPromoPrice()));
    		mPromoStartDate.setText(CommonUtil.formatDate(product.getPromoStart()));
    		mPromoEndDate.setText(CommonUtil.formatDate(product.getPromoEnd()));
    		mStockText.setText(CommonUtil.formatNumber(product.getStock()));
    		mMinStockText.setText(CommonUtil.formatNumber(product.getMinStock()));
    		
    		mTypeSp.setSelection(typeIndex);
    		mStatusSp.setSelection(statusIndex);
    		mPicRequiredSp.setSelection(picRequiredIndex);
    		mQuantityTypeSp.setSelection(quantityTypeIndex);
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String code = mCodeText.getText().toString();
    	String name = mNameText.getText().toString();
    	String type = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
    	
    	Float price1 = CommonUtil.parseFloatCurrency(mPrice1Text.getText().toString());
    	Float price2 = CommonUtil.parseFloatCurrency(mPrice2Text.getText().toString());
    	Float price3 = CommonUtil.parseFloatCurrency(mPrice3Text.getText().toString());
    	Float costPrice = CommonUtil.parseFloatCurrency(mCostPriceText.getText().toString());
    	String picRequired = CodeBean.getNvlCode((CodeBean) mPicRequiredSp.getSelectedItem());
    	Float commision = CommonUtil.parseFloatCurrency(mCommisionText.getText().toString());
    	Float promoPrice = CommonUtil.parseFloatCurrency(mPromoPriceText.getText().toString());
    	Date startDate = CommonUtil.parseDate(mPromoStartDate.getText().toString());
    	Date endDate = CommonUtil.parseDate(mPromoEndDate.getText().toString());
    	String quantityType = CodeBean.getNvlCode((CodeBean) mQuantityTypeSp.getSelectedItem());
    	Float stock = CommonUtil.parseFloatCurrency(mStockText.getText().toString());
    	Float minStock = CommonUtil.parseFloatCurrency(mMinStockText.getText().toString());
    	String status = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setCode(code);
    		mItem.setName(name);
    		mItem.setType(type);
    		mItem.setPrice1(price1);
    		mItem.setPrice2(price2);
    		mItem.setPrice3(price3);
    		mItem.setCostPrice(costPrice);
    		mItem.setPicRequired(picRequired);
    		mItem.setCommision(commision);
    		mItem.setPromoPrice(promoPrice);
    		mItem.setPromoStart(startDate);
    		mItem.setPromoEnd(endDate);
    		mItem.setQuantityType(quantityType);
    		mItem.setStock(stock);
    		mItem.setMinStock(minStock);
    		mItem.setStatus(status);
    		
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
    protected Long getItemId(Product product) {
    	
    	return product.getId(); 
    } 
    
    @Override
    protected boolean isValidated() {
    	
    	boolean isValidated = super.isValidated();
    	
    	if (isValidated) {
    		
    		if ((mItem.getCostPrice() != null && mItem.getPrice1() != null && mItem.getPrice1() <= mItem.getCostPrice()) ||
    			(mItem.getCostPrice() != null && mItem.getPrice2() != null && mItem.getPrice2() <= mItem.getCostPrice()) ||
    			(mItem.getCostPrice() != null && mItem.getPrice3() != null && mItem.getPrice3() <= mItem.getCostPrice())) {
    			
    			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_product_price_issue));
    			isValidated = false;
    		}
    	}
    	
    	return isValidated;
    }
    
    @Override
    protected boolean addItem() {
    	
        mProductDaoService.addProduct(mItem);
        
        CommonUtil.sendEvent(getString(R.string.event_cat_product), getString(R.string.event_act_registration));
        
        mNameText.getText().clear();
        mProductGroupText.getText().clear();
        mPrice1Text.getText().clear();
        mPrice2Text.getText().clear();
        mPrice3Text.getText().clear();
        mCostPriceText.getText().clear();
        mCommisionText.getText().clear();
        mPromoPriceText.getText().clear();
        mPromoStartDate.getText().clear();
        mPromoEndDate.getText().clear();
        mStockText.getText().clear();
        mMinStockText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mProductDaoService.updateProduct(mItem);
    	
    	return true;
    }
    
    public void setProductGroup(ProductGroup productGroup) {
		
    	if (mItem != null) {
    		
    		if (productGroup != null) {
    			
    			mItem.setProductGroupId(productGroup.getId());
    			
    		} else {
    			
    			mItem.setProductGroup(null);	
    		}
    		
    		updateView(mItem);
    	}
	}
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public Product updateItem(Product product) {

    	product = mProductDaoService.getProduct(product.getId());
    	
    	this.mItem = product;
    	
    	return product;
    }
    
    private View.OnClickListener getProductGroupOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mProductGroupText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = false;
						mProductItemListener.onSelectProductGroup(isMandatory);
					}
				}
			}
		};
    }
}
package com.tokoku.pos.data.merchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.MerchantAccess;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.model.PaymentTypeStatus;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MerchantEditFragment extends BaseEditFragment<Merchant> {
    
	EditText mCodeText;
	EditText mNameText;
	Spinner mTypeSp;
    EditText mAddressText;
    EditText mTelephoneText;
    EditText mContactNameText;
    EditText mContactTelephoneText;
    EditText mContactEmailText;
    EditText mLoginIdText;
    EditText mPasswordText;
    EditText mPeriodStartDate;
    EditText mPeriodEndDate;
    EditText mLocaleText;
    EditText mTaxText;
    EditText mServiceChargeText;
    EditText mPriceLabel1Text;
    EditText mPriceLabel2Text;
    EditText mPriceLabel3Text;
    EditText mSecurityQuestionText;
    EditText mSecurityAnswerText;
    
    LinearLayout mPriceLabel1Panel;
    LinearLayout mPriceLabel2Panel;
    LinearLayout mPriceLabel3Panel;
    
    Spinner mPriceTypeCountSp;
    Spinner mOrderTypeSp;
    Spinner mStatusSp;
    
    LinearLayout mCodePanel;
    
    LinearLayout mStatusPanel;
    LinearLayout orderTypePanel;
    LinearLayout paymentTypePanel;
    LinearLayout accessRightPanel;
    LinearLayout accessRightRowPanel;
    
    List<PaymentTypeStatus> mPaymentTypeStatuses;
    List<MerchantAccess> mMerchantAccesses;
    
    List<ImageButton> mPaymentTypeButtons;
    List<ImageButton> mAccessButtons;
    
    CodeSpinnerArrayAdapter statusArrayAdapter;
    CodeSpinnerArrayAdapter typeArrayAdapter;
    CodeSpinnerArrayAdapter orderTypeArrayAdapter;
    CodeSpinnerArrayAdapter priceTypeCountArrayAdapter;
    
    BaseItemListener<Locale> mLocaleItemListener;
    
    private MerchantDaoService mMerchantDaoService = new MerchantDaoService();
    private MerchantAccessDaoService mMerchantAccessDaoService = new MerchantAccessDaoService();
    
    LayoutInflater mInflater;
    ViewGroup mContainer;
    
    View accessView;
    
    boolean mIsEnableInputFields;
    
    Locale mLocale;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	mInflater = inflater;
    	mContainer = container;
    	
    	View view = inflater.inflate(R.layout.data_merchant_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mLocaleItemListener = (BaseItemListener<Locale>) activity;
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
    	
    	mCodePanel = (LinearLayout) view.findViewById(R.id.codePanel);
    	
    	mCodeText = (EditText) view.findViewById(R.id.codeText);
        mNameText = (EditText) view.findViewById(R.id.nameText);
    	mTypeSp = (Spinner) view.findViewById(R.id.typeSp);
    	mAddressText = (EditText) view.findViewById(R.id.addressText);
    	mTelephoneText = (EditText) view.findViewById(R.id.telephoneText);
    	mContactNameText = (EditText) view.findViewById(R.id.contactNameText);
    	mContactTelephoneText = (EditText) view.findViewById(R.id.contactTelephoneText);
    	mContactEmailText = (EditText) view.findViewById(R.id.contactEmailText);
    	mLoginIdText = (EditText) view.findViewById(R.id.loginIdText);
    	mPasswordText = (EditText) view.findViewById(R.id.passwordText);
    	mPeriodStartDate = (EditText) view.findViewById(R.id.periodStartDate);
    	mPeriodEndDate = (EditText) view.findViewById(R.id.periodEndDate);
    	mLocaleText = (EditText) view.findViewById(R.id.localeText);
    	mPriceLabel1Text = (EditText) view.findViewById(R.id.priceLabel1Text);
    	mPriceLabel2Text = (EditText) view.findViewById(R.id.priceLabel2Text);
    	mPriceLabel3Text = (EditText) view.findViewById(R.id.priceLabel3Text);
    	mSecurityQuestionText = (EditText) view.findViewById(R.id.securityQuestionText);
    	mSecurityAnswerText = (EditText) view.findViewById(R.id.securityAnswerText);
    	mTaxText = (EditText) view.findViewById(R.id.taxText);
    	mServiceChargeText = (EditText) view.findViewById(R.id.serviceChargeText);
    	mPriceTypeCountSp = (Spinner) view.findViewById(R.id.priceTypeCountSp);
    	mOrderTypeSp = (Spinner) view.findViewById(R.id.orderTypeSp);
    	mStatusSp = (Spinner) view.findViewById(R.id.statusSp);
    	
    	if (!UserUtil.isRoot()) {
    		mCodePanel.setVisibility(View.GONE);
    	} else {
    		mCodePanel.setVisibility(View.VISIBLE);
    	}
    	
    	// disable locale selection for demo account
    	if (!UserUtil.isRoot() && MerchantUtil.getMerchantId() != Constant.DEMO_MERCHANT_ID) {
    		mLocaleText.setOnClickListener(getLocaleOnClickListener());
    	}
    	
    	mPriceTypeCountSp.setOnItemSelectedListener(getPriceTypeCountOnItemSelectedListener());
    	mTypeSp.setOnItemSelectedListener(getMerchantTypeItemSelectedListener());
    	
    	mStatusPanel = (LinearLayout) view.findViewById(R.id.statusPanel);
    	
    	mPriceLabel1Panel = (LinearLayout) view.findViewById(R.id.priceLabel1Panel);
    	mPriceLabel2Panel = (LinearLayout) view.findViewById(R.id.priceLabel2Panel);
    	mPriceLabel3Panel = (LinearLayout) view.findViewById(R.id.priceLabel3Panel);
    	
    	orderTypePanel = (LinearLayout) view.findViewById(R.id.orderTypePanel);
    	paymentTypePanel = (LinearLayout) view.findViewById(R.id.paymentTypePanel);
    	
    	accessRightRowPanel = (LinearLayout) view.findViewById(R.id.accessRightsRowPanel);
    	accessRightPanel = (LinearLayout) view.findViewById(R.id.accessRightsPanel);
    	
    	/*registerRootField(mNameText);
    	registerRootField(mTypeSp);
    	registerRootField(mAddressText);
    	registerRootField(mTelephoneText);*/
    	
    	//registerRootField(mCodeText);
    	registerRootField(mLoginIdText);
    	registerRootField(mPeriodStartDate);
    	registerRootField(mPeriodEndDate);
    	
    	registerField(mNameText);
    	registerField(mTypeSp);
    	registerField(mAddressText);
    	registerField(mTelephoneText);
    	registerField(mContactNameText);
    	registerField(mContactTelephoneText);
    	registerField(mContactEmailText);
    	registerField(mLoginIdText);
    	registerField(mPasswordText);
    	registerField(mPeriodStartDate);
    	registerField(mPeriodEndDate);
    	registerField(mLocaleText);
    	registerField(mPriceLabel1Text);
    	registerField(mPriceLabel2Text);
    	registerField(mPriceLabel3Text);
    	registerField(mSecurityQuestionText);
    	registerField(mSecurityAnswerText);
    	registerField(mTaxText);
    	registerField(mPriceTypeCountSp);
    	registerField(mOrderTypeSp);
    	registerField(mServiceChargeText);
    	registerField(mStatusSp);
    	
    	enableInputFields(false);
    	
    	mPeriodStartDate.setOnClickListener(getDateFieldOnClickListener("startDatePicker"));
        mPeriodEndDate.setOnClickListener(getDateFieldOnClickListener("endDatePicker"));
		
    	linkDatePickerWithInputField("startDatePicker", mPeriodStartDate);
    	linkDatePickerWithInputField("endDatePicker", mPeriodEndDate);
    	
    	typeArrayAdapter = new CodeSpinnerArrayAdapter(mTypeSp, getActivity(), CodeUtil.getMerchantTypes());
    	mTypeSp.setAdapter(typeArrayAdapter);
    	
    	orderTypeArrayAdapter = new CodeSpinnerArrayAdapter(mOrderTypeSp, getActivity(), CodeUtil.getOrderTypes());
    	mOrderTypeSp.setAdapter(orderTypeArrayAdapter);
    	
    	priceTypeCountArrayAdapter = new CodeSpinnerArrayAdapter(mPriceTypeCountSp, getActivity(), CodeUtil.getPriceTypeCount());
    	mPriceTypeCountSp.setAdapter(priceTypeCountArrayAdapter);
    	
    	statusArrayAdapter = new CodeSpinnerArrayAdapter(mStatusSp, getActivity(), CodeUtil.getStatus());
    	mStatusSp.setAdapter(statusArrayAdapter);
    	
    	if (UserUtil.isRoot()) {
    		mStatusPanel.setVisibility(View.VISIBLE);
    		mPasswordText.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			mStatusPanel.setVisibility(View.GONE);
		}
    }
    
    @Override
    protected void updateView(Merchant merchant) {
    	
    	if (merchant != null) {
    		
    		int typeIndex = typeArrayAdapter.getPosition(merchant.getType());
    		int priceTypeCountIndex = priceTypeCountArrayAdapter.getPosition(String.valueOf(merchant.getPriceTypeCount()));
    		int orderTypeIndex = orderTypeArrayAdapter.getPosition(merchant.getOrderType());
    		int statusIndex = statusArrayAdapter.getPosition(merchant.getStatus());
    		
    		String code = CommonUtil.formatString(merchant.getRefId());
    		code = code.substring(code.length()-6);
    		
    		mCodeText.setText(code);
    		
    		mNameText.setText(merchant.getName());
    		mAddressText.setText(merchant.getAddress());
    		mTelephoneText.setText(merchant.getTelephone());
    		mContactNameText.setText(merchant.getContactName());
    		mContactTelephoneText.setText(merchant.getContactTelephone());
    		mContactEmailText.setText(merchant.getContactEmail());
    		mLoginIdText.setText(merchant.getLoginId());
    		mPasswordText.setText(merchant.getPassword());
    		mPeriodStartDate.setText(CommonUtil.formatDate(merchant.getPeriodStart()));
    		mPeriodEndDate.setText(CommonUtil.formatDate(merchant.getPeriodEnd()));
    		
    		mLocale = CommonUtil.parseLocale(merchant.getLocale());
    		mLocaleText.setText(mLocale.getDisplayName());
    		
    		mPriceLabel1Text.setText(merchant.getPriceLabel1());
    		mPriceLabel2Text.setText(merchant.getPriceLabel2());
    		mPriceLabel3Text.setText(merchant.getPriceLabel3());
    		mSecurityQuestionText.setText(CommonUtil.getNvlString(merchant.getSecurityQuestion()));
    		mSecurityAnswerText.setText(CommonUtil.getNvlString(merchant.getSecurityAnswer()));
    		
    		mTaxText.setText(CommonUtil.formatNumber(merchant.getTaxPercentage()));
    		mServiceChargeText.setText(CommonUtil.formatNumber(merchant.getServiceChargePercentage()));
    		
    		mTypeSp.setSelection(typeIndex);
    		mPriceTypeCountSp.setSelection(priceTypeCountIndex);
    		mOrderTypeSp.setSelection(orderTypeIndex);
    		mStatusSp.setSelection(statusIndex);
    		
    		if (UserUtil.isRoot()) {
    			// accessRightRowPanel.setVisibility(View.VISIBLE);
    			accessRightRowPanel.setVisibility(View.GONE);
    		} else {
    			accessRightRowPanel.setVisibility(View.GONE);
    		}
    		
    		paymentTypePanel.removeAllViews();
    		
    		mPaymentTypeButtons = new ArrayList<ImageButton>();
    		
    		mPaymentTypeStatuses = getPaymentStatuses();
    		
    		for (final PaymentTypeStatus paymentTypeStatus : mPaymentTypeStatuses) {
        		
        		View paymentTypeView = mInflater.inflate(R.layout.data_merchant_access_list_item, mContainer, false);
        		
        		TextView nameText = (TextView) paymentTypeView.findViewById(R.id.nameText);
    			final ImageButton paymentTypeButton = (ImageButton) paymentTypeView.findViewById(R.id.checkedButton);
    			
    			paymentTypeButton.setEnabled(mIsEnableInputFields);
    			mPaymentTypeButtons.add(paymentTypeButton);
    			
    			char c = 8226;
    			
    			nameText.setText(c + " " + paymentTypeStatus.getPaymentType().getLabel());
    			
    			boolean isChecked = Constant.STATUS_YES.equals(paymentTypeStatus.getStatus());
    			
    			if (isChecked) {
					paymentTypeButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
				} else {
					paymentTypeButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
				}
    			
    			paymentTypeButton.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					
    					if (!isEnableInputFields) {
    						return;
    					}
    					
    					boolean isSelected = Constant.STATUS_YES.equals(paymentTypeStatus.getStatus());
    					
    					if (!isSelected) {
    						paymentTypeButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
    						paymentTypeStatus.setStatus(Constant.STATUS_YES);
    					} else {
    						paymentTypeButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
    						paymentTypeStatus.setStatus(Constant.STATUS_NO);
    					}
    				}
    			});
    			
    			paymentTypePanel.addView(paymentTypeView);
        	}
    		
    		accessRightPanel.removeAllViews();
    		
    		mMerchantAccesses = mMerchantAccessDaoService.getMerchantAccessList(merchant.getId());
    		
    		mAccessButtons = new ArrayList<ImageButton>();
    		
    		for (final MerchantAccess merchantAccess : mMerchantAccesses) {
        		
        		accessView = mInflater.inflate(R.layout.data_merchant_access_list_item, mContainer, false);
        		
        		TextView nameText = (TextView) accessView.findViewById(R.id.nameText);
    			final ImageButton checkedButton = (ImageButton) accessView.findViewById(R.id.checkedButton);
    			
    			checkedButton.setEnabled(mIsEnableInputFields);
    			mAccessButtons.add(checkedButton);
    			
    			char c = 8226;
    			
    			nameText.setText(c + " " + CodeUtil.getModuleAccessLabel(merchantAccess.getCode()));
    			
    			boolean isChecked = Constant.STATUS_YES.equals(merchantAccess.getStatus());
    			
    			if (isChecked) {
					checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
				} else {
					checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
				}
    			
    			checkedButton.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					
    					if (!isEnableInputFields) {
    						return;
    					}
    					
    					boolean isSelected = Constant.STATUS_YES.equals(merchantAccess.getStatus());
    					
    					if (!isSelected) {
    						checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
    						merchantAccess.setStatus(Constant.STATUS_YES);
    					} else {
    						checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
    						merchantAccess.setStatus(Constant.STATUS_NO);
    					}
    				}
    			});
    			
    			accessRightPanel.addView(accessView);
        	}
    		
    		refreshVisibleField();
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	String type = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
    	String address = mAddressText.getText().toString();
    	String telephone = mTelephoneText.getText().toString();
    	String contactName = mContactNameText.getText().toString();
    	String contactTelephone = mContactTelephoneText.getText().toString();
    	String contactEmail = mContactEmailText.getText().toString();
    	String loginId = mLoginIdText.getText().toString();
    	String password = mPasswordText.getText().toString();
    	Float tax = CommonUtil.parseFloatNumber(mTaxText.getText().toString());
    	Float serviceCharge = CommonUtil.parseFloatNumber(mServiceChargeText.getText().toString());
    	Date startDate = CommonUtil.parseDate(mPeriodStartDate.getText().toString());
    	Date endDate = CommonUtil.parseDate(mPeriodEndDate.getText().toString());
    	
    	String locale = mLocale != null ? mLocale.getLanguage() + "," + mLocale.getCountry() : Constant.EMPTY_STRING;
    	
    	Integer priceTypeCount = Integer.valueOf(CodeBean.getNvlCode((CodeBean) mPriceTypeCountSp.getSelectedItem()));
    	String priceLabel1 = mPriceLabel1Text.getText().toString();
    	String priceLabel2 = mPriceLabel2Text.getText().toString();
    	String priceLabel3 = mPriceLabel3Text.getText().toString();
    	String securityQuestion = mSecurityQuestionText.getText().toString();
    	String securityAnswer = mSecurityAnswerText.getText().toString();
    	String orderType = CodeBean.getNvlCode((CodeBean) mOrderTypeSp.getSelectedItem());
    	String status = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
    	
    	if (mItem != null) {
    		
    		mItem.setName(name);
    		mItem.setType(type);
    		mItem.setAddress(address);
    		mItem.setTelephone(telephone);
    		mItem.setContactName(contactName);
    		mItem.setContactTelephone(contactTelephone);
    		mItem.setContactEmail(contactEmail);
    		mItem.setLoginId(loginId);
    		mItem.setPassword(password);
    		mItem.setPeriodStart(startDate);
    		mItem.setPeriodEnd(endDate);
    		mItem.setLocale(locale);
    		mItem.setPriceTypeCount(priceTypeCount);
    		mItem.setPriceLabel1(priceLabel1);
    		mItem.setPriceLabel2(priceLabel2);
    		mItem.setPriceLabel3(priceLabel3);
    		mItem.setSecurityQuestion(securityQuestion);
    		mItem.setSecurityAnswer(securityAnswer);
    		mItem.setTaxPercentage(tax);
    		mItem.setServiceChargePercentage(serviceCharge);
    		mItem.setOrderType(orderType);
    		mItem.setStatus(status);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		
    		String userId = UserUtil.getUser().getUserId();
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(userId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(userId);
    		mItem.setUpdateDate(new Date());
    		
    		String paymentType = Constant.EMPTY_STRING;
    		
    		for (PaymentTypeStatus paymentTypeStatus : mPaymentTypeStatuses) {
    			
    			if (Constant.STATUS_YES.equals(paymentTypeStatus.getStatus())) {
    				
    				paymentType += !CommonUtil.isEmpty(paymentType) ? Constant.DATA_SEPARATOR : Constant.EMPTY_STRING; 
    				paymentType += paymentTypeStatus.getPaymentType().getCode();
    			}
    		}
    		
    		mItem.setPaymentType(paymentType);
    		
    		for (MerchantAccess merchantAccess : mMerchantAccesses) {
    			
    			merchantAccess.setUploadStatus(Constant.STATUS_YES);
    			
        		if (merchantAccess.getCreateBy() == null) {
        			merchantAccess.setCreateBy(userId);
        			merchantAccess.setCreateDate(new Date());
        		}
        		
        		merchantAccess.setUpdateBy(userId);
        		merchantAccess.setUpdateDate(new Date());
        	}
    	}
    }
    
    @Override
    protected Long getItemId(Merchant merchant) {
    	
    	return merchant.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
    	boolean isUpdated = false;
    	
    	Merchant merchant = mMerchantDaoService.getMerchantByLoginId(mItem.getLoginId());
    	
    	// new provided login id is conflict with some other merchant
    	if (merchant != null) {
    		
    		NotificationUtil.setAlertMessage(getFragmentManager(), getActivity().getString(R.string.error_register_merchant_conflict));
    		
    	} else {
    		
	        mMerchantDaoService.addMerchant(mItem);
	        
	        for (MerchantAccess merchantAccess : mMerchantAccesses) {
	        	
	        	merchantAccess.setMerchant(mItem);
	        	mMerchantAccessDaoService.addMerchantAccess(merchantAccess);
	        }
	        
	        mNameText.getText().clear();
	        mAddressText.getText().clear();
	        mTelephoneText.getText().clear();
	        mContactNameText.getText().clear();
	        mContactTelephoneText.getText().clear();
	        mContactEmailText.getText().clear();
	        mLoginIdText.getText().clear();
	        mPasswordText.getText().clear();
	        mPeriodStartDate.getText().clear();
	        mPeriodEndDate.getText().clear();
	        mLocaleText.getText().clear();
	        mPriceLabel1Text.getText().clear();
	        mPriceLabel2Text.getText().clear();
	        mPriceLabel3Text.getText().clear();
	        mSecurityQuestionText.getText().clear();
	        mSecurityAnswerText.getText().clear();
	        mTaxText.getText().clear();
	        mServiceChargeText.getText().clear();
	        
	        mItem = null;
	        
	        isUpdated = true;
    	}
    	
    	return isUpdated;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	boolean isUpdated = false;
    	
    	Merchant merchant = mMerchantDaoService.getMerchantByLoginId(mItem.getLoginId());
    	
    	// new provided login id is conflict with some other merchant
    	if (merchant != null && merchant.getId().longValue() != mItem.getId().longValue()) {
    		
    		NotificationUtil.setAlertMessage(getFragmentManager(), getActivity().getString(R.string.error_register_merchant_conflict));
    	
    	} else {
    		
    		mMerchantDaoService.updateMerchant(mItem);
    		mMerchantAccessDaoService.updateMerchantAccess(mMerchantAccesses);
    		
    		if (MerchantUtil.getMerchantId() != null && // if root merchant id is null
    			MerchantUtil.getMerchantId().longValue() == mItem.getId().longValue()) {
    			
    			MerchantUtil.setMerchant(mItem);
    		}
    		
    		isUpdated = true;
    	}
    	
    	return isUpdated;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public Merchant updateItem(Merchant merchant) {

    	merchant = mMerchantDaoService.getMerchant(merchant.getId());
    	
    	this.mItem = merchant;
    	
    	return merchant;
    }
    
    @Override
    public void enableInputFields(boolean isEnabled) {
    	
    	super.enableInputFields(isEnabled);
    	
    	mIsEnableInputFields = isEnabled;
    	
    	if (mAccessButtons == null) {
    		return;
    	}
    	
    	for (ImageButton button : mPaymentTypeButtons) {
    		button.setEnabled(isEnabled);
    	}
    	
    	for (ImageButton button : mAccessButtons) {
    		button.setEnabled(isEnabled);
    	}
    	
    	// disable editing for demo account
    	
    	if (mItem != null && mItem.getId() == Constant.DEMO_MERCHANT_ID) {
    		mNameText.setEnabled(false);
    		mAddressText.setEnabled(false);
    		mLoginIdText.setEnabled(false);
    		mPasswordText.setEnabled(false);
    	}
    }
    
    private AdapterView.OnItemSelectedListener getPriceTypeCountOnItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
			
        	@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
        		refreshVisibleField();
        	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
    
    private AdapterView.OnItemSelectedListener getMerchantTypeItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
			
        	@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
        		refreshVisibleField();
        	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
    
    private View.OnClickListener getLocaleOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (isEnableInputFields) {
						
					saveItem();
					
					boolean isMandatory = true;
					mLocaleItemListener.onSelectLocale(isMandatory);
				}
			}
		};
    }
    
    private void refreshVisibleField() {
    	
    	Integer priceTypeCount = Integer.valueOf(CodeBean.getNvlCode((CodeBean) mPriceTypeCountSp.getSelectedItem()));
    	String merchantType = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
    	
    	mPriceLabel1Panel.setVisibility(View.GONE);
    	mPriceLabel2Panel.setVisibility(View.GONE);
    	mPriceLabel3Panel.setVisibility(View.GONE);
    	
		mandatoryFields.clear();
		mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	mandatoryFields.add(new FormFieldBean(mAddressText, R.string.field_address));
    	mandatoryFields.add(new FormFieldBean(mLoginIdText, R.string.field_login_id));
    	mandatoryFields.add(new FormFieldBean(mPasswordText, R.string.field_password));
    	mandatoryFields.add(new FormFieldBean(mPeriodStartDate, R.string.field_period_start));
    	mandatoryFields.add(new FormFieldBean(mPeriodEndDate, R.string.field_period_end));
    	mandatoryFields.add(new FormFieldBean(mTaxText, R.string.field_tax_percentage));
    	mandatoryFields.add(new FormFieldBean(mServiceChargeText, R.string.field_service_charge_percentage));
    	mandatoryFields.add(new FormFieldBean(mSecurityQuestionText, R.string.field_security_question));
    	mandatoryFields.add(new FormFieldBean(mSecurityAnswerText, R.string.field_security_answer));
    	
    	floatFields.add(new FormFieldBean(mTaxText, R.string.field_tax_percentage));
    	floatFields.add(new FormFieldBean(mServiceChargeText, R.string.field_service_charge_percentage));
		
    	if (priceTypeCount == 2) {
    		
    		mPriceLabel1Panel.setVisibility(View.VISIBLE);
    		mPriceLabel2Panel.setVisibility(View.VISIBLE);	
    		
    		mandatoryFields.add(new FormFieldBean(mPriceLabel1Text, R.string.field_price_label_1));
    		mandatoryFields.add(new FormFieldBean(mPriceLabel2Text, R.string.field_price_label_2));
    		
    	} else if (priceTypeCount == 3) {
    		
    		mPriceLabel1Panel.setVisibility(View.VISIBLE);
    		mPriceLabel2Panel.setVisibility(View.VISIBLE);	
    		mPriceLabel3Panel.setVisibility(View.VISIBLE);
    		
    		mandatoryFields.add(new FormFieldBean(mPriceLabel1Text, R.string.field_price_label_1));
    		mandatoryFields.add(new FormFieldBean(mPriceLabel2Text, R.string.field_price_label_2));
    		mandatoryFields.add(new FormFieldBean(mPriceLabel3Text, R.string.field_price_label_3));
    	}
    	
    	if (Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(merchantType) ||
    		Constant.MERCHANT_TYPE_GOODS_N_SERVICES.equals(merchantType)) {
    		
    		orderTypePanel.setVisibility(View.VISIBLE);
    		
    	} else {
    		
    		orderTypePanel.setVisibility(View.GONE);
    	}
    	
    	highlightMandatoryFields();
    }
    
    private List<PaymentTypeStatus> getPaymentStatuses() {
    	
    	String merchantPaymentType = mItem.getPaymentType();
    	
    	String[] merchantPaymentTypeStatus = !CommonUtil.isEmpty(merchantPaymentType) ? merchantPaymentType.split(Constant.DATA_SEPARATOR) : null;
    	HashMap<String, String> merchantPaymentTypeStatuses = new HashMap<String, String>();
    	
    	if (merchantPaymentTypeStatus != null) {
	    	
    		for (String paymentTypeStatus : merchantPaymentTypeStatus) {
	    		merchantPaymentTypeStatuses.put(paymentTypeStatus, Constant.STATUS_YES);
	    	}
    	}
    	
    	List<PaymentTypeStatus> paymentTypeStatuses = new ArrayList<PaymentTypeStatus>();
    	List<CodeBean> paymentTypes = Arrays.asList(CodeUtil.getPaymentTypes());
    	
    	for (CodeBean paymentType : paymentTypes) {
    		
    		PaymentTypeStatus paymentTypeStatus = new PaymentTypeStatus();
        	paymentTypeStatus.setPaymentType(paymentType);
        	
        	if (merchantPaymentTypeStatuses.get(paymentType.getCode()) != null) {
        		paymentTypeStatus.setStatus(Constant.STATUS_YES);
        	} else {
        		paymentTypeStatus.setStatus(Constant.STATUS_NO);
        	}
        	
        	paymentTypeStatuses.add(paymentTypeStatus);
    	}
    	
    	return paymentTypeStatuses;
    }
    
    public void setLocale(Locale locale) {
		
    	mLocale = locale;
    	
    	if (mItem != null) {
    		
    		if (mLocale != null) {

                String lang = CommonUtil.convertLanguageIso3toIso(mLocale.getISO3Language());
                String country = CommonUtil.convertCountryIso3toIso(mLocale.getISO3Country());

    			mItem.setLocale(lang + "," + country);
    		}
    		
    		updateView(mItem);
    	}
	}
    
    protected boolean isValidated() {
    	
    	if (super.isValidated()) {
    		if (CommonUtil.isEmpty(mItem.getPaymentType())) {
    			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_no_payment_type));
    			return false;
    		} else {
    			return true;
    		}
    	} else {
    		return false;
    	}
    }
}
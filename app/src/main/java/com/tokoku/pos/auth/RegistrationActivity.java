package com.tokoku.pos.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.MerchantAccess;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.popup.search.LocaleDlgFragment;
import com.tokoku.pos.popup.search.LocaleSelectionListener;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class RegistrationActivity extends BaseAuthActivity implements RegistrationListener, LocaleSelectionListener {

	Merchant mMerchant;
	
	TextView mInfoText;
	
	EditText mNameText;
	Spinner mTypeSp;
	EditText mAddressText;
	EditText mTelephoneText;
	EditText mEmailText;
	EditText mLocaleText;
	EditText mLoginIdText;
	EditText mPasswordText;
	EditText mPasswordConfirmText;
	EditText mSecurityQuestionText;
	EditText mSecurityAnswerText;
	
	Button mOkBtn;
	
	private LocaleDlgFragment mLocaleDlgFragment;
	
	private static String mLocaleDlgFragmentTag = "mLocaleDlgFragmentTag";
	
	private static final String LOCALE = "LOCALE";
	private static final String IS_REGISTRATION_SUCCESSFUL = "IS_REGISTRATION_SUCCESSFUL";
	
	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
	CodeSpinnerArrayAdapter typeArrayAdapter;
	
	boolean mIsRegistrationSuccessful = false;
	
	Locale mLocale;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			mLocale = (Locale) savedInstanceState.getSerializable(LOCALE);
			mIsRegistrationSuccessful = (Boolean) savedInstanceState.getSerializable(IS_REGISTRATION_SUCCESSFUL);
		}
		
		if (mLocale == null) {
			mLocale = Locale.getDefault();
		}
		
		DbUtil.initDb(this);
		CodeUtil.initCodes(this);
		
		mMerchantDaoService = new MerchantDaoService();
		mMerchantAccessDaoService = new MerchantAccessDaoService();
		
		mHttpAsyncManager = new HttpAsyncManager(context);
		
		mLocaleDlgFragment = (LocaleDlgFragment) getFragmentManager().findFragmentByTag(mLocaleDlgFragmentTag);
		
		if (mLocaleDlgFragment == null) {
			mLocaleDlgFragment = new LocaleDlgFragment();
		}
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(mProgressDialogTag);
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
		
		setContentView(R.layout.auth_registration_activity);
		
		mInfoText = (TextView) findViewById(R.id.infoText);
		mTypeSp = (Spinner) findViewById(R.id.typeSp);
		mNameText = (EditText) findViewById(R.id.nameText);
		mAddressText = (EditText) findViewById(R.id.addressText);
		mTelephoneText = (EditText) findViewById(R.id.telephoneText);
		mEmailText = (EditText) findViewById(R.id.emailText);
		mLocaleText = (EditText) findViewById(R.id.localeText);
		mLoginIdText = (EditText) findViewById(R.id.loginIdText);
		mPasswordText = (EditText) findViewById(R.id.passwordText);
		mPasswordConfirmText = (EditText) findViewById(R.id.passwordConfirmText);
		mSecurityQuestionText = (EditText) findViewById(R.id.securityQuestionText);
		mSecurityAnswerText = (EditText) findViewById(R.id.securityAnswerText);
		
		registerField(mNameText);
		registerField(mAddressText);
		registerField(mTelephoneText);
		registerField(mEmailText);
		registerField(mLocaleText);
		registerField(mLoginIdText);
		registerField(mPasswordText);
		registerField(mPasswordConfirmText);
		registerField(mSecurityQuestionText);
		registerField(mSecurityAnswerText);
		
		registerMandatoryField(new FormFieldBean(mNameText, R.string.field_name));
		registerMandatoryField(new FormFieldBean(mAddressText, R.string.field_address));
		registerMandatoryField(new FormFieldBean(mTelephoneText, R.string.field_telephone));
		registerMandatoryField(new FormFieldBean(mEmailText, R.string.field_email));
		registerMandatoryField(new FormFieldBean(mLocaleText, R.string.field_locale));
		registerMandatoryField(new FormFieldBean(mLoginIdText, R.string.field_login_id));
		registerMandatoryField(new FormFieldBean(mPasswordText, R.string.field_password));
		registerMandatoryField(new FormFieldBean(mPasswordConfirmText, R.string.field_password_confirm));
		registerMandatoryField(new FormFieldBean(mSecurityQuestionText, R.string.field_security_question));
		registerMandatoryField(new FormFieldBean(mSecurityAnswerText, R.string.field_security_answer));
		
		highlightMandatoryFields();
		
		mLocaleText.setFocusable(false);
		mLocaleText.setOnClickListener(getLocaleOnClickListener());
		
		mOkBtn = (Button) findViewById(R.id.loginBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		typeArrayAdapter = new CodeSpinnerArrayAdapter(mTypeSp, this, CodeUtil.getMerchantTypes(), 
				R.layout.register_spinner_items, 
				R.layout.register_spinner_items_selected, 
				R.layout.register_spinner_selected_item);
		
    	mTypeSp.setAdapter(typeArrayAdapter);
    	
    	updateView();
    }
	
	public void onStart() {
		
		super.onStart();
		updateView();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(LOCALE, (Serializable) mLocale);
		outState.putSerializable(IS_REGISTRATION_SUCCESSFUL, (Serializable) mIsRegistrationSuccessful);
	}
	
	private View.OnClickListener getLocaleOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				boolean isMandatory = true;
				onSelectLocale(isMandatory);
			}
		};
    }
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!mIsRegistrationSuccessful) {
				
					if (isValidated()) {
						
						mProgressDialog.show(getFragmentManager(), mProgressDialogTag);
					
						UserUtil.setMerchant(false);
						
						String type = CodeBean.getNvlCode((CodeBean) mTypeSp.getSelectedItem());
						String locale = mLocale != null ? mLocale.getLanguage() + "," + mLocale.getCountry() : Constant.EMPTY_STRING;
						
						String name = mNameText.getText().toString();
						String address = mAddressText.getText().toString();
						String telephone = mTelephoneText.getText().toString();
						String email = mEmailText.getText().toString();
						String loginId = CommonUtil.trim(mLoginIdText.getText().toString());
						String password = CommonUtil.trim(mPasswordText.getText().toString());
						String securityQuestion = mSecurityQuestionText.getText().toString();
						String securityAnswer = CommonUtil.trim(mSecurityAnswerText.getText().toString());
						
						Merchant merchant = new Merchant();
						
						merchant.setType(type);
						merchant.setName(name);
						merchant.setAddress(address);
						merchant.setTelephone(telephone);
						merchant.setContactEmail(email);
						merchant.setLocale(locale);
						merchant.setLoginId(loginId);
						merchant.setPassword(password);
						merchant.setSecurityQuestion(securityQuestion);
						merchant.setSecurityAnswer(securityAnswer);
						
						// default value
						
						merchant.setRefId(CommonUtil.generateRefId());
						
						Calendar start = Calendar.getInstance();
						Calendar end = Calendar.getInstance();
						end.add(Calendar.MONTH, 1);
						
						merchant.setPrinterRequired(Constant.STATUS_INACTIVE);
						merchant.setPrinterMiniFont(Constant.STATUS_YES);
						merchant.setPrinterLineSize(32);
						
						merchant.setPeriodStart(start.getTime());
						merchant.setPeriodEnd(end.getTime());
						
						merchant.setPaymentType(Constant.PAYMENT_TYPE_CASH);
						merchant.setPriceTypeCount(1);
						merchant.setDiscountType(Constant.DISCOUNT_TYPE_PERCENTAGE);
						
						merchant.setTaxPercentage(Float.valueOf(0));
						merchant.setServiceChargePercentage(Float.valueOf(0));
						
						// enforce the user to perform account activation
						merchant.setStatus(Constant.STATUS_INACTIVE);
						
						merchant.setCreateBy(Constant.SYSTEM);
						merchant.setCreateDate(new Date());
						merchant.setUpdateBy(Constant.SYSTEM);
						merchant.setUpdateDate(new Date());
						
						MerchantBean merchantBean = BeanUtil.getBean(merchant);
						
						mHttpAsyncManager = new HttpAsyncManager(context);
						mHttpAsyncManager.registerMerchant(merchantBean);
					}
					
				} else {
					
					Intent intent = new Intent(context, UserLoginActivity.class);
					startActivity(intent);
				} 
			}
		};
	}
	
	protected boolean isValidated() {
    	
    	if (super.isValidated()) {
		
			if (!mPasswordText.getText().toString().equals(mPasswordConfirmText.getText().toString())) {
	    		
	    		NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_password_dont_match));
	    		mPasswordText.requestFocus();
	    		return false;
	    		
	    	} else {
	    		return true;
	    	}
    	} else {
    		return false;
    	}
    }
	
	private void onSelectLocale(boolean isMandatory) {
		
		if (mLocaleDlgFragment.isAdded()) {
			return;
		}
		
		mLocaleDlgFragment.setMandatory(isMandatory);
		mLocaleDlgFragment.show(getFragmentManager(), mLocaleDlgFragmentTag);
	}
	
	@Override
	public void onLocaleSelected(Locale locale) {
		
		mLocale = locale;
		
		updateView();
	}
	
	@Override
	public void onMerchantRegistered(Merchant merchant) {
		
		mProgressDialog.dismissAllowingStateLoss();
		
		if (merchant != null) {		
			
			mIsRegistrationSuccessful = true;
			
			mMerchant = merchant;
			
			MerchantUtil.setMerchant(mMerchant);
			
			mMerchant.setIsLogin(true);
			
			mMerchantDaoService.addMerchant(mMerchant);
			
			DbUtil.switchDb(getApplicationContext(), mMerchant.getId());
			
			mMerchantDaoService = new MerchantDaoService();
			mMerchantAccessDaoService = new MerchantAccessDaoService();
			
			mMerchantDaoService.addMerchant(mMerchant);
			
			List<MerchantAccess> mMerchantAccesses = mMerchantAccessDaoService.getMerchantAccessList(merchant.getId());
			
			for (MerchantAccess merchantAccess : mMerchantAccesses) {
	        	
	        	merchantAccess.setMerchant(mMerchant);
	        	merchantAccess.setUploadStatus(Constant.STATUS_YES);
	        	merchantAccess.setStatus(Constant.STATUS_YES);
	        	
	        	merchantAccess.setCreateBy(mMerchant.getLoginId());
	        	merchantAccess.setCreateDate(new Date());
	        	merchantAccess.setUpdateBy(mMerchant.getLoginId());
	        	merchantAccess.setUpdateDate(new Date());
	        	
	        	mMerchantAccessDaoService.addMerchantAccess(merchantAccess);
	        }
			
			updateView();
			
			CommonUtil.sendEvent(getString(R.string.event_cat_merchant), getString(R.string.event_act_registration));
		}
	}
	
	private void updateView() {
		
		mLocaleText.setText(mLocale.getDisplayName());
		
		if (mIsRegistrationSuccessful) {
			
			mInfoText.setVisibility(View.VISIBLE);
			mInfoText.setText(getString(R.string.msg_merchant_registration_success));
			
			mTypeSp.setVisibility(View.GONE);
			mNameText.setVisibility(View.GONE);
			mAddressText.setVisibility(View.GONE);
			mTelephoneText.setVisibility(View.GONE);
			mEmailText.setVisibility(View.GONE);
			mLocaleText.setVisibility(View.GONE);
			mLoginIdText.setVisibility(View.GONE);
			mPasswordText.setVisibility(View.GONE);
			mPasswordConfirmText.setVisibility(View.GONE);
			mSecurityQuestionText.setVisibility(View.GONE);
			mSecurityAnswerText.setVisibility(View.GONE);
		
		} else {
			
			mInfoText.setVisibility(View.GONE);
			mInfoText.setText(getString(R.string.msg_merchant_registration_info));
			
			mTypeSp.setVisibility(View.VISIBLE);
			mNameText.setVisibility(View.VISIBLE);
			mAddressText.setVisibility(View.VISIBLE);
			mTelephoneText.setVisibility(View.VISIBLE);
			mEmailText.setVisibility(View.VISIBLE);
			mLocaleText.setVisibility(View.VISIBLE);
			mLoginIdText.setVisibility(View.VISIBLE);
			mPasswordText.setVisibility(View.VISIBLE);
			mPasswordConfirmText.setVisibility(View.VISIBLE);
			mSecurityQuestionText.setVisibility(View.VISIBLE);
			mSecurityAnswerText.setVisibility(View.VISIBLE);
		}
	}
}

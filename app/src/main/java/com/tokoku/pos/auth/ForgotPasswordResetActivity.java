package com.tokoku.pos.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class ForgotPasswordResetActivity extends BaseAuthActivity implements ForgotPasswordListener {

	Merchant mMerchant;
	
	TextView mInfoText;
	
	EditText mPasswordText;
	EditText mPasswordConfirmText;
	
	private static final String IS_RESET_SUCCESSFUL = "IS_RESET_SUCCESSFUL";
	
	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
	Button mOkBtn;
	
	Boolean mIsResetSuccessful = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DbUtil.initDb(this);
		CodeUtil.initCodes(this);
		
		if (savedInstanceState != null) {
			mIsResetSuccessful = (Boolean) savedInstanceState.getSerializable(IS_RESET_SUCCESSFUL);
		}
		
		mMerchantDaoService = new MerchantDaoService();
		
		mHttpAsyncManager = new HttpAsyncManager(context);
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(mProgressDialogTag);
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
		
		setContentView(R.layout.auth_forgot_password_reset_activity);
		
		mInfoText = (TextView)  findViewById(R.id.infoText);
		mPasswordText = (EditText) findViewById(R.id.passwordText);
		mPasswordConfirmText = (EditText) findViewById(R.id.passwordConfirmText);
		
		registerField(mPasswordText);
		registerField(mPasswordConfirmText);
		
		registerMandatoryField(new FormFieldBean(mPasswordText, R.string.password));
		registerMandatoryField(new FormFieldBean(mPasswordConfirmText, R.string.field_password_confirm));
		
		highlightMandatoryFields();
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		mMerchant = MerchantUtil.getMerchant();
		
		String securityQuestion = mMerchant.getSecurityAnswer();
		securityQuestion += securityQuestion.contains("?") ? Constant.EMPTY_STRING : " ?";
		
		mInfoText.setText(mMerchant.getName());
		
		updateView();
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putSerializable(IS_RESET_SUCCESSFUL, (Serializable) mIsResetSuccessful);
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!mIsResetSuccessful) {
					
					if (isValidated()) {
						
						//mProgressDialog.show(getFragmentManager(), mProgressDialogTag);
					
						UserUtil.setMerchant(false);
						
						String loginId = MerchantUtil.getMerchant().getLoginId();
						String password = mPasswordText.getText().toString();
						
						Merchant merchant = mMerchantDaoService.getMerchantByLoginId(loginId);
						
						if (merchant != null) {
							
							DbUtil.switchDb(getApplicationContext(), mMerchant.getId());
							mMerchantDaoService = new MerchantDaoService();
							
							merchant = mMerchantDaoService.getMerchantByLoginId(loginId);
							
							merchant.setPassword(password);
							merchant.setUploadStatus(Constant.STATUS_YES);
							
							mMerchantDaoService.updateMerchant(merchant);
							
							DbUtil.switchDb(getApplicationContext(), null);
							mMerchantDaoService = new MerchantDaoService();
						}
						
						//mHttpAsyncManager = new HttpAsyncManager(context);
						//mHttpAsyncManager.resetPassword(loginId, password);
						
						mIsResetSuccessful = true;
						updateView();
					}
				
				} else {
					
					MerchantUtil.setMerchant(null);
					
					Intent intent = new Intent(context, MerchantLoginActivity.class);
					startActivity(intent);
					
					finish();
				}
			}
		};
	}
	
	@Override
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
	
	@Override
	public void onMerchantRetrieved(Merchant merchant) {
		
		if (merchant != null) {		
			
			mIsResetSuccessful = true;
			updateView();
		}
		
		mProgressDialog.dismissAllowingStateLoss();
	}
	
	private void updateView() {
		
		mInfoText.setText(getString(R.string.msg_reset_password_success));
		
		if (mIsResetSuccessful) {
			
			mInfoText.setVisibility(View.VISIBLE);
			mPasswordText.setVisibility(View.GONE);
			mPasswordConfirmText.setVisibility(View.GONE);
		
		} else {
			
			mInfoText.setVisibility(View.GONE);
			mPasswordText.setVisibility(View.VISIBLE);
			mPasswordConfirmText.setVisibility(View.VISIBLE);
		}
	}
}

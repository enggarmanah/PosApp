package com.tokoku.pos.auth;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class ForgotPasswordActivity extends BaseAuthActivity implements ForgotPasswordListener {

	Merchant mMerchant;
	
	EditText mLoginIdText;
	
	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
	Button mOkBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DbUtil.initDb(this);
		CodeUtil.initCodes(this);
		
		mMerchantDaoService = new MerchantDaoService();
		mMerchantAccessDaoService = new MerchantAccessDaoService();
		
		mHttpAsyncManager = new HttpAsyncManager(context);
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(mProgressDialogTag);
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
		
		setContentView(R.layout.auth_forgot_password_activity);
		
		mLoginIdText = (EditText) findViewById(R.id.loginIdText);
		
		registerField(mLoginIdText);
		
		registerMandatoryField(new FormFieldBean(mLoginIdText, R.string.field_login_id));
		
		highlightMandatoryFields();
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
    }
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (isValidated()) {
					
					UserUtil.setMerchant(false);
					
					String loginId = mLoginIdText.getText().toString();
					
					Merchant merchant = mMerchantDaoService.getMerchantByLoginId(loginId);
					
					if (merchant != null) {
						
						MerchantUtil.setMerchant(merchant);
						
						Intent intent = new Intent(context, ForgotPasswordValidateActivity.class);
						startActivity(intent);
						
					} else {
						
						mProgressDialog.show(getFragmentManager(), mProgressDialogTag);
						
						mHttpAsyncManager = new HttpAsyncManager(context);
						mHttpAsyncManager.getMerchantByLoginId(loginId);
					}
				}
			}
		};
	}
	
	@Override
	public void onMerchantRetrieved(Merchant merchant) {
		
		mProgressDialog.dismissAllowingStateLoss();
		
		if (merchant != null) {		
			
			mMerchant = merchant;
			MerchantUtil.setMerchant(mMerchant);
			
			Intent intent = new Intent(context, ForgotPasswordValidateActivity.class);
			startActivity(intent);
		
		} else {
			
			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_merchant_login_id_not_exist));
    		mLoginIdText.requestFocus();
		}
	}
}

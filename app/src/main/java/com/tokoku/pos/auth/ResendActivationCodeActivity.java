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
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;

public class ResendActivationCodeActivity extends BaseAuthActivity implements ResendActivationCodeListener {

	Merchant mMerchant;
	
	EditText mEmailText;
	
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
		
		setContentView(R.layout.auth_resend_email_activity);
		
		mEmailText = (EditText) findViewById(R.id.emailText);
		
		registerField(mEmailText);
		
		registerMandatoryField(new FormFieldBean(mEmailText, R.string.field_security_answer));
		
		highlightMandatoryFields();
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		mMerchant = MerchantUtil.getMerchant();
    }
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (isValidated()) {
					
					mProgressDialog.show(getFragmentManager(), mProgressDialogTag);
					
					String email = mEmailText.getText().toString();
					
					Merchant merchant = MerchantUtil.getMerchant();
					merchant.setContactEmail(email);
					
					MerchantBean merchantBean = BeanUtil.getBean(merchant);
					
					mHttpAsyncManager = new HttpAsyncManager(context);
					mHttpAsyncManager.resendActivationCode(merchantBean);
				}
			}
		};
	}
	
	@Override
	public void onCodeSent(Merchant merchant) {
		
		mProgressDialog.dismissAllowingStateLoss();
		
		if (merchant != null) {	
			
			mMerchantDaoService.updateMerchant(merchant);
			
			Intent intent = new Intent(context, MerchantActivationActivity.class);
			startActivity(intent);
			
			finish();
		}
	}
}

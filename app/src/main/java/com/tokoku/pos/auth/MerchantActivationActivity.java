package com.tokoku.pos.auth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.MerchantAccess;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class MerchantActivationActivity extends BaseAuthActivity {

	Merchant mMerchant;
	
	TextView mActivationInfoText;
	TextView mResendActivationCodeText;
	TextView mHelpText;
	
	EditText mActivationCodeText;
	
	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
	Button mOkBtn;
	
	protected static final String progressDialogTag = "progressDialogTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DbUtil.initDb(this);
		CodeUtil.initCodes(this);
		
		MerchantUtil.recreateDao();
		
		mMerchantDaoService = new MerchantDaoService();
		mMerchantAccessDaoService = new MerchantAccessDaoService();
		
		setContentView(R.layout.auth_activation_activity);
		
	}
	
	private void initVariables() {
		
		mActivationInfoText = (TextView) findViewById(R.id.activationInfoText);
		mResendActivationCodeText = (TextView) findViewById(R.id.resendActivationCodeText);
		mHelpText = (TextView) findViewById(R.id.helpText);
		mActivationCodeText = (EditText) findViewById(R.id.activationCodeText);
		
		clearRegisteredFields();
		registerField(mActivationCodeText);
		
		clearRegisteredMandatoryFields();
		registerMandatoryField(new FormFieldBean(mActivationCodeText, R.string.merchant_activation_hint));
		
		highlightMandatoryFields();
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		
		mResendActivationCodeText.setOnClickListener(getResendActivationCodeTextOnClickListener());
		mHelpText.setOnClickListener(getHelpTextOnClickListener());
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		initVariables();
		
		mMerchant = MerchantUtil.getMerchant();
		
		if (mMerchant != null) {
			mActivationInfoText.setText(getString(R.string.msg_merchant_activation_info, mMerchant.getContactEmail()));
		} else {
			mActivationInfoText.setText(getString(R.string.msg_merchant_activation_info_contact_us));
		}
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (isValidated()) {
					
					UserUtil.setMerchant(false);
					
					mMerchant = MerchantUtil.getMerchant();
					
					String activationCode = mActivationCodeText.getText().toString();
					
					String correctCode4 = mMerchant.getRefId().substring(mMerchant.getRefId().length()-4);
					String correctCode6 = mMerchant.getRefId().substring(mMerchant.getRefId().length()-6);
					
					boolean isEqual4 = correctCode4.equals(activationCode);
					boolean isEqual6 = correctCode6.equals(activationCode);
					
					if (isEqual4 || isEqual6) {
						
						mMerchant.setStatus(Constant.STATUS_ACTIVE);
			        	mMerchant.setUploadStatus(Constant.STATUS_YES);
						mMerchantDaoService.updateMerchant(mMerchant);
						
						List<MerchantAccess> mMerchantAccesses = mMerchantAccessDaoService.getMerchantAccessList(mMerchant.getId());
						
						for (MerchantAccess merchantAccess : mMerchantAccesses) {
				        	
				        	merchantAccess.setMerchant(mMerchant);
				        	merchantAccess.setUploadStatus(Constant.STATUS_YES);
				        	merchantAccess.setStatus(Constant.STATUS_YES);
				        	
				        	merchantAccess.setCreateBy(mMerchant.getLoginId());
				        	merchantAccess.setCreateDate(new Date());
				        	merchantAccess.setUpdateBy(mMerchant.getLoginId());
				        	merchantAccess.setUpdateDate(new Date());
				        	
				        	if (merchantAccess.getId() == null) {
				        		mMerchantAccessDaoService.addMerchantAccess(merchantAccess);
				        	} else {
				        		mMerchantAccessDaoService.updateMerchantAccess(merchantAccess);
				        	}
				        }
						
						CommonUtil.sendEvent(getString(R.string.event_cat_merchant), getString(R.string.event_act_activation));
						
						/*Intent intent = new Intent(context, UserLoginActivity.class);
						startActivity(intent);
						
						finish();*/
						
						if (mProgressDialog.isAdded()) {
							return;
						}
						
						mProgressDialog.show(getFragmentManager(), progressDialogTag);
						
						if (mHttpAsyncManager == null) {
							mHttpAsyncManager = new HttpAsyncManager(context);
						}
						
						MerchantBean merchantBean = BeanUtil.getBean(mMerchant);
						mHttpAsyncManager.activateMerchant(merchantBean);
						
					} else {
						
						NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_activation_code_incorrect));
					}
				}
			}
		};
	}
	
	private View.OnClickListener getResendActivationCodeTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, ResendActivationCodeActivity.class);
				startActivity(intent);
				
				finish();
			}
		};
	}
		
	private View.OnClickListener getHelpTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String number = Constant.ADMIN_CONTACT;
		        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
			}
		};
	}
	
	@Override
	protected void onSyncCompleted() {
		
		Intent intent = new Intent(context, UserLoginActivity.class);
		startActivity(intent);
		
		finish();
	}
}

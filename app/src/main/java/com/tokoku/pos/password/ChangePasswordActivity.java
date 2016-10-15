package com.tokoku.pos.password;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.User;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BasePopUpActivity;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.dao.UserDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends BasePopUpActivity {

	EditText mPasswordText;
	EditText mPasswordNewText;
	EditText mPasswordConfirmText;
	
	Button mOkBtn;
	Button mCancelBtn;
	
	private UserDaoService mUserDaoService = new UserDaoService();
	private MerchantDaoService mMerchantDaoService = new MerchantDaoService();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setTheme(android.R.style.Theme_Holo_Light_Dialog);
		
		setContentView(R.layout.password_change_activity);
		
		setFinishOnTouchOutside(false);
	}

	@Override
	public void onStart() {

		super.onStart();

		mPasswordText = (EditText) findViewById(R.id.passwordText);
		mPasswordNewText = (EditText) findViewById(R.id.passwordNewText);
		mPasswordConfirmText = (EditText) findViewById(R.id.passwordConfirmText);

		registerField(mPasswordText);
		registerField(mPasswordNewText);
		registerField(mPasswordConfirmText);
		
		registerMandatoryField(new FormFieldBean(mPasswordText, R.string.field_password));
		registerMandatoryField(new FormFieldBean(mPasswordNewText, R.string.field_password_new));
		registerMandatoryField(new FormFieldBean(mPasswordConfirmText, R.string.field_password_confirm));
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mCancelBtn = (Button) findViewById(R.id.cancelBtn);

		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
	}
	
	@Override
	protected boolean isValidated() {
		
		if (super.isValidated()) {
			
			String password = mPasswordText.getText().toString();
			String passwordNew = mPasswordNewText.getText().toString();
			String passwordConfirm = mPasswordConfirmText.getText().toString();
			
			String passwordCurrent = Constant.EMPTY_STRING;
			
			if (UserUtil.isMerchant()) {
				passwordCurrent = MerchantUtil.getMerchant().getPassword();
			} else {
				passwordCurrent = UserUtil.getUser().getPassword();
			}
			
			if (!passwordCurrent.equals(password)) {
				
				NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_password_incorrect));
				mPasswordText.requestFocus();
				return false;
			
			} else if (!passwordNew.equals(passwordConfirm)) {
				
				NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_password_dont_match));
				mPasswordConfirmText.requestFocus();
				return false;
			
			} else {
				
				return true;
			}
			
		} else {
			
			return false;
		}
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (isValidated()) {
				
					String password = mPasswordNewText.getText().toString();
					
					if (UserUtil.isMerchant()) {
						
						Merchant merchant = MerchantUtil.getMerchant();
						merchant.setPassword(password);
						
						mMerchantDaoService.updateMerchant(merchant);
					
					} else {
						
						User user = UserUtil.getUser();
				    	user.setPassword(password);
						
						mUserDaoService.updateUser(user);
					}
					
					finish();
				}
			}
		};
	}

	private View.OnClickListener getCancelBtnOnClickListener() {

		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		};
	}
}

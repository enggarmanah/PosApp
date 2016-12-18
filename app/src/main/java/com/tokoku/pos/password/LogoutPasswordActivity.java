package com.tokoku.pos.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.pos.dao.Merchant;
import com.android.pos.dao.User;
import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.tokoku.pos.base.activity.BasePopUpActivity;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.dao.UserDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class LogoutPasswordActivity extends BasePopUpActivity {

	EditText mPasswordText;
	
	Button mOkBtn;
	Button mCancelBtn;
	
	private UserDaoService mUserDaoService = new UserDaoService();
	private MerchantDaoService mMerchantDaoService = new MerchantDaoService();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setTheme(android.R.style.Theme_Holo_Light_Dialog);
		
		setContentView(R.layout.merchant_logout_activity);
		
		setFinishOnTouchOutside(false);
	}

	@Override
	public void onStart() {

		super.onStart();

		mPasswordText = (EditText) findViewById(R.id.passwordText);

		registerField(mPasswordText);
		
		registerMandatoryField(new FormFieldBean(mPasswordText, R.string.field_password));
		
		mOkBtn = (Button) findViewById(R.id.okBtn);
		mCancelBtn = (Button) findViewById(R.id.cancelBtn);

		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
	}
	
	@Override
	protected boolean isValidated() {
		
		if (super.isValidated()) {
			
			String password = mPasswordText.getText().toString();
			
			String merchantPassword = MerchantUtil.getMerchant().getPassword();

			if (!merchantPassword.equals(password)) {
				
				NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_password_incorrect));
				mPasswordText.requestFocus();
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

					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK, resultIntent);
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

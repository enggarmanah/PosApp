package com.tokoku.pos.auth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.tokoku.pos.async.HttpAsyncListener;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.password.LogoutPasswordActivity;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;

public class BaseAuthActivity extends Activity implements HttpAsyncListener {

	protected final Context context = this;
	
	protected HttpAsyncManager mHttpAsyncManager;
	protected static ProgressDlgFragment mProgressDialog;
	
	protected static String mProgressDialogTag = "progressDialogTag";
	
	protected MerchantDaoService mMerchantDaoService;
	protected MerchantAccessDaoService mMerchantAccessDaoService;
	
	protected static Integer mProgress = 0;
	
	Merchant mMerchant;
	
	EditText mLoginIdText;
	
	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
	Button mOkBtn;

	protected static final int AUTHENTICHATION_ON_EXIT = 1;
	
	@Override
	public void onStart() {
		super.onStart();
		activityVisible = true;
		
		if (mProgress == 100 && mProgressDialog.isVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		activityVisible = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		activityVisible = false;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	}
	
	private static boolean activityVisible = false;
	
	public static boolean isActivityVisible() {
		return activityVisible;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.user_login_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_item_exit:

			if (MerchantUtil.getMerchantId().longValue() != Constant.DEMO_MERCHANT_ID) {

                Intent intent = new Intent(context, LogoutPasswordActivity.class);
				startActivityForResult(intent, AUTHENTICHATION_ON_EXIT);

			} else {
                Intent intent = new Intent(context, MerchantLoginActivity.class);
                intent.putExtra("logout", true);
                startActivity(intent);
            }
			
			return true;

		default:
			
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
	}
	
	protected void registerField(Object field) {
    	
    	mInputFields.add(field);
    }
	
	protected void clearRegisteredFields() {
		
		mInputFields.clear();
	}
	
	protected void registerMandatoryField(FormFieldBean formField) {
    	
    	mMandatoryFields.add(formField);
    }
	
	protected void clearRegisteredMandatoryFields() {
		
		mMandatoryFields.clear();
	}
	
	protected boolean isValidated() {
    	
    	for (FormFieldBean field : mMandatoryFields) {
    		
    		View input = field.getField();
    		String value = null;
    		
    		if (input instanceof TextView) {
    			
    			TextView inputText = (TextView) input;
    			value = inputText.getText().toString();
    		}
    		
    		if (CommonUtil.isEmpty(value)) {
    			
    			String fieldLabel = getString(field.getLabel());
    			
    			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_empty_mandatory_field, fieldLabel));
    			
    			input.requestFocus();
    			
    			return false;
    		}
    	}
    	
    	return true;
    }
	
	protected void highlightMandatoryFields() {
    	
		for (Object field : mInputFields) {
			
			if (field instanceof EditText) {
    			
				EditText editText = (EditText) field;
    			editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    		}
		}
		
    	for (FormFieldBean field : mMandatoryFields) {
    		
    		View input = field.getField();
    		String value = null;
    		EditText editText = null;
    		
    		if (input instanceof EditText) {
    			
    			editText = (EditText) input;
    			value = editText.getText().toString();
    			
    			editText.addTextChangedListener(getMandataryFieldOnTextChangedListener(editText));
    		}
    		
    		if (editText != null) {
    			
    			if (CommonUtil.isEmpty(value)) {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			} else {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			}
    		}
    	}
    }
	
	protected TextWatcher getMandataryFieldOnTextChangedListener(final EditText editText) {
    	
    	return new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				String value = editText.getText().toString();
				
				if (CommonUtil.isEmpty(value)) {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			} else {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			}
			}
		};
    }
	
	protected void onSyncCompleted() {
		System.out.println("task sync completed");
	}
	
	@Override
	public void setSyncProgress(int progress) {
		
		mProgress = progress;
		
		if (mProgressDialog != null) {
			
			mProgressDialog.setProgress(progress);
			
			if (progress == 100) {
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						
						if (isActivityVisible()) {
							mProgressDialog.dismissAllowingStateLoss();
						}
						
						onSyncCompleted();
					}
				}, 500);
			}
		}
	}
	
	@Override
	public void setSyncMessage(String message) {
		
		if (mProgressDialog != null) {
			
			mProgressDialog.setMessage(message);
		}
	}
	
	@Override
	public void onTimeOut() {
		
		mProgress = 100;
		
		if (isActivityVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
		
		NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_server_not_connected));
	}
	
	@Override
	public void onSyncError() {
		
		mProgress = 100;
		
		if (isActivityVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
		
		NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_server_sync_error));
	}
	
	@Override
	public void onSyncError(String message) {
		
		mProgress = 100;
		
		if (isActivityVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
		
		NotificationUtil.setAlertMessage(getFragmentManager(), message);
	}
}

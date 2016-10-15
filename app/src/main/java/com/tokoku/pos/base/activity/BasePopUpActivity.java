package com.tokoku.pos.base.activity;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.NotificationUtil;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BasePopUpActivity extends Activity {

	List<Object> mInputFields = new ArrayList<Object>();
	protected List<FormFieldBean> mMandatoryFields = new ArrayList<FormFieldBean>();
	
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
	
	protected void registerField(Object field) {
    	
    	mInputFields.add(field);
    }
	
	protected void registerMandatoryField(FormFieldBean formField) {
    	
    	mMandatoryFields.add(formField);
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
}

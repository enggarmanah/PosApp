package com.tokoku.pos.base.fragment;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class BaseEditFragment<T> extends BaseFragment {
    
	ArrayAdapter<CharSequence> statusArrayAdapter;
    
    protected T mItem;
    
    BaseItemListener<T> mItemListener;
    
    List<Object> mInputFields = new ArrayList<Object>();
    List<Object> mRootInputFields = new ArrayList<Object>();
    
    protected boolean isEnableInputFields = false;
    
    protected List<FormFieldBean> mandatoryFields = new ArrayList<FormFieldBean>();
    protected List<FormFieldBean> floatFields = new ArrayList<FormFieldBean>();
	
	protected boolean isValidated() {
    	
    	for (FormFieldBean field : mandatoryFields) {
    		
    		View input = field.getField();
    		String value = null;
    		
    		if (input instanceof EditText) {
    			
    			EditText inputText = (EditText) input;
    			value = inputText.getText().toString();
    		}
    		
    		if (CommonUtil.isEmpty(value)) {
    			
    			String fieldLabel = getString(field.getLabel());
    			
    			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_empty_mandatory_field, fieldLabel));
    			
    			input.requestFocus();
    			
    			return false;
    		}
    	}
    	
    	for (FormFieldBean field : floatFields) {
    		
    		View input = field.getField();
    		String value = null;
    		
    		if (input instanceof EditText) {
    			
    			EditText inputText = (EditText) input;
    			value = inputText.getText().toString();
    		}
    		
    		if (!CommonUtil.isEmpty(value)) {
    			
    			Float floatValue = CommonUtil.parseFloatNumber(value);
    			String fieldLabel = getString(field.getLabel());
    			
    			if (floatValue == null) {
    				
    				NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_invalid_format_field, fieldLabel, CommonUtil.formatNumber(2.5f)));
        			input.requestFocus();
        			
        			return false;
    			}
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
		
    	for (FormFieldBean field : mandatoryFields) {
    		
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
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text_required));
    			} else {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			}
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<T>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<T>");
        }
    }
    
    @Override
    public void onStart() {
        
    	super.onStart();
        
        updateView(mItem);
        
        // new item case
        if (mItem != null && getItemId(mItem) == null) {
        	enableInputFields(true);
        }
    }
    
    protected void initViewReference(View view) {
    	
    	mInputFields.clear();
    	mandatoryFields.clear();
    }
    
    @Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		saveItem();
	}
    
    protected void registerField(Object field) {
    	
    	mInputFields.add(field);
    }
    
    protected void registerRootField(Object field) {
    	
    	mRootInputFields.add(field);
    }
    
    public void enableInputFields(boolean isEnabled) {
    	
    	isEnableInputFields = isEnabled;
    	
    	for (Object field : mInputFields) {
    		
    		if (field instanceof EditText) {
    			
    			EditText editText = (EditText) field;
    			
    			if (mRootInputFields.contains(field)) {
    				
    				if (UserUtil.isRoot()) {
    					editText.setEnabled(isEnabled);
    				} else {
    					editText.setEnabled(false);
    				}
    		
    			} else {
    				editText.setEnabled(isEnabled);
    			}
    			
    		} else if (field instanceof Spinner) {
    			
    			Spinner spinner = (Spinner) field;
    			
    			if (mRootInputFields.contains(field)) {
    				
    				if (UserUtil.isRoot()) {
    					spinner.setEnabled(isEnabled);
    				} else {
    					spinner.setEnabled(false);
    				}
    		
    			} else {
    				spinner.setEnabled(isEnabled);
    			}
    		}
    	}
    }
    
    protected void linkDatePickerWithInputField(String fragmentTag, TextView inputField) {
    	
    	DatePickerFragment dp = (DatePickerFragment) getFragmentManager().findFragmentByTag(fragmentTag);
    	if (dp != null) {
    		dp.setInputField(inputField);
    	}
    }
    
    protected View.OnClickListener getDateFieldOnClickListener(final String fragmentTag) {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText dateInput = (EditText) v;
				
				if (getActivity().getCurrentFocus() != null) {
					getActivity().getCurrentFocus().clearFocus();
				}
				
				DatePickerFragment fragment = null;
				
				if (getFragmentManager().findFragmentByTag(fragmentTag) != null) {
					fragment = (DatePickerFragment) getFragmentManager().findFragmentByTag(fragmentTag);
				} else {
					fragment = new DatePickerFragment(dateInput);
				}
				
				if (fragment.isAdded()) {
					return;
				}
				
				fragment.show(getFragmentManager(), fragmentTag);
			}
		};
    }
    
    protected View.OnFocusChangeListener getCurrencyFieldOnFocusChangeListener() {
    	
    	return new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				EditText numberInput = (EditText) v;
				String digits = numberInput.getText().toString();
				
				String value = Constant.EMPTY_STRING;
				
				if (hasFocus) {
                	value = CommonUtil.formatPlainNumber(CommonUtil.parseFloatCurrency(digits));
                	numberInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                	numberInput.setText(value);
				} else {
					value = CommonUtil.formatCurrency(digits);
					numberInput.setInputType(InputType.TYPE_CLASS_TEXT);
					numberInput.setText(value);
				}
			}
		};
    }
    
    protected View.OnFocusChangeListener getNumberFieldOnFocusChangeListener() {
    	
    	return new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				EditText numberInput = (EditText) v;
				String digits = numberInput.getText().toString();
				
				String value = Constant.EMPTY_STRING;
				
				if (hasFocus) {
                	value = CommonUtil.formatPlainNumber(CommonUtil.parseFloatNumber(digits));
                	numberInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                	numberInput.setText(value);
				} else {
					value = CommonUtil.formatNumber(digits);
					numberInput.setInputType(InputType.TYPE_CLASS_TEXT);
					numberInput.setText(value);
				}
			}
		};
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
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text_required));
    			} else {
    				editText.setBackground(getResources().getDrawable(R.drawable.selector_input_text));
    			}
			}
		};
    }
    
    protected abstract void updateView(T product);
    
    protected void showView() {
    	
    	highlightMandatoryFields();
    	
    	if (getView().getVisibility() == View.INVISIBLE) {
    		
    		getView().setVisibility(View.VISIBLE);
    	}
    	
    	if (getFirstField() != null) {
    		getFirstField().requestFocus();
    	}
    }
    
    protected void hideView() {
    	
    	if (getView().getVisibility() == View.VISIBLE) {
    		
    		getView().setVisibility(View.INVISIBLE);
    	}
    }
    
    protected abstract void saveItem();
    
    protected abstract Long getItemId(T item);
    
    public void saveEditItem() {
    	
    	saveItem();
    	
    	if (!isValidated()) {
			return;
		}
		
    	if (getFirstField() != null) {
    		getFirstField().requestFocus();
    	}
    	
		saveItem();
    	
    	if (getItemId(mItem) == null) {
    		
    		if (addItem()) {
    			mItemListener.onAddCompleted();
    			showMessage(R.string.msg_data_save_success);
    		}
    	} else {
    		
    		if (updateItem()) {
    			updateView(mItem);
    			mItemListener.onUpdateCompleted();
    			showMessage(R.string.msg_data_save_success);
    		}
    	}
   
    	hideKeyboard();
    }
    
    public void discardEditItem() {
    	
    	mItem = null;
		
		mItemListener.onItemUnselected();
		mItemListener.displaySearch();
		
		hideKeyboard();
    }
    
    protected abstract boolean addItem();
    
    protected abstract boolean updateItem();
    
    protected abstract TextView getFirstField();
    
    protected void hideKeyboard() {
    	
    	if (getFirstField() != null) {
        	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        	imm.hideSoftInputFromWindow(getFirstField().getWindowToken(), 0);
    	}
    }
    
    public void setItem(T item) {
    	
    	this.mItem = item;
    }
    
    public abstract T updateItem(T item);
    
    public void refreshView() {
    	
    	// stop if the view is not yet initialized 
    	
    	if (getView() != null) {
    		updateView(mItem);
    	}
    }
    
    public void addItem(T item) {
    	
    	this.mItem = item;
    }
}
package com.tokoku.pos.popup.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class LocaleDlgFragment extends BaseSearchDlgFragment<Locale> implements LocaleArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mLocaleSearchText;
	ListView mLocaleListView;
	TextView mNoLocaleText;
	
	LocaleSelectionListener mActionListener;
	
	LocaleArrayAdapter localeArrayAdapter;
	
	boolean mIsMandatory = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<Locale>();
        
        localeArrayAdapter = new LocaleArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_locale_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mLocaleSearchText = (EditText) getView().findViewById(R.id.localeSearchText);
		
		mLocaleSearchText.setText(CommonUtil.getNvlString(mQuery));
		mLocaleSearchText.setSelection(mLocaleSearchText.getText().length());
		//mLocaleSearchText.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
		
		mLocaleSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(getLocales(mQuery, 0));
				localeArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mLocaleListView = (ListView) getView().findViewById(R.id.localeListView);
		mLocaleListView.setAdapter(localeArrayAdapter);
		
		mNoLocaleText = (TextView) getView().findViewById(R.id.noLocaleText);
		mNoLocaleText.setOnClickListener(getNoLocaleTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoLocaleText.setVisibility(View.GONE);
		} else {
			mNoLocaleText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		mItems.addAll(getLocales(mQuery, 0));
		localeArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (LocaleSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onLocaleSelected(Locale locale) {
		
		dismiss();
		mActionListener.onLocaleSelected(locale);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
	
	private View.OnClickListener getNoLocaleTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onLocaleSelected(null);
				dismiss();
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
	
	@Override
	protected List<Locale> getItems(String query) {
		
		return getLocales(mQuery, 0);
	}
	
	@Override
	protected List<Locale> getNextItems(String query, int lastIndex) {
		
		return getLocales(mQuery, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		localeArrayAdapter.notifyDataSetChanged();
	}
	
	private List<Locale> getLocales(String query, int lastIndex) {
		
		List<Locale> locales = new ArrayList<Locale>();
		
		if (!CommonUtil.isEmpty(mQuery) && mQuery.length() < 2) {
			
			locales.addAll(Arrays.asList(Locale.getAvailableLocales()));
			return locales;
		}
		
		for (Locale locale : Locale.getAvailableLocales()) {

			String language = Constant.EMPTY_STRING;
			String country = Constant.EMPTY_STRING;
			
			try {
				language = locale.getLanguage();
				country = locale.getCountry();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!CommonUtil.isEmpty(language) && !CommonUtil.isEmpty(country)) {
				
				String displayName = locale.getDisplayName().toLowerCase(Locale.getDefault());
				String searchName = query.toLowerCase(Locale.getDefault());
				
				if (CommonUtil.isEmpty(query)) {
					locales.add(locale);
					
				} else if (displayName.contains(searchName)) {

					locales.add(locale);
				}
			}
		}
		
		return locales;
	}
}

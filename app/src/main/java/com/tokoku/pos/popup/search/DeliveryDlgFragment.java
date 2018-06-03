package com.tokoku.pos.popup.search;

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

import com.tokoku.pos.R;
import com.tokoku.pos.dao.DeliveryDaoService;
import com.tokoku.pos.model.Delivery;
import com.tokoku.pos.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDlgFragment extends BaseSearchDlgFragment<Delivery> implements DeliveryArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mDeliverySearchText;
	ListView mDeliveryListView;
	TextView mNoDeliveryText;
	
	DeliverySelectionListener mActionListener;
	
	DeliveryArrayAdapter deliveryArrayAdapter;
	
	boolean mIsMandatory = false;
	
	private DeliveryDaoService mDeliveryDaoService = new DeliveryDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<Delivery>();
        
        deliveryArrayAdapter = new DeliveryArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_delivery_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mDeliverySearchText = (EditText) getView().findViewById(R.id.deliverySearchText);
		
		mDeliverySearchText.setText(CommonUtil.getNvlString(mQuery));
		mDeliverySearchText.setSelection(mDeliverySearchText.getText().length());
		//mDeliverySearchText.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
		
		mDeliverySearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(mDeliveryDaoService.getDeliveries(mQuery, 0));
				deliveryArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mDeliveryListView = (ListView) getView().findViewById(R.id.deliveryListView);
		mDeliveryListView.setAdapter(deliveryArrayAdapter);
		
		mNoDeliveryText = (TextView) getView().findViewById(R.id.noDeliveryText);
		mNoDeliveryText.setOnClickListener(getNoDeliveryTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoDeliveryText.setVisibility(View.GONE);
		} else {
			mNoDeliveryText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		mItems.addAll(mDeliveryDaoService.getDeliveries(mQuery, 0));
		deliveryArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (DeliverySelectionListener) activity;
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
	public void onDeliverySelected(Delivery delivery) {
		
		dismiss();
		mActionListener.onDeliverySelected(delivery);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
	
	private View.OnClickListener getNoDeliveryTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onDeliverySelected(null);
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
	protected List<Delivery> getItems(String query) {
		
		return mDeliveryDaoService.getDeliveries(mQuery, 0);
	}
	
	@Override
	protected List<Delivery> getNextItems(String query, int lastIndex) {
		
		return mDeliveryDaoService.getDeliveries(mQuery, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		deliveryArrayAdapter.notifyDataSetChanged();
	}
}

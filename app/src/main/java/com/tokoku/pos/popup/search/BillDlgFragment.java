package com.tokoku.pos.popup.search;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BillDlgFragment extends BaseSearchDlgFragment<Bills> implements BillArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mBillsSearchText;
	ListView mBillsListView;
	TextView mNoBillsText;
	
	BillSelectionListener mActionListener;
	
	BillArrayAdapter billArrayAdapter;
	
	boolean mIsMandatory = false;
	boolean mIsProductPurchase = false;
	
	private BillsDaoService mBillsDaoService = new BillsDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<Bills>();
        
        billArrayAdapter = new BillArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_bill_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mBillsSearchText = (EditText) getView().findViewById(R.id.billSearchText);
		
		mBillsSearchText.setText(CommonUtil.getNvlString(mQuery));
		mBillsSearchText.setSelection(mBillsSearchText.getText().length());
		//mBillsSearchText.requestFocus();
		
		mBillsSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				
				if (mIsProductPurchase) {
					mItems.addAll(mBillsDaoService.getBills(mQuery, Constant.BILL_TYPE_PRODUCT_PURCHASE, 0));
				} else {
					mItems.addAll(mBillsDaoService.getBills(mQuery, 0));
				}
				
				billArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mBillsListView = (ListView) getView().findViewById(R.id.billListView);
		mBillsListView.setAdapter(billArrayAdapter);
		
		mNoBillsText = (TextView) getView().findViewById(R.id.noBillText);
		mNoBillsText.setOnClickListener(getNoBillsTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoBillsText.setVisibility(View.GONE);
		} else {
			mNoBillsText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		
		if (mIsProductPurchase) {
			mItems.addAll(mBillsDaoService.getBills(mQuery, Constant.BILL_TYPE_PRODUCT_PURCHASE, 0));
		} else {
			mItems.addAll(mBillsDaoService.getBills(mQuery, 0));
		}
		
		billArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (BillSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BillSelectionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
		
	public void setProductPurchase(boolean isProductPurchase) {
		
		mIsProductPurchase = isProductPurchase;
	}
	
	@Override
	public void onBillsSelected(Bills bill) {
		
		dismiss();
		mActionListener.onBillSelected(bill);
	}
	
	private View.OnClickListener getNoBillsTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBillSelected(null);
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
	protected List<Bills> getItems(String query) {
		
		if (mIsProductPurchase) {
			return mBillsDaoService.getBills(mQuery, Constant.BILL_TYPE_PRODUCT_PURCHASE, 0);
		} else {
			return mBillsDaoService.getBills(mQuery, 0);
		}
	}
	
	@Override
	protected List<Bills> getNextItems(String query, int lastIndex) {
		
		if (mIsProductPurchase) {
			return mBillsDaoService.getBills(mQuery, Constant.BILL_TYPE_PRODUCT_PURCHASE, lastIndex);
		} else {
			return mBillsDaoService.getBills(mQuery, lastIndex);
		}
	}
	
	@Override
	protected void refreshList() {
		
		billArrayAdapter.notifyDataSetChanged();
	}
}

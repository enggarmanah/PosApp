package com.tokoku.pos.popup.search;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.TransactionsBean;
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

public class CreditDlgFragment extends BaseSearchDlgFragment<TransactionsBean> implements CreditArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mCreditSearchText;
	ListView mCreditListView;
	TextView mNoCreditText;
	
	CreditSelectionListener mActionListener;
	
	CreditArrayAdapter creditArrayAdapter;
	
	boolean mIsMandatory = false;
	
	private TransactionsDaoService mTransactionsDaoService = new TransactionsDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<TransactionsBean>();
        
        creditArrayAdapter = new CreditArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_credit_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mCreditSearchText = (EditText) getView().findViewById(R.id.creditSearchText);
		
		mCreditSearchText.setText(CommonUtil.getNvlString(mQuery));
		mCreditSearchText.setSelection(mCreditSearchText.getText().length());
		//mCreditSearchText.requestFocus();
		
		mCreditSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(mTransactionsDaoService.getUnpaidTransactions(mQuery, 0));
				creditArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mCreditListView = (ListView) getView().findViewById(R.id.transactionListView);
		mCreditListView.setAdapter(creditArrayAdapter);
		
		mNoCreditText = (TextView) getView().findViewById(R.id.noCreditText);
		mNoCreditText.setOnClickListener(getNoTransactionsTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoCreditText.setVisibility(View.GONE);
		} else {
			mNoCreditText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		mItems.addAll(mTransactionsDaoService.getUnpaidTransactions(mQuery, 0));
		creditArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CreditSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionSelectionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
	
	@Override
	public void onTransactionsSelected(TransactionsBean transaction) {
		
		dismiss();
		mActionListener.onTransactionSelected(transaction);
	}
	
	private View.OnClickListener getNoTransactionsTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onTransactionSelected(null);
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
	protected List<TransactionsBean> getItems(String query) {
		
		return mTransactionsDaoService.getUnpaidTransactions(mQuery, 0);
	}
	
	@Override
	protected List<TransactionsBean> getNextItems(String query, int lastIndex) {
		
		return mTransactionsDaoService.getUnpaidTransactions(mQuery, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		creditArrayAdapter.notifyDataSetChanged();
	}
}

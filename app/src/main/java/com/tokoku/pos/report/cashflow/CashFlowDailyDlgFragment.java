package com.tokoku.pos.report.cashflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.popup.search.BaseSearchDlgFragment;
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

public class CashFlowDailyDlgFragment extends BaseSearchDlgFragment<Transactions> implements CashFlowDailyArrayAdapter.ItemActionListener {
	
	TextView mHeaderText;
	TextView mCancelBtn;
	EditText mTransactionsSearchText;
	ListView mTransactionsListView;
	
	CashFlowActionListener mActionListener;
	
	CashFlowDailyArrayAdapter transactionArrayAdapter;
	
	Date mTransactionDate;
	
	private TransactionsDaoService mTransactionsDaoService = new TransactionsDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<Transactions>();
        
        transactionArrayAdapter = new CashFlowDailyArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_cashflow_transaction_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mHeaderText = (TextView) getView().findViewById(R.id.headerText);
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		
		mHeaderText.setText(CommonUtil.formatDate(mTransactionDate));
		
		mTransactionsSearchText = (EditText) getView().findViewById(R.id.transactionSearchText);
		mTransactionsSearchText.setText(Constant.EMPTY_STRING);
		mTransactionsSearchText.requestFocus();
		
		mTransactionsSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(mTransactionsDaoService.getTransactions(mQuery, mTransactionDate, 0));
				transactionArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mTransactionsListView = (ListView) getView().findViewById(R.id.transactionListView);
		mTransactionsListView.setAdapter(transactionArrayAdapter);
		
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		mItems.clear();
		mItems.addAll(mTransactionsDaoService.getTransactions(mQuery, mTransactionDate, 0));
		transactionArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CashFlowActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionSelectionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onTransactionsSelected(Transactions transaction) {
		
		//dismiss();
	}
	
	public void setTransactionDate(Date transactionDate) {
		
		mTransactionDate = transactionDate;
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
	protected List<Transactions> getItems(String query) {
		
		return mTransactionsDaoService.getTransactions(mQuery, mTransactionDate, 0);
	}
	
	@Override
	protected List<Transactions> getNextItems(String query, int lastIndex) {
		
		return mTransactionsDaoService.getTransactions(mQuery, mTransactionDate, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		transactionArrayAdapter.notifyDataSetChanged();
	}
}

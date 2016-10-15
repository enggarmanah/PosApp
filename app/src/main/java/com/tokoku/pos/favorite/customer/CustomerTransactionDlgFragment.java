package com.tokoku.pos.favorite.customer;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.TransactionItem;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.TransactionItemDaoService;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerTransactionDlgFragment extends DialogFragment {
	
	TextView mCancelBtn;
	EditText mTransactionItemSearchText;
	ListView mTransactionItemListView;
	
	CustomerTransactionArrayAdapter customerTransactionsArrayAdapter;
	
	Customer mCustomer;
	List<TransactionItem> mTransactionItems = new ArrayList<TransactionItem>();
	
	private TransactionItemDaoService mTransactionItemDaoService = new TransactionItemDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        customerTransactionsArrayAdapter = new CustomerTransactionArrayAdapter(getActivity(), mTransactionItems);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.favorite_customer_transaction_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mTransactionItemSearchText = (EditText) getView().findViewById(R.id.billSearchText);
		mTransactionItemSearchText.setText(Constant.EMPTY_STRING);
		
		mTransactionItemSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mTransactionItems.clear();
				mTransactionItems.addAll(mTransactionItemDaoService.getCustomerTransactionItems(mCustomer, s.toString(), 0));
				customerTransactionsArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mTransactionItemListView = (ListView) getView().findViewById(R.id.inventoryListView);
		mTransactionItemListView.setAdapter(customerTransactionsArrayAdapter);
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		customerTransactionsArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	public void setCustomer(Customer customer) {
		
		mCustomer = customer;
		
		mTransactionItems.clear();
		mTransactionItems.addAll(mTransactionItemDaoService.getCustomerTransactionItems(mCustomer, Constant.EMPTY_STRING, 0));
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
}

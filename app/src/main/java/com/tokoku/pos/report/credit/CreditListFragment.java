package com.tokoku.pos.report.credit;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.TransactionsBean;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

public class CreditListFragment extends BaseFragment 
	implements CreditArrayAdapter.ItemActionListener {
	
	private ListView mProductList;
	
	private List<TransactionsBean> mTransactions;
	
	private TransactionsBean mSelectedTransaction;
	
	private CreditArrayAdapter mAdapter;
	
	private CreditActionListener mActionListener;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private String mQuery = Constant.EMPTY_STRING;
	
	private TransactionsDaoService mTransactionsDaoService = new TransactionsDaoService();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_credit_list_fragment, container, false);
		
		if (mTransactions == null) {
			mTransactions = new ArrayList<TransactionsBean>();
		}
		
		mAdapter = new CreditArrayAdapter(getActivity(), mTransactions, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mProductList = (ListView) getView().findViewById(R.id.billInfoList);
		
		mProductList.setItemsCanFocus(true);
		mProductList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mProductList.setOnScrollListener(getListOnScrollListener());
		
		mProductList.setAdapter(mAdapter);
			
		if (mSelectedTransaction != null) {
			onTransactionSelected(mSelectedTransaction);
		}
		
		searchTransaction(mQuery);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CreditActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CreditActionListener");
        }
    }
	
	public void refreshContent() {
		
		searchTransaction(mQuery);
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	public void setSelectedTransaction(TransactionsBean transaction) {
		
		mSelectedTransaction = transaction;
		
		if (transaction == null) {
			return;
		}
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void searchTransaction(String query) {
		
		mQuery = query;
		mIsEndOfList = false;
		
		if (!isViewInitialized()) {
			
			mSelectedTransaction = null;
			return;
		}
		
		mActionListener.onTransactionUnselected();
		
		mTransactions.clear();
		mTransactions.addAll(mTransactionsDaoService.getCreditTransactions(mQuery, 0));
		
		updateContent();
	}
	
	@Override
	public void onTransactionSelected(TransactionsBean transaction) {
		
		mActionListener.onTransactionSelected(transaction);
	}
	
	@Override
	public TransactionsBean getSelectedTransaction() {
		
		return mSelectedTransaction;
	}
	
	private AbsListView.OnScrollListener getListOnScrollListener() {
		
		return new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
				if (firstVisibleItem == 0) {
					return;
				}
				
				int lastInScreen = firstVisibleItem + visibleItemCount;
				
				if((lastInScreen == totalItemCount) && !mIsLoadData && !mIsEndOfList) {
					
					mIsLoadData = true;
					
					List<TransactionsBean> list = null;
					
					list = mTransactionsDaoService.getCreditTransactions(Constant.EMPTY_STRING, mTransactions.size());
						
					if (list.size() == 0) {
						mIsEndOfList = true;
					}
					
					String message = Constant.EMPTY_STRING;
					
					if (mIsEndOfList) {
						message = getString(R.string.alert_data_no_more);
					} else {
						message = getString(R.string.alert_data_show_next);
					}
					
					Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
					
					mTransactions.addAll(list);
					mAdapter.notifyDataSetChanged();
						
					mIsLoadData = false;
				}
			}
		};
	}
}
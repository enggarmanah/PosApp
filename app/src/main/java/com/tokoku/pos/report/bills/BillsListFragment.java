package com.tokoku.pos.report.bills;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.BillsDaoService;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BillsListFragment extends BaseFragment 
	implements BillsArrayAdapter.ItemActionListener {
	
	private TextView mTitleText;
	
	private ListView mBillsList;
	
	private List<Bills> mBills;
	private List<Bills> mPastDueBills;
	
	private Bills mSelectedBill;
	
	private BillsArrayAdapter mAdapter;
	private BillsArrayAdapter mStockAlertAdapter;
	
	private BillsActionListener mActionListener;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private boolean mIsShowAllBills = false;
	private boolean mIsShowPastDueBills = false;
	
	private String mQuery = Constant.EMPTY_STRING;
	
	private BillsDaoService mBillsDaoService = new BillsDaoService();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if (!mIsShowPastDueBills) {
			mIsShowAllBills = true;
		}
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_bills_list_fragment, container, false);
		
		if (mBills == null) {
			mBills = mBillsDaoService.getBillsReport(Constant.EMPTY_STRING, 0);
		}
		
		mAdapter = new BillsArrayAdapter(getActivity(), mBills, this);
		
		if (mPastDueBills == null) {
			mPastDueBills = mBillsDaoService.getPastDueBills(Constant.EMPTY_STRING, 0);
		}
		
		mStockAlertAdapter = new BillsArrayAdapter(getActivity(), mPastDueBills, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mTitleText = (TextView) getView().findViewById(R.id.titleText);
		
		mBillsList = (ListView) getView().findViewById(R.id.billInfoList);
		
		mBillsList.setItemsCanFocus(true);
		mBillsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mBillsList.setOnScrollListener(getListOnScrollListener());
		
		if (mIsShowAllBills) {
			
			mTitleText.setText(getString(R.string.report_bill));
			mBillsList.setAdapter(mAdapter);
			
		} else if (mIsShowPastDueBills) {
			
			mTitleText.setText(getString(R.string.report_bill_pastdue));
			mBillsList.setAdapter(mStockAlertAdapter);
		}
		
		if (mSelectedBill != null) {
			onBillSelected(mSelectedBill);	
		}
		
		searchBills(mQuery);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (BillsActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InventoryReportActionListener");
        }
    }
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		mStockAlertAdapter.notifyDataSetChanged();
		mAdapter.notifyDataSetChanged();
	}
	
	public void setSelectedBill(Bills bill) {
		
		mSelectedBill = bill;
		
		if (bill == null) {
			return;
		}
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void showAllBills() {
		
		mIsShowAllBills = true;
		mIsShowPastDueBills =  false;
		
		if (!isViewInitialized()) {
			return;
		}
		
		mTitleText.setText(getString(R.string.report_bill));
		mBillsList.setAdapter(mAdapter);
	}
	
	public void showPastDueBills() {
		
		mIsShowAllBills = false;
		mIsShowPastDueBills =  true;
		
		if (!isViewInitialized()) {
			return;
		}
		
		mTitleText.setText(getString(R.string.report_bill_pastdue));
		mBillsList.setAdapter(mStockAlertAdapter);
	}
	
	public void searchBills(String query) {
		
		mQuery = query;
		mIsEndOfList = false;
		
		if (!isViewInitialized()) {
			
			mSelectedBill = null;
			return;
		}
		
		mActionListener.onBillUnselected();
		
		if (mIsShowAllBills) { 
		
			mBills.clear();
			mBills.addAll(mBillsDaoService.getBillsReport(mQuery, 0));
		
		} else if (mIsShowPastDueBills) {
			
			mPastDueBills.clear();
			mPastDueBills.addAll(mBillsDaoService.getPastDueBills(mQuery, 0));
		}
			
		updateContent();
	}
	
	@Override
	public void onBillSelected(Bills bill) {
		
		mActionListener.onBillSelected(bill);
	}
	
	@Override
	public Bills getSelectedBill() {
		
		return mSelectedBill;
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
					
					List<Bills> list = null;
					
					if (mIsShowAllBills) {
						list = mBillsDaoService.getBills(Constant.EMPTY_STRING, mBills.size());
						
					} else if (mIsShowPastDueBills) {
						list = mBillsDaoService.getPastDueBills(Constant.EMPTY_STRING, mPastDueBills.size());
					}
					
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
					
					if (mIsShowAllBills) {
						
						mBills.addAll(list);
						mAdapter.notifyDataSetChanged();
						
					} else if (mIsShowPastDueBills) {
						
						mPastDueBills.addAll(list);
						mStockAlertAdapter.notifyDataSetChanged();
					}
					
					mIsLoadData = false;
				}
			}
		};
	}
}
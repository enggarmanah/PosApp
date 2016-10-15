package com.tokoku.pos.favorite.customer;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.CustomerDaoService;
import com.tokoku.pos.model.CustomerStatisticBean;
import com.tokoku.pos.util.MerchantUtil;

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

public class CustomerListFragment extends BaseFragment 
	implements CustomerArrayAdapter.ItemActionListener {
	
	private TextView mTitleText;
	
	private ListView mCustomerStatisticBeanList;
	
	private List<CustomerStatisticBean> mCustomerStatisticBeans;
	
	private CustomerStatisticBean mSelectedCustomerStatisticBean;
	
	private CustomerArrayAdapter mAdapter;
	
	private CustomerActionListener mActionListener;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private String mQuery = Constant.EMPTY_STRING;
	
	private CustomerDaoService mCustomerDaoService = new CustomerDaoService();
	
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
		
		View view = inflater.inflate(R.layout.favorite_customer_list_fragment, container, false);
		
		if (mCustomerStatisticBeans == null) {
			mCustomerStatisticBeans = mCustomerDaoService.getCustomerStatistics(Constant.EMPTY_STRING, 0);
		}
		
		mAdapter = new CustomerArrayAdapter(getActivity(), mCustomerStatisticBeans, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mTitleText = (TextView) getView().findViewById(R.id.titleText);
		
		if (Constant.MERCHANT_TYPE_MEDICAL_SERVICES.equals(MerchantUtil.getMerchantType())) {
			
			mTitleText.setText(getString(R.string.menu_favorite_patient));
			
		} else {
			mTitleText.setText(getString(R.string.menu_favorite_customer));
		}
		
		mCustomerStatisticBeanList = (ListView) getView().findViewById(R.id.transactionList);
		
		mCustomerStatisticBeanList.setItemsCanFocus(true);
		mCustomerStatisticBeanList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mCustomerStatisticBeanList.setOnScrollListener(getListOnScrollListener());
		
		mCustomerStatisticBeanList.setAdapter(mAdapter);
			
		if (mSelectedCustomerStatisticBean != null) {
			onCustomerStatisticSelected(mSelectedCustomerStatisticBean);	
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CustomerActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InventoryReportActionListener");
        }
    }
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	public void setSelectedCustomerStatisticBean(CustomerStatisticBean customerStatistic) {
		
		mSelectedCustomerStatisticBean = customerStatistic;
		
		if (customerStatistic == null) {
			return;
		}
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void searchCustomerStatisticBean(String query) {
		
		mQuery = query;
		mIsEndOfList = false;
		
		if (!isViewInitialized()) {
			
			mSelectedCustomerStatisticBean = null;
			return;
		}
		
		mActionListener.onCustomerStatisticUnselected();
		
		mCustomerStatisticBeans.clear();
		mCustomerStatisticBeans.addAll(mCustomerDaoService.getCustomerStatistics(mQuery, 0));
		
		updateContent();
	}
	
	@Override
	public void onCustomerStatisticSelected(CustomerStatisticBean customerStatistic) {
		
		mActionListener.onCustomerStatisticSelected(customerStatistic);
	}
	
	@Override
	public CustomerStatisticBean getSelectedCustomerStatistic() {
		
		return mSelectedCustomerStatisticBean;
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
					
					List<CustomerStatisticBean> list = null;
					
					list = mCustomerDaoService.getCustomerStatistics(Constant.EMPTY_STRING, mCustomerStatisticBeans.size());
						
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
					
					mCustomerStatisticBeans.addAll(list);
					mAdapter.notifyDataSetChanged();
					
					mIsLoadData = false;
				}
			}
		};
	}
}
package com.tokoku.pos.favorite.supplier;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.model.SupplierStatisticBean;

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

public class SupplierListFragment extends BaseFragment 
	implements SupplierArrayAdapter.ItemActionListener {
	
	private TextView mTitleText;
	
	private ListView mSupplierStatisticBeanList;
	
	private List<SupplierStatisticBean> mSupplierStatisticBeans;
	
	private SupplierStatisticBean mSelectedSupplierStatisticBean;
	
	private SupplierArrayAdapter mAdapter;
	
	private SupplierActionListener mActionListener;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private String mQuery = Constant.EMPTY_STRING;
	
	private SupplierDaoService mSupplierDaoService = new SupplierDaoService();
	
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
		
		View view = inflater.inflate(R.layout.favorite_supplier_list_fragment, container, false);
		
		if (mSupplierStatisticBeans == null) {
			mSupplierStatisticBeans = mSupplierDaoService.getSupplierStatistics(Constant.EMPTY_STRING, 0);
		}
		
		mAdapter = new SupplierArrayAdapter(getActivity(), mSupplierStatisticBeans, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mTitleText = (TextView) getView().findViewById(R.id.titleText);
		
		mSupplierStatisticBeanList = (ListView) getView().findViewById(R.id.transactionList);
		
		mSupplierStatisticBeanList.setItemsCanFocus(true);
		mSupplierStatisticBeanList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mSupplierStatisticBeanList.setOnScrollListener(getListOnScrollListener());
		
		mSupplierStatisticBeanList.setAdapter(mAdapter);
			
		if (mSelectedSupplierStatisticBean != null) {
			onSupplierStatisticSelected(mSelectedSupplierStatisticBean);	
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (SupplierActionListener) activity;
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
	
	public void setSelectedSupplierStatisticBean(SupplierStatisticBean supplierStatistic) {
		
		mSelectedSupplierStatisticBean = supplierStatistic;
		
		if (supplierStatistic == null) {
			return;
		}
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void showAllSupplierStatisticBeans() {
		
		mTitleText.setText(getString(R.string.product));
		mSupplierStatisticBeanList.setAdapter(mAdapter);
	}
	
	public void searchSupplierStatisticBean(String query) {
		
		mQuery = query;
		mIsEndOfList = false;
		
		if (!isViewInitialized()) {
			
			mSelectedSupplierStatisticBean = null;
			return;
		}
		
		mActionListener.onSupplierStatisticUnselected();
		
		mSupplierStatisticBeans.clear();
		mSupplierStatisticBeans.addAll(mSupplierDaoService.getSupplierStatistics(mQuery, 0));
		
		updateContent();
	}
	
	@Override
	public void onSupplierStatisticSelected(SupplierStatisticBean supplierStatistic) {
		
		mActionListener.onSupplierStatisticSelected(supplierStatistic);
	}
	
	@Override
	public SupplierStatisticBean getSelectedSupplierStatistic() {
		
		return mSelectedSupplierStatisticBean;
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
					
					List<SupplierStatisticBean> list = null;
					
					list = mSupplierDaoService.getSupplierStatistics(Constant.EMPTY_STRING, mSupplierStatisticBeans.size());
						
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
					
					mSupplierStatisticBeans.addAll(list);
					mAdapter.notifyDataSetChanged();
					
					mIsLoadData = false;
				}
			}
		};
	}
}
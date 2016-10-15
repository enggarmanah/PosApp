package com.tokoku.pos.report.inventory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tokoku.pos.R;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Product;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.util.CommonUtil;

public class InventoryDetailFragment extends BaseFragment {
	
	private ImageButton mBackButton;
	
	private TextView mProductNameText;
	private TextView mProductStockText;
	
	protected ListView mInventoryList;

	protected Product mProduct;
	protected List<Inventory> mInventories;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private InventoryActionListener mActionListener;
	
	private InventoryDetailArrayAdapter mAdapter;
	
	private InventoryDaoService mInventoryDaoService = new InventoryDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_inventory_detail_fragment, container, false);
		
		if (mInventories == null) {
			mInventories = new ArrayList<Inventory>();
		}
		
		mProductNameText = (TextView) view.findViewById(R.id.productNameText);
		mProductStockText = (TextView) view.findViewById(R.id.productStockText);
		
		mAdapter = new InventoryDetailArrayAdapter(getActivity(), mInventories);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mInventoryList = (ListView) getView().findViewById(R.id.productStatisticList);

		mInventoryList.setAdapter(mAdapter);
		mInventoryList.setItemsCanFocus(true);
		mInventoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		mInventoryList.setOnScrollListener(getListOnScrollListener());
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void onStart() {
		super.onStart();
		
		initViewVariables();
		updateContent();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            mActionListener = (InventoryActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InventoryReportActionListener");
        }
    }
	
	public void setProduct(Product product) {
		
		mProduct = product;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		if (mProduct != null) {
			
			Float stock = CommonUtil.getNvlFloat(mProduct.getStock());
			
			mProductNameText.setText(mProduct.getName());
			mProductStockText.setText(getString(R.string.report_inventory_stock, CommonUtil.formatNumber(stock)));
			
			mInventories.clear();
			
			mInventories.addAll(mInventoryDaoService.getInventories(mProduct, 0));
			
			mAdapter.notifyDataSetChanged();
			
			getView().setVisibility(View.VISIBLE);
		
		} else {
		
			getView().setVisibility(View.INVISIBLE);
			return;
		}
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackPressed();
			}
		};
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
					
					List<Inventory> list = mInventoryDaoService.getInventories(mProduct, mInventories.size());
					
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
						
					mInventories.addAll(list);
					mAdapter.notifyDataSetChanged();
					
					mIsLoadData = false;
				}
			}
		};
	}
}
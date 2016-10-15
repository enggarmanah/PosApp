package com.tokoku.pos.favorite.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.model.ProductGroupStatisticBean;
import com.tokoku.pos.util.CommonUtil;

public class SupplierDetailFragment extends BaseFragment {
	
	private ImageButton mBackButton;
	private ImageButton mTransactionsButton;
	
	private TextView mSupplierNameText;
	
	private TextView mFirstTransactionText;
	private TextView mLastTransactionText;
	private TextView mAverageTransactionText;
	
	private TextView mTransactionCountText;
	private TextView mProductCountText;
	private TextView mTotalAmountText;
	
	private TextView mMinTransactionText;
	private TextView mMaxTransactionText;
	private TextView mAverageAmountText;
	
	protected ListView mProductGroupStatisticList;

	protected Supplier mSupplier;
	protected List<ProductGroupStatisticBean> mProductGroupStatistics;
	
	private SupplierActionListener mActionListener;
	
	private SupplierDetailArrayAdapter mAdapter;
	
	private ProductGroupDaoService mProductGroupDaoService = new ProductGroupDaoService();
	private SupplierDaoService mSupplierDaoService = new SupplierDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.favorite_supplier_detail_fragment, container, false);
		
		if (mProductGroupStatistics == null) {
			mProductGroupStatistics = new ArrayList<ProductGroupStatisticBean>();
		}
		
		mSupplierNameText = (TextView) view.findViewById(R.id.supplierNameText);
		
		mFirstTransactionText = (TextView) view.findViewById(R.id.firstTransactionText);
		mLastTransactionText = (TextView) view.findViewById(R.id.lastTransactionText);
		mAverageTransactionText = (TextView) view.findViewById(R.id.averageTransactionText);
		
		mTransactionCountText = (TextView) view.findViewById(R.id.transactionCountText);
		mProductCountText = (TextView) view.findViewById(R.id.productCountText);
		mTotalAmountText = (TextView) view.findViewById(R.id.totalAmountText);
		
		mMinTransactionText = (TextView) view.findViewById(R.id.minTransactionText);
		mMaxTransactionText = (TextView) view.findViewById(R.id.maxTransactionText);
		mAverageAmountText = (TextView) view.findViewById(R.id.averageAmountText);
		
		mAdapter = new SupplierDetailArrayAdapter(getActivity(), mProductGroupStatistics);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mProductGroupStatisticList = (ListView) getView().findViewById(R.id.productStatisticList);

		mProductGroupStatisticList.setAdapter(mAdapter);
		mProductGroupStatisticList.setItemsCanFocus(true);
		mProductGroupStatisticList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mTransactionsButton = (ImageButton) getView().findViewById(R.id.transactionsButton);
		mTransactionsButton.setOnClickListener(getTransactionsButtonOnClickListener());
		
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
            mActionListener = (SupplierActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SupplierActionListener");
        }
    }
	
	public void setSupplier(Supplier supplier) {
		
		mSupplier = supplier;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		if (mSupplier != null) {
			
			mSupplierNameText.setText(mSupplier.getName());
			
			Date firstTransaction = mSupplierDaoService.getFirstInventory(mSupplier);
			Date lastTransaction = mSupplierDaoService.getLastInventory(mSupplier);
			
			Long transactionsCount = CommonUtil.getNvlLong(mSupplierDaoService.getBillsCount(mSupplier));
			Long productsCount = CommonUtil.getNvlLong(mSupplierDaoService.getProductsCount(mSupplier));
			Long transactionAmount = CommonUtil.getNvlLong(mSupplierDaoService.getBillsAmount(mSupplier));
			
			Long minTransaction = CommonUtil.getNvlLong(mSupplierDaoService.getMinBill(mSupplier));
			Long maxTransaction = CommonUtil.getNvlLong(mSupplierDaoService.getMaxBill(mSupplier));
			Long averageAmount = transactionAmount / transactionsCount;
			
			long amonth = 60 * 60 * 24 * 30;
			
			Long transactionPeriod = (new Date().getTime() - firstTransaction.getTime()) / 1000;
			Long averageTransactionPeriod = transactionPeriod / transactionsCount;
			Long month = averageTransactionPeriod / amonth;
			Long day = averageTransactionPeriod % amonth / 30;
			
			String dayStr = String.valueOf(day).substring(0, 1);
			
			String averageTransaction = getString(R.string.favorite_average_transaction, month, dayStr);
			
			
			mFirstTransactionText.setText(CommonUtil.formatDate(firstTransaction));
			mLastTransactionText.setText(CommonUtil.formatDate(lastTransaction));
			mAverageTransactionText.setText(averageTransaction);
			
			mTransactionCountText.setText(CommonUtil.formatNumber(transactionsCount));
			mProductCountText.setText(CommonUtil.formatNumber(productsCount));
			mTotalAmountText.setText(CommonUtil.formatCurrency(transactionAmount));
			
			mMinTransactionText.setText(CommonUtil.formatCurrency(minTransaction));
			mMaxTransactionText.setText(CommonUtil.formatCurrency(maxTransaction));
			mAverageAmountText.setText(CommonUtil.formatCurrency(averageAmount));
			
			mProductGroupStatistics.clear();
			
			mProductGroupStatistics.addAll(mProductGroupDaoService.getProductGroupStatistics(mSupplier));
			
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
	
	private View.OnClickListener getTransactionsButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.showSupplierInventories(mSupplier);
			}
		};
	}
}
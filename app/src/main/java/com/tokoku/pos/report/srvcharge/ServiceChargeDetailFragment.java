package com.tokoku.pos.report.srvcharge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.TransactionItemDaoService;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;

public class ServiceChargeDetailFragment extends BaseFragment implements ServiceChargeDetailArrayAdapter.ItemActionListener {
	
	private LinearLayout mCustomerPanel;
	private LinearLayout mWaitressPanel;
	
	private ImageButton mBackButton;
	private TextView mTitleText;
	
	private TextView mTransactionNoText;
	private TextView mDateText;
	private TextView mWaitressText;	
	private TextView mCashierText;
	private TextView mCustomerText;
	
	private LinearLayout mTaxPanel;
	private LinearLayout mServiceChargePanel;
	
	private TextView mDiscountLabelText;
	private TextView mDiscountText;
	private TextView mTaxText;
	private TextView mTaxLabelText;
	private TextView mServiceChargeText;
	private TextView mServiceChargeLabelText;
	private TextView mTotalOrderText;
	private TextView mPaymentTypeText;
	
	protected ListView mTransactionItemList;

	protected Transactions mTransaction;
	protected List<TransactionItem> mTransactionItems;
	
	private ServiceChargeActionListener mActionListener;
	
	private ServiceChargeDetailArrayAdapter mAdapter;
	
	private TransactionItemDaoService mTransactionItemDaoService = new TransactionItemDaoService(); 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_transaction_detail_fragment, container, false);
		
		if (mTransactionItems == null) {
			mTransactionItems = new ArrayList<TransactionItem>();
		} 
		
		mAdapter = new ServiceChargeDetailArrayAdapter(getActivity(), mTransactionItems, this);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mTransactionItemList = (ListView) getView().findViewById(R.id.newDeviceList);

		mTransactionItemList.setAdapter(mAdapter);
		mTransactionItemList.setItemsCanFocus(true);
		mTransactionItemList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mCustomerPanel = (LinearLayout) getView().findViewById(R.id.customerPanel);
		mWaitressPanel = (LinearLayout) getView().findViewById(R.id.waitressPanel);
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
		
		mTitleText = (TextView) getView().findViewById(R.id.titleText);
		
		mTransactionNoText = (TextView) getView().findViewById(R.id.transactionNoText);
		mDateText = (TextView) getView().findViewById(R.id.dateText);
		mWaitressText = (TextView) getView().findViewById(R.id.waitressText);
		mCashierText = (TextView) getView().findViewById(R.id.cashierText);
		mCustomerText = (TextView) getView().findViewById(R.id.customerText);	
		
		mDiscountLabelText = (TextView) getView().findViewById(R.id.discountLabelText);
		mDiscountLabelText.setText(getString(R.string.no_discount));
		
		mDiscountText = (TextView) getView().findViewById(R.id.discountText);
		mDiscountText.setText(Constant.EMPTY_STRING);
		
		mTaxLabelText = (TextView) getView().findViewById(R.id.taxLabel);
		mTaxText = (TextView) getView().findViewById(R.id.taxText);
		mTaxText.setText(CommonUtil.formatNumber(0));
		
		mServiceChargeLabelText = (TextView) getView().findViewById(R.id.serviceChargeLabel);
		mServiceChargeText = (TextView) getView().findViewById(R.id.serviceChargeText);
		mServiceChargeText.setText(CommonUtil.formatNumber(0));
		
		mPaymentTypeText = (TextView) getView().findViewById(R.id.paymentTypeText);
		
		mTotalOrderText = (TextView) getView().findViewById(R.id.totalOrderText);
		mTotalOrderText.setText(CommonUtil.formatCurrency(0));
		
		mTaxPanel = (LinearLayout) getView().findViewById(R.id.taxPanel);
		mServiceChargePanel = (LinearLayout) getView().findViewById(R.id.serviceChargePanel);
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
            mActionListener = (ServiceChargeActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	@Override
	public void onItemSelected(TransactionItem transactionItem) {
	}
	
	public void setTransaction(Transactions transaction) {
		
		mTransaction = transaction;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		if (mTransaction != null) {
			
			mTransactionItems.clear();
			mTransactionItems.addAll(mTransactionItemDaoService.getTransactionItemsByTransactionId(mTransaction.getId()));
			mAdapter.notifyDataSetChanged();
			
			getView().setVisibility(View.VISIBLE);
		
		} else {
		
			getView().setVisibility(View.INVISIBLE);
			return;
		}
		
		mDateText.setText(CommonUtil.formatDayDateTime(mTransaction.getTransactionDate()));
		mTitleText.setText(CommonUtil.formatCurrency(mTransaction.getTaxAmount()));
		
		mTransactionNoText.setText(mTransaction.getTransactionNo());
		
		mCashierText.setText(mTransaction.getCashierName());
		
		if (!CommonUtil.isEmpty(mTransaction.getWaitressName())) {
			mWaitressText.setText(mTransaction.getWaitressName());
			mWaitressPanel.setVisibility(View.VISIBLE);
		} else {
			mWaitressPanel.setVisibility(View.GONE);
		} 
		
		if (!CommonUtil.isEmpty(mTransaction.getCustomerName())) {
			mCustomerText.setText(mTransaction.getCustomerName());
			mCustomerPanel.setVisibility(View.VISIBLE);
		} else {
			mCustomerPanel.setVisibility(View.GONE);
		}
		
		if (!CommonUtil.isEmpty(mTransaction.getDiscountName()) &&
			mTransaction.getDiscountAmount() != 0) {
			
			String label = mTransaction.getDiscountName();
			if (mTransaction.getDiscountPercentage() != 0) {
				label += " " + CommonUtil.formatPercentage(mTransaction.getDiscountPercentage());
			}
			mDiscountLabelText.setText(label);
			mDiscountText.setText(CommonUtil.formatCurrency(mTransaction.getDiscountAmount()));
			
		} else {
			mDiscountLabelText.setText(getString(R.string.no_discount));
			mDiscountText.setText(Constant.EMPTY_STRING);
		}
		
		float tax = mTransaction.getTaxAmount();
		float serviceCharge = mTransaction.getServiceChargeAmount();
		
		if (mTransaction.getTaxAmount() != 0) {
			
			String label = getString(R.string.field_tax_percentage) + " " + CommonUtil.formatPercentage(mTransaction.getTaxPercentage());
			
			mTaxLabelText.setText(label);
			mTaxText.setText(CommonUtil.formatCurrency(mTransaction.getTaxAmount()));
		}
		
		if (mTransaction.getServiceChargeAmount() != 0) {
			
			String label = getString(R.string.field_service_charge_percentage) + " " + CommonUtil.formatPercentage(mTransaction.getServiceChargePercentage());
			
			mServiceChargeLabelText.setText(label);
			mServiceChargeText.setText(CommonUtil.formatCurrency(mTransaction.getServiceChargeAmount()));
		}
		
		mTaxText.setText(CommonUtil.formatCurrency(tax));
		mServiceChargeText.setText(CommonUtil.formatCurrency(serviceCharge));
		
		mTaxPanel.setVisibility(View.VISIBLE);
		mServiceChargePanel.setVisibility(View.VISIBLE);
		
		if (tax == 0) {
			mTaxPanel.setVisibility(View.GONE);
		}
		
		if (serviceCharge == 0) {
			mServiceChargePanel.setVisibility(View.GONE);
		}
		
		mPaymentTypeText.setText(CodeUtil.getPaymentTypeLabel(mTransaction.getPaymentType()));
		mTotalOrderText.setText(CommonUtil.formatCurrency(mTransaction.getTotalAmount()));
		
		mAdapter.notifyDataSetChanged();
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackPressed();
			}
		};
	}
}
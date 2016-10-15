package com.tokoku.pos.report.credit;

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
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.TransactionItem;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.common.ConfirmListener;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.dao.TransactionItemDaoService;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.ConfirmationUtil;

public class CreditDetailFragment extends BaseFragment implements CreditDetailProductArrayAdapter.ItemActionListener {
	
	private LinearLayout mCustomerPanel;
	private LinearLayout mWaitressPanel;
	
	private LinearLayout mCreditPaymentsBelowPanel;
	private LinearLayout mProductSoldBelowPanel;
	
	private ImageButton mBackButton;
	private TextView mOutstandingText;
	
	private TextView mTransactionNoText;
	private TextView mDateText;
	private TextView mWaitressText;	
	private TextView mCashierText;
	private TextView mCustomerText;
	
	private TextView mInitialPaymentText;
	private TextView mTotalCreditPaymentText;
	
	private TextView mTotalPurchaseText;
	private TextView mTotalPaymentText;
	
	private TextView mCreditPaymentsText;
	private TextView mProductPurchasedText;
	
	private LinearLayout mTaxPanel;
	private LinearLayout mServiceChargePanel;
	
	private TextView mDiscountLabelText;
	private TextView mDiscountText;
	private TextView mTaxText;
	private TextView mTaxLabelText;
	private TextView mServiceChargeText;
	private TextView mServiceChargeLabelText;
	private TextView mTotalOrderText;
	
	private TextView mAddPaymentText;
	
	protected ListView mCreditInfoListView;

	protected TransactionsBean mTransaction;
	protected List<TransactionItem> mTransactionItems;
	
	private List<Cashflow> mCashflows;
	
	private CreditActionListener mActionListener;
	
	private CreditDetailPaymentArrayAdapter mPaymentAdapter;
	private CreditDetailProductArrayAdapter mTransactionItemsAdapter;
	
	private CashflowDaoService mCashflowDaoService = new CashflowDaoService();
	private TransactionItemDaoService mTransactionItemDaoService = new TransactionItemDaoService(); 
	
	Float mOutstanding;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_credit_detail_fragment, container, false);
		
		if (mTransactionItems == null) {
			mTransactionItems = new ArrayList<TransactionItem>();
		}
		
		if (mCashflows == null) {
			mCashflows = new ArrayList<Cashflow>();
		}
		
		mPaymentAdapter = new CreditDetailPaymentArrayAdapter(getActivity(), mCashflows);
		mTransactionItemsAdapter = new CreditDetailProductArrayAdapter(getActivity(), mTransactionItems, this);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mCreditInfoListView = (ListView) getView().findViewById(R.id.creditInfoList);

		mCreditInfoListView.setAdapter(mTransactionItemsAdapter);
		mCreditInfoListView.setItemsCanFocus(true);
		mCreditInfoListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mCreditPaymentsBelowPanel  = (LinearLayout) getView().findViewById(R.id.creditPaymentsBelowPanel);
		mProductSoldBelowPanel  = (LinearLayout) getView().findViewById(R.id.productSoldbelowPanel);
		
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
		
		mOutstandingText = (TextView) getView().findViewById(R.id.titleText);
		
		mTransactionNoText = (TextView) getView().findViewById(R.id.transactionNoText);
		mDateText = (TextView) getView().findViewById(R.id.dateText);
		mWaitressText = (TextView) getView().findViewById(R.id.waitressText);
		mCashierText = (TextView) getView().findViewById(R.id.cashierText);
		mCustomerText = (TextView) getView().findViewById(R.id.customerText);
		
		mInitialPaymentText = (TextView) getView().findViewById(R.id.initialPaymentText);
		mTotalCreditPaymentText = (TextView) getView().findViewById(R.id.totalCreditPaymentText);
		
		mTotalPurchaseText = (TextView) getView().findViewById(R.id.totalPurchaseText);
		mTotalPaymentText = (TextView) getView().findViewById(R.id.totalPaymentText);

		mCreditPaymentsText = (TextView) getView().findViewById(R.id.creditPaymentsText);
		mProductPurchasedText = (TextView) getView().findViewById(R.id.productPurchasedText);
		
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
		
		mTotalOrderText = (TextView) getView().findViewById(R.id.totalOrderText);
		mTotalOrderText.setText(CommonUtil.formatCurrency(0));
		
		mTaxPanel = (LinearLayout) getView().findViewById(R.id.taxPanel);
		mServiceChargePanel = (LinearLayout) getView().findViewById(R.id.serviceChargePanel);
		
		mCreditPaymentsText.setOnClickListener(getCreditPaymentsOnClickListener());
		mProductPurchasedText.setOnClickListener(getProductPurchasedOnClickListener());
		
		mAddPaymentText = (TextView) getView().findViewById(R.id.paymentText);
		mAddPaymentText.setOnClickListener(getAddPaymentBtnOnClickListener());
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
            mActionListener = (CreditActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	@Override
	public void onItemSelected(TransactionItem transactionItem) {
	}
	
	public void setTransaction(TransactionsBean transaction) {
		
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
			mTransactionItems.addAll(mTransactionItemDaoService.getTransactionItemsByTransactionId(mTransaction.getRemote_id()));
			mTransactionItemsAdapter.notifyDataSetChanged();
			
			getView().setVisibility(View.VISIBLE);
		
		} else {
		
			getView().setVisibility(View.INVISIBLE);
			return;
		}
		
		mDateText.setText(CommonUtil.formatDayDateTime(mTransaction.getTransaction_date()));
		
		mTransactionNoText.setText(mTransaction.getTransaction_no());
		mCashierText.setText(mTransaction.getCashier_name());
		
		if (!CommonUtil.isEmpty(mTransaction.getWaitress_name())) {
			mWaitressText.setText(mTransaction.getWaitress_name());
			mWaitressPanel.setVisibility(View.VISIBLE);
		} else {
			mWaitressPanel.setVisibility(View.GONE);
		} 
		
		if (!CommonUtil.isEmpty(mTransaction.getCustomer_name())) {
			mCustomerText.setText(mTransaction.getCustomer_name());
			mCustomerPanel.setVisibility(View.VISIBLE);
		} else {
			mCustomerPanel.setVisibility(View.GONE);
		}
		
		Float totalCreditPayments = mCashflowDaoService.getTotalCreditPayments(mTransaction);
		Float totalPayment = mTransaction.getPayment_amount() + totalCreditPayments;
		
		mOutstanding = mTransaction.getTotal_amount() - totalPayment;
		
		mOutstandingText.setText(CommonUtil.formatCurrency(mOutstanding));
		
		mInitialPaymentText.setText(CommonUtil.formatCurrency(mTransaction.getPayment_amount()));
		mTotalCreditPaymentText.setText(CommonUtil.formatCurrency(totalCreditPayments));
		
		mTotalPurchaseText.setText(CommonUtil.formatCurrency(mTransaction.getTotal_amount()));
		mTotalPaymentText.setText(CommonUtil.formatCurrency(totalPayment));
		
		if (!CommonUtil.isEmpty(mTransaction.getDiscount_name()) &&
			mTransaction.getDiscount_amount() != 0) {
			
			String label = mTransaction.getDiscount_name();
			if (mTransaction.getDiscount_percentage() != 0) {
				label += " " + CommonUtil.formatPercentage(mTransaction.getDiscount_percentage());
			}
			mDiscountLabelText.setText(label);
			mDiscountText.setText(CommonUtil.formatCurrency(mTransaction.getDiscount_amount()));
			
		} else {
			mDiscountLabelText.setText(getString(R.string.no_discount));
			mDiscountText.setText(Constant.EMPTY_STRING);
		}
		
		float tax = mTransaction.getTax_amount();
		float serviceCharge = mTransaction.getService_charge_amount();
		
		if (mTransaction.getTax_amount() != 0) {
			
			String label = getString(R.string.field_tax_percentage) + " " + CommonUtil.formatPercentage(mTransaction.getTax_percentage());
			
			mTaxLabelText.setText(label);
			mTaxText.setText(CommonUtil.formatCurrency(mTransaction.getTax_amount()));
		}
		
		if (mTransaction.getService_charge_amount() != 0) {
			
			String label = getString(R.string.field_service_charge_percentage) + " " + CommonUtil.formatPercentage(mTransaction.getService_charge_percentage());
			
			mServiceChargeLabelText.setText(label);
			mServiceChargeText.setText(CommonUtil.formatCurrency(mTransaction.getService_charge_amount()));
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
		
		mTotalOrderText.setText(CommonUtil.formatCurrency(mTransaction.getTotal_amount()));
		
		mTransactionItemsAdapter.notifyDataSetChanged();
		
		updateList();
	}
	
	private void updateList() {
		
		mCreditPaymentsBelowPanel.setVisibility(View.GONE);
		mProductSoldBelowPanel.setVisibility(View.GONE);
		
		if (mOutstanding > 0) {
			mCreditPaymentsBelowPanel.setVisibility(View.VISIBLE);
		}
		
		mCashflows.clear();

		List<Cashflow> cashflows = mCashflowDaoService.getCashflows(mTransaction);
		mCashflows.addAll(cashflows);
		
		mCreditPaymentsText.setTextColor(getResources().getColor(R.color.text_blue));
		mCreditInfoListView.setAdapter(mPaymentAdapter);
		
		mPaymentAdapter.notifyDataSetChanged();
		
		mTransactionItems.clear();

		List<TransactionItem> transactionItems = mTransactionItemDaoService.getTransactionItemsByTransactionId(mTransaction.getRemote_id());
		mTransactionItems.addAll(transactionItems);
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackPressed();
			}
		};
	}
		
	private View.OnClickListener getProductPurchasedOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mCreditPaymentsText.setTextColor(getResources().getColor(R.color.text_light));
				mProductPurchasedText.setTextColor(getResources().getColor(R.color.text_blue));
				
				mCreditInfoListView.setAdapter(mTransactionItemsAdapter);
				
				mCreditPaymentsBelowPanel.setVisibility(View.GONE);
				mProductSoldBelowPanel.setVisibility(View.VISIBLE);
			}
		};
	}
	
	private View.OnClickListener getCreditPaymentsOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mCreditPaymentsText.setTextColor(getResources().getColor(R.color.text_blue));
				mProductPurchasedText.setTextColor(getResources().getColor(R.color.text_light));
				
				mCreditInfoListView.setAdapter(mPaymentAdapter);
				
				if (mOutstanding > 0) {
					mCreditPaymentsBelowPanel.setVisibility(View.VISIBLE);
				}
				
				mProductSoldBelowPanel.setVisibility(View.GONE);
			}
		};
	}
			
	private View.OnClickListener getAddPaymentBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ConfirmationUtil.confirmTask(getFragmentManager(), (ConfirmListener) getActivity(), ConfirmationUtil.ADD_PAYMENT, getString(R.string.report_credit_add_payment_confirmation));
			}
		};
	}
}
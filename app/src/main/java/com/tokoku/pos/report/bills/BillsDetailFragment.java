package com.tokoku.pos.report.bills;

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
import com.android.pos.dao.Bills;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.Inventory;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.util.CommonUtil;

public class BillsDetailFragment extends BaseFragment {
	
	private ImageButton mBackButton;
	
	private LinearLayout mSupplierAndDatePanel;
	private LinearLayout mSupplierPanel;
	private LinearLayout mDueDatePanel;
	private LinearLayout mPaymentDatePanel;
	
	private TextView mReferenceNoText;
	private TextView mDateText;
	
	private TextView mSupplierText;
	private TextView mDueDateText;
	private TextView mPaymentDateText;
	
	private TextView mTotalAmountText;
	private TextView mPaymentAmountText;
	private TextView mOutstandingAmountText;
	
	private TextView mRemarksText;
	
	private TextView mBillPaymentsText;
	private TextView mProductPurchasedText;
	
	private ListView mBillInfoListView;

	private Bills mBill;
	
	private List<Cashflow> mCashflows;
	private List<Inventory> mInventories;
	
	private BillsActionListener mActionListener;
	
	private BillsDetailPaymentArrayAdapter mPaymentAdapter;
	private BillsDetailProductArrayAdapter mProductPurchasedAdapter;
	
	private CashflowDaoService mCashflowDaoService = new CashflowDaoService();
	private InventoryDaoService mInventoryDaoService = new InventoryDaoService();
	private BillsDaoService mBillsDaoService = new BillsDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_bills_detail_fragment, container, false);
		
		if (mCashflows == null) {
			mCashflows = new ArrayList<Cashflow>();
		}
		
		if (mInventories == null) {
			mInventories = new ArrayList<Inventory>();
		}
		
		mPaymentAdapter = new BillsDetailPaymentArrayAdapter(getActivity(), mCashflows);
		mProductPurchasedAdapter = new BillsDetailProductArrayAdapter(getActivity(), mInventories);
		
		initViewVariables(view);
		
		return view;
	}
	
	private void initViewVariables(View view) {
		
		mBillInfoListView = (ListView) view.findViewById(R.id.billInfoList);
		
		mBillInfoListView.setItemsCanFocus(true);
		mBillInfoListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mSupplierAndDatePanel = (LinearLayout) view.findViewById(R.id.supplierAndDatePanel);
		mSupplierPanel = (LinearLayout) view.findViewById(R.id.supplierPanel);
		mDueDatePanel = (LinearLayout) view.findViewById(R.id.dueDatePanel);
		mPaymentDatePanel = (LinearLayout) view.findViewById(R.id.paymentDatePanel);
		
		mBackButton = (ImageButton) view.findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mReferenceNoText = (TextView) view.findViewById(R.id.referenceNoText);
		mDateText = (TextView) view.findViewById(R.id.dateText);
		
		mSupplierText = (TextView) view.findViewById(R.id.supplierText);
		mDueDateText = (TextView) view.findViewById(R.id.dueDateText);
		mPaymentDateText = (TextView) view.findViewById(R.id.paymentDateText);
		mTotalAmountText = (TextView) view.findViewById(R.id.totalAmountText);
		mPaymentAmountText = (TextView) view.findViewById(R.id.paymentAmountText);
		mOutstandingAmountText = (TextView) view.findViewById(R.id.outstandingAmountText);
		mRemarksText = (TextView) view.findViewById(R.id.remarksText);
		
		mBillPaymentsText = (TextView) view.findViewById(R.id.billPaymentsText);
		mProductPurchasedText = (TextView) view.findViewById(R.id.productPurchasedText);
		
		mBillPaymentsText.setOnClickListener(getBillPaymentsOnClickListener());
		mProductPurchasedText.setOnClickListener(getProductPurchasedOnClickListener());
	}
	
	public void onStart() {
		super.onStart();
		
		updateContent();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (BillsActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BillsReportActionListener");
        }
    }
	
	public void setBill(Bills bill) {
		
		mBill = bill;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}

		if (mBill == null) {

			getView().setVisibility(View.INVISIBLE);
			return;
		}
		
		mBillsDaoService.updatePaymentDetails(mBill.getId());

		mReferenceNoText.setText(mBill.getBillReferenceNo());
		mDateText.setText(CommonUtil.formatDate(mBill.getBillDate()));

		Float totalAmount = CommonUtil.getNvlFloat(mBill.getBillAmount());
		Float paymentAmount = CommonUtil.getNvlFloat(mBill.getPayment());
		Float outstandingAmount = totalAmount - paymentAmount;
		
		mSupplierText.setText(mBill.getSupplierName());
		mDueDateText.setText(CommonUtil.formatDate(mBill.getBillDueDate()));
		mPaymentDateText.setText(CommonUtil.formatDate(mBill.getPaymentDate()));
		mTotalAmountText.setText(CommonUtil.formatCurrency(totalAmount));
		mPaymentAmountText.setText(CommonUtil.formatCurrency(paymentAmount));
		mOutstandingAmountText.setText(CommonUtil.formatCurrency(outstandingAmount));
		mRemarksText.setText(mBill.getRemarks());
		
		updateList();
		
		if (CommonUtil.isEmpty(mBill.getSupplierName())) {
			mSupplierPanel.setVisibility(View.GONE);
		} else {
			mSupplierPanel.setVisibility(View.VISIBLE);
		}
		
		if (mBill.getBillDueDate() == null) {
			mDueDatePanel.setVisibility(View.GONE);
		} else {
			mDueDatePanel.setVisibility(View.VISIBLE);
		}
		
		if (mBill.getPaymentDate() == null) {
			mPaymentDatePanel.setVisibility(View.GONE);
		} else {
			mPaymentDatePanel.setVisibility(View.VISIBLE);
		}
		
		if (CommonUtil.isEmpty(mBill.getSupplierName()) && mBill.getBillDueDate() == null && mBill.getPaymentDate() == null) {
			mSupplierAndDatePanel.setVisibility(View.GONE);
		} else {
			mSupplierAndDatePanel.setVisibility(View.VISIBLE);
		}
		
		getView().setVisibility(View.VISIBLE);
	}
	
	private void updateList() {
		
		mCashflows.clear();

		List<Cashflow> cashflows = mCashflowDaoService.getCashflows(mBill);
		mCashflows.addAll(cashflows);
		
		if (cashflows.size() > 0) {
			mBillPaymentsText.setVisibility(View.VISIBLE);
			mBillPaymentsText.setTextColor(getResources().getColor(R.color.text_blue));
			mBillInfoListView.setAdapter(mPaymentAdapter);
		} else {
			mBillPaymentsText.setVisibility(View.GONE);
		}
		
		mPaymentAdapter.notifyDataSetChanged();
		
		mInventories.clear();

		List<Inventory> inventories = mInventoryDaoService.getInventories(mBill);
		mInventories.addAll(inventories);
		
		if (inventories.size() > 0) {
			mProductPurchasedText.setVisibility(View.VISIBLE);
			
			if (cashflows.size() == 0) {
				mProductPurchasedText.setTextColor(getResources().getColor(R.color.text_blue));
				mBillInfoListView.setAdapter(mProductPurchasedAdapter);
			}
		} else {
			mProductPurchasedText.setVisibility(View.GONE);
		}
		
		mProductPurchasedAdapter.notifyDataSetChanged();
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
				
				mBillPaymentsText.setTextColor(getResources().getColor(R.color.text_light));
				mProductPurchasedText.setTextColor(getResources().getColor(R.color.text_blue));
				
				mBillInfoListView.setAdapter(mProductPurchasedAdapter);
			}
		};
	}
	
	private View.OnClickListener getBillPaymentsOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mBillPaymentsText.setTextColor(getResources().getColor(R.color.text_blue));
				mProductPurchasedText.setTextColor(getResources().getColor(R.color.text_light));
				
				mBillInfoListView.setAdapter(mPaymentAdapter);
			}
		};
	}
}
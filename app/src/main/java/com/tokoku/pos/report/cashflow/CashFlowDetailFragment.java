package com.tokoku.pos.report.cashflow;

import java.util.ArrayList;
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
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.model.CashFlowMonthBean;
import com.tokoku.pos.model.CashflowBean;
import com.tokoku.pos.util.CommonUtil;

public class CashFlowDetailFragment extends BaseFragment 
	implements CashFlowDetailArrayAdapter.ItemActionListener {
	
	private ImageButton mBackButton;
	
	private TextView mDateText;
	private TextView mTotalText;
	
	private ListView mCashflowList;

	private CashFlowMonthBean mCashFlowMonth;
	private List<CashflowBean> mCashflows;
	
	private CashFlowActionListener mActionListener;
	
	private CashFlowDetailArrayAdapter mAdapter;
	
	private CashflowDaoService mCashflowDaoService = new CashflowDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_cashflow_detail_fragment, container, false);
		
		if (mCashflows == null) {
			mCashflows = new ArrayList<CashflowBean>();
		} 
		
		mAdapter = new CashFlowDetailArrayAdapter(getActivity(), mCashflows, this);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mCashflowList = (ListView) getView().findViewById(R.id.billsList);

		mCashflowList.setAdapter(mAdapter);
		mCashflowList.setItemsCanFocus(true);
		mCashflowList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
		
		mDateText = (TextView) getView().findViewById(R.id.dateText);
		mTotalText = (TextView) getView().findViewById(R.id.totalText);
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
            mActionListener = (CashFlowActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashFlowActionListener");
        }
    }
	
	public void setCashFlowMonth(CashFlowMonthBean cashFlowMonth) {
		
		mCashFlowMonth = cashFlowMonth;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	private Double getCashflowsTotalAmount(List<CashflowBean> cashflows) {
		
		Double totalAmount = 0d;
		
		for (CashflowBean cashflow : cashflows) {
			System.out.println("Cashflow: " + CommonUtil.formatCurrency(totalAmount) + " + " + CommonUtil.formatCurrency(cashflow.getCash_amount()));
			totalAmount += cashflow.getCash_amount();
			System.out.println("Total: " + CommonUtil.formatCurrency(totalAmount));
		}
		
		return totalAmount;
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}

		if (mCashFlowMonth == null) {

			getView().setVisibility(View.INVISIBLE);
			return;
		}

		mDateText.setText(CommonUtil.formatMonth(mCashFlowMonth.getMonth()));

		mCashflows.clear();

		List<CashflowBean> cashflows = mCashflowDaoService.getCashFlowDays(mCashFlowMonth);

		mCashflows.addAll(cashflows);

		mAdapter.notifyDataSetChanged();

		Double cashflowsTotalAmount = getCashflowsTotalAmount(cashflows);

		mTotalText.setText(CommonUtil.formatCurrency(cashflowsTotalAmount));

		if (cashflowsTotalAmount < 0) {
			mTotalText.setTextColor(getActivity().getResources().getColor(R.color.text_red));
		} else {
			mTotalText.setTextColor(getActivity().getResources().getColor(R.color.section_header_text));
		}

		getView().setVisibility(View.VISIBLE);

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
	
	@Override
	public void onCashflowSelected(CashflowBean cashFlow) {
		
		mActionListener.onCashFlowSelected(cashFlow);
	}
}
package com.tokoku.pos.report.tax;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TaxListFragment extends BaseFragment 
	implements TaxArrayAdapter.ItemActionListener, 
		TaxDayArrayAdapter.ItemActionListener, 
		TaxMonthArrayAdapter.ItemActionListener,
		TaxYearArrayAdapter.ItemActionListener {
	
	private ImageButton mBackButton;
	
	private TextView mNavigationTitle;
	private TextView mNavText;
	
	private ListView mTransactionList;
	
	private List<TransactionYearBean> mTransactionYears;
	private List<TransactionMonthBean> mTransactionMonths;
	private List<TransactionDayBean> mTransactionDays;
	private List<Transactions> mTransactions;
	
	private TransactionYearBean mSelectedTransactionYear;
	private TransactionMonthBean mSelectedTransactionMonth;
	private TransactionDayBean mSelectedTransactionDay;
	private Transactions mSelectedTransaction;
	
	private TaxYearArrayAdapter mTransactionYearAdapter;
	private TaxMonthArrayAdapter mTransactionMonthAdapter;
	private TaxDayArrayAdapter mTransactionDayAdapter;
	private TaxArrayAdapter mTransactionAdapter;
	
	private TaxActionListener mActionListener;
	
	private TransactionsDaoService mTransactionDaoService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mTransactionDaoService = new TransactionsDaoService();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_transaction_list_fragment, container, false);
		
		if (mTransactionYears == null) {
			mTransactionYears = new ArrayList<TransactionYearBean>();
		}
		
		if (mTransactionMonths == null) {
			mTransactionMonths = new ArrayList<TransactionMonthBean>();
		}
		
		if (mTransactionDays == null) {
			mTransactionDays = new ArrayList<TransactionDayBean>();
		}
		
		if (mTransactions == null) {
			mTransactions = new ArrayList<Transactions>();
		}
		
		mTransactionYearAdapter = new TaxYearArrayAdapter(getActivity(), mTransactionYears, this);
		mTransactionMonthAdapter = new TaxMonthArrayAdapter(getActivity(), mTransactionMonths, this);
		mTransactionDayAdapter = new TaxDayArrayAdapter(getActivity(), mTransactionDays, this);
		mTransactionAdapter = new TaxArrayAdapter(getActivity(), mTransactions, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mNavigationTitle = (TextView) getView().findViewById(R.id.navigationTitle);
		mNavText = (TextView) getView().findViewById(R.id.navText);
		
		mTransactionList = (ListView) getActivity().findViewById(R.id.transactionList);
		
		mTransactionList.setItemsCanFocus(true);
		mTransactionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		if (mSelectedTransactionDay != null) {
			
			Transactions transaction = mSelectedTransaction; 
			onTransactionDaySelected(mSelectedTransactionDay);
			mSelectedTransaction = transaction;
			
			if (mSelectedTransaction != null) {
				onTransactionSelected(mSelectedTransaction);
			}
				
		} else if (mSelectedTransactionMonth != null) {
			onTransactionMonthSelected(mSelectedTransactionMonth);
			
		} else if (mSelectedTransactionYear != null) {
			onTransactionYearSelected(mSelectedTransactionYear);
			
		} else {
			displayTransactionAllYears();
		}
	}
	
	public void updateContent() {
		
		if (mSelectedTransaction != null) {
			onTransactionSelected(mSelectedTransaction);
			
		} else if (mSelectedTransactionDay != null) {
			onTransactionDaySelected(mSelectedTransactionDay);
			
		} else if (mSelectedTransactionMonth != null) {
			onTransactionMonthSelected(mSelectedTransactionMonth);
			
		} else if (mSelectedTransactionYear != null) {
			onTransactionYearSelected(mSelectedTransactionYear);
			
		} else {
			displayTransactionAllYears();
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (TaxActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	public void setSelectedTransactionYear(TransactionYearBean transactionYear) {
		
		mSelectedTransactionYear = transactionYear;
	}
	
	public void setSelectedTransactionMonth(TransactionMonthBean transactionMonth) {
		
		mSelectedTransactionMonth = transactionMonth;
	}
	
	public void setSelectedTransactionDay(TransactionDayBean transactionDay) {
		
		mSelectedTransactionDay = transactionDay;
	}
	
	public void setSelectedTransaction(Transactions transaction) {
		
		mSelectedTransaction = transaction;
	}
	
	private long getTransactionYearsTaxAmount(List<TransactionYearBean> transactionYears) {
		
		long totalAmount = 0;
		
		for (TransactionYearBean transactionYear : transactionYears) {
			totalAmount += transactionYear.getAmount();
		}
		
		return totalAmount;
	}
	
	private long getTransactionMonthsTaxAmount(List<TransactionMonthBean> transactionMonths) {
		
		long totalAmount = 0;
		
		for (TransactionMonthBean transactionMonth : transactionMonths) {
			totalAmount += transactionMonth.getAmount();
		}
		
		return totalAmount;
	}
	
	private int getTransactionDaysTaxAmount(List<TransactionDayBean> transactionDays) {
		
		int totalAmount = 0;
		
		for (TransactionDayBean transactionDay : transactionDays) {
			totalAmount += transactionDay.getAmount();
		}
		
		return totalAmount;
	}
	
	private long getTransactionsTaxAmount(List<Transactions> transactions) {
		
		long totalAmount = 0;
		
		for (Transactions transaction : transactions) {
			totalAmount += transaction.getTaxAmount();
		}
		
		return totalAmount;
	}
	
	public void displayTransactionAllYears() {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		mSelectedTransactionMonth = null;
		mSelectedTransactionYear = null;
		
		mTransactionYears.clear();
		mTransactionYears.addAll(mTransactionDaoService.getTransactionTaxYears());
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(false);
		
		mNavigationTitle.setText(getString(R.string.transaction_total));
		mNavText.setText(CommonUtil.formatCurrency(getTransactionYearsTaxAmount(mTransactionYears)));
		
		mTransactionList.setAdapter(mTransactionYearAdapter);
	}
	
	public void displayTransactionOnYear(TransactionYearBean transactionYear) {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		mSelectedTransactionMonth = null;
		
		mTransactionMonths.clear();
		mTransactionMonths.addAll(mTransactionDaoService.getTransactionTaxMonths(transactionYear));
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(true);
		
		mNavigationTitle.setText(getString(R.string.report_year, CommonUtil.formatYear(transactionYear.getYear())).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionMonthsTaxAmount(mTransactionMonths)));
		
		mTransactionList.setAdapter(mTransactionMonthAdapter);
	}
	
	public void displayTransactionOnMonth(TransactionMonthBean transactionMonth) {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		
		mTransactionDays.clear();
		mTransactionDays.addAll(mTransactionDaoService.getTransactionTaxDays(transactionMonth));
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(true);
		
		mNavigationTitle.setText(CommonUtil.formatMonth(transactionMonth.getMonth()).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionDaysTaxAmount(mTransactionDays)));
		
		mTransactionList.setAdapter(mTransactionDayAdapter);
	}
	
	public void displayTransactionOnDay(TransactionDayBean transactionDay) {
		
		mSelectedTransaction = null;
		
		if (!isViewInitialized()) {
			return;
		}
		
		onTransactionDaySelected(transactionDay);
	}
	
	@Override
	public void onTransactionSelected(Transactions transaction) {
		
		mSelectedTransaction = transaction;
		mActionListener.onTransactionSelected(transaction);
		mTransactionAdapter.notifyDataSetChanged();
	}
	
	@Override
	public Transactions getSelectedTransaction() {
		
		return mSelectedTransaction;
	}
	
	@Override
	public void onTransactionYearSelected(TransactionYearBean transactionYear) {
		
		setBackButtonVisible(true);
		
		mSelectedTransactionYear = transactionYear;
		mSelectedTransactionMonth = null;
		
		mActionListener.onTransactionYearSelected(transactionYear);
		
		mTransactionMonths.clear();
		mTransactionMonths.addAll(mTransactionDaoService.getTransactionTaxMonths(transactionYear));
		
		mNavigationTitle.setText(getString(R.string.report_year, CommonUtil.formatYear(transactionYear.getYear())).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionMonthsTaxAmount(mTransactionMonths)));
		
		mTransactionList.setAdapter(mTransactionMonthAdapter);
	}
	
	@Override
	public TransactionYearBean getSelectedTransactionYear() {
		
		return mSelectedTransactionYear;
	}
	
	@Override
	public void onTransactionMonthSelected(TransactionMonthBean transactionMonth) {
		
		setBackButtonVisible(true);
		
		mSelectedTransactionMonth = transactionMonth;
		mSelectedTransactionDay = null;
		
		mActionListener.onTransactionMonthSelected(transactionMonth);
		
		mTransactionDays.clear();
		mTransactionDays.addAll(mTransactionDaoService.getTransactionTaxDays(transactionMonth));
		
		mNavigationTitle.setText(CommonUtil.formatMonth(transactionMonth.getMonth()).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionDaysTaxAmount(mTransactionDays)));
		
		mTransactionList.setAdapter(mTransactionDayAdapter);
	}
	
	@Override
	public TransactionMonthBean getSelectedTransactionMonth() {
		
		return mSelectedTransactionMonth;
	}
	
	@Override
	public void onTransactionDaySelected(TransactionDayBean transactionDay) {
		
		setBackButtonVisible(true);
		
		mSelectedTransactionDay = transactionDay;
		mSelectedTransaction = null;
		
		mActionListener.onTransactionDaySelected(transactionDay);
		
		mTransactions.clear();
		mTransactions.addAll(mTransactionDaoService.getTransactions(transactionDay.getDate()));
		
		mNavigationTitle.setText(CommonUtil.formatDayDate(transactionDay.getDate()).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionsTaxAmount(mTransactions)));
		
		mTransactionList.setAdapter(mTransactionAdapter);
	}
	
	@Override
	public TransactionDayBean getSelectedTransactionDay() {
		
		return mSelectedTransactionDay;
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackPressed();
			}
		};
	}
	
	private void setBackButtonVisible(boolean isVisible) {
		
		if (isVisible) {
			mBackButton.setVisibility(View.VISIBLE);
		} else {
			mBackButton.setVisibility(View.GONE);
		}
	}
}
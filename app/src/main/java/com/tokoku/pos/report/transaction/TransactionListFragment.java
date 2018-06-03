package com.tokoku.pos.report.transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PoiUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TransactionListFragment extends BaseFragment 
	implements TransactionArrayAdapter.ItemActionListener, 
		TransactionDayArrayAdapter.ItemActionListener, 
		TransactionMonthArrayAdapter.ItemActionListener,
		TransactionYearArrayAdapter.ItemActionListener {
	
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
	
	private TransactionYearArrayAdapter mTransactionYearAdapter;
	private TransactionMonthArrayAdapter mTransactionMonthAdapter;
	private TransactionDayArrayAdapter mTransactionDayAdapter;
	private TransactionArrayAdapter mTransactionAdapter;
	
	private TransactionActionListener mActionListener;
	
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
		
		mTransactionYearAdapter = new TransactionYearArrayAdapter(getActivity(), mTransactionYears, this);
		mTransactionMonthAdapter = new TransactionMonthArrayAdapter(getActivity(), mTransactionMonths, this);
		mTransactionDayAdapter = new TransactionDayArrayAdapter(getActivity(), mTransactionDays, this);
		mTransactionAdapter = new TransactionArrayAdapter(getActivity(), mTransactions, this);
		
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
            mActionListener = (TransactionActionListener) activity;
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
	
	private double getTransactionYearsTotalAmount(List<TransactionYearBean> transactionYears) {
		
		double totalAmount = 0;
		
		for (TransactionYearBean transactionYear : transactionYears) {
			totalAmount += transactionYear.getAmount();
		}
		
		return totalAmount;
	}
	
	private double getTransactionMonthsTotalAmount(List<TransactionMonthBean> transactionMonths) {
		
		double totalAmount = 0;
		
		for (TransactionMonthBean transactionMonth : transactionMonths) {
			totalAmount += transactionMonth.getAmount();
		}
		
		return totalAmount;
	}
	
	private double getTransactionDaysTotalAmount(List<TransactionDayBean> transactionDays) {
		
		double totalAmount = 0;
		
		for (TransactionDayBean transactionDay : transactionDays) {
			totalAmount += transactionDay.getAmount();
		}
		
		return totalAmount;
	}
	
	private double getTransactionsTotalAmount(List<Transactions> transactions) {
		
		double totalAmount = 0;
		
		for (Transactions transaction : transactions) {
			totalAmount += transaction.getTotalAmount();
		}
		
		return totalAmount;
	}
	
	public void displayTransactionAllYears() {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		mSelectedTransactionMonth = null;
		mSelectedTransactionYear = null;
		
		mTransactionYears.clear();
		mTransactionYears.addAll(mTransactionDaoService.getTransactionYears());
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(false);
		
		mNavigationTitle.setText(getString(R.string.transaction_total));
		mNavText.setText(CommonUtil.formatCurrency(getTransactionYearsTotalAmount(mTransactionYears)));
		
		mTransactionList.setAdapter(mTransactionYearAdapter);
	}
	
	public void displayTransactionOnYear(TransactionYearBean transactionYear) {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		mSelectedTransactionMonth = null;
		
		if (!isViewInitialized()) {
			return;
		}
		
		mTransactionMonths.clear();
		mTransactionMonths.addAll(mTransactionDaoService.getTransactionMonths(transactionYear));
		
		setBackButtonVisible(true);
		
		mNavigationTitle.setText(getString(R.string.report_year, CommonUtil.formatYear(transactionYear.getYear())).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionMonthsTotalAmount(mTransactionMonths)));
		
		mTransactionList.setAdapter(mTransactionMonthAdapter);
	}
	
	public void displayTransactionOnMonth(TransactionMonthBean transactionMonth) {
		
		mSelectedTransaction = null;
		mSelectedTransactionDay = null;
		
		mTransactionDays.clear();
		mTransactionDays.addAll(mTransactionDaoService.getTransactionDays(transactionMonth));
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(true);
		
		mNavigationTitle.setText(CommonUtil.formatMonth(transactionMonth.getMonth()).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionDaysTotalAmount(mTransactionDays)));
		
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
		mTransactionMonths.addAll(mTransactionDaoService.getTransactionMonths(transactionYear));
		
		mNavigationTitle.setText(getString(R.string.report_year, CommonUtil.formatYear(transactionYear.getYear())).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionMonthsTotalAmount(mTransactionMonths)));
		
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
		mTransactionDays.addAll(mTransactionDaoService.getTransactionDays(transactionMonth));
		
		mNavigationTitle.setText(CommonUtil.formatMonth(transactionMonth.getMonth()).toUpperCase());
		mNavText.setText(CommonUtil.formatCurrency(getTransactionDaysTotalAmount(mTransactionDays)));
		
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
		mNavText.setText(CommonUtil.formatCurrency(getTransactionsTotalAmount(mTransactions)));
		
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

    public void generateReport(String title) {

		mActionListener.onGenerateReportStart();

        //New Workbook
		XSSFWorkbook wb = PoiUtil.getWorkbook();

        Cell c = null;

        CellStyle headerCs = PoiUtil.getHeaderCellStyle(wb);
		CellStyle contentCs = PoiUtil.getContentCellStyle(wb);
		CellStyle contentNumberCs = PoiUtil.getContentNumberCellStyle(wb);
		CellStyle bottomCs = PoiUtil.getBottomCellStyle(wb);
		CellStyle bottomNumberCs = PoiUtil.getBottomNumberCellStyle(wb);

        //New Sheet
        Sheet sheet = PoiUtil.getSheet(wb, title);

        int index = 0;

        // Generate column headings
        Row row = sheet.createRow(index++);

        c = row.createCell(0);
        c.setCellValue(mNavigationTitle.getText().toString());
        c.setCellStyle(headerCs);

        c = row.createCell(1);
        c.setCellValue(Constant.EMPTY_STRING);
        c.setCellStyle(headerCs);

        sheet.setColumnWidth(0, (12 * 500));
        sheet.setColumnWidth(1, (8 * 500));

        if (mSelectedTransactionDay != null) {

			for (Transactions transaction : mTransactions) {

                row = sheet.createRow(index++);

                c = row.createCell(0);
                c.setCellValue(CommonUtil.formatDateTime(transaction.getTransactionDate()));
                c.setCellStyle(contentCs);

                c = row.createCell(1);
                c.setCellValue(transaction.getTotalAmount());
                c.setCellStyle(contentNumberCs);
            }

            row = sheet.createRow(index++);

            c = row.createCell(0);
            c.setCellValue(getString(R.string.total));
            c.setCellStyle(bottomCs);

            c = row.createCell(1);
            c.setCellValue(getTransactionsTotalAmount(mTransactions));
            c.setCellStyle(bottomNumberCs);

        } else if (mSelectedTransactionMonth != null) {

			for (TransactionDayBean transactionDay : mTransactionDays) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatDate(transactionDay.getDate(), "dd MMM yyyy, EEEE"));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(transactionDay.getAmount());
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(getTransactionDaysTotalAmount(mTransactionDays));
			c.setCellStyle(bottomNumberCs);

        } else if (mSelectedTransactionYear != null) {

			for (TransactionMonthBean transactionMonth : mTransactionMonths) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatMonth(transactionMonth.getMonth()));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(transactionMonth.getAmount());
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(getTransactionMonthsTotalAmount(mTransactionMonths));
			c.setCellStyle(bottomNumberCs);

        } else {

			for (TransactionYearBean transactionYear : mTransactionYears) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatYear(transactionYear.getYear()));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(transactionYear.getAmount());
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(getTransactionYearsTotalAmount(mTransactionYears));
			c.setCellStyle(bottomNumberCs);
        }

        mActionListener.onGenerateReportCompleted();

		String subject = title + Constant.SPACE_DASH_SPACE_STRING + mNavigationTitle.getText().toString();

        // Create a path where we will place our List of objects on external storage
		File file = CommonUtil.generateReportFile(subject, wb);

		//startActivityForResult(CommonUtil.getActionViewIntent(file), 1);
		startActivityForResult(CommonUtil.getActionSendIntent(file, subject), 1);
    }
}
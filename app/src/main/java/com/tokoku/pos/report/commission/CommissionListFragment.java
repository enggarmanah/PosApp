package com.tokoku.pos.report.commission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.model.CommissionMonthBean;
import com.tokoku.pos.model.CommissionYearBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PoiUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CommissionListFragment extends BaseFragment 
	implements CommissionMonthArrayAdapter.ItemActionListener,
		CommissionYearArrayAdapter.ItemActionListener {
	
	private ImageButton mBackButton;
	
	private TextView mNavigationTitle;
	private TextView mNavText;
	
	private ListView mCommisionList;
	
	private List<CommissionYearBean> mCommisionYears;
	private List<CommissionMonthBean> mCommisionMonths;

    private CommissionYearBean mTransactionYear;
    private CommissionMonthBean mTransactionMonth;
	
	private Employee mSelectedEmployee;
	private CommissionYearBean mSelectedCommisionYear;
	private CommissionMonthBean mSelectedCommisionMonth;
	
	private CommissionYearArrayAdapter mCommisionYearAdapter;
	private CommissionMonthArrayAdapter mCommisionMonthAdapter;
	
	private CommissionActionListener mActionListener;
	
	private ProductDaoService mProductDaoService = new ProductDaoService();
	
	private String mStatus;
	
	public CommissionListFragment() {
		
		initList();
	}
	
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
		
		View view = inflater.inflate(R.layout.report_transaction_list_fragment, container, false);
		
		mCommisionYearAdapter = new CommissionYearArrayAdapter(getActivity(), mCommisionYears, this);
		mCommisionMonthAdapter = new CommissionMonthArrayAdapter(getActivity(), mCommisionMonths, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mNavigationTitle = (TextView) getView().findViewById(R.id.navigationTitle);
		mNavText = (TextView) getView().findViewById(R.id.navText);
		
		mCommisionList = (ListView) getActivity().findViewById(R.id.transactionList);
		
		mCommisionList.setItemsCanFocus(true);
		mCommisionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		if (mSelectedCommisionMonth != null) {
			
			displayCommisionOnYear(mSelectedCommisionYear);
			displayCommisionOnMonth(mSelectedCommisionMonth);
		}
		if (mSelectedCommisionYear != null) {
			
			displayCommisionOnYear(mSelectedCommisionYear);
			
		} else {
			displayCommisionAllYears();
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CommissionActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CommisionActionListener");
        }
    }
	
	private void initList() {
		
		if (mCommisionYears == null) {
			mCommisionYears = new ArrayList<CommissionYearBean>();
		}
		
		if (mCommisionMonths == null) {
			mCommisionMonths = new ArrayList<CommissionMonthBean>();
		}
	}
	
	public void updateContent() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		if (CommissionActivity.DISPLAY_TRANSACTION_ALL_YEARS.equals(mStatus)) {
			displayCommisionAllYears();
			
		} else if (CommissionActivity.DISPLAY_TRANSACTION_ON_YEAR.equals(mStatus)) {
			displayCommisionOnYear(mSelectedCommisionYear);
			
		} else if (CommissionActivity.DISPLAY_TRANSACTION_ON_MONTH.equals(mStatus)) {
			displayCommisionOnMonth(mSelectedCommisionMonth);
		}
	}
	
	public void setSelectedEmployee(Employee employee) {
		
		mSelectedEmployee = employee;
	}
	
	public void setSelectedCommisionYear(CommissionYearBean transactionYear) {
		
		mSelectedCommisionYear = transactionYear;
		
		if (transactionYear == null) {
			return;
		}
		
		mStatus = CommissionActivity.DISPLAY_TRANSACTION_ON_YEAR;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void setSelectedCommisionMonth(CommissionMonthBean transactionMonth) {
		
		mSelectedCommisionMonth = transactionMonth;
		
		if (transactionMonth == null) {
			return;
		}
		
		mStatus = CommissionActivity.DISPLAY_TRANSACTION_ON_MONTH;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void refreshCommisionYear() {
		
		displayCommisionOnYear(mSelectedCommisionYear);
	} 
	
	private long getCommisionYearsTotalAmount(List<CommissionYearBean> transactionYears) {
		
		long totalAmount = 0;
		
		for (CommissionYearBean transactionYear : transactionYears) {
			totalAmount += transactionYear.getAmount();
		}
		
		return totalAmount;
	}
	
	private long getCommisionMonthsTotalAmount(List<CommissionMonthBean> transactionMonths) {
		
		long totalAmount = 0;
		
		for (CommissionMonthBean transactionMonth : transactionMonths) {
			totalAmount += transactionMonth.getAmount();
		}
		
		return totalAmount;
	}
	
	public void displayCommisionAllYears() {
		
		mStatus = CommissionActivity.DISPLAY_TRANSACTION_ALL_YEARS;
		
		mCommisionYears.clear();
		
		mCommisionYears.addAll(mProductDaoService.getCommisionYears(mSelectedEmployee));
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(false);
		
		mNavigationTitle.setText(getString(R.string.total));
		
		mNavText.setText(CommonUtil.formatCurrency(getCommisionYearsTotalAmount(mCommisionYears)));
		
		mCommisionList.setAdapter(mCommisionYearAdapter);
	}
	
	public void displayCommisionOnYear(CommissionYearBean transactionYear) {

        mTransactionYear = transactionYear;
		
		mStatus = CommissionActivity.DISPLAY_TRANSACTION_ON_YEAR;
		
		mCommisionMonths.clear();
		
		mCommisionMonths.addAll(mProductDaoService.getCommisionMonths(transactionYear, mSelectedEmployee));
		
		if (!isViewInitialized()) {
			return;
		}
		
		setBackButtonVisible(true);
		
		mNavigationTitle.setText(getString(R.string.report_year, CommonUtil.formatYear(transactionYear.getYear())));
		
		mNavText.setText(CommonUtil.formatCurrency(getCommisionMonthsTotalAmount(mCommisionMonths)));
		
		mCommisionList.setAdapter(mCommisionMonthAdapter);
	}
	
	public void displayCommisionOnMonth(CommissionMonthBean transactionMonth) {

         mTransactionMonth = transactionMonth;
		
		mStatus = CommissionActivity.DISPLAY_TRANSACTION_ON_MONTH;
		
		mCommisionList.setAdapter(mCommisionMonthAdapter);
		mCommisionMonthAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCommisionYearSelected(CommissionYearBean transactionYear) {
		
		mActionListener.onCommissionYearSelected(transactionYear);
	}
	
	@Override
	public CommissionYearBean getSelectedCommisionYear() {
		
		return mSelectedCommisionYear;
	}
	
	@Override
	public void onCommisionMonthSelected(CommissionMonthBean transactionMonth) {
		
		mActionListener.onCommissionMonthSelected(transactionMonth);
	}
	
	@Override
	public CommissionMonthBean getSelectedCommisionMonth() {
		
		return mSelectedCommisionMonth;
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
		Workbook wb = PoiUtil.getWorkbook();

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

        if (CommissionActivity.DISPLAY_TRANSACTION_ALL_YEARS.equals(mStatus)) {

			for (CommissionYearBean commisionYear : mCommisionYears) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatYear(commisionYear.getYear()));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(CommonUtil.formatCurrency(commisionYear.getAmount()));
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(CommonUtil.formatCurrency(getCommisionYearsTotalAmount(mCommisionYears)));
			c.setCellStyle(bottomNumberCs);

		} else if (CommissionActivity.DISPLAY_TRANSACTION_ON_YEAR.equals(mStatus) ||
                CommissionActivity.DISPLAY_TRANSACTION_ON_MONTH.equals(mStatus)) {

			for (CommissionMonthBean commisionMonth : mCommisionMonths) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatMonth(commisionMonth.getMonth()));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(CommonUtil.formatCurrency(commisionMonth.getAmount()));
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(CommonUtil.formatCurrency(getCommisionMonthsTotalAmount(mCommisionMonths)));
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
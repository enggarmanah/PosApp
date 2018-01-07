package com.tokoku.pos.report.commission;

import java.io.File;
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

import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.model.CommissionMonthBean;
import com.tokoku.pos.model.EmployeeCommissionBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PoiUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CommissionDetailFragment extends BaseFragment implements CommissionDetailArrayAdapter.ItemActionListener {
	
	private ImageButton mBackButton;
	private TextView mNavigationTitle;
	
	private TextView mInfoText;
	private TextView mDateText;
	private TextView mTotalItemText;
	private TextView mTotalCommissionText;
	
	protected ListView mEmployeeCommisionList;

	protected CommissionMonthBean mCommisionMonth;
	protected Employee mEmployee;
	
	protected List<EmployeeCommissionBean> mEmployeeCommisions;
	protected List<EmployeeCommissionBean> mEmployeeCommisionDetails;
	
	private CommissionActionListener mActionListener;
	
	private CommissionDetailArrayAdapter mCommisionAdapter;
	private CommissionDetailEmployeeArrayAdapter mCommisionDetailAdapter;
	
	private ProductDaoService mProductDaoService = new ProductDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_commision_detail_fragment, container, false);
		
		if (mEmployeeCommisions == null) {
			mEmployeeCommisions = new ArrayList<EmployeeCommissionBean>();
		}
		
		if (mEmployeeCommisionDetails == null) {
			mEmployeeCommisionDetails = new ArrayList<EmployeeCommissionBean>();
		}
		
		mCommisionAdapter = new CommissionDetailArrayAdapter(getActivity(), mEmployeeCommisions, this);
		mCommisionDetailAdapter = new CommissionDetailEmployeeArrayAdapter(getActivity(), mEmployeeCommisionDetails);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mEmployeeCommisionList = (ListView) getView().findViewById(R.id.employeeCommisionList);

		mEmployeeCommisionList.setAdapter(mCommisionAdapter);
		mEmployeeCommisionList.setItemsCanFocus(true);
		mEmployeeCommisionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mDateText = (TextView) getView().findViewById(R.id.dateText);
		mInfoText = (TextView) getView().findViewById(R.id.infoText);
		mTotalItemText = (TextView) getView().findViewById(R.id.totalItemText);
		mTotalCommissionText = (TextView) getView().findViewById(R.id.totalCommissionText);
	}
	
	private void initBackButton() {
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane && mEmployee == null) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void onStart() {
		super.onStart();

		mNavigationTitle = (TextView) getView().findViewById(R.id.infoText);
		
		initViewVariables();
		updateContent();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CommissionActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	@Override
	public void onEmployeeSelected(Employee employee) {
		
		mEmployee = employee;
		
		mActionListener.onEmployeeSelected(employee);
		
		updateContent();
	}
	
	public void setEmployee(Employee employee) {
		
		mEmployee = employee;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void setCommisionMonth(CommissionMonthBean transactionMonth) {
		
		mCommisionMonth = transactionMonth;
		mEmployee = null;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (mEmployee != null) {
			
			mEmployeeCommisionDetails.clear();
			
			mEmployeeCommisionDetails.addAll(mProductDaoService.getEmployeeCommisions(mCommisionMonth, mEmployee));
			mInfoText.setText(mEmployee.getName());
			
			mCommisionDetailAdapter.notifyDataSetChanged();
			
			mEmployeeCommisionList.setAdapter(mCommisionDetailAdapter);
			mTotalItemText.setText(Constant.EMPTY_STRING);
			mTotalCommissionText.setText(CommonUtil.formatCurrency(getEmployeeCommissionTotal(mEmployeeCommisionDetails)));
			
		} else if (mCommisionMonth != null) {
			
			mEmployeeCommisions.clear();
			
			mEmployeeCommisions.addAll(mProductDaoService.getEmployeeCommisions(mCommisionMonth));
			mInfoText.setText(getString(R.string.report_commision));
			
			mCommisionAdapter.notifyDataSetChanged();
			
			mEmployeeCommisionList.setAdapter(mCommisionAdapter);
            mTotalItemText.setText(Constant.EMPTY_STRING);
			mTotalCommissionText.setText(CommonUtil.formatCurrency(getEmployeeCommissionTotal(mEmployeeCommisions)));
			
			getView().setVisibility(View.VISIBLE);
		
		} else {
			
			if (getView() != null) {
				getView().setVisibility(View.INVISIBLE);
			}
			return;
		}
		
		mDateText.setText(CommonUtil.formatMonth(mCommisionMonth.getMonth()));
		
		mCommisionAdapter.notifyDataSetChanged();
		
		initBackButton();
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackPressed();
			}
		};
	}
	
	private Double getEmployeeCommissionTotal(List<EmployeeCommissionBean> commisions) {
		
		Double total = Double.valueOf(0);
		
		for (EmployeeCommissionBean commision : commisions) {
			total += commision.getCommision();
		}
		
		return total;
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
		sheet.setColumnWidth(1, (15 * 500));

		if (mEmployee != null) {

			c = row.createCell(2);
			c.setCellValue(Constant.EMPTY_STRING);
			c.setCellStyle(headerCs);

			sheet.setColumnWidth(2, (12 * 500));

			for (EmployeeCommissionBean employeeCommission : mEmployeeCommisionDetails) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(CommonUtil.formatDateMonthTime(employeeCommission.getTransaction_date()));
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(employeeCommission.getQuantity() + Constant.SPACE_STRING + employeeCommission.getProduct_name());
				c.setCellStyle(contentCs);

				c = row.createCell(2);
				c.setCellValue(CommonUtil.formatCurrency(employeeCommission.getCommision()));
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellStyle(bottomCs);

			c = row.createCell(2);
			c.setCellValue(CommonUtil.formatCurrency(getEmployeeCommissionTotal(mEmployeeCommisionDetails)));
			c.setCellStyle(bottomNumberCs);

		} else if (mCommisionMonth != null) {

			for (EmployeeCommissionBean employeeCommission : mEmployeeCommisions) {

				row = sheet.createRow(index++);

				c = row.createCell(0);
				c.setCellValue(employeeCommission.getEmployee_name());
				c.setCellStyle(contentCs);

				c = row.createCell(1);
				c.setCellValue(CommonUtil.formatCurrency(employeeCommission.getCommision()));
				c.setCellStyle(contentNumberCs);
			}

			row = sheet.createRow(index++);

			c = row.createCell(0);
			c.setCellValue(getString(R.string.total));
			c.setCellStyle(bottomCs);

			c = row.createCell(1);
			c.setCellValue(CommonUtil.formatCurrency(getEmployeeCommissionTotal(mEmployeeCommisions)));
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
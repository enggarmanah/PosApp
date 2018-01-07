package com.tokoku.pos.report.cashflow;

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

import com.android.pos.dao.Bills;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.dao.CustomerDaoService;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.CashFlowMonthBean;
import com.tokoku.pos.model.CashflowBean;
import com.tokoku.pos.model.ProductStatisticBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PoiUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CashFlowDetailFragment extends BaseFragment 
	implements CashFlowDetailArrayAdapter.ItemActionListener {

	BillsDaoService billDaoService = new BillsDaoService();
	TransactionsDaoService transactionDaoService = new TransactionsDaoService();
	SupplierDaoService supplierDaoService = new SupplierDaoService();
	CustomerDaoService customerDaoService = new CustomerDaoService();
	
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
		c.setCellValue(getActivity().getString(R.string.field_date));
		c.setCellStyle(headerCs);

		c = row.createCell(1);
		c.setCellValue(getActivity().getString(R.string.field_type));
		c.setCellStyle(headerCs);

		c = row.createCell(2);
		c.setCellValue(getActivity().getString(R.string.field_remarks));
		c.setCellStyle(headerCs);

		c = row.createCell(3);
		c.setCellValue(getActivity().getString(R.string.report_debit));
		c.setCellStyle(headerCs);

		c = row.createCell(4);
		c.setCellValue(getActivity().getString(R.string.report_credit));
		c.setCellStyle(headerCs);

		sheet.setColumnWidth(0, (8 * 500));
		sheet.setColumnWidth(1, (12 * 500));
		sheet.setColumnWidth(2, (16 * 500));
		sheet.setColumnWidth(3, (8 * 500));
		sheet.setColumnWidth(4, (8 * 500));

		Double totalDebit = 0d;
		Double totalCredit = 0d;

		for (CashflowBean cashflow : mCashflows) {

			row = sheet.createRow(index++);

			String date = CommonUtil.formatDate(cashflow.getCash_date());

			String type = Constant.EMPTY_STRING;

			if (cashflow.getType() != null) {
				type = CodeUtil.getCashflowTypeLabel(cashflow.getType());
			} else {
				type = getActivity().getString(R.string.transaction_daily);
			}

			Double debit = 0d;
			Double credit = 0d;

			if (Constant.CASHFLOW_TYPE_CAPITAL_IN.equals(cashflow.getType()) ||
					Constant.CASHFLOW_TYPE_BANK_WITHDRAWAL.equals(cashflow.getType()) ||
					Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(cashflow.getType()) ||
					cashflow.getType() == null) {

				debit =  Math.abs(new Double(cashflow.getCash_amount()));
				totalDebit += debit;
			} else {
				credit =  Math.abs(new Double(cashflow.getCash_amount()));
				totalCredit += credit;
			}

			String billReferenceNo = null;
			String supplierName = null;

			if (cashflow.getBill_id() != null) {

				Bills bill = billDaoService.getBills(cashflow.getBill_id());

				billReferenceNo = bill.getBillReferenceNo();

				if (bill.getSupplierId() != null) {
					supplierName = supplierDaoService.getSupplier(bill.getSupplierId()).getName();
				}
			}

			String transactionNo = null;
			String customerName = null;

			if (cashflow.getTransaction_id() != null) {

				Transactions transaction = transactionDaoService.getTransactions(cashflow.getTransaction_id());

				transactionNo = transaction.getTransactionNo();
				customerName = customerDaoService.getCustomer(transaction.getCustomerId()).getName();
			}

			String remarks = Constant.EMPTY_STRING;

			if (!CommonUtil.isEmpty(billReferenceNo)) {
				remarks = billReferenceNo;

			} else if (!CommonUtil.isEmpty(transactionNo)) {
				remarks = transactionNo;
			}

			if (!CommonUtil.isEmpty(cashflow.getRemarks())) {

				if (!CommonUtil.isEmpty(remarks)) {
					remarks += ". ";
				}
				remarks += cashflow.getRemarks();
			}

			if (!CommonUtil.isEmpty(supplierName)) {
				remarks += " (" + supplierName + ")";

			} else if (!CommonUtil.isEmpty(customerName)){
				remarks += " (" + customerName + ")";
			}

			c = row.createCell(0);
			c.setCellValue(date);
			c.setCellStyle(contentCs);

			c = row.createCell(1);
			c.setCellValue(type);
			c.setCellStyle(contentCs);

			c = row.createCell(2);
			c.setCellValue(remarks);
			c.setCellStyle(contentCs);

			c = row.createCell(3);
			c.setCellValue(debit);
			c.setCellStyle(contentNumberCs);

			c = row.createCell(4);
			c.setCellValue(credit);
			c.setCellStyle(contentNumberCs);
		}

		row = sheet.createRow(index++);

		c = row.createCell(0);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(1);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(2);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(3);
		c.setCellValue(totalDebit);
		c.setCellStyle(bottomNumberCs);

		c = row.createCell(4);
		c.setCellValue(totalCredit);
		c.setCellStyle(bottomNumberCs);

		row = sheet.createRow(index++);

		c = row.createCell(0);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(1);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(2);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(bottomCs);

		c = row.createCell(3);
		c.setCellValue(getString(R.string.total));
		c.setCellStyle(bottomCs);

		c = row.createCell(4);
		c.setCellValue(totalDebit-totalCredit);
		c.setCellStyle(bottomNumberCs);

		mActionListener.onGenerateReportCompleted();

        String subject = title + Constant.SPACE_DASH_SPACE_STRING + mDateText.getText().toString();

		// Create a path where we will place our List of objects on external storage
		File file = CommonUtil.generateReportFile(subject, wb);

		//startActivityForResult(CommonUtil.getActionViewIntent(file), 1);
		startActivityForResult(CommonUtil.getActionSendIntent(file, subject), 1);
	}
}
package com.tokoku.pos.report.product;

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

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.model.ProductStatisticBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PoiUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ProductStatisticDetailFragment extends BaseFragment implements ProductStatisticDetailArrayAdapter.ItemActionListener {
	
	private ImageButton mBackButton;
	
	private TextView mDateText;
	private TextView mProductInfoText;
	
	protected ListView mProductStatisticList;

	protected TransactionMonthBean mTransactionMonth;
	protected List<ProductStatisticBean> mProductStatistics;
	
	protected String mProductInfo = Constant.PRODUCT_REVENUE;
	
	private ProductStatisticActionListener mActionListener;
	
	private ProductStatisticDetailArrayAdapter mAdapter;
	
	private ProductDaoService mProductDaoService = new ProductDaoService();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.report_product_statistic_detail_fragment, container, false);
		
		if (mProductStatistics == null) {
			mProductStatistics = new ArrayList<ProductStatisticBean>();
		} 
		
		mAdapter = new ProductStatisticDetailArrayAdapter(getActivity(), mProductStatistics, this);
		
		return view;
	}
	
	private void initViewVariables() {
		
		mProductStatisticList = (ListView) getView().findViewById(R.id.productStatisticList);

		mProductStatisticList.setAdapter(mAdapter);
		mProductStatisticList.setItemsCanFocus(true);
		mProductStatisticList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mBackButton = (ImageButton) getView().findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
		
		mDateText = (TextView) getView().findViewById(R.id.dateText);
		mProductInfoText = (TextView) getView().findViewById(R.id.productInfoText);
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
            mActionListener = (ProductStatisticActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	public void setProductInfo(String productInfo) {
		
		mProductInfo = productInfo;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public String getProductInfo() {
		
		return mProductInfo;
	}
	
	public void setTransactionMonth(TransactionMonthBean transactionMonth) {
		
		mTransactionMonth = transactionMonth;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	public void updateContent() {
		
		if (mTransactionMonth != null) {
			
			mProductStatistics.clear();
			
			if (Constant.PRODUCT_QUANTITY.equals(mProductInfo)) {
				mProductStatistics.addAll(mProductDaoService.getProductStatisticsQuantity(mTransactionMonth));
				mProductInfoText.setText(getString(R.string.report_sale_count));
				
			} else if (Constant.PRODUCT_REVENUE.equals(mProductInfo)) {
				mProductStatistics.addAll(mProductDaoService.getProductStatisticsRevenue(mTransactionMonth));
				mProductInfoText.setText(getString(R.string.report_sale_income));
			
			} else if (Constant.PRODUCT_PROFIT.equals(mProductInfo)) {
				mProductStatistics.addAll(mProductDaoService.getProductStatisticsProfit(mTransactionMonth));
				mProductInfoText.setText(getString(R.string.report_sale_profit));
			}
			
			mAdapter.notifyDataSetChanged();
			
			getView().setVisibility(View.VISIBLE);
		
		} else {
			
			if (getView() != null) {
				getView().setVisibility(View.INVISIBLE);
			}
			
			return;
		}
		
		mDateText.setText(CommonUtil.formatMonth(mTransactionMonth.getMonth()));
		
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

	private Double getTotalFromProductStatistic(List<ProductStatisticBean> productStatistics) {

        Double total = 0d;

        for (ProductStatisticBean productStatistic : productStatistics) {
            total += productStatistic.getValue();
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
		c.setCellValue(mProductInfoText.getText().toString());
		c.setCellStyle(headerCs);

		c = row.createCell(1);
		c.setCellValue(Constant.EMPTY_STRING);
		c.setCellStyle(headerCs);

		sheet.setColumnWidth(0, (12 * 500));
		sheet.setColumnWidth(1, (8 * 500));

        for (ProductStatisticBean productStatistic : mProductStatistics) {

            row = sheet.createRow(index++);

            c = row.createCell(0);
            c.setCellValue(productStatistic.getProduct_name());
            c.setCellStyle(contentCs);

            c = row.createCell(1);
            c.setCellStyle(contentNumberCs);
            c.setCellValue(productStatistic.getValue());
        }

        row = sheet.createRow(index++);

        c = row.createCell(0);
        c.setCellValue(getString(R.string.total));
        c.setCellStyle(bottomCs);

        c = row.createCell(1);
        c.setCellValue(getTotalFromProductStatistic(mProductStatistics));
        c.setCellStyle(bottomNumberCs);

		mActionListener.onGenerateReportCompleted();

		String subject = title + Constant.SPACE_DASH_SPACE_STRING + mProductInfoText.getText().toString() + Constant.SPACE_STRING +  mDateText.getText().toString();

		// Create a path where we will place our List of objects on external storage
		File file = CommonUtil.generateReportFile(subject, wb);

		//startActivityForResult(CommonUtil.getActionViewIntent(file), 1);
		startActivityForResult(CommonUtil.getActionSendIntent(file, subject), 1);
	}
}
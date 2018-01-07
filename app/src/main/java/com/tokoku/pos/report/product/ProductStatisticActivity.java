package com.tokoku.pos.report.product;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProductStatisticActivity extends BaseActivity 
	implements ProductStatisticActionListener {
	
	protected ProductStatisticListFragment mProductStatisticListFragment;
	protected ProductStatisticDetailFragment mProductStatisticDetailFragment;
	
	boolean mIsMultiplesPane = false;
	
	private TransactionYearBean mSelectedTransactionYear;
	private TransactionMonthBean mSelectedTransactionMonth;

	protected static String mGenerateReportProgressDialogTag = "generateReportProgressDialogTag";
	
	private String mSelectedProductInfo = Constant.PRODUCT_QUANTITY;
	
	private static String SELECTED_TRANSACTION_YEAR = "SELECTED_TRANSACTION_YEAR";
	private static String SELECTED_TRANSACTION_MONTH = "SELECTED_TRANSACTION_MONTH";
	private static String SELECTED_PRODUCT_INFO = "SELECTED_PRODUCT_INFO";
	
	public static final String DISPLAY_TRANSACTION_ALL_YEARS = "DISPLAY_TRANSACTION_ALL_YEARS";
	public static final String DISPLAY_TRANSACTION_ON_YEAR = "DISPLAY_TRANSACTION_ON_YEAR";
	public static final String DISPLAY_TRANSACTION_ON_MONTH = "DISPLAY_TRANSACTION_ON_MONTH";
	
	private String mProductStatisticListFragmentTag = "productStatisticListFragmentTag";
	private String mProductStatisticDetailFragmentTag = "productStatisticDetailFragmentTag";
	
	private boolean mIsDisplayTransactionAllYears = false;
	private boolean mIsDisplayTransactionYear = false;
	private boolean mIsDisplayTransactionMonth = false;

	private MenuItem mMenuEmail;
	private MenuItem mMenuRevenue;
	private MenuItem mMenuProfit;
	private MenuItem mMenuQuantity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_transaction_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mProductStatisticListFragmentTag, mProductStatisticDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_report_product_statistic));
		setSelectedMenu(getString(R.string.menu_report_product_statistic));
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedProductInfo = (String) savedInstanceState.getSerializable(SELECTED_PRODUCT_INFO);
			
			mSelectedTransactionYear = (TransactionYearBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_YEAR);
			mSelectedTransactionMonth = (TransactionMonthBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_MONTH);
			
			mIsDisplayTransactionAllYears = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ALL_YEARS);
			mIsDisplayTransactionYear = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ON_YEAR);
			mIsDisplayTransactionMonth = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ON_MONTH);
		
		} else {
			
			mIsDisplayTransactionMonth = true;
			
			mSelectedTransactionMonth = new TransactionMonthBean();
			mSelectedTransactionMonth.setMonth(CommonUtil.getCurrentMonth());
			
			mSelectedTransactionYear = new TransactionYearBean();
			mSelectedTransactionYear.setYear(CommonUtil.getCurrentYear());
		} 
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mProductStatisticListFragment = (ProductStatisticListFragment) getFragmentManager().findFragmentByTag(mProductStatisticListFragmentTag);
		
		if (mProductStatisticListFragment == null) {
			mProductStatisticListFragment = new ProductStatisticListFragment();

		} else {
			removeFragment(mProductStatisticListFragment);
		}
		
		mProductStatisticDetailFragment = (ProductStatisticDetailFragment) getFragmentManager().findFragmentByTag(mProductStatisticDetailFragmentTag);
		
		if (mProductStatisticDetailFragment == null) {
			mProductStatisticDetailFragment = new ProductStatisticDetailFragment();

		} else {
			removeFragment(mProductStatisticDetailFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_PRODUCT_INFO, (Serializable) mSelectedProductInfo);
		
		outState.putSerializable(SELECTED_TRANSACTION_YEAR, (Serializable) mSelectedTransactionYear);
		outState.putSerializable(SELECTED_TRANSACTION_MONTH, (Serializable) mSelectedTransactionMonth);
		
		outState.putSerializable(DISPLAY_TRANSACTION_ALL_YEARS, (Serializable) mIsDisplayTransactionAllYears);
		outState.putSerializable(DISPLAY_TRANSACTION_ON_YEAR, (Serializable) mIsDisplayTransactionYear);
		outState.putSerializable(DISPLAY_TRANSACTION_ON_MONTH, (Serializable) mIsDisplayTransactionMonth);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mProductStatisticListFragment.setSelectedProductInfo(mSelectedProductInfo);
		mProductStatisticListFragment.setSelectedTransactionYear(mSelectedTransactionYear);
		mProductStatisticListFragment.setSelectedTransactionMonth(mSelectedTransactionMonth);
		
		mProductStatisticDetailFragment.setProductInfo(mSelectedProductInfo);
		mProductStatisticDetailFragment.setTransactionMonth(mSelectedTransactionMonth);
		
		if (mIsMultiplesPane) {

			addFragment(mProductStatisticListFragment, mProductStatisticListFragmentTag);
			addFragment(mProductStatisticDetailFragment, mProductStatisticDetailFragmentTag);
			
		} else {

			if (mSelectedTransactionMonth != null) {
				
				addFragment(mProductStatisticDetailFragment, mProductStatisticDetailFragmentTag);
				
			} else {
				
				addFragment(mProductStatisticListFragment, mProductStatisticListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_product_menu, menu);

		mMenuEmail = menu.findItem(R.id.menu_item_email);
		mMenuRevenue = menu.findItem(R.id.menu_item_revenue);
		mMenuProfit = menu.findItem(R.id.menu_item_profit);
		mMenuQuantity = menu.findItem(R.id.menu_item_quantity);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		initMenus();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void initMenus() {
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		mMenuEmail.setVisible(false);

		if (!isDrawerOpen) {

			if (MerchantUtil.hasActiveCloud() && mIsDisplayTransactionMonth) {
				mMenuEmail.setVisible(true);
			}
		
			mMenuRevenue.setVisible(true);
			mMenuProfit.setVisible(true);
			mMenuQuantity.setVisible(true);
			
			if (Constant.PRODUCT_REVENUE.equals(mSelectedProductInfo)) {
				mMenuRevenue.setVisible(false);
				
			} else if (Constant.PRODUCT_PROFIT.equals(mSelectedProductInfo)) {
				mMenuProfit.setVisible(false);
			
			} else if (Constant.PRODUCT_QUANTITY.equals(mSelectedProductInfo)) {
				mMenuQuantity.setVisible(false);
			}
		} else {
			
			mMenuRevenue.setVisible(false);
			mMenuProfit.setVisible(false);
			mMenuQuantity.setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		synchronized (CommonUtil.LOCK) {
		
			switch (item.getItemId()) {

				case R.id.menu_item_email:

					mProductStatisticDetailFragment.generateReport(getString(R.string.menu_report_product_statistic));

					return true;

				case R.id.menu_item_revenue:
					
					mSelectedProductInfo = Constant.PRODUCT_REVENUE;
					
					mProductStatisticListFragment.setSelectedProductInfo(mSelectedProductInfo);
					mProductStatisticDetailFragment.setProductInfo(mSelectedProductInfo);
					
					refreshParentView();
					initMenus();
					
					return true;
				
				case R.id.menu_item_profit:
					
					mSelectedProductInfo = Constant.PRODUCT_PROFIT;
					
					mProductStatisticListFragment.setSelectedProductInfo(mSelectedProductInfo);
					mProductStatisticDetailFragment.setProductInfo(mSelectedProductInfo);
					
					refreshParentView();
					initMenus();
					
					return true;
				
				case R.id.menu_item_quantity:
					
					mSelectedProductInfo = Constant.PRODUCT_QUANTITY;
					
					mProductStatisticListFragment.setSelectedProductInfo(mSelectedProductInfo);
					mProductStatisticDetailFragment.setProductInfo(mSelectedProductInfo);
					
					refreshParentView();
					initMenus();
					
					return true;
					
				default:
					return super.onOptionsItemSelected(item);
			}
		}
	}
	
	@Override
	public void onTransactionYearSelected(TransactionYearBean transactionYear) {
		
		mSelectedTransactionYear = transactionYear;
		
		resetDisplayStatus();
		mIsDisplayTransactionYear = true;

        initMenus();

		mProductStatisticListFragment.setSelectedTransactionYear(transactionYear);
	}
	
	@Override
	public void onTransactionMonthSelected(TransactionMonthBean transactionMonth) {
		
		mSelectedTransactionMonth = transactionMonth;
		
		resetDisplayStatus();
		mIsDisplayTransactionMonth = true;

        initMenus();

		if (mIsMultiplesPane) {
			
			mProductStatisticListFragment.setSelectedTransactionMonth(transactionMonth);
			mProductStatisticDetailFragment.setTransactionMonth(transactionMonth);
			
		} else {

            replaceFragment(mProductStatisticDetailFragment, mProductStatisticDetailFragmentTag);
            mProductStatisticDetailFragment.setTransactionMonth(transactionMonth);
        }
	}
	
	private void refreshParentView() {
		
		if (mIsMultiplesPane && mIsDisplayTransactionMonth) {
			
			mProductStatisticListFragment.setSelectedTransactionYear(mSelectedTransactionYear);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			onBackToParent();
		}
	}
	
	private void onBackToParent() {
		
		setDisplayStatusToParent();
		
		mProductStatisticListFragment.setSelectedTransactionYear(mSelectedTransactionYear);
		mProductStatisticListFragment.setSelectedTransactionMonth(mSelectedTransactionMonth);
		
		mProductStatisticDetailFragment.setProductInfo(mSelectedProductInfo);
		mProductStatisticDetailFragment.setTransactionMonth(mSelectedTransactionMonth);
		
		if (mIsMultiplesPane) {
			
			initFragment();
			
		} else {
			
			replaceFragment(mProductStatisticListFragment, mProductStatisticListFragmentTag);
			initFragment();
		}

		initMenus();
	}
	
	private void initFragment() {
		
		if (mIsDisplayTransactionAllYears) {
			
			mProductStatisticListFragment.displayTransactionAllYears();
			
		} else if (mIsDisplayTransactionYear) {
			
			mProductStatisticListFragment.displayTransactionOnYear(mSelectedTransactionYear);
		}
	}
	
	private void resetDisplayStatus() {
		
		mIsDisplayTransactionAllYears = false;
		mIsDisplayTransactionYear = false;
		mIsDisplayTransactionMonth = false;
	}
	
	private void setDisplayStatusToParent() {
		
		if (mIsDisplayTransactionYear) {
			
			mIsDisplayTransactionYear = false;
			mSelectedTransactionYear = null;
			
			mIsDisplayTransactionAllYears = true;
			
		} else if (mIsDisplayTransactionMonth) {
			
			mIsDisplayTransactionMonth = false;
			mSelectedTransactionMonth = null;
			
			if (mIsMultiplesPane) {
				mIsDisplayTransactionAllYears = true;
			} else {
				mIsDisplayTransactionYear = true;
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mProductStatisticListFragment.updateContent();
		mProductStatisticDetailFragment.updateContent();
	}

	@Override
	public void onGenerateReportStart() {

		mProgressDialog.show(getFragmentManager(), mGenerateReportProgressDialogTag);
		mProgressDialog.setMessage(getString(R.string.msg_generate_report));
	}

	@Override
	public void onGenerateReportCompleted() {

		mProgressDialog.dismissAllowingStateLoss();
	}
}
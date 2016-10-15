package com.tokoku.pos.report.bills;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

public class BillsActivity extends BaseActivity 
	implements BillsActionListener, SearchView.OnQueryTextListener {
	
	private SearchView searchView;
	
	private MenuItem mSearchMenu;
	private MenuItem mListMenu;
	private MenuItem mAlertMenu;
	
	private TextView mAlertMenuText;
	private ImageButton mAlertMenuBtn;
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private BillsListFragment mBillsReportListFragment;
	private BillsDetailFragment mBillsReportDetailFragment;
	
	boolean mIsMultiplesPane = false;
	
	private Bills mSelectedBill;
	
	private static String SELECTED_BILL = "SELECTED_BILL";
	
	private final String mBillsReportListFragmentTag = "billsReportListFragmentTag";
	private final String mBillsReportDetailFragmentTag = "billsReportDetailFragmentTag";
	
	private Integer mPastDueBillsCount = 0;
	
	private boolean mIsShowAllBills = false;
	private boolean mIsShowPastDueBills = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_inventory_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mBillsReportListFragmentTag, mBillsReportDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_report_bills));
		setSelectedMenu(getString(R.string.menu_report_bills));
		
		updatePastDueBills();
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedBill = (Bills) savedInstanceState.getSerializable(SELECTED_BILL);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mBillsReportListFragment = (BillsListFragment) getFragmentManager().findFragmentByTag(mBillsReportListFragmentTag);
		
		if (mBillsReportListFragment == null) {
			mBillsReportListFragment = new BillsListFragment();

		} else {
			removeFragment(mBillsReportListFragment);
		}
		
		mBillsReportDetailFragment = (BillsDetailFragment) getFragmentManager().findFragmentByTag(mBillsReportDetailFragmentTag);
		
		if (mBillsReportDetailFragment == null) {
			mBillsReportDetailFragment = new BillsDetailFragment();

		} else {
			removeFragment(mBillsReportDetailFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_BILL, (Serializable) mSelectedBill);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mBillsReportListFragment.setSelectedBill(mSelectedBill);
		mBillsReportDetailFragment.setBill(mSelectedBill);
		
		if (mIsMultiplesPane) {

			addFragment(mBillsReportListFragment, mBillsReportListFragmentTag);
			addFragment(mBillsReportDetailFragment, mBillsReportDetailFragmentTag);
			
		} else {

			if (mSelectedBill != null) {
				
				addFragment(mBillsReportDetailFragment, mBillsReportDetailFragmentTag);
				
			} else {
				
				addFragment(mBillsReportListFragment, mBillsReportListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_inventory_menu, menu);
		
		mSearchMenu = menu.findItem(R.id.menu_item_search);
		mListMenu = menu.findItem(R.id.menu_item_list);
		mAlertMenu = menu.findItem(R.id.menu_item_alert);
		
		mAlertMenuText = (TextView) menu.findItem(R.id.menu_item_alert).getActionView().findViewById(R.id.menu_item_alert_text);
		mAlertMenuBtn = (ImageButton) menu.findItem(R.id.menu_item_alert).getActionView().findViewById(R.id.menu_item_alert_icon);
		
		String alertText = "0";
		
		if (mPastDueBillsCount > 99) {
			alertText = "++";
		} else if (mPastDueBillsCount > 0) {
			alertText = CommonUtil.formatNumber(mPastDueBillsCount);
		}
		
		mAlertMenuText.setText(alertText);
		mAlertMenuBtn.setOnClickListener(getMenuAlertOnClickListener());
		
		hideListAndAlertMenu();
		
		if (mPastDueBillsCount != 0 &&
			mIsShowAllBills) {
			
			mAlertMenu.setVisible(true);
		}
		
		searchView = (SearchView) mSearchMenu.getActionView();
		searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.START));

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (null != searchManager) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		hideSelectedMenu();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	private View.OnClickListener getMenuAlertOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mIsShowAllBills = false;
				mIsShowPastDueBills = true;
				
				mListMenu.setVisible(true);
				mAlertMenu.setVisible(false);
				
				mBillsReportListFragment.showPastDueBills();
				mSearchMenu.collapseActionView();
				
				showInventoryList();
			}
		};
	}
	
	private void hideListAndAlertMenu() {
		
		if (mListMenu != null) {
			mListMenu.setVisible(false);
		}
		
		if (mAlertMenu != null) {
			mAlertMenu.setVisible(false);
		}
	}
	
	private void hideSelectedMenu() {
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		mSearchMenu.setVisible(!isDrawerOpen);
		
		if (mIsShowAllBills) {
			mAlertMenu.setVisible(!isDrawerOpen);
		}
		
		if (mIsShowPastDueBills) {
			mListMenu.setVisible(!isDrawerOpen);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
			case R.id.menu_item_list:
				
				showInventoryList();
				
				if (mPastDueBillsCount != 0) {
				
					mIsShowAllBills = true;
					mIsShowPastDueBills = false;
					
					mListMenu.setVisible(false);
					mAlertMenu.setVisible(true);
					
					mBillsReportListFragment.showAllBills();
				}
				
				return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBillSelected(Bills bill) {
		
		mSelectedBill = bill;
		
		mBillsReportListFragment.setSelectedBill(bill);
		mBillsReportDetailFragment.setBill(mSelectedBill);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mBillsReportDetailFragment, mBillsReportDetailFragmentTag);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			
			showInventoryList();
		}
	}
	
	private void showInventoryList() {
		
		mSelectedBill = null;
		
		mBillsReportListFragment.setSelectedBill(mSelectedBill);
		mBillsReportDetailFragment.setBill(mSelectedBill);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mBillsReportListFragment, mBillsReportListFragmentTag);
		}
	}
	
	@Override
	public boolean onQueryTextChange(String query) {
		
		boolean isQuerySimilar = query.equals(prevQuery);
		
		prevQuery = query;
		
		if (!isQuerySimilar) {
			
			if (mIsMultiplesPane) {
				
				doSearch(query);
				
			} else {
				
				replaceFragment(mBillsReportListFragment, mBillsReportListFragmentTag);
				doSearch(query);
			}
		}

		return true;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {

		doSearch(query);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

		return true;
	}
	
	private void doSearch(String query) {
		
		mBillsReportListFragment.searchBills(query);
	}
	
	@Override
	public void onBillUnselected() {
		
		mSelectedBill = null;

		mBillsReportListFragment.setSelectedBill(mSelectedBill);
		mBillsReportDetailFragment.setBill(mSelectedBill);
	}
	
	private void updatePastDueBills() {
		
		MerchantUtil.refreshPastDueBillsCount();
		
		mPastDueBillsCount = MerchantUtil.getPastDueBillsCount();
		
		if (mAlertMenuText != null) {
			mAlertMenuText.setText(CommonUtil.formatNumber(mPastDueBillsCount));
		}
		
		mIsShowAllBills = false;
		mIsShowPastDueBills = false;
		
		if (mPastDueBillsCount == 0) {
			hideListAndAlertMenu();
			
		} else {
			
			mIsShowAllBills = true;
			
			hideListAndAlertMenu();
			
			if (mAlertMenu != null) {
				mAlertMenu.setVisible(true);
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mBillsReportListFragment.searchBills(prevQuery);
		mBillsReportListFragment.updateContent();
		mBillsReportDetailFragment.updateContent();
	}
}
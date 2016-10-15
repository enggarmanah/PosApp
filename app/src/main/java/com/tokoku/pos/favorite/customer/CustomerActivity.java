package com.tokoku.pos.favorite.customer;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.model.CustomerStatisticBean;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class CustomerActivity extends BaseActivity 
	implements CustomerActionListener, SearchView.OnQueryTextListener {
	
	private SearchView searchView;
	
	private MenuItem mSearchMenu;
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private CustomerListFragment mCustomerListFragment;
	private CustomerDetailFragment mCustomerDetailFragment;
	private CustomerTransactionDlgFragment mCustomerTransactionDlgFragment;
	
	boolean mIsMultiplesPane = false;
	
	private CustomerStatisticBean mSelectedCustomerStatistic;
	
	private static String SELECTED_CUSTOMER_STATISTIC = "SELECTED_CUSTOMER_STATISTIC";
	
	private final String mCustomerListFragmentTag = "customerListFragmentTag";
	private final String mCustomerDetailFragmentTag = "customerDetailFragmentTag";
	private final String mCustomerTransactionDlgFragmentTag = "mCustomerTransactionDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_inventory_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mCustomerListFragmentTag, mCustomerDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		if (Constant.MERCHANT_TYPE_MEDICAL_SERVICES.equals(MerchantUtil.getMerchantType())) {
			
			setTitle(getString(R.string.menu_favorite_patient));
			setSelectedMenu(getString(R.string.menu_favorite_patient));
			
		} else {
			setTitle(getString(R.string.menu_favorite_customer));
			setSelectedMenu(getString(R.string.menu_favorite_customer));
		}
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedCustomerStatistic = (CustomerStatisticBean) savedInstanceState.getSerializable(SELECTED_CUSTOMER_STATISTIC);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mCustomerListFragment = (CustomerListFragment) getFragmentManager().findFragmentByTag(mCustomerListFragmentTag);
		
		if (mCustomerListFragment == null) {
			mCustomerListFragment = new CustomerListFragment();

		} else {
			removeFragment(mCustomerListFragment);
		}
		
		mCustomerDetailFragment = (CustomerDetailFragment) getFragmentManager().findFragmentByTag(mCustomerDetailFragmentTag);
		
		if (mCustomerDetailFragment == null) {
			mCustomerDetailFragment = new CustomerDetailFragment();

		} else {
			removeFragment(mCustomerDetailFragment);
		}
		
		mCustomerTransactionDlgFragment = (CustomerTransactionDlgFragment) getFragmentManager().findFragmentByTag(mCustomerTransactionDlgFragmentTag);
		
		if (mCustomerTransactionDlgFragment == null) {
			mCustomerTransactionDlgFragment = new CustomerTransactionDlgFragment();

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_CUSTOMER_STATISTIC, (Serializable) mSelectedCustomerStatistic);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		Customer customer = mSelectedCustomerStatistic != null ? mSelectedCustomerStatistic.getCustomer() : null;
		
		mCustomerListFragment.setSelectedCustomerStatisticBean(mSelectedCustomerStatistic);
		mCustomerDetailFragment.setCustomer(customer);
		
		if (mIsMultiplesPane) {

			addFragment(mCustomerListFragment, mCustomerListFragmentTag);
			addFragment(mCustomerDetailFragment, mCustomerDetailFragmentTag);
			
		} else {

			if (mSelectedCustomerStatistic != null) {
				
				addFragment(mCustomerDetailFragment, mCustomerDetailFragmentTag);
				
			} else {
				
				addFragment(mCustomerListFragment, mCustomerListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favorite_customer_menu, menu);
		
		mSearchMenu = menu.findItem(R.id.menu_item_search);
		
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
	
	private void hideSelectedMenu() {
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		mSearchMenu.setVisible(!isDrawerOpen);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCustomerStatisticSelected(CustomerStatisticBean customerStatistic) {
		
		mSelectedCustomerStatistic = customerStatistic;
		
		mCustomerListFragment.setSelectedCustomerStatisticBean(customerStatistic);
		mCustomerDetailFragment.setCustomer(customerStatistic.getCustomer());
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mCustomerDetailFragment, mCustomerDetailFragmentTag);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			
			showCustomerStatistics();
		}
	}
	
	@Override
	public void showCustomerTransactions(Customer customer) {
		
		if (mCustomerTransactionDlgFragment.isAdded()) {
			return;
		}
		
		mCustomerTransactionDlgFragment.show(getFragmentManager(), mCustomerTransactionDlgFragmentTag);
		mCustomerTransactionDlgFragment.setCustomer(customer);
	}
	
	private void showCustomerStatistics() {
		
		mSelectedCustomerStatistic = null;
		
		Customer customer = mSelectedCustomerStatistic != null ? mSelectedCustomerStatistic.getCustomer() : null;
		
		mCustomerListFragment.setSelectedCustomerStatisticBean(mSelectedCustomerStatistic);
		mCustomerDetailFragment.setCustomer(customer);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mCustomerListFragment, mCustomerListFragmentTag);
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
				
				replaceFragment(mCustomerListFragment, mCustomerListFragmentTag);
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
		
		mCustomerListFragment.searchCustomerStatisticBean(query);
	}
	
	@Override
	public void onCustomerStatisticUnselected() {
		
		mSelectedCustomerStatistic = null;
		
		Customer customer = mSelectedCustomerStatistic != null ? mSelectedCustomerStatistic.getCustomer() : null;

		mCustomerListFragment.setSelectedCustomerStatisticBean(mSelectedCustomerStatistic);
		mCustomerDetailFragment.setCustomer(customer);
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mCustomerListFragment.updateContent();
		mCustomerDetailFragment.updateContent();
	}
}
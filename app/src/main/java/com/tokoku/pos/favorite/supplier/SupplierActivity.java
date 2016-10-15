package com.tokoku.pos.favorite.supplier;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.model.SupplierStatisticBean;
import com.tokoku.pos.util.CommonUtil;

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

public class SupplierActivity extends BaseActivity 
	implements SupplierActionListener, SearchView.OnQueryTextListener {
	
	private SearchView searchView;
	
	private MenuItem mSearchMenu;
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private SupplierListFragment mSupplierListFragment;
	private SupplierDetailFragment mSupplierDetailFragment;
	private SupplierInventoryDlgFragment mSupplierTransactionDlgFragment;
	
	boolean mIsMultiplesPane = false;
	
	private SupplierStatisticBean mSelectedSupplierStatistic;
	
	private static String SELECTED_CUSTOMER_STATISTIC = "SELECTED_CUSTOMER_STATISTIC";
	
	private final String mSupplierListFragmentTag = "supplierListFragmentTag";
	private final String mSupplierDetailFragmentTag = "supplierDetailFragmentTag";
	private final String mSupplierTransactionDlgFragmentTag = "mSupplierTransactionDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_inventory_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mSupplierListFragmentTag, mSupplierDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_favorite_supplier));
		setSelectedMenu(getString(R.string.menu_favorite_supplier));
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedSupplierStatistic = (SupplierStatisticBean) savedInstanceState.getSerializable(SELECTED_CUSTOMER_STATISTIC);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mSupplierListFragment = (SupplierListFragment) getFragmentManager().findFragmentByTag(mSupplierListFragmentTag);
		
		if (mSupplierListFragment == null) {
			mSupplierListFragment = new SupplierListFragment();

		} else {
			removeFragment(mSupplierListFragment);
		}
		
		mSupplierDetailFragment = (SupplierDetailFragment) getFragmentManager().findFragmentByTag(mSupplierDetailFragmentTag);
		
		if (mSupplierDetailFragment == null) {
			mSupplierDetailFragment = new SupplierDetailFragment();

		} else {
			removeFragment(mSupplierDetailFragment);
		}
		
		mSupplierTransactionDlgFragment = (SupplierInventoryDlgFragment) getFragmentManager().findFragmentByTag(mSupplierTransactionDlgFragmentTag);
		
		if (mSupplierTransactionDlgFragment == null) {
			mSupplierTransactionDlgFragment = new SupplierInventoryDlgFragment();

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_CUSTOMER_STATISTIC, (Serializable) mSelectedSupplierStatistic);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		Supplier supplier = mSelectedSupplierStatistic != null ? mSelectedSupplierStatistic.getSupplier() : null;
		
		mSupplierListFragment.setSelectedSupplierStatisticBean(mSelectedSupplierStatistic);
		mSupplierDetailFragment.setSupplier(supplier);
		
		if (mIsMultiplesPane) {

			addFragment(mSupplierListFragment, mSupplierListFragmentTag);
			addFragment(mSupplierDetailFragment, mSupplierDetailFragmentTag);
			
		} else {

			if (mSelectedSupplierStatistic != null) {
				
				addFragment(mSupplierDetailFragment, mSupplierDetailFragmentTag);
				
			} else {
				
				addFragment(mSupplierListFragment, mSupplierListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favorite_supplier_menu, menu);
		
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
	public void onSupplierStatisticSelected(SupplierStatisticBean supplierStatistic) {
		
		mSelectedSupplierStatistic = supplierStatistic;
		
		mSupplierListFragment.setSelectedSupplierStatisticBean(supplierStatistic);
		mSupplierDetailFragment.setSupplier(supplierStatistic.getSupplier());
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mSupplierDetailFragment, mSupplierDetailFragmentTag);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			
			showSupplierStatistics();
		}
	}
	
	@Override
	public void showSupplierInventories(Supplier supplier) {
		
		if (mSupplierTransactionDlgFragment.isAdded()) {
			return;
		}
		
		mSupplierTransactionDlgFragment.show(getFragmentManager(), mSupplierTransactionDlgFragmentTag);
		mSupplierTransactionDlgFragment.setSupplier(supplier);
	}
	
	private void showSupplierStatistics() {
		
		mSelectedSupplierStatistic = null;
		
		Supplier supplier = mSelectedSupplierStatistic != null ? mSelectedSupplierStatistic.getSupplier() : null;
		
		mSupplierListFragment.setSelectedSupplierStatisticBean(mSelectedSupplierStatistic);
		mSupplierDetailFragment.setSupplier(supplier);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mSupplierListFragment, mSupplierListFragmentTag);
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
				
				replaceFragment(mSupplierListFragment, mSupplierListFragmentTag);
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
		
		mSupplierListFragment.searchSupplierStatisticBean(query);
	}
	
	@Override
	public void onSupplierStatisticUnselected() {
		
		mSelectedSupplierStatistic = null;
		
		Supplier supplier = mSelectedSupplierStatistic != null ? mSelectedSupplierStatistic.getSupplier() : null;

		mSupplierListFragment.setSelectedSupplierStatisticBean(mSelectedSupplierStatistic);
		mSupplierDetailFragment.setSupplier(supplier);
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mSupplierListFragment.updateContent();
		mSupplierDetailFragment.updateContent();
	}
}
package com.tokoku.pos.report.credit;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.common.ConfirmListener;
import com.tokoku.pos.data.cashflow.CashflowMgtActivity;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.util.CommonUtil;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class CreditActivity extends BaseActivity 
	implements CreditActionListener, SearchView.OnQueryTextListener, ConfirmListener {
	
	private SearchView searchView;
	
	private MenuItem mSearchMenu;
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private CreditListFragment mCreditListFragment;
	private CreditDetailFragment mCreditDetailFragment;
	
	boolean mIsMultiplesPane = false;
	
	private TransactionsBean mSelectedTransaction;
	
	private static String SELECTED_TRANSACTION = "SELECTED_TRANSACTION";
	
	private final String mCreditListFragmentTag = "creditListFragmentTag";
	private final String mCreditDetailFragmentTag = "creditDetailFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_inventory_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mCreditListFragmentTag, mCreditDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_report_credit));
		setSelectedMenu(getString(R.string.menu_report_credit));
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedTransaction = (TransactionsBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mCreditListFragment = (CreditListFragment) getFragmentManager().findFragmentByTag(mCreditListFragmentTag);
		
		if (mCreditListFragment == null) {
			mCreditListFragment = new CreditListFragment();

		} else {
			removeFragment(mCreditListFragment);
		}
		
		mCreditDetailFragment = (CreditDetailFragment) getFragmentManager().findFragmentByTag(mCreditDetailFragmentTag);
		
		if (mCreditDetailFragment == null) {
			mCreditDetailFragment = new CreditDetailFragment();

		} else {
			removeFragment(mCreditDetailFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_TRANSACTION, (Serializable) mSelectedTransaction);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mCreditListFragment.setSelectedTransaction(mSelectedTransaction);
		mCreditDetailFragment.setTransaction(mSelectedTransaction);
		
		if (mIsMultiplesPane) {

			addFragment(mCreditListFragment, mCreditListFragmentTag);
			addFragment(mCreditDetailFragment, mCreditDetailFragmentTag);
			
		} else {

			if (mSelectedTransaction != null) {
				
				addFragment(mCreditDetailFragment, mCreditDetailFragmentTag);
				
			} else {
				
				addFragment(mCreditListFragment, mCreditListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_credit_menu, menu);
		
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
	public void onTransactionSelected(TransactionsBean transaction) {
		
		mSelectedTransaction = transaction;
		
		mCreditListFragment.setSelectedTransaction(transaction);
		mCreditDetailFragment.setTransaction(mSelectedTransaction);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mCreditDetailFragment, mCreditDetailFragmentTag);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			
			showInventoryList();
		}
	}
	
	private void showInventoryList() {
		
		mSelectedTransaction = null;
		
		mCreditListFragment.setSelectedTransaction(mSelectedTransaction);
		mCreditDetailFragment.setTransaction(mSelectedTransaction);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mCreditListFragment, mCreditListFragmentTag);
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
				
				replaceFragment(mCreditListFragment, mCreditListFragmentTag);
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
		
		mCreditListFragment.searchTransaction(query);
	}
	
	@Override
	public void onTransactionUnselected() {
		
		mSelectedTransaction = null;

		mCreditListFragment.setSelectedTransaction(mSelectedTransaction);
		mCreditDetailFragment.setTransaction(mSelectedTransaction);
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mCreditListFragment.refreshContent();
		mCreditDetailFragment.updateContent();
	}
	
	@Override
	public void onConfirm(String task) {
		
		Intent intent = new Intent(getApplicationContext(), CashflowMgtActivity.class);
		intent.putExtra(Constant.SELECTED_CREDIT_FOR_PAYMENT, mSelectedTransaction);
		startActivity(intent);
	}
}
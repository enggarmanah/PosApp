package com.tokoku.pos.base.activity;

import java.io.Serializable;
import java.util.List;

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
import android.widget.SearchView;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncListener;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.common.ConfirmDeleteDlgFragment;
import com.tokoku.pos.data.merchant.MerchantMgtActivity;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public abstract class BaseItemMgtActivity<S, E, T> extends BaseActivity 
	implements BaseItemListener<T>, SearchView.OnQueryTextListener, HttpAsyncListener {
	
	protected S mSearchFragment;
	protected E mEditFragment;
	protected ConfirmDeleteDlgFragment<T> mConfirmDeleteFragment;

	boolean mIsMultiplesPane = false;
	boolean mIsEnableSearch = true;

	SearchView searchView;
	
	MenuItem mSearchMenu;
	MenuItem mListMenu;
	MenuItem mSaveMenu;
	MenuItem mDiscardMenu;
	MenuItem mEditMenu;
	MenuItem mDeleteMenu;

	List<T> mItems;
	T mSelectedItem;

	private static String LIST = "LIST";
	private static String SELECTED_ITEM = "SELECTED_ITEM";

	private String mSearchFragmentTag = "searchFragment";
	private String mEditFragmentTag = "editFragment";
	private String mConfirmDeleteFragmentTag = "confirmDeleteFragment";
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private boolean mIsOnEdit = false;
	
	final Context context = this;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ref_item_mgt_layout);

		initDrawerMenu();

		initFragments(savedInstanceState);

		initWaitAfterFragmentRemovedTask(mSearchFragmentTag, mEditFragmentTag);
		
		mConfirmDeleteFragment = (ConfirmDeleteDlgFragment<T>) getFragmentManager().findFragmentByTag(mConfirmDeleteFragmentTag);
		
		if (mConfirmDeleteFragment == null) {
			mConfirmDeleteFragment = new ConfirmDeleteDlgFragment<T>();
		}
		
		mHttpAsyncManager = new HttpAsyncManager(this);
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(progressDialogTag);
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		if (getFragmentManager().findFragmentByTag(progressDialogTag) != null) {
			mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(progressDialogTag);
		}
		
		if (mProgress == 100 && mProgressDialog.isVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
	}

	protected abstract S getSearchFragmentInstance();

	protected abstract E getEditFragmentInstance();

	@SuppressWarnings("unchecked")
	private void initFragments(Bundle savedInstanceState) {

		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mSearchFragment = (S) getFragmentManager().findFragmentByTag(mSearchFragmentTag);
		mEditFragment = (E) getFragmentManager().findFragmentByTag(mEditFragmentTag);

		if (mSearchFragment == null) {
			mSearchFragment = getSearchFragmentInstance();

		} else {
			removeFragment(mSearchFragment);
		}

		if (mEditFragment == null) {
			mEditFragment = getEditFragmentInstance();

		} else {
			removeFragment(mEditFragment);
		}

		if (savedInstanceState != null) {

			mItems = (List<T>) savedInstanceState.getSerializable(LIST);
			mSelectedItem = (T) savedInstanceState.getSerializable(SELECTED_ITEM);
		}
		
		if (mItems == null) {
			mItems = getItemsInstance();
		}
		
		setSearchFragmentItems(mItems);
		setSearchFragmentSelectedItem(mSelectedItem);

		setEditFragmentItem(mSelectedItem);
	}
	
	protected abstract List<T> getItemsInstance();
	
	protected abstract void setSearchFragmentItems(List<T> item);

	protected abstract void setSearchFragmentSelectedItem(T item);

	protected abstract void setEditFragmentItem(T item);
	
	protected abstract void enableEditFragmentInputFields(boolean isEnabled);

	@Override
	protected void afterFragmentRemoved() {

		loadFragments();
	}
	
	private boolean isFragmentHasBeenLoaded() {
		
		return (getFragmentManager().findFragmentByTag(mSearchFragmentTag) != null ||
				getFragmentManager().findFragmentByTag(mEditFragmentTag) != null);  
	}
	
	private void loadFragments() {
		
		if (isFragmentHasBeenLoaded()) {
			return;
		}

		if (mIsMultiplesPane) {
			addFragment(mSearchFragment, mSearchFragmentTag);
			addFragment(mEditFragment, mEditFragmentTag);

		} else {

			if (mSelectedItem == null) {
				addFragment(mSearchFragment, mSearchFragmentTag);
				
			} else {
				addFragment(mEditFragment, mEditFragmentTag);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putSerializable(LIST, (Serializable) mItems);
		outState.putSerializable(SELECTED_ITEM, (Serializable) mSelectedItem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_mgt_menu, menu);

		mSearchMenu = menu.findItem(R.id.menu_item_search);
		mListMenu = menu.findItem(R.id.menu_item_list);
		mSaveMenu = menu.findItem(R.id.menu_item_save);
		mDiscardMenu = menu.findItem(R.id.menu_item_discard);
		mEditMenu = menu.findItem(R.id.menu_item_edit);
		mDeleteMenu = menu.findItem(R.id.menu_item_delete);
		
		searchView = (SearchView) mSearchMenu.getActionView();
		searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.START));

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (null != searchManager) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		
		if (mSelectedItem == null) {
			showNavigationMenu();
		} else {
			showNavigationAndItemMenu();
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		if (mSelectedItem != null) {
			
			mEditMenu.setVisible(!isDrawerOpen);
			mDeleteMenu.setVisible(!isDrawerOpen);
			
			if (mIsMultiplesPane) {
				mSearchMenu.setVisible(!isDrawerOpen);
			} else {
				mListMenu.setVisible(!isDrawerOpen);
			}
		} else {
			mSearchMenu.setVisible(!isDrawerOpen);
		}
		
		if (mIsOnEdit) {
			mSaveMenu.setVisible(!isDrawerOpen);
			mDiscardMenu.setVisible(!isDrawerOpen);
		}
		
		if (this instanceof MerchantMgtActivity && !UserUtil.isRoot()) {
			
			mSearchMenu.setVisible(false);
			mDeleteMenu.setVisible(false);
		}
		
		afterPrepareOptionsMenu();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	protected void afterPrepareOptionsMenu() {}

	protected abstract View getSearchFragmentView();

	protected abstract View getEditFragmentView();

	protected abstract void doSearch(String query);

	protected abstract T getItemInstance();
	
	@Override
	public void onBackPressed() {
		
		showAllItems();
		showNavigationMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_item_search:
			
			showNavigationMenu();

			if (getSearchFragmentView() == null) {
				replaceFragment(mSearchFragment, mSearchFragmentTag);
			}

			doSearch(Constant.EMPTY_STRING);

			showMessage(R.string.msg_notification_search_action);

			return true;

		case R.id.menu_item_list:

			showAllItems();
			
			showNavigationMenu();

			return true;
			
		case R.id.menu_item_edit:
			
			showEditMenu();

			return true;
			
		case R.id.menu_item_delete:
			
			confirmDelete(mSelectedItem);
			
			return true;
			
		case R.id.menu_item_save:
			
			saveItem();
			
			return true;
			
		case R.id.menu_item_discard:
			
			if (getItemId(mSelectedItem) == null) { 
				showNavigationMenu();
				discardItem();
				
			} else {
				showNavigationAndItemMenu();
				updateEditFragmentItem(mSelectedItem);
				refreshEditView();
			}
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected abstract void saveItem();
	
	protected abstract void discardItem();
	
	public abstract void deleteItem(T item);
	
	protected abstract String getItemName(T item);
	
	private void confirmDelete(final T item) {
		
		if (MerchantUtil.getMerchantId() == Constant.DEMO_MERCHANT_ID) {
			
			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_demo_account));
			return;
		}
		
		if (mConfirmDeleteFragment.isAdded()) {
			return;
		}
		
		mConfirmDeleteFragment.show(getFragmentManager(), mConfirmDeleteFragmentTag);
		mConfirmDeleteFragment.setItemToBeDeleted(item, getItemName(item));
	}
	
	@Override
	public boolean onQueryTextChange(String query) {
		
		boolean isQuerySimilar = query.equals(prevQuery);
		
		prevQuery = query;
		
		if (mIsEnableSearch && !isQuerySimilar) {
			
			showNavigationMenu();

			if (mIsMultiplesPane) {
				doSearch(query);
			} else {
				if (!isSearchVisible()) {
					replaceFragment(mSearchFragment, mSearchFragmentTag);
				}
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

	private boolean isSearchVisible() {

		if (getFragmentManager().findFragmentByTag(mSearchFragmentTag) != null) {
			return true;
		} else {
			return false;
		}
	}

	protected abstract void unSelectItem();

	@Override
	public void displaySearch() {
		
		showNavigationMenu();
		
		if (mIsMultiplesPane) {
			
			unSelectItem();
		
		} else {
			
			replaceFragment(mSearchFragment, mSearchFragmentTag);
		}
	}

	protected abstract T updateEditFragmentItem(T item);

	protected abstract void refreshEditView();
	
	protected void showNavigationMenu() {
		
		if (mSearchMenu == null) {
			return;
		}
		
		mSearchMenu.setVisible(true);
		
		if (this instanceof MerchantMgtActivity && !UserUtil.isRoot()) {
			mSearchMenu.setVisible(false);
		}
		
		mListMenu.setVisible(false);
		
		mEditMenu.setVisible(false);
		mDeleteMenu.setVisible(false);
		
		mSaveMenu.setVisible(false);
		mDiscardMenu.setVisible(false);
		
		mIsOnEdit = false;
	}
	
	protected void showNavigationAndItemMenu() {
		
		enableEditFragmentInputFields(false);
		
		if (mIsMultiplesPane) {
			mSearchMenu.setVisible(true);
			mListMenu.setVisible(false);
			
			if (this instanceof MerchantMgtActivity && !UserUtil.isRoot()) {
				mSearchMenu.setVisible(false);
			}
		
		} else {
			mSearchMenu.setVisible(false);
			mListMenu.setVisible(true);
		}
		
		mEditMenu.setVisible(true);
		
		mDeleteMenu.setVisible(false);
		
		if (mSelectedItem instanceof Merchant) {
			if (UserUtil.isRoot()) {
				mDeleteMenu.setVisible(true);
			}
		} else {
			mDeleteMenu.setVisible(true);
		}
		
		mSaveMenu.setVisible(false);
		mDiscardMenu.setVisible(false);
		
		mIsOnEdit = false;
	}
	
	protected void showEditMenu() {
		
		enableEditFragmentInputFields(true);
		
		mSearchMenu.setVisible(false);
		mListMenu.setVisible(false);
		
		mEditMenu.setVisible(false);
		mDeleteMenu.setVisible(false);
		
		mSaveMenu.setVisible(true);
		mDiscardMenu.setVisible(true);
		
		mIsOnEdit = true;
	}
	
	public void disableEditMenu() {
		
		mEditMenu.setVisible(false);
		mDeleteMenu.setVisible(false);
	}

	@Override
	public void updateItem(T item) {
		
		mSelectedItem = item;
		
		showNavigationAndItemMenu();
		
		if (mIsMultiplesPane) {
			updateEditFragmentItem(item);
			refreshEditView();

		} else {
			
			mIsEnableSearch = false;
			mSearchMenu.collapseActionView();
			mIsEnableSearch = true;
			
			replaceFragment(mEditFragment, mEditFragmentTag);
			updateEditFragmentItem(item);
		}
	}

	protected abstract void addEditFragmentItem(T item);

	@Override
	public void addItem() {

		mSearchMenu.collapseActionView();

		unSelectItem();

		showEditMenu();
		
		mSelectedItem = getItemInstance();

		addEditFragmentItem(mSelectedItem);

		if (mIsMultiplesPane) {
			refreshEditView();
		} else {
			replaceFragment(mEditFragment, mEditFragmentTag);
			refreshEditView();
		}
	}

	public abstract void reloadItems();

	@Override
	public void onAddCompleted() {
		
		showNavigationMenu();
		
		if (!mSearchMenu.collapseActionView()) {
			reloadItems();
		}

		if (mIsMultiplesPane) {
			getEditFragmentView().setVisibility(View.INVISIBLE);
		} else {
			replaceFragment(mSearchFragment, mSearchFragmentTag);
		}
	}
	
	@Override
	public void onDeleteCompleted() {
		
		mSelectedItem = null;
		
		showNavigationMenu();
		
		if (!mSearchMenu.collapseActionView()) {
			reloadItems();
		}

		if (mIsMultiplesPane) {
			getEditFragmentView().setVisibility(View.INVISIBLE);
		} else {
			replaceFragment(mSearchFragment, mSearchFragmentTag);
		}
	}

	protected abstract Long getItemId(T item);

	protected abstract void refreshItem(T item);

	protected abstract void refreshSearchFragmentItems();

	@Override
	public void onUpdateCompleted() {
		
		showNavigationAndItemMenu();
		
		for (T item : mItems) {
			if (getItemId(item) == getItemId(mSelectedItem)) {
				refreshItem(item);
				break;
			}
		}
		
		refreshSearchFragmentItems();
	}

	@Override
	public void onItemUnselected() {
		
		showNavigationMenu();
		
		mSelectedItem = null;

		if (mIsMultiplesPane) {

			setEditFragmentItem(null);

			if (getEditFragmentView() != null) {
				getEditFragmentView().setVisibility(View.INVISIBLE);
			}

		} else {
			
			setSearchFragmentSelectedItem(mSelectedItem);
			refreshSearchFragmentItems();
		}
	}

	private void showAllItems() {
		
		mSelectedItem = null;
		
		mSearchMenu.collapseActionView();

		reloadItems();

		if (mIsMultiplesPane) {
			refreshSearchFragmentItems();
		} else {
			if (getSearchFragmentView() == null) {
				replaceFragment(mSearchFragment, mSearchFragmentTag);
			}
		}
	}
	
	public void onSelectEmployee(boolean isMandatory) {}
	
	public void onSelectProduct(boolean isMandatory) {}
	
	public void onSelectProductGroup(boolean isMandatory) {}
	
	public void onSelectSupplier(boolean isMandatory) {}
	
	public void onSelectBill(boolean isMandatory) {}
	
	public void onSelectTransaction(boolean isMandatory) {}
	
	public void onSelectLocale(boolean isMandatory) {}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		showAllItems();
	}
}
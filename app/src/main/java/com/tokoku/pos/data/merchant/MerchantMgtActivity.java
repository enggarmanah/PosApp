package com.tokoku.pos.data.merchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.popup.search.LocaleDlgFragment;
import com.tokoku.pos.popup.search.LocaleSelectionListener;
import com.tokoku.pos.util.UserUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MerchantMgtActivity extends BaseItemMgtActivity<MerchantSearchFragment, MerchantEditFragment, Merchant>
	implements LocaleSelectionListener {
	
	List<Merchant> mMerchants;
	Merchant mSelectedMerchant;
	
	private LocaleDlgFragment mLocaleDlgFragment;
	
	private static String mLocaleDlgFragmentTag = "mLocaleDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHttpAsyncManager = new HttpAsyncManager(this);
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag("progressDialogTag");
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
		
		mLocaleDlgFragment = (LocaleDlgFragment) getFragmentManager().findFragmentByTag(mLocaleDlgFragmentTag);
		
		if (mLocaleDlgFragment == null) {
			mLocaleDlgFragment = new LocaleDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_reference_merchant));
		
		if (UserUtil.isRoot()) {
			setSelectedMenu(getString(R.string.menu_merchant));
		} else {
			setSelectedMenu(getString(R.string.menu_reference_merchant));
		}
		
		if (mProgress == 100 && mProgressDialog.isVisible()) {
			mProgressDialog.dismissAllowingStateLoss();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_item_sync:
			
			if (mProgressDialog.isAdded()) {
				return true;
			}
			
			mProgressDialog.show(getFragmentManager(), "progressDialogTag");
			
			mHttpAsyncManager.syncMerchants();
			
			return true;

		default:
			
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onSelectLocale(boolean isMandatory) {
		
		if (mLocaleDlgFragment.isAdded()) {
			return;
		}
		
		mLocaleDlgFragment.setMandatory(isMandatory);
		mLocaleDlgFragment.show(getFragmentManager(), mLocaleDlgFragmentTag);
	}
	
	@Override
	public void onLocaleSelected(Locale locale) {
		
		mEditFragment.setLocale(locale);
	}
	
	@Override
	protected MerchantSearchFragment getSearchFragmentInstance() {
		
		return new MerchantSearchFragment();
	}

	@Override
	protected MerchantEditFragment getEditFragmentInstance() {
		
		return new MerchantEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Merchant> merchants) {
		
		mSearchFragment.setItems(merchants);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Merchant merchant) {
		
		mSearchFragment.setSelectedItem(merchant);
	}
	
	@Override
	protected void setEditFragmentItem(Merchant merchant) {
		
		mEditFragment.setItem(merchant);
	}
	
	@Override
	protected View getSearchFragmentView() {
		
		return mSearchFragment.getView();
	}

	@Override
	protected View getEditFragmentView() {
		
		return mEditFragment.getView();
	}
	
	@Override
	protected void enableEditFragmentInputFields(boolean isEnabled) {
		
		mEditFragment.enableInputFields(isEnabled);
	}

	@Override
	protected void doSearch(String query) {
		
		mSearchFragment.searchItem(query);
	}
	
	@Override
	protected Merchant getItemInstance() {
		
		return new Merchant();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Merchant updateEditFragmentItem(Merchant merchant) {
		
		return mEditFragment.updateItem(merchant);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Merchant merchant) {
	
		mEditFragment.addItem(merchant);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Merchant merchant) {
		
		return merchant.getId();
	}
	
	@Override
	protected void refreshItem(Merchant merchant) {
		
		merchant.refresh();
	}
	
	@Override
	protected void refreshSearchFragmentItems() {
		
		mSearchFragment.refreshItems();
	}
	
	@Override
	protected void saveItem() {
		
		mEditFragment.saveEditItem();
	}
	
	@Override
	protected void discardItem() {
		
		mEditFragment.discardEditItem();
	}
	
	@Override
	public void deleteItem(Merchant item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Merchant item) {
		
		return item.getName();
	}
	
	@Override
	protected List<Merchant> getItemsInstance() {
		
		return new ArrayList<Merchant>();
	}
}
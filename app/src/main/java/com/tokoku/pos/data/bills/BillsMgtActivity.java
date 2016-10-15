package com.tokoku.pos.data.bills;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.popup.search.SupplierDlgFragment;
import com.tokoku.pos.popup.search.SupplierSelectionListener;

import android.os.Bundle;
import android.view.View;

public class BillsMgtActivity extends BaseItemMgtActivity<BillsSearchFragment, BillsEditFragment, Bills> 
	implements SupplierSelectionListener {
	
	List<Bills> mBills;
	Bills mSelectedBills;
	
	private SupplierDlgFragment mSupplierDlgFragment;
	
	private static String mSupplierDlgFragmentTag = "mSupplierDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSupplierDlgFragment = (SupplierDlgFragment) getFragmentManager().findFragmentByTag(mSupplierDlgFragmentTag);
		
		if (mSupplierDlgFragment == null) {
			mSupplierDlgFragment = new SupplierDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_bills));
		setSelectedMenu(getString(R.string.menu_bills));
	}
	
	@Override
	protected BillsSearchFragment getSearchFragmentInstance() {
		
		return new BillsSearchFragment();
	}

	@Override
	protected BillsEditFragment getEditFragmentInstance() {
		
		return new BillsEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Bills> inventories) {
		
		mSearchFragment.setItems(inventories);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Bills bills) {
		
		mSearchFragment.setSelectedItem(bills);
	}
	
	@Override
	protected void setEditFragmentItem(Bills bills) {
		
		mEditFragment.setItem(bills);
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
	protected Bills getItemInstance() {
		
		return new Bills();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Bills updateEditFragmentItem(Bills bills) {
		
		return mEditFragment.updateItem(bills);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Bills bills) {
	
		mEditFragment.addItem(bills);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Bills bills) {
		
		return bills.getId();
	}
	
	@Override
	protected void refreshItem(Bills bills) {
		
		bills.refresh();
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
	public void deleteItem(Bills item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Bills item) {
		
		return item.getRemarks();
	}
	
	@Override
	protected List<Bills> getItemsInstance() {
		
		return new ArrayList<Bills>();
	}
	
	public void onSelectSupplier(boolean isMandatory) {
		
		if (mSupplierDlgFragment.isAdded()) {
			return;
		}
		
		mSupplierDlgFragment.setMandatory(isMandatory);
		mSupplierDlgFragment.show(getFragmentManager(), mSupplierDlgFragmentTag);
	}
	
	public void onSupplierSelected(Supplier supplier) {
		
		mEditFragment.setSupplier(supplier);
	}
}
package com.tokoku.pos.data.supplier;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;

import android.os.Bundle;
import android.view.View;

public class SupplierMgtActivity extends BaseItemMgtActivity<SupplierSearchFragment, SupplierEditFragment, Supplier> {
	
	List<Supplier> mSuppliers;
	Supplier mSelectedSupplier;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_reference_supplier));
		setSelectedMenu(getString(R.string.menu_reference_supplier));
	}
	
	@Override
	protected SupplierSearchFragment getSearchFragmentInstance() {
		
		return new SupplierSearchFragment();
	}

	@Override
	protected SupplierEditFragment getEditFragmentInstance() {
		
		return new SupplierEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Supplier> suppliers) {
		
		mSearchFragment.setItems(suppliers);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Supplier supplier) {
		
		mSearchFragment.setSelectedItem(supplier);
	}
	
	@Override
	protected void setEditFragmentItem(Supplier supplier) {
		
		mEditFragment.setItem(supplier);
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
	protected Supplier getItemInstance() {
		
		return new Supplier();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Supplier updateEditFragmentItem(Supplier supplier) {
		
		return mEditFragment.updateItem(supplier);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Supplier supplier) {
	
		mEditFragment.addItem(supplier);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Supplier supplier) {
		
		return supplier.getId();
	}
	
	@Override
	protected void refreshItem(Supplier supplier) {
		
		supplier.refresh();
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
	public void deleteItem(Supplier item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Supplier item) {
		
		return item.getName();
	}
	
	@Override
	protected List<Supplier> getItemsInstance() {
		
		return new ArrayList<Supplier>();
	}
}
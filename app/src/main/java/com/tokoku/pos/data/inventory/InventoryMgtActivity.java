package com.tokoku.pos.data.inventory;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Product;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.popup.search.BillDlgFragment;
import com.tokoku.pos.popup.search.BillSelectionListener;
import com.tokoku.pos.popup.search.ProductDlgFragment;
import com.tokoku.pos.popup.search.ProductSelectionListener;
import com.tokoku.pos.popup.search.SupplierDlgFragment;
import com.tokoku.pos.popup.search.SupplierSelectionListener;

import android.os.Bundle;
import android.view.View;

public class InventoryMgtActivity extends BaseItemMgtActivity<InventorySearchFragment, InventoryEditFragment, Inventory> 
	implements ProductSelectionListener, SupplierSelectionListener, BillSelectionListener {
	
	List<Inventory> mInventories;
	Inventory mSelectedInventory;
	
	private ProductDlgFragment mProductDlgFragment;
	private SupplierDlgFragment mSupplierDlgFragment;
	private BillDlgFragment mBillDlgFragment;
	
	private static String mProductDlgFragmentTag = "mProductDlgFragmentTag";
	private static String mSupplierDlgFragmentTag = "mSupplierDlgFragmentTag";
	private static String mBillDlgFragmentTag = "mBillDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mProductDlgFragment = (ProductDlgFragment) getFragmentManager().findFragmentByTag(mProductDlgFragmentTag);
		
		if (mProductDlgFragment == null) {
			mProductDlgFragment = new ProductDlgFragment();
		}
		
		mSupplierDlgFragment = (SupplierDlgFragment) getFragmentManager().findFragmentByTag(mSupplierDlgFragmentTag);
		
		if (mSupplierDlgFragment == null) {
			mSupplierDlgFragment = new SupplierDlgFragment();
		}
		
		mBillDlgFragment = (BillDlgFragment) getFragmentManager().findFragmentByTag(mBillDlgFragmentTag);
		
		if (mBillDlgFragment == null) {
			mBillDlgFragment = new BillDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_inventory));
		setSelectedMenu(getString(R.string.menu_inventory));
	}
	
	@Override
	protected InventorySearchFragment getSearchFragmentInstance() {
		
		return new InventorySearchFragment();
	}

	@Override
	protected InventoryEditFragment getEditFragmentInstance() {
		
		return new InventoryEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Inventory> inventories) {
		
		mSearchFragment.setItems(inventories);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Inventory inventory) {
		
		mSearchFragment.setSelectedItem(inventory);
	}
	
	@Override
	protected void setEditFragmentItem(Inventory inventory) {
		
		mEditFragment.setItem(inventory);
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
	protected Inventory getItemInstance() {
		
		return new Inventory();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Inventory updateEditFragmentItem(Inventory inventory) {
		
		return mEditFragment.updateItem(inventory);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Inventory inventory) {
	
		mEditFragment.addItem(inventory);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Inventory inventory) {
		
		return inventory.getId();
	}
	
	@Override
	protected void refreshItem(Inventory inventory) {
		
		inventory.refresh();
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
	public void deleteItem(Inventory item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Inventory item) {
		
		return item.getProductName();
	}
	
	@Override
	protected List<Inventory> getItemsInstance() {
		
		return new ArrayList<Inventory>();
	}
	
	public void onSelectProduct(boolean isMandatory) {
		
		if (mProductDlgFragment.isAdded()) {
			return;
		}
		
		mProductDlgFragment.setMandatory(isMandatory);
		mProductDlgFragment.show(getFragmentManager(), mProductDlgFragmentTag);
	}
	
	public void onProductSelected(Product product) {
		
		mEditFragment.setProduct(product);
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
	
	public void onSelectBill(boolean isMandatory) {
		
		if (mBillDlgFragment.isAdded()) {
			return;
		}
		
		mBillDlgFragment.setMandatory(isMandatory);
		mBillDlgFragment.setProductPurchase(true);
		mBillDlgFragment.show(getFragmentManager(), mBillDlgFragmentTag);
	}
	
	public void onBillSelected(Bills bill) {
		
		mEditFragment.setBill(bill);
	}
}
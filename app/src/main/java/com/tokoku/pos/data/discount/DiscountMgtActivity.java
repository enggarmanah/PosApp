package com.tokoku.pos.data.discount;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Discount;
import com.android.pos.dao.DiscountDao;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.util.DbUtil;

import android.os.Bundle;
import android.view.View;

public class DiscountMgtActivity extends BaseItemMgtActivity<DiscountSearchFragment, DiscountEditFragment, Discount> {
	
	List<Discount> mDiscounts;
	Discount mSelectedDiscount;
	
	DiscountDao discountDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		discountDao = DbUtil.getSession().getDiscountDao();
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_reference_discount));
		setSelectedMenu(getString(R.string.menu_reference_discount));
	}
	
	@Override
	protected DiscountSearchFragment getSearchFragmentInstance() {
		
		return new DiscountSearchFragment();
	}

	@Override
	protected DiscountEditFragment getEditFragmentInstance() {
		
		return new DiscountEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Discount> discounts) {
		
		mSearchFragment.setItems(discounts);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Discount discount) {
		
		mSearchFragment.setSelectedItem(discount);
	}
	
	@Override
	protected void setEditFragmentItem(Discount discount) {
		
		mEditFragment.setItem(discount);
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
	protected Discount getItemInstance() {
		
		return new Discount();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Discount updateEditFragmentItem(Discount discount) {
		
		return mEditFragment.updateItem(discount);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Discount discount) {
	
		mEditFragment.addItem(discount);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Discount discount) {
		
		return discount.getId();
	}
	
	@Override
	protected void refreshItem(Discount discount) {
		
		discountDao.getSession().refresh(discount);
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
	public void deleteItem(Discount item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Discount item) {
		
		return item.getName();
	}
	
	@Override
	protected List<Discount> getItemsInstance() {
		
		return new ArrayList<Discount>();
	}
}
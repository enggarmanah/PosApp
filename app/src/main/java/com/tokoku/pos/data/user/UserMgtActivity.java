package com.tokoku.pos.data.user;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.User;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.popup.search.EmployeeDlgFragment;
import com.tokoku.pos.popup.search.EmployeeSelectionListener;

import android.os.Bundle;
import android.view.View;

public class UserMgtActivity extends BaseItemMgtActivity<UserSearchFragment, UserEditFragment, User> 
	implements EmployeeSelectionListener {
	
	List<User> mUsers;
	User mSelectedUser;
			
	private EmployeeDlgFragment mEmployeeDlgFragment;
	
	private static String mEmployeeDlgFragmentTag = "mEmployeeDlgFragmentTag";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mEmployeeDlgFragment = (EmployeeDlgFragment) getFragmentManager().findFragmentByTag(mEmployeeDlgFragmentTag);
		
		if (mEmployeeDlgFragment == null) {
			mEmployeeDlgFragment = new EmployeeDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_user_access));
		setSelectedMenu(getString(R.string.menu_user_access));
	}
	
	@Override
	protected UserSearchFragment getSearchFragmentInstance() {
		
		return new UserSearchFragment();
	}

	@Override
	protected UserEditFragment getEditFragmentInstance() {
		
		return new UserEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<User> users) {
		
		mSearchFragment.setItems(users);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(User user) {
		
		mSearchFragment.setSelectedItem(user);
	}
	
	@Override
	protected void setEditFragmentItem(User user) {
		
		mEditFragment.setItem(user);
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
	protected User getItemInstance() {
		
		return new User();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected User updateEditFragmentItem(User user) {
		
		return mEditFragment.updateItem(user);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(User user) {
	
		mEditFragment.addItem(user);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(User user) {
		
		return user.getId();
	}
	
	@Override
	protected void refreshItem(User user) {
		
		user.refresh();
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
	public void deleteItem(User item) {
		
		mSearchFragment.onItemDeleted(item);
		
	}
	
	@Override
	protected String getItemName(User item) {
		
		return item.getName();
	}
	
	@Override
	protected List<User> getItemsInstance() {
		
		return new ArrayList<User>();
	}
	
	@Override
	public void onSelectEmployee(boolean isMandatory) {
		
		if (mEmployeeDlgFragment.isAdded()) {
			return;
		}
		
		mEmployeeDlgFragment.setMandatory(isMandatory);
		mEmployeeDlgFragment.show(getFragmentManager(), mEmployeeDlgFragmentTag);
	}
	
	@Override
	public void onEmployeeSelected(Employee employee) {
		
		mEditFragment.setEmployee(employee);
	}
}
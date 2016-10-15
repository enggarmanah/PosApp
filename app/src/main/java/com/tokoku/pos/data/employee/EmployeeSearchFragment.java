package com.tokoku.pos.data.employee;

import java.util.List;

import com.android.pos.dao.Employee;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.EmployeeDaoService;

import android.app.Activity;

public class EmployeeSearchFragment extends BaseSearchFragment<Employee> {

	private EmployeeDaoService mEmployeeDaoService = new EmployeeDaoService();

	public void initAdapter() {
		
		mAdapter = new EmployeeSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Employee>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Employee>");
        }
    }

	protected Long getItemId(Employee item) {
		return item.getId();
	}
	
	public List<Employee> getItems(String query) {

		return mEmployeeDaoService.getEmployees(query, 0);
	}
	
	public List<Employee> getNextItems(String query, int lastIndex) {

		return mEmployeeDaoService.getEmployees(query, lastIndex);
	}

	public void onItemDeleted(Employee item) {

		mEmployeeDaoService.deleteEmployee(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
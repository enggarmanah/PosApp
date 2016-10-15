package com.tokoku.pos.data.employee;

import java.util.List;

import com.android.pos.dao.Employee;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;

import android.content.Context;

public class EmployeeSearchArrayAdapter extends BaseSearchArrayAdapter<Employee> {
	
	public EmployeeSearchArrayAdapter(Context context, List<Employee> employees, Employee selectedEmployee, ItemActionListener<Employee> listener) {
		super(context, employees, selectedEmployee, listener);
	}
	
	@Override
	public Long getItemId(Employee employee) {
		return employee.getId();
	}
	
	@Override
	public String getItemName(Employee employee) {
		return employee.getName();
	}
}
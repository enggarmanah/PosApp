package com.tokoku.pos.cashier;

import android.content.Context;
import android.widget.Spinner;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.base.adapter.BaseSpinnerArrayAdapter;

public class CashierProductCountPicSpinnerArrayAdapter extends BaseSpinnerArrayAdapter<Employee> {

	Spinner spinner;

	public CashierProductCountPicSpinnerArrayAdapter(Spinner spinner, Context context, Employee[] options) {
		super(context, options, R.layout.cashier_spinner_items, R.layout.cashier_spinner_items_selected,
				R.layout.cashier_spinner_selected_item);
		
		this.spinner = spinner;
	}
	
	@Override
	protected String getSelectedValue() {

		if (spinner.getSelectedItem() != null) {
			return String.valueOf(((Employee) spinner.getSelectedItem()).getId());
		} else {
			return null;
		}
	}

	@Override
	protected String getLabel1(Employee employee) {

		return employee.getName();
	}

	@Override
	protected String getValue(Employee employee) {

		return String.valueOf(employee.getId());
	}
}

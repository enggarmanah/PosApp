package com.tokoku.pos.data.customer;

import java.util.List;

import com.android.pos.dao.Customer;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;

import android.content.Context;

public class CustomerSearchArrayAdapter extends BaseSearchArrayAdapter<Customer> {
	
	public CustomerSearchArrayAdapter(Context context, List<Customer> customers, Customer selectedCustomer, ItemActionListener<Customer> listener) {
		super(context, customers, selectedCustomer, listener);
	}
	
	@Override
	public Long getItemId(Customer customer) {
		return customer.getId();
	}
	
	@Override
	public String getItemName(Customer customer) {
		return customer.getName();
	}
}
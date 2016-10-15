package com.tokoku.pos;

import com.android.pos.dao.Customer;

public class Session {
	
	private static Customer customer;

	public static Customer getCustomer() {
		return customer;
	}

	public static void setCustomer(Customer customer) {
		Session.customer = customer;
	}
}

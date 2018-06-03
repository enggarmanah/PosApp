package com.tokoku.pos;

import com.android.pos.dao.Customer;
import com.android.pos.dao.Inventory;

public class Session {
	
	private static Customer customer;
	private static Inventory inventory;

	public static Customer getCustomer() {
		return customer;
	}

	public static void setCustomer(Customer customer) {
		Session.customer = customer;
	}

    public static Inventory getInventory() {
        return inventory;
    }

    public static void setInventory(Inventory inventory) {
        Session.inventory = inventory;
    }
}

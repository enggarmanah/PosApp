package com.tokoku.pos.model;

import java.io.Serializable;

import com.android.pos.dao.Customer;

@SuppressWarnings("serial")
public class CustomerStatisticBean implements Serializable {

	private Customer customer;
	private String name;
	private Long quantity;
	private Long amount;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}

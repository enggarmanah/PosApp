package com.tokoku.pos.model;

import java.io.Serializable;

import com.android.pos.dao.Supplier;

@SuppressWarnings("serial")
public class SupplierStatisticBean implements Serializable {

	private Supplier supplier;
	private String name;
	private Long quantity;
	private Long amount;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

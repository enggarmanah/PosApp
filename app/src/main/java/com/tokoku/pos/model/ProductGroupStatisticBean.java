package com.tokoku.pos.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProductGroupStatisticBean implements Serializable {

	private String name;
	private Long quantity;
	private Long amount;
	
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

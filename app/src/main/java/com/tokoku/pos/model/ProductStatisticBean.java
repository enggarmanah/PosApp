package com.tokoku.pos.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProductStatisticBean implements Serializable {

	private String product_name;
	private Double value;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}

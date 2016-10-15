package com.tokoku.pos.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProductStatisticBean implements Serializable {

	private String product_name;
	private long value;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}

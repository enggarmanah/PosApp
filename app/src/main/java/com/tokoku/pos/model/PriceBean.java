package com.tokoku.pos.model;

public class PriceBean {
	
	private String type;
	private Float value;
	
	public PriceBean(String type, Float value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
}

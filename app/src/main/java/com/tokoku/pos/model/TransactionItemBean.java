package com.tokoku.pos.model;

public class TransactionItemBean {

	private Long id;
	private Long remote_id;
	private String ref_id;
	private Long merchant_id;
	private Long transaction_id;
	private Long product_id;
	private String product_name;
	private String product_type;
	private Float price;
	private Float cost_price;
	private Float discount;
	private Float quantity;
	private Float commision;
	private Long employee_id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getRemote_id() {
		return remote_id;
	}

	public void setRemote_id(Long remote_id) {
		this.remote_id = remote_id;
	}
	
	public String getRef_id() {
		return ref_id;
	}

	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}

	public Long getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(Long merchant_id) {
		this.merchant_id = merchant_id;
	}

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getCost_price() {
		return cost_price;
	}

	public void setCost_price(Float cost_price) {
		this.cost_price = cost_price;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Float getCommision() {
		return commision;
	}

	public void setCommision(Float commision) {
		this.commision = commision;
	}

	public Long getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(Long employee_id) {
		this.employee_id = employee_id;
	}
}

package com.tokoku.pos.model;

public class OrdersBean {

	private Long id;
	private Long remote_id;
	private String ref_id;
	private Long merchant_id;
	private String order_no;
	private java.util.Date order_date;
	private String order_type;
	private String order_reference;
	private Long waitress_id;
	private String waitress_name;
	private Long customer_id;
	private String customer_name;
	private String status;

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

	public Long getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(Long merchant_id) {
		this.merchant_id = merchant_id;
	}
	
	public String getRef_id() {
		return ref_id;
	}

	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public java.util.Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(java.util.Date order_date) {
		this.order_date = order_date;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getOrder_reference() {
		return order_reference;
	}

	public void setOrder_reference(String order_reference) {
		this.order_reference = order_reference;
	}
	
	public Long getWaitress_id() {
		return waitress_id;
	}

	public void setWaitress_id(Long waitress_id) {
		this.waitress_id = waitress_id;
	}

	public String getWaitress_name() {
		return waitress_name;
	}

	public void setWaitress_name(String waitress_name) {
		this.waitress_name = waitress_name;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

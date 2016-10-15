package com.tokoku.pos.model;

public class BillsBean extends BaseBean {

	private String bill_reference_no;
	private String bill_type;
	private java.util.Date bill_date;
	private java.util.Date bill_due_date;
	private Float bill_amount;
	private java.util.Date payment_date;
	private Float payment;
	private Long supplier_id;
	private String supplier_name;
	private java.util.Date delivery_date;
	private String remarks;
	private String status;

	public String getBill_reference_no() {
		return bill_reference_no;
	}

	public void setBill_reference_no(String bill_reference_no) {
		this.bill_reference_no = bill_reference_no;
	}

	public String getBill_type() {
		return bill_type;
	}

	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}

	public java.util.Date getBill_date() {
		return bill_date;
	}

	public void setBill_date(java.util.Date bill_date) {
		this.bill_date = bill_date;
	}

	public java.util.Date getBill_due_date() {
		return bill_due_date;
	}

	public void setBill_due_date(java.util.Date bill_due_date) {
		this.bill_due_date = bill_due_date;
	}

	public Float getBill_amount() {
		return bill_amount;
	}

	public void setBill_amount(Float bill_amount) {
		this.bill_amount = bill_amount;
	}

	public java.util.Date getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(java.util.Date payment_date) {
		this.payment_date = payment_date;
	}

	public Float getPayment() {
		return payment;
	}

	public void setPayment(Float payment) {
		this.payment = payment;
	}

	public Long getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(Long supplier_id) {
		this.supplier_id = supplier_id;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public java.util.Date getDelivery_date() {
		return delivery_date;
	}

	public void setDelivery_date(java.util.Date delivery_date) {
		this.delivery_date = delivery_date;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

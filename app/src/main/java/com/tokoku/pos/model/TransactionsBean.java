package com.tokoku.pos.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TransactionsBean implements Serializable {

	private Long id;
	private Long remote_id;
	private String ref_id;
	private Long merchant_id;
	private String transaction_no;
	private String order_type;
	private String order_reference;
	private java.util.Date transaction_date;
	private Float bill_amount;
	private String discount_name;
	private Float discount_percentage;
	private Float discount_amount;
	private Float tax_percentage;
	private Float tax_amount;
	private Float service_charge_percentage;
	private Float service_charge_amount;
	private Float total_amount;
	private Float payment_amount;
	private Float return_amount;
	private String payment_type;
	private Long cashier_id;
	private String cashier_name;
	private Long waitress_id;
	private String waitress_name;
	private Long customer_id;
	private String customer_name;	
	private String status;
	private Date sync_date;
	
	//only for Bean to capture Total Credit Payments
	private Float total_credit_payment;
	
	public Float getTotal_credit_payment() {
		return total_credit_payment;
	}

	public void setTotal_credit_payment(Float total_credit_payment) {
		this.total_credit_payment = total_credit_payment;
	}

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

	public String getTransaction_no() {
		return transaction_no;
	}

	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
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

	public java.util.Date getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(java.util.Date transaction_date) {
		this.transaction_date = transaction_date;
	}

	public Float getBill_amount() {
		return bill_amount;
	}

	public void setBill_amount(Float bill_amount) {
		this.bill_amount = bill_amount;
	}

	public String getDiscount_name() {
		return discount_name;
	}

	public void setDiscount_name(String discount_name) {
		this.discount_name = discount_name;
	}

	public Float getDiscount_percentage() {
		return discount_percentage;
	}

	public void setDiscount_percentage(Float discount_percentage) {
		this.discount_percentage = discount_percentage;
	}

	public Float getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(Float discount_amount) {
		this.discount_amount = discount_amount;
	}

	public Float getTax_percentage() {
		return tax_percentage;
	}

	public void setTax_percentage(Float tax_percentage) {
		this.tax_percentage = tax_percentage;
	}

	public Float getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(Float tax_amount) {
		this.tax_amount = tax_amount;
	}

	public Float getService_charge_percentage() {
		return service_charge_percentage;
	}

	public void setService_charge_percentage(Float service_charge_percentage) {
		this.service_charge_percentage = service_charge_percentage;
	}

	public Float getService_charge_amount() {
		return service_charge_amount;
	}

	public void setService_charge_amount(Float service_charge_amount) {
		this.service_charge_amount = service_charge_amount;
	}

	public Float getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(Float total_amount) {
		this.total_amount = total_amount;
	}

	public Float getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(Float payment_amount) {
		this.payment_amount = payment_amount;
	}

	public Float getReturn_amount() {
		return return_amount;
	}

	public void setReturn_amount(Float return_amount) {
		this.return_amount = return_amount;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public Long getCashier_id() {
		return cashier_id;
	}

	public void setCashier_id(Long cashier_id) {
		this.cashier_id = cashier_id;
	}

	public String getCashier_name() {
		return cashier_name;
	}

	public void setCashier_name(String cashier_name) {
		this.cashier_name = cashier_name;
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

	public Date getSync_date() {
		return sync_date;
	}

	public void setSync_date(Date sync_date) {
		this.sync_date = sync_date;
	}
}

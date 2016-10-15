package com.tokoku.pos.model;

import java.util.Date;

public class CashflowBean extends BaseBean {

	private String type;
	private Long bill_id;
	private Long transaction_id;
	private Float cash_amount;
	private Date cash_date;
	private String remarks;
	private String status;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getBill_id() {
		return bill_id;
	}

	public void setBill_id(Long bill_id) {
		this.bill_id = bill_id;
	}

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Float getCash_amount() {
		return cash_amount;
	}

	public void setCash_amount(Float cash_amount) {
		this.cash_amount = cash_amount;
	}

	public Date getCash_date() {
		return cash_date;
	}

	public void setCash_date(Date cash_date) {
		this.cash_date = cash_date;
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

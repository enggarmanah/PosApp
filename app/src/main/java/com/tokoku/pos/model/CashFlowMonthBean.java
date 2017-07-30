package com.tokoku.pos.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CashFlowMonthBean implements Serializable {

	private Date month;
	private Double amount;

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}

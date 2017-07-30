package com.tokoku.pos.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TransactionDayBean implements Serializable {

	private Date date;
	private Double amount;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}

package com.tokoku.pos.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TransactionYearBean implements Serializable {

	private Date year;
	private Double amount;

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}

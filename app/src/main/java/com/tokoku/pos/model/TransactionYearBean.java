package com.tokoku.pos.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TransactionYearBean implements Serializable {

	private Date year;
	private Float amount;

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}
}

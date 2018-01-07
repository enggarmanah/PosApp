package com.tokoku.pos.report.product;

import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;

public interface ProductStatisticActionListener {
	
	public void onTransactionYearSelected(TransactionYearBean transactionYear);
	
	public void onTransactionMonthSelected(TransactionMonthBean transactionMonth);
	
	public void onBackPressed();

	public void onGenerateReportStart();

	public void onGenerateReportCompleted();
}

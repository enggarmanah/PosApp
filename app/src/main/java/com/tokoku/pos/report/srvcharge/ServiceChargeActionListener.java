package com.tokoku.pos.report.srvcharge;

import com.android.pos.dao.Transactions;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;

public interface ServiceChargeActionListener {
	
	public void onTransactionYearSelected(TransactionYearBean transactionYear);
	
	public void onTransactionMonthSelected(TransactionMonthBean transactionMonth);
	
	public void onTransactionDaySelected(TransactionDayBean transactionDay);
	
	public void onTransactionSelected(Transactions transaction);
	
	public void onTransactionDeleted(Transactions transaction);
	
	public void onBackPressed();
}

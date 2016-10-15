package com.tokoku.pos.report.credit;

import com.tokoku.pos.model.TransactionsBean;

public interface CreditActionListener {
	
	public void onTransactionSelected(TransactionsBean transaction);
	
	public void onTransactionUnselected();
	
	public void onBackPressed();
}

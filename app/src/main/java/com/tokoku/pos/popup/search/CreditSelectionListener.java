package com.tokoku.pos.popup.search;

import com.tokoku.pos.model.TransactionsBean;

public interface CreditSelectionListener {
	
	public void onTransactionSelected(TransactionsBean transaction);
}

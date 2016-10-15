package com.tokoku.pos.report.bills;

import com.android.pos.dao.Bills;

public interface BillsActionListener {
	
	public void onBillSelected(Bills bill);
	
	public void onBillUnselected();
	
	public void onBackPressed();
}


package com.tokoku.pos.report.cashflow;

import com.tokoku.pos.model.CashFlowMonthBean;
import com.tokoku.pos.model.CashFlowYearBean;
import com.tokoku.pos.model.CashflowBean;

public interface CashFlowActionListener {
	
	public void onCashFlowYearSelected(CashFlowYearBean cashFlowYear);
	
	public void onCashFlowMonthSelected(CashFlowMonthBean cashFlowMonth);
	
	public void onCashFlowSelected(CashflowBean cashFlow);
	
	public void onBackPressed();
}

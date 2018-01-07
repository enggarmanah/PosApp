package com.tokoku.pos.report.commission;

import com.android.pos.dao.Employee;
import com.tokoku.pos.model.CommissionMonthBean;
import com.tokoku.pos.model.CommissionYearBean;

public interface CommissionActionListener {
	
	public void onCommissionYearSelected(CommissionYearBean transactionYear);
	
	public void onCommissionMonthSelected(CommissionMonthBean transactionMonth);
	
	public void onBackPressed();
	
	public void onEmployeeSelected(Employee employee);

	public void onGenerateReportStart();

	public void onGenerateReportCompleted();
}

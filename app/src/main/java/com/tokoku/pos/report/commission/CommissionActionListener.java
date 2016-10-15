package com.tokoku.pos.report.commission;

import com.android.pos.dao.Employee;
import com.tokoku.pos.model.CommisionMonthBean;
import com.tokoku.pos.model.CommisionYearBean;

public interface CommissionActionListener {
	
	public void onCommisionYearSelected(CommisionYearBean transactionYear);
	
	public void onCommisionMonthSelected(CommisionMonthBean transactionMonth);
	
	public void onBackPressed();
	
	public void onEmployeeSelected(Employee employee);
}

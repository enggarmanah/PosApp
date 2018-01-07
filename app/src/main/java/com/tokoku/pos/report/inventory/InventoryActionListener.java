package com.tokoku.pos.report.inventory;

import com.android.pos.dao.Product;

public interface InventoryActionListener {
	
	public void onProductSelected(Product product);
	
	public void onProductUnselected();
	
	public void cancelRefreshProductStock();
	
	public void onBackPressed();

	public void onGenerateReportStart();

	public void onGenerateReportCompleted();
}

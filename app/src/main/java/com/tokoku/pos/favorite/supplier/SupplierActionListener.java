package com.tokoku.pos.favorite.supplier;

import com.android.pos.dao.Supplier;
import com.tokoku.pos.model.SupplierStatisticBean;

public interface SupplierActionListener {
	
	public void onSupplierStatisticSelected(SupplierStatisticBean supplierStatistic);
	
	public void onSupplierStatisticUnselected();
	
	public void onBackPressed();
	
	public void showSupplierInventories(Supplier supplier);
}

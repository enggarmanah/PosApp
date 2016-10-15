package com.tokoku.pos.order;

import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;

public interface OrderActionListener {
	
	public void onOrderReferenceSelected(String item);
	
	public void onBackButtonClicked();
	
	public void onOrderItemSelected(OrderItem orderItem);
	
	public void onAddNewOrder(Orders order);
}

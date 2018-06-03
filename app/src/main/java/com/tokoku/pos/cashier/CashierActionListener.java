package com.tokoku.pos.cashier;

import com.android.pos.dao.Customer;
import com.android.pos.dao.Discount;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.android.pos.dao.Transactions;

import java.util.Date;

public interface CashierActionListener {
	
	public void onShowProductGroups();
	
	public void onProductSelected(Product product);
	
	public void onProductSelected(Product product, Float price, Employee personInCharge, Float quantity, String remarks);
	
	public void onPaymentRequested(Float totalBill);
	
	public void onOrderRequested(Integer totalOrder);
	
	public void onPaymentInfoProvided(Date transactionDate, Customer customer, String paymentType, Float totalBill, Float payment);
	
	public void onOrderInfoProvided(String orderReference, String orderType, Employee waitress, Customer customer);
	
	public void onPaymentCompleted(Transactions transaction);
	
	public void onOrderConfirmed(Orders order);
	
	public void onPrintReceipt(Transactions transaction, String printingOption);
	
	public void onPrintOrder(Orders order);
	
	public void onSelectDiscount();
	
	public void onDiscountSelected(Discount discount);
	
	public void onSelectEmployee();
	
	public void onSelectCustomer();
	
	public void onClearTransaction();
}

package com.tokoku.pos.model;

import java.util.Date;
import java.util.List;

public class SyncResponseBean {

	public static final String SUCCESS = "SUC";
	public static final String ERROR = "ERR";

	protected String respCode;
	protected String respDescription;
	protected Date respDate;
	
	protected String sync_key;
	
	protected long nextIndex;
	protected long resultCount;
	protected int syncRecordLimit;
	
	protected List<SyncStatusBean> status;
	
	protected List<String> taskHasUpdates;
	
	protected MerchantBean merchant;
	protected UserBean user;
	
	protected List<BillsBean> bills;
	protected List<CustomerBean> customers;
	protected List<CashflowBean> cashflows;
	protected List<DiscountBean> discounts;
	protected List<EmployeeBean> employees;
	protected List<InventoryBean> inventories;
	protected List<MerchantAccessBean> merchantAccesses;
	protected List<MerchantBean> merchants;
	protected List<OrderItemBean> orderItems;
	protected List<OrdersBean> orders;
	protected List<ProductBean> products; 
	protected List<ProductGroupBean> productGroups;
	protected List<SupplierBean> suppliers;
	protected List<TransactionItemBean> transactionItems;
	protected List<TransactionsBean> transactions;
	protected List<UserAccessBean> userAccesses;
	protected List<UserBean> users;
	
	public String getSync_key() {
		return sync_key;
	}

	public void setSync_key(String sync_key) {
		this.sync_key = sync_key;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String code) {
		this.respCode = code;
	}

	public String getRespDescription() {
		return respDescription;
	}

	public void setRespDescription(String description) {
		this.respDescription = description;
	}
	
	public Date getRespDate() {
		return respDate;
	}

	public void setRespDate(Date respDate) {
		this.respDate = respDate;
	}
	
	public long getNextIndex() {
		return nextIndex;
	}

	public void setNextIndex(long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public long getResultCount() {
		return resultCount;
	}

	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}
	
	public int getSyncRecordLimit() {
		return syncRecordLimit;
	}

	public void setSyncRecordLimit(int syncRecordLimit) {
		this.syncRecordLimit = syncRecordLimit;
	}

	public MerchantBean getMerchant() {
		return merchant;
	}

	public void setMerchant(MerchantBean merchant) {
		this.merchant = merchant;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public List<SyncStatusBean> getStatus() {
		return status;
	}

	public void setStatus(List<SyncStatusBean> status) {
		this.status = status;
	}
	
	public List<String> getTaskHasUpdates() {
		return taskHasUpdates;
	}

	public void setTaskHasUpdates(List<String> taskHasUpdates) {
		this.taskHasUpdates = taskHasUpdates;
	}

	public List<BillsBean> getBills() {
		return bills;
	}

	public void setBills(List<BillsBean> bills) {
		this.bills = bills;
	}

	public List<CustomerBean> getCustomers() {
		return customers;
	}

	public void setCustomers(List<CustomerBean> customers) {
		this.customers = customers;
	}
	
	public List<CashflowBean> getCashflows() {
		return cashflows;
	}

	public void setCashflows(List<CashflowBean> cashflows) {
		this.cashflows = cashflows;
	}

	public List<DiscountBean> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<DiscountBean> discounts) {
		this.discounts = discounts;
	}

	public List<EmployeeBean> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeBean> employees) {
		this.employees = employees;
	}

	public List<InventoryBean> getInventories() {
		return inventories;
	}

	public void setInventories(List<InventoryBean> inventories) {
		this.inventories = inventories;
	}

	public List<MerchantAccessBean> getMerchantAccesses() {
		return merchantAccesses;
	}

	public void setMerchantAccesses(List<MerchantAccessBean> merchantAccesses) {
		this.merchantAccesses = merchantAccesses;
	}

	public List<MerchantBean> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<MerchantBean> merchants) {
		this.merchants = merchants;
	}

	public List<OrderItemBean> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemBean> orderItems) {
		this.orderItems = orderItems;
	}

	public List<OrdersBean> getOrders() {
		return orders;
	}

	public void setOrders(List<OrdersBean> orders) {
		this.orders = orders;
	}

	public List<ProductBean> getProducts() {
		return products;
	}

	public void setProducts(List<ProductBean> products) {
		this.products = products;
	}

	public List<ProductGroupBean> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(List<ProductGroupBean> productGroups) {
		this.productGroups = productGroups;
	}

	public List<SupplierBean> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<SupplierBean> suppliers) {
		this.suppliers = suppliers;
	}

	public List<TransactionItemBean> getTransactionItems() {
		return transactionItems;
	}

	public void setTransactionItems(List<TransactionItemBean> transactionItems) {
		this.transactionItems = transactionItems;
	}

	public List<TransactionsBean> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionsBean> transactions) {
		this.transactions = transactions;
	}

	public List<UserAccessBean> getUserAccesses() {
		return userAccesses;
	}

	public void setUserAccesses(List<UserAccessBean> userAccesses) {
		this.userAccesses = userAccesses;
	}

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserBean> users) {
		this.users = users;
	}
}

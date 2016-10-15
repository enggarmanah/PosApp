package com.tokoku.pos.model;

import java.util.Date;
import java.util.List;

public class SyncRequestBean {

	protected Long merchant_id;
	protected String sync_key;
	protected Date sync_date;
	protected String uuid;
	protected String cert_dn;
	
	protected Integer appVersion;
	
	protected long index;
	protected long resultCount;
	
	protected List<String> getRequests;
	
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

	public Long getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(Long merchant_id) {
		this.merchant_id = merchant_id;
	}
	
	public String getSync_key() {
		return sync_key;
	}

	public void setSync_key(String sync_key) {
		this.sync_key = sync_key;
	}

	public Date getSync_date() {
		return sync_date;
	}

	public void setSync_date(Date sync_date) {
		this.sync_date = sync_date;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getCert_dn() {
		return cert_dn;
	}

	public void setCert_dn(String cert_dn) {
		this.cert_dn = cert_dn;
	}
	
	public Integer getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public long getResultCount() {
		return resultCount;
	}

	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}

	public List<String> getGetRequests() {
		return getRequests;
	}

	public void setGetRequests(List<String> getRequests) {
		this.getRequests = getRequests;
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

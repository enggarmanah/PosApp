package com.tokoku.pos.util;

import com.android.pos.dao.Bills;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Discount;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.MerchantAccess;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.android.pos.dao.Supplier;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.Transactions;
import com.android.pos.dao.User;
import com.android.pos.dao.UserAccess;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.BillsBean;
import com.tokoku.pos.model.CashflowBean;
import com.tokoku.pos.model.CustomerBean;
import com.tokoku.pos.model.DiscountBean;
import com.tokoku.pos.model.EmployeeBean;
import com.tokoku.pos.model.InventoryBean;
import com.tokoku.pos.model.MerchantAccessBean;
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.model.OrderItemBean;
import com.tokoku.pos.model.OrdersBean;
import com.tokoku.pos.model.ProductBean;
import com.tokoku.pos.model.ProductGroupBean;
import com.tokoku.pos.model.SupplierBean;
import com.tokoku.pos.model.TransactionItemBean;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.model.UserAccessBean;
import com.tokoku.pos.model.UserBean;

public class BeanUtil {
	
	public static ProductGroupBean getBean(ProductGroup productGroup) {
		
		ProductGroupBean bean = new ProductGroupBean();
		
		bean.setMerchant_id(productGroup.getMerchantId());
		bean.setRemote_id(productGroup.getId());
		bean.setRef_id(productGroup.getRefId());
		bean.setName(productGroup.getName());
		bean.setStatus(productGroup.getStatus());
		bean.setCreate_by(productGroup.getCreateBy());
		bean.setCreate_date(productGroup.getCreateDate());
		bean.setUpdate_by(productGroup.getUpdateBy());
		bean.setUpdate_date(productGroup.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(ProductGroup productGroup, ProductGroupBean bean) {
		
		productGroup.setMerchantId(bean.getMerchant_id());
		productGroup.setId(bean.getRemote_id());
		productGroup.setRefId(bean.getRef_id());
		productGroup.setName(bean.getName());
		productGroup.setStatus(bean.getStatus());
		productGroup.setUploadStatus(Constant.STATUS_NO);
		productGroup.setCreateBy(bean.getCreate_by());
		productGroup.setCreateDate(bean.getCreate_date());
		productGroup.setUpdateBy(bean.getUpdate_by());
		productGroup.setUpdateDate(bean.getUpdate_date());
	}
	
	public static DiscountBean getBean(Discount discount) {
		
		DiscountBean bean = new DiscountBean();
		
		bean.setMerchant_id(discount.getMerchantId());
		bean.setRemote_id(discount.getId());
		bean.setRef_id(discount.getRefId());
		bean.setName(discount.getName());
		bean.setPercentage(discount.getPercentage());
		bean.setStatus(discount.getStatus());
		bean.setCreate_by(discount.getCreateBy());
		bean.setCreate_date(discount.getCreateDate());
		bean.setUpdate_by(discount.getUpdateBy());
		bean.setUpdate_date(discount.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Discount discount, DiscountBean bean) {
		
		discount.setMerchantId(bean.getMerchant_id());
		discount.setId(bean.getRemote_id());
		discount.setRefId(bean.getRef_id());
		discount.setName(bean.getName());
		discount.setPercentage(bean.getPercentage());
		discount.setStatus(bean.getStatus());
		discount.setUploadStatus(Constant.STATUS_NO);
		discount.setCreateBy(bean.getCreate_by());
		discount.setCreateDate(bean.getCreate_date());
		discount.setUpdateBy(bean.getUpdate_by());
		discount.setUpdateDate(bean.getUpdate_date());
	}
	
	public static MerchantBean getBean(Merchant merchant) {
		
		MerchantBean bean = new MerchantBean();
		
		bean.setRemote_id(merchant.getId());
		bean.setRef_id(merchant.getRefId());
		bean.setName(merchant.getName());
		bean.setType(merchant.getType());
		bean.setAddress(merchant.getAddress());
		bean.setTelephone(merchant.getTelephone());
		bean.setContact_name(merchant.getContactName());
		bean.setContact_telephone(merchant.getContactTelephone());
		bean.setContact_email(merchant.getContactEmail());
		bean.setLogin_id(merchant.getLoginId());
		bean.setPassword(merchant.getPassword());
		bean.setPeriod_start(merchant.getPeriodStart());
		bean.setPeriod_end(merchant.getPeriodEnd());
		bean.setPrice_type_count(merchant.getPriceTypeCount());
		bean.setPrice_label_1(merchant.getPriceLabel1());
		bean.setPrice_label_2(merchant.getPriceLabel2());
		bean.setPrice_label_3(merchant.getPriceLabel3());
		bean.setSecurity_question(merchant.getSecurityQuestion());
		bean.setSecurity_answer(merchant.getSecurityAnswer());
		bean.setDiscount_type(merchant.getDiscountType());
		bean.setOrder_type(merchant.getOrderType());
		bean.setPayment_type(merchant.getPaymentType());
		bean.setTax_percentage(merchant.getTaxPercentage());
		bean.setService_charge_percentage(merchant.getServiceChargePercentage());
		bean.setPrinter_required(merchant.getPrinterRequired());
		bean.setPrinter_mini_font(merchant.getPrinterMiniFont());
		bean.setPrinter_line_size(merchant.getPrinterLineSize());
		bean.setLocale(merchant.getLocale());
		bean.setSecurity_question(merchant.getSecurityQuestion());
		bean.setSecurity_answer(merchant.getSecurityAnswer());
		bean.setStatus(merchant.getStatus());
		bean.setCreate_by(merchant.getCreateBy());
		bean.setCreate_date(merchant.getCreateDate());
		bean.setUpdate_by(merchant.getUpdateBy());
		bean.setUpdate_date(merchant.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Merchant merchant, MerchantBean bean) {
		
		merchant.setId(bean.getRemote_id());
		merchant.setRefId(bean.getRef_id());
		merchant.setName(bean.getName());
		merchant.setType(bean.getType());
		merchant.setAddress(bean.getAddress());
		merchant.setTelephone(bean.getTelephone());
		merchant.setContactName(bean.getContact_name());
		merchant.setContactTelephone(bean.getContact_telephone());
		merchant.setContactEmail(bean.getContact_email());
		merchant.setLoginId(bean.getLogin_id());
		merchant.setPassword(bean.getPassword());
		merchant.setPeriodStart(bean.getPeriod_start());
		merchant.setPeriodEnd(bean.getPeriod_end());
		merchant.setPriceTypeCount(bean.getPrice_type_count());
		merchant.setPriceLabel1(bean.getPrice_label_1());
		merchant.setPriceLabel2(bean.getPrice_label_2());
		merchant.setPriceLabel3(bean.getPrice_label_3());
		merchant.setSecurityQuestion(bean.getSecurity_question());
		merchant.setSecurityAnswer(bean.getSecurity_answer());
		merchant.setDiscountType(bean.getDiscount_type());
		merchant.setOrderType(bean.getOrder_type());
		merchant.setPaymentType(bean.getPayment_type());
		merchant.setTaxPercentage(bean.getTax_percentage());
		merchant.setServiceChargePercentage(bean.getService_charge_percentage());
		
		// retain local value unless it's empty
		if (merchant.getPrinterRequired() == null) {
			merchant.setPrinterRequired(bean.getPrinter_required());
		}
		
		if (merchant.getPrinterMiniFont() == null) {
			merchant.setPrinterMiniFont(bean.getPrinter_mini_font());
		}
		
		if (merchant.getPrinterLineSize() == null) {
			merchant.setPrinterLineSize(bean.getPrinter_line_size());
		}
		
		merchant.setLocale(bean.getLocale());
		merchant.setSecurityQuestion(bean.getSecurity_question());
		merchant.setSecurityAnswer(bean.getSecurity_answer());
		merchant.setStatus(bean.getStatus());
		merchant.setUploadStatus(Constant.STATUS_NO);
		merchant.setCreateBy(bean.getCreate_by());
		merchant.setCreateDate(bean.getCreate_date());
		merchant.setUpdateBy(bean.getUpdate_by());
		merchant.setUpdateDate(bean.getUpdate_date());
	}
	
	public static EmployeeBean getBean(Employee employee) {
		
		EmployeeBean bean = new EmployeeBean();
		
		bean.setMerchant_id(employee.getMerchantId());
		bean.setRemote_id(employee.getId());
		bean.setRef_id(employee.getRefId());
		bean.setName(employee.getName());
		bean.setTelephone(employee.getTelephone());
		bean.setAddress(employee.getAddress());
		bean.setStatus(employee.getStatus());
		bean.setCreate_by(employee.getCreateBy());
		bean.setCreate_date(employee.getCreateDate());
		bean.setUpdate_by(employee.getUpdateBy());
		bean.setUpdate_date(employee.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Employee employee, EmployeeBean bean) {
		
		employee.setMerchantId(bean.getMerchant_id());
		employee.setId(bean.getRemote_id());
		employee.setRefId(bean.getRef_id());
		employee.setName(bean.getName());
		employee.setTelephone(bean.getTelephone());
		employee.setAddress(bean.getAddress());
		employee.setStatus(bean.getStatus());
		employee.setUploadStatus(Constant.STATUS_NO);
		employee.setCreateBy(bean.getCreate_by());
		employee.setCreateDate(bean.getCreate_date());
		employee.setUpdateBy(bean.getUpdate_by());
		employee.setUpdateDate(bean.getUpdate_date());
	}
	
	public static CustomerBean getBean(Customer customer) {
		
		CustomerBean bean = new CustomerBean();
		
		bean.setMerchant_id(customer.getMerchantId());
		bean.setRemote_id(customer.getId());
		bean.setRef_id(customer.getRefId());
		bean.setName(customer.getName());
		bean.setTelephone(customer.getTelephone());
		bean.setEmail(customer.getEmail());
		bean.setEmail_status(customer.getEmailStatus());
		bean.setAddress(customer.getAddress());
		bean.setStatus(customer.getStatus());
		bean.setCreate_by(customer.getCreateBy());
		bean.setCreate_date(customer.getCreateDate());
		bean.setUpdate_by(customer.getUpdateBy());
		bean.setUpdate_date(customer.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Customer customer, CustomerBean bean) {
		
		customer.setMerchantId(bean.getMerchant_id());
		customer.setId(bean.getRemote_id());
		customer.setRefId(bean.getRef_id());
		customer.setName(bean.getName());
		customer.setTelephone(bean.getTelephone());
		customer.setEmail(bean.getEmail());
		customer.setEmailStatus(bean.getEmail_status());
		customer.setAddress(bean.getAddress());
		customer.setStatus(bean.getStatus());
		customer.setUploadStatus(Constant.STATUS_NO);
		customer.setCreateBy(bean.getCreate_by());
		customer.setCreateDate(bean.getCreate_date());
		customer.setUpdateBy(bean.getUpdate_by());
		customer.setUpdateDate(bean.getUpdate_date());
	}
	
	public static ProductBean getBean(Product product) {
		
		ProductBean bean = new ProductBean();
		
		bean.setMerchant_id(product.getMerchantId());
		bean.setRemote_id(product.getId());
		bean.setRef_id(product.getRefId());
		bean.setProduct_group_id(product.getProductGroupId());
		bean.setCode(product.getCode());
		bean.setName(product.getName());
		bean.setType(product.getType());
		bean.setPrice_1(product.getPrice1());
		bean.setPrice_2(product.getPrice2());
		bean.setPrice_3(product.getPrice3());
		bean.setCost_price(product.getCostPrice());
		bean.setPic_required(product.getPicRequired());
		bean.setCommision(product.getCommision());
		bean.setPromo_price(product.getPromoPrice());
		bean.setPromo_start(product.getPromoStart());
		bean.setPromo_end(product.getPromoEnd());
		bean.setQuantity_type(product.getQuantityType());
		bean.setStock(product.getStock());
		bean.setMin_stock(product.getMinStock());
		bean.setStatus(product.getStatus());
		bean.setCreate_by(product.getCreateBy());
		bean.setCreate_date(product.getCreateDate());
		bean.setUpdate_by(product.getUpdateBy());
		bean.setUpdate_date(product.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Product product, ProductBean bean) {
		
		product.setMerchantId(bean.getMerchant_id());
		product.setId(bean.getRemote_id());
		product.setRefId(bean.getRef_id());
		product.setProductGroupId(bean.getProduct_group_id());
		product.setCode(bean.getCode());
		product.setName(bean.getName());
		product.setType(bean.getType());
		product.setPrice1(bean.getPrice_1());
		product.setPrice2(bean.getPrice_2());
		product.setPrice3(bean.getPrice_3());
		product.setCostPrice(bean.getCost_price());
		product.setPicRequired(bean.getPic_required());
		product.setCommision(bean.getCommision());
		product.setPromoPrice(bean.getPromo_price());
		product.setPromoStart(bean.getPromo_start());
		product.setPromoEnd(bean.getPromo_end());
		product.setQuantityType(bean.getQuantity_type());
		product.setStock(bean.getStock());
		product.setMinStock(bean.getMin_stock());
		product.setStatus(bean.getStatus());
		product.setUploadStatus(Constant.STATUS_NO);
		product.setCreateBy(bean.getCreate_by());
		product.setCreateDate(bean.getCreate_date());
		product.setUpdateBy(bean.getUpdate_by());
		product.setUpdateDate(bean.getUpdate_date());
	}
	
	public static UserBean getBean(User user) {
		
		UserBean bean = new UserBean();
		
		bean.setMerchant_id(user.getMerchantId());
		bean.setRemote_id(user.getId());
		bean.setRef_id(user.getRefId());
		bean.setName(user.getName());
		bean.setUser_id(user.getUserId());
		bean.setPassword(user.getPassword());
		bean.setRole(user.getRole());
		bean.setEmployee_id(user.getEmployeeId());
		bean.setStatus(user.getStatus());
		bean.setCreate_by(user.getCreateBy());
		bean.setCreate_date(user.getCreateDate());
		bean.setUpdate_by(user.getUpdateBy());
		bean.setUpdate_date(user.getUpdateDate());
		
		return bean;
	}
	
    public static void updateBean(User user, UserBean bean) {
		
		user.setMerchantId(bean.getMerchant_id());
		user.setId(bean.getRemote_id());
		user.setRefId(bean.getRef_id());
		user.setName(bean.getName());
		user.setUserId(bean.getUser_id());
		user.setPassword(bean.getPassword());
		user.setRole(bean.getRole());
		user.setEmployeeId(bean.getEmployee_id());
		user.setStatus(bean.getStatus());
		user.setUploadStatus(Constant.STATUS_NO);
		user.setCreateBy(bean.getCreate_by());
		user.setCreateDate(bean.getCreate_date());
		user.setUpdateBy(bean.getUpdate_by());
		user.setUpdateDate(bean.getUpdate_date());
	}
    
    public static TransactionsBean getBean(Transactions transactions) {
		
		TransactionsBean bean = new TransactionsBean();
		
		bean.setMerchant_id(transactions.getMerchantId());
		bean.setRemote_id(transactions.getId());
		bean.setRef_id(transactions.getRefId());
		bean.setTransaction_no(transactions.getTransactionNo());
		bean.setTransaction_date(transactions.getTransactionDate());
		bean.setOrder_type(transactions.getOrderType());
		bean.setOrder_reference(transactions.getOrderReference());
		bean.setBill_amount(transactions.getBillAmount());
		bean.setDiscount_name(transactions.getDiscountName());
		bean.setDiscount_percentage(transactions.getDiscountPercentage());
		bean.setDiscount_amount(transactions.getDiscountAmount());
		bean.setTax_percentage(transactions.getTaxPercentage());
		bean.setTax_amount(transactions.getTaxAmount());
		bean.setService_charge_percentage(transactions.getServiceChargePercentage());
		bean.setService_charge_amount(transactions.getServiceChargeAmount());
		bean.setTotal_amount(transactions.getTotalAmount());
		bean.setPayment_amount(transactions.getPaymentAmount());
		bean.setReturn_amount(transactions.getReturnAmount());
		bean.setPayment_type(transactions.getPaymentType());
		bean.setCashier_id(transactions.getCashierId());
		bean.setCashier_name(transactions.getCashierName());
		bean.setWaitress_id(transactions.getWaitressId());
		bean.setWaitress_name(transactions.getWaitressName());
		bean.setCustomer_id(transactions.getCustomerId());
		bean.setCustomer_name(transactions.getCustomerName());
		bean.setStatus(transactions.getStatus());
		
		return bean;
	}
    
	public static void updateBean(Transactions transactions, TransactionsBean bean) {
		
		transactions.setMerchantId(bean.getMerchant_id());
		transactions.setId(bean.getRemote_id());
		transactions.setRefId(bean.getRef_id());
		transactions.setTransactionNo(bean.getTransaction_no());
		transactions.setTransactionDate(bean.getTransaction_date());
		transactions.setOrderType(bean.getOrder_type());
		transactions.setOrderReference(bean.getOrder_reference());
		transactions.setBillAmount(bean.getBill_amount());
		transactions.setDiscountName(bean.getDiscount_name());
		transactions.setDiscountPercentage(bean.getDiscount_percentage());
		transactions.setDiscountAmount(bean.getDiscount_amount());
		transactions.setTaxPercentage(bean.getTax_percentage());
		transactions.setTaxAmount(bean.getTax_amount());
		transactions.setServiceChargePercentage(bean.getService_charge_percentage());
		transactions.setServiceChargeAmount(bean.getService_charge_amount());
		transactions.setTotalAmount(bean.getTotal_amount());
		transactions.setPaymentAmount(bean.getPayment_amount());
		transactions.setReturnAmount(bean.getReturn_amount());
		transactions.setPaymentType(bean.getPayment_type());
		transactions.setCashierId(bean.getCashier_id());
		transactions.setCashierName(bean.getCashier_name());
		transactions.setWaitressId(bean.getWaitress_id());
		transactions.setWaitressName(bean.getWaitress_name());
		transactions.setCustomerId(bean.getCustomer_id());
		transactions.setCustomerName(bean.getCustomer_name());
		transactions.setUploadStatus(Constant.STATUS_NO);
		transactions.setStatus(bean.getStatus());
	}
	
    public static TransactionItemBean getBean(TransactionItem transactionItem) {
		
		TransactionItemBean bean = new TransactionItemBean();
		
		bean.setMerchant_id(transactionItem.getMerchantId());
		bean.setRemote_id(transactionItem.getId());
		bean.setRef_id(transactionItem.getRefId());
		bean.setTransaction_id(transactionItem.getTransactionId());
		bean.setProduct_id(transactionItem.getProductId());
		bean.setProduct_name(transactionItem.getProductName());
		bean.setProduct_type(transactionItem.getProductType());
		bean.setPrice(transactionItem.getPrice());
		bean.setCost_price(transactionItem.getCostPrice());
		bean.setDiscount(transactionItem.getDiscount());
		bean.setQuantity(transactionItem.getQuantity());
		bean.setCommision(transactionItem.getCommision());
		bean.setEmployee_id(transactionItem.getEmployeeId());
		
		return bean;
	}
    
	public static void updateBean(TransactionItem transactionItem, TransactionItemBean bean) {
		
		transactionItem.setMerchantId(bean.getMerchant_id());
		transactionItem.setId(bean.getRemote_id());
		transactionItem.setRefId(bean.getRef_id());
		transactionItem.setTransactionId(bean.getTransaction_id());
		transactionItem.setProductId(bean.getProduct_id());
		transactionItem.setProductName(bean.getProduct_name());
		transactionItem.setProductType(bean.getProduct_type());
		transactionItem.setPrice(bean.getPrice());
		transactionItem.setCostPrice(bean.getCost_price());
		transactionItem.setDiscount(bean.getDiscount());
		transactionItem.setQuantity(bean.getQuantity());
		transactionItem.setCommision(bean.getCommision());
		transactionItem.setEmployeeId(bean.getEmployee_id());
		transactionItem.setUploadStatus(Constant.STATUS_NO);
	}
	
	public static SupplierBean getBean(Supplier supplier) {
		
		SupplierBean bean = new SupplierBean();
		
		bean.setMerchant_id(supplier.getMerchantId());
		bean.setRemote_id(supplier.getId());
		bean.setRef_id(supplier.getRefId());
		bean.setName(supplier.getName());
		bean.setTelephone(supplier.getTelephone());
		bean.setAddress(supplier.getAddress());
		bean.setPic_name(supplier.getPicName());
		bean.setPic_telephone(supplier.getPicTelephone());
		bean.setRemarks(supplier.getRemarks());
		bean.setStatus(supplier.getStatus());
		bean.setCreate_by(supplier.getCreateBy());
		bean.setCreate_date(supplier.getCreateDate());
		bean.setUpdate_by(supplier.getUpdateBy());
		bean.setUpdate_date(supplier.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Supplier supplier, SupplierBean bean) {
		
		supplier.setMerchantId(bean.getMerchant_id());
		supplier.setId(bean.getRemote_id());
		supplier.setRefId(bean.getRef_id());
		supplier.setName(bean.getName());
		supplier.setTelephone(bean.getTelephone());
		supplier.setAddress(bean.getAddress());
		supplier.setPicName(bean.getPic_name());
		supplier.setPicTelephone(bean.getPic_telephone());
		supplier.setStatus(bean.getStatus());
		supplier.setRemarks(bean.getRemarks());
		supplier.setUploadStatus(Constant.STATUS_NO);
		supplier.setCreateBy(bean.getCreate_by());
		supplier.setCreateDate(bean.getCreate_date());
		supplier.setUpdateBy(bean.getUpdate_by());
		supplier.setUpdateDate(bean.getUpdate_date());
	}
	
	public static InventoryBean getBean(Inventory inventory) {
		
		InventoryBean bean = new InventoryBean();
		
		bean.setMerchant_id(inventory.getMerchantId());
		bean.setRemote_id(inventory.getId());
		bean.setRef_id(inventory.getRefId());
		bean.setProduct_id(inventory.getProductId());
		bean.setProduct_name(inventory.getProductName());
		bean.setProduct_cost_price(inventory.getProductCostPrice());
		bean.setQuantity(inventory.getQuantity());
		bean.setBill_id(inventory.getBillId());
		bean.setBill_reference_no(inventory.getBillReferenceNo());
		bean.setSupplier_id(inventory.getSupplierId());
		bean.setSupplier_name(inventory.getSupplierName());
		bean.setInventory_date(inventory.getInventoryDate());
		bean.setRemarks(inventory.getRemarks());
		bean.setStatus(inventory.getStatus());
		bean.setCreate_by(inventory.getCreateBy());
		bean.setCreate_date(inventory.getCreateDate());
		bean.setUpdate_by(inventory.getUpdateBy());
		bean.setUpdate_date(inventory.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Inventory inventory, InventoryBean bean) {
		
		inventory.setMerchantId(bean.getMerchant_id());
		inventory.setId(bean.getRemote_id());
		inventory.setRefId(bean.getRef_id());
		inventory.setProductId(bean.getProduct_id());
		inventory.setProductName(bean.getProduct_name());
		inventory.setProductCostPrice(bean.getProduct_cost_price());
		inventory.setQuantity(bean.getQuantity());
		inventory.setBillId(bean.getBill_id());
		inventory.setBillReferenceNo(bean.getBill_reference_no());
		inventory.setSupplierId(bean.getSupplier_id());
		inventory.setSupplierName(bean.getSupplier_name());
		inventory.setInventoryDate(bean.getInventory_date());
		inventory.setRemarks(bean.getRemarks());
		inventory.setStatus(bean.getStatus());
		inventory.setUploadStatus(Constant.STATUS_NO);
		inventory.setCreateBy(bean.getCreate_by());
		inventory.setCreateDate(bean.getCreate_date());
		inventory.setUpdateBy(bean.getUpdate_by());
		inventory.setUpdateDate(bean.getUpdate_date());
	}
	
	public static BillsBean getBean(Bills bills) {
		
		BillsBean bean = new BillsBean();
		
		bean.setMerchant_id(bills.getMerchantId());
		bean.setRemote_id(bills.getId());
		bean.setRef_id(bills.getRefId());
		bean.setSupplier_id(bills.getSupplierId());
		bean.setSupplier_name(bills.getSupplierName());
		bean.setBill_type(bills.getBillType());
		bean.setBill_reference_no(bills.getBillReferenceNo());
		bean.setBill_date(bills.getBillDate());
		bean.setBill_due_date(bills.getBillDueDate());
		bean.setBill_amount(bills.getBillAmount());
		bean.setStatus(bills.getStatus());
		bean.setPayment_date(bills.getPaymentDate());
		bean.setPayment(bills.getPayment());
		
		bean.setDelivery_date(bills.getDeliveryDate());
		bean.setRemarks(bills.getRemarks());
		bean.setStatus(bills.getStatus());
		
		bean.setCreate_by(bills.getCreateBy());
		bean.setCreate_date(bills.getCreateDate());
		bean.setUpdate_by(bills.getUpdateBy());
		bean.setUpdate_date(bills.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Bills bills, BillsBean bean) {
		
		bills.setMerchantId(bean.getMerchant_id());
		bills.setId(bean.getRemote_id());
		bills.setRefId(bean.getRef_id());
		bills.setSupplierId(bean.getSupplier_id());
		bills.setSupplierName(bean.getSupplier_name());
		bills.setBillType(bean.getBill_type());
		bills.setBillReferenceNo(bean.getBill_reference_no());
		bills.setBillDate(bean.getBill_date());
		bills.setBillDueDate(bean.getBill_due_date());
		bills.setBillAmount(bean.getBill_amount());
		bills.setStatus(bean.getStatus());
		bills.setPaymentDate(bean.getPayment_date());
		bills.setPayment(bean.getPayment());
		
		bills.setDeliveryDate(bean.getDelivery_date());
		bills.setRemarks(bean.getRemarks());
		bills.setStatus(bean.getStatus());
		
		bills.setUploadStatus(Constant.STATUS_NO);
		bills.setCreateBy(bean.getCreate_by());
		bills.setCreateDate(bean.getCreate_date());
		bills.setUpdateBy(bean.getUpdate_by());
		bills.setUpdateDate(bean.getUpdate_date());
	}
	
	public static CashflowBean getBean(Cashflow cashflow) {
		
		CashflowBean bean = new CashflowBean();
		
		bean.setMerchant_id(cashflow.getMerchantId());
		bean.setRemote_id(cashflow.getId());
		bean.setRef_id(cashflow.getRefId());
		bean.setType(cashflow.getType());
		bean.setBill_id(cashflow.getBillId());
		bean.setTransaction_id(cashflow.getTransactionId());
		bean.setCash_amount(cashflow.getCashAmount());
		bean.setCash_date(cashflow.getCashDate());
		bean.setRemarks(cashflow.getRemarks());
		bean.setStatus(cashflow.getStatus());
		
		bean.setCreate_by(cashflow.getCreateBy());
		bean.setCreate_date(cashflow.getCreateDate());
		bean.setUpdate_by(cashflow.getUpdateBy());
		bean.setUpdate_date(cashflow.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(Cashflow cashflow, CashflowBean bean) {
		
		cashflow.setMerchantId(bean.getMerchant_id());
		cashflow.setId(bean.getRemote_id());
		cashflow.setRefId(bean.getRef_id());
		
		cashflow.setType(bean.getType());
		cashflow.setBillId(bean.getBill_id());
		cashflow.setTransactionId(bean.getTransaction_id());
		
		cashflow.setCashAmount(bean.getCash_amount());
		cashflow.setCashDate(bean.getCash_date());
		cashflow.setRemarks(bean.getRemarks());
		cashflow.setStatus(bean.getStatus());
		
		cashflow.setUploadStatus(Constant.STATUS_NO);
		cashflow.setCreateBy(bean.getCreate_by());
		cashflow.setCreateDate(bean.getCreate_date());
		cashflow.setUpdateBy(bean.getUpdate_by());
		cashflow.setUpdateDate(bean.getUpdate_date());
	}
	
	public static MerchantAccessBean getBean(MerchantAccess merchantAccess) {
		
		MerchantAccessBean bean = new MerchantAccessBean();
		
		bean.setMerchant_id(merchantAccess.getMerchantId());
		bean.setRemote_id(merchantAccess.getId());
		bean.setRef_id(merchantAccess.getRefId());
		bean.setName(merchantAccess.getName());
		bean.setCode(merchantAccess.getCode());
		bean.setStatus(merchantAccess.getStatus());
		bean.setCreate_by(merchantAccess.getCreateBy());
		bean.setCreate_date(merchantAccess.getCreateDate());
		bean.setUpdate_by(merchantAccess.getUpdateBy());
		bean.setUpdate_date(merchantAccess.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(MerchantAccess merchantAccess, MerchantAccessBean bean) {
		
		merchantAccess.setMerchantId(bean.getMerchant_id());
		merchantAccess.setId(bean.getRemote_id());
		merchantAccess.setRefId(bean.getRef_id());
		merchantAccess.setName(bean.getName());
		merchantAccess.setCode(bean.getCode());
		merchantAccess.setStatus(bean.getStatus());
		merchantAccess.setUploadStatus(Constant.STATUS_NO);
		merchantAccess.setCreateBy(bean.getCreate_by());
		merchantAccess.setCreateDate(bean.getCreate_date());
		merchantAccess.setUpdateBy(bean.getUpdate_by());
		merchantAccess.setUpdateDate(bean.getUpdate_date());
	}
	
	public static UserAccessBean getBean(UserAccess userAccess) {
		
		UserAccessBean bean = new UserAccessBean();
		
		bean.setMerchant_id(userAccess.getMerchantId());
		bean.setRemote_id(userAccess.getId());
		bean.setRef_id(userAccess.getRefId());
		bean.setUser_id(userAccess.getUserId());
		bean.setName(userAccess.getName());
		bean.setCode(userAccess.getCode());
		bean.setStatus(userAccess.getStatus());
		bean.setCreate_by(userAccess.getCreateBy());
		bean.setCreate_date(userAccess.getCreateDate());
		bean.setUpdate_by(userAccess.getUpdateBy());
		bean.setUpdate_date(userAccess.getUpdateDate());
		
		return bean;
	}
	
	public static void updateBean(UserAccess userAccess, UserAccessBean bean) {
		
		userAccess.setMerchantId(bean.getMerchant_id());
		userAccess.setId(bean.getRemote_id());
		userAccess.setRefId(bean.getRef_id());
		userAccess.setUserId(bean.getUser_id());
		userAccess.setName(bean.getName());
		userAccess.setCode(bean.getCode());
		userAccess.setStatus(bean.getStatus());
		userAccess.setUploadStatus(Constant.STATUS_NO);
		userAccess.setCreateBy(bean.getCreate_by());
		userAccess.setCreateDate(bean.getCreate_date());
		userAccess.setUpdateBy(bean.getUpdate_by());
		userAccess.setUpdateDate(bean.getUpdate_date());
	}
	
	public static OrdersBean getBean(Orders orders) {
		
		OrdersBean bean = new OrdersBean();
		
		bean.setMerchant_id(orders.getMerchantId());
		bean.setRemote_id(orders.getId());
		bean.setRef_id(orders.getRefId());
		bean.setOrder_no(orders.getOrderNo());
		bean.setOrder_date(orders.getOrderDate());
		bean.setOrder_type(orders.getOrderType());
		bean.setOrder_reference(orders.getOrderReference());
		bean.setWaitress_id(orders.getWaitressId());
		bean.setWaitress_name(orders.getWaitressName());
		bean.setCustomer_id(orders.getCustomerId());
		bean.setCustomer_name(orders.getCustomerName());
		bean.setStatus(orders.getStatus());
		
		return bean;
	}
	
    public static void updateBean(Orders orders, OrdersBean bean) {
		
		orders.setMerchantId(bean.getMerchant_id());
		orders.setId(bean.getRemote_id());
		orders.setRefId(bean.getRef_id());
		orders.setOrderNo(bean.getOrder_no());
		orders.setOrderDate(bean.getOrder_date());
		orders.setOrderType(bean.getOrder_type());
		orders.setOrderReference(bean.getOrder_reference());
		orders.setWaitressId(bean.getWaitress_id());
		orders.setWaitressName(bean.getWaitress_name());
		orders.setCustomerId(bean.getCustomer_id());
		orders.setCustomerName(bean.getCustomer_name());
		orders.setStatus(bean.getStatus());
		orders.setUploadStatus(Constant.STATUS_NO);
	}
    
    public static OrderItemBean getBean(OrderItem orderItem) {
		
    	OrderItemBean bean = new OrderItemBean();
		
		bean.setMerchant_id(orderItem.getMerchantId());
		bean.setRemote_id(orderItem.getId());
		bean.setRef_id(orderItem.getRefId());
		bean.setOrder_id(orderItem.getOrderId());
		bean.setOrder_no(orderItem.getOrderNo());
		bean.setProduct_id(orderItem.getProductId());
		bean.setProduct_name(orderItem.getProductName());
		bean.setQuantity(orderItem.getQuantity());
		bean.setRemarks(orderItem.getRemarks());
		bean.setEmployee_id(orderItem.getEmployeeId());
		
		return bean;
	}
	
    public static void updateBean(OrderItem orderItem, OrderItemBean bean) {
		
		orderItem.setMerchantId(bean.getMerchant_id());
		orderItem.setId(bean.getRemote_id());
		orderItem.setRefId(bean.getRef_id());
		orderItem.setOrderId(bean.getOrder_id());
		orderItem.setOrderNo(bean.getOrder_no());
		orderItem.setProductId(bean.getProduct_id());
		orderItem.setProductName(bean.getProduct_name());
		orderItem.setQuantity(bean.getQuantity());
		orderItem.setRemarks(bean.getRemarks());
		orderItem.setEmployeeId(bean.getEmployee_id());
		orderItem.setUploadStatus(Constant.STATUS_NO);
    }
}

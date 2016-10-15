package com.android.pos.dao;

import java.io.Serializable;

import com.android.pos.dao.DaoSession;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TRANSACTION_ITEM.
 */
@SuppressWarnings("serial")
public class TransactionItem implements Serializable {

    private Long id;
    private String refId;
    private long merchantId;
    private long transactionId;
    private long productId;
    private String productName;
    private String productType;
    private Float price;
    private Float costPrice;
    private Float discount;
    private Float quantity;
    private Float commision;
    private String remarks;
    private Long employeeId;
    private String uploadStatus;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TransactionItemDao myDao;

    private Merchant merchant;
    private Long merchant__resolvedKey;

    private Transactions transactions;
    private Long transactions__resolvedKey;

    private Product product;
    private Long product__resolvedKey;

    private Employee employee;
    private Long employee__resolvedKey;


    public TransactionItem() {
    }

    public TransactionItem(Long id) {
        this.id = id;
    }

    public TransactionItem(Long id, String refId, long merchantId, long transactionId, long productId, String productName, String productType, Float price, Float costPrice, Float discount, Float quantity, Float commision, String remarks, Long employeeId, String uploadStatus) {
        this.id = id;
        this.refId = refId;
        this.merchantId = merchantId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.price = price;
        this.costPrice = costPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.commision = commision;
        this.remarks = remarks;
        this.employeeId = employeeId;
        this.uploadStatus = uploadStatus;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTransactionItemDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getCommision() {
        return commision;
    }

    public void setCommision(Float commision) {
        this.commision = commision;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    /** To-one relationship, resolved on first access. */
    public Merchant getMerchant() {
        long __key = this.merchantId;
        if (merchant__resolvedKey == null || !merchant__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MerchantDao targetDao = daoSession.getMerchantDao();
            Merchant merchantNew = targetDao.load(__key);
            synchronized (this) {
                merchant = merchantNew;
            	merchant__resolvedKey = __key;
            }
        }
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        if (merchant == null) {
            throw new DaoException("To-one property 'merchantId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.merchant = merchant;
            merchantId = merchant.getId();
            merchant__resolvedKey = merchantId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Transactions getTransactions() {
        long __key = this.transactionId;
        if (transactions__resolvedKey == null || !transactions__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TransactionsDao targetDao = daoSession.getTransactionsDao();
            Transactions transactionsNew = targetDao.load(__key);
            synchronized (this) {
                transactions = transactionsNew;
            	transactions__resolvedKey = __key;
            }
        }
        return transactions;
    }

    public void setTransactions(Transactions transactions) {
        if (transactions == null) {
            throw new DaoException("To-one property 'transactionId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.transactions = transactions;
            transactionId = transactions.getId();
            transactions__resolvedKey = transactionId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Product getProduct() {
        long __key = this.productId;
        if (product__resolvedKey == null || !product__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductDao targetDao = daoSession.getProductDao();
            Product productNew = targetDao.load(__key);
            synchronized (this) {
                product = productNew;
            	product__resolvedKey = __key;
            }
        }
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new DaoException("To-one property 'productId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.product = product;
            productId = product.getId();
            product__resolvedKey = productId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Employee getEmployee() {
        Long __key = this.employeeId;
        if (employee__resolvedKey == null || !employee__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EmployeeDao targetDao = daoSession.getEmployeeDao();
            Employee employeeNew = targetDao.load(__key);
            synchronized (this) {
                employee = employeeNew;
            	employee__resolvedKey = __key;
            }
        }
        return employee;
    }

    public void setEmployee(Employee employee) {
        synchronized (this) {
            this.employee = employee;
            employeeId = employee == null ? null : employee.getId();
            employee__resolvedKey = employeeId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}

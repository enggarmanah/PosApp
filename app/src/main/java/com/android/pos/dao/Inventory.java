package com.android.pos.dao;

import java.io.Serializable;

import com.android.pos.dao.DaoSession;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table INVENTORY.
 */
@SuppressWarnings("serial")
public class Inventory implements Serializable {

    private Long id;
    private String refId;
    private long merchantId;
    private long productId;
    private String productName;
    private Float productCostPrice;
    private Float quantity;
    private Long billId;
    private String billReferenceNo;
    private Long supplierId;
    private String supplierName;
    private java.util.Date inventoryDate;
    private String remarks;
    private String status;
    private String uploadStatus;
    private String createBy;
    private java.util.Date createDate;
    private String updateBy;
    private java.util.Date updateDate;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient InventoryDao myDao;

    private Merchant merchant;
    private Long merchant__resolvedKey;

    private Product product;
    private Long product__resolvedKey;

    private Bills bills;
    private Long bills__resolvedKey;

    private Supplier supplier;
    private Long supplier__resolvedKey;


    public Inventory() {
    }

    public Inventory(Long id) {
        this.id = id;
    }

    public Inventory(Long id, String refId, long merchantId, long productId, String productName, Float productCostPrice, Float quantity, Long billId, String billReferenceNo, Long supplierId, String supplierName, java.util.Date inventoryDate, String remarks, String status, String uploadStatus, String createBy, java.util.Date createDate, String updateBy, java.util.Date updateDate) {
        this.id = id;
        this.refId = refId;
        this.merchantId = merchantId;
        this.productId = productId;
        this.productName = productName;
        this.productCostPrice = productCostPrice;
        this.quantity = quantity;
        this.billId = billId;
        this.billReferenceNo = billReferenceNo;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.inventoryDate = inventoryDate;
        this.remarks = remarks;
        this.status = status;
        this.uploadStatus = uploadStatus;
        this.createBy = createBy;
        this.createDate = createDate;
        this.updateBy = updateBy;
        this.updateDate = updateDate;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInventoryDao() : null;
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

    public Float getProductCostPrice() {
        return productCostPrice;
    }

    public void setProductCostPrice(Float productCostPrice) {
        this.productCostPrice = productCostPrice;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillReferenceNo() {
        return billReferenceNo;
    }

    public void setBillReferenceNo(String billReferenceNo) {
        this.billReferenceNo = billReferenceNo;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public java.util.Date getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(java.util.Date inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public java.util.Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(java.util.Date updateDate) {
        this.updateDate = updateDate;
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
    public Bills getBills() {
        Long __key = this.billId;
        if (bills__resolvedKey == null || !bills__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BillsDao targetDao = daoSession.getBillsDao();
            Bills billsNew = targetDao.load(__key);
            synchronized (this) {
                bills = billsNew;
            	bills__resolvedKey = __key;
            }
        }
        return bills;
    }

    public void setBills(Bills bills) {
        synchronized (this) {
            this.bills = bills;
            billId = bills == null ? null : bills.getId();
            bills__resolvedKey = billId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Supplier getSupplier() {
        Long __key = this.supplierId;
        if (supplier__resolvedKey == null || !supplier__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SupplierDao targetDao = daoSession.getSupplierDao();
            Supplier supplierNew = targetDao.load(__key);
            synchronized (this) {
                supplier = supplierNew;
            	supplier__resolvedKey = __key;
            }
        }
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        synchronized (this) {
            this.supplier = supplier;
            supplierId = supplier == null ? null : supplier.getId();
            supplier__resolvedKey = supplierId;
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

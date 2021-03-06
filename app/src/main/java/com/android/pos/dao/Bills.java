package com.android.pos.dao;

import java.io.Serializable;
import java.util.List;
import com.android.pos.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table BILLS.
 */
public class Bills implements Serializable {

    private Long id;
    private String refId;
    private long merchantId;
    private String billReferenceNo;
    private String billType;
    private java.util.Date billDate;
    private java.util.Date billDueDate;
    private Float billAmount;
    private java.util.Date paymentDate;
    private Float payment;
    private String deliveryNo;
    private Long supplierId;
    private String supplierName;
    private java.util.Date deliveryDate;
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
    private transient BillsDao myDao;

    private Merchant merchant;
    private Long merchant__resolvedKey;

    private Supplier supplier;
    private Long supplier__resolvedKey;

    private List<Cashflow> cashflowList;
    private List<Inventory> inventoryList;

    public Bills() {
    }

    public Bills(Long id) {
        this.id = id;
    }

    public Bills(Long id, String refId, long merchantId, String billReferenceNo, String billType, java.util.Date billDate, java.util.Date billDueDate, Float billAmount, java.util.Date paymentDate, Float payment, String deliveryNo, Long supplierId, String supplierName, java.util.Date deliveryDate, String remarks, String status, String uploadStatus, String createBy, java.util.Date createDate, String updateBy, java.util.Date updateDate) {
        this.id = id;
        this.refId = refId;
        this.merchantId = merchantId;
        this.billReferenceNo = billReferenceNo;
        this.billType = billType;
        this.billDate = billDate;
        this.billDueDate = billDueDate;
        this.billAmount = billAmount;
        this.paymentDate = paymentDate;
        this.payment = payment;
        this.deliveryNo = deliveryNo;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.deliveryDate = deliveryDate;
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
        myDao = daoSession != null ? daoSession.getBillsDao() : null;
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

    public String getBillReferenceNo() {
        return billReferenceNo;
    }

    public void setBillReferenceNo(String billReferenceNo) {
        this.billReferenceNo = billReferenceNo;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public java.util.Date getBillDate() {
        return billDate;
    }

    public void setBillDate(java.util.Date billDate) {
        this.billDate = billDate;
    }

    public java.util.Date getBillDueDate() {
        return billDueDate;
    }

    public void setBillDueDate(java.util.Date billDueDate) {
        this.billDueDate = billDueDate;
    }

    public Float getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Float billAmount) {
        this.billAmount = billAmount;
    }

    public java.util.Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(java.util.Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Float getPayment() {
        return payment;
    }

    public void setPayment(Float payment) {
        this.payment = payment;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
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

    public java.util.Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(java.util.Date deliveryDate) {
        this.deliveryDate = deliveryDate;
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

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Cashflow> getCashflowList() {
        if (cashflowList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CashflowDao targetDao = daoSession.getCashflowDao();
            List<Cashflow> cashflowListNew = targetDao._queryBills_CashflowList(id);
            synchronized (this) {
                if(cashflowList == null) {
                    cashflowList = cashflowListNew;
                }
            }
        }
        return cashflowList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCashflowList() {
        cashflowList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Inventory> getInventoryList() {
        if (inventoryList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InventoryDao targetDao = daoSession.getInventoryDao();
            List<Inventory> inventoryListNew = targetDao._queryBills_InventoryList(id);
            synchronized (this) {
                if(inventoryList == null) {
                    inventoryList = inventoryListNew;
                }
            }
        }
        return inventoryList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInventoryList() {
        inventoryList = null;
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

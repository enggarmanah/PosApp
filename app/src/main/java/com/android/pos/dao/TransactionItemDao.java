package com.android.pos.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.android.pos.dao.TransactionItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TRANSACTION_ITEM.
*/
public class TransactionItemDao extends AbstractDao<TransactionItem, Long> {

    public static final String TABLENAME = "TRANSACTION_ITEM";

    /**
     * Properties of entity TransactionItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RefId = new Property(1, String.class, "refId", false, "REF_ID");
        public final static Property MerchantId = new Property(2, long.class, "merchantId", false, "MERCHANT_ID");
        public final static Property TransactionId = new Property(3, long.class, "transactionId", false, "TRANSACTION_ID");
        public final static Property ProductId = new Property(4, long.class, "productId", false, "PRODUCT_ID");
        public final static Property ProductName = new Property(5, String.class, "productName", false, "PRODUCT_NAME");
        public final static Property ProductType = new Property(6, String.class, "productType", false, "PRODUCT_TYPE");
        public final static Property Price = new Property(7, Float.class, "price", false, "PRICE");
        public final static Property CostPrice = new Property(8, Float.class, "costPrice", false, "COST_PRICE");
        public final static Property Discount = new Property(9, Float.class, "discount", false, "DISCOUNT");
        public final static Property Quantity = new Property(10, Float.class, "quantity", false, "QUANTITY");
        public final static Property Commision = new Property(11, Float.class, "commision", false, "COMMISION");
        public final static Property Remarks = new Property(12, String.class, "remarks", false, "REMARKS");
        public final static Property EmployeeId = new Property(13, Long.class, "employeeId", false, "EMPLOYEE_ID");
        public final static Property UploadStatus = new Property(14, String.class, "uploadStatus", false, "UPLOAD_STATUS");
    };

    private DaoSession daoSession;

    private Query<TransactionItem> transactions_TransactionItemListQuery;
    private Query<TransactionItem> employee_TransactionItemListQuery;
    private Query<TransactionItem> product_TransactionItemListQuery;

    public TransactionItemDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TRANSACTION_ITEM' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'REF_ID' TEXT," + // 1: refId
                "'MERCHANT_ID' INTEGER NOT NULL ," + // 2: merchantId
                "'TRANSACTION_ID' INTEGER NOT NULL ," + // 3: transactionId
                "'PRODUCT_ID' INTEGER NOT NULL ," + // 4: productId
                "'PRODUCT_NAME' TEXT," + // 5: productName
                "'PRODUCT_TYPE' TEXT," + // 6: productType
                "'PRICE' DECIMAL(10,2)," + // 7: price
                "'COST_PRICE' DECIMAL(10,2)," + // 8: costPrice
                "'DISCOUNT' DECIMAL(10,2)," + // 9: discount
                "'QUANTITY' DECIMAL(10,2)," + // 10: quantity
                "'COMMISION' DECIMAL(10,2)," + // 11: commision
                "'REMARKS' TEXT," + // 12: remarks
                "'EMPLOYEE_ID' INTEGER," + // 13: employeeId
                "'UPLOAD_STATUS' TEXT);"); // 14: uploadStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TRANSACTION_ITEM'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TransactionItem entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String refId = entity.getRefId();
        if (refId != null) {
            stmt.bindString(2, refId);
        }
        stmt.bindLong(3, entity.getMerchantId());
        stmt.bindLong(4, entity.getTransactionId());
        stmt.bindLong(5, entity.getProductId());
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(6, productName);
        }
 
        String productType = entity.getProductType();
        if (productType != null) {
            stmt.bindString(7, productType);
        }
 
        Float price = entity.getPrice();
        if (price != null) {
            stmt.bindDouble(8, price);
        }
 
        Float costPrice = entity.getCostPrice();
        if (costPrice != null) {
            stmt.bindDouble(9, costPrice);
        }
 
        Float discount = entity.getDiscount();
        if (discount != null) {
            stmt.bindDouble(10, discount);
        }
 
        Float quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindDouble(11, quantity);
        }
 
        Float commision = entity.getCommision();
        if (commision != null) {
            stmt.bindDouble(12, commision);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(13, remarks);
        }
 
        Long employeeId = entity.getEmployeeId();
        if (employeeId != null) {
            stmt.bindLong(14, employeeId);
        }
 
        String uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindString(15, uploadStatus);
        }
    }

    @Override
    protected void attachEntity(TransactionItem entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TransactionItem readEntity(Cursor cursor, int offset) {
        TransactionItem entity = new TransactionItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // refId
            cursor.getLong(offset + 2), // merchantId
            cursor.getLong(offset + 3), // transactionId
            cursor.getLong(offset + 4), // productId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // productName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // productType
            cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7), // price
            cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8), // costPrice
            cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9), // discount
            cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10), // quantity
            cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11), // commision
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // remarks
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13), // employeeId
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // uploadStatus
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TransactionItem entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRefId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMerchantId(cursor.getLong(offset + 2));
        entity.setTransactionId(cursor.getLong(offset + 3));
        entity.setProductId(cursor.getLong(offset + 4));
        entity.setProductName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setProductType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPrice(cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7));
        entity.setCostPrice(cursor.isNull(offset + 8) ? null : cursor.getFloat(offset + 8));
        entity.setDiscount(cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9));
        entity.setQuantity(cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10));
        entity.setCommision(cursor.isNull(offset + 11) ? null : cursor.getFloat(offset + 11));
        entity.setRemarks(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setEmployeeId(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
        entity.setUploadStatus(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TransactionItem entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TransactionItem entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "transactionItemList" to-many relationship of Transactions. */
    public List<TransactionItem> _queryTransactions_TransactionItemList(long transactionId) {
        synchronized (this) {
            if (transactions_TransactionItemListQuery == null) {
                QueryBuilder<TransactionItem> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.TransactionId.eq(null));
                queryBuilder.orderRaw("PRODUCT_NAME ASC");
                transactions_TransactionItemListQuery = queryBuilder.build();
            }
        }
        Query<TransactionItem> query = transactions_TransactionItemListQuery.forCurrentThread();
        query.setParameter(0, transactionId);
        return query.list();
    }

    /** Internal query to resolve the "transactionItemList" to-many relationship of Employee. */
    public List<TransactionItem> _queryEmployee_TransactionItemList(Long employeeId) {
        synchronized (this) {
            if (employee_TransactionItemListQuery == null) {
                QueryBuilder<TransactionItem> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.EmployeeId.eq(null));
                queryBuilder.orderRaw("_id ASC");
                employee_TransactionItemListQuery = queryBuilder.build();
            }
        }
        Query<TransactionItem> query = employee_TransactionItemListQuery.forCurrentThread();
        query.setParameter(0, employeeId);
        return query.list();
    }

    /** Internal query to resolve the "transactionItemList" to-many relationship of Product. */
    public List<TransactionItem> _queryProduct_TransactionItemList(long productId) {
        synchronized (this) {
            if (product_TransactionItemListQuery == null) {
                QueryBuilder<TransactionItem> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ProductId.eq(null));
                queryBuilder.orderRaw("_id ASC");
                product_TransactionItemListQuery = queryBuilder.build();
            }
        }
        Query<TransactionItem> query = product_TransactionItemListQuery.forCurrentThread();
        query.setParameter(0, productId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getMerchantDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getTransactionsDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getProductDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getEmployeeDao().getAllColumns());
            builder.append(" FROM TRANSACTION_ITEM T");
            builder.append(" LEFT JOIN MERCHANT T0 ON T.'MERCHANT_ID'=T0.'_id'");
            builder.append(" LEFT JOIN TRANSACTIONS T1 ON T.'TRANSACTION_ID'=T1.'_id'");
            builder.append(" LEFT JOIN PRODUCT T2 ON T.'PRODUCT_ID'=T2.'_id'");
            builder.append(" LEFT JOIN EMPLOYEE T3 ON T.'EMPLOYEE_ID'=T3.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected TransactionItem loadCurrentDeep(Cursor cursor, boolean lock) {
        TransactionItem entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Merchant merchant = loadCurrentOther(daoSession.getMerchantDao(), cursor, offset);
         if(merchant != null) {
            entity.setMerchant(merchant);
        }
        offset += daoSession.getMerchantDao().getAllColumns().length;

        Transactions transactions = loadCurrentOther(daoSession.getTransactionsDao(), cursor, offset);
         if(transactions != null) {
            entity.setTransactions(transactions);
        }
        offset += daoSession.getTransactionsDao().getAllColumns().length;

        Product product = loadCurrentOther(daoSession.getProductDao(), cursor, offset);
         if(product != null) {
            entity.setProduct(product);
        }
        offset += daoSession.getProductDao().getAllColumns().length;

        Employee employee = loadCurrentOther(daoSession.getEmployeeDao(), cursor, offset);
        entity.setEmployee(employee);

        return entity;    
    }

    public TransactionItem loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<TransactionItem> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<TransactionItem> list = new ArrayList<TransactionItem>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<TransactionItem> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<TransactionItem> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}

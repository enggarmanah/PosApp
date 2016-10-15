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

import com.android.pos.dao.OrderItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ORDER_ITEM.
*/
public class OrderItemDao extends AbstractDao<OrderItem, Long> {

    public static final String TABLENAME = "ORDER_ITEM";

    /**
     * Properties of entity OrderItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RefId = new Property(1, String.class, "refId", false, "REF_ID");
        public final static Property MerchantId = new Property(2, long.class, "merchantId", false, "MERCHANT_ID");
        public final static Property OrderId = new Property(3, long.class, "orderId", false, "ORDER_ID");
        public final static Property OrderNo = new Property(4, String.class, "orderNo", false, "ORDER_NO");
        public final static Property ProductId = new Property(5, long.class, "productId", false, "PRODUCT_ID");
        public final static Property ProductName = new Property(6, String.class, "productName", false, "PRODUCT_NAME");
        public final static Property Quantity = new Property(7, Float.class, "quantity", false, "QUANTITY");
        public final static Property Remarks = new Property(8, String.class, "remarks", false, "REMARKS");
        public final static Property EmployeeId = new Property(9, Long.class, "employeeId", false, "EMPLOYEE_ID");
        public final static Property UploadStatus = new Property(10, String.class, "uploadStatus", false, "UPLOAD_STATUS");
    };

    private DaoSession daoSession;

    private Query<OrderItem> orders_OrderItemListQuery;

    public OrderItemDao(DaoConfig config) {
        super(config);
    }
    
    public OrderItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ORDER_ITEM' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'REF_ID' TEXT," + // 1: refId
                "'MERCHANT_ID' INTEGER NOT NULL ," + // 2: merchantId
                "'ORDER_ID' INTEGER NOT NULL ," + // 3: orderId
                "'ORDER_NO' TEXT," + // 4: orderNo
                "'PRODUCT_ID' INTEGER NOT NULL ," + // 5: productId
                "'PRODUCT_NAME' TEXT," + // 6: productName
                "'QUANTITY' REAL," + // 7: quantity
                "'REMARKS' TEXT," + // 8: remarks
                "'EMPLOYEE_ID' INTEGER," + // 9: employeeId
                "'UPLOAD_STATUS' TEXT);"); // 10: uploadStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ORDER_ITEM'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, OrderItem entity) {
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
        stmt.bindLong(4, entity.getOrderId());
 
        String orderNo = entity.getOrderNo();
        if (orderNo != null) {
            stmt.bindString(5, orderNo);
        }
        stmt.bindLong(6, entity.getProductId());
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(7, productName);
        }
 
        Float quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindDouble(8, quantity);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(9, remarks);
        }
 
        Long employeeId = entity.getEmployeeId();
        if (employeeId != null) {
            stmt.bindLong(10, employeeId);
        }
 
        String uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindString(11, uploadStatus);
        }
    }

    @Override
    protected void attachEntity(OrderItem entity) {
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
    public OrderItem readEntity(Cursor cursor, int offset) {
        OrderItem entity = new OrderItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // refId
            cursor.getLong(offset + 2), // merchantId
            cursor.getLong(offset + 3), // orderId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // orderNo
            cursor.getLong(offset + 5), // productId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // productName
            cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7), // quantity
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // remarks
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // employeeId
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // uploadStatus
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, OrderItem entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRefId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMerchantId(cursor.getLong(offset + 2));
        entity.setOrderId(cursor.getLong(offset + 3));
        entity.setOrderNo(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProductId(cursor.getLong(offset + 5));
        entity.setProductName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setQuantity(cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7));
        entity.setRemarks(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setEmployeeId(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setUploadStatus(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(OrderItem entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(OrderItem entity) {
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
    
    /** Internal query to resolve the "orderItemList" to-many relationship of Orders. */
    public List<OrderItem> _queryOrders_OrderItemList(long orderId) {
        synchronized (this) {
            if (orders_OrderItemListQuery == null) {
                QueryBuilder<OrderItem> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.OrderId.eq(null));
                queryBuilder.orderRaw("PRODUCT_NAME ASC");
                orders_OrderItemListQuery = queryBuilder.build();
            }
        }
        Query<OrderItem> query = orders_OrderItemListQuery.forCurrentThread();
        query.setParameter(0, orderId);
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
            SqlUtils.appendColumns(builder, "T1", daoSession.getOrdersDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getProductDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getEmployeeDao().getAllColumns());
            builder.append(" FROM ORDER_ITEM T");
            builder.append(" LEFT JOIN MERCHANT T0 ON T.'MERCHANT_ID'=T0.'_id'");
            builder.append(" LEFT JOIN ORDERS T1 ON T.'ORDER_ID'=T1.'_id'");
            builder.append(" LEFT JOIN PRODUCT T2 ON T.'PRODUCT_ID'=T2.'_id'");
            builder.append(" LEFT JOIN EMPLOYEE T3 ON T.'EMPLOYEE_ID'=T3.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected OrderItem loadCurrentDeep(Cursor cursor, boolean lock) {
        OrderItem entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Merchant merchant = loadCurrentOther(daoSession.getMerchantDao(), cursor, offset);
         if(merchant != null) {
            entity.setMerchant(merchant);
        }
        offset += daoSession.getMerchantDao().getAllColumns().length;

        Orders orders = loadCurrentOther(daoSession.getOrdersDao(), cursor, offset);
         if(orders != null) {
            entity.setOrders(orders);
        }
        offset += daoSession.getOrdersDao().getAllColumns().length;

        Product product = loadCurrentOther(daoSession.getProductDao(), cursor, offset);
         if(product != null) {
            entity.setProduct(product);
        }
        offset += daoSession.getProductDao().getAllColumns().length;

        Employee employee = loadCurrentOther(daoSession.getEmployeeDao(), cursor, offset);
        entity.setEmployee(employee);

        return entity;    
    }

    public OrderItem loadDeep(Long key) {
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
    public List<OrderItem> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<OrderItem> list = new ArrayList<OrderItem>(count);
        
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
    
    protected List<OrderItem> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<OrderItem> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}

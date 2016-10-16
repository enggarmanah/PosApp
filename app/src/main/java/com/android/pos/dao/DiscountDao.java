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

import com.android.pos.dao.Discount;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DISCOUNT.
*/
public class DiscountDao extends AbstractDao<Discount, Long> {

    public static final String TABLENAME = "DISCOUNT";

    /**
     * Properties of entity Discount.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RefId = new Property(1, String.class, "refId", false, "REF_ID");
        public final static Property MerchantId = new Property(2, long.class, "merchantId", false, "MERCHANT_ID");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property Percentage = new Property(5, Float.class, "percentage", false, "PERCENTAGE");
        public final static Property Amount = new Property(6, Float.class, "amount", false, "AMOUNT");
        public final static Property Status = new Property(7, String.class, "status", false, "STATUS");
        public final static Property UploadStatus = new Property(8, String.class, "uploadStatus", false, "UPLOAD_STATUS");
        public final static Property CreateBy = new Property(9, String.class, "createBy", false, "CREATE_BY");
        public final static Property CreateDate = new Property(10, java.util.Date.class, "createDate", false, "CREATE_DATE");
        public final static Property UpdateBy = new Property(11, String.class, "updateBy", false, "UPDATE_BY");
        public final static Property UpdateDate = new Property(12, java.util.Date.class, "updateDate", false, "UPDATE_DATE");
    };

    private DaoSession daoSession;


    public DiscountDao(DaoConfig config) {
        super(config);
    }
    
    public DiscountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DISCOUNT' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'REF_ID' TEXT," + // 1: refId
                "'MERCHANT_ID' INTEGER NOT NULL ," + // 2: merchantId
                "'NAME' TEXT NOT NULL ," + // 3: name
                "'TYPE' TEXT," + // 4: type
                "'PERCENTAGE' REAL," + // 5: percentage
                "'AMOUNT' REAL," + // 6: amount
                "'STATUS' TEXT," + // 7: status
                "'UPLOAD_STATUS' TEXT," + // 8: uploadStatus
                "'CREATE_BY' TEXT," + // 9: createBy
                "'CREATE_DATE' INTEGER," + // 10: createDate
                "'UPDATE_BY' TEXT," + // 11: updateBy
                "'UPDATE_DATE' INTEGER);"); // 12: updateDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DISCOUNT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Discount entity) {
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
        stmt.bindString(4, entity.getName());
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        Float percentage = entity.getPercentage();
        if (percentage != null) {
            stmt.bindDouble(6, percentage);
        }
 
        Float amount = entity.getAmount();
        if (amount != null) {
            stmt.bindDouble(7, amount);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(8, status);
        }
 
        String uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindString(9, uploadStatus);
        }
 
        String createBy = entity.getCreateBy();
        if (createBy != null) {
            stmt.bindString(10, createBy);
        }
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(11, createDate.getTime());
        }
 
        String updateBy = entity.getUpdateBy();
        if (updateBy != null) {
            stmt.bindString(12, updateBy);
        }
 
        java.util.Date updateDate = entity.getUpdateDate();
        if (updateDate != null) {
            stmt.bindLong(13, updateDate.getTime());
        }
    }

    @Override
    protected void attachEntity(Discount entity) {
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
    public Discount readEntity(Cursor cursor, int offset) {
        Discount entity = new Discount( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // refId
            cursor.getLong(offset + 2), // merchantId
            cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // percentage
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6), // amount
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // status
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // uploadStatus
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // createBy
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // createDate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // updateBy
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)) // updateDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Discount entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRefId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMerchantId(cursor.getLong(offset + 2));
        entity.setName(cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPercentage(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setAmount(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
        entity.setStatus(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUploadStatus(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCreateBy(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCreateDate(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setUpdateBy(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUpdateDate(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Discount entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Discount entity) {
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getMerchantDao().getAllColumns());
            builder.append(" FROM DISCOUNT T");
            builder.append(" LEFT JOIN MERCHANT T0 ON T.'MERCHANT_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Discount loadCurrentDeep(Cursor cursor, boolean lock) {
        Discount entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Merchant merchant = loadCurrentOther(daoSession.getMerchantDao(), cursor, offset);
         if(merchant != null) {
            entity.setMerchant(merchant);
        }

        return entity;    
    }

    public Discount loadDeep(Long key) {
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
    public List<Discount> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Discount> list = new ArrayList<Discount>(count);
        
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
    
    protected List<Discount> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Discount> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}

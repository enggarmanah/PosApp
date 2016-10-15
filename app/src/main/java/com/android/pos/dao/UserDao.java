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

import com.android.pos.dao.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table USER.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RefId = new Property(1, String.class, "refId", false, "REF_ID");
        public final static Property MerchantId = new Property(2, long.class, "merchantId", false, "MERCHANT_ID");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property UserId = new Property(4, String.class, "userId", false, "USER_ID");
        public final static Property Password = new Property(5, String.class, "password", false, "PASSWORD");
        public final static Property Role = new Property(6, String.class, "role", false, "ROLE");
        public final static Property EmployeeId = new Property(7, Long.class, "employeeId", false, "EMPLOYEE_ID");
        public final static Property Status = new Property(8, String.class, "status", false, "STATUS");
        public final static Property UploadStatus = new Property(9, String.class, "uploadStatus", false, "UPLOAD_STATUS");
        public final static Property CreateBy = new Property(10, String.class, "createBy", false, "CREATE_BY");
        public final static Property CreateDate = new Property(11, java.util.Date.class, "createDate", false, "CREATE_DATE");
        public final static Property UpdateBy = new Property(12, String.class, "updateBy", false, "UPDATE_BY");
        public final static Property UpdateDate = new Property(13, java.util.Date.class, "updateDate", false, "UPDATE_DATE");
    };

    private DaoSession daoSession;

    private Query<User> merchant_UserListQuery;

    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'USER' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'REF_ID' TEXT," + // 1: refId
                "'MERCHANT_ID' INTEGER NOT NULL ," + // 2: merchantId
                "'NAME' TEXT," + // 3: name
                "'USER_ID' TEXT," + // 4: userId
                "'PASSWORD' TEXT," + // 5: password
                "'ROLE' TEXT," + // 6: role
                "'EMPLOYEE_ID' INTEGER," + // 7: employeeId
                "'STATUS' TEXT," + // 8: status
                "'UPLOAD_STATUS' TEXT," + // 9: uploadStatus
                "'CREATE_BY' TEXT," + // 10: createBy
                "'CREATE_DATE' INTEGER," + // 11: createDate
                "'UPDATE_BY' TEXT," + // 12: updateBy
                "'UPDATE_DATE' INTEGER);"); // 13: updateDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'USER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
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
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(5, userId);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(6, password);
        }
 
        String role = entity.getRole();
        if (role != null) {
            stmt.bindString(7, role);
        }
 
        Long employeeId = entity.getEmployeeId();
        if (employeeId != null) {
            stmt.bindLong(8, employeeId);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(9, status);
        }
 
        String uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindString(10, uploadStatus);
        }
 
        String createBy = entity.getCreateBy();
        if (createBy != null) {
            stmt.bindString(11, createBy);
        }
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(12, createDate.getTime());
        }
 
        String updateBy = entity.getUpdateBy();
        if (updateBy != null) {
            stmt.bindString(13, updateBy);
        }
 
        java.util.Date updateDate = entity.getUpdateDate();
        if (updateDate != null) {
            stmt.bindLong(14, updateDate.getTime());
        }
    }

    @Override
    protected void attachEntity(User entity) {
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
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // refId
            cursor.getLong(offset + 2), // merchantId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // userId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // password
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // role
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // employeeId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // status
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // uploadStatus
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // createBy
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // createDate
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // updateBy
            cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)) // updateDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRefId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMerchantId(cursor.getLong(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUserId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPassword(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setRole(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setEmployeeId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setStatus(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUploadStatus(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCreateBy(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCreateDate(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setUpdateBy(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUpdateDate(cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
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
    
    /** Internal query to resolve the "userList" to-many relationship of Merchant. */
    public List<User> _queryMerchant_UserList(long merchantId) {
        synchronized (this) {
            if (merchant_UserListQuery == null) {
                QueryBuilder<User> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.MerchantId.eq(null));
                queryBuilder.orderRaw("NAME ASC");
                merchant_UserListQuery = queryBuilder.build();
            }
        }
        Query<User> query = merchant_UserListQuery.forCurrentThread();
        query.setParameter(0, merchantId);
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
            SqlUtils.appendColumns(builder, "T1", daoSession.getEmployeeDao().getAllColumns());
            builder.append(" FROM USER T");
            builder.append(" LEFT JOIN MERCHANT T0 ON T.'MERCHANT_ID'=T0.'_id'");
            builder.append(" LEFT JOIN EMPLOYEE T1 ON T.'EMPLOYEE_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected User loadCurrentDeep(Cursor cursor, boolean lock) {
        User entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Merchant merchant = loadCurrentOther(daoSession.getMerchantDao(), cursor, offset);
         if(merchant != null) {
            entity.setMerchant(merchant);
        }
        offset += daoSession.getMerchantDao().getAllColumns().length;

        Employee employee = loadCurrentOther(daoSession.getEmployeeDao(), cursor, offset);
        entity.setEmployee(employee);

        return entity;    
    }

    public User loadDeep(Long key) {
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
    public List<User> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<User> list = new ArrayList<User>(count);
        
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
    
    protected List<User> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<User> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}

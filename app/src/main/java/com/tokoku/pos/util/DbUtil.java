package com.tokoku.pos.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.android.pos.dao.BillsDao;
import com.android.pos.dao.CashflowDao;
import com.android.pos.dao.DaoMaster;
import com.android.pos.dao.DaoSession;
import com.android.pos.dao.DiscountDao;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.MerchantAccessDao;
import com.android.pos.dao.OrderItemDao;
import com.android.pos.dao.OrdersDao;
import com.android.pos.dao.ProductDao;
import com.android.pos.dao.SupplierDao;
import com.android.pos.dao.TransactionsDao;
import com.android.pos.dao.DaoMaster.DevOpenHelper;
import com.android.pos.dao.ProductGroupDao;
import com.android.pos.dao.TransactionItemDao;
import com.android.pos.dao.UserAccessDao;

public class DbUtil {
	
	private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static Context context;
    
    public static class DbOpenHelper extends DaoMaster.DevOpenHelper {
    	
    	private DbOpenHelper(Context context, String name, CursorFactory factory) {
            
    		super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
        	Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            
        	// handle version 2 changes
            if (oldVersion < 2) {
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'PIC_REQUIRED' TEXT");
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'COMMISION' INTEGER");
            }
            
            // handle version 3 changes
            if (oldVersion < 3) {
            	db.execSQL("ALTER TABLE 'USER' ADD 'ROLE' TEXT");
            }
            
            // handle version 4 changes
            if (oldVersion < 4) {
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'PRODUCT_GROUP_ID' INTEGER");
            	ProductGroupDao.createTable(db, true);
            }
            
            // handle version 5 changes
            if (oldVersion < 5) {
            	TransactionItemDao.dropTable(db, true);
            	TransactionItemDao.createTable(db, true);
            }
            
            // handle version 6 changes
            if (oldVersion < 6) {
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'TAX_PERCENTAGE' INTEGER");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'SERVICE_CHARGE_PERCENTAGE' INTEGER");
            	
            	TransactionsDao.dropTable(db, true);
            	TransactionsDao.createTable(db, true);
            	DiscountDao.createTable(db, true);
            }
            
            // handle version 7 changes
            if (oldVersion < 7) {
            	TransactionsDao.dropTable(db, true);
            	TransactionsDao.createTable(db, true);
            }
            
            // handle version 8 changes
            if (oldVersion < 8) {
            	TransactionItemDao.dropTable(db, true);
            	TransactionItemDao.createTable(db, true);
            	TransactionsDao.dropTable(db, true);
            	TransactionsDao.createTable(db, true);
            }
            
            // handle version 9 changes
            if (oldVersion < 9) {
            	db.execSQL("ALTER TABLE 'PRODUCT_GROUP' ADD 'MERCHANT_ID' INTEGER");
            	db.execSQL("ALTER TABLE 'DISCOUNT' ADD 'MERCHANT_ID' INTEGER");
            }
            
            // handle version 10 changes
            if (oldVersion < 10) {
            	db.execSQL("ALTER TABLE 'PRODUCT_GROUP' ADD 'STATUS' TEXT");
            }
            
            // handle version 11 changes
            if (oldVersion < 11) {
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'STATUS' TEXT");
            }
            
            // handle version 12 changes
            if (oldVersion < 12) {
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'UPLOAD_STATUS' TEXT");
            }
            
            // handle version 13 changes
            if (oldVersion < 13) {
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'MERCHANT_ID' INTEGER");
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'UPLOAD_STATUS' TEXT");
            }
            
            // handle version 14 changes
            if (oldVersion < 14) {
            	db.execSQL("ALTER TABLE 'DISCOUNT' ADD 'AMOUNT' INTEGER");
            }
            
            // handle version 15 changes
            if (oldVersion < 15) {
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'IS_LOGIN' INTEGER");
            }
            
            // handle version 16 changes
            if (oldVersion < 16) {
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'COST_PRICE' INTEGER");
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'COST_PRICE' INTEGER");
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'DISCOUNT' INTEGER");
            }
            
            // handle version 17 changes
            if (oldVersion < 17) {
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'TELEPHONE' TEXT");
            }
            
            // handle version 18 changes
            if (oldVersion < 18) {
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_TYPE' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_ADDRESS' TEXT");
            }
            
            // handle version 19 changes
            if (oldVersion < 19) {
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'CAPACITY' INTEGER");
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'STOCK' INTEGER");
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'MIN_STOCK' INTEGER");
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'COMMISION' INTEGER");
            	
            	//OrdersDao.dropTable(db, true);
            	//OrdersDao.createTable(db, true);
            	
            	//OrderItemDao.dropTable(db, true);
            	//OrderItemDao.createTable(db, true);
            }
            
            // handle version 20 changes
            if (oldVersion < 20) {
            	//db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'DEBT_REFERENCE' TEXT");
            }
            
            // handle version 21 changes
            if (oldVersion < 21) {
            	//db.execSQL("ALTER TABLE 'ORDERS' ADD 'ORDER_TYPE' TEXT");
            	//db.execSQL("ALTER TABLE 'ORDERS' ADD 'REFERENCE_NO' TEXT");
            	
            	//TransactionsDao.dropTable(db, true);
            	//TransactionsDao.createTable(db, true);
            	
            	//TransactionItemDao.dropTable(db, true);
            	//TransactionItemDao.createTable(db, true);
            }
            
            // handle version 22 changes
            if (oldVersion < 22) {
            	
            	OrdersDao.dropTable(db, true);
            	OrdersDao.createTable(db, true);
            	
            	OrderItemDao.dropTable(db, true);
            	OrderItemDao.createTable(db, true);
            }
            
            // handle version 23 changes
            if (oldVersion < 23) {
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'ORDER_TYPE' TEXT");
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'ORDER_REFERENCE' TEXT");
            }
            
            // handle version 24 changes
            if (oldVersion < 24) {
            	//db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'REMARKS' TEXT");
            	//db.execSQL("ALTER TABLE 'ORDER_ITEM' ADD 'REMARKS' TEXT");
            }
            
            // handle version 25 changes
            if (oldVersion < 25) {
            	
            	SupplierDao.dropTable(db, true);
            	SupplierDao.createTable(db, true);
            	
            	BillsDao.dropTable(db, true);
            	BillsDao.createTable(db, true);
            	
            	InventoryDao.dropTable(db, true);
            	InventoryDao.createTable(db, true);
            }
            
            // handle version 26 changes
            if (oldVersion < 26) {
            	
            	BillsDao.dropTable(db, true);
            	BillsDao.createTable(db, true);
            	
            	InventoryDao.dropTable(db, true);
            	InventoryDao.createTable(db, true);
            }
            
            // handle version 27 changes
            if (oldVersion < 27) {
            	
            	InventoryDao.dropTable(db, true);
            	InventoryDao.createTable(db, true);
            	
            	BillsDao.dropTable(db, true);
            	BillsDao.createTable(db, true);
            }
            
            // handle version 28 changes
            if (oldVersion < 28) {
            	
            	db.execSQL("ALTER TABLE 'INVENTORY' ADD 'PRODUCT_COST_PRICE' INTEGER");
            }
            
            // handle version 29 changes
            if (oldVersion < 29) {
            	
            	MerchantAccessDao.dropTable(db, true);
            	MerchantAccessDao.createTable(db, true);
            }
            
            // handle version 30 changes
            if (oldVersion < 30) {
            	
            	UserAccessDao.dropTable(db, true);
            	UserAccessDao.createTable(db, true);
            }
            
            // handle version 31 changes
            if (oldVersion < 31) {
            	
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'ORDER_NO' TEXT");
            	db.execSQL("ALTER TABLE 'ORDER_ITEM' ADD 'ORDER_NO' TEXT");
            }
            
            // handle version 32 changes
            if (oldVersion < 32) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_REQUIRED' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_LINE_SIZE' INTEGER");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_MINI_FONT' TEXT");
            }
            
            // handle version 33 changes
            if (oldVersion < 33) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'CONTACT_EMAIL' TEXT");
            }
            
            // handle version 34 changes
            if (oldVersion < 34) {
            	
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'CODE' TEXT");
            }
            
            // handle version 35 changes
            if (oldVersion < 35) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'DISCOUNT_TYPE' TEXT");
            }
            
            // handle version 36 changes
            if (oldVersion < 36) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRICE_TYPE_COUNT' INTEGER");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRICE_LABEL1' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRICE_LABEL2' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRICE_LABEL3' TEXT");
            }
            
            // handle version 37 changes
            if (oldVersion < 37) {
            	
            	ProductDao.dropTable(db, true);
            	ProductDao.createTable(db, true);
            }
            
            // handle version 38 changes
            if (oldVersion < 38) {
            	
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'QUANTITY_TYPE' DECIMAL(10,2)");
            }
            
            // handle version 39 changes
            if (oldVersion < 39) {
            	
            	db.execSQL("ALTER TABLE 'BILLS' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'CUSTOMER' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'DISCOUNT' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'EMPLOYEE' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'INVENTORY' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT_ACCESS' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'PRODUCT' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'PRODUCT_GROUP' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'SUPPLIER' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'TRANSACTION_ITEM' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'USER' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'USER_ACCESS' ADD 'REF_ID' TEXT");
            }
            
            // handle version 40 changes
            if (oldVersion < 40) {
            	
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'CUSTOMER_ID' INTEGER");
            	db.execSQL("ALTER TABLE 'ORDER_ITEM' ADD 'REF_ID' TEXT");
            	db.execSQL("ALTER TABLE 'ORDER_ITEM' ADD 'EMPLOYEE_ID' INTEGER");
            }
            
            // handle version 41 changes
            if (oldVersion < 41) {
            	
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'WAITRESS_ID' INTEGER");
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'WAITRESS_NAME' TEXT");
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'WAITRESS_ID' INTEGER");
            	db.execSQL("ALTER TABLE 'TRANSACTIONS' ADD 'WAITRESS_NAME' TEXT");
            }
            
            // handle version 42 changes 
            if (oldVersion < 42) {
            	
            	db.execSQL("ALTER TABLE 'ORDERS' ADD 'UPLOAD_STATUS' TEXT");
            	db.execSQL("ALTER TABLE 'ORDER_ITEMS' ADD 'UPLOAD_STATUS' TEXT");
            }
            
            // handle version 43 changes
            if (oldVersion < 43) {
            	
            	CashflowDao.createTable(db, true);
            }
            
            // handle version 44 changes
            if (oldVersion < 44) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PAYMENT_TYPE' TEXT");
            }
            
            // handle version 45 changes
            if (oldVersion < 45) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'LOCALE' TEXT");
            	db.execSQL("ALTER TABLE 'USER' ADD 'EMPLOYEE_ID' INTEGER");
            }
            
            // handle version 46 changes
            if (oldVersion < 46) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'SECURITY_QUESTION' TEXT");
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'SECURITY_ANSWER' TEXT");
            }
            
            // handle version 47 changes
            if (oldVersion < 47) {
            	
            	db.execSQL("ALTER TABLE 'MERCHANT' ADD 'ORDER_TYPE' TEXT");
            }

			// handle version 47 changes
			if (oldVersion < 48) {

				db.execSQL("ALTER TABLE 'PRODUCT' ADD 'PRICE_TYPE' TEXT");
				db.execSQL("ALTER TABLE 'PRODUCT' ADD 'COMMISION_TYPE' TEXT");

				db.execSQL("ALTER TABLE 'DISCOUNT' ADD 'TYPE' TEXT");
			}

			// handle version 48 changes
			if (oldVersion < 49) {

				db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTER_FOOTER' TEXT");
				db.execSQL("ALTER TABLE 'MERCHANT' ADD 'PRINTING_OPTION' TEXT");

				db.execSQL("ALTER TABLE 'INVENTORY' ADD 'DELIVERY_NO' TEXT");
			}

			// handle version 49 changes
			if (oldVersion < 50) {

				db.execSQL("ALTER TABLE 'BILLS' ADD 'DELIVERY_NO' TEXT");
			}
            
            //DaoMaster.dropAllTables(db, true);
            //onCreate(db);
        }
    }
    
    public static void initDb(Context ctx) {
    	
    	context = ctx;
    	
    	if (daoSession == null) {
    		
    		DevOpenHelper helper = new DbOpenHelper(context, "pos.db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            
            System.out.println("db : " + db);
    	}
    }
    
    private static Long getDbId() {
    	
    	String dbFile = db.getPath();
    	String strId = dbFile.replaceAll("[^0-9]", "");
    	
    	Long dbId = null;
    	
    	if (strId.length() > 0) {
    		dbId = Long.valueOf(strId);
    	}
    	
    	return dbId;
    }

	public static void closeDb() {

		if (db != null) {
			db.close();
			daoSession = null;
		}
	}

	public static void switchDb(Context ctx, Long merchantId) {
    	
    	if (ctx != null) {
    		context = ctx;
    	}
    	
    	if (merchantId == getDbId() || (merchantId != null && merchantId.equals(getDbId()))) {
    		
    		System.out.println("Equal DB : No Switch");
    		return;
    	}
    	
    	System.out.println("Close DB : " + db);
    	
    	if (db != null) {
    		db.close();
    	}
    	
    	String dbFile = (merchantId == null ? "pos.db" : "pos-" + merchantId + ".db");
    	
    	DevOpenHelper helper = new DbOpenHelper(context, dbFile, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        System.out.println("Open DB : " + db);
        
        /*db.execSQL("UPDATE 'BILLS' set upload_status = 'Y'");
        db.execSQL("UPDATE 'CASHFLOW' set upload_status = 'Y'");
        db.execSQL("UPDATE 'CUSTOMER' set upload_status = 'Y'");
        db.execSQL("UPDATE 'DISCOUNT' set upload_status = 'Y'");
        db.execSQL("UPDATE 'EMPLOYEE' set upload_status = 'Y'");
        db.execSQL("UPDATE 'INVENTORY' set upload_status = 'Y'");
        db.execSQL("UPDATE 'MERCHANT' set upload_status = 'Y'");
        db.execSQL("UPDATE 'MERCHANT_ACCESS' set upload_status = 'Y'");
        db.execSQL("UPDATE 'ORDER_ITEM' set upload_status = 'Y'");
        db.execSQL("UPDATE 'ORDERS' set upload_status = 'Y'");
        db.execSQL("UPDATE 'PRODUCT' set upload_status = 'Y'");
        db.execSQL("UPDATE 'PRODUCT_GROUP' set upload_status = 'Y'");
        db.execSQL("UPDATE 'SUPPLIER' set upload_status = 'Y'");
        db.execSQL("UPDATE 'TRANSACTION_ITEM' set upload_status = 'Y'");
        db.execSQL("UPDATE 'TRANSACTIONS' set upload_status = 'Y'");
        db.execSQL("UPDATE 'USER' set upload_status = 'Y'");
        db.execSQL("UPDATE 'USER_ACCESS' set upload_status = 'Y'"); */
    }
    
    public static DaoSession getSession() {
    	
    	if (daoSession == null) {
    		initDb(context);
    	}
    	
    	return daoSession;
    }
    
    public static SQLiteDatabase getDb() {
    	
    	return db;
    }
}

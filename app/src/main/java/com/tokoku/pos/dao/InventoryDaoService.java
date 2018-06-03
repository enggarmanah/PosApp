package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Bills;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.Product;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.InventoryBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class InventoryDaoService {
	
	private InventoryDao inventoryDao = DbUtil.getSession().getInventoryDao();
	
	public void addInventory(Inventory inventory) {
		
		if (CommonUtil.isEmpty(inventory.getRefId())) {
			inventory.setRefId(CommonUtil.generateRefId());
		}
		
		inventoryDao.insert(inventory);
	}
	
	public void updateInventory(Inventory inventory) {
		
		inventoryDao.update(inventory);
	}
	
	public void deleteInventory(Inventory inventory) {

		Inventory entity = inventoryDao.load(inventory.getId());
		
		if (entity != null) {
			entity.setStatus(Constant.STATUS_DELETED);
			entity.setUploadStatus(Constant.STATUS_YES);
			inventoryDao.update(entity);
		}
	}
	
	public Inventory getInventory(Long id) {
		
		Inventory inventory = inventoryDao.load(id);
		
		if (inventory != null) {
			inventoryDao.detach(inventory);
		}
		
		return inventory;
	}
	
	public List<Inventory> getInventories(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String statusSale = Constant.INVENTORY_STATUS_SALE;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM inventory "
				+ " WHERE (bill_reference_no like ? OR product_name like ? OR supplier_name like ? OR remarks like ? ) AND status <> ? AND status <> ? "
				+ " ORDER BY inventory_date DESC, _id DESC LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, queryStr, queryStr, statusSale, status, limit, lastIdx});
		
		List<Inventory> list = new ArrayList<Inventory>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Inventory item = getInventory(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<Inventory> getInventories(Bills bill) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String billReferenceNo = bill.getBillReferenceNo();
		String statusSale = Constant.INVENTORY_STATUS_SALE;
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM inventory "
				+ " WHERE bill_reference_no like ? AND status <> ? AND status <> ? "
				+ " ORDER BY inventory_date DESC, _id DESC ",
				new String[] { billReferenceNo, statusSale, status});
		
		List<Inventory> list = new ArrayList<Inventory>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Inventory item = getInventory(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<Inventory> getInventories(String billReferenceNo) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM inventory "
				+ " WHERE bill_reference_no like ? AND status <> ? "
				+ " ORDER BY inventory_date DESC, _id DESC ",
				new String[] { billReferenceNo, status});
		
		List<Inventory> list = new ArrayList<Inventory>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Inventory item = getInventory(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}

	public List<Inventory> getInventories(String deliveryNo, Date deliveryDate) {

		SQLiteDatabase db = DbUtil.getDb();

		String status = Constant.STATUS_DELETED;

		String deliveryDateStr = String.valueOf(deliveryDate.getTime());

		Cursor cursor = db.rawQuery("SELECT _id "
						+ " FROM inventory "
						+ " WHERE delivery_no = ? AND inventory_date = ? AND status <> ? "
						+ " ORDER BY inventory_date DESC, _id DESC ",
				new String[] { deliveryNo, deliveryDateStr, status});

		List<Inventory> list = new ArrayList<Inventory>();

		while(cursor.moveToNext()) {

			Long id = cursor.getLong(0);
			Inventory item = getInventory(id);
			list.add(item);
		}

		cursor.close();

		return list;
	}
	
	public List<Inventory> getInventories(Product product, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String productId = String.valueOf(product.getId());
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM inventory "
				+ " WHERE product_id = ? AND status <> ? "
				+ " ORDER BY inventory_date DESC LIMIT ? OFFSET ? ",
				new String[] { productId, status, limit, lastIdx});
		
		List<Inventory> list = new ArrayList<Inventory>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Inventory item = getInventory(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public Float getProductQuantity(Product product) {

		Float quantity = Float.valueOf(0);
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String productId = String.valueOf(product.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT SUM(quantity) "
				+ " FROM inventory "
				+ " WHERE product_id = ? AND status <> ? ",
				new String[] { productId, status });
		
		while(cursor.moveToNext()) {
			
			quantity = cursor.getFloat(0);
		}
		
		cursor.close();

		return quantity;
	}
	
	public List<InventoryBean> getInventoriesForUpload(int limit) {

		QueryBuilder<Inventory> qb = inventoryDao.queryBuilder();
		qb.where(InventoryDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(InventoryDao.Properties.InventoryDate);
		
		Query<Inventory> q = qb.build();
		
		ArrayList<InventoryBean> inventoryBeans = new ArrayList<InventoryBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Inventory inventory : q.list().subList(0, maxIndex)) {
			
			inventoryBeans.add(BeanUtil.getBean(inventory));
		}
		
		return inventoryBeans;
	}
	
	public void updateInventories(List<InventoryBean> inventories) {
		
		DbUtil.getDb().beginTransaction();
		
		List<InventoryBean> shiftedBeans = new ArrayList<InventoryBean>();
		
		for (InventoryBean bean : inventories) {
			
			boolean isAdd = false;
			
			Inventory inventory = inventoryDao.load(bean.getRemote_id());
			
			if (inventory == null) {
				inventory = new Inventory();
				isAdd = true;

			} else if (!CommonUtil.compareString(inventory.getRefId(), bean.getRef_id())) {
				InventoryBean shiftedBean = BeanUtil.getBean(inventory);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(inventory, bean);
			
			if (isAdd) {
				inventoryDao.insert(inventory);
			} else {
				inventoryDao.update(inventory);
			}
		}
		
		for (InventoryBean bean : shiftedBeans) {
			
			Inventory inventory = new Inventory();
			BeanUtil.updateBean(inventory, bean);
			
			inventory.setId(null);
			inventory.setUploadStatus(Constant.STATUS_YES);
			
			inventoryDao.insert(inventory);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	public void updateInventoryStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Inventory inventory = inventoryDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				inventory.setUploadStatus(Constant.STATUS_NO);
				inventoryDao.update(inventory);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getInventoriesForUploadCount() > 0;
	}
	
	public long getInventoriesForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM inventory WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
	
	public List<Inventory> getSupplierInventories(Supplier supplier, String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT "
				+ "   i._id "
				+ " FROM "
				+ "   inventory i, product p, product_group pg "
				+ " WHERE "
				+ "   p._id = i.product_id AND "
				+ "   pg._id = p.product_group_id AND "
				+ "   i.supplier_id = ? AND "
				+ "   (i.product_name like ? OR pg.name like ?) AND "
				+ "   i.status <> ? "
				+ " ORDER BY "
				+ "   i.inventory_date DESC "
				+ " LIMIT ? OFFSET ? ",
				new String[] { supplierId, queryStr, queryStr, status, limit, lastIdx});
		
		List<Inventory> list = new ArrayList<Inventory>();
		
		while(cursor.moveToNext()) {
			
			Inventory inventory = inventoryDao.load(cursor.getLong(0));
			
			list.add(inventory);
		}
		
		cursor.close();
		
		return list;
	}
}

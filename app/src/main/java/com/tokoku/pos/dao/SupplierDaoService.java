package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Bills;
import com.android.pos.dao.BillsDao;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.Supplier;
import com.android.pos.dao.SupplierDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.SupplierBean;
import com.tokoku.pos.model.SupplierStatisticBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class SupplierDaoService {
	
	private SupplierDao supplierDao = DbUtil.getSession().getSupplierDao();
	private InventoryDao inventoryDao = DbUtil.getSession().getInventoryDao();
	private BillsDao billDao = DbUtil.getSession().getBillsDao();
	
	public void addSupplier(Supplier supplier) {
		
		if (CommonUtil.isEmpty(supplier.getRefId())) {
			supplier.setRefId(CommonUtil.generateRefId());
		}
		
		supplierDao.insert(supplier);
	}
	
	public void updateSupplier(Supplier supplier) {
		
		supplierDao.update(supplier);
	}
	
	public void deleteSupplier(Supplier supplier) {

		Supplier entity = supplierDao.load(supplier.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		supplierDao.update(entity);
	}
	
	public Supplier getSupplier(Long id) {
		
		Supplier supplier = supplierDao.load(id);
		
		if (supplier != null) { 
			supplierDao.detach(supplier);
		}
		
		return supplier;
	}
	
	public List<Supplier> getSuppliers(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM supplier "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<Supplier> list = new ArrayList<Supplier>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Supplier item = getSupplier(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<SupplierBean> getSuppliersForUpload(int limit) {

		QueryBuilder<Supplier> qb = supplierDao.queryBuilder();
		qb.where(SupplierDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(SupplierDao.Properties.Name);
		
		Query<Supplier> q = qb.build();
		
		ArrayList<SupplierBean> supplierBeans = new ArrayList<SupplierBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Supplier supplier : q.list().subList(0, maxIndex)) {
			
			supplierBeans.add(BeanUtil.getBean(supplier));
		}
		
		return supplierBeans;
	}
	
	public void updateSuppliers(List<SupplierBean> suppliers) {
		
		DbUtil.getDb().beginTransaction();
		
		List<SupplierBean> shiftedBeans = new ArrayList<SupplierBean>();
		
		for (SupplierBean bean : suppliers) {
			
			boolean isAdd = false;
			
			Supplier supplier = supplierDao.load(bean.getRemote_id());
			
			if (supplier == null) {
				supplier = new Supplier();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(supplier.getRefId(), bean.getRef_id())) {
				SupplierBean shiftedBean = BeanUtil.getBean(supplier);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(supplier, bean);
			
			if (isAdd) {
				supplierDao.insert(supplier);
			} else {
				supplierDao.update(supplier);
			}
		}
		
		for (SupplierBean bean : shiftedBeans) {
			
			Supplier supplier = new Supplier();
			BeanUtil.updateBean(supplier, bean);
			
			Long oldId = supplier.getId();
			
			supplier.setId(null);
			supplier.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = supplierDao.insert(supplier);
			
			updateInventoryFk(oldId, newId);
			updateBillFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateInventoryFk(Long oldId, Long newId) {
		
		Supplier supplier = supplierDao.load(oldId);
		
		for (Inventory i : supplier.getInventoryList()) {
			
			i.setSupplierId(newId);
			i.setUploadStatus(Constant.STATUS_YES);
			inventoryDao.update(i);
		}
	}
	
	private void updateBillFk(Long oldId, Long newId) {
		
		Supplier supplier = supplierDao.load(oldId);
		
		for (Bills b : supplier.getBillsList()) {
			
			b.setSupplierId(newId);
			b.setUploadStatus(Constant.STATUS_YES);
			billDao.update(b);
		}
	}
	
	public void updateSupplierStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Supplier supplier = supplierDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				supplier.setUploadStatus(Constant.STATUS_NO);
				supplierDao.update(supplier);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getSuppliersForUploadCount() > 0;
	}
	
	public long getSuppliersForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM supplier WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
	
	public List<SupplierStatisticBean> getSupplierStatistics(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT "
				+ "   s._id, sum(i.quantity) quantity, sum(i.quantity * i.product_cost_price) amount "
				+ " FROM "
				+ "   supplier s, inventory i "
				+ " WHERE "
				+ "   s._id = i.supplier_id AND "
				+ "   s.name like ? AND i.status <> ? "
				+ " GROUP BY "
				+ "   i.supplier_id "
				+ " ORDER BY "
				+ "   quantity DESC "
				+ " LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<SupplierStatisticBean> list = new ArrayList<SupplierStatisticBean>();
		
		while(cursor.moveToNext()) {
			
			SupplierStatisticBean supplierStatistic = new SupplierStatisticBean();
			
			Supplier supplier = supplierDao.load(cursor.getLong(0));
			
			supplierStatistic.setSupplier(supplier);
			supplierStatistic.setName(supplier.getName());
			supplierStatistic.setQuantity(cursor.getLong(1));
			supplierStatistic.setAmount(cursor.getLong(2));
			
			list.add(supplierStatistic);
		}
		
		cursor.close();
		
		return list;
	}
	
	public Long getBillsCount(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT count(_id) "
				+ " FROM bills b "
				+ " WHERE "
				+ " b.supplier_id = ? AND "
				+ " b.status <> ? ",
				new String[] { supplierId, status });
		
		Long inventoriesCount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			inventoriesCount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return inventoriesCount;
	}
	
	public Long getProductsCount(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT sum(i.quantity) "
				+ " FROM "
				+ "   inventory i "
				+ " WHERE "
				+ "   i.supplier_id = ? AND "
				+ "   i.status <> ? "
				+ " GROUP BY "
				+ "   i.supplier_id ",
				new String[] { supplierId, status });
		
		Long productsCount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			productsCount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return productsCount;
	}
	
	public Long getBillsAmount(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT sum(b.bill_amount) "
				+ " FROM "
				+ "   bills b "
				+ " WHERE "
				+ "   b.supplier_id = ? AND "
				+ "   b.status <> ? ",
				new String[] { supplierId, status });
		
		Long inventoriesAmount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			inventoriesAmount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return inventoriesAmount;
	}
	
	public Date getFirstInventory(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', min(inventory_date)/1000, 'unixepoch', 'localtime') "
				+ " FROM "
				+ "   inventory i "
				+ " WHERE "
				+ "   i.supplier_id = ? AND "
				+ "   i.status <> ? ",
				new String[] { supplierId, status });
		
		Date firstInventory = null;
		
		if (cursor.moveToNext()) {
			
			firstInventory = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
		}
		
		cursor.close();
		
		return firstInventory;
	}
	
	public Date getLastInventory(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', max(inventory_date)/1000, 'unixepoch', 'localtime') "
				+ " FROM "
				+ "   inventory i "
				+ " WHERE "
				+ "   i.supplier_id = ? AND "
				+ "   i.status <> ? ",
				new String[] { supplierId, status });
		
		Date lastInventory = null;
		
		if (cursor.moveToNext()) {
			
			lastInventory = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
		}
		
		cursor.close();
		
		return lastInventory;
	}
	
	public Long getMinBill(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT min(b.bill_amount) "
				+ " FROM "
				+ "   bills b "
				+ " WHERE "
				+ "   b.supplier_id = ? AND "
				+ "   b.status <> ? ",
				new String[] { supplierId, status });
		
		Long minBill = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			minBill = cursor.getLong(0);
		}
		
		cursor.close();
		
		return minBill;
	}
	
	public Long getMaxBill(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT max(b.bill_amount) "
				+ " FROM "
				+ "   bills b "
				+ " WHERE "
				+ "   b.supplier_id = ? AND "
				+ "   b.status <> ? ",
				new String[] { supplierId, status });
		
		Long maxBill = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			maxBill = cursor.getLong(0);
		}
		
		cursor.close();
		
		return maxBill;
	}
}

package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Bills;
import com.android.pos.dao.BillsDao;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.CashflowDao;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.BillsBean;
import com.tokoku.pos.model.CashFlowMonthBean;
import com.tokoku.pos.model.CashFlowYearBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class BillsDaoService {
	
	private BillsDao billsDao = DbUtil.getSession().getBillsDao();
	private InventoryDao inventoryDao = DbUtil.getSession().getInventoryDao();
	private CashflowDao cashflowDao = DbUtil.getSession().getCashflowDao();

	private InventoryDaoService inventoryDaoService = new InventoryDaoService();
	
	public void addBills(Bills bills) {
		
		if (CommonUtil.isEmpty(bills.getRefId())) {
			bills.setRefId(CommonUtil.generateRefId());
		}
		
		billsDao.insert(bills);

        // linked from delivery no
        if (!CommonUtil.isEmpty(bills.getDeliveryNo())) {

            List<Inventory> inventories = inventoryDaoService.getInventories(bills.getDeliveryNo(), bills.getDeliveryDate());

            for (Inventory i : inventories) {
                i.setBills(bills);
                i.setBillId(bills.getId());
                i.setBillReferenceNo(bills.getBillReferenceNo());
                i.setUploadStatus(Constant.STATUS_YES);
                inventoryDao.update(i);
            }
        }
	}
	
	public void updateBills(Bills bills) {
		
		billsDao.update(bills);

		// linked from delivery no
		if (!CommonUtil.isEmpty(bills.getDeliveryNo())) {

            for (Inventory i : bills.getInventoryList()) {
                i.setBills(null);
                i.setBillId(null);
                i.setBillReferenceNo(null);
                i.setUploadStatus(Constant.STATUS_YES);
                inventoryDao.update(i);
            }

            List<Inventory> inventories = inventoryDaoService.getInventories(bills.getDeliveryNo(), bills.getDeliveryDate());

            for (Inventory i : inventories) {
                i.setBills(bills);
                i.setBillId(bills.getId());
                i.setBillReferenceNo(bills.getBillReferenceNo());
                i.setUploadStatus(Constant.STATUS_YES);
                inventoryDao.update(i);
            }

        // linked from inventory by selecting the invoice
		} else {
			for (Inventory i : bills.getInventoryList()) {
				i.setBillReferenceNo(bills.getBillReferenceNo());
				i.setUploadStatus(Constant.STATUS_YES);
				inventoryDao.update(i);
			}
		}
	}
	
	public void deleteBills(Bills bills) {

		Bills entity = billsDao.load(bills.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		billsDao.update(entity);
	}
	
	public Bills getBills(Long id) {
		
		Bills bills = billsDao.load(id);
		
		if (bills != null) {
			billsDao.detach(bills);
		}
		
		return bills;
	}
	
	public List<Bills> getBills(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE (bill_reference_no like ? OR supplier_name like ? OR remarks like ? ) AND status <> ? "
				+ " ORDER BY "
				+ "   bill_date DESC, _id DESC "
				+ " LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, queryStr, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<Bills> getBills(String query, String billType, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE (bill_reference_no like ? OR supplier_name like ? OR remarks like ? ) AND bill_type = ? AND status <> ? "
				+ " ORDER BY "
				+ "   bill_date DESC, _id DESC "
				+ " LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, queryStr, billType, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<Bills> getBillsReport(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE (bill_reference_no like ? OR supplier_name like ? OR remarks like ? ) AND status <> ? "
				+ " ORDER BY "
				+ "   CASE "
				+ "     WHEN bill_amount > payment AND bill_due_date IS NOT NULL "
				+ "       THEN 1 "
				+ "     WHEN bill_amount > payment AND bill_due_date IS NULL "
				+ "       THEN 2 "
				+ "     ELSE 3 "
				+ "   END ASC, bill_due_date ASC, payment_date DESC "
				+ " LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, queryStr, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<Bills> getBills(Date month, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(month).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(month).getTime());
		
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE payment_date BETWEEN ? AND ? AND status <> ? "
				+ " ORDER BY bill_date LIMIT ? OFFSET ? ",
				new String[] { startDate, endDate, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<Bills> getPastDueBills(String query, int lastIndex) {

		/*SQLiteDatabase db = DbUtil.getDb();
		
		String today = String.valueOf(new Date().getTime());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE payment < bill_amount AND bill_due_date <= ? AND status <> ? "
				+ " ORDER BY bill_date ",
				new String[] { today, status});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;*/
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String today = String.valueOf(new Date().getTime());
		String queryStr = CommonUtil.getSqlLikeString(query);
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE "
				+ "   payment < bill_amount AND bill_due_date <= ? AND "
				+ "   (bill_reference_no like ? OR supplier_name like ? OR remarks like ? ) AND status <> ? "
				+ " ORDER BY bill_date DESC, bill_reference_no ASC LIMIT ? OFFSET ? ",
				new String[] { today, queryStr, queryStr, queryStr, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public Integer getPastDueBillsCount() {
		
		Integer pastDueBillsCount = 0;
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String today = String.valueOf(new Date().getTime());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT count(_id) "
				+ " FROM bills "
				+ " WHERE payment < bill_amount AND bill_due_date <= ? AND status <> ? "
				+ " ORDER BY bill_date ",
				new String[] { today, status});
		
		if (cursor.moveToNext()) {
			
			pastDueBillsCount = cursor.getInt(0);
		}
		
		cursor.close();

		return pastDueBillsCount;
	}
	
	public List<Bills> getOutstandingBills() {

		SQLiteDatabase db = DbUtil.getDb();
		
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE payment < bill_amount AND status <> ? "
				+ " ORDER BY bill_date ",
				new String[] { status });
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public Integer getOutstandingBillsCount() {
		
		Integer pastDueBillsCount = 0;
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT count(_id) "
				+ " FROM bills "
				+ " WHERE payment < bill_amount AND status <> ? "
				+ " ORDER BY bill_due_date ",
				new String[] { status });
		
		if (cursor.moveToNext()) {
			
			pastDueBillsCount = cursor.getInt(0);
		}
		
		cursor.close();

		return pastDueBillsCount;
	}
	
	public List<Bills> getProductPurchaseBills(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String type = Constant.BILL_TYPE_PRODUCT_PURCHASE;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM bills "
				+ " WHERE (bill_reference_no like ? OR supplier_name like ? OR remarks like ? ) AND bill_type = ? AND status <> ? "
				+ " ORDER BY delivery_date DESC LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, queryStr, type, status, limit, lastIdx});
		
		List<Bills> list = new ArrayList<Bills>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Bills item = getBills(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<BillsBean> getBillsForUpload(int limit) {

		QueryBuilder<Bills> qb = billsDao.queryBuilder();
		qb.where(BillsDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(BillsDao.Properties.DeliveryDate);
		
		Query<Bills> q = qb.build();
		
		ArrayList<BillsBean> billsBeans = new ArrayList<BillsBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Bills bills : q.list().subList(0, maxIndex)) {
			
			billsBeans.add(BeanUtil.getBean(bills));
		}
		
		return billsBeans;
	}
	
	public void updateBills(List<BillsBean> bills) {
		
		DbUtil.getDb().beginTransaction();
		
		List<BillsBean> shiftedBeans = new ArrayList<BillsBean>();
		
		for (BillsBean bean : bills) {
			
			boolean isAdd = false;
			
			Bills bill = billsDao.load(bean.getRemote_id());
			
			if (bill == null) {
				bill = new Bills();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(bill.getRefId(), bean.getRef_id())) {
				BillsBean shiftedBean = BeanUtil.getBean(bill);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(bill, bean);
			
			if (isAdd) {
				billsDao.insert(bill);
			} else {
				billsDao.update(bill);
			}
			
			updatePaymentDetails(bill.getId());
		}
		
		for (BillsBean bean : shiftedBeans) {
			
			Bills bill = new Bills();
			BeanUtil.updateBean(bill, bean);
			
			Long oldId = bill.getId();
			
			bill.setId(null);
			bill.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = billsDao.insert(bill);
			
			updateInventoryFk(oldId, newId);
			updateCashflowFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateInventoryFk(Long oldId, Long newId) {
		
		Bills oldBill = billsDao.load(oldId);
		
		for (Inventory i : oldBill.getInventoryList()) {
			i.setBillId(newId);
			i.setUploadStatus(Constant.STATUS_YES);
			inventoryDao.update(i);
		}
	}
	
	private void updateCashflowFk(Long oldId, Long newId) {
		
		Bills oldBill = billsDao.load(oldId);
		
		for (Cashflow c : oldBill.getCashflowList()) {
			c.setBillId(newId);
			c.setUploadStatus(Constant.STATUS_YES);
			cashflowDao.update(c);
		}
	}
	
	public void updateBillsStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Bills bills = billsDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				bills.setUploadStatus(Constant.STATUS_NO);
				billsDao.update(bills);
			}
		} 
	}
	
	public List<CashFlowYearBean> getBillYears() {
		
		ArrayList<CashFlowYearBean> cashFlowYears = new ArrayList<CashFlowYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', payment_date/1000, 'unixepoch', 'localtime'), SUM(payment) payment "
				+ " FROM bills "
				+ " WHERE payment_date IS NOT NULL AND payment IS NOT NULL "
				+ " GROUP BY strftime('%Y', payment_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double value = cursor.getDouble(1);
			CashFlowYearBean cashFlowYear = new CashFlowYearBean();
			cashFlowYear.setYear(date);
			cashFlowYear.setAmount(value);
			cashFlowYears.add(cashFlowYear);
		}
		
		cursor.close();
		
		return cashFlowYears;
	}
	
	public List<CashFlowMonthBean> getBillMonths(CashFlowYearBean cashFlowYear) {
		
		ArrayList<CashFlowMonthBean> cashFlowMonths = new ArrayList<CashFlowMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(cashFlowYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(cashFlowYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', payment_date/1000, 'unixepoch', 'localtime'), SUM(payment) payment "
				+ " FROM bills "
				+ " WHERE payment_date BETWEEN ? AND ? AND payment IS NOT NULL "
				+ " GROUP BY strftime('%m-%Y', payment_date/1000, 'unixepoch', 'localtime') ", 
				new String[] { startDate, endDate });
		
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double value = cursor.getDouble(1);
			CashFlowMonthBean cashFlowMonth = new CashFlowMonthBean();
			cashFlowMonth.setMonth(date);
			cashFlowMonth.setAmount(value);
			cashFlowMonths.add(cashFlowMonth);
		}
		
		cursor.close();
		
		return cashFlowMonths;
	}
	
	public void updatePaymentDetails(Long billId) {
		
		Bills bill = getBills(billId);
		
		Date lastPaymentDate = null;
		Float totalPayment = Float.valueOf(0);
		
		QueryBuilder<Cashflow> qb = cashflowDao.queryBuilder();
		qb.where(CashflowDao.Properties.BillId.eq(billId), 
				CashflowDao.Properties.Status.notEq(Constant.STATUS_DELETED)).orderAsc(CashflowDao.Properties.CashDate);
		
		Query<Cashflow> q = qb.build();
		
		for (Cashflow cashflow : q.list()) {
			
			lastPaymentDate = cashflow.getCashDate();
			totalPayment += Math.abs(cashflow.getCashAmount());
		}
		
		bill.setPaymentDate(lastPaymentDate);
		bill.setPayment(totalPayment);
		bill.setUploadStatus(Constant.STATUS_YES);

		updateBills(bill);
	}
	
	public boolean hasUpdate() {
		
		return getBillsForUploadCount() > 0;
	}
	
	public long getBillsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM bills WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

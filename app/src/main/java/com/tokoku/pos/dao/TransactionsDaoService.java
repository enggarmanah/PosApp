package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Cashflow;
import com.android.pos.dao.CashflowDao;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.TransactionItemDao;
import com.android.pos.dao.Transactions;
import com.android.pos.dao.TransactionsDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class TransactionsDaoService {
	
	private TransactionsDao mTransactionsDao = DbUtil.getSession().getTransactionsDao();
	private TransactionItemDao mTransactionItemDao = DbUtil.getSession().getTransactionItemDao();
	private CashflowDao mCashflowDao = DbUtil.getSession().getCashflowDao();

	private TransactionItemDaoService mTransactionItemDaoService = new TransactionItemDaoService();
	private InventoryDaoService mInventoryDaoService = new InventoryDaoService();
	
	public void addTransactions(Transactions transactions) {
		
		if (CommonUtil.isEmpty(transactions.getRefId())) {
			transactions.setRefId(CommonUtil.generateRefId());
		}
		
		mTransactionsDao.insert(transactions);
	}
	
	public void updateTransactions(Transactions transactions) {
		
		mTransactionsDao.update(transactions);
	}
	
	public void deleteTransactions(Transactions transactions) {

		Transactions entity = mTransactionsDao.load(transactions.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		mTransactionsDao.update(entity);
	}
	
	public Transactions getTransactions(Long id) {
		
		Transactions transaction = mTransactionsDao.load(id);
		
		if (transaction != null) {
			mTransactionsDao.detach(transaction);
		}
		
		return transaction;
	}
	
	public List<TransactionsBean> getUnpaidTransactions(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String paymentType = Constant.PAYMENT_TYPE_CREDIT;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT t._id, c.cash_amount "
				+ " FROM transactions t LEFT JOIN (SELECT transaction_id, SUM(cash_amount) cash_amount FROM cashflow GROUP BY transaction_id) c ON t._id = c.transaction_id "
				+ " WHERE (transaction_no like ? OR customer_name like ? ) AND (c.cash_amount IS NULL OR c.cash_amount + t.payment_amount < t.total_amount) AND payment_type = ? AND status <> ? "
				+ " ORDER BY transaction_date DESC, transaction_no ASC LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, paymentType, status, limit, lastIdx});
		
		List<TransactionsBean> list = new ArrayList<TransactionsBean>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Float totalCreditPayment = cursor.getFloat(1);
			
			Transactions item = getTransactions(id);
			
			TransactionsBean bean = BeanUtil.getBean(item); 
			bean.setTotal_credit_payment(totalCreditPayment);
			
			list.add(bean);
		}
		
		cursor.close();

		return list;
	}
	
	public List<TransactionsBean> getCreditTransactions(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String paymentType = Constant.PAYMENT_TYPE_CREDIT;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT t._id, IFNULL(c.cash_amount, 0) cash_amount, (t.total_amount - t.payment_amount - IFNULL(c.cash_amount, 0)) outstanding_amount  "
				+ " FROM transactions t LEFT JOIN (SELECT transaction_id, SUM(cash_amount) cash_amount FROM cashflow WHERE status <> ? GROUP BY transaction_id) c ON t._id = c.transaction_id "
				+ " WHERE (transaction_no like ? OR customer_name like ? ) AND payment_type = ? AND status <> ? "
				+ " ORDER BY outstanding_amount DESC, transaction_date ASC LIMIT ? OFFSET ? ",
				new String[] { status, queryStr, queryStr, paymentType, status, limit, lastIdx});
		
		List<TransactionsBean> list = new ArrayList<TransactionsBean>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Float totalCreditPayment = cursor.getFloat(1);
			
			Transactions item = getTransactions(id);
			
			TransactionsBean bean = BeanUtil.getBean(item); 
			bean.setTotal_credit_payment(totalCreditPayment);
			
			list.add(bean);
		}
		
		cursor.close();

		return list;
	}
	
	public List<Transactions> getTransactions(String query, Date date, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String startDate = String.valueOf(date.getTime());
		String endDate = String.valueOf(CommonUtil.getEndDay(date).getTime());
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM transactions "
				+ " WHERE transaction_date BETWEEN ? AND ? AND (transaction_no like ? OR customer_name like ? ) AND status <> ? "
				+ " ORDER BY transaction_date ASC, transaction_no DESC LIMIT ? OFFSET ? ",
				new String[] { startDate, endDate, queryStr, queryStr, status, limit, lastIdx});
		
		List<Transactions> list = new ArrayList<Transactions>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Transactions item = getTransactions(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<TransactionsBean> getTransactionsForUpload(int limit) {

		QueryBuilder<Transactions> qb = mTransactionsDao.queryBuilder();
		qb.where(TransactionsDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(TransactionsDao.Properties.Id);
		
		Query<Transactions> q = qb.build();
		
		ArrayList<TransactionsBean> transactionsBeans = new ArrayList<TransactionsBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Transactions transaction : q.list().subList(0, maxIndex)) {
			
			transactionsBeans.add(BeanUtil.getBean(transaction));
		}
		
		return transactionsBeans;
	}
	
	public void updateTransactions(List<TransactionsBean> transactions) {
		
		DbUtil.getDb().beginTransaction();
		
		List<TransactionsBean> shiftedBeans = new ArrayList<TransactionsBean>();
		
		for (TransactionsBean bean : transactions) {
			
			boolean isAdd = false;
			
			Transactions transaction = mTransactionsDao.load(bean.getRemote_id());
			
			if (transaction == null) {
				transaction = new Transactions();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(transaction.getRefId(), bean.getRef_id())) {
				TransactionsBean shiftedBean = BeanUtil.getBean(transaction);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(transaction, bean);
			
			if (isAdd) {
				mTransactionsDao.insert(transaction);
			} else {
				mTransactionsDao.update(transaction);
			}
		}
				
		for (TransactionsBean bean : shiftedBeans) {
			
			Transactions transaction = new Transactions();
			BeanUtil.updateBean(transaction, bean);
			
			Long oldId = transaction.getId();
			
			transaction.setId(null);
			transaction.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = mTransactionsDao.insert(transaction);
			
			updateTransactionItemFk(oldId, newId);
			updateCashflowFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateTransactionItemFk(Long oldId, Long newId) {
		
		Transactions t = mTransactionsDao.load(oldId);
		
		for (TransactionItem ti : t.getTransactionItemList()) {
			
			ti.setTransactionId(newId);
			ti.setUploadStatus(Constant.STATUS_YES);
			mTransactionItemDao.update(ti);
		}
	}
	
	private void updateCashflowFk(Long oldId, Long newId) {
		
		Transactions t = mTransactionsDao.load(oldId);
		
		for (Cashflow c : t.getCashflowList()) {
			
			c.setTransactionId(newId);
			c.setUploadStatus(Constant.STATUS_YES);
			mCashflowDao.update(c);
		}
	}
	
	public void updateTransactionsStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Transactions transactions = mTransactionsDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				transactions.setUploadStatus(Constant.STATUS_NO);
				mTransactionsDao.update(transactions);
			}
		} 
	}
	
	public List<TransactionYearBean> getTransactionYears() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(total_amount) total_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double amount = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(amount);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionMonthBean> getTransactionMonths(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(total_amount) total_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(amount);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public List<TransactionDayBean> getTransactionDays(TransactionMonthBean transactionMonth) {
		
		ArrayList<TransactionDayBean> transactionDays = new ArrayList<TransactionDayBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(total_amount) total_amount "
				+ " FROM transactions " 
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionDayBean summary = new TransactionDayBean();
			summary.setDate(date);
			summary.setAmount(amount);
			transactionDays.add(summary);
		}
		
		cursor.close();
		
		return transactionDays;
	}
	
	public List<TransactionYearBean> getTransactionTaxYears() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(tax_amount) tax_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double amount = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(amount);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionMonthBean> getTransactionTaxMonths(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(tax_amount) tax_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(amount);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public List<TransactionDayBean> getTransactionTaxDays(TransactionMonthBean transactionMonth) {
		
		ArrayList<TransactionDayBean> transactionDays = new ArrayList<TransactionDayBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(tax_amount) tax_amount "
				+ " FROM transactions " 
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionDayBean summary = new TransactionDayBean();
			summary.setDate(date);
			summary.setAmount(amount);
			transactionDays.add(summary);
		}
		
		cursor.close();
		
		return transactionDays;
	}
	
public List<TransactionYearBean> getTransactionServiceChargeYears() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(service_charge_amount) servie_charge_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double amount = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(amount);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionMonthBean> getTransactionServiceChargeMonths(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(service_charge_amount) service_charge_amount "
				+ " FROM transactions "
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(amount);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public List<TransactionDayBean> getTransactionServiceChargeDays(TransactionMonthBean transactionMonth) {
		
		ArrayList<TransactionDayBean> transactionDays = new ArrayList<TransactionDayBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(service_charge_amount) service_charge_amount "
				+ " FROM transactions " 
				+ " WHERE status = 'A' AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%d-%m-%Y', transaction_date/1000, 'unixepoch', 'localtime')", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
			Double amount = cursor.getDouble(1);
			TransactionDayBean summary = new TransactionDayBean();
			summary.setDate(date);
			summary.setAmount(amount);
			transactionDays.add(summary);
		}
		
		cursor.close();
		
		return transactionDays;
	}
	
	public List<Transactions> getTransactions(Date transactionDate) {
		
		Date startDate = transactionDate;
		Date endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000 - 1);
		
		QueryBuilder<Transactions> qb = mTransactionsDao.queryBuilder();
		qb.where(TransactionsDao.Properties.Status.eq(Constant.STATUS_ACTIVE),
				TransactionsDao.Properties.TransactionDate.between(startDate, endDate))
		  .orderAsc(TransactionsDao.Properties.TransactionDate);

		Query<Transactions> q = qb.build();
		List<Transactions> list = q.list();
		
		return list;
	}

	public void reUploadTransactions(Date transactionDate) {

		List<Transactions> transactions = getTransactions(transactionDate);

		for (Transactions transaction : transactions) {

			transaction.setUploadStatus(Constant.STATUS_YES);
			updateTransactions(transaction);

			List<TransactionItem> transactionItems = transaction.getTransactionItemList();

			for (TransactionItem transactionItem : transactionItems) {
				transactionItem.setUploadStatus(Constant.STATUS_YES);
				mTransactionItemDaoService.updateTransactionItem(transactionItem);
			}

			List<Inventory> inventories = mInventoryDaoService.getInventories(transaction.getTransactionNo());

			for (Inventory inventory : inventories) {
				inventory.setUploadStatus(Constant.STATUS_YES);
				mInventoryDaoService.updateInventory(inventory);
			}
		}
	}
	
	public boolean hasUpdate() {
	
		return getTransactionsForUploadCount() > 0;
	}
	
	public long getTransactionsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM transactions WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Customer;
import com.android.pos.dao.CustomerDao;
import com.android.pos.dao.Transactions;
import com.android.pos.dao.TransactionsDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.CustomerBean;
import com.tokoku.pos.model.CustomerStatisticBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class CustomerDaoService {
	
	private CustomerDao customerDao = DbUtil.getSession().getCustomerDao();
	private TransactionsDao transactionDao = DbUtil.getSession().getTransactionsDao();
	
	public void addCustomer(Customer customer) {
		
		if (CommonUtil.isEmpty(customer.getRefId())) {
			customer.setRefId(CommonUtil.generateRefId());
		}
		
		customerDao.insert(customer);
	}
	
	public void updateCustomer(Customer customer) {
		
		customerDao.update(customer);
	}
	
	public void deleteCustomer(Customer customer) {

		Customer entity = customerDao.load(customer.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		customerDao.update(entity);
	}
	
	public Customer getCustomer(Long id) {
		
		Customer customer =  customerDao.load(id);
		
		if (customer != null) {
			customerDao.detach(customer);
		}
		
		return customer;
	}
	
	public List<Customer> getCustomers(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM customer "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<Customer> list = new ArrayList<Customer>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Customer item = getCustomer(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<CustomerBean> getCustomersForUpload(int limit) {

		QueryBuilder<Customer> qb = customerDao.queryBuilder();
		qb.where(CustomerDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(CustomerDao.Properties.Name);
		
		Query<Customer> q = qb.build();
		
		ArrayList<CustomerBean> customerBeans = new ArrayList<CustomerBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Customer prdGroup : q.list().subList(0, maxIndex)) {
			
			customerBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return customerBeans;
	}
	
	public void updateCustomers(List<CustomerBean> customers) {
		
		DbUtil.getDb().beginTransaction();
		
		List<CustomerBean> shiftedBeans = new ArrayList<CustomerBean>();
		
		for (CustomerBean bean : customers) {
			
			boolean isAdd = false;
			
			Customer customer = customerDao.load(bean.getRemote_id());
			
			if (customer == null) {
				customer = new Customer();
				isAdd = true;
				
			} else if (!CommonUtil.compareString(customer.getRefId(), bean.getRef_id())) {
				CustomerBean shiftedBean = BeanUtil.getBean(customer);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(customer, bean);
			
			if (isAdd) {
				customerDao.insert(customer);
			} else {
				customerDao.update(customer);
			}
		} 
		
		for (CustomerBean bean : shiftedBeans) {
			
			Customer customer = new Customer();
			BeanUtil.updateBean(customer, bean);
			
			Long oldId = customer.getId();
			
			customer.setId(null);
			customer.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = customerDao.insert(customer);
			
			updateTransactionsFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateTransactionsFk(Long oldId, Long newId) {
		
		Customer customer = customerDao.load(oldId);
		
		for (Transactions transactions : customer.getTransactionsList()) {
			
			transactions.setCustomerId(newId);
			transactions.setUploadStatus(Constant.STATUS_YES);
			transactionDao.update(transactions);
		}
	}
	
	public void updateCustomerStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Customer customer = customerDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				customer.setUploadStatus(Constant.STATUS_NO);
				customerDao.update(customer);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getCustomersForUploadCount() > 0;
	}
	
	public long getCustomersForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM customer WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
	
	public List<CustomerStatisticBean> getCustomerStatistics(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT "
				+ " t.customer_id, sum(ti.quantity) quantity, sum(ti.quantity * ti.price) amount "
				+ " FROM customer c, transactions t, transaction_item ti "
				+ " WHERE "
				+ " c._id = t.customer_id AND "
				+ " t._id = ti.transaction_id AND "
				+ " c.name like ? AND t.status <> ? "
				+ " GROUP BY t.customer_id "
				+ " ORDER BY quantity DESC LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<CustomerStatisticBean> list = new ArrayList<CustomerStatisticBean>();
		
		while(cursor.moveToNext()) {
			
			CustomerStatisticBean customerStatistic = new CustomerStatisticBean();
			
			Customer customer = customerDao.load(cursor.getLong(0));
			
			customerStatistic.setCustomer(customer);
			customerStatistic.setName(customer.getName());
			customerStatistic.setQuantity(cursor.getLong(1));
			customerStatistic.setAmount(cursor.getLong(2));
			
			list.add(customerStatistic);
		}
		
		cursor.close();
		
		return list;
	}
	
	public Long getTransactionsCount(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT count(_id) "
				+ " FROM transactions t "
				+ " WHERE "
				+ " t.customer_id = ? AND "
				+ " t.status <> ? ",
				new String[] { customerId, status });
		
		Long transcationsCount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			transcationsCount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return transcationsCount;
	}
	
	public Long getProductsCount(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT sum(ti.quantity) "
				+ " FROM "
				+ "   transactions t, transaction_item ti "
				+ " WHERE "
				+ "   t._id = ti.transaction_id AND "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? "
				+ " GROUP BY "
				+ "   t.customer_id ",
				new String[] { customerId, status });
		
		Long productsCount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			productsCount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return productsCount;
	}
	
	public Long getTransactionsAmount(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT sum(ti.quantity * ti.price) "
				+ " FROM "
				+ "   transactions t, transaction_item ti "
				+ " WHERE "
				+ "   t._id = ti.transaction_id AND "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? ",
				new String[] { customerId, status });
		
		Long transcationsAmount = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			transcationsAmount = cursor.getLong(0);
		}
		
		cursor.close();
		
		return transcationsAmount;
	}
	
	public Date getFirstTransaction(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', min(transaction_date)/1000, 'unixepoch', 'localtime') "
				+ " FROM "
				+ "   transactions t "
				+ " WHERE "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? ",
				new String[] { customerId, status });
		
		Date firstTransaction = null;
		
		if (cursor.moveToNext()) {
			
			firstTransaction = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
		}
		
		cursor.close();
		
		return firstTransaction;
	}
	
	public Date getLastTransaction(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y', max(transaction_date)/1000, 'unixepoch', 'localtime') "
				+ " FROM "
				+ "   transactions t "
				+ " WHERE "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? ",
				new String[] { customerId, status });
		
		Date lastTransaction = null;
		
		if (cursor.moveToNext()) {
			
			lastTransaction = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy");
		}
		
		cursor.close();
		
		return lastTransaction;
	}
	
	public Long getMinTransactions(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT min(t.total_amount) "
				+ " FROM "
				+ "   transactions t "
				+ " WHERE "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? ",
				new String[] { customerId, status });
		
		Long minTransaction = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			minTransaction = cursor.getLong(0);
		}
		
		cursor.close();
		
		return minTransaction;
	}
	
	public Long getMaxTransactions(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		String status = Constant.STATUS_DELETED;
		
		Cursor cursor = db.rawQuery("SELECT max(t.total_amount) "
				+ " FROM "
				+ "   transactions t "
				+ " WHERE "
				+ "   t.customer_id = ? AND "
				+ "   t.status <> ? ",
				new String[] { customerId, status });
		
		Long maxTransaction = Long.valueOf(0);
		
		if (cursor.moveToNext()) {
			
			maxTransaction = cursor.getLong(0);
		}
		
		cursor.close();
		
		return maxTransaction;
	}
}

package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Employee;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductDao;
import com.android.pos.dao.ProductGroup;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.TransactionItemDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.CommisionMonthBean;
import com.tokoku.pos.model.CommisionYearBean;
import com.tokoku.pos.model.EmployeeCommisionBean;
import com.tokoku.pos.model.ProductBean;
import com.tokoku.pos.model.ProductStatisticBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ProductDaoService {
	
	private ProductDao productDao = DbUtil.getSession().getProductDao();
	private TransactionItemDao transactionItemDao = DbUtil.getSession().getTransactionItemDao();
	private InventoryDao inventoryDao = DbUtil.getSession().getInventoryDao();
	
	public void addProduct(Product product) {
		
		if (CommonUtil.isEmpty(product.getRefId())) {
			product.setRefId(CommonUtil.generateRefId());
		}
		
		productDao.insert(product);
	}
	
	public void updateProduct(Product product) {
		
		productDao.update(product);
	}
	
	public void deleteProduct(Product product) {

		Product entity = productDao.load(product.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		productDao.update(entity);
	}
	
	public Product getProduct(Long id) {
		
		Product product = productDao.load(id);
		
		if (product != null) {
			productDao.detach(product);
		}
		
		return product;
	}
	
	public List<Product> getProducts(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM product "
				+ " WHERE (name LIKE ? OR code LIKE ?) AND status <> ? "
				+ " ORDER BY name, price1 LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, status, limit, lastIdx});
		
		List<Product> list = new ArrayList<Product>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Product item = getProduct(id);
			list.add(item);
		}

		cursor.close();
		
		return list;
	}
	
	public List<Product> getGoodsProducts(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String productType = Constant.PRODUCT_TYPE_GOODS;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM product "
				+ " WHERE (name LIKE ? OR code LIKE ?) AND type = ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, queryStr, productType, status, limit, lastIdx});
		
		List<Product> list = new ArrayList<Product>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Product item = getProduct(id);
			list.add(item);
		}

		cursor.close();
		
		return list;
	}
	
	public List<Product> getBelowStockLimitProducts(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String productType = Constant.PRODUCT_TYPE_GOODS;
		String status = Constant.STATUS_ACTIVE;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM product "
				+ " WHERE name like ? AND type = ? AND status = ? "
				+ " AND stock < min_stock "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, productType, status, limit, lastIdx});
		
		List<Product> list = new ArrayList<Product>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Product item = getProduct(id);
			list.add(item);
		}

		cursor.close();
		
		return list;
	}
	
	public List<Product> getProducts(String query, ProductGroup productGroup) {

		QueryBuilder<Product> qb = productDao.queryBuilder();
		
		if (productGroup == null) {
			qb.where(qb.and(qb.or(ProductDao.Properties.Name.like("%" + query + "%"), 
					              ProductDao.Properties.Code.like("%" + query + "%")),
					ProductDao.Properties.Status.notEq(Constant.STATUS_INACTIVE),
					ProductDao.Properties.Status.notEq(Constant.STATUS_DELETED))).orderAsc(ProductDao.Properties.Name);
		} else {
			qb.where(qb.and(qb.or(ProductDao.Properties.Name.like("%" + query + "%"), 
		              			  ProductDao.Properties.Code.like("%" + query + "%")),
		              ProductDao.Properties.ProductGroupId.eq(productGroup.getId()),
		              ProductDao.Properties.Status.notEq(Constant.STATUS_INACTIVE),
		              ProductDao.Properties.Status.notEq(Constant.STATUS_DELETED))).orderAsc(ProductDao.Properties.Name);
		}

		Query<Product> q = qb.build();
		List<Product> list = q.list();
		
		return list;
	}
	
	public List<Product> getProducts() {

		QueryBuilder<Product> qb = productDao.queryBuilder();
		qb.where(ProductDao.Properties.Status.notEq(Constant.STATUS_DELETED)).orderAsc(ProductDao.Properties.Name);
		
		Query<Product> q = qb.build();
		
		return q.list();
	}
	
	public List<Product> getProducts(String type) {

		QueryBuilder<Product> qb = productDao.queryBuilder();
		qb.where(ProductDao.Properties.Type.eq(type), ProductDao.Properties.Status.notEq(Constant.STATUS_DELETED)).orderAsc(ProductDao.Properties.Name);
		
		Query<Product> q = qb.build();
		
		return q.list();
	}
	
	public List<ProductBean> getProductsForUpload(int limit) {

		QueryBuilder<Product> qb = productDao.queryBuilder();
		qb.where(ProductDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(ProductDao.Properties.Name);
		
		Query<Product> q = qb.build();
		
		ArrayList<ProductBean> productBeans = new ArrayList<ProductBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Product prdGroup : q.list().subList(0, maxIndex)) {
			
			productBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return productBeans;
	}
	
	public void updateProducts(List<ProductBean> products) {
		
		DbUtil.getDb().beginTransaction();
		
		List<ProductBean> shiftedBeans = new ArrayList<ProductBean>();
		
		for (ProductBean bean : products) {
			
			boolean isAdd = false;
			
			Product product = productDao.load(bean.getRemote_id());
			
			if (product == null) {
				product = new Product();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(product.getRefId(), bean.getRef_id())) {
				ProductBean shiftedBean = BeanUtil.getBean(product);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(product, bean);
			
			if (isAdd) {
				productDao.insert(product);
			} else {
				productDao.update(product);
			}
		}
		
		for (ProductBean bean : shiftedBeans) {
			
			Product product = new Product();
			BeanUtil.updateBean(product, bean);
			
			Long oldId = product.getId();
			
			product.setId(null);
			product.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = productDao.insert(product);
			
			updateTransactionItemFk(oldId, newId);
			updateInventoryFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateTransactionItemFk(Long oldId, Long newId) {
		
		Product p = productDao.load(oldId);
		
		for (TransactionItem ti : p.getTransactionItemList()) {
			
			ti.setProductId(newId);
			ti.setUploadStatus(Constant.STATUS_YES);
			transactionItemDao.update(ti);
		}
	}
	
	private void updateInventoryFk(Long oldId, Long newId) {
		
		Product p = productDao.load(oldId);
		
		for (Inventory i : p.getInventoryList()) {
			
			i.setProductId(newId);
			i.setUploadStatus(Constant.STATUS_YES);
			inventoryDao.update(i);
		}
	}
	
	public void updateProductStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Product product = productDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				product.setUploadStatus(Constant.STATUS_NO);
				productDao.update(product);
			}
		} 
	}
	
	public void updateProductQuantity() {
		
		SQLiteDatabase db = DbUtil.getDb();
		String status = Constant.STATUS_DELETED;
		
		db.execSQL("UPDATE product SET stock = (SELECT SUM(quantity) "
				+ " FROM inventory "
				+ " WHERE product_id = product._id AND status <> ?) ",
				new String[] { status });
	}
	
	public List<ProductStatisticBean> getProductStatisticsQuantity(TransactionMonthBean transactionMonth) {
		
		ArrayList<ProductStatisticBean> productStatistics = new ArrayList<ProductStatisticBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT product_name, SUM(quantity) quantity "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY product_name "
				+ " ORDER BY quantity DESC, product_name ASC ", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			String productName = cursor.getString(0);
			Double value = cursor.getDouble(1);
			
			ProductStatisticBean productStatistic = new ProductStatisticBean();
			
			productStatistic.setProduct_name(productName);
			productStatistic.setValue(value);
			
			productStatistics.add(productStatistic);
		}
		
		cursor.close();
		
		return productStatistics;
	}
	
	public List<ProductStatisticBean> getProductStatisticsRevenue(TransactionMonthBean transactionMonth) {
		
		ArrayList<ProductStatisticBean> productStatistics = new ArrayList<ProductStatisticBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT product_name, SUM((price - (CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END)) * quantity) revenue "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY product_name "
				+ " ORDER BY revenue DESC, product_name ASC ", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			String productName = cursor.getString(0);
			Double value = cursor.getDouble(1);
			
			ProductStatisticBean productStatistic = new ProductStatisticBean();
			
			productStatistic.setProduct_name(productName);
			productStatistic.setValue(value);
			
			productStatistics.add(productStatistic);
		}
		
		cursor.close();
		
		return productStatistics;
	}
	
	public List<ProductStatisticBean> getProductStatisticsProfit(TransactionMonthBean transactionMonth) {
		
		ArrayList<ProductStatisticBean> productStatistics = new ArrayList<ProductStatisticBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(transactionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(transactionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT product_name, SUM((price - (CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END) - cost_price) * quantity) profit "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY product_name "
				+ " ORDER BY profit DESC, product_name ASC ", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			String productName = cursor.getString(0);
			Double value = cursor.getDouble(1);
			
			ProductStatisticBean productStatistic = new ProductStatisticBean();
			
			productStatistic.setProduct_name(productName);
			productStatistic.setValue(value);
			
			productStatistics.add(productStatistic);
		}
		
		cursor.close();
		
		return productStatistics;
	}
	
	public List<TransactionYearBean> getTransactionYearsQuantity() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(quantity) quantity "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double value = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(value);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionYearBean> getTransactionYearsRevenue() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM((price-(CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END)) * quantity) revenue "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double value = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(value);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionYearBean> getTransactionYearsProfit() {
		
		ArrayList<TransactionYearBean> transactionYears = new ArrayList<TransactionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM((price - (CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END) - cost_price) * quantity) profit "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id "
				+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')", null);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Double value = cursor.getDouble(1);
			TransactionYearBean transactionYear = new TransactionYearBean();
			transactionYear.setYear(date);
			transactionYear.setAmount(value);
			transactionYears.add(transactionYear);
		}
		
		cursor.close();
		
		return transactionYears;
	}
	
	public List<TransactionMonthBean> getTransactionMonthsQuantity(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(quantity) quantity "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime') ", 
				new String[] { startDate, endDate });
		
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double value = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(value);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public List<TransactionMonthBean> getTransactionMonthsRevenue(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM((price-(CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END)) * quantity) revenue "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime') ", 
				new String[] { startDate, endDate });
		
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double value = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(value);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public List<TransactionMonthBean> getTransactionMonthsProfit(TransactionYearBean transactionYear) {
		
		ArrayList<TransactionMonthBean> transactionMonths = new ArrayList<TransactionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(transactionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(transactionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM((price - (CASE WHEN discount IS NOT NULL THEN discount ELSE 0 END) - cost_price) * quantity) profit "
				+ " FROM transactions t, transaction_item ti "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime') ", 
				new String[] { startDate, endDate });
		
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Double value = cursor.getDouble(1);
			TransactionMonthBean transactionMonth = new TransactionMonthBean();
			transactionMonth.setMonth(date);
			transactionMonth.setAmount(value);
			transactionMonths.add(transactionMonth);
		}
		
		cursor.close();
		
		return transactionMonths;
	}
	
	public boolean hasUpdate() {
	
		return getProductsForUploadCount() > 0;
	}
	
	public long getProductsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM product WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
	
	public Integer getBelowStockLimitProductCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM product WHERE stock < min_stock  AND status = 'A'", null);
			
		cursor.moveToFirst();
			
		Integer count = cursor.getInt(0);
		
		cursor.close();
		
		return count;
	}
	
	public List<EmployeeCommisionBean> getEmployeeCommisions(CommisionMonthBean commisionMonth) {
		
		ArrayList<EmployeeCommisionBean> employeeCommisions = new ArrayList<EmployeeCommisionBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(commisionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(commisionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT ti.employee_id, e.name employee_name, SUM(commision * quantity) commision "
				+ " FROM transactions t, transaction_item ti, employee e "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND ti.employee_id = e._id AND transaction_date BETWEEN ? AND ? "
				+ " GROUP BY employee_name "
				+ " ORDER BY employee_name DESC, commision ASC ", new String[] { startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Long employeeId = cursor.getLong(0);
			String employeeName = cursor.getString(1);
			Double commision = cursor.getDouble(2);
			
			EmployeeCommisionBean employeeCommision = new EmployeeCommisionBean();
			
			employeeCommision.setEmployee_id(employeeId);
			employeeCommision.setEmployee_name(employeeName);
			employeeCommision.setCommision(commision);
			
			employeeCommisions.add(employeeCommision);
		}
		
		cursor.close();
		
		return employeeCommisions;
	}
	
	public List<CommisionYearBean> getCommisionYears(Employee employee) {
		
		ArrayList<CommisionYearBean> commisionYears = new ArrayList<CommisionYearBean>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String query = null; 
		String[] parameters = null;
		
		if (employee != null) {
			
			query = "SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(commision * quantity) commision "
					+ " FROM transactions t, transaction_item ti "
					+ " WHERE t.status = 'A' AND ti.employee_id = ? AND t._id = ti.transaction_id"
					+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')";
			
			parameters = new String[] { employee.getId().toString() };
			
		} else {
			
			query = "SELECT strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(commision) commision "
					+ " FROM transactions t, transaction_item ti "
					+ " WHERE t.status = 'A' AND t._id = ti.transaction_id "
					+ " GROUP BY strftime('%Y', transaction_date/1000, 'unixepoch', 'localtime')";
		}
		
		Cursor cursor = db.rawQuery(query, parameters);
			
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "yyyy");
			Float value = cursor.getFloat(1);
			CommisionYearBean commisionYear = new CommisionYearBean();
			commisionYear.setYear(date);
			commisionYear.setAmount(value);
			commisionYears.add(commisionYear);
		}
		
		cursor.close();
		
		return commisionYears;
	}
	
	public List<CommisionMonthBean> getCommisionMonths(CommisionYearBean commisionYear, Employee employee) {
		
		ArrayList<CommisionMonthBean> commisionMonths = new ArrayList<CommisionMonthBean>();
		
		String startDate = String.valueOf(CommonUtil.getFirstDayOfYear(commisionYear.getYear()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfYear(commisionYear.getYear()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String query = null; 
		String[] parameters = null;
		
		if (employee != null) {
			
			query = "SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(commision * quantity) commision "
					+ " FROM transactions t, transaction_item ti "
					+ " WHERE t.status = 'A' AND ti.employee_id = ? AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
					+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime') ";
			
			parameters = new String[] { employee.getId().toString(), startDate, endDate };
			
		} else {
			
			query = "SELECT strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime'), SUM(commision) commision "
					+ " FROM transactions t, transaction_item ti "
					+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND transaction_date BETWEEN ? AND ? "
					+ " GROUP BY strftime('%m-%Y', transaction_date/1000, 'unixepoch', 'localtime') ";
			
			parameters = new String[] { startDate, endDate };
		}
		
		Cursor cursor = db.rawQuery(query, parameters);
		
		while(cursor.moveToNext()) {
			
			Date date = CommonUtil.parseDate(cursor.getString(0), "MM-yyyy");
			Float value = cursor.getFloat(1);
			CommisionMonthBean commisionMonth = new CommisionMonthBean();
			commisionMonth.setMonth(date);
			commisionMonth.setAmount(value);
			commisionMonths.add(commisionMonth);
		}
		
		cursor.close();
		
		return commisionMonths;
	}
	
	public List<EmployeeCommisionBean> getEmployeeCommisions(CommisionMonthBean commisionMonth, Employee employee) {
		
		ArrayList<EmployeeCommisionBean> employeeCommisions = new ArrayList<EmployeeCommisionBean>();
		
		String employeeIdStr = String.valueOf(employee.getId());
		String startDate = String.valueOf(CommonUtil.getFirstDayOfMonth(commisionMonth.getMonth()).getTime());
		String endDate = String.valueOf(CommonUtil.getLastDayOfMonth(commisionMonth.getMonth()).getTime());
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', transaction_date/1000, 'unixepoch', 'localtime'), p.name product_name, quantity, (ti.commision * quantity) commision "
				+ " FROM transactions t, transaction_item ti, product p "
				+ " WHERE t.status = 'A' AND t._id = ti.transaction_id AND ti.product_id = p._id AND employee_id = ? AND transaction_date BETWEEN ? AND ? "
				+ " ORDER BY ti._id ASC ", new String[] { employeeIdStr, startDate, endDate });
			
		while(cursor.moveToNext()) {
			
			Date transactionDate = CommonUtil.parseDate(cursor.getString(0), "dd-MM-yyyy hh:mm:ss");
			String productName = cursor.getString(1);
			Integer quantity = cursor.getInt(2);
			Double commision = cursor.getDouble(3);
			
			EmployeeCommisionBean employeeCommision = new EmployeeCommisionBean();
			
			employeeCommision.setTransaction_date(transactionDate);
			employeeCommision.setProduct_name(productName);
            employeeCommision.setQuantity(quantity);
			employeeCommision.setCommision(commision);
			
			employeeCommisions.add(employeeCommision);
		}
		
		cursor.close();
		
		return employeeCommisions;
	}
}

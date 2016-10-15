package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Customer;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductDao;
import com.android.pos.dao.ProductGroup;
import com.android.pos.dao.ProductGroupDao;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.ProductGroupBean;
import com.tokoku.pos.model.ProductGroupStatisticBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ProductGroupDaoService {
	
	private ProductGroupDao productGroupDao = DbUtil.getSession().getProductGroupDao();
	private ProductDao productDao = DbUtil.getSession().getProductDao();
	
	public void addProductGroup(ProductGroup productGroup) {
		
		if (CommonUtil.isEmpty(productGroup.getRefId())) {
			productGroup.setRefId(CommonUtil.generateRefId());
		}
		
		productGroupDao.insert(productGroup);
	}
	
	public void updateProductGroup(ProductGroup productGroup) {
		
		productGroupDao.update(productGroup);
	}
	
	public void deleteProductGroup(ProductGroup productGroup) {

		ProductGroup entity = productGroupDao.load(productGroup.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		productGroupDao.update(entity);
	}
	
	public ProductGroup getProductGroup(Long id) {
		
		ProductGroup productGroup = productGroupDao.load(id);
		
		if (productGroup != null) {
			productGroupDao.detach(productGroup);
		}	
		
		return productGroup;
	}
	
	public List<ProductGroup> getProductGroups() {

		QueryBuilder<ProductGroup> qb = productGroupDao.queryBuilder();
		qb.where(ProductGroupDao.Properties.Status.eq(Constant.STATUS_ACTIVE)).orderAsc(ProductGroupDao.Properties.Name);

		Query<ProductGroup> q = qb.build();
		List<ProductGroup> list = q.list();

		return list;
	}
	
	public List<ProductGroup> getProductGroups(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM product_group "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<ProductGroup> list = new ArrayList<ProductGroup>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			ProductGroup item = getProductGroup(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<ProductGroupBean> getProductGroupsForUpload(int limit) {

		QueryBuilder<ProductGroup> qb = productGroupDao.queryBuilder();
		qb.where(ProductGroupDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(ProductGroupDao.Properties.Name);
		
		Query<ProductGroup> q = qb.build();
		
		ArrayList<ProductGroupBean> productGroupBeans = new ArrayList<ProductGroupBean>();

		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (ProductGroup prdGroup : q.list().subList(0, maxIndex)) {
			
			productGroupBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return productGroupBeans;
	}
	
	public void updateProductGroups(List<ProductGroupBean> productGroups) {
		
		DbUtil.getDb().beginTransaction();
		
		List<ProductGroupBean> shiftedBeans = new ArrayList<ProductGroupBean>();
		
		for (ProductGroupBean bean : productGroups) {
			
			boolean isAdd = false;
			
			ProductGroup productGroup = productGroupDao.load(bean.getRemote_id());
			
			if (productGroup == null) {
				productGroup = new ProductGroup();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(productGroup.getRefId(), bean.getRef_id())) {
				ProductGroupBean shiftedBean = BeanUtil.getBean(productGroup);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(productGroup, bean);
			
			if (isAdd) {
				productGroupDao.insert(productGroup);
			} else {
				productGroupDao.update(productGroup);
			}
		} 
		
		for (ProductGroupBean bean : shiftedBeans) {
			
			ProductGroup productGroup = new ProductGroup();
			BeanUtil.updateBean(productGroup, bean);
			
			Long oldId = productGroup.getId();
			
			productGroup.setId(null);
			productGroup.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = productGroupDao.insert(productGroup);
			
			updateProductFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateProductFk(Long oldId, Long newId) {
		
		ProductGroup pg = productGroupDao.load(oldId);
		
		for (Product p : pg.getProductList()) {
			
			p.setProductGroupId(newId);
			p.setUploadStatus(Constant.STATUS_YES);
			productDao.update(p);
		}
	}
	
	public void updateProductGroupStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			ProductGroup productGroup = productGroupDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				productGroup.setUploadStatus(Constant.STATUS_NO);
				productGroupDao.update(productGroup);
			}
		} 
	}
	
	public boolean hasUpdate() {
		
		return getProductGroupsForUploadCount() > 0;
	}
	
	public long getProductGroupsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM product_group WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
	
	public List<ProductGroupStatisticBean> getProductGroupStatistics(Customer customer) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String customerId = String.valueOf(customer.getId());
		
		Cursor cursor = db.rawQuery("SELECT "
				+ "   pg.name, sum(ti.quantity) quantity, sum(ti.quantity * ti.price) amount"
				+ " FROM "
				+ "   product_group pg, product p, transactions t, transaction_item ti "
				+ " WHERE "
				+ "   pg._id = p.product_group_id AND "
				+ "   p._id = ti.product_id AND "
				+ "   t._id = ti.transaction_id AND "
				+ "   t.customer_id = ? AND t.status <> 'D' "
				+ " GROUP BY pg.name "
				+ " ORDER BY quantity DESC ",
				new String[] { customerId });
		
		List<ProductGroupStatisticBean> list = new ArrayList<ProductGroupStatisticBean>();
		
		while(cursor.moveToNext()) {
			
			ProductGroupStatisticBean productGroupStatistic = new ProductGroupStatisticBean();
			
			productGroupStatistic.setName(cursor.getString(0));
			productGroupStatistic.setQuantity(cursor.getLong(1));
			productGroupStatistic.setAmount(cursor.getLong(2));
			
			list.add(productGroupStatistic);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<ProductGroupStatisticBean> getProductGroupStatistics(Supplier supplier) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String supplierId = String.valueOf(supplier.getId());
		
		Cursor cursor = db.rawQuery("SELECT "
				+ "   pg.name, sum(i.quantity) quantity, sum(i.quantity * i.product_cost_price) amount"
				+ " FROM "
				+ "   product_group pg, product p, inventory i "
				+ " WHERE "
				+ "   pg._id = p.product_group_id AND "
				+ "   p._id = i.product_id AND "
				+ "   i.supplier_id = ? AND i.status <> 'D' "
				+ " GROUP BY pg.name "
				+ " ORDER BY quantity DESC ",
				new String[] { supplierId });
		
		List<ProductGroupStatisticBean> list = new ArrayList<ProductGroupStatisticBean>();
		
		while(cursor.moveToNext()) {
			
			ProductGroupStatisticBean productGroupStatistic = new ProductGroupStatisticBean();
			
			productGroupStatistic.setName(cursor.getString(0));
			productGroupStatistic.setQuantity(cursor.getLong(1));
			productGroupStatistic.setAmount(cursor.getLong(2));
			
			list.add(productGroupStatistic);
		}
		
		cursor.close();
		
		return list;
	}
}

package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Discount;
import com.android.pos.dao.DiscountDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.DiscountBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class DiscountDaoService {
	
	private DiscountDao discountDao = DbUtil.getSession().getDiscountDao();
	
	public void addDiscount(Discount discount) {
		
		if (CommonUtil.isEmpty(discount.getRefId())) {
			discount.setRefId(CommonUtil.generateRefId());
		}
		
		discountDao.insert(discount);
	}
	
	public void updateDiscount(Discount discount) {
		
		discountDao.update(discount);
	}
	
	public void deleteDiscount(Discount discount) {

		Discount entity = discountDao.load(discount.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		discountDao.update(entity);
	}
	
	public Discount getDiscount(Long id) {
		
		Discount discount = discountDao.load(id);
		
		if (discount != null) {
			discountDao.detach(discount);
		}
		
		return discount;
	}
	
	public List<Discount> getDiscounts() {

		QueryBuilder<Discount> qb = discountDao.queryBuilder();
		qb.where(DiscountDao.Properties.Status.notEq(Constant.STATUS_DELETED)).orderAsc(DiscountDao.Properties.Percentage);

		Query<Discount> q = qb.build();
		List<Discount> list = q.list();

		return list;
	}
	
	public List<Discount> getDiscounts(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM discount "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<Discount> list = new ArrayList<Discount>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Discount item = getDiscount(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<DiscountBean> getDiscountsForUpload(int limit) {

		QueryBuilder<Discount> qb = discountDao.queryBuilder();
		qb.where(DiscountDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(DiscountDao.Properties.Name);
		
		Query<Discount> q = qb.build();
		
		ArrayList<DiscountBean> discountBeans = new ArrayList<DiscountBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Discount prdGroup : q.list().subList(0, maxIndex)) {
			
			discountBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return discountBeans;
	}
	
	public void updateDiscounts(List<DiscountBean> discounts) {
		
		DbUtil.getDb().beginTransaction();
		
		List<DiscountBean> shiftedBeans = new ArrayList<DiscountBean>();
		
		for (DiscountBean bean : discounts) {
			
			boolean isAdd = false;
			
			Discount discount = discountDao.load(bean.getRemote_id());
			
			if (discount == null) {
				discount = new Discount();
				isAdd = true;
				
			} else if (!CommonUtil.compareString(discount.getRefId(), bean.getRef_id())) {
				DiscountBean shiftedBean = BeanUtil.getBean(discount);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(discount, bean);
			
			if (isAdd) {
				discountDao.insert(discount);
			} else {
				discountDao.update(discount);
			}
		}
		
		for (DiscountBean bean : shiftedBeans) {
			
			Discount discount = new Discount();
			BeanUtil.updateBean(discount, bean);
			
			discount.setId(null);
			discount.setUploadStatus(Constant.STATUS_YES);
			
			discountDao.insert(discount);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	public void updateDiscountStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Discount discount = discountDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				discount.setUploadStatus(Constant.STATUS_NO);
				discountDao.update(discount);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getDiscountsForUploadCount() > 0;
	}
	
	public long getDiscountsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM discount WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

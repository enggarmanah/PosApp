package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.OrderItem;
import com.android.pos.dao.OrderItemDao;
import com.android.pos.dao.Orders;
import com.android.pos.dao.OrdersDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.OrdersBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class OrdersDaoService {
	
	private OrdersDao mOrdersDao = DbUtil.getSession().getOrdersDao();
	private OrderItemDao mOrderItemDao = DbUtil.getSession().getOrderItemDao();
	
	public void addOrders(Orders orders) {
		
		if (CommonUtil.isEmpty(orders.getRefId())) {
			orders.setRefId(CommonUtil.generateRefId());
		}
		
		mOrdersDao.insert(orders);
	}
	
	public void updateOrders(Orders orders) {
		
		mOrdersDao.update(orders);
	}
	
	public void deleteOrders(Orders orders) {

		Orders entity = mOrdersDao.load(orders.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		mOrdersDao.update(entity);
	}
	
	public void deletePhysicalOrders(Orders orders) {
		
		Orders order = mOrdersDao.load(orders.getId());
		
		for (OrderItem orderItem : order.getOrderItemList()) {
			
			mOrderItemDao.delete(orderItem);
		}
		
		order.delete();
	}
	
	public Orders getOrders(Long id) {
		
		Orders order = mOrdersDao.load(id);
		
		if (order != null) {
			mOrdersDao.detach(order);
		}
		
		return order;
	}
	
	public List<OrdersBean> getOrdersForUpload(int limit) {

		QueryBuilder<Orders> qb = mOrdersDao.queryBuilder();
		qb.where(OrdersDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(OrdersDao.Properties.Id);
		
		Query<Orders> q = qb.build();
		
		ArrayList<OrdersBean> orderBeans = new ArrayList<OrdersBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Orders order : q.list().subList(0, maxIndex)) {
			
			orderBeans.add(BeanUtil.getBean(order));
		}
		
		return orderBeans;
	}
	
	public Orders getOrders(String orderNo) {

		QueryBuilder<Orders> qb = mOrdersDao.queryBuilder();
		
		qb.where(OrdersDao.Properties.OrderNo.eq(orderNo)).orderAsc(OrdersDao.Properties.Id);
		
		Orders orders = qb.unique();
		
		return orders;
	}
	
	public List<Orders> getOrders() {

		QueryBuilder<Orders> qb = mOrdersDao.queryBuilder();
		
		qb.where(OrdersDao.Properties.Status.eq(Constant.STATUS_ACTIVE)).orderAsc(OrdersDao.Properties.Id);
		
		Query<Orders> q = qb.build();
		
		return q.list();
	}
	
	public List<Orders> getOrdersByOrderReference(String orderReference) {

		QueryBuilder<Orders> qb = mOrdersDao.queryBuilder();
		
		qb.where(OrdersDao.Properties.OrderReference.eq(orderReference),
				OrdersDao.Properties.Status.eq(Constant.STATUS_ACTIVE)).orderAsc(OrdersDao.Properties.Id);
		
		Query<Orders> q = qb.build();
		
		return q.list();
	}
	
	public List<String> getOrderReferences() {
		
		ArrayList<String> orderReferences = new ArrayList<String>();
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT DISTINCT order_reference "
				+ " FROM orders "
				+ " WHERE status='A' "
				+ " ORDER BY order_reference ASC ", null);
			
		while(cursor.moveToNext()) {
			
			String orderReference = cursor.getString(0);
			orderReferences.add(orderReference);
		}
		
		cursor.close();
		
		return orderReferences;
	}
	
	public void updateOrders(List<OrdersBean> orders) {
		
		DbUtil.getDb().beginTransaction();
		
		List<OrdersBean> shiftedBeans = new ArrayList<OrdersBean>();
		
		for (OrdersBean bean : orders) {
			
			boolean isAdd = false;
			
			Orders order = mOrdersDao.load(bean.getRemote_id());
			
			if (order == null) {
				order = new Orders();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(order.getRefId(), bean.getRef_id())) {
				OrdersBean shiftedBean = BeanUtil.getBean(order);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(order, bean);
			
			if (isAdd) {
				mOrdersDao.insert(order);
			} else {
				mOrdersDao.update(order);
			}
		}
				
		for (OrdersBean bean : shiftedBeans) {
			
			Orders order = new Orders();
			BeanUtil.updateBean(order, bean);
			
			Long oldId = order.getId();
			
			order.setId(null);
			order.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = mOrdersDao.insert(order);
			
			updateOrderItemFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateOrderItemFk(Long oldId, Long newId) {
		
		Orders t = mOrdersDao.load(oldId);
		
		for (OrderItem ti : t.getOrderItemList()) {
			
			ti.setOrderId(newId);
			ti.setUploadStatus(Constant.STATUS_YES);
			mOrderItemDao.update(ti);
		}
	}
	
	public void updateOrdersStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Orders orders = mOrdersDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				orders.setUploadStatus(Constant.STATUS_NO);
				mOrdersDao.update(orders);
			}
		} 
	}
	
	public boolean hasUpdate() {
		
		return getOrdersForUploadCount() > 0;
	}
	
	public long getOrdersForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM orders WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

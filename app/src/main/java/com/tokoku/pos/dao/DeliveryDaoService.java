package com.tokoku.pos.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Bills;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.InventoryDao;
import com.android.pos.dao.Product;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.Delivery;
import com.tokoku.pos.model.InventoryBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class DeliveryDaoService {
	
	private InventoryDao inventoryDao = DbUtil.getSession().getInventoryDao();
	
	public List<Delivery> getDeliveries(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = CommonUtil.getSqlLikeString(query);
		String statusSale = Constant.INVENTORY_STATUS_SALE;
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT distinct delivery_no, inventory_date "
				+ " FROM inventory "
				+ " WHERE (delivery_no like ? ) AND status <> ? AND status <> ? AND delivery_no IS NOT NULL "
				+ " ORDER BY inventory_date DESC, _id DESC LIMIT ? OFFSET ? ",
				new String[] { queryStr, statusSale, status, limit, lastIdx});
		
		List<Delivery> list = new ArrayList<Delivery>();
		
		while(cursor.moveToNext()) {

			Delivery delivery = new Delivery();
			delivery.setNo(cursor.getString(0));
			delivery.setDate(new Date(cursor.getLong(1)));
			list.add(delivery);
		}
		
		cursor.close();
		
		return list;
	}
}

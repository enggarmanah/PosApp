package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Merchant;
import com.android.pos.dao.MerchantDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MerchantDaoService {
	
	private MerchantDao merchantDao = DbUtil.getSession().getMerchantDao();
	
	public void addMerchant(Merchant merchant) {
		
		if (CommonUtil.isEmpty(merchant.getRefId())) {
			merchant.setRefId(CommonUtil.generateRefId());
		}
		
		Long id = merchantDao.insert(merchant);
		merchant.setId(id);
	}
	
	public void updateMerchant(Merchant merchant) {
		
		merchantDao.update(merchant);
	}
	
	public void deleteMerchant(Merchant merchant) {

		Merchant entity = merchantDao.load(merchant.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		merchantDao.update(entity);
	}
	
	public Merchant getMerchant(Long id) {
		
		System.out.println(DbUtil.getDb());
		
		Merchant merchant = merchantDao.load(id);
		
		if (merchant != null) {
			merchantDao.detach(merchant);
		}
		
		return merchant;
	}
	
	/* public Merchant validateMerchant(String loginId, String password) {
		
		QueryBuilder<Merchant> qb = merchantDao.queryBuilder();
		qb.where(MerchantDao.Properties.LoginId.eq(loginId), MerchantDao.Properties.Password.eq(password));

		Query<Merchant> q = qb.build();
		Merchant merchant = q.unique();
		
		if (merchant != null) {
			
			merchant.setIsLogin(true);
			merchantDao.update(merchant);
		}
		
		return merchant;
	} */
	
	public Merchant validateMerchant(String loginId, String password) {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String code = "000000" + password;
		code = "%" + code.substring(code.length()-6);
		
		String status = Constant.STATUS_ACTIVE;
		
		/*Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM merchant "
				+ " WHERE LOWER(login_id) = ? AND LOWER(password) = ? AND status = ? ",
				new String[] { loginId.toLowerCase(CommonUtil.getLocale()).trim(), password.toLowerCase(CommonUtil.getLocale()).trim(), status });*/
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM merchant "
				+ " WHERE LOWER(login_id) = ? AND (LOWER(password) = ? OR ref_id LIKE ?)",
				new String[] { loginId.toLowerCase(CommonUtil.getLocale()).trim(), password.toLowerCase(CommonUtil.getLocale()).trim(), code });
		
		Merchant merchant = null;
		
		if (cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			merchant = getMerchant(id);
			
			merchant.setIsLogin(true);
			merchantDao.update(merchant);
		}
		
		cursor.close();

		return merchant;
	}
	
	public boolean isEmptyDb() {
		
		QueryBuilder<Merchant> qb = merchantDao.queryBuilder();
		
		Query<Merchant> q = qb.build();
		List<Merchant> merchants = q.list();
		
		return merchants.size() == 0;
	}
	
	public Merchant getMerchantByLoginId(String loginId) {
		
		QueryBuilder<Merchant> qb = merchantDao.queryBuilder();
		qb.where(MerchantDao.Properties.LoginId.eq(loginId));

		Query<Merchant> q = qb.build();
		Merchant merchant = q.unique();

		return merchant;
	}
	
	public Merchant getActiveMerchant() {
		
		QueryBuilder<Merchant> qb = merchantDao.queryBuilder();
		qb.where(MerchantDao.Properties.IsLogin.eq(true));

		Query<Merchant> q = qb.build();
		
		List<Merchant> merchants = q.list();
		
		// if somehow multiple merchants is active on the device, disable all
		for (Merchant merchant : merchants) {
			
			merchant.setIsLogin(false);
			merchantDao.update(merchant);
		}
		
		Merchant merchant = null;
		
		// enable the first one on the list
		if (merchants.size() > 0) {
			
			merchant = merchants.get(0);
			merchant.setIsLogin(true);
			merchantDao.update(merchant);
		}

		return merchant;
	}
	
	public void logoutMerchant(Merchant merchant) {
		
		merchant.setIsLogin(false);
		merchantDao.update(merchant);
	}
	
	public List<Merchant> getMerchants(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM merchant "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY UPPER(name) LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<Merchant> list = new ArrayList<Merchant>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Merchant item = getMerchant(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<MerchantBean> getMerchantsForUpload(int limit) {

		QueryBuilder<Merchant> qb = merchantDao.queryBuilder();
		qb.where(MerchantDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(MerchantDao.Properties.Name);
		
		Query<Merchant> q = qb.build();
		
		ArrayList<MerchantBean> merchantBeans = new ArrayList<MerchantBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Merchant prdGroup : q.list().subList(0, maxIndex)) {
			
			merchantBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return merchantBeans;
	}
	
	public void updateMerchants(List<MerchantBean> merchants) {
		
		DbUtil.getDb().beginTransaction();
		
		List<MerchantBean> shiftedBeans = new ArrayList<MerchantBean>();
		
		for (MerchantBean bean : merchants) {
			
			boolean isAdd = false;
			
			Merchant merchant = merchantDao.load(bean.getRemote_id());
			
			if (merchant == null) {
				merchant = new Merchant();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(merchant.getRefId(), bean.getRef_id())) {
				MerchantBean shiftedBean = BeanUtil.getBean(merchant);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(merchant, bean);
			
			if (isAdd) {
				addMerchant(merchant);
			} else {
				updateMerchant(merchant);
			}
		}
		
		for (MerchantBean bean : shiftedBeans) {
			
			Merchant merchant = new Merchant();
			BeanUtil.updateBean(merchant, bean);
			
			merchant.setId(null);
			merchant.setUploadStatus(Constant.STATUS_YES);
			
			merchantDao.insert(merchant);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	public void updateMerchantStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Merchant merchant = merchantDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				merchant.setUploadStatus(Constant.STATUS_NO);
				merchantDao.update(merchant);
			}
		} 
	}
	
	public boolean hasUpdate() {
		
		return getMerchantsForUploadCount() > 0;
	}
	
	public long getMerchantsForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM merchant WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

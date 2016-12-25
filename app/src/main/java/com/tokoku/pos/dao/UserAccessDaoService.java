package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.MerchantAccess;
import com.android.pos.dao.MerchantAccessDao;
import com.android.pos.dao.UserAccess;
import com.android.pos.dao.UserAccessDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.model.UserAccessBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;
import com.tokoku.pos.util.MerchantUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class UserAccessDaoService {
	
	private UserAccessDao userAccessDao = DbUtil.getSession().getUserAccessDao();
	private MerchantAccessDao merchantAccessDao = DbUtil.getSession().getMerchantAccessDao();
	
	public void addUserAccess(UserAccess userAccess) {
		
		if (CommonUtil.isEmpty(userAccess.getRefId())) {
			userAccess.setRefId(CommonUtil.generateRefId());
		}
		
		userAccessDao.insert(userAccess);
	}
	
	public void updateUserAccess(UserAccess userAccess) {
		
		userAccessDao.update(userAccess);
	}
	
	public void updateUserAccess(List<UserAccess> userAccesses) {
		
		for (UserAccess userAccess : userAccesses) {
			
			if (userAccess.getId() == null) {
				addUserAccess(userAccess);
			} else {
				updateUserAccess(userAccess);
			}
		}
	}
	
	public void deleteUserAccess(UserAccess userAccess) {

		UserAccess entity = userAccessDao.load(userAccess.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		userAccessDao.update(entity);
	}
	
	public UserAccess getUserAccess(Long id) {
		
		UserAccess userAccess = userAccessDao.load(id);

		if (userAccess != null) {
			userAccessDao.detach(userAccess);
		}
		
		return userAccess;
	}
	
	public List<UserAccess> getUserAccessList(Long userId) {

        if (userId == null) {
			userId = Long.valueOf(-1);
		}
		
		QueryBuilder<UserAccess> qb = userAccessDao.queryBuilder();
		qb.where(UserAccessDao.Properties.UserId.eq(userId)).orderAsc(UserAccessDao.Properties.Id);
		
		Query<UserAccess> q = qb.build();
		
		ArrayList<UserAccess> userAccessList = new ArrayList<UserAccess>();
		HashMap<String, UserAccess> userAccessMap = new HashMap<String, UserAccess>();
		
		for (UserAccess userAccess : q.list()) {
			userAccessMap.put(userAccess.getCode(), userAccess);
		}
		
		for (MerchantAccess merchantAccess : getMerchantAccessList(MerchantUtil.getMerchantId())) {
			
			UserAccess userAccess = userAccessMap.get(merchantAccess.getCode());

			if (userAccess == null) {
				
				userAccess = new UserAccess();
				userAccess.setMerchantId(MerchantUtil.getMerchantId());
				userAccess.setUserId(userId);
				userAccess.setCode(merchantAccess.getCode());
				userAccess.setName(merchantAccess.getName());
				userAccess.setStatus(Constant.STATUS_NO);
			}
			
			userAccessList.add(getCopy(userAccess));
		}
		
		return userAccessList;
	}

    public UserAccess getCopy(UserAccess bean) {

        UserAccess userAccess = new UserAccess();

        userAccess.setMerchantId(bean.getMerchantId());
        userAccess.setId(bean.getId());
        userAccess.setRefId(bean.getRefId());
        userAccess.setUserId(bean.getUserId());
        userAccess.setName(bean.getName());
        userAccess.setCode(bean.getCode());
        userAccess.setStatus(bean.getStatus());
        userAccess.setUploadStatus(bean.getUploadStatus());
        userAccess.setCreateBy(bean.getCreateBy());
        userAccess.setCreateDate(bean.getCreateDate());
        userAccess.setUpdateBy(bean.getUpdateBy());
        userAccess.setUpdateDate(bean.getUpdateDate());

        return userAccess;
    }
	
	public List<UserAccessBean> getUserAccessesForUpload(int limit) {

		QueryBuilder<UserAccess> qb = userAccessDao.queryBuilder();
		qb.where(UserAccessDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(UserAccessDao.Properties.Name);
		
		Query<UserAccess> q = qb.build();
		
		ArrayList<UserAccessBean> userAccessBeans = new ArrayList<UserAccessBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (UserAccess userAccess : q.list().subList(0, maxIndex)) {
			
			userAccessBeans.add(BeanUtil.getBean(userAccess));
		}
		
		return userAccessBeans;
	}
	
	public void updateUserAccesses(List<UserAccessBean> userAccesss) {
		
		DbUtil.getDb().beginTransaction();
		
		List<UserAccessBean> shiftedBeans = new ArrayList<UserAccessBean>();
		
		for (UserAccessBean bean : userAccesss) {
			
			boolean isAdd = false;
			
			UserAccess userAccess = userAccessDao.load(bean.getRemote_id());
			
			if (userAccess == null) {
				userAccess = new UserAccess();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(userAccess.getRefId(), bean.getRef_id())) {
				UserAccessBean shiftedBean = BeanUtil.getBean(userAccess);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(userAccess, bean);
			
			if (isAdd) {
				userAccessDao.insert(userAccess);
			} else {
				userAccessDao.update(userAccess);
			}
		}
				
		for (UserAccessBean bean : shiftedBeans) {
			
			UserAccess userAccess = new UserAccess();
			BeanUtil.updateBean(userAccess, bean);
			
			userAccess.setId(null);
			userAccess.setUploadStatus(Constant.STATUS_YES);
			
			userAccessDao.insert(userAccess);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	public void updateUserAccessStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			UserAccess userAccess = userAccessDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				userAccess.setUploadStatus(Constant.STATUS_NO);
				userAccessDao.update(userAccess);
			}
		} 
	}
	
	private List<MerchantAccess> getMerchantAccessList(Long merchantId) {

		QueryBuilder<MerchantAccess> qb = merchantAccessDao.queryBuilder();
		qb.where(MerchantAccessDao.Properties.MerchantId.eq(merchantId),
				MerchantAccessDao.Properties.Status.eq(Constant.STATUS_YES))
				.orderAsc(MerchantAccessDao.Properties.Name);
		
		Query<MerchantAccess> q = qb.build();
		
		return q.list();
	}
	
	public boolean hasUpdate() {
		
		return getUserAccessesForUploadCount() > 0;
	}
	
	public long getUserAccessesForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM user_access WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Transactions;
import com.android.pos.dao.TransactionsDao;
import com.android.pos.dao.User;
import com.android.pos.dao.UserAccess;
import com.android.pos.dao.UserAccessDao;
import com.android.pos.dao.UserDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.model.UserBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class UserDaoService {
	
	private UserDao userDao = DbUtil.getSession().getUserDao();
	private UserAccessDao userAccessDao = DbUtil.getSession().getUserAccessDao();
	private TransactionsDao transactionDao = DbUtil.getSession().getTransactionsDao();
	
	public void addUser(User user) {
		
		if (CommonUtil.isEmpty(user.getRefId())) {
			user.setRefId(CommonUtil.generateRefId());
		}
		
		Long id = userDao.insert(user);
		user.setId(id);
	}
	
	public void updateUser(User user) {
		
		userDao.update(user);
	}
	
	public void deleteUser(User user) {

		User entity = userDao.load(user.getId());
		
		if (entity != null) {
			
			entity.setStatus(Constant.STATUS_DELETED);
			entity.setUploadStatus(Constant.STATUS_YES);
			userDao.update(entity);
		}
	}
	
	public User getUser(Long id) {
		
		User user = userDao.load(id);
		
		if (user != null) {
			userDao.detach(user);
		}
		
		return user;
	}
	
	/*public User validateUser(String loginId, String password) {
		
		QueryBuilder<User> qb = userDao.queryBuilder();
		
		qb.where(UserDao.Properties.UserId.eq(loginId), 
				UserDao.Properties.Password.eq(password));

		Query<User> q = qb.build();
		User user = q.unique();
		
		return user;
	}*/
	
	public User validateUser(String loginId, String password) {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String status = Constant.STATUS_ACTIVE;
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM user "
				+ " WHERE LOWER(user_id) = ? AND LOWER(password) = ? AND status = ? ",
				new String[] { loginId.toLowerCase(CommonUtil.getLocale()).trim(), password.toLowerCase(CommonUtil.getLocale()).trim(), status });
		
		User user = null;
		
		if (cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			user = getUser(id);
		}
		
		cursor.close();

		return user;
	}
	
	public List<User> getUsers(String query, int lastIndex) {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM user "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<User> list = new ArrayList<User>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			User item = getUser(id);
			list.add(item);
		}
		
		cursor.close();

		return list;
	}
	
	public List<UserBean> getUsersForUpload(int limit) {

		QueryBuilder<User> qb = userDao.queryBuilder();
		qb.where(UserDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(UserDao.Properties.Name);
		
		Query<User> q = qb.build();
		
		ArrayList<UserBean> userBeans = new ArrayList<UserBean>();

		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (User user : q.list().subList(0, maxIndex)) {
			
			userBeans.add(BeanUtil.getBean(user));
		}
		
		return userBeans;
	}
	
	public void updateUsers(List<UserBean> users) {
		
		DbUtil.getDb().beginTransaction();
		
		List<UserBean> shiftedBeans = new ArrayList<UserBean>();
		
		for (UserBean bean : users) {
			
			boolean isAdd = false;
			
			User user = userDao.load(bean.getRemote_id());
			
			if (user == null) {
				user = new User();
				isAdd = true;
			
			} else if (!CommonUtil.compareString(user.getRefId(), bean.getRef_id())) {
				UserBean shiftedBean = BeanUtil.getBean(user);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(user, bean);
			
			if (isAdd) {
				userDao.insert(user);
			} else {
				userDao.update(user);
			}
		}
				
		for (UserBean bean : shiftedBeans) {
			
			User user = new User();
			BeanUtil.updateBean(user, bean);
			
			Long oldId = user.getId();
			
			user.setId(null);
			user.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = userDao.insert(user);
			
			updateUserAccessFk(oldId, newId);
			updateTransactionsFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateUserAccessFk(Long oldId, Long newId) {
		
		User user = userDao.load(oldId);
		
		for (UserAccess ua : user.getUserAccessList()) {
			ua.setUserId(newId);
			ua.setUploadStatus(Constant.STATUS_YES);
			userAccessDao.update(ua);
		}
	}
		
	private void updateTransactionsFk(Long oldId, Long newId) {
		
		User user = userDao.load(oldId);
		
		for (Transactions transactions : user.getTransactionsList()) {
			
			transactions.setCashierId(newId);
			transactions.setUploadStatus(Constant.STATUS_YES);
			transactionDao.update(transactions);
		}
	}
	
	public void updateUserStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			User user = userDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				user.setUploadStatus(Constant.STATUS_NO);
				userDao.update(user);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getUsersForUploadCount() > 0;
	}
	
	public long getUsersForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM user WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

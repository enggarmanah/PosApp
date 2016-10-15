package com.tokoku.pos.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.pos.dao.Employee;
import com.android.pos.dao.EmployeeDao;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.TransactionItemDao;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.EmployeeBean;
import com.tokoku.pos.model.SyncStatusBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.DbUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class EmployeeDaoService {
	
	private EmployeeDao employeeDao = DbUtil.getSession().getEmployeeDao();
	private TransactionItemDao transactionItemDao = DbUtil.getSession().getTransactionItemDao();
	
	public void addEmployee(Employee employee) {
		
		if (CommonUtil.isEmpty(employee.getRefId())) {
			employee.setRefId(CommonUtil.generateRefId());
		}
		
		employeeDao.insert(employee);
	}
	
	public void updateEmployee(Employee employee) {
		
		employeeDao.update(employee);
	}
	
	public void deleteEmployee(Employee employee) {

		Employee entity = employeeDao.load(employee.getId());
		entity.setStatus(Constant.STATUS_DELETED);
		entity.setUploadStatus(Constant.STATUS_YES);
		employeeDao.update(entity);
	}
	
	public Employee getEmployee(Long id) {
		
		Employee employee = employeeDao.load(id);
		
		if (employee != null) {
			employeeDao.detach(employee);
		}
		
		return employee;
	}
	
	public List<Employee> getEmployees(String query, int lastIndex) {

		SQLiteDatabase db = DbUtil.getDb();
		
		String queryStr = "%" + CommonUtil.getNvlString(query) + "%";
		String status = Constant.STATUS_DELETED;
		String limit = Constant.QUERY_LIMIT;
		String lastIdx = String.valueOf(lastIndex);
		
		Cursor cursor = db.rawQuery("SELECT _id "
				+ " FROM employee "
				+ " WHERE name like ? AND status <> ? "
				+ " ORDER BY name LIMIT ? OFFSET ? ",
				new String[] { queryStr, status, limit, lastIdx});
		
		List<Employee> list = new ArrayList<Employee>();
		
		while(cursor.moveToNext()) {
			
			Long id = cursor.getLong(0);
			Employee item = getEmployee(id);
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
	
	public List<EmployeeBean> getEmployeesForUpload(int limit) {

		QueryBuilder<Employee> qb = employeeDao.queryBuilder();
		qb.where(EmployeeDao.Properties.UploadStatus.eq(Constant.STATUS_YES)).orderAsc(EmployeeDao.Properties.Name);
		
		Query<Employee> q = qb.build();
		
		ArrayList<EmployeeBean> employeeBeans = new ArrayList<EmployeeBean>();
		
		int maxIndex = CommonUtil.getMaxIndex(q.list().size(), limit);
		
		for (Employee prdGroup : q.list().subList(0, maxIndex)) {
			
			employeeBeans.add(BeanUtil.getBean(prdGroup));
		}
		
		return employeeBeans;
	}
	
	public void updateEmployees(List<EmployeeBean> employees) {
		
		DbUtil.getDb().beginTransaction();
		
		List<EmployeeBean> shiftedBeans = new ArrayList<EmployeeBean>();
		
		for (EmployeeBean bean : employees) {
			
			boolean isAdd = false;
			
			Employee employee = employeeDao.load(bean.getRemote_id());
			
			if (employee == null) {
				employee = new Employee();
				isAdd = true;
				
			} else if (!CommonUtil.compareString(employee.getRefId(), bean.getRef_id())) {
				EmployeeBean shiftedBean = BeanUtil.getBean(employee);
				shiftedBeans.add(shiftedBean);
			}
			
			BeanUtil.updateBean(employee, bean);
			
			if (isAdd) {
				employeeDao.insert(employee);
			} else {
				employeeDao.update(employee);
			}
		}
		
		for (EmployeeBean bean : shiftedBeans) {
			
			Employee employee = new Employee();
			BeanUtil.updateBean(employee, bean);
			
			Long oldId = employee.getId();
			
			employee.setId(null);
			employee.setUploadStatus(Constant.STATUS_YES);
			
			Long newId = employeeDao.insert(employee);
			
			updateTransactionItemFk(oldId, newId);
		}
		
		DbUtil.getDb().setTransactionSuccessful();
		DbUtil.getDb().endTransaction();
	}
	
	private void updateTransactionItemFk(Long oldId, Long newId) {
		
		Employee e = employeeDao.load(oldId);
		
		for (TransactionItem ti : e.getTransactionItemList()) {
			
			ti.setEmployeeId(newId);
			ti.setUploadStatus(Constant.STATUS_YES);
			transactionItemDao.update(ti);
		}
	}
	
	public void updateEmployeeStatus(List<SyncStatusBean> syncStatusBeans) {
		
		for (SyncStatusBean bean : syncStatusBeans) {
			
			Employee employee = employeeDao.load(bean.getRemoteId());
			
			if (SyncStatusBean.SUCCESS.equals(bean.getStatus())) {
				employee.setUploadStatus(Constant.STATUS_NO);
				employeeDao.update(employee);
			}
		} 
	}
	
	public boolean hasUpdate() {
	
		return getEmployeesForUploadCount() > 0;
	}
	
	public long getEmployeesForUploadCount() {
		
		SQLiteDatabase db = DbUtil.getDb();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(_id) FROM employee WHERE upload_status = 'Y'", null);
			
		cursor.moveToFirst();
			
		Long count = cursor.getLong(0);
		
		cursor.close();
		
		return count;
	}
}

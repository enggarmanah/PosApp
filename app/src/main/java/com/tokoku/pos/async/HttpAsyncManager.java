package com.tokoku.pos.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;

import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tokoku.pos.Config;
import com.tokoku.pos.Constant;
import com.tokoku.pos.Installation;
import com.tokoku.pos.auth.ForgotPasswordListener;
import com.tokoku.pos.auth.LoginListener;
import com.tokoku.pos.auth.RegistrationListener;
import com.tokoku.pos.auth.ResendActivationCodeListener;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CashflowDaoService;
import com.tokoku.pos.dao.CustomerDaoService;
import com.tokoku.pos.dao.DiscountDaoService;
import com.tokoku.pos.dao.EmployeeDaoService;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.dao.MerchantAccessDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.dao.OrderItemDaoService;
import com.tokoku.pos.dao.OrdersDaoService;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.dao.TransactionItemDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.dao.UserAccessDaoService;
import com.tokoku.pos.dao.UserDaoService;
import com.tokoku.pos.model.MerchantBean;
import com.tokoku.pos.model.SyncRequestBean;
import com.tokoku.pos.model.SyncResponseBean;
import com.tokoku.pos.model.UserBean;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpAsyncManager {
	
	private static HttpAsyncTask task;
	private static HttpAsyncListener mAsyncListener;

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static OkHttpClient mHttpClient = new OkHttpClient().newBuilder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS).build();

	private static Call mCall = null;
	
	private Context mContext;
	private MerchantBean mMerchant;
	private String mLoginId;
	private String mPassword;

	private ProductGroupDaoService mProductGroupDaoService;
	private DiscountDaoService mDiscountDaoService;
	private MerchantDaoService mMerchantDaoService;
	private EmployeeDaoService mEmployeeDaoService;
	private CustomerDaoService mCustomerDaoService;
	private ProductDaoService mProductDaoService;
	private UserDaoService mUserDaoService;
	private UserAccessDaoService mUserAccessDaoService;
	private TransactionsDaoService mTransactionsDaoService;
	private TransactionItemDaoService mTransactionItemDaoService;
	private SupplierDaoService mSupplierDaoService;
	private BillsDaoService mBillDaoService;
	private CashflowDaoService mCashflowDaoService;
	private InventoryDaoService mInventoryDaoService;
	private MerchantAccessDaoService mMerchantAccessDaoService;
	private OrdersDaoService mOrdersDaoService;
	private OrderItemDaoService mOrderItemDaoService;

	private int mTaskIndex = 0;
	
	private List<String> mTasks;
	private List<String> mGetTasks;
	private List<String> mUpdateTasks;
	
	private Map<String, String> taskMessage;
	
	private static ObjectMapper mapper;
	
	private Long startTime;
	private Date mSyncDate;
	private String mSyncKey;
	private int mSyncRecordLimit;
	private int mAppVersion;
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public HttpAsyncManager(Context context) {

		this.mContext = context;
		
		mAsyncListener = (HttpAsyncListener) context;
		
		try {
			PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			mAppVersion = pInfo.versionCode;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		mProductGroupDaoService = new ProductGroupDaoService();
		mDiscountDaoService = new DiscountDaoService();
		mMerchantDaoService = new MerchantDaoService();
		mEmployeeDaoService = new EmployeeDaoService();
		mCustomerDaoService = new CustomerDaoService();
		mProductDaoService = new ProductDaoService();
		mUserDaoService = new UserDaoService();
		mUserAccessDaoService = new UserAccessDaoService();
		mTransactionsDaoService = new TransactionsDaoService();
		mTransactionItemDaoService = new TransactionItemDaoService();
		mSupplierDaoService = new SupplierDaoService();
		mBillDaoService = new BillsDaoService();
		mCashflowDaoService = new CashflowDaoService();
		mInventoryDaoService = new InventoryDaoService();
		mMerchantAccessDaoService = new MerchantAccessDaoService();
		mOrdersDaoService = new OrdersDaoService();
		mOrderItemDaoService = new OrderItemDaoService();
		
		mTasks = new ArrayList<String>();
		mGetTasks = new ArrayList<String>();
		mUpdateTasks = new ArrayList<String>();
		
		taskMessage = new HashMap<String, String>();
		
		taskMessage.put(Constant.TASK_REGISTER_MERCHANT, context.getString(R.string.task_register_merchant));
		taskMessage.put(Constant.TASK_RESEND_ACTIVATION_CODE, context.getString(R.string.task_resend_activation_code));
		taskMessage.put(Constant.TASK_GET_MERCHANT_BY_LOGIN_ID, context.getString(R.string.task_get_merchant_by_login_id));
		taskMessage.put(Constant.TASK_RESET_PASSWORD, context.getString(R.string.task_reset_password));
		taskMessage.put(Constant.TASK_VALIDATE_MERCHANT, context.getString(R.string.task_validate_merchant));
		taskMessage.put(Constant.TASK_VALIDATE_USER, context.getString(R.string.task_validate_user));
		taskMessage.put(Constant.TASK_ROOT_GET_MERCHANT, context.getString(R.string.task_root_get_merchant));
		taskMessage.put(Constant.TASK_ROOT_GET_MERCHANT_ACCESS, context.getString(R.string.task_root_get_merchant_access));
		taskMessage.put(Constant.TASK_ACTIVATE_MERCHANT, context.getString(R.string.task_activate_merchant));
		taskMessage.put(Constant.TASK_UPDATE_ORDER, context.getString(R.string.task_submit_orders));
		taskMessage.put(Constant.TASK_UPDATE_ORDER_ITEM, context.getString(R.string.task_submit_order_items));
		taskMessage.put(Constant.TASK_GET_ORDER, context.getString(R.string.task_get_orders));
		taskMessage.put(Constant.TASK_GET_ORDER_ITEM, context.getString(R.string.task_get_order_item));
		taskMessage.put(Constant.TASK_GET_PRODUCT_GROUP, context.getString(R.string.task_get_product_group));
		taskMessage.put(Constant.TASK_UPDATE_PRODUCT_GROUP, context.getString(R.string.task_update_product_group));
		taskMessage.put(Constant.TASK_GET_DISCOUNT, context.getString(R.string.task_get_discount));
		taskMessage.put(Constant.TASK_UPDATE_DISCOUNT, context.getString(R.string.task_update_discount));
		taskMessage.put(Constant.TASK_GET_EMPLOYEE, context.getString(R.string.task_get_employee));
		taskMessage.put(Constant.TASK_UPDATE_EMPLOYEE, context.getString(R.string.task_update_employee));
		taskMessage.put(Constant.TASK_GET_CUSTOMER, context.getString(R.string.task_get_customer));
		taskMessage.put(Constant.TASK_UPDATE_CUSTOMER, context.getString(R.string.task_update_customer));
		taskMessage.put(Constant.TASK_GET_PRODUCT, context.getString(R.string.task_get_product));
		taskMessage.put(Constant.TASK_UPDATE_PRODUCT, context.getString(R.string.task_update_product));
		taskMessage.put(Constant.TASK_GET_USER, context.getString(R.string.task_get_user));
		taskMessage.put(Constant.TASK_UPDATE_USER, context.getString(R.string.task_update_user));
		taskMessage.put(Constant.TASK_GET_USER_ACCESS, context.getString(R.string.task_get_user_access));
		taskMessage.put(Constant.TASK_UPDATE_USER_ACCESS, context.getString(R.string.task_update_user_access));
		taskMessage.put(Constant.TASK_GET_TRANSACTION, context.getString(R.string.task_get_transactions));
		taskMessage.put(Constant.TASK_UPDATE_TRANSACTION, context.getString(R.string.task_update_transactions));
		taskMessage.put(Constant.TASK_GET_TRANSACTION_ITEM, context.getString(R.string.task_get_transaction_item));
		taskMessage.put(Constant.TASK_UPDATE_TRANSACTION_ITEM, context.getString(R.string.task_update_transaction_item));
		taskMessage.put(Constant.TASK_GET_SUPPLIER, context.getString(R.string.task_get_supplier));
		taskMessage.put(Constant.TASK_UPDATE_SUPPLIER, context.getString(R.string.task_update_supplier));
		taskMessage.put(Constant.TASK_GET_BILL, context.getString(R.string.task_get_bill));
		taskMessage.put(Constant.TASK_UPDATE_BILL, context.getString(R.string.task_update_bill));
		taskMessage.put(Constant.TASK_GET_CASHFLOW, context.getString(R.string.task_get_cashflow));
		taskMessage.put(Constant.TASK_UPDATE_CASHFLOW, context.getString(R.string.task_update_cashflow));
		taskMessage.put(Constant.TASK_GET_INVENTORY, context.getString(R.string.task_get_inventory));
		taskMessage.put(Constant.TASK_UPDATE_INVENTORY, context.getString(R.string.task_update_inventory));
		taskMessage.put(Constant.TASK_GET_MERCHANT, context.getString(R.string.task_get_merchant));
		taskMessage.put(Constant.TASK_UPDATE_MERCHANT, context.getString(R.string.task_update_merchant));
		taskMessage.put(Constant.TASK_GET_MERCHANT_ACCESS, context.getString(R.string.task_get_merchant_access));
		taskMessage.put(Constant.TASK_UPDATE_MERCHANT_ACCESS, context.getString(R.string.task_update_merchant_access));
		taskMessage.put(Constant.TASK_GET_LAST_SYNC, context.getString(R.string.task_get_last_sync));
		taskMessage.put(Constant.TASK_UPDATE_LAST_SYNC, context.getString(R.string.task_update_last_sync));
	}
	
	private String getMessage(String task) {
		
		return taskMessage.get(task);
	} 
	
	private void executeNextTask() {
		
		if (mTaskIndex >= mTasks.size()) {
			return;
		}
		
		String taskName = mTasks.get(mTaskIndex);
		String message = getMessage(mTasks.get(mTaskIndex));
		
		if (Constant.TASK_COMPLETED.equals(taskName)) {
			syncCompleted();
			return;
		}
		
		int progress = mTaskIndex * 100 / mTasks.size();
		
		mAsyncListener.setSyncProgress(progress);
		mAsyncListener.setSyncMessage(message);
		
		task = new HttpAsyncTask();
		task.execute(taskName);
		
		if (mTaskIndex == 0) {
				
			Handler handler = new Handler();
			handler.postDelayed(getTimeOutHandler(task), Constant.TIMEOUT);
		}
		
		mTaskIndex++;  	
	}
	
	private void getNextRecord(long index, long total) {
		
		mTaskIndex--;
		
		String taskName = mTasks.get(mTaskIndex);
		String message = getMessage(mTasks.get(mTaskIndex));
		
		int progress = mTaskIndex * 100 / mTasks.size() + (int) (index / (float) total * 100 / mTasks.size());
		
		mAsyncListener.setSyncProgress(progress);
		mAsyncListener.setSyncMessage(message + " " + (int) (index / (float) total * 100) + " %");
		
		task = new HttpAsyncTask();
		task.execute(taskName, String.valueOf(index));
		
		mTaskIndex++; 
	}
	
	public Runnable getTimeOutHandler(final HttpAsyncTask task) {
		
		return new Runnable() {
			
			@Override
			public void run() {
				
				if (task.getStatus() == AsyncTask.Status.RUNNING) {
					task.cancel(true);
					abortHttpRequest();
					mAsyncListener.onTimeOut();
				}
			}
		};
	}
	
	public static boolean stopSyncTask() {
		
		boolean isCancelled = false;
		
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
			task.cancel(true);
			abortHttpRequest();
			isCancelled = true;
		} else {
			isCancelled = true;
		}
		
		return isCancelled;
	}
	
	public void validateMerchant(String loginId, String password) {
		
		startTime = new Date().getTime();
		
		mLoginId = loginId.toUpperCase(CommonUtil.getLocale());
		mPassword = password.toUpperCase(CommonUtil.getLocale());
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_VALIDATE_MERCHANT);
		
		executeNextTask();
	}
	
	public void getMerchantByLoginId(String loginId) {
		
		startTime = new Date().getTime();
		
		mLoginId = loginId;
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_MERCHANT_BY_LOGIN_ID);
		
		executeNextTask();
	}
	
	public void resetPassword(String loginId, String password) {
		
		startTime = new Date().getTime();
		
		mLoginId = loginId;
		mPassword = password;
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_RESET_PASSWORD);
		
		executeNextTask();
	}
	
	public void registerMerchant(MerchantBean merchant) {
		
		startTime = new Date().getTime();
		
		mMerchant = merchant;
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_REGISTER_MERCHANT);
		
		executeNextTask();
	}
	
	public void resendActivationCode(MerchantBean merchant) {
		
		startTime = new Date().getTime();
		
		mMerchant = merchant;
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_RESEND_ACTIVATION_CODE);
		
		executeNextTask();
	}
	
	public void validateUser(String userId, String password) {
		
		startTime = new Date().getTime();
		
		mLoginId = userId;
		mPassword = password;
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_VALIDATE_USER);
		
		executeNextTask();
	}
	
	public void syncMerchants() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_ROOT_GET_MERCHANT);
		// conflicting ID with other merchants
		//mGetTasks.add(Constant.TASK_ROOT_GET_MERCHANT_ACCESS);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT);
		// conflicting ID with other merchants
		//mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT_ACCESS);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncMerchant() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_EMPLOYEE);
		mGetTasks.add(Constant.TASK_GET_USER);
		mGetTasks.add(Constant.TASK_GET_USER_ACCESS);
		mGetTasks.add(Constant.TASK_GET_MERCHANT_ACCESS);
		mGetTasks.add(Constant.TASK_GET_MERCHANT);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_EMPLOYEE);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER_ACCESS);
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT);
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT_ACCESS);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void activateMerchant(MerchantBean merchant) {
		
		mMerchant = merchant;
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		
		mTasks.add(Constant.TASK_ACTIVATE_MERCHANT);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncUsers() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_USER);
		mGetTasks.add(Constant.TASK_GET_USER_ACCESS);
		mGetTasks.add(Constant.TASK_GET_MERCHANT_ACCESS);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_USER);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER_ACCESS);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncAll() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_MERCHANT);
		mGetTasks.add(Constant.TASK_GET_MERCHANT_ACCESS);
		mGetTasks.add(Constant.TASK_GET_USER);
		mGetTasks.add(Constant.TASK_GET_USER_ACCESS);
		mGetTasks.add(Constant.TASK_GET_PRODUCT_GROUP);
		mGetTasks.add(Constant.TASK_GET_PRODUCT);
		mGetTasks.add(Constant.TASK_GET_DISCOUNT);
		mGetTasks.add(Constant.TASK_GET_EMPLOYEE);
		mGetTasks.add(Constant.TASK_GET_CUSTOMER);
		mGetTasks.add(Constant.TASK_GET_SUPPLIER);
		mGetTasks.add(Constant.TASK_GET_TRANSACTION);
		mGetTasks.add(Constant.TASK_GET_TRANSACTION_ITEM);
		mGetTasks.add(Constant.TASK_GET_BILL);
		mGetTasks.add(Constant.TASK_GET_CASHFLOW);
		mGetTasks.add(Constant.TASK_GET_INVENTORY);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT);
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT_ACCESS);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER_ACCESS);
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT_GROUP);
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT);
		mUpdateTasks.add(Constant.TASK_UPDATE_DISCOUNT);
		mUpdateTasks.add(Constant.TASK_UPDATE_EMPLOYEE);
		mUpdateTasks.add(Constant.TASK_UPDATE_CUSTOMER);
		mUpdateTasks.add(Constant.TASK_UPDATE_SUPPLIER);
		mUpdateTasks.add(Constant.TASK_UPDATE_TRANSACTION);
		mUpdateTasks.add(Constant.TASK_UPDATE_TRANSACTION_ITEM);
		mUpdateTasks.add(Constant.TASK_UPDATE_BILL);
		mUpdateTasks.add(Constant.TASK_UPDATE_CASHFLOW);
		mUpdateTasks.add(Constant.TASK_UPDATE_INVENTORY);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncPartial() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_MERCHANT);
		mGetTasks.add(Constant.TASK_GET_MERCHANT_ACCESS);
		mGetTasks.add(Constant.TASK_GET_USER);
		mGetTasks.add(Constant.TASK_GET_USER_ACCESS);
		
		mGetTasks.add(Constant.TASK_GET_PRODUCT_GROUP);
		mGetTasks.add(Constant.TASK_GET_PRODUCT);
		mGetTasks.add(Constant.TASK_GET_DISCOUNT);
		mGetTasks.add(Constant.TASK_GET_EMPLOYEE);
		mGetTasks.add(Constant.TASK_GET_CUSTOMER);
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_DATA_MANAGEMENT) || 
			UserUtil.isUserHasAccess(Constant.ACCESS_BILLS) || UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW) ||
			UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mGetTasks.add(Constant.TASK_GET_SUPPLIER);
		}
		
		if (UserUtil.isCashier() || UserUtil.isEmployee() || UserUtil.isUserHasReportsAccess() ||
			UserUtil.isUserHasAccess(Constant.ACCESS_CASHIER) || UserUtil.isUserHasAccess(Constant.ACCESS_ORDER)) {
			
			mGetTasks.add(Constant.TASK_GET_TRANSACTION);
			mGetTasks.add(Constant.TASK_GET_TRANSACTION_ITEM);
		}
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_BILLS) || 
			UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW) || UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mGetTasks.add(Constant.TASK_GET_BILL);
		}
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW)) {
			
			mGetTasks.add(Constant.TASK_GET_CASHFLOW);
		}
		
		if (UserUtil.isCashier() || UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mGetTasks.add(Constant.TASK_GET_INVENTORY);
		}
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT);
		mUpdateTasks.add(Constant.TASK_UPDATE_MERCHANT_ACCESS);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER);
		mUpdateTasks.add(Constant.TASK_UPDATE_USER_ACCESS);
		
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT_GROUP);
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT);
		mUpdateTasks.add(Constant.TASK_UPDATE_DISCOUNT);
		mUpdateTasks.add(Constant.TASK_UPDATE_EMPLOYEE);
		mUpdateTasks.add(Constant.TASK_UPDATE_CUSTOMER);
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_DATA_MANAGEMENT) || 
			UserUtil.isUserHasAccess(Constant.ACCESS_BILLS) || UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW) ||
			UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mUpdateTasks.add(Constant.TASK_UPDATE_SUPPLIER);
		}
		
		if (UserUtil.isCashier() || UserUtil.isEmployee() || UserUtil.isUserHasReportsAccess() ||
			UserUtil.isUserHasAccess(Constant.ACCESS_CASHIER) || UserUtil.isUserHasAccess(Constant.ACCESS_ORDER)) {
			
			mUpdateTasks.add(Constant.TASK_UPDATE_TRANSACTION);
			mUpdateTasks.add(Constant.TASK_UPDATE_TRANSACTION_ITEM);
		}
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_BILLS) || 
			UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW) || UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mUpdateTasks.add(Constant.TASK_UPDATE_BILL);
		}
		
		if (UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_CASHFLOW)) {
			
			mUpdateTasks.add(Constant.TASK_UPDATE_CASHFLOW);
		}
		
		if (UserUtil.isCashier() || UserUtil.isUserHasReportsAccess() || UserUtil.isUserHasAccess(Constant.ACCESS_INVENTORY)) {
			
			mUpdateTasks.add(Constant.TASK_UPDATE_INVENTORY);
		}
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncOrders() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_ORDER);
		mGetTasks.add(Constant.TASK_GET_ORDER_ITEM);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_ORDER);
		mUpdateTasks.add(Constant.TASK_UPDATE_ORDER_ITEM);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}
	
	public void syncProducts() {
		
		startTime = new Date().getTime();
		
		mTaskIndex = 0;
		
		mTasks.clear();
		mTasks.add(Constant.TASK_GET_LAST_SYNC);
		
		mGetTasks.clear();
		mGetTasks.add(Constant.TASK_GET_PRODUCT_GROUP);
		mGetTasks.add(Constant.TASK_GET_PRODUCT);
		
		mUpdateTasks.clear();
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT_GROUP);
		mUpdateTasks.add(Constant.TASK_UPDATE_PRODUCT);
		
		mTasks.addAll(getTaskWithUpdate(mUpdateTasks));
		
		mTasks.add(Constant.TASK_UPDATE_LAST_SYNC);
		mTasks.add(Constant.TASK_COMPLETED);
		
		executeNextTask();
	}

	private void syncCompleted() {
		
		System.out.println("Processing Time : " + (new Date().getTime() - startTime));
		
		mAsyncListener.setSyncProgress(100);
	}
	
	private List<String> getTaskWithUpdate(List<String> tasks) {
		
		List<String> taskWithUpdates = new ArrayList<String>();
		
		for (String task : tasks) {
			
			if (Constant.TASK_UPDATE_PRODUCT_GROUP.equals(task) &&  mProductGroupDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_PRODUCT_GROUP);
			
			} else if (Constant.TASK_UPDATE_PRODUCT.equals(task) &&  mProductDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_PRODUCT);
			
			} else if (Constant.TASK_UPDATE_DISCOUNT.equals(task) &&  mDiscountDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_DISCOUNT);
			
			} else if (Constant.TASK_UPDATE_EMPLOYEE.equals(task) &&  mEmployeeDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_EMPLOYEE);
			
			} else if (Constant.TASK_UPDATE_CUSTOMER.equals(task) &&  mCustomerDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_CUSTOMER);
			
			} else if (Constant.TASK_UPDATE_USER.equals(task) &&  mUserDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_USER);
			
			} else if (Constant.TASK_UPDATE_USER_ACCESS.equals(task) &&  mUserAccessDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_USER_ACCESS);
			
			} else if (Constant.TASK_UPDATE_TRANSACTION.equals(task) &&  mTransactionsDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_TRANSACTION);
			
			} else if (Constant.TASK_UPDATE_TRANSACTION_ITEM.equals(task) &&  mTransactionItemDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_TRANSACTION_ITEM);
			
			} else if (Constant.TASK_UPDATE_ORDER.equals(task) &&  mOrdersDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_ORDER);
			
			} else if (Constant.TASK_UPDATE_ORDER_ITEM.equals(task) &&  mOrderItemDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_ORDER_ITEM);
			
			} else if (Constant.TASK_UPDATE_SUPPLIER.equals(task) &&  mSupplierDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_SUPPLIER);
			
			} else if (Constant.TASK_UPDATE_BILL.equals(task) &&  mBillDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_BILL);
			
			} else if (Constant.TASK_UPDATE_CASHFLOW.equals(task) &&  mCashflowDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_CASHFLOW);
			
			} else if (Constant.TASK_UPDATE_INVENTORY.equals(task) &&  mInventoryDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_INVENTORY);
			
			} else if (Constant.TASK_UPDATE_MERCHANT.equals(task) &&  mMerchantDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_MERCHANT);
			
			} else if (Constant.TASK_UPDATE_MERCHANT_ACCESS.equals(task) &&  mMerchantAccessDaoService.hasUpdate()) {
				taskWithUpdates.add(Constant.TASK_UPDATE_MERCHANT_ACCESS);
			
			} 
		}
		
		return taskWithUpdates;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {

		String task;
		int index = 0;

		@Override
		protected String doInBackground(String... params) {

			task = params[0];
			
			if (params.length >= 2) {
				index = Integer.valueOf(params[1]);
			}
			
			Long merchantId = null;
			
			if (UserUtil.isRoot()) {
				merchantId = Long.valueOf(-1);
			} else {
				merchantId = MerchantUtil.getMerchantId();
			}
			
			String url = Constant.EMPTY_STRING;
			
			SyncRequestBean request = new SyncRequestBean();
			
			request.setMerchant_id(merchantId);
			request.setUuid(Installation.getInstallationId(mContext));
			request.setSync_key(mSyncKey);
			request.setSync_date(mSyncDate);
			request.setIndex(index);
			request.setAppVersion(mAppVersion);
			
			request.setCert_dn(CommonUtil.getCertDN(mContext));
			
			if (Constant.TASK_REGISTER_MERCHANT.equals(params[0])) {
				
				url = Config.SERVER_URL + "/merchantRegisterJsonServlet";

				request.setMerchant(mMerchant);

			} else if (Constant.TASK_RESEND_ACTIVATION_CODE.equals(params[0])) {
				
				url = Config.SERVER_URL + "/merchantResendActivationCodeJsonServlet";

				request.setMerchant(mMerchant);

			} else if (Constant.TASK_VALIDATE_MERCHANT.equals(params[0])) {
				
				url = Config.SERVER_URL + "/merchantValidateJsonServlet";

				MerchantBean merchant = new MerchantBean();
				
				merchant.setLogin_id(mLoginId);
				merchant.setPassword(mPassword);
				
				request.setMerchant(merchant);

			} else if (Constant.TASK_ACTIVATE_MERCHANT.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantActivateJsonServlet";

				request.setMerchant(mMerchant);

			} else if (Constant.TASK_GET_MERCHANT_BY_LOGIN_ID.equals(params[0])) {
				
				url = Config.SERVER_URL + "/merchantGetByLoginIdJsonServlet";

				MerchantBean merchant = new MerchantBean();
				
				merchant.setLogin_id(mLoginId);
				
				request.setMerchant(merchant);

			} else if (Constant.TASK_RESET_PASSWORD.equals(params[0])) {
				
				url = Config.SERVER_URL + "/merchantResetPasswordJsonServlet";

				MerchantBean merchant = new MerchantBean();
				
				merchant.setLogin_id(mLoginId);
				merchant.setPassword(mPassword);
				
				request.setMerchant(merchant);

			} else if (Constant.TASK_VALIDATE_USER.equals(params[0])) {
				
				url = Config.SERVER_URL + "/userValidateJsonServlet";

				UserBean user = new UserBean();
				
				user.setUser_id(mLoginId);
				user.setPassword(mPassword);
				
				request.setUser(user);

			} else if (Constant.TASK_ROOT_GET_MERCHANT.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantGetAllJsonServlet";

			} else if (Constant.TASK_GET_LAST_SYNC.equals(params[0])) {

				url = Config.SERVER_URL + "/getLastSyncJsonServlet";

				request.setGetRequests(mGetTasks);

			} else if (Constant.TASK_GET_ORDER.equals(params[0])) {

				url = Config.SERVER_URL + "/ordersGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_ORDER.equals(params[0])) {

				url = Config.SERVER_URL + "/ordersUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mOrdersDaoService.getOrdersForUploadCount());
				request.setOrders(mOrdersDaoService.getOrdersForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_ORDER_ITEM.equals(params[0])) {

				url = Config.SERVER_URL + "/orderItemGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_ORDER_ITEM.equals(params[0])) {

				url = Config.SERVER_URL + "/orderItemUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mOrderItemDaoService.getOrderItemsForUploadCount());
				request.setOrderItems(mOrderItemDaoService.getOrderItemsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_PRODUCT_GROUP.equals(params[0])) {

				url = Config.SERVER_URL + "/productGroupGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_PRODUCT_GROUP.equals(params[0])) {

				url = Config.SERVER_URL + "/productGroupUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mProductGroupDaoService.getProductGroupsForUploadCount());
				request.setProductGroups(mProductGroupDaoService.getProductGroupsForUpload(mSyncRecordLimit));
				
			} else if (Constant.TASK_GET_DISCOUNT.equals(params[0])) {

				url = Config.SERVER_URL + "/discountGetJsonServlet";

			} else if (Constant.TASK_UPDATE_DISCOUNT.equals(params[0])) {

				url = Config.SERVER_URL + "/discountUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mDiscountDaoService.getDiscountsForUploadCount());
				request.setDiscounts(mDiscountDaoService.getDiscountsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_EMPLOYEE.equals(params[0])) {

				url = Config.SERVER_URL + "/employeeGetJsonServlet";

			} else if (Constant.TASK_UPDATE_EMPLOYEE.equals(params[0])) {

				url = Config.SERVER_URL + "/employeeUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mEmployeeDaoService.getEmployeesForUploadCount());
				request.setEmployees(mEmployeeDaoService.getEmployeesForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_CUSTOMER.equals(params[0])) {

				url = Config.SERVER_URL + "/customerGetJsonServlet";

			} else if (Constant.TASK_UPDATE_CUSTOMER.equals(params[0])) {

				url = Config.SERVER_URL + "/customerUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mCustomerDaoService.getCustomersForUploadCount());
				request.setCustomers(mCustomerDaoService.getCustomersForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_PRODUCT.equals(params[0])) {

				url = Config.SERVER_URL + "/productGetJsonServlet";

			} else if (Constant.TASK_UPDATE_PRODUCT.equals(params[0])) {

				url = Config.SERVER_URL + "/productUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mProductDaoService.getProductsForUploadCount());
				request.setProducts(mProductDaoService.getProductsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_USER.equals(params[0])) {

				url = Config.SERVER_URL + "/userGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_USER.equals(params[0])) {

				url = Config.SERVER_URL + "/userUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mUserDaoService.getUsersForUploadCount());
				request.setUsers(mUserDaoService.getUsersForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_USER_ACCESS.equals(params[0])) {

				url = Config.SERVER_URL + "/userAccessGetJsonServlet";

			} else if (Constant.TASK_UPDATE_USER_ACCESS.equals(params[0])) {

				url = Config.SERVER_URL + "/userAccessUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mUserAccessDaoService.getUserAccessesForUploadCount());
				request.setUserAccesses(mUserAccessDaoService.getUserAccessesForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_TRANSACTION.equals(params[0])) {

				url = Config.SERVER_URL + "/transactionsGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_TRANSACTION.equals(params[0])) {

				url = Config.SERVER_URL + "/transactionsUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mTransactionsDaoService.getTransactionsForUploadCount());
				request.setTransactions(mTransactionsDaoService.getTransactionsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_TRANSACTION_ITEM.equals(params[0])) {

				url = Config.SERVER_URL + "/transactionItemGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_TRANSACTION_ITEM.equals(params[0])) {

				url = Config.SERVER_URL + "/transactionItemUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mTransactionItemDaoService.getTransactionItemsForUploadCount());
				request.setTransactionItems(mTransactionItemDaoService.getTransactionItemsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_SUPPLIER.equals(params[0])) {

				url = Config.SERVER_URL + "/supplierGetJsonServlet";

			} else if (Constant.TASK_UPDATE_SUPPLIER.equals(params[0])) {

				url = Config.SERVER_URL + "/supplierUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mSupplierDaoService.getSuppliersForUploadCount());
				request.setSuppliers(mSupplierDaoService.getSuppliersForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_BILL.equals(params[0])) {

				url = Config.SERVER_URL + "/billGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_BILL.equals(params[0])) {

				url = Config.SERVER_URL + "/billUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mBillDaoService.getBillsForUploadCount());
				request.setBills(mBillDaoService.getBillsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_CASHFLOW.equals(params[0])) {

				url = Config.SERVER_URL + "/cashflowGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_CASHFLOW.equals(params[0])) {

				url = Config.SERVER_URL + "/cashflowUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mCashflowDaoService.getCashflowsForUploadCount());
				request.setCashflows(mCashflowDaoService.getCashflowForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_INVENTORY.equals(params[0])) {

				url = Config.SERVER_URL + "/inventoryGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_INVENTORY.equals(params[0])) {

				url = Config.SERVER_URL + "/inventoryUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mInventoryDaoService.getInventoriesForUploadCount());
				request.setInventories(mInventoryDaoService.getInventoriesForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_GET_MERCHANT.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_MERCHANT.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mMerchantDaoService.getMerchantsForUploadCount());
				request.setMerchants(mMerchantDaoService.getMerchantsForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_ROOT_GET_MERCHANT_ACCESS.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantAccessGetAllJsonServlet";
				
			} else if (Constant.TASK_GET_MERCHANT_ACCESS.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantAccessGetJsonServlet";
				
			} else if (Constant.TASK_UPDATE_MERCHANT_ACCESS.equals(params[0])) {

				url = Config.SERVER_URL + "/merchantAccessUpdateJsonServlet";
				
				request.setIndex(0);
				request.setResultCount(mMerchantAccessDaoService.getMerchantAccessesForUploadCount());
				request.setMerchantAccesses(mMerchantAccessDaoService.getMerchantAccessesForUpload(mSyncRecordLimit));

			} else if (Constant.TASK_UPDATE_LAST_SYNC.equals(params[0])) {

				url = Config.SERVER_URL + "/updateLastSyncJsonServlet";
				
				request.setGetRequests(mGetTasks);
			}
			
			return POST(url, request);
		}

		@Override
		protected void onPostExecute(String result) {
			
			try {
				
				SyncResponseBean resp = mapper.readValue(result, SyncResponseBean.class);
				
				if (SyncResponseBean.ERROR.equals(resp.getRespCode())) {
					
					mAsyncListener.onSyncError(getErrorDescription(resp.getRespDescription()));
					
				} else if (SyncResponseBean.SUCCESS.equals(resp.getRespCode())) {
					
					if (Constant.TASK_REGISTER_MERCHANT.equals(task)) {
						
						MerchantBean bean =  resp.getMerchant();
						
						Merchant merchant = null;
						
						if (bean != null) {
							
							merchant = new Merchant();
							BeanUtil.updateBean(merchant, bean);
						}
						
						RegistrationListener mRegistrationListener = (RegistrationListener) mContext;
						mRegistrationListener.onMerchantRegistered(merchant);
						
					} else if (Constant.TASK_RESEND_ACTIVATION_CODE.equals(task)) {
						
						MerchantBean bean =  resp.getMerchant();
						
						Merchant merchant = null;
						
						if (bean != null) {
							
							merchant = new Merchant();
							BeanUtil.updateBean(merchant, bean);
						}
						
						ResendActivationCodeListener mResendActivationCodeListener = (ResendActivationCodeListener) mContext;
						mResendActivationCodeListener.onCodeSent(merchant);
						
					} else if (Constant.TASK_VALIDATE_MERCHANT.equals(task)) {
	
						MerchantBean bean =  resp.getMerchant();
						
						Merchant merchant = null;
						
						if (bean != null) {
							
							merchant = new Merchant();
							BeanUtil.updateBean(merchant, bean);
						}
						
						LoginListener mLoginListener = (LoginListener) mContext;
						mLoginListener.onMerchantValidated(merchant);
						
					} else if (Constant.TASK_ACTIVATE_MERCHANT.equals(task)) {
	
						MerchantBean bean =  resp.getMerchant();
						// no action required, just need to update the data on server end
						
					} else if (Constant.TASK_GET_MERCHANT_BY_LOGIN_ID.equals(task)) {
	
						MerchantBean bean =  resp.getMerchant();
						
						Merchant merchant = null;
						
						if (bean != null) {
							
							merchant = new Merchant();
							BeanUtil.updateBean(merchant, bean);
						}
						
						ForgotPasswordListener mResetPasswordListener = (ForgotPasswordListener) mContext;
						mResetPasswordListener.onMerchantRetrieved(merchant);
						
					}  else if (Constant.TASK_RESET_PASSWORD.equals(task)) {
	
						MerchantBean bean =  resp.getMerchant();
						
						Merchant merchant = null;
						
						if (bean != null) {
							
							merchant = new Merchant();
							BeanUtil.updateBean(merchant, bean);
						}
						
						ForgotPasswordListener mResetPasswordListener = (ForgotPasswordListener) mContext;
						mResetPasswordListener.onMerchantRetrieved(merchant);
						
					} else if (Constant.TASK_VALIDATE_USER.equals(task)) {
	
						UserBean bean =  resp.getUser();
						
						User user = null;
						
						if (bean != null) {
							
							user = new User();
							user.setUserId(bean.getUser_id());
							user.setName(bean.getName());
						}
						
						LoginListener mLoginListener = (LoginListener) mContext;
						mLoginListener.onUserValidated(user);
						
					} else if (Constant.TASK_GET_LAST_SYNC.equals(task)) {
	
						mSyncDate = resp.getRespDate();
						mSyncKey = resp.getSync_key();
						mSyncRecordLimit = resp.getSyncRecordLimit();
						
						mTasks.remove(0);
						mTaskIndex--;
						
						List<String> taskHasUpdates = resp.getTaskHasUpdates();
						taskHasUpdates.addAll(mTasks);
						
						mTasks.clear();
						mTasks.addAll(taskHasUpdates);
	
					} else if (Constant.TASK_GET_ORDER.equals(task)) {
	
						mOrdersDaoService.updateOrders(resp.getOrders());
	
					} else if (Constant.TASK_UPDATE_ORDER.equals(task)) {
						
						mOrdersDaoService.updateOrdersStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_ORDER_ITEM.equals(task)) {
	
						mOrderItemDaoService.updateOrderItems(resp.getOrderItems());
	
					} else if (Constant.TASK_UPDATE_ORDER_ITEM.equals(task)) {
						
						mOrderItemDaoService.updateOrderItemStatus(resp.getStatus());
						
					} else if (Constant.TASK_GET_PRODUCT_GROUP.equals(task)) {
						
						mProductGroupDaoService.updateProductGroups(resp.getProductGroups());
	
					} else if (Constant.TASK_UPDATE_PRODUCT_GROUP.equals(task)) {
	
						mProductGroupDaoService.updateProductGroupStatus(resp.getStatus());
						
					} else if (Constant.TASK_GET_DISCOUNT.equals(task)) {
	
						mDiscountDaoService.updateDiscounts(resp.getDiscounts());
	
					} else if (Constant.TASK_UPDATE_DISCOUNT.equals(task)) {
	
						mDiscountDaoService.updateDiscountStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_EMPLOYEE.equals(task)) {
	
						mEmployeeDaoService.updateEmployees(resp.getEmployees());
	
					} else if (Constant.TASK_UPDATE_EMPLOYEE.equals(task)) {
	
						mEmployeeDaoService.updateEmployeeStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_CUSTOMER.equals(task)) {
	
						mCustomerDaoService.updateCustomers(resp.getCustomers());
	
					} else if (Constant.TASK_UPDATE_CUSTOMER.equals(task)) {
	
						mCustomerDaoService.updateCustomerStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_PRODUCT.equals(task)) {
	
						mProductDaoService.updateProducts(resp.getProducts());
	
					} else if (Constant.TASK_UPDATE_PRODUCT.equals(task)) {
	
						mProductDaoService.updateProductStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_USER.equals(task)) {
	
						mUserDaoService.updateUsers(resp.getUsers());
	
					} else if (Constant.TASK_UPDATE_USER.equals(task)) {
	
						mUserDaoService.updateUserStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_USER_ACCESS.equals(task)) {
	
						mUserAccessDaoService.updateUserAccesses(resp.getUserAccesses());
	
					} else if (Constant.TASK_UPDATE_USER_ACCESS.equals(task)) {
	
						mUserAccessDaoService.updateUserAccessStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_TRANSACTION.equals(task)) {
	
						mTransactionsDaoService.updateTransactions(resp.getTransactions());
	
					} else if (Constant.TASK_UPDATE_TRANSACTION.equals(task)) {
	
						mTransactionsDaoService.updateTransactionsStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_TRANSACTION_ITEM.equals(task)) {
	
						mTransactionItemDaoService.updateTransactionItems(resp.getTransactionItems());
	
					} else if (Constant.TASK_UPDATE_TRANSACTION_ITEM.equals(task)) {
	
						mTransactionItemDaoService.updateTransactionItemStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_SUPPLIER.equals(task)) {
	
						mSupplierDaoService.updateSuppliers(resp.getSuppliers());
	
					} else if (Constant.TASK_UPDATE_SUPPLIER.equals(task)) {
	
						mSupplierDaoService.updateSupplierStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_BILL.equals(task)) {
	
						mBillDaoService.updateBills(resp.getBills());
	
					} else if (Constant.TASK_UPDATE_BILL.equals(task)) {
	
						mBillDaoService.updateBillsStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_CASHFLOW.equals(task)) {
	
						mCashflowDaoService.updateCashflows(resp.getCashflows());
	
					} else if (Constant.TASK_UPDATE_CASHFLOW.equals(task)) {
	
						mCashflowDaoService.updateCashflowStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_INVENTORY.equals(task)) {
	
						mInventoryDaoService.updateInventories(resp.getInventories());
	
					} else if (Constant.TASK_UPDATE_INVENTORY.equals(task)) {
	
						mInventoryDaoService.updateInventoryStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_MERCHANT.equals(task) ||
							   Constant.TASK_ROOT_GET_MERCHANT.equals(task)) {
	
						mMerchantDaoService.updateMerchants(resp.getMerchants());
	
					} else if (Constant.TASK_UPDATE_MERCHANT.equals(task)) {
	
						mMerchantDaoService.updateMerchantStatus(resp.getStatus());
	
					} else if (Constant.TASK_GET_MERCHANT_ACCESS.equals(task) ||
							   Constant.TASK_ROOT_GET_MERCHANT_ACCESS.equals(task)) {
	
						mMerchantAccessDaoService.updateMerchantAccesses(resp.getMerchantAccesses());
	
					} else if (Constant.TASK_UPDATE_MERCHANT_ACCESS.equals(task)) {
	
						mMerchantAccessDaoService.updateMerchantAccessStatus(resp.getStatus());
	
					} else if (Constant.TASK_UPDATE_LAST_SYNC.equals(task)) {
	
						if (mContext instanceof LoginListener) {
							
							LoginListener mLoginListener = (LoginListener) mContext;
							mLoginListener.onMerchantsUpdated();
						}
					}
					
					if (resp.getNextIndex() > 0) {
						getNextRecord(resp.getNextIndex(), resp.getResultCount());
					} else {
						executeNextTask();
					}
				}
					
			} catch (Exception e) {
				
				e.printStackTrace();
				mAsyncListener.onSyncError();
			}
		}
	}

	public static String POST(String url, Object object) {

		String resp = null;

		try {

			final OutputStream out = new ByteArrayOutputStream();
			final ObjectMapper objectMapper = new ObjectMapper();
			final ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();

			writer.writeValue(out, object);

			String json = out.toString();

			RequestBody body = RequestBody.create(JSON, compress(json.getBytes()));
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();

			mCall = mHttpClient.newCall(request);
			Response response = mCall.execute();
			resp = uncompress(response.body().bytes());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return resp;
	}
	
	private static void abortHttpRequest() {

        if (mCall != null) {
            mCall.cancel();
        }
	}

    public static byte[] compress(byte[] bytes) throws IOException {

        System.out.println("Size Original : " + bytes.length);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);

        gos.write(bytes);
        gos.close();

        byte[] output = baos.toByteArray();

        System.out.println("Size Compressed : " + output.length);

        return output;
    }

    public static String uncompress(byte[] bytes)
            throws IOException {

        String result = null;
        GZIPInputStream gis = null;

        try {

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            gis = new GZIPInputStream(bais);
            result = IOUtils.toString(gis);

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            if (gis != null) {
                gis.close();
            }
        }

        return result;
    }
	
	public String getErrorDescription(String code) {
		
		String errorDesc = Constant.EMPTY_STRING;
		
		if (Constant.ERROR_SYSTEM_MAINTENANCE.equals(code)) {
			errorDesc = mContext.getString(R.string.error_system_maintenance);
		
		} else if (Constant.ERROR_APP_UPDATE_REQUIRED.equals(code)) {
			errorDesc = mContext.getString(R.string.error_app_update_required);
		
		} else if (Constant.ERROR_INVALID_TOKEN.equals(code)) {
			errorDesc = mContext.getString(R.string.error_invalid_token);
		
		} else if (Constant.ERROR_SERVICE_EXPIRED.equals(code)) {
			errorDesc = mContext.getString(R.string.error_service_expired);
		
		} else if (Constant.ERROR_COULD_NOT_OBTAINED_LOCK.equals(code)) {
			errorDesc = mContext.getString(R.string.error_could_not_obtained_lock);
		
		} else if (Constant.ERROR_REGISTER_MERCHANT_CONFLICT.equals(code)) {
			errorDesc = mContext.getString(R.string.error_register_merchant_conflict);
		
		} else if (Constant.ERROR_INVALID_APP_CERT.equals(code)) {
			errorDesc = mContext.getString(R.string.error_invalid_app_cert);
		
		} else { 
			errorDesc = mContext.getString(R.string.error_general);
		}
		
		return errorDesc;
	}
}

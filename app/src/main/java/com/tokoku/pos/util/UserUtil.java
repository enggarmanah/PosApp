package com.tokoku.pos.util;

import java.util.HashMap;

import com.android.pos.dao.User;
import com.android.pos.dao.UserAccess;
import com.tokoku.pos.Config;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.UserAccessDaoService;
import com.tokoku.pos.dao.UserDaoService;

public class UserUtil {
	
	private static UserDaoService mUserDaoService = new UserDaoService();
	private static UserAccessDaoService mUserAccessDaoService = new UserAccessDaoService();
	
	private static User mUser;
	private static HashMap<String, UserAccess> mUserAccesses;
	private static boolean mIsMerchant = false;
	
	public static User getUser() {
		
		if (mUser == null && Config.isDebug()) {
			
			DbUtil.switchDb(null, MerchantUtil.getMerchantId());
			
			mUserDaoService = new UserDaoService();
			mUserAccessDaoService = new UserAccessDaoService();
			
			setUser(mUserDaoService.getUser(Long.valueOf(1)));
		}
		
		return mUser;
	}
	
	public static void setUser(User user) {
		
		mUser = user;
		
		mUserAccesses = new HashMap<String, UserAccess>();
		
		// if root, no need to get access list
		if (user.getId() == null) {
			return;
		}
		
		mUserDaoService = new UserDaoService();
		mUserAccessDaoService = new UserAccessDaoService();
		
		for (UserAccess userAccess : mUserAccessDaoService.getUserAccessList(user.getId())) {
			
			mUserAccesses.put(userAccess.getCode(), userAccess);
		}
	}
	
	public static void resetUser() {
		
		mUser = null;
		mUserAccesses = new HashMap<String, UserAccess>();
	}
	
	public static boolean isMerchant() {
		
		return mIsMerchant;
	}
	
	public static void setMerchant(boolean isMerchant) {
		
		mIsMerchant = isMerchant;
	}
	
	public static boolean isCashier() {
		
		if (getUser() == null) {
			return false;
		}
		
		return Constant.USER_ROLE_CASHIER.equals(getUser().getRole());
	}
	
	public static boolean isAdmin() {
		
		if (getUser() == null) {
			return false;
		}
		
		return Constant.USER_ROLE_ADMIN.equals(getUser().getRole());
	}
	
	public static boolean isWaitress() {
		
		if (getUser() == null) {
			return false;
		}
		
		return Constant.USER_ROLE_WAITRESS.equals(getUser().getRole());
	}
	
	public static boolean isEmployee() {
		
		if (getUser() == null) {
			return false;
		}
		
		return Constant.USER_ROLE_EMPLOYEE.equals(getUser().getRole());
	}
	
	public static boolean isRoot() {
		
		if (mUser == null) {
			return false;
		}
		
		if (mUser != null && Constant.ROOT.equals(mUser.getUserId())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isUserHasAccess(String accessCode) {
		
		boolean isHasAccess = false;
		
		UserAccess userAccess = mUserAccesses.get(accessCode);
		
		if (userAccess != null && Constant.STATUS_YES.equals(userAccess.getStatus())) {
			isHasAccess = true;
		}
		
		return isHasAccess;
	}
	
	public static boolean isUserHasReportsAccess() {
		
		return isUserHasAccess(Constant.ACCESS_REPORT_BILLS) || isUserHasAccess(Constant.ACCESS_REPORT_CASHFLOW) ||
			isUserHasAccess(Constant.ACCESS_REPORT_COMMISION) || isUserHasAccess(Constant.ACCESS_REPORT_CREDIT) ||
			isUserHasAccess(Constant.ACCESS_REPORT_INVENTORY) || isUserHasAccess(Constant.ACCESS_REPORT_PRODUCT_STATISTIC) || 
			isUserHasAccess(Constant.ACCESS_REPORT_SERVICE_CHARGE) || isUserHasAccess(Constant.ACCESS_REPORT_TAX) ||
			isUserHasAccess(Constant.ACCESS_REPORT_TRANSACTION);
	}
}

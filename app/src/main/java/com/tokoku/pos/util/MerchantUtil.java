package com.tokoku.pos.util;

import com.android.pos.dao.Merchant;
import com.tokoku.pos.Config;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.dao.ProductDaoService;

import java.util.Date;

public class MerchantUtil {
	
	private static MerchantDaoService merchantDaoService = new MerchantDaoService();
	private static ProductDaoService productDaoService = new ProductDaoService();
	private static BillsDaoService billDaoService = new BillsDaoService();
	
	private static Merchant mMerchant;
	
	private static Integer mBelowStockLimitProductCount;
	private static Integer mPastDueBillsCount;
	private static Integer mOutstandingBillsCount;
	
	
	public static Merchant getMerchant() {
		
		if (mMerchant == null && Config.isDebug() && !UserUtil.isRoot()) {
			
			Long merchantId = Long.valueOf(10189);
			
			DbUtil.switchDb(null, merchantId);
			MerchantUtil.recreateDao();
			
			mMerchant = merchantDaoService.getMerchant(merchantId);
		}
		
		return mMerchant;
	}
	
	public static Long getMerchantId() {
		
		return getMerchant() != null ? getMerchant().getId() : null;
	}
	
	public static String getMerchantType() {
		
		return getMerchant() != null ? getMerchant().getType() : null;
	}
		
	public static String getLoginId() {
		
		return getMerchant() != null ? getMerchant().getLoginId() : null;
	}

	public static boolean hasActiveCloud() {

		return (getMerchant().getPeriodEnd().compareTo(new Date()) > 0);
	}

	public static void setMerchant(Merchant merchant) {
		
		mMerchant = merchant;
		
		mBelowStockLimitProductCount = null;
		mPastDueBillsCount = null;
		mOutstandingBillsCount = null;
		
		if (mMerchant != null) {
			
			CommonUtil.setLocale(CommonUtil.parseLocale(mMerchant.getLocale()));
		}
	}
	
	public static void recreateDao() {
		
		merchantDaoService = new MerchantDaoService();
		productDaoService = new ProductDaoService();
		billDaoService = new BillsDaoService();
	}
	
	public static Integer getBelowStockLimitProductCount() {
		
		if (mBelowStockLimitProductCount == null) {
			refreshBelowStockLimitProductCount(); 
		}
		
		return mBelowStockLimitProductCount; 
	}
	
	public static Integer getPastDueBillsCount() {
		
		if (mPastDueBillsCount == null) {
			refreshPastDueBillsCount();
		}
		
		return mPastDueBillsCount; 
	}
	
	public static Integer getOutstandingBillsCount() {
		
		if (mOutstandingBillsCount == null) {
			refreshOutstandingBillsCount();
		}
		
		return mOutstandingBillsCount; 
	}
	
	public static void refreshBelowStockLimitProductCount() {
		
		mBelowStockLimitProductCount = productDaoService.getBelowStockLimitProductCount();
	}
	
	public static void refreshPastDueBillsCount() {
		
		mPastDueBillsCount = billDaoService.getPastDueBillsCount();
	}
	
	public static void refreshOutstandingBillsCount() {
		
		mOutstandingBillsCount = billDaoService.getOutstandingBillsCount();
	}
}

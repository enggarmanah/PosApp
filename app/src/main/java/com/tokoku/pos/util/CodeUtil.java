package com.tokoku.pos.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.tokoku.pos.R;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;

public class CodeUtil {
	
	private static CodeBean[] merchantTypes;
	private static CodeBean[] productStatus;
	private static CodeBean[] status;
	private static CodeBean[] productTypes;
	private static CodeBean[] fnBProductTypes;
	private static CodeBean[] booleans;
	private static CodeBean[] roles;
	private static CodeBean[] emailStatus;
	private static CodeBean[] priceTypeCount;
	private static CodeBean[] quantityType;
	private static CodeBean[] discountTypes;
	private static CodeBean[] orderTypes;
	private static CodeBean[] paymentTypes;
	private static CodeBean[] printingOptions;
	private static CodeBean[] fnBorderTypes;
	private static CodeBean[] billTypes;
	private static CodeBean[] inventoryStatus;
	private static CodeBean[] moduleAccess;
	private static CodeBean[] fontSizes;
	private static CodeBean[] cashflowTypes;
	private static CodeBean[] priceTypes;
	private static CodeBean[] commissionTypes;
	
	private static List<CodeBean> inventorystatusList;
	
	private static Context mContext;
	private static Boolean isInitialized = false;
	
	public static void initCodes(Context context) {
		
		if (isInitialized) {
			return;
		}
		
		mContext = context;
		
		roles = new CodeBean[4];
		
		CodeBean code = new CodeBean();
		code.setCode(Constant.USER_ROLE_CASHIER);
		code.setLabel(mContext.getString(R.string.user_role_cashier));
		roles[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.USER_ROLE_WAITRESS);
		code.setLabel(mContext.getString(R.string.user_role_waitress));
		roles[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.USER_ROLE_EMPLOYEE);
		code.setLabel(mContext.getString(R.string.user_role_employee));
		roles[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.USER_ROLE_ADMIN);
		code.setLabel(mContext.getString(R.string.user_role_admin));
		roles[3] = code;
		
		priceTypeCount = new CodeBean[3];
		
		code = new CodeBean();
		code.setCode(Constant.PRICE_TYPE_COUNT_1);
		code.setLabel(mContext.getString(R.string.price_type_count_1));
		priceTypeCount[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PRICE_TYPE_COUNT_2);
		code.setLabel(mContext.getString(R.string.price_type_count_2));
		priceTypeCount[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PRICE_TYPE_COUNT_3);
		code.setLabel(mContext.getString(R.string.price_type_count_3));
		priceTypeCount[2] = code;
		
		fontSizes = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.FONT_SIZE_SMALL);
		code.setLabel(mContext.getString(R.string.font_size_small));
		fontSizes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.FONT_SIZE_REGULAR);
		code.setLabel(mContext.getString(R.string.font_size_regular));
		fontSizes[1] = code;
		
		quantityType = new CodeBean[8];
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_PIECE);
		code.setLabel(mContext.getString(R.string.quantity_type_piece));
		quantityType[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_PACK);
		code.setLabel(mContext.getString(R.string.quantity_type_pack));
		quantityType[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_DOZEN);
		code.setLabel(mContext.getString(R.string.quantity_type_dozen));
		quantityType[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_KG);
		code.setLabel(mContext.getString(R.string.quantity_type_kg));
		quantityType[3] = code;

		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_GRAM);
		code.setLabel(mContext.getString(R.string.quantity_type_gram));
		quantityType[4] = code;
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_METER);
		code.setLabel(mContext.getString(R.string.quantity_type_meter));
		quantityType[5] = code;
		
		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_LITER);
		code.setLabel(mContext.getString(R.string.quantity_type_liter));
		quantityType[6] = code;

		code = new CodeBean();
		code.setCode(Constant.QUANTITY_TYPE_MILILITER);
		code.setLabel(mContext.getString(R.string.quantity_type_milliliter));
		quantityType[7] = code;
		
		moduleAccess = new CodeBean[20];
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_CASHIER);
		code.setLabel(mContext.getString(R.string.menu_cashier));
		code.setOrder(mContext.getString(R.string.menu_cashier_order));
		moduleAccess[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_WAITRESS);
		code.setLabel(mContext.getString(R.string.menu_waitress));
		code.setOrder(mContext.getString(R.string.menu_waitress_order));
		moduleAccess[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_ORDER);
		code.setLabel(mContext.getString(R.string.menu_order));
		code.setOrder(mContext.getString(R.string.menu_order_order));
		moduleAccess[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_DATA_MANAGEMENT);
		code.setLabel(mContext.getString(R.string.menu_data_management));
		code.setOrder(mContext.getString(R.string.menu_data_management_order));
		moduleAccess[3] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_CUSTOMER);
		code.setLabel(mContext.getString(R.string.menu_customer));
		code.setOrder(mContext.getString(R.string.menu_customer_order));
		moduleAccess[4] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_INVENTORY);
		code.setLabel(mContext.getString(R.string.menu_inventory));
		code.setOrder(mContext.getString(R.string.menu_inventory_order));
		moduleAccess[5] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_BILLS);
		code.setLabel(mContext.getString(R.string.menu_bills));
		code.setOrder(mContext.getString(R.string.menu_bills_order));
		moduleAccess[6] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_CASHFLOW);
		code.setLabel(mContext.getString(R.string.menu_cashflow));
		code.setOrder(mContext.getString(R.string.menu_cashflow_order));
		moduleAccess[7] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_USER_ACCESS);
		code.setLabel(mContext.getString(R.string.menu_user_access));
		code.setOrder(mContext.getString(R.string.menu_user_access_order));
		moduleAccess[8] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_TRANSACTION);
		code.setLabel(mContext.getString(R.string.menu_report_transaction));
		code.setOrder(mContext.getString(R.string.menu_report_transaction_order));
		moduleAccess[9] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_PRODUCT_STATISTIC);
		code.setLabel(mContext.getString(R.string.menu_report_product_statistic));
		code.setOrder(mContext.getString(R.string.menu_report_product_statistic_order));
		moduleAccess[10] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_COMMISION);
		code.setLabel(mContext.getString(R.string.menu_report_commision));
		code.setOrder(mContext.getString(R.string.menu_report_commision_order));
		moduleAccess[11] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_INVENTORY);
		code.setLabel(mContext.getString(R.string.menu_report_inventory));
		code.setOrder(mContext.getString(R.string.menu_report_inventory_order));
		moduleAccess[12] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_CASHFLOW);
		code.setLabel(mContext.getString(R.string.menu_report_cashflow));
		code.setOrder(mContext.getString(R.string.menu_report_cashflow_order));
		moduleAccess[13] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_CREDIT);
		code.setLabel(mContext.getString(R.string.menu_report_credit));
		code.setOrder(mContext.getString(R.string.menu_report_credit_order));
		moduleAccess[14] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_BILLS);
		code.setLabel(mContext.getString(R.string.menu_report_bills));
		code.setOrder(mContext.getString(R.string.menu_report_bills_order));
		moduleAccess[15] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_TAX);
		code.setLabel(mContext.getString(R.string.menu_report_tax));
		code.setOrder(mContext.getString(R.string.menu_report_tax_order));
		moduleAccess[16] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_REPORT_SERVICE_CHARGE);
		code.setLabel(mContext.getString(R.string.menu_report_service_charge));
		code.setOrder(mContext.getString(R.string.menu_report_service_charge_order));
		moduleAccess[17] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_FAVORITE_CUSTOMER);
		code.setLabel(mContext.getString(R.string.menu_favorite_customer));
		code.setOrder(mContext.getString(R.string.menu_favorite_customer_order));
		moduleAccess[18] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ACCESS_FAVORITE_SUPPLIER);
		code.setLabel(mContext.getString(R.string.menu_favorite_supplier));
		code.setOrder(mContext.getString(R.string.menu_favorite_supplier_order));
		moduleAccess[19] = code;
				
		status = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_ACTIVE);
		code.setLabel(mContext.getString(R.string.status_active));
		status[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_INACTIVE);
		code.setLabel(mContext.getString(R.string.status_inactive));
		status[1] = code;
		
		productStatus = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_ACTIVE);
		code.setLabel(mContext.getString(R.string.product_status_active));
		productStatus[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_INACTIVE);
		code.setLabel(mContext.getString(R.string.product_status_inactive));
		productStatus[1] = code;
		
		productTypes = new CodeBean[3];
		
		code = new CodeBean();
		code.setCode(Constant.PRODUCT_TYPE_GOODS);
		code.setLabel(mContext.getString(R.string.product_type_goods));
		productTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PRODUCT_TYPE_SERVICE);
		code.setLabel(mContext.getString(R.string.product_type_service));
		productTypes[1] = code;

		code = new CodeBean();
		code.setCode(Constant.PRODUCT_TYPE_NON_GOODS_N_SERVICE);
		code.setLabel(mContext.getString(R.string.product_type_non_goods_n_service));
		productTypes[2] = code;
		
		fnBProductTypes = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode("M");
		code.setLabel(mContext.getString(R.string.fnb_product_type_self_made));
		fnBProductTypes[0] = code;
		
		code = new CodeBean();
		code.setCode("P");
		code.setLabel(mContext.getString(R.string.fnb_product_type_purchased));
		fnBProductTypes[1] = code;
		
		booleans = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_YES);
		code.setLabel(mContext.getString(R.string.status_yes));
		booleans[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_NO);
		code.setLabel(mContext.getString(R.string.status_no));
		booleans[1] = code;
		
		merchantTypes = new CodeBean[4];
		
		code = new CodeBean();
		code.setCode(Constant.MERCHANT_TYPE_GENERAL_GOODS);
		code.setLabel(mContext.getString(R.string.merchant_type_general_goods));
		merchantTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.MERCHANT_TYPE_GOODS_N_SERVICES);
		code.setLabel(mContext.getString(R.string.merchant_type_goods_and_service));
		merchantTypes[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES);
		code.setLabel(mContext.getString(R.string.merchant_type_food_and_beverage));
		merchantTypes[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.MERCHANT_TYPE_MEDICAL_SERVICES);
		code.setLabel(mContext.getString(R.string.merchant_type_medical_service));
		merchantTypes[3] = code;
			
		cashflowTypes = new CodeBean[8];
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_BILL_PAYMENT);
		code.setLabel(mContext.getString(R.string.cashflow_type_bill_payment));
		cashflowTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_EXPENSE);
		code.setLabel(mContext.getString(R.string.cashflow_type_expense));
		cashflowTypes[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_REFUND);
		code.setLabel(mContext.getString(R.string.cashflow_type_refund));
		cashflowTypes[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_INVC_PAYMENT);
		code.setLabel(mContext.getString(R.string.cashflow_type_invc_payment));
		cashflowTypes[3] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_BANK_DEPOSIT);
		code.setLabel(mContext.getString(R.string.cashflow_type_bank_deposit));
		cashflowTypes[4] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_BANK_WITHDRAWAL);
		code.setLabel(mContext.getString(R.string.cashflow_type_bank_withdrawal));
		cashflowTypes[5] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_CAPITAL_IN);
		code.setLabel(mContext.getString(R.string.cashflow_type_capital_in));
		cashflowTypes[6] = code;
		
		code = new CodeBean();
		code.setCode(Constant.CASHFLOW_TYPE_CAPITAL_OUT);
		code.setLabel(mContext.getString(R.string.cashflow_type_capital_out));
		cashflowTypes[7] = code;
		
		emailStatus = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_YES);
		code.setLabel(mContext.getString(R.string.email_status_yes));
		emailStatus[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.STATUS_NO);
		code.setLabel(mContext.getString(R.string.email_status_no));
		emailStatus[1] = code;
		
		discountTypes = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.DISCOUNT_TYPE_PERCENTAGE);
		code.setLabel(mContext.getString(R.string.discount_type_percent));
		discountTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.DISCOUNT_TYPE_AMOUNT);
		code.setLabel(mContext.getString(R.string.discount_type_amount));
		discountTypes[1] = code;
			
		orderTypes = new CodeBean[3];
		
		code = new CodeBean();
		code.setCode(Constant.ORDER_TYPE_NO);
		code.setLabel(mContext.getString(R.string.order_type_no));
		orderTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ORDER_TYPE_BEFORE_PAYMENT);
		code.setLabel(mContext.getString(R.string.order_type_before_payment));
		orderTypes[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.ORDER_TYPE_AFTER_PAYMENT);
		code.setLabel(mContext.getString(R.string.order_type_after_payment));
		orderTypes[2] = code;
		
		paymentTypes = new CodeBean[4];
		
		code = new CodeBean();
		code.setCode(Constant.PAYMENT_TYPE_CASH);
		code.setLabel(mContext.getString(R.string.payment_type_cash));
		paymentTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PAYMENT_TYPE_DEBIT_CARD);
		code.setLabel(mContext.getString(R.string.payment_type_debit_card));
		paymentTypes[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PAYMENT_TYPE_CREDIT_CARD);
		code.setLabel(mContext.getString(R.string.payment_type_credit_card));
		paymentTypes[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.PAYMENT_TYPE_CREDIT);
		code.setLabel(mContext.getString(R.string.payment_type_credit));
		paymentTypes[3] = code;

		printingOptions = new CodeBean[4];

		code = new CodeBean();
		code.setCode(Constant.PRINTING_OPTION_NO);
		code.setLabel(mContext.getString(R.string.printing_option_no));
		printingOptions[0] = code;

		code = new CodeBean();
		code.setCode(Constant.PRINTING_OPTION_YES_ONCE);
		code.setLabel(mContext.getString(R.string.printing_option_yes_once));
		printingOptions[1] = code;

		code = new CodeBean();
		code.setCode(Constant.PRINTING_OPTION_YES_TWICE);
		code.setLabel(mContext.getString(R.string.printing_option_yes_twice));
		printingOptions[2] = code;

		code = new CodeBean();
		code.setCode(Constant.PRINTING_OPTION_SEND_EMAIL);
		code.setLabel(mContext.getString(R.string.printing_option_send_email));
		printingOptions[3] = code;

		fnBorderTypes = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.TXN_ORDER_TYPE_DINE_IN);
		code.setLabel(mContext.getString(R.string.order_type_dine_in));
		fnBorderTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.TXN_ORDER_TYPE_TAKEWAY);
		code.setLabel(mContext.getString(R.string.order_type_take_away));
		fnBorderTypes[1] = code;
				
		billTypes = new CodeBean[2];
		
		code = new CodeBean();
		code.setCode(Constant.BILL_TYPE_PRODUCT_PURCHASE);
		code.setLabel(mContext.getString(R.string.bill_type_product_purchase));
		billTypes[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.BILL_TYPE_EXPENSE);
		code.setLabel(mContext.getString(R.string.bill_type_expense));
		billTypes[1] = code;
		
		inventoryStatus = new CodeBean[8];
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_PURCHASE);
		code.setLabel(mContext.getString(R.string.inventory_status_purchase));
		inventoryStatus[0] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_SELF_PRODUCTION);
		code.setLabel(mContext.getString(R.string.inventory_status_self_production));
		inventoryStatus[1] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_REFUND);
		code.setLabel(mContext.getString(R.string.inventory_status_refund));
		inventoryStatus[2] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_RETURN);
		code.setLabel(mContext.getString(R.string.inventory_status_return));
		inventoryStatus[3] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_REPLACEMENT);
		code.setLabel(mContext.getString(R.string.inventory_status_replacement));
		inventoryStatus[4] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_LOST);
		code.setLabel(mContext.getString(R.string.inventory_status_lost));
		inventoryStatus[5] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_DAMAGE);
		code.setLabel(mContext.getString(R.string.inventory_status_damage));
		inventoryStatus[6] = code;
		
		code = new CodeBean();
		code.setCode(Constant.INVENTORY_STATUS_INITIAL_STOCK);
		code.setLabel(mContext.getString(R.string.inventory_status_initial_stock));
		inventoryStatus[7] = code;
		
		inventorystatusList = new ArrayList<CodeBean>(Arrays.asList(inventoryStatus));
		
		CodeBean bean = new CodeBean();
		bean.setCode(Constant.INVENTORY_STATUS_SALE);
		bean.setLabel(mContext.getString(R.string.inventory_status_sale));
		inventorystatusList.add(bean);
		
		bean = new CodeBean();
		bean.setCode(Constant.INVENTORY_STATUS_REFUND);
		bean.setLabel(mContext.getString(R.string.inventory_status_refund));
		inventorystatusList.add(bean);

		priceTypes = new CodeBean[2];

		code = new CodeBean();
		code.setCode(Constant.PRICE_TYPE_STATIC);
		code.setLabel(mContext.getString(R.string.price_type_static));
		priceTypes[0] = code;

		code = new CodeBean();
		code.setCode(Constant.PRICE_TYPE_DYNAMIC);
		code.setLabel(mContext.getString(R.string.price_type_dynamic));
		priceTypes[1] = code;

		commissionTypes = new CodeBean[2];

		code = new CodeBean();
		code.setCode(Constant.COMMISSION_TYPE_AMOUNT);
		code.setLabel(mContext.getString(R.string.commission_type_amount));
		commissionTypes[0] = code;

		code = new CodeBean();
		code.setCode(Constant.COMMISSION_TYPE_PERCENTAGE);
		code.setLabel(mContext.getString(R.string.commission_type_percentage));
		commissionTypes[1] = code;
		
		isInitialized = true;
	}
	
	public static CodeBean[] getMerchantTypes() {
		return merchantTypes;
	}
	
	public static CodeBean[] getCashflowTypes() {
		return cashflowTypes;
	}
	
	public static CodeBean[] getStatus() {
		return status;
	}
	
	public static CodeBean[] getProductStatus() {
		return productStatus;
	}
	
	public static CodeBean[] getProductTypes() {
		return productTypes;
	}
	
	public static CodeBean[] getFnBProductTypes() {
		return fnBProductTypes;
	}
	
	public static CodeBean[] getBooleans() {
		return booleans;
	}
	
	public static CodeBean[] getRoles() {
		return roles;
	}
	
	public static CodeBean[] getEmailStatus() {
		return emailStatus;
	}
	
	public static CodeBean[] getDiscountTypes() {
		return discountTypes;
	}
	
	public static CodeBean[] getQuantityTypes() {
		return quantityType;
	}

    public static CodeBean[] getPriceTypes() {
        return priceTypes;
    }

    public static CodeBean[] getCommissionTypes() {
        return commissionTypes;
    }
	
	public static CodeBean[] getFontSizes() {
		return fontSizes;
	}
	
	public static CodeBean[] getOrderTypes() {
		return orderTypes;
	}
	
	public static CodeBean[] getPaymentTypes() {
		return paymentTypes;
	}

	public static CodeBean[] getPrintingOptions() {
        return printingOptions;
    }

	public static CodeBean[] getFnBOrderTypes() {
		return fnBorderTypes;
	}
	
	public static CodeBean[] getBillTypes() {
		return billTypes;
	}
	
	public static CodeBean[] getInventoryStatus() {
		return inventoryStatus;
	}
	
	public static CodeBean[] getPriceTypeCount() {
		return priceTypeCount;
	}
	
	public static CodeBean[] getModuleAccesses() {
		return moduleAccess;
	}
	
	public static String getModuleAccessLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : moduleAccess) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}
	
	public static String getPaymentTypeLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : paymentTypes) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}
	
	public static String getOrderTypeLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : fnBorderTypes) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}
	
	public static String getInvetoriStatusLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : inventorystatusList) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}
	
	public static String getBillsTypeLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : billTypes) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}
	
	public static String getCashflowTypeLabel(String code) {
		
		String label = Constant.EMPTY_STRING;
		
		for (CodeBean codeBean : cashflowTypes) {
			if (codeBean.getCode().equals(code)) {
				label = codeBean.getLabel();
				break;
			}
		}
		
		return label;
	}

	public static String getProductType(String label) {

		String code = Constant.PRODUCT_TYPE_GOODS;

		for (CodeBean codeBean : productTypes) {
			if (codeBean.getLabel().equals(label)) {
				code = codeBean.getCode();
				break;
			}
		}

		return code;
	}

    public static String getProductTypeLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : productTypes) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }

    public static String getPriceType(String label) {

        String code = Constant.PRICE_TYPE_STATIC;

        for (CodeBean codeBean : priceTypes) {
            if (codeBean.getLabel().equals(label)) {
                code = codeBean.getCode();
                break;
            }
        }

        return code;
    }

    public static String getPriceTypeLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : priceTypes) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }

    public static String getPicRequired(String label) {

        String code = Constant.STATUS_NO;

        for (CodeBean codeBean : booleans) {
            if (codeBean.getLabel().equals(label)) {
                code = codeBean.getCode();
                break;
            }
        }

        return code;
    }

    public static String getPicRequiredLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : booleans) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }

    public static String getCommissionType(String label) {

        String code = Constant.COMMISSION_TYPE_AMOUNT;

        for (CodeBean codeBean : commissionTypes) {
            if (codeBean.getLabel().equals(label)) {
                code = codeBean.getCode();
                break;
            }
        }

        return code;
    }

    public static String getCommissionTypeLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : commissionTypes) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }

    public static String getQuantityType(String label) {

        String code = Constant.QUANTITY_TYPE_PIECE;

        for (CodeBean codeBean : quantityType) {
            if (codeBean.getLabel().equals(label)) {
                code = codeBean.getCode();
                break;
            }
        }

        return code;
    }

    public static String getQuantityTypeLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : quantityType) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }

    public static String getProductStatus(String label) {

        String code = Constant.STATUS_ACTIVE;

        for (CodeBean codeBean : productStatus) {
            if (codeBean.getLabel().equals(label)) {
                code = codeBean.getCode();
                break;
            }
        }

        return code;
    }

    public static String getProductStatusLabel(String code) {

        String label = Constant.EMPTY_STRING;

        for (CodeBean codeBean : productStatus) {
            if (codeBean.getCode().equals(code)) {
                label = codeBean.getLabel();
                break;
            }
        }

        return label;
    }
}

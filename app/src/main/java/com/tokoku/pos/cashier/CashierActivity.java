package com.tokoku.pos.cashier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Discount;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.common.ConfirmListener;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.dao.MerchantDaoService;
import com.tokoku.pos.dao.OrderItemDaoService;
import com.tokoku.pos.dao.OrdersDaoService;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.dao.TransactionItemDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.popup.search.CustomerDlgFragment;
import com.tokoku.pos.popup.search.CustomerSelectionListener;
import com.tokoku.pos.popup.search.EmployeeDlgFragment;
import com.tokoku.pos.popup.search.EmployeeSelectionListener;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.ConfirmationUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.PrintUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class CashierActivity extends BaseActivity 
	implements CashierActionListener, SearchView.OnQueryTextListener, ConfirmListener,
		CashierProductCountDlgFragment.ProductActionListener, CustomerSelectionListener, EmployeeSelectionListener {
	
	LinearLayout messagePanel;
	TextView messageText;

	private CashierProductSearchFragment mProductSearchFragment;
	private CashierOrderFragment mOrderFragment;
	private CashierOrderDlgFragment mOrderDlgFragment;
	private CashierProductCountDlgFragment mProductCountDlgFragment;
	private CashierPaymentDlgFragment mPaymentDlgFragment;
	private CashierOrderSummaryDlgFragment mOrderSummaryDlgFragment;
	private CashierPaymentSummaryDlgFragment mPaymentSummaryDlgFragment;
	private CashierDiscountDlgFragment mDiscountDlgFragment;
	private CashierDiscountNominalDlgFragment mDiscountNominalDlgFragment;
	private CustomerDlgFragment mCustomerDlgFragment;
	private EmployeeDlgFragment mEmployeeDlgFragment;
	
	boolean mIsMultiplesPane = false;
	boolean mIsEnableSearch = true;
	
	SearchView mSearchView;
	
	MenuItem mSearchItem;
	MenuItem mListItem;
	MenuItem mSelectPrinterItem;
	
	Product mSelectedProduct;
	List<TransactionItem> mTransactionItems;
	String state;
	
	HashMap<Long, Boolean> mSelectedOrders;
	Orders mSelectedOrder;
	
	Discount mDiscount;

	private static final String TRANSACTION_ITEMS = "TRANSACTION_ITEMS";
	private static final String DISCOUNT = "DISCOUNT";
	private static final String STATE = "STATE";
	private static final String SELECTED_ORDER = "SELECTED_ORDER";

	private static final String mProductSearchFragmentTag = "productSearchFragmentTag";
	private static final String mProductCountDlgFragmentTag = "productCountDlgFragmentTag";
	private static final String mOrderFragmentTag = "orderFragment";
	private static final String mPaymentDlgFragmentTag = "paymentDlgFragmentTag";
	private static final String mOrderDlgFragmentTag = "orderDlgFragmentTag";
	private static final String mPaymentSummaryDlgFragmentTag = "paymentSummaryDlgFragmentTag";
	private static final String mOrderSummaryDlgFragmentTag = "orderSummaryDlgFragmentTag";
	private static final String mDiscountDlgFragmentTag = "mDiscountDlgFragmentTag";
	private static final String mDiscountNominalDlgFragmentTag = "discountNominalDlgFragmentTag";
	private static final  String mCustomerDlgFragmentTag = "customerDlgFragmentTag";
	private static final  String mEmployeeDlgFragmentTag = "employeeDlgFragmentTag";
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private TransactionsDaoService mTransactionDaoService;
	private TransactionItemDaoService mTransactionItemDaoService;
	private ProductDaoService mProductDaoService;
	private InventoryDaoService mInventoryDaoService;
	
	private OrdersDaoService mOrderDaoService;
	private OrderItemDaoService mOrderItemDaoService;
	
	private static boolean isTryToConnect = false;
	private boolean mIsSubmitOrder = false;
	
	private Orders mOrder;
	private Customer mCustomer;
	
	private List<OrderItem> mOrderItems;
	
	private Employee mWaitress;
	private String mOrderType;
	private String mOrderReference;
	
	private String mState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cashier_activity);
		
		mState = Constant.CASHIER_STATE_CASHIER;
		
		mTransactionDaoService = new TransactionsDaoService();
		mTransactionItemDaoService = new TransactionItemDaoService();
		mProductDaoService = new ProductDaoService();
		mInventoryDaoService = new InventoryDaoService();
		
		mOrderDaoService = new OrdersDaoService();
		mOrderItemDaoService = new OrderItemDaoService();
		
		initDrawerMenu();

		initFragments(savedInstanceState);
		
		initWaitAfterFragmentRemovedTask(mProductSearchFragmentTag, mOrderFragmentTag);
		
		messagePanel = (LinearLayout) findViewById(R.id.messagePanel);
		messageText = (TextView) findViewById(R.id.messageText);
		
		messagePanel.setVisibility(View.GONE);
		
		messageText.setOnClickListener(getMessageTextOnClickListener());
		
		if (!isTryToConnect && PrintUtil.isPrinterActive() && !PrintUtil.isPrinterConnected() && !UserUtil.isWaitress()) {
			
			isTryToConnect = true;
			connectToMerchantPrinter();
		
		} else {
			
			PrintUtil.initBluetooth(this);
		}
		
		processExtras(getIntent().getExtras());
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setSelectedMenu(getString(R.string.menu_cashier));
		
		if (PrintUtil.isPrinterActive() && !PrintUtil.isPrinterConnected() && !UserUtil.isWaitress()) {
			setMessage(getString(R.string.printer_please_check_printer));
		} else {
			clearMessage();
		}
		
		mOrderFragment.setCashierState(mState);
	}
	
	private void connectToMerchantPrinter() {
		
		Merchant merchant = MerchantUtil.getMerchant();
		String printerType = merchant.getPrinterType();
		String printerAddress = merchant.getPrinterAddress();
		
		try {
			if (PrintUtil.initBluetooth(this)) {
				
				if (!CommonUtil.isEmpty(printerAddress)) {
					
					setMessage(getString(R.string.printer_connected_to, printerAddress));
					connectToPrinter(printerType, printerAddress);
				
				} else {
					
					setMessage(getString(R.string.printer_please_check_printer));
					PrintUtil.selectBluetoothPrinter();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setMessage(getString(R.string.printer_bluetooth_adapter_error));
		}
	}
	
	public void setMessage(String message) {
		
		messagePanel.setVisibility(View.VISIBLE);
		messageText.setText(message);
	}
	
	public void clearMessage() {
		
		messageText.setText(Constant.EMPTY_STRING);
		messagePanel.setVisibility(View.GONE);
	}
	
	public void disablePrinterOption() {
		
		clearMessage();
		
		if (mSelectPrinterItem != null) {
			mSelectPrinterItem.setVisible(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void initFragments(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			
			mTransactionItems = (List<TransactionItem>) savedInstanceState.getSerializable(TRANSACTION_ITEMS);
			mDiscount = (Discount) savedInstanceState.getSerializable(DISCOUNT);
			mSelectedOrder = (Orders) savedInstanceState.getSerializable(SELECTED_ORDER);
			mState = (String) savedInstanceState.getSerializable(STATE);
		}

		if (mTransactionItems == null) {
			
			mTransactionItems = new ArrayList<TransactionItem>();
		}

		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mProductSearchFragment = (CashierProductSearchFragment) getFragmentManager().findFragmentByTag(mProductSearchFragmentTag);
		mOrderFragment = (CashierOrderFragment) getFragmentManager().findFragmentByTag(mOrderFragmentTag);

		if (mProductSearchFragment == null) {
			mProductSearchFragment = new CashierProductSearchFragment();

		} else {
			removeFragment(mProductSearchFragment);
		}

		if (mOrderFragment == null) {
			mOrderFragment = new CashierOrderFragment();

		} else {
			removeFragment(mOrderFragment);
		}

		mOrderFragment.setTransactionItems(mTransactionItems);
		mOrderFragment.setDiscount(mDiscount);
		
		mProductCountDlgFragment = (CashierProductCountDlgFragment) getFragmentManager().findFragmentByTag(mProductCountDlgFragmentTag);
		
		if (mProductCountDlgFragment == null) {
			mProductCountDlgFragment = new CashierProductCountDlgFragment();
		}
		
		mPaymentDlgFragment = (CashierPaymentDlgFragment) getFragmentManager().findFragmentByTag(mPaymentDlgFragmentTag);
		
		if (mPaymentDlgFragment == null) {
			mPaymentDlgFragment = new CashierPaymentDlgFragment();
		}
		
		mOrderDlgFragment = (CashierOrderDlgFragment) getFragmentManager().findFragmentByTag(mOrderDlgFragmentTag);
		
		if (mOrderDlgFragment == null) {
			mOrderDlgFragment = new CashierOrderDlgFragment();
		}
		
		mPaymentSummaryDlgFragment = (CashierPaymentSummaryDlgFragment) getFragmentManager().findFragmentByTag(mPaymentSummaryDlgFragmentTag);
		
		if (mPaymentSummaryDlgFragment == null) {
			mPaymentSummaryDlgFragment = new CashierPaymentSummaryDlgFragment();
		}
		
		mOrderSummaryDlgFragment = (CashierOrderSummaryDlgFragment) getFragmentManager().findFragmentByTag(mOrderSummaryDlgFragmentTag);
		
		if (mOrderSummaryDlgFragment == null) {
			mOrderSummaryDlgFragment = new CashierOrderSummaryDlgFragment();
		}
		
		mDiscountDlgFragment = (CashierDiscountDlgFragment) getFragmentManager().findFragmentByTag(mDiscountDlgFragmentTag);
		
		if (mDiscountDlgFragment == null) {
			mDiscountDlgFragment = new CashierDiscountDlgFragment();
		}
		
		mDiscountNominalDlgFragment = (CashierDiscountNominalDlgFragment) getFragmentManager().findFragmentByTag(mDiscountNominalDlgFragmentTag);
		
		if (mDiscountNominalDlgFragment == null) {
			mDiscountNominalDlgFragment = new CashierDiscountNominalDlgFragment();
		}
		
		mCustomerDlgFragment = (CustomerDlgFragment) getFragmentManager().findFragmentByTag(mCustomerDlgFragmentTag);
		
		if (mCustomerDlgFragment == null) {
			mCustomerDlgFragment = new CustomerDlgFragment();
		}
		
		mEmployeeDlgFragment = (EmployeeDlgFragment) getFragmentManager().findFragmentByTag(mEmployeeDlgFragmentTag);
		
		if (mEmployeeDlgFragment == null) {
			mEmployeeDlgFragment = new EmployeeDlgFragment();
		}
	}

	@Override
	protected void afterFragmentRemoved() {
		
		System.out.println("afterFragmentRemoved");
		
		loadFragments();
	}
	
	private void loadFragments() {

		if (mIsMultiplesPane) {

			addFragment(mProductSearchFragment, mProductSearchFragmentTag);
			addFragment(mOrderFragment, mOrderFragmentTag);
			
		} else {

			if (mSelectedProduct != null) {
				addFragment(mProductSearchFragment, mProductSearchFragmentTag);
				
			} else {
				addFragment(mOrderFragment, mOrderFragmentTag);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putSerializable(TRANSACTION_ITEMS, (Serializable) mTransactionItems);
		outState.putSerializable(DISCOUNT, (Serializable) mDiscount);
		outState.putSerializable(SELECTED_ORDER, mSelectedOrder);
		outState.putSerializable(STATE, mState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cashier_menu, menu);

		mSearchItem = menu.findItem(R.id.menu_item_search);
		mListItem = menu.findItem(R.id.menu_item_list);
		mSelectPrinterItem = menu.findItem(R.id.menu_item_select_printer);

		mSearchView = (SearchView) mSearchItem.getActionView();
		mSearchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.START));

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
		if (null != searchManager) {
			mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		mSearchItem.setVisible(!isDrawerOpen);
		mListItem.setVisible(!isDrawerOpen);
		
		mSelectPrinterItem.setVisible(false);
		
		if (PrintUtil.isPrinterActive() && !PrintUtil.isPrinterConnected() && !UserUtil.isWaitress()) {
			mSelectPrinterItem.setVisible(!isDrawerOpen);
		}

		return super.onPrepareOptionsMenu(menu);
	}
	
	public void setSelectPrinterVisible(boolean isVisible) {
		
		if (mSelectPrinterItem != null) {
			mSelectPrinterItem.setVisible(isVisible);
		}
	}
	
	protected void doSearch(String query) {
		
		if (mIsMultiplesPane) {
			
			mProductSearchFragment.searchProducts(query);
			
		} else {
			
			replaceFragment(mProductSearchFragment, mProductSearchFragmentTag);
			mProductSearchFragment.searchProducts(query);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		synchronized (CommonUtil.LOCK) {
		
			switch (item.getItemId()) {
	
			case R.id.menu_item_search:
	
				doSearch(Constant.EMPTY_STRING);
	
				//showMessage(R.string.msg_notification_search_action);
				
				int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
				EditText searchEditText = (EditText) mSearchView.findViewById(searchSrcTextId);
				searchEditText.setHint(getString(R.string.search_product));
				searchEditText.setHintTextColor(getResources().getColor(R.color.search_hint_color));
	
				return true;
				
			case R.id.menu_item_select_printer:
				
				if (!PrintUtil.isBluetoothEnabled()) {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.printer_bluetooth_inactive));
	    			return true;
				}
				
				PrintUtil.initBluetooth(this);
				PrintUtil.selectBluetoothPrinter();
	
				return true;
	
			case R.id.menu_item_list:
				
				hideSearchView();
				
				if (mIsMultiplesPane) {
					
					mProductSearchFragment.showProductGroups();
					
				} else {
					
					Fragment f = getFragmentManager().findFragmentByTag(mProductSearchFragmentTag);
					
					if (f != null) {
						replaceFragment(mOrderFragment, mOrderFragmentTag);
					} else {
						replaceFragment(mProductSearchFragment, mProductSearchFragmentTag);
						mProductSearchFragment.showProductGroups();
					}
				}
	
				return true;
	
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

	public boolean onQueryTextChange(String query) {

		boolean isQuerySimilar = query.equals(prevQuery);

		prevQuery = query;

		if (mIsEnableSearch && !isQuerySimilar) {

			doSearch(query);
		}

		return true;
	}

	public boolean onQueryTextSubmit(String query) {

		doSearch(query);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

		return true;
	}

	public void displaySearch() {

		if (!mIsMultiplesPane) {
			replaceFragment(mProductSearchFragment, mProductSearchFragmentTag);
		}
	}
	
	public void hideSearchView() {
		
		mIsEnableSearch = false;
		mSearchItem.collapseActionView();
		mIsEnableSearch = true;
	}
	
	private void clearTransaction() {
		
		mSelectedProduct = null;
		mSelectedOrder = null;
		mDiscount = null;
		
		mTransactionItems.clear();
		
		mState = Constant.CASHIER_STATE_CASHIER;
		
		mOrder = null;
		mCustomer = null;
		
		mWaitress = null;
		mOrderType = null;
		mOrderReference = null;
		
		mProductSearchFragment.showProductGroups();
		
		mOrderFragment.setTransactionItems(mTransactionItems);
		mOrderFragment.setDiscount(mDiscount);
		mOrderFragment.setCashierState(mState);
		mOrderFragment.setSelectedOrders(mSelectedOrder, false);
	}
	
	@Override
	public void onClearTransaction() {
		
		clearTransaction();
	}
	
	public View.OnClickListener getMessageTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!PrintUtil.isBluetoothEnabled()) {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.printer_bluetooth_inactive));
	    			
	    			return;
				}
				
				PrintUtil.selectBluetoothPrinter();
			}
		};
	}
	
	public TransactionItem getTransactionItem(Product product, Float price, Employee personInCharge, Float quantity, String remarks) {

		TransactionItem transItem = new TransactionItem();

		transItem.setQuantity(quantity);
		transItem.setProduct(product);
		transItem.setProductId(product.getId());
		transItem.setProductName(product.getName());
		transItem.setProductType(product.getType());
		
		if (personInCharge != null) {
			
			transItem.setEmployeeId(personInCharge.getId());
			transItem.setEmployee(personInCharge);

            float commission = product.getCommision() != null ? product.getCommision() : 0f;

            if (Constant.COMMISSION_TYPE_PERCENTAGE.equals(product.getCommisionType())) {
                transItem.setCommision(commission * price / 100);
            } else {
                transItem.setCommision(commission);
            }
		}
		
		float costPrice = 0f;

        if (Constant.PRICE_TYPE_DYNAMIC.equals(product.getPriceType())) {

            costPrice = price;

        } else {

            costPrice = product.getCostPrice() != null ? product.getCostPrice() : CommonUtil.getCurrentPrice(product);
        }
		
		transItem.setPrice(price);
		transItem.setCostPrice(costPrice);

		/* transaction item discount is updated upon payment

		if (mDiscount != null) {
			
			float discountPercentage = mDiscount.getPercentage();
			discountAmount = discountPercentage * transItem.getPrice() / 100;
		}*/

		float discountAmount = 0;
		transItem.setDiscount(discountAmount);

		transItem.setRemarks(remarks);
		
		return transItem;
	}
	
	@Override
	public void onShowProductGroups() {
		
		hideSearchView();
		mProductSearchFragment.showProductGroups();
	}
	
	@Override
	public void onProductSelected(Product product) {
		
		if (!PrintUtil.isPrinterConnected()) {
			setMessage(getString(R.string.printer_please_check_printer));
			setSelectPrinterVisible(true);
		}
		
		onProductSelected(product, CommonUtil.getCurrentPrice(product), null, Float.valueOf(0), Constant.EMPTY_STRING);
	}

	@Override
	public void onProductSelected(Product product, Float price, Employee personInCharge, Float quantity, String remarks) {
		
		if (mProductCountDlgFragment.isAdded()) {
			return;
		}
		
		mSelectedProduct = product;
		
		mProductCountDlgFragment.show(getFragmentManager(), mProductCountDlgFragmentTag);
		mProductCountDlgFragment.setProduct(product, price, personInCharge, quantity, remarks);
		
		hideSearchView();
	}

	@Override
	public void onProductQuantitySelected(Product product, Float price, Employee personInCharge, Float quantity, String remarks) {

		mSelectedProduct = null;

		TransactionItem transItem = getTransactionItem(product, price, personInCharge, quantity, remarks);
		
		if (mIsMultiplesPane) {
			mOrderFragment.addTransactionItem(transItem);
		} else {
			replaceFragment(mOrderFragment, mOrderFragmentTag);
			mOrderFragment.addTransactionItem(transItem);
		}
	}

	@Override
	public void onPaymentRequested(Float totalBill) {
		
		if (mPaymentDlgFragment.isAdded()) {
			return;
		}
		
		mPaymentDlgFragment.show(getFragmentManager(), mPaymentDlgFragmentTag);
		mPaymentDlgFragment.setBillInfo(totalBill, mCustomer);
	}
	
	@Override
	public void onOrderRequested(Integer totalOrder) {
		
		if (mOrderDlgFragment.isAdded()) {
			return;
		}
		
		mOrderDlgFragment.show(getFragmentManager(), mOrderDlgFragmentTag);
		mOrderDlgFragment.setTotalOrder(totalOrder);
	}

	@Override
	public void onPaymentInfoProvided(Customer customer, String paymentType, Float totalBill, Float payment) {
		
		if (mPaymentSummaryDlgFragment.isAdded()) {
			return;
		}
		
		mPaymentSummaryDlgFragment.show(getFragmentManager(), mPaymentSummaryDlgFragmentTag);
		mPaymentSummaryDlgFragment.setPaymentInfo(customer, paymentType, totalBill, payment);
	}
	
	@Override
	public void onOrderInfoProvided(String orderReference, String orderType, Employee waitress, Customer customer) {
		
		if (mOrderSummaryDlgFragment.isAdded()) {
			return;
		}
		
		mOrderSummaryDlgFragment.show(getFragmentManager(), mOrderSummaryDlgFragmentTag);
		mOrderSummaryDlgFragment.setOrderInfo(orderReference, orderType, waitress, customer);
	}
	
	@Override
	public void onPaymentCompleted(Transactions transaction) {

        // 10 Dec 2016, disable item discount, item value on Product Report become not round
		updateTransactionItemsDiscount();

		transaction.setEmployee(mWaitress);
		transaction.setOrderType(mOrderType);
		transaction.setOrderReference(mOrderReference);
		
		float totalBill = 0;
		
		for (TransactionItem transactionItem : mTransactionItems) {
			totalBill += transactionItem.getQuantity() * transactionItem.getPrice();
		}
		
		transaction.setBillAmount(totalBill);
		
		float discountAmount = 0;
		
		if (mDiscount != null) {
			
			if (mDiscount.getPercentage() != null && mDiscount.getPercentage() != 0) {
				discountAmount = mDiscount.getPercentage() * totalBill / 100;
			} else {
				discountAmount = mDiscount.getAmount();
			}
			
			transaction.setDiscountName(mDiscount.getName());
			transaction.setDiscountPercentage(mDiscount.getPercentage());
			transaction.setDiscountAmount(discountAmount);
		}
		
		totalBill = totalBill - discountAmount;
		
		Merchant merchant = MerchantUtil.getMerchant();
		
		float taxAmount = merchant.getTaxPercentage() * totalBill / 100;
		
		transaction.setTaxPercentage(merchant.getTaxPercentage());
		transaction.setTaxAmount(taxAmount);
		
		float serviceChargeAmount = merchant.getServiceChargePercentage() * totalBill / 100;
		
		transaction.setServiceChargePercentage(merchant.getServiceChargePercentage());
		transaction.setServiceChargeAmount(serviceChargeAmount);
		
		transaction.setMerchant(MerchantUtil.getMerchant());
		transaction.setUploadStatus(Constant.STATUS_YES);
		
		if (mWaitress != null) {
			transaction.setWaitressId(mWaitress.getId());
			transaction.setWaitressName(mWaitress.getName());
		}
		
		mTransactionDaoService.addTransactions(transaction);
		
		for (TransactionItem transactionItem : mTransactionItems) {
			
			transactionItem.setTransactionId(transaction.getId());
			transactionItem.setMerchant(MerchantUtil.getMerchant());
			transactionItem.setUploadStatus(Constant.STATUS_YES);
			
			if (transactionItem.getQuantity() != 0) {
				mTransactionItemDaoService.addTransactionItem(transactionItem);
			}
			
			// track the product movement in inventory if it's not resto menu
			
			Product product = mProductDaoService.getProduct(transactionItem.getProductId());
			boolean isGoods = Constant.PRODUCT_TYPE_GOODS.equals(product.getType());
			
			if (isGoods) {
				
				Inventory inventory = new Inventory();
				inventory.setMerchant(MerchantUtil.getMerchant());
				inventory.setBillReferenceNo(transaction.getTransactionNo());
				inventory.setProduct(transactionItem.getProduct());
				inventory.setProductName(transactionItem.getProductName());
				inventory.setQuantity(-transactionItem.getQuantity());
				inventory.setProductCostPrice(transactionItem.getCostPrice());
				inventory.setInventoryDate(new Date());
				inventory.setStatus(Constant.INVENTORY_STATUS_SALE);
				inventory.setUploadStatus(Constant.STATUS_YES);
				inventory.setCreateBy(UserUtil.getUser().getUserId());
				inventory.setCreateDate(new Date());
				inventory.setUpdateBy(UserUtil.getUser().getUserId());
				inventory.setUpdateDate(new Date());
				
				if (transactionItem.getQuantity() != 0) {
					mInventoryDaoService.addInventory(inventory);
				}
			}
		}
		
		// clear orders
		if (mSelectedOrders != null) {
			
			for (Long orderId : mSelectedOrders.keySet()) {
				
				Orders order = mOrderDaoService.getOrders(orderId);
				
				mOrderDaoService.deleteOrders(order);
			}
			
			mSelectedOrders.clear();
		}
	}
	
	@Override
	public void onOrderConfirmed(Orders order) {
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		
		for (TransactionItem transactionItem : mTransactionItems) {
			
			OrderItem orderItem = new OrderItem();
			
			orderItem.setOrderNo(order.getOrderNo());
			
			orderItem.setMerchantId(MerchantUtil.getMerchantId());
			orderItem.setProductId(transactionItem.getProductId());
			orderItem.setProductName(transactionItem.getProductName());
			orderItem.setQuantity(transactionItem.getQuantity());
			orderItem.setRemarks(transactionItem.getRemarks());
			
			orderItems.add(orderItem);
		}
		
		mOrder = order;
		mOrderItems = orderItems;
		
		order.setUploadStatus(Constant.STATUS_YES);
		mOrderDaoService.addOrders(order);
		
		for (OrderItem orderItem : mOrderItems) {
			
			orderItem.setOrderId(order.getId());
			orderItem.setUploadStatus(Constant.STATUS_YES);
			mOrderItemDaoService.addOrderItem(orderItem);
		}
		
		if (UserUtil.isWaitress()) {
			
			mIsSubmitOrder = true;
			
			if (mProgressDialog.isAdded()) {
				return;
			}
			
			mProgressDialog.show(getFragmentManager(), progressDialogTag);
			
			if (mHttpAsyncManager == null) {
				mHttpAsyncManager = new HttpAsyncManager(this);
			}
			
			mHttpAsyncManager.syncOrders();
		}
	}

	@Override
	public void onPrintReceipt(Transactions transaction) {
		
		transaction = mTransactionDaoService.getTransactions(transaction.getId());
		
		try {
			PrintUtil.printTransaction(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			showMessage(getString(R.string.printer_cant_print));
		}
	}
	
	@Override
	public void onPrintOrder(Orders order) {
		
		order = mOrderDaoService.getOrders(order.getId());
		
		try {
			PrintUtil.printOrder(order);
		} catch (Exception e) {
			e.printStackTrace();
			showMessage(getString(R.string.printer_cant_print));
		}
		
		String message = getString(R.string.confirm_print_customer_copy);
		ConfirmationUtil.confirmTask(getFragmentManager(), this, ConfirmationUtil.PRINT_ORDER, message);
	}
	
	@Override
	public void onSelectDiscount() {
		
		/*if (Constant.DISCOUNT_TYPE_AMOUNT.equals(MerchantUtil.getMerchant().getDiscountType())) {
			
			if (mDiscountNominalDlgFragment.isAdded()) {
				return;
			}
			
			mDiscountNominalDlgFragment.show(getFragmentManager(), mDiscountNominalDlgFragmentTag);
			mDiscountNominalDlgFragment.setDiscount(mDiscount);
			
		} else {
			
			if (mDiscountDlgFragment.isAdded()) {
				return;
			}
			
			mDiscountDlgFragment.show(getFragmentManager(), mDiscountDlgFragmentTag);
		}*/

		if (mDiscountDlgFragment.isAdded()) {
			return;
		}

		mDiscountDlgFragment.show(getFragmentManager(), mDiscountDlgFragmentTag);
	}
	
	@Override
	public void onDiscountSelected(Discount discount) {
		
		if (discount != null && Constant.DISCOUNT_TYPE_AMOUNT.equals(discount.getType())) {

			//mDiscountNominalDlgFragment.dismissAllowingStateLoss();

			if (discount.getAmount() == 0f) {

				if (mDiscountNominalDlgFragment.isAdded()) {
					return;
				}

				mDiscountNominalDlgFragment.show(getFragmentManager(), mDiscountNominalDlgFragmentTag);
				mDiscountNominalDlgFragment.setDiscount(mDiscount);

            } else {

                mDiscount = discount;

                mOrderFragment.setDiscount(discount);

                //transaction item discount is updated upon payment
                //updateTransactionItemsDiscount();

                mDiscountDlgFragment.dismissAllowingStateLoss();
            }

		} else {

			mDiscount = discount;

			mOrderFragment.setDiscount(discount);

            //transaction item discount is updated upon payment
			//updateTransactionItemsDiscount();

			mDiscountDlgFragment.dismissAllowingStateLoss();
		}
	}
	
	private void updateTransactionItemsDiscount() {
		
		int totalItem = 0;
		
		for (TransactionItem transactionItem : mTransactionItems) {

			if (!Constant.PRICE_TYPE_DYNAMIC.equals(transactionItem.getProduct().getPriceType())) {

                totalItem += transactionItem.getQuantity();
            }
		}
		
		for (TransactionItem transactionItem : mTransactionItems) {

            if (!Constant.PRICE_TYPE_DYNAMIC.equals(transactionItem.getProduct().getPriceType())) {

                float discountPercentage = 0;
                float discountNominal = 0;
                float discountAmount = 0;

                if (mDiscount != null) {

                    discountPercentage = mDiscount.getPercentage() != null ? mDiscount.getPercentage() : 0;
                    discountNominal = mDiscount.getAmount() != null ? mDiscount.getAmount() : 0;

                    if (discountPercentage != 0) {
                        discountAmount = discountPercentage * transactionItem.getPrice() / 100;
                    } else if (discountNominal != 0) {
                        discountAmount = (discountNominal / totalItem);
                    }
                }

                transactionItem.setDiscount(discountAmount);
            }
		}
	}
	
	public void onSelectEmployee() {
		
		if (mEmployeeDlgFragment.isAdded()) {
			return;
		}
		
		mEmployeeDlgFragment.show(getFragmentManager(), mEmployeeDlgFragmentTag);
	}
	
	public void onEmployeeSelected(Employee employee) {
		
		if (mProductCountDlgFragment.isAdded()) {
			mProductCountDlgFragment.setEmployee(employee);
		
		} else if (mOrderDlgFragment.isAdded()) {
			mOrderDlgFragment.setWaitress(employee);
		}
	}
	
	public void onSelectCustomer() {
		
		if (mCustomerDlgFragment.isAdded()) {
			return;
		}
		
		mCustomerDlgFragment.show(getFragmentManager(), mCustomerDlgFragmentTag);
	}
	
	public void onCustomerSelected(Customer customer) {
		
		if (mPaymentDlgFragment.isAdded()) {
			mPaymentDlgFragment.setCustomer(customer);
		
		} else if (mOrderDlgFragment.isAdded()) {
			mOrderDlgFragment.setCustomer(customer);
		}
	}
	
	@Override
	public void onConfirm(String task) {
		
		if (ConfirmationUtil.PRINT_ORDER.equals(task) &&
			PrintUtil.isPrinterActive()) {
			
			try {
				PrintUtil.printOrder(mOrder);
			} catch (Exception e) {
				e.printStackTrace();
				showMessage(getString(R.string.printer_cant_print));
			}
		
		} else if (ConfirmationUtil.CANCEL_TRANSACTION.equals(task)) {
			
			onClearTransaction();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		
		Bundle extras = intent.getExtras();
		
		processExtras(extras);
	}
	
	@SuppressWarnings("unchecked")
	private void processExtras(Bundle extras) {
		
		if (extras != null) {
			
			clearTransaction();
			
			mSelectedOrders = (HashMap<Long, Boolean>) extras.getSerializable(Constant.SELECTED_ORDERS_FOR_PAYMENT);
			mSelectedOrder = (Orders) extras.getSerializable(Constant.SELECTED_ORDERS_FOR_NEW_ITEM);
			
			if (mSelectedOrders != null) {
				
				mState = Constant.CASHIER_STATE_ORDER_PAYMENT;
				
				for (Long orderId : mSelectedOrders.keySet()) {
					
					boolean isSelected = mSelectedOrders.get(orderId);
					
					if (isSelected) {
						
						Orders order = mOrderDaoService.getOrders(orderId);
						
						mOrderFragment.setSelectedOrders(order, false);
						
						mWaitress = order.getWaitressId() != null ? order.getEmployee() : null;
						
						mOrderType = order.getOrderType();
						mOrderReference = order.getOrderReference();
						
						mCustomer = order.getCustomerId() == null ? new Customer() : order.getCustomer();
						mCustomer.setName(order.getCustomerName());
						
						List<OrderItem> orderItems = mOrderItemDaoService.getOrderItemsByOrderId(orderId);
						
						for (OrderItem orderItem : orderItems) {
							
							Product product =  mProductDaoService.getProduct(orderItem.getProductId());
							TransactionItem transItem = getTransactionItem(product, CommonUtil.getCurrentPrice(product), null, orderItem.getQuantity(), orderItem.getRemarks());
							
							mOrderFragment.addTransactionItem(transItem, true);
						}
					}
				}
			
			} else if (mSelectedOrder != null) {
				
				mState = Constant.CASHIER_STATE_ORDER_NEW_ITEM;
				
				mCustomer = mSelectedOrder.getCustomerId() == null ? new Customer() : mSelectedOrder.getCustomer();
				mCustomer.setName(mSelectedOrder.getCustomerName());
				
				mOrderFragment.setSelectedOrders(mSelectedOrder, true);
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		if (mIsSubmitOrder) {
			
			mIsSubmitOrder = false;
			
			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.order_submit_ok));
		}
		
		onClearTransaction();
	}
	
	// Printer - Begin
	
	private void connectToPrinter(String printerType, String address) {
		
		PrintUtil.setPrinterLineSize(MerchantUtil.getMerchant().getPrinterLineSize());
		
		PrintUtil.connectToBluetoothPrinter(address);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		switch (requestCode) {

		case PrintUtil.REQUEST_CONNECT_DEVICE:

			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				
				// Get the device Printer Type
				String printerType = intent.getExtras().getString(CashierPrinterListActivity.EXTRA_PRINTER_TYPE);
				
				// Get the device MAC address
				String address = intent.getExtras().getString(CashierPrinterListActivity.EXTRA_DEVICE_ADDRESS);
				
				Merchant merchant = MerchantUtil.getMerchant();
				merchant.setPrinterType(printerType);
				merchant.setPrinterAddress(address);
				
				MerchantDaoService merchantDaoService = new MerchantDaoService();
				merchantDaoService.updateMerchant(merchant);
				
				connectToPrinter(printerType, address);
			}
			
			break;
			
		case PrintUtil.REQUEST_ENABLE_BT:

			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				
				setMessage(getString(R.string.printer_connecting));
				
				// Bluetooth is now enabled, so set up a chat session
				PrintUtil.selectBluetoothPrinter();
				
				setSelectPrinterVisible(true);
				
			} else {

				// User did not enable Bluetooth or an error occured
				showMessage(getString(R.string.printer_bluetooth_inactive));
			}
			
			break;
		}
	}

	// printer - end
}
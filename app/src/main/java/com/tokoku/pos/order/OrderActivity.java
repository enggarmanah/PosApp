package com.tokoku.pos.order;

import java.io.Serializable;
import java.util.HashMap;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.tokoku.pos.Constant;
import com.tokoku.pos.async.HttpAsyncManager;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.cashier.CashierActivity;
import com.tokoku.pos.cashier.CashierProductCountDlgFragment;
import com.tokoku.pos.dao.OrderItemDaoService;
import com.tokoku.pos.popup.search.EmployeeDlgFragment;
import com.tokoku.pos.popup.search.EmployeeSelectionListener;
import com.tokoku.pos.util.CommonUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OrderActivity extends BaseActivity 
	implements OrderActionListener, CashierProductCountDlgFragment.ProductActionListener, EmployeeSelectionListener {
	
	protected OrderListFragment mOrderListFragment;
	protected OrderDetailFragment mOrderDetailFragment;
	protected CashierProductCountDlgFragment mProductCountDlgFragment;
	protected EmployeeDlgFragment mEmployeeDlgFragment;
	
	private String mOrderListFragmentTag = "OrderListFragmentTag";
	private String mOrderDetailFragmentTag = "OrderDetailFragmentTag";
	private String mProductCountDlgFragmentTag = "productCountDlgFragmentTag";
	private static final  String mEmployeeDlgFragmentTag = "employeeDlgFragmentTag";
	
	private static final String SELECTED_ORDER_REFERENCE = "SELECTED_ORDER_REFERENCE";
	private static final String SELECTED_ORDER_ITEM = "SELECTED_ORDER_ITEM";
	
	boolean mIsMultiplesPane = false;
	
	private String mSelectedOrderReference;
	private OrderItem mSelectedOrderItem;
	
	private MenuItem mMenuPayment;
	private MenuItem mMenuSync;
	
	@SuppressLint("UseSparseArrays")
	public HashMap<Long, Boolean> mSelectedOrders = new HashMap<Long, Boolean>();
	
	public boolean mIsOrderSelected = false;
	
	OrderItemDaoService mOrderItemDaoService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_transaction_activity);

		initDrawerMenu();
		
		initFragments();
		
		mOrderItemDaoService = new OrderItemDaoService();
		
		initWaitAfterFragmentRemovedTask(mOrderListFragmentTag, mOrderDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_order));
		setSelectedMenu(getString(R.string.menu_order));
		
		mIsOrderSelected = false;
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedOrderReference = (String) savedInstanceState.getSerializable(SELECTED_ORDER_REFERENCE);
			mSelectedOrderItem = (OrderItem) savedInstanceState.getSerializable(SELECTED_ORDER_ITEM);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (mProductCountDlgFragment == null) {
			mProductCountDlgFragment = new CashierProductCountDlgFragment();
		}

		mOrderListFragment = (OrderListFragment) getFragmentManager().findFragmentByTag(mOrderListFragmentTag);
		
		if (mOrderListFragment == null) {
			mOrderListFragment = new OrderListFragment();

		} else {
			removeFragment(mOrderListFragment);
		}
		
		mOrderDetailFragment = (OrderDetailFragment) getFragmentManager().findFragmentByTag(mOrderDetailFragmentTag);
		
		if (mOrderDetailFragment == null) {
			mOrderDetailFragment = new OrderDetailFragment();

		} else {
			removeFragment(mOrderDetailFragment);
		}
		
		mOrderDetailFragment.setSelectedOrders(mSelectedOrders);
		
		if (mEmployeeDlgFragment == null) {
			mEmployeeDlgFragment = new EmployeeDlgFragment();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_ORDER_REFERENCE, (Serializable) mSelectedOrderReference);
		outState.putSerializable(SELECTED_ORDER_ITEM, (Serializable) mSelectedOrderItem);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mOrderListFragment.setSelectedOrderReference(mSelectedOrderReference);
		mOrderDetailFragment.displayOrders(mSelectedOrderReference);
		
		if (mIsMultiplesPane) {

			addFragment(mOrderListFragment, mOrderListFragmentTag);
			addFragment(mOrderDetailFragment, mOrderDetailFragmentTag);
			
		} else {

			if (mSelectedOrderReference != null) {
				
				addFragment(mOrderDetailFragment, mOrderDetailFragmentTag);
				
			} else {
				
				addFragment(mOrderListFragment, mOrderListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.order_menu, menu);
		
		mMenuPayment = menu.findItem(R.id.menu_payment);
		mMenuSync = menu.findItem(R.id.menu_item_sync);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		displayMenus();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void displayMenus() {
		
		mMenuPayment.setVisible(false);
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		if (!isDrawerOpen) {
		
			if (mSelectedOrderReference != null) {
				mMenuPayment.setVisible(true);
			}	
		}
		
		mMenuSync.setVisible(!isDrawerOpen);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
	
			case R.id.menu_payment:
				
				// make payment
				
				displayMenus();
				
				mIsOrderSelected = true;
				
				refreshOrders();
				
				Intent intent = new Intent(getApplicationContext(), CashierActivity.class);
				intent.putExtra(Constant.SELECTED_ORDERS_FOR_PAYMENT, mSelectedOrders);
				startActivity(intent);
				
				return true;
				
			case R.id.menu_item_sync:
				
				if (mProgressDialog.isAdded()) {
					return true;
				}
				
				mProgressDialog.show(getFragmentManager(), progressDialogTag);
				
				if (mHttpAsyncManager == null) {
					mHttpAsyncManager = new HttpAsyncManager(this);
				}
				
				mHttpAsyncManager.syncOrders();
				
				return true;
					
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onOrderReferenceSelected(String orderReference) {
		
		mSelectedOrderReference = orderReference;
		
		displayMenus();
		
		mOrderDetailFragment.displayOrders(orderReference);
		
		if (mIsMultiplesPane) {
			
			mOrderListFragment.setSelectedOrderReference(orderReference);
			
		} else {
			
			replaceFragment(mOrderDetailFragment, mOrderDetailFragmentTag);
		}
	}
	
	private void refreshOrders() {
		
		mSelectedOrderReference = null;
		
		displayMenus();
		
		if (!mIsMultiplesPane) {
			
			mOrderListFragment.setSelectedOrderReference(mSelectedOrderReference);
			replaceFragment(mOrderListFragment, mOrderListFragmentTag);
		
		} else {
			
			mOrderListFragment.setSelectedOrderReference(mSelectedOrderReference);
			mOrderDetailFragment.displayOrders(mSelectedOrderReference);
		}
	}
	
	@Override
	public void onBackButtonClicked() {
		
		refreshOrders();
	}
	
	@Override
	public void onOrderItemSelected(OrderItem orderItem) {
		
		if (mProductCountDlgFragment.isAdded()) {
			return;
		}
		
		mSelectedOrderItem = orderItem;
		
		Product product = orderItem.getProduct();
		Float price = CommonUtil.getCurrentPrice(orderItem.getProduct());
		Employee personInCharge = orderItem.getEmployee();
		Float quantity = orderItem.getQuantity();
		String remarks = orderItem.getRemarks();
		
		mProductCountDlgFragment.show(getFragmentManager(), mProductCountDlgFragmentTag);
		mProductCountDlgFragment.setProduct(product, price, personInCharge, quantity, remarks);
	}
	
	@Override
	public void onProductQuantitySelected(Product product, Float price, Employee personInCharge, Float quantity, String remarks) {
		
		mSelectedOrderItem.setEmployee(personInCharge);
		mSelectedOrderItem.setQuantity(quantity);
		mSelectedOrderItem.setRemarks(remarks);
		mSelectedOrderItem.setUploadStatus(Constant.STATUS_YES);
		mOrderItemDaoService.updateOrderItem(mSelectedOrderItem);
		mOrderDetailFragment.displayOrders(mSelectedOrderReference);
	}
	
	@Override
	public void onAddNewOrder(Orders order) {
		
		Intent intent = new Intent(getApplicationContext(), CashierActivity.class);
		intent.putExtra(Constant.SELECTED_ORDERS_FOR_NEW_ITEM, order);
		startActivity(intent);
	}
	
	public void onSelectEmployee() {
		
		if (mEmployeeDlgFragment.isAdded()) {
			return;
		}
		
		mEmployeeDlgFragment.show(getFragmentManager(), mEmployeeDlgFragmentTag);
	}
	
	public void onEmployeeSelected(Employee employee) {
		
		mProductCountDlgFragment.setEmployee(employee);
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		refreshOrders();
		
		//NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.order_download_ok));
	}
}
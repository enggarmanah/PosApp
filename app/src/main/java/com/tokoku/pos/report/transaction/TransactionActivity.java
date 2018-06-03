package com.tokoku.pos.report.transaction;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.common.ProgressDlgFragment;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.PrintUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TransactionActivity extends BaseActivity 
	implements TransactionActionListener {
	
	protected TransactionListFragment mTransactionListFragment;
	protected TransactionDetailFragment mTransactionDetailFragment;
	protected TransactionDeleteDlgFragment mTransactionDeleteFragment;

	protected static ProgressDlgFragment mProgressDialog;

	protected static String mGenerateReportProgressDialogTag = "generateReportProgressDialogTag";
	
	boolean mIsMultiplesPane = false;
	
	private TransactionYearBean mSelectedTransactionYear;
	private TransactionMonthBean mSelectedTransactionMonth;
	private TransactionDayBean mSelectedTransactionDay;
	private Transactions mSelectedTransaction;
	
	private static String SELECTED_TRANSACTION_YEAR = "SELECTED_TRANSACTION_YEAR";
	private static String SELECTED_TRANSACTION_MONTH = "SELECTED_TRANSACTION_MONTH";
	private static String SELECTED_TRANSACTION_DAY = "SELECTED_TRANSACTION_DAY";
	private static String SELECTED_TRANSACTION = "SELECTED_TRANSACTION";
	
	private String mTransactionListFragmentTag = "transactionListFragmentTag";
	private String mTransactionDetailFragmentTag = "transactionDetailFragmentTag";
	private String mTransactionDeleteFragmentTag = "transactionDeleteFragmentTag";
	
	private boolean mIsDisplayTransactionAllYears = false;
	private boolean mIsDisplayTransactionYear = false;
	private boolean mIsDisplayTransactionMonth = false;
	private boolean mIsDisplayTransactionDay = false;
	private boolean mIsDisplayTransaction = false;

	private MenuItem mEmailItem;
	private MenuItem mPrintItem;
	private MenuItem mDeleteItem;
	private MenuItem mUpItem;
	
	private TransactionsDaoService mTransactionsDaoService;
	private InventoryDaoService mInventoryDaoService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_transaction_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mTransactionListFragmentTag, mTransactionDetailFragmentTag);

		mTransactionsDaoService = new TransactionsDaoService();
		mInventoryDaoService = new InventoryDaoService();

		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(mGenerateReportProgressDialogTag);

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_report_transaction));
		setSelectedMenu(getString(R.string.menu_report_transaction));
		
		// handle demo option
		
		if (CommonUtil.isDemo()) {
			
			selectItem(mSyncIndex);
			CommonUtil.setDemo(false);
		}
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedTransactionYear = (TransactionYearBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_YEAR);
			mSelectedTransactionMonth = (TransactionMonthBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_MONTH);
			mSelectedTransactionDay = (TransactionDayBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_DAY);
			mSelectedTransaction = (Transactions) savedInstanceState.getSerializable(SELECTED_TRANSACTION);
			
			if (mSelectedTransaction != null) {
				mIsDisplayTransaction = true;
			} else if (mSelectedTransactionDay != null) {
				mIsDisplayTransactionDay = true;
			} else if (mSelectedTransactionMonth != null) {
				mIsDisplayTransactionMonth = true;
			} else if (mSelectedTransactionYear != null) {
				mIsDisplayTransactionYear = true;
			} else {
				mIsDisplayTransactionAllYears = true;
			}
			
		} else {
			
			mIsDisplayTransactionMonth = true;
			
			mSelectedTransactionMonth = new TransactionMonthBean();
			mSelectedTransactionMonth.setMonth(CommonUtil.getCurrentMonth());
				
			mSelectedTransactionYear = new TransactionYearBean();
			mSelectedTransactionYear.setYear(CommonUtil.getCurrentYear());
		} 
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mTransactionListFragment = (TransactionListFragment) getFragmentManager().findFragmentByTag(mTransactionListFragmentTag);
		
		if (mTransactionListFragment == null) {
			mTransactionListFragment = new TransactionListFragment();

		} else {
			removeFragment(mTransactionListFragment);
		}
		
		mTransactionDetailFragment = (TransactionDetailFragment) getFragmentManager().findFragmentByTag(mTransactionDetailFragmentTag);
		
		if (mTransactionDetailFragment == null) {
			mTransactionDetailFragment = new TransactionDetailFragment();

		} else {
			removeFragment(mTransactionDetailFragment);
		}
		
		mTransactionDeleteFragment = (TransactionDeleteDlgFragment) getFragmentManager().findFragmentByTag(mTransactionDeleteFragmentTag);
		
		if (mTransactionDeleteFragment == null) {
			mTransactionDeleteFragment = new TransactionDeleteDlgFragment();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_TRANSACTION_YEAR, (Serializable) mSelectedTransactionYear);
		outState.putSerializable(SELECTED_TRANSACTION_MONTH, (Serializable) mSelectedTransactionMonth);
		outState.putSerializable(SELECTED_TRANSACTION_DAY, (Serializable) mSelectedTransactionDay);
		outState.putSerializable(SELECTED_TRANSACTION, (Serializable) mSelectedTransaction);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mTransactionListFragment.setSelectedTransactionYear(mSelectedTransactionYear);
		mTransactionListFragment.setSelectedTransactionMonth(mSelectedTransactionMonth);
		mTransactionListFragment.setSelectedTransactionDay(mSelectedTransactionDay);
		mTransactionListFragment.setSelectedTransaction(mSelectedTransaction);

		if (mIsMultiplesPane) {

			addFragment(mTransactionListFragment, mTransactionListFragmentTag);
			addFragment(mTransactionDetailFragment, mTransactionDetailFragmentTag);
			
			mTransactionDetailFragment.setTransaction(mSelectedTransaction);
			
		} else {

			if (mSelectedTransaction != null) {
				
				addFragment(mTransactionDetailFragment, mTransactionDetailFragmentTag);
				mTransactionDetailFragment.setTransaction(mSelectedTransaction);
				
			} else {
				
				addFragment(mTransactionListFragment, mTransactionListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_transaction_menu, menu);

		mEmailItem = menu.findItem(R.id.menu_item_email);
		mPrintItem = menu.findItem(R.id.menu_item_print);
		mDeleteItem = menu.findItem(R.id.menu_item_delete);
		mUpItem = menu.findItem(R.id.menu_item_up);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		initMenus();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.menu_item_email:

                if (mIsDisplayTransactionYear || mIsDisplayTransactionMonth || mIsDisplayTransactionDay) {
                    mTransactionListFragment.generateReport(getString(R.string.menu_report_transaction));

                } else if (mIsDisplayTransaction) {

                    try {
                        PrintUtil.initPdf(this);
                        Uri uri = PrintUtil.printPdf(mSelectedTransaction);

                        startActivityForResult(CommonUtil.getActionSendIntent(new File(uri.getPath()), getString(R.string.receipt)), 1);

                        /*Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(uri, "application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

				return true;

			case R.id.menu_item_print:
				
				if (PrintUtil.isPrinterConnected()) {

				    try {
						PrintUtil.printTransaction(mSelectedTransaction);

					} catch (Exception e) {
						e.printStackTrace();
						showMessage(getString(R.string.printer_cant_print));
					}
				} else {
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.printer_please_check_printer));
				}
				
				return true;
				
			case R.id.menu_item_delete:
				
				if (MerchantUtil.getMerchantId() == Constant.DEMO_MERCHANT_ID) {
					
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_demo_account));
					return true;
				}
				
				if (mTransactionDeleteFragment.isAdded()) {
					return true;
				}
				
				mTransactionDeleteFragment.show(getFragmentManager(), mTransactionDeleteFragmentTag);
				mTransactionDeleteFragment.setTransactionToBeDeleted(mSelectedTransaction, mSelectedTransaction.getTransactionNo());
				
				return true;
		
			case R.id.menu_item_up:
				
				onBackToParent();
				
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void hideAllMenus() {

		mEmailItem.setVisible(false);
		mPrintItem.setVisible(false);
		mDeleteItem.setVisible(false);
		mUpItem.setVisible(false);
	}
	
	@Override
	public void onTransactionYearSelected(TransactionYearBean transactionYear) {
		
		mSelectedTransactionYear = transactionYear;
		
		resetDisplayStatus();
		mIsDisplayTransactionYear = true;
		
		initMenus();
	}
	
	@Override
	public void onTransactionMonthSelected(TransactionMonthBean transactionMonth) {
		
		mSelectedTransactionMonth = transactionMonth;
		
		resetDisplayStatus();
		mIsDisplayTransactionMonth = true;
		
		initMenus();
	}
	
	@Override
	public void onTransactionDaySelected(TransactionDayBean transactionDay) {
		
		mSelectedTransactionDay = transactionDay;
		
		resetDisplayStatus();
		mIsDisplayTransactionDay = true;
		
		initMenus();
		
		mTransactionListFragment.setSelectedTransactionDay(mSelectedTransactionDay);
	}
	
	@Override
	public void onTransactionSelected(Transactions transaction) {
		
		mSelectedTransaction = transaction;
		
		resetDisplayStatus();
		mIsDisplayTransaction = true;
		
		if (mIsMultiplesPane) {

			mTransactionDetailFragment.setTransaction(transaction);
			
		} else {

			replaceFragment(mTransactionDetailFragment, mTransactionDetailFragmentTag);
			mTransactionDetailFragment.setTransaction(transaction);
		}
		
		initMenus();
	}
	
	@Override
	public void onTransactionDeleted(Transactions transaction) {
		
		mTransactionsDaoService.deleteTransactions(transaction);
		
		List<Inventory> inventories = mInventoryDaoService.getInventories(transaction.getTransactionNo());
		
		for (Inventory inventory : inventories) {
			inventory.setStatus(Constant.STATUS_DELETED);
			inventory.setUploadStatus(Constant.STATUS_YES);
			mInventoryDaoService.updateInventory(inventory);
		}
		
		onBackToParent();
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			onBackToParent();
		}
	}
	
	private void onBackToParent() {
		
		setDisplayStatusToParent();
		
		if (mIsMultiplesPane) {
			
			initFragment();
			
			mTransactionDetailFragment.setTransaction(null);
			
		} else {
			
			replaceFragment(mTransactionListFragment, mTransactionListFragmentTag);
			
			initFragment();
		}
		
		initMenus();
	}
	
	private void initFragment() {
		
		if (mIsDisplayTransactionAllYears) {
			mTransactionListFragment.displayTransactionAllYears();
			
		} else if (mIsDisplayTransactionYear) {
			mTransactionListFragment.displayTransactionOnYear(mSelectedTransactionYear);
			
		} else if (mIsDisplayTransactionMonth) {
			mTransactionListFragment.displayTransactionOnMonth(mSelectedTransactionMonth);
			
		} else if (mIsDisplayTransactionDay) {
			mTransactionListFragment.displayTransactionOnDay(mSelectedTransactionDay);
		
		} else if (mIsDisplayTransaction) {
			mUpItem.setVisible(true);
			
			if (Constant.USER_ROLE_CASHIER.equals(UserUtil.getUser().getRole())) {
				mPrintItem.setVisible(true);
			} else if (Constant.USER_ROLE_ADMIN.equals(UserUtil.getUser().getRole())) {
				mDeleteItem.setVisible(true);
			}
		}
	}
	
	private void initMenus() {
		
		if (mUpItem == null) {
			return;
		}
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		if (isDrawerOpen) {
			
			hideAllMenus();
		
		} else {
			
			hideAllMenus();

			if (mIsDisplayTransactionAllYears) {

				if (MerchantUtil.hasActiveCloud()) {
					mEmailItem.setVisible(true);
				}

			} else if (mIsDisplayTransactionYear || mIsDisplayTransactionMonth || mIsDisplayTransactionDay) {

				if (MerchantUtil.hasActiveCloud()) {
					mEmailItem.setVisible(true);
				}

				mUpItem.setVisible(true);
				
			} else if (mIsDisplayTransaction) {
				
				mUpItem.setVisible(false);
                mEmailItem.setVisible(true);
				mPrintItem.setVisible(true);
				
				if (Constant.USER_ROLE_ADMIN.equals(UserUtil.getUser().getRole())) {
					mDeleteItem.setVisible(true);
				}
			}
		}
	}
	
	private void resetDisplayStatus() {
		
		mIsDisplayTransactionAllYears = false;
		mIsDisplayTransactionYear = false;
		mIsDisplayTransactionMonth = false;
		mIsDisplayTransactionDay = false;
		mIsDisplayTransaction = false;
	}
	
	private void setDisplayStatusToParent() {
		
		if (mIsDisplayTransactionYear) {
			
			mIsDisplayTransactionYear = false;
			mSelectedTransactionYear = null;
			
			mIsDisplayTransactionAllYears = true;
			
		} else if (mIsDisplayTransactionMonth) {
			
			mIsDisplayTransactionMonth = false;
			mSelectedTransactionMonth = null;
			
			mIsDisplayTransactionYear = true;
			
		} else if (mIsDisplayTransactionDay) {
			
			mIsDisplayTransactionDay = false;
			mSelectedTransactionDay = null;
			
			mIsDisplayTransactionMonth = true;
		
		} else if (mIsDisplayTransaction) {
					
			mIsDisplayTransaction = false;
			mSelectedTransaction = null;
			
			if (mIsMultiplesPane) {
				
				/*mIsDisplayTransactionDay = false;
				mSelectedTransactionDay = null;
				
				mIsDisplayTransactionMonth = true;*/
				
				mIsDisplayTransactionDay = true;
				
			} else {
				mIsDisplayTransactionDay = true;
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mTransactionListFragment.updateContent();
		mTransactionDetailFragment.updateContent();
	}

	@Override
	public void onGenerateReportStart() {

		mProgressDialog.show(getFragmentManager(), mGenerateReportProgressDialogTag);
		mProgressDialog.setMessage(getString(R.string.msg_generate_report));
	}

	@Override
	public void onGenerateReportCompleted() {

		mProgressDialog.dismissAllowingStateLoss();
	}
}
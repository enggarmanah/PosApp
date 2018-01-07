package com.tokoku.pos.report.commission;

import java.io.Serializable;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.User;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.dao.EmployeeDaoService;
import com.tokoku.pos.model.CommissionMonthBean;
import com.tokoku.pos.model.CommissionYearBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommissionActivity extends BaseActivity 
	implements CommissionActionListener {
	
	protected CommissionListFragment mCommissionListFragment;
	protected CommissionDetailFragment mCommissionDetailFragment;
	
	boolean mIsMultiplesPane = false;

    protected static String mGenerateReportProgressDialogTag = "generateReportProgressDialogTag";

	private MenuItem mMenuEmail;
	
	private CommissionYearBean mSelectedCommissionYear;
	private CommissionMonthBean mSelectedCommissionMonth;
	
	private Employee mSelectedEmployee;
	private Employee mUserEmployee;
	
	private static String SELECTED_TRANSACTION_YEAR = "SELECTED_TRANSACTION_YEAR";
	private static String SELECTED_TRANSACTION_MONTH = "SELECTED_TRANSACTION_MONTH";
	private static String SELECTED_EMPLOYEE = "SELECTED_EMPLOYEE";
	
	public static final String DISPLAY_TRANSACTION_ALL_YEARS = "DISPLAY_TRANSACTION_ALL_YEARS";
	public static final String DISPLAY_TRANSACTION_ON_YEAR = "DISPLAY_TRANSACTION_ON_YEAR";
	public static final String DISPLAY_TRANSACTION_ON_MONTH = "DISPLAY_TRANSACTION_ON_MONTH";
	
	private String mProductStatisticListFragmentTag = "productStatisticListFragmentTag";
	private String mProductStatisticDetailFragmentTag = "productStatisticDetailFragmentTag";
	
	private boolean mIsDisplayCommissionAllYears = false;
	private boolean mIsDisplayCommissionYear = false;
	private boolean mIsDisplayCommissionMonth = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_commision_activity);

		initDrawerMenu();
		
		initFragments();
		
		initWaitAfterFragmentRemovedTask(mProductStatisticListFragmentTag, mProductStatisticDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_report_commision));
		setSelectedMenu(getString(R.string.menu_report_commision));
		
		User user = UserUtil.getUser();
		
		if (!Constant.USER_ROLE_ADMIN.equals(user.getRole()) && user.getEmployeeId() != null) {
			
			EmployeeDaoService employeeDaoService = new EmployeeDaoService();
			mUserEmployee = employeeDaoService.getEmployee(user.getEmployeeId());
			mSelectedEmployee = mUserEmployee;
			
			mCommissionDetailFragment.setEmployee(mSelectedEmployee);
		}
		
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedCommissionYear = (CommissionYearBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_YEAR);
			mSelectedCommissionMonth = (CommissionMonthBean) savedInstanceState.getSerializable(SELECTED_TRANSACTION_MONTH);
			
			mSelectedEmployee = (Employee) savedInstanceState.getSerializable(SELECTED_EMPLOYEE);
			
			mIsDisplayCommissionAllYears = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ALL_YEARS);
			mIsDisplayCommissionYear = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ON_YEAR);
			mIsDisplayCommissionMonth = (Boolean) savedInstanceState.getSerializable(DISPLAY_TRANSACTION_ON_MONTH);
		
		} else {
			
			mIsDisplayCommissionMonth = true;
			
			mSelectedCommissionMonth = new CommissionMonthBean();
			mSelectedCommissionMonth.setMonth(CommonUtil.getCurrentMonth());
			
			mSelectedCommissionYear = new CommissionYearBean();
			mSelectedCommissionYear.setYear(CommonUtil.getCurrentYear());
		} 
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mCommissionListFragment = (CommissionListFragment) getFragmentManager().findFragmentByTag(mProductStatisticListFragmentTag);
		
		if (mCommissionListFragment == null) {
			mCommissionListFragment = new CommissionListFragment();

		} else {
			removeFragment(mCommissionListFragment);
		}
		
		mCommissionDetailFragment = (CommissionDetailFragment) getFragmentManager().findFragmentByTag(mProductStatisticDetailFragmentTag);
		
		if (mCommissionDetailFragment == null) {
			mCommissionDetailFragment = new CommissionDetailFragment();

		} else {
			removeFragment(mCommissionDetailFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_TRANSACTION_YEAR, (Serializable) mSelectedCommissionYear);
		outState.putSerializable(SELECTED_TRANSACTION_MONTH, (Serializable) mSelectedCommissionMonth);
		
		outState.putSerializable(SELECTED_EMPLOYEE, (Serializable) mSelectedEmployee);
		
		outState.putSerializable(DISPLAY_TRANSACTION_ALL_YEARS, (Serializable) mIsDisplayCommissionAllYears);
		outState.putSerializable(DISPLAY_TRANSACTION_ON_YEAR, (Serializable) mIsDisplayCommissionYear);
		outState.putSerializable(DISPLAY_TRANSACTION_ON_MONTH, (Serializable) mIsDisplayCommissionMonth);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mCommissionListFragment.setSelectedEmployee(mUserEmployee);
		mCommissionListFragment.setSelectedCommisionYear(mSelectedCommissionYear);
		mCommissionListFragment.setSelectedCommisionMonth(mSelectedCommissionMonth);
		
		mCommissionDetailFragment.setCommisionMonth(mSelectedCommissionMonth);
		mCommissionDetailFragment.setEmployee(mSelectedEmployee);
		
		if (mIsMultiplesPane) {

			addFragment(mCommissionListFragment, mProductStatisticListFragmentTag);
			addFragment(mCommissionDetailFragment, mProductStatisticDetailFragmentTag);
			
		} else {

			if (mSelectedCommissionMonth != null) {
				
				addFragment(mCommissionDetailFragment, mProductStatisticDetailFragmentTag);
				
			} else {
				
				addFragment(mCommissionListFragment, mProductStatisticListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_cashflow_menu, menu);

		mMenuEmail = menu.findItem(R.id.menu_item_email);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	private void initMenus() {

		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		mMenuEmail.setVisible(false);

		if (!isDrawerOpen) {

			if (MerchantUtil.hasActiveCloud()) {
				mMenuEmail.setVisible(mIsDisplayCommissionAllYears || mIsDisplayCommissionYear || mIsDisplayCommissionMonth);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		synchronized (CommonUtil.LOCK) {
		
			switch (item.getItemId()) {

				case R.id.menu_item_email:

				    if (mCommissionListFragment.isAdded()) {
                        mCommissionListFragment.generateReport(getString(R.string.menu_report_commision));
                    } else {
                        mCommissionDetailFragment.generateReport(getString(R.string.menu_report_commision));
                    }

					return true;
	
				default:
					return super.onOptionsItemSelected(item);
			}
		}
	}
	
	@Override
	public void onCommissionYearSelected(CommissionYearBean commissionYear) {
		
		mSelectedCommissionYear = commissionYear;
		
		resetDisplayStatus();
		mIsDisplayCommissionYear = true;
		
		mCommissionListFragment.setSelectedEmployee(mUserEmployee);
		mCommissionListFragment.setSelectedCommisionYear(commissionYear);

		initMenus();
	}
	
	@Override
	public void onCommissionMonthSelected(CommissionMonthBean commissionMonth) {
		
		mSelectedCommissionMonth = commissionMonth;
		
		resetDisplayStatus();
		mIsDisplayCommissionMonth = true;
		
		if (mIsMultiplesPane) {
			
			mCommissionDetailFragment.setCommisionMonth(commissionMonth);
			
			if (mUserEmployee != null) {
				mCommissionDetailFragment.setEmployee(mUserEmployee);
			}
		} else {

			replaceFragment(mCommissionDetailFragment, mProductStatisticDetailFragmentTag);
			
			mCommissionDetailFragment.setCommisionMonth(commissionMonth);
			
			if (mUserEmployee != null) {
				mCommissionDetailFragment.setEmployee(mUserEmployee);
			}
		}

		initMenus();
	}
	
	@Override
	public void onEmployeeSelected(Employee employee) {
		
		mSelectedEmployee = employee;
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			onBackToParent();
		}
	}
	
	private void onBackToParent() {
		
		if (mSelectedEmployee != null && mUserEmployee == null) {
			
			mSelectedEmployee = null;
			mCommissionDetailFragment.setCommisionMonth(mSelectedCommissionMonth);
			
			return;
		}
		
		setDisplayStatusToParent();
		
		mCommissionListFragment.setSelectedEmployee(mUserEmployee);
		mCommissionListFragment.setSelectedCommisionYear(mSelectedCommissionYear);
		mCommissionListFragment.setSelectedCommisionMonth(mSelectedCommissionMonth);
		
		mCommissionDetailFragment.setCommisionMonth(mSelectedCommissionMonth);
		
		if (mIsMultiplesPane) {
			
			initFragment();
			
		} else {
			
			replaceFragment(mCommissionListFragment, mProductStatisticListFragmentTag);
			initFragment();
		}
	}
	
	private void initFragment() {
		
		if (mIsDisplayCommissionAllYears) {
			
			mCommissionListFragment.displayCommisionAllYears();
			
		} else if (mIsDisplayCommissionYear) {
			
			mCommissionListFragment.displayCommisionOnYear(mSelectedCommissionYear);
		}
	}
	
	private void resetDisplayStatus() {
		
		mIsDisplayCommissionAllYears = false;
		mIsDisplayCommissionYear = false;
		mIsDisplayCommissionMonth = false;
	}
	
	private void setDisplayStatusToParent() {
		
		if (mIsDisplayCommissionYear) {
			
			mIsDisplayCommissionYear = false;
			mSelectedCommissionYear = null;
			
			mIsDisplayCommissionAllYears = true;
			
		} else if (mIsDisplayCommissionMonth) {
			
			mIsDisplayCommissionMonth = false;
			mSelectedCommissionMonth = null;
			
			if (mIsMultiplesPane) {
				mIsDisplayCommissionAllYears = true;
			} else {
				mIsDisplayCommissionYear = true;
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mCommissionListFragment.updateContent();
		mCommissionDetailFragment.updateContent();
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
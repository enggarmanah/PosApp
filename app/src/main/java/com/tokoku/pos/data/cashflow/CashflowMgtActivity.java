package com.tokoku.pos.data.cashflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseItemMgtActivity;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.popup.search.BillDlgFragment;
import com.tokoku.pos.popup.search.BillSelectionListener;
import com.tokoku.pos.popup.search.CreditDlgFragment;
import com.tokoku.pos.popup.search.CreditSelectionListener;
import com.tokoku.pos.util.BeanUtil;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CashflowMgtActivity extends BaseItemMgtActivity<CashflowSearchFragment, CashflowEditFragment, Cashflow> 
	implements CreditSelectionListener, BillSelectionListener {
	
	List<Cashflow> mInventories;
	Cashflow mSelectedCashflow;
	
	private CreditDlgFragment mTransactionDlgFragment;
	private BillDlgFragment mBillDlgFragment;
	
	private static String mTransactionDlgFragmentTag = "mTransactionDlgFragmentTag";
	private static String mBillDlgFragmentTag = "mBillDlgFragmentTag";
	
	private boolean mIsCreditPayment = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTransactionDlgFragment = (CreditDlgFragment) getFragmentManager().findFragmentByTag(mTransactionDlgFragmentTag);
		
		if (mTransactionDlgFragment == null) {
			mTransactionDlgFragment = new CreditDlgFragment();
		}
		
		mBillDlgFragment = (BillDlgFragment) getFragmentManager().findFragmentByTag(mBillDlgFragmentTag);
		
		if (mBillDlgFragment == null) {
			mBillDlgFragment = new BillDlgFragment();
		}
		
		processExtras(getIntent().getExtras());
	}
	
	@Override
	public void onStart() {
		
		super.onStart();

		setTitle(getString(R.string.menu_cashflow));
		setSelectedMenu(getString(R.string.menu_cashflow));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		
		Bundle extras = intent.getExtras();
		
		processExtras(extras);
	}
	
	@Override
	protected void afterPrepareOptionsMenu() {
		
		if (mIsCreditPayment) {
			mIsCreditPayment = false;
			
			updateItem(mSelectedCashflow);
			showEditMenu();
		}
	};
	
	@Override
	protected CashflowSearchFragment getSearchFragmentInstance() {
		
		return new CashflowSearchFragment();
	}

	@Override
	protected CashflowEditFragment getEditFragmentInstance() {
		
		return new CashflowEditFragment();
	}
	
	@Override
	protected void setSearchFragmentItems(List<Cashflow> inventories) {
		
		mSearchFragment.setItems(inventories);
	}
	
	@Override
	protected void setSearchFragmentSelectedItem(Cashflow cashflow) {
		
		mSearchFragment.setSelectedItem(cashflow);
	}
	
	@Override
	protected void setEditFragmentItem(Cashflow cashflow) {
		
		mEditFragment.setItem(cashflow);
	}
	
	@Override
	protected View getSearchFragmentView() {
		
		return mSearchFragment.getView();
	}

	@Override
	protected View getEditFragmentView() {
		
		return mEditFragment.getView();
	}
	
	@Override
	protected void enableEditFragmentInputFields(boolean isEnabled) {
		
		mEditFragment.enableInputFields(isEnabled);
	}

	@Override
	protected void doSearch(String query) {
		
		mSearchFragment.searchItem(query);
	}
	
	@Override
	protected Cashflow getItemInstance() {
		
		return new Cashflow();
	}
	
	@Override
	protected void unSelectItem() {
		
		mSearchFragment.unSelectItem();
	}
	
	@Override
	protected Cashflow updateEditFragmentItem(Cashflow cashflow) {
		
		return mEditFragment.updateItem(cashflow);
	}

	@Override
	protected void refreshEditView() {
		
		mEditFragment.refreshView();
	}
	
	@Override
	protected void addEditFragmentItem(Cashflow cashflow) {
	
		mEditFragment.addItem(cashflow);
	}
	
	@Override
	public void reloadItems() {
		
		mSearchFragment.reloadItems();
	}
	
	@Override
	protected Long getItemId(Cashflow cashflow) {
		
		return cashflow.getId();
	}
	
	@Override
	protected void refreshItem(Cashflow cashflow) {
		
		cashflow.refresh();
	}
	
	@Override
	protected void refreshSearchFragmentItems() {
		
		mSearchFragment.refreshItems();
	}
	
	@Override
	protected void saveItem() {
		
		mEditFragment.saveEditItem();
	}
	
	@Override
	protected void discardItem() {
		
		mEditFragment.discardEditItem();
	}
	
	@Override
	public void deleteItem(Cashflow item) {
		
		mSearchFragment.onItemDeleted(item);
	}
	
	@Override
	protected String getItemName(Cashflow item) {
		
		return CodeUtil.getCashflowTypeLabel(item.getType()) + " " + CommonUtil.formatDate(item.getCashDate()) + " " + CommonUtil.formatCurrency(item.getCashAmount());
	}
	
	@Override
	protected List<Cashflow> getItemsInstance() {
		
		return new ArrayList<Cashflow>();
	}
	
	public void onSelectTransaction(boolean isMandatory) {
		
		if (mTransactionDlgFragment.isAdded()) {
			return;
		}
		
		mTransactionDlgFragment.setMandatory(isMandatory);
		mTransactionDlgFragment.show(getFragmentManager(), mTransactionDlgFragmentTag);
	}
	
	public void onTransactionSelected(TransactionsBean transaction) {
		
		Transactions t = new Transactions();
		BeanUtil.updateBean(t, transaction);
		
		mEditFragment.setTransaction(t);
	}
	
	public void onSelectBill(boolean isMandatory) {
		
		if (mBillDlgFragment.isAdded()) {
			return;
		}
		
		mBillDlgFragment.setMandatory(isMandatory);
		mBillDlgFragment.show(getFragmentManager(), mBillDlgFragmentTag);
	}
	
	public void onBillSelected(Bills bill) {
		
		mEditFragment.setBill(bill);
	}
	
	private void processExtras(Bundle extras) {
		
		if (extras != null) {
			
			mIsCreditPayment = true;
			
			TransactionsBean transaction = (TransactionsBean) extras.getSerializable(Constant.SELECTED_CREDIT_FOR_PAYMENT);
			
			mSelectedCashflow = new Cashflow();
			
			mSelectedCashflow.setType(Constant.CASHFLOW_TYPE_INVC_PAYMENT);
			mSelectedCashflow.setTransactionId(transaction.getRemote_id());
			mSelectedCashflow.setCashDate(new Date());
		}
	}
}
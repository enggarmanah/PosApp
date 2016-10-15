package com.tokoku.pos.report.inventory;

import java.io.Serializable;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Product;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.common.ProgressDlgFragment;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

public class InventoryActivity extends BaseActivity 
	implements InventoryActionListener, SearchView.OnQueryTextListener {
	
	protected static ProgressDlgFragment mProgressDialog;
	
	protected static String mUpdateProductStockProgressDialogTag = "updateProductStockprogressDialogTag";
	
	private SearchView searchView;
	
	private MenuItem mSearchMenu;
	private MenuItem mListMenu;
	private MenuItem mAlertMenu;
	private MenuItem mRefreshMenu;
	
	private TextView mAlertMenuText;
	private ImageButton mAlertMenuBtn;
	
	private String prevQuery = Constant.EMPTY_STRING;
	
	private InventoryListFragment mInventoryReportListFragment;
	private InventoryDetailFragment mInventoryReportDetailFragment;
	
	boolean mIsMultiplesPane = false;
	
	private Product mSelectedProduct;
	
	private static String SELECTED_PRODUCT = "SELECTED_PRODUCT";
	
	private final String mInventoryReportListFragmentTag = "inventoryReportListFragmentTag";
	private final String mInventoryReportDetailFragmentTag = "inventoryReportDetailFragmentTag";
	
	private Integer mBelowStockLimitProductCount = 0;
	
	private boolean mIsShowAllProducts = false;
	private boolean mIsShowBelowStockLimitProducts = false;
	private boolean mIsProductStockUpdateInProgress = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initInstanceState(savedInstanceState);
		
		setContentView(R.layout.report_inventory_activity);

		initDrawerMenu();
		
		initFragments();
		
		mProgressDialog = (ProgressDlgFragment) getFragmentManager().findFragmentByTag(mUpdateProductStockProgressDialogTag);
		
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDlgFragment();
		}
		
		mProgressDialog.setListener(this);
		
		initWaitAfterFragmentRemovedTask(mInventoryReportListFragmentTag, mInventoryReportDetailFragmentTag);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_report_inventory));
		setSelectedMenu(getString(R.string.menu_report_inventory));
	}
	
	protected void refreshProductStock() {
		
		if (mIsProductStockUpdateInProgress) {
			return;
		}
		
		mIsProductStockUpdateInProgress = true;
		
		mProgressDialog.show(getFragmentManager(), mUpdateProductStockProgressDialogTag);
		mProgressDialog.setMessage(getString(R.string.msg_refresh_data));
		
		UpdateProductStockTask task = new UpdateProductStockTask();
		task.execute();
	}
	
	@Override
	public void cancelRefreshProductStock() {
		
		mIsProductStockUpdateInProgress = false;
	}
	
	private void initInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			
			mSelectedProduct = (Product) savedInstanceState.getSerializable(SELECTED_PRODUCT);
		}
	}
	
	private void initFragments() {
		
		mIsMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);

		mInventoryReportListFragment = (InventoryListFragment) getFragmentManager().findFragmentByTag(mInventoryReportListFragmentTag);
		
		if (mInventoryReportListFragment == null) {
			mInventoryReportListFragment = new InventoryListFragment();

		} else {
			removeFragment(mInventoryReportListFragment);
		}
		
		mInventoryReportDetailFragment = (InventoryDetailFragment) getFragmentManager().findFragmentByTag(mInventoryReportDetailFragmentTag);
		
		if (mInventoryReportDetailFragment == null) {
			mInventoryReportDetailFragment = new InventoryDetailFragment();

		} else {
			removeFragment(mInventoryReportDetailFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SELECTED_PRODUCT, (Serializable) mSelectedProduct);
	}
	
	@Override
	protected void afterFragmentRemoved() {
		
		loadFragments();
	}
	
	private void loadFragments() {
		
		mInventoryReportListFragment.setSelectedProduct(mSelectedProduct);
		mInventoryReportDetailFragment.setProduct(mSelectedProduct);
		
		if (mIsMultiplesPane) {

			addFragment(mInventoryReportListFragment, mInventoryReportListFragmentTag);
			addFragment(mInventoryReportDetailFragment, mInventoryReportDetailFragmentTag);
			
		} else {

			if (mSelectedProduct != null) {
				
				addFragment(mInventoryReportDetailFragment, mInventoryReportDetailFragmentTag);
				
			} else {
				
				addFragment(mInventoryReportListFragment, mInventoryReportListFragmentTag);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.report_inventory_menu, menu);
		
		mSearchMenu = menu.findItem(R.id.menu_item_search);
		mListMenu = menu.findItem(R.id.menu_item_list);
		mAlertMenu = menu.findItem(R.id.menu_item_alert);
		mRefreshMenu = menu.findItem(R.id.menu_item_refresh);
		
		mAlertMenuText = (TextView) menu.findItem(R.id.menu_item_alert).getActionView().findViewById(R.id.menu_item_alert_text);
		mAlertMenuBtn = (ImageButton) menu.findItem(R.id.menu_item_alert).getActionView().findViewById(R.id.menu_item_alert_icon);
		
		String alertText = "0";
		
		if (mBelowStockLimitProductCount > 99) {
			alertText = "++";
		} else if (mBelowStockLimitProductCount > 0) {
			alertText = CommonUtil.formatNumber(mBelowStockLimitProductCount);
		}
		
		mAlertMenuText.setText(alertText);
		mAlertMenuBtn.setOnClickListener(getMenuAlertOnClickListener());
		
		hideListAndAlertMenu();
		
		if (mBelowStockLimitProductCount != 0 &&
			mIsShowAllProducts) {
			
			mAlertMenu.setVisible(true);
		}
		
		searchView = (SearchView) mSearchMenu.getActionView();
		searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.START));

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (null != searchManager) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		hideSelectedMenu();
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	private View.OnClickListener getMenuAlertOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mIsShowAllProducts = false;
				mIsShowBelowStockLimitProducts = true;
				
				mListMenu.setVisible(true);
				mAlertMenu.setVisible(false);
				
				mInventoryReportListFragment.showBelowStockLimitProducts();
				mSearchMenu.collapseActionView();
				
				showInventoryList();
			}
		};
	}
	
	private void hideListAndAlertMenu() {
		
		if (mListMenu != null) {
			mListMenu.setVisible(false);
		}
		
		if (mAlertMenu != null) {
			mAlertMenu.setVisible(false);
		}
	}
	
	private void hideSelectedMenu() {
		
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		mSearchMenu.setVisible(!isDrawerOpen);
		mRefreshMenu.setVisible(!isDrawerOpen);
		
		if (mIsShowAllProducts) {
			mAlertMenu.setVisible(!isDrawerOpen);
		}
		
		if (mIsShowBelowStockLimitProducts) {
			mListMenu.setVisible(!isDrawerOpen);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
			case R.id.menu_item_refresh:
				
				refreshProductStock();
				
				return true;
		
			case R.id.menu_item_list:
				
				showInventoryList();
				
				if (mBelowStockLimitProductCount != 0) {
				
					mIsShowAllProducts = true;
					mIsShowBelowStockLimitProducts = false;
					
					mListMenu.setVisible(false);
					mAlertMenu.setVisible(true);
					
					mInventoryReportListFragment.showAllProducts();
				}
				
				return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onProductSelected(Product product) {
		
		mSelectedProduct = product;
		
		mInventoryReportListFragment.setSelectedProduct(product);
		mInventoryReportDetailFragment.setProduct(mSelectedProduct);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mInventoryReportDetailFragment, mInventoryReportDetailFragmentTag);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		synchronized (CommonUtil.LOCK) {
			
			showInventoryList();
		}
	}
	
	private void showInventoryList() {
		
		mSelectedProduct = null;
		
		mInventoryReportListFragment.setSelectedProduct(mSelectedProduct);
		mInventoryReportDetailFragment.setProduct(mSelectedProduct);
		
		if (!mIsMultiplesPane) {
			
			replaceFragment(mInventoryReportListFragment, mInventoryReportListFragmentTag);
		}
	}
	
	@Override
	public boolean onQueryTextChange(String query) {
		
		boolean isQuerySimilar = query.equals(prevQuery);
		
		prevQuery = query;
		
		if (!isQuerySimilar) {
			
			if (mIsMultiplesPane) {
				
				doSearch(query);
				
			} else {
				
				replaceFragment(mInventoryReportListFragment, mInventoryReportListFragmentTag);
				doSearch(query);
			}
		}

		return true;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {

		doSearch(query);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

		return true;
	}
	
	private void doSearch(String query) {
		
		mInventoryReportListFragment.searchProduct(query);
	}
	
	@Override
	public void onProductUnselected() {
		
		mSelectedProduct = null;

		mInventoryReportListFragment.setSelectedProduct(mSelectedProduct);
		mInventoryReportDetailFragment.setProduct(mSelectedProduct);
	}
	
	private void updateBelowStockLimitProduct() {
		
		MerchantUtil.refreshBelowStockLimitProductCount();
		
		mBelowStockLimitProductCount = MerchantUtil.getBelowStockLimitProductCount();
		
		if (mAlertMenuText != null) {
			mAlertMenuText.setText(CommonUtil.formatNumber(mBelowStockLimitProductCount));
		}
		
		mIsShowAllProducts = false;
		mIsShowBelowStockLimitProducts = false;
		
		if (mBelowStockLimitProductCount == 0) {
			hideListAndAlertMenu();
			
		} else {
			
			mIsShowAllProducts = true;
			
			hideListAndAlertMenu();
			
			if (mAlertMenu != null) {
				mAlertMenu.setVisible(true);
			}
		}
	}
	
	@Override
	protected void onAsyncTaskCompleted() {
		
		super.onAsyncTaskCompleted();
		
		mInventoryReportListFragment.updateContent();
		mInventoryReportDetailFragment.updateContent();
	}
	
	private class UpdateProductStockTask extends AsyncTask<String, Integer, Boolean> {
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			ProductDaoService productDaoService = new ProductDaoService();
			InventoryDaoService inventoryDaoServie = new InventoryDaoService();
			
			List<Product> products = productDaoService.getProducts(Constant.PRODUCT_TYPE_GOODS);
			
			if (products != null) {
				
				int i = 0;
				
				for (Product product : products) {
					
					Float quantity = inventoryDaoServie.getProductQuantity(product);
					product.setStock(quantity);
					
					productDaoService.updateProduct(product);
					publishProgress((int) ((i++ / (float) products.size()) * 100));
					
					if (!mIsProductStockUpdateInProgress) {
						break;
					}
					
					try {
						Thread.sleep(Constant.PRODUCT_UPDATE_DELAY_PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (mProgressDialog != null) {
				mProgressDialog.dismissAllowingStateLoss();
			}
				
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			int prg = progress[0];
			
			if (mProgressDialog != null) {
				mProgressDialog.setProgress(prg);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
		
			updateBelowStockLimitProduct();
			mIsProductStockUpdateInProgress = false;
			mInventoryReportListFragment.searchProduct(prevQuery);
		}
	}
}
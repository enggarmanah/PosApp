package com.tokoku.pos.cashier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class CashierProductSearchFragment extends BaseFragment 
	implements CashierProductGroupArrayAdapter.ItemActionListener,
	CashierProductArrayAdapter.ItemActionListener {
	
	private TextView mNavigationTitle;
	private ImageButton mBackButton;
	
	private ListView mProductGroupList;
	private List<ProductGroup> mProductGroups;
	private ProductGroup mSelectedPrdGroup;
	private List<Product> mProducts;
	
	private CashierProductGroupArrayAdapter mProductGroupAdapter;
	private CashierProductArrayAdapter mProductAdapter;
	
	private CashierActionListener mActionListener;
	
	private ProductGroupDaoService mProductGroupDaoService = new ProductGroupDaoService();
	private ProductDaoService mProductDaoService = new ProductDaoService();
	
	private static String PRODUCT_GROUPS = "PRODUCT_GROUPS";
	private static String SELECTED_PRODUCT_GROUP = "SELECTED_PRODUCT_GROUP";
	private static String PRODUCTS = "PRODUCTS";
	
	private String mSearchQuery;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			mProductGroups = (List<ProductGroup>) savedInstanceState.getSerializable(PRODUCT_GROUPS);
			mProducts = (List<Product>) savedInstanceState.getSerializable(PRODUCTS);
			mSelectedPrdGroup = (ProductGroup) savedInstanceState.getSerializable(SELECTED_PRODUCT_GROUP);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		outState.putSerializable(PRODUCT_GROUPS, (Serializable) mProductGroups);
		outState.putSerializable(PRODUCTS, (Serializable) mProducts);
		outState.putSerializable(SELECTED_PRODUCT_GROUP, mSelectedPrdGroup);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.cashier_product_search_fragment, container, false);
		
		if (mProductGroups == null) {
			mProductGroups = mProductGroupDaoService.getProductGroups();
		}
		
		if (mSearchQuery != null) {
			mProducts = mProductDaoService.getProducts(mSearchQuery, mSelectedPrdGroup);
		}
		
		if (mProducts == null) {
			mProducts = new ArrayList<Product>();
		}
		
		mProductGroupAdapter = new CashierProductGroupArrayAdapter(getActivity(), mProductGroups, this);
		mProductAdapter = new CashierProductArrayAdapter(getActivity(), mProducts, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mBackButton = (ImageButton) getActivity().findViewById(R.id.backButton);
		mNavigationTitle = (TextView) getActivity().findViewById(R.id.navigationTitle);
		mProductGroupList = (ListView) getActivity().findViewById(R.id.productGroupList);
		
		if (mProducts.size() > 0) {
			mProductGroupList.setAdapter(mProductAdapter);
		} else {
			mProductGroupList.setAdapter(mProductGroupAdapter);
		}
		
		mProductGroupList.setItemsCanFocus(true);
		mProductGroupList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mBackButton.setOnClickListener(getUpButtonOnClickListener());
		
		refreshNavigationPanel();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CashierActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	private void refreshNavigationPanel() {
		
		if (mSelectedPrdGroup != null) {
			mNavigationTitle.setText(mSelectedPrdGroup.getName().toUpperCase());
			mBackButton.setVisibility(View.VISIBLE);
		
		} else if (mSearchQuery != null) {
			mNavigationTitle.setText(getString(R.string.product_search));
			mBackButton.setVisibility(View.VISIBLE);
			
		} else {
			mNavigationTitle.setText(getString(R.string.product_group_list));
			mBackButton.setVisibility(View.GONE);
		}
	}
	
	public void searchProducts(String query) {
		
		mSearchQuery = query;
		
		if (!isViewInitialized()) {
			return;
		}
		
		mSelectedPrdGroup = null;
		
		mProducts.clear();
		mProducts.addAll(mProductDaoService.getProducts(query, mSelectedPrdGroup));
		
		mProductGroupList.setAdapter(mProductAdapter);
		
		refreshNavigationPanel(); 
	}
	
	public void showProductGroups() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		mSearchQuery = null;
		mSelectedPrdGroup = null;
		
		if (mProducts != null) {
			mProducts.clear();
		}
		
		mProductGroups.clear();
		mProductGroups.addAll(mProductGroupDaoService.getProductGroups());
		mProductGroupList.setAdapter(mProductGroupAdapter);
		
		refreshNavigationPanel();
	}
	
	@Override
	public void onProductGroupSelected(ProductGroup prdGroup) {
		
		mSelectedPrdGroup = prdGroup;
		
		mSearchQuery = Constant.EMPTY_STRING;
		
		mProducts.clear();
		mProducts.addAll(mProductDaoService.getProducts(mSearchQuery, mSelectedPrdGroup));
		
		mProductGroupList.setAdapter(mProductAdapter);
		
		refreshNavigationPanel();
	}
	
	@Override
	public void onProductSelected(Product product) {
		
		Employee personInCharge = null;
		Float quantity = Float.valueOf(0);
		
		mActionListener.onProductSelected(product, CommonUtil.getCurrentPrice(product), personInCharge, quantity, Constant.EMPTY_STRING);
	}
	
	private View.OnClickListener getUpButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onShowProductGroups();
			}
		};
	}
}
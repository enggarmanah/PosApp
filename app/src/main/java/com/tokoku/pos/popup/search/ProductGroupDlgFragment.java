package com.tokoku.pos.popup.search;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.ProductGroup;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ProductGroupDlgFragment extends BaseSearchDlgFragment<ProductGroup> implements ProductGroupArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mProductGroupSearchText;
	ListView mProductGroupListView;
	TextView mNoProductGroupText;
	
	ProductGroupSelectionListener mActionListener;
	
	ProductGroupArrayAdapter productGroupArrayAdapter;
	
	boolean mIsMandatory = false;
	
	private ProductGroupDaoService mProductGroupDaoService = new ProductGroupDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<ProductGroup>();
        
        productGroupArrayAdapter = new ProductGroupArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_product_group_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mProductGroupSearchText = (EditText) getView().findViewById(R.id.productGroupSearchText);
		
		mProductGroupSearchText.setText(CommonUtil.getNvlString(mQuery));
		mProductGroupSearchText.setSelection(mProductGroupSearchText.getText().length());
		//mProductGroupSearchText.requestFocus();
		
		mProductGroupSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(mProductGroupDaoService.getProductGroups(mQuery, 0));
				productGroupArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mProductGroupListView = (ListView) getView().findViewById(R.id.productGroupListView);
		mProductGroupListView.setAdapter(productGroupArrayAdapter);
		mProductGroupListView.setOnScrollListener(getListOnScrollListener());
		
		mNoProductGroupText = (TextView) getView().findViewById(R.id.noProductGroupText);
		mNoProductGroupText.setOnClickListener(getNoProductGroupTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoProductGroupText.setVisibility(View.GONE);
		} else {
			mNoProductGroupText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		mItems.addAll(mProductGroupDaoService.getProductGroups(mQuery, 0));
		productGroupArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (ProductGroupSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProductGroupSelectionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
	
	@Override
	public void onProductGroupSelected(ProductGroup productGroup) {
		
		dismiss();
		mActionListener.onProductGroupSelected(productGroup);
	}
	
	private View.OnClickListener getNoProductGroupTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onProductGroupSelected(null);
				dismiss();
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
	
	@Override
	protected List<ProductGroup> getItems(String query) {
		
		return mProductGroupDaoService.getProductGroups(mQuery, 0);
	}
	
	@Override
	protected List<ProductGroup> getNextItems(String query, int lastIndex) {
		
		return mProductGroupDaoService.getProductGroups(mQuery, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		productGroupArrayAdapter.notifyDataSetChanged();
	}
}

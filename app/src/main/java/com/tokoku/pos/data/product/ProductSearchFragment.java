package com.tokoku.pos.data.product;

import java.util.List;

import com.android.pos.dao.Product;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.ProductDaoService;

import android.app.Activity;

public class ProductSearchFragment extends BaseSearchFragment<Product> {

	private ProductDaoService mProductDaoService = new ProductDaoService();

	public void initAdapter() {
		
		mAdapter = new ProductSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Product>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Product>");
        }
    }

	protected Long getItemId(Product item) {
		return item.getId();
	}
	
	protected String getItemIndex(Product item) {
		return item.getName();
	}
	
	public List<Product> getItems(String query) {

		return mProductDaoService.getProducts(query, 0);
	}
	
	@Override
	public List<Product> getNextItems(String query, int lastIndex) {

		return mProductDaoService.getProducts(query, lastIndex);
	}

	public void onItemDeleted(Product item) {
		
		mProductDaoService.deleteProduct(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
package com.tokoku.pos.data.supplier;

import java.util.List;

import com.android.pos.dao.Supplier;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.SupplierDaoService;

import android.app.Activity;

public class SupplierSearchFragment extends BaseSearchFragment<Supplier> {

	private SupplierDaoService mSupplierDaoService = new SupplierDaoService();

	public void initAdapter() {
		
		mAdapter = new SupplierSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Supplier>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Supplier>");
        }
    }

	protected Long getItemId(Supplier item) {
		return item.getId();
	}
	
	public List<Supplier> getItems(String query) {

		return mSupplierDaoService.getSuppliers(query, 0);
	}
	
	public List<Supplier> getNextItems(String query, int lastIndex) {

		return mSupplierDaoService.getSuppliers(query, lastIndex);
	}

	public void onItemDeleted(Supplier item) {

		mSupplierDaoService.deleteSupplier(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
package com.tokoku.pos.data.bills;

import java.util.List;

import com.android.pos.dao.Bills;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.BillsDaoService;

import android.app.Activity;

public class BillsSearchFragment extends BaseSearchFragment<Bills> {

	private BillsDaoService mBillsDaoService = new BillsDaoService();

	public void initAdapter() {
		
		mAdapter = new BillsSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Bills>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Bills>");
        }
    }

	protected Long getItemId(Bills item) {
		return item.getId();
	}
	
	public List<Bills> getItems(String query) {

		return mBillsDaoService.getBills(query, 0);
	}
	
	public List<Bills> getNextItems(String query, int lastIndex) {

		return mBillsDaoService.getBills(query, lastIndex);
	}

	public void onItemDeleted(Bills item) {

		mBillsDaoService.deleteBills(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
package com.tokoku.pos.data.cashflow;

import java.util.List;

import com.android.pos.dao.Cashflow;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.CashflowDaoService;

import android.app.Activity;

public class CashflowSearchFragment extends BaseSearchFragment<Cashflow> {

	private CashflowDaoService mCashflowDaoService = new CashflowDaoService();

	public void initAdapter() {
		
		mAdapter = new CashflowSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Cashflow>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Cashflow>");
        }
    }

	protected Long getItemId(Cashflow item) {
		return item.getId();
	}
	
	public List<Cashflow> getItems(String query) {

		return mCashflowDaoService.getCashflows(query, 0);
	}
	
	public List<Cashflow> getNextItems(String query, int lastIndex) {

		return mCashflowDaoService.getCashflows(query, lastIndex);
	}

	public void onItemDeleted(Cashflow item) {

		mCashflowDaoService.deleteCashflow(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
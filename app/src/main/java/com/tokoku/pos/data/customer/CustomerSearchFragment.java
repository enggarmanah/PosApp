package com.tokoku.pos.data.customer;

import java.util.List;

import com.android.pos.dao.Customer;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.CustomerDaoService;

import android.app.Activity;

public class CustomerSearchFragment extends BaseSearchFragment<Customer> {

	private CustomerDaoService mCustomerDaoService = new CustomerDaoService();

	public void initAdapter() {
		
		mAdapter = new CustomerSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Customer>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Customer>");
        }
    }

	protected Long getItemId(Customer item) {
		return item.getId();
	}
	
	public List<Customer> getItems(String query) {

		return mCustomerDaoService.getCustomers(query, 0);
	}
	
	public List<Customer> getNextItems(String query, int lastIndex) {

		return mCustomerDaoService.getCustomers(query, lastIndex);
	}

	public void onItemDeleted(Customer item) {

		mCustomerDaoService.deleteCustomer(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
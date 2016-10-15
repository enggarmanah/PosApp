package com.tokoku.pos.data.inventory;

import java.util.List;

import com.android.pos.dao.Inventory;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.InventoryDaoService;

import android.app.Activity;

public class InventorySearchFragment extends BaseSearchFragment<Inventory> {

	private InventoryDaoService mInventoryDaoService = new InventoryDaoService();

	public void initAdapter() {
		
		mAdapter = new InventorySearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<Inventory>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<Inventory>");
        }
    }

	protected Long getItemId(Inventory item) {
		return item.getId();
	}
	
	public List<Inventory> getItems(String query) {

		return mInventoryDaoService.getInventories(query, 0);
	}
	
	public List<Inventory> getNextItems(String query, int lastIndex) {

		return mInventoryDaoService.getInventories(query, lastIndex);
	}

	public void onItemDeleted(Inventory item) {

		mInventoryDaoService.deleteInventory(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
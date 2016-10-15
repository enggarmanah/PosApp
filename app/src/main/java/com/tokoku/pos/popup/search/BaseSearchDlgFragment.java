package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;

import android.app.DialogFragment;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.Toast;

public abstract class BaseSearchDlgFragment<T> extends DialogFragment {
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	protected List<T> mItems;
	
	protected abstract List<T> getItems(String query);
	
	protected abstract List<T> getNextItems(String query, int lastIndex);
	
	protected abstract void refreshList();
	
	protected String mQuery = Constant.EMPTY_STRING;
	
	protected AbsListView.OnScrollListener getListOnScrollListener() {
		
		return new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
				if (firstVisibleItem == 0) {
					return;
				}
				
				int lastInScreen = firstVisibleItem + visibleItemCount;
				
				if((lastInScreen == totalItemCount) && !mIsLoadData && !mIsEndOfList) {
					
					mIsLoadData = true;
					
					List<T> list = getNextItems(mQuery, mItems.size());
					
					if (list.size() == 0) {
						mIsEndOfList = true;
					}
					
					String message = Constant.EMPTY_STRING;
					
					if (mIsEndOfList) {
						message = getString(R.string.alert_data_no_more);
					} else {
						message = getString(R.string.alert_data_show_next);
					}
					
					Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
						
					mItems.addAll(list);
					
					//mAdapter.notifyDataSetChanged();
					
					mIsLoadData = false;
				}
			}
		};
	}
}

package com.tokoku.pos.base.fragment;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.base.listener.BaseItemListener;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public abstract class BaseSearchFragment<T> extends BaseFragment 
	implements BaseSearchArrayAdapter.ItemActionListener<T> {
	
	protected ImageButton mAddButton;
	
	protected BaseSearchArrayAdapter<T> mAdapter;
	protected BaseItemListener<T> mItemListener;

	protected ListView mSearchResultList;

	protected List<T> mItems;
	protected T mSelectedItem;
	
	private boolean mIsLoadData = false;
	private boolean mIsEndOfList = false;
	
	private String mQuery = Constant.EMPTY_STRING;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.app_item_search_fragment, container, false);

		if (mItems != null && mItems.size() == 0) {
			mItems.addAll(getItems(Constant.EMPTY_STRING));
		}
		
		initAdapter();
		
		mAddButton = (ImageButton) view.findViewById(R.id.addButton);
		mAddButton.setOnClickListener(getAddButtonOnClickListener());
		
		mSearchResultList = (ListView) view.findViewById(R.id.searchResultList);
		
		mSearchResultList.setAdapter(mAdapter);
		mSearchResultList.setItemsCanFocus(true);
		mSearchResultList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mSearchResultList.setOnScrollListener(getListOnScrollListener());
		
		return view;
	}
	
	private AbsListView.OnScrollListener getListOnScrollListener() {
		
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
					mAdapter.notifyDataSetChanged();
					
					mIsLoadData = false;
				}
			}
		};
	}
	
	public abstract void initAdapter();
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		updateContent();
	}
	
	private View.OnClickListener getAddButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mItemListener.addItem();
			}
		};
	}
	
	protected abstract Long getItemId(T item);
	
	protected String getItemIndex(T item) {
		
		return null;
	}
	
	public void searchItem(String query) {
		
		mQuery = query;
		mIsEndOfList = false;
		
		if (!isViewInitialized()) {
			mSelectedItem = null;
			return;
		}
		
		updateContent();
	}
	
	protected void updateContent() {
		
		unSelectItem();
		
		mItems.clear();
		mItems.addAll(getItems(mQuery));
		
		mAdapter.notifyDataSetChanged();
	}
	
	public abstract List<T> getItems(String query);
	
	public abstract List<T> getNextItems(String query, int lastIndex);

	public abstract void onItemDeleted(T item);

	public void onItemSelected(T item) {

		mSelectedItem = item;
		
		mItemListener.updateItem(item);
	}
	
	public T getSelectedItem() {
		
		return mSelectedItem;
	}
	
	public void setSelectedItem(T item) {
		
		mSelectedItem = item;
	}
	
	public List<T> getItems() {
		
		return mItems;
	}
	
	public void setItems(List<T> items) {
		
		mItems = items;
	}
	
	public void unSelectItem() {
		
		mSelectedItem = null;
		
		mAdapter.unSelectItem();
		mItemListener.onItemUnselected();
	}
	
	public void refreshItems() {
		
		if (isViewInitialized()) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void reloadItems() {
		
		searchItem(Constant.EMPTY_STRING);
	}
	
	protected ImageButton getAddBtn() {
		
		return mAddButton;
	}
}
package com.tokoku.pos.base.adapter;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class BaseSearchArrayAdapter<T> extends ArrayAdapter<T> {

	protected Context context;
	protected List<T> items;
	protected T selectedItem;
	protected ItemActionListener<T> mCallback;

	protected View mSelectedView;

	public interface ItemActionListener<T> {

		public void onItemDeleted(T item);

		public void onItemSelected(T item);
	}

	class ViewHolder {
		
		TextView nameText;
		TextView infoText;
	}

	public BaseSearchArrayAdapter(Context context, List<T> items, T selectedItem, ItemActionListener<T> listener) {
		super(context, R.layout.app_list_item, items);
		
		this.context = context;
		this.items = items;
		this.selectedItem = selectedItem;
		this.mCallback = listener;
	}
	
	public BaseSearchArrayAdapter(Context context, int layoutId, List<T> items, T selectedItem, ItemActionListener<T> listener) {
		super(context, layoutId, items);
		
		this.context = context;
		this.items = items;
		this.selectedItem = selectedItem;
		this.mCallback = listener;
	}
	
	public abstract Long getItemId(T item);
	
	public abstract String getItemName(T item);
	
	public String getItemInfo(T item) { 
		
		return null; 
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final T item = items.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		TextView infoText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.app_list_item, parent, false);
			nameText = (TextView) rowView.findViewById(R.id.nameText);
			infoText = (TextView) rowView.findViewById(R.id.infoText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.nameText = nameText;
			viewHolder.infoText = infoText;
			
			rowView.setTag(viewHolder);

		} else {

			@SuppressWarnings("unchecked")
			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			nameText = viewHolder.nameText;
			infoText = viewHolder.infoText;
		}
		
		nameText.setText(getItemName(item));
		infoText.setText(getItemInfo(item));
		
		if (CommonUtil.isEmpty(getItemInfo(item))) {
			infoText.setVisibility(View.GONE);
		} else {
			infoText.setVisibility(View.VISIBLE);
		}

		if (selectedItem != null && getItemId(selectedItem) == getItemId(item)) {
			
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
			mSelectedView = rowView;
			
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(item, nameText));

		return rowView;
	}
	
	protected View.OnClickListener getItemOnClickListener(final T item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				selectedItem = item;
				v.setSelected(true);

				if (mSelectedView != null) {

					mSelectedView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
					mSelectedView.refreshDrawableState();
				}

				mSelectedView = v;

				mSelectedView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));

				mCallback.onItemSelected(item);
			}
		};
	}

	public void unSelectItem() {

		selectedItem = null;
		
		if (mSelectedView != null) {

			mSelectedView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}
	}
}
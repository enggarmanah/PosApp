package com.tokoku.pos.base.adapter;

import com.tokoku.pos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class BaseSpinnerArrayAdapter<T> extends ArrayAdapter<T> {

	protected Context context;
	protected T[] options;
	
	protected int mListNormalLayout = R.layout.app_spinner_items; 
	protected int mListSelectedLayout = R.layout.app_spinner_items_selected;
	protected int mItemSelected = R.layout.app_spinner_selected_item;
	
	public BaseSpinnerArrayAdapter(Context context, T[] options) {
		
		super(context, R.layout.app_spinner_items, options);
		this.context = context;
		this.options = options;
	}
	
	public BaseSpinnerArrayAdapter(Context context, T[] options, int listNormalLayout, int listSelectedLayout, int itemSelected) {
		
		super(context, R.layout.app_spinner_items, options);
		this.context = context;
		this.options = options;
		
		mListNormalLayout = listNormalLayout;
		mListSelectedLayout = listSelectedLayout;
		mItemSelected = itemSelected;
	}

	public int getCount() {
		return options.length;
	}

	public T getItem(int position) {
		return options[position];
	}

	public long getItemId(int position) {
		return position;
	}
	
	protected abstract String getLabel1(T option);
	
	protected String getLabel2(T option) { return null; };
	
	protected abstract String getValue(T option);
	
	protected abstract String getSelectedValue();
	
	public int getPosition(String value) {
		
		int position = 0;
		
		int index = 0;
		for (T option : options) {
			
			if (getValue(option).equals(value)) {
				position = index;
				break;
			}
			
			index++;
		}
		
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return getCustomView(position, convertView, parent, false);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		return getCustomView(position, convertView, parent, true);
	}
	
	private View getCustomView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
		
		T option = options[position];
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		boolean isSelected = getValue(option).equals(getSelectedValue());
		
		View rowView = convertView;
		
		TextView label1Text = null;
		TextView label2Text = null;
			
		if (isDropDown) {
			rowView = inflater.inflate(isSelected ? mListSelectedLayout : mListNormalLayout, parent, false);
		} else {
			rowView = inflater.inflate(mItemSelected, parent, false);
		}
		
		label1Text = (TextView) rowView.findViewById(R.id.label1Text);
		label1Text.setText(getLabel1(option));
		
		label2Text = (TextView) rowView.findViewById(R.id.label2Text);
		
		if (label2Text != null) {
			
			if (getLabel2(option) != null) {
				String label2 = getLabel2(option);
				label2Text.setVisibility(View.VISIBLE);
				label2Text.setText(label2);
			} else {
				label2Text.setVisibility(View.GONE);
			}
			
		}
		
		return rowView;
	}
}

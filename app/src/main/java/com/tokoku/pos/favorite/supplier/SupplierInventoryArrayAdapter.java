package com.tokoku.pos.favorite.supplier;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Inventory;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SupplierInventoryArrayAdapter extends ArrayAdapter<Inventory> {

	private Context context;
	private List<Inventory> inventories;

	class ViewHolder {
		TextView productNameText;
		TextView deliveryDateText;
		TextView productGroupNameText;
		TextView amountText;
	}

	public SupplierInventoryArrayAdapter(Context context, List<Inventory> inventories) {

		super(context, R.layout.favorite_supplier_inventory_list_item, inventories);
		
		this.context = context;
		this.inventories = inventories;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Inventory inventory = inventories.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView productNameText = null;
		TextView deliveryDateText = null;
		TextView productGroupNameText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.favorite_supplier_inventory_list_item, parent, false);
			
			productNameText = (TextView) rowView.findViewById(R.id.productNameText);
			deliveryDateText = (TextView) rowView.findViewById(R.id.deliveryDateText);
			productGroupNameText = (TextView) rowView.findViewById(R.id.productGroupNameText);
			amountText = (TextView) rowView.findViewById(R.id.amountText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.productNameText = productNameText;
			viewHolder.deliveryDateText = deliveryDateText;
			viewHolder.productGroupNameText = productGroupNameText;
			viewHolder.amountText = amountText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			productNameText = viewHolder.productNameText;
			deliveryDateText = viewHolder.deliveryDateText;
			productGroupNameText = viewHolder.productGroupNameText;
			amountText = viewHolder.amountText;
		}
		
		Float price = inventory.getProductCostPrice();
		
		productNameText.setText(inventory.getProductName());
		deliveryDateText.setText(CommonUtil.formatDate(inventory.getInventoryDate()));
		productGroupNameText.setText(inventory.getProduct().getProductGroup().getName());
		amountText.setText(CommonUtil.formatNumber(inventory.getQuantity()) + "  x  " + CommonUtil.formatCurrency(price));

		return rowView;
	}
}
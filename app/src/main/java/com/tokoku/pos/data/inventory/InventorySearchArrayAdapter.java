package com.tokoku.pos.data.inventory;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Inventory;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InventorySearchArrayAdapter extends BaseSearchArrayAdapter<Inventory> {
	
	class ViewHolder {
		ImageView flowImage;
		TextView itemText;
		TextView remarksText;
		TextView deliveryDateText;
		TextView quantityText;
		TextView supplierText;
	}
	
	public InventorySearchArrayAdapter(Context context, List<Inventory> inventories, Inventory selectedInventory, ItemActionListener<Inventory> listener) {
		super(context, R.layout.inventory_list_item, inventories, selectedInventory, listener);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Inventory item = items.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		ImageView flowImage = null;
		TextView nameText = null;
		TextView remarksText = null;
		TextView quantityText = null;
		TextView deliveryDateText = null;
		TextView supplierText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.inventory_list_item, parent, false);
			
			flowImage = (ImageView) rowView.findViewById(R.id.flowImage);
			nameText = (TextView) rowView.findViewById(R.id.nameText);
			remarksText = (TextView) rowView.findViewById(R.id.remarksText);
			quantityText = (TextView) rowView.findViewById(R.id.quantityText);
			deliveryDateText = (TextView) rowView.findViewById(R.id.deliveryDate);
			supplierText = (TextView) rowView.findViewById(R.id.supplierText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.flowImage = flowImage;
			viewHolder.itemText = nameText;
			viewHolder.remarksText = remarksText;
			viewHolder.quantityText = quantityText;
			viewHolder.deliveryDateText = deliveryDateText;
			viewHolder.supplierText = supplierText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			flowImage = viewHolder.flowImage;
			nameText = viewHolder.itemText;
			remarksText = viewHolder.remarksText;
			quantityText = viewHolder.quantityText;
			deliveryDateText = viewHolder.deliveryDateText;
			supplierText = viewHolder.supplierText;
		}
			
		int padding = flowImage.getPaddingLeft();
		
		flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_black));
		flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_out));
		
		if (Constant.INVENTORY_STATUS_PURCHASE.equals(item.getStatus()) ||
			Constant.INVENTORY_STATUS_REPLACEMENT.equals(item.getStatus()) ||
			Constant.INVENTORY_STATUS_REFUND.equals(item.getStatus()) ||
			Constant.INVENTORY_STATUS_INITIAL_STOCK.equals(item.getStatus()) ||
			Constant.INVENTORY_STATUS_SELF_PRODUCTION.equals(item.getStatus())) {
			
			flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_forward_black));
			flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_in));
		}
		
	    flowImage.setPadding(padding, padding, padding, padding);
		
	    String remarks = CommonUtil.isEmpty(item.getRemarks()) ? CodeUtil.getInvetoriStatusLabel(item.getStatus()) : CodeUtil.getInvetoriStatusLabel(item.getStatus()) + ". " + item.getRemarks(); 
	    
	    nameText.setText(getItemName(item));
	    quantityText.setText(CommonUtil.formatNumber(Math.abs(CommonUtil.getNvlFloat(item.getQuantity()))));
		deliveryDateText.setText(CommonUtil.formatDate(item.getInventoryDate()));
		remarksText.setText(remarks);
		
		if (!CommonUtil.isEmpty(item.getSupplierName())) {
			supplierText.setText(CommonUtil.formatNumber(item.getSupplierName()));
			supplierText.setVisibility(View.VISIBLE);
		} else {
			supplierText.setVisibility(View.GONE);
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
	
	@Override
	public Long getItemId(Inventory inventory) {
		return inventory.getId();
	}
	
	@Override
	public String getItemName(Inventory inventory) {
		return inventory.getProductName();
	}
}
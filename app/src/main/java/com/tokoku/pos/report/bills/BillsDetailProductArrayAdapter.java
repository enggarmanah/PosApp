package com.tokoku.pos.report.bills;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Inventory;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BillsDetailProductArrayAdapter extends ArrayAdapter<Inventory> {

	private Context context;
	private List<Inventory> mInventories;
	
	class ViewHolder {
		ImageView flowImage;
		TextView remarksText;
		TextView productText;
		TextView amountText;
		TextView supplierText;
	}

	public BillsDetailProductArrayAdapter(Context context, List<Inventory> inventories) {

		super(context, R.layout.report_bills_detail_product_list_item, inventories);
		
		this.context = context;
		this.mInventories = inventories;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Inventory inventory = mInventories.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		ImageView flowImage = null;
		TextView productText = null;
		TextView remarksText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_bills_detail_product_list_item, parent, false);
			
			flowImage = (ImageView) rowView.findViewById(R.id.flowImage);
			productText = (TextView) rowView.findViewById(R.id.productText);
			remarksText = (TextView) rowView.findViewById(R.id.remarksText);
			amountText = (TextView) rowView.findViewById(R.id.amountText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.flowImage = flowImage;
			viewHolder.remarksText = remarksText;
			viewHolder.amountText = amountText;
			viewHolder.productText = productText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			flowImage = viewHolder.flowImage;
			remarksText = viewHolder.remarksText;
			amountText = viewHolder.amountText;
			productText = viewHolder.productText;
		}
			
		int padding = flowImage.getPaddingLeft();
		
		flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_black));
		flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_out));
		
		if (Constant.INVENTORY_STATUS_PURCHASE.equals(inventory.getStatus()) ||
			Constant.INVENTORY_STATUS_REPLACEMENT.equals(inventory.getStatus())) {
			
			flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_forward_black));
			flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_in));
		}
		
	    flowImage.setPadding(padding, padding, padding, padding);
		
	    productText.setText(inventory.getProductName());
	    remarksText.setText(CommonUtil.formatNumber(CommonUtil.getNvlFloat(inventory.getQuantity())) + "  x  " + CommonUtil.formatCurrency(CommonUtil.getNvlFloat(inventory.getProductCostPrice())));
		amountText.setText(CommonUtil.formatCurrency(CommonUtil.getNvlFloat(inventory.getQuantity()) * CommonUtil.getNvlFloat(inventory.getProductCostPrice())));
		
		return rowView;
	}
}
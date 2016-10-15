package com.tokoku.pos.favorite.customer;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.ProductGroupStatisticBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerDetailArrayAdapter extends ArrayAdapter<ProductGroupStatisticBean> {

	private Context context;
	private List<ProductGroupStatisticBean> mProductGroupStatistics;

	class ViewHolder {
		TextView nameText;
		TextView quantityText;
		TextView amountText;
	}

	public CustomerDetailArrayAdapter(Context context, List<ProductGroupStatisticBean> productGroupStatistics) {

		super(context, R.layout.favorite_customer_detail_list_item, productGroupStatistics);
		
		this.context = context;
		this.mProductGroupStatistics = productGroupStatistics;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ProductGroupStatisticBean productGroupStatistic = mProductGroupStatistics.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		TextView quantityText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.favorite_customer_detail_list_item, parent, false);
			
			nameText = (TextView) rowView.findViewById(R.id.nameText);
			quantityText = (TextView) rowView.findViewById(R.id.quantityText);
			amountText = (TextView) rowView.findViewById(R.id.amountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.nameText = nameText;
			viewHolder.quantityText = quantityText;
			viewHolder.amountText = amountText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			nameText = viewHolder.nameText;
			quantityText = viewHolder.quantityText;
			amountText = viewHolder.amountText;
		}
		
		String quantityStr = CommonUtil.formatNumber(productGroupStatistic.getQuantity());
		String amountStr = CommonUtil.formatCurrency(productGroupStatistic.getAmount());
		
		nameText.setText(productGroupStatistic.getName());
		quantityText.setText(quantityStr);
		amountText.setText(amountStr);
		
		rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		
		return rowView;
	}
}
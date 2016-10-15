package com.tokoku.pos.favorite.customer;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.CustomerStatisticBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerArrayAdapter extends ArrayAdapter<CustomerStatisticBean> {

	private Context context;
	private List<CustomerStatisticBean> mCustomerStatistics;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onCustomerStatisticSelected(CustomerStatisticBean item);
		
		public CustomerStatisticBean getSelectedCustomerStatistic();
	}

	class ViewHolder {
		TextView nameText;
		TextView quantityText;
		TextView amountText;
	}

	public CustomerArrayAdapter(Context context, List<CustomerStatisticBean> customerStatistics, ItemActionListener listener) {

		super(context, R.layout.favorite_customer_list_item, customerStatistics);
		
		this.context = context;
		this.mCustomerStatistics = customerStatistics;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final CustomerStatisticBean customerStatistic = mCustomerStatistics.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		TextView quantityText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.favorite_customer_list_item, parent, false);
			
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
		
		String quantityStr = CommonUtil.formatNumber(customerStatistic.getQuantity());
		String amountStr = CommonUtil.formatCurrency(customerStatistic.getAmount());
		
		nameText.setText(customerStatistic.getName());
		quantityText.setText(quantityStr);
		amountText.setText(amountStr);
		
		rowView.setOnClickListener(getItemOnClickListener(customerStatistic, nameText));
		
		CustomerStatisticBean selectedCustomerStatisticBean = mCallback.getSelectedCustomerStatistic();
		
		if (selectedCustomerStatisticBean != null && selectedCustomerStatisticBean.getName() == customerStatistic.getName()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final CustomerStatisticBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCustomerStatisticSelected(item);
			}
		};
	}
}
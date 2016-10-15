package com.tokoku.pos.favorite.supplier;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.SupplierStatisticBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SupplierArrayAdapter extends ArrayAdapter<SupplierStatisticBean> {

	private Context context;
	private List<SupplierStatisticBean> mSupplierStatistics;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onSupplierStatisticSelected(SupplierStatisticBean item);
		
		public SupplierStatisticBean getSelectedSupplierStatistic();
	}

	class ViewHolder {
		TextView nameText;
		TextView quantityText;
		TextView amountText;
	}

	public SupplierArrayAdapter(Context context, List<SupplierStatisticBean> supplierStatistics, ItemActionListener listener) {

		super(context, R.layout.favorite_supplier_list_item, supplierStatistics);
		
		this.context = context;
		this.mSupplierStatistics = supplierStatistics;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final SupplierStatisticBean supplierStatistic = mSupplierStatistics.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		TextView quantityText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.favorite_supplier_list_item, parent, false);
			
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
		
		String quantityStr = CommonUtil.formatNumber(supplierStatistic.getQuantity());
		String amountStr = CommonUtil.formatCurrency(supplierStatistic.getAmount());
		
		nameText.setText(supplierStatistic.getName());
		quantityText.setText(quantityStr);
		amountText.setText(amountStr);
		
		rowView.setOnClickListener(getItemOnClickListener(supplierStatistic, nameText));
		
		SupplierStatisticBean selectedSupplierStatisticBean = mCallback.getSelectedSupplierStatistic();
		
		if (selectedSupplierStatisticBean != null && selectedSupplierStatisticBean.getName() == supplierStatistic.getName()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final SupplierStatisticBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onSupplierStatisticSelected(item);
			}
		};
	}
}
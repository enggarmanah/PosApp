package com.tokoku.pos.report.commission;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.CommisionMonthBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommissionMonthArrayAdapter extends ArrayAdapter<CommisionMonthBean> {

	private Context context;
	private List<CommisionMonthBean> commisionMonths;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onCommisionMonthSelected(CommisionMonthBean item);
		
		public CommisionMonthBean getSelectedCommisionMonth();
	}

	class ViewHolder {
		TextView commisionDateText;
		TextView commisionAmountText;
	}

	public CommissionMonthArrayAdapter(Context context, List<CommisionMonthBean> commisionMonths, ItemActionListener listener) {

		super(context, R.layout.report_commision_list_item, commisionMonths);
		
		this.context = context;
		this.commisionMonths = commisionMonths;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final CommisionMonthBean commisionMonth = commisionMonths.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView commisionDate = null;
		TextView commisionAmount = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_commision_list_item, parent, false);
			
			commisionDate = (TextView) rowView.findViewById(R.id.commisionDateText);
			commisionAmount = (TextView) rowView.findViewById(R.id.commisionAmountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.commisionDateText = commisionDate;
			viewHolder.commisionAmountText = commisionAmount;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			commisionDate = viewHolder.commisionDateText;
			commisionAmount = viewHolder.commisionAmountText;
		}
		
		commisionDate.setText(CommonUtil.formatMonth(commisionMonth.getMonth()));
		commisionAmount.setText(CommonUtil.formatCurrency(commisionMonth.getAmount()));
		

		rowView.setOnClickListener(getItemOnClickListener(commisionMonth, commisionDate));
		
		CommisionMonthBean selectedCommisionMonth = mCallback.getSelectedCommisionMonth();
		
		if (selectedCommisionMonth != null && selectedCommisionMonth.getMonth().getTime() == commisionMonth.getMonth().getTime()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final CommisionMonthBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCommisionMonthSelected(item);
			}
		};
	}
}
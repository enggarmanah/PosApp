package com.tokoku.pos.report.srvcharge;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.TransactionDayBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServiceChargeDayArrayAdapter extends ArrayAdapter<TransactionDayBean> {

	private Context context;
	private List<TransactionDayBean> transactionDays;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onTransactionDaySelected(TransactionDayBean item);
		
		public TransactionDayBean getSelectedTransactionDay();
	}

	class ViewHolder {
		TextView transactionDateText;
		TextView totalAmountText;
	}

	public ServiceChargeDayArrayAdapter(Context context, List<TransactionDayBean> transactions, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, transactions);
		
		this.context = context;
		this.transactionDays = transactions;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionDayBean transactionDay = transactionDays.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView transDate = null;
		TextView totalAmount = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_transaction_list_item, parent, false);
			
			transDate = (TextView) rowView.findViewById(R.id.transactionDateText);
			totalAmount = (TextView) rowView.findViewById(R.id.totalAmountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.transactionDateText = transDate;
			viewHolder.totalAmountText = totalAmount;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			transDate = viewHolder.transactionDateText;
			totalAmount = viewHolder.totalAmountText;
		}
		
		transDate.setText(CommonUtil.formatDate(transactionDay.getDate()));
		totalAmount.setText(CommonUtil.formatCurrency(transactionDay.getAmount()));

		rowView.setOnClickListener(getItemOnClickListener(transactionDay, transDate));
		
		TransactionDayBean selectedTransactionDay = mCallback.getSelectedTransactionDay();
		
		if (selectedTransactionDay != null && selectedTransactionDay.getDate() == transactionDay.getDate()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final TransactionDayBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onTransactionDaySelected(item);
			}
		};
	}
}
package com.tokoku.pos.report.srvcharge;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServiceChargeMonthArrayAdapter extends ArrayAdapter<TransactionMonthBean> {

	private Context context;
	private List<TransactionMonthBean> transactioMonths;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onTransactionMonthSelected(TransactionMonthBean item);
		
		public TransactionMonthBean getSelectedTransactionMonth();
	}

	class ViewHolder {
		TextView transactionDateText;
		TextView totalAmountText;
	}

	public ServiceChargeMonthArrayAdapter(Context context, List<TransactionMonthBean> transactionMonths, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, transactionMonths);
		
		this.context = context;
		this.transactioMonths = transactionMonths;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionMonthBean transactionMonth = transactioMonths.get(position);
		
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
		
		transDate.setText(CommonUtil.formatMonth(transactionMonth.getMonth()));
		totalAmount.setText(CommonUtil.formatCurrency(transactionMonth.getAmount()));

		rowView.setOnClickListener(getItemOnClickListener(transactionMonth, transDate));
		
		TransactionMonthBean selectedTransactionMonth = mCallback.getSelectedTransactionMonth();
		
		if (selectedTransactionMonth != null && selectedTransactionMonth.getMonth() == transactionMonth.getMonth()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final TransactionMonthBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onTransactionMonthSelected(item);
			}
		};
	}
}
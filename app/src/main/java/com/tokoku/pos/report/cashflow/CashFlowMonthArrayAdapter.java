package com.tokoku.pos.report.cashflow;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.CashFlowMonthBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CashFlowMonthArrayAdapter extends ArrayAdapter<CashFlowMonthBean> {

	private Context context;
	private List<CashFlowMonthBean> transactioMonths;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onCashFlowMonthSelected(CashFlowMonthBean item);
		
		public CashFlowMonthBean getSelectedCashFlowMonth();
	}

	class ViewHolder {
		TextView cashFlowDateText;
		TextView totalAmountText;
	}

	public CashFlowMonthArrayAdapter(Context context, List<CashFlowMonthBean> cashFlowMonths, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, cashFlowMonths);
		
		this.context = context;
		this.transactioMonths = cashFlowMonths;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final CashFlowMonthBean cashFlowMonth = transactioMonths.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView cashFlowDateText = null;
		TextView totalAmountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_transaction_list_item, parent, false);
			
			cashFlowDateText = (TextView) rowView.findViewById(R.id.transactionDateText);
			totalAmountText = (TextView) rowView.findViewById(R.id.totalAmountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.cashFlowDateText = cashFlowDateText;
			viewHolder.totalAmountText = totalAmountText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			cashFlowDateText = viewHolder.cashFlowDateText;
			totalAmountText = viewHolder.totalAmountText;
		}
		
		cashFlowDateText.setText(CommonUtil.formatMonth(cashFlowMonth.getMonth()));
		totalAmountText.setText(CommonUtil.formatCurrency(cashFlowMonth.getAmount()));	
		
		if (cashFlowMonth.getAmount() < 0) {
			totalAmountText.setTextColor(context.getResources().getColor(R.color.text_red));
		} else {
			totalAmountText.setTextColor(context.getResources().getColor(R.color.text_blue));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(cashFlowMonth, cashFlowDateText));
		
		CashFlowMonthBean selectedCashFlowMonth = mCallback.getSelectedCashFlowMonth();
		
		if (selectedCashFlowMonth != null && selectedCashFlowMonth.getMonth().getTime() == cashFlowMonth.getMonth().getTime()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final CashFlowMonthBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCashFlowMonthSelected(item);
			}
		};
	}
}
package com.tokoku.pos.report.cashflow;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.CashFlowYearBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CashFlowYearArrayAdapter extends ArrayAdapter<CashFlowYearBean> {

	private Context context;
	private List<CashFlowYearBean> transactioYears;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onCashFlowYearSelected(CashFlowYearBean item);
		
		public CashFlowYearBean getSelectedCashFlowYear();
	}

	class ViewHolder {
		TextView cashFlowDateText;
		TextView totalAmountText;
	}

	public CashFlowYearArrayAdapter(Context context, List<CashFlowYearBean> cashFlowYears, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, cashFlowYears);
		
		this.context = context;
		this.transactioYears = cashFlowYears;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final CashFlowYearBean cashFlowYear = transactioYears.get(position);
		
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
		
		cashFlowDateText.setText(CommonUtil.formatYear(cashFlowYear.getYear()));
		totalAmountText.setText(CommonUtil.formatCurrency(cashFlowYear.getAmount()));
		
		if (cashFlowYear.getAmount() < 0) {
			totalAmountText.setTextColor(context.getResources().getColor(R.color.text_red));
		} else {
			totalAmountText.setTextColor(context.getResources().getColor(R.color.text_blue));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(cashFlowYear, cashFlowDateText));
		
		CashFlowYearBean selectedCashFlowYear = mCallback.getSelectedCashFlowYear();
		
		if (selectedCashFlowYear != null && selectedCashFlowYear.getYear() == cashFlowYear.getYear()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final CashFlowYearBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCashFlowYearSelected(item);
			}
		};
	}
}
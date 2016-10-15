package com.tokoku.pos.report.bills;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Cashflow;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BillsDetailPaymentArrayAdapter extends ArrayAdapter<Cashflow> {

	private Context context;
	private List<Cashflow> mCashflows;
	
	class ViewHolder {
		TextView dateText;
		TextView amountText;
	}

	public BillsDetailPaymentArrayAdapter(Context context, List<Cashflow> cashflows) {

		super(context, R.layout.report_bills_detail_payment_list_item, cashflows);
		
		this.context = context;
		this.mCashflows = cashflows;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Cashflow cashflow = mCashflows.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView dateText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_bills_detail_payment_list_item, parent, false);
			
			dateText = (TextView) rowView.findViewById(R.id.dateText);
			amountText = (TextView) rowView.findViewById(R.id.amountText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.amountText = amountText;
			viewHolder.dateText = dateText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			amountText = viewHolder.amountText;
			dateText = viewHolder.dateText;
		}
			
		dateText.setText(CommonUtil.formatDate(cashflow.getCashDate()));
	    amountText.setText(CommonUtil.formatCurrency(Math.abs(cashflow.getCashAmount())));
		
		return rowView;
	}
}
package com.tokoku.pos.report.cashflow;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CashFlowDailyArrayAdapter extends ArrayAdapter<Transactions> {

	private Context context;
	private List<Transactions> transactions;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onTransactionsSelected(Transactions item);
	}

	class ViewHolder {
		TextView transactionReferenceNoText;
		TextView transactionDateText;
		TextView transactionRemarksText;
		TextView transactionSupplierText;
	}

	public CashFlowDailyArrayAdapter(Context context, List<Transactions> transactions, ItemActionListener listener) {

		super(context, R.layout.popup_credit_list_item, transactions);
		
		this.context = context;
		this.transactions = transactions;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Transactions transaction = transactions.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView transactionNoText = null;
		TextView transactionDateText = null;
		TextView transactionCustomerText = null;
		TextView transactionAmountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_credit_list_item, parent, false);
			
			transactionNoText = (TextView) rowView.findViewById(R.id.transactionNoText);
			transactionDateText = (TextView) rowView.findViewById(R.id.transactionDateText);
			transactionCustomerText = (TextView) rowView.findViewById(R.id.customerText);
			transactionAmountText = (TextView) rowView.findViewById(R.id.amountText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.transactionReferenceNoText = transactionNoText;
			viewHolder.transactionDateText = transactionDateText;
			viewHolder.transactionRemarksText = transactionCustomerText;
			viewHolder.transactionSupplierText = transactionAmountText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			transactionNoText = viewHolder.transactionReferenceNoText;
			transactionDateText = viewHolder.transactionDateText;
			transactionCustomerText = viewHolder.transactionRemarksText;
			transactionAmountText = viewHolder.transactionSupplierText;
		}
		
		transactionNoText.setText(transaction.getTransactionNo());
		transactionDateText.setText(CommonUtil.formatTime(transaction.getTransactionDate()));
		transactionCustomerText.setText(transaction.getCustomerName());
		
		if (Constant.PAYMENT_TYPE_CREDIT.equals(transaction.getPaymentType())) {
			transactionAmountText.setText(CommonUtil.formatCurrency(transaction.getPaymentAmount()));
		} else {
			transactionAmountText.setText(CommonUtil.formatCurrency(transaction.getTotalAmount()));
		}
		
		if (!CommonUtil.isEmpty(transaction.getCustomerName())) {
			transactionCustomerText.setVisibility(View.VISIBLE);
		} else {
			transactionCustomerText.setVisibility(View.GONE);			
		}
		
		rowView.setOnClickListener(getItemOnClickListener(transaction, transactionNoText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Transactions transaction, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onTransactionsSelected(transaction);
			}
		};
	}
}
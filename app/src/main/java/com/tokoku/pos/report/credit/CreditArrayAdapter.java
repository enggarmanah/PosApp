package com.tokoku.pos.report.credit;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.model.TransactionsBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CreditArrayAdapter extends ArrayAdapter<TransactionsBean> {

	private Context context;
	private List<TransactionsBean> transactions;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onTransactionSelected(TransactionsBean item);
		
		public TransactionsBean getSelectedTransaction();
	}

	class ViewHolder {
		TextView transactionReferenceNoText;
		TextView transactionDateText;
		TextView transactionRemarksText;
		TextView transactionSupplierText;
	}

	public CreditArrayAdapter(Context context, List<TransactionsBean> transactions, ItemActionListener listener) {

		super(context, R.layout.report_credit_list_item, transactions);
		
		this.context = context;
		this.transactions = transactions;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionsBean transaction = transactions.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView transactionNoText = null;
		TextView transactionDateText = null;
		TextView transactionCustomerText = null;
		TextView transactionAmountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_credit_list_item, parent, false);
			
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
		
		Float outstandingAmount = transaction.getTotal_amount() - transaction.getPayment_amount() - transaction.getTotal_credit_payment();
		
		transactionNoText.setText(transaction.getTransaction_no());
		transactionDateText.setText(CommonUtil.formatDate(transaction.getTransaction_date()));
		transactionCustomerText.setText(transaction.getCustomer_name());
		transactionAmountText.setText(CommonUtil.formatCurrency(outstandingAmount));
		
		if (!CommonUtil.isEmpty(transaction.getCustomer_name())) {
			transactionCustomerText.setVisibility(View.VISIBLE);
		} else {
			transactionCustomerText.setVisibility(View.GONE);			
		}
		
		rowView.setOnClickListener(getItemOnClickListener(transaction, transactionNoText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final TransactionsBean transaction, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onTransactionSelected(transaction);
			}
		};
	}
}
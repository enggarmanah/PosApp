package com.tokoku.pos.favorite.customer;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.TransactionItem;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerTransactionArrayAdapter extends ArrayAdapter<TransactionItem> {

	private Context context;
	private List<TransactionItem> transactionItems;

	class ViewHolder {
		TextView productNameText;
		TextView transactionDateText;
		TextView productGroupNameText;
		TextView amountText;
	}

	public CustomerTransactionArrayAdapter(Context context, List<TransactionItem> transactionItems) {

		super(context, R.layout.favorite_customer_transaction_list_item, transactionItems);
		
		this.context = context;
		this.transactionItems = transactionItems;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionItem transactionItem = transactionItems.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView productNameText = null;
		TextView transactionDateText = null;
		TextView productGroupNameText = null;
		TextView amountText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.favorite_customer_transaction_list_item, parent, false);
			
			productNameText = (TextView) rowView.findViewById(R.id.productNameText);
			transactionDateText = (TextView) rowView.findViewById(R.id.transactionDateText);
			productGroupNameText = (TextView) rowView.findViewById(R.id.productGroupNameText);
			amountText = (TextView) rowView.findViewById(R.id.amountText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.productNameText = productNameText;
			viewHolder.transactionDateText = transactionDateText;
			viewHolder.productGroupNameText = productGroupNameText;
			viewHolder.amountText = amountText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			productNameText = viewHolder.productNameText;
			transactionDateText = viewHolder.transactionDateText;
			productGroupNameText = viewHolder.productGroupNameText;
			amountText = viewHolder.amountText;
		}
		
		Float price = transactionItem.getPrice() - transactionItem.getDiscount();
		
		productNameText.setText(transactionItem.getProductName());
		transactionDateText.setText(CommonUtil.formatDate(transactionItem.getTransactions().getTransactionDate()));
		productGroupNameText.setText(transactionItem.getProduct().getProductGroup().getName());
		amountText.setText(CommonUtil.formatNumber(transactionItem.getQuantity()) + "  x  " + CommonUtil.formatCurrency(price));

		return rowView;
	}
}
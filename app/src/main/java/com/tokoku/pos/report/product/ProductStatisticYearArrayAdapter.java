package com.tokoku.pos.report.product;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.TransactionYearBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductStatisticYearArrayAdapter extends ArrayAdapter<TransactionYearBean> {

	private Context context;
	private List<TransactionYearBean> transactioYears;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onTransactionYearSelected(TransactionYearBean item);
		
		public TransactionYearBean getSelectedTransactionYear();
		
		public String getSelectedProductInfo();
	}

	class ViewHolder {
		TextView transactionDateText;
		TextView totalAmountText;
	}

	public ProductStatisticYearArrayAdapter(Context context, List<TransactionYearBean> transactionYears, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, transactionYears);
		
		this.context = context;
		this.transactioYears = transactionYears;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionYearBean transactionYear = transactioYears.get(position);
		
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
		
		transDate.setText(CommonUtil.formatYear(transactionYear.getYear()));
		
		if (Constant.PRODUCT_QUANTITY.equals(mCallback.getSelectedProductInfo())) {
			totalAmount.setText(CommonUtil.formatNumber(transactionYear.getAmount()));
		} else {
			totalAmount.setText(CommonUtil.formatCurrency(transactionYear.getAmount()));	
		}

		rowView.setOnClickListener(getItemOnClickListener(transactionYear, transDate));
		
		TransactionYearBean selectedTransactionYear = mCallback.getSelectedTransactionYear();
		
		if (selectedTransactionYear != null && selectedTransactionYear.getYear() == transactionYear.getYear()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final TransactionYearBean item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onTransactionYearSelected(item);
			}
		};
	}
}
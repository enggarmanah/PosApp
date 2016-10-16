package com.tokoku.pos.report.transaction;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Product;
import com.android.pos.dao.TransactionItem;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TransactionDetailArrayAdapter extends ArrayAdapter<TransactionItem> {

	private Context context;
	private List<TransactionItem> transactionItems;
	private ItemActionListener mCallback;
	
	private ProductDaoService mProductDaoService = new ProductDaoService();

	public interface ItemActionListener {

		public void onItemSelected(TransactionItem item);
	}

	class ViewHolder {
		TextView quantityText;
		TextView unitText;
		TextView nameText;
		TextView remarksText;
		TextView priceText;
		TextView totalPriceText;
	}

	public TransactionDetailArrayAdapter(Context context, List<TransactionItem> transactionItems, ItemActionListener listener) {

		super(context, R.layout.cashier_order_list_item, transactionItems);
		
		this.context = context;
		this.transactionItems = transactionItems;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final TransactionItem transactionItem = transactionItems.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView quantityText = null;
		TextView unitText = null;
		TextView nameText = null;
		TextView remarksText = null;
		TextView priceText = null;
		TextView totalPriceText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.cashier_order_list_item, parent, false);
			
			quantityText = (TextView) rowView.findViewById(R.id.quantityText);
			unitText = (TextView) rowView.findViewById(R.id.unitText);
			nameText = (TextView) rowView.findViewById(R.id.nameText);
			remarksText = (TextView) rowView.findViewById(R.id.infoText);
			priceText = (TextView) rowView.findViewById(R.id.priceText);
			totalPriceText = (TextView) rowView.findViewById(R.id.totalPriceText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.quantityText = quantityText;
			viewHolder.unitText = unitText;
			viewHolder.nameText = nameText;
			viewHolder.remarksText = remarksText;
			viewHolder.priceText = priceText;
			viewHolder.totalPriceText = totalPriceText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			quantityText = viewHolder.quantityText;
			unitText = viewHolder.unitText;
			nameText = viewHolder.nameText;
			remarksText = viewHolder.remarksText;
			priceText = viewHolder.priceText;
			totalPriceText = viewHolder.totalPriceText;
		}
		
		Product product = mProductDaoService.getProduct(transactionItem.getProductId());
		
		quantityText.setText(CommonUtil.formatNumber(transactionItem.getQuantity()));
		unitText.setText(CommonUtil.getShortUnitName(product.getQuantityType()));
		nameText.setText(transactionItem.getProductName());
		
		if (transactionItem.getEmployee() != null) {
			
			remarksText.setText(transactionItem.getEmployee().getName());
			remarksText.setVisibility(View.VISIBLE);
			
		} else {
			
			remarksText.setVisibility(View.GONE);
		}
		
		priceText.setText(CommonUtil.formatCurrencyWithoutSymbol(transactionItem.getPrice()));
		totalPriceText.setText(CommonUtil.formatCurrency(transactionItem.getQuantity() * transactionItem.getPrice()));

		rowView.setOnClickListener(getItemOnClickListener(transactionItem));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final TransactionItem item) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onItemSelected(item);
			}
		};
	}
}
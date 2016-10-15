package com.tokoku.pos.report.inventory;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Product;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InventoryArrayAdapter extends ArrayAdapter<Product> {

	private Context context;
	private List<Product> mProducts;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onProductSelected(Product item);
		
		public Product getSelectedProduct();
	}

	class ViewHolder {
		TextView productNameText;
		TextView productStockText;
	}

	public InventoryArrayAdapter(Context context, List<Product> products, ItemActionListener listener) {

		super(context, R.layout.report_inventory_list_item, products);
		
		this.context = context;
		this.mProducts = products;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Product product = mProducts.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView productNameText = null;
		TextView productStockText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_transaction_list_item, parent, false);
			
			productNameText = (TextView) rowView.findViewById(R.id.transactionDateText);
			productStockText = (TextView) rowView.findViewById(R.id.totalAmountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.productNameText = productNameText;
			viewHolder.productStockText = productStockText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			productNameText = viewHolder.productNameText;
			productStockText = viewHolder.productStockText;
		}
		
		Float stock = CommonUtil.getNvlFloat(product.getStock());
		
		productNameText.setText(product.getName());
		productStockText.setText(CommonUtil.formatNumber(stock));
		
		productStockText.setTextColor(context.getResources().getColor(R.color.list_row_normal_light_text));
		
		if (product.getStock() != null && product.getMinStock() != null && product.getStock() < product.getMinStock()) {
			productStockText.setTextColor(context.getResources().getColor(R.color.text_red));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(product, productNameText));
		
		Product selectedProduct = mCallback.getSelectedProduct();
		
		if (selectedProduct != null && selectedProduct.getId() == product.getId()) {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Product item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onProductSelected(item);
			}
		};
	}
}
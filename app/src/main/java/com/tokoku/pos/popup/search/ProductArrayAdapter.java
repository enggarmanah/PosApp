package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductArrayAdapter extends ArrayAdapter<Product> {

	private Context context;
	private List<Product> products;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onProductSelected(Product item);
	}

	class ViewHolder {
		TextView itemText;
	}

	public ProductArrayAdapter(Context context, List<Product> products, ItemActionListener listener) {

		super(context, R.layout.popup_product_list_item, products);
		
		this.context = context;
		this.products = products;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Product product = products.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		TextView productName = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_product_list_item, parent, false);
			productName = (TextView) rowView.findViewById(R.id.nameText);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemText = productName;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			productName = viewHolder.itemText;
		}
		
		productName.setText(product.getName());

		rowView.setOnClickListener(getItemOnClickListener(product, productName));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Product product, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onProductSelected(product);
			}
		};
	}
}
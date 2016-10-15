package com.tokoku.pos.cashier;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Product;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CashierProductArrayAdapter extends ArrayAdapter<Product> {

	private Context context;
	private List<Product> products;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onProductSelected(Product item);
	}

	class ViewHolder {
		
		TextView codeText;
		TextView nameText;
		TextView priceText;
	}

	public CashierProductArrayAdapter(Context context, List<Product> products, ItemActionListener listener) {

		super(context, R.layout.cashier_product_list_item, products);
		
		this.context = context;
		this.products = products;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Product product = products.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView prdCode = null;
		TextView prdName = null;
		TextView prdPrice = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.cashier_product_list_item, parent, false);
			
			prdCode = (TextView) rowView.findViewById(R.id.codeText);
			prdName = (TextView) rowView.findViewById(R.id.nameText);
			prdPrice = (TextView) rowView.findViewById(R.id.priceText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.codeText = prdCode;
			viewHolder.nameText = prdName;
			viewHolder.priceText = prdPrice;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			prdCode = viewHolder.codeText;
			prdName = viewHolder.nameText;
			prdPrice = viewHolder.priceText;
		}
		
		prdCode.setText(product.getCode());
		prdName.setText(product.getName() + "  [" + CommonUtil.getShortUnitName(product.getQuantityType()) + "]");
		
		prdPrice.setText(CommonUtil.formatCurrency(CommonUtil.getCurrentPrice(product)));
		
		if (CommonUtil.isEmpty(product.getCode())) {
			prdCode.setVisibility(View.GONE);
		} else {
			prdCode.setVisibility(View.VISIBLE);
		}
		
		if (UserUtil.isWaitress()) {
			prdPrice.setVisibility(View.GONE);
		} else {
			prdPrice.setVisibility(View.VISIBLE);
		}
		
		rowView.setOnClickListener(getItemOnClickListener(product, prdName));

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
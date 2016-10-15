package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.ProductGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductGroupArrayAdapter extends ArrayAdapter<ProductGroup> {

	private Context context;
	private List<ProductGroup> productGroups;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onProductGroupSelected(ProductGroup item);
	}

	class ViewHolder {
		TextView itemText;
	}

	public ProductGroupArrayAdapter(Context context, List<ProductGroup> productGroups, ItemActionListener listener) {

		super(context, R.layout.popup_product_group_list_item, productGroups);
		
		this.context = context;
		this.productGroups = productGroups;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ProductGroup productGroup = productGroups.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		TextView productGroupName = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_product_group_list_item, parent, false);
			productGroupName = (TextView) rowView.findViewById(R.id.nameText);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemText = productGroupName;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			productGroupName = viewHolder.itemText;
		}
		
		productGroupName.setText(productGroup.getName());

		rowView.setOnClickListener(getItemOnClickListener(productGroup, productGroupName));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final ProductGroup productGroup, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onProductGroupSelected(productGroup);
			}
		};
	}
}
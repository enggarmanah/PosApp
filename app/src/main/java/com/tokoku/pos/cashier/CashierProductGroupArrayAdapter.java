package com.tokoku.pos.cashier;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.ProductGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CashierProductGroupArrayAdapter extends ArrayAdapter<ProductGroup> {

	private Context context;
	private List<ProductGroup> productGroups;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onProductGroupSelected(ProductGroup item);
	}

	class ViewHolder {
		TextView itemText;
	}

	public CashierProductGroupArrayAdapter(Context context, List<ProductGroup> prdGroups, ItemActionListener listener) {

		super(context, R.layout.cashier_product_group_list_item, prdGroups);
		
		this.context = context;
		this.productGroups = prdGroups;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ProductGroup prdGroup = productGroups.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		TextView prdGroupName = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.cashier_product_group_list_item, parent, false);
			prdGroupName = (TextView) rowView.findViewById(R.id.nameText);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemText = prdGroupName;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			prdGroupName = viewHolder.itemText;
		}
		
		prdGroupName.setText(prdGroup.getName());

		rowView.setOnClickListener(getItemOnClickListener(prdGroup, prdGroupName));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final ProductGroup item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onProductGroupSelected(item);
			}
		};
	}
}
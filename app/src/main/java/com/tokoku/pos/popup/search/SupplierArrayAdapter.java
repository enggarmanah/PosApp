package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SupplierArrayAdapter extends ArrayAdapter<Supplier> {

	private Context context;
	private List<Supplier> suppliers;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onSupplierSelected(Supplier item);
	}

	class ViewHolder {
		TextView itemText;
	}

	public SupplierArrayAdapter(Context context, List<Supplier> suppliers, ItemActionListener listener) {

		super(context, R.layout.popup_supplier_list_item, suppliers);
		
		this.context = context;
		this.suppliers = suppliers;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Supplier supplier = suppliers.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		TextView supplierName = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_supplier_list_item, parent, false);
			supplierName = (TextView) rowView.findViewById(R.id.nameText);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemText = supplierName;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			supplierName = viewHolder.itemText;
		}
		
		supplierName.setText(supplier.getName());

		rowView.setOnClickListener(getItemOnClickListener(supplier, supplierName));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Supplier supplier, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onSupplierSelected(supplier);
			}
		};
	}
}
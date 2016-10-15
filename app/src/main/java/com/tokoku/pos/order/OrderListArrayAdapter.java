package com.tokoku.pos.order;

import java.util.List;

import com.tokoku.pos.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderListArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private List<String> orderReferences;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onOrderReferenceSelected(String item);
		
		public String getSelectedOrderReference();
	}

	class ViewHolder {
		TextView orderReferenceText;
	}

	public OrderListArrayAdapter(Context context, List<String> order, ItemActionListener listener) {

		super(context, R.layout.report_transaction_list_item, order);
		
		this.context = context;
		this.orderReferences = order;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final String orderReference = orderReferences.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView orderReferenceText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.order_list_item, parent, false);
			
			orderReferenceText = (TextView) rowView.findViewById(R.id.orderReferenceText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.orderReferenceText = orderReferenceText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			orderReferenceText = viewHolder.orderReferenceText;
		}
		
		orderReferenceText.setText(orderReference);
		
		rowView.setOnClickListener(getItemOnClickListener(orderReference, orderReferenceText));
		
		String selectedOrderReference = mCallback.getSelectedOrderReference();
		
		if (selectedOrderReference != null && selectedOrderReference.equals(orderReference)) {
			rowView.setBackground(context.getResources().getDrawable(R.drawable.bg_order_selected));
		} else {
			rowView.setBackground(context.getResources().getDrawable(R.drawable.bg_order_normal));
		}
		
		if (orderReference.length() > 2) {
			orderReferenceText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		}

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final String item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onOrderReferenceSelected(item);
			}
		};
	}
}
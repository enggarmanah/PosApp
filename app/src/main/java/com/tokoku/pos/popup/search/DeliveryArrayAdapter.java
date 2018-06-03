package com.tokoku.pos.popup.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.tokoku.pos.model.Delivery;
import com.tokoku.pos.util.CommonUtil;

import java.util.List;

public class DeliveryArrayAdapter extends ArrayAdapter<Delivery> {

	private Context context;
	private List<Delivery> deliveries;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onDeliverySelected(Delivery item);
	}

	class ViewHolder {
		TextView noText;
		TextView dateText;
	}

	public DeliveryArrayAdapter(Context context, List<Delivery> deliveries, ItemActionListener listener) {

		super(context, R.layout.popup_delivery_list_item, deliveries);
		
		this.context = context;
		this.deliveries = deliveries;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Delivery delivery = deliveries.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView noText = null;
		TextView dateText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_delivery_list_item, parent, false);
			
			noText = (TextView) rowView.findViewById(R.id.noText);
			dateText = (TextView) rowView.findViewById(R.id.dateText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.noText = noText;
			viewHolder.dateText = dateText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			noText = viewHolder.noText;
			dateText = viewHolder.dateText;
		}
		
		noText.setText(delivery.getNo());
		dateText.setText(CommonUtil.formatDate(delivery.getDate()));
		
		rowView.setOnClickListener(getItemOnClickListener(delivery, noText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Delivery delivery, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onDeliverySelected(delivery);
			}
		};
	}
}
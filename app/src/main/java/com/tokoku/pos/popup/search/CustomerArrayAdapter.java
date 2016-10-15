package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerArrayAdapter extends ArrayAdapter<Customer> {

	private Context context;
	private List<Customer> customers;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onCustomerSelected(Customer item);
	}

	class ViewHolder {
		TextView nameText;
		TextView telephoneText;
		TextView addressText;
	}

	public CustomerArrayAdapter(Context context, List<Customer> customers, ItemActionListener listener) {

		super(context, R.layout.popup_customer_list_item, customers);
		
		this.context = context;
		this.customers = customers;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Customer customer = customers.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		TextView telephoneText = null;
		TextView addressText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_customer_list_item, parent, false);
			
			nameText = (TextView) rowView.findViewById(R.id.nameText);
			telephoneText = (TextView) rowView.findViewById(R.id.telephoneText);
			addressText = (TextView) rowView.findViewById(R.id.addressText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.nameText = nameText;
			viewHolder.telephoneText = telephoneText;
			viewHolder.addressText = addressText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			nameText = viewHolder.nameText;
			telephoneText = viewHolder.telephoneText;
			addressText = viewHolder.addressText;
		}
		
		nameText.setText(customer.getName());
		telephoneText.setText(customer.getTelephone());
		addressText.setText(customer.getAddress());
		
		if (CommonUtil.isEmpty(customer.getTelephone())) {
			telephoneText.setVisibility(View.GONE);
		} else {
			telephoneText.setVisibility(View.VISIBLE);
		}
		
		if (CommonUtil.isEmpty(customer.getAddress())) {
			addressText.setVisibility(View.GONE);
		} else {
			addressText.setVisibility(View.VISIBLE);
		}

		rowView.setOnClickListener(getItemOnClickListener(customer, nameText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Customer customer, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCustomerSelected(customer);
			}
		};
	}
}
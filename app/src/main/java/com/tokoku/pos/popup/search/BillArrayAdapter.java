package com.tokoku.pos.popup.search;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BillArrayAdapter extends ArrayAdapter<Bills> {

	private Context context;
	private List<Bills> bills;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onBillsSelected(Bills item);
	}

	class ViewHolder {
		TextView billReferenceNoText;
		TextView billDateText;
		TextView billRemarksText;
		TextView billSupplierText;
	}

	public BillArrayAdapter(Context context, List<Bills> bills, ItemActionListener listener) {

		super(context, R.layout.popup_bill_list_item, bills);
		
		this.context = context;
		this.bills = bills;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Bills bill = bills.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView billReferenceNoText = null;
		TextView billDateText = null;
		TextView billRemarksText = null;
		TextView billSupplierText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_bill_list_item, parent, false);
			
			billReferenceNoText = (TextView) rowView.findViewById(R.id.billReferenceNoText);
			billDateText = (TextView) rowView.findViewById(R.id.billDateText);
			billRemarksText = (TextView) rowView.findViewById(R.id.remarksText);
			billSupplierText = (TextView) rowView.findViewById(R.id.supplierText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.billReferenceNoText = billReferenceNoText;
			viewHolder.billDateText = billDateText;
			viewHolder.billRemarksText = billRemarksText;
			viewHolder.billSupplierText = billSupplierText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			billReferenceNoText = viewHolder.billReferenceNoText;
			billDateText = viewHolder.billDateText;
			billRemarksText = viewHolder.billRemarksText;
			billSupplierText = viewHolder.billSupplierText;
		}
		
		billReferenceNoText.setText(bill.getBillReferenceNo());
		billDateText.setText(CommonUtil.formatDate(bill.getBillDate()));
		billRemarksText.setText(bill.getRemarks());
		billSupplierText.setText(bill.getSupplierName());
		
		if (CommonUtil.isEmpty(bill.getRemarks())) {
			billRemarksText.setVisibility(View.GONE);
		} else {
			billRemarksText.setVisibility(View.VISIBLE);
		}
		
		if (bill.getSupplier() == null) {
			billSupplierText.setVisibility(View.GONE);
		} else {
			billSupplierText.setVisibility(View.VISIBLE);
		}
		
		rowView.setOnClickListener(getItemOnClickListener(bill, billReferenceNoText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Bills bill, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onBillsSelected(bill);
			}
		};
	}
}
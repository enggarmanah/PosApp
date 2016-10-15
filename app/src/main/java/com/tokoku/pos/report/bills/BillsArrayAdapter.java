package com.tokoku.pos.report.bills;

import java.util.Date;
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

public class BillsArrayAdapter extends ArrayAdapter<Bills> {

	private Context context;
	private List<Bills> mBills;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onBillSelected(Bills item);
		
		public Bills getSelectedBill();
	}
	
	class ViewHolder {
		TextView itemText;
		TextView remarksText;
		TextView billDateText;
		TextView billAmountText;
		TextView supplierText;
	}
	
	public BillsArrayAdapter(Context context, List<Bills> bills, ItemActionListener listener) {

		super(context, R.layout.bills_list_item, bills);
		
		this.context = context;
		this.mBills = bills;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Bills bill = mBills.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView billReferenceNoText = null;
		TextView remarksText = null;
		TextView supplierText = null;
		TextView billAmountText = null;
		TextView billDateText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.bills_list_item, parent, false);
			
			billReferenceNoText = (TextView) rowView.findViewById(R.id.nameText);
			remarksText = (TextView) rowView.findViewById(R.id.remarksText);
			supplierText = (TextView) rowView.findViewById(R.id.supplierText);
			billAmountText = (TextView) rowView.findViewById(R.id.billAmountText);
			billDateText = (TextView) rowView.findViewById(R.id.billDate);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.itemText = billReferenceNoText;
			viewHolder.remarksText = remarksText;
			viewHolder.supplierText = supplierText;
			viewHolder.billAmountText = billAmountText;
			viewHolder.billDateText = billDateText;
			
			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			billReferenceNoText = viewHolder.itemText;
			remarksText = viewHolder.remarksText;
			billAmountText = viewHolder.billAmountText;
			billDateText = viewHolder.billDateText;
			supplierText = viewHolder.supplierText;
		}
			
		String remarks = bill.getRemarks();
	    
	    billReferenceNoText.setText(bill.getBillReferenceNo());
	    
	    if (CommonUtil.isEmpty(bill.getBillReferenceNo())) {
	    	billReferenceNoText.setText(context.getString(R.string.bill_no_receipt));
		}
	    
		billDateText.setText(CommonUtil.formatDate(bill.getBillDate()));
		remarksText.setText(remarks);
		
		float payment = CommonUtil.getNvlFloat(bill.getPayment());
		float billAmount = CommonUtil.getNvlFloat(bill.getBillAmount());
		
		if (payment < billAmount) {
			billAmountText.setText(CommonUtil.formatCurrency(billAmount - payment));
			
			Date dueDate = bill.getBillDueDate();
			Date curDate = new Date();
			
			if (dueDate != null) {
				billDateText.setText(CommonUtil.formatDate(bill.getBillDueDate()));
			}
			
			if (dueDate != null && curDate.getTime() > dueDate.getTime()) {
				billAmountText.setTextColor(context.getResources().getColor(R.color.text_red));
			} else {
				billAmountText.setTextColor(context.getResources().getColor(R.color.text_orange));
			}
		} else {
			billDateText.setText(CommonUtil.formatDate(bill.getPaymentDate()));
			billAmountText.setText(CommonUtil.formatCurrency(payment));
			billAmountText.setTextColor(context.getResources().getColor(R.color.text_medium));
		}
		
		supplierText.setText(bill.getSupplierName());
		
		if (!CommonUtil.isEmpty(bill.getSupplierName())) {
			supplierText.setVisibility(View.VISIBLE);
		} else {
			supplierText.setVisibility(View.GONE);
		}
		
		if (mCallback.getSelectedBill() != null && mCallback.getSelectedBill().getId() == bill.getId()) {
			
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
			
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(bill, billReferenceNoText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Bills item, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onBillSelected(item);
			}
		};
	}
}
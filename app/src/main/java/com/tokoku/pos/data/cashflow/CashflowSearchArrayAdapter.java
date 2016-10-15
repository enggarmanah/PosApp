package com.tokoku.pos.data.cashflow;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Cashflow;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CustomerDaoService;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CashflowSearchArrayAdapter extends BaseSearchArrayAdapter<Cashflow> {
	
	BillsDaoService billDaoService = new BillsDaoService();
	TransactionsDaoService transactionDaoService = new TransactionsDaoService();
	SupplierDaoService supplierDaoService = new SupplierDaoService();
	CustomerDaoService customerDaoService = new CustomerDaoService();
	
	class ViewHolder {
		ImageView flowImage;
		TextView typeText;
		TextView remarksText;
		TextView cashDateText;
		TextView cashAmountText;
		TextView entityText;
	}
	
	public CashflowSearchArrayAdapter(Context context, List<Cashflow> cashflows, Cashflow selectedCashflow, ItemActionListener<Cashflow> listener) {
		super(context, R.layout.report_cashflow_list_item, cashflows, selectedCashflow, listener);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Cashflow item = items.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		ImageView flowImage = null;
		TextView typeText = null;
		TextView remarksText = null;
		TextView cashAmountText = null;
		TextView cashDateText = null;
		TextView entityText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_cashflow_list_item, parent, false);
			
			flowImage = (ImageView) rowView.findViewById(R.id.flowImage);
			typeText = (TextView) rowView.findViewById(R.id.typeText);
			remarksText = (TextView) rowView.findViewById(R.id.remarksText);
			cashAmountText = (TextView) rowView.findViewById(R.id.cashAmountText);
			cashDateText = (TextView) rowView.findViewById(R.id.cashDateText);
			entityText = (TextView) rowView.findViewById(R.id.entityText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.flowImage = flowImage;
			viewHolder.typeText = typeText;
			viewHolder.remarksText = remarksText;
			viewHolder.cashAmountText = cashAmountText;
			viewHolder.cashDateText = cashDateText;
			viewHolder.entityText = entityText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			flowImage = viewHolder.flowImage;
			typeText = viewHolder.typeText;
			remarksText = viewHolder.remarksText;
			cashAmountText = viewHolder.cashAmountText;
			cashDateText = viewHolder.cashDateText;
			entityText = viewHolder.entityText;
		}
			
		int padding = flowImage.getPaddingLeft();
		
		flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_black));
		flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_out));
		
		if (Constant.CASHFLOW_TYPE_CAPITAL_IN.equals(item.getType()) ||
			Constant.CASHFLOW_TYPE_BANK_WITHDRAWAL.equals(item.getType()) ||
			Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(item.getType())) {
			
			flowImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_forward_black));
			flowImage.setBackground(context.getResources().getDrawable(R.drawable.bg_flow_in));
		}
		
	    flowImage.setPadding(padding, padding, padding, padding);
	    
	    float cashAmount = CommonUtil.getNvlFloat(item.getCashAmount());
		
	    typeText.setText(CodeUtil.getCashflowTypeLabel(item.getType()));
	    cashAmountText.setText(CommonUtil.formatCurrency(Math.abs(cashAmount)));
		cashDateText.setText(CommonUtil.formatDate(item.getCashDate()));
		
		String billReferenceNo = null;
		String supplierName = null;
		
		if (item.getBillId() != null) {
			
			Bills bill = billDaoService.getBills(item.getBillId());
			
			billReferenceNo = bill.getBillReferenceNo();
			
			if (bill.getSupplierId() != null) {
				supplierName = supplierDaoService.getSupplier(bill.getSupplierId()).getName();
			}
		}
		
		String transactionNo = null;
		String customerName = null;
		
		if (item.getTransactionId() != null) {
			
			Transactions transaction = transactionDaoService.getTransactions(item.getTransactionId());
			
			transactionNo = transaction.getTransactionNo();
			customerName = customerDaoService.getCustomer(transaction.getCustomerId()).getName();
		}
		
		String remarks = Constant.EMPTY_STRING;
		
		if (!CommonUtil.isEmpty(billReferenceNo)) {
			remarks = billReferenceNo;
			
		} else if (!CommonUtil.isEmpty(transactionNo)) {
			remarks = transactionNo;
		}
		
		if (!CommonUtil.isEmpty(item.getRemarks())) {
			
			if (!CommonUtil.isEmpty(remarks)) {
				remarks += ". ";
			}
			remarks += item.getRemarks();
		}
		
		if (!CommonUtil.isEmpty(remarks)) {
			remarksText.setText(remarks);
			remarksText.setVisibility(View.VISIBLE);
		} else {
			remarksText.setVisibility(View.GONE);
		}
		
		if (!CommonUtil.isEmpty(supplierName)) {
			entityText.setText(supplierName);
			entityText.setVisibility(View.VISIBLE);
		
		} else if (!CommonUtil.isEmpty(supplierName)){
			entityText.setText(customerName);
			entityText.setVisibility(View.VISIBLE);
		
		} else {
			entityText.setVisibility(View.GONE);
		}
		
		if (selectedItem != null && getItemId(selectedItem) == getItemId(item)) {
			
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
			mSelectedView = rowView;
			
		} else {
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
		}
		
		rowView.setOnClickListener(getItemOnClickListener(item, typeText));

		return rowView;
	}
	
	@Override
	public Long getItemId(Cashflow cashflow) {
		
		return cashflow.getId();
	}
	
	@Override
	public String getItemName(Cashflow cashflow) {
		
		return CodeUtil.getCashflowTypeLabel(cashflow.getType());
	}
}
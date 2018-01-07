package com.tokoku.pos.report.cashflow;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.BillsDaoService;
import com.tokoku.pos.dao.CustomerDaoService;
import com.tokoku.pos.dao.SupplierDaoService;
import com.tokoku.pos.dao.TransactionsDaoService;
import com.tokoku.pos.model.CashflowBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CashFlowDetailArrayAdapter extends ArrayAdapter<CashflowBean> {
	
	private Context mContext;
	
	BillsDaoService billDaoService = new BillsDaoService();
	TransactionsDaoService transactionDaoService = new TransactionsDaoService();
	SupplierDaoService supplierDaoService = new SupplierDaoService();
	CustomerDaoService customerDaoService = new CustomerDaoService();
	
	private ItemActionListener mCallback;
	
	List<CashflowBean> mCashflows;
	
	public interface ItemActionListener {

		public void onCashflowSelected(CashflowBean item);
	}
	
	class ViewHolder {
		
		ImageView flowImage;
		TextView typeText;
		TextView remarksText;
		TextView cashDateText;
		TextView cashAmountText;
		TextView entityText;
	}
	
	public CashFlowDetailArrayAdapter(Context context, List<CashflowBean> cashflows, ItemActionListener listener) {
		
		super(context, R.layout.report_cashflow_list_item, cashflows);

		mContext = context;
		mCashflows = cashflows;
		mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final CashflowBean cashflow = mCashflows.get(position);
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		
		flowImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_back_black));
		flowImage.setBackground(mContext.getResources().getDrawable(R.drawable.bg_flow_out));
		
		if (Constant.CASHFLOW_TYPE_CAPITAL_IN.equals(cashflow.getType()) ||
			Constant.CASHFLOW_TYPE_BANK_WITHDRAWAL.equals(cashflow.getType()) ||
			Constant.CASHFLOW_TYPE_INVC_PAYMENT.equals(cashflow.getType()) ||
			cashflow.getType() == null) {
			
			flowImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_forward_black));
			flowImage.setBackground(mContext.getResources().getDrawable(R.drawable.bg_flow_in));
		}
		
	    flowImage.setPadding(padding, padding, padding, padding);
		
	    if (cashflow.getType() != null) {
	    	typeText.setText(CodeUtil.getCashflowTypeLabel(cashflow.getType()));
	    } else {
	    	typeText.setText(mContext.getString(R.string.transaction_daily));
	    }
	    
	    cashAmountText.setText(CommonUtil.formatCurrency(Math.abs(cashflow.getCash_amount())));
		cashDateText.setText(CommonUtil.formatDate(cashflow.getCash_date()));
		
		rowView.setOnClickListener(getItemOnClickListener(cashflow));
		
		String billReferenceNo = null;
		String supplierName = null;
		
		if (cashflow.getBill_id() != null) {
			
			Bills bill = billDaoService.getBills(cashflow.getBill_id());
			
			billReferenceNo = bill.getBillReferenceNo();
			
			if (bill.getSupplierId() != null) {
				supplierName = supplierDaoService.getSupplier(bill.getSupplierId()).getName();
			}
		}
		
		String transactionNo = null;
		String customerName = null;
		
		if (cashflow.getTransaction_id() != null) {
			
			Transactions transaction = transactionDaoService.getTransactions(cashflow.getTransaction_id());
			
			transactionNo = transaction.getTransactionNo();
			customerName = customerDaoService.getCustomer(transaction.getCustomerId()).getName();
		}
		
		String remarks = Constant.EMPTY_STRING;
		
		if (!CommonUtil.isEmpty(billReferenceNo)) {
			remarks = billReferenceNo;
			
		} else if (!CommonUtil.isEmpty(transactionNo)) {
			remarks = transactionNo;
		}
		
		if (!CommonUtil.isEmpty(cashflow.getRemarks())) {
			
			if (!CommonUtil.isEmpty(remarks)) {
				remarks += ". ";
			}
			remarks += cashflow.getRemarks();
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
		
		} else if (!CommonUtil.isEmpty(customerName)){
			entityText.setText(customerName);
			entityText.setVisibility(View.VISIBLE);
		
		} else {
			entityText.setVisibility(View.GONE);
		}
		
		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final CashflowBean item) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onCashflowSelected(item);
			}
		};
	}
}
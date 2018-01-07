package com.tokoku.pos.report.commission;

import java.util.List;

import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.tokoku.pos.model.EmployeeCommissionBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommissionDetailEmployeeArrayAdapter extends ArrayAdapter<EmployeeCommissionBean> {

	private Context context;
	private List<EmployeeCommissionBean> employeeCommisions;
	
	class ViewHolder {
		
		TextView dateText;
		TextView productNameText;
		TextView commisionText;
	}

	public CommissionDetailEmployeeArrayAdapter(Context context, List<EmployeeCommissionBean> employeeCommisions) {

		super(context, R.layout.report_commision_detail_employee_list_item, employeeCommisions);
		
		this.context = context;
		this.employeeCommisions = employeeCommisions;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		EmployeeCommissionBean employeeCommision = employeeCommisions.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView dateText = null;
		TextView productNameText = null;
		TextView commisionText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_commision_detail_employee_list_item, parent, false);
			
			dateText = (TextView) rowView.findViewById(R.id.dateText);
			productNameText = (TextView) rowView.findViewById(R.id.productNameText);
			commisionText = (TextView) rowView.findViewById(R.id.commisionText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.dateText = dateText;
			viewHolder.productNameText = productNameText;
			viewHolder.commisionText = commisionText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			dateText = viewHolder.dateText;
			productNameText = viewHolder.productNameText;
			commisionText = viewHolder.commisionText;
		}
		
		dateText.setText(CommonUtil.formatDateMonthTime(employeeCommision.getTransaction_date()));
		productNameText.setText(employeeCommision.getQuantity() + Constant.SPACE_STRING + employeeCommision.getProduct_name());
		commisionText.setText(CommonUtil.formatCurrency(employeeCommision.getCommision()));
		
		return rowView;
	}
}
package com.tokoku.pos.report.commission;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.model.EmployeeCommissionBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommissionDetailArrayAdapter extends ArrayAdapter<EmployeeCommissionBean> {

	private Context context;
	private List<EmployeeCommissionBean> employeeCommisions;
	private ItemActionListener mCallback;
	
	public interface ItemActionListener {

		public void onEmployeeSelected(Employee employee);
	}

	class ViewHolder {
		TextView employeeNameText;
		TextView commisionText;
	}

	public CommissionDetailArrayAdapter(Context context, List<EmployeeCommissionBean> employeeCommisions, ItemActionListener listener) {

		super(context, R.layout.report_commision_detail_list_item, employeeCommisions);
		
		this.context = context;
		this.employeeCommisions = employeeCommisions;
		mCallback = (ItemActionListener) listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		EmployeeCommissionBean employeeCommision = employeeCommisions.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView employeeNameText = null;
		TextView commisionText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_commision_detail_list_item, parent, false);
			
			employeeNameText = (TextView) rowView.findViewById(R.id.employeeNameText);
			commisionText = (TextView) rowView.findViewById(R.id.commisionText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.employeeNameText = employeeNameText;
			viewHolder.commisionText = commisionText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			employeeNameText = viewHolder.employeeNameText;
			commisionText = viewHolder.commisionText;
		}
		
		employeeNameText.setText(employeeCommision.getEmployee_name());
		commisionText.setText(CommonUtil.formatCurrency(employeeCommision.getCommision()));
		
		Employee employee = new Employee();
		
		employee.setId(employeeCommision.getEmployee_id());
		employee.setName(employeeCommision.getEmployee_name());
		
		rowView.setOnClickListener(getEmployeeOnClickListener(employee));
		
		return rowView;
	}
	
	private View.OnClickListener getEmployeeOnClickListener(final Employee employee) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mCallback.onEmployeeSelected(employee);
			}
		};
	}
}
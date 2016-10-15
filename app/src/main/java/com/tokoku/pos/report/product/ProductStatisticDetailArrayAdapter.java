package com.tokoku.pos.report.product;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.ProductStatisticBean;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductStatisticDetailArrayAdapter extends ArrayAdapter<ProductStatisticBean> {

	private Context context;
	private List<ProductStatisticBean> productStatistics;
	private ItemActionListener mCallback;
	
	public interface ItemActionListener {

		public String getProductInfo();
	}

	class ViewHolder {
		TextView productNameText;
		TextView saleCountText;
	}

	public ProductStatisticDetailArrayAdapter(Context context, List<ProductStatisticBean> productStatistics, ItemActionListener listener) {

		super(context, R.layout.report_product_statistic_list_item, productStatistics);
		
		this.context = context;
		this.productStatistics = productStatistics;
		mCallback = (ItemActionListener) listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ProductStatisticBean productStatistic = productStatistics.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView productName = null;
		TextView saleCount = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.report_product_statistic_list_item, parent, false);
			
			productName = (TextView) rowView.findViewById(R.id.productNameText);
			saleCount = (TextView) rowView.findViewById(R.id.saleCountText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.productNameText = productName;
			viewHolder.saleCountText = saleCount;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			productName = viewHolder.productNameText;
			saleCount = viewHolder.saleCountText;
		}
		
		productName.setText(productStatistic.getProduct_name());
		
		if (Constant.PRODUCT_QUANTITY.equals(mCallback.getProductInfo())) {
			saleCount.setText(CommonUtil.formatNumber(productStatistic.getValue()));
		} else {
			saleCount.setText(CommonUtil.formatCurrency(productStatistic.getValue()));
		}

		return rowView;
	}
}
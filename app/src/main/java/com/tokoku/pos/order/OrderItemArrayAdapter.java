package com.tokoku.pos.order;

import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.tokoku.pos.dao.OrdersDaoService;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.PrintUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class OrderItemArrayAdapter extends ArrayAdapter<OrderItem> {

	private Context context;
	private List<OrderItem> orderItems;
	private ItemActionListener mCallback;
	
	public interface ItemActionListener {
		
		public Boolean getOrderStatus(Long orderId);
		
		public void setOrderStatus(Long orderId, boolean isSelected);
		
		public void onOrderItemSelected(OrderItem orderItem);
		
		public void onSetMessage(String message);
	}

	class ViewHolder {
		TextView productNameText;
		TextView productInfoText;
		TextView productQuantityText;
		ImageButton printButton;
		ImageButton checkedButton;
	}

	public OrderItemArrayAdapter(Context context, List<OrderItem> productStatistics, ItemActionListener listener) {

		super(context, R.layout.order_item_list_item, productStatistics);
		
		this.context = context;
		this.orderItems = productStatistics;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final OrderItem orderItem = orderItems.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView productNameText = null;
		TextView productInfoText = null;
		TextView productQuantityText = null;
		ImageButton printButton = null;
		ImageButton checkedButton = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.order_item_list_item, parent, false);
			
			productNameText = (TextView) rowView.findViewById(R.id.productNameText);
			productInfoText = (TextView) rowView.findViewById(R.id.productInfoText);
			productQuantityText = (TextView) rowView.findViewById(R.id.productQuantityText);
			printButton = (ImageButton) rowView.findViewById(R.id.printButton);
			checkedButton = (ImageButton) rowView.findViewById(R.id.checkedButton);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.productNameText = productNameText;
			viewHolder.productInfoText = productInfoText;
			viewHolder.productQuantityText = productQuantityText;
			viewHolder.printButton = printButton;
			viewHolder.checkedButton = checkedButton;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			productNameText = viewHolder.productNameText;
			productInfoText = viewHolder.productInfoText;
			productQuantityText = viewHolder.productQuantityText;
			printButton = viewHolder.printButton;
			checkedButton = viewHolder.checkedButton;
		}
		
		// header
		if (orderItem.getId() == null) {
			
			productNameText.setText(orderItem.getProductName());
			productInfoText.setVisibility(View.GONE);
			productQuantityText.setVisibility(View.GONE);
			
			printButton.setVisibility(View.VISIBLE);
			checkedButton.setVisibility(View.VISIBLE);
			
			boolean isSelected = mCallback.getOrderStatus(orderItem.getOrderId());
			
			if (isSelected) {
				checkedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black));
			} else {
				checkedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_clear_black));
			}
			
			printButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Orders order = new OrdersDaoService().getOrders(orderItem.getOrderId());
					
					if (!PrintUtil.isPrinterConnected()) {
						mCallback.onSetMessage(context.getString(R.string.printer_cant_print));
						return;
					}
					
					try {
						PrintUtil.printOrder(order);
					} catch (Exception e) {
						mCallback.onSetMessage(context.getString(R.string.printer_cant_print));
					}
				}
			});
			
			final ImageButton button = checkedButton;
			
			checkedButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					boolean isSelected = !mCallback.getOrderStatus(orderItem.getOrderId());
					mCallback.setOrderStatus(orderItem.getOrderId(), isSelected);
					
					if (isSelected) {
						button.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black));
					} else {
						button.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_clear_black));
					}
				}
			});
			
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_selected_background));
			
		// content
		} else {
			
			productQuantityText.setVisibility(View.VISIBLE);
			printButton.setVisibility(View.GONE);
			checkedButton.setVisibility(View.GONE);
			
			productNameText.setText(orderItem.getProductName());
			productQuantityText.setText(CommonUtil.formatNumber(orderItem.getQuantity()));
			
			if (!CommonUtil.isEmpty(orderItem.getRemarks())) {
				
				productInfoText.setText(orderItem.getRemarks());
				productInfoText.setVisibility(View.VISIBLE);
				
			} else if (orderItem.getEmployee() != null) {
				
				productInfoText.setText(orderItem.getEmployee().getName());
				productInfoText.setVisibility(View.VISIBLE);
				
			} else {
				
				productInfoText.setVisibility(View.GONE);
			}
			
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_row_normal_background));
			
			rowView.setOnClickListener(getItemOnClickListener(orderItem));
		}
		
		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final OrderItem item) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onOrderItemSelected(item);
			}
		};
	}
}
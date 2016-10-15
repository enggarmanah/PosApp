package com.tokoku.pos.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.OrderItemDaoService;
import com.tokoku.pos.dao.OrdersDaoService;
import com.tokoku.pos.model.TransactionMonthBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.NotificationUtil;

public class OrderDetailFragment extends BaseFragment implements OrderItemArrayAdapter.ItemActionListener {
	
	private TextView mOrderInfo;
	
	private ImageButton mBackButton;
	private ImageButton mAddButton;
	
	protected TransactionMonthBean mTransactionMonth;
	
	protected String mOrderReference;
	
	private OrderActionListener mActionListener;
	
	private OrdersDaoService mOrderDaoService = new OrdersDaoService();
	private OrderItemDaoService mOrderItemDaoService = new OrderItemDaoService();
	
	List<OrderItem> mOrderItems;
	OrderItemArrayAdapter mAdapter;
	
	HashMap<Long, Boolean> mSelectedOrders;
	Orders mSelectedOrder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.order_detail_fragment, container, false);
		
		initViewVariables(view);
		
		return view;
	}
	
	private void initViewVariables(View view) {
		
		ListView mOrdersList = (ListView) view.findViewById(R.id.orderList);
		
		mOrderItems = new ArrayList<OrderItem>();
		
		mAdapter = new OrderItemArrayAdapter(getActivity(), mOrderItems, this);
			
		mOrdersList.setAdapter(mAdapter);
		mOrdersList.setItemsCanFocus(true);
		mOrdersList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mOrderInfo = (TextView) view.findViewById(R.id.orderInfoText);
		
		mBackButton = (ImageButton) view.findViewById(R.id.backButton);
		mBackButton.setOnClickListener(getBackButtonOnClickListener());
		
		mAddButton = (ImageButton) view.findViewById(R.id.addButton);
		mAddButton.setOnClickListener(getAddButtonOnClickListener());
		
		boolean isMultiplesPane = getResources().getBoolean(R.bool.has_multiple_panes);
		
		if (isMultiplesPane) {
			mBackButton.setVisibility(View.GONE);
		} else {
			mBackButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void onStart() {
		super.onStart();
		
		updateContent();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (OrderActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TransactionActionListener");
        }
    }
	
	private void updateContent() {
		
		mSelectedOrders.clear();
		
		getView().setVisibility(View.VISIBLE);
		
		if (mOrderReference == null) {
			
			getView().setVisibility(View.INVISIBLE);
			return;
		}
		
		List<Orders> mOrders = mOrderDaoService.getOrdersByOrderReference(mOrderReference);
		
		mOrderItems.clear();
		
		String orderType = Constant.EMPTY_STRING;
		String orderReference = Constant.EMPTY_STRING;
		
		mSelectedOrder = null;
		
		for (Orders order : mOrders) {
			
			orderType = order.getOrderType();
			orderReference = order.getOrderReference();
			
			if (mSelectedOrder == null) {
				
				mSelectedOrder = new Orders();
				mSelectedOrder.setOrderType(orderType);
				mSelectedOrder.setOrderReference(orderReference);
				mSelectedOrder.setWaitressId(order.getWaitressId());
				mSelectedOrder.setWaitressName(order.getWaitressName());
				mSelectedOrder.setEmployee(order.getEmployee());
				mSelectedOrder.setCustomerId(order.getCustomerId());
				mSelectedOrder.setCustomerName(order.getCustomerName());
				mSelectedOrder.setCustomer(order.getCustomer());
			}
			
			mSelectedOrders.put(order.getId(), true);
			OrderItem orderItemHeader = new OrderItem();
			
			orderItemHeader.setOrderId(order.getId());
			orderItemHeader.setProductName(CommonUtil.formatDateTime(order.getOrderDate()));
			
			mOrderItems.add(orderItemHeader);
			
			mOrderItems.addAll(mOrderItemDaoService.getOrderItemsByOrderId(order.getId()));
		}
		
		mAdapter.notifyDataSetChanged();
		
		if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(orderType)) {
			
			mOrderInfo.setText(getString(R.string.order_table_no, orderReference));
			
		} else {
			
			mOrderInfo.setText(getString(R.string.order_customer, orderReference));
		}
	}
	
	private View.OnClickListener getBackButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onBackButtonClicked();
			}
		};
	}
	
	private View.OnClickListener getAddButtonOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onAddNewOrder(mSelectedOrder);
			}
		};
	}
	
	public void setSelectedOrders(HashMap<Long, Boolean> selectedOrders) {
		
		mSelectedOrders = selectedOrders;
	}
	
	public void displayOrders(String orderReference) {
		
		mOrderReference = orderReference;
		
		if (isViewInitialized()) {
			updateContent();
		}
	}
	
	@Override
	public Boolean getOrderStatus(Long orderId) {
		
		return mSelectedOrders.get(orderId);
	}
	
	@Override
	public void setOrderStatus(Long orderId, boolean isSelected) {
	
		mSelectedOrders.put(orderId, isSelected);
	}
	
	@Override
	public void onOrderItemSelected(OrderItem orderItem) {
		
		mActionListener.onOrderItemSelected(orderItem);
	}
	
	@Override
	public void onSetMessage(String message) {
		
		NotificationUtil.setAlertMessage(getFragmentManager(), message);
	}
}
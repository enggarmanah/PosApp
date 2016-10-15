package com.tokoku.pos.order;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.dao.OrdersDaoService;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

public class OrderListFragment extends BaseFragment 
	implements OrderListArrayAdapter.ItemActionListener {
	
	private GridView mOrderList;
	
	private List<String> mOrderReferences;
	
	private String mSelectedOrderReference;
	
	private OrderListArrayAdapter mOrderListAdapter;
	
	private OrderActionListener mActionListener;
	
	private OrdersDaoService mOrderDaoService = new OrdersDaoService();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.order_list_fragment, container, false);
		
		if (mOrderReferences == null) {
			mOrderReferences = new ArrayList<String>();
		}
		
		mOrderListAdapter = new OrderListArrayAdapter(getActivity(), mOrderReferences, this);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mOrderList = (GridView) getActivity().findViewById(R.id.orderList);
		
		mOrderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mOrderList.setAdapter(mOrderListAdapter);
		
		updateOrders();
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
	
	private void updateOrders() {
		
		mOrderReferences.clear();
		mOrderReferences.addAll(mOrderDaoService.getOrderReferences());
		mOrderListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onOrderReferenceSelected(String item) {
		
		mActionListener.onOrderReferenceSelected(item);
	}
	
	public String getSelectedOrderReference() {
		
		return mSelectedOrderReference;
	}
	
	public void setSelectedOrderReference(String item) {
		
		mSelectedOrderReference = item;
		
		if (isViewInitialized()) {
			
			//mOrderListAdapter.notifyDataSetChanged();
			updateOrders();
		}
	}
}
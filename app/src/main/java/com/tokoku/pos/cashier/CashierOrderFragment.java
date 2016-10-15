package com.tokoku.pos.cashier;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tokoku.pos.R;
import com.android.pos.dao.Customer;
import com.android.pos.dao.Discount;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.android.pos.dao.TransactionItem;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.fragment.BaseFragment;
import com.tokoku.pos.common.ConfirmListener;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.ConfirmationUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

public class CashierOrderFragment extends BaseFragment implements CashierOrderArrayAdapter.ItemActionListener {
	
	private TextView mHeaderText;
	
	private TextView mDiscountLabelText;
	private TextView mDiscountText;
	private TextView mTaxLabelText;
	private TextView mTaxText;
	private TextView mServiceChargeLabelText;
	private TextView mServiceChargeText;
	private TextView mTotalBillText;
	
	private LinearLayout mDiscountPanel;
	private LinearLayout mTaxPanel;
	private LinearLayout mServiceChargePanel;
	
	private TextView mPaymentText;
	private TextView mOrderNewItemText;
	private TextView mOrderText;
	private TextView mCancelText;
	
	private FrameLayout mPaymentPanel;
	private FrameLayout mOrderNewItemPanel;
	private FrameLayout mOrderPanel;
	
	private TextView mOrderDivider;
	
	protected ListView mTransactionItemList;

	protected List<TransactionItem> mTransactionItems;
	protected ProductGroup mSelectedTransactionItem;
	
	private CashierActionListener mActionListener;
	
	private CashierOrderArrayAdapter mAdapter;
	
	private Discount mDiscount;
	private int mTotalItem = 0;
	
	private String mCashierState = Constant.CASHIER_STATE_CASHIER;
	
	private boolean mIsAddOn = false;
	private Orders mSelectedOrder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.cashier_order_fragment, container, false);
		
		mAdapter = new CashierOrderArrayAdapter(getActivity(), mTransactionItems, this);
		
		initViewVariables(view);
		
		return view;
	}
	
	private void initViewVariables(View view) {
		
		mHeaderText = (TextView) view.findViewById(R.id.headerText);
		
		mTransactionItemList = (ListView) view.findViewById(R.id.transactionItemList);

		mTransactionItemList.setAdapter(mAdapter);
		mTransactionItemList.setItemsCanFocus(true);
		mTransactionItemList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		
		mDiscountPanel = (LinearLayout) view.findViewById(R.id.discountPanel);
		mDiscountPanel.setOnClickListener(getDiscountPanelOnClickListener());
		
		mDiscountLabelText = (TextView) view.findViewById(R.id.discountLabelText);
		mDiscountLabelText.setText(getString(R.string.no_discount));
		
		mDiscountText = (TextView) view.findViewById(R.id.discountText);
		mDiscountText.setText(Constant.EMPTY_STRING);
		
		mTaxLabelText = (TextView) view.findViewById(R.id.taxLabel);
		mTaxText = (TextView) view.findViewById(R.id.taxText);
		mTaxText.setText(CommonUtil.formatNumber(0));
		
		mServiceChargeLabelText = (TextView) view.findViewById(R.id.serviceChargeLabel);
		mServiceChargeText = (TextView) view.findViewById(R.id.serviceChargeText);
		mServiceChargeText.setText(CommonUtil.formatNumber(0));
		
		mTotalBillText = (TextView) view.findViewById(R.id.totalOrderText);
		mTotalBillText.setText(CommonUtil.formatCurrency(0));
		
		mPaymentText = (TextView) view.findViewById(R.id.paymentText);
		mPaymentText.setOnClickListener(getPaymentBtnOnClickListener());
		
		mOrderNewItemText = (TextView) view.findViewById(R.id.orderNewItemText);
		mOrderNewItemText.setOnClickListener(getOrderNewItemBtnOnClickListener());
		
		mOrderText = (TextView) view.findViewById(R.id.orderText);
		mOrderText.setOnClickListener(getOrderBtnOnClickListener());
		
		mCancelText = (TextView) view.findViewById(R.id.cancelText);
		mCancelText.setOnClickListener(getCancelBtnOnClickListener());
		
		mTaxPanel = (LinearLayout) view.findViewById(R.id.taxPanel);
		mServiceChargePanel = (LinearLayout) view.findViewById(R.id.serviceChargePanel);
		
		mPaymentPanel = (FrameLayout) view.findViewById(R.id.paymentPanel);
		mOrderNewItemPanel = (FrameLayout) view.findViewById(R.id.orderNewItemPanel);
		mOrderPanel = (FrameLayout) view.findViewById(R.id.orderPanel);
		mOrderDivider = (TextView) view.findViewById(R.id.orderDivider);
		
		if (UserUtil.isWaitress()) {
			mDiscountPanel.setVisibility(View.GONE);
		} else {
			mDiscountPanel.setVisibility(View.VISIBLE);
		}
	}
	
	public void onStart() {
		super.onStart();
		
		updatePayableInfo();
		initHeader();
		initButtons();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (CashierActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	@Override
	public void onItemSelected(TransactionItem transactionItem) {
		
		Employee employee = null;
		
		if (transactionItem.getEmployeeId() != null) {
			employee = transactionItem.getEmployee();
		}
		
		mActionListener.onProductSelected(transactionItem.getProduct(), transactionItem.getPrice(), employee, transactionItem.getQuantity(), transactionItem.getRemarks());
	}
	
	public void setTransactionItems(List<TransactionItem> transactionItems) {
		
		mTransactionItems = transactionItems;
	}
	
	public void setDiscount(Discount discount) {
		
		mDiscount = discount;
		
		if (isViewInitialized()) {
			updatePayableInfo();
		}
	}
	
	public void addTransactionItem(TransactionItem item) {
		
		boolean isExist = false;
		
		for (TransactionItem transItem : mTransactionItems) {
			
			if (transItem.getProductId() == item.getProductId()) {
				if (item.getQuantity() == 0) {
					mTransactionItems.remove(transItem);
				} else {
					if (item.getEmployeeId() != null) {
						transItem.setEmployee(item.getEmployee());
					}
					transItem.setQuantity(item.getQuantity());
					transItem.setPrice(item.getPrice());
					transItem.setRemarks(item.getRemarks());
				}
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			mTransactionItems.add(item);
		}
		
		if (!isViewInitialized()) {
			return;
		}
		
		updatePayableInfo();
	}
	
	public void addTransactionItem(TransactionItem item, boolean isMergeQuantity) {
		
		boolean isExist = false;
		
		for (TransactionItem transItem : mTransactionItems) {
			
			if (transItem.getProductId() == item.getProductId()) {
				if (item.getQuantity() == 0) {
					mTransactionItems.remove(transItem);
				} else {
					if (isMergeQuantity) {
						transItem.setQuantity(transItem.getQuantity() + item.getQuantity());
					} else {
						transItem.setQuantity(item.getQuantity());
					}
				}
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			mTransactionItems.add(item);
		}
		
		if (!isViewInitialized()) {
			return;
		}
		
		updatePayableInfo();
	}
	
	private void updatePayableInfo() {
		
		if (!isViewInitialized()) {
			return;
		}
		
		float totalBill = 0;
		mTotalItem = 0;
		
		for (TransactionItem transItem : mTransactionItems) {
			totalBill += transItem.getQuantity() * transItem.getPrice();
			mTotalItem += transItem.getQuantity();
		}
		
		float discount = 0;
		
		if (mDiscount != null) {
			
			String label = mDiscount.getName();
			
			if (mDiscount.getPercentage() != null && mDiscount.getPercentage() != 0) {
				label = label + " " + CommonUtil.formatPercentage(mDiscount.getPercentage());
			}
			
			mDiscountLabelText.setText(label);
			
			if (mDiscount.getPercentage() != null && mDiscount.getPercentage() != 0) {
				discount = mDiscount.getPercentage() * totalBill / 100;
			} else {
				discount = mDiscount.getAmount();
			}
			
			mDiscountText.setText("- " + CommonUtil.formatCurrency(discount));
				
			if (mDiscount.getPercentage() != null && mDiscount.getAmount() != null && 
				mDiscount.getPercentage() == 0 && mDiscount.getAmount() == 0) {

				mDiscountLabelText.setText(getString(R.string.no_discount));
				mDiscountText.setText(Constant.EMPTY_STRING);
			}
			
		} else {
			mDiscountLabelText.setText(getString(R.string.no_discount));
			mDiscountText.setText(Constant.EMPTY_STRING);
		}
		
		totalBill = totalBill - discount;
		
		Merchant merchant = MerchantUtil.getMerchant();
		
		float taxPercentage = CommonUtil.getNvlFloat(merchant.getTaxPercentage());
		float serviceChargePercentage = CommonUtil.getNvlFloat(merchant.getServiceChargePercentage());
		
		float tax = (totalBill * taxPercentage / 100);
		float serviceCharge = (int) Math.round(totalBill * serviceChargePercentage / 100);
		
		if (tax != 0) {
			
			String label = getString(R.string.field_tax_percentage) + " " + CommonUtil.formatPercentage(taxPercentage);
			
			mTaxLabelText.setText(label);
			mTaxText.setText(CommonUtil.formatCurrency(tax));
		}
		
		if (serviceCharge != 0) {
			
			String label = getString(R.string.field_service_charge_percentage) + " " + CommonUtil.formatPercentage(serviceChargePercentage);
			
			mServiceChargeLabelText.setText(label);
			mServiceChargeText.setText(CommonUtil.formatCurrency(serviceCharge));
		}
		
		mTaxText.setText(CommonUtil.formatCurrency(tax));
		mServiceChargeText.setText(CommonUtil.formatCurrency(serviceCharge));
		
		mTaxPanel.setVisibility(View.VISIBLE);
		mServiceChargePanel.setVisibility(View.VISIBLE);
		
		if (tax == 0) {
			mTaxPanel.setVisibility(View.GONE);
		}
		
		if (serviceCharge == 0) {
			mServiceChargePanel.setVisibility(View.GONE);
		}
		
		float totalPayable = totalBill + tax + serviceCharge;
		
		if (UserUtil.isWaitress()) {
			mTotalBillText.setText(CommonUtil.formatNumber(mTotalItem));
		} else {
			mTotalBillText.setText(CommonUtil.formatCurrency(totalPayable));
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	private void initHeader() {
		
		String header = null;
		
		if (mSelectedOrder != null) {
			
			if (mIsAddOn) {
			
				if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mSelectedOrder.getOrderType())) {
					header = getString(R.string.order_table_no_add_on, mSelectedOrder.getOrderReference());
				} else {
					header = getString(R.string.order_customer_add_on, mSelectedOrder.getOrderReference());
				}
			} else {
				if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(mSelectedOrder.getOrderType())) {
					header = getString(R.string.order_table_no, mSelectedOrder.getOrderReference());
				} else {
					header = getString(R.string.order_customer, mSelectedOrder.getOrderReference());
				}
			}
		} else {
			
			header = getString(R.string.order_transaction_list);
		}
		
		if (UserUtil.isWaitress()) {
			
			header = getString(R.string.order_order_list);
		}
		
		mHeaderText.setText(header);
	}
	
	private void initButtons() {
		
		if (Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(MerchantUtil.getMerchant().getType()) ||
			Constant.MERCHANT_TYPE_GOODS_N_SERVICES.equals(MerchantUtil.getMerchant().getType())) {
			
			hidePaymentButton();
			hideOrderNewItemButton();
			hideOrderButton();
			
			if (Constant.CASHIER_STATE_CASHIER.equals(mCashierState)) {
				
				showPaymentButton();
				
				if (Constant.ORDER_TYPE_BEFORE_PAYMENT.equals(MerchantUtil.getMerchant().getOrderType())) {
					
					showOrderButton();
				}
			
			} else if (Constant.CASHIER_STATE_ORDER_PAYMENT.equals(mCashierState)) {
				
				showPaymentButton();
				
			} else if (Constant.CASHIER_STATE_ORDER_NEW_ITEM.equals(mCashierState)) {
				
				showOrderNewItemButton();
			}
			
			if (UserUtil.isWaitress()) {
				
				hidePaymentButton();
				hideOrderNewItemButton();
				
				showOrderButton();
				mOrderDivider.setVisibility(View.GONE);
			}
			
		} else {
			hideOrderNewItemButton();
			hideOrderButton();
		}
	}
	
	private void hidePaymentButton() {
		
		mPaymentPanel.setVisibility(View.GONE);
	}
	
	private void showPaymentButton() {
		
		mPaymentPanel.setVisibility(View.VISIBLE);
	}
	
	private void hideOrderNewItemButton() {
		
		mOrderNewItemPanel.setVisibility(View.GONE);
	}
	
	private void showOrderNewItemButton() {
		
		mOrderNewItemPanel.setVisibility(View.VISIBLE);
	}
	
	private void hideOrderButton() {
		
		mOrderPanel.setVisibility(View.GONE);
		mOrderDivider.setVisibility(View.GONE);
	}
	
	private void showOrderButton() {
		
		mOrderPanel.setVisibility(View.VISIBLE);
		mOrderDivider.setVisibility(View.VISIBLE);
	}
	
	public void setCashierState(String state) {
		
		mCashierState = state;
		
		if (isViewInitialized()) {
			initButtons();
		}
	}
	
	public void setSelectedOrders(Orders order, boolean isAddOn) {
		
		mSelectedOrder = order;
		mIsAddOn = isAddOn;
		
		if (isViewInitialized()) {
			initHeader();
		}
	}
	
	private View.OnClickListener getDiscountPanelOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onSelectDiscount();
			}
		};
	}
	
	private Float getTotalQuantity(List<TransactionItem> transactionItems) {
		
		Float total = Float.valueOf(0);
		
		for (TransactionItem transactionItem : transactionItems) {
			total = total + transactionItem.getQuantity();
		}
		
		return total;
	}
	
	private View.OnClickListener getPaymentBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mTransactionItems != null && getTotalQuantity(mTransactionItems) == 0) {
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_transaction_list_empty));
				} else {
					
					for (TransactionItem tranItem : mTransactionItems) {
						
						Product product = tranItem.getProduct();
						
						if (Constant.STATUS_YES.equals(product.getPicRequired()) &&
							tranItem.getEmployeeId() == null) {
							
							NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_pic_required, product.getName()));
							return;
						}
					}
					
					mActionListener.onPaymentRequested(CommonUtil.parseFloatCurrency(mTotalBillText.getText().toString()));
				}
			}
		};
	}
	
	private View.OnClickListener getOrderNewItemBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mTransactionItems != null && mTransactionItems.size() == 0) {
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_order_list_empty));
				} else {
					
					String orderReference = mSelectedOrder.getOrderReference();
					String orderType = mSelectedOrder.getOrderType();
					
					Employee employee = mSelectedOrder.getWaitressId() != null ? mSelectedOrder.getEmployee() : null;
					Customer customer = mSelectedOrder.getCustomerId() != null ? mSelectedOrder.getCustomer() : new Customer();
					
					customer.setName(mSelectedOrder.getCustomerName());
					
					mActionListener.onOrderInfoProvided(orderReference, orderType, employee, customer);
				}
			}
		};
	}
	
	private View.OnClickListener getOrderBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mTransactionItems != null && mTransactionItems.size() == 0) {
					NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_order_list_empty));
				} else {
					mActionListener.onOrderRequested(mTotalItem);
				}
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ConfirmationUtil.confirmTask(getFragmentManager(), (ConfirmListener) getActivity(), ConfirmationUtil.CANCEL_TRANSACTION, getString(R.string.confirm_transaction_cancellation));
			}
		};
	}
}
package com.tokoku.pos.data.inventory;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Bills;
import com.android.pos.dao.Inventory;
import com.android.pos.dao.Product;
import com.android.pos.dao.Supplier;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.Session;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.InventoryDaoService;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class InventoryEditFragment extends BaseEditFragment<Inventory> {
    
	Spinner mStatusSp;
	EditText mProductNameText;
	EditText mProductCostPriceText;
	EditText mQuantityText;
	EditText mDeliveryNoText;
	EditText mBillsReferenceNoText;
    EditText mSupplierNameText;
    EditText mInventoryDate;
    EditText mRemarksText;
    
    LinearLayout mProductCostPricePanel;
    LinearLayout mDeliveryNoPanel;
    LinearLayout mBillsReferenceNoPanel;
    LinearLayout mSupplierPanel;
    LinearLayout mRemarksPanel;
    	
    CodeSpinnerArrayAdapter statusArrayAdapter;
    
    private InventoryDaoService mInventoryDaoService = new InventoryDaoService();
    private ProductDaoService mProductDaoService = new ProductDaoService();
    
    BaseItemListener<Inventory> mInventoryItemListener;
    
    String mStatus;
    
    @Override
    protected void hideKeyboard() {
    	
    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mProductNameText.getWindowToken(), 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_inventory_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mInventoryItemListener = (BaseItemListener<Inventory>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<T>");
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void initViewReference(View view) {
        
    	super.initViewReference(view);
    	
    	mStatusSp = (Spinner) view.findViewById(R.id.statusSp);
    	mProductNameText = (EditText) view.findViewById(R.id.productNameText);
    	mProductCostPriceText = (EditText) view.findViewById(R.id.productCostPriceText);
    	mQuantityText = (EditText) view.findViewById(R.id.quantityText);
		mDeliveryNoText = (EditText) view.findViewById(R.id.deliveryNoText);
    	mBillsReferenceNoText = (EditText) view.findViewById(R.id.billsReferenceNoText);
    	mSupplierNameText = (EditText) view.findViewById(R.id.supplierNameText);
    	mInventoryDate = (EditText) view.findViewById(R.id.inventoryDate);
    	mRemarksText = (EditText) view.findViewById(R.id.remarksText);
    	
    	mProductCostPricePanel = (LinearLayout) view.findViewById(R.id.productCostPricePanel);
        mDeliveryNoPanel = (LinearLayout) view.findViewById(R.id.deliveryNoPanel);
    	mBillsReferenceNoPanel = (LinearLayout) view.findViewById(R.id.billReferenceNoPanel);
    	mSupplierPanel = (LinearLayout) view.findViewById(R.id.supplierPanel);
        mRemarksPanel = (LinearLayout) view.findViewById(R.id.remarksPanel);
    	
    	registerField(mStatusSp);
    	registerField(mProductNameText);
    	registerField(mProductCostPriceText);
    	registerField(mQuantityText);
		registerField(mDeliveryNoText);
    	registerField(mBillsReferenceNoText);
    	registerField(mSupplierNameText);
    	registerField(mInventoryDate);
    	registerField(mRemarksText);
    	
    	enableInputFields(false);
    	
    	mProductCostPriceText.setOnFocusChangeListener(getCurrencyFieldOnFocusChangeListener());
    	mQuantityText.setOnFocusChangeListener(getNumberFieldOnFocusChangeListener());
    	
    	statusArrayAdapter = new CodeSpinnerArrayAdapter(mStatusSp, getActivity(), CodeUtil.getInventoryStatus());
    	mStatusSp.setAdapter(statusArrayAdapter);
    	
    	mInventoryDate.setOnClickListener(getDateFieldOnClickListener("deliveryDatePicker"));
    	
    	linkDatePickerWithInputField("deliveryDatePicker", mInventoryDate);
    	
    	mProductNameText.setFocusable(false);
    	mProductNameText.setOnClickListener(getProductOnClickListener());
    	
    	mBillsReferenceNoText.setFocusable(false);
    	mBillsReferenceNoText.setOnClickListener(getBillOnClickListener());
    	
    	mSupplierNameText.setFocusable(false);
    	mSupplierNameText.setOnClickListener(getSupplierOnClickListener());
    	
    	mStatusSp.setOnItemSelectedListener(getStatusOnItemSelectedListener());
    }
    
    @Override
    protected void updateView(Inventory inventory) {
    	
    	if (inventory != null) {
    		
    		int statusIndex = statusArrayAdapter.getPosition(inventory.getStatus());
    		mStatusSp.setSelection(statusIndex);
    		
    		mProductNameText.setText(inventory.getProductName());
    		mProductCostPriceText.setText(CommonUtil.formatCurrency(inventory.getProductCostPrice()));
    		mQuantityText.setText(inventory.getQuantity() == null ? Constant.EMPTY_STRING : CommonUtil.formatNumber(Math.abs(inventory.getQuantity())));
    		mDeliveryNoText.setText(inventory.getDeliveryNo());
    		mBillsReferenceNoText.setText(inventory.getBillReferenceNo());
    		mSupplierNameText.setText(inventory.getSupplierName());
    		mInventoryDate.setText(CommonUtil.formatDate(inventory.getInventoryDate()));
    		mRemarksText.setText(inventory.getRemarks());
    		
    		showView();
    		
    		if (Constant.INVENTORY_STATUS_SALE.equals(inventory.getStatus())) {
    			mInventoryItemListener.disableEditMenu();
    		}
    		
    	} else {
    		
    		hideView();
    	}
    	
        mStatus = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
        refreshVisibleField();
    }
    
    @Override
    protected void saveItem() {
    	
    	String productName = mProductNameText.getText().toString();
    	Float productCostPrice = CommonUtil.parseFloatCurrency(mProductCostPriceText.getText().toString());
    	Float quantity = CommonUtil.parseFloatNumber(mQuantityText.getText().toString());
    	String deliveryNo = mDeliveryNoText.getText().toString();
    	String billReferenceNo = mBillsReferenceNoText.getText().toString();
    	String supplierName = mSupplierNameText.getText().toString();
    	Date deliveryDate = CommonUtil.parseDate(mInventoryDate.getText().toString());
    	String remarks = mRemarksText.getText().toString();
    	String status = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setProductName(productName);
    		mItem.setProductCostPrice(productCostPrice);
    		
    		if (quantity != null) {
    			
    			if (Constant.INVENTORY_STATUS_PURCHASE.equals(status) ||
    				Constant.INVENTORY_STATUS_REPLACEMENT.equals(status) ||
    				Constant.INVENTORY_STATUS_REFUND.equals(status) ||
    				Constant.INVENTORY_STATUS_INITIAL_STOCK.equals(status) ||
    				Constant.INVENTORY_STATUS_SELF_PRODUCTION.equals(status)) {
    				
    				mItem.setQuantity(quantity);
    			} else {
    				mItem.setQuantity(-quantity);
    			}
    		}

            mItem.setDeliveryNo(deliveryNo);
    		mItem.setBillReferenceNo(billReferenceNo);
    		mItem.setSupplierName(supplierName);
    		mItem.setInventoryDate(deliveryDate);
    		mItem.setRemarks(remarks);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		mItem.setStatus(status);
    		
    		String loginId = UserUtil.getUser().getUserId();
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(loginId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(loginId);
    		mItem.setUpdateDate(new Date());
    	}
    }
    
    @Override
    protected Long getItemId(Inventory inventory) {
    	
    	return inventory.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
        mInventoryDaoService.addInventory(mItem);
        
        mProductNameText.getText().clear();
        mQuantityText.getText().clear();
        mDeliveryNoText.getText().clear();
        mBillsReferenceNoText.getText().clear();
        mSupplierNameText.getText().clear();
        mInventoryDate.getText().clear();
        mRemarksText.getText().clear();

        Session.setInventory(mItem);
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mInventoryDaoService.updateInventory(mItem);
    	
    	// update product cost price
    	
    	if (mItem.getProductCostPrice() != null && mItem.getProductCostPrice() != 0) {
    	
	    	Product product = mProductDaoService.getProduct(mItem.getProductId());
	    	product.setCostPrice(mItem.getProductCostPrice());
	    	mProductDaoService.updateProduct(product);
    	}

    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return null;
    }
    
    @Override
    public Inventory updateItem(Inventory inventory) {

    	inventory = mInventoryDaoService.getInventory(inventory.getId());
    	
    	this.mItem = inventory;
    	
    	return inventory;
    }
    
    public void setProduct(Product product) {
		
    	if (mItem != null) {
    		
    		if (product != null) {
    			
    			mItem.setProductId(product.getId());
    			mItem.setProductName(product.getName());
    			//mItem.setProductCostPrice(product.getCostPrice());
    			
    		} else {
    			
    			mItem.setProduct(null);
    			mItem.setProductName(Constant.EMPTY_STRING);	
    		}
    		
    		updateView(mItem);
    	}
	}
    
    public void setSupplier(Supplier supplier) {
		
    	if (mItem != null) {
    		
    		if (supplier != null) {
    			
    			mItem.setSupplierId(supplier.getId());
    			mItem.setSupplierName(supplier.getName());
    			
    		} else {
    			
    			mItem.setSupplier(null);
    			mItem.setSupplierName(Constant.EMPTY_STRING);	
    		}
    		
    		updateView(mItem);
    	}
	}
    
    public void setBill(Bills bill) {
		
    	if (mItem != null) {
    		
    		if (bill != null) {
    			
    			mItem.setBillId(bill.getId());
    			mItem.setBillReferenceNo(bill.getBillReferenceNo());
    			mItem.setSupplierId(bill.getSupplierId());
    			mItem.setSupplierName(bill.getSupplierName());
    			
    			if (Constant.INVENTORY_STATUS_PURCHASE.equals(mStatus)) {
    				mItem.setInventoryDate(bill.getDeliveryDate());
        		}
    			
    		} else {
    			
    			mItem.setBills(null);
    			mItem.setBillReferenceNo(Constant.EMPTY_STRING);
    			mItem.setSupplier(null);
    			mItem.setSupplierName(Constant.EMPTY_STRING);
    			mItem.setInventoryDate(null);
    		}
    		
    		updateView(mItem);
    	}
	}
    
    private void refreshVisibleField() {
    	
    	mProductCostPricePanel.setVisibility(View.VISIBLE);
    	mDeliveryNoPanel.setVisibility(View.VISIBLE);
    	mBillsReferenceNoPanel.setVisibility(View.VISIBLE);
    	mSupplierPanel.setVisibility(View.VISIBLE);
		mRemarksPanel.setVisibility(View.VISIBLE);
		
		mandatoryFields.clear();
		
		if (Constant.INVENTORY_STATUS_PURCHASE.equals(mStatus)) {
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	//mandatoryFields.add(new FormFieldBean(mBillsReferenceNoText, R.string.field_bills_reference_no));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
		
		} else if (Constant.INVENTORY_STATUS_REFUND.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
            mDeliveryNoPanel.setVisibility(View.GONE);
			mBillsReferenceNoPanel.setVisibility(View.GONE);
			mSupplierPanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
		
		} else if (Constant.INVENTORY_STATUS_RETURN.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mBillsReferenceNoText, R.string.field_bills_reference_no));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
		
		} else if (Constant.INVENTORY_STATUS_REPLACEMENT.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mBillsReferenceNoText, R.string.field_bills_reference_no));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
		
		} else if (Constant.INVENTORY_STATUS_LOST.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
            mDeliveryNoPanel.setVisibility(View.GONE);
			mBillsReferenceNoPanel.setVisibility(View.GONE);
			mSupplierPanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
			
		} else if (Constant.INVENTORY_STATUS_DAMAGE.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
            mDeliveryNoPanel.setVisibility(View.GONE);
			mBillsReferenceNoPanel.setVisibility(View.GONE);
			mSupplierPanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
			
		}  else if (Constant.INVENTORY_STATUS_INITIAL_STOCK.equals(mStatus) ||
				    Constant.INVENTORY_STATUS_SELF_PRODUCTION.equals(mStatus)) {
			
			mProductCostPricePanel.setVisibility(View.GONE);
            mDeliveryNoPanel.setVisibility(View.GONE);
			mBillsReferenceNoPanel.setVisibility(View.GONE);
			mSupplierPanel.setVisibility(View.GONE);
			
			mandatoryFields.add(new FormFieldBean(mProductNameText, R.string.field_product));
	    	mandatoryFields.add(new FormFieldBean(mQuantityText, R.string.field_quantity));
	    	mandatoryFields.add(new FormFieldBean(mInventoryDate, R.string.field_delivery_date));
		}
		
		highlightMandatoryFields();
    }
    
    private AdapterView.OnItemSelectedListener getStatusOnItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
			
        	@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
		    	mStatus = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
		    	
		    	refreshVisibleField();

		    	boolean isAdd = mItem == null || mItem.getId() == null;

		    	if (isAdd) {
                    if (Constant.INVENTORY_STATUS_PURCHASE.equals(mStatus)) {

                        Inventory prevPurchasedInventory = Session.getInventory();

                        if (prevPurchasedInventory != null) {

                            mItem.setDeliveryNo(prevPurchasedInventory.getDeliveryNo());
                            mItem.setInventoryDate(prevPurchasedInventory.getInventoryDate());
                            mItem.setBills(prevPurchasedInventory.getBills());
                            mItem.setBillReferenceNo(prevPurchasedInventory.getBillReferenceNo());
                            mItem.setSupplierName(prevPurchasedInventory.getSupplierName());
                        }
                    } else {

                        mItem.setDeliveryNo(null);
                        mItem.setInventoryDate(null);
                        mItem.setBills(null);
                        mItem.setBillReferenceNo(null);
                        mItem.setSupplierName(null);
                    }

                    updateView(mItem);
                }
		    }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
    
    public View.OnClickListener getProductOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mProductNameText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mInventoryItemListener.onSelectProduct(isMandatory);
					}
				}
			}
		};
    }
    
    public View.OnClickListener getSupplierOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mSupplierNameText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mInventoryItemListener.onSelectSupplier(isMandatory);
					}
				}
			}
		};
    }
    
    public View.OnClickListener getBillOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mBillsReferenceNoText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = true;
						mInventoryItemListener.onSelectBill(isMandatory);
					}
				}
			}
		};
    }
}
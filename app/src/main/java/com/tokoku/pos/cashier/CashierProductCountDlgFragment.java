package com.tokoku.pos.cashier;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.Product;
import com.tokoku.pos.Constant;
import com.tokoku.pos.model.PriceBean;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CashierProductCountDlgFragment extends DialogFragment {
	
	public interface ProductActionListener {
		
		public void onProductQuantitySelected(Product product, Float price, Employee personInCharge, Float quantity, String remarks);
		
		public void onSelectEmployee();
	}
	
	Spinner mPriceSp;
	
	TextView mProductText;
	TextView mPersonInChargeText;
	TextView mQuantityText;
	
	LinearLayout mProductCountPanel;
	LinearLayout mNumberBtnPanel;
	
	TextView mRemarksBtn;
	EditText mRemarksText;
	
	Button number0Btn;
	Button number1Btn;
	Button number2Btn;
	Button number3Btn;
	Button number4Btn;
	Button number5Btn;
	Button number6Btn;
	Button number7Btn;
	Button number8Btn;
	Button number9Btn;
	
	Button mActionCBtn;
	ImageButton mActionXBtn;
	
	Button mOkBtn;
	Button mCancelBtn;
	
	Product mProduct;
	Float mPrice;
	Employee mPersonInCharge;
	String mRemarks;
	Float mQuantity;
	
	ProductActionListener mActionListener;
	
	CashierProductCountPriceSpinnerArrayAdapter mPriceArrayAdapter;
	
	private static String PRODUCT = "PRODUCT";
	private static String PRICE = "PRICE";
	private static String PERSON_IN_CHARGE = "PERSON_IN_CHARGE";
	private static String REMARKS = "REMARKS";
	private static String QUANTITY = "QUANTITY";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        if (savedInstanceState != null) {
        	
        	mProduct = (Product) savedInstanceState.get(PRODUCT);
        	mPrice = (Float) savedInstanceState.get(PRICE);
        	mPersonInCharge = (Employee) savedInstanceState.get(PERSON_IN_CHARGE);
        	mRemarks = (String) savedInstanceState.getString(REMARKS);
        	mQuantity = (Float) savedInstanceState.get(QUANTITY);
        }
        
        setCancelable(false);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		saveDataFromView();
		
		outState.putSerializable(PRODUCT, mProduct);
		outState.putSerializable(PRICE, mPrice);
		outState.putSerializable(PERSON_IN_CHARGE, mPersonInCharge);
		outState.putSerializable(REMARKS, mRemarks);
		outState.putSerializable(QUANTITY, mQuantity);
	}
	
	private void saveDataFromView() {
		
		float quantity = 1; 
		String quantityStr = "1";
		
		if (!CommonUtil.isEmpty(mQuantityText.getText().toString())) {
			quantityStr = mQuantityText.getText().toString();
		}
		
		try {
			quantity = Float.valueOf(quantityStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		mQuantity = quantity;
	}
	
	public void setEmployee(Employee employee) {
		
		mPersonInCharge = employee;
		
		refreshDisplay();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.cashier_product_count_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mProductText = (TextView) getView().findViewById(R.id.productText);
		mPersonInChargeText = (TextView) getView().findViewById(R.id.personInChargeText);
		mPriceSp = (Spinner) getView().findViewById(R.id.priceSp);
		mQuantityText = (TextView) getView().findViewById(R.id.countText);
		
		mRemarksBtn = (TextView) getView().findViewById(R.id.remarksBtn);
		mRemarksText = (EditText) getView().findViewById(R.id.remarksText);
		
		mNumberBtnPanel = (LinearLayout) getView().findViewById(R.id.numberBtnPanel);
		mProductCountPanel = (LinearLayout) getView().findViewById(R.id.productCountPanel);
		
		number0Btn = (Button) getView().findViewById(R.id.number0Btn);
		number1Btn = (Button) getView().findViewById(R.id.number1Btn);
		number2Btn = (Button) getView().findViewById(R.id.number2Btn);
		number3Btn = (Button) getView().findViewById(R.id.number3Btn);
		number4Btn = (Button) getView().findViewById(R.id.number4Btn);
		number5Btn = (Button) getView().findViewById(R.id.number5Btn);
		number6Btn = (Button) getView().findViewById(R.id.number6Btn);
		number7Btn = (Button) getView().findViewById(R.id.number7Btn);
		number8Btn = (Button) getView().findViewById(R.id.number8Btn);
		number9Btn = (Button) getView().findViewById(R.id.number9Btn);
		
		mActionCBtn = (Button) getView().findViewById(R.id.actionCBtn);
		mActionXBtn = (ImageButton) getView().findViewById(R.id.actionXBtn);
		
		mOkBtn = (Button) getView().findViewById(R.id.okBtn);
		mCancelBtn = (Button) getView().findViewById(R.id.cancelBtn);
		
		mPersonInChargeText.setOnClickListener(getPersonInChargeTextOnClickListener());
		mRemarksBtn.setOnClickListener(getRemarksBtnOnClickListener());
		
		number0Btn.setOnClickListener(getNumberBtnOnClickListener("0"));
		number1Btn.setOnClickListener(getNumberBtnOnClickListener("1"));
		number2Btn.setOnClickListener(getNumberBtnOnClickListener("2"));
		number3Btn.setOnClickListener(getNumberBtnOnClickListener("3"));
		number4Btn.setOnClickListener(getNumberBtnOnClickListener("4"));
		number5Btn.setOnClickListener(getNumberBtnOnClickListener("5"));
		number6Btn.setOnClickListener(getNumberBtnOnClickListener("6"));
		number7Btn.setOnClickListener(getNumberBtnOnClickListener("7"));
		number8Btn.setOnClickListener(getNumberBtnOnClickListener("8"));
		number9Btn.setOnClickListener(getNumberBtnOnClickListener("9"));
		
		mActionXBtn.setOnClickListener(getDeleteBtnOnClickListener());
		
		mOkBtn.setOnClickListener(getOkBtnOnClickListener());
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		refreshDisplay();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (ProductActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	private void refreshDisplay() {
		
		if (getView() == null) {
			return;
		}
		
		if (Constant.MERCHANT_TYPE_FOODS_N_BEVERAGES.equals(MerchantUtil.getMerchantType())) {
			mRemarksBtn.setVisibility(View.VISIBLE);
		} else {
			mRemarksBtn.setVisibility(View.GONE);
		}
		
		/*if (Constant.PRODUCT_TYPE_SERVICE.equals(mProduct.getType())) {
			
			mQuantity = Float.valueOf(1);
			
			mProductCountPanel.setVisibility(View.GONE);
			mNumberBtnPanel.setVisibility(View.GONE);
		
		} else {
			
			mProductCountPanel.setVisibility(View.VISIBLE);
			mNumberBtnPanel.setVisibility(View.VISIBLE);
		}*/
		
		mProductText.setText(mProduct.getName());
		mRemarksText.setText(mRemarks);
		mQuantityText.setText(CommonUtil.formatNumber(mQuantity));
		
		if (mPersonInCharge != null) {
			mPersonInChargeText.setText(mPersonInCharge.getName());
		} else {
			mPersonInChargeText.setText(getString(R.string.track_employee));
		}
		
		displayRemarks(!CommonUtil.isEmpty(mRemarks));
		
		PriceBean[] prices = getPrices();
		
		mPriceArrayAdapter = new CashierProductCountPriceSpinnerArrayAdapter(mPriceSp, getActivity(), prices);
		mPriceSp.setAdapter(mPriceArrayAdapter);
		
		if (prices != null && prices.length == 1) {
			mPriceSp.setEnabled(false);
		} else {
			mPriceSp.setEnabled(true);
			mPriceSp.setSelection(getPriceSelectedIndex(prices, mPrice));
		}
			
		if (Constant.STATUS_YES.equals(mProduct.getPicRequired())) {
			mPersonInChargeText.setVisibility(View.VISIBLE);
		} else {
			mPersonInChargeText.setVisibility(View.GONE);
		}
		
		if (Constant.QUANTITY_TYPE_KG.equals(mProduct.getQuantityType()) ||
			Constant.QUANTITY_TYPE_METER.equals(mProduct.getQuantityType()) ||
			Constant.QUANTITY_TYPE_LITER.equals(mProduct.getQuantityType())) {
			
			mActionCBtn.setText(CommonUtil.getNumberDecimalSeparator());
			mActionCBtn.setOnClickListener(getCommaBtnOnClickListener());
		} else {
			mActionCBtn.setOnClickListener(getClearBtnOnClickListener());
		}
	}
	
	private int getPriceSelectedIndex(PriceBean[] prices, Float selectedPrice) {
		
		int index = 0;
		
		if (prices != null) {
			
			for (PriceBean priceBean : prices) {
				if (priceBean.getValue() == selectedPrice) {
					break;
				}
				index++;
			}
		}
		
		return index;
	}
		
	private PriceBean[] getPrices() {
		
		List<PriceBean> list = new ArrayList<PriceBean>();
		
		if (mProduct != null) {
		
			Merchant merchant = MerchantUtil.getMerchant();
			
			PriceBean price1 = new PriceBean(merchant.getPriceLabel1(), mProduct.getPrice1());
			PriceBean price2 = new PriceBean(merchant.getPriceLabel2(), mProduct.getPrice2());
			PriceBean price3 = new PriceBean(merchant.getPriceLabel3(), mProduct.getPrice3());
			
			list.add(price1);
			
			int priceTypeCount = merchant.getPriceTypeCount() != null ? merchant.getPriceTypeCount() : 1;
			
			boolean isPrice2Available = price2.getValue() != null && price2.getValue() > 0;
			boolean isPrice3Available = price3.getValue() != null && price3.getValue() > 0;
			
			if (priceTypeCount >= 2 && isPrice2Available) {
				list.add(price2);
			}
			
			if (priceTypeCount >= 3 && isPrice3Available) {
				list.add(price3);
			}
			
			if (list.size() == 1) {
				price1.setType(getString(R.string.field_price));
			}
		}
		
		return list.toArray(new PriceBean[list.size()]);
	}
	
	private View.OnClickListener getNumberBtnOnClickListener(final String numberText) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String quantityText = mQuantityText.getText().toString();
				String decimalSeparator = CommonUtil.getNumberDecimalSeparator(); 
				
				if (decimalSeparator != null && quantityText.contains(decimalSeparator)) {
					mQuantityText.setText(mQuantityText.getText() + numberText);
					
				} else {
					float payment = CommonUtil.parseFloatNumber(mQuantityText.getText().toString());
					float number = CommonUtil.parseFloatNumber(numberText);
					
					float total = payment * 10 + number;
					
					mQuantityText.setText(CommonUtil.formatNumber(total));
				}
			}
		};
	}
	
	private View.OnClickListener getClearBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mQuantityText.setText("0");
			}
		};
	}
		
	private View.OnClickListener getCommaBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String quantityText = mQuantityText.getText().toString();
				String decimalSeparator = CommonUtil.getNumberDecimalSeparator(); 
				
				if (!quantityText.contains(decimalSeparator)) {
					mQuantityText.setText(mQuantityText.getText() + decimalSeparator);
				}
			}
		};
	}
	
	private View.OnClickListener getDeleteBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				float quantity = CommonUtil.parseFloatNumber(mQuantityText.getText().toString());
				String number = CommonUtil.isRound(quantity) ? String.valueOf((int) quantity) : String.valueOf(quantity);
				
				if (number.length() == 1) {
					mQuantityText.setText("0");
				} else {
					number = CommonUtil.formatNumber(number.substring(0, number.length()-1));
					mQuantityText.setText(number);
				}
			}
		};
	}
	
	private View.OnClickListener getPersonInChargeTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveDataFromView();
				mActionListener.onSelectEmployee();
			}
		};
	}
	
	private View.OnClickListener getOkBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Float price = ((PriceBean) mPriceSp.getSelectedItem()).getValue();
				
				mQuantity = CommonUtil.parseFloatNumber(mQuantityText.getText().toString());
				mActionListener.onProductQuantitySelected(mProduct, price, mPersonInCharge, mQuantity, mRemarksText.getText().toString());
				dismiss();
			}
		};
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
	
	private View.OnClickListener getRemarksBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mRemarksText.getVisibility() == View.VISIBLE) {
					
					mRemarksText.setText(Constant.EMPTY_STRING);
					displayRemarks(false);
					
				} else {
					
					displayRemarks(true);
				}
			}
		};
	}
	
	public void setProduct(Product product, Float price, Employee personInCharge, Float quantity, String remarks) {
		
		this.mProduct = product;
		this.mPrice = price;
		this.mPersonInCharge = personInCharge;
		this.mRemarks = remarks;
		this.mQuantity = quantity;
		
		refreshDisplay();
	}
	
	public void displayRemarks(boolean isDisplayed) {
		
		if (isDisplayed) {
			
			mRemarksText.setVisibility(View.VISIBLE);
			mRemarksText.requestFocus();
			
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mRemarksText, InputMethodManager.SHOW_IMPLICIT);
			
		} else {
			
			mRemarksText.setVisibility(View.GONE);
		}
	}
}

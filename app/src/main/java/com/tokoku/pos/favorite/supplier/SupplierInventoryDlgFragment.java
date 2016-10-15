package com.tokoku.pos.favorite.supplier;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Supplier;
import com.android.pos.dao.Inventory;
import com.tokoku.pos.Constant;
import com.tokoku.pos.dao.InventoryDaoService;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SupplierInventoryDlgFragment extends DialogFragment {
	
	TextView mCancelBtn;
	EditText mInventorySearchText;
	ListView mInventoryListView;
	
	SupplierInventoryArrayAdapter supplierTransactionsArrayAdapter;
	
	Supplier mSupplier;
	List<Inventory> mInventories = new ArrayList<Inventory>();
	
	private InventoryDaoService mInventoryDaoService = new InventoryDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        supplierTransactionsArrayAdapter = new SupplierInventoryArrayAdapter(getActivity(), mInventories);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.favorite_supplier_inventory_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mInventorySearchText = (EditText) getView().findViewById(R.id.billSearchText);
		mInventorySearchText.setText(Constant.EMPTY_STRING);
		
		mInventorySearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mInventories.clear();
				mInventories.addAll(mInventoryDaoService.getSupplierInventories(mSupplier, s.toString(), 0));
				supplierTransactionsArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mInventoryListView = (ListView) getView().findViewById(R.id.inventoryListView);
		mInventoryListView.setAdapter(supplierTransactionsArrayAdapter);
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		supplierTransactionsArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	public void setSupplier(Supplier supplier) {
		
		mSupplier = supplier;
		
		mInventories.clear();
		mInventories.addAll(mInventoryDaoService.getSupplierInventories(mSupplier, Constant.EMPTY_STRING, 0));
	}
	
	private View.OnClickListener getCancelBtnOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dismiss();
			}
		};
	}
}

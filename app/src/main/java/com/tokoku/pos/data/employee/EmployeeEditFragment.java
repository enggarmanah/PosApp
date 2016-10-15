package com.tokoku.pos.data.employee;

import java.util.Date;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.dao.EmployeeDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EmployeeEditFragment extends BaseEditFragment<Employee> {
    
	EditText mNameText;
	EditText mTelephoneText;
    EditText mAddressText;
    Spinner mStatusSp;
    
    CodeSpinnerArrayAdapter statusArrayAdapter;
    
    private EmployeeDaoService mEmployeeDaoService = new EmployeeDaoService();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.data_employee_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void initViewReference(View view) {
        
    	super.initViewReference(view);
    	
        mNameText = (EditText) view.findViewById(R.id.nameText);
    	mTelephoneText = (EditText) view.findViewById(R.id.telephoneText);
    	mAddressText = (EditText) view.findViewById(R.id.addressText);
    	mStatusSp = (Spinner) view.findViewById(R.id.statusSp);
    	
    	registerField(mNameText);
    	registerField(mTelephoneText);
    	registerField(mAddressText);
    	registerField(mStatusSp);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	
    	statusArrayAdapter = new CodeSpinnerArrayAdapter(mStatusSp, getActivity(), CodeUtil.getStatus());
    	mStatusSp.setAdapter(statusArrayAdapter);
    }
    
    @Override
    protected void updateView(Employee employee) {
    	
    	if (employee != null) {
    		
    		int statusIndex = statusArrayAdapter.getPosition(employee.getStatus());
    		
    		mNameText.setText(employee.getName());
    		mTelephoneText.setText(employee.getTelephone());
    		mAddressText.setText(employee.getAddress());
    		
    		mStatusSp.setSelection(statusIndex);
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	String telephone = mTelephoneText.getText().toString();
    	String address = mAddressText.getText().toString();
    	String status = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
    	
    	if (mItem != null) {
    		
    		Long merchantId = MerchantUtil.getMerchant().getId();
    		
    		mItem.setMerchantId(merchantId);
    		mItem.setName(name);
    		mItem.setTelephone(telephone);
    		mItem.setAddress(address);
    		mItem.setStatus(status);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		
    		String userId = UserUtil.getUser().getUserId();
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(userId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(userId);
    		mItem.setUpdateDate(new Date());
    	}
    }
    
    @Override
    protected Long getItemId(Employee employee) {
    	
    	return employee.getId(); 
    } 
    
    @Override
    protected boolean addItem() {
    	
    	mEmployeeDaoService.addEmployee(mItem);
    	
        mNameText.getText().clear();
        mTelephoneText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mEmployeeDaoService.updateEmployee(mItem);
    	
    	return true;
    }
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public Employee updateItem(Employee employee) {

    	employee = mEmployeeDaoService.getEmployee(employee.getId());
    	
    	this.mItem = employee;
    	
    	return employee;
    }
}
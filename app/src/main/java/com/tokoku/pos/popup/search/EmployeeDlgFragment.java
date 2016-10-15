package com.tokoku.pos.popup.search;

import java.util.ArrayList;
import java.util.List;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.tokoku.pos.dao.EmployeeDaoService;
import com.tokoku.pos.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class EmployeeDlgFragment extends BaseSearchDlgFragment<Employee> implements EmployeeArrayAdapter.ItemActionListener {
	
	TextView mCancelBtn;
	EditText mEmployeeSearchText;
	ListView mEmployeeListView;
	TextView mNoEmployeeText;
	
	EmployeeSelectionListener mActionListener;
	
	EmployeeArrayAdapter employeeArrayAdapter;
	
	boolean mIsMandatory = false;
	
	private EmployeeDaoService mEmployeeDaoService = new EmployeeDaoService();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        
        setCancelable(false);
        
        mItems = new ArrayList<Employee>();
        
        employeeArrayAdapter = new EmployeeArrayAdapter(getActivity(), mItems, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.popup_employee_fragment, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		mEmployeeSearchText = (EditText) getView().findViewById(R.id.employeeSearchText);
		
		mEmployeeSearchText.setText(CommonUtil.getNvlString(mQuery));
		mEmployeeSearchText.setSelection(mEmployeeSearchText.getText().length());
		//mEmployeeSearchText.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
		
		mEmployeeSearchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				mQuery = s.toString();
				
				mItems.clear();
				mItems.addAll(mEmployeeDaoService.getEmployees(mQuery, 0));
				employeeArrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mEmployeeListView = (ListView) getView().findViewById(R.id.employeeListView);
		mEmployeeListView.setAdapter(employeeArrayAdapter);
		
		mNoEmployeeText = (TextView) getView().findViewById(R.id.noEmployeeText);
		mNoEmployeeText.setOnClickListener(getNoEmployeeTextOnClickListener());
		
		mCancelBtn = (TextView) getView().findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(getCancelBtnOnClickListener());
		
		if (mIsMandatory) {
			mNoEmployeeText.setVisibility(View.GONE);
		} else {
			mNoEmployeeText.setVisibility(View.VISIBLE);
		}
		
		mItems.clear();
		mItems.addAll(mEmployeeDaoService.getEmployees(mQuery, 0));
		employeeArrayAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActionListener = (EmployeeSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CashierActionListener");
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onEmployeeSelected(Employee employee) {
		
		dismiss();
		mActionListener.onEmployeeSelected(employee);
	}
	
	public void setMandatory(boolean isMandatory) {
		
		mIsMandatory = isMandatory;
	}
	
	private View.OnClickListener getNoEmployeeTextOnClickListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mActionListener.onEmployeeSelected(null);
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
	
	@Override
	protected List<Employee> getItems(String query) {
		
		return mEmployeeDaoService.getEmployees(mQuery, 0);
	}
	
	@Override
	protected List<Employee> getNextItems(String query, int lastIndex) {
		
		return mEmployeeDaoService.getEmployees(mQuery, lastIndex);
	}
	
	@Override
	protected void refreshList() {
		
		employeeArrayAdapter.notifyDataSetChanged();
	}
}

package com.tokoku.pos.data.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.tokoku.pos.R;
import com.android.pos.dao.Employee;
import com.android.pos.dao.User;
import com.android.pos.dao.UserAccess;
import com.tokoku.pos.CodeBean;
import com.tokoku.pos.Constant;
import com.tokoku.pos.base.adapter.CodeSpinnerArrayAdapter;
import com.tokoku.pos.base.fragment.BaseEditFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.EmployeeDaoService;
import com.tokoku.pos.dao.UserAccessDaoService;
import com.tokoku.pos.dao.UserDaoService;
import com.tokoku.pos.model.FormFieldBean;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.UserUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UserEditFragment extends BaseEditFragment<User> {
    
	LinearLayout mEmployeeLayout;
	
	EditText mNameText;
	EditText mUserIdText;
    EditText mPasswordText;
    EditText mEmployeeText;
    Spinner mRoleSp;
    Spinner mStatusSp;
    
    LinearLayout accessRightPanel;
    
    List<UserAccess> mUserAccesses;
    List<ImageButton> mCheckedButtons;
    
    CodeSpinnerArrayAdapter roleArrayAdapter;
    CodeSpinnerArrayAdapter statusArrayAdapter;
    
    HashMap<UserAccess, ImageButton> mAllAccessRights = new HashMap<UserAccess, ImageButton>();
    HashMap<UserAccess, ImageButton> mCashierAccessRights = new HashMap<UserAccess, ImageButton>();
    HashMap<UserAccess, ImageButton> mWaitressAccessRights = new HashMap<UserAccess, ImageButton>();
    HashMap<UserAccess, ImageButton> mEmployeeAccessRights = new HashMap<UserAccess, ImageButton>();
    HashMap<UserAccess, ImageButton> mAdminAccessRights = new HashMap<UserAccess, ImageButton>();
    
    private UserDaoService mUserDaoService = new UserDaoService();
    private UserAccessDaoService mUserAccessDaoService = new UserAccessDaoService();
    private EmployeeDaoService mEmployeeDaoService = new EmployeeDaoService();
    
    BaseItemListener<User> mUserItemListener;
    
    LayoutInflater mInflater;
    ViewGroup mContainer;
    
    View accessView;
    
    boolean mIsEnableInputFields;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	mInflater = inflater;
    	mContainer = container;
    	
    	View view = inflater.inflate(R.layout.data_user_fragment, container, false);
    	
    	initViewReference(view);
    	
    	return view;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mUserItemListener = (BaseItemListener<User>) activity;
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
    	
    	mEmployeeLayout = (LinearLayout) view.findViewById(R.id.employeeLayout);
    	
    	mNameText = (EditText) view.findViewById(R.id.nameText);
    	mUserIdText = (EditText) view.findViewById(R.id.userIdText);
    	mPasswordText = (EditText) view.findViewById(R.id.passwordText);
    	mEmployeeText = (EditText) view.findViewById(R.id.employeeText);
    	mRoleSp = (Spinner) view.findViewById(R.id.roleSp);
    	mStatusSp = (Spinner) view.findViewById(R.id.statusSp);
    	
    	accessRightPanel = (LinearLayout) view.findViewById(R.id.accessRightsPanel);
    	
    	mEmployeeText.setFocusable(false);
    	mEmployeeText.setOnClickListener(getEmployeeOnClickListener());

    	registerField(mNameText);
    	registerField(mUserIdText);
    	registerField(mPasswordText);
    	registerField(mEmployeeText);
    	registerField(mRoleSp);
    	registerField(mStatusSp);
    	
    	enableInputFields(false);
    	
    	mandatoryFields.add(new FormFieldBean(mNameText, R.string.field_name));
    	mandatoryFields.add(new FormFieldBean(mUserIdText, R.string.field_price));
    	mandatoryFields.add(new FormFieldBean(mPasswordText, R.string.field_password));
    	
    	roleArrayAdapter = new CodeSpinnerArrayAdapter(mRoleSp, getActivity(), CodeUtil.getRoles());
    	mRoleSp.setAdapter(roleArrayAdapter);
    	
    	statusArrayAdapter = new CodeSpinnerArrayAdapter(mStatusSp, getActivity(), CodeUtil.getStatus());
    	mStatusSp.setAdapter(statusArrayAdapter);
    	
    	if (UserUtil.isMerchant()) {
    		mPasswordText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
    }
    
    @Override
    protected void updateView(User user) {

    	int employeeCount = mEmployeeDaoService.getEmployees(Constant.EMPTY_STRING, 0).size();
    	
    	if (employeeCount != 0) {
    		mEmployeeLayout.setVisibility(View.VISIBLE);
    	} else {
    		mEmployeeLayout.setVisibility(View.GONE);
    	}
    	
    	if (user != null) {
    		
    		int roleIndex = roleArrayAdapter.getPosition(user.getRole());
    		int statusIndex = statusArrayAdapter.getPosition(user.getStatus());
    		
    		mNameText.setText(user.getName());
    		mUserIdText.setText(user.getUserId());
    		mPasswordText.setText(user.getPassword());
    		
    		if (user.getEmployeeId() != null) {
    			
    			Employee employee = mEmployeeDaoService.getEmployee(user.getEmployeeId());
    			
    			if (employee != null) {
    				
    				String employeeName = employee.getName();
    				mEmployeeText.setText(employeeName);
    			}
    			
    		} else {
    			
    			mEmployeeText.setText(Constant.EMPTY_STRING);
    		}

    		mRoleSp.setSelection(roleIndex, false);
    		mStatusSp.setSelection(statusIndex);

            mRoleSp.setOnItemSelectedListener(getRoleOnItemSelectedListener());

    		accessRightPanel.removeAllViews();
    		
    		mUserAccesses = mUserAccessDaoService.getUserAccessList(user.getId());
    		
    		mCheckedButtons = new ArrayList<ImageButton>();
    		
    		for (final UserAccess userAccess : mUserAccesses) {
        		
        		accessView = mInflater.inflate(R.layout.data_user_access_list_item, mContainer, false);
        		
        		TextView nameText = (TextView) accessView.findViewById(R.id.nameText);
    			final ImageButton checkedButton = (ImageButton) accessView.findViewById(R.id.checkedButton);
    			
    			checkedButton.setEnabled(mIsEnableInputFields);
    			mCheckedButtons.add(checkedButton);
    			
    			initDefaultAccessRights(userAccess, checkedButton);
    			
    			char c = 8226;
    			
    			nameText.setText(c + " " + CodeUtil.getModuleAccessLabel(userAccess.getCode()));
    			
    			boolean isChecked = Constant.STATUS_YES.equals(userAccess.getStatus());
    			
    			if (isChecked) {
					checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
				} else {
					checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
				}
    			
    			checkedButton.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {

                        // disable editing for demo account

                        if (MerchantUtil.getMerchantId() == Constant.DEMO_MERCHANT_ID &&
                                mItem != null && Long.valueOf(Constant.DEMO_USER_ID).equals(mItem.getId())) {

                            return;
                        }
    					
    					if (!isEnableInputFields) {
    						return;
    					}
    					
    					boolean isSelected = Constant.STATUS_YES.equals(userAccess.getStatus());
    					
    					if (!isSelected) {
    						checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
    						userAccess.setStatus(Constant.STATUS_YES);
    					} else {
    						checkedButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
    						userAccess.setStatus(Constant.STATUS_NO);
    					}
    				}
    			});
    			
    			accessRightPanel.addView(accessView);
        	}
    		
    		showView();
    		
    	} else {
    		
    		hideView();
    	}
    }
    
    @Override
    protected void saveItem() {
    	
    	String name = mNameText.getText().toString();
    	String userId = CommonUtil.trim(mUserIdText.getText().toString());
    	String password = CommonUtil.trim(mPasswordText.getText().toString());
    	String role = CodeBean.getNvlCode((CodeBean) mRoleSp.getSelectedItem());
    	String status = CodeBean.getNvlCode((CodeBean) mStatusSp.getSelectedItem());
    	
    	if (mItem != null) {
    		
    		mItem.setMerchantId(MerchantUtil.getMerchantId());
    		
    		mItem.setName(name);
    		mItem.setUserId(userId);
    		mItem.setPassword(password);
    		mItem.setRole(role);
    		mItem.setStatus(status);
    		
    		mItem.setUploadStatus(Constant.STATUS_YES);
    		
    		String loginId = null;
    		
    		if (UserUtil.isMerchant()) {
    			loginId = MerchantUtil.getMerchant().getLoginId();
    		} else {
    			loginId = UserUtil.getUser().getUserId();
    		} 
    		
    		if (mItem.getCreateBy() == null) {
    			mItem.setCreateBy(loginId);
    			mItem.setCreateDate(new Date());
    		}
    		
    		mItem.setUpdateBy(loginId);
    		mItem.setUpdateDate(new Date());
    		
    		for (UserAccess userAccess : mUserAccesses) {
    			
    			userAccess.setUploadStatus(Constant.STATUS_YES);
        		
        		if (userAccess.getCreateBy() == null) {
        			userAccess.setCreateBy(loginId);
        			userAccess.setCreateDate(new Date());
        		}
        		
        		userAccess.setUpdateBy(loginId);
        		userAccess.setUpdateDate(new Date());
        	}
    	}
    }
    
    @Override
    protected Long getItemId(User user) {
    	
    	return user.getId(); 
    } 
    
    @Override
    protected boolean isValidated() {
    	
    	boolean isValidated = super.isValidated();
    	
    	if (isValidated) {
    		
    		if (MerchantUtil.getLoginId().equals(mUserIdText.getText().toString())) {
    			
    			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.msg_register_user_conflict));
    			isValidated = false;
    		}
    	}
    	
    	return isValidated;
    }
    
    @Override
    protected boolean addItem() {
    	
        mUserDaoService.addUser(mItem);
        
        for (UserAccess userAccess : mUserAccesses) {
        	
        	userAccess.setUser(mItem);
        	mUserAccessDaoService.addUserAccess(userAccess);
        }
        
        CommonUtil.sendEvent(getString(R.string.event_cat_user), getString(R.string.event_act_registration));
        
        mNameText.getText().clear();
        mUserIdText.getText().clear();
        
        mItem = null;
        
        return true;
    }
    
    @Override
    protected boolean updateItem() {
    	
    	mUserDaoService.updateUser(mItem);
		mUserAccessDaoService.updateUserAccess(mUserAccesses);
		
		User user = UserUtil.getUser();
		
		if (user.getId() == mItem.getId()) {
			user = mUserDaoService.getUser(mItem.getId());
			UserUtil.setUser(user);
		}
    	
    	return true;
    }
    
    public void setEmployee(Employee employee) {
		
    	if (mItem != null) {
    		
    		if (employee != null) {
    			
    			mItem.setEmployeeId(employee.getId());
    			
    		} else {
    			
    			mItem.setEmployee(null);	
    		}
    		
    		updateView(mItem);
    	}
	}
    
    @Override
    protected TextView getFirstField() {
    	
    	return mNameText;
    }
    
    @Override
    public User updateItem(User user) {

    	user = mUserDaoService.getUser(user.getId());
    	
    	this.mItem = user;
    	
    	return user;
    }
    
    @Override
    public void enableInputFields(boolean isEnabled) {
    	
    	super.enableInputFields(isEnabled);
    	
    	mIsEnableInputFields = isEnabled;
    	
    	if (mCheckedButtons == null) {
    		return;
    	}
    	
    	for (ImageButton button : mCheckedButtons) {
    		button.setEnabled(isEnabled);
    	}
    	
    	// disable editing for demo account
    	
    	if (MerchantUtil.getMerchantId() == Constant.DEMO_MERCHANT_ID && 
    		mItem != null && Long.valueOf(Constant.DEMO_USER_ID).equals(mItem.getId())) {
    		
    		mNameText.setEnabled(false);
    		mUserIdText.setEnabled(false);
    		mPasswordText.setEnabled(false);
            mRoleSp.setEnabled(false);
            mStatusSp.setEnabled(false);
    	}
    }
    
    private View.OnClickListener getEmployeeOnClickListener() {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mEmployeeText.isEnabled()) {
					
					if (isEnableInputFields) {
						
						saveItem();
						
						boolean isMandatory = false;
						mUserItemListener.onSelectEmployee(isMandatory);
					}
				}
			}
		};
    }
    
    private void initDefaultAccessRights(UserAccess userAccess, ImageButton checkedBtn) {
    	
    	mAllAccessRights.put(userAccess, checkedBtn);
    	
    	if (Constant.ACCESS_CASHIER.equals(userAccess.getCode()) || Constant.ACCESS_ORDER.equals(userAccess.getCode()) || Constant.ACCESS_REPORT_TRANSACTION.equals(userAccess.getCode())) {
    		
    		mCashierAccessRights.put(userAccess, checkedBtn);
    	}
    	
    	if (Constant.ACCESS_WAITRESS.equals(userAccess.getCode()) || Constant.ACCESS_ORDER.equals(userAccess.getCode())) {
    		
    		mWaitressAccessRights.put(userAccess, checkedBtn);
    	}
    	
    	if (Constant.ACCESS_REPORT_COMMISION.equals(userAccess.getCode())) {
    		
    		mWaitressAccessRights.put(userAccess, checkedBtn);
    	}
    	
    	if (!Constant.ACCESS_WAITRESS.equals(userAccess.getCode()) && !Constant.ACCESS_ORDER.equals(userAccess.getCode())) {
    		
    		mAdminAccessRights.put(userAccess, checkedBtn);
    	}
    }
    
    private AdapterView.OnItemSelectedListener getRoleOnItemSelectedListener() {
    	
    	return new AdapterView.OnItemSelectedListener() {
    		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		    	CodeBean codeBean = (CodeBean) mRoleSp.getSelectedItem();
		    	
		    	Set<UserAccess> keys = mAllAccessRights.keySet();
		    	HashMap<UserAccess, ImageButton> accessRights = mAllAccessRights;
		    	
		    	for (UserAccess userAccess : keys) {
	    			
	    			ImageButton checkedBtn = accessRights.get(userAccess);
	    			
	    			userAccess.setStatus(Constant.STATUS_NO);
	    			checkedBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_clear_black));
	    		}
		    	
		    	if (Constant.USER_ROLE_CASHIER.equals(codeBean.getCode())) {
		    		
		    		keys = mCashierAccessRights.keySet();
		    		accessRights = mCashierAccessRights;
		    	
		    	} else if (Constant.USER_ROLE_WAITRESS.equals(codeBean.getCode())) {
		    		
		    		keys = mWaitressAccessRights.keySet();
		    		accessRights = mWaitressAccessRights;
		    	
		    	} else if (Constant.USER_ROLE_EMPLOYEE.equals(codeBean.getCode())) {
		    		
		    		keys = mEmployeeAccessRights.keySet();
		    		accessRights = mEmployeeAccessRights;
		    	
		    	} else if (Constant.USER_ROLE_ADMIN.equals(codeBean.getCode())) {
		    		
		    		keys = mAdminAccessRights.keySet();
		    		accessRights = mAdminAccessRights;
		    	}
		    	
		    	if (keys == null) { return; }
		    	
		    	for (UserAccess userAccess : keys) {
	    			
	    			ImageButton checkedBtn = accessRights.get(userAccess);
	    			
	    			userAccess.setStatus(Constant.STATUS_YES);
	    			checkedBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_black));
	    		}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
    }
}
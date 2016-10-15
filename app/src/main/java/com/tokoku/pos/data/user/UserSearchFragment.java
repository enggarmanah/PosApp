package com.tokoku.pos.data.user;

import java.util.List;

import com.android.pos.dao.User;
import com.tokoku.pos.R;
import com.tokoku.pos.base.fragment.BaseSearchFragment;
import com.tokoku.pos.base.listener.BaseItemListener;
import com.tokoku.pos.dao.UserDaoService;
import com.tokoku.pos.util.NotificationUtil;

import android.app.Activity;

public class UserSearchFragment extends BaseSearchFragment<User> {

	private UserDaoService mUserDaoService = new UserDaoService();

	public void initAdapter() {
		
		mAdapter = new UserSearchArrayAdapter(getActivity(), mItems, mSelectedItem, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mItemListener = (BaseItemListener<User>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BaseItemListener<User>");
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
		
		/*if (mItems != null && mItems.size() == 0) {
			NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.msg_register_user));
		}*/
	}
	
	protected Long getItemId(User item) {
		return item.getId();
	}
	
	public List<User> getItems(String query) {

		return mUserDaoService.getUsers(query, 0);
	}
	
	public List<User> getNextItems(String query, int lastIndex) {

		return mUserDaoService.getUsers(query, lastIndex);
	}

	public void onItemDeleted(User item) {

		mUserDaoService.deleteUser(item);
		
		mItems.remove(item);
		mAdapter.notifyDataSetChanged();
		
		mSelectedItem = null;
		mItemListener.onDeleteCompleted();
	}
}
package com.tokoku.pos.data.user;

import java.util.List;

import com.android.pos.dao.User;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;

import android.content.Context;

public class UserSearchArrayAdapter extends BaseSearchArrayAdapter<User> {
	
	public UserSearchArrayAdapter(Context context, List<User> users, User selectedUser, ItemActionListener<User> listener) {
		super(context, users, selectedUser, listener);
	}
	
	@Override
	public Long getItemId(User user) {
		return user.getId();
	}
	
	@Override
	public String getItemName(User user) {
		return user.getName();
	}
}
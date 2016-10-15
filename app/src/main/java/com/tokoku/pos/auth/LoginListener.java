package com.tokoku.pos.auth;

import com.android.pos.dao.Merchant;
import com.android.pos.dao.User;

public interface LoginListener {
	
	public void onMerchantValidated(Merchant merchant);
	
	public void onUserValidated(User user);
	
	public void onMerchantsUpdated();
}

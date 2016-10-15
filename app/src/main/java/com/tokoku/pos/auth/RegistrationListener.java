package com.tokoku.pos.auth;

import com.android.pos.dao.Merchant;

public interface RegistrationListener {
	
	public void onMerchantRegistered(Merchant merchant);
}

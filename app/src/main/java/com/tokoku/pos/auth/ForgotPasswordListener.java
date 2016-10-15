package com.tokoku.pos.auth;

import com.android.pos.dao.Merchant;

public interface ForgotPasswordListener {
	
	public void onMerchantRetrieved(Merchant merchant);
}

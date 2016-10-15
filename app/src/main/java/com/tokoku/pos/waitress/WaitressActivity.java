package com.tokoku.pos.waitress;

import com.tokoku.pos.R;
import com.tokoku.pos.cashier.CashierActivity;

public class WaitressActivity extends CashierActivity {
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		setTitle(getString(R.string.menu_waitress));
		setSelectedMenu(getString(R.string.menu_waitress));
	}
}

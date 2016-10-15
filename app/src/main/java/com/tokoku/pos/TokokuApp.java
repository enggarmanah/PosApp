package com.tokoku.pos;

import com.tokoku.pos.util.CommonUtil;

import android.app.Application;

public class TokokuApp extends Application {

	@Override
	public void onCreate() {
		
		CommonUtil.initTracker(this);
	}
}

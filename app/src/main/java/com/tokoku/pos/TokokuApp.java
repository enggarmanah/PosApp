package com.tokoku.pos;

import com.tokoku.pos.util.CommonUtil;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

public class TokokuApp extends MultiDexApplication {

	@Override
	public void onCreate() {

		super.onCreate();
		CommonUtil.initTracker(this);
	}
}

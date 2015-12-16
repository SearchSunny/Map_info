package com.mapbar.info.collection;

import android.app.Application;
/**
 * 全局Application
 * @author miaowei
 *
 */
public class MApplacation extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MException handler = MException.getInstance();
		handler.init(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

}

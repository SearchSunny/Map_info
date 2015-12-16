package com.mapbar.info.collection.net;

import android.content.Context;

import com.mapbar.android.net.HttpHandler;

public class MHttpHandler extends HttpHandler {
	
	public MHttpHandler(Context context) {
		this("Col", context);
	}

	public MHttpHandler(String tag, Context context) {
		super(tag, context);
	}
}

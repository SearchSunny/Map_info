package com.mapbar.info.collection.widget;

import java.util.ArrayList;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class MyPageAdapter extends PagerAdapter {

	private ArrayList<ImageView> mList;

	public MyPageAdapter(ArrayList<ImageView> list) {
		this.mList = list;
	}

	@Override
	public void destroyItem(View cellction, int position, Object view) {
		((ViewPager) cellction).removeView(mList.get(position));// 销毁position位置的界面
	}

	@Override
	public int getCount() {
		return mList.size();// 初始化position位置的界面
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		((ViewPager) collection).addView(mList.get(position), 0);// 初始化position位置的界面
		return mList.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (obj);// 判断是否由对象生成界面
	}
}

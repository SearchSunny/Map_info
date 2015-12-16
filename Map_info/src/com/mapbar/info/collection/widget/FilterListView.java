package com.mapbar.info.collection.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 过滤(附近\全部)搜索之后的数据展示列表
 * @author miaowei
 *
 */
public class FilterListView extends ListView {

	private FilterVisibleListener listener;

	public FilterListView(Context context) {
		super(context);
	}

	public FilterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FilterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (listener != null)
			listener.onFilterVisibleChageListener(visibility, getId());
	}

	public void setOnVisibleListener(FilterVisibleListener listener) {
		this.listener = listener;
	}
}

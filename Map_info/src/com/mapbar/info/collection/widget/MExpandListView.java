package com.mapbar.info.collection.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;

import com.mapbar.info.collection.util.LogPrint;
/**
 * 自定义MExpandListView(可扩展列表view),我的任务中用到
 * @author miaowei
 *
 */
public class MExpandListView extends ExpandableListView {
	private int mStartY, mMoveY;
	private int mHeadViewHeight = 50;
	/**
	 * 
	 */
	private ListViewFooter listViewFooter;
	private RefreshList refreshList;

	/**
	 * 刷新回调接口
	 * @author miaowei
	 *
	 */
	public interface RefreshList {
		public void onRefershList();
	}

	public void setRefreshListener(RefreshList refreshList) {
		this.refreshList = refreshList;

	}

	public MExpandListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		listViewFooter = new ListViewFooter(context);
		listViewFooter.setState(ListViewFooter.STATE_NORMAL);
		addFooterView(listViewFooter);
	}

	public MExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LogPrint.Print("MExpandListView==this==="+this);
		listViewFooter = new ListViewFooter(context);
		listViewFooter.setState(ListViewFooter.STATE_NORMAL);
		addFooterView(listViewFooter);
		listViewFooter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载更多
				if (!getAdapter().isEmpty()) {
					listViewFooter.setVisibility(View.VISIBLE);
					listViewFooter.setState(ListViewFooter.STATE_LOADING);
					if (refreshList != null) {
						refreshList.onRefershList();
					}
				}

			}
		});
	}

	public MExpandListView(Context context) {
		super(context);

		addFooterView(new ListViewFooter(context));

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mStartY = (int) event.getY();
			break;
			case MotionEvent.ACTION_UP:
				if ((((int) event.getY()) - mStartY) < -mHeadViewHeight && getLastVisiblePosition() >= (getCount() - 1)) {
					// 加载更多
					if (!getAdapter().isEmpty()) {
						listViewFooter.setVisibility(View.VISIBLE);
						listViewFooter.setState(ListViewFooter.STATE_LOADING);
						if (refreshList != null) {
							refreshList.onRefershList();
						}
						listViewFooter.setVisibility(View.GONE);
					}
				}
			break;

			default:
			break;
		}
		return super.onTouchEvent(event);
	}

	public void closeFooter() {
		listViewFooter.setState(ListViewFooter.STATE_NORMAL);
	}

	public void setFooterVisible(int visible) {
		listViewFooter.setVisibility(visible);
	}

}

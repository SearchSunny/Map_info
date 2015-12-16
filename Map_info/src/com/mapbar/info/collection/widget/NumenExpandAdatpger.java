package com.mapbar.info.collection.widget;

import com.mapbar.info.collection.R;
import com.mapbar.info.collection.widget.MExpandListAdatpger.ViewHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 热点答疑
 * @author rock
 * 
 */
public class NumenExpandAdatpger extends BaseExpandableListAdapter {
	private String[] guroupData;
	private String[] childData;
	private Context mContext;

	public NumenExpandAdatpger(Context mContext, String[] groupdata, String[] childdata) {
		this.guroupData = groupdata;
		this.childData = childdata;
		this.mContext = mContext;
	}

	@Override
	public int getGroupCount() {

		return guroupData.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View itemView, ViewGroup parent) {
		ViewHolder viewHoler;
		final int i = groupPosition;

		if (itemView == null) {
			itemView = View.inflate(mContext, R.layout.layout_numen_item, null);
			viewHoler = new ViewHolder();
			viewHoler.textView = (TextView) itemView.findViewById(R.id.numen_item_text);
			viewHoler.imageView = (ImageView) itemView.findViewById(R.id.nemen_request_starte);
			itemView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) itemView.getTag();
		}
		viewHoler.textView.setText(guroupData[groupPosition]);
		if (isExpanded)
			viewHoler.imageView.setImageResource(R.drawable.my_task_list_oopen);
		else
			viewHoler.imageView.setImageResource(R.drawable.my_task_list_close);

		return itemView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View itemView, ViewGroup parent) {
		ViewHolder_child viewHoler;
		final int i = groupPosition;

		if (itemView == null) {
			itemView = View.inflate(mContext, R.layout.layout_numen_item_child, null);
			viewHoler = new ViewHolder_child();
			viewHoler.textView = (TextView) itemView.findViewById(R.id.numen_item_child_text);
			itemView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder_child) itemView.getTag();
		}
		viewHoler.textView.setText(childData[groupPosition]);

		return itemView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	class ViewHolder {
		TextView textView;
		ImageView imageView;

	}

	class ViewHolder_child {
		TextView textView;

	}
}

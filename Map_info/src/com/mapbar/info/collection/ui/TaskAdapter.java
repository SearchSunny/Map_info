package com.mapbar.info.collection.ui;

import java.util.ArrayList;

import com.mapbar.info.collection.R;
import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
/**
 * 搜索出的任务
 * @author miaowei
 *
 */
public class TaskAdapter extends BaseAdapter implements ListAdapter {

	private Context mContext;
	private ArrayList<TaskDetail> adrList;

	/**
	 * 构造函数(搜索出的任务)
	 * 
	 * @param context
	 * @param list
	 *            地址
	 * @param rList
	 *            价格
	 */
	public TaskAdapter(Context context, ArrayList<TaskDetail> list) {

		this.mContext = context;
		this.adrList = list;

	}

	@Override
	public int getCount() {
		return adrList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	public TaskDetail getTask(int position) {
		return adrList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup arg2) {
		ViewHoler viewHoler;
		if (itemView == null) {
			itemView = View.inflate(mContext, R.layout.layout_list_item, null);
			viewHoler = new ViewHoler();
			viewHoler.adrView = (TextView) itemView.findViewById(R.id.layout_list_item_adr);
			viewHoler.riceView = (TextView) itemView.findViewById(R.id.layout_list_item_rice);
			itemView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler) itemView.getTag();
		}
		viewHoler.adrView.setText(adrList.get(position).getName());
		viewHoler.riceView.setText(adrList.get(position).getRice() + "元/条");

		return itemView;
	}

	class ViewHoler {
		TextView adrView;
		TextView riceView;
	}
}

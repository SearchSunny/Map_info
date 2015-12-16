package com.mapbar.info.collection.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.info.collection.PageListener;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.StringUtils;

/**
 * 待提交\已提交\已过期(ListAdapter)
 * @author rock
 * 
 */
@SuppressLint("UseSparseArrays")
public class MExpandListAdatpger extends BaseExpandableListAdapter {

	private onCheckOpenGroupListener openGroupListener;

	public interface onCheckOpenGroupListener {
		public void onCheckOpenGroup(int group);
	}

	public void setOnCheckOPenGroupLisrener(onCheckOpenGroupListener listener) {
		openGroupListener = listener;
	}

	private List<TaskDetail> list;
	private Context mContext;
	/**
	 * 父节点状态
	 */
	private Map<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	/**
	 * 
	 */
	private Map<Integer, Boolean> bridge_state = new HashMap<Integer, Boolean>();
	/**
	 * 子节点状态
	 */
	private Map<Integer, HashMap<Integer, Boolean>> childState = new HashMap<Integer, HashMap<Integer, Boolean>>();
	private int flag;
	private PageListener listener;

	public void setPageListener(PageListener pageListener) {
		listener = pageListener;
	}

	public MExpandListAdatpger(Context context, List<TaskDetail> tasks) {
		LogPrint.Print("MExpandListAdatpger==list==="+MExpandListAdatpger.this);
		LogPrint.Print("MExpandListAdatpger==list.size()==="+tasks.size());
		this.list = tasks;
		this.mContext = context;

	}

	/**
	 * 设置数据源
	 * @param tasks
	 */
	public void setListTaskDetails(List<TaskDetail> tasks){
		
		this.list = tasks;
	}
	
	public Map<Integer, Boolean> getGroupSelected() {
		return state;
	}

	public int getDataSize() {
		return list.size();
	}

	public Map<Integer, HashMap<Integer, Boolean>> getChildGroupSelected() {
		return childState;
	}

	public void addState(int i, boolean b) {
		state.put(i, b);
	}

	public void addChildState(int k, HashMap<Integer, Boolean> map) {
		childState.put(k, map);
	}

	public TaskDetail getTaskDetail(int group) {
		return list.get(group);
	}

	public TaskPoint getTaskPoint(int group, int childPosition) {
		return list.get(group).getList().get(childPosition);
	}

	@Override
	public int getGroupCount() {

		return list.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		LogPrint.Print("console","MExpandListAdatpger=getChildrenCount="+list.get(groupPosition).getList().size());
		return list.get(groupPosition).getList().size();
	}

	public void removeChecked(int k) {
		state.remove(k);
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private boolean checked;

	public void setGroupChecked(boolean b) {
		this.checked = b;
	}

	private boolean isPreesed = true;

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View itemView, ViewGroup parent) {
		ViewHolder viewHoler;
		final int i = groupPosition;
		LogPrint.Print("console","MExpandListAdatpger=groupPosition="+groupPosition +"i="+i);
		if (itemView == null) {
			itemView = View.inflate(mContext, R.layout.layout_my_task_list_item, null);
			viewHoler = new ViewHolder();
			viewHoler.checkBox = (CheckBox) itemView.findViewById(R.id.my_task_list_checkbox);
			viewHoler.textView = (TextView) itemView.findViewById(R.id.my_task_list_text);
			viewHoler.textView2 = (TextView) itemView.findViewById(R.id.my_task_list_text_decri);
			viewHoler.textView3 = (TextView) itemView.findViewById(R.id.my_task_list_text_decri_times);
			viewHoler.textView4 = (TextView) itemView.findViewById(R.id.my_task_list_text_decri_statue);
			viewHoler.imageView = (ImageView) itemView.findViewById(R.id.my_task_list_img);
			itemView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) itemView.getTag();
		}
		viewHoler.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bridge_state.put(i, checked);
			}
		});
		viewHoler.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifyDataSetChanged();
				setGroupChecked(isChecked);
				if (isChecked) {
					addState(i, isChecked);
					//实现全部获取提交
					if (list.get(i).getList() != null && list.get(i).getList().size() > 0) {
					
						for (int j = 0; j < list.get(i).getList().size(); j++) {
							
							HashMap<Integer, Boolean> child = getChildMap(i);
							if (isChecked) {
								child.put(j, true);
								
							} else {
								child.remove(j);
							}
							childState.put(i, child);
							
						}
					}
					
				} else if (!isChecked) {
					removeChecked(i);
					if (list != null && list.size() > 0) {
						
						if (list.get(i).getList() != null && list.get(i).getList().size() > 0) {
							
							for (int j = 0; j < list.get(i).getList().size(); j++) {
								
								HashMap<Integer, Boolean> child = getChildMap(i);
								if (isChecked) {
									child.put(j, true);

								} else {
									child.remove(j);
								}
								childState.put(i, child);
								
							}
						}
					}
					
					
				}

			}
		});

		if (state.containsKey(groupPosition)) {
			viewHoler.checkBox.setChecked(true);
			if (openGroupListener != null)
				openGroupListener.onCheckOpenGroup(groupPosition);
		} else {
			viewHoler.checkBox.setChecked(false);
		}

		viewHoler.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.showOtherPage(groupPosition);
			}
		});
		if (list != null && list.size() > 0) {
			viewHoler.textView.setText(list.get(groupPosition).getName());
			viewHoler.textView2.setText(list.get(groupPosition).getList().size() + "个采集点");
			String lostTime = list.get(groupPosition).getLostTime();
			if (!StringUtils.isNullColums(lostTime)) {
				
				viewHoler.textView3.setText("任务到期时间：" + lostTime);
			}else {
				
				viewHoler.textView3.setVisibility(View.GONE);
			}
			
			if (list.get(groupPosition).getVerifyInfo() != null && !list.get(groupPosition).getVerifyInfo().equals("")
					&& !list.get(groupPosition).getVerifyInfo().equals("null")) {
				viewHoler.textView4.setVisibility(View.VISIBLE);
				viewHoler.textView4.setText(list.get(groupPosition).getVerifyInfo());
			} else {
				viewHoler.textView4.setVisibility(View.GONE);
			}
		}
		
		return itemView;
	}

	public HashMap<Integer, Boolean> getChildMap(int k) {
		HashMap<Integer, Boolean> child = null;
		if (childState.containsKey(k)) {
			child = childState.get(k);
		} else {
			child = new HashMap<Integer, Boolean>();
		}
		return child;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View itemView, ViewGroup parent) {
		ViewHolderChild viewHoler;
		final int k = groupPosition;
		final int c = childPosition;
		LogPrint.Print("console","MExpandListAdatpger=getChildView="+childPosition);
		if (itemView == null) {
			itemView = View.inflate(mContext, R.layout.layout_my_task_list_child_item, null);
			viewHoler = new ViewHolderChild();
			viewHoler.checkBox = (CheckBox) itemView.findViewById(R.id.my_task_list_checkbox_child);
			viewHoler.textView = (TextView) itemView.findViewById(R.id.my_task_list_text);
			viewHoler.textView2 = (TextView) itemView.findViewById(R.id.my_task_list_text_child_dcri);
			viewHoler.textView3 = (TextView) itemView.findViewById(R.id.my_task_list_text_child_status);
			itemView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolderChild) itemView.getTag();
		}
		if (list != null && list.size() > 0) {
			
			TaskPoint point = list.get(groupPosition).getList().get(childPosition);
			Log.e("data", "point=" + point.getIsFull());
			Log.e("data", "poi=" + point.getName());
			if (point.getIsFull() == MColums.POINT_FULL) {
				viewHoler.checkBox.setEnabled(true);
			} else {
				viewHoler.checkBox.setEnabled(false);
			}
		}
		
		/*if (bridge_state.containsKey(k)) {
			HashMap<Integer, Boolean> child = getChildMap(k);
			if (bridge_state.get(k)) {
				child.put(c, true);
			} else {
				child.remove(c);
			}
			childState.put(k, child);

		}*/
		viewHoler.checkBox.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bridge_state.remove(k);
				return false;
			}
		});
		viewHoler.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifyDataSetChanged();
				HashMap<Integer, Boolean> child = getChildMap(k);
				if (isChecked) {
					child.put(c, true);
					int size = getChildrenCount(k);
					if (child.size() == size)
						addState(k, isChecked);

				} else {
					child.remove(c);
					removeChecked(k);
				}
				childState.put(k, child);

			}
		});

		if (childState.containsKey(groupPosition)) {
			boolean b = childState.get(groupPosition).containsKey(childPosition);
			if (b)
				viewHoler.checkBox.setChecked(true);
			else
				viewHoler.checkBox.setChecked(false);
		} else {
			viewHoler.checkBox.setChecked(false);
		}
		if (list != null && list.size() > 0) {
			
			viewHoler.textView.setText(list.get(groupPosition).getList().get(childPosition).getName());
			viewHoler.textView2.setText("单号：" + list.get(groupPosition).getList().get(childPosition).getOrder());
			TaskPoint point2 = list.get(groupPosition).getList().get(childPosition);
			if (point2.getStatus() != null && !point2.getStatus().equals("") && !point2.getStatus().equals("null")) {
				viewHoler.textView3.setVisibility(View.VISIBLE);
				String statu = null;
				if (point2.getStatus().equals("0")) {
					
					statu = "待审核";
				}else if (point2.getStatus().equals("1")) {
					
					statu = "通过";
				}
				else if (point2.getStatus().equals("2")) {
					
					statu = "驳回";
				}else if (point2.getStatus().equals("1")) {
					
					statu = "待处理";
				}
					
				viewHoler.textView3.setText(statu);
			} else {
				viewHoler.textView3.setVisibility(View.GONE);
			}
		}
		
		return itemView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ViewHolder {
		CheckBox checkBox;
		TextView textView;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		ImageView imageView;

		@Override
		public String toString() {

			return "ViewHolder";
		}
	}

	class ViewHolderChild {
		CheckBox checkBox;
		TextView textView;
		TextView textView2;
		TextView textView3;

		@Override
		public String toString() {
			return "ViewHolderChild";
		}
	}

	public void destory() {
		state.clear();
		childState.clear();
	}

	@Override
	public long getCombinedGroupId(long groupId) {

		return super.getCombinedGroupId(groupId);
	}

	/**
	 * 清除节点状态
	 */
	public void reStore() {
		state.clear();
		childState.clear();
		bridge_state.clear();
	}

}

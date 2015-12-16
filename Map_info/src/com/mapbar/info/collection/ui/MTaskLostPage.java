package com.mapbar.info.collection.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.PageListener;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.util.FileSizeUtil;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.widget.MExpandListAdatpger;
import com.mapbar.info.collection.widget.MExpandListAdatpger.onCheckOpenGroupListener;
import com.mapbar.info.collection.widget.MExpandListView;
import com.mapbar.info.collection.widget.MExpandListView.RefreshList;
/**
 * 我的任务(已过期页面)
 * @author miaowei
 *
 */
public class MTaskLostPage extends BasePage implements OnGroupClickListener, OnChildClickListener, PageListener,
		onCheckOpenGroupListener {

	/**
	 * 已过期数据源
	 */
	private ArrayList<TaskDetail> arrayListLost = new ArrayList<TaskDetail>();

	/**
	 * 已过期ListView
	 */
	private MExpandListView expandableListView;
	private MExpandListAdatpger mExpandListAdatpger;
	private ActivityInterface activityInterface;
	private MTaskPage mTaskPage;
	private Context mContext;

	private LinkedList<Integer> list_group_position;
	private HashMap<Integer, LinkedList<Integer>> hashMap;
	private LinkedList<Integer> chlid_list;
	private int groupPosition;
	private int childPosition;
	private DecimalFormat df = new DecimalFormat("######0.00");
	private int start = 0;
	private int end = 20;

	/**
	 * 我的任务(已过期)
	 * @param context
	 * @param view
	 * @param event
	 */
	public MTaskLostPage(Context context, View view, ActivityInterface event) {
		
		LogPrint.Print("MTaskLostPage==initView==="+this);
		mContext = context;
		activityInterface = event;
		expandableListView = (MExpandListView) view.findViewById(R.id.my_task_list_lost);
		mExpandListAdatpger = new MExpandListAdatpger(context, arrayListLost);
		mExpandListAdatpger.setPageListener(this);
		expandableListView.setAdapter(mExpandListAdatpger);
		expandableListView.setOnGroupClickListener(this);
		expandableListView.setOnChildClickListener(this);
		mExpandListAdatpger.setOnCheckOPenGroupLisrener(this);
		text = (TextView) view.findViewById(R.id.my_task_text);

		/*expandableListView.setRefreshListener(new RefreshList() {

			@Override
			public void onRefershList() {
				setTaskDetail();
			}
		});*/
	}

	
	private void setTaskDetail() {
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllTask(MColums.LOST);
		if (details.size() > 0)
			start = start + 20;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			ArrayList<TaskPoint> points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.LOST);
			if (points.size() == 0) {
				DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayListLost.add(taskDetail);
		}
		if (arrayListLost.size() == 0) {
			expandableListView.setVisibility(View.GONE);
		}
		mExpandListAdatpger.notifyDataSetChanged();
	}

	private void setTaskDetail(int start, int end) {
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllTask(MColums.LOST);
		if (details.size() > 0)
			start = end;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			ArrayList<TaskPoint> points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.LOST);
			if (points.size() == 0) {
				DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayListLost.add(taskDetail);
		}
		mExpandListAdatpger.notifyDataSetChanged();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		if (vis == View.VISIBLE) {
			setTotal();
		}
		super.onAttachedToWindow(flag, position);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	private TextView text;
	private double size = 0;
	private int vis = -1;

	/**
	 * 加载(已过期)数据
	 * @param visible
	 */
	public void setListVisible(int visible) {
		vis = visible;
		expandableListView.setVisibility(visible);
		if (visible == View.VISIBLE) {
			setTaskDetail();
			setTotal();
		} else {
			start = 0;
			end = 20;
			arrayListLost.clear();
		}
	}

	/**
	 * 本地数据库中统计已过期任务的数量
	 */
	private void setTotal() {
		ArrayList<TaskDetail> t_list = DManager.getInstance(mContext).queryAllTask(MColums.LOST);
		int p_size = DManager.getInstance(mContext).queryOldPoint(MColums.LOST).size();
		size = 0;
		for (int i = 0; i < t_list.size(); i++) {
			ArrayList<TaskPoint> p_list = DManager.getInstance(mContext)
					.queryPoint(t_list.get(i).getId(), MColums.LOST);
			for (int j = 0; j < p_list.size(); j++) {
				Double double1 = FileSizeUtil.getFileOrFilesSize(SdcardUtil.getSdcardCollInfo() + t_list.get(i).getId()
						+ "/" + p_list.get(j).getIname(), FileSizeUtil.SIZETYPE_MB);
				size = size + double1;
			}
		}

		text.setText("已过期" + t_list.size() + "个任务，" + p_size + "个采集点。" + "本地占用" + df.format(size) + "MB");
	}

	public boolean getListVisible() {
		int v = expandableListView.getVisibility();
		return v == View.VISIBLE ? true : false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		TaskPoint taskPoint = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		activityInterface.setOldTaskPiint(taskPoint);
		activityInterface.showPage(Configs.VIEW_POSITION_MY_OLD, mTaskPage, Configs.VIEW_POSITION_OLD_ITEM_DETAIL, 0,
				null, null);

		return false;

	}

	public void notifiDataChaged() {
		arrayListLost.clear();
		mExpandListAdatpger.notifyDataSetChanged();
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		return false;
	}

	@Override
	public void showOtherPage(int group) {
		TaskDetail detail = mExpandListAdatpger.getTaskDetail(group);
		activityInterface.setOldTaskDetail(detail);
		activityInterface
				.showPage(Configs.VIEW_POSITION_MY_TASK, this, Configs.VIEW_POSITION_OLD_DETAIL, 0, null, null);
	}

	public void setTaskPage(MTaskPage mTaskPage) {

		this.mTaskPage = mTaskPage;
	}

	/**
	 * 
	 * 
	 * @param type
	 */
	protected HashMap<Integer, LinkedList<Integer>> getCheckedState() {
		HashMap<Integer, LinkedList<Integer>> map = new HashMap<Integer, LinkedList<Integer>>();
		Map<Integer, HashMap<Integer, Boolean>> child = mExpandListAdatpger.getChildGroupSelected();
		if (child.size() > 0) {

			Set<Entry<Integer, HashMap<Integer, Boolean>>> set_child = child.entrySet();
			Iterator<Entry<Integer, HashMap<Integer, Boolean>>> iterator_child = set_child.iterator();
			LinkedList<Integer> list = null;
			while (iterator_child.hasNext()) {
				Entry<Integer, HashMap<Integer, Boolean>> entry = iterator_child.next();
				HashMap<Integer, Boolean> value = entry.getValue();
				int group_positon = entry.getKey();
				Set<Integer> value_set = value.keySet();
				Iterator<Integer> v_iteor = value_set.iterator();
				list = new LinkedList<Integer>();
				while (v_iteor.hasNext()) {
					int childPosition = v_iteor.next();
					list.add(childPosition);
				}
				map.put(group_positon, list);
			}
		}
		return map;
	}

	public void startDThread() {
		new Thread(new startDeleteThread()).start();
	}

	private class startDeleteThread implements Runnable {
		@Override
		public void run() {
			startDelete();
		}
	}

	public void startDelete() {
		hashMap = getCheckedState();
		if (hashMap.size() == 0) {
			handler.sendEmptyMessage(delete_null);
			return;
		}
		Set<Integer> set = hashMap.keySet();
		Iterator<Integer> iterator = set.iterator();
		list_group_position = new LinkedList<Integer>();
		while (iterator.hasNext()) {
			int position = iterator.next();
			list_group_position.add(position);
		}
		setUpload();
	}

	private void setUpload() {
		if (list_group_position.size() > 0) {
			groupPosition = list_group_position.get(0);
			chlid_list = hashMap.get(groupPosition);
			setUpload2();
		}
	}

	private void setUpload2() {
		if (chlid_list.size() == 0) {
			activityInterface.dismissNetDialog();
			 handler.sendEmptyMessage(null_null);
			return;
		}
		childPosition = chlid_list.get(0);
		handler.sendEmptyMessage(delete_next);
	}

	private final int delete_next = 0;
	private final int delete_false = 1;
	private final int delete_null = 2;
	private final int null_null = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{

				case delete_next:
					mExpandListAdatpger.notifyDataSetChanged();

					if (chlid_list.size() > 0)
						chlid_list.remove(0);
					if (chlid_list.size() > 0) {

						delete();
						setUpload2();
						break;
					} else {
						if (list_group_position.size() > 0)
							list_group_position.remove(0);
						if (list_group_position.size() > 0) {
							delete();
							setUpload();
							break;
						} else {
							delete();
							setTaskDetail(0, start + end);
							activityInterface.dismissNetDialog();
							mExpandListAdatpger.notifyDataSetChanged();
							mExpandListAdatpger.reStore();
							break;
						}
					}

//					delete();
//					setUpload2();
//				break;
				case delete_false:
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
					activityInterface.dismissNetDialog();
					mExpandListAdatpger.reStore();
				break;
				case delete_null:
					activityInterface.dismissNetDialog();
					if (mExpandListAdatpger.getDataSize() > 0)
						Toast.makeText(mContext, "请选择要删除的数据", 0).show();
					else
						Toast.makeText(mContext, "您没有任何任务", 0).show();
					break;
				case null_null:
					Toast.makeText(mContext, "请选择要删除的点", 0).show();
				break;
				default:
				break;
			}
		};
	};

	public void delete() {
		TaskPoint point = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		TaskDetail detail = mExpandListAdatpger.getTaskDetail(groupPosition);
		String path = SdcardUtil.getSdcardCollInfo() + detail.getId() + "/" + point.getIname();
		mTaskPage.deletePhoto(path);
		mTaskPage.deletePhoto(SdcardUtil.getSdcardCollInfo() + detail.getId());
		DManager.getInstance(mContext).deletePoint(point.getCameraId());

	}

	@Override
	public void onCheckOpenGroup(int group) {
		// TODO Auto-generated method stub
		expandableListView.expandGroup(group);
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		start = 0;
		end = 20;
		arrayListLost.clear();
		super.onDetachedFromWindow(flag);
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "MTaskLostPage--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
}

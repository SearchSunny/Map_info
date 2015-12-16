package com.mapbar.info.collection.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.PageListener;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.db.MColums.PF_STATE;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.upload.FileUtil;
import com.mapbar.info.collection.upload.FormFile;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.MessageID;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.TimerTaskGetImageLength;
import com.mapbar.info.collection.util.Util;
import com.mapbar.info.collection.widget.MExpandListAdatpger;
import com.mapbar.info.collection.widget.MExpandListAdatpger.onCheckOpenGroupListener;
import com.mapbar.info.collection.widget.MExpandListView;
import com.mapbar.info.collection.widget.MExpandListView.RefreshList;

/**
 * 我的任务界面(待提交页面)
 * @author miaowei
 *
 */
@SuppressLint("ResourceAsColor")
public class MTaskPage extends BasePage implements OnClickListener, OnGroupClickListener, OnChildClickListener,
		PageListener, onCheckOpenGroupListener {

	private Context mContext;

	private ActivityInterface activityInterface;

	/**
	 * 提交采集点
	 */
	private Button btn_submit;
	/**
	 * 关闭任务
	 */
	private Button btn_close;
	/**
	 * 全部删除
	 */
	private Button btn_delete;
	private TitleBar titleBar;
	private ImageButton btn_back;

	/**
	 * 待提交
	 */
	private Button btn_wait;
	/**
	 * 已提交
	 */
	private Button btn_old;
	/**
	 * 已过期
	 */
	private Button btn_lost;
	private TextView text;

	/**
	 * 待提交ListView
	 */
	private MExpandListView expandableListView;
	/**
	 * 
	 */
	private MExpandListAdatpger mExpandListAdatpger;

	/**
	 * 待提交数据源
	 */
	private ArrayList<TaskDetail> arrayList = new ArrayList<TaskDetail>();

	/**
	 * 已过期
	 */
	private MTaskLostPage lostPage;
	/**
	 * 已提交
	 */
	private MTaskOldPage oldPage;

	/**
	 * 待提交
	 */
	private int nomral = 0;
	/**
	 * 已提交
	 */
	private int old = 1;
	/**
	 * 已过期
	 */
	private int lost = 2;
	/**
	 * 标识是否已提交模块
	 */
	private boolean isOld = false;
	private Location mLocation;
	
	private List<Integer> list_group;
	private String tid = "";
	private boolean isClose = true;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat gpsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 用于轮询校验图片是否上传成功
	 */
	Timer timer =  null;
	TimerTaskGetImageLength myTimerTask;
	
	private enum Type
	{
		updata, delete
	};

	/**
	 * 我的任务界面(待提交页面)
	 * @param context
	 * @param view
	 * @param aif
	 */
	public MTaskPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.activityInterface = aif;
		titleBar = new TitleBar(context, view, this, aif);
		lostPage = new MTaskLostPage(context, view, aif);
		oldPage = new MTaskOldPage(context, view, aif);
		timer = new Timer();
		oldPage.setTaskPage(this);
		lostPage.setTaskPage(this);
		initView(view);
	}

	private void initView(View view) {
		LogPrint.Print("MTaskPage==initView==="+this);
		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		btn_submit = (Button) view.findViewById(R.id.my_task_summit_task);
		btn_close = (Button) view.findViewById(R.id.my_task_close_task);
		btn_delete = (Button) view.findViewById(R.id.my_task_btn_delete);

		btn_wait = (Button) view.findViewById(R.id.my_task_wait);
		btn_old = (Button) view.findViewById(R.id.my_task_old);
		btn_lost = (Button) view.findViewById(R.id.my_task_lose);
		text = (TextView) view.findViewById(R.id.my_task_text);

		expandableListView = (MExpandListView) view.findViewById(R.id.my_task_list);
		mExpandListAdatpger = new MExpandListAdatpger(mContext, arrayList);
		mExpandListAdatpger.setPageListener(this);
		mExpandListAdatpger.setOnCheckOPenGroupLisrener(this);
		expandableListView.setAdapter(mExpandListAdatpger);
		expandableListView.setOnGroupClickListener(this);
		expandableListView.setOnChildClickListener(this);

		btn_wait.setOnClickListener(this);
		btn_old.setOnClickListener(this);
		btn_lost.setOnClickListener(this);

		btn_submit.setOnClickListener(this);
		btn_close.setOnClickListener(this);
		//-----二期暂时关闭提交任务,关闭任务操作
		//btn_submit.setEnabled(false);
		//btn_submit.setBackgroundColor(R.color.gray);
		//btn_submit.setTextColor(R.color.text_gray);
		//btn_close.setEnabled(false);
		//btn_close.setBackgroundColor(R.color.gray);
		//btn_close.setTextColor(R.color.text_gray);
		//-----
		btn_delete.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_wait.setEnabled(false);
		setWhichListVisible(nomral);

		expandableListView.setRefreshListener(new RefreshList() {

			@Override
			public void onRefershList() {
				setTaskDetail();
			}
		});
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {

		mExpandListAdatpger.notifyDataSetChanged();

		super.onAttachedToWindow(flag, position);
		if (flag == Configs.VIEW_POSITION_INDEX) {
			activityInterface.showNetWaitDialog("数据加载中......");
			new Thread(new reFreshData()).start();
		} else {
			setTaskDetail();
		}
		int t_size = DManager.getInstance(mContext).queryAllTask(MColums.WAIT).size();
		int p_size = DManager.getInstance(mContext).queryOldPoint(MColums.WAIT).size();
		//关闭
		text.setText("共计" + t_size + "个任务未完成，" + p_size + "个采集点未提交");
		lostPage.onAttachedToWindow(flag, position);
		oldPage.onAttachedToWindow(flag, position);

		if (!btn_old.isEnabled()) {
			setWhichListVisible(old);
		} else if (!btn_lost.isEnabled()) {
			setWhichListVisible(lost);
		}
	}

	/**
	 * 
	 * @author miaowei
	 *
	 */
	private class reFreshData implements Runnable {

		@Override
		public void run() {
			ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllTask(MColums.WAIT);
			for (int i = 0; i < details.size(); i++) {
				TaskDetail taskDetail = details.get(i);
				try {
					if (taskDetail.getLostTime() != null && !taskDetail.getLostTime().equals("")) {
						
						long seconds = dateFormat.parse(taskDetail.getLostTime()).getTime();
						long cur = System.currentTimeMillis();
						if (cur > seconds) {
							DManager.getInstance(mContext).updataTaskFlag(details.get(i).getId(), MColums.LOST);
							ArrayList<TaskPoint> points = DManager.getInstance(mContext).queryPoint(details.get(i).getId(),
									MColums.WAIT);
							for (int j = 0; j < points.size(); j++) {
								DManager.getInstance(mContext).updatePointFlag(points.get(j).getCameraId(), PF_STATE.LOST,
										System.currentTimeMillis() + "");
							}
						}
					}
					

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			handler.sendEmptyMessage(REQUEST_dATA);
		}

	}

	private int start = 0;
	private int end = 20;

	/**
	 * 从本地加载(待提交)数据
	 */
	private void setTaskDetail() {
		//从本地加载(待提交)数据
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllTaskLimit(MColums.WAIT, start, end);
		ArrayList<TaskPoint> points = null;
		if (details.size() > 0)
			start = start + 20;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.WAIT);
			if (points.size() == 0 && taskDetail.getType().equals("0")) {
				DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayList.add(taskDetail);
		}
		if (arrayList.size() == 0)
			expandableListView.setVisibility(View.GONE);
		if (details.size() > 0) {
			text.setVisibility(View.VISIBLE);
			//关闭
			text.setText("共计" + details.size() + "个任务未完成，" + points.size() + "个采集点未提交");
		}
		handler.sendEmptyMessageDelayed(7, 1000);
		mExpandListAdatpger.notifyDataSetChanged();
	}

	/**
	 * 从本地分页加载(待提交)数据
	 * @param start
	 * @param end
	 */
	private void setTaskDetail(int start, int end) {
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllTaskLimit(MColums.WAIT, start, end);
		ArrayList<TaskPoint> points = null;
		arrayList.clear();
		this.start = end;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.WAIT);
			if (points.size() == 0 && taskDetail.getType().equals("0")) {
				DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayList.add(taskDetail);
		}
		if (details.size() > 0) {
			//关闭
			text.setText("共计" + details.size() + "个任务未完成，" + points.size() + "个采集点未提交");
		}
		mExpandListAdatpger.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		goBack();

		return true;
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_MY_TASK;
	}

	@Override
	public void goBack() {
		setWhichListVisible(nomral);
		//activityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_TASK_COL_POINT, null, null);
		activityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_INDEX, null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.my_task_summit_task: //提交采集点
				isClose = false;
				hashMap = getCheckedState();
				if (hashMap.size() == 0) {
					activityInterface.dismissNetDialog();
					if (isClose) {
						update = true;
						deleteTask();
					} else {
						if (mExpandListAdatpger.getDataSize() > 0)
							Toast.makeText(mContext, "请选择要提交的点", 0).show();
						else
							Toast.makeText(mContext, "您没有任何任务", 0).show();
					}

					break;
				}
				showUploadDialog();
			break;
			case R.id.my_task_close_task:  //关闭任务
				Map<Integer, Boolean> group = mExpandListAdatpger.getGroupSelected();
				if (group.size() == 0) {
					if (mExpandListAdatpger.getDataSize() > 0)
						Toast.makeText(mContext, "请选择要完成的任务", 0).show();
					else
						Toast.makeText(mContext, "您没有任何任务", 0).show();
					break;
				}
				isClose = true;
				showUploadDialog();
			break;
			case R.id.my_task_btn_delete: //删除任务
				if (isOld) {
					activityInterface.showNetWaitDialog("删除中，请稍后......");
					oldPage.startDThread();
				} else {
					activityInterface.showNetWaitDialog("删除中，请稍后......");
					lostPage.startDThread();
				}
			break;
			case R.id.btn_home:
				goBack();
			break;
			case R.id.my_task_wait://待提交
				text.setVisibility(View.VISIBLE);
				setWhichListVisible(nomral);
				int t_size = DManager.getInstance(mContext).queryAllTask(MColums.WAIT).size();
				int p_size = DManager.getInstance(mContext).queryOldPoint(MColums.WAIT).size();
				//关闭
				text.setText("共计" + t_size + "个任务未完成，" + p_size + "个采集点未提交");
			break;
			case R.id.my_task_lose: //已过期
				text.setVisibility(View.VISIBLE);
				setWhichListVisible(lost);
			break;
			case R.id.my_task_old: //已提交
				setWhichListVisible(old);
				//去除已提交的提示信息(总计提交XXX个任务，XXX个采集点。本地占用XXXMB)
				text.setVisibility(View.INVISIBLE);
			break;

			default:
			break;
		}

	}

	@Override
	public void onDetachedFromWindow(int flag) {
		arrayList.clear();
		start = 0;
		end = 20;
		oldPage.onDetachedFromWindow(flag);
		lostPage.onDetachedFromWindow(flag);
		super.onDetachedFromWindow(flag);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mExpandListAdatpger.destory();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		TaskPoint taskPoint = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		TaskDetail detail = mExpandListAdatpger.getTaskDetail(groupPosition);
		activityInterface.dataBridge(taskPoint);
		activityInterface.setData(detail);
		activityInterface.showPage(Configs.VIEW_POSITION_MY_TASK_CHILD, this, Configs.VIEW_POSITION_DETAIL,
				Configs.VIEW_POSITION_DETAIL_TASK, null, null);

		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

		return false;
	}

	/**
	 * 切换显示界面
	 * @param flag(0-待提交 1-已提交 2-已过期)
	 */
	private void setWhichListVisible(int flag) {
		switch (flag)
		{
			case 0:
				expandableListView.setVisibility(View.VISIBLE);
				lostPage.setListVisible(View.GONE);
				oldPage.setListVisible(View.GONE);
				
				btn_delete.setVisibility(View.GONE);
				btn_close.setVisibility(View.VISIBLE);
				btn_submit.setVisibility(View.VISIBLE);
				btn_wait.setEnabled(false);
				btn_old.setEnabled(true);

				btn_lost.setEnabled(true);
				btn_wait.setTextColor(mContext.getResources().getColor(R.color.white));
				btn_old.setTextColor(mContext.getResources().getColor(R.color.text_gray));
				btn_lost.setTextColor(mContext.getResources().getColor(R.color.text_gray));
			break;
			case 1:
				expandableListView.setVisibility(View.GONE);
				lostPage.setListVisible(View.GONE);
				oldPage.setListVisible(View.VISIBLE);
				//btn_delete.setVisibility(View.VISIBLE);
				btn_close.setVisibility(View.GONE);
				btn_submit.setVisibility(View.GONE);
				isOld = true;
				btn_wait.setEnabled(true);
				btn_old.setEnabled(false);
				btn_lost.setEnabled(true);
				btn_wait.setTextColor(mContext.getResources().getColor(R.color.text_gray));
				btn_old.setTextColor(mContext.getResources().getColor(R.color.white));
				btn_lost.setTextColor(mContext.getResources().getColor(R.color.text_gray));
			break;
			case 2:
				expandableListView.setVisibility(View.GONE);
				lostPage.setListVisible(View.VISIBLE);
				oldPage.setListVisible(View.GONE);
				isOld = false;
				//btn_delete.setVisibility(View.VISIBLE);
				btn_close.setVisibility(View.GONE);
				btn_submit.setVisibility(View.GONE);
				btn_wait.setEnabled(true);
				btn_old.setEnabled(true);
				btn_lost.setEnabled(false);
				btn_wait.setTextColor(mContext.getResources().getColor(R.color.text_gray));
				btn_old.setTextColor(mContext.getResources().getColor(R.color.text_gray));
				btn_lost.setTextColor(mContext.getResources().getColor(R.color.white));
			break;

			default:
			break;
		}
	}

	@Override
	public void showOtherPage(int group) {
		TaskDetail taskDetail = mExpandListAdatpger.getTaskDetail(group);
		activityInterface.setData(taskDetail);
		activityInterface.showPage(Configs.VIEW_POSITION_MY_TASK, this, Configs.VIEW_POSITION_CAMERA,
				Configs.VIEW_POSITION_MY_TASK_P, null, null);
	}

	/**
	 * 
	 * 获取已选项点
	 * @param type
	 */
	protected HashMap<Integer, LinkedList<Integer>> getCheckedState() {
		HashMap<Integer, LinkedList<Integer>> map = new HashMap<Integer, LinkedList<Integer>>();
		//已选择子节点
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

	/**
	 * 关闭任务
	 */
	protected void deleteTask() {
		activityInterface.showNetWaitDialog("数据关闭中......");
		Map<Integer, Boolean> group = mExpandListAdatpger.getGroupSelected();
		if (group.size() > 0) {
			Set<Integer> set = group.keySet();
			Iterator<Integer> iterator = set.iterator();
			list_group = new LinkedList<Integer>();
			while (iterator.hasNext()) {
				int groupPosition = iterator.next();
				list_group.add(groupPosition);
			}
			delete();
		} else {
			Toast.makeText(mContext, "请选择要完成的任务", 0).show();
		}
	}

	private void delete() {
		if (list_group.size() > 0) {
			int groupPosition = list_group.get(0);
			TaskDetail taskDetail = mExpandListAdatpger.getTaskDetail(groupPosition);
			//如果任务类型不是随手拍
			if (!taskDetail.getType().equals("2")) {
				
				closeTask(taskDetail);
			}else {
				Toast.makeText(mContext, "随手拍任务不用关闭", Toast.LENGTH_SHORT).show();
				activityInterface.dismissNetDialog();
			}
			
		}
	}

	/**
	 * 任务关闭成功
	 */
	private final int CLOSE_AGAIN = 0;
	/**
	 * 任务关闭失败
	 */
	private final int CLOSE_FAIL = 1;
	private final int CLOSE_NET_FAIL = 2;
	/**
	 * 标识信息上传成功
	 */
	private final int UPLOAD_SUCCESS = 3;
	private final int UPLOAD_FAIL = 4;
	private final int REQUEST_dATA = 5;
	private final int UPLOAD_NEXT = 6;
	private final int TIMEOUT = 8;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case CLOSE_AGAIN:

					int positon = list_group.get(0);

					TaskDetail taskDetail = mExpandListAdatpger.getTaskDetail(positon);
					DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
					mExpandListAdatpger.getGroupSelected().remove(positon);
					mExpandListAdatpger.getChildGroupSelected().remove(positon);
					new Thread(new deleteCloseTask(taskDetail)).start();
					list_group.remove(0);
					if (list_group.size() > 0) {
						delete();
					} else {
						mExpandListAdatpger.notifyDataSetChanged();
						activityInterface.dismissNetDialog();
						mExpandListAdatpger.reStore();
						//关闭
						Toast.makeText(mContext, "任务已完成成功", 0).show();
						arrayList.clear();
						setTaskDetail(0, start + end);
						mExpandListAdatpger.notifyDataSetChanged();
					}

				break;
				case CLOSE_FAIL://任务关闭失败
					String code = (String) msg.obj;
					activityInterface.ShowCodeDialog(Integer.valueOf(code),MTaskPage.this);
					mExpandListAdatpger.reStore();
					arrayList.clear();
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
					//mExpandListAdatpger.notifyDataSetChanged();
					//关闭
					Toast.makeText(mContext, "任务完成失败", 0).show();
					activityInterface.dismissNetDialog();
				break;
				case CLOSE_NET_FAIL:
					mExpandListAdatpger.notifyDataSetChanged();
					//关闭
					Toast.makeText(mContext, "任务完成失败", 0).show();
					activityInterface.dismissNetDialog();
					arrayList.clear();
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
				break;
				case UPLOAD_SUCCESS: //采集点信息提交成功
					
					setUploadImage();
					
				break;
				case MessageID.UPLOAD_SUCCESS_IMAGE:
					
					timerAndTimerTaskCancel();
					
					mExpandListAdatpger.notifyDataSetChanged();

					if (chlid_list.size() > 0) {
						
						chlid_list.remove(0);
						
					}
					if (chlid_list.size() > 0) {

						TaskPoint point = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
						point.setFlag(MColums.OLD);

						DManager.getInstance(mContext).updatePointFlag(point.getCameraId(), PF_STATE.OLD,
								System.currentTimeMillis() + "");
					} else {
		                if (list_group_position.size() > 0) {
		                	
							TaskDetail taskDetail2 = mExpandListAdatpger.getTaskDetail(groupPosition);
							taskDetail2.setFalg(MColums.OLD);
							DManager.getInstance(mContext).addOldTask(taskDetail2);
							//关闭任务
							if (isClose) {
								TaskDetail task = mExpandListAdatpger.getTaskDetail(list_group_position.get(0));
								closeTask(task);
							}
							list_group_position.remove(0);
						}

						if (list_group_position.size() > 0) {
							deletePoint();
							setUpload();
							break;
						} else {
							Toast.makeText(mContext, "任务提交完毕", 0).show();
							LogPrint.Print("fileupload","任务提交完毕==================");
							deletePoint();
							arrayList.clear();
							activityInterface.dismissNetDialog();
							mExpandListAdatpger.reStore();
							//setTaskDetail(0, start + end);
							handler.sendEmptyMessage(MessageID.UPLOAD_TASK_SUCCESS);
							
							break;
						}
					}

					deletePoint();
					setUpload2();
				break;
				case UPLOAD_FAIL:
					arrayList.clear();
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "任务信息提交失败", Toast.LENGTH_SHORT).show();
					mExpandListAdatpger.reStore();
					activityInterface.ShowCodeDialog(msg.arg1, MTaskPage.this);
				break;
				case REQUEST_dATA:
					setTaskDetail();
					activityInterface.dismissNetDialog();
				break;
				case UPLOAD_NEXT:
					mExpandListAdatpger.notifyDataSetChanged();

					if (chlid_list.size() > 0){
						chlid_list.remove(0);
					}
					if (chlid_list.size() > 0) {

						// TaskPoint point =
						// mExpandListAdatpger.getTaskPoint(groupPosition,
						// childPosition);
						// point.setFlag(MColums.OLD);

						// DManager.getInstance(mContext).updatePointFlag(point.getCameraId(),
						// PF_STATE.OLD,
						// System.currentTimeMillis() + "");
					} else {
						if (list_group_position.size() > 0){
							list_group_position.remove(0);
							if (isClose) {
								TaskDetail task = mExpandListAdatpger.getTaskDetail(list_group_position.get(0));
								closeTask(task);
							}
						}
							
						if (list_group_position.size() > 0) {
							// deletePoint();
							setUpload();
							break;
						} else {
							Toast.makeText(mContext, "信息不全的点无法提交", 0).show();
							// deletePoint();
							arrayList.clear();
							setTaskDetail(0, start + end);
							activityInterface.dismissNetDialog();
							mExpandListAdatpger.notifyDataSetChanged();
							mExpandListAdatpger.reStore();
							break;
						}
					}
					// deletePoint();
					setUpload2();
				break;
				case 7:
					expandableListView.closeFooter();
					activityInterface.dismissNetDialog();
				break;
				case TIMEOUT:
					Toast.makeText(mContext, "网络不给力，请检查网络", Toast.LENGTH_SHORT).show();
					activityInterface.dismissNetDialog();
				break;
				case MessageID.UPLOAD_FAIL_IMAGE:
					Toast.makeText(mContext, "图片提交失败", Toast.LENGTH_SHORT).show();
					activityInterface.dismissNetDialog();
					break;
				case MessageID.UPLOAD_FAIL_NOIMAGE:
					arrayList.clear();
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "任务信息提交失败，检测到没有图片", Toast.LENGTH_SHORT).show();
					mExpandListAdatpger.reStore();
					break;
				case MessageID.UPLOAD_SUCCESS_IMAGE_PATH:
					String imagePath = msg.obj.toString();
					if (timer != null) {
						
						if (myTimerTask != null){
							
							myTimerTask.cancel();  //将原任务从队列中移除
							
						  }
					}else {
						
						timer = new Timer();
					}
					
					myTimerTask = new TimerTaskGetImageLength(imagePath, mContext, handler);
					//轮询校验图片是否上传成功
					timer.schedule(myTimerTask,3*1000);
				     break;
				case MessageID.UPLOAD_TASK_SUCCESS:
					//更新数据
					setTaskDetail(0, start + end);
					break;
				default:
				break;
			}
		};
	};

	private MHttpHandler httpHandler;

	private class closeThread implements Runnable {

		@Override
		public void run() {

			deleteTask();
		}

	}

	/**
	 * 关闭任务
	 * @param task
	 */
	private void closeTask(final TaskDetail task) {
		String url = UrlConfig.closeTaskUrl + "token="+StringUtils.getLoginToken(mContext) + "&loginId=" + StringUtils.getLoginId(mContext) + "&taskId=" + task.getId();
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {

					try {
						JSONObject jsonObject = new JSONObject(new String(data));
						boolean b = jsonObject.getBoolean("result");
						if (b) {
							if (isClose && update)
								handler.sendEmptyMessage(CLOSE_AGAIN);
						} else {
							String code = jsonObject.getString("message");
							Message message = handler.obtainMessage();
							message.what = CLOSE_FAIL;
							message.obj = code;
							handler.sendMessage(message);
						}

					} catch (JSONException e) {
						handler.sendEmptyMessage(CLOSE_NET_FAIL);
						e.printStackTrace();
					}

				} else {
					if (arg0 != 200)
						handler.sendEmptyMessage(TIMEOUT);
				}
			}
		});
		httpHandler.execute();
	}

	/**
	 * 已选择的父节点
	 */
	private LinkedList<Integer> list_group_position;
	/**
	 * 已选择的父子节点项
	 */
	private HashMap<Integer, LinkedList<Integer>> hashMap;
	private LinkedList<Integer> chlid_list;
	private int groupPosition;
	private int childPosition;
	private boolean update = false;

	/**
	 * 开始提交采集点
	 */
	private void initUpload() {
		hashMap = getCheckedState();
		if (hashMap.size() == 0) {
			activityInterface.dismissNetDialog();
			if (isClose) {
				update = true;
				//如果是随手拍任务不进行删除type==2
				deleteTask();
			} else {
				if (mExpandListAdatpger.getDataSize() > 0)
					Toast.makeText(mContext, "请选择要提交的点", 0).show();
				else
					Toast.makeText(mContext, "您没有任何任务", 0).show();
			}

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
		update = false;
		if (list_group_position.size() > 0) {
			groupPosition = list_group_position.get(0);
			
			chlid_list = hashMap.get(groupPosition);
			
			setUpload2();
		}
	}

	private void setUpload2() {
		if (chlid_list.size() == 0) {
			activityInterface.dismissNetDialog();
			Toast.makeText(mContext, "请选择要提交的点", 0).show();
			return;
		}
		childPosition = chlid_list.get(0);
		TaskPoint point = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		if (point.getIsFull() == 2) {
			handler.sendEmptyMessage(UPLOAD_NEXT);
			return;
		}
		//开启线程上传任务采集点信息
		new Thread(new dThread(point)).start();
	}

	/**
	 * 上传采集点图片
	 */
	private void setUploadImage(){
		
		if (chlid_list.size() == 0) {
			activityInterface.dismissNetDialog();
			Toast.makeText(mContext, "请选择要提交的点", 0).show();
			return;
		}
		childPosition = chlid_list.get(0);
		TaskPoint point = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		if (point.getIsFull() == 2) {
			handler.sendEmptyMessage(UPLOAD_NEXT);
			return;
		}
		//开启线程上传任务采集点图片
		new Thread(new dThreadTaskImage(point)).start();
	}
	/**
	 * 更新采集点为已提交
	 */
	private void deletePoint() {
		TaskPoint point = mExpandListAdatpger.getTaskPoint(groupPosition, childPosition);
		DManager.getInstance(mContext).updatePointFlag(point.getCameraId(), PF_STATE.OLD,
				System.currentTimeMillis() + "");
	}

	/**
	 * 单独提交采集点信息/关闭任务(关闭任务之前需要先提交采集点，提交采集点成功后再关闭任务)
	 * @param point
	 */
	private void upLoadFile(TaskPoint point) {
		Message msg = handler.obtainMessage();
		//判断网络是否有效
		if (Util.isNetworkAvailable(mContext)) {
			if (point == null) {
				activityInterface.dismissNetDialog();
				Toast.makeText(mContext, "提交失败", 0).show();
				return;
			}else {
				
				FileUtil fileUtil = new FileUtil(mContext);
				Map<String, String> map = new HashMap<String, String>();
				map.put("taskId", point.getTid());
				map.put("loginId", StringUtils.getLoginId(mContext));
				map.put("id", point.getCameraId());
				map.put("name", point.getName());
				//如果电子眼类为空，则默认为26
				map.put("type", !StringUtils.isNullColums(point.getType())?point.getType():"26");
				map.put("speed", point.getLspeed());
				map.put("angle", point.getAngle());
				map.put("direction", point.getOri());
				map.put("description", point.getContent());
				//单号
				map.put("order", point.getOrder());
				//图片名称
				map.put("iname", point.getIname());

				//经度
				map.put("longitude",String.valueOf(point.getLon()));
				//纬度
				map.put("latitude", String.valueOf(point.getLat()));
				//GPS速度
				map.put("gpsSpeed", String.valueOf(point.getLspeed()));
				//GPS角度
				map.put("gpsAngle", String.valueOf(point.getAngle()));
				if (mLocation != null) {
					
					//gpsTime
					map.put("gpsTime", String.valueOf(gpsDateFormat.format(mLocation.getTime())));
					//GPS海拔
					map.put("gpsHeight", String.valueOf(mLocation.getAltitude()));
				}else {
					//gpsTime
					map.put("gpsTime", String.valueOf(gpsDateFormat.format(System.currentTimeMillis())));
					//GPS海拔
					map.put("gpsHeight", String.valueOf(0));
				}
				//任务 ID+图片名称 的图片路径
				String str = point.getTid() + "/" + point.getIname();
				FormFile[] files = BitmapUtils.getFileList(str);
				if (files != null && files.length > 0) {
					
					map.put("fileUpload", String.valueOf(files.length));
					//判断如果有信息上传成功，但是图片没上传成功的任务
					String pointStatus = DManager.getInstance(mContext).queryPointStatus(DataBaseHelper.TABNAME_POINT, point.getCameraId());
					if (pointStatus != null && pointStatus.equals("old")) {
						
						//开启线程上传任务采集点图片
						new Thread(new dThreadTaskImage(point)).start();
						
					}else {
						
						String resultString = fileUtil.postFileCompress(UrlConfig.uploadUrl+"token="+StringUtils.getLoginToken(mContext)+"&", map,null);
						if (!StringUtils.isNullColums(resultString)) {
							
							try {
								
								JSONObject jsonObject = new  JSONObject(resultString);
								boolean result = jsonObject.getBoolean("result");
								LogPrint.Print("postmessage=="+jsonObject.getString("message"));
								String messageString = jsonObject.getString("message");
								if (result) {
									msg.what = UPLOAD_SUCCESS;
									handler.sendMessage(msg);
									
								}else {
									int code = Integer.valueOf(messageString);
									msg.arg1 = code;
									msg.what = UPLOAD_FAIL;
									handler.sendMessage(msg);
								}
								
							} catch (Exception e) {
								e.printStackTrace();
								msg.what = UPLOAD_FAIL;
								handler.sendMessage(msg);
							}
						}else {
							msg.what = UPLOAD_FAIL;
							handler.sendMessage(msg);
						}
					}
					
				}else{
					msg.what = MessageID.UPLOAD_FAIL_NOIMAGE;
					handler.sendMessage(msg);
					
				}
				
			}
			
		}else {
			
			handler.sendEmptyMessage(TIMEOUT);
		}
		
		
	}
	
	/**
	 * 单独提交任务图片
	 * @param point
	 */
	private void upLoadFileImage(TaskPoint point) {
		//判断网络是否有效
		if (Util.isNetworkAvailable(mContext)) {
			Message message = handler.obtainMessage();
			if (point == null) {
				//activityInterface.dismissNetDialog();
				Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
				return;
			}else {
				FileUtil fileUtil = new FileUtil(mContext);
				Map<String, String> map = new HashMap<String, String>();
				map.put("taskId", point.getTid());
				map.put("loginId", StringUtils.getLoginId(mContext));
				map.put("id", point.getCameraId());
				map.put("name", point.getName());
				//如果电子眼类为空，则默认为26
				map.put("type", !StringUtils.isNullColums(point.getType())?point.getType():"26");
				map.put("speed", point.getLspeed());
				map.put("angle", point.getAngle());
				map.put("direction", point.getOri());
				map.put("description", point.getContent());
				//单号
				map.put("order", point.getOrder());
				//图片名称
				map.put("iname", point.getIname());
				map.put("longitude",String.valueOf(point.getLon()));
				map.put("latitude", String.valueOf(point.getLat()));
				
				//任务 ID+图片名称 的图片路径
				String str = point.getTid() + "/" + point.getIname();
				String resultString = fileUtil.postFileCompress(UrlConfig.SAVEIMAGE_URL+"token="+StringUtils.getLoginToken(mContext)+"&", map, BitmapUtils.getFileList(str));
				if (!StringUtils.isNullColums(resultString)) {
					
					try {
						
						JSONObject jsonObject = new  JSONObject(resultString);
						boolean result = jsonObject.getBoolean("result");
						LogPrint.Print("fileupload","postmessage=="+jsonObject.getString("message"));
						if (result) {
							//请求校验图片是否上传成功,因为在上传图片时，后台返回的都是成功，即使失败也会返回成功
							//校验返回头部信息的长度大小Content-Length
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							String imagePath = (String)dataJson.getString("url-1");
							message.obj = imagePath;
							message.what = MessageID.UPLOAD_SUCCESS_IMAGE_PATH;
							handler.sendMessage(message);
							
							
						}else {
							
							handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
						}
						
					} catch (Exception e) {
						
						e.printStackTrace();
						handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
					}
				}else {
					
					handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
				}
			}
			
		}else {
			
			handler.sendEmptyMessage(TIMEOUT);
		}
		
		
	}

	/**
	 * 获取任务图片的路径
	 * 
	 * @param name
	 * @return
	 */
	private ArrayList<String> getPhotoById(String name) {
		ArrayList<String> pathList = new ArrayList<String>();

		File file = new File(SdcardUtil.getSdcardCollInfo() + name);
		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			return null;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			String path = tempList[i];
			pathList.add(path);
		}
		return pathList;

	}

	/* 获得指定路径下所有文件 */
	public FormFile[] getFileList(TaskPoint point) {
		ArrayList<String> pathList = getPhotoById(point.getTid());
		if (pathList != null && pathList.size() > 0) {
			ArrayList<FormFile> formList = new ArrayList<FormFile>();

			for (int i = 0; i < pathList.size(); i++) {
				String path = SdcardUtil.getSdcardCollInfo() + point.getTid() + "/" + pathList.get(i);
				File file = new File(path);
				if (file.exists()) {
					FormFile formFile = new FormFile(file.getName(), file, null, null);
					formList.add(formFile);
				} else {
					return null;
				}
			}
			FormFile[] formFiles = new FormFile[formList.size()];
			for (int i = 0; i < formList.size(); i++) {
				formFiles[i] = formList.get(i);
			}
			return formFiles;
		}
		return null;
	}

	/**
	 * 提交采集点信息
	 * @author miaowei
	 *
	 */
	private class dThread implements Runnable {
		private TaskPoint taskPoint;

		private dThread(TaskPoint point) {
			taskPoint = point;
		}

		@Override
		public void run() {
			upLoadFile(taskPoint);
		}
	}
	/**
	 * 提交采集点图片
	 * @author miaowei
	 *
	 */
	private class dThreadTaskImage implements Runnable {
		private TaskPoint taskPoint;

		private dThreadTaskImage(TaskPoint point) {
			taskPoint = point;
		}

		@Override
		public void run() {
			upLoadFileImage(taskPoint);
		}
	}

	private class deleteCloseTask implements Runnable {
		private TaskDetail taskDetail;

		public deleteCloseTask(TaskDetail detail) {
			this.taskDetail = detail;
		}

		@Override
		public void run() {
			List<TaskPoint> listPoint = taskDetail.getList();
			String path = SdcardUtil.getSdcardCollInfo() + taskDetail.getId();
			for (int j = 0; j < listPoint.size(); j++) {
				path = path + "/" + listPoint.get(j).getIname();
				deletePhoto(path);
			}
			deletePhoto(SdcardUtil.getSdcardCollInfo() + taskDetail.getId());
		}

	}

	private class deleteLostTask implements Runnable {

		@Override
		public void run() {

			ArrayList<TaskDetail> taskDetails = DManager.getInstance(mContext).queryAllTask(MColums.LOST);
			ArrayList<TaskPoint> listPoint = DManager.getInstance(mContext).queryOldPoint(MColums.LOST);
			for (int i = 0; i < taskDetails.size(); i++) {
				String path = SdcardUtil.getSdcardCollInfo() + taskDetails.get(i).getId();
				for (int j = 0; j < listPoint.size(); j++) {
					path = path + "/" + listPoint.get(j).getIname();
					deletePhoto(path);
				}
				DManager.getInstance(mContext).deleteTaskDetail(taskDetails.get(i).getId());
				deletePhoto(SdcardUtil.getSdcardCollInfo() + taskDetails.get(i).getId());
			}
			DManager.getInstance(mContext).deletePointByFlag(MColums.LOST);
			activityInterface.dismissNetDialog();

		}

	}

	/**
	 * 删除文件
	 * @param path
	 */
	public void deletePhoto(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		String[] list = file.list();
		for (int i = 0; i < list.length; i++) {
			String str = path + "/" + list[i];
			File files = new File(str);
			if (!files.exists()) {
				return;
			}
			boolean bb = files.delete();
		}
		file.delete();
	}

	@Override
	public void onCheckOpenGroup(int group) {
		expandableListView.expandGroup(group);

	}

	private AlertDialog dialog;

	/**
	 * 上传服务器等待对话框
	 */
	private void showUploadDialog() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext).create();
		} else if (dialog.isShowing()) {
			return;
		}

		dialog.show();
		dialog.setContentView(R.layout.layout_dialog);
		dialog.setCancelable(false);
		TextView textView = (TextView) dialog.findViewById(R.id.text_dialog);
		textView.setText(mContext.getResources().getString(R.string.dialog_upload));

		dialog.findViewById(R.id.continue_confirm).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				activityInterface.showNetWaitDialog("数据上传中，请稍后......");
				dialog.cancel();
				initUpload();
			}
		});

		dialog.findViewById(R.id.continue_cancle).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});

	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		LogPrint.Print("gps", "MTaskPage--onLocationChanged---location="+location.getProvider());
		if (location.getProvider().equals(Configs.LOCATIONTYPE)) {
			CustomCameraActivity.mlocation = location;
			mLocation = location;
		}else {
			
			mLocation = null;
		}
		
	}

	/**
	 * 取消定时器任务
	 */
	private void timerAndTimerTaskCancel() {
		if (timer != null) {
			
			timer = null;
		}
	}

}

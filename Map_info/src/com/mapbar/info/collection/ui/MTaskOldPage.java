package com.mapbar.info.collection.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.PageListener;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.FileSizeUtil;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.widget.MExpandListAdatpger;
import com.mapbar.info.collection.widget.MExpandListAdatpger.onCheckOpenGroupListener;
import com.mapbar.info.collection.widget.MExpandListView;
import com.mapbar.info.collection.widget.MExpandListView.RefreshList;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
/**
 * 我的任务(已提交页面)
 * @author miaowei
 *
 */
public class MTaskOldPage extends BasePage implements OnGroupClickListener, OnChildClickListener, PageListener,
		onCheckOpenGroupListener {

	/**
	 * 已提交ListView
	 */
	private MExpandListView expandableListView;
	private MExpandListAdatpger mExpandListAdatpger;

	/**
	 * 已提交数据源
	 */
	private ArrayList<TaskDetail> arrayListOld = new ArrayList<TaskDetail>();
	private MTaskPage mTaskPage;
	private ActivityInterface activityInterface;
	private Context mContext;

	private LinkedList<Integer> list_group_position;
	private HashMap<Integer, LinkedList<Integer>> hashMap;
	private LinkedList<Integer> chlid_list;
	private int groupPosition;
	private int childPosition;
	private DecimalFormat df = new DecimalFormat("######0.00");

	private TextView text;
	private double size = 0;
	private int vis = -1;
	/**
	 * 我的任务(已提交)
	 * @param context
	 * @param view
	 * @param aif
	 */
	public MTaskOldPage(Context context, View view, ActivityInterface aif) {
		
		LogPrint.Print("MTaskOldPage==initView==="+this);
		mContext = context;
		activityInterface = aif;
		expandableListView = (MExpandListView) view.findViewById(R.id.my_task_list_old);
		mExpandListAdatpger = new MExpandListAdatpger(context, arrayListOld);
		mExpandListAdatpger.setOnCheckOPenGroupLisrener(this);
		mExpandListAdatpger.setPageListener(this);
		expandableListView.setAdapter(mExpandListAdatpger);
		expandableListView.setOnGroupClickListener(this);
		expandableListView.setOnChildClickListener(this);
		text = (TextView) view.findViewById(R.id.my_task_text);

		expandableListView.setRefreshListener(new RefreshList() {

			@Override
			public void onRefershList() {
				if (arrayListOld.size() >= 20) {
					
					getCimmitDataFromSerlver(true);
				}
				
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 加载(已提交)数据
	 * @param visible
	 */
	public void setListVisible(int visible) {
		vis = visible;
		expandableListView.setVisibility(visible);
		if (visible == View.VISIBLE) {
			getCimmitDataFromSerlver(false);
			//setTotal();
		} else {
			start = 0;
			end = 20;
			mExpandListAdatpger.notifyDataSetChanged();
			arrayListOld.clear();
		}
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		if (vis == View.VISIBLE) {
			setTotal();
		}
		super.onAttachedToWindow(flag, position);
	}

	/**
	 * 本地数据库中统计已提交任务的数量
	 */
	private void setTotal() {
		size = 0;
		//数据中获取总计提交数
		ArrayList<TaskDetail> t_list = DManager.getInstance(mContext).queryAllOldTask(MColums.OLD);
		int p_size = DManager.getInstance(mContext).queryOldPoint(MColums.OLD).size();

		for (int i = 0; i < t_list.size(); i++) {
			ArrayList<TaskPoint> p_list = DManager.getInstance(mContext).queryPoint(t_list.get(i).getId(), MColums.OLD);
			for (int j = 0; j < p_list.size(); j++) {
				Double double1 = FileSizeUtil.getFileOrFilesSize(SdcardUtil.getSdcardCollInfo() + t_list.get(i).getId()
						+ "/" + p_list.get(j).getIname(), FileSizeUtil.SIZETYPE_MB);
				size = size + double1;
			}
		}
		setTaskSize(t_list.size(), p_size, df.format(size));
		
	}

	private MHttpHandler httpHandler;
	private StringBuilder builder;

	/**
	 * 网络获取已提交任务
	 * @param bl
	 *            false 是刷新 不删除集合
	 */
	private void getCimmitDataFromSerlver(boolean bl) {
		activityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		if (builder == null)
			builder = new StringBuilder();
		else
			builder.delete(0, builder.length());

		TaskDetail taskDe = null;

		if (!bl && arrayListOld.size() > 0) {
			arrayListOld.clear();
			mExpandListAdatpger.notifyDataSetChanged();
		}

		int satrt = arrayListOld.size();
		int end = 20;
		if (arrayListOld.size() > 0) {
			taskDe = arrayListOld.get(arrayListOld.size() - 1);
		}

		builder.append(UrlConfig.getMyTaskCimmit);
		builder.append("token="+StringUtils.getLoginToken(mContext));
		builder.append("&loginId=" + StringUtils.getLoginId(mContext));
		builder.append("&start=" + satrt);
		builder.append("&max=" + end);

		if (taskDe != null) {
			builder.append("&currentTime=" + taskDe.getCurrentTime());
		}
		LogPrint.Print("getMyTaskCimmit==="+builder.toString());
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(builder.toString(), HttpRequestType.GET);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@SuppressWarnings("unused")
			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						String jsonStr = new String(data, "UTF-8");
						JSONObject jsonObject = new JSONObject(jsonStr);

						boolean b = jsonObject.getBoolean("result");
						if (b) {
							String messageString = jsonObject.getString("message");
							if (!StringUtils.isNullColums(messageString)) {
									
									ArrayList<TaskDetail> waitTaskList = new ArrayList<TaskDetail>();
									JSONObject dataJson = (JSONObject) jsonObject.get("data");
									JSONArray jsonRowsArray = (JSONArray) dataJson.get("rows");
									if (jsonRowsArray.length() > 0) {
										
										String curTime = "0";
										if (jsonObject.getString("id") != null && !jsonObject.getString("id").equals("") ) {
											
											curTime = jsonObject.getString("id");
										}
										TaskDetail task;
										List<TaskPoint> list;
										TaskPoint taskPoint;
										for (int i = 0; i < jsonRowsArray.length(); i++) {
											task = new TaskDetail();
											list = new ArrayList<TaskPoint>();
											//JSONObject json = dataJson.getJSONObject(i);
											JSONObject jsonTask = jsonRowsArray.getJSONObject(i);
											task.setStartTime(StringUtils.isNullColums(jsonTask.getString("createTime"))?"":jsonTask.getString("createTime"));
											task.setLostTime(StringUtils.isNullColums(jsonTask.getString("exceedTime"))?"":jsonTask.getString("exceedTime"));
											task.setType(jsonTask.getInt("taskType") + "");
											task.setCloseTime(StringUtils.isNullColums(jsonTask.getString("clostTime"))?"":jsonTask.getString("clostTime"));
											task.setDescription(StringUtils.isNullColums(jsonTask.getString("description"))?"":jsonTask.getString("description"));
											task.setExecutorTime(StringUtils.isNullColums(jsonTask.getString("executorTime"))?"":jsonTask.getString("executorTime"));
											task.setId(jsonTask.getString("id"));
											task.setUpdateTime(StringUtils.isNullColums(jsonTask.getString("lastUpdateTime"))?"":jsonTask.getString("lastUpdateTime"));
											task.setName(jsonTask.getString("name"));
											if (jsonTask.getInt("taskType") == 2) {
												
												task.setRice(0.5);
											}else {
												
												task.setRice(StringUtils.isNullColums(jsonTask.getDouble("price")+"") ? Double.parseDouble("0.0"):jsonTask.getDouble("price"));
											}
											//task.setRice(StringUtils.isNullColums(jsonTask.getDouble("price")+"") ? Double.parseDouble("0.0"):jsonTask.getDouble("price"));
											task.setRoadcameraCount(jsonTask.getInt("roadcameraCount"));
											//task.setVerifyInfo(jsonTask.getString("verifyInfo"));
											task.setCurrentTime(curTime);
											//2014-11-7添加任务状态
											//task.setStatus(jsonTask.getString("status"));
											JSONArray jsonArray = jsonRowsArray.getJSONObject(i).getJSONArray("roadcameraInfo");
											for (int j = 0; j < jsonArray.length(); j++) {
												taskPoint = new TaskPoint();
												JSONObject object = jsonArray.getJSONObject(j);
												//taskPoint.setAngle(StringUtils.isNullColums(object.getString("angle"))?"":object.getString("angle"));
												taskPoint.setContent(object.getString("description"));
												taskPoint.setOri(object.getString("direction"));
												taskPoint.setCameraId(object.getString("id"));
												//taskPoint.setLastTime(StringUtils.isNullColums(object.getString("lastTime"))?"":object.getString("lastTime"));
												taskPoint.setLat(object.getDouble("latitude") + "");
												taskPoint.setLon(object.getDouble("longitude") + "");
												
												//电子眼名称
												taskPoint.setName(StringUtils.isNullColums(object.getString("name"))?"":object.getString("name"));
												//电子眼图片名称
												taskPoint.setIname(StringUtils.isNullColums(object.getString("iname"))?"":object.getString("iname"));
												//电子眼单号
												taskPoint.setOrder(StringUtils.isNullColums(object.getString("order"))?"":object.getString("order"));
												
												//taskPoint.setOrder(object.getString("odd"));
												taskPoint.setPhotoUrl(object.getString("photoUrl"));
												//taskPoint.setRefectReason(object.getString("refectReason"));
												taskPoint.setLspeed(object.getString("speed"));
												taskPoint.setStatus(object.getString("status"));
												taskPoint.setCameraTypeText(object.getString("type"));
												taskPoint.setTid(task.getId());
												list.add(taskPoint);
											}
											task.setList(list);
											waitTaskList.add(task);
										}
										Message message = handler.obtainMessage();
										message.what = success;
										message.obj = waitTaskList;
										handler.sendMessage(message);
									}else {
										
										Message message = handler.obtainMessage();
										message.what = fail;
										message.obj = jsonObject.getString("message");
										handler.sendMessage(message);
									}
							}
							
						} else {
							Message message = handler.obtainMessage();
							message.what = fail;
							message.obj = jsonObject.getString("message");
							handler.sendMessage(message);
						}

					} catch (Exception e) {
						handler.sendEmptyMessage(netWrong);
						e.printStackTrace();
					}

				} else {
					if (arg0 != 200)
						handler.sendEmptyMessage(netWrong);
					else
						handler.sendEmptyMessage(TIMEOUT);
				}
			}
		});
		httpHandler.execute();
	}

	private int start = 0;
	private int end = 20;

	/**
	 * 从数据库中获取要显示的数据
	 */
	private void setTaskDetail() {
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllOldTaskByLimit(start, end);
		ArrayList<TaskPoint> points = null;
		if (details.size() > 0)
			start = start + 20;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.OLD);
			if (points.size() == 0) {
				DManager.getInstance(mContext).deleteOldTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayListOld.add(taskDetail);
		}
		if (details.size() > 0) {
			
			setTaskSize(details.size(), points.size(),String.valueOf(0));
		}
		if (arrayListOld.size() == 0)
			expandableListView.setVisibility(View.GONE);
		handler.sendEmptyMessageDelayed(wait_pr0bar, 1000);
		mExpandListAdatpger.notifyDataSetChanged();
	}

	/**
	 * 
	 * 从数据库中获取要显示的数据
	 */
	private void setTaskDetail(int start, int end) {
		arrayListOld.clear();
		ArrayList<TaskDetail> details = DManager.getInstance(mContext).queryAllOldTaskByLimit(start, end);
		if (details.size() > 0)
			this.start = end;
		for (int i = 0; i < details.size(); i++) {
			TaskDetail taskDetail = details.get(i);
			ArrayList<TaskPoint> points = DManager.getInstance(mContext).queryPoint(taskDetail.getId(), MColums.OLD);
			if (points.size() == 0) {
				DManager.getInstance(mContext).deleteOldTaskDetail(taskDetail.getId());
				continue;
			}
			taskDetail.setList(points);
			arrayListOld.add(taskDetail);
		}
		mExpandListAdatpger.notifyDataSetChanged();
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

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_POSITION_MY_OLD;
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
	 * 检测扩展列表的子元素是否选中
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

	public class startDeleteThread implements Runnable {
		@Override
		public void run() {
			startDelete();
		}
	}

	private void startDelete() {
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
	private final int wait_pr0bar = 2;
	/**
	 * 没有删除的任务
	 */
	private final int delete_null = 3;
	private final int null_null = 4;
	/**
	 * 查询成功
	 */
	private final int success = 5;
	private final int fail = 6;
	private final int netWrong = 7;
	private final int TIMEOUT = 8;

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

					// delete();
					// setUpload2();
					// break;
				case delete_false:
					setTaskDetail(0, start + end);
					mExpandListAdatpger.notifyDataSetChanged();
					activityInterface.dismissNetDialog();
					mExpandListAdatpger.reStore();
				break;
				case wait_pr0bar:
					expandableListView.closeFooter();
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
				case fail:
					setTaskSize(0,0,String.valueOf(0));
					expandableListView.closeFooter();
					activityInterface.dismissNetDialog();
					String str = (String) msg.obj;
					activityInterface.ShowCodeDialog(Integer.valueOf(str),MTaskOldPage.this);
				break;
				case netWrong:
					setTaskSize(0,0,String.valueOf(0));
					expandableListView.closeFooter();
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "获取内容失败", 0).show();
				break;
				case TIMEOUT:
					setTaskSize(0,0,String.valueOf(0));
					expandableListView.closeFooter();
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "网络不给力，请检查网络！", 0).show();
				break;
				case success:
					expandableListView.closeFooter();
					ArrayList<TaskDetail> waitTaskList = (ArrayList<TaskDetail>) msg.obj;
					for (int i = 0; i < waitTaskList.size(); i++) {
						TaskDetail cimmit = waitTaskList.get(i);
						arrayListOld.add(cimmit);
					}
					if (waitTaskList.size() > 0) {
						setTaskSize(waitTaskList.size(),0,String.valueOf(0));
					}
					activityInterface.dismissNetDialog();
					//mExpandListAdatpger.setListTaskDetails(arrayListOld);
					mExpandListAdatpger.notifyDataSetChanged();
				break;

				default:
				break;
			}
		};
	};

	
	/**
	 * 设置提示
	 * @param size 任务数量
	 * @param pointSize 采集点数量
	 * @param localMB 本地占用
	 */
	public void setTaskSize(int size,int pointSize,String localMB){
		
		text.setVisibility(View.VISIBLE);
		text.setGravity(View.LAYOUT_DIRECTION_LTR);
		/*if (pointSize > 0 && Float.parseFloat(localMB) > 0) {
			
			text.setText("总计提交" + size + "个任务，" + pointSize + "个采集点。" + "本地占用" + localMB + "MB");
		}else if (pointSize > 0 && size > 0) {
			
			text.setText("总计提交" + size + "个任务，" + pointSize + "个采集点。");
		}else {
			
			text.setText("总计提交" + size + "个任务");
		}*/
		text.setText("总计提交" + size + "个任务");
	}
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
		arrayListOld.clear();
		start = 0;
		mExpandListAdatpger.notifyDataSetChanged();
		end = 20;
		super.onDetachedFromWindow(flag);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "MTaskOldPage--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}

}

package com.mapbar.info.collection.ui;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.Configs.FTYPE;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;
import com.mapbar.info.collection.widget.MyListView;
import com.mapbar.info.collection.widget.MyListView.MYListViewListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 点击新任务后的查找\搜索界面
 * @author miaowei
 *
 */
@SuppressLint("HandlerLeak")
public class NewTaskPage extends BasePage implements OnClickListener {

	private Context mContext;
	private ActivityInterface mActivityInterface;
	private ImageButton btn_back;
	/**
	 * 附近
	 */
	private Button btn_nearby;
	/**
	 * 全部
	 */
	private Button btn_all;
	private ImageView view_search;
	/**
	 * 按名称、距离、单价
	 */
	private RelativeLayout filter_dis;
	/**
	 * 按1000米、3000、5000
	 */
	private RelativeLayout filter_other;
	/**
	 * 附近
	 */
	private MyListView task_list;
	/**
	 * 全部
	 */
	private MyListView task_all_list;
	/**
	 * 条件为附近的搜索数据
	 */
	private ArrayList<TaskDetail> adrList = new ArrayList<TaskDetail>();
	/**
	 * 条件为全部的搜索数据
	 */
	private ArrayList<TaskDetail> city_adrList = new ArrayList<TaskDetail>();
	private TaskAdapter taskAdapter;
	private TaskAdapter taskAllAdapter;
	private FilterPage filterPage;
	/**
	 * 搜索页面
	 */
	private SearchPage searchPage;
	private final int SUCCESS_LIST = 0;
	private final int SUCCESS_LIST_A = 2;
	private final int FAILED = 1;
	private static final int FAILED_A = 3;
	private static final int TIMEOUT = 4;
	private static final int CHANGED = 5;
	private static final int DISMISS = 6;
	private static final int DATA_FALSE = 7;
	private StringBuilder builder = new StringBuilder();

	private HttpHandler httpHandler;
	private String locType = null;
	private SharedPreferences preferences;
	private Editor editor;
	private Button search_btn;
	/**
	 * 开始页数
	 */
	private  int START_PAGE = 0;
	/**
	 * 最大返回记录数
	 */
	private int ROWS = 20;
	/**
	 * 点击新任务后的查找\搜索界面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public NewTaskPage(Context context, View view, ActivityInterface aif) {
		mActivityInterface = aif;
		mContext = context;
		btn_back = (ImageButton) view.findViewById(R.id.new_task_tittle);
		btn_nearby = (Button) view.findViewById(R.id.new_task_nearby);
		btn_all = (Button) view.findViewById(R.id.new_task_all);
		view_search = (ImageView) view.findViewById(R.id.new_task_search);
		filter_dis = (RelativeLayout) view.findViewById(R.id.filter_task_o);
		filter_other = (RelativeLayout) view.findViewById(R.id.filter_task_k);
		btn_all.setOnClickListener(this);
		btn_nearby.setOnClickListener(this);
		view_search.setOnClickListener(this);
		filter_dis.setOnClickListener(this);
		filter_other.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		search_btn = (Button) view.findViewById(R.id.serarch_btn_search);

		btn_nearby.setEnabled(false);

		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = preferences.edit();

		task_list = (MyListView) view.findViewById(R.id.new_task_list);
		taskAdapter = new TaskAdapter(mContext, adrList);
		task_list.setAdapter(taskAdapter);
		task_list.setOnItemClickListener(itemClickListener);

		task_all_list = (MyListView) view.findViewById(R.id.new_task_list_all);
		taskAllAdapter = new TaskAdapter(mContext, city_adrList);
		task_all_list.setAdapter(taskAllAdapter);
		task_all_list.setOnItemClickListener(itemClickListenerAll);

		filterPage = new FilterPage(mContext, view, this, aif);
		searchPage = new SearchPage(mContext, view, mActivityInterface);
		task_all_list.setPullLoadEnable(true);
		task_list.setPullLoadEnable(true);

		task_list.setMyListViewListener(new MYListViewListener() {

			@Override
			public void onRefresh() {
				if (START_PAGE > 0) {
					
					START_PAGE = 0;
				}
				getTaskInfo(Configs.CDI, Configs.Dis, null, false);

			}

			@Override
			public void onLoadMore() {
				getTaskInfo(Configs.CDI, Configs.Dis, null, true);

			}
		});

		task_all_list.setMyListViewListener(new MYListViewListener() {

			@Override
			public void onRefresh() {
				if (START_PAGE > 0) {
					
					START_PAGE = 0;
				}
				getAllTaskList(filterPage.condi, filterPage.curCity, false);
			}

			@Override
			public void onLoadMore() {
				
				getAllTaskList(filterPage.condi, filterPage.curCity, true);

			}
		});
	}

	/**
	 * 跳转至抢单页面
	 * @param tsk
	 */
	private void showCameraPage(TaskDetail tsk) {

		mActivityInterface.setData(tsk);
		mActivityInterface.showPage(Configs.VIEW_POSITION_NEW_TASK, this, Configs.VIEW_POSITION_CAMERA, 0, null, null);

	}

	/**
	 * 
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TaskDetail tsk = taskAdapter.getTask(position - 1);
			showCameraPage(tsk);
		}
	};
	/**
	 * 
	 */
	private OnItemClickListener itemClickListenerAll = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TaskDetail tsk = taskAllAdapter.getTask(position - 1);
			showCameraPage(tsk);
		}
	};

	
	@Override
	public void onAttachedToWindow(int flag, int position) {
		super.onAttachedToWindow(flag, position);
         if (START_PAGE > 0) {
			
			START_PAGE = 0;
		}
		mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		searchPage.onAttachedToWindow(flag, position);
		filterPage.onAttachedToWindow(flag, position);
		searchPage.setSearchBodyVisible(View.GONE);
		if (searchPage.getVisible()) {
			searchPage.onClick(search_btn);
			return;
		}
		if (!btn_nearby.isEnabled()) {
			getTaskInfo(FTYPE.NAME, 1000, null, false);
		} else {
			getAllTaskList(filterPage.condi, filterPage.curCity, false);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			mActivityInterface.dismissNetDialog();
			if (filterPage.onKeyDown(keyCode, event)) {
				return true;
			}
			if (searchPage.onKeyDown(keyCode, event)) {
				return true;
			}

			goBack();

		}

		return true;
	}

	private double lat;
	private double lon;

	@Override
	public void onLocationChanged(Location location) {
		LogPrint.Print("gps", "NewTaskPage--onLocationChanged---location="+location.getProvider());
		lat = location.getLatitude();
		lon = location.getLongitude();

		searchPage.onLocationChanged(location);
		filterPage.onLocationChanged(location);
		if (location.getProvider().equals(Configs.LOCATIONTYPE)) {
			locType = "84";
		} else if (location.getProvider().equals("cell")) {
			locType = "02";
		}
		CustomCameraActivity.mlocation = location;
		super.onLocationChanged(location);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_NEW_TASK;
	}

	@Override
	public void goBack() {
		btn_nearby.setEnabled(false);
		btn_all.setEnabled(true);
		task_all_list.setVisibility(View.GONE);
		task_list.setVisibility(View.VISIBLE);
		filterPage.showChoiceBody(View.VISIBLE, Configs.FILTER_BY_NEARBY);
		/*mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_TASK_COL_POINT, null,
				null);*/
		mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_INDEX, null,
				null);
	}

	@Override
	public void onClick(View v) {
       if (START_PAGE > 0) {
			
			START_PAGE = 0;
		}
		switch (v.getId())
		{
			case R.id.new_task_tittle:
				if (filterPage.getBodyVisible() == View.VISIBLE) {
					filterPage.setBodyVisible(View.GONE);
				} else if (filterPage.getAllBodyVisible() == View.VISIBLE) {
					filterPage.setAllBodyVisible(View.GONE);
				} else {
					goBack();
				}

			break;
			case R.id.new_task_nearby: //附近
				btn_nearby.setEnabled(false);
				btn_all.setEnabled(true);
				task_all_list.setVisibility(View.GONE);
				task_list.setVisibility(View.VISIBLE);
				mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
				getTaskInfo(FTYPE.NAME, 1000, null, false);
				filterPage.showChoiceBody(View.VISIBLE, Configs.FILTER_BY_NEARBY);
			break;
			case R.id.new_task_all: //全部
				btn_nearby.setEnabled(true);
				btn_all.setEnabled(false);
				task_list.setVisibility(View.GONE);
				task_all_list.setVisibility(View.VISIBLE);
				filterPage.showChoiceBody(View.VISIBLE, Configs.FILTER_BY_ALL);
				filterPage.getCityList();
			break;
			case R.id.new_task_search: //搜索
				searchPage.setSearchBodyVisible(View.VISIBLE);
				filterPage.showChoiceBodyList(View.GONE, Configs.FILTER_BY_ALL);
			break;
			case R.id.filter_task_k: //按1000米、3000、5000
				filterPage.showChoiceBodyList(View.VISIBLE, Configs.FILTER_BY_NEARBY);
				filterPage.setFilterMode(Configs.FILTER_BY_DISTAMNCE);
			break;
			case R.id.filter_task_o: //按名称、距离、单价
				filterPage.showChoiceBodyList(View.VISIBLE, Configs.FILTER_BY_NEARBY);
				filterPage.setFilterMode(Configs.FILTER_BY_OTHER);
			break;

			default:
			break;
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private static final int Data = 6;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what)
			{
				case SUCCESS_LIST:

					ArrayList<TaskDetail> list = (ArrayList<TaskDetail>) msg.obj;
					if (list.size() > 0) {
						editor.putString(Configs.currentTime, list.get(0).getTimeId());
						editor.commit();
					}
					for (int i = 0; i < list.size(); i++) {
						adrList.add(list.get(i));
						taskAdapter.notifyDataSetChanged();
					}
					taskAdapter.notifyDataSetChanged();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					//sendEmptyMessage(DISMISS);
					mActivityInterface.dismissNetDialog();
				break;
				case SUCCESS_LIST_A:
					ArrayList<TaskDetail> aList = (ArrayList<TaskDetail>) msg.obj;
					if (aList.size() > 0) {
						editor.putString(Configs.currentTime, aList.get(0).getTimeId());
						editor.commit();
					}
					for (int i = 0; i < aList.size(); i++) {
						city_adrList.add(aList.get(i));
						taskAllAdapter.notifyDataSetChanged();
					}
					task_all_list.setVisibility(View.VISIBLE);
					task_all_list.stopLoadMore();
					task_all_list.stopRefresh();
					taskAllAdapter.notifyDataSetChanged();
					sendEmptyMessageDelayed(DISMISS, 500);
				break;
				case FAILED:
					mActivityInterface.dismissNetDialog();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					Toast.makeText(mContext, "读取数据失败", 0).show();
				break;
				case FAILED_A:
					mActivityInterface.dismissNetDialog();
					task_all_list.stopLoadMore();
					task_all_list.stopRefresh();
					Toast.makeText(mContext, "读取数据失败", 0).show();
				break;
				case TIMEOUT:
					mActivityInterface.dismissNetDialog();
					task_all_list.stopLoadMore();
					task_all_list.stopRefresh();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					Toast.makeText(mContext, "网络不给力，请检查网络！", 0).show();
				break;
				case CHANGED:
					task_all_list.stopLoadMore();
					task_all_list.stopRefresh();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					taskAdapter.notifyDataSetChanged();
					taskAllAdapter.notifyDataSetChanged();
				break;
				case DISMISS:
					mActivityInterface.dismissNetDialog();
				break;
				case DATA_FALSE:
					mActivityInterface.dismissNetDialog();
					String str = (String) msg.obj;
					task_all_list.stopLoadMore();
					task_all_list.stopRefresh();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					mActivityInterface.ShowCodeDialog(Integer.valueOf(str),NewTaskPage.this);
				break;

				default:
				break;
			}
			// add_list.setVisibility(View.GONE);
			// taskAdapter.notifyDataSetChanged();
		};

	};

	/**
	 * 
	 * @param type
	 *            排序
	 * @param dis
	 *            范围
	 * @param city
	 *            城市
	 * @param b
	 *            只有在加载更多的时候才为true,其它时间为false;
	 */
	public synchronized void getTaskInfo(FTYPE type, int dis, String city, final boolean bl) {

		if (locType == null) {
			handler.sendEmptyMessage(CHANGED);
			return;
		}

		TaskDetail taskDe = null;

		if (!bl && adrList.size() > 0) {
			adrList.clear();
			taskAdapter.notifyDataSetChanged();
		}

		//int satrt = adrList.size();
		//int end = 20;
		
		/*if (adrList.size() > 0) {
			taskDe = adrList.get(adrList.size() - 1);
		}*/
		/*if (adrList.size() > 20) {
		
			page++;
		}*/
		String order = "name";
		switch (type)
		{
			case DISTANCE:
				order = "range";
			break;
			case NAME:
				order = "name";

			break;
			case PRICE:
				order = "price";
			break;

			default:
			break;
		}
		String url;
		if (builder == null)
			builder = new StringBuilder();
		builder.append(UrlConfig.newTaskUrl);
		builder.append("loginId=" + StringUtils.getLoginId(mContext));
		builder.append("&token=" + StringUtils.getLoginToken(mContext));
		builder.append("&lat=" + lat);
		builder.append("&lon=" + lon);
		builder.append("&range=" + dis);
		builder.append("&page=" + START_PAGE);
		builder.append("&rows=" + ROWS);
		builder.append("&order=" + order);
		builder.append("&ll=" + locType);

		/*if (taskDe != null){
			
			builder.append("&currentTime=" + taskDe.getTimeId());
		}*/
			

		url = builder.toString();
		LogPrint.Print("getTaskInfourl=" + url);
		ArrayList<TaskDetail> adrList;

		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.GET);
		httpHandler.setHeader("charset", "UTF-8");
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				builder.delete(0, builder.length());
				if (data != null) {
					try {
						String jsonStr = new String(data, "UTF-8");
						JSONObject jsonObject = new JSONObject(jsonStr);
						LogPrint.Print("json", "getTaskInfo===jsonObject=="+jsonObject);
						boolean b = jsonObject.getBoolean("result");
						if (b) {
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							String timeId = "0";
							if (jsonObject.getString("id") != null && jsonObject.getString("id").equals("")) {
								
								timeId = (String) jsonObject.getString("id");
							}
							JSONArray jsonArray = (JSONArray) dataJson.get("rows");
							if (jsonArray.length() > 0) {
								START_PAGE += ROWS;
								TaskDetail taskDetail;
								ArrayList<TaskDetail> adrList = new ArrayList<TaskDetail>();
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject jsonObjec = (JSONObject) jsonArray.get(i);
									taskDetail = new TaskDetail();
									taskDetail.setDescription(jsonObjec.getString("description"));
									taskDetail.setFalg(MColums.WAIT);
									taskDetail.setId(jsonObjec.getString("id"));
									taskDetail.setUpdateTime(jsonObjec.getString("lastUpdateTime"));
									taskDetail.setName(jsonObjec.getString("name"));
									taskDetail.setRice(jsonObjec.getDouble("price"));
									taskDetail.setType(jsonObjec.getInt("taskType") + "");
									taskDetail.setStartTime(jsonObjec.getString("createTime"));
									taskDetail.setLostTime(jsonObjec.getString("exceedTime"));
									//2014-11-7添加任务状态
									//taskDetail.setStatus(jsonObjec.getString("status"));
									taskDetail.setTimeId(timeId);
									adrList.add(taskDetail);
								}
								Message msg = handler.obtainMessage();
								msg.obj = adrList;
								msg.what = SUCCESS_LIST;
								handler.sendMessage(msg);
							}else {
								
								Message message = handler.obtainMessage();
								message.what = DATA_FALSE;
								message.obj = jsonObject.getString("message");
								handler.sendMessage(message);
							}
							

						} else {
							Message message = handler.obtainMessage();
							message.what = DATA_FALSE;
							message.obj = jsonObject.getString("message");
							handler.sendMessage(message);
						}

					} catch (Exception e) {
						handler.sendEmptyMessage(FAILED);
						e.printStackTrace();
					}

				} else {
					if (arg0 != 200){
						
						handler.sendEmptyMessage(TIMEOUT);
					}else {
						
						handler.sendEmptyMessage(FAILED);
					}
						
				}
			}
		});
		httpHandler.execute();

	}

	/**
	 * 全部界面
	 * 
	 * @param type
	 * @param city
	 * @param b
	 *            更多加载为true
	 */
	public synchronized void getAllTaskList(String type, String city, final boolean bl) {

		if (locType == null) {
			handler.sendEmptyMessage(CHANGED);
			return;
		}

		TaskDetail taskDe = null;
		if (!bl) {
			city_adrList.clear();
			taskAllAdapter.notifyDataSetChanged();
		}
		/*int satrt = city_adrList.size();
		int end = 20;
		if (city_adrList.size() > 0) {
			taskDe = city_adrList.get(city_adrList.size() - 1);
		}*/
		String url;
		if (builder == null)
			builder = new StringBuilder();
		builder.append(UrlConfig.newTaskUrl);
		builder.append("loginId=" + StringUtils.getLoginId(mContext));
		builder.append("&token=" + StringUtils.getLoginToken(mContext));
		// builder.append("&lat=" + lat);
		// builder.append("&lon=" + lon);
		builder.append("&page=" + START_PAGE);
		builder.append("&rows=" + ROWS);
		builder.append("&order=" + type);
		builder.append("&ll=" + locType);
		if (!StringUtils.isNullColums(city)){
			
			String cityString = Util.enDcodeUtf(city, "utf-8");
			builder.append("&regional=" + cityString);
		 }
			
		if (taskDe != null)
			builder.append("&currentTime=" + taskDe.getTimeId());
		url = builder.toString();
		LogPrint.Print("getAllTaskListurl=" + url);
		builder.delete(0, builder.length());
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.GET);
		httpHandler.setHeader("charset", "UTF-8");
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						String jsonStr = new String(data, "UTF-8");
						JSONObject jsonObject = new JSONObject(jsonStr);
						
						LogPrint.Print("json", "getAllTaskList===jsonObject=="+jsonObject);
						boolean b = jsonObject.getBoolean("result");
						if (b) {
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							String timeId = "0";
							if (jsonObject.getString("id") != null && jsonObject.getString("id").equals("")) {
								
								timeId = (String) jsonObject.getString("id");
							}
							JSONArray jsonArray = (JSONArray) dataJson.get("rows");
							TaskDetail taskDetail;
							if (jsonArray.length() > 0) {
								
								START_PAGE += ROWS;
								ArrayList<TaskDetail> city_adrList = new ArrayList<TaskDetail>();
								for (int i = 0; i < jsonArray.length(); i++) {
									
									JSONObject jsonObjec = (JSONObject) jsonArray.get(i);
									taskDetail = new TaskDetail();
									taskDetail.setDescription(jsonObjec.getString("description"));
									taskDetail.setFalg(MColums.WAIT);
									taskDetail.setId(jsonObjec.getString("id"));
									taskDetail.setUpdateTime(jsonObjec.getString("lastUpdateTime"));
									taskDetail.setName(jsonObjec.getString("name"));
									taskDetail.setRice(jsonObjec.getDouble("price"));
									taskDetail.setType(jsonObjec.getInt("taskType") + "");
									taskDetail.setStartTime(jsonObjec.getString("createTime"));
									taskDetail.setLostTime(jsonObjec.getString("exceedTime"));
									taskDetail.setTimeId(timeId);
									//2014-11-7添加任务状态
									//taskDetail.setStatus(jsonObjec.getString("status"));
									city_adrList.add(taskDetail);
								}
								Message msg = handler.obtainMessage();
								msg.what = SUCCESS_LIST_A;
								msg.obj = city_adrList;
								handler.sendMessage(msg);
								
							}else {
								
								Message message = handler.obtainMessage();
								message.what = DATA_FALSE;
								message.obj = jsonObject.getString("message");
								handler.sendMessage(message);
							}

						} else {
							String msg = jsonObject.getString("message");
							mActivityInterface.ShowCodeDialog(Integer.valueOf(msg),NewTaskPage.this);
						}

					} catch (Exception e) {
						handler.sendEmptyMessage(FAILED_A);
						e.printStackTrace();
					} 
				} else {
					if (arg0 != 200)

						handler.sendEmptyMessage(TIMEOUT);
					else
						handler.sendEmptyMessage(FAILED_A);
				}
			}
		});
		httpHandler.execute();

	}

	@Override
	public void onDetachedFromWindow(int flag) {
		filterPage.onDetachedFromWindow(-1);
		searchPage.onDetachedFromWindow(-1);
		city_adrList.clear();
		adrList.clear();
		taskAdapter.notifyDataSetChanged();
		taskAllAdapter.notifyDataSetChanged();
		super.onDetachedFromWindow(flag);
	}
	

}

package com.mapbar.info.collection.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;
import com.mapbar.info.collection.widget.MTaskListView;
import com.mapbar.info.collection.widget.MTaskListView.TaskListViewListener;
import com.mapbar.info.collection.widget.MyListView;
import com.mapbar.info.collection.widget.MyListView.MYListViewListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
/**
 * 搜索页面
 * @author miaowei
 *
 */
public class SearchPage extends BasePage implements OnClickListener, OnItemClickListener {

	private Context mContext;
	private MyListView result_list;
	private TaskAdapter resultAdapter;
	private ArrayList<TaskDetail> adrList = new ArrayList<TaskDetail>();
	private Button search_btn;
	private Button cancle_btn;
	private LinearLayout layout;
	private ActivityInterface mActivityInterface;
	private EditText editText;
	private MyListView task_list;

	private TextView filter_range;
	private TextView filter_cdi;
	private TextView filter_city;
	private TextView filter_price;
	private TextView filter_dis_by_all;
	private String keyWord;
	private ImageView delete_img;
	private SharedPreferences preferences;
	private Editor editor;

	/**
	 * 开始页数
	 */
	private  int START_PAGE = 0;
	/**
	 * 最大返回记录数
	 */
	private int ROWS = 20;
	/**
	 * 搜索页面
	 * @param context
	 * @param view
	 * @param interface1
	 */
	public SearchPage(Context context, View view, ActivityInterface interface1) {
		this.mActivityInterface = interface1;
		this.mContext = context;
		result_list = (MyListView) view.findViewById(R.id.search_result_list);
		cancle_btn = (Button) view.findViewById(R.id.serarch_btn_cancle);
		search_btn = (Button) view.findViewById(R.id.serarch_btn_search);
		editText = (EditText) view.findViewById(R.id.search_top_edit);
		search_btn.setOnClickListener(this);
		cancle_btn.setOnClickListener(this);
		result_list.setOnItemClickListener(this);

		resultAdapter = new TaskAdapter(mContext, adrList);
		result_list.setAdapter(resultAdapter);
		layout = (LinearLayout) view.findViewById(R.id.search_body_main);
		layout.setClickable(true);
		task_list = (MyListView) view.findViewById(R.id.new_task_list);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = preferences.edit();

		filter_range = (TextView) view.findViewById(R.id.new_task_dis_filter);
		filter_cdi = (TextView) view.findViewById(R.id.new_task_filter_condation);
		filter_city = (TextView) view.findViewById(R.id.new_task_filter_all);
		filter_price = (TextView) view.findViewById(R.id.filter_all_price);
		filter_dis_by_all = (TextView) view.findViewById(R.id.filter_all_dis);
		result_list.setPullLoadEnable(true);
		result_list.setMyListViewListener(new MYListViewListener() {

			@Override
			public void onRefresh() {
				if (START_PAGE > 0) {
					
					START_PAGE = 0;
				}
				if (task_list.getVisibility() == View.VISIBLE) {
					searchByText(true, keyWord, getNearFilterKey(Configs.RANGE), getNearFilterKey(Configs.CONDITION),
							null, false);
				} else {
					searchByText(false, keyWord, null, null, Configs.curCity, false);
				}
			}

			@Override
			public void onLoadMore() {
				if (task_list.getVisibility() == View.VISIBLE) {
					searchByText(true, keyWord, getNearFilterKey(Configs.RANGE), getNearFilterKey(Configs.CONDITION),
							null, true);
				} else {
					searchByText(false, keyWord, null, null, Configs.curCity, true);
				}

			}
		});

		delete_img = (ImageView) view.findViewById(R.id.search_img_delete);
		delete_img.setOnClickListener(this);

		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search_btn.setVisibility(View.VISIBLE);
				cancle_btn.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	public void setSearchBodyVisible(int visible) {
		layout.setVisibility(visible);
		setLayoutBk();
	}
	public boolean getVisible(){
		int vi=layout.getVisibility();
		return vi==View.VISIBLE?true:false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showCameraPage(parent, position - 1);
	}

	private void showCameraPage(AdapterView<?> parent, int position) {
		TaskDetail tsk = resultAdapter.getTask(position);
		mActivityInterface.setData(tsk);
		mActivityInterface.showPage(Configs.VIEW_POSITION_SEARCH, this, Configs.VIEW_POSITION_CAMERA, 0, null, null);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
			case R.id.serarch_btn_cancle:
				goBack();
			break;
			case R.id.search_img_delete:
				editText.setText("");
			break;
			case R.id.serarch_btn_search: //全局搜索
				
				if (editText.getText().toString() != null && !editText.getText().toString().equals("")) {
					mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
					search_btn.setVisibility(View.GONE);
					cancle_btn.setVisibility(View.VISIBLE);
					keyWord =StringUtils.replaceBlank(editText.getText().toString()) ;
					if (task_list.getVisibility() == View.VISIBLE) {
						searchByText(true, keyWord, getNearFilterKey(Configs.RANGE),
								getNearFilterKey(Configs.CONDITION), null, false);
					} else {
						searchByText(false, keyWord, null, null, Configs.curCity, false);
					}

				} else {
					Toast.makeText(mContext, "请输入关键字", 0).show();
				}

			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
       if (START_PAGE > 0) {
			
			START_PAGE = 0;
		}
		layout.setBackgroundColor(mContext.getResources().getColor(R.color.choice_bg_color));
		super.onAttachedToWindow(flag, position);
	}
    public void setLayoutBk(){
    	layout.setBackgroundColor(mContext.getResources().getColor(R.color.choice_bg_color));
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout.getVisibility() == View.VISIBLE) {
				
				goBack();
				return true;
			}
		}
		return false;
	}

	@Override
	public void goBack() {
		if (layout.getVisibility() == View.VISIBLE) {
			search_btn.setVisibility(View.VISIBLE);
			cancle_btn.setVisibility(View.GONE);
			layout.setVisibility(View.GONE);
			adrList.clear();
			result_list.setVisibility(View.INVISIBLE);
			resultAdapter.notifyDataSetChanged();
		}
		super.goBack();
	}

	private double lat;
	private double lon;
	private String locType=null;

	@Override
	public void onLocationChanged(Location location) {
		LogPrint.Print("gps", "SearchPage--onLocationChanged---location="+location.getProvider());
		lat = location.getLatitude();
		lon = location.getLongitude();
		if (location.getProvider().equals(Configs.LOCATIONTYPE)) {
			locType = "84";
		} else if (location.getProvider().equals("cell")) {
			locType = "02";
		}
		CustomCameraActivity.mlocation = location;
		super.onLocationChanged(location);
	}

	private MHttpHandler httpHandler;
	private StringBuilder builder = new StringBuilder();

	/**
	 * 
	 * @param str
	 *            关键字
	 * @param range
	 *            范围（附近）
	 * @param condation
	 *            条件（附近and all）排序
	 * @param curCity
	 *            区域（all）
	 * 
	 * 
	 */
	protected void searchByText(boolean isNearby, String str, String range, String condation, String curCity,
			final boolean bl) {
		String cityString = null;
		if (!StringUtils.isNullColums(str)) {
			
			cityString =Util.enDcodeUtf(str, "utf-8");
		}
		if(locType==null){
			handler.sendEmptyMessage(3);
			return;
		}

		TaskDetail taskDe = null;
		if (!bl) {
			adrList.clear();
		}

		/*int satrt = adrList.size();
		int end =  20;
		if (adrList.size() > 0) {
			taskDe = adrList.get(adrList.size() - 1);
		}*/

		String strr = UrlConfig.newTaskUrl + "loginId=" + StringUtils.getLoginId(mContext) + "&token=" + StringUtils.getLoginToken(mContext) + "&name=" + cityString;
		builder.append(strr);
		if (isNearby) {
			builder.append("&range=" + range);
		} else if (!StringUtils.isNullColums(curCity)) {
			
			String curCityString = Util.enDcodeUtf(curCity, "utf-8");
			builder.append("&regional=" + curCityString);
			
		}

		builder.append("&order=" + condation);

		builder.append("&lat=" + lat);
		builder.append("&lon=" + lon);
		builder.append("&page=" + START_PAGE);
		builder.append("&rows=" + ROWS);
		builder.append("&ll=" +locType );
		/*if (taskDe != null){
			
			builder.append("&currentTime=" + taskDe.getTimeId());
		}*/
			
		String url = builder.toString();
		builder.delete(0, builder.length());
		LogPrint.Print("searchByText=="+url);
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
								ArrayList<TaskDetail> adrList = new ArrayList<TaskDetail>();
								TaskDetail taskDetail;
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
								Message msg=handler.obtainMessage();
								msg.what=0;
								msg.obj=adrList;
								handler.sendMessage(msg);
							}else {
								
								Message message = handler.obtainMessage();
								message.what = 5;
								message.obj = jsonObject.getString("message");
								handler.sendMessage(message);
							}
							

						} else {
							String msg = jsonObject.getString("message");
							mActivityInterface.ShowCodeDialog(Integer.valueOf(msg),SearchPage.this);
						}

					} catch (Exception e) {
						handler.sendEmptyMessage(1);
						e.printStackTrace();
					} 

				} else {
					if (arg0 != 200)
						handler.sendEmptyMessage(2);
					else
						handler.sendEmptyMessage(1);
				}
			}
		});
		httpHandler.execute();
	}

	/**
	 * 获取附近页面的Filter 关键字
	 */
	public String getNearFilterKey(int tyep) {
		if (tyep == Configs.RANGE) {
			String range = filter_range.getText().toString();
			return range;
		} else if (tyep == Configs.CONDITION) {
			String cdition = filter_cdi.getText().toString();
			if (cdition.equals("按名称")) {
				return "name";
			} else if (cdition.equals("按距离")) {
				return "range";
			}
			if (cdition.equals("按单价")) {
				return "price";
			}
			return cdition;
		} else if (tyep == Configs.BYPRICE) {
			String price = filter_price.getText().toString();
			return price;

		} else if (tyep == Configs.BYDISTANCE) {
			String dis = filter_dis_by_all.getText().toString();
			return dis;

		}
		return null;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 0:
					ArrayList<TaskDetail> list = (ArrayList<TaskDetail>) msg.obj;
					if (list.size() > 0) {
						editor.putString(Configs.currentTime, list.get(0).getTimeId());
						editor.commit();
					}
					for (int i = 0; i < list.size(); i++) {
						adrList.add(list.get(i));
						resultAdapter.notifyDataSetChanged();
					}
					mActivityInterface.dismissNetDialog();
					resultAdapter.notifyDataSetChanged();
					result_list.stopRefresh();
					result_list.stopLoadMore();
				    sendEmptyMessage(4);
				break;
				case 1:
					Toast.makeText(mContext, "读取数据失败", 0).show();
				break;
				case 2:
					mActivityInterface.dismissNetDialog();
					resultAdapter.notifyDataSetChanged();
					result_list.stopLoadMore();
					result_list.stopRefresh();
					if (adrList.size() > 0) {
						result_list.setVisibility(View.VISIBLE);
						layout.setBackgroundColor(mContext.getResources().getColor(R.color.userlogin_bk));
					} else {
						Toast.makeText(mContext, "网络不给力，请检查网络", 0).show();
					}

				break;
				case 3:
					mActivityInterface.dismissNetDialog();
					result_list.stopLoadMore();
					result_list.stopRefresh();
					
					break;
				case 4:
					mActivityInterface.dismissNetDialog();
					if (adrList.size() > 0) {
						result_list.setVisibility(View.VISIBLE);
						layout.setBackgroundColor(mContext.getResources().getColor(R.color.userlogin_bk));
					} else {
						Toast.makeText(mContext, "请更换关键字后再试！", 0).show();
					}
					break;
				case 5:
					mActivityInterface.dismissNetDialog();
					String str = (String) msg.obj;
					result_list.stopLoadMore();
					result_list.stopRefresh();
					task_list.stopLoadMore();
					task_list.stopRefresh();
					mActivityInterface.ShowCodeDialog(Integer.valueOf(str),SearchPage.this);
					break;
				default:
				break;
			}
		};
	};

	public void onDetachedFromWindow(int flag) {
		adrList.clear();
	};
}

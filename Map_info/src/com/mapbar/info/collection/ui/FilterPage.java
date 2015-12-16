package com.mapbar.info.collection.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.Configs.FTYPE;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.widget.FilterListView;
import com.mapbar.info.collection.widget.FilterVisibleListener;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 过滤搜索条件(按名称、按距离、按单价)
 * @author miaowei
 *
 */
public class FilterPage extends BasePage implements OnClickListener, FilterVisibleListener {
	private Context mContext;
	/**
	 * 附近
	 */
	private FilterListView choice_list;
	/**
	 * 搜索
	 */
	private FilterListView choice_other_list;
	/**
	 * 全部
	 */
	private FilterListView choice_list_all;
	private ArrayList<String> city = new ArrayList<String>();
	private ArrayList<String> source = new ArrayList<String>();
	private ArrayList<String> other = new ArrayList<String>();
	private LinearLayout layout_body;
	private LinearLayout layout_body_all;
	private int flag;
	private ChoiceAdatpter adatpter;
	private ChoiceAdatpter other_adapter;
	private ChoiceAdatpter all_adapter;
	private LinearLayout search_top_nearby;
	/**
	 * 
	 */
	private LinearLayout search_top_all;
	private LinearLayout layout_all_filter_bar;

	private TextView filter_dis;
	private TextView filter_cdt;
	private TextView filter_all;
	private ImageView filter_dis_img;
	private ImageView filter_cdt_img;
	private ImageView filter_all_img;
	private NewTaskPage newTaskPage;
	private ActivityInterface mActivityInterface;
	private Button btn_dis;
	private Button btn_price;
	public String curCity;
	public String condi = "name";

	/**
	 * 过滤搜索条件(按名称、按距离、按单价)
	 * @param context
	 * @param view
	 * @param newTaskPage
	 * @param aif
	 */
	public FilterPage(Context context, View view, NewTaskPage newTaskPage, ActivityInterface aif) {
		mActivityInterface = aif;
		mContext = context;
		this.newTaskPage = newTaskPage;
		choice_list = (FilterListView) view.findViewById(R.id.choice_list);
		choice_list_all = (FilterListView) view.findViewById(R.id.choice_list_all);
		choice_other_list = (FilterListView) view.findViewById(R.id.choice_other);

		choice_list.setOnVisibleListener(this);
		choice_list_all.setOnVisibleListener(this);
		choice_other_list.setOnVisibleListener(this);

		source.add("1000");
		source.add("3000");
		source.add("5000");
		other.add("按名称");
		other.add("按距离");
		other.add("按单价");

		layout_body = (LinearLayout) view.findViewById(R.id.choice_body);
		layout_body_all = (LinearLayout) view.findViewById(R.id.choice_body_all);

		search_top_nearby = (LinearLayout) view.findViewById(R.id.search_top_nearby);

		search_top_all = (LinearLayout) view.findViewById(R.id.search_top_all);
		search_top_all.setOnClickListener(this);

		adatpter = new ChoiceAdatpter(source);
		other_adapter = new ChoiceAdatpter(other);
		all_adapter = new ChoiceAdatpter(city);

		all_adapter.setVisibleK(false);
		other_adapter.setVisibleK(false);
		adatpter.setVisibleK(true);

		choice_list.setAdapter(adatpter);
		choice_other_list.setAdapter(other_adapter);
		choice_list_all.setAdapter(all_adapter);

		layout_all_filter_bar = (LinearLayout) view.findViewById(R.id.list_all_filter_bar);
		layout_all_filter_bar.setOnClickListener(this);

		choice_list.setOnItemClickListener(disItemClickListener);
		choice_other_list.setOnItemClickListener(othreItemClickListener);
		choice_list_all.setOnItemClickListener(adrItemClickListener);

		filter_dis = (TextView) view.findViewById(R.id.new_task_dis_filter);
		filter_cdt = (TextView) view.findViewById(R.id.new_task_filter_condation);
		filter_all = (TextView) view.findViewById(R.id.new_task_filter_all);

		filter_all_img = (ImageView) view.findViewById(R.id.new_task_filter_all_img);
		filter_cdt_img = (ImageView) view.findViewById(R.id.new_task_filter_condation_img);
		filter_dis_img = (ImageView) view.findViewById(R.id.new_task_dis_filter_mg);
		btn_price = (Button) view.findViewById(R.id.filter_all_price);
		btn_dis = (Button) view.findViewById(R.id.filter_all_dis);

		btn_price.setOnClickListener(this);
		btn_dis.setOnClickListener(this);
	}

	private OnItemClickListener disItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			setListItemSelector(parent, view, position);
			choice_list.setVisibility(View.GONE);
			TextView vi = (TextView) parent.getChildAt(position).findViewById(R.id.filter_text);
			filter_dis.setText(vi.getText());
			Configs.Dis = Integer.valueOf(vi.getText().toString());
			mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
			newTaskPage.getTaskInfo(FTYPE.NOMRAL, Integer.valueOf(vi.getText().toString()), null, false);
			adatpter.setSelected(position);

		}
	};
	private OnItemClickListener othreItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			setListItemSelector(parent, view, position);
			choice_other_list.setVisibility(View.GONE);
			TextView vi = (TextView) parent.getChildAt(position).findViewById(R.id.filter_text);
			filter_cdt.setText(vi.getText());

			if (vi.getText().toString().equals("按名称")) {
				Configs.CDI = FTYPE.NAME;
			} else if (vi.getText().toString().equals("按距离")) {
				Configs.CDI = FTYPE.DISTANCE;
			} else if (vi.getText().toString().equals("按单价")) {
				Configs.CDI = FTYPE.PRICE;
			}
			other_adapter.setSelected(position);
			mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
			newTaskPage.getTaskInfo(Configs.CDI, Configs.Dis, null, false);
		}
	};
	private OnItemClickListener adrItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			setListItemSelector(parent, view, position);
			choice_list_all.setVisibility(View.GONE);
			TextView vi = (TextView) view.findViewById(R.id.filter_text);
			filter_all.setText(vi.getText());
			all_adapter.setSelected(position);
			String str = vi.getText().toString();
			if (str != null && !str.equals("") && !str.equals("null")) {
				curCity = str;
				Configs.curCity = str;
			}

			mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
			newTaskPage.getAllTaskList(condi, vi.getText().toString(), false);
		}
	};

	/**
	 * filter 设置 item 选中其它条目 图片隐藏
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 */
	private void setListItemSelector(AdapterView<?> parent, View view, int position) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			if (i == position)
				continue;
			View vi = parent.getChildAt(i).findViewById(R.id.filter_img);
			vi.setVisibility(View.INVISIBLE);
		}
		onKeyDown(4, null);
		view.findViewById(R.id.filter_img).setVisibility(View.VISIBLE);
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		super.onAttachedToWindow(flag, position);
		filter_dis.setText("1000");
		filter_cdt.setText("按名称");
		other_adapter.setSelected(0);
		adatpter.setSelected(0);
		condi = "name";

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mActivityInterface.dismissNetDialog();
			if (layout_body.getVisibility() == View.VISIBLE) {
				layout_body.setVisibility(View.GONE);
				return true;
			} else if (layout_body_all.getVisibility() == View.VISIBLE) {
				layout_body_all.setVisibility(View.GONE);
				layout_all_filter_bar.setVisibility(View.VISIBLE);
				return true;
			} else {
				return false;
			}

		}
		return false;
	}

	@Override
	public void goBack() {
		super.goBack();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.search_top_all: //按距离搜索
				showChoiceBodyList(View.VISIBLE, Configs.FILTER_BY_ALL);
				layout_all_filter_bar.setVisibility(View.GONE);
				choice_list_all.setVisibility(View.VISIBLE);
			break;
			case R.id.list_all_filter_bar:

			break;
			case R.id.filter_all_dis: //按名称搜索
				condi = "name";
				mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
				newTaskPage.getAllTaskList(condi, curCity, false);
			break;
			case R.id.filter_all_price: //按单价搜索
				condi = "price";
				mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
				newTaskPage.getAllTaskList(condi, curCity, false);
			break;
			default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void showChoiceBody(int visible, int flag) {
		layout_body_all.setVisibility(View.GONE);
		layout_body.setVisibility(View.GONE);
		if (flag == Configs.FILTER_BY_NEARBY) {
			if (visible == View.VISIBLE) {
				search_top_all.setVisibility(View.GONE);
				choice_list_all.setVisibility(View.GONE);
			}
			search_top_nearby.setVisibility(visible);

		} else if (flag == Configs.FILTER_BY_ALL) {
			if (visible == View.VISIBLE) {
				search_top_nearby.setVisibility(View.GONE);
				choice_list.setVisibility(View.GONE);
				choice_other_list.setVisibility(View.GONE);
			}

			search_top_all.setVisibility(visible);

		}
	}

	public void showChoiceBodyList(int visible, int flag) {
		if (flag == Configs.FILTER_BY_NEARBY) {
			if (visible == View.VISIBLE)
				layout_body_all.setVisibility(View.GONE);
			layout_body.setVisibility(visible);
		} else if (flag == Configs.FILTER_BY_ALL) {
			if (visible == View.VISIBLE)
				layout_body.setVisibility(View.GONE);
			layout_body_all.setVisibility(visible);
		}
	}

	public int getBodyVisible() {
		return layout_body.getVisibility();
	}

	public int getAllBodyVisible() {
		return layout_body_all.getVisibility();
	}

	public void setBodyVisible(int i) {
		if (i == View.GONE) {
			choice_list.setVisibility(i);
			choice_other_list.setVisibility(i);
		}
		layout_body.setVisibility(i);
	}

	public void setAllBodyVisible(int i) {
		if (i == View.GONE) {
			choice_list_all.setVisibility(i);
		}
		layout_body_all.setVisibility(i);
		layout_all_filter_bar.setVisibility(View.VISIBLE);
	}

	public void setFilterMode(int flag) {
		if (flag == Configs.FILTER_BY_DISTAMNCE) {
			choice_other_list.setVisibility(View.GONE);
			choice_list.setVisibility(View.VISIBLE);
		} else if (flag == Configs.FILTER_BY_OTHER) {
			choice_other_list.setVisibility(View.VISIBLE);
			choice_list.setVisibility(View.GONE);
		}
	}

	private class ChoiceAdatpter extends BaseAdapter {

		private ArrayList<String> data;
		private boolean showk;
		private int selected = -1;

		public ChoiceAdatpter(ArrayList<String> str) {
			data = str;
		}

		public void setSelected(int i) {
			this.selected = i;
		}

		public void setVisibleK(boolean b) {
			showk = b;
		}

		@Override
		public int getCount() {

			return data.size();
		}

		@Override
		public Object getItem(int position) {

			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View itemView, ViewGroup parent) {
			ViewHoler viewHoler;
			if (itemView == null) {
				itemView = View.inflate(mContext, R.layout.layout_filter_item_task, null);
				viewHoler = new ViewHoler();
				viewHoler.adrView = (TextView) itemView.findViewById(R.id.filter_text);
				viewHoler.k = (TextView) itemView.findViewById(R.id.filter_text_k);
				viewHoler.filter_img = (ImageView) itemView.findViewById(R.id.filter_img);
				itemView.setTag(viewHoler);
			} else {
				viewHoler = (ViewHoler) itemView.getTag();
			}
			if (showk)
				viewHoler.k.setVisibility(View.VISIBLE);
			else
				viewHoler.k.setVisibility(View.GONE);

			viewHoler.adrView.setText(data.get(position));
			if (selected == position)
				viewHoler.filter_img.setVisibility(View.VISIBLE);
			else
				viewHoler.filter_img.setVisibility(View.INVISIBLE);
			return itemView;
		}

	}

	class ViewHoler {
		TextView adrView;
		TextView k;
		ImageView filter_img;
	}

	@Override
	public void onFilterVisibleChageListener(int visible, int id) {
		switch (id)
		{
			case R.id.choice_list:
				Log.e("Filter", "List");
				if (visible == View.VISIBLE) {
					filter_dis.setTextColor(mContext.getResources().getColor(R.color.green));
					filter_dis_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_open));
				} else {
					filter_dis.setTextColor(mContext.getResources().getColor(R.color.user_text_c));
					filter_dis_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_close));
				}

			break;
			case R.id.choice_other:
				if (visible == View.VISIBLE) {
					filter_cdt.setTextColor(mContext.getResources().getColor(R.color.green));
					filter_cdt_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_open));
				} else {
					filter_cdt.setTextColor(mContext.getResources().getColor(R.color.user_text_c));
					filter_cdt_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_close));
				}
			break;
			case R.id.choice_list_all: //
				if (visible == View.VISIBLE) {
					filter_all.setTextColor(mContext.getResources().getColor(R.color.green));
					filter_all_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_open));
				} else {
					filter_all.setTextColor(mContext.getResources().getColor(R.color.user_text_c));
					filter_all_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.filter_state_close));
				}
			break;

			default:
			break;
		}

	}

	private double lat;
	private double lon;

	@Override
	public void onLocationChanged(Location location) {
		LogPrint.Print("gps", "FilterPage--onLocationChanged---location="+location.getProvider());
		lat = location.getLatitude();
		lon = location.getLongitude();
		CustomCameraActivity.mlocation = location;
		super.onLocationChanged(location);
	}

	private MHttpHandler httpHandler;

	/**
	 * 获取区域城市内容
	 */
	public void getCityList() {
		mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		city.clear();
		String url = UrlConfig.regUrl + "lonlat=" + lon + "," + lat + "&loginId=" + StringUtils.getLoginId(mContext) + "&token="
				+ StringUtils.getLoginToken(mContext);
		
		LogPrint.Print("getCityList--Url==="+url);
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
							ArrayList<String> city = new ArrayList<String>();
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							String cur = dataJson.getString("current");
							JSONArray array = dataJson.getJSONArray("town");
							for (int i = 0; i < array.length(); i++) {
								String str = (String) array.getString(i);
								city.add(str);
							}
							Message message = handler.obtainMessage();
							curCity = cur;
							message.obj = city;
							message.what = 0;
							handler.sendMessage(message);
						} else {
							Message message = handler.obtainMessage();
							message.what=3;
							message.obj=jsonObject.getString("message");
							handler.sendMessage(message);
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(1);
						e.printStackTrace();
					}
				} else {
					if (arg0 != 200)
						handler.sendEmptyMessage(1);
					else
						handler.sendEmptyMessage(2);
				}

			}
		});
		httpHandler.execute();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what)
			{
				case 0:
					ArrayList<String> list = (ArrayList<String>) msg.obj;
					for (int i = 0; i < list.size(); i++) {
						city.add(list.get(i));
						//all_adapter.notifyDataSetChanged();
					}
					all_adapter.notifyDataSetChanged();
					if (curCity != null && !curCity.equals("") && !curCity.equals("null")) {
						filter_all.setText(curCity);
					} else {
						mActivityInterface.dismissNetDialog();
						Toast.makeText(mContext, "获取区域信息失败", 0).show();
					}
					condi = "name";
					newTaskPage.getAllTaskList("name", curCity, false);
				break;
				case 1:
					mActivityInterface.dismissNetDialog();
					all_adapter.notifyDataSetChanged();
					Toast.makeText(mContext, "获取区域信息失败", 0).show();
				break;
				case 2:
					mActivityInterface.dismissNetDialog();
					all_adapter.notifyDataSetChanged();
					Toast.makeText(mContext, "网络不给力，请检查网络", 0).show();
				break;
				case 3:
					mActivityInterface.dismissNetDialog();
					String str=(String) msg.obj;
					mActivityInterface.ShowCodeDialog(Integer.valueOf(str),FilterPage.this);
					break;

				default:
				break;
			}
		};
	};

	public void onDetachedFromWindow(int flag) {
		condi = "name";
	};
}

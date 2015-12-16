package com.mapbar.info.collection.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的信息
 * @author miaowei
 *
 */
public class UserPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface mActivityInterface;
	private TextView user_name;
	private TextView user_account;
	private TextView user_phone;
	/**
	 * 总收入
	 */
	private TextView user_all_earn;
	/**
	 * 当月收入
	 */
	private TextView user_month_earn;
	private TextView user_coll_day;
	private TextView user_coll_point;
	private TextView user_coll_eff;
	private ImageButton button_back;
	private TitleBar titleBar;
	/**
	 * 未支付金额
	 */
	private TextView mText_user_notpay;
	/**
	 * 已支付金额
	 */
	private TextView mText_user_yetpay;
	/**
	 * 用户登录密码
	 */
	private TextView mText_user_password;

	private Button user_btn_task;
	private Button user_btn_more;
	private Button user_btn_info;
	private SharedPreferences preferences;
	private Editor editor;
	private MHttpHandler httpHandler;
	private DecimalFormat df = new DecimalFormat("######0.00");
	/**
	 * 创建子账号
	 */
	private RelativeLayout mRelativeCreateUser;
	private TextView mTextViewCreate_user;
	/**
	 * 我要计算
	 */
	private Button mButton_count;
	
	private User user;

	/**
	 * 我的信息
	 * @param context
	 * @param view
	 * @param aif
	 */
	public UserPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.mActivityInterface = aif;

		titleBar = new TitleBar(mContext, view, this, aif);
		user_name = (TextView) view.findViewById(R.id.user_name);
		user_account = (TextView) view.findViewById(R.id.user_account);
		user_phone = (TextView) view.findViewById(R.id.user_phone);

		user_all_earn = (TextView) view.findViewById(R.id.user_all_money);
		user_month_earn = (TextView) view.findViewById(R.id.user_month_money);
		//user_coll_day = (TextView) view.findViewById(R.id.user_use_day);
		user_coll_point = (TextView) view.findViewById(R.id.user_right_coll);
		user_coll_eff = (TextView) view.findViewById(R.id.user_right_l);
		
		mText_user_notpay = (TextView)view.findViewById(R.id.text_user_notpay);
		mText_user_yetpay = (TextView)view.findViewById(R.id.text_user_yetpay);
		
		mRelativeCreateUser = (RelativeLayout)view.findViewById(R.id.relativeCreateUser);
		mTextViewCreate_user = (TextView)view.findViewById(R.id.text_CreateUser);
		
		mText_user_password = (TextView)view.findViewById(R.id.user_password);
		
		mButton_count = (Button)view.findViewById(R.id.btn_count);
		mButton_count.setOnClickListener(this);
		button_back = (ImageButton) view.findViewById(R.id.btn_home);
		button_back.setOnClickListener(this);

		user_btn_task = (Button) view.findViewById(R.id.user_btn_do_task);
		user_btn_info = (Button) view.findViewById(R.id.user_btn_user_info);
		user_btn_more = (Button) view.findViewById(R.id.user_btn_seeting_more);

		user_btn_task.setOnClickListener(this);
		user_btn_info.setOnClickListener(this);
		user_btn_more.setOnClickListener(this);
		user_btn_info.setSelected(true);
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = preferences.edit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		if (user == null) {
			
			getUserInfo();
			mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
			super.onAttachedToWindow(flag, position);
		}
		
		
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		super.onDetachedFromWindow(flag);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		goBack();
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		LogPrint.Print("gps", "UserPage--onLocationChanged---location="+location.getProvider());
		CustomCameraActivity.mlocation = location;
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_USER_INFO;
	}

	@Override
	public void onTitleBarClick(View v) {
		super.onTitleBarClick(v);
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_INDEX, null, null);
	}

	@Override
	public void goToMap() {
		super.goToMap();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btn_home:
				mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_INDEX, null,
						null);
			break;

			case R.id.user_btn_do_task:
				mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_INDEX, null,
						null);
			break;
			case R.id.user_btn_seeting_more:
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, 0, null, null);
			break;
			case R.id.user_btn_user_info:
			break;
			//创建子账号
			case R.id.text_CreateUser:
				
				mActivityInterface.showPage(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_CREATE_USER,
						Configs.VIEW_CREATE_USER, null, null);
				break;
				//我要计算
			case R.id.btn_count:
			    CustomToast.show(mContext,"此功能正在开发中，请等待");
				break;
			default:
			break;
		}

	}

	private void setUser(User user) {

		user_name.setText(user.getName());
		//String account = preferences.getString("accounts", "");
		String account = SharedPreferencesUtil.getUserName(mContext);
		user_account.setText(account);
		user_phone.setText(user.getPhone());
		user_all_earn.setText("¥" + user.getaEarn() + "");
		user_month_earn.setText("¥" + user.getmEarn() + "");
		//user_coll_day.setText(user.getcDay() + "天");
		user_coll_point.setText(user.getValid() + "条");
		user_coll_eff.setText(user.getPass() + "%");
		//新增未支付金额，已支付金额
		mText_user_notpay.setText("¥" + user.getNotPay() + "");
		mText_user_yetpay.setText("¥" + user.getYetPay() + "");
		
		mText_user_password.setText(SharedPreferencesUtil.getUserPassword(mContext));

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
			    case -1: //
			    	mActivityInterface.dismissNetDialog();
			    	mActivityInterface.ShowCodeDialog(msg.arg1, UserPage.this);
				break;
				case 0:
					mActivityInterface.dismissNetDialog();
					user = (User) msg.obj;
					setUser(user);
					//判断当前用户是否代理商
					int loginType = SharedPreferencesUtil.getLoginType(mContext);
					LogPrint.Print("loginType==="+loginType);
					if (loginType != -1 && loginType == 1) {
						
						mRelativeCreateUser.setVisibility(View.VISIBLE);
						mTextViewCreate_user.setText(Html.fromHtml("<u>"+"创建子账号"+"</u>"));
						mTextViewCreate_user.setOnClickListener(UserPage.this);
						
					}else {
						
						mRelativeCreateUser.setVisibility(View.GONE);
					}
				break;
				case 1:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "获取我的信息失败", Toast.LENGTH_SHORT).show();

				break;
				case 2:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "网络不给力，请检查网络", Toast.LENGTH_SHORT).show();

				break;

				default:
				break;
			}
		};
	};

	private void getUserInfo() {

		String url = UrlConfig.userUrl + "loginId="+StringUtils.getLoginId(mContext) + "&token=" + StringUtils.getLoginToken(mContext);
		LogPrint.Print("getUserInfo=="+url);
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {
		Message message = handler.obtainMessage();

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						String jsonStr = new String(data, "UTF-8");
						JSONObject jsonObject = new JSONObject(jsonStr);
						boolean b = (Boolean) jsonObject.getBoolean("result");
						if (b) {
							JSONObject jsonArray = jsonObject.getJSONObject("data");
							String valid = (String) jsonArray.get("ef");
							String earnM = (String) jsonArray.get("mr");
							String earnAll = (String) jsonArray.get("tr");
							String phone = (String) jsonArray.get("p");
							String colDay = (String) jsonArray.get("dn");
							String name = (String) jsonArray.get("un");
							String pass = (String) jsonArray.get("pr");
							//String id = (String) jsonObject.get("id");
							
							//已支付金额
							double yetPay = (Double)jsonArray.get("yetpay");
							//未支付金额
							double notPay = (Double)jsonArray.get("notpay");
							User user = new User();
							user.setaEarn(earnAll);
							user.setcDay(Integer.parseInt(colDay));
							user.setmEarn(earnM);
							user.setName(name);
							user.setPass(pass);
							user.setPhone(phone);
							user.setValid(Integer.parseInt(valid));
							
							user.setNotPay(String.valueOf(notPay));
							user.setYetPay(String.valueOf(yetPay));
							Message message = new Message();
							message.what = 0;
							message.obj = user;
							handler.sendMessage(message);
						} else {
							String msg = (String) jsonObject.get("message");
							int code = Integer.valueOf(msg);
							message.what = -1;
							message.arg1 = code;
							handler.sendMessage(message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(1);
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(1);
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

	/**
	 * 用户
	 * @author miaowei
	 *
	 */
	private class User {

		/**
		 * 用户手机号
		 */
		private String phone;
		/**
		 * 用户名
		 */
		private String name;
		/**
		 * 总收入
		 */
		private String aEarn;
		/**
		 * 当月收入
		 */
		private String mEarn;
		/**
		 * 采集天数
		 */
		private Integer cDay;
		/**
		 * 有效采集
		 */
		private Integer valid;
		/**
		 * 采集通过率
		 */
		private String pass;
		/**
		 * 未支付金额
		 */
		private String notPay;
		/**
		 * 已支付金额
		 */
		private String yetPay;
		
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getaEarn() {
			return aEarn;
		}
		public void setaEarn(String aEarn) {
			this.aEarn = aEarn;
		}
		public String getmEarn() {
			return mEarn;
		}
		public void setmEarn(String mEarn) {
			this.mEarn = mEarn;
		}
		public Integer getcDay() {
			return cDay;
		}
		public void setcDay(Integer cDay) {
			this.cDay = cDay;
		}
		public Integer getValid() {
			return valid;
		}
		public void setValid(Integer valid) {
			this.valid = valid;
		}
		public String getPass() {
			return pass;
		}
		public void setPass(String pass) {
			this.pass = pass;
		}
		public String getNotPay() {
			return notPay;
		}
		public void setNotPay(String notPay) {
			this.notPay = notPay;
		}
		public String getYetPay() {
			return yetPay;
		}
		public void setYetPay(String yetPay) {
			this.yetPay = yetPay;
		}

		
	}

}

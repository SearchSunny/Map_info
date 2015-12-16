package com.mapbar.info.collection.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;
/**
 * 登录页
 * @author miaowei
 *
 */
public class LoginPage extends BasePage implements View.OnClickListener, OnFocusChangeListener {
	private Context mContext;
	private ActivityInterface activityInterface;
	private EditText userName;
	private EditText passWord;
	private Button btn_sumit;
	private ImageView clear_account;
	private ImageView clear_password;
	private MHttpHandler httpHandler;
	private SharedPreferences preferences;
	private Editor editor;

	public LoginPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		activityInterface = aif;
		userName = (EditText) view.findViewById(R.id.userlogin_edit_account);
		passWord = (EditText) view.findViewById(R.id.userlogin_edit_pwd);
		btn_sumit = (Button) view.findViewById(R.id.userlogin_submit);
		btn_sumit.setOnClickListener(this);

		clear_account = (ImageView) view.findViewById(R.id.login_btn_clear_account);
		clear_password = (ImageView) view.findViewById(R.id.login_clear_password);
		clear_account.setOnClickListener(this);
		clear_password.setOnClickListener(this);

		userName.setOnFocusChangeListener(this);
		passWord.setOnFocusChangeListener(this);
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = preferences.edit();
		//String name = preferences.getString("accounts", "");
		//String psd = preferences.getString("passwords", "");
		  String name = SharedPreferencesUtil.getUserName(mContext);
		  String psd = SharedPreferencesUtil.getUserPassword(mContext);
		if (!name.equals("")) {
			userName.setText(name);
			passWord.setText(psd);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		super.onAttachedToWindow(flag, position);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_POSITION_LOGIN;
	}

	@Override
	public void onTitleBarClick(View v) {

		super.onTitleBarClick(v);
	}

	@Override
	public void goBack() {
		super.goBack();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.userlogin_submit: //登录
				String name = userName.getText().toString();
				String pwd = passWord.getText().toString();
				if (name == null || pwd == null || name.equals("") || pwd.equals("")) {
					Toast.makeText(mContext, "请输入用户名或密码", 0).show();
				} else {
					handler.sendEmptyMessage(4);
					String account=StringUtils.replaceBlank(name);
					String pssword=StringUtils.replaceBlank(pwd);
					new Thread(new LThread(account, pssword)).start();
				}
				hideSoft(v);
			break;
			case R.id.login_clear_password:
				passWord.setText("");
			break;
			case R.id.login_btn_clear_account:
				userName.setText("");
			break;

			default:
			break;
		}

	}

	private class LThread implements Runnable {
		String name;
		String pwd;

		public LThread(String n, String p) {
			name = n;
			pwd = p;
		}

		@Override
		public void run() {
			login(name, pwd);

		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId())
		{
			case R.id.userlogin_edit_account:
				clear_account.setVisibility(View.VISIBLE);
				clear_password.setVisibility(View.INVISIBLE);
			break;
			case R.id.userlogin_edit_pwd:
				clear_account.setVisibility(View.INVISIBLE);
				clear_password.setVisibility(View.VISIBLE);
			break;

			default:
			break;
		}
	}

	/**
	 * 保存当前登录用户名和密码
	 * @param bl
	 */
	private void show(boolean bl) {
		if (bl) {
			/*editor=preferences.edit();
			editor.putString("accounts", userName.getText().toString());
			editor.putString("passwords", passWord.getText().toString());
			editor.commit();*/
			SharedPreferencesUtil.saveUserName(mContext, userName.getText().toString());
			SharedPreferencesUtil.saveUserPassword(mContext, passWord.getText().toString());
			
			
			SharedPreferencesUtil.saveLoginID(mContext, Configs.LOGINID);
			SharedPreferencesUtil.saveLoginToken(mContext, Configs.TOKEN);
			//SharedPreferencesUtil.saveLoginSessionID(mContext, Configs.JSESSIONID);
			SharedPreferencesUtil.saveIsFirstLogin(mContext, false);
			SharedPreferencesUtil.saveFirstLoginDate(mContext, Util.getCurrentDate(new Date()));
			activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_INDEX,
					Configs.VIEW_POSITION_INDEX, null, null);
		} else {
			Toast.makeText(mContext, "登录失败", 0).show();
		}
	}

	// userType: 0公司1代理商2采集员
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			activityInterface.dismissNetDialog();
			switch (msg.what)
			{
				case 3:
					//show(true);
					if (msg.arg1 == 0) {
						
						Toast.makeText(mContext, "非法用户登录失败", Toast.LENGTH_SHORT).show();
						
					}else if (msg.arg1 == 1 || msg.arg1 == 2) {
						
						SharedPreferencesUtil.saveLoginType(mContext, msg.arg1);
						show(true);
					}
				break;
				case 4:
					activityInterface.showNetWaitDialog("登录中......");

				break;
				case 5:
					String m = (String) msg.obj;
					int code = Integer.valueOf(m);
					activityInterface.ShowCodeDialog(code,LoginPage.this);

				break;
				case 6:
					Toast.makeText(mContext, "登录失败", 0).show();
				break;
				case 7:
					Toast.makeText(mContext, "请连接网络", Toast.LENGTH_SHORT).show();
				break;
				default:
				break;
			}
		};
	};
	/**
	 * 登录服务请求
	 * @param name 用户名
	 * @param password 密码
	 */
	private void login(String name, String password) {
		if (Util.isNetworkAvailable(mContext)) {
			
			Configs.userName=name;
			LogPrint.Print("Configs.userName===="+Configs.userName);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			//String url = UrlConfig.loginUrl + "loginId=" + name + "&loginPwd=" + password + "&clientSource=2";
			String url = UrlConfig.loginUrl + "loginId=" + name + "&loginPwd=" + password;
			LogPrint.Print("LoginPage==="+url);
			HttpPost httpget = new HttpPost(url);
			HttpResponse response = null;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
				//handler.sendEmptyMessage(6);
			} catch (IOException e1) {
				e1.printStackTrace();
				//handler.sendEmptyMessage(6);
			}
			HttpEntity entity = null;
			try {

				entity = response.getEntity();
			} catch (Exception e) {
				e.printStackTrace();
				//handler.sendEmptyMessage(6);
			}
			InputStream inputStream = null;
			String str = null;

			try {
				/*if (response.getHeaders("Set-Cookie").length > 1) {

					Configs.TOKEN = response.getHeaders("Set-Cookie")[1].getValue().split(";")[0];
					Configs.JSESSIONID = response.getHeaders("Set-Cookie")[0].getValue().split(";")[0];
					editor.putString("token", Configs.TOKEN);
					editor.putString("jsessionid", Configs.JSESSIONID);
					editor.commit();
					
					
				} else {
					Configs.TOKEN = preferences.getString("token", "");
					Configs.JSESSIONID = preferences.getString("jsessionid", "");
				}*/

				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				//long size = response.getEntity().getContentLength();
				long size = 1024;
				inputStream = entity.getContent();
				byte[] buffer = new byte[(int) size];
				int len = inputStream.read(buffer);
				while (len != -1) {
					arrayOutputStream.write(buffer, 0, len);
					len = inputStream.read(buffer);
				}
				byte[] result = arrayOutputStream.toByteArray();
				str = new String(result);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				//handler.sendEmptyMessage(6);
			} catch (Exception e) {
				//handler.sendEmptyMessage(6);
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (str != null || !str.equals("")) {
					JSONObject jsonObject = new JSONObject(str);
					boolean bl = jsonObject.getBoolean("result");
					Message message = handler.obtainMessage();
					if (bl) {
						String token = jsonObject.getString("id");
						Configs.TOKEN = token;
						JSONObject dataJson = (JSONObject) jsonObject.get("data");
						LogPrint.Print("LoginPage===JSONObject="+dataJson);
						String loginId = dataJson.getString("loginId");
						if (!loginId.equals(preferences.getString("loginId", ""))) {
							editor.putString("loginId", loginId);
							editor.commit();
						}
						Configs.LOGINID = loginId;
						int type = dataJson.getInt("userType");
						message.arg1 = type;
						message.what = 3;
						handler.sendMessage(message);
					} else {
						String msg = jsonObject.getString("message");
						message.obj = msg;
						message.what = 5;
						handler.sendMessage(message);

					}
				} else {

					handler.sendEmptyMessage(6);
				}

			} catch (Exception e) {
				handler.sendEmptyMessage(6);
				e.printStackTrace();
			}
		}else {
			
			handler.sendEmptyMessage(7);
		}
		

	}

	/**
	 * 隐藏软键盘
	 * @param view
	 */
	private void hideSoft(View view) {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}

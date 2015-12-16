package com.mapbar.info.collection.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 修改密码页面
 * @author miaowei
 *
 */
public class AlertPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface mActivityInterface;

	private EditText editText_old;
	private EditText editText_new;
	private EditText editText_agin;

	private Button btn_clear;
	private Button btn_sumit;

	/**
	 * 修改密码页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public AlertPage(Context context, View view, ActivityInterface aif) {
		TitleBar bar = new TitleBar(context, view, this, aif);
		this.mContext = context;
		this.mActivityInterface = aif;

		editText_old = (EditText) view.findViewById(R.id.alert_pas_old);
		
		editText_new = (EditText) view.findViewById(R.id.alert_pas_new);
		editText_agin = (EditText) view.findViewById(R.id.alert_pas_new_new);
		btn_clear = (Button) view.findViewById(R.id.alert_btn_clear);
		btn_sumit = (Button) view.findViewById(R.id.alert_btn_confirm);
		btn_sumit.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		super.onAttachedToWindow(flag, position);
		editText_old.setText(SharedPreferencesUtil.getUserPassword(mContext));
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == event.KEYCODE_BACK) {
			goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, null, null);
		super.goBack();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
			case R.id.alert_btn_clear:
				//editText_old.setText("");
				editText_new.setText("");
				editText_agin.setText("");
				//editText_old.requestFocus();
			break;
			case R.id.alert_btn_confirm:
				if (editText_new.getText().toString().equals(editText_agin.getText().toString())) {
					if (editText_new.getText().toString() == null || editText_new.getText().toString().equals("")) {
						Toast.makeText(mContext, "新密码不能为空", 0).show();
					} else if (editText_old.getText().toString() == null
							|| editText_old.getText().toString().equals("")) {
						Toast.makeText(mContext, "密码不能为空", 0).show();
					} else {
						String old = StringUtils.replaceBlank(editText_old.getText().toString());
						String nu = StringUtils.replaceBlank(editText_new.getText().toString());
						alterPwd(old, nu);
					}
				} else {
					Toast.makeText(mContext, "新密码不一致，请重新输入", 0).show();
				}
			break;

			default:
			break;
		}

	}

	private HttpHandler httpHandler;

	// {"data":null,"id":"","message":"-1","result":true}
	private void alterPwd(String old, String newp) {
		mActivityInterface.showNetWaitDialog("正在修改......");
		String url = UrlConfig.alterPwdUrl + "loginId=" + StringUtils.getLoginId(mContext) + "&newCipher=" + newp + "&oldCipher=" + old
				+ "&token=" + StringUtils.getLoginToken(mContext);
		Log.e("url", "alterUrl=" + url);
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {

				if (data != null) {
					String str = new String(data);
					try {
						JSONObject jsonObject = new JSONObject(str);
						boolean b = jsonObject.getBoolean("result");
						if (b) {
							handler.sendEmptyMessage(success);
						} else {
							String stsr = jsonObject.getString("message");
							Message msg = handler.obtainMessage();
							msg.what = failed;
							msg.obj = stsr;
							handler.sendMessage(msg);
						}
					} catch (JSONException e) {
						handler.sendEmptyMessage(netWrong);
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(netWrong);
				}

			}
		});
		httpHandler.execute();
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_ALTER_PASSWORD;
	}

	private final int success = 0;
	private final int failed = 1;
	private final int netWrong = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case success:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "密码修改成功", 0).show();
					SharedPreferencesUtil.saveUserPassword(mContext, editText_new.getText().toString());
					goBack();
				break;
				case failed:
					mActivityInterface.dismissNetDialog();
					String str = (String) msg.obj;
					mActivityInterface.ShowCodeDialog(Integer.valueOf(str),AlertPage.this);
				break;
				case netWrong:
					Toast.makeText(mContext, "密码修改失败", 0).show();
					mActivityInterface.dismissNetDialog();
				break;

				default:
				break;
			}
		};
	};

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
       LogPrint.Print("gps", "AlertPage--onLocationChanged---location="+location.getProvider());
		
		super.onLocationChanged(location);
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
}

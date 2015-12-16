package com.mapbar.info.collection.ui;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.mapbar.info.collection.widget.SpinnerAdapter;

/**
 * 创建子账号页面
 * @author miaowei
 *
 */
public class CreateUserPage extends BasePage implements OnClickListener{

	private Context mContext;
	private ActivityInterface activityInterface;
	private TitleBar titleBar;
	/**
	 * 回退
	 */
	private ImageButton btn_back;
	/**
	 * 用户名
	 */
	private EditText edit_userNikeName;
	private String mUserNikeName;
	/**
	 * 姓名
	 */
	private EditText edit_userName;
	private String mEdit_userName;
	/**
	 * 密码
	 */
	private EditText edit_userPwd;
	/**
	 * 默认密码
	 */
	private String mEdit_userPwd = "123456";
	/**
	 * 邮箱
	 */
	private EditText edit_userEmail;
	private String mEdit_userEmail;
	/**
	 * 支付宝账号
	 */
	private EditText edit_userAlipay;
	private String mEdit_userAlipay;
	/**
	 * 手机号
	 */
	private EditText edit_userPhone;
	private String mEdit_userPhone;
	/**
	 * QQ号
	 */
	private EditText edit_userQQ;
	private String mEdit_userQQ;
	
	/**
	 * 性别
	 */
	private String[] mItems;
	private Spinner spinner_userGender;
	private SpinnerAdapter adapter; 
	/**
	 * 性别(0-男1-女)
	 */
	private int mUserGender_Source;
	/**
	 * 城市
	 */
	private EditText edit_userCity;
	private String mEdit_userCity;
	
	/**
	 * 确认button
	 */
	private Button button_userConfirm;
	/**
	 * 当前用户登录ID
	 */
	private  String mUserId;
	/**
	 * 当前用户登录类型  0公司1代理商2采集员
	 */
	private int mUserType;
	
	private SharedPreferences preferences;
	
	private MHttpHandler httpHandler;
	/**
	 * 创建子账号页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public CreateUserPage(Context context, View view, ActivityInterface aif) {
		
		this.mContext = context;
		activityInterface = aif;
		
		titleBar = new TitleBar(mContext, view, this, aif);
		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		
		mItems = mContext.getResources().getStringArray(R.array.mw_spinnername);
		
		edit_userNikeName = (EditText)view.findViewById(R.id.edit_userNikeName);
		edit_userName = (EditText)view.findViewById(R.id.edit_userName);
		edit_userPwd = (EditText)view.findViewById(R.id.edit_userPwd);
		edit_userEmail = (EditText)view.findViewById(R.id.edit_userEmail);
		edit_userAlipay = (EditText)view.findViewById(R.id.edit_userAlipay);
		edit_userPhone = (EditText)view.findViewById(R.id.edit_userPhone);
		edit_userQQ = (EditText)view.findViewById(R.id.edit_userQQ);
		
		spinner_userGender = (Spinner)view.findViewById(R.id.spiner_usergender);
		edit_userCity = (EditText)view.findViewById(R.id.edit_userCity);
		button_userConfirm = (Button)view.findViewById(R.id.button_userConfirm);
		
		//将可选内容与ArrayAdapter连接起来  
        adapter = new SpinnerAdapter(mContext, mItems);
        spinner_userGender.setAdapter(adapter);
        //添加事件Spinner事件监听    
        spinner_userGender.setOnItemSelectedListener(new SpinnerSelectedListener());  
        //设置默认值  
        spinner_userGender.setVisibility(View.VISIBLE);
		
		edit_userEmail.addTextChangedListener(textWatcher);
		button_userConfirm.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mUserId = preferences.getString("loginId", "") ;
		mUserType = SharedPreferencesUtil.getLoginType(mContext);
	}
	/**
	 * spinner监听器
	 * @author miaowei
	 *
	 */
	private class SpinnerSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			if (mItems[position].equals("男")) {
				
				mUserGender_Source = 0;
			}else {
				
				mUserGender_Source = 1;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
			
		}
		
		
	}
	/**
	 * 输入动作改变
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			/*if (s.toString().length() > 0) {
				if(edit_userEmail.getText().toString().indexOf("@") > 0&&edit_userEmail.getText().toString().indexOf(".") > 0){
					mEdit_userEmail = edit_userEmail.getText().toString().trim();
				}else{
					
					CustomToast.show(mContext, "邮箱格式不正确",3);
				}
			}*/
		}
	};
	@Override
	public void onAttachedToWindow(int flag, int position) {
		super.onAttachedToWindow(flag, position);
		
		
	}
	
	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_CREATE_USER;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void goBack() {
		super.goBack();
		activityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_USER_INFO, null, null);
	}

	/**
	 * 清除基本信息
	 */
	private void clearInfo(){
		edit_userNikeName.setText("");
		edit_userName.setText("");
		edit_userEmail.setText("");
		edit_userAlipay.setText("");
		edit_userPhone.setText("");
		edit_userQQ.setText("");
		edit_userCity.setText("");
	}
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_home:
			goBack();
			break;
			//确认
		case R.id.button_userConfirm:
			if (checkInput()) {
				LogPrint.Print(checkInput()+"");
				LogPrint.Print("创建子账号信息完整=========");
				activityInterface.showNetWaitDialog("创建账号中......");
				new Thread(new CreateUserThread()).start();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 检查用户输入项
	 */
	private boolean checkInput(){
		
		if (StringUtils.isNullColums(edit_userNikeName.getText().toString())) {
			
			CustomToast.show(mContext, "请输入用户名", 3);
			edit_userNikeName.requestFocus();
			return false;
		}else if (StringUtils.isNullColums(edit_userName.getText().toString())) {
			
			CustomToast.show(mContext, "请输入姓名", 3);
			edit_userName.requestFocus();
			return false;
		}/*else if (StringUtils.isNullColums(edit_userPwd.getText().toString())) {
			
			CustomToast.show(mContext, "请输入密码", 3);
			edit_userPwd.requestFocus();
			return false;
		}else if (StringUtils.isNullColums(edit_userEmail.getText().toString())) {
			
			CustomToast.show(mContext, "请输入邮箱", 3);
			edit_userEmail.requestFocus();
			return false;
		}*/else if (StringUtils.isNullColums(edit_userAlipay.getText().toString())) {
			
			CustomToast.show(mContext, "请输入支付宝账号", 3);
			edit_userAlipay.requestFocus();
			return false;
			
		}else if (StringUtils.isNullColums(edit_userPhone.getText().toString())) {
			
			CustomToast.show(mContext, "请输入手机号", 3);
			edit_userPhone.requestFocus();
			return false;
		}else if (StringUtils.isNullColums(edit_userQQ.getText().toString())) {
			
			CustomToast.show(mContext, "请输入QQ号", 3);
			edit_userQQ.requestFocus();
			return false;
		}else if (StringUtils.isNullColums(edit_userCity.getText().toString())) {
			
			CustomToast.show(mContext, "请输入城市", 3);
			edit_userCity.requestFocus();
			return false;
			
		} else {
			if (!StringUtils.isNullColums(edit_userEmail.getText().toString())) {
				if(edit_userEmail.getText().toString().indexOf("@") > 0&&edit_userEmail.getText().toString().indexOf(".") > 0){
					
					mEdit_userEmail = edit_userEmail.getText().toString().trim();
					
				}else{
					
					CustomToast.show(mContext, "邮箱格式不正确",3);
					edit_userEmail.requestFocus();
					return false;
				}
			}
			if (!StringUtils.isNullColums(edit_userPhone.getText().toString())) {
				
				if (edit_userPhone.getText().toString().contains("-")) {
					
					CustomToast.show(mContext, "请正确输入手机号码",3);
					edit_userPhone.requestFocus();
					return false;
					
				}else if (edit_userPhone.getText().toString().length() < 11 || edit_userPhone.getText().toString().length() > 11) {
					
					CustomToast.show(mContext, "手机号码位数不正确",3);
					edit_userPhone.requestFocus();
					return false;
					
				}else {
					
					mEdit_userPhone = edit_userPhone.getText().toString().trim();
				}
				
			}
			if (!StringUtils.isNullColums(edit_userQQ.getText().toString())) {
				
				if (edit_userQQ.getText().toString().contains("-")) {
					
					CustomToast.show(mContext, "请正确输入QQ号码",3);
					edit_userQQ.requestFocus();
					return false;
					
				}else {
					
					mEdit_userQQ = edit_userQQ.getText().toString().trim();
				}
			}
			mUserNikeName = edit_userNikeName.getText().toString().trim();
			mEdit_userName = edit_userName.getText().toString().trim();
			//mEdit_userPwd = edit_userPwd.getText().toString().trim();
			mEdit_userAlipay = edit_userAlipay.getText().toString().trim();
			mEdit_userCity = edit_userCity.getText().toString().trim();
			return true;
		}
	}
	/**
	 * 
	 * @author miaowei
	 *
	 */
	private class CreateUserThread implements Runnable{

		@Override
		public void run() {
			
			getCreateUserResult();
		}
		
	}
	/**
	 * 获取创建子账号结果
	 */
	private void getCreateUserResult() {

		//检查网络链接是否成功
		if (Util.isNetworkAvailable(mContext)){
			String url = UrlConfig.URL_GET_CREATEUSER_URL + "token="+StringUtils.getLoginToken(mContext);
			LogPrint.Print("getCreateUserResult==="+url);
			Message message = handler.obtainMessage();
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type","application/json");
			request.setHeader("charset", "UTF-8");
			HttpResponse response = null;
			try {
				// 先封装一个 JSON 对象  
				JSONObject map = new JSONObject();  
				map.put("loginId", mUserNikeName);
				map.put("sex", mUserGender_Source);
				map.put("firstName", mEdit_userName);
				map.put("email", !StringUtils.isNullColums(mEdit_userEmail)?mEdit_userEmail:"");
				map.put("alipay", mEdit_userAlipay);
				map.put("mobile", mEdit_userPhone);
				map.put("qq", mEdit_userQQ);
				map.put("city", mEdit_userCity); 
				// 绑定到请求 Entry  
				StringEntity se = new StringEntity(map.toString(),"utf-8"); 
				se.setContentEncoding("utf-8");
				request.setEntity(se);  
				// 发送请求  
				response = httpclient.execute(request);
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据  
				String retSrc = EntityUtils.toString(response.getEntity());  
				// 生成 JSON 对象  
				JSONObject result = new JSONObject(retSrc);
				boolean b = result.getBoolean("result");
				String code = result.getString("message");
				if (b) {
					message.what = 0;
					handler.sendMessage(message);
				} else {
					message.what = 4;
					message.arg1 = Integer.parseInt(code); 
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(1);
				e.printStackTrace();
			}
		}else {
			
			handler.sendEmptyMessage(3);
		}
		

	}



	/**
	 * 返回信息处理
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				//成功
				case 0:
					activityInterface.dismissNetDialog();
					//Toast.makeText(mContext, "创建子账号成功", Toast.LENGTH_SHORT).show();
					clearInfo();
					goBack();
					copyFromEditText1();
				break;
				//失败
				case 1:
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "创建子账号失败", Toast.LENGTH_SHORT).show();
				break;
				case 2:
					activityInterface.dismissNetDialog();
					Toast.makeText(mContext, "网络不给力，请检查网络", Toast.LENGTH_SHORT).show();
				break;
				//网络未连接
				case 3:
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_SHORT).show();
				break;
				case 4:
					activityInterface.dismissNetDialog();
					activityInterface.ShowCodeDialog(msg.arg1,CreateUserPage.this);
					break;
				default:
				break;
			}
		};
	};
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		
       LogPrint.Print("gps", "CreateUserPage--onLocationChanged---location="+location.getProvider());
		
		super.onLocationChanged(location);
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
	
	private ClipboardManager mClipboard = null;
	/**
	 * 剪贴板复制
	 */
	private void copyFromEditText1() {

        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        String userInfoString = "用户名："+mUserNikeName+"\n"+
                "姓   名："+mEdit_userName+"\n"+
        		"密   码："+mEdit_userPwd+"\n"+
                "支付宝："+mEdit_userAlipay+"\n"+
        		"手机号："+mEdit_userPhone+"\n"+
                "Q  Q："+mEdit_userQQ+"\n"+
        		"城  市："+mEdit_userCity+"\n";
        StringBuilder sbBuilder = new StringBuilder();
        sbBuilder.append(userInfoString);
        if (!StringUtils.isNullColums(mEdit_userEmail)) {
        	sbBuilder.append("邮  箱："+edit_userEmail.getText());
		}
        // Creates a new text clip to put on the clipboard
        
        ClipData clip = ClipData.newPlainText("simple text",sbBuilder.toString());

        // Set the clipboard's primary clip.
        mClipboard.setPrimaryClip(clip);
        
        Toast.makeText(mContext, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

}

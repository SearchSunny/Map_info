package com.mapbar.info.collection;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.ui.AboutPage;
import com.mapbar.info.collection.ui.AdHelpPage;
import com.mapbar.info.collection.ui.AlertPage;
import com.mapbar.info.collection.ui.BasePage;
import com.mapbar.info.collection.ui.CameraPage;
import com.mapbar.info.collection.ui.ColPointPgae;
import com.mapbar.info.collection.ui.CreateUserPage;
import com.mapbar.info.collection.ui.CustomCameraActivity;
import com.mapbar.info.collection.ui.DetailOldItemPage;
import com.mapbar.info.collection.ui.DetailOldPage;
import com.mapbar.info.collection.ui.DetailPage;
import com.mapbar.info.collection.ui.IntroducePage;
import com.mapbar.info.collection.ui.IntroducePageHelp;
import com.mapbar.info.collection.ui.LoginPage;
import com.mapbar.info.collection.ui.MTaskPage;
import com.mapbar.info.collection.ui.MainPage;
import com.mapbar.info.collection.ui.MorePage;
import com.mapbar.info.collection.ui.NewTaskPage;
import com.mapbar.info.collection.ui.NumenPage;
import com.mapbar.info.collection.ui.UserPage;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;

/**
 * 
 * @author rock
 * 
 */
@SuppressLint("InflateParams")
public class MainActivity extends Activity implements ActivityInterface {

	private ViewAnimator mViewAnimator;
	/**
	 * 初始化View操作对象
	 */
	private Hashtable<Integer, BasePage> mHt_PageActs = new Hashtable<Integer, BasePage>();
	/**
	 * 初始化View
	 */
	private Hashtable<Integer, View> mHt_PageViews = new Hashtable<Integer, View>();
	private int mCurrentPageIndex = Configs.VIEW_POSITION_LOGIN;
	private boolean isCanExit = false;
	private boolean isFinishInit = false;
	private LayoutInflater mInflater;
	/**
	 * 任务详情
	 */
	private TaskDetail mTask;
	private ProgressDialog checkProgressDialog;
	private Editor editor;
	private TaskDetail oldTask;
	/**
	 * 软件更新
	 */
	private SoftUpdateManager softUpdateManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		LogPrint.isPrintLogMsg(true);
		initView();
		SdcardUtil.initInstance(this);
		softUpdateManager = new SoftUpdateManager(this);
		
		DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	Configs.WIDTH = dm.widthPixels;
    	Configs.HEIGHT = dm.heightPixels;
    	LogPrint.Print("dm=screen_width = "+Configs.WIDTH);
    	LogPrint.Print("dm=screen_height = "+Configs.HEIGHT);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		//软件更新
		//1.判断网络是否有效
		if (Util.isNetworkAvailable(this)) {
			
			//2.判断SD卡容量 小于10M提示用户
			if (SdcardUtil.getSdcardAvailableSize() < 10) {
				
				CustomToast.show(this, "您的存储空间已不足");
				
			}else {
				
				softUpdateManager.httpRequest(UrlConfig.URL_SOFT_UPDATA_CMMOBI);
			}
			
		}else {
			
			CustomToast.show(this, "请检查网络连接");
		}
		
	}

	private void initView() {

		mViewAnimator = (ViewAnimator) findViewById(R.id.animator);
		mInflater = LayoutInflater.from(this);

		CustomCameraActivity.SetActivityInterface(this);
		View loginView = mInflater.inflate(R.layout.layout_login, null);
		mViewAnimator.addView(loginView);
		mHt_PageViews.put(Configs.VIEW_POSITION_LOGIN, loginView);
		mHt_PageActs.put(Configs.VIEW_POSITION_LOGIN, new LoginPage(this, loginView, this));

		View mainView = mInflater.inflate(R.layout.layout_index, null);
		mViewAnimator.addView(mainView);
		mHt_PageViews.put(Configs.VIEW_POSITION_INDEX, mainView);
		mHt_PageActs.put(Configs.VIEW_POSITION_INDEX, new MainPage(this, mainView, this));

		View collView = mInflater.inflate(R.layout.layout_collection_point, null);
		mViewAnimator.addView(collView);
		mHt_PageViews.put(Configs.VIEW_POSITION_TASK_COL_POINT, collView);
		mHt_PageActs.put(Configs.VIEW_POSITION_TASK_COL_POINT, new ColPointPgae(this, collView, this));

		View userView = mInflater.inflate(R.layout.layout_user, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_USER_INFO, userView);
		mHt_PageActs.put(Configs.VIEW_POSITION_USER_INFO, new UserPage(this, userView, this));
		
		View createUserView = mInflater.inflate(R.layout.layout_create_user, null);
		mHt_PageViews.put(Configs.VIEW_CREATE_USER, createUserView);
		mHt_PageActs.put(Configs.VIEW_CREATE_USER, new CreateUserPage(this, createUserView, this));
		

		View moreView = mInflater.inflate(R.layout.layout_more, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_MORE, moreView);
		mHt_PageActs.put(Configs.VIEW_POSITION_MORE, new MorePage(this, moreView, this));

		View newTaskView = mInflater.inflate(R.layout.layout_new_task, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_NEW_TASK, newTaskView);
		mHt_PageActs.put(Configs.VIEW_POSITION_NEW_TASK, new NewTaskPage(this, newTaskView, this));

		View cameraTask = mInflater.inflate(R.layout.layout_camera, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_CAMERA, cameraTask);
		mHt_PageActs.put(Configs.VIEW_POSITION_CAMERA, new CameraPage(this, cameraTask, this));

		View myTaskView = mInflater.inflate(R.layout.layout_my_task, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_MY_TASK, myTaskView);
		mHt_PageActs.put(Configs.VIEW_POSITION_MY_TASK, new MTaskPage(this, myTaskView, this));

		View aboutView = mInflater.inflate(R.layout.layout_about, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_ABOUT, aboutView);
		mHt_PageActs.put(Configs.VIEW_POSITION_ABOUT, new AboutPage(this, aboutView, this));

		View alertView = mInflater.inflate(R.layout.layout_alert_password, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_ALTER_PASSWORD, alertView);
		mHt_PageActs.put(Configs.VIEW_POSITION_ALTER_PASSWORD, new AlertPage(this, alertView, this));

		View detailView = mInflater.inflate(R.layout.layout_detail, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_DETAIL, detailView);
		mHt_PageActs.put(Configs.VIEW_POSITION_DETAIL, new DetailPage(this, detailView, this));

		View oldDetailView = mInflater.inflate(R.layout.layout_old_task_detail, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_OLD_DETAIL, oldDetailView);
		mHt_PageActs.put(Configs.VIEW_POSITION_OLD_DETAIL, new DetailOldPage(this, oldDetailView, this));

		View oldDetailItemView = mInflater.inflate(R.layout.layout_old_task_item_detail, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_OLD_ITEM_DETAIL, oldDetailItemView);
		mHt_PageActs.put(Configs.VIEW_POSITION_OLD_ITEM_DETAIL, new DetailOldItemPage(this, oldDetailItemView, this));

		View numenView = mInflater.inflate(R.layout.layout_numen, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_NUMEN, numenView);
		mHt_PageActs.put(Configs.VIEW_POSITION_NUMEN, new NumenPage(this, numenView, this));

		View introdueView = mInflater.inflate(R.layout.layout_introduce_img, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_INTRODUCE_IMG, introdueView);
		mHt_PageActs.put(Configs.VIEW_POSITION_INTRODUCE_IMG, new IntroducePage(this, introdueView, this));

		View introdueImgView = mInflater.inflate(R.layout.layout_introduce_img_view, null);
		mHt_PageViews.put(Configs.VIEW_POSITION_INTRODUCE_HELP, introdueImgView);
		mHt_PageActs.put(Configs.VIEW_POSITION_INTRODUCE_HELP, new IntroducePageHelp(this, introdueImgView, this));

		
		View adHelppape = mInflater.inflate(R.layout.layout_adhelppage, null);
		mHt_PageViews.put(Configs.VIEW_AD_HELP, adHelppape);
		mHt_PageActs.put(Configs.VIEW_AD_HELP, new AdHelpPage(this, adHelppape, this));
		//是否第一次登录
		if (SharedPreferencesUtil.getIsFirstLogin(getApplicationContext())) {
			
			//SharedPreferencesUtil.saveIsFirstLogin(getApplicationContext(), false);
			//SharedPreferencesUtil.saveFirstLoginDate(getApplicationContext(), Util.getCurrentDate(new Date()));
			//默认广告引导页面
			int position = Configs.VIEW_AD_HELP;

			mHt_PageActs.get(position).onAttachedToWindow(Configs.VIEW_FLAG_NONE, Configs.VIEW_FLAG_NONE);

			showPage(-1, null, position, 0, null, null);
			
		}else {
			
			String currentDateString = Util.getCurrentDate(new Date());
			String firstLoginDate = SharedPreferencesUtil.getFirstLoginDate(getApplicationContext());
			//对比时间是否超过24小时
			long minute = Util.getMinuteTime(currentDateString, firstLoginDate);
			//如果超过24小时(1天)，进入广告引导页面
			if (minute > 1440) {
				
				//默认广告引导页面
				int position = Configs.VIEW_AD_HELP;

				mHt_PageActs.get(position).onAttachedToWindow(Configs.VIEW_FLAG_NONE, Configs.VIEW_FLAG_NONE);

				showPage(Configs.VIEW_FLAG_NONE, null, position, 0, null, null);
				
			}
			else {
				/*if (Util.isNetworkAvailable(getApplicationContext())) {
					
					//默认为登录页面
					int position = Configs.VIEW_POSITION_LOGIN;

					mHt_PageActs.get(position).onAttachedToWindow(Configs.VIEW_FLAG_NONE, Configs.VIEW_FLAG_NONE);

					showPage(0, null, position, 0, null, null);
					
				}else{
					
					//默认为登首页面
					int position = Configs.VIEW_POSITION_INDEX;

					mHt_PageActs.get(position).onAttachedToWindow(Configs.VIEW_POSITION_LOGIN, Configs.VIEW_FLAG_NONE);

					showPage(0, null, position, Configs.VIEW_FLAG_NONE, null, null);
				}*/
				
				//默认为登首页面
				int position = Configs.VIEW_POSITION_INDEX;

				mHt_PageActs.get(position).onAttachedToWindow(Configs.VIEW_POSITION_LOGIN, Configs.VIEW_FLAG_NONE);

				showPage(0, null, position, Configs.VIEW_FLAG_NONE, null, null);
					
					
				
			}
		}

		isFinishInit = true;
	}

	@Override
	public void showPage(int flag, LayoutInterface lif, int index, int position, Animation in, Animation out) {
		setCurrentPage(lif, index, in, out, 0);
		//100有点特殊表示是从自定义Camera过来的
		if (position == 100) {
			
			mHt_PageActs.get(mCurrentPageIndex).onOldTaskDetail(oldTask);
			mHt_PageActs.get(mCurrentPageIndex).setDetailItem(mTask);
			
		    mHt_PageActs.get(mCurrentPageIndex).setDataBridge(dataBridge);
		    mHt_PageActs.get(mCurrentPageIndex).onAttachedToWindow(flag, position);

		}
		if (lif == null)
			return;
			
		mHt_PageActs.get(mCurrentPageIndex).onOldTaskDetail(oldTask);
		mHt_PageActs.get(mCurrentPageIndex).setDetailItem(mTask);
		if (flag != Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA)
			mHt_PageActs.get(mCurrentPageIndex).setDataBridge(dataBridge);
		mHt_PageActs.get(mCurrentPageIndex).onAttachedToWindow(flag, position);
		lif.onDetachedFromWindow(flag);
		if (flag == Configs.VIEW_POSITION_MY_OLD)
			mHt_PageActs.get(mCurrentPageIndex).onOldTaskPoint(oldTaskPoint);
	}

	/**
	 * 在showPage 前调用
	 */
	@Override
	public void setData(TaskDetail task) {
		this.mTask = task;
	}

	/**
	 * 显示当前VIEW
	 * @param lif
	 * @param index 
	 * @param in
	 * @param out
	 * @param k 
	 */
	private void setCurrentPage(LayoutInterface lif, int index, Animation in, Animation out, int k) {
		isCanExit = false;
		View curView = mHt_PageViews.get(mCurrentPageIndex);
		View nextView = mHt_PageViews.get(index);
		int count = mViewAnimator.getChildCount();
		if (count > 0) {
			for (int i = count - 1; i >= 0; i--) {
				View child = mViewAnimator.getChildAt(i);
				if (child.equals(curView) || child.equals(nextView)) {
					continue;
				}
				mViewAnimator.removeView(child);
			}
		}
		int targetIndex = mViewAnimator.indexOfChild(nextView);
		if (targetIndex == -1) {
			mViewAnimator.addView(nextView);
			targetIndex = mViewAnimator.getChildCount() - 1;
		}
		if (in != null)
			mViewAnimator.setInAnimation(in);
		if (out != null)
			mViewAnimator.setOutAnimation(out);

		curView.clearFocus();
		// nextView.requestFocusFromTouch();

		mViewAnimator.setDisplayedChild(targetIndex);
		mCurrentPageIndex = index;
	}

	/**
	 * 
	 */
	@Override
	public void showPrevious(int flag, LayoutInterface lif, int index, Animation in, Animation out) {
		setCurrentPage(lif, index, in, out, 1);
		lif.onDetachedFromWindow(flag);
		mHt_PageActs.get(mCurrentPageIndex).onOldTaskDetail(oldTask);
		mHt_PageActs.get(mCurrentPageIndex).setDetailItem(mTask);
		mHt_PageActs.get(mCurrentPageIndex).onImageName(imageName);
		mHt_PageActs.get(mCurrentPageIndex).onAttachedToWindow(flag, -1);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isFinishInit)
				return true;
			if (mHt_PageActs.get(mCurrentPageIndex).onKeyDown(keyCode, event)) {
				return true;
			}
			if (!isCanExit) {
				mHandler.sendEmptyMessageDelayed(1, 10000);
				isCanExit = true;
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public Vector getMyChannels() {

		return null;
	}

	@Override
	public Location getMyLocation() {

		return null;
	}

	@Override
	public void refresh() {

	}

	private String urls;

	@Override
	public void onLocationChangedFromGps(Location location) {
		
		if (location.getProvider().equals("cell")) {
			locType = "02";
		} else if (location.getProvider().equals("gps")) {
			locType = "84";
		}

		urls = UrlConfig.uplodeGpsUr + "uid=" + StringUtils.getLoginId(getApplicationContext()) + "&longitude="
				+ location.getLongitude() + "&latitude=" + location.getLatitude() + "&token=" + StringUtils.getLoginToken(getApplicationContext()) + "&f="
				+ locType;
		
		startUploadGps();
		LogPrint.Print("console","MainActivity-mCurrentPageIndex="+mCurrentPageIndex);
		if (mCurrentPageIndex != Configs.VIEW_POSITION_NEW_TASK){
			
			mHt_PageActs.get(Configs.VIEW_POSITION_NEW_TASK).onLocationChanged(location);
		}
			
		mHt_PageActs.get(mCurrentPageIndex).onLocationChanged(location);
	}

	private Timer timer;

	/**
	 * 上传GPS经纬度等信息，每间隔30000ms执行
	 */
	private void startUploadGps() {
		if (timer == null) {
			timer = new Timer();

			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					mHandler.sendEmptyMessage(0);
				}
			}, Configs.UPBETEEWN, Configs.UPBETEEWN);
		}
	}

	@Override
	public void setPoiPosition(int position) {

	}

	@Override
	public int getPoiPosition() {
		return 0;
	}

	@Override
	public void setCurChannelPosition(int position) {

	}

	@Override
	public int getCurChannelPosition() {
		return 0;
	}

	@Override
	public String getCurrentCity() {

		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		if (StringUtils.getLoginToken(getApplicationContext()) != null) {
			String url = UrlConfig.userUrl + "loginId=" + StringUtils.getLoginId(getApplicationContext()) + "&token=" + StringUtils.getLoginToken(getApplicationContext());
			exulreUrl(url);
		}
		if (timer != null)
			timer.cancel();
		timer = null;
		SdcardUtil.unInitInstance();
		mHt_PageActs.get(mCurrentPageIndex).onDestroy();
		super.onDestroy();
	}

	@Override
	public void StartActivityForFlag(Intent intent, int flag) {

		startActivityForResult(intent, flag);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHt_PageActs.get(mCurrentPageIndex).onResult(requestCode, resultCode, data);
	}

	
	protected Dialog onCreateDialog(final int id,final LayoutInterface layoutInterface) {
		String str = null;
		switch (id)
		{
			case Configs.LOGINFIAD:
				str = getResources().getString(R.string.code_f1);
			break;
			case Configs.LOGINFIAD_F:
				str = getResources().getString(R.string.code_f2);
			break;
			case Configs.SUCCESS:
				str = getResources().getString(R.string.code_1);
			break;
			case Configs.NO_LOGIN:
				str = getResources().getString(R.string.code_2);
			break;
			case Configs.PARAMRS_ERROR:
				str = getResources().getString(R.string.code_3);
			break;
			case Configs.NO_PERMISSION:
				str = getResources().getString(R.string.code_4);
			break;
			case Configs.IDORPWD_NULL:
				str = getResources().getString(R.string.code_5);
			break;
			case Configs.IDORPWD_ERROR:
				str = getResources().getString(R.string.code_6);
			break;
			case Configs.ACCOUNT_LOST:
				str = getResources().getString(R.string.code_7);
			break;
			case Configs.ACCOUNT_NO:
				str = getResources().getString(R.string.code_8);
			break;
			case Configs.CREATE_USER_REPEAT:
				str = getResources().getString(R.string.code_9);
				break;
			case Configs.ACCOUNT_LOSTED:
				str = getResources().getString(R.string.code_1011);
			break;
			case Configs.TASK_NO_EXISTS:
				str = getResources().getString(R.string.code_201);
			break;
			case Configs.TASK_STATE_ERROR:
				str = getResources().getString(R.string.code_204);
			break;
			case Configs.TASK_NO_ID:
				str = getResources().getString(R.string.code_207);
			break;
			case Configs.DETAIL_BACK_NULL:
				str = getResources().getString(R.string.code_208);
			break;
			case Configs.TASK_RESULT_NULL:
				str = getResources().getString(R.string.code_209);
			break;
			case Configs.SEARCH_TASK_TOTAL:
				str = getResources().getString(R.string.code_2012);
			break;
			case Configs.UDATATE_NULL:
				str = getResources().getString(R.string.code_4020);
			break;
			case Configs.UPDATE_PWD_NULL:
				str = getResources().getString(R.string.code_4021);
			break;
			case Configs.NOW_PWD:
				str = getResources().getString(R.string.code_4023);
			break;
			case Configs.NOW_PWD_ERROR:
				str = getResources().getString(R.string.code_4024);
			break;
			case Configs.U_E:
				str = getResources().getString(R.string.code_4025);
			break;
			case Configs.U_C_E:
				str = getResources().getString(R.string.code_403);
			break;
			case Configs.EX_ID_NULL:
				str = getResources().getString(R.string.code_701);
			break;
			case Configs.N_F_X:
				str = getResources().getString(R.string.code_501);
			break;
			case Configs.CAMERA_ID_NULL:
				str = getResources().getString(R.string.code_301);
			break;
			case Configs.CAMERA_NULL:
				str = getResources().getString(R.string.code_302);
			break;
			case Configs.ERROR:
				str = getResources().getString(R.string.code_801);
			break;
			case Configs.TASK_IS_EXCEED:
				str = getResources().getString(R.string.code_210);
			break;
			case Configs.TASK_IS_ONE:
				str = getResources().getString(R.string.code_211);
			break;
			case Configs.TASK_QZ_ERROR:
				str = getResources().getString(R.string.code_212);
			break;
			case Configs.TASK_QZ_OTHER_HANDLE:
				str = getResources().getString(R.string.code_213);
			break;

			default:
			break;
		}
		if (str == null || str.equals("")){
			
			return null;
		}
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.setContentView(R.layout.layout_dialog_code);
		dialog.setCancelable(false);

		TextView text = (TextView) dialog.findViewById(R.id.error_text);
		text.setText(str);
		dialog.findViewById(R.id.error_confirm).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//token失效
				if (id == -1) {
					//清除token、cookie
					editor.putString("token", "");
					editor.putString("jsessionid", "");
					editor.commit();
					SharedPreferencesUtil.saveLoginToken(getApplicationContext(), "");
					SharedPreferencesUtil.saveLoginSessionID(getApplicationContext(), "");
					MainActivity.this.showPrevious(Configs.VIEW_FLAG_NONE,layoutInterface, Configs.VIEW_POSITION_LOGIN, null, null);
				}
				dialog.cancel();
			}
		});

		return dialog;
	}

	@Override
	public void ShowCodeDialog(int code,LayoutInterface layoutInterface) {
		onCreateDialog(code,layoutInterface);
	}

	/**
	 * 显示等待框
	 * @param msg 提示文字
	 */
	public void shoProgressDialog(String msg) {

		if (checkProgressDialog != null && checkProgressDialog.isShowing())
			return;
		if (checkProgressDialog == null) {
			checkProgressDialog = new ProgressDialog(this);
		}

		checkProgressDialog.setMessage(msg);
		checkProgressDialog.setCancelable(true);
		checkProgressDialog.setCanceledOnTouchOutside(false);
		checkProgressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent evnet) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					/*if (checkProgressDialog != null&&mCurrentPageIndex!=Configs.VIEW_POSITION_NEW_TASK)
						checkProgressDialog.dismiss();
					return true;*/
					//解决在点击附近时显示数据加载中提示框一直显示的情况 2014-10-14
					if (checkProgressDialog != null){
						
						checkProgressDialog.dismiss();
					}
						
				} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		});
		checkProgressDialog.setCancelable(false);
		checkProgressDialog.setCanceledOnTouchOutside(false);
		checkProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		checkProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				checkProgressDialog = null;
			}
		});
		checkProgressDialog.show();

	}

	@Override
	public void showNetWaitDialog(String msg) {
		shoProgressDialog(msg);
	}

	@Override
	public void dismissNetDialog() {
		
		if (checkProgressDialog != null && checkProgressDialog.isShowing()) {
			checkProgressDialog.dismiss();
			checkProgressDialog = null;
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 0:
					uploadGps(urls);
				break;
				case 1:
					isCanExit = false;
				break;

				default:
				break;
			}

		};
	};
	private SharedPreferences preferences;
	private HttpHandler handler;
	private HttpHandler up_handler;
	private String locType = "02";

	/**
	 * 
	 * @param url
	 */
	public void uploadGps(String url) {
		up_handler = new MHttpHandler(this);
		up_handler.setRequest(url, HttpRequestType.POST);
		LogPrint.Print("gpsupload","gpsuploadUrl=="+urls);
		up_handler.setCache(CacheType.NOCACHE);
		up_handler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				try {
					String jsonStr = new String(data, "UTF-8");
					JSONObject jsonObject = new JSONObject(jsonStr);

					boolean b = jsonObject.getBoolean("result");
					if (b) {
						LogPrint.Print("gpsupload","上传经纬度成功");
					} else {
						LogPrint.Print("gpsupload","上传经纬度失败");
						String msg = jsonObject.getString("message");
						ShowCodeDialog(Integer.valueOf(msg),null);
						
					}

				} catch (UnsupportedEncodingException e) {
					Toast.makeText(getApplication(), "上传经纬度失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		up_handler.execute();
	}

	public void exulreUrl(String url) {
		handler = new MHttpHandler(this);
		handler.setRequest(url, HttpRequestType.POST);
		handler.setCache(CacheType.NOCACHE);
		handler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				try {
					String jsonStr = new String(data, "UTF-8");
					JSONObject jsonObject = new JSONObject(jsonStr);

					boolean b = jsonObject.getBoolean("result");
					if (b) {

					} else {
						// String msg = jsonObject.getString("message");
						// ShowCodeDialog(Integer.valueOf(msg));
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		handler.execute();
	}

	private Object dataBridge;
	/**
	 * 图片名称
	 */
	private String imageName;
	private TaskPoint oldTaskPoint;

	@Override
	public void dataBridge(Object object) {
		dataBridge = object;
	}

	@Override
	public void setImageName(String name) {
		this.imageName = name;
	}

	@Override
	public void setOldTaskPiint(TaskPoint point) {
		oldTaskPoint = point;
	}

	@Override
	public void setOldTaskDetail(TaskDetail detail) {
		oldTask = detail;
	}

	public void setLocation(Location location) {
		CameraPage cameraPage = (CameraPage) mHt_PageActs.get(Configs.VIEW_POSITION_CAMERA);
		cameraPage.setCameraShow(location);
	}

	@Override
	public boolean isNetDialogisShowing() {
		boolean b = false;
		if (checkProgressDialog != null)
			b = checkProgressDialog.isShowing();
		return b;
	}

}

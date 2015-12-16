package com.mapbar.info.collection.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mapbar.android.location.CellLocationProvider;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.MyLocation;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.ad.AdNoticeAdapter;
import com.mapbar.info.collection.ad.AdNoticeView;
import com.mapbar.info.collection.bean.ImageAD;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.MessageID;
/**
 * 登录后的主页面
 * @author miaowei
 *
 */
public class MainPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface activityInterface;
	/**
	 * 我的任务
	 */
	private Button btn_my_task;
	/**
	 * 随手拍
	 */
	private Button the_about_task;
	/**
	 * 信息采集点
	 */
	private Button btn_coll_point;
	/**
	 * 做任务
	 */
	private Button btn_do_task;
	/**
	 * 我的信息
	 */
	private Button btn_user_info;
	/**
	 * 更多
	 */
	private Button btn_more;
	private TitleBar titleBar;
	private ImageButton button_back;
	private MyLocation location;
	private CellLocationProvider cellLocationProvider;
	/**
	 * 广告图片
	 */
	private AdNoticeView adview;
	
    AdNoticeAdapter adAdapter;
    private boolean isLoc = false;
    private TaskDetail mTask;
    /**
     * 登录后的主页面
     * @param context
     * @param view
     * @param aif
     */
	public MainPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.activityInterface = aif;
		titleBar = new TitleBar(mContext, view, this, aif);

		btn_coll_point = (Button) view.findViewById(R.id.the_new_task);
		btn_do_task = (Button) view.findViewById(R.id.btn_do_task);
		btn_user_info = (Button) view.findViewById(R.id.btn_user_info);
		btn_more = (Button) view.findViewById(R.id.btn_seeting_more);
		button_back = (ImageButton) view.findViewById(R.id.btn_home);
		
		
		btn_my_task = (Button)view.findViewById(R.id.the_my_task);
		the_about_task = (Button)view.findViewById(R.id.the_about_task);
		
		adview = (AdNoticeView) view.findViewById(R.id.ad_index_adview);
		
		
		btn_coll_point.setOnClickListener(this);
		btn_do_task.setOnClickListener(this);
		btn_user_info.setOnClickListener(this);
		btn_more.setOnClickListener(this);
		button_back.setOnClickListener(this);
		btn_do_task.setSelected(true);
		
		btn_my_task.setOnClickListener(this);
		the_about_task.setOnClickListener(this);
	}

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_POSITION_INDEX;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void goBack() {

		super.goBack();
	}

	@Override
	public void onDestroy() {
		if (location != null) {
			
			location.disableMyLocation();
			if (cellLocationProvider != null) {
			
				cellLocationProvider.disableLocation();
			}
		}
		
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		isLoc = false;
		if (adAdapter == null) {
			
			adAdapter = new AdNoticeAdapter(mContext, adview,activityInterface);
			adview.setAdapter(adAdapter);
		}
		
		titleBar.setFromViewFlag(flag);
		//if (flag == Configs.VIEW_POSITION_LOGIN) {
		
		if (location == null) {
			
			location = new MyLocation(mContext, activityInterface);
			if (!location.isGPSENable()){
				
				showContinueDialog();
				
			}else{
				//开启GPS
				mHandler.sendEmptyMessage(MessageID.LOCATION_OPEN);
			}
		}
		
		//}
		//客户要求(有任务未完成的提示)去除 2014-12-30
		/*boolean b = DManager.getInstance(mContext).iSNCimmitTaskExistes(MColums.WAIT);
		if (b){
			
			showWaitTaskDialog();
		}*/
			
	}

	private void showWaitTaskDialog() {

		final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.show();
		dialog.setContentView(R.layout.layout_dialog);
		dialog.setCancelable(false);

		dialog.findViewById(R.id.continue_confirm).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contineTask();
				dialog.cancel();
			}
		});

		dialog.findViewById(R.id.continue_cancle).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});

	}
	private void contineTask() {
		activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_MY_TASK, Configs.VIEW_FLAG_NONE,
				null, null);
	}
	/**
	 * Gps定位是否启用对话框
	 */
	private void showContinueDialog() {

		final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.show();
		dialog.setContentView(R.layout.layout_dialog);
		TextView textView = (TextView) dialog.findViewById(R.id.text_dialog);
		textView.setText("Gps尚未开启，是否开启Gps定位");
		dialog.setCancelable(false);

		dialog.findViewById(R.id.continue_confirm).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				mHandler.sendEmptyMessage(MessageID.LOCATION_ENABLED_GPS);
				dialog.cancel();
				
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
	public void onDetachedFromWindow(int flag) {
		isLoc = false;
		super.onDetachedFromWindow(flag);
	}

	@Override
	public void onLocationChanged(Location location) {
		LogPrint.Print("gps", "MainPage--onLocationChanged---location="+location.getProvider());
		
		super.onLocationChanged(location);
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			CustomCameraActivity.mlocation = location;
			isLoc = true;
		}else {
			
			isLoc = false;
		}
		AdNoticeAdapter.mLocation = location;
		adAdapter.notifyDataSetChanged();
		
	}

	@Override
	public int getFromPageFlag() {
		return super.getFromPageFlag();
	}

	@Override
	public void onTitleBarClick(View v) {

		super.onTitleBarClick(v);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.the_new_task: //新任务
				/*activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_TASK_COL_POINT, 0, null,
						null);*/
				activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_NEW_TASK,
						Configs.VIEW_FLAG_NONE, null, null);
			break;
			case R.id.the_my_task://我的任务
				activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_MY_TASK,
						Configs.VIEW_FLAG_NONE, null, null);
				break;
			case R.id.the_about_task://随我拍
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("mTask", mTask);
				if (!isLoc) {
					
					bundle.putBoolean("gpsIsOpen", false);
					
				}else {
					
					bundle.putBoolean("gpsIsOpen", isLoc);
				}
				
				
				bundle.putString("ImageName", "");
				intent.putExtras(bundle);
				intent.setClass(mContext, CustomCameraActivity.class);
				
				mContext.startActivity(intent);
				break;
			case R.id.btn_do_task://做任务

			break;
			case R.id.btn_user_info://我的信息
				activityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_USER_INFO, 0, null, null);
			break;
			case R.id.btn_seeting_more://更多
				activityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, 0, null, null);

			break;
			case R.id.btn_home:
				goBack();
			break;

			default:
			break;
		}

	}
	
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MessageID.LOCATION_ENABLED_GPS:
				location.enableMyLocation();
				cellLocationProvider = new CellLocationProvider(mContext);
				cellLocationProvider.addLocationListener(location);
				cellLocationProvider.enableLocation();
				break;
			case MessageID.LOCATION_OPEN:
				location.enableMyLocation();
				cellLocationProvider = new CellLocationProvider(mContext);
				cellLocationProvider.addLocationListener(location);
				cellLocationProvider.enableLocation();
				break;
			default:
				break;
			}
			
		};
	};

}

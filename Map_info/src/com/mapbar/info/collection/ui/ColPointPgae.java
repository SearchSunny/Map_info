package com.mapbar.info.collection.ui;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.util.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
/**
 * 新任务\我的任务(客户提出此页面暂停使用 2014-12-01)
 * @author miaowei
 *
 */
public class ColPointPgae extends BasePage implements OnClickListener {

	private Context mContext;
	private ActivityInterface activityInterface;
	private ImageButton btn_new_task;
	private ImageButton btn_my_task;
	private ImageButton button_back;
	/**
	 * 随手拍
	 */
	private ImageButton the_about_task;
	private TitleBar titleBar;
	private boolean isLoc = false;
	private Location mLocation;
	
	private TaskDetail mTask;
	public ColPointPgae(Context context, View view, ActivityInterface aif) {
		this.activityInterface = aif;
		this.mContext = context;
		titleBar = new TitleBar(context, view, this, aif);
		btn_new_task = (ImageButton) view.findViewById(R.id.the_new_task);
		btn_my_task = (ImageButton) view.findViewById(R.id.the_my_task);
		button_back = (ImageButton) view.findViewById(R.id.btn_home);

		the_about_task = (ImageButton)view.findViewById(R.id.the_about_task);
		
		btn_new_task.setOnClickListener(this);
		btn_my_task.setOnClickListener(this);
		button_back.setOnClickListener(this);
		
		the_about_task.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		isLoc = false;
		titleBar.setFromViewFlag(flag);
		if (flag == Configs.VIEW_POSITION_INDEX) {
			boolean b = DManager.getInstance(mContext).iSNCimmitTaskExistes(MColums.WAIT);
			if (b)
				showContinueDialog();
		}
	}

	private void showContinueDialog() {

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

	@Override
	public void onDetachedFromWindow(int flag) {
		isLoc = false;
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
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			CustomCameraActivity.mlocation = location;
			isLoc = true;
		}else {
			
			isLoc = false;
		}
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_TASK_COL_POINT;
	}

	@Override
	public void onTitleBarClick(View v) {
		super.onTitleBarClick(v);
	}

	@Override
	public void goBack() {
		activityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_INDEX, null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.the_my_task:

				activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_MY_TASK,
						Configs.VIEW_FLAG_NONE, null, null);

			break;
			case R.id.the_new_task:
				activityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_NEW_TASK,
						Configs.VIEW_FLAG_NONE, null, null);
			break;
			case R.id.btn_home:
				goBack();
			break;
			//随手拍
			case R.id.the_about_task:
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
			default:
			break;
		}

	}

}

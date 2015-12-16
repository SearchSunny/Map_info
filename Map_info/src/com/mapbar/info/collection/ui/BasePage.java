package com.mapbar.info.collection.ui;

import com.mapbar.info.collection.LayoutInterface;
import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;

import android.content.Intent;
import android.location.Location;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BasePage implements LayoutInterface {

	@Override
	public void onResume() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public void onAttachedToWindow(int flag, int position) {

	}

	@Override
	public void onDetachedFromWindow(int flag) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public int getMyViewPosition() {
		return 0;
	}

	@Override
	public void onTitleBarClick(View v) {

	}

	@Override
	public void goBack() {

	}

	@Override
	public void goToMap() {

	}

	@Override
	public boolean canPerfirmEvent() {
		return false;
	}

	@Override
	public int getFromPageFlag() {
		return 0;
	}

	@Override
	public void closeSeparated() {

	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public void setDetailItem(TaskDetail task) {

	}

	public void onResult(int requestCode, int resultCode, Intent data) {

	}

	public void setDataBridge(Object object) {
	}

	public void onImageName(String imageName) {

	}
	public void onOldTaskPoint(TaskPoint oldTaskPoint) {
		
	}
	public void onOldTaskDetail(TaskDetail taskDetail){
		
	}
}

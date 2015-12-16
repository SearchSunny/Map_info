package com.mapbar.info.collection;

import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;

import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
/**
 * 定义回调事件
 * 此接口用于控制各页面的生命周期
 * @author miaowei
 *
 */
public interface LayoutInterface
{
	public void onResume();
	public void onDestroy();
	/**
	 * 在onResume()方法之前调用
	 * @param flag  标识是从哪个页面来的
	 * @param position 暂时未使用
	 */
	public void onAttachedToWindow(int flag, int position);
	/**
	 * 
	 * 类似onStop()功能
	 * @param flag
	 */
	public void onDetachedFromWindow(int flag);
	public boolean onKeyDown(int keyCode, KeyEvent event);
	/**
	 * 
	 * @param location
	 */
	public void onLocationChanged(Location location);
	public int getMyViewPosition();
	public void onTitleBarClick(View v);
	public void goBack();
	public void goToMap();
	
	
	public boolean canPerfirmEvent();
	public int getFromPageFlag();
	public void closeSeparated();
	
	public void setDetailItem(TaskDetail task);
}

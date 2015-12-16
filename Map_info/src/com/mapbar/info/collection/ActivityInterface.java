package com.mapbar.info.collection;

import java.util.Vector;

import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;

import android.content.Intent;
import android.location.Location;
import android.view.animation.Animation;

/**
 * 此接口主要用于控制各页面的跳转
 * @author rock
 * 
 */
public interface ActivityInterface {
	/**
	 * show view
	 * 显示下一个页面
	 * @param flag 标识当前页面
	 * @param lif 标识当前VIEW
	 * @param index 指定跳转的页面
	 * @param position 
	 * @param in
	 * @param out
	 */
	public void showPage(int flag, LayoutInterface lif, int index, int position, Animation in, Animation out);

	/**
	 * 显示上一个页面()，用于回退到上一个页面
	 * @param flag 标识当前页面
	 * @param lif 标识当前VIEW
	 * @param index 指定跳转的页面
	 * @param in
	 * @param out
	 */
	public void showPrevious(int flag, LayoutInterface lif, int index, Animation in, Animation out);

	public Vector getMyChannels();

	public Location getMyLocation();

	public void refresh();

	public void onLocationChangedFromGps(Location location);

	public void setPoiPosition(int position);

	public int getPoiPosition();

	public void setCurChannelPosition(int position);

	public int getCurChannelPosition();

	public String getCurrentCity();

	public void setData(TaskDetail task);

	public void StartActivityForFlag(Intent intent, int flag);

	/**
	 * 用于接口返回提示信息
	 * @param code 后台返回代码
	 * @param layoutInterface 当前接口对象
	 */
	public void ShowCodeDialog(int code,LayoutInterface layoutInterface);

	/**
	 * 显示等待框
	 * @param msg
	 */
	public void showNetWaitDialog(String msg);

	/**
	 * 解除等待框
	 */
	public void dismissNetDialog();

	public void dataBridge(Object object);

	public void setImageName(String name);
	
	public void setOldTaskPiint(TaskPoint point);
	
    public void setOldTaskDetail(TaskDetail detail);
    public void setLocation(Location location);
    
    public boolean isNetDialogisShowing();

}

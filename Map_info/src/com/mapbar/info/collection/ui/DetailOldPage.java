package com.mapbar.info.collection.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.util.FileSizeUtil;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;

import android.R.integer;
import android.content.Context;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 已提交任务详情页面
 * @author miaowei
 *
 */
public class DetailOldPage extends BasePage implements OnClickListener {

	private Context mContext;
	private ActivityInterface mInterface;
	private ImageButton btn_back;
	private TextView text_tittle;
	private TextView sTime;
	private TextView lTime;
	private TextView gTime;
	private TextView earn_money;
	private TextView col_point;
	private TextView submit_point;
	private TextView close_time;
	private TextView total_money;
	/**
	 * 关闭时间
	 */
	private TextView bottom_time;
	private int fromFlag;
	private TaskDetail taskDetail;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 已提交任务详情页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public DetailOldPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.mInterface = aif;
		TitleBar bar = new TitleBar(context, view, this, aif);

		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		btn_back.setOnClickListener(this);
		text_tittle = (TextView) view.findViewById(R.id.title_text);

		sTime = (TextView) view.findViewById(R.id.old_task_stime);
		lTime = (TextView) view.findViewById(R.id.old_task_ltime);
		gTime = (TextView) view.findViewById(R.id.old_task_gtime);
		earn_money = (TextView) view.findViewById(R.id.old_task_earn_money);
		col_point = (TextView) view.findViewById(R.id.old_task_col_point);
		submit_point = (TextView) view.findViewById(R.id.old_task_cimmit_point);
		close_time = (TextView) view.findViewById(R.id.old_task_ctime);
		total_money = (TextView) view.findViewById(R.id.old_task_momery);
		bottom_time = (TextView) view.findViewById(R.id.old_task_ctime_bottom);

	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_OLD_DETAIL;
	}
	  private double size=0;
	@Override
	public void onAttachedToWindow(int flag, int position) {
		fromFlag = flag;

		text_tittle.setText(taskDetail.getName());
		sTime.setText(taskDetail.getStartTime());
		lTime.setText(taskDetail.getLostTime());
		if (taskDetail.getQdTime() == null || taskDetail.getQdTime().equals("") || taskDetail.getQdTime().equals("null")) {
			gTime.setText("无");
		} else {
			gTime.setText(dateFormat.format(Long.valueOf(taskDetail.getQdTime())));
		}
		if (taskDetail.getCloseTime() == null || taskDetail.getCloseTime().equals("") || taskDetail.getCloseTime().equals("null")) {
			bottom_time.setText("无");
		} else {
			bottom_time.setText(dateFormat.format(Long.valueOf(taskDetail.getCloseTime())));
		}
		
		earn_money.setText(taskDetail.getRice() + "/条");
		col_point.setText(DManager.getInstance(mContext).queryAllPoint().size() + "条");
		submit_point.setText(DManager.getInstance(mContext).queryOldPoint(MColums.OLD).size() + "条");
		
		
		List<TaskPoint> listPoint=taskDetail.getList();
		for (int i = 0; i < listPoint.size(); i++) {
			TaskPoint point=listPoint.get(i);
			Double double2 = FileSizeUtil.getFileOrFilesSize(SdcardUtil.getSdcardCollInfo() + taskDetail.getId()+"/"+point.getIname(),
					FileSizeUtil.SIZETYPE_MB);
			size=size+double2;
		}
		total_money.setText(size + "MB");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		goBack();
		return true;
	}

	@Override
	public void goBack() {
		mInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, 0, null, null);
	}

	@Override
	public void onClick(View v) {
		goBack();
	}

	@Override
	public void onOldTaskDetail(TaskDetail taskDetail) {
		this.taskDetail = taskDetail;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "DetailOldPage--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}

}

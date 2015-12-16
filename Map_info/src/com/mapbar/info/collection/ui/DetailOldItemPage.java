package com.mapbar.info.collection.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.widget.ScrollLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author miaowei
 *
 */
public class DetailOldItemPage extends BasePage implements OnClickListener {

	private Context mContext;
	private ActivityInterface mInterface;
	private ImageButton btn_back;
	private TextView text_tittle;

	private TextView order;
	private TextView lat;
	private TextView lon;
	private TextView name;
	private TextView type;
	private TextView limit_speed;
	private TextView orientation;
	private TextView angle;
	private TextView content;
	private TextView lastTime;
	/**
	 * 限速值
	 */
	private TextView speedTextView;

	private LinearLayout show_img;
	private int fromFlag;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private DecimalFormat df = new DecimalFormat("######0.000000");
	private ScrollLayout scrollLayout;
	private LinearLayout layoutStatus;
	private LinearLayout layoutReseon;
	private View status, resuon;
	private TextView resonText;
	private TextView statusText;

	/**
	 * 已提交任务详情页
	 * @param context
	 * @param view
	 * @param aif
	 */
	public DetailOldItemPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.mInterface = aif;
		TitleBar bar = new TitleBar(context, view, this, aif);

		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		btn_back.setOnClickListener(this);
		text_tittle = (TextView) view.findViewById(R.id.title_text);

		order = (TextView) view.findViewById(R.id.old_task_commited_order);
		lon = (TextView) view.findViewById(R.id.old_task_cimmited_lon);
		statusText = (TextView) view.findViewById(R.id.old_commited_content_status);
		resonText = (TextView) view.findViewById(R.id.old_commited_content_reseon);
		lat = (TextView) view.findViewById(R.id.old_task_cimmited_lat);
		name = (TextView) view.findViewById(R.id.old_task_commited_name);
		type = (TextView) view.findViewById(R.id.old_task_commited_type);
		speedTextView = (TextView)view.findViewById(R.id.old_task_commited_limit_speed);
		orientation = (TextView) view.findViewById(R.id.old_task_cimmited_ori);
		content = (TextView) view.findViewById(R.id.old_commited_content);
		lastTime = (TextView) view.findViewById(R.id.old_task_ctime_bottom);

		scrollLayout = (ScrollLayout) view.findViewById(R.id.detaile_old_scrolllayout);

		show_img = (LinearLayout) view.findViewById(R.id.detail_old_item_show);
		show_img.setOnClickListener(this);

		layoutStatus = (LinearLayout) view.findViewById(R.id.task_cimmit_status);
		layoutReseon = (LinearLayout) view.findViewById(R.id.task_cimmit_reseon);
		resuon = (View) view.findViewById(R.id.line_reseon);
		status = (View) view.findViewById(R.id.line_statue);

	}

	private void initView(TaskPoint point) {
		text_tittle.setText(point.getTittle());
		order.setText(point.getOrder());
		lon.setText(df.format(Double.valueOf(point.getLon())));
		lat.setText(df.format(Double.valueOf(point.getLat())));
		name.setText(point.getName());
		type.setText(point.getCameraTypeText());
		orientation.setText(point.getOri());
		content.setText(StringUtils.isNullColums(point.getContent())?" ":point.getContent());
		if (StringUtils.isNullColums(point.getLastAlret())) {
			lastTime.setText("无");
		} else {
			lastTime.setText(dateFormat.format(Long.valueOf(point.getLastAlret())));
		}

		if (!StringUtils.isNullColums(point.getLspeed())) {
			
			speedTextView.setText(point.getLspeed());
		}
		else {
			speedTextView.setText("0");
		}
		if (!StringUtils.isNullColums(point.getRefectReason())) {
			layoutReseon.setVisibility(View.VISIBLE);
			resonText.setText(point.getRefectReason());
			resuon.setVisibility(View.VISIBLE);
		} else {
			layoutReseon.setVisibility(View.GONE);
			resuon.setVisibility(View.GONE);
		}
		if (!StringUtils.isNullColums(point.getStatus())) {
			layoutStatus.setVisibility(View.VISIBLE);
			String statu = null;
			if (point.getStatus().equals("0")) {
				
				statu = "待审核";
			}else if (point.getStatus().equals("1")) {
				
				statu = "通过";
			}
			else if (point.getStatus().equals("2")) {
				
				statu = "驳回";
			}else if (point.getStatus().equals("1")) {
				
				statu = "待处理";
			}
			statusText.setText(statu);
			status.setVisibility(View.VISIBLE);
		} else {
			layoutStatus.setVisibility(View.GONE);
			status.setVisibility(View.GONE);
		}

	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_OLD_ITEM_DETAIL;
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		fromFlag = flag;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (scrollLayout.getVisibility() == View.VISIBLE) {
			scrollLayout.setVisibility(View.GONE);
			scrollLayout.removeAllViews();
			return true;
		}

		goBack();
		return true;
	}

	@Override
	public void goBack() {
		mInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, 0, null, null);
	}

	@Override
	public void onOldTaskPoint(TaskPoint oldTaskPoint) {
		point = oldTaskPoint;
		initView(oldTaskPoint);
	}

	private TaskPoint point;

	private class loadBitmap implements Runnable {

		@Override
		public void run() {
			String name;
			if (point.getIname() == null || point.getIname().equals("") || point.getIname().equals("null")) {
				name = DManager.getInstance(mContext).queryPointWhichPoint(DataBaseHelper.TABNAME_POINT,
						point.getCameraId());
			} else {
				name = point.getIname();
			}

			String str = point.getTid() + "/" + name;
			ArrayList<Bitmap> listBitmap = BitmapUtils.getBitmapFromSDCard(str);
			Message message = handler.obtainMessage();
			message.what = 2;
			message.obj = listBitmap;
			handler.sendMessage(message);

		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 0:
					mInterface.dismissNetDialog();
					scrollLayout.setVisibility(View.VISIBLE);
				break;
				case 1:
					mInterface.dismissNetDialog();
					Toast.makeText(mContext, "图片加载失败", 0).show();
				break;
				case 2:
					ArrayList<Bitmap> listBitmap = (ArrayList<Bitmap>) msg.obj;
					for (int i = 0; i < listBitmap.size(); i++) {
						ImageView imageView = new ImageView(mContext);
						imageView.setImageBitmap(listBitmap.get(i));
						scrollLayout.addView(imageView);
					}

					if (scrollLayout.getChildCount() > 0)
						handler.sendEmptyMessage(0);
					else
						handler.sendEmptyMessage(1);
				break;

				default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
			case R.id.detail_old_item_show:
				scrollLayout.setDefaultScreen(0);
				mInterface.showNetWaitDialog("图片加载中请稍后......");
				new Thread(new loadBitmap()).start();
			break;
			case R.id.btn_home:
				goBack();
			break;

			default:
			break;
		}

	}

	@Override
	public void onDetachedFromWindow(int flag) {
		scrollLayout.removeAllViews();
		super.onDetachedFromWindow(flag);
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
       LogPrint.Print("gps", "DetailOldItemPage--onLocationChanged---location="+location.getProvider());
		
		super.onLocationChanged(location);
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
}

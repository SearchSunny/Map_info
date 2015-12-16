package com.mapbar.info.collection.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.ImageBean;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 放弃/存储页面(二期放弃使用)
 * @author miaowei
 *
 */
public class CameraShowPage extends BasePage implements OnClickListener {

	private Context mContext;

	private Button btn_delete;
	private Button btn_save;
	private ImageView img_show;
	private RelativeLayout layout;
	private TextView tittle;
	private ActivityInterface mActivityInterface;
	private CameraPage page;
	private Location location;
	private SharedPreferences preference;
	private Editor editor;
	/**
	 * 标识不同界面的跳转
	 */
	private int fromFlag;
	private String cameraid;
	public  long lat;
	public long lon;

	public CameraShowPage(Context context, View view, ActivityInterface aif, CameraPage page) {
		this.page = page;
		this.mContext = context;
		this.mActivityInterface = aif;
		btn_delete = (Button) view.findViewById(R.id.camera_show_delete);
		btn_save = (Button) view.findViewById(R.id.camera_show_save);
		img_show = (ImageView) view.findViewById(R.id.camera_show_view);
		layout = (RelativeLayout) view.findViewById(R.id.camera_layout_show);
		tittle = (TextView) view.findViewById(R.id.title_text);
		btn_delete.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		preference = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = preference.edit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.camera_show_delete: //放弃
				page.deletePhoto();
				page.openCamera(null);
				layout.setVisibility(View.GONE);
			break;
			case R.id.camera_show_save: //存储,同时请求服务器获取单号
				// new Thread(new SaveBitmapThread()).start();
				if (fromFlag == Configs.VIEW_POSITION_MY_TASK_CHILD) {
					layout.setVisibility(View.GONE);
					mActivityInterface.showPage(Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA, page,
							Configs.VIEW_POSITION_DETAIL, Configs.VIEW_POSITION_DETAIL_TASK, null, null);
				} else if (fromFlag == Configs.VIEW_POSITION_MY_TASK) {
					getOrederNO();
				} else if (fromFlag == Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA) {
					goTODetailPage();
				} else if (fromFlag == Configs.VIEW_POSITION_DETAIL_ADD_POINT) {
					getOrederNO();
				} else {
					getOreder();
				}

			break;

			default:
			break;
		}
	}

	private class SaveBitmapThread implements Runnable {
		@Override
		public void run() {
			BitmapUtils.saveBitmapToSD(path, getBitmap(path, imgName), location);
		}
	}

	/**
	 * 未使用
	 */
	public void saveImg() {
		ImageBean imageBean = new ImageBean();
		imageBean.setImageName(imgName);
		imageBean.setCameraId(cameraid);
		DManager.getInstance(mContext).addImageName(imageBean);
	}

	public void setVisible(int visible) {
		layout.setVisibility(visible);
	}

	public boolean getVisible() {
		int v = layout.getVisibility();
		return v == View.VISIBLE ? true : false;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		page.deletePhoto();
		page.openCamera(null);
		layout.setVisibility(View.GONE);

		return false;
	}

	private String imgName;
	private String path;

	/**
	 * 显示拍照后 旋转为正确角度的图片
	 * 
	 * @param path
	 *            图片的绝对地址
	 * @param name
	 *            每个point 图片的文件夹名称
	 */
	public void showView(String path, String name) {
		this.path = path;
		imgName = name;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 8;

		int degree = BitmapUtils.readPictureDegree(path);

		Bitmap cameraBitmap = BitmapFactory.decodeFile(path, bitmapOptions);

		Bitmap bitmap = BitmapUtils.rorateBitamp(degree, cameraBitmap);
		img_show.setImageBitmap(bitmap);

		layout.setVisibility(View.VISIBLE);

	}

	public Bitmap getBitmap(String path, String name) {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 8;

		int degree = BitmapUtils.readPictureDegree(path);

		Bitmap cameraBitmap = BitmapFactory.decodeFile(path, bitmapOptions);

		Bitmap bitmap = BitmapUtils.rorateBitamp(degree, cameraBitmap);

		return bitmap;

	}

	/**
	 * 点击放弃的 删除已经拍摄的图片
	 * 
	 * @param name
	 */
	public void deletePhoto(String name) {

		layout.setVisibility(View.GONE);
	}
	public void  setLocation(Location location){
		this.location=location;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	private HttpHandler httpHandler;
	private TaskDetail mTask;

	/**
	 * 抢单
	 */
	// {"data":null,"id":"","message":"","result":true}
	private void getOreder() {
		mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		String url = UrlConfig.orderUrl + "loginId=" + StringUtils.getLoginId(mContext) + "&token=" + StringUtils.getLoginToken(mContext) + "&taskId="
				+ mTask.getId();
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						JSONObject jsonObject = new JSONObject(new String(data));
						boolean b = jsonObject.getBoolean("result");
						if (b)
							mHandler.sendEmptyMessage(0);
						else {
							Message message = mHandler.obtainMessage();
							String msg = jsonObject.getString("message");
							message.obj = msg;
							message.what = 1;
							mHandler.sendMessage(message);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					if (arg0 != 200)
						mHandler.sendEmptyMessage(5);
					else
						mHandler.sendEmptyMessage(1);
				}
			}
		});
		httpHandler.execute();
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 获取单号(二期暂停使用)
	 */
	private void getOrederNO() {
		mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		String url;
		if (location != null) {
			
			url = UrlConfig.getOrderNOUrl + "loginId=" + StringUtils.getLoginId(mContext) + "&token=" + StringUtils.getLoginToken(mContext) + "&taskId="
					+ mTask.getId() + "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&gpsSpeed="
					+ location.getSpeed() + "&gpsAngle=" + location.getBearing() + "&gpsTime="
					+ dateFormat.format(location.getTime()) + "&gpsHeight=" + location.getAltitude();
		}else {
			
			url = UrlConfig.getOrderNOUrl + "loginId=" + StringUtils.getLoginId(mContext) +
					 "&token=" + StringUtils.getLoginToken(mContext) + "&taskId="
					 + mTask.getId() + "&lat=39.93786&lon=116.43486" + "&gpsSpeed=" + "30"
					 + "&gpsAngle=" + "112.3"
					 + "&gpsTime=" + dateFormat.format(System.currentTimeMillis()) +
					 "&gpsHeight=" + "112";
		}
	

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
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							TaskPoint point = new TaskPoint();
							
							if (location != null) {
								
								point.setLat(location.getLatitude() + "");
								point.setLon(location.getLongitude() + "");
								point.setAngle(String.valueOf((int)location.getBearing()));
								
							}else {
								
								point.setLat("39.9378");
								point.setLon("116.43486");
								point.setAngle("60");
							}
							point.setCameraId(dataJson.getString("id"));
							point.setOrder(dataJson.getString("odd"));
							point.setIname(imgName);
							Message message = mHandler.obtainMessage();
							message.obj = point;
							message.what = 2;
							mHandler.sendMessage(message);
						} else {
							String msg = jsonObject.getString("message");
							Message message = mHandler.obtainMessage();
							message.what = 4;
							message.obj = msg;

							mHandler.sendMessage(message);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (arg0 != 200)
						mHandler.sendEmptyMessage(5);
					else
						mHandler.sendEmptyMessage(3);
				}
			}
		});
		httpHandler.execute();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		fromFlag = flag;
	}

	@Override
	public void setDetailItem(TaskDetail task) {
		this.mTask = task;
	}

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_POSITION_CAMERA_SHOW;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 0:
					saveTask();
					getOrederNO();
				break;
				case 1:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "抢单失败", 0).show();
					String str = (String) msg.obj;
					if (str == null || str.equals("")) {
						break;
					}
					int code = Integer.valueOf(str);
					mActivityInterface.ShowCodeDialog(code,CameraShowPage.this);
				break;
				case 2://成功获取单号
					mActivityInterface.dismissNetDialog();
					TaskPoint info = (TaskPoint) msg.obj;
					info.setTittle(tittle.getText().toString());
					info.setTid(mTask.getId());
					info.setTaskType(Integer.valueOf(mTask.getType()));

					cameraid = info.getCameraId();
					// saveImg();
					mActivityInterface.setData(mTask);
					mActivityInterface.dataBridge(info);
					layout.setVisibility(View.GONE);
					//跳转到
					mActivityInterface.showPage(getMyViewPosition(), page, Configs.VIEW_POSITION_DETAIL,
							Configs.VIEW_POSITION_DETAIL_CAMERA, null, null);
				break;
				case 3:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "获取单号失败", 0).show();
				break;
				case 4:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "获取单号失败", 0).show();
					String sr = (String) msg.obj;
					int cod = Integer.valueOf(sr);
					mActivityInterface.ShowCodeDialog(cod,CameraShowPage.this);
				break;
				case 5:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "网络不给力，请检查网络", 0).show();
				break;
				default:
				break;
			}

		};
	};

	/**
	 * 跳转到任务点详情页面
	 */
	private void goTODetailPage() {
		layout.setVisibility(View.GONE);
		mActivityInterface.showPage(Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA, page, Configs.VIEW_POSITION_DETAIL,
				Configs.VIEW_POSITION_DETAIL_CAMERA, null, null);
	}

	/**
	 * 抢单成功后保存任务到数据库
	 */
	public void saveTask() {
		boolean b = DManager.getInstance(mContext).queryDataExists(DataBaseHelper.TABNAME_WAIT, this.mTask.getId());
		if (!b) {
			mTask.setQdTime(System.currentTimeMillis() + "");
			DManager.getInstance(mContext).addTaskDetail(mTask);
		}

	}
}

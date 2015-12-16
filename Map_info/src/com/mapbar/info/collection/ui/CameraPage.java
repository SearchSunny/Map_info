package com.mapbar.info.collection.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.zip.DataFormatException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mapbar.android.location.DebugManager;
import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.widget.MProgressView;
import com.mapbar.info.collection.widget.ScrollLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 抢单/拍照页面
 * @author miaowei
 *
 */
public class CameraPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface mActivityInterface;
	private TitleBar titleBar;
	private ImageButton btn_back;
	private TextView tittle_text;
	private TextView detail_text;
	private Button btn_cut;
	private TaskDetail mTask;
	private MProgressView mProgressView;
	private RelativeLayout mProgressReLayout;
	private File fils;
	private CameraShowPage cameraShowPage;
	private int fromFlag;
	private String ImageName;
	private int position_falg;
	private LinearLayout linearLayout_bottom;
	private TextView text_startTime, textLostTime, price, task_type;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private DecimalFormat df = new DecimalFormat("######0.000000");
	private TaskPoint point;
	/**
	 * GPS是否打开(true=打开,false=未打开)
	 */
	private boolean gpsIsOpen;
	
	/**
	 * 是抢单还是拍照(true拍照/false抢单)
	 */
	private boolean isCameraAndRob;
	/**
	 * 标识是否第一次抢单成功(true是,false否)(暂未使用)
	 */
	public static boolean isFirsetSuccess;
	/**
	 * 是否是从自定义拍照页面过来的(1是 0否)(暂未使用)
	 */
	public static int isCameraPage;
	

	private Location mLocation;
	/**
	 * 抢单/拍照页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public CameraPage(Context context, View view, ActivityInterface aif) {
		this.mActivityInterface = aif;
		this.mContext = context;
		point = new TaskPoint();
		cameraShowPage = new CameraShowPage(context, view, aif, this);
		titleBar = new TitleBar(mContext, view, this, mActivityInterface);
		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		tittle_text = (TextView) view.findViewById(R.id.title_text);
		detail_text = (TextView) view.findViewById(R.id.camera_detail_address);
		task_type = (TextView) view.findViewById(R.id.new_task_type);
		text_startTime = (TextView) view.findViewById(R.id.new_task_stime);
		textLostTime = (TextView) view.findViewById(R.id.new_task_ltime);
		price = (TextView) view.findViewById(R.id.new_task_earn_money);
		btn_cut = (Button) view.findViewById(R.id.camera_cut);
		btn_cut.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		mProgressView = (MProgressView) view.findViewById(R.id.camrea_progressbar);
		mProgressReLayout = (RelativeLayout) view.findViewById(R.id.camrea_pro_view);
		linearLayout_bottom = (LinearLayout) view.findViewById(R.id.camarea_bottom);
		linearLayout_bottom.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (cameraShowPage.getVisible()) {
			cameraShowPage.setVisible(View.GONE);
			openCamera(null);
			return true;
		}

		goBack();

		return true;
	}

	@Override
	public void goBack() {
		if (fromFlag == Configs.VIEW_POSITION_MY_TASK || position_falg == Configs.VIEW_POSITION_MY_TASK_P){
			
			mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_MY_TASK, null, null);
			
		}else if (fromFlag == Configs.VIEW_POSITION_NONE) {
			
			mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_INDEX, null, null);
			
		}else{
			
			mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_NEW_TASK, null, null);
		}
			
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		fromFlag = flag;
		
		/*if (mTask.getStatus().equals("1")) {
			
			btn_cut.setText("抢单");
			isCameraAndRob = false;
		}else {
			
			btn_cut.setText("拍照");
			isCameraAndRob = true;
		}*/
		if (Configs.VIEW_POSITION_NEW_TASK == flag || flag == Configs.VIEW_POSITION_INTRODUCE_IMG || flag == Configs.VIEW_POSITION_INDEX || flag == Configs.VIEW_POSITION_SEARCH) {
			btn_cut.setText("抢单");
			isCameraAndRob = false;
		} else {
			btn_cut.setText("拍照");
			isCameraAndRob = true;
		}
		btn_cut.setEnabled(true);
		btn_cut.setTextColor(mContext.getResources().getColor(R.color.white));

		text_startTime.setText(mTask.getStartTime());
		if (mTask.getType().equals("0")){
			
			task_type.setText("单次任务");
			
		} else if (mTask.getType().equals("1")) {
			
			task_type.setText("多次任务");
			
		}else{
			
			task_type.setText("随手拍");
		}
			
		com.mapbar.info.collection.DebugManager.println(mTask.getQdTime());
		textLostTime.setText((StringUtils.isNullColums(mTask.getLostTime()))?"":mTask.getLostTime());
		price.setText(mTask.getRice() + "元/条");

		//表示继续拍照
		if (flag == Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA || flag == Configs.VIEW_POSITION_MY_TASK_CHILD) {
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("mTask", mTask);
			if (gpsIsOpen) {
				
				bundle.putBoolean("gpsIsOpen", gpsIsOpen);
			}else {
				bundle.putBoolean("gpsIsOpen", false);
			}
			
			bundle.putString("ImageName", ImageName);
			intent.putExtras(bundle);
			intent.setClass(mContext, CustomCameraActivity.class);
			
			mContext.startActivity(intent);
			//openCamera(ImageName);
		}
		if (position != -1)
			position_falg = position;
		cameraShowPage.onAttachedToWindow(flag, position);
	}

	@Override
	public void setDetailItem(TaskDetail task) {
		this.mTask = task;
		tittle_text.setText(task.getName());
		detail_text.setText(task.getDescription());
		cameraShowPage.setDetailItem(task);
	}

	@Override
	public void onImageName(String imageName) {
		this.ImageName = imageName;
		super.onImageName(imageName);
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		//(此功能暂停，二期需求2014-8-13)
		/*if (mTask.getType().equals("0") && fromFlag == Configs.VIEW_POSITION_MY_TASK) {
			btn_cut.setEnabled(false);
			btn_cut.setTextColor(mContext.getResources().getColor(R.color.user_text_c));
		} else if ("gps".equals(location.getProvider())) {
			this.location = location;
			cameraShowPage.onLocationChanged(location);
			btn_cut.setEnabled(true);
			btn_cut.setTextColor(mContext.getResources().getColor(R.color.white));
		} else if ("cell".equals(location.getProvider())) {
			btn_cut.setEnabled(false);
			btn_cut.setTextColor(mContext.getResources().getColor(R.color.user_text_c));
		}*/
		//测试使用
		//btn_cut.setEnabled(true);
		LogPrint.Print("gps", "CameraPage--onLocationChanged---location="+location.getProvider());
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			mLocation = location;
			CustomCameraActivity.mlocation = location;
			gpsIsOpen = true;
		}else {
		     gpsIsOpen = false;
		}
		
	}

	@Override
	public void onDetachedFromWindow(int flag) {

		super.onDetachedFromWindow(flag);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_CAMERA;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.camera_cut: //抢单\拍照
				
				//openCamera(null);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("mTask", mTask);
				if (!gpsIsOpen) {
					
					bundle.putBoolean("gpsIsOpen", false);
				}else {
					
					bundle.putBoolean("gpsIsOpen", gpsIsOpen);
				}
				bundle.putString("ImageName", "");
				bundle.putBoolean("isCameraAndRob",isCameraAndRob);
				bundle.putString("title",!StringUtils.isNullColums(tittle_text.getText().toString())?tittle_text.getText().toString():"" );
				intent.putExtras(bundle);
				intent.setClass(mContext, CustomCameraActivity.class);
				
				mContext.startActivity(intent);
				

			break;
			case R.id.btn_home:
				goBack();
			break;
			case R.id.camarea_bottom: //拍照教程

				mActivityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_INTRODUCE_IMG, -1, null,
						null);

			break;

			default:
			break;
		}

	}

	/**
	 * 接收拍摄照片点击确定之后的返回值
	 */
	@Override
	public void onResult(int requestCode, int resultCode, Intent data) {
		super.onResult(requestCode, resultCode, data);
		// -1表示确定 0取消
		if (resultCode == -1) {
			//把拍好的照片显示出来(此功能暂停，二期需求2014-8-13)
			//cameraShowPage.showView(fils.getAbsolutePath(), image_name);
			try {
				//往图片上加经纬度等信息
				ExifInterface exif = new ExifInterface(path);
				//纬度
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, mLocation.getLatitude()+"");
				//经度
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, mLocation.getLongitude()+"");
				//方向
				exif.setAttribute("GPSImgDirection", mLocation.getBearing() + "");
				//海拔高度
				exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, mLocation.getAltitude()+"");
				//速度
				exif.setAttribute("GPSSpeed", mLocation.getSpeed() + "");
				//
				exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP,dateFormat.format(mLocation.getTime()));
				
				//测试时使用 
				//纬度参考
				/*exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, location.getLatitude() > 0 ? "N" : "S");
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, decimalToDMS(location.getLongitude()));
				//经度参考
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, location.getLongitude() > 0 ? "E" : "W");
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, location.getLongitude() > 0 ? "E" : "W");
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "116.42666288651526");
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "39.936562408693135");
				exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, dateFormat.format(System.currentTimeMillis()));
				*/
				
				exif.saveAttributes();
				cameraShowPage.setLocation(mLocation);
//				com.mapbar.info.collection.DebugManager.println(location.getLatitude() + "===="
//						+ location.getLongitude() + "===" + location.getTime() + "====|||"
//						+ decimalToDMS(location.getLatitude()) + "|||" + decimalToDMS(location.getLongitude()));
			} catch (Exception e) {
			}
			//取消 继续拍照
		} else if (resultCode == Activity.RESULT_CANCELED) {
			if (fromFlag == Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA || fromFlag == Configs.VIEW_POSITION_MY_TASK_CHILD) {
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_DETAIL,
						Configs.VIEW_POSITION_DETAIL_CAMERA, null, null);
			}
		}

	}

	private String decimalToDMS(double coord) {
		String output, degrees, minutes, seconds;

		double mod = coord % 1;
		int intPart = (int) coord;

		degrees = String.valueOf(intPart);

		coord = mod * 60;
		mod = coord % 1;
		intPart = (int) coord;
		if (intPart < 0) {
			intPart *= -1;
		}

		minutes = String.valueOf(intPart);

		coord = mod * 60;
		intPart = (int) coord;
		if (intPart < 0) {
			intPart *= -1;
		}
		seconds = String.valueOf(intPart);
		output = degrees + "/1," + minutes + "/1," + seconds + "/1";

		return output;
	}

	private String path;
	private String image_name;

	public void setCameraShow(Location location) {
		this.mLocation = location;
	}

	/**
	 * 打开摄像头\并指定图片存放路径
	 * @param name
	 */
	public void openCamera(String name) {
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		if (name == null)
			image_name = System.currentTimeMillis() + "";
		else
			image_name = name;

		//创建以 任务ID + 图片名称的文件夹
		File file = new File(SdcardUtil.getSdcardCollInfo() + mTask.getId() + "/" + image_name + "/");
		if (!file.exists())
			file.mkdirs();

		//创建以 任务ID + 图片名称的实体图片
		fils = new File(SdcardUtil.getSdcardCollInfo() + mTask.getId() + "/" + image_name + "/"
				+ +System.currentTimeMillis() + ".jpg");
		
		path = fils.getAbsolutePath();
		Uri uri = Uri.fromFile(fils);
		// 设置系统相机拍摄照片完成后图片文件的存放地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		mActivityInterface.StartActivityForFlag(intent, 0);
	}
	

	public void deletePhoto() {
		File file = new File(path);
		Log.e("deletePhoto", path);
		if (!file.exists()) {
			return;
		}
		file.delete();
	}
	
	private HttpHandler httpHandler;
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
							mHandler.sendEmptyMessage(000000);
						else {
							Message message = mHandler.obtainMessage();
							String msg = jsonObject.getString("message");
							message.obj = msg;
							message.what = 100000;
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
	
	/**
	 * 获取单号(暂停使用)
	 */
	private void getOrederNO() {
		mActivityInterface.showNetWaitDialog(mContext.getResources().getString(R.string.dialog_net));
		/*String url = UrlConfig.getOrderNOUrl + "loginId=" + Configs.LOGINID + "&" + Configs.TOKEN + "&taskId="
				+ mTask.getId() + "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&gpsSpeed="
				+ location.getSpeed() + "&gpsAngle=" + location.getBearing() + "&gpsTime="
				+ dateFormat.format(location.getTime()) + "&gpsHeight=" + location.getAltitude();*/
		//测试时使用
		 String url = UrlConfig.getOrderNOUrl + "loginId=" + StringUtils.getLoginId(mContext) +
		 "&token=" + StringUtils.getLoginToken(mContext) + "&taskId="
		 + mTask.getId() + "&lat=39.93786&lon=116.43486" + "&gpsSpeed=" + "30"
		 + "&gpsAngle=" + "112.3"
		 + "&gpsTime=" + dateFormat.format(System.currentTimeMillis()) +
		 "&gpsHeight=" + "112";

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
							point.setLat(mLocation.getLatitude() + "");
							point.setLon(mLocation.getLongitude() + "");
							//测试使用
							/*point.setLat("39.9378");
							point.setLon("116.43486");*/
							point.setCameraId(dataJson.getString("id"));
							point.setOrder(dataJson.getString("odd"));
							point.setIname(image_name);
							Message message = mHandler.obtainMessage();
							message.obj = point;
							message.what = 200000;
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
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 000000: //抢单成功
					saveTask(); //保存任务到数据库
					//getOrederNO(); //不再获取单号
					mActivityInterface.dismissNetDialog();
					
					point.setLat(mLocation.getLatitude() + "");
					point.setLon(mLocation.getLongitude() + "");
					//GPS角度
					point.setAngle(String.valueOf((int)mLocation.getBearing()));
					//手机方向
					//point.setOri(String.valueOf(100));
					//测试使用
					/*point.setLat("39.9378");
					point.setLon("116.43486");
					point.setAngle("60");
					point.setOri(String.valueOf(100));
					*/
					point.setCameraId("");
					point.setOrder("");
					point.setIname(image_name);
					point.setTittle(tittle_text.getText().toString());
					point.setTid(mTask.getId());
					point.setTaskType(Integer.valueOf(mTask.getType()));
					
					mActivityInterface.setData(mTask);
					mActivityInterface.dataBridge(point);
					goTODetailPage();
				break;
				case 100000: //抢单失败
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "抢单失败", 0).show();
					String str = (String) msg.obj;
					if (str == null || str.equals("")) {
						break;
					}
					int code = Integer.valueOf(str);
					mActivityInterface.ShowCodeDialog(code,CameraPage.this);
				break;
				case 200000: //成功获取单号
					mActivityInterface.dismissNetDialog();
					TaskPoint info = (TaskPoint) msg.obj;
					info.setTittle(tittle_text.getText().toString());
					info.setTid(mTask.getId());
					info.setTaskType(Integer.valueOf(mTask.getType()));

					//cameraid = info.getCameraId();
					// saveImg();
					mActivityInterface.setData(mTask);
					mActivityInterface.dataBridge(info);
					//跳转到
					mActivityInterface.showPage(getMyViewPosition(), CameraPage.this, Configs.VIEW_POSITION_DETAIL,
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
					mActivityInterface.ShowCodeDialog(cod,CameraPage.this);
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
	 * 抢单成功后保存任务到数据库
	 */
	public void saveTask() {
		boolean b = DManager.getInstance(mContext).queryDataExists(DataBaseHelper.TABNAME_WAIT, this.mTask.getId());
		if (!b) {
			mTask.setQdTime(System.currentTimeMillis() + "");
			DManager.getInstance(mContext).addTaskDetail(mTask);
		}

	}
	/**
	 * 跳转到任务点详情页面
	 */
	private void goTODetailPage() {
		mActivityInterface.showPage(Configs.VIEW_POSITION_CAMERA, this, Configs.VIEW_POSITION_DETAIL,
				Configs.VIEW_POSITION_DETAIL_CAMERA, null, null);
	}
}

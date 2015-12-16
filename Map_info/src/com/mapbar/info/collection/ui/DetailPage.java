package com.mapbar.info.collection.ui;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.event.EventInfoBase;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.upload.FileUtil;
import com.mapbar.info.collection.upload.FormFile;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.MessageID;
import com.mapbar.info.collection.util.ParseXmlService;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.TimerTaskGetImageLength;
import com.mapbar.info.collection.util.Util;
import com.mapbar.info.collection.widget.ScrollLayout;
/**
 * 任务点详情页面
 * @author miaowei
 *
 */
@SuppressLint("ResourceAsColor")
public class DetailPage extends BasePage{

	private Context mContext;
	private ActivityInterface mActivityInterface;
	/**
	 * 单号
	 */
	private TextView order;
	/**
	 * 纬度
	 */
	private TextView lat;
	/**
	 * 经度
	 */
	private TextView lon;
	private TextView tittle;

	/**
	 * 采集点任务名称
	 */
	private EditText edit_name;
	/**
	 * 限速值
	 */
	private EditText edit_limit_speed;
	/**
	 * 方向
	 */
	private EditText edit_orietation;
	
	/**
	 * 角度
	 */
	private EditText edit_angle;
	/**
	 * 备注
	 */
	private EditText edit_content;

	private Button btn_reset;
	private Button btn_save;
	private Button btn_submit;

	private ImageButton btn_back;
	private LinearLayout layout;
	/**
	 * 继续拍照
	 */
	private LinearLayout continue_camera;

	/**
	 * 对话框的一种(相对于某个控件弹出),暂时没用
	 */
	private PopupWindow popupWindow;
	/**
	 * 电子眼类型
	 */
	private TextView type;
	private View linearLayout;
	private ImageView img;
	private int fromFlag;

	private TaskPoint point;
	/**
	 * 电子眼类型ID
	 */
	private String camera_type;
	/**
	 * 电子眼类型文字
	 */
	private String camera_type_text;
	
	private int from_position;
	private DecimalFormat df = new DecimalFormat("######0.000000");
	private ScrollLayout scrollLayout;
	private String angle_text;
	private String ori_text;
	/**
	 * 标识方向文字
	 */
	private TextView edit_input_oriText;
	/**
	 * 第一张缩略图
	 */
	private ImageView image_First_Bitmap;
	/**
	 * 第二张缩略图
	 */
	private ImageView image_Second_Bitmap;
	/**
	 * 解析XML数据(电子眼类型)
	 */
	private ParseXmlService parseXml;
	/**
	 * 自定义删除按钮ID
	 */
	private final int imageDeleteId = 1000000;
    /**
     * GPS是否找开
     */
	private boolean isLoc = false;
	
	private Location mLocation;
	/**
	 * 用于轮询校验图片是否上传成功
	 */
	Timer timer =  null;
	TimerTaskGetImageLength myTimerTask;
	private SimpleDateFormat gpsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 任务点详情页面
	 * @param context
	 * @param detailView
	 * @param aif
	 */
	public DetailPage(Context context, View detailView, ActivityInterface aif) {
		this.mContext = context;
		this.mActivityInterface = aif;
		parseXml = new ParseXmlService();
		timer = new Timer();
		TitleBar bar = new TitleBar(context, detailView, this, mActivityInterface);
		order = (TextView) detailView.findViewById(R.id.detail_order_id);
		lat = (TextView) detailView.findViewById(R.id.detail_order_lat);
		lon = (TextView) detailView.findViewById(R.id.detail_order_lon);
		tittle = (TextView) detailView.findViewById(R.id.title_text);
		type = (TextView) detailView.findViewById(R.id.detail_type);
		
		edit_name = (EditText) detailView.findViewById(R.id.detail_edit_input_name);
		edit_limit_speed = (EditText) detailView.findViewById(R.id.detail_edit_input_limit_speed);
		edit_orietation = (EditText) detailView.findViewById(R.id.detail_edit_input_ori);
		edit_angle = (EditText) detailView.findViewById(R.id.detail_edit_input_angle);
		edit_content = (EditText) detailView.findViewById(R.id.detail_eidt_content);
		edit_input_oriText = (TextView)detailView.findViewById(R.id.detail_edit_input_oriText);
		edit_orietation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null &&! s.toString().equals("")) {
					int angle = Integer.valueOf(s.toString());
					if (angle > 360) {
						edit_orietation.setText(angle_text);
						edit_orietation.setSelection(angle_text.length());
						Toast.makeText(mContext, "方向值不能大于360", 0).show();
						return;
					}
					angle_text=s.toString();
					String orientationString =  Util.getOrientation(s.toString());
					edit_input_oriText.setText(orientationString);
				}else {
					
					edit_input_oriText.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		edit_angle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null &&! s.toString().equals("")) {
					int angle = Integer.valueOf(s.toString());
					if (angle > 360) {
						edit_angle.setText(ori_text);
						edit_angle.setSelection(ori_text.length());
						Toast.makeText(mContext, "角度值不能大于360", 0).show();
						return;
					}
					ori_text=s.toString();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btn_reset = (Button) detailView.findViewById(R.id.detail_btn_reset);
		btn_submit = (Button) detailView.findViewById(R.id.detail_btn_commit);
		btn_save = (Button) detailView.findViewById(R.id.detail_btn_save);
		btn_back = (ImageButton) detailView.findViewById(R.id.btn_home);
		layout = (LinearLayout) detailView.findViewById(R.id.detail_show);
		continue_camera = (LinearLayout) detailView.findViewById(R.id.detail_camera_continue);
		image_First_Bitmap = (ImageView)detailView.findViewById(R.id.image_one_bitmap);
		image_Second_Bitmap = (ImageView)detailView.findViewById(R.id.image_two_bitmap);
		linearLayout = detailView.findViewById(R.id.detail_type_layout);

		btn_reset.setOnClickListener(onClickListener);
		btn_submit.setOnClickListener(onClickListener);
		//-----二期暂时关闭提交操作
		//btn_submit.setEnabled(false);
	    //btn_submit.setBackgroundColor(R.color.gray);
	    //btn_submit.setTextColor(R.color.text_gray);
	    //-----
		btn_save.setOnClickListener(onClickListener);
		btn_back.setOnClickListener(onClickListener);
		layout.setOnClickListener(onClickListener);
		type.setOnClickListener(onClickListener);
		continue_camera.setOnClickListener(onClickListener);

		scrollLayout = (ScrollLayout) detailView.findViewById(R.id.detail_scrolllayout);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_DETAIL;
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		//continue_camera.setEnabled(false);
		//scrollLayout.setDefaultScreen(0);
		scrollLayout.removeAllViews();
		if (flag != -1)
			fromFlag = flag;
		if (position != -1)
			from_position = position;
		if (flag != Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA || flag != Configs.VIEW_POSITION_MY_TASK
				|| flag != Configs.VIEW_POSITION_MY_TASK_CHILD)
		{
			reset();
		}
			
		setText();
	}

	private void setText() {

		if (point != null) {
			
			order.setText(!StringUtils.isNullColums(point.getOrder())?point.getOrder():"");
			lat.setText(df.format(Double.valueOf(point.getLat())));
			lon.setText(df.format(Double.valueOf(point.getLon())));
			tittle.setText(point.getTittle());
			edit_name.setText(point.getName());
			edit_limit_speed.setText(point.getLspeed());
			
			if (!StringUtils.isNullColums(point.getOri())) {
				String orientationString =  Util.getOrientation(point.getOri());
				edit_input_oriText.setText(orientationString);
				edit_orietation.setText(point.getOri());
			}else {
				
				edit_orietation.setText(String.valueOf(0));
			}
			if (point.getAngle() != null && !point.getAngle().equals("")) {
				
				edit_angle.setText(point.getAngle());
			}else {
					
				edit_angle.setText(String.valueOf(0));
				
			}
			
			edit_content.setText(point.getContent());
			if (!StringUtils.isNullColums(point.getCameraTypeText())) {
				type.setText(point.getCameraTypeText());
				camera_type_text = point.getCameraTypeText();
				camera_type = point.getType();
			} else {
				String cameraTypeText = SharedPreferencesUtil.getCameraTypeText(mContext);
				String cameraTypeId = SharedPreferencesUtil.getCameraTypeId(mContext);
				if (!StringUtils.isNullColums(cameraTypeText)) {
					
					type.setText(cameraTypeText);
				}else {
					
					type.setText("电子眼类型");
				}
				point.setCameraTypeText(type.getText().toString());
				camera_type_text = type.getText().toString();
				camera_type = cameraTypeId;
			}
			if (image_First_Bitmap.getDrawable() != null) {
				image_First_Bitmap.setImageBitmap(null);
				image_First_Bitmap.setImageResource(R.drawable.intdex_page_add_app);
			}
			if (image_Second_Bitmap.getDrawable() != null) {
				image_Second_Bitmap.setImageBitmap(null);
				image_Second_Bitmap.setImageResource(R.drawable.intdex_page_add_app);
			}
			getImageBitmap();
		}
		
		
	}

	/**
	 * 设置缩略图
	 */
	private void getImageBitmap() {
		if (point.getIname() != null && !point.getIname().equals("")) {
			
			String str = point.getTid() + "/" + point.getIname();
			
			ArrayList<String> pathList = BitmapUtils.getPhotoById(str);
			if (pathList != null && pathList.size() >= 1) {
				//获取图片路径
				File fileDir = new File(SdcardUtil.getSdcardCollInfo() + str + "/" + pathList.get(0));	
				//处理图片
				Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(fileDir.getPath(),200,200);
				
				Bitmap background = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.intdex_page_cll_point);
				//缩放图片
				Bitmap map = BitmapUtils.resizeImage(bitmap, background.getWidth(), background.getWidth());
				//处理圆角
				image_First_Bitmap.setImageBitmap(BitmapUtils.mergerIcon(background, map, 7,0,0));
				
			}
				
			if (pathList != null && pathList.size() == 2) {
				
				//获取图片路径
				File fileDir1 = new File(SdcardUtil.getSdcardCollInfo() + str + "/" + pathList.get(1));	
				//处理图片
				Bitmap bitmap1 = BitmapUtils.decodeSampledBitmapFromResource(fileDir1.getPath(),200,200);
				
				Bitmap background1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.intdex_page_cll_point);
				//缩放图片
				Bitmap map1 = BitmapUtils.resizeImage(bitmap1, background1.getWidth(), background1.getWidth());
				//处理圆角
				image_Second_Bitmap.setImageBitmap(BitmapUtils.mergerIcon(background1, map1, 7,0,0));
			}
			
			
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		savePoint(1);
		if (scrollLayout.getVisibility() == View.VISIBLE) {
			scrollLayout.removeAllViews();
			//scrollLayout.setDefaultScreen(0);
			scrollLayout.setVisibility(View.GONE);
			return true;
		}
		goBack();
		return true;
	}

	@Override
	public void goBack() {
		if (fromFlag == Configs.VIEW_POSITION_MY_TASK_CHILD) {
			mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, null, null);
		} else if (fromFlag == Configs.VIEW_POSITION_MY_TASK) {
			mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, null, null);
		} else {

			if (fromFlag == Configs.VIEW_POSITION_MY_TASK || from_position == Configs.VIEW_POSITION_DETAIL_TASK) {
				mActivityInterface
						.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, null, null);
			} else if (point.getTaskType() == 0) {
				if (checkFiledisNull(false))
					savePoint(MColums.POINT_FULL);
				else
					savePoint(MColums.POINT_N_FULL);
				mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_NEW_TASK, null, null);
			} else if (point.getTaskType() == 1) {
				if (checkFiledisNull(false))
					savePoint(MColums.POINT_FULL);
				else
					savePoint(MColums.POINT_N_FULL);
				mActivityInterface.showPrevious(Configs.VIEW_POSITION_DETAIL_ADD_POINT, this,
						Configs.VIEW_POSITION_CAMERA, null, null);
			}else if (point.getTaskType() == 2) {
				if (checkFiledisNull(false))
					savePoint(MColums.POINT_FULL);
				else
					savePoint(MColums.POINT_N_FULL);
				mActivityInterface.showPrevious(Configs.VIEW_POSITION_DETAIL_ADD_POINT, this,
						Configs.VIEW_POSITION_CAMERA, null, null);
			}

		}
	}
	private AlertDialog dialog;

	/**
	 * 
	 */
	private void showUploadDialog() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext).create();
		} else if (dialog.isShowing()) {
			return;
		}

		dialog.show();
		dialog.setContentView(R.layout.layout_dialog);
		dialog.setCancelable(false);
		TextView textView = (TextView) dialog.findViewById(R.id.text_dialog);
		textView.setText(mContext.getResources().getString(R.string.dialog_upload));

		dialog.findViewById(R.id.continue_confirm).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				mActivityInterface.showNetWaitDialog("数据保存中......");
				new Thread(new uploadThread()).start();
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
	/**
	 * 删除图片提示对话框
	 */
	private void showDeleteImageDialog() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext).create();
		} else if (dialog.isShowing()) {
			return;
		}

		dialog.show();
		dialog.setContentView(R.layout.layout_dialog);
		dialog.setCancelable(false);
		TextView textView = (TextView) dialog.findViewById(R.id.text_dialog);
		textView.setText(mContext.getResources().getString(R.string.dialog_imgdelete));

		dialog.findViewById(R.id.continue_confirm).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				mActivityInterface.showNetWaitDialog("删除中......");
				new Thread(new deleteImageThread()).start();
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

	private void reset() {
		edit_name.setText("");
		edit_angle.setText("");
		edit_content.setText("");
		edit_limit_speed.setText("");
		edit_orietation.setText("");
		edit_input_oriText.setText("");
		edit_name.requestFocus();
		camera_type = null;
		camera_type_text = null;
		type.setText("电子眼类型");
	}

	/**
	 * 0单次任务，1多次任务 根据任务类型选择点击保存或者提交后的页面
	 */
	private void goNextPage() {
		if (fromFlag == Configs.VIEW_POSITION_MY_TASK || from_position == Configs.VIEW_POSITION_DETAIL_TASK) {
			if (checkFiledisNull(false))
				updatePoint(MColums.POINT_FULL);
			else
				updatePoint(MColums.POINT_N_FULL);
			mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, null, null);
		} else if (point.getTaskType() == 0) {
			if (!checkFiledisNull(false)) {
			} else {
				savePoint(MColums.POINT_FULL);
				mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_NEW_TASK, null, null);
			}

		} else if (point.getTaskType() == 1) {
			if (!checkFiledisNull(false)) {
			} else {
				savePoint(MColums.POINT_FULL);
				/*mActivityInterface.showPrevious(Configs.VIEW_POSITION_DETAIL_ADD_POINT, this,
						Configs.VIEW_POSITION_CAMERA, null, null);*/
				//---------修改再次跳转至拍照页面-----2014-8-26
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("mTask", taskDetail);
				if (isLoc) {
					
					bundle.putBoolean("gpsIsOpen", isLoc);
				}else {
					
					bundle.putBoolean("gpsIsOpen", false);
				}
				
				bundle.putString("ImageName", "");
				bundle.putString("title",!StringUtils.isNullColums(taskDetail.getName())?taskDetail.getName():"" );
				intent.putExtras(bundle);
				intent.setClass(mContext, CustomCameraActivity.class);
				
				mContext.startActivity(intent);
				
				
			}
		}else if (point.getTaskType() == 2) {
			
			if (!checkFiledisNull(false)) {
			} else {
				savePoint(MColums.POINT_FULL);
				
				mActivityInterface.showPrevious(getMyViewPosition(), this,
						Configs.VIEW_POSITION_INDEX, null, null);
				
			}
		}

	}
    
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		LogPrint.Print("gps", "DetailPage--onLocationChanged---location="+location.getProvider());
		if (location.getProvider().equals(Configs.LOCATIONTYPE)) {
			this.mLocation=location;
			CustomCameraActivity.mlocation = location;
			isLoc = true;
			continue_camera.setEnabled(true);
		} else {
			isLoc = false;
			continue_camera.setEnabled(false);
		}
		//二期需求-没有GPS的情况下也可以启用
		continue_camera.setEnabled(true); 
	}

	/**
	 * 检测列项是否为空
	 * @param b=false(不验证)b=true(验证)
	 * @return true
	 */
	private boolean checkFiledisNull(boolean b) {
		
		if (type.getText().toString().equals("电子眼类型")) {
			if (b){
				
				Toast.makeText(mContext, "请选择电子眼类型", Toast.LENGTH_SHORT).show();
				return false;
			}
				
		}
		/*else if (edit_angle.getText().toString().equals("")) {
			
			if (b){
				
				Toast.makeText(mContext, "请输入角度", Toast.LENGTH_SHORT).show();
				return false;
			}
		}*/
		return true;
	}

	/**
	 * 保存采集点到数据库中
	 * 
	 * @param flag 用于判断信息是否添加完整 1为完整，2为不完整
	 *            信息是否完整
	 */
	private void savePoint(int flag) {

		TaskPoint points = new TaskPoint();
		points.setAngle(edit_angle.getText().toString());
		points.setCameraId(point.getCameraId());
		points.setContent(edit_content.getText().toString());
		points.setFlag(MColums.WAIT);
		points.setIname(point.getIname());
		points.setLat(point.getLat());
		points.setLon(point.getLon());
		points.setLspeed(edit_limit_speed.getText().toString());
		points.setName(edit_name.getText().toString());
		points.setOrder(point.getOrder());
		if (!StringUtils.isNullColums(edit_orietation.getText().toString())) {
			
			points.setOri(edit_orietation.getText().toString());
		}else {
			points.setOri(String.valueOf(0));
			
		}
		points.setTid(point.getTid());
		points.setType(!StringUtils.isNullColums(camera_type)?camera_type:"26");
		points.setCameraTypeText(!StringUtils.isNullColums(camera_type_text)?camera_type_text:"");
		points.setIsFull(flag);
		points.setTaskType(point.getTaskType());
		points.setTittle(tittle.getText().toString());
		points.setLastAlret(System.currentTimeMillis() + "");
		//DManager.getInstance(mContext).addPoint(points);
		//因在拍照页面已经做过保存操作，故在些进行更新操作(2014-09-30)
		DManager.getInstance(mContext).updatePoint(points, points.getCameraId());
	}

	/**
	 * 更新
	 * 
	 * @param flag 用于判断信息是否添加完整 1为完整，2为不完整
	 *            信息是否完整
	 */
	private void updatePoint(int flag) {

		TaskPoint points = new TaskPoint();
		points.setAngle(edit_angle.getText().toString());
		points.setCameraId(point.getCameraId());
		points.setContent(edit_content.getText().toString());
		points.setFlag(MColums.WAIT);
		points.setIname(point.getIname());
		points.setLat(point.getLat());
		points.setLon(point.getLon());
		points.setLspeed(edit_limit_speed.getText().toString());
		points.setName(edit_name.getText().toString());
		points.setOrder(point.getOrder());
		points.setOri(edit_orietation.getText().toString());
		points.setTid(point.getTid());
		points.setType(!StringUtils.isNullColums(camera_type)?camera_type:"");
		points.setCameraTypeText(!StringUtils.isNullColums(camera_type_text)?camera_type_text:"");
		points.setIsFull(flag);
		points.setTaskType(point.getTaskType());
		points.setTittle(point.getTittle());
		points.setLastAlret(System.currentTimeMillis() + "");
		DManager.getInstance(mContext).updatePoint(points, points.getCameraId());
	}

	private TaskPoint getPoint() {
		if (!isNull())
			return null;
		TaskPoint points = new TaskPoint();
		points.setAngle(edit_angle.getText().toString());
		points.setCameraId(point.getCameraId());
		points.setContent(edit_content.getText().toString());
		points.setFlag(MColums.WAIT);
		points.setIname(point.getIname());
		points.setLat(point.getLat());
		points.setLon(point.getLon());
		points.setLspeed(edit_limit_speed.getText().toString());
		points.setName(edit_name.getText().toString());
		points.setOrder(point.getOrder());
		if (!StringUtils.isNullColums(edit_orietation.getText().toString())) {
			
			points.setOri(edit_orietation.getText().toString());
			
		}else {
			
			points.setOri(String.valueOf(0));
			
		}
		points.setTid(point.getTid());
		points.setType(!StringUtils.isNullColums(camera_type)?camera_type:"");
		points.setCameraTypeText(!StringUtils.isNullColums(camera_type_text)?camera_type_text:"");
		points.setTaskType(point.getTaskType());
		points.setTittle(tittle.getText().toString());
		return points;
	}

	@SuppressLint({ "ShowToast", "ShowToast", "ShowToast" })
	private boolean isNull() {
		 /*private EditText edit_name;
		 private EditText edit_limit_speed;
		 private EditText edit_orietation;
		 private EditText edit_angle;
		 private EditText edit_content;
		if (type.getText().toString().equals("电子眼类型")) {
			handler.sendEmptyMessage(10);
			return false;
		}
		if (isNullColums(edit_name.getText().toString())) {
			handler.sendEmptyMessage(11);
			return false;
		}
		if (isNullColums(edit_orietation.getText().toString())) {
			handler.sendEmptyMessage(12);
			return false;
		}*/
		return true;
	}

	

	private class loadOneBitmap implements Runnable {

		@Override
		public void run() {
			String str = point.getTid() + "/" + point.getIname();
			ArrayList<Bitmap> listBitmap = BitmapUtils.getBitmapFromSDCard(str);

			Message message = handler.obtainMessage();
			message.obj = listBitmap;
			message.what = 8;
			handler.sendMessage(message);

		}

	}
	
	private class loadTwoBitmap implements Runnable {

		@Override
		public void run() {
			String str = point.getTid() + "/" + point.getIname();
			ArrayList<Bitmap> listBitmap = BitmapUtils.getBitmapFromSDCard(str);

			Message message = handler.obtainMessage();
			message.obj = listBitmap;
			message.what = 13;
			handler.sendMessage(message);

		}

	}

	private String tid = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 0:
					mActivityInterface.dismissNetDialog();
					scrollLayout.setVisibility(View.VISIBLE);
				break;
				case 1:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "图片加载失败", 0).show();
				break;
				case 3:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "数据加载失败", 0).show();
				break;
				case 4:
					String cod = (String) msg.obj;
					mActivityInterface.ShowCodeDialog(Integer.valueOf(cod),DetailPage.this);
				break;
				case 5:
					showCameraTypeList();
				break;
				case 6:
					setUploadImage();
				break;
				case 7:
					mActivityInterface.dismissNetDialog();
					savePoint(MColums.POINT_FULL);
					goNextPage();
					Toast.makeText(mContext, "提交失败，请检查网络", 2).show();
				break;
				case 8:
					scrollLayout.removeAllViews();
					ArrayList<Bitmap> listBitmap = (ArrayList<Bitmap>) msg.obj;
					if (listBitmap != null && listBitmap.size() > 0) {
						
						for (int i = 0; i < listBitmap.size(); i++) {
							RelativeLayout view_pageLayout = new RelativeLayout(mContext);
							ImageButton imageDelete = new ImageButton(mContext);    
							imageDelete.setId(imageDeleteId);   
							imageDelete.setOnClickListener(onClickListener);
							imageDelete.setBackgroundResource(R.drawable.clear_account);  
							
							
							 RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams  
							            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);    
							    lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);    
							    lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							ImageView imageView = new ImageView(mContext);
							imageView.setImageBitmap(listBitmap.get(i));
							//添加图片放大、缩小事件
							//imageView.setOnTouchListener(touchListener);
							view_pageLayout.addView(imageView,lp2);
							
						    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
						            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    
						    lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						    //RelativeLayout.TRUE 相对于父控件
						    lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);    
						    // imageDelete 位于父 View 的顶部，在父 View 中水平居中    
						     view_pageLayout.addView(imageDelete, lp1 );
							//---
						    
							
							scrollLayout.addView(view_pageLayout);
							
							/*ImageView imageView = new ImageView(mContext);
							imageView.setImageBitmap(listBitmap.get(i));
							scrollLayout.addView(imageView);*/
						}
						mActivityInterface.dismissNetDialog();
						//这个很重要指定默认为第一张
						scrollLayout.setDefaultScreen(0);
						scrollLayout.setVisibility(View.VISIBLE);
					}else {
						
						mActivityInterface.dismissNetDialog();
						scrollLayout.setVisibility(View.GONE);
					}
				break;
				case 9:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "网络未连接，请检查网络", 2).show();
				break;
				case 10:
					Toast.makeText(mContext, "请选择电子眼类型", Toast.LENGTH_SHORT).show();
				break;
				case 11:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "删除成功", 2).show();
					scrollLayout.setVisibility(View.GONE);
					if (image_First_Bitmap.getDrawable() != null) {
						image_First_Bitmap.setImageBitmap(null);
						image_First_Bitmap.setImageResource(R.drawable.intdex_page_add_app);
					}
					if (image_Second_Bitmap.getDrawable() != null) {
						image_Second_Bitmap.setImageBitmap(null);
						image_Second_Bitmap.setImageResource(R.drawable.intdex_page_add_app);
					}
					getImageBitmap();
					break;
				case 12:
					mActivityInterface.dismissNetDialog();
					Toast.makeText(mContext, "删除失败", 2).show();
					break;
				case 13:
					scrollLayout.removeAllViews();
					ArrayList<Bitmap> listBitmap1 = (ArrayList<Bitmap>) msg.obj;
					if (listBitmap1 != null && listBitmap1.size() > 0) {
						
						for (int i = 0; i < listBitmap1.size(); i++) {
							RelativeLayout view_pageLayout = new RelativeLayout(mContext);
							ImageButton imageDelete = new ImageButton(mContext);    
							imageDelete.setId(imageDeleteId);   
							imageDelete.setOnClickListener(onClickListener);
							imageDelete.setBackgroundResource(R.drawable.clear_account);  
							
							
							  RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams  
							            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);    
							    lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);    
							    lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							ImageView imageView = new ImageView(mContext);
							imageView.setImageBitmap(listBitmap1.get(i));
							view_pageLayout.addView(imageView,lp2);
							
						    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
						            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    
						    lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						    //RelativeLayout.TRUE 相对于父控件
						    lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);    
						    // imageDelete 位于父 View 的顶部，在父 View 中水平居中    
						     view_pageLayout.addView(imageDelete, lp1 );
							//---
						   
							scrollLayout.addView(view_pageLayout);
							
							/*ImageView imageView = new ImageView(mContext);
							imageView.setImageBitmap(listBitmap.get(i));
							scrollLayout.addView(imageView);*/
						}
						mActivityInterface.dismissNetDialog();
						//这个很重要指定默认为第二张
						scrollLayout.setDefaultScreen(1);
						scrollLayout.setVisibility(View.VISIBLE);
					}else {
						
						mActivityInterface.dismissNetDialog();
						scrollLayout.setVisibility(View.GONE);
					}
					break;
				   case MessageID.UPLOAD_SUCCESS_IMAGE_PATH:
					   String imagePath = msg.obj.toString();
						if (timer != null) {
							
							if (myTimerTask != null){
								
								myTimerTask.cancel();  //将原任务从队列中移除
								
							  }
							
						}else {
							
							timer = new Timer();
						}
						
						myTimerTask = new TimerTaskGetImageLength(imagePath, mContext, handler);
						//轮询校验图片是否上传成功
						timer.schedule(myTimerTask,3*1000);
				     break;
				   case MessageID.UPLOAD_SUCCESS_IMAGE:
					   timerAndTimerTaskCancel();
					   Toast.makeText(mContext, "提交成功", 0).show();
						mActivityInterface.dismissNetDialog();
						tid = taskDetail.getId();
						taskDetail.setFalg(MColums.OLD);
						DManager.getInstance(mContext).addOldTask(taskDetail);
						if (taskDetail.getType().equals("0")) {
							DManager.getInstance(mContext).deleteTaskDetail(taskDetail.getId());
						}

						TaskPoint point = getPoint();
						boolean bl = DManager.getInstance(mContext).queryPointExite(DataBaseHelper.TABNAME_POINT,
								point.getCameraId());

						point.setFlag(MColums.OLD);
						point.setIsFull(MColums.POINT_FULL);
						point.setLastAlret(System.currentTimeMillis() + "");
						if (bl) {
							DManager.getInstance(mContext).updatePoint(point, point.getCameraId());
							/*if (fromFlag == Configs.VIEW_POSITION_MY_TASK || from_position == Configs.VIEW_POSITION_DETAIL_TASK) {
								goTaskPage();
								break;
							}*/
							goTaskPage();
							break;
						} else {
							DManager.getInstance(mContext).addPoint(point);
						}

						goNextPage();
					   
					   break;
				   case MessageID.UPLOAD_FAIL_IMAGE:
						Toast.makeText(mContext, "图片提交失败", Toast.LENGTH_SHORT).show();
						mActivityInterface.dismissNetDialog();
						break;
				   case MessageID.UPLOAD_FAIL_NOIMAGE:
						Toast.makeText(mContext, "任务信息提交失败，检测到没有图片", Toast.LENGTH_SHORT).show();
						break;
				default:
				break;
			}
		};
	};

	private void goTaskPage() {

		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MY_TASK, null, null);
	}

	private TaskDetail taskDetail;

	public void setDetailItem(com.mapbar.info.collection.bean.TaskDetail task) {
		this.taskDetail = task;
	}

	@Override
	public void setDataBridge(Object object) {
		point = (TaskPoint) object;
		super.setDataBridge(object);
	}

	public Bitmap showView(String path) {

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 8;

		int degree = BitmapUtils.readPictureDegree(path);

		Bitmap cameraBitmap = BitmapFactory.decodeFile(path, bitmapOptions);

		Bitmap bitmap = BitmapUtils.rorateBitamp(degree, cameraBitmap);
		return bitmap;
	}

	private MHttpHandler httpHandler;
	/**
	 * 存储电子眼类型
	 */
	private HashMap<String, String> cameraMap = new HashMap<String, String>();

	/**
	 * 获取电子眼类型(由于每次获取电子类型都需要请求服务器导致请求过慢，此方法放弃使用)
	 */
	protected void getCameraList() {
		String url = UrlConfig.cameraTypeUrl + "Id=" + point.getTid() + "&token=" + StringUtils.getLoginToken(mContext) + "&loginId="
				+ StringUtils.getLoginId(mContext);
		httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				Log.e("getCameraList", "" + new String(data));
				if (data != null) {

					try {
						JSONObject jsonObject = new JSONObject(new String(data));
						boolean bl = jsonObject.getBoolean("result");
						if (bl) {
							cameraMap.clear();
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							dataJson.length();
							for (int i = 1; i < dataJson.length() + 1; i++) {
								String name = dataJson.getString(i + "");
								cameraMap.put("" + i, name);
							}
							handler.sendEmptyMessage(5);
						} else {
							Message message = handler.obtainMessage();
							message.what = 4;
							message.obj = jsonObject.getString("message");
							handler.sendMessage(message);

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					handler.sendEmptyMessage(3);
				}
			}
		});
		httpHandler.execute();

	}

	@Override
	public void onDetachedFromWindow(int flag) {
		scrollLayout.removeAllViews();
		//scrollLayout.setDefaultScreen(0);
		super.onDetachedFromWindow(flag);
	}

	@Override
	public void onDestroy() {
		cameraMap.clear();
		super.onDestroy();
	}

	/**
	 * 显示电子眼类型
	 */
	public void showCameraTypeList() {
		final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		if (!dialog.isShowing())
			dialog.show();
		dialog.setContentView(R.layout.layout_dialog_camera_type);
		dialog.setCancelable(false);

		ListView dialog_list = (ListView) dialog.findViewById(R.id.layout_dialog_list);
		dialog_list.setAdapter(new ChoiceAdatpter(cameraMap));
		dialog_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setTypeCamera(position + 1 + "");
				dialog.dismiss();
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});

	}

	private void setTypeCamera(String id) {
		camera_type = id;
		camera_type_text = cameraMap.get(id);
		type.setText(cameraMap.get(id));
		//放到缓存里
		SharedPreferencesUtil.saveCameraTypeText(mContext,camera_type_text);
		SharedPreferencesUtil.saveCameraTypeId(mContext, camera_type);
	}

	/**
	 * 上传提交任务
	 */
	private void upLoadFile() {

		// 判断网络是否有效
		if (Util.isNetworkAvailable(mContext)) {
			if (point == null) {
				mActivityInterface.dismissNetDialog();
				Toast.makeText(mContext, "提交失败", 0).show();
				return;
			} else {

				FileUtil fileUtil = new FileUtil(mContext);
				Map<String, String> map = new HashMap<String, String>();
				map.put("taskId", point.getTid());
				map.put("loginId", StringUtils.getLoginId(mContext));
				map.put("id", point.getCameraId());
				map.put("name", point.getName());
				//如果电子眼类为空，则默认为26
				map.put("type", !StringUtils.isNullColums(camera_type)?camera_type:"26");
				map.put("speed", point.getLspeed());
				map.put("angle", point.getAngle());
				map.put("direction", point.getOri());
				map.put("description", point.getContent());
				//单号
				map.put("order", point.getOrder());
				//图片名称
				map.put("iname", point.getIname());
				// 经度
				map.put("longitude",String.valueOf(point.getLon()));
				// 纬度
				map.put("latitude", String.valueOf(point.getLat()));
				// GPS速度
				map.put("gpsSpeed", String.valueOf(point.getLspeed()));
				// GPS角度
				map.put("gpsAngle", String.valueOf(point.getAngle()));

				if (mLocation != null) {
					
					// psTime
					map.put("gpsTime", String.valueOf(gpsDateFormat.format(mLocation.getTime())));
					// GPS海拔
					map.put("gpsHeight",String.valueOf(mLocation.getAltitude()));
				} else {
					map.put("gpsTime", String.valueOf(gpsDateFormat.format(System.currentTimeMillis())));
					map.put("gpsHeight", String.valueOf(0));
				}
				// 任务 ID+图片名称 的图片路径
				String str = point.getTid() + "/" + point.getIname();
				FormFile[] files = BitmapUtils.getFileList(str);
				if (files != null && files.length > 0){
					
					map.put("fileUpload", String.valueOf(files.length));
					//判断如是有信息上传成功，但是图片没上传成功的任务
					String pointStatus = DManager.getInstance(mContext).queryPointStatus(DataBaseHelper.TABNAME_POINT, point.getCameraId());
					if (pointStatus != null && pointStatus.equals("old")) {
						
						//开启线程上传任务采集点图片
						new Thread(new dThreadTaskImage(point)).start();
						
					}else {
						
						String resultString = fileUtil.postFileCompress(UrlConfig.uploadUrl+"token="+StringUtils.getLoginToken(mContext)+"&", map,null);
						if (!StringUtils.isNullColums(resultString)) {
							
							try {
								
								JSONObject jsonObject = new  JSONObject(resultString);
								boolean result = jsonObject.getBoolean("result");
								LogPrint.Print("postmessage=="+jsonObject.getString("message"));
								if (result) {
									
									handler.sendEmptyMessage(6);
									
								}else {
									
									// 提交失败
									handler.sendEmptyMessage(7);
								}
								
							} catch (Exception e) {
								
								e.printStackTrace();
								// 提交失败
								handler.sendEmptyMessage(7);
							}
						}else {
							
							// 提交失败
							handler.sendEmptyMessage(7);
						}
					}
					
				}else {
					
					handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_NOIMAGE);
				}
				
			
			}

		} else {

			handler.sendEmptyMessage(9);
		}

	}
	/**
	 * 上传采集点图片
	 */
	private void setUploadImage(){
		
		TaskPoint point = getPoint();
		//开启线程上传任务采集点图片
		new Thread(new dThreadTaskImage(point)).start();
	}
	/**
	 * 单独提交任务图片
	 * @param point
	 */
	private void upLoadFileImage(TaskPoint point) {
		//判断网络是否有效
		if (Util.isNetworkAvailable(mContext)) {
			Message message = handler.obtainMessage();
			if (point == null) {
				//activityInterface.dismissNetDialog();
				Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
				return;
			}else {
				FileUtil fileUtil = new FileUtil(mContext);
				Map<String, String> map = new HashMap<String, String>();
				map.put("taskId", point.getTid());
				map.put("loginId", StringUtils.getLoginId(mContext));
				map.put("id", point.getCameraId());
				map.put("name", point.getName());
				//如果电子眼类为空，则默认为26
				map.put("type", !StringUtils.isNullColums(point.getType())?point.getType():"26");
				map.put("speed", point.getLspeed());
				map.put("angle", point.getAngle());
				map.put("direction", point.getOri());
				map.put("description", point.getContent());
				//单号
				map.put("order", point.getOrder());
				//图片名称
				map.put("iname", point.getIname());
				map.put("longitude",String.valueOf(point.getLon()));
				map.put("latitude", String.valueOf(point.getLat()));
				
				//任务 ID+图片名称 的图片路径
				String str = point.getTid() + "/" + point.getIname();
				String resultString = fileUtil.postFileCompress(UrlConfig.SAVEIMAGE_URL+"token="+StringUtils.getLoginToken(mContext)+"&", map, BitmapUtils.getFileList(str));
				if (!StringUtils.isNullColums(resultString)) {
					
					try {
						
						JSONObject jsonObject = new  JSONObject(resultString);
						boolean result = jsonObject.getBoolean("result");
						LogPrint.Print("postmessage=="+jsonObject.getString("message"));
						if (result) {
							//请求校验图片是否上传成功,因为在上传图片时，后台返回的都是成功，即使失败也会返回成功
							//校验返回头部信息的长度大小Content-Length
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							String imagePath = (String)dataJson.getString("url-1");
						
							message.obj = imagePath;
							message.what = MessageID.UPLOAD_SUCCESS_IMAGE_PATH;
							handler.sendMessage(message);
							  
							
						}else {
							
							handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
						}
						
					} catch (Exception e) {
						
						e.printStackTrace();
						handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
					}
				}else {
					
					handler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
				}
				
			}
			
		}else {
			
			handler.sendEmptyMessage(9);
		}
		
		
	}
	/**
	 * 删除图片
	 * @author miaowei
	 *
	 */
	private class deleteImageThread implements Runnable {
		@Override
		public void run() {
			try {
				int index = scrollLayout.getCurScreen();
				LogPrint.Print("deleteImageThread===index="+index);
				String str = point.getTid() + "/" + point.getIname();
				ArrayList<String> pathList = BitmapUtils.getPhotoById(str);
				if (pathList != null && pathList.size() > 0) {
					if (index == 0) {
						deleteFile(SdcardUtil.getSdcardCollInfo() + str + "/" + pathList.get(0));
					}else if (index == 1) {
						deleteFile(SdcardUtil.getSdcardCollInfo() + str + "/" + pathList.get(1));
					}
				}
				handler.sendEmptyMessage(11);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(12);
			}
			
		}
		
	}
	/**
	 * 根据文件路径删除文件
	 */
	private void deleteFile(String imageUrl) {

		File file = new File(imageUrl);
		if (file.exists()) {

			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].exists()) {

						files[i].delete();
					}
				}

			} else {

				file.delete();
			}
		}
	}
	/**
	 * 提交采集点信息
	 * @author miaowei
	 *
	 */
	private class uploadThread implements Runnable {
		@Override
		public void run() {

			upLoadFile();
		}
	}
	
	/**
	 * 提交采集点图片
	 * @author miaowei
	 *
	 */
	private class dThreadTaskImage implements Runnable {
		private TaskPoint taskPoint;

		private dThreadTaskImage(TaskPoint point) {
			taskPoint = point;
		}

		@Override
		public void run() {
			upLoadFileImage(taskPoint);
		}
	}

	private class ChoiceAdatpter extends BaseAdapter {

		private HashMap<String, String> data;

		public ChoiceAdatpter(HashMap<String, String> str) {
			data = str;
		}

		@Override
		public int getCount() {

			return data.size();
		}

		@Override
		public Object getItem(int position) {

			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View itemView, ViewGroup parent) {
			ViewHoler viewHoler;
			if (itemView == null) {
				itemView = View.inflate(mContext, R.layout.layout_dialog_list_item, null);
				viewHoler = new ViewHoler();
				viewHoler.adrView = (TextView) itemView.findViewById(R.id.dialog_list_item_text);
				itemView.setTag(viewHoler);
			} else {
				viewHoler = (ViewHoler) itemView.getTag();
			}

			viewHoler.adrView.setText(data.get(position + 1 + ""));

			return itemView;
		}

	}

	class ViewHoler {
		TextView adrView;
	}
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch (v.getId())
			{
				case R.id.btn_home: //返回
					goBack();
				break;
				case R.id.detail_btn_reset:
					reset();
				break;
				case R.id.detail_btn_commit: //提交
					boolean b = checkFiledisNull(true);
					if (b) {
						showUploadDialog();
					}
				break;
				case R.id.detail_btn_save: //保存
					boolean check = checkFiledisNull(true);
					if (check) {
						
						goNextPage();
					}
					
				break;
				case R.id.detail_show: //查看第一张已拍摄图片
					String str = point.getTid() + "/" + point.getIname();
					ArrayList<Bitmap> listBitmap = BitmapUtils.getBitmapFromSDCard(str);
					if (listBitmap != null && listBitmap.size() >= 1) {
						
						new Thread(new loadOneBitmap()).start();
						mActivityInterface.showNetWaitDialog("图片加载中......");
					}else {
						
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("mTask", taskDetail);
						if (isLoc) {
							
							bundle.putBoolean("gpsIsOpen", isLoc);
						}else {
							
							bundle.putBoolean("gpsIsOpen", false);
						}
						
						bundle.putBoolean("isContinueCamear", true);
						bundle.putString("ImageName", !StringUtils.isNullColums(point.getIname())?point.getIname():"");
						bundle.putString("title",!StringUtils.isNullColums(taskDetail.getName())?taskDetail.getName():"" );
						intent.putExtras(bundle);
						intent.setClass(mContext, CustomCameraActivity.class);
						
						mContext.startActivity(intent);
					}
					
				break;
				case R.id.detail_type: //电子眼类型
					//缓存是否有数据
				try {
					if (cameraMap != null && cameraMap.size() > 0) {
						
						handler.sendEmptyMessage(5);
						
					}else {
						
						InputStream inStream = mContext.getResources().getAssets().open("camertype.xml");
						cameraMap.clear();
						cameraMap = parseXml.parseXml(inStream);
						handler.sendEmptyMessage(5);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					CustomToast.show(mContext, "解析电子眼类型失败");
				}
				break;
				case R.id.detail_camera_continue: //查看第二张已拍摄图片
					String str1 = point.getTid() + "/" + point.getIname();
					ArrayList<Bitmap> listBitmap1 = BitmapUtils.getBitmapFromSDCard(str1);
					if (listBitmap1 != null && listBitmap1.size() == 2) {
						
						new Thread(new loadTwoBitmap()).start();
						mActivityInterface.showNetWaitDialog("图片加载中......");
						
					}else {
						
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("mTask", taskDetail);
						if (isLoc) {
							
							bundle.putBoolean("gpsIsOpen", isLoc);
						}else {
							
							bundle.putBoolean("gpsIsOpen", false);
						}
						
						bundle.putBoolean("isContinueCamear", true);
						bundle.putString("ImageName", !StringUtils.isNullColums(point.getIname())?point.getIname():"");
						bundle.putString("title",!StringUtils.isNullColums(taskDetail.getName())?taskDetail.getName():"" );
						intent.putExtras(bundle);
						intent.setClass(mContext, CustomCameraActivity.class);
						
						mContext.startActivity(intent);
						/*if (fromFlag == Configs.VIEW_POSITION_MY_TASK_CHILD) {
							mActivityInterface.setData(taskDetail);
							mActivityInterface.setLocation(location);
							mActivityInterface.setImageName(point.getIname());
							mActivityInterface.showPrevious(Configs.VIEW_POSITION_MY_TASK_CHILD, this,
									Configs.VIEW_POSITION_CAMERA, null, null);
							break;
						}

						mActivityInterface.setImageName(point.getIname());
						mActivityInterface.showPrevious(Configs.VIEW_POSITION_DETAIL_OPEN_CAMREA, this,
								Configs.VIEW_POSITION_CAMERA, null, null);*/
					}
					
				break;
				case imageDeleteId:
					showDeleteImageDialog();
				break;
				default:
				break;
			}
			
		}
	};
	
	/**
	 * 取消定时器任务
	 */
	private void timerAndTimerTaskCancel() {
		if (timer != null) {
			
			timer = null;
			//ThreadGetImageLength.getInstance().cancel();
		}
	}
	//------------------------以下实现图片放大、缩小功能-----------------------------
	
	private enum Type{
		/**
		 * 记录是拖放照片模式还是放大缩小照片模式
		 */
		MODE,
		/**
		 * 放大缩小照片模式
		 */
		MODE_ZOOM
	}
	
	/**
	 * 内部类，用于设置事件
	 * @author miaowei
	 *
	 */
	private class MainEventEnmu extends EventInfoBase<Enum<?>>{
		
		@Override
		public void setEvent(Enum<?> event) {
			
			super.setEvent(event);
		}
	}
	/**
	 * 图片放大、缩小事件
	 */
	private OnTouchListener touchListener = new OnTouchListener() {
		/**
		 * 用于记录开始时候的坐标位置
		 */
		private PointF startPoint = new PointF();
		/**
		 * 用于记录拖拉图片移动的坐标位置
		 */
		private Matrix matrix = new Matrix();
		/**
		 * 用于记录图片要进行拖拉时候的坐标位置
		 */
		private Matrix currentMatrix = new Matrix();
		/**
		 * 两个手指的开始距离
		 */
	    private float startDis;
	    /**
	     * 两个手指的中间点
	     */
	    private PointF midPointF;
	    MainEventEnmu mainEventEnmu = new MainEventEnmu();
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		
			//通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			//手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				//记录ImageView当前的移动位置
				currentMatrix.set(((ImageView)v).getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
				//手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				//放大缩小图片 
				if (mainEventEnmu.getEvent() == Type.MODE_ZOOM) {
					//结束距离
					float endDis = distance(event);
					// 两个手指并拢在一起的时候像素大于10
					if (endDis > 10f) {
						// 得到缩放倍数
						float scale = endDis / startDis;
						
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale,midPointF.x,midPointF.y);
					}
				}
				break;
				// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				//当触点离开屏幕，但是屏幕上还有触点(手指)，多点
			case MotionEvent.ACTION_POINTER_UP:
				mainEventEnmu.setEvent(Type.MODE);
				break;
				// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕，多点
			case MotionEvent.ACTION_POINTER_DOWN:
				mainEventEnmu.setEvent(Type.MODE_ZOOM);
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				// 两个手指并拢在一起的时候像素大于10
				if (startDis > 10f) {
					
					midPointF = mid(event);
					 //记录当前ImageView的缩放倍数
					currentMatrix.set(((ImageView)v).getImageMatrix());
				}
				break;
			}
			((ImageView)v).setImageMatrix(matrix);
			return true;
		}
	};
	/**
	 * 计算两个手指间的距离
	 * @param event
	 * @return
	 */
	private float distance(MotionEvent event){
		
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		//使用勾股定理返回两点神之间的距离
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
	/**
	 * 计算两个手指间的中间点
	 * @return
	 */
	private PointF mid(MotionEvent evntEvent){
		
		float midX = (evntEvent.getX(1) + evntEvent.getX(0));
		float midY = (evntEvent.getY(1) + evntEvent.getY(0));
		
		return new PointF(midX, midY);
		
	}
	
}

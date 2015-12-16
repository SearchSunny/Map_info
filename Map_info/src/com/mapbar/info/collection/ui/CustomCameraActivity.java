package com.mapbar.info.collection.ui;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbar.android.location.CellLocationProvider;
import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.MyLocation;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.DManager;
import com.mapbar.info.collection.db.DataBaseHelper;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StorageHelper;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;


/**
 * 自定义Camear并实现手机传感器
 * @author miaowei
 *
 */
@SuppressLint("NewApi")
public class CustomCameraActivity extends Activity implements SensorEventListener,SurfaceHolder.Callback{

	/** 相机   **/
    private Camera mCamera;  
    /** 缩略图  **/
    ImageView ThumbsView;
    FrameLayout mFrameLayout;
    /**
     * 正在拍的照片
     */
    private FrameLayout FramePreviewView;
    /**
     * 拍好的临时照片
     */
    private FrameLayout FrameThumbsView;
    /**
     * 取消
     */
    private Button BtnCancel;
    /**
     * 确定
     */
    private Button BtnOk;
    /**
     * 继续拍照按钮 
     */
    private Button BtnContinue; 
    /**
     * 单次拍照按钮 
     */
    private Button BtnSingle;  
    /**
     * 拍照(继续拍照页面过来使用)
     */
    private Button BtnCamera;
    /**
	 * GPS是否打开(true=打开,false=未打开)
	 */
	private boolean gpsIsOpen;
	
	private static ActivityInterface mActivityInterface;
	/**
	 * 图片的存放路径
	 */
	private String path;
	/**
	 * 图片名称
	 */
	private String image_name;
	/**
	 * 创建以 任务ID + 图片名称的实体图片路径
	 */
	private File fils;
	/**
	 * 任务详情
	 */
	private TaskDetail mTask;
	/**
	 * 处理与服务器交互
	 */
	private HttpHandler httpHandler;
	/**
	 * 任务点
	 */
	private TaskPoint point;
	public static Location mlocation;
	private CustomCameraActivity page;
	private MyLocation myLocation;
	private CellLocationProvider cellLocationProvider;
	
	private String title;
	/**
	 * 定义传感器SensorManager
	 */
	private SensorManager mSensorManager;
	/**
	 * 手机方向
	 */
	private int sensorOrientation;
	/**
	 * 手机方向  x值
	 */
	private int orientation; 
	/**
	 * 拍照的临时预览文件
	 */
	private File mPictureFile;
	
	private SimpleDateFormat dateFormat;
	/**
	 * 随手拍任务创建时间
	 */
	private SimpleDateFormat aboutDateFormat;
	/**
	 * 获取屏幕方向
	 */
	Configuration config;
	/**
	 * 标识是否第一次抢单(true第一次抢单成功)
	 */
	public  boolean isFirst;
	/**
	 * 抢单是否成功
	 */
	private boolean isGetOrderSuccess;
	/**
	 * 点击拍照时的Progress
	 */
	private View mProgressContainer;
	/**
	 * 
	 */
	private SurfaceView mSurfaceView;
	/**
	 * 是否继续拍照(true是 false否)
	 * 是否从继续拍照页面过来的
	 */
	private boolean isContinueCamear;
	/**
	 * 任务点图片的数量
	 */
	private int count;
	/**
	 * 是连拍或单拍(true连拍/false单拍)
	 */
	private boolean isContinueAndSingle = true;
	/**
	 * 标识是否随手拍(true是)
	 */
	private boolean isAbout_task = false;
	/**
	 * 任务类型(1-多次 0-单次 2-随手拍)
	 */
	private String taskType;
	/**
	 * 旋转之后的图片，定义为全局是因为在取消时需要手动回收
	 */
	private Bitmap mBitmap;
	/**
	 * 屏幕方向值
	 */
	private int rotation;
	/**
	 * 内部类，获取屏幕方向
	 */
	private MyOrientationEventListener myOrientationEventListener;
	/**
	 * 设备型号名称
	 */
	private String deviceName;
	/**
	 * 获取操作系统版本
	 */
	private String deviceVersion;
	/**
	 * GPS定位
	 */
	//private LocationManager locationManager;
	
	/*public CustomCameraActivity(Context context, View view, ActivityInterface aif, CustomCameraActivity page,boolean isOpenCamera) {
		
		this.page = page;
		this.this = context;
		this.mActivityInterface = aif;

		FramePreviewView = (FrameLayout)view.findViewById(R.id.FramePreviewView);
        FrameThumbsView = (FrameLayout)view.findViewById(R.id.FrameThumbsView);
        ThumbsView = (ImageView)view.findViewById(R.id.ThumbsView);
        
        BtnOk = (Button)view.findViewById(R.id.BtnOk);
        BtnCancel = (Button)view.findViewById(R.id.BtnCancel);
        
        BtnCapture = (Button)view.findViewById(R.id.BtnCapture);
        mFrameLayout = (FrameLayout)view.findViewById(R.id.PreviewView); 
        BtnCancel.setOnClickListener(onClickListener);
        BtnCapture.setOnClickListener(onClickListener);
        BtnOk.setOnClickListener(onClickListener);
        
        if (isOpenCamera) {
        	
        	if(CheckCameraHardware(this)==false)
            {
                Toast.makeText(this, "很抱歉，您的设备可能不支持摄像头功能！", Toast.LENGTH_SHORT).show();
                return;
            }
        	//** 获取相机  **//*
            mCamera=getCameraInstance();
            //** 获取预览界面   **//*
            mPreview = new CameraPreview(this, mCamera); 
            
            mFrameLayout.addView(mPreview); 
            mCamera.startPreview();
		}
        
		
	}*/
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		//取得SensorManager实例
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//myLocation = new MyLocation(this, mActivityInterface);
		
    	point = new TaskPoint();
    	dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    	aboutDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	config = getResources().getConfiguration();
    	deviceName = Util.getDeviceName();
    	deviceVersion = Util.getOs_Version();
    	gpsIsOpen = getIntent().getBooleanExtra("gpsIsOpen", false);

    	image_name = getIntent().getExtras().getString("ImageName");
    	title = getIntent().getExtras().getString("title");
    	isContinueCamear = getIntent().getExtras().getBoolean("isContinueCamear", false);
    	isFirst = getIntent().getExtras().getBoolean("isCameraAndRob", false);
    	
    	if (getIntent().getExtras().getSerializable("mTask") != null) {
		
    		mTask = (TaskDetail)getIntent().getExtras().getSerializable("mTask");
    		taskType = mTask.getType();
    		if (taskType.equals("2")) {
				
    			isAbout_task = true;
    			//随手拍没有抢单
    			isFirst = false;
			}else {
				
				isAbout_task = false;
			}
    		
		}else {
			
			mTask = new TaskDetail();
			mTask.setId("00000000");
			mTask.setDescription("随手拍");
			mTask.setFalg(MColums.WAIT);
			mTask.setName("随手拍");
			mTask.setType(String.valueOf(2));
			mTask.setRice(0.5);
			isAbout_task = true;
			//随手拍没有抢单
			isFirst = false;
			mTask.setStartTime(aboutDateFormat.format(System.currentTimeMillis()));
		}
    	
    	LogPrint.Print("isFirst============"+isFirst);
    	myOrientationEventListener = new MyOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
    	
    	//** 隐藏标题栏  **//
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        //** 隐藏状态栏  **//
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.layout_camera_custom);
        //** 硬件检查  **//
        if(CheckCameraHardware(this)==false)
        {
            Toast.makeText(this, "很抱歉，您的设备可能不支持摄像头功能！", Toast.LENGTH_SHORT).show();
            return;
        }
        FramePreviewView = (FrameLayout)findViewById(R.id.FramePreviewView);
        FrameThumbsView = (FrameLayout)findViewById(R.id.FrameThumbsView);
        ThumbsView = (ImageView)findViewById(R.id.ThumbsView);
        
        
        mProgressContainer = findViewById(R.id.crime_camera_progress);
		mProgressContainer.setVisibility(View.GONE);
        
        BtnOk = (Button)findViewById(R.id.BtnOk);
        BtnCancel = (Button)findViewById(R.id.BtnCancel);
        
        BtnContinue = (Button)findViewById(R.id.BtnContinue);
        BtnSingle = (Button)findViewById(R.id.BtnSingle);
        
        BtnCamera = (Button)findViewById(R.id.BtnCamera);
        
        BtnContinue.setOnClickListener(onClickListener);
        BtnSingle.setOnClickListener(onClickListener);
        BtnCamera.setOnClickListener(onClickListener);
        
        BtnCancel.setOnClickListener(onClickListener);
        BtnOk.setOnClickListener(onClickListener);
       
        
        mSurfaceView = (SurfaceView)findViewById(R.id.PreviewView); 
        SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
       

    	if (isContinueCamear) {
			//循环图片数量
            String str = mTask.getId() + "/" + image_name;
               //根据需求拍照时一个点最多允许两张照片 2014年8月11日
            count = BitmapUtils.getPhotoCount(str);
    		 if (taskType.equals("0") && count >= 1) {
				
	             isContinueAndSingle = false; //默认为单拍
			}
    		 
    		 BtnContinue.setVisibility(View.GONE);
    		 BtnSingle.setVisibility(View.GONE);
    		 BtnCamera.setVisibility(View.VISIBLE);
           	
		 }else {
			
			 BtnCamera.setVisibility(View.GONE);
			 BtnContinue.setVisibility(View.VISIBLE);
    		 BtnSingle.setVisibility(View.VISIBLE);
		}
	
    }
    /**
     * 点击事件
     */
    private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			sensorOrientation = orientation;
            //gpsIsOpen = true;
			switch (v.getId()) {
			case R.id.BtnContinue: //连续拍照
				LogPrint.Print("sensorOrientation手机方向======"+sensorOrientation);
				if (count >= 2) {
					
					 Toast.makeText(CustomCameraActivity.this, "一个点最多允许两张照片",3).show();
					 return;
				   }
				//gps是否打开
				if ((mlocation != null && mlocation.getProvider().equals(Configs.LOCATIONTYPE))) {
					//Toast.makeText(CustomCameraActivity.this, "经度"+mlocation.getLongitude()+"纬度"+mlocation.getLatitude(),3).show();
					isContinueAndSingle = true;
					if (!isAbout_task) {
						
                		if (!isFirst && !isContinueCamear) {
                			
                			getOreder();
                			
    					}else {
							
    						mCamera.takePicture(mShutterCallback, null,mPicureCallback);
						}
                		
                		//如果抢单成功,或者是从继续拍照页面过来的
                    	/*2014-12-20 if (isGetOrderSuccess || isContinueCamear || isFirst) {
							
                    		mCamera.takePicture(mShutterCallback, null, mPicureCallback);
						}*/
                		
					}else{
						
                        mCamera.takePicture(mShutterCallback, null, mPicureCallback);
                       
					}
            		
            	}else {
            		//enableMyLocation();
					Toast.makeText(CustomCameraActivity.this, "尚未连接GPS，请稍候拍照", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnSingle: //单次拍照
				LogPrint.Print("sensorOrientation手机方向======"+sensorOrientation);
				if (count >= 2) {
					
					 Toast.makeText(CustomCameraActivity.this, "一个点最多允许两张照片",3).show();
					 return;
				   }
				//gps是否打开
            	if (mlocation != null && mlocation.getProvider().equals(Configs.LOCATIONTYPE)) {
            	    //Toast.makeText(CustomCameraActivity.this, "经度"+mlocation.getLongitude()+"纬度"+mlocation.getLatitude(),3).show();
            		isContinueAndSingle = false;
            		
            		mCamera.takePicture(mShutterCallback, null, mPicureCallback);
            		
            	}else {
            		//enableMyLocation();
					Toast.makeText(CustomCameraActivity.this, "尚未连接GPS，请稍候拍照", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnCamera: //拍照
				LogPrint.Print("sensorOrientation手机方向======"+sensorOrientation);
				if (count >= 2) {
					
					 Toast.makeText(CustomCameraActivity.this, "一个点最多允许两张照片",3).show();
					 return;
				   }
				//gps是否打开
            	if (mlocation != null && mlocation.getProvider().equals(Configs.LOCATIONTYPE)) {
            		isContinueAndSingle = false;
            		mCamera.takePicture(mShutterCallback, null, mPicureCallback);
            	}else {
            		//enableMyLocation();
					Toast.makeText(CustomCameraActivity.this, "尚未连接GPS，请稍候拍照", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnCancel: //取消
				deletePhoto();
				count--;
				if (mBitmap != null && !mBitmap.isRecycled()){
	            	
					mBitmap.recycle();
	            }
				/**开始预览**/
	            mCamera.startPreview();
				FramePreviewView.setVisibility(View.VISIBLE);
				FrameThumbsView.setVisibility(View.GONE);
				break;
			case R.id.BtnOk: //确定
				//如果当前任务已被抢单
				if(isFirst || isContinueCamear){
					savePoint(1);
					OnFinish();
					goTODetailPage();
				}else if (isAbout_task) {
					
					mHandler.sendEmptyMessage(800000);
					
				}else{
					//开始抢单
					getOreder();
				}
				break;
			default:
				break;
			}
			
		}
	};
	public void OnFinish(){
		if(mActivityInterface != null){
			
			mActivityInterface.dismissNetDialog();
		}
		this.finish();
		
	}
	
	/**
	 * 相机捕获图像时调用
	 */
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			mPictureFile = StorageHelper.getOutputFile(mTask, image_name);
            //抢单成功之后需要设置图片名称point.setIname(image_name);故在此先赋值
            image_name = StorageHelper.imageName;
            if (deviceName.equals("U8860")) {
				
            	FramePreviewView.setVisibility(View.GONE);
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			
		}
	};
	
    /** 拍照回调接口  **/
    private PictureCallback mPicureCallback=new PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] mData, Camera camera) 
        {
        	    LogPrint.Print("拍照回调接口");
                 if (mPictureFile == null){
                	 
                     return; 
                 }
                Message message =  mHandler.obtainMessage();
                message.what=600000;
                message.obj = mData;
                mHandler.sendMessage(message);
                //camearPhoto(mData);
                
				
        }
        
    };
    
    /**
     * 处理照相机拍摄之后的图片
     * @param mData
     */
    public void camearPhoto(byte[] mData){
        try {
        	
        	//是连拍还是单拍
        	if (isContinueAndSingle) {
        		FileOutputStream fos = null;
        		try {
        			/*Message message = mHandler.obtainMessage();
 		            Bundle bundle = new Bundle();
 		            bundle.putByteArray("mData", mData);
 		            bundle.putString("path", mPictureFile.getAbsolutePath());
 		            message.setData(bundle);
 		            message.what = 900000;
 		            mHandler.sendMessage(message);*/
                	File file = StorageHelper.getOutputFile(mTask, image_name);
                	if (file == null) {
        				
                		return;
        			}
            		//----------直接存储照片--------------------------
                	fos = new FileOutputStream(mPictureFile); 
                    fos.write(mData);
                    fos.flush();
                    fos.close();
                    //-----------------------------------------------------------
                    //往图片上加经纬度等属性信息
                    bitmapAddExif(mPictureFile.getAbsolutePath());
                    mProgressContainer.setVisibility(View.GONE);
                    if (deviceName.equals("U8860")) {
						
                    	FramePreviewView.setVisibility(View.VISIBLE);
					}
                    //如果不是从继续拍照过来的,则直接保存
                    if (!isContinueCamear) {
                    	if (isAbout_task) {
    						
                        	mHandler.sendEmptyMessage(800000);
                        	
    					}else{
    						
    						savePoint(1);
    					}
                   	 
        			}else {
        				
        				count ++;
        			}
                    
                    
                    mCamera.setPreviewCallbackWithBuffer(null);
                    /**停止预览**/
                    mCamera.stopPreview();
                    /**开始预览**/
                    mCamera.startPreview();
                	
				} catch (Exception e) {
					mProgressContainer.setVisibility(View.GONE);
					CustomToast.show(CustomCameraActivity.this, "拍摄照片出现异常", 2);
					e.printStackTrace();
					
				}finally{
					if (fos != null) {
						
						fos.close();
					}
				}
        		
			}else {
				FileOutputStream fos = null;
				try {
					File file = StorageHelper.getOutputFile(mTask, image_name);
                	if (file == null) {
        				
                		return;
        			}
					//----------直接存储照片--------------------------
					fos = new FileOutputStream(mPictureFile); 
		            fos.write(mData);
		            fos.flush();
		            fos.close();
		            
		            Message message = mHandler.obtainMessage();
		            Bundle bundle = new Bundle();
		            bundle.putByteArray("mData", mData);
		            bundle.putString("path", mPictureFile.getAbsolutePath());
		            message.setData(bundle);
		            message.what = 700000;
		            mHandler.sendMessage(message);
		            
					
				} catch (Exception e) {
					mProgressContainer.setVisibility(View.GONE);
					CustomToast.show(CustomCameraActivity.this, "拍摄照片出现异常", 2);
					e.printStackTrace();
				}finally{
					if (fos != null) {
						
						fos.close();
					}
					
				}
			}
        	
        } catch (FileNotFoundException e) { 
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    /** 官方建议的安全地访问摄像头的方法  **/
    public static Camera getCameraInstance(){ 
        Camera c = null; 
		try {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

				c = Camera.open(0);
			} else {

				c = Camera.open();
			}
		} catch (Exception e) {
			Log.d("TAG", "Error is " + e.getMessage());
		}
        return c;
    }
     
    /** 检查设备是否支持摄像头  **/
    private boolean CheckCameraHardware(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        { 
            // 摄像头存在 
            return true; 
        } else { 
            // 摄像头不存在 
            return false; 
        } 
    }
     
    @Override
	public void onPause() {
        super.onPause();
        LogPrint.Print("onPause");
        if (mCamera != null){ 
            mCamera.release();
            mCamera = null; 
        } 
		//如果调用了registerListener
		//这里我们需要unregisterListener来卸载\取消注册
    	LogPrint.Print("mSensorManager.unregisterListener");
		mSensorManager.unregisterListener(this);
		
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	LogPrint.Print("onStop");
    }
 
    @Override
	public void onResume() 
    {
        super.onResume();
        LogPrint.Print("onConfigurationChanged");
        //激活获取屏幕方向监听
    	if (myOrientationEventListener.canDetectOrientation()) {
    		
    		myOrientationEventListener.enable();
		}
        if(mCamera == null)
        {
            mCamera = getCameraInstance();
               
        }
        //这里我们指定类型为TYPE_ORIENTATION(方向感应器)
  		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) == null) {
  			
  			Toast.makeText(this, "手机不支持方向感应器", Toast.LENGTH_SHORT).show();
  			
  		}else {
  			
  			Sensor sensor_orientation=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
  			mSensorManager.registerListener(this,sensor_orientation, SensorManager.SENSOR_DELAY_UI);
  		}
  		if (findDeviceName(deviceName)) {
    		
    		if(config.orientation ==Configuration.ORIENTATION_PORTRAIT){
    			
    			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    			
    		}
			
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	myOrientationEventListener.disable();
    	if (mlocation != null) {
			
    		mlocation = null;
		}
    	/*if (locationManager != null) {
			
			locationManager.removeUpdates(CustomCameraActivity.this);
		}*/
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		/*if (!isAbout_task) {
            	OnFinish();
            	goTOCameraPage();
    		}else if (isContinueCamear) {
    			
    			OnFinish();
    			
    		}else{
    			
    			OnFinish();
    			goTOaboutPage();
    		}*/
    		OnFinish();
		}
    	
    	
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	LogPrint.Print("onConfigurationChanged");
    	
    }
	/**
	 * 抢单
	 */
	private void getOreder() {
		//mActivityInterface.showNetWaitDialog(this.getResources().getString(R.string.dialog_net));
		//判断网络是否有效
		if (Util.isNetworkAvailable(CustomCameraActivity.this)) {
		mActivityInterface.showNetWaitDialog(CustomCameraActivity.this.getResources().getString(R.string.dialog_net));
		String url = UrlConfig.orderUrl + "loginId=" + StringUtils.getLoginId(getApplicationContext()) + "&token=" + StringUtils.getLoginToken(getApplicationContext()) + "&taskId="
				+ mTask.getId();
		LogPrint.Print("orderUrl==="+url);
		httpHandler = new MHttpHandler(this);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						JSONObject jsonObject = new JSONObject(new String(data));
						
						LogPrint.Print("getOreder-----jsonObject==="+jsonObject);
						boolean b = jsonObject.getBoolean("result");
						if (b){
							mHandler.sendEmptyMessage(000000);
							}
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
					if (arg0 != 200){
						
						mHandler.sendEmptyMessage(500000);
					}else{
						mHandler.sendEmptyMessage(100000);
					}
						
				}
			}
		});
		httpHandler.execute();
		
		}else{
			
			mHandler.sendEmptyMessage(500000);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case 000000: //抢单成功
					saveTask(); //保存任务到数据库
					mActivityInterface.dismissNetDialog();
					/*if (mlocation != null) {
						
						point.setLat(mlocation.getLatitude() + "");
						point.setLon(mlocation.getLongitude() + "");
						//GPS角度
						point.setAngle(String.valueOf((int)mlocation.getBearing()));
						point.setLspeed(String.valueOf((int)mlocation.getSpeed()));
					}else {
						
						point.setLat("0");
						point.setLon("0");
						point.setAngle("0");
						point.setLspeed("0");
					}
					//手机方向
					point.setOri(String.valueOf(sensorOrientation));
					point.setCameraId("");
					point.setOrder("DZY"+System.currentTimeMillis());
					point.setIname(image_name);
					point.setTittle(mTask.getName());
					point.setTid(mTask.getId());
					point.setTaskType(Integer.valueOf(mTask.getType()));*/
					
					//mActivityInterface.setData(mTask);
					//mActivityInterface.dataBridge(point);
					
					//2014-12-20 savePoint(1); 
					
					isFirst = true;
					isGetOrderSuccess = true;
					
					if (isContinueAndSingle){
						
						 mCamera.takePicture(mShutterCallback, null, mPicureCallback);
					}	
                   
					
					//如果是单拍模式 2014-12-20
					/* 2014-12-20 if (!isContinueAndSingle) {
						
						OnFinish();
						goTODetailPage();
					}*/
					
				break;
				case 100000: //抢单失败
					//mActivityInterface.dismissNetDialog();
					isGetOrderSuccess = false;
					String str = (String) msg.obj;
					if (str == null || str.equals("")) {
						break;
					}
					int code = Integer.valueOf(str);
					String lintString = "抢单失败";
					switch (code) {
					case Configs.TASK_QZ_ERROR:
						lintString = getResources().getString(R.string.code_212);
					break;
					case Configs.TASK_QZ_OTHER_HANDLE:
						lintString = getResources().getString(R.string.code_213);
					break;
					}
					Toast.makeText(CustomCameraActivity.this, lintString, Toast.LENGTH_SHORT).show();
					//mActivityInterface.ShowCodeDialog(code);
				break;
				case 300000:
					mProgressContainer.setVisibility(View.GONE);
					break;
				case 500000:
					isGetOrderSuccess = false;
					//mActivityInterface.dismissNetDialog();
					Toast.makeText(CustomCameraActivity.this, "网络不给力，请检查网络", 0).show();
				break;
				case 600000:
					byte[] mData = (byte[])msg.obj;
					camearPhoto(mData);
					break;
				case 700000: //处理图片旋转后显在界面
					Bundle bundle = msg.getData();
					byte[] imageData = bundle.getByteArray("mData");
					String path = bundle.getString("path");
			        //往图片上加经纬度等属性信息
	            	bitmapAddExif(path);
		            
	            	/*try {
	        			ImageInfo i = new ImageInfo(path);
	        			//i.setQuality(75);
	        			MagickImage m = new MagickImage(i);
	        			//int newHeight = (int) ((640/(float)m.getWidth()) * m.getHeight());
	        			m = m.scaleImage(640, 800);
	        			m = m.cropImage(new Rectangle((640-480)/2, 0, 500, 500));
	        			m = m.charcoalImage(0.5, 0.5);
	        			
	        			try {
	        				byte blob[] = m.imageToBlob(i);
	        				FileOutputStream fos = new FileOutputStream(new File(path));
	        				fos.write(blob);
	        				fos.close();
	        			}
	        			catch (Exception e) {
	        				e.printStackTrace();
	        			}
	        		} 
	                catch (MagickException e) {
	        			e.printStackTrace();
	        		}*/
	            	
				    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				    //图片变成原来的多少倍，例如此处是5表示则1/5.
					bitmapOptions.inSampleSize = 2;
					// true 禁止为bitmap分配内存 仅仅返回宽，高
					//bitmapOptions.inJustDecodeBounds = true;
					//mBitmap = BitmapFactory.decodeFile(path,bitmapOptions);
					mBitmap =BitmapFactory.decodeByteArray(imageData, 0, imageData.length,bitmapOptions);
		            if (mBitmap != null) {
			            count ++;
			            mProgressContainer.setVisibility(View.GONE);
			            
			            if (isContinueCamear) {
			            	
			            	mCamera.setPreviewCallbackWithBuffer(null);
				            /**停止预览**/
				            mCamera.stopPreview();
			            	OnFinish();
							goTODetailPage();
							
						}else {
						
				            BitmapDrawable drawable = new BitmapDrawable(mBitmap);
							FrameThumbsView.setVisibility(View.VISIBLE);
				            //ThumbsView.setImageBitmap(testBitmap);
				            ThumbsView.setImageDrawable(drawable);
				            FramePreviewView.setVisibility(View.GONE);
				            mCamera.setPreviewCallbackWithBuffer(null);
							/**停止预览**/
				            mCamera.stopPreview();
				            
						}
			            
					}
		            bundle = null;
					break;
				case 800000: //随手拍
					if (!isFirst) {
						saveTask(); //保存任务到数据库
						if (mlocation != null) {
							
							point.setLat(mlocation.getLatitude() + "");
							point.setLon(mlocation.getLongitude() + "");
							//GPS角度
							point.setAngle(String.valueOf((int)mlocation.getBearing()));
						}else {
							
							point.setLat("0");
							point.setLon("0");
							point.setAngle("0");
						}
						//手机方向
						point.setOri(String.valueOf(sensorOrientation));
						
						point.setCameraId("");
						point.setOrder("DZY"+System.currentTimeMillis());
						point.setIname(image_name);
						//point.setTittle(title);
						point.setTittle(mTask.getName());
						point.setTid(mTask.getId());
						point.setTaskType(Integer.valueOf(mTask.getType()));
						
						//mActivityInterface.setData(mTask);
						//mActivityInterface.dataBridge(point);
						//isFirst = true;
						savePoint(1);
					}else {
						
						savePoint(1);
					}
					//如果是单拍模式
					if (!isContinueAndSingle) {
						
						OnFinish();
						goTODetailPage();
					}
					break;
				case 900000:
					Bundle bundleData = msg.getData();
					byte[] data = bundleData.getByteArray("mData");
					
					//byte[] tempImageData = BitmapUtils.compressBitmap(data,600);
					try {
						FileOutputStream fos = new FileOutputStream(mPictureFile); 
	                    fos.write(data);
	                    fos.flush();
	                    fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					bitmapAddExif(mPictureFile.getAbsolutePath());
					break;
				default:
				break;
			}

		};
	};
	/**
	 * 取消删除临时预览图片
	 */
	public void deletePhoto() {
		
		File file = new File(mPictureFile.getAbsolutePath());
		Log.e("deletePhoto", mPictureFile.getAbsolutePath());
		if (file.exists()) {
			
			file.delete();
			LogPrint.Print("取消删除临时预览图片成功----");
		}else {
			
			LogPrint.Print("取消删除临时预览图片失败----");
		}
		
	}
	
	/**
	 * 抢单成功后保存任务到数据库
	 */
	public void saveTask() {
		boolean b = DManager.getInstance(this).queryDataExists(DataBaseHelper.TABNAME_WAIT, this.mTask.getId());
		if (!b) {
			mTask.setQdTime(System.currentTimeMillis() + "");
			DManager.getInstance(this).addTaskDetail(mTask);
		}
	}
	/**
	 * 跳转到任务点详情页面
	 */
	private void goTODetailPage() {
		mActivityInterface.showPage(Configs.VIEW_POSITION_CAMERA,null, Configs.VIEW_POSITION_DETAIL,
				100, null, null);
	}
	/**
	 * 跳转到随手拍界面
	 */
	private void goTOaboutPage() {
		mActivityInterface.showPage(Configs.VIEW_POSITION_CAMERA,null, Configs.VIEW_POSITION_TASK_COL_POINT,
				100, null, null);
	}
	
	/**
	 * 跳转到抢单/拍照页面
	 */
	private void goTOCameraPage() {
		mActivityInterface.showPage(Configs.VIEW_POSITION_NONE,null,Configs.VIEW_POSITION_MY_TASK,
				100, null, null);
	}
	
	
	public static void SetActivityInterface(ActivityInterface activityInterface){
		
		mActivityInterface = activityInterface;
	}
	/**
	 * GPS是否打开
	 */
	public void gpsOpen(){
		
    	if (!myLocation.isGPSENable()){
    		
    		gpsIsOpen = false;
    	}
		else{
			myLocation.enableMyLocation();
			gpsIsOpen = true;
			cellLocationProvider = new CellLocationProvider(this);
			cellLocationProvider.addLocationListener(myLocation);
			cellLocationProvider.enableLocation();
		}
		
	}
	/**
	 * 往图片上加经纬度等信息
	 * @param path 图片路径
	 */
	public void bitmapAddExif(String path){
		//往图片上加经纬度等信息
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);
			if (mlocation != null) {
				//纬度
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, mlocation.getLatitude()+"");
				//经度
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, mlocation.getLongitude()+"");
				//角度
				exif.setAttribute("GPSImgDirection", mlocation.getBearing() + "");
				//海拔高度
				exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, mlocation.getAltitude()+"");
				//速度
				exif.setAttribute("GPSSpeed", mlocation.getSpeed() + "");
				//手机方向
				//exif.setAttribute("SensorOrientation", sensorOrientation + "");
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, sensorOrientation + "");
				//
				exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP,dateFormat.format(mlocation.getTime()));
			}else {
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, sensorOrientation + "");
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "0");
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "0");
				exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, dateFormat.format(System.currentTimeMillis()));
			}
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	//---------------------------sensor传感器使用--------------------------//
	/**
	 * 当进准度发生改变时
	 * sensor->传感器
	 * accuracy->精准度
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		
	}

	/**
	 * 当传感器在被改变时触发
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//方向传感器
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
			//x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
			float x = event.values[SensorManager.DATA_X];
			float y = event.values[SensorManager.DATA_Y];
			float z = event.values[SensorManager.DATA_Z];
			orientation = (int)x;
			//LogPrint.Print("orientation手机方向======"+orientation);
		}else {
			
			orientation = 0;
		}
	}
	
	
	public Bitmap getBitmap(String path) {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 0;

		int degree = BitmapUtils.readPictureDegree(path);

		Bitmap cameraBitmap = BitmapFactory.decodeFile(path, null);

		Bitmap bitmap = BitmapUtils.rorateBitamp(90, cameraBitmap);
		
		if (cameraBitmap != null && !cameraBitmap.isRecycled()){
			
			cameraBitmap.recycle();
		}

		return bitmap;

	}
	
	/**
	 * 保存采集点到数据库中
	 * 
	 * @param flag 用于判断信息是否添加完整 1为完整，2为不完整
	 *            信息是否完整
	 */
	private void savePoint(int flag) {

		TaskPoint points = new TaskPoint();
		
		points.setCameraId(UUID.randomUUID().toString());
		points.setContent("");
		points.setFlag(MColums.WAIT);
		points.setIname(image_name);
		if (mlocation != null) {
			
		points.setAngle(String.valueOf((int)mlocation.getBearing()));
		points.setLat(String.valueOf(mlocation.getLatitude()));
		points.setLon(String.valueOf(mlocation.getLongitude()));
		points.setLspeed(String.valueOf(mlocation.getSpeed()));
		
		}else {
			
		points.setAngle("0");
		points.setLat("0");
		points.setLon("0");
		points.setLspeed("0");
		
		}
		
		
		points.setName(!StringUtils.isNullColums(mTask.getName())?mTask.getName():"");
		points.setTid(!StringUtils.isNullColums(mTask.getId())?mTask.getId():"");
		points.setOrder("DZY"+System.currentTimeMillis());
		points.setOri(String.valueOf(sensorOrientation));
		
		String cameraTypeText = SharedPreferencesUtil.getCameraTypeText(this);
		String cameraTypeId = SharedPreferencesUtil.getCameraTypeId(this);
			
		if (!StringUtils.isNullColums(cameraTypeText) && !StringUtils.isNullColums(cameraTypeId)) {
			
			points.setCameraTypeText(cameraTypeText);
			points.setType(cameraTypeId);
			
		}else if(!StringUtils.isNullColums(point.getCameraTypeText()) && !StringUtils.isNullColums(point.getType())){
			
			points.setCameraTypeText(point.getCameraTypeText());
			points.setType(point.getType());
			
		}else {
			
			points.setCameraTypeText("电子眼类型");
			points.setType("26");
			
		}
		points.setIsFull(flag);
		points.setTaskType(point.getTaskType());
		points.setTittle(mTask.getName());
		points.setLastAlret(System.currentTimeMillis() + "");
		
		//判断表中是否已经有了这条数据
		boolean find = DManager.getInstance(this).iSAdd(points.getCameraId());
		if (!find) {
			DManager.getInstance(this).addPoint(points);
		}
		//设置为空是为下一个任务点重新命名
		image_name = "";
		if (!isContinueAndSingle) {
			
			mActivityInterface.setData(mTask);
			mActivityInterface.dataBridge(points);
			
			OnFinish();
			goTODetailPage();
		}
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		LogPrint.Print("surfaceChanged");
		if (mCamera == null) {
			
			return;
		}
		
		LogPrint.Print("deviceName==="+deviceName);
		LogPrint.Print("deviceVersion==="+deviceVersion);
		//这个函数会影响最终拍出的图片方向
		android.hardware.Camera.Parameters parameters = mCamera.getParameters();
		//针对米4  使用setPreviewSize宽高不能大于 parameters.getPreviewSize()，否者会出现setParameters failed错误
		if (deviceName.equals("MI 4W")) {
			//预览界面大小
			parameters.setPreviewSize(height,width);
			//JPEG图像设置质量
			parameters.setJpegQuality(80);
			//设置图片大小
		    parameters.setPictureSize(height, width);
			
		} // 针对酷派 8297 不能以width/1.5或height/1.5,否则会出现图片为全绿色
		else if (deviceName.equals("Coolpad 8297")) {
			
			//确定预览界面大小
			Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
			//预览界面大小
		    parameters.setPreviewSize(size.width, size.height);
		    //JPEG图像设置质量
			parameters.setJpegQuality(80);
		    //设置图片尺寸大小
			size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
			//设置图片大小
		    parameters.setPictureSize(size.width, size.height);
			
		}//针对红米note,如果不设置parameters.setPictureSize(size.width, size.height);图片像素会变小，上传图片会失败
		else if (deviceName.contains("HM NOTE")) {
			
			//确定预览界面大小
			Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
			//预览界面大小
			parameters.setPreviewSize(size.width, size.height);
		    //JPEG图像设置质量
			parameters.setJpegQuality(80);
		    //设置图片尺寸大小
			//对于其它手机机型使用些方法可能会出现问题
			//size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
			//int cWidth = Math.round((float)(size.width/1.5));
			//int cHeight = Math.round((float)(size.height/1.5));
			//parameters.setPictureSize(cWidth, cHeight);
			//设置图片大小
		    parameters.setPictureSize(size.width, size.height);
		    
		}else if (deviceName.contains("HUAWEI G750-T01") || deviceName.contains("X1 7.0")) {
			
			
			//确定预览界面大小
			Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
			//预览界面大小
			parameters.setPreviewSize(size.width, size.height);
		    //JPEG图像设置质量
			parameters.setJpegQuality(90);
		    //设置图片尺寸大小
			//对于其它手机机型使用些方法可能会出现问题
			/*size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
			int cWidth = Math.round((float)(size.width/1.5));
			int cHeight = Math.round((float)(size.height/1.5));
			parameters.setPictureSize(cWidth, cHeight);*/
			//设置图片大小
		    //parameters.setPictureSize(size.width, size.height);
			
		}else{
			
			//确定预览界面大小
			Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
			//预览界面大小
			parameters.setPreviewSize(size.width, size.height);
		    //JPEG图像设置质量
			parameters.setJpegQuality(100);
		    //设置图片尺寸大小
			//对于其它手机机型使用些方法可能会出现问题
			size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
			int cWidth = Math.round((float)(size.width/1.5));
			int cHeight = Math.round((float)(size.height/1.5));
			parameters.setPictureSize(cWidth, cHeight);
			//设置图片大小
		    //parameters.setPictureSize(size.width, size.height);
			
		}
	    //设置焦点模式为连续自动对焦
	    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	    
	    int tempRotation = onOrientationChanged(rotation);
	    //针对acerAK330和三星note1,横屏时出现预览界面图像倒转现象
	    if (findDeviceName(deviceName)) {
    		
	    	parameters.setRotation(tempRotation);
		    setCameraDisplayOrientation(mCamera);
		}else {
			//如果屏幕为竖屏
			   if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
				   
					 parameters.setRotation(tempRotation);
					 setCameraDisplayOrientation(mCamera);
				     
					//该函数会影响预览界面的显示方向
					/*mCamera.setDisplayOrientation(90);
					parameters.setRotation(90);*/
				   
				}else {
					
					parameters.setRotation(tempRotation);
					mCamera.setDisplayOrientation(tempRotation);
				}
		}
	    
	    mCamera.setParameters(parameters);
		try {
			
			mCamera.startPreview();
		} catch (Exception e) {
			mCamera.release();
			mCamera = null;
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		try {
			if (mCamera != null) {
				
				mCamera.setPreviewDisplay(holder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		if (mCamera != null) {
			
			mCamera.stopPreview();
		}
		
	}
	
	/**
	 * 找出设备支持的最佳尺寸
	 * @param sizes
	 * @param width
	 * @param height
	 * @return
	 */
	
	private Size getBestSupportedSize(List<Size> sizes,int width,int height){
		
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size size : sizes) {
			
			int area = size.width * size.height;
			if (area > largestArea) {
				
				bestSize = size;
				largestArea = area;
			}
		}
		return bestSize;
	}
	
	
	/**
	 * 保持照片与实景一致
	 * 处理照相机的旋转角度
	 * @param orientation
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public int onOrientationChanged(int orientation) {
		
	     android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			
	    	 android.hardware.Camera.getCameraInfo(1, info);
		 }else {
			 android.hardware.Camera.getCameraInfo(0, info);
		}
	     int rotation = 0;
	     orientation = (orientation + 45) / 90 * 90;
	     //如果为前置摄像头
	     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	         rotation = (info.orientation - orientation + 360) % 360;
	     } else {  // back-facing camera
	         rotation = (info.orientation + orientation) % 360;
	     }
	     
	     return rotation;
	 }
	
	/**
	 * 保持预览界面与实景一致
	 * @param camera
	 */
	public  void setCameraDisplayOrientation(android.hardware.Camera camera) {
		 
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			
	    	 android.hardware.Camera.getCameraInfo(1, info);
		 }else {
			 android.hardware.Camera.getCameraInfo(0, info);
		}
	    int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
	 
	    int degrees = 0;
	 
	    switch (rotation) {
	 
	        case Surface.ROTATION_0: degrees = 0; break;
	 
	        case Surface.ROTATION_90: degrees = 90; break;
	 
	        case Surface.ROTATION_180: degrees = 180; break;
	 
	        case Surface.ROTATION_270: degrees = 270; break;
	 
	    }
	 
	    int result;
	 
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	 
	        result = (info.orientation + degrees) % 360;
	 
	        result = (360 - result) % 360;  // compensate the mirror
	 
	    } else {  // back-facing
	 
	        result = (info.orientation - degrees + 360) % 360;
	 
	    }
			
	 	camera.setDisplayOrientation(result);
	}
	/**
	 * 屏幕方向事件监听
	 * @author miaowei
	 *
	 */
	public class MyOrientationEventListener extends OrientationEventListener{

		

		public MyOrientationEventListener(Context context, int rate) {
			super(context, rate);
		
		}

		@Override
		public void onOrientationChanged(int orientation) {
			
			rotation = orientation;
			
		}
		
	}
	
	
	/**
	 * 调用location服务并判断location是否启用(暂时未用)
	 * @return
	 */
	public synchronized void enableMyLocation() {
		//locationManager.sendExtraCommand("gps", "delete_aiding_data", null);
		//注册一个周期性的位置更新(从GPS获取位置信息，并且是每隔10ms更新一次，并且不考虑位置的变化)
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0.0F, this);
	}
	
	/**
	 * GPS是否启用
	 * @return
	 */
	public boolean isGPSENable() {
		String str = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null) {
			return str.contains(Configs.LOCATIONTYPE);
		} else {
			return false;
		}
	}
	/**
	 * 查找设备型号名称
	 * @return
	 */
	private boolean findDeviceName(String deviceName){
		//针对米4  使用setPreviewSize宽高不能大于 parameters.getPreviewSize()，否者会出现setParameters failed错误
		// 针对酷派 8297 不能以width/1.5或height/1.5,否则会出现图片为全绿色
		//针对acerAK330和三星note1,三星note3横屏时出现预览界面图像倒转现象
		String modeDeviceName = deviceName;
		modeDeviceName.contains("SM");
		if (modeDeviceName.equals("AK330") || modeDeviceName.equals("GT-N7000") || modeDeviceName.equals("SM-N9002")
				|| modeDeviceName.equals("SM-N9005")
				|| modeDeviceName.equals("SM-N9008") || modeDeviceName.equals("SM-N9009") || modeDeviceName.equals("SM-N9008S")
				|| modeDeviceName.equals("SM-N9008V") || modeDeviceName.equals("GT-I9508") || modeDeviceName.equals("GT-S5830i")
				|| modeDeviceName.equals("GT-I9200")) {
			
			return true;
			
		}
		
		return false;
		
	}
	

	/**
	 * 84经纬度位置转换02经纬度位置
	 */
	private void locationConvert() {
		if (Configs.LOCATIONTYPE.equals(mlocation.getProvider())) {
			// 根据84经纬度创建02经纬度的GeoPoint对象
			Point pt = new Point((int) (mlocation.getLongitude() * 1E5),
					(int) (mlocation.getLatitude() * 1E5));
			//pt = mNaviMapView.getController().getMapPoint(pt);
			LogPrint.Print("ptLongitude=="+pt.x / 1E5 +"//ptLatitude=="+pt.y / 1E5);
			mlocation.setLongitude(pt.x / 1E5);
			mlocation.setLatitude(pt.y / 1E5);
		}
	};
}

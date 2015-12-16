package com.mapbar.info.collection.util;


import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

/**
 * 公用类
 * @author miaowei
 *
 */
public class Util {
	
	/**
	 * 是否有效联网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 网络连接的管理
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null){
			
			return false;
		}
			
		// 网络的状态信息
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null) {
			
			return false;
		}
		// 网络是否已被打开
		if (netinfo.isConnected()) {
			
			return true;
		}
		return false;
	}

	/**
	 * 网络是否是打开的(WIFI\cmwap\cmnet)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkOpen(Context context) {
		boolean isOpen = false;
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			isOpen = cwjManager.getActiveNetworkInfo().isAvailable();
		} catch (Exception ex) {
			// 如果出异常，那么就是电信3G卡
			isOpen = false;
		}

		return isOpen;
	}

	/**
	 * 获取连接类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getApnType(Context context) {
		String result = null;
		try {
			if (isNetWorkOpen(context)) {
				ConnectivityManager mag = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				String type = mag.getActiveNetworkInfo().getTypeName();
				if (type.toLowerCase().equals("wifi")) {
					result = "wifi";
				} else {
					NetworkInfo mobInfo = mag
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					result = mobInfo.getExtraInfo();
				}
			}
		} catch (Exception e) {

			LogPrint.Print(e.getMessage().toString());
		}
		return result.toLowerCase();
	}

	/**
	 * 获得操作系统版本
	 * 
	 * @return
	 */
	public static String getOs_Version() {
		if (null != android.os.Build.VERSION.RELEASE) {
			return android.os.Build.VERSION.RELEASE;
		}
		return "";
	}

	/**
	 * 获取imei
	 * 
	 * @param activity
	 * @return
	 */
	public static String getIMEI(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(activity.TELEPHONY_SERVICE);
			String mImei = tm.getDeviceId();
			if (mImei != null) {
				mImei = mImei.trim();
				return mImei;
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "";
	}

	/**
	 * 获取imsi
	 * 
	 * @param activity
	 * @return
	 */
	public static String getIMSI(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(activity.TELEPHONY_SERVICE);
			String mImsi = tm.getSimSerialNumber();// sim卡后面的20位唯一标市
			if (mImsi != null) {
				mImsi = mImsi.trim();
				return mImsi;
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "";
	}

	/**
	 * 获取sim卡类型
	 * 
	 * @param activity
	 * @return
	 */
	public static String getSimType(Context activity) {
		try {
			String mImsi = getIMSI(activity);
			if (mImsi.length() >= 6) {
				return mImsi.substring(4, 6);
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "-1";
	}

	/**
	 * 获得设备名称
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		if (null != android.os.Build.MODEL) {
			return android.os.Build.MODEL;
		}
		return "";
	}
	
	/**
	 * 获取当前设置宽、高
	 * @param context
	 * @return
	 */
	public static String getDeviceWidthAndHeight(Context context){
		StringBuilder sbBuilder = new StringBuilder();
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		sbBuilder.append(display.getWidth()+"X"+display.getHeight());
		return sbBuilder.toString();
	}
	/**
	 * 日期对比
	 * @param time1 当前时间
	 * @param time2 广告过期时间
	 * @return
	 */
	public static boolean timeContrast(String time1,String time2){
		//时间对比
		SimpleDateFormat objFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date d1 = objFormat.parse(time1);
			Date d2 = objFormat.parse(time2);
			//LogPrint.Print("timeContrast=="+d1.getTime()+"//"+d2.getTime());
			if (d1.getTime() > d2.getTime()) {
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	/**
	 * 日期对比，获取分钟
	 * @param time1 当前时间
	 * @param time2 缓存时间
	 * @return 返回相差多少分钟 1440表示相当于24小时(1天)
	 */
	public static long getMinuteTime(String time1,String time2){
		//时间对比
		SimpleDateFormat objFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date d1 = objFormat.parse(time1);
			Date d2 = objFormat.parse(time2);
			long d3 = d1.getTime() - d2.getTime();
			long minute = d3 / (1000 * 60);
			return minute;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
		
	}
	/**
	 * 获取当前时间
	 * @param date java.util.Date
	 * @return yyyyMMddHHmmss
	 */
	public static String getCurrentDate(Date date){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		//当前时间
		String nowDateString = sdf.format(date);
		
		return nowDateString;
	}
	
	/**
	 * 获取标识方向文字值(东-西-南-北)
	 * @param sensorOrientation 方向数字值
	 */
	public static String getOrientation(String orientation){
		
		String stringOrientation = null;
		int sensorOrientation = Integer.parseInt(orientation);
		//东北
		if (sensorOrientation >=28 && sensorOrientation <= 72) {
		
			stringOrientation = "东北";
			LogPrint.Print("东北...................");
			
		}//东
		else if (sensorOrientation >=73 && sensorOrientation <= 117) {
			
			stringOrientation = "东";
			LogPrint.Print("东...................");
		}//东南
		else if (sensorOrientation >= 118 && sensorOrientation <= 162) {
			
			stringOrientation = "东南";
			LogPrint.Print("东南...................");
		}//南
		else if (sensorOrientation >= 163 && sensorOrientation <= 207) {
			stringOrientation = "南";
			LogPrint.Print("南...................");
		}//西南
		else if (sensorOrientation >= 208 && sensorOrientation <= 252) {
			
			stringOrientation = "西南";
			LogPrint.Print("西南...................");
		}//西
		else if (sensorOrientation >= 253 && sensorOrientation <= 297) {
			
			stringOrientation = "西";
			LogPrint.Print("西...................");
		}//西北
		else if (sensorOrientation >= 298 && sensorOrientation <= 342) {
			
			stringOrientation = "西北";
			LogPrint.Print("西北...................");
		}//北
		else if (sensorOrientation >=343 || sensorOrientation <= 27) {
			
			stringOrientation = "北";
			LogPrint.Print("北...................");
		}
		
		return stringOrientation;
	}
	/**
	 * 编码UTF-8
	 * @return
	 */
	public static String enDcodeUtf(String str,String charsetName){
		
		try {
			String encode = URLEncoder.encode(str, charsetName);
			return encode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
}

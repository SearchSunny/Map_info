package com.mapbar.info.collection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.mapbar.android.location.CellLocationProvider;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
/**
 * 初始化Location值
 * @author miaowei
 *
 */
public class MyLocation implements LocationListener {

	private volatile boolean mIsMyLocationEnabled = false;
	private Context mContext;
	private final ArrayList<NameAndDate> mEnabledProviders = new ArrayList<NameAndDate>(2);
	@SuppressWarnings("unused")
	private static final long FALLBACK_PROVIDER_THRESHOLD_MILLIS = 10000L;
	/**
	 * 
	 */
	private static final String[] DESIRED_PROVIDER_NAMES = { "gps" };
	private static final float LOCATTION_SPEED_FILTER = 1.0f;
	/**
	 * 第一次连接GPS
	 */
	private boolean firstGps;
	private final static String LOG_FILE_PATH = SdcardUtil.getSdcardCollInfoNO() + "/gpslog/";
	private SimpleDateFormat gpsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ActivityInterface activityInterface;

	public MyLocation(Context c, ActivityInterface interface1) {
		this.activityInterface = interface1;
		mContext = c;

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	/**
	 * 调用location服务并判断location是否启用
	 * @return
	 */
	public synchronized boolean enableMyLocation() {
		LocationManager service = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
		service.sendExtraCommand("gps", "delete_aiding_data", null);
		service.removeUpdates(this);
		if (!isGPSENable()) {
			tryOpenGPS();
		}
		this.mEnabledProviders.clear();
		this.mIsMyLocationEnabled = true;
		for (String name : DESIRED_PROVIDER_NAMES) {
			try {
				this.mEnabledProviders.add(new NameAndDate(name));
				//注册一个周期性的位置更新(从GPS获取位置信息，并且是每隔100ms更新一次，并且不考虑位置的变化)
				service.requestLocationUpdates(name, 100, 0.0F, this);
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			}
		}
		return this.mIsMyLocationEnabled;
	}

	@Override
	public void onLocationChanged(Location location) {

		LogPrint.Print("gps", "MyLocation----onLocationChange=="+location);
		/*if (!firstGps) {
			
			writeGPSdata(location);
			firstGps = true;
			
		}else {
			
			if (Configs.LOCATIONTYPE.equalsIgnoreCase(location.getProvider())){
				
				writeGPSdata(location);
			}
		}*/
		
		activityInterface.onLocationChangedFromGps(location);

	}

	public synchronized void disableMyLocation() {
		LocationManager service = (LocationManager) this.mContext.getSystemService("location");
		service.sendExtraCommand("gps", "delete_aiding_data", null);
		service.removeUpdates(this);
		
		this.mEnabledProviders.clear();
		this.mIsMyLocationEnabled = false;
	}

	private static class NameAndDate {
		public String name;
		public long date;

		public NameAndDate(String name) {
			this.name = name;
			this.date = -9223372036854775808L;
		}
	}

	/**
	 * GPS是否启用
	 * @return
	 */
	public boolean isGPSENable() {
		String str = Settings.Secure.getString(mContext.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null) {
			return str.contains("gps");
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	public void tryOpenGPS() {
		Intent intentSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mContext.startActivity(intentSettings);
	}
	
	
	/**
	 * 记录GPS日志信息
	 * @param loc
	 */
	private void writeGPSdata(Location loc) {
		StringBuffer sb = new StringBuffer();
		try {
			if (loc != null) {
				sb.append("纬度"+loc.getLatitude() + ",经度" + loc.getLongitude() + ",海拔高度"
						+ loc.getAltitude() + ",速度" + loc.getSpeed() + ",角度"
						+ loc.getBearing() + ",时间"+ gpsDateFormat.format(loc.getTime()) + ",连接方式"+loc.getProvider()
						+",当前时间"+gpsDateFormat.format(new Date(System.currentTimeMillis()))+",");

				Bundle extras = loc.getExtras();
				if (extras != null) {
					int num = extras.getInt("NumSatellite");
					sb.append(num);
					if (num != 0) {
						for (int i = 0; i < num; i++) {
							sb.append("," + extras.getInt("SatelliteID" + i));// 卫星身份证
							sb.append("," + extras.getInt("SignalStrength" + i));// 信号强度
							sb.append("," + extras.getInt("Azimuth" + i));// 方位角(地平经度)
							sb.append("," + extras.getInt("ElevationAngle" + i));// 升运角,倾斜角
						}
					}
				} else {
					sb.append(0);// 最后添加一个0
				}
			}
		} catch (Exception e) {
		}
		writeGPSdata(sb.toString());
	}

	private OutputStream osGPS;

	private void writeGPSdata(String str) {
		if (osGPS == null) {

			File fl = new File(LOG_FILE_PATH);
			if (!fl.exists() || !fl.isDirectory()) {
				fl.mkdir();
			}

			File flDebug = null;
			long tmpFile = System.currentTimeMillis();// 系统时间
			String dataFile = LOG_FILE_PATH + tmpFile + ".txt";
			flDebug = new File(dataFile);
			try {
				flDebug.createNewFile();
				osGPS = new FileOutputStream(flDebug);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (osGPS == null)
			return;
		try {
			str = str + "\n";
			byte[] buffer = str.getBytes();
			osGPS.write(buffer);
			osGPS.flush();
		} catch (Exception e) {
		}
	}

}

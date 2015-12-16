package com.mapbar.info.collection.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences存储公共类
 * @author miaowei
 *
 */
public class SharedPreferencesUtil {
	
	/**
	 * 保存电子眼类型文字
	 * @param context
	 * @param CameraTypeText   电子眼类型文字
	 */
    public static void saveCameraTypeText(Context context, String CameraTypeText){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.cameratypetext", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("CameraTypeText", CameraTypeText);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
	 * 保存电子眼类型ID
	 * @param context
	 * @param CameraTypeText 电子眼类型ID
	 * @param CameraTypeId   电子眼类型文字
	 */
    public static void saveCameraTypeId(Context context,String CameraTypeId){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.cameratypeid", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("CameraTypeId", CameraTypeId);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取电子眼类型文字
     * @param context
     * @return
     */
    public static String getCameraTypeText(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.cameratypetext", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("CameraTypeText", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    
    /**
     * 获取电子眼类型ID
     * @param context
     * @return
     */
    public static String getCameraTypeId(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.cameratypeid", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("CameraTypeId", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    
    
    /**
	 * 保存登录用户ID
	 * @param context
	 * @param userName
	 */
    public static void saveLoginID(Context context, String loginId){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.loginid", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("loginid", loginId);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取登录用户ID
     * @param context
     * @return
     */
    public static String getLoginID(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.loginid", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("loginid", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    /**
	 * 保存登录用户Token
	 * @param context
	 * @param userName
	 */
    public static void saveLoginToken(Context context, String token){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.token", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("token", token);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取登录用户Token
     * @param context
     * @return
     */
    public static String getLoginToken(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.token", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("token", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    /**
	 * 保存登录用户Session
	 * @param context
	 * @param userName
	 */
    public static void saveLoginSessionID(Context context, String sessionId){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.sessionId", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("sessionId", sessionId);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取登录用户Session
     * @param context
     * @return
     */
    public static String getLoginSessionID(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.sessionId", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("sessionId", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    /**
	 * 保存登录用户名
	 * @param context
	 * @param userName
	 */
    public static void saveUserName(Context context, String userName){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.username", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("accounts", userName);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取登录用户名
     * @param context
     * @return
     */
    public static String getUserName(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.username", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("accounts", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    
    /**
	 * 保存当前登录用户密码
	 * @param context
	 * @param userName
	 */
    public static void saveUserPassword(Context context, String userPassword){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.userpwd", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("userPassword", userPassword);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取当前登录用户密码
     * @param context
     * @return
     */
    public static String getUserPassword(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.userpwd", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("userPassword", "");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    
    /**
     * 保存当前登录用户类型
     * @param context
     * @param LoginType 0公司1代理商2采集员
     */
    public static void saveLoginType(Context context, int LoginType){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.logintype", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putInt("LoginType", LoginType);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取当前登录用户类型
     * @param context
     * @return 0公司1代理商2采集员
     */
    public static int getLoginType(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.logintype", Activity.MODE_PRIVATE);
    		return sharedPreferences.getInt("LoginType",-1);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return -1;
    }
    
    /**
     * 保存是否第一次登录
     * @param context
     * @param isFirstLogin true是，false否
     */
    public static void saveIsFirstLogin(Context context, boolean isFirstLogin){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.firstlogin", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putBoolean("isFirstLogin", isFirstLogin);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取是否第一次登录
     * @param context
     * @return true是，false否表示已经登录过
     */
    public static boolean getIsFirstLogin(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.firstlogin", Activity.MODE_PRIVATE);
    		return sharedPreferences.getBoolean("isFirstLogin",true);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * 保存第一次登录时间
     * @param context
     * @param firstDate 登录时间yyyyMMddHHmmss
     */
    public static void saveFirstLoginDate(Context context, String firstDate){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.firstdate", Activity.MODE_PRIVATE);
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putString("firstDate", firstDate);
    		editor.commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 获取第一次登录时间
     * @param context
     * @return true是，false否
     */
    public static String getFirstLoginDate(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.map.info.firstdate", Activity.MODE_PRIVATE);
    		return sharedPreferences.getString("firstDate","");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
}

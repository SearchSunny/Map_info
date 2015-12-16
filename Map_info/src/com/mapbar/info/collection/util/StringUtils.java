package com.mapbar.info.collection.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.mapbar.info.collection.Configs;

public class StringUtils {

	// 去除字符串中的空格、回车、换行符、制表符
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	/**
	 * 判断字符串是否为空或null
	 * @param str
	 * @return
	 */
	public static boolean isNullColums(String str) {
		if (str == null || str.trim().equals("") || str.trim().equals("null")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户名，只限用于数据库查询时使用
	 * @return
	 */
	public static String getUserName(Context mContext){
		
		String userName = "";
		
		if (StringUtils.isNullColums(Configs.userName)) {
			
			userName = SharedPreferencesUtil.getUserName(mContext);
			
		}else{
			
			userName = Configs.userName;
		}
		
		return userName;
	}
	/**
	 * 获取登录用户ID
	 * @param mContext
	 * @return
	 */
    public static String getLoginId(Context mContext){
		
		String loginId = "";
		
		if (StringUtils.isNullColums(Configs.LOGINID)) {
			
			loginId = SharedPreferencesUtil.getLoginID(mContext);
			
		}else{
			
			loginId = Configs.LOGINID;
		}
		
		return loginId;
	}
    
    /**
	 * 获取登录用户Token
	 * @param mContext
	 * @return
	 */
    public static String getLoginToken(Context mContext){
		
		String loginToken = "";
		
		if (StringUtils.isNullColums(Configs.TOKEN)) {
			
			loginToken = SharedPreferencesUtil.getLoginToken(mContext);
			
		}else{
			
			loginToken = Configs.TOKEN;
		}
		
		return loginToken;
	}
    
    /**
	 * 获取登录用户Session
	 * @param mContext
	 * @return
	 */
    public static String getLoginSessionId(Context mContext){
		
		String sessionId = "";
		
		if (StringUtils.isNullColums(Configs.JSESSIONID)) {
			
			sessionId = SharedPreferencesUtil.getLoginSessionID(mContext);
			
		}else{
			
			sessionId = Configs.JSESSIONID;
		}
		
		return sessionId;
	}
}

package com.mapbar.info.collection.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片广告
 * @author miaowei
 *
 */
public class ImageAD {

	/**
	 * 广告ID
	 */
	private String adId;
	/**
	 * 广告名称
	 */
	
	private String adName;
	/**
	 * 广告图片URL
	 */
	private String adUrl;
	/**
	 * 广告类型(1/任务 2/活动)
	 */
	private String adAction;
	/**
	 * 链接地址(如果是任务，就显示任务ID 如果是链接就是URL)
	 */
	private String task_or_link;
	/**
	 * 广告开始时间
	 */
	private String adStartDate;
	/**
	 * 广告过期时间
	 */
	private String adEndDate;
	/**
	 * 广告备注
	 */
	private String adRemark;
	

	public String getAdUrl() {
		return adUrl;
	}

	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}

	public String getAdAction() {
		return adAction;
	}

	public void setAdAction(String adAction) {
		this.adAction = adAction;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getTask_or_link() {
		return task_or_link;
	}

	public void setTask_or_link(String task_or_link) {
		this.task_or_link = task_or_link;
	}

	public String getAdStartDate() {
		return adStartDate;
	}

	public void setAdStartDate(String adStartDate) {
		this.adStartDate = adStartDate;
	}

	public String getAdEndDate() {
		return adEndDate;
	}

	public void setAdEndDate(String adEndDate) {
		try {
			Date date = StrToDate(adEndDate);
			String tempDate = DateToStr(date);
			this.adEndDate = tempDate;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	private  Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date = null;
	   try {
	    date = format.parse(str);
	   } catch (ParseException e) {
	    e.printStackTrace();
	   }
	   return date;
	}
	/**
	* 日期转换成字符串
	* @param date 
	* @return str
	*/
	private  String DateToStr(Date date) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	   String str = format.format(date);
	   return str;
	}

	public String getAdRemark() {
		return adRemark;
	}

	public void setAdRemark(String adRemark) {
		this.adRemark = adRemark;
	}
}

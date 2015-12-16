package com.mapbar.info.collection.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 任务点
 * @author rock
 * 
 */
public class TaskPoint  implements Serializable{

	// {
	// "angle": "",
	// "description": "",
	// "direction": "2",
	// "id": "0f448d3f-c5dd-46ac-b49e-a795231b8784",
	// "lastTime": "2014-07-24 16:23:34",
	// "latitude": 39.936486,
	// "longitude": 116.428355,
	// "name": "Q1",
	// "odd": "DZY2014072400002",
	// "photoUrl": "8a8aa19f47676e3301476777ded00001,",
	// "refectReason": "",
	// "speed": "",
	// "status": "待审核",
	// "type": "禁止掉头转弯停车"
	// },
	
	/**
	 * 电子眼ID
	 */
	private String cameraId;
	/**
	 * 经度
	 */
	private String lon;
	/**
	 * 纬度
	 */
	private String lat;
	/**
	 * 
	 */
	private String name;

	/**
	 * 电子眼类型ID
	 */
	private String type;
	
	private String lspeed;
	/**
	 * 手机方向
	 */
	private String ori;
	/**
	 * 角度
	 */
	private String angle;
	private String content;
	/**
	 * 图片名称
	 */
	private String iname;
	/**
	 * 任务id
	 */
	private String tid;
	/**
	 * 标识
	 * wait 待提交
	 * old  已提交
	 * lost 已过期
	 */
	private String flag;
	/**
	 * 单号
	 */
	private String order;

	//
	private String tittle;
	/**
	 * 任务类型一次性任务或多次任务
	 */
	private int taskType;
	
	
	private String lastAlret;
	
	private String cameraTypeText;
	
	private String lastTime;
	private String photoUrl;
	private String refectReason;
	private String status;
	

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getRefectReason() {
		return refectReason;
	}

	public void setRefectReason(String refectReason) {
		this.refectReason = refectReason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCameraTypeText() {
		return cameraTypeText;
	}

	public void setCameraTypeText(String cameraTypeText) {
		this.cameraTypeText = cameraTypeText;
	}

	public String getLastAlret() {
		return lastAlret;
	}

	public void setLastAlret(String lastAlret) {
		this.lastAlret = lastAlret;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	/**
	 * 用于判断信息是否添加完整 1为完整，2为不完整
	 */
	private int isFull;

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public int getIsFull() {
		return isFull;
	}

	public void setIsFull(int isFull) {
		this.isFull = isFull;
	}

	public String getCameraId() {
		return cameraId;
	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 电子眼类型id
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 电子眼类型id
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getLspeed() {
		return lspeed;
	}

	public void setLspeed(String lspeed) {
		this.lspeed = lspeed;
	}

	public String getOri() {
		return ori;
	}

	/**
	 * 设置手机方向
	 * @param ori
	 */
	public void setOri(String ori) {
		this.ori = ori;
	}

	public String getAngle() {
		return angle;
	}

	/**
	 * 设置GPS角度
	 * @param angle
	 */
	public void setAngle(String angle) {
		this.angle = angle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIname() {
		return iname;
	}

	public void setIname(String iname) {
		this.iname = iname;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "TaskPoint";
	}
}

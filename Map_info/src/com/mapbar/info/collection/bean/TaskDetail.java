package com.mapbar.info.collection.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 任务详情(包含多个任务点)
 * @author rock
 * 
 */
public class TaskDetail implements Serializable{

	// task. id:����id
	// task.name:�������
	// task. price:�۸�
	// task. description:��ע
	// task. taskType:��������
	// task. createTime:������ʱ��
	// task. exceedTime:�������ʱ��
	// task. qzTime:��������ʱ��
	// task. closeTime:����ر�ʱ��
	// account:���������������ר�У�
	// task. executorName:������ȡ����ƣ�������ר�У�
	// task. collectCount:�Ѳɼ���Ϣ��������������ר�У�
	// task. status:�����Ƿ�ر�,2Ϊ�ѹرգ�������ר�У�

	// "createTime": "2014-05-13 19:10:08",
	// "description": "撒发生的",
	// "exceedTime": "2014-05-13 00:00:00",
	// "id": "54ecc760-af95-46dc-a27c-4ce749cc2695",
	// "lastUpdateTime": null,
	// "name": "的萨芬",
	// "price": 0
	
	// "task": {
	// "closeTime": null,
	// "createTime": "2014-07-24 15:31:05",
	// "description": "北京市东城区东中街东直门街道清水苑社区东中街西78米",
	// "exceedTime": "2014-07-24 16:09:07",
	// "executorTime": null,
	// "id": "fe28c4f1-b073-4bf5-94c9-0f8df1ae97f0",
	// "lastUpdateTime": "2014-07-24 16:24:24",
	// "name": "已提交1",
	// "price": 1,
	// "roadcameraCount": 3,
	// "taskType": 1,
	// "verifyInfo": "待审：1条，通过：1条,驳回：1条"
	// }
	
	private List<TaskPoint> list;

	public List<TaskPoint> getList() {
		return list;
	}

	public void setList(List<TaskPoint> list) {
		this.list = list;
	}

	private String id;
	private String name;
	private double rice;
	private String description;
	/**
	 * 任务类型(1-多次 0-单次 2-随手拍)
	 */
	private String type;
	private String startTime;
	private String updateTime;
	/**
	 * 抢单时间
	 */
	private String qdTime;
	/**
	 * 标识
	 * wait 待提交
	 * old  已提交
	 * lost 已过期
	 */
	private String falg;
	private String lostTime;
	private String timeId;
	/**
	 * 关闭时间
	 */
	private String closeTime;
	private String executorTime;
	private int roadcameraCount;
	private String verifyInfo;
	private String currentTime;

	/**
	 * 任务状态,标识任务是否被抢到
	 * 1-新建任务未抢单
	 */
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getExecutorTime() {
		return executorTime;
	}

	public void setExecutorTime(String executorTime) {
		this.executorTime = executorTime;
	}

	public int getRoadcameraCount() {
		return roadcameraCount;
	}

	public void setRoadcameraCount(int roadcameraCount) {
		this.roadcameraCount = roadcameraCount;
	}

	public String getVerifyInfo() {
		return verifyInfo;
	}

	public void setVerifyInfo(String verifyInfo) {
		this.verifyInfo = verifyInfo;
	}

	public String getTimeId() {
		return timeId;
	}

	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}

	public String getLostTime() {
		return lostTime;
	}

	public void setLostTime(String lostTime) {
		this.lostTime = lostTime;
	}

	public String getFalg() {
		return falg;
	}

	public void setFalg(String falg) {
		this.falg = falg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRice() {
		return rice;
	}

	public void setRice(double rice) {
		this.rice = rice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getQdTime() {
		return qdTime;
	}

	public void setQdTime(String qdTime) {
		this.qdTime = qdTime;
	}

}

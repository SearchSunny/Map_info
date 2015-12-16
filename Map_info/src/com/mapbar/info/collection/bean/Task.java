package com.mapbar.info.collection.bean;

import java.util.List;

/**
 * 任务(未使用)
 * @author rock
 * 
 */
public class Task {

	private String address;
	private String rice;
	private String name;

	private List<TaskPoint> list;
	private String id;
	private String content;
	private String type;
	private String sTime;
	private String lTime;
	private String gTime;
	private String cTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String getlTime() {
		return lTime;
	}

	public void setlTime(String lTime) {
		this.lTime = lTime;
	}

	public String getgTime() {
		return gTime;
	}

	public void setgTime(String gTime) {
		this.gTime = gTime;
	}

	public String getcTime() {
		return cTime;
	}

	public void setcTime(String cTime) {
		this.cTime = cTime;
	}

	public List<TaskPoint> getList() {
		return list;
	}

	public void setList(List<TaskPoint> list) {
		this.list = list;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRice() {
		return rice;
	}

	public void setRice(String rice) {
		this.rice = rice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

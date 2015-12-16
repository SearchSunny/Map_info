package com.mapbar.info.collection.bean;
/**
 * 用于将图片保存数据时使用(暂未使用)
 * @author miaowei
 *
 */
public class ImageBean {

	private String cameraId;
	private String ImageName;

	public String getImageName() {
		return ImageName;
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

	public String getCameraId() {
		return cameraId;
	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}


}

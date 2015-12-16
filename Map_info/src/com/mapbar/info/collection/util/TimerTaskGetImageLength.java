package com.mapbar.info.collection.util;

import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Im;

import com.mapbar.info.collection.upload.FileUtil;

/**
 * 校验上传的采集点图片是否存在
 * @author miaowei
 *
 */
public class TimerTaskGetImageLength extends TimerTask {
	/**
	 * 图片路径
	 */
	private String imagePath;
	/**
	 * 工具类
	 */
	FileUtil fileUtil;
	/**
	 *图片大小
	 */
	private long imageLength;
	
	private Handler mHandler;
	
	public long getImageLength() {
		
		return imageLength;
	}

	public  void setImageLength(long imageLength) {
		
		this.imageLength = imageLength;
	}

	public TimerTaskGetImageLength (String imagePath,Context mContext,Handler handler) {
		
		fileUtil = new FileUtil(mContext);
		this.imagePath = imagePath;
		this.mHandler = handler;
	}
	@Override
	public void run() {
		LogPrint.Print("fileupload","imagePath=="+imagePath);
		long imageLength = fileUtil.postCheckImage(imagePath);
		if (imageLength > 0) {
			
			mHandler.sendEmptyMessage(MessageID.UPLOAD_SUCCESS_IMAGE);
		}else {
			
			mHandler.sendEmptyMessage(MessageID.UPLOAD_FAIL_IMAGE);
		}
		setImageLength(imageLength);
	}
}

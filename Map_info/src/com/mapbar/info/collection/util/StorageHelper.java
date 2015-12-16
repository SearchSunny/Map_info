package com.mapbar.info.collection.util;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.mapbar.info.collection.bean.TaskDetail;

import android.R.integer;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
/**
 * 采集与保存,这里给出一个用于保存文件的辅助类
 * @author miaowei
 *
 */
public class StorageHelper {

	public static final int MEDIA_TYPE_IMAGE=0;
	public static final int MEDIA_TYPE_VIDEO=1;
	public static  String imageName;
	   public static Uri  getOutputUri(File mFile)
	   {
	       return Uri.fromFile(mFile);
	   }
	    
	   /**
	    * 删除指定文件
	    * @param mTask
	    */
	   public static void getDeleteFile(String mTaskId)
	   {
		   File file = new File(SdcardUtil.getSdcardCollInfo() + mTaskId + "/" + imageName + "/");
		   if (file.exists()) {
			
			   if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].exists()) {
						
						files[i].delete();
					}
				}
				file.delete();
			 }
		   }
	   }
	   /**
	    * 创建图片文件路径
	    * @param mTask 需要的任务ID
	    * @param image_name 需要的图片名称
	    * @return
	    */
	   public static File getOutputFile(TaskDetail mTask,String image_name)
	   {
		   if (image_name == null || image_name.equals("")){
			   
			   //image_name = System.currentTimeMillis() + "";
			   image_name = String.valueOf(UUID.randomUUID());
			   
		   }
		   imageName = image_name;
		 //创建以 任务ID + 图片名称的文件夹
	 		File file = new File(SdcardUtil.getSdcardCollInfo() + mTask.getId() + "/" + image_name + "/");
	 		if (!file.exists())
	 			file.mkdirs();

	 		//创建以 任务ID + 图片名称的实体图片
	 		//File fils = new File(SdcardUtil.getSdcardCollInfo() + mTask.getId() + "/" + image_name + "/"+System.currentTimeMillis() + ".jpg");
	 		File fils = new File(SdcardUtil.getSdcardCollInfo() + mTask.getId() + "/" + image_name + "/"+UUID.randomUUID() + ".jpg");
	       return fils;
	   }
	   
	 
	   
	   /**
		 * 读取图片:旋转的角度
		 * 
		 * @param path
		 *            图片绝对路径
		 * @return degree旋转的角度
		 */
		public static int readPictureDegree(String path) {
			int degree = 0;
			try {
				ExifInterface exifInterface = new ExifInterface(path);
				// exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
				// exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);
				exifInterface.saveAttributes();
				int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				switch (orientation)
				{
					case ExifInterface.ORIENTATION_ROTATE_90:
						degree = 90;
					break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						degree = 180;
					break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						degree = 270;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return degree;
		}
		
		
		/**
		 * 旋转图片
		 * 
		 * @param angle
		 *            旋转的角度
		 * @param bitmap
		 *            要旋转的对象
		 * @return 旋转过后的 bitmap
		 */
		public static Bitmap rorateBitamp(int angle, Bitmap bitmap) {
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		}
		/**
		 * 旋转图片
		 * 
		 * @param angle
		 *            旋转的角度
		 * @param bitmap
		 *            要旋转的对象
		 * @return 旋转过后的 bitmap
		 */
		public static Bitmap rorateBitamp(int angle,byte[] mData) {
			Bitmap cameraBitmap=BitmapFactory.decodeByteArray(mData, 0, mData.length);
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);
			return resizedBitmap;
		}

}

package com.mapbar.info.collection.ui;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * 创建一个预览类。这里我们使用的是官方API中提供的一个基本的预览类：(暂未使用)
 * @author miaowei
 *
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	
	/** Camera **/
    private Camera mCamera;
    /** SurfaceHolder **/
    private SurfaceHolder mHolder;
     
    /** CamreaPreview构造函数   **/
    @SuppressWarnings("deprecation")
    public CameraPreview(Context mContext,Camera mCamera) 
    {
        super(mContext);
        this.mCamera=mCamera;
        // 安装一个SurfaceHolder.Callback，
        // 这样创建和销毁底层surface时能够获得通知。
        mHolder = getHolder(); 
        // 已过期的设置，但版本低于3.0的Android还需要
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        mHolder.addCallback(this); 
        
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) 
    {
         
    	if (mCamera == null) {
			
			return;
		}
		Camera.Parameters parameters = mCamera.getParameters();
		//确定预览界面大小
		Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
		parameters.setPreviewSize(size.width, size.height);
		
		//设置图片尺寸大小
		size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
	    parameters.setPictureSize(size.width, size.height);
	    
	    
	    mCamera.setParameters(parameters);
		try {
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
		} catch (Exception e) {
			mCamera.release();
			mCamera = null;
		}
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder holder) 
    {
    	
    	try {
			if (mCamera != null) {
				
				mCamera.setPreviewDisplay(holder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       /*try {
    	 //设置holder主要是用于surfaceView的图片的实时预览，以及获取图片等功能，可以理解为控制camera的操作.. 
    	   if (mCamera == null) {
			
    		   mCamera = CustomCameraActivity.getCameraInstance();
		}
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
        //设置camera预览的角度，因为默认图片是倾斜90度的
        mCamera.setDisplayOrientation(90);
       } catch (IOException e) {
        Log.d("TAG", "Error is "+e.getMessage());
       }*/    
    }
 
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) 
    {
    	
    	if (mCamera != null) {
			
    		mCamera.stopPreview();
		}
    	
    	
    	
        // 如果预览无法更改或旋转，注意此处的事件
        // 确保在缩放或重排时停止预览
        /*if (mHolder.getSurface() == null){ 
          // 预览surface不存在
          return; 
        } 
       
        // 更改时停止预览 
       try { 
            mCamera.stopPreview(); 
        } catch (Exception e){ 
          // 忽略：试图停止不存在的预览
        } 
		// 在此进行缩放、旋转和重新组织格式
         //以新的设置启动预
        try { 
            mCamera.setPreviewDisplay(mHolder); 
            mCamera.setDisplayOrientation(90); 
            mCamera.startPreview(); 
        } catch (Exception e){ 
            Log.d("TAG", "Error is " + e.getMessage()); 
        }*/ 
        
    }
    
    /**
	 * 找出设备支持的最佳尺寸
	 * @param sizes
	 * @param width
	 * @param height
	 * @return
	 */
	
	private Size getBestSupportedSize(List<Size> sizes,int width,int height){
		
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size size : sizes) {
			
			int area = size.width * size.height;
			if (area > largestArea) {
				
				bestSize = size;
				largestArea = area;
			}
		}
		return bestSize;
	}
}

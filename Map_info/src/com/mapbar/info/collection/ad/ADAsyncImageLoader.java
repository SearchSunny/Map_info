package com.mapbar.info.collection.ad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mapbar.info.collection.util.SdcardUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

/**
 * 异步加载下载图片SoftReference软引方式
 * @author miaowei
 *
 */
@SuppressLint("NewApi")
public class ADAsyncImageLoader {
	
	//SoftReference是软引用，是为了更好的为了系统回收变量
    private HashMap<String, SoftReference<Drawable>> imageCache;
    private Context ctx;
    private ExecutorService executorService = Executors.newFixedThreadPool(3);    //固定三个线程来执行任务
    public ADAsyncImageLoader(Context ctx) {
   	   imageCache = new HashMap<String, SoftReference<Drawable>>();
   	   this.ctx=ctx;
    }
    
    /**
     * 加载图片，并请求下载
     * @param imageUrl 图片广告URL
     * @param AdName广告图片名，用于生成png图片时命名
     * @param imageCallback 回调接口
     * @return
     */
    public Drawable loadDrawable(final String imageUrl,final String AdName, final ImageCallback imageCallback){
        if (imageCache.containsKey(imageUrl)) {
        	//从缓存中获取
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (drawable != null) {
            	
                return drawable;
            } 
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj,imageUrl);
            }
        };
        //建立新一个新的线程下载图片
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(ctx, imageUrl,"ad_img_"+AdName+".png");
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        });
        return null;
    }
       /**
        * 网络图片先下载到本地cache目录保存，以imagUrl的图片文件名保存。如果有同名文件在cache目录就从本地加载
        * @param context
        * @param imageUrl 广告URl
        * @param imgName 图片名称
        * @return
        */
 		public static Drawable loadImageFromUrl(Context context, String imageUrl,String imgName) {
 			
 			 if(imageUrl == null ){
  	        	
  	        	return null;  
  	        }
 			Drawable drawable = null;  
 	        String imagePath = "";  
 	        String   fileName   = "";  
 	              
 	    // 获取url中图片的文件名与后缀  
 	        if(imageUrl!=null&&imageUrl.length()!=0){   
 	            fileName  = imgName;  
 	        }  
 	          
 	        // 图片在手机本地的存放路径,注意：fileName为空的情况  
 	        //imagePath = context.getCacheDir() + "/" + fileName;  
 	        imagePath = SdcardUtil.getSdcardCollInfoNO() + "/cache"; 
 	        Log.i("ad","icon_imagePath = " + imagePath);  
 	        File fileCacheFile = new File(imagePath);
 	        if (!fileCacheFile.exists()) {
 	        	
 	        	fileCacheFile.mkdir();
			}
 	        File file = new File(SdcardUtil.getSdcardCollInfoNO() + "/cache",fileName);// 保存文件  
 	       //可以在这里通过文件名来判断，是否本地有此图片   
 	        if(!file.exists()&&!file.isDirectory()){
 	        	Log.i("ad","icon_file.toString()=" + file.toString());  
 	            try {  
 	            	file.createNewFile();
 	                FileOutputStream fos= new FileOutputStream(file);
 	                //开始下载图片
 	                InputStream is = new URL(imageUrl).openStream();  
 	                int   data = is.read();   
 	                while(data!=-1){   
 	                        fos.write(data);   
 	                        data=is.read();;   
 	                }   
 	                fos.close();  
 	                is.close();  
 	                
 	                drawable = Drawable.createFromPath(file.toString());  
// 	                Log.i(TAG, "file.exists()不文件存在，网上下载:" + drawable.toString());   
 	            } catch (IOException e) {  
 	                Log.e("ad", e.toString() + "图片下载及保存时出现异常！");  
 	            }  
 	        }else  
 	        {  
 	            drawable = Drawable.createFromPath(file.toString());  
// 	            Log.i("test", "file.exists()文件存在，本地获取");   
 	        } 
 	        return drawable ;
 		}
  
    /**
     * 回调函数，用来回调显示广告图片
     * @author miaowei
     *
     */
	  public interface ImageCallback {
	         public void imageLoaded(Drawable imageDrawable, String imageUrl);
	     }
}
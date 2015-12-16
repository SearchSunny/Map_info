package com.mapbar.info.collection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.CustomToast;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SdcardUtil;

/**
 * 软件更新
 * @author miaowei
 *
 */
public class SoftUpdateManager{
	/**  
	 * 下载中
	 */
    private static final int DOWNLOAD = 1;  
    /**
     *  下载结束  
     */
    private static final int DOWNLOAD_FINISH = 2;
    /**
     * 解析json成功
     */
    private static final int SUCCESS = 3;
    /**
     * 解析json失败
     */
    private static final int FAIL = 4;
    /**
     * 请求超时
     */
    private static final int TIMEOUT = 5;
    /**
     *  保存解析的json信息 <version,name,url>
     *
     */  
    HashMap<String, String> mHashMap;  
    /**
     *  下载保存路径
     */  
    private String mSavePath;  
    /**
     *  记录进度条数量 
     */  
    private int progress;  
    /** 
     * 是否取消更新
     */  
    private boolean cancelUpdate = false;  
  
    private Context mContext;  
    /** 
     * 更新进度条
     *  
     */  
    private ProgressBar mProgress;
    /**
     * 提示对话框
     */
    private Dialog mDownloadDialog;
    
    private MHttpHandler httpHandler;
    
    private static String TAG = "SoftUpdateManager";
    /**
     * 版本号名称
     */
    private String versionName;
    
    public SoftUpdateManager(Context context) {
		this.mContext = context;
		mHashMap = new HashMap<String, String>();
	}
    
    /**
     * 处理请求
     */
    private Handler mHandler = new Handler()  
    {  
        public void handleMessage(Message msg)  
        {  
            switch (msg.what)  
            {  
            case SUCCESS:
            	checkUpdate();
            	break;
               // 正在下载  
            case DOWNLOAD: 
            	// 设置进度条位置  
                mProgress.setProgress(progress);  
                break;  
            case DOWNLOAD_FINISH:  
                // 安装文件  
                installApk();  
                break;
            case FAIL://获取版本号等相关信息失败
            	//CustomToast.show(mContext, "获取版本号等相关信息失败");
            	break;
            case TIMEOUT://连网超时
            	//CustomToast.show(mContext, "连网超时");
            	break;
            default:  
                break;  
            }  
        };  
    };
    
    
    /** 
     * 检测软件更新 
     */  
    public void checkUpdate()  
    {  
        if (isUpdate())  
        {  
            // 显示提示对话框  
            showNoticeDialog();  
        }/* else  
        {  
            CustomToast.show(mContext,  mContext.getString(R.string.soft_update_no));
        }*/  
    } 
    /**
     * 请求服务器，获取版本号等相关信息
     * @param url
     */
    public void httpRequest(String url){
    	LogPrint.Print(TAG, "SoftUpdateManager===url==="+url);
    	httpHandler = new MHttpHandler(mContext);
		httpHandler.setCache(CacheType.NOCACHE);
		httpHandler.setRequest(url, HttpRequestType.POST);
		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

			@Override
			public void onResponse(int arg0, String arg1, byte[] data) {
				if (data != null) {
					try {
						JSONObject jsonObject = new JSONObject(new String(data));
						LogPrint.Print(TAG, "SoftUpdateManager===jsonObject==="+jsonObject);
						boolean bl = jsonObject.getBoolean("result");
						if (bl) {
							JSONObject dataJson = (JSONObject) jsonObject.get("data");
							LogPrint.Print(TAG, "SoftUpdateManager===dataJson==="+dataJson);
								mHashMap.put("version", dataJson.getString("versionCode"));
								//mHashMap.put("version","10192756");
								mHashMap.put("name", dataJson.getString("fileName"));
								mHashMap.put("versionName", dataJson.getString("versionName"));
								mHashMap.put("url", dataJson.getString("downUrl"));
								mHandler.sendEmptyMessage(SUCCESS);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(FAIL);
						
					}

				} else {
					if (arg0 != 200){
						
						mHandler.sendEmptyMessage(TIMEOUT);
					}
						
				}
			}
		});
		httpHandler.execute();
    }
    
    
    /** 
     * 检查软件是否有更新版本 
     *  
     * @return 
     */  
    private boolean isUpdate()  
    {  
        // 获取当前软件版本  
        int versionCode = getVersionCode(mContext);  
        if (null != mHashMap)  
        {  
            int serviceCode = Integer.valueOf(mHashMap.get("version"));  
            // 版本判断  
            if (serviceCode > versionCode)  
            {  
                return true;  
            }  
        }  
        return false;  
    } 
    
    /** 
     * 获取软件版本号 
     * @param context 
     * @return 
     */  
    private int getVersionCode(Context context)  
    {  
        int versionCode = 0;  
        try  
        {  
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode  
            versionCode = context.getPackageManager().getPackageInfo("com.mapbar.info.collection", 0).versionCode;
            versionName = context.getPackageManager().getPackageInfo("com.mapbar.info.collection", 0).versionName;
        } catch (NameNotFoundException e)  
        {  
            e.printStackTrace();  
        }  
        return versionCode;  
    } 
    /** 
     * 显示软件更新对话框 
     */  
    private void showNoticeDialog()  
    {  
        // 构造对话框  
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.soft_update_title);  
        builder.setMessage(mContext.getString(R.string.soft_update_info)+"\n"+"版本号："+ mHashMap.get("versionName"));
        // 更新  
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()  
        {  
            @Override  
            public void onClick(DialogInterface dialog, int which)  
            {  
                dialog.dismiss();  
                // 显示下载对话框  
                showDownloadDialog();  
            }  
        });  
        // 稍后更新  
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()  
        {  
            @Override  
            public void onClick(DialogInterface dialog, int which)  
            {  
                dialog.dismiss();  
            }  
        });  
        Dialog noticeDialog = builder.create();  
        //按对话框以外的地方不起作用
        noticeDialog.setCanceledOnTouchOutside(false);
        //按返回键不起作用(先预留)
        //noticeDialog.setCancelable(false);
        noticeDialog.show();  
    } 
    
    /** 
     * 显示软件下载对话框 
     */  
    private void showDownloadDialog()  
    {  
        // 构造软件下载对话框  
        AlertDialog.Builder builder = new Builder(mContext);  
        builder.setTitle(R.string.soft_updating);  
        // 给下载对话框增加进度条  
        final LayoutInflater inflater = LayoutInflater.from(mContext);  
        View v = inflater.inflate(R.layout.layout_softupdate, null);  
        mProgress = (ProgressBar) v.findViewById(R.id.grogress);  
        builder.setView(v);  
        // 取消更新  
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()  
        {  
            @Override  
            public void onClick(DialogInterface dialog, int which)  
            {  
                dialog.dismiss();  
                // 设置取消状态  
                cancelUpdate = true;  
            }  
        });  
        mDownloadDialog = builder.create();  
        //按对话框以外的地方不起作用
        mDownloadDialog.setCanceledOnTouchOutside(false);
        //按返回键不起作用
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();  
        //下载文件  
        downloadApk();  
    } 
    
    
    
    /** 
     * 下载apk文件 
     */  
    private void downloadApk()  
    {  
        // 启动新线程下载软件  
        new downloadApkThread().start();  
    }
    
    
    
    
    /** 
     * 下载文件线程 
     *  
     */  
    private class downloadApkThread extends Thread  
    {  
        @Override  
        public void run()  
        {  
            try  
            {  
                // 判断SD卡是否存在，并且是否具有读写权限  
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))  
                {  
                    // 获得存储卡的路径  
                    /*String sdpath = Environment.getExternalStorageDirectory().getPath() + "/"; 
                    mSavePath = sdpath + "download";  */
                	mSavePath = SdcardUtil.dir_download;
                    URL url = new URL(mHashMap.get("url"));  
                    // 创建连接  
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
                    conn.connect();  
                    // 获取文件大小  
                    int length = conn.getContentLength();  
                    // 创建输入流  
                    InputStream is = conn.getInputStream();  
  
                    File file = new File(mSavePath);  
                    // 判断文件目录是否存在  
                    if (!file.exists())  
                    {  
                        file.mkdir();  
                    }  
                    File apkFile = new File(mSavePath, mHashMap.get("name"));
                    //文件字节点
                    FileOutputStream fos = new FileOutputStream(apkFile);  
                    int count = 0;  
                    // 缓存  
                    byte buf[] = new byte[1024];  
                    // 写入到文件中  
                    do  
                    {  
                        int numread = is.read(buf);  
                        count += numread;  
                        // 计算进度条位置  
                        progress = (int) (((float) count / length) * 100);  
                        // 更新进度  
                        mHandler.sendEmptyMessage(DOWNLOAD);  
                        if (numread <= 0)  
                        {  
                            // 下载完成  
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);  
                            break;  
                        }  
                        // 写入文件  
                        fos.write(buf, 0, numread);  
                    } while (!cancelUpdate);// 点击取消就停止下载.  
                    fos.close();  
                    is.close();  
                }  
            } catch (MalformedURLException e)  
            {  
                e.printStackTrace();  
            } catch (IOException e)  
            {  
                e.printStackTrace();  
            }  
            // 取消下载对话框显示  
            mDownloadDialog.dismiss();  
        }  
    }; 
    
    
    /** 
     * 安装APK文件 
     */  
    private void installApk()  
    {  
        File apkfile = new File(mSavePath, mHashMap.get("name"));  
        if (!apkfile.exists())  
        {  
            return;  
        }  
        //创建URI  
        Uri uri=Uri.fromFile(new File(SdcardUtil.dir_download+"/"+mHashMap.get("name")));
        // 通过Intent安装APK文件  
        Intent i = new Intent(Intent.ACTION_VIEW); 
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity  
        i.setDataAndType(uri, "application/vnd.android.package-archive");  
        mContext.startActivity(i);  
    }  
}

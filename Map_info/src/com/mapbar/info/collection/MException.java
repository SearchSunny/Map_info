package com.mapbar.info.collection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
/**
 * 异常处理类
 * @author miaowei
 *
 */
public class MException implements UncaughtExceptionHandler {

	// 需求是 整个应用程序 只有一个 MyCrash-Handler
	private static MException myCrashHandler;
	private Context context;
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Thread.UncaughtExceptionHandler mDefaultHandler; 
	// 1.私有化构造方法
	private MException() {

	}

	public synchronized static MException getInstance() {
		
		if (myCrashHandler != null) {
			return myCrashHandler;
		} else {
			myCrashHandler = new MException();
			return myCrashHandler;
		}
	}

	public void init(Context context) {
		this.context = context;
		  mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
	}

	public void uncaughtException(Thread arg0, Throwable arg1) {

		String versioninfo = getVersionInfo();

		// 3.把错误的堆栈信息 获取出来
		String errorinfo = getErrorInfo(arg1);

		// 4.把所有的信息 还有信息对应的时间 提交到服务器
		try {
			DebugManager.println(versioninfo + "\n Exception:\n" + errorinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDefaultHandler.uncaughtException(arg0, arg1);
		// 干掉当前的程序
	}
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//showDialog();
		};
	};
	private void showDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		dialog.setContentView(R.layout.layout_dialog_code);
		dialog.setCancelable(false);

		TextView text = (TextView) dialog.findViewById(R.id.error_text);
		text.setText("程序异常退出");
		dialog.findViewById(R.id.error_confirm).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				android.os.Process.killProcess(android.os.Process.myPid());

			}
		});

	}

	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param arg1
	 * @return
	 */
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

}

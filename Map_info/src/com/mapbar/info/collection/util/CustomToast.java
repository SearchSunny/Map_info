package com.mapbar.info.collection.util;

import com.mapbar.info.collection.R;

import android.R.integer;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义toast
 * @author miaowei
 *
 */
public class CustomToast {
	/**
	 * 正常Toast
	 * @param ctx
	 * @param str 提示信息
	 */
	public static void show(Context ctx,String str){
		 Toast toast=new Toast(ctx);
		 LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     	 View contentView = inflater.inflate(R.layout.layout_toast, null);
     	 TextView text1=(TextView)contentView.findViewById(R.id.toast_text1);
     	 text1.setText(str);
         toast.setView(contentView); 
         toast.setDuration(Toast.LENGTH_SHORT);
		 toast.show();
	}
	
	/**
	 * 指定Toast时间
	 * @param ctx
	 * @param str
	 * @param time
	 */
	public static void show(Context ctx,String str,int time){
		Toast toast = new Toast(ctx);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.layout_toast, null);
		TextView text1 = (TextView) contentView.findViewById(R.id.toast_text1);
		text1.setText(str);
		toast.setView(contentView);
		toast.setDuration(time);
		toast.show();
	}
	
	/**
	 * 指定Toast位置 x坐标
	 * @param ctx
	 * @param str
	 * @param time
	 * @param x
	 */
	public static void show(Context ctx,String str,int time,int x){
		 Toast toast=new Toast(ctx);
		 LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	 View contentView = inflater.inflate(R.layout.layout_toast, null);
    	 TextView text1=(TextView)contentView.findViewById(R.id.toast_text1);
    	 text1.setText(str);
        toast.setView(contentView); 
        toast.setGravity(Gravity.BOTTOM,x,Gravity.TOP+100);
        toast.setDuration(time);
		 toast.show();
	}
	/**
	 * 指定Toast位置 x坐标,y坐标
	 * @param ctx
	 * @param str
	 * @param time
	 * @param x x坐标
	 * @param y y坐标
	 */
	public static void show(Context ctx,String str,int time,int x,int y){
		 Toast toast=new Toast(ctx);
		 LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View contentView = inflater.inflate(R.layout.layout_toast, null);
		 TextView text1=(TextView)contentView.findViewById(R.id.toast_text1);
		 text1.setText(str);
		 toast.setView(contentView); 
		 toast.setGravity(Gravity.BOTTOM, x ,y);
		 toast.setDuration(time);
		 toast.show();
	}

}

package com.mapbar.info.collection.ad;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.mapbar.android.net.HttpHandler.CacheType;
import com.mapbar.android.net.HttpHandler.HttpHandlerListener;
import com.mapbar.android.net.HttpHandler.HttpRequestType;
import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.UrlConfig;
import com.mapbar.info.collection.ad.ADAsyncImageLoader.ImageCallback;
import com.mapbar.info.collection.bean.ImageAD;
import com.mapbar.info.collection.bean.Task;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.db.MColums;
import com.mapbar.info.collection.net.MHttpHandler;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.MessageID;
import com.mapbar.info.collection.util.SdcardUtil;
import com.mapbar.info.collection.util.StringUtils;
import com.mapbar.info.collection.util.Util;

/**
 * 广告适配
 * @author miaowei
 *
 */
public class AdNoticeAdapter extends BaseAdapter {

	private ADAsyncImageLoader adasyncImageLoader;
	private LayoutInflater mInflater;
	private Context ctx;
	private ArrayList<ImageAD> adLists;
	private AdNoticeView adView;
	private ActivityInterface mActivityInterface;
	private String [] urls = new String [2];
	private MHttpHandler httpHandler;
	/*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	//当前时间
	String nowDateString = sdf.format(new Date());*/
	/**
	 * 当前经度
	 */
	private String mLongitude;
	/**
	 * 当前纬度
	 */
	private String mLatitude;
	
	public static Location mLocation;
	
	private static final String TAG = "AD";
	/**
	 * 
	 * @param mContext
	 * @param adView
	 * @param Longitude 经度
	 * @param Latitude 纬度
	 */
	public AdNoticeAdapter(Context mContext, AdNoticeView adView,ActivityInterface aif) {
		this.ctx = mContext;
		this.mActivityInterface = aif;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		urls[0] = UrlConfig.ADIMAGE_URl+"9d3bbb00-2f63-4b3a-a22d-2df8d11796a7.jpg";
		urls[1] = UrlConfig.ADIMAGE_URl+"6f6915ae-34f1-488a-b417-c183236d4dc7.jpg";
		adLists = new ArrayList<ImageAD>();
		adasyncImageLoader = new ADAsyncImageLoader(mContext);
		this.adView = adView;
		//模拟数据
		/*for (int i = 0; i < urls.length; i++) {
			ImageAD ad = new ImageAD();
			ad.setAdUrl(urls[i]);
			ad.setAdName("adName"+i);
			adLists.add(ad);
		}*/
		
		if (mLocation != null) {
			
			this.mLongitude = String.valueOf(mLocation.getLongitude());
			this.mLatitude = String.valueOf(mLocation.getLatitude());
			
		}else {
			
			this.mLongitude = String.valueOf(0);
			this.mLatitude = String.valueOf(0);
		}
		
		//请求后台广告图片数据
		getAds(mLongitude,mLatitude);
	}

	@Override
	public int getCount() {
		return adLists.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AdViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new AdViewHolder();
			convertView = mInflater.inflate(R.layout.index_ad_item, null);
			viewHolder.adimg = (ImageView) convertView
					.findViewById(R.id.index_ad_item_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (AdViewHolder) convertView.getTag();
		}

		if (adLists.size() == 0) {
			return null;
		}
		final ImageAD ad = adLists.get(position);
		LogPrint.Print(TAG,"getAdUrl==="+ad.getAdUrl());
		//如果当前时间大于广告过期时间，不显图片。同时拿到广告图片名称到文件中查找是否有些图片，如果有进行删除
		if (!Util.timeContrast(Util.getCurrentDate(new Date()), ad.getAdEndDate())) {
			viewHolder.adimg.setVisibility(View.VISIBLE);
			viewHolder.adimg.setTag(ad.getAdUrl());
			Drawable cachedImage = adasyncImageLoader.loadDrawable(ad.getAdUrl(),ad.getAdName(), new ImageCallback() {

						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							
						   ImageView imageViewByTag = (ImageView) adView
									.findViewWithTag(imageUrl);
							// 防止图片url获取不到图片是，占位图片不见了的情况
							if (imageViewByTag != null && imageDrawable != null) { 
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});
			if (cachedImage == null) {
				viewHolder.adimg.setImageResource(R.drawable.user_top_bg);
			} else {
				viewHolder.adimg.setImageDrawable(cachedImage);
			}
			viewHolder.adimg.setScaleType(ScaleType.FIT_XY);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//如果类型为任务-进入抢单页面
					if (ad.getAdAction().equals("0")) {
						
						goToCameraPage(ad.getTask_or_link());
					} 
					//如果类型为活动-进入浏览器
					else if (ad.getAdAction().equals("1")) {
						
						goToBrowse(ad.getTask_or_link());
					} 
				}
			});
		}else {
			
			viewHolder.adimg.setVisibility(View.GONE);
			File file = new File(SdcardUtil.getSdcardCollInfoNO() + "/cache","ad_img_"+ad.getAdName()+".png");
			if (file.exists()) {
				file.delete();
				LogPrint.Print(TAG,"删除广告图片成功");
			}
		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	class AdViewHolder {
		/**
		 * 广告图片
		 */
		ImageView adimg;
	}

	/**
     * 请求服务器，获取图片广告路径
     * @param 请求url
     */
    public void getAds(String longitude,String latitude){
    	final Message msg = handler.obtainMessage();
    	if (Util.isNetworkAvailable(ctx)) {
			
    		StringBuilder builder = new StringBuilder();
        	builder.append(UrlConfig.URL_GET_IMAGEAD_URL);
        	builder.append("&token=" + StringUtils.getLoginToken(ctx));
        	builder.append("&dlon=" + longitude);
        	builder.append("&dlat=" + latitude);
        	httpHandler = new MHttpHandler(ctx);
    		httpHandler.setCache(CacheType.NOCACHE);
    		httpHandler.setRequest(builder.toString(), HttpRequestType.POST);
    		LogPrint.Print(TAG,"getAds===="+builder.toString());
    		httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

    			@Override
    			public void onResponse(int arg0, String arg1, byte[] data) {
    				if (data != null) {
    					try {
    						JSONObject jsonObject = new JSONObject(new String(data));
    						LogPrint.Print(TAG,"ad===jsonObject==="+jsonObject);
    						boolean bl = jsonObject.getBoolean("result");
    						if (bl) {
    							JSONObject dataJson = (JSONObject) jsonObject.get("data");
    							LogPrint.Print(TAG,"ad===dataJson==="+dataJson);
    							int total = dataJson.getInt("total");
    							if (total > 0) {
    								//获取广告
    								msg.what = MessageID.AD_GET_AD_SUCCESS;
    								msg.obj = dataJson;
    								handler.sendMessage(msg);
    								//setListItems(dataJson);
								}
    							
    						}else {
    							
    							LogPrint.Print(TAG,"ad===jsonObject===result==="+bl);	
    						}
    						
    					} catch (JSONException e) {
    						e.printStackTrace();
    					}

    				} else {
    					
    					LogPrint.Print(TAG,"ad===data==="+data);	
    				}
    			}
    		});
    		httpHandler.execute();
    		
		}else {
			msg.what = MessageID.NETWORK_FAILD;
			handler.sendMessage(msg);
		}
    	
    }

	/**
	 * 添加广告列表
	 * @param jsonObject
	 * @throws JSONException
	 */
	public void setListItems(JSONObject jsonObject){
		
		try {
			JSONArray app_array = jsonObject.getJSONArray("rows");
			for (int i = 0; i < app_array.length(); i++) {
				ImageAD imageAd = new ImageAD();
				imageAd.setAdId(app_array.getJSONObject(i).getString("id"));
				imageAd.setAdName(app_array.getJSONObject(i).getString("advert_name"));
				imageAd.setAdUrl(UrlConfig.ADIMAGE_URl+app_array.getJSONObject(i).getString("advert_photo"));
				imageAd.setAdAction(app_array.getJSONObject(i).getString("advert_type"));
				imageAd.setTask_or_link(app_array.getJSONObject(i).getString("task_or_link"));
				imageAd.setAdStartDate(app_array.getJSONObject(i).getString("state_time"));
				imageAd.setAdEndDate(app_array.getJSONObject(i).getString("end_time"));
				imageAd.setAdRemark(app_array.getJSONObject(i).getString("advert_desc"));
				//如果没有当前时间大于广告过期时间
				if (!Util.timeContrast(Util.getCurrentDate(new Date()), imageAd.getAdEndDate())){
					
					adLists.add(imageAd);
				}
				
			}
			notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 进入抢单页面
	 * @param taskId 任务ID
	 */
	void goToCameraPage(String taskId) {
		if (StringUtils.isNullColums(taskId)) {
			Toast.makeText(ctx, "非法活动", Toast.LENGTH_SHORT).show();
		}else{
			try {
				//任务ID获取任务
				getTaskById(taskId);
			} catch (Exception e) {
				Toast.makeText(ctx, "非法活动", Toast.LENGTH_SHORT).show();
			}
			
		}
		
		
	}

	/**
	 * 进入浏览器 
	 */
	void goToBrowse(String Url) {
		if (StringUtils.isNullColums(Url)) {
			Toast.makeText(ctx, "非法活动", Toast.LENGTH_SHORT).show();
		}else {
			try {
				Intent viewIntent = new Intent("android.intent.action.VIEW",Uri.parse(Url));
				ctx.startActivity(viewIntent);
			} catch (Exception e) {
				Toast.makeText(ctx, "非法活动", Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
	
	/**
	 * 根据任务id， 获取任务
	 * @param taskId
	 */
	private void getTaskById(String taskId) {
		if (Util.isNetworkAvailable(ctx)) {

			StringBuilder builder = new StringBuilder();

			builder.append(UrlConfig.newTaskUrl);
			builder.append("loginId=" + StringUtils.getLoginId(ctx));
			builder.append("&token=" + StringUtils.getLoginToken(ctx));
			builder.append("&taskId=" + taskId);
			LogPrint.Print(TAG,"getTaskById==="+builder.toString());
			httpHandler = new MHttpHandler(ctx);
			httpHandler.setCache(CacheType.NOCACHE);
			httpHandler.setRequest(builder.toString(), HttpRequestType.GET);
			httpHandler.setHttpHandlerListener(new HttpHandlerListener() {

				@Override
				public void onResponse(int arg0, String arg1, byte[] data) {
					if (data != null) {
						try {
							String jsonStr = new String(data, "UTF-8");
							JSONObject jsonObject = new JSONObject(jsonStr);
							boolean b = jsonObject.getBoolean("result");
							if (b) {
								JSONObject dataJson = (JSONObject) jsonObject.get("data");
								String timeId = "0";
								if (jsonObject.getString("id") != null && jsonObject.getString("id").equals("")) {
									
									timeId = (String) jsonObject.getString("id");
								}
								JSONArray jsonArray = (JSONArray) dataJson.get("rows");
								ArrayList<TaskDetail> adrList = new ArrayList<TaskDetail>();
								TaskDetail taskDetail;
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject jsonObjec = (JSONObject) jsonArray.get(i);
									taskDetail = new TaskDetail();
									taskDetail.setDescription(jsonObjec.getString("description"));
									taskDetail.setFalg(MColums.WAIT);
									taskDetail.setId(jsonObjec.getString("id"));
									taskDetail.setUpdateTime(jsonObjec.getString("lastUpdateTime"));
									taskDetail.setName(jsonObjec.getString("name"));
									taskDetail.setRice(jsonObjec.getDouble("price"));
									taskDetail.setType(jsonObjec.getInt("taskType") + "");
									taskDetail.setStartTime(jsonObjec.getString("createTime"));
									taskDetail.setLostTime(jsonObjec.getString("exceedTime"));
									//2014-11-7添加任务状态
									//taskDetail.setStatus(jsonObjec.getString("status"));
									taskDetail.setTimeId(timeId);
									adrList.add(taskDetail);
								}
								Message msg = handler.obtainMessage();
								msg.what = MessageID.AD_GET_TASK_SUCCESS;
								msg.obj = adrList;
								handler.sendMessage(msg);

							} else {
								String msg = jsonObject.getString("message");
								mActivityInterface.ShowCodeDialog(Integer.valueOf(msg),null);
							}

						} catch (Exception e) {
							handler.sendEmptyMessage(MessageID.GET_DATA_FAILD);
							e.printStackTrace();
						}

					} else {
						if (arg0 != 200)
							handler.sendEmptyMessage(MessageID.NETWORK_FAILD);
						else
							handler.sendEmptyMessage(MessageID.GET_DATA_FAILD);
					}
				}
			});
			httpHandler.execute();

		}else {
			
			handler.sendEmptyMessage(MessageID.NETWORK_FAILD);
		}
	  
	}
	/**
	 * 
	 */
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MessageID.AD_GET_TASK_SUCCESS://成功获取任务
				if (msg.obj != null) {
					
					ArrayList<TaskDetail> list = (ArrayList<TaskDetail>) msg.obj;
					if (list != null && list.size() > 0) {
						
						for (int i = 0; i < list.size(); i++) {
							
							mActivityInterface.setData(list.get(i));
						}
						mActivityInterface.showPage(Configs.VIEW_POSITION_NONE,null,Configs.VIEW_POSITION_CAMERA,
								100, null, null);
					}else {
						
						Toast.makeText(ctx, "未查询到广告任务", Toast.LENGTH_SHORT).show();
					}
					
				}
				break;
			case MessageID.GET_DATA_FAILD:
				Toast.makeText(ctx, "读取数据失败", Toast.LENGTH_SHORT).show();
				break;
			case MessageID.NETWORK_FAILD:
				Toast.makeText(ctx, "网络不给力，请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case MessageID.AD_GET_AD_SUCCESS://成功获取广告
				if (msg.obj != null){
					
					JSONObject jsonObject = (JSONObject)msg.obj;
					setListItems(jsonObject);
				}
				break;
			default:
				break;
			}
			
		};
	};
}

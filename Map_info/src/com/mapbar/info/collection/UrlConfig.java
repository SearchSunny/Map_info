package com.mapbar.info.collection;

public class UrlConfig {
	/**
	 * 内网映射地址
	 */
	//private final static String BASE_URl = "http://119.255.37.167:8529";
	/**
	 * 广告图片内网地址
	 */
	//public final static String ADIMAGE_URl = "http://119.255.37.167:8033/roadcamera/";
	/**
	 * 服务地址外网
	 */
	private final static String BASE_URl = "http://poi.gpsedog.com/";
	
	/**
	 * 广告图片外网地址
	 */
	public final static String ADIMAGE_URl = BASE_URl+"roadcamera_ad/";
	/**
	 * 未使用
	 */
	public static final String existsUrl = BASE_URl + "roadcamera-mobile/clear?";
	
	/**
	 * 获取单号URL(二期暂停使用)
	 */
	public static final String getOrderNOUrl = BASE_URl + "roadcamera-mobile/task/task!getRoadCameraByTask.action?";
	/**
	 * 获取电子眼类型URL(由于每次获取电子类型都需要请求服务器导致请求过慢，此接口放弃使用 2014-9-1)
	 */
	public static final String cameraTypeUrl = BASE_URl + "roadcamera-mobile/lbs/lbs!getTypes.action?";
	//--------------------------------------使用以下接口名
	/**
	 * 登录URL
	 */
	public static final String loginUrl = BASE_URl + "/roadcamera-mobile/user/login?projectId=444&";
	/**
	 * 获取用户信息URL
	 */
	public static final String userUrl = BASE_URl + "/roadcamera-mobile/user/getUserInfo?projectId=444&";
	/**
	 * 修改密码URL
	 */
	public static final String alterPwdUrl = BASE_URl + "/roadcamera-mobile/user/changePwd?projectId=444&";
	/**
	 * 获取搜索的任务
	 */
	public static final String newTaskUrl = BASE_URl + "/roadcamera-mobile/task/getCurrentTask?projectId=444&";
	/**
	 * 抢单URL
	 */
	public static final String orderUrl = BASE_URl + "/roadcamera-mobile/task/taskClaim?projectId=444&";
	/**
	 * 获取已提交的任务
	 */
	public static final String getMyTaskCimmit = BASE_URl + "/roadcamera-mobile/task/getCommitTask?projectId=444&";
	
	/**
	 * 关闭任务URL
	 */
	public static final String closeTaskUrl = BASE_URl + "/roadcamera-mobile/task/setTaskClose?projectId=444&";
	/**
	 * 上传GPS相关信息
	 */
	public static final String uplodeGpsUr = BASE_URl + "/roadcamera-mobile/user/lbsUpdate?projectId=444&";
	/**
	 * 获取区域城市URL
	 */
	public static final String regUrl = BASE_URl + "/roadcamera-mobile/api/GetInverseCity?projectId=444&";
	
	/**
	 * 客户端软件更新URL
	 */
	public static final  String URL_SOFT_UPDATA_CMMOBI = BASE_URl + "/roadcamera-mobile/version/getVersion?projectId=444";
	/**
	 * 上传提交任务URL(保存电子眼信息)
	 */
	public static final String uploadUrl = BASE_URl +"/roadcamera-mobile/roadcamera/saveInfo?projectId=444&";
	/**
	 * 保存电子眼信息--图片（图片处理、存放缩略图、加水印）
	 */
	public static final String SAVEIMAGE_URL = BASE_URl +"/roadcamera-mobile/roadcamera/saveImage?projectId=444&";
	/**
	 * 代理商创建子账号URL
	 */
	public static final String URL_GET_CREATEUSER_URL = BASE_URl + "/roadcamera-mobile/user/createSubAccount?projectId=444&";
	/**
	 * 获取图片广告URL
	 */
	public static final String URL_GET_IMAGEAD_URL = BASE_URl + "/roadcamera-mobile/advert/getAdvert?projectId=444";
	
	
}

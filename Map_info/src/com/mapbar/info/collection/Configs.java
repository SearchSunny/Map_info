package com.mapbar.info.collection;

import android.location.Location;

/**
 * 
 * @author rock
 * 
 */

public class Configs {
	/**
	 * 未使用
	 */
	public final static int VIEW_POSITION_NONE = -1;
	/**
	 * 登录界面
	 */
	public static final int VIEW_POSITION_LOGIN = 0;
	/**
	 * 登录之后首界面
	 */
	public static final int VIEW_POSITION_INDEX = 1;
	/**
	 * 我的信息采集点(新任务\我的任务)界面
	 */
	public static final int VIEW_POSITION_TASK_COL_POINT = 2;
	/**
	 * 未使用
	 */
	public static final int VIEW_POSITION_LIST = 3;
	/**
	 * 抢单界面
	 */
	public static final int VIEW_POSITION_CAMERA = 4;
	/**
	 * 未使用
	 */
	public static final int VIEW_POSITION_COMMIT_INFO = 5;
	/**
	 * 我的任务界面
	 */
	public static final int VIEW_POSITION_MY_TASK = 6;
	public static final int VIEW_POSITION_TASK_ROAD = 7;
	public static final int VIEW_POSITION_TASK_POINT = 8;
	/**
	 * 我的信息界面
	 */
	public static final int VIEW_POSITION_USER_INFO = 9;
	/**
	 * 点击新任务后的查找\搜索界面
	 */
	public static final int VIEW_POSITION_NEW_TASK = 10;
	/**
	 * 更多界面
	 */
	public static final int VIEW_POSITION_MORE = 11;
	/**
	 * 
	 */
	public static final int VIEW_POSITION_REQUEST = 12;
	/**
	 * 修改密码界面
	 */
	public static final int VIEW_POSITION_ALTER_PASSWORD = 13;
	/**
	 * 关于界面
	 */
	public static final int VIEW_POSITION_ABOUT = 14;
	public final static int FILTER_BY_DISTAMNCE = 15;
	public final static int FILTER_BY_OTHER = 16;

	/**
	 * 附近
	 */
	public final static int FILTER_BY_NEARBY = 17;
	/**
	 * 全部
	 */
	public final static int FILTER_BY_ALL = 18;
	public final static int VIEW_POSITION_SEARCH = 19;
	/**
	 * 抢单之后要提交的任务详情页面
	 */
	public static final int VIEW_POSITION_DETAIL = 20;
	/**
	 * 已提交任务---任务详情
	 */
	public final static int VIEW_POSITION_OLD_DETAIL = 21;
	/**
	 * 已提交任务---任务点详情
	 */
	public final static int VIEW_POSITION_OLD_ITEM_DETAIL = 22;
	/**
	 * 
	 */
	public static final int VIEW_POSITION_CAMERA_SHOW = 23;
	/**
	 * 打开相机标识
	 */
	public static final int VIEW_POSITION_DETAIL_OPEN_CAMREA = 24;
	public static final int VIEW_POSITION_DETAIL_ADD_POINT = 25;
	public static final int VIEW_POSITION_MY_OLD = 26;
	/**
	 * 对应我的任务--子任务
	 */
	public static final int VIEW_POSITION_MY_TASK_CHILD = 27;
	/**
	 * 标识是从我的任务右边按钮点击过去的
	 */
	public static final int VIEW_POSITION_MY_TASK_P = 28;
	public static final int VIEW_POSITION_DETAIL_TASK = 29;
	public static final int VIEW_POSITION_DETAIL_CAMERA = 30;
	/**
	 * 热点答疑
	 */
	public static final int VIEW_POSITION_NUMEN = 31;
	/**
	 * 拍照教程
	 */
	public static final int VIEW_POSITION_INTRODUCE_IMG = 32;
	public static final int VIEW_POSITION_INTRODUCE_HELP = 33;
	public static final int VIEW_POSITION_PHOTO = 34;
	/**
	 * 自定义相机Camera界面(暂未使用)
	 */
	public static final int VIEW_CAMERA_CUSTOM = 35;
	/**
	 * 创建子账号页面
	 */
	public static final int VIEW_CREATE_USER = 36;
	/**
	 * 广告引导页面
	 */
	public static final int VIEW_AD_HELP = 37;
	
	public final static int VIEW_FLAG_NONE = -1;
	public final static int VALUE_POSITION_NONE = -1;
	public final static int USER_PERSION = 0;
	/**
	 * 屏幕高度
	 */
	public static int HEIGHT;
	/**
	 * 屏幕宽度
	 */
	public static int WIDTH;

	public static final int LOGINFIAD = -1;
	public static final int LOGINFIAD_F = -2;
	public static final int SUCCESS = 1;
	public static final int NO_LOGIN = 2;
	public static final int PARAMRS_ERROR = 3;
	public static final int NO_PERMISSION = 4;
	public static final int IDORPWD_NULL = 5;
	public static final int IDORPWD_ERROR = 6;
	public static final int ACCOUNT_LOST = 7;
	public static final int ACCOUNT_NO = 8;
	/**
	 * 重复创建子账号
	 */
	public static final int CREATE_USER_REPEAT = 9;
	public static final int ACCOUNT_LOSTED = 1011;
	public static final int TASK_NO_EXISTS = 201;
	public static final int TASK_STATE_ERROR = 204;
	public static final int TASK_NO_ID = 207;
	public static final int DETAIL_BACK_NULL = 208;
	/**
	 * 任务结果为空
	 */
	public static final int TASK_RESULT_NULL = 209;
	public static final int SEARCH_TASK_START = 2011;
	public static final int SEARCH_TASK_TOTAL = 2012;
	public static final int UDATATE_NULL = 4020;
	public static final int UPDATE_PWD_NULL = 4021;
	public static final int NOW_PWD = 4023;
	public static final int NOW_PWD_ERROR = 4024;
	public static final int U_E = 4025;
	public static final int U_C_E = 403;
	public static final int EX_ID_NULL = 701;
	public static final int N_F_X = 501;
	public static final int CAMERA_ID_NULL = 301;
	public static final int CAMERA_NULL = 302;
	public static final int ERROR = 801;
	public static final int TASK_IS_EXCEED = 210;
	public static final int TASK_IS_ONE = 211;
	public static final int TASK_QZ_ERROR = 212;
	public static final int TASK_QZ_OTHER_HANDLE = 213;
	public static final int LOGIN_ERROR = 106;

	public static String TOKEN = null;
	public static String JSESSIONID = null;
	public static String LOGINID = null;

	public static enum FTYPE
	{
		NAME, DISTANCE, PRICE, NOMRAL
	}

	public static int Dis = 1000;
	public static FTYPE CDI = FTYPE.NOMRAL;
	public static String city;
	public static int START_ALL = 0;
	public static int END_ALL = 19;

	public static final int RANGE = 0;
	public static final int CONDITION = 1;
	public static final int BYPRICE = 3;
	public static final int BYDISTANCE = 4;
	public static final double LON = 116.43486;
	public static final double LAT = 39.93786;
	/**
	 * 
	 */
	public static final int UPBETEEWN = 30000;
	public static String curCity = null;
	public static String  userName;
	public static String  currentTime;

	/**
	 * 定位类型
	 * cell测试使用
	 * gps正式使用
	 */
	public static final String LOCATIONTYPE = "cell";
	// FEEDBACK_DETAIL_NOT_NULL 208 回执单内容不能为空
	// TASK_RESULT_IS_NULL 209 任务结果为空
	// SEARCH_TASK_START 2011 记录起始不能小于0
	// SEARCH_TASK_MAX 2012 记录总数必须大于0
	// LBS_UPDATE_ID_NOT_NULL 4020 用户id不能为空
	// LBS_UPDATE_NEWPWD_NOT_NULL 4021 新密码不可为空
	// LBS_UPDATE_OLDPWD_NOT_NULL 4023 当前密码不可为空
	// LBS_UPDATE_OLDPWD_ERROR 4024 当前密码输入不正确
	// LBS_UPDATE_EXCEPTION 4025 更新异常
	// LBS_CONTACT_ERROR 403 客户端获取联系人失败
	// EXEUTOR_ID_NOT_NULL 701 执行人id不能为空
	// CANNOT_FIND_VERSION_FILE 501 无法找到配置文件
	// ROADCAMERA_ID_NOT_NULL 301 电子眼id不能为空
	// ROADCAMERA_IS_NULL 302 电子眼结果为空

	// B_SUCCESS 1 成功
	// B_NO_LOGIN 2 尚未登录
	// B_ILLEGAL_ARG 3 参数非法：经纬度不能为空
	// B_NO_PERMISSION 4 无权限访问指定记录
	// LOGIN_IDORPWD_NOTNULL 5 账户ID、密码不能为空
	// LOGIN_IDORPWD_ERROR 6 账户ID或密码错误 请重新输入
	// LOGIN_EXPIRES 107 指定账户已经过期
	// LOGIN_EXPIRES 108 不是外勤人员
	// TOKEN_NOT_EXIST 1010 客户端cookie不存在token
	// ACCOUNT_LOGOUT_OR_EXPIRES 1011 账户已经注销或过期
	// TASK_NOT_EXIST 201 指定任务不存在
	// TASK_FEEDBACK_NOT_EXIST 204 任务状态非法
	// TASK_ID_NOT_NULL 207 任务id不能为空

}

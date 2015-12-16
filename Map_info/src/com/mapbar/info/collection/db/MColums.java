package com.mapbar.info.collection.db;

public final class MColums {
	// task. id:任务id
	// task.name:任务名称
	// task. price:价格
	// task. description:备注
	// task. taskType:任务类型
	// task. createTime:任务发起时间
	// task. exceedTime:任务过期时间
	// task. qzTime:任务抢单时间
	// task. closeTime:任务关闭时间
	// flag
	// account:任务分配名单（代理商专有）
	// task. executorName:任务领取人名称（代理商专有）
	// task. collectCount:已采集信息点数量（代理商专有）
	// task. status:任务是否关闭,2为已关闭（代理商专有）

	/**
	 * 待提交
	 */
	public static final String WAIT = "wait";// 待提交
	/**
	 * 已提交
	 */
	public static final String OLD = "old";// 已提交
	/**
	 * 已过期
	 */
	public static final String LOST = "lost";// 已过期

	public static final String TASK_ID = "id";
	public static final String TASK_NAME = "name";
	public static final String TASK_PRICE = "rice";
	public static final String TASK_DESCRIPTION = "description";
	public static final String TASK_TYPE = "type";
	public static final String TASK_STIME = "stime";
	public static final String TASK_UTIME = "utime";
	public static final String TASK_QTIME = "qtime";
	public static final String TASK_FLAG = "flag";// 过期 否？
	public static final String TASK_LTIME = "losttime";// 过期 否？
	public static final String TASK_TIMEID = "timeid";
	public static final String TASK_USERNAME = "username";

	public static final int T_ID = 1;
	public static final int T_NAME = 2;
	public static final int T_PRICE = 3;
	public static final int T_DESCRI = 4;
	public static final int T_TYPE = 5;
	public static final int T_STIME = 6;
	public static final int T_UTIME = 7;
	public static final int T_QTIME = 8;
	public static final int T_FLAG = 9;
	public static final int T_LTIME = 10;
	public static final int T_TIMEID = 11;
	public static final int T_USERNAME = 12;

	public static final String OLD_ID = "id";
	public static final String OLD_NAME = "name";
	public static final String OLD_PRICE = "rice";
	public static final String OLD_DESCRIPTION = "description";
	public static final String OLD_TYPE = "type";
	public static final String OLD_STIME = "stime";
	public static final String OLD_UTIME = "utime";
	public static final String OLD_QTIME = "qtime";
	public static final String OLD_FLAG = "flag";// // 过期 否？
	public static final String OLD_LTIME = "losttime";// 过期 否？
	public static final String OLD_TIMEID = "timeid";
	public static final String OLD_USERNAME = "username";

	public static final int O_ID = 0;
	public static final int O_NAME = 1;
	public static final int O_PRICE = 2;
	public static final int O_DESCRI = 3;
	public static final int O_TYPE = 4;
	public static final int O_STIME = 5;
	public static final int O_UTIME = 6;
	public static final int O_QTIME = 7;
	public static final int O_FLAG = 8;
	public static final int O_LTIME = 9;
	public static final int O_TIMEID = 10;
	public static final int O_USERNAME = 11;

	public static final String POINT_ID = "id";// cameraID
	public static final String POINT_LAT = "lat";
	public static final String POINT_LON = "lon";
	public static final String POINT_NAME = "name";
	public static final String POINT_TYPE = "type";// 电子眼类型的ID
	public static final String POINT_LSPEED = "lspeed";
	public static final String POINT_ORIENTATION = "ori";
	public static final String POINT_ANGLE = "angle";
	public static final String POINT_CONTENT = "content";
	public static final String POINT_IMGNAME = "iname";
	public static final String POINT_TASK_ID = "tid";// task 的 id
	public static final String POINT_FLAG = "flag";// 提交或 未提交 或者过期
	public static final String POINT_ORDER = "morder";
	public static final String POINT_CAMERAID = "camreaid"; // 电子眼ID
	public static final String POINT_ISFULL = "isfull"; // 信息是否完整
	public static final String POINT_TASKTYPE = "tasktype";// 单次任务 多次任务
	public static final String POINT_DTITTLE = "DTITTLE";
	public static final String POINT_LAST_ALTER = "LASTX";
	public static final String POINT_CAMERATYPE_TEXT = "TYPETEXT";
	public static final String POINT_USERNAME = "username";

	public static enum PF_STATE
	{
		WAIT, OLD, LOST
	}

	public static final int P_ID = 0;
	public static final int P_LAT = 1;
	public static final int P_LON = 2;
	public static final int P_NAME = 3;
	public static final int P_TYPE = 4;
	public static final int P_LSPEED = 5;
	public static final int P_ORIENTATION = 6;
	public static final int P_ANGLE = 7;
	public static final int P_CONTENT = 8;
	public static final int P_IMGNAME = 9;
	public static final int P_TASK_ID = 10;// task 的 id
	public static final int P_FLAG = 11;// // 提交或 未提交 或者过期
	public static final int P_ORDER = 12;
	public static final int P_CAMREAID = 13;
	public static final int P_ISFULL = 14;
	public static final int P_TASKTYPE = 15;
	public static final int P_DTITTLE = 16;
	public static final int P_LAST_ALTER = 17;
	public static final int P_CAMERATYPE_TEXT = 18;
	public static final int P_USERNAME = 19;

	public static final String IMG_NAME = "IMGNAME";
	public static final String IMG_CAMREAID = "CAMREAID";
	public static final int I_NAME = 1;
	public static final int I_CID = 2;
	/**
	 * 完整
	 */
	public static final int POINT_FULL = 1;
	/**
	 * 不完整
	 */
	public static final int POINT_N_FULL = 2;

}

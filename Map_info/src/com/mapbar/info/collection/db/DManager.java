package com.mapbar.info.collection.db;

import java.util.ArrayList;

import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.bean.ImageBean;
import com.mapbar.info.collection.bean.TaskDetail;
import com.mapbar.info.collection.bean.TaskPoint;
import com.mapbar.info.collection.db.MColums.PF_STATE;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 本地数据库管理
 * @author miaowei
 *
 */
public class DManager {
	private static DataBaseHelper baseHelper;
	private static SQLiteDatabase db;
	private static DManager dManager;
	private static Context mContext;

	private DManager() {
		baseHelper = new DataBaseHelper(mContext);
		db = baseHelper.getWritableDatabase();
	}

	public static DManager getInstance(Context context) {
		mContext = context;
		if (dManager == null)
			dManager = new DManager();
		return dManager;
	}

	
	/**
	 * 数据库中添加一条数据
	 * A ok
	 * @param detail
	 *            数据对象
	 * @return 是否添加成功
	 */
	public synchronized boolean addTaskDetail(TaskDetail detail) {
		/*try {
			String sql = "insert into "+DataBaseHelper.TABNAME_WAIT+"(id,description,flag,utime,name,rice,qtime,stime,type,losttime,username) values("+
		             "'"+detail.getId()+"',"+"'"+detail.getDescription()+"',"+ "'"+detail.getFalg()+"',"+
		             "'"+detail.getUpdateTime()+"',"+"'"+detail.getName()+"',"+detail.getRice()+","+
		             "'"+detail.getQdTime()+"',"+"'"+detail.getStartTime()+"',"+"'"+detail.getType()+"',"+
		             "'"+detail.getLostTime()+"',"+"'"+Configs.userName+"')";
				LogPrint.Print("DManager","addTaskDetail==sql"+sql);
				SQLiteDatabase db = baseHelper.getWritableDatabase();
				if(db != null){
					db.execSQL(sql);
					//db.close();
				}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;*/
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.TASK_ID, detail.getId());
		values.put(MColums.TASK_DESCRIPTION, detail.getDescription());
		values.put(MColums.TASK_FLAG, detail.getFalg());
		values.put(MColums.TASK_UTIME, detail.getUpdateTime());
		values.put(MColums.TASK_NAME, detail.getName());
		values.put(MColums.TASK_PRICE, detail.getRice());
		values.put(MColums.TASK_QTIME, detail.getQdTime());
		values.put(MColums.TASK_STIME, detail.getStartTime());
		values.put(MColums.TASK_TYPE, detail.getType());
		values.put(MColums.TASK_LTIME, detail.getLostTime());
		values.put(MColums.TASK_USERNAME, userName);

		long i = db.insert(DataBaseHelper.TABNAME_WAIT, null, values);
		return i != -1;
	}
	/**A
	 * 保存采集点信息到数据库
	 * @param point
	 *            要保存的数据
	 * @return success fail
	 */
	public synchronized boolean addPoint(TaskPoint point) {

		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.POINT_ANGLE, point.getAngle());
		values.put(MColums.POINT_CONTENT, point.getContent());
		values.put(MColums.POINT_FLAG, point.getFlag());
		values.put(MColums.POINT_CAMERAID, point.getCameraId());
		values.put(MColums.POINT_IMGNAME, point.getIname());
		values.put(MColums.POINT_LAT, point.getLat());
		values.put(MColums.POINT_LON, point.getLon());
		values.put(MColums.POINT_LSPEED, point.getLspeed());
		values.put(MColums.POINT_NAME, point.getName());
		values.put(MColums.POINT_ORIENTATION, point.getOri());
		values.put(MColums.POINT_TASK_ID, point.getTid());
		values.put(MColums.POINT_TYPE, point.getType());
		values.put(MColums.POINT_ORDER, point.getOrder());
		values.put(MColums.POINT_ISFULL, point.getIsFull());
		values.put(MColums.POINT_TASKTYPE, point.getTaskType());
		values.put(MColums.POINT_DTITTLE, point.getTittle());
		values.put(MColums.POINT_LAST_ALTER, point.getLastAlret());
		values.put(MColums.POINT_CAMERATYPE_TEXT, point.getCameraTypeText());
		values.put(MColums.POINT_USERNAME, userName);

		/*boolean b = iSAdd(point.getCameraId());
		if (!b) {
			long i = db.insert(DataBaseHelper.TABNAME_POINT, null, values);
			return i != -1;
		}*/
		long i = db.insert(DataBaseHelper.TABNAME_POINT, null, values);
		return i != -1;

	}
	/**
	 * 判断表中是否已经有了这条数据
	 * 
	 * @param tableName
	 *            表名
	 * @param id
	 *            数据id
	 * @return true 已经存在 false 没有
	 */
	public boolean queryDataExists(String tableName, String id) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cur = db.rawQuery("SELECT * FROM " + tableName + " where id=? and username=?", new String[] { id,
				userName});
		if (cur != null) {
			return cur.moveToNext();
		}
		return false;

	}
	/**
	 * 查询采集点表是否有数据
	 * @param cid
	 * @return
	 */
	public boolean iSAdd(String cid) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cu = db.rawQuery("select * from " + DataBaseHelper.TABNAME_POINT + " WHERE CAMREAID=? and username=?",
				new String[] { cid, userName });
		if (cu != null) {
			return cu.moveToNext();
		}
		return false;
	}
	/**
	 * 添加任务之前查询任务是否存在
	 * @param taskStatu任务状态 
	 * @return
	 */
	public boolean queryTask(String taskStatu) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cu = db.rawQuery("select * from " + DataBaseHelper.TABNAME_WAIT + " WHERE flag =? and username=?",
				new String[] { taskStatu, userName });
		try {
			if (cu != null) {
				LogPrint.Print("queryTask==cu.getCount()="+cu.getCount());
				if (cu.getCount() > 0) {
					
					return true;
				}else {
					
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			cu.close();
		}
		return false;
	}

	/**
	 * B ok
	 * @param flag
	 *            是否已经过期
	 * @return 过期或者未过期的Task 根据 flag 返回
	 */
	public ArrayList<TaskDetail> queryAllTask(String flag) {
		TaskDetail detail;
		String userName = StringUtils.getUserName(mContext);
		
		ArrayList<TaskDetail> list = null;
		Cursor cu = db.rawQuery("select * from " + DataBaseHelper.TABNAME_WAIT + " where flag=? and username=?",
				new String[] { flag,userName });
		cu.getCount();
		if (cu != null) {
			list = new ArrayList<TaskDetail>();
			while (cu.moveToNext()) {
				detail = new TaskDetail();
				detail.setDescription(cu.getString(MColums.T_DESCRI));
				detail.setFalg(cu.getString(MColums.T_FLAG));
				detail.setId(cu.getString(MColums.T_ID));
				detail.setUpdateTime(cu.getString(MColums.T_UTIME));
				detail.setName(cu.getString(MColums.T_NAME));
				detail.setQdTime(cu.getString(MColums.T_QTIME));
				detail.setRice(cu.getDouble(MColums.T_PRICE));
				detail.setStartTime(cu.getString(MColums.T_STIME));
				detail.setType(cu.getString(MColums.T_TYPE));
				detail.setLostTime(cu.getString(MColums.T_LTIME));
				list.add(detail);
			}
		}
		cu.close();
		return list;
	}

	/**ok
	 * 分页查找
	 * @param flag
	 *            是否已经过期
	 * @return 过期或者未过期的Task 根据 flag 返回
	 */
	public ArrayList<TaskDetail> queryAllTaskLimit(String flag, int start, int end) {
		TaskDetail detail;
		ArrayList<TaskDetail> list = null;
		String userName = StringUtils.getUserName(mContext);
		String Sql = "select * from " + DataBaseHelper.TABNAME_WAIT + " WHERE FLAG=" + "'" + flag + "'"
				+ " and username=" + "'" + userName + "'" + " limit " + end + " offset " + start;
		Cursor cu = db.rawQuery(Sql, null);
		if (cu != null) {
			list = new ArrayList<TaskDetail>();
			while (cu.moveToNext()) {
				detail = new TaskDetail();
				detail.setDescription(cu.getString(MColums.T_DESCRI));
				detail.setFalg(cu.getString(MColums.T_FLAG));
				detail.setId(cu.getString(MColums.T_ID));
				detail.setUpdateTime(cu.getString(MColums.T_UTIME));
				detail.setName(cu.getString(MColums.T_NAME));
				detail.setQdTime(cu.getString(MColums.T_QTIME));
				detail.setRice(cu.getDouble(MColums.T_PRICE));
				detail.setStartTime(cu.getString(MColums.T_STIME));
				detail.setType(cu.getString(MColums.T_TYPE));
				detail.setLostTime(cu.getString(MColums.T_LTIME));
				list.add(detail);
			}
		}
		cu.close();
		return list;
	}

	public boolean iSNCimmitTaskExistes(String flag) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cu = db.rawQuery("select * from " + DataBaseHelper.TABNAME_POINT + " WHERE FLAG=? and username=?",
				new String[] { flag, userName });
		if (cu != null) {
			while (cu.moveToNext()) {
				return true;
			}
		}
		cu.close();
		return false;
	}

	

	/**ok
	 * 更新该条数据的flag 用于区分是否过期
	 * 
	 * @param id
	 *            要更新的数据
	 * @param state
	 *            否是过期的falg Mcolums.WAIT OLD LOST
	 */
	public synchronized void updataTaskFlag(String id, String state) {
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.TASK_FLAG, state);
		db.update(DataBaseHelper.TABNAME_WAIT, values, "id=? and username=?", new String[] { id, userName });
	}

	/**ok
	 * 根据id 删除这条数据
	 * 
	 * @param id
	 *            要删除数据的id
	 */
	public void deleteTaskDetail(String id) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_WAIT, "id=? and username=?", new String[] { id, userName });
	}

	/**
	 * 根据flag 删除所有未提交 已提交 过期的数据
	 * 
	 * @param flag
	 *            要删除数据的flag
	 */
	public void deleteTaskDetailAll(String flag) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_WAIT, "flag=? and username=?", new String[] { flag, userName });
	}

	/**
	 * 数据库中添加一条数据
	 * 
	 * @param detail
	 *            标识为已提交数据对象
	 * @return 是否添加成功
	 */
	public synchronized boolean addOldTask(TaskDetail detail) {
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.OLD_ID, detail.getId());
		values.put(MColums.OLD_DESCRIPTION, detail.getDescription());
		values.put(MColums.OLD_FLAG, detail.getFalg());
		values.put(MColums.OLD_UTIME, detail.getUpdateTime());
		values.put(MColums.OLD_NAME, detail.getName());
		values.put(MColums.OLD_PRICE, detail.getRice());
		values.put(MColums.OLD_QTIME, detail.getQdTime());
		values.put(MColums.OLD_STIME, detail.getStartTime());
		values.put(MColums.OLD_TYPE, detail.getType());
		values.put(MColums.OLD_LTIME, detail.getLostTime());
		values.put(MColums.OLD_USERNAME, userName);
		long i = db.insert(DataBaseHelper.TABNAME_OLD, null, values);
		return i != -1;
	}

	/**
	 * 
	 * @param flag
	 *            是否已经过期
	 * @return 过期或者未过期的Task 根据 flag 返回
	 */
	public synchronized ArrayList<TaskDetail> queryAllOldTask(String flag) {
		TaskDetail detail;
		ArrayList<TaskDetail> list = null;
		String userName = StringUtils.getUserName(mContext);
		Cursor cu = db.rawQuery("select * from " + DataBaseHelper.TABNAME_OLD + " where username=" + "'"
				+ userName + "'", null);
		if (cu != null) {
			list = new ArrayList<TaskDetail>();
			while (cu.moveToNext()) {
				detail = new TaskDetail();
				detail.setDescription(cu.getString(MColums.O_DESCRI));
				detail.setFalg(cu.getString(MColums.O_FLAG));
				detail.setId(cu.getString(MColums.O_ID));
				detail.setUpdateTime(cu.getString(MColums.O_UTIME));
				detail.setName(cu.getString(MColums.O_NAME));
				detail.setQdTime(cu.getString(MColums.O_QTIME));
				detail.setRice(cu.getInt(MColums.O_PRICE));
				detail.setStartTime(cu.getString(MColums.O_STIME));
				detail.setType(cu.getString(MColums.O_TYPE));
				detail.setLostTime(cu.getString(MColums.O_LTIME));
				list.add(detail);
			}
		}
		cu.close();
		return list;
	}

	/**
	 * 
	 * @param flag
	 *            是否已经过期
	 * @return 过期或者未过期的Task 根据 flag 返回
	 */
	public synchronized ArrayList<TaskDetail> queryAllOldTaskByLimit(int start, int end) {
		TaskDetail detail;
		ArrayList<TaskDetail> list = null;// " WHERE FLAG=" + "'" + flag + "'" +
		String userName = StringUtils.getUserName(mContext);
		String Sql = "select * from " + DataBaseHelper.TABNAME_OLD + " where username=" + "'" + userName + "'"
				+ " limit " + end + " offset " + start;
		Cursor cu = db.rawQuery(Sql, null);
		if (cu != null) {
			list = new ArrayList<TaskDetail>();
			while (cu.moveToNext()) {
				detail = new TaskDetail();
				detail.setDescription(cu.getString(MColums.O_DESCRI));
				detail.setFalg(cu.getString(MColums.O_FLAG));
				detail.setId(cu.getString(MColums.O_ID));
				detail.setUpdateTime(cu.getString(MColums.O_UTIME));
				detail.setName(cu.getString(MColums.O_NAME));
				detail.setQdTime(cu.getString(MColums.O_QTIME));
				detail.setRice(cu.getInt(MColums.O_PRICE));
				detail.setStartTime(cu.getString(MColums.O_STIME));
				detail.setType(cu.getString(MColums.O_TYPE));
				detail.setLostTime(cu.getString(MColums.O_LTIME));
				list.add(detail);
			}
		}
		cu.close();
		return list;
	}

	/**
	 * 更新该条数据的flag 用于区分是否过期
	 * 
	 * @param id
	 *            要更新的数据
	 * @param state
	 *            否是过期的falg Mcolums.WAIT OLD LOST
	 */
	public synchronized void updataOldTaskFlag(String id, String state) {
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.OLD_FLAG, state);
		db.update(DataBaseHelper.TABNAME_OLD, values, "id=? and username=?", new String[] { id, userName });
	}

	/**
	 * 根据id 删除这条数据
	 * 
	 * @param id
	 *            要删除数据的id
	 */
	public void deleteOldTaskDetail(String id) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_OLD, "id=? and username=?", new String[] { id, userName});
	}

	/**
	 * 根据flag 删除所有未提交 已提交 过期的数据
	 * 
	 * @param flag
	 *            要删除数据的flag
	 */
	public void deleteOldTaskDetailAll() {
		db.delete(DataBaseHelper.TABNAME_OLD, null, null);
	}

	

	/**
	 * ok
	 * 根据task id 和flag 查询 采集点的信息
	 * 
	 * @param tid
	 *            taskid
	 * @param flag
	 *            提交或者未提交的过期的
	 * @return 返回未提交或者提交过的 TaskPoint 集合
	 */
	public ArrayList<TaskPoint> queryPoint(String tid, String flag) {
		ArrayList<TaskPoint> list = null;
		TaskPoint point;
		String userName = StringUtils.getUserName(mContext);
		Cursor cursor = db.rawQuery("select * from " + DataBaseHelper.TABNAME_POINT
				+ " where tid=? and flag=? and username=?", new String[] { tid, flag, userName });
		if (cursor != null) {
			list = new ArrayList<TaskPoint>();
			while (cursor.moveToNext()) {
				point = new TaskPoint();
				point.setAngle(cursor.getString(MColums.P_ANGLE));
				point.setContent(cursor.getString(MColums.P_CONTENT));
				point.setFlag(cursor.getString(MColums.P_FLAG));
				point.setCameraId(cursor.getString(MColums.P_CAMREAID));
				point.setIname(cursor.getString(MColums.P_IMGNAME));
				point.setLat(cursor.getString(MColums.P_LAT));
				point.setLon(cursor.getString(MColums.P_LON));
				point.setLspeed(cursor.getString(MColums.P_LSPEED));
				point.setName(cursor.getString(MColums.P_NAME));
				point.setOri(cursor.getString(MColums.P_ORIENTATION));
				point.setTid(cursor.getString(MColums.P_TASK_ID));
				point.setType(cursor.getString(MColums.P_TYPE));
				point.setOrder(cursor.getString(MColums.P_ORDER));
				point.setIsFull(cursor.getInt(MColums.P_ISFULL));
				point.setTaskType(cursor.getInt(MColums.P_TASKTYPE));
				point.setTittle(cursor.getString(MColums.P_DTITTLE));
				point.setLastAlret(cursor.getString(MColums.P_LAST_ALTER));
				point.setCameraTypeText(cursor.getString(MColums.P_CAMERATYPE_TEXT));

				list.add(point);
			}
		}

		return list;
	}

	/**B ok
	 * 根据task id 和flag 查询 采集点的信息
	 * 
	 * @param tid
	 *            taskid
	 * @param flag
	 *            提交或者未提交的过期的
	 * @return 返回未提交或者提交过的 TaskPoint 集合
	 */
	public ArrayList<TaskPoint> queryOldPoint(String flag) {
		ArrayList<TaskPoint> list = null;
		TaskPoint point;
		String userName = StringUtils.getUserName(mContext);
		Cursor cursor = db.rawQuery("select * from " + DataBaseHelper.TABNAME_POINT + " where flag=? and username=?",
				new String[] { flag, userName });
		if (cursor != null) {
			list = new ArrayList<TaskPoint>();
			while (cursor.moveToNext()) {
				point = new TaskPoint();
				point.setAngle(cursor.getString(MColums.P_ANGLE));
				point.setContent(cursor.getString(MColums.P_CONTENT));
				point.setFlag(cursor.getString(MColums.P_FLAG));
				point.setCameraId(cursor.getString(MColums.P_CAMREAID));
				point.setIname(cursor.getString(MColums.P_IMGNAME));
				point.setLat(cursor.getString(MColums.P_LAT));
				point.setLon(cursor.getString(MColums.P_LON));
				point.setLspeed(cursor.getString(MColums.P_LSPEED));
				point.setName(cursor.getString(MColums.P_NAME));
				point.setOri(cursor.getString(MColums.P_ORIENTATION));
				point.setTid(cursor.getString(MColums.P_TASK_ID));
				point.setType(cursor.getString(MColums.P_TYPE));
				point.setOrder(cursor.getString(MColums.P_ORDER));
				point.setIsFull(cursor.getInt(MColums.P_ISFULL));
				point.setTaskType(cursor.getInt(MColums.P_TASKTYPE));
				point.setTittle(cursor.getString(MColums.P_DTITTLE));
				point.setLastAlret(cursor.getString(MColums.P_LAST_ALTER));
				point.setCameraTypeText(cursor.getString(MColums.P_CAMERATYPE_TEXT));

				list.add(point);
			}
		}

		return list;
	}

	/**
	 * 根据task id 和flag 查询 采集点的信息
	 * 
	 * @param tid
	 *            taskid
	 * @param flag
	 *            提交或者未提交的过期的
	 * @return 返回未提交或者提交过的 TaskPoint 集合
	 */
	public ArrayList<TaskPoint> queryAllPoint() {
		ArrayList<TaskPoint> list = null;
		TaskPoint point;
		String userName = StringUtils.getUserName(mContext);
		Cursor cursor = db.rawQuery("select * from " + DataBaseHelper.TABNAME_POINT + " where username=?",
				new String[] { userName });
		if (cursor != null) {
			list = new ArrayList<TaskPoint>();
			while (cursor.moveToNext()) {
				point = new TaskPoint();
				point.setAngle(cursor.getString(MColums.P_ANGLE));
				point.setContent(cursor.getString(MColums.P_CONTENT));
				point.setFlag(cursor.getString(MColums.P_FLAG));
				point.setCameraId(cursor.getString(MColums.P_CAMREAID));
				point.setIname(cursor.getString(MColums.P_IMGNAME));
				point.setLat(cursor.getString(MColums.P_LAT));
				point.setLon(cursor.getString(MColums.P_LON));
				point.setLspeed(cursor.getString(MColums.P_LSPEED));
				point.setName(cursor.getString(MColums.P_NAME));
				point.setOri(cursor.getString(MColums.P_ORIENTATION));
				point.setTid(cursor.getString(MColums.P_TASK_ID));
				point.setType(cursor.getString(MColums.P_TYPE));
				point.setOrder(cursor.getString(MColums.P_ORDER));
				point.setIsFull(cursor.getInt(MColums.P_ISFULL));
				point.setIsFull(cursor.getInt(MColums.P_TASKTYPE));
				point.setTittle(cursor.getString(MColums.P_DTITTLE));
				point.setLastAlret(cursor.getString(MColums.P_LAST_ALTER));
				point.setCameraTypeText(cursor.getString(MColums.P_CAMERATYPE_TEXT));
				list.add(point);
			}
		}

		return list;
	}

	/**ok
	 * 更新point的flag 为已提交
	 * 
	 * @param id
	 *            要更新的id
	 * @param state
	 *            要更新的字段
	 * @param str
	 *            当前系统时间
	 */
	public synchronized void updatePointFlag(String cameraId, PF_STATE state, String str) {
		String flag = null;
		String userName = StringUtils.getUserName(mContext);
		switch (state)
		{
			case WAIT:
				flag = MColums.WAIT;
			break;
			case OLD:
				flag = MColums.OLD;
			break;
			case LOST:
				flag = MColums.LOST;
			break;

			default:
			break;
		}
		ContentValues values = new ContentValues();
		values.put(MColums.POINT_FLAG, flag);
		values.put(MColums.POINT_LAST_ALTER, str);

		db.update(DataBaseHelper.TABNAME_POINT, values, "CAMREAID=? and username=?", new String[] { cameraId,
				userName });

	}

	/**
	 * 更新point的flag 为已提交
	 * 
	 * @param id
	 *            要更新的id
	 * @param state
	 *            要更新的字段
	 */
	public synchronized void updatePoint(TaskPoint point, String cameraId) {
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.POINT_ANGLE, point.getAngle());
		values.put(MColums.POINT_CONTENT, point.getContent());
		values.put(MColums.POINT_FLAG, point.getFlag());
		values.put(MColums.POINT_CAMERAID, point.getCameraId());
		values.put(MColums.POINT_IMGNAME, point.getIname());
		values.put(MColums.POINT_LAT, point.getLat());
		values.put(MColums.POINT_LON, point.getLon());
		values.put(MColums.POINT_LSPEED, point.getLspeed());
		values.put(MColums.POINT_NAME, point.getName());
		values.put(MColums.POINT_ORIENTATION, point.getOri());
		values.put(MColums.POINT_TASK_ID, point.getTid());
		values.put(MColums.POINT_TYPE, point.getType());
		values.put(MColums.POINT_ORDER, point.getOrder());
		values.put(MColums.POINT_ISFULL, point.getIsFull());
		values.put(MColums.POINT_TASKTYPE, point.getTaskType());
		values.put(MColums.POINT_DTITTLE, point.getTittle());
		values.put(MColums.POINT_LAST_ALTER, point.getLastAlret());
		values.put(MColums.POINT_CAMERATYPE_TEXT, point.getCameraTypeText());

		db.update(DataBaseHelper.TABNAME_POINT, values, "CAMREAID=? and username=?", new String[] { cameraId,
				userName });

	}

	/**
	 * 更新采集点信息的状态 是完整
	 * 
	 * @param cameraId
	 * @param isfull
	 * @param str
	 *            当前系统时间
	 */
	public synchronized void updataPointFull(String cameraId, int isfull, String str) {
		String userName = StringUtils.getUserName(mContext);
		ContentValues values = new ContentValues();
		values.put(MColums.POINT_ISFULL, isfull);
		values.put(MColums.POINT_LAST_ALTER, str);
		db.update(DataBaseHelper.TABNAME_POINT, values, "CAMREAID=? and username=?", new String[] { cameraId,
				userName });

	}

	/**
	 * 根据id 删除 point 数据
	 * 
	 * @param id
	 */
	public void deletePoint(String cameraId) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_POINT, "CAMREAID=? and username=?",
				new String[] { cameraId, userName });
	}

	/**
	 * 根据TaskId 删除 point
	 * 
	 * @param tid
	 */
	public void deletePointByTid(String tid, String flag) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_POINT, "TID=? and flag=? and username=?", new String[] { tid, flag,
				userName });
	}

	public void deletePointByFlag(String flag) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_POINT, " flag=? and username=?", new String[] { flag, userName });
	}

	/**
	 * 根据flag 删除全部 未提交的 已提交的 过期的
	 * 
	 * @param flag
	 */
	public void deleteAllPoint(String flag) {
		String userName = StringUtils.getUserName(mContext);
		db.delete(DataBaseHelper.TABNAME_POINT, "flag=? and username=?", new String[] { flag, userName });
	}

	

	/**
	 * 查询采集点表，采集点是否存在
	 * @param tableName 表名
	 * @param camreaid 电子眼ID
	 * @return
	 */
	public boolean queryPointExite(String tableName, String camreaid) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cur = db.rawQuery("SELECT * FROM " + tableName + " where camreaid=? and username=?", new String[] {
				camreaid, userName });
		if (cur != null) {
			return cur.moveToNext();
		}
		return false;

	}

	/**
	 * 根据电子眼ID，获取采集点状态，待提交、已提交、已过期
	 * @param tableName 采集点表
	 * @param camreaid 电子眼ID
	 * @return 采集点状态
	 */
	public String queryPointStatus(String tableName, String camreaid) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cur = db.rawQuery("SELECT * FROM " + tableName + " where camreaid=? and username=?", new String[] {
				camreaid, userName });
		if (cur != null) {
			boolean b = cur.moveToNext();
			if (b)
				return cur.getString(MColums.P_FLAG);
		}
		return null;

	}
	
	public String queryPointWhichPoint(String tableName, String camreaid) {
		String userName = StringUtils.getUserName(mContext);
		Cursor cur = db.rawQuery("SELECT * FROM " + tableName + " where camreaid=? and username=?", new String[] {
				camreaid, userName });
		if (cur != null) {
			boolean b = cur.moveToNext();
			if (b)
				return cur.getString(MColums.P_IMGNAME);
		}
		return null;

	}

	/**
	 * 保存图片名字到数据库
	 * 
	 * @param bean
	 * @return
	 */
	public boolean addImageName(ImageBean bean) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(MColums.IMG_NAME, bean.getImageName());
		contentValues.put(MColums.IMG_CAMREAID, bean.getCameraId());

		long i = db.insert(DataBaseHelper.TABNAME_IMG, null, contentValues);
		return i != -1;

	}

	/**
	 * 根据cid 获取 该point的图片Name
	 * 
	 * @param cid
	 * @return
	 */
	public ArrayList<ImageBean> queryImageNameByCameraID(String cid) {
		ArrayList<ImageBean> list = null;
		ImageBean imageBean;

		Cursor cursor = db.rawQuery("select * from " + DataBaseHelper.TABNAME_IMG + " where CAMREAID=?",
				new String[] { cid });
		if (cursor != null) {
			list = new ArrayList<ImageBean>();
			while (cursor.moveToNext()) {
				imageBean = new ImageBean();
				imageBean.setCameraId(cursor.getString(MColums.I_CID));
				imageBean.setImageName(cursor.getString(MColums.I_NAME));
				list.add(imageBean);
			}
		}

		return list;

	}

	/**
	 * 根据cid
	 * 
	 * @param cid
	 */
	public void deleteImgName(String cid) {
		db.delete(DataBaseHelper.TABNAME_POINT, "CAMREAID=?", new String[] { cid });
	}
	
}

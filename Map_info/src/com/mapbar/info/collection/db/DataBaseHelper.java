package com.mapbar.info.collection.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "ipoint.db";
	/**
	 * 待提交表
	 */
	public static final String TABNAME_WAIT = "wait";
	/**
	 * 已提交表
	 */
	public static final String TABNAME_OLD = "old";
	/**
	 * 任务点表
	 */
	public static final String TABNAME_POINT = "point";
	/**
	 * 拍摄图片表(未使用)
	 */
	public static final String TABNAME_IMG = "img_name";

	public DataBaseHelper(Context context) {
		super(context, DBNAME, null, 2);
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		/*String sql = "CREATE TABLE IF NOT EXISTS "
				+ TABNAME_WAIT
				+ "("
				+ "ID TEXT PRIMARY KEY, NAME TEXT,RICE INTEGER,DESCRIPTION TEXT,TYPE TEXT,STIME NUMBER,UTIME NUMBER,QTIME NUMBER,FLAG TEXT,LOSTTIME NUMBER,TIMEID TEXT,USERNAME TEXT"
				+ ")";*/
		
		  String sql = "CREATE TABLE IF NOT EXISTS "
				+ TABNAME_WAIT
				+ "("
				+ "UID INTEGER PRIMARY KEY autoincrement,ID TEXT, NAME TEXT,RICE DOUBLE,DESCRIPTION TEXT,TYPE TEXT,STIME NUMBER,UTIME NUMBER,QTIME NUMBER,FLAG TEXT,LOSTTIME NUMBER,TIMEID TEXT,USERNAME TEXT"
				+ ")";
		
		String sql_OLD = "CREATE TABLE IF NOT EXISTS "
				+ TABNAME_OLD
				+ "("
				+ "ID TEXT PRIMARY KEY, NAME TEXT,RICE INTEGER,DESCRIPTION TEXT,TYPE TEXT,STIME NUMBER,UTIME NUMBER,QTIME NUMBER,FLAG TEXT,LOSTTIME NUMBER,TIMEID TEXT,USERNAME TEXT"
				+ ")";
		String sql_point = "CREATE TABLE IF NOT EXISTS "
				+ TABNAME_POINT
				+ "("
				+ "ID INTEGER PRIMARY KEY  autoincrement, LAT TEXT,LON TEXT,NAME TEXT,TYPE TEXT,LSPEED TEXT,ORI TEXT,ANGLE TEXT,CONTENT TEXT,INAME TEXT,TID TEXT,FLAG TEXT,MORDER TEXT,CAMREAID TEXT,ISFULL INTEGER,TASKTYPE INTEGER,DTITTLE TEXT,LASTX TEXT,TYPETEXT TEXT,USERNAME TEXT"
				+ ")";
		String sql_img_name = "CREATE TABLE IF NOT EXISTS " + TABNAME_IMG + "("
				+ "ID INTEGER PRIMARY KEY  autoincrement, IMGNAME TEXT,CAMREAID TEXT" + ")";
		db.execSQL(sql);
		db.execSQL(sql_OLD);
		db.execSQL(sql_point);
		db.execSQL(sql_img_name);
	}

   //升级数据库步骤
	//1、将表A重命名，改为A_temp临时表
	//2、创建新表A
	//3、将临时表的数据添加到表A
	//4、将临时表删除
	/**
	 * 
	 * @param db 执行SQL语句
	 * @param tableName 表名
	 * @param columns 添加的列名
	 */
	private void upgradeTables(SQLiteDatabase db,String tableName,String columns){
		
		try {
			db.beginTransaction();
			//1 将表A重命名，改为A_temp临时表
			String tempTableName = tableName + "_temp";
			String sql = "alter table "+ tableName + " RENAME TO "+ tempTableName;
			db.execSQL(sql);
			
		
			//2 创建新表A
			onCreate(db);
			
			//3 将临时表的数据添加到表A
			sql =   "INSERT INTO " + tableName +  
	                " (" + columns + ") " +  
	                " SELECT " + columns + " FROM " + tempTableName;  
			db.execSQL(sql);
			
			//4 将临时表删除
			db.execSQL("drop table " + tempTableName);
			
			db.setTransactionSuccessful(); 
		}catch(SQLException e){
			
			e.printStackTrace();
		}
		catch (Exception e) {
			
			e.printStackTrace();
			
		}finally{
			
			db.endTransaction();
		}
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		for (int i = oldVersion; i <= newVersion; i++) {
			
			switch (i) {
			case 2:
				upgradeTables(db, TABNAME_WAIT, "ID,NAME,RICE,DESCRIPTION,TYPE,STIME,UTIME,QTIME,FLAG,LOSTTIME,TIMEID,USERNAME");
				break;
			default:
				break;
			}
		}
	}
}

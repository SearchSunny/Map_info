package com.mapbar.info.collection.db;

import android.provider.BaseColumns;

public final class TaskProviderConfigs {
	public static final String AUTHORITY = "com.mapbar.android.accompany.provider.FavoriteProvider";

	public TaskProviderConfigs() {

	}

	private final class Tas implements BaseColumns {

		private Tas() {
			
		}

		public static final String TASK_ID = "id";
		public static final String TASK_NAME = "name";
		public static final String TASK_PRICE = "price";
		public static final String TASK_CONTENT = "content";
		public static final String TASK_TYPE = "type";
		public static final String TASK_STIME = "stime";
		public static final String TASK_ETIME = "etime";
		public static final String TASK_LTIME = "ltime";
		

		public static final String POINT_ID = "id";
		public static final float POINT_LAT = 0;
		public static final float POINT_LON = 0;
		public static final int POINT_LSPEED = 0;
		public static final int POINT_ORIENTATION = 0;
		public static final int POINT_ANGLE = 0;
		public static final String POINT_TASK_ID = "flag";
		public static final String POINT_CONTENT = "content";
	}

}

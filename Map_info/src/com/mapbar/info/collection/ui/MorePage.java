package com.mapbar.info.collection.ui;

import android.content.Context;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.util.LogPrint;
/**
 * 更多页面
 * @author miaowei
 *
 */
public class MorePage extends BasePage implements OnClickListener {

	private Context mContext;
	private ActivityInterface mActivityInterface;
	private TitleBar bar;
	private RelativeLayout alter_password;
	private RelativeLayout sloute_question;
	private RelativeLayout new_hands;
	private RelativeLayout about_app;
	private Button user_btn_task;
	private Button user_btn_more;
	private Button user_btn_info;

	/**
	 * 更多页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public MorePage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		this.mActivityInterface = aif;
		bar = new TitleBar(mContext, view, this, mActivityInterface);

		alter_password = (RelativeLayout) view.findViewById(R.id.more_item_alter_pwd);
		sloute_question = (RelativeLayout) view.findViewById(R.id.more_item_request);
		new_hands = (RelativeLayout) view.findViewById(R.id.more_item_new);
		about_app = (RelativeLayout) view.findViewById(R.id.more_item_about);

		alter_password.setOnClickListener(this);
		sloute_question.setOnClickListener(this);
		new_hands.setOnClickListener(this);
		about_app.setOnClickListener(this);

		user_btn_task = (Button) view.findViewById(R.id.more_btn_do_task);
		user_btn_info = (Button) view.findViewById(R.id.more_btn_user_info);
		user_btn_more = (Button) view.findViewById(R.id.more_btn_seeting_more);

		user_btn_task.setOnClickListener(this);
		user_btn_info.setOnClickListener(this);
		user_btn_more.setOnClickListener(this);
		user_btn_more.setSelected(true);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow(int flag, int position) {

		super.onAttachedToWindow(flag, position);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		goBack();

		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		LogPrint.Print("gps", "MorePage--onLocationChanged---location="+location.getProvider());
		CustomCameraActivity.mlocation = location;
		
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_MORE;
	}

	@Override
	public void onTitleBarClick(View v) {
		super.onTitleBarClick(v);
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_INDEX, null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			//修改密码
			case R.id.more_item_alter_pwd:
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_ALTER_PASSWORD, 0,
						null, null);
			break;
			//热点答疑
			case R.id.more_item_request:
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_NUMEN, 0, null, null);

			break;
			//新手攻略(2.0改为拍照教程)
			case R.id.more_item_new:
				mActivityInterface.showPage(getMyViewPosition(), this, Configs.VIEW_POSITION_INTRODUCE_IMG, -1, null,
						null);
			break;
			//关于
			case R.id.more_item_about:
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_ABOUT, 0, null, null);
			break;
			//做任务
			case R.id.more_btn_do_task:
				mActivityInterface.showPrevious(Configs.VALUE_POSITION_NONE, this, Configs.VIEW_POSITION_INDEX, null,
						null);
			break;
			//更多
			case R.id.user_btn_seeting_more:
			break;
			//我的信息
			case R.id.more_btn_user_info:
				mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_USER_INFO, 0, null,
						null);
			break;

			default:
			break;
		}

	}

}

package com.mapbar.info.collection.ui;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.util.LogPrint;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 关于页面
 * @author miaowei
 *
 */
public class AboutPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface mActivityInterface;

	private TextView textSoft;
	private ImageButton button;

	/**
	 * 关于页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public AboutPage(Context context, View view, ActivityInterface aif) {
		TitleBar bar = new TitleBar(context, view, this, aif);
		this.mContext = context;
		this.mActivityInterface = aif;
		textSoft = (TextView) view.findViewById(R.id.about_soft);
		button = (ImageButton) view.findViewById(R.id.btn_home);
		button.setOnClickListener(this);
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packInfo.versionName;
		textSoft.setText(version);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, null, null);
		return true;
	}

	@Override
	public void goBack() {
		super.goBack();
	}

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_POSITION_ABOUT;
	}

	@Override
	public void onClick(View v) {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, null, null);

	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		
       LogPrint.Print("gps", "AboutPage--onLocationChanged---location="+location.getProvider());
		
		super.onLocationChanged(location);
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}

}

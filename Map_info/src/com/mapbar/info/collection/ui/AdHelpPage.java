package com.mapbar.info.collection.ui;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.TitleBar;
import com.mapbar.info.collection.widget.HelpAdapter;
import com.mapbar.info.collection.widget.MyGallery;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 广告引导页面
 * @author miaowei
 *
 */
public class AdHelpPage extends BasePage implements OnClickListener {
	private Context mContext;
	private ActivityInterface mActivityInterface;

	private MyGallery myGallery;
	/**
	 * 广告引导页面
	 * @param context
	 * @param view
	 * @param aif
	 */
	public AdHelpPage(Context context, View view, ActivityInterface aif) {
		this.mContext = context;
		myGallery = (MyGallery)view.findViewById(R.id.gallery);
		myGallery.setSpacing(10);
		myGallery.setAdapter(new HelpAdapter(mContext,aif));
		this.mActivityInterface = aif;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	public void goBack() {
		super.goBack();
	}

	@Override
	public int getMyViewPosition() {

		return Configs.VIEW_AD_HELP;
	}

	@Override
	public void onClick(View v) {
		
	}


}

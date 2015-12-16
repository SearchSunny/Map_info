package com.mapbar.info.collection.ui;

import android.content.Context;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.MainActivity;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.widget.ScrollLayout;
/**
 * 拍照教程使用说明
 * @author miaowei
 *
 */
public class IntroducePageHelp extends BasePage {

	private Context mContext;
	private ActivityInterface mActivityInterface;

	private ScrollLayout scrollLayout;
	private String[] introduce;
	private TextView text;

	/**
	 * 拍照教程使用说明
	 * @param context
	 * @param view
	 * @param aif
	 */
	public IntroducePageHelp(Context context, View view, ActivityInterface aif) {
		mContext = context;
		mActivityInterface = aif;
		introduce = context.getResources().getStringArray(R.array.new_introduce_list_text);
		scrollLayout = (ScrollLayout) view.findViewById(R.id.introduce_scrolllayout);
		text = (TextView) view.findViewById(R.id.introduce_help_image_text);
	}

	private int position;

	@Override
	public void onAttachedToWindow(int flag, int position) {
		this.position = position;
		text.setText(introduce[position]);
		scrollLayout.setDefaultScreen(0);
		scrollLayout.removeAllViews();
		// for (int i = 0; i < ids.length; i++) {
		// ImageView imageView = new ImageView(mContext);
		// imageView.setBackgroundDrawable(mContext.getResources().getDrawable(ids[i]));
		// scrollLayout.addView(imageView);
		// }
		((MainActivity) mActivityInterface).runOnUiThread(new LoadImg());
	}

	private class LoadImg implements Runnable {
		@Override
		public void run() {
			int ids[] = getDrawableID(position);
			for (int i = 0; i < ids.length; i++) {
				ImageView imageView = new ImageView(mContext);

				imageView.setImageDrawable(mContext.getResources().getDrawable(ids[i]));
				scrollLayout.addView(imageView);
			}

		}
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		scrollLayout.removeAllViews();
		scrollLayout.setDefaultScreen(0);
		super.onDetachedFromWindow(flag);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_INTRODUCE_HELP;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		goBack();
		return true;
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_INTRODUCE_IMG, null, null);
	}

	private int[] getDrawableID(int position) {
		if (position == 0) { //警示牌
			int[] ids = { R.drawable.introduce_four_one, R.drawable.introduce_four_two,
					R.drawable.introduce_four_three, R.drawable.introduce_four_four, R.drawable.introduce_four_five,
					R.drawable.introduce_four_six };
			return ids;
		} else if (position == 1) {//限高牌
			int[] ids = { R.drawable.introduce_five_one };
			return ids;
		} else if (position == 2) {//红灯路口摄像头
			
			int[] ids = { R.drawable.introduce_one_one, R.drawable.introduce_one_two };
			return ids;
		} else if (position == 3) {//固定测速摄像头
			int[] ids = { R.drawable.introduce_two_one, R.drawable.introduce_two_two, R.drawable.introduce_two_three,
					R.drawable.introduce_two_four, R.drawable.introduce_two_five, R.drawable.introduce_two_six };
			return ids;
		} else if (position == 4) {//监控照相摄像头
			
			int[] ids = { R.drawable.introduce_three_one, R.drawable.introduce_three_two,
					R.drawable.introduce_three_three };
			return ids;
		} else if (position == 5) {//汽车及轮胎修理
			int[] ids = { R.drawable.introduce_six_one, R.drawable.introduce_six_two, R.drawable.introduce_six_three };
			return ids;
		} else if (position == 6) {//停车场及物流市场
			int[] ids = { R.drawable.introduce_selven_one, R.drawable.introduce_selven_two,
					R.drawable.introduce_selven_three };
			return ids;
		}

		return null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "IntroducePageHelp--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}

}

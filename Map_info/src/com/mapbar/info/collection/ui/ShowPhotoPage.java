package com.mapbar.info.collection.ui;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.widget.MyPageAdapter;
import com.mapbar.info.collection.widget.NumenExpandAdatpger;
import com.mapbar.info.collection.widget.ScrollLayout;

/**
 * 暂且搁置 等有时间再 优化
 * 
 * @author admin
 * 
 */
public class ShowPhotoPage extends BasePage implements OnItemClickListener {

	private Context mContext;
	private View view;
	private ActivityInterface mActivityInterface;
	private MyPageAdapter pageAdapter;

	public ShowPhotoPage(Context context, View view, ActivityInterface aif) {
		mContext = context;
		mActivityInterface = aif;

	}

	@Override
	public void onAttachedToWindow(int flag, int position) {

		super.onAttachedToWindow(flag, position);
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow(flag);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_PHOTO;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		goBack();
		return true;
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_CAMERA, null, null);
	}

	private class ImageAdapter extends BaseAdapter {
		private String[] data;
		private Context mContext;

		public ImageAdapter(Context context, String[] data) {
			this.data = data;
			mContext = context;

		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View itemView, ViewGroup parent) {
			ViewHoler viewHoler;
			if (itemView == null) {
				itemView = View.inflate(mContext, R.layout.layout_introduce_item, null);
				viewHoler = new ViewHoler();
				viewHoler.textView = (TextView) itemView.findViewById(R.id.introduce_item_text);
				itemView.setTag(viewHoler);
			} else {
				viewHoler = (ViewHoler) itemView.getTag();
			}
			viewHoler.textView.setText(data[position]);
			return itemView;
		}

	}

	class ViewHoler {
		TextView textView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mActivityInterface.showPage(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_INTRODUCE_HELP, position, null,
				null);
	}
}

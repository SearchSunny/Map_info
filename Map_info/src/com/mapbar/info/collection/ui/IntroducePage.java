package com.mapbar.info.collection.ui;

import android.content.Context;
import android.location.Location;
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
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.widget.NumenExpandAdatpger;
import com.mapbar.info.collection.widget.ScrollLayout;
/**
 * 拍照教程
 * @author miaowei
 *
 */
public class IntroducePage extends BasePage implements OnItemClickListener {

	private Context mContext;
	private View view;
	private ActivityInterface mActivityInterface;
	private NumenExpandAdatpger adatpger;
	private ListView imgListView;

	private ImageButton btn_back;
	private TextView textView;
	private ImageAdapter adapter;
	private ScrollLayout scrollLayout;
	/**
	 * 标识是从哪个页面过来的
	 */
	private int fromFlag;

	/**
	 * 拍照教程
	 * @param context
	 * @param view
	 * @param aif
	 */
	public IntroducePage(Context context, View view, ActivityInterface aif) {
		mContext = context;
		mActivityInterface = aif;
		textView = (TextView) view.findViewById(R.id.title_text);
		imgListView = (ListView) view.findViewById(R.id.introduce_listview);
		String[] introduce = context.getResources().getStringArray(R.array.new_introduce_list);
		adapter = new ImageAdapter(mContext, introduce);


		imgListView.setAdapter(adapter);
		imgListView.setOnItemClickListener(this);
		btn_back = (ImageButton) view.findViewById(R.id.btn_home);

		textView.setText("拍照教程");
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();

			}
		});

	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		fromFlag = flag;
		super.onAttachedToWindow(flag, position);
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow(flag);
	}

	@Override
	public int getMyViewPosition() {
		return Configs.VIEW_POSITION_INTRODUCE_IMG;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		goBack();
		return true;
	}

	@Override
	public void goBack() {
		//如果是从抢单页面过来的
		if (fromFlag == Configs.VIEW_POSITION_CAMERA){
			
			mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_CAMERA, null, null);
			
		}else {
			
			mActivityInterface.showPrevious(getMyViewPosition(), this, Configs.VIEW_POSITION_MORE, null, null);
		}
		
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
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "IntroducePage--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
}

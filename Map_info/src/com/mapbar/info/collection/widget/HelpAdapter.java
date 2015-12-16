package com.mapbar.info.collection.widget;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.util.SharedPreferencesUtil;
import com.mapbar.info.collection.util.Util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * 广告引导
 * @author miaowei
 *
 */
public class HelpAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context context;
	private ActivityInterface mActivityInterface;
	public HelpAdapter(Context context,ActivityInterface aif){
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.mActivityInterface = aif;
	}
	
	@Override
	public int getCount() {
		
		return 3;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HelpHolder holder = null;
		if(convertView == null){
			holder = new HelpHolder();
			switch (position) {
			case 0:
				convertView = mInflater.inflate(R.layout.layout_help1a, null);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.help1a);
				convertView.setTag(holder);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.layout_help2a, null);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.help2a);
				convertView.setTag(holder);
				break;
			case 2:
				convertView = mInflater.inflate(R.layout.layout_help3a, null);
				ImageView imageView = (ImageView)convertView.findViewById(R.id.help3_2);
				imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (SharedPreferencesUtil.getIsFirstLogin(context)) {
							

							mActivityInterface.showPage(-1, null, Configs.VIEW_POSITION_LOGIN, 0, null, null);
							
						}else {
							
							mActivityInterface.showPage(-1, null, Configs.VIEW_POSITION_INDEX, 0, null, null);
						}
						
						
					}
				});
				convertView.setTag(holder);
				break;
			}
		}else{
			holder = (HelpHolder)convertView.getTag();
		}
		return convertView;
	}

	public final class HelpHolder{
		public LinearLayout layout = null;
	}
}

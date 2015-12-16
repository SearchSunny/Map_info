package com.mapbar.info.collection.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mapbar.info.collection.R;
/**
 * 性别 adapter创建子帐使用
 * @author miaowei
 *
 */
public class SpinnerAdapter extends BaseAdapter {

	private Context mContext;
	private String[] mItem;
	public SpinnerAdapter(Context context,String[] items) {
	
		mContext = context;
		mItem = items;
		
	}
	@Override
	public int getCount() {
		return mItem.length;
	}

	@Override
	public Object getItem(int position) {
		return mItem[position];
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
		convertView = _LayoutInflater.inflate(R.layout.spinner_item, null);
		if (convertView != null) {
			TextView _TextView1 = (TextView) convertView.findViewById(R.id.textView1);
			_TextView1.setText(mItem[position]);
		}
		return convertView;
	}

	
	
}

package com.mapbar.info.collection.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mapbar.info.collection.R;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Bitmap> list;

	public ImageAdapter(Context c, ArrayList<Bitmap> list) {
		this.list = list;
		this.mContext = c;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoler_G viewHoler;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_detail_photo_item, null);
			viewHoler = new ViewHoler_G();
			viewHoler.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler_G) convertView.getTag();
		}
		viewHoler.img.setImageBitmap(list.get(position));

		return convertView;
	}
}

class ViewHoler_G {
	ImageView img;
}
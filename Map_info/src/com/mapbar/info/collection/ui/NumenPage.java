package com.mapbar.info.collection.ui;

import android.content.Context;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapbar.info.collection.ActivityInterface;
import com.mapbar.info.collection.Configs;
import com.mapbar.info.collection.R;
import com.mapbar.info.collection.util.LogPrint;
import com.mapbar.info.collection.widget.NumenExpandAdatpger;
/**
 * 热点答疑页
 * @author miaowei
 *
 */
public class NumenPage extends BasePage {

	private Context mContext;
	private View view;
	private ActivityInterface mActivityInterface;
	private NumenExpandAdatpger adatpger;
	private ExpandableListView expandListView;

	private ImageButton btn_back;
	private TextView textView;

	/**
	 * 热点答疑页
	 * @param context
	 * @param view
	 * @param aif
	 */
	public NumenPage(Context context, View view, ActivityInterface aif) {
		mContext = context;
		mActivityInterface = aif;
		textView = (TextView) view.findViewById(R.id.title_text);
		expandListView = (ExpandableListView) view.findViewById(R.id.numen_expand_listview);
		btn_back = (ImageButton) view.findViewById(R.id.btn_home);

		String[] request = context.getResources().getStringArray(R.array.new_introduce_request);
		String[] quesition = context.getResources().getStringArray(R.array.new_introduce_question);
		adatpger = new NumenExpandAdatpger(mContext, request, quesition);
		expandListView.setAdapter(adatpger);
		textView.setText("热点答疑");
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();

			}
		});

	}

	@Override
	public void onAttachedToWindow(int flag, int position) {
		// TODO Auto-generated method stub
		super.onAttachedToWindow(flag, position);
	}

	@Override
	public void onDetachedFromWindow(int flag) {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow(flag);
	}

	@Override
	public int getMyViewPosition() {
		// TODO Auto-generated method stub
		return Configs.VIEW_POSITION_NUMEN;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, null, null);
		return true;
	}

	@Override
	public void goBack() {
		mActivityInterface.showPrevious(Configs.VIEW_FLAG_NONE, this, Configs.VIEW_POSITION_MORE, null, null);
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
        LogPrint.Print("gps", "NumenPage--onLocationChanged---location="+location.getProvider());
		
		if (Configs.LOCATIONTYPE.equals(location.getProvider())) {
			
			CustomCameraActivity.mlocation = location;
		}
	}
}

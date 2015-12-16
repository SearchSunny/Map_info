package com.mapbar.info.collection;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class TitleBar implements OnClickListener {
	private ActivityInterface mActivityInterface;
	private LayoutInterface mLayoutInterface;

	private int mFromViewFlag = -1;

	private ImageButton btn_back;
	private TextView text;
	private Context context;
	private LinearLayout layout;
	private View view;

	public TitleBar(Context context, View view, LayoutInterface lif, ActivityInterface aif) {
		mActivityInterface = aif;
		mLayoutInterface = lif;

		this.context = context;

		btn_back = (ImageButton) view.findViewById(R.id.btn_home);
		text = (TextView) view.findViewById(R.id.title_text);
		layout=(LinearLayout)view.findViewById(R.id.titlebar);
		this.view=view;
		setTitle();
		  /* <TextView
           android:id="@+id/title_text"
           style="@style/textAppearanceXXXLarge"
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           android:layout_centerVertical="true"
           android:layout_toRightOf="@+id/btn_home"
           android:gravity="center"
           android:singleLine="true"
           android:text="做任务"
           android:textColor="#FFF"
           android:textStyle="bold" />*/
	}

	public void setFromViewFlag(int flag) {
		if (mFromViewFlag != flag) {
			mFromViewFlag = flag;
			setTitle();
		}
	}

	public void setFromViewFlag(int flag, int position) {
		mFromViewFlag = flag;
		setTitle();
	}

	private void setTitle() {
		
		switch (mLayoutInterface.getMyViewPosition())
		{
			case Configs.VIEW_POSITION_INDEX:
				setRightBackVisible(View.GONE);
				text.setText(context.getResources().getString(R.string.the_do_task));
			break;
			case Configs.VIEW_POSITION_TASK_COL_POINT:
				setRightBackVisible(View.VISIBLE);
				text.setText(context.getResources().getString(R.string.the_info_point));
			break;
			case Configs.VIEW_POSITION_USER_INFO:
				setRightBackVisible(View.GONE);
				text.setText(context.getResources().getString(R.string.user_info));
			break;
			case Configs.VIEW_CREATE_USER:
				setRightBackVisible(View.VISIBLE);
				text.setText(context.getResources().getString(R.string.create_user));
				break;
			case Configs.VIEW_POSITION_MORE:
				setRightBackVisible(View.GONE);
				text.setText(context.getResources().getString(R.string.the_more));
			break;
			case Configs.VIEW_POSITION_CAMERA:
				setRightBackVisible(View.VISIBLE);
			break;
			case Configs.VIEW_POSITION_MY_TASK:
				setRightBackVisible(View.VISIBLE);
				text.setText(context.getResources().getString(R.string.the_my_task));
			break;
			case Configs.VIEW_POSITION_ALTER_PASSWORD:
				setRightBackVisible(View.GONE);
				text.setText("修改密码");
			break;
			case Configs.VIEW_POSITION_ABOUT:
				setRightBackVisible(View.VISIBLE);
				text.setText("关于");
			break;
			case Configs.VIEW_POSITION_DETAIL:
				setRightBackVisible(View.VISIBLE);
			break;
			case Configs.VIEW_POSITION_OLD_DETAIL:
				setRightBackVisible(View.VISIBLE);
			break;
			case Configs.VIEW_POSITION_OLD_ITEM_DETAIL:
				setRightBackVisible(View.VISIBLE);
			break;
			case Configs.VIEW_POSITION_NUMEN:
				setRightBackVisible(View.VISIBLE);
				text.setText("热点答疑");
			break;

			default:
			break;
		}

//		int lw = view.getMeasuredWidth();
//		int bw = btn_back.getMeasuredWidth();
//
//		int marginLeft = lw / 2 - bw;
//
//		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT);
//		layoutParams.leftMargin = marginLeft;
//		layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.btn_home);
//		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,1);
//		text.setLayoutParams(layoutParams);
	}

	private void setRightBackVisible(int visible) {
		btn_back.setVisibility(visible);
	}

	@Override
	public void onClick(View v) {
	}

}

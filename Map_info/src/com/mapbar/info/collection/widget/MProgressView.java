package com.mapbar.info.collection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class MProgressView extends ImageView {
	public MProgressView(Context context) {
		this(context, null, 0x101008a);
	}

	public MProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0x101008a);
	}

	public MProgressView(Context context, String imgUrl) {
		this(context, null, 0x101008a);
	}

	public MProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setWillNotDraw(false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (this.getVisibility() == View.INVISIBLE || this.getVisibility() == View.GONE)
			return;
		Drawable d = this.getDrawable();
		if (d == null)
			return;
		d = boundCenter(d);
		canvas.save();
		canvas.translate(this.getPaddingLeft() + d.getIntrinsicWidth() / 2, this.getPaddingTop() + this.getHeight() / 2);
		canvas.rotate(mRotate);
		d.draw(canvas);
		canvas.restore();
	}

	protected static Drawable boundCenter(Drawable balloon) {
		int width = balloon.getIntrinsicWidth();
		int w2 = width / 2;
		int height = balloon.getIntrinsicHeight();
		int h2 = height / 2;
		balloon.setBounds(-w2, -h2, width - w2, height - h2);
		return balloon;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	public void stop() {
		mRotate = 0;
		mActive = false;
		postInvalidate();
	}

	public void start() {
		runForRotate();
	}

	private float mRotate;
	private boolean mActive;

	private void runForRotate() {
		if (mActive)
			return;
		mActive = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mActive) {
					try {
						mRotate += 15;
						if (!mActive)
							mRotate = 0;
						Rect outRect = new Rect();
						MProgressView.this.getHitRect(outRect);
						if (getParent() != null) {
							if (getParent().getParent() != null) {
								// ((View)(getParent().getParent())).postInvalidate();
								((View) (getParent().getParent())).postInvalidate(outRect.left, outRect.top,
										outRect.right, outRect.bottom);
							} else {
								// ((View)(getParent())).postInvalidate();
								((View) (getParent())).postInvalidate(outRect.left, outRect.top, outRect.right,
										outRect.bottom);
							}
							// postInvalidate();
						}
						// else
						// {
						postInvalidate(outRect.left, outRect.top, outRect.right, outRect.bottom);
						// }
						Thread.sleep(50);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
}

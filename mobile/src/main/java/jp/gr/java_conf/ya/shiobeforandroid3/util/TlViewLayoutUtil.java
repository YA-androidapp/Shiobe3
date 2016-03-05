package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import jp.gr.java_conf.ya.shiobeforandroid3.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TlViewLayoutUtil {

	private boolean pref_enable_singleline;
	public static int ID_LAYOUT = 999999;
	public static final int ID_LAYOUT1 = ID_LAYOUT--;
	public static final int ID_IMAGEVIEW = ID_LAYOUT--;
	public static final int ID_IMAGEVIEWBACKGROUND = ID_LAYOUT--;
	public static final int ID_TEXTVIEW = ID_LAYOUT--;
	public static final int ID_BUTTON1 = ID_LAYOUT--;
	public static final int ID_BUTTON2 = ID_LAYOUT--;
	public static final int ID_BUTTON3 = ID_LAYOUT--;
	public static final int ID_BUTTON4 = ID_LAYOUT--;
	public static final int ID_BUTTON5 = ID_LAYOUT--;
	public static final int ID_BUTTON6 = ID_LAYOUT--;

	private int pref_header_height = -1, pref_tl_iconsize = 112;
	private float pref_tl_fontsize = 14;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	//	private static final int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	private static final int RWC = RelativeLayout.LayoutParams.WRAP_CONTENT;
	private static final int RMP = RelativeLayout.LayoutParams.MATCH_PARENT;

	private LinearLayout.LayoutParams layoutParamsWeight1 = new LinearLayout.LayoutParams(WC, WC);

	private String pref_header_bgcolor = "", pref_tl_bgcolor_buttons = "", pref_tl_fontcolor_buttons = "";

	public TlViewLayoutUtil(boolean pref_enable_singleline, int pref_header_height, float pref_tl_fontsize, int pref_tl_iconsize, String pref_header_bgcolor, String pref_tl_bgcolor_buttons,
			String pref_tl_fontcolor_buttons) {
		layoutParamsWeight1.weight = 1.0f;

		this.pref_enable_singleline = pref_enable_singleline;
		this.pref_header_height = pref_header_height;
		this.pref_tl_fontsize = pref_tl_fontsize;
		this.pref_tl_iconsize = pref_tl_iconsize;
		this.pref_header_bgcolor = pref_header_bgcolor;
		this.pref_tl_bgcolor_buttons = pref_tl_bgcolor_buttons;
		this.pref_tl_fontcolor_buttons = pref_tl_fontcolor_buttons;
	}

	public LinearLayout getTlViewLayout1(final Context context) {
		return getTlViewLayout1(context, true);
	}

	public LinearLayout getTlViewLayout1(final Context context, final boolean showButton) {
		final DisplayMetrics metrics = new DisplayMetrics();
		( (Activity) context ).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final float dpi = metrics.density;
		final int padding = (int) ( 1 * dpi );

		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		final ImageView imageViewTwtr = new ImageView(context);
		imageViewTwtr.setImageResource(R.drawable.bird_gray_16);
		layout.addView(imageViewTwtr);

		final LinearLayout layout1 = new LinearLayout(context);
		layout1.setId(ID_LAYOUT1);
		layout1.setGravity(Gravity.TOP);
		try {
			layout1.setBackgroundColor(Color.parseColor(pref_header_bgcolor));
		} catch (IllegalArgumentException e1) {
		}
		layout1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout1.setPadding(padding, padding, padding, padding);

		final ImageView imageView = new ImageView(context);
		imageView.setId(ID_IMAGEVIEW);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(pref_tl_iconsize, pref_tl_iconsize));
		layout1.addView(imageView);

		final RelativeLayout relativeLayout = new RelativeLayout(context);
		relativeLayout.setGravity(Gravity.TOP);

		final ImageView imageViewBackground = new ImageView(context);
		imageViewBackground.setId(ID_IMAGEVIEWBACKGROUND);
		imageViewBackground.setLayoutParams(new RelativeLayout.LayoutParams(RMP, RWC));
		imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

		final TextView textView = new TextView(context);
		textView.setId(ID_TEXTVIEW);
		final MovementMethod movementmethod = LinkMovementMethod.getInstance();
		textView.setMovementMethod(movementmethod);
		try {
			textView.setBackgroundColor(Color.parseColor(pref_header_bgcolor));
		} catch (final IllegalArgumentException e1) {
		}
		textView.setLayoutParams(new RelativeLayout.LayoutParams(RWC, RWC));

		textView.setTextSize(pref_tl_fontsize * dpi);
		textView.setPadding(padding, 0, padding, 0);
		if (pref_enable_singleline) {
			textView.setMaxLines(2);
		} else {
			if (pref_header_height > -1) {
				textView.setMaxLines(pref_header_height);
			}
		}
		final RelativeLayout.LayoutParams RLP = new RelativeLayout.LayoutParams(RMP, RWC);

		imageViewBackground.setLayoutParams(RLP);
		relativeLayout.addView(imageViewBackground);

		textView.setLayoutParams(RLP);
		relativeLayout.addView(textView);

		layout1.addView(relativeLayout);

		layout.addView(layout1);

		if (showButton) {
			final LinearLayout layout_buttons1 = new LinearLayout(context);
			layout_buttons1.setOrientation(LinearLayout.HORIZONTAL);

			final Button button1 = new Button(context);
			final Button button3 = new Button(context);
			final Button button5 = new Button(context);

			if (pref_tl_bgcolor_buttons.equals("") == false) {
				try {
					final int tl_bgcolor_buttons = Color.parseColor(pref_tl_bgcolor_buttons);
					layout.setBackgroundColor(tl_bgcolor_buttons);
					button1.setBackgroundColor(tl_bgcolor_buttons);
					button3.setBackgroundColor(tl_bgcolor_buttons);
					button5.setBackgroundColor(tl_bgcolor_buttons);
				} catch (final IllegalArgumentException e) {
				}
			}

			if (pref_tl_fontcolor_buttons.equals("") == false) {
				try {
					final int tl_fontcolor_buttons = Color.parseColor(pref_tl_fontcolor_buttons);
					button1.setTextColor(tl_fontcolor_buttons);
					button3.setTextColor(tl_fontcolor_buttons);
					button5.setTextColor(tl_fontcolor_buttons);
				} catch (final IllegalArgumentException e) {
				}
			}

			button1.setId(ID_BUTTON1);
			button1.setTag(ID_BUTTON1);
			button3.setId(ID_BUTTON3);
			button1.setTag(ID_BUTTON3);
			button5.setId(ID_BUTTON5);
			button1.setTag(ID_BUTTON5);

			button1.setText(context.getString(R.string.zenkaku_arrow_up));
			button3.setText(context.getString(R.string.zenkaku_arrow2_down));
			button5.setText(context.getString(R.string.unread));

			button1.setTextSize(8.0f);
			button3.setTextSize(8.0f);
			button5.setTextSize(8.0f);

			layout_buttons1.addView(button1, layoutParamsWeight1);
			layout_buttons1.addView(button3, layoutParamsWeight1);
			layout_buttons1.addView(button5, layoutParamsWeight1);

			layout.addView(layout_buttons1);
		}
		return layout;
	}

	public LinearLayout getTlViewLayout2(final Context context) {
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		final LinearLayout layout_buttons2 = new LinearLayout(context);
		layout_buttons2.setOrientation(LinearLayout.HORIZONTAL);
		layout_buttons2.setGravity(Gravity.TOP);

		final Button button2 = new Button(context);
		final Button button4 = new Button(context);
		final Button button6 = new Button(context);

		if (pref_tl_bgcolor_buttons.equals("") == false) {
			try {
				final int tl_bgcolor_buttons = Color.parseColor(pref_tl_bgcolor_buttons);
				layout.setBackgroundColor(tl_bgcolor_buttons);
				button2.setBackgroundColor(tl_bgcolor_buttons);
				button4.setBackgroundColor(tl_bgcolor_buttons);
				button6.setBackgroundColor(tl_bgcolor_buttons);
			} catch (IllegalArgumentException e) {
			}
		}

		if (pref_tl_fontcolor_buttons.equals("") == false) {
			try {
				final int tl_fontcolor_buttons = Color.parseColor(pref_tl_fontcolor_buttons);
				button2.setTextColor(tl_fontcolor_buttons);
				button4.setTextColor(tl_fontcolor_buttons);
				button6.setTextColor(tl_fontcolor_buttons);
			} catch (IllegalArgumentException e) {
			}
		}

		button2.setId(ID_BUTTON2);
		button2.setTag(ID_BUTTON2);
		button4.setId(ID_BUTTON4);
		button4.setTag(ID_BUTTON4);
		button6.setId(ID_BUTTON6);
		button6.setTag(ID_BUTTON6);

		button2.setText(context.getString(R.string.zenkaku_arrow_down));
		button4.setText(context.getString(R.string.zenkaku_arrow2_up));
		button6.setText(context.getString(R.string.unread));

		button2.setTextSize(8.0f);
		button4.setTextSize(8.0f);
		button6.setTextSize(8.0f);

		layout_buttons2.addView(button2, layoutParamsWeight1);
		layout_buttons2.addView(button4, layoutParamsWeight1);
		layout_buttons2.addView(button6, layoutParamsWeight1);

		layout.addView(layout_buttons2);

		final ImageView imageViewTwtr = new ImageView(context);
		imageViewTwtr.setImageResource(R.drawable.bird_gray_16);
		layout.addView(imageViewTwtr);

		return layout;
	}
}

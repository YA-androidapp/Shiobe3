package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jp.gr.java_conf.ya.shiobeforandroid3.util.FontUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.ScalableView;
import jp.gr.java_conf.ya.shiobeforandroid3.util.UrlUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// http://typea.info/blg/glob/2010/08/androidx06ht-desire-3.html
public final class ImageViewer extends Activity implements OnTouchListener {
	private ScalableView imageView1;
	private ProgressBar progressBar1;
	private final Matrix matrix = new Matrix();
	private final Matrix savedMatrix = new Matrix();
	private final PointF start = new PointF();
	private float oldDist, curRatio = 1f;
	private final PointF mid = new PointF();
	private SharedPreferences pref_app;
	private String[] urls = { "", "", "" };

	private static final int NONE = 0, DRAG = 1, ZOOM = 2;
	private int mode;

	private static final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private final ProgressDialog createDialog(final int id) {
		if (getString(id).equals("") == false) {
			try {
				final ProgressDialog pDialog = new ProgressDialog(this);
				try {
					dismissDialog(id);
				} catch (final Exception e) {
				}

				pDialog.setTitle(id);
				pDialog.setMessage("Loading...");

				return pDialog;
			} catch (final Exception e) {
			}
		}
		return null;
	}

	private final void exif_show(final String filePath) {
		WriteLog.write(this, "filePath: " + filePath);

		if (!isFinishing()) {
			showDialog(R.string.exif_show);
		}

		new Thread(new Runnable() {
			@Override
			public final void run() {
				final String[] exifs = getExifs(filePath);
				if (exifs != null) {
					runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							new AlertDialog.Builder(ImageViewer.this).setTitle(R.string.exif_show).setItems(exifs, new DialogInterface.OnClickListener() {
								@Override
								public final void onClick(final DialogInterface dialog, final int which) {
								}
							}).create().show();

							try {
								dismissDialog(R.string.exif_show);
							} catch (IllegalArgumentException e) {
							}
						}
					});
				} else {
					try {
						dismissDialog(R.string.exif_show);
					} catch (IllegalArgumentException e) {
					}
				}
			}
		}).start();
	}

	private final String expand_image_url(final String urlStr) {
		return urlStr.replaceAll("(https?://f[.]hatena[.]ne[.]jp/(([\\w-])[\\w-]+)/((\\d{8})\\d+))",
		// "http://img.f.hatena.ne.jp/images/fotolife/$3/$2/$5/$4_120.jpg")
		"http://img.f.hatena.ne.jp/images/fotolife/$3/$2/$5/$4.jpg").replaceAll("(https?://img[.]ly/([-_0-9a-zA-Z]+))", "https://img.ly/show/thumb/$2").replaceAll("(https?://imgur[.]com/([-_0-9a-zA-Z]+))", "https://i.imgur.com/$2.jpg").replaceAll("(https?://instagr[.]?(?:am|am[.]com)/p/([\\w-]+))/?(?:media/\\?size=(?:t|l))?/?",
		// "$1/media/?size=t")
		"$1/media/?size=l").replaceAll("(https?://moby[.]to/(\\w+))",
		// "http://moby.to/$2:small")
		"http://moby.to/$2:full").replaceAll("(https?://movapic[.]com/pic/(\\w+))",
		// "http://image.movapic.com/pic/s_$2.jpeg")
		"http://image.movapic.com/pic/t_$2.jpeg").replaceAll("(https?://(?:www[.]nicovideo[.]jp/watch/|nico[.]ms/)(?:s|n)m([0-9]+))", "http://tn-skr.smilevideo.jp/smile?i=$2").replaceAll("(https?://ow[.]ly/i/(\\w+))", "http://static.ow.ly/photos/thumb/$2.jpg").replaceAll("(https?://photozou[.]jp/photo/show/\\d+/([\\d]+))",
		// "http://photozou.jp/p/thumb/$2")
		"http://photozou.jp/p/img/$2").replaceAll("(https?://tweetphoto[.]com/\\d+|https?://plixi[.]com/p/\\d+|https?://lockerz[.]com/s/\\d+)",
		// "http://api.plixi.com/api/tpapi.svc/imagefromurl?url=$1")
		// "http://api.plixi.com/api/tpapi.svc/imagefromurl?size=thumbnail&url=$1")
		"http://api.plixi.com/api/tpapi.svc/imagefromurl?size=big&url=$1").replaceAll("(https?://tuna[.]be/t/([-_0-9a-zA-Z]+))",
		// "http://tuna.be/show/mini/$2")
		"http://tuna.be/show/thumb/$2").replaceAll("(https?://p[.]twipple[.]jp/(?:show/(?:large|thumb)/)?([-_0-9a-zA-Z]+))",
		// "http://p.twpl.jp/show/orig/$2")
		// "http://p.twipple.jp/show/thumb/$2")
		"http://p.twipple.jp/show/large/$2").replaceAll("(https?://p[.]twpl[.]jp/(?:show/(?:large|thumb)/)?([-_0-9a-zA-Z]+))",
		// "http://p.twpl.jp/show/orig/$2")
		// "http://p.twipple.jp/show/thumb/$2")
		"http://p.twipple.jp/show/large/$2").replaceAll("(https?://twitgoo[.]com/([-_0-9a-zA-Z]+))",
		// "http://twitgoo.com/$2/mini")
		"http://twitgoo.com/$2/img").replaceAll("(https?://twitpic[.]com/(?:show/(?:mini|thumb)/)?([-_0-9a-zA-Z]+))",
		// "http://twitpic.com/show/mini/$2")
		"https://twitpic.com/show/thumb/$2").replaceAll("(https?://(?:twitter.)?yfrog[.]com/([-_0-9a-zA-Z]+)(:[a-zA-Z]+)?)",
		// "https://yfrog.com/$2:small")
		// "https://yfrog.com/$2:iphone")
		"https://yfrog.com/$2:medium").replaceAll("https?://(?:www[.]youtube[.]com/watch(?:\\?|#!)v=|youtu[.]be/)([\\w_\\-]+)(?:[-_.!~*\\'()a-zA-Z0-9;\\/?:@&=+$,%#]*)",
		// "http://i.ytimg.com/vi/$1/default.jpg")
		"https://i.ytimg.com/vi/$1/hqdefault.jpg").replaceAll("<a[^>]*>(https?://via[.]me/([-_0-9a-zA-Z]+))</a[^>]*>",
		// "<a href=\"http://mgng.aws.af.cm/misc/viame/?r=thumb&id=$2\"><img src=\"http://mgng.aws.af.cm/misc/viame/?r=thumb&id=$2\" /></a><a href=\"$1\">via.me</a>")
		"<a href=\"http://mgng.aws.af.cm/misc/viame/?r=media&id=$2\"><img src=\"http://mgng.aws.af.cm/misc/viame/?r=media&id=$2\" /></a><a href=\"$1\">via.me</a>").replaceAll("((?:https?|ftp)://[-_.!*\"'()a-zA-Z0-9;/?:\\\\@&=+$,%#]+[.](gif|jpeg?|png|tiff?))", "$1");
	}

	private final String[] getExifs(final String filePath) {
		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(filePath);
		} catch (IOException e) {
			WriteLog.write(this, e);
		}

		if (exifInterface != null) {
			final String[] tags =
					{ ExifInterface.TAG_DATETIME, ExifInterface.TAG_FLASH, ExifInterface.TAG_FOCAL_LENGTH, ExifInterface.TAG_GPS_ALTITUDE, ExifInterface.TAG_GPS_DATESTAMP,
							ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_PROCESSING_METHOD, ExifInterface.TAG_GPS_TIMESTAMP, ExifInterface.TAG_IMAGE_LENGTH,
							ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_ORIENTATION, ExifInterface.TAG_WHITE_BALANCE
					//, ExifInterface.TAG_APERTURE
					//, ExifInterface.TAG_EXPOSURE_TIME
					//, ExifInterface.TAG_ISO
					};

			final ArrayList<String> exifs = new ArrayList<String>(15); // tags.length

			for (String tag : tags) {
				final String exif = getExifString(exifInterface, tag);
				if (exif.equals("") == false) {
					exifs.add(exif);
				}
			}

			return exifs.toArray(new String[exifs.size()]);
		}
		return null;
	}

	private final String getExifString(final ExifInterface exifInterface, final String tag) {
		final String attr = exifInterface.getAttribute(tag);
		if (attr != null) {
			if (attr.equals("") == false) {
				return tag + ": " + attr;
			}
		}

		return "";
	}

	//	private final float getFitScale(final int dest_width, final int dest_height, final int src_width, final int src_height) {
	//		float ret = 0;
	//
	//		if (dest_width < dest_height) {
	//			//縦が長い
	//			if (src_width < src_height) {
	//				//縦が長い
	//				ret = (float) dest_height / (float) src_height;
	//
	//				if (( src_width * ret ) > dest_width) {
	//					//縦に合わせると横がはみ出る
	//					ret = (float) dest_width / (float) src_width;
	//				}
	//			} else {
	//				//横が長い
	//				ret = (float) dest_width / (float) src_width;
	//			}
	//		} else {
	//			//横が長い
	//			if (src_width < src_height) {
	//				//縦が長い
	//				ret = (float) dest_height / (float) src_height;
	//			} else {
	//				//横が長い
	//				ret = (float) dest_width / (float) src_width;
	//
	//				if (( src_height * ret ) > dest_height) {
	//					//横に合わせると縦がはみ出る
	//					ret = (float) dest_height / (float) src_height;
	//				}
	//			}
	//		}
	//
	//		return ret;
	//	}

	private final Matrix getMatrix(final String imagePathString) {
		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(imagePathString);
		} catch (IOException e) {
		}
		final Matrix matrix = new Matrix();
		if (null != exifInterface) {
			final int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (orientation) {
			case ExifInterface.ORIENTATION_UNDEFINED:
				break;
			case ExifInterface.ORIENTATION_NORMAL:
				break;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.postScale(-1f, 1f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.postRotate(180f);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.postScale(1f, -1f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.postRotate(90f);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.postRotate(-90f);
				matrix.postScale(1f, -1f);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.postRotate(90f);
				matrix.postScale(1f, -1f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.postRotate(-90f);
				break;
			}
		}
		return matrix;
	}

	private final int getPrefColor(String key, String defaultValueString, int defaultValue) {
		try {
			return Color.parseColor(pref_app.getString(key, defaultValueString));
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	private final void image_set(final String path) {
		if (path.equals("")) {
			WriteLog.write(this, "image_set(final String imagePathString) (imagePathString.equals(\"\"))");
			return;
		}

		File imageFile = null;
		imageFile = new File(path);
		WriteLog.write(ImageViewer.this, "imageFile: " + imageFile);
		if (!imageFile.exists()) {
			return;
		}

		//		if (imageFile != null) {
		//			WriteLog.write(ImageViewer.this, "image_set(final String path " + path + ") imagePath: "
		//					+ imagePath);
		//			try {
		//				imagePath = imageFile.getAbsoluteFile();
		//			} catch (final Exception e) {
		//				WriteLog.write(UpdateTweet.this, e);
		//			}
		//		}

		if (path.equals("") == false) {
			new Thread(new Runnable() {
				@Override
				public final void run() {
					image_set_part(path);
				}
			}).start();
		}
	}

	private final void image_set_part(final String imagePathString) {
		try {
			final Bitmap img = BitmapFactory.decodeFile(imagePathString);
			if (img != null) {
				final Bitmap img2 = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), getMatrix(imagePathString), true);
				if (img2 != null) {
					runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							imageView1.setImageBitmap(img2);
							imageView1.setVisibility(View.VISIBLE);
							progressBar1.setVisibility(View.GONE);
						}
					});
				}
			}
		} catch (final OutOfMemoryError e) {
			WriteLog.write(ImageViewer.this, e);
			try {
				System.gc();
				System.runFinalization();
				System.gc();
			} catch (Exception e1) {
				WriteLog.write(ImageViewer.this, e1);
			}
			toast(getString(R.string.too_large_picture));

			try {
				final Bitmap img = BitmapFactory.decodeFile(imagePathString);
				final Bitmap img2 = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), getMatrix(imagePathString), true);
				if (img2 != null) {
					runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							imageView1.setImageBitmap(img2);
							imageView1.setVisibility(View.VISIBLE);
							progressBar1.setVisibility(View.GONE);
							WriteLog.write(ImageViewer.this, "setImageBitmap(img)2");
						}
					});
				}
			} catch (final OutOfMemoryError e2) {
				try {
					final Boolean pref_pictureappended_oome_argb4444 = pref_app.getBoolean("pref_pictureappended_oome_argb4444", false);
					int pref_pictureappended_oome_insamplesize = ListAdapter.getPrefInt(this, "pref_pictureappended_oome_insamplesize", "2");

					final BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
					if (pref_pictureappended_oome_argb4444) {
						// ARGBでそれぞれ0～127段階の色を使用（メモリ対策）
						bmfOptions.inPreferredConfig = Config.ARGB_4444;
					}

					bmfOptions.inSampleSize = pref_pictureappended_oome_insamplesize;
					// システムメモリ上に再利用性の無いオブジェクトがある場合に勝手に解放（メモリ対策）
					bmfOptions.inPurgeable = true;
					// 現在の表示メトリクスの取得
					final WindowManager windowManager1 = getWindowManager();
					final Display display1 = windowManager1.getDefaultDisplay();
					final DisplayMetrics displayMetrics1 = new DisplayMetrics();
					display1.getMetrics(displayMetrics1);
					// ビットマップのサイズを現在の表示メトリクスに合わせる（メモリ対策）
					bmfOptions.inDensity = displayMetrics1.densityDpi;
					final Bitmap img = BitmapFactory.decodeFile(imagePathString, bmfOptions);
					if (img != null) {
						runOnUiThread(new Runnable() {
							@Override
							public final void run() {
								imageView1.setImageBitmap(img);
								imageView1.setVisibility(View.VISIBLE);
								progressBar1.setVisibility(View.GONE);
								WriteLog.write(ImageViewer.this, "setImageBitmap(img)3");
							}
						});
					}
				} catch (Exception e3) {
					WriteLog.write(ImageViewer.this, e3);
				}
			} catch (Exception e2) {
				WriteLog.write(ImageViewer.this, e2);
			}
		} catch (final Exception e) {
			WriteLog.write(ImageViewer.this, e);
		}
	}

	// 2点間の中間点を計算
	private final void midPoint(final PointF point, final MotionEvent event) {
		final float x = event.getX(0) + event.getX(1);
		final float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageviewer);

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		imageView1 = (ScalableView) findViewById(R.id.imgView1);
		imageView1.setVisibility(View.GONE);

		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.VISIBLE);

		final TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setBackgroundColor(getPrefColor("pref_tl_bgcolor", "#000000", Color.BLACK));
		textView1.setTextColor(getPrefColor("pref_tl_fontcolor_statustext", "#ffffff", Color.WHITE));
		textView1.setSingleLine();
		final MovementMethod movementmethod = LinkMovementMethod.getInstance();
		textView1.setMovementMethod(movementmethod);
		( new FontUtil() ).setFont(textView1, this);

		new Thread(new Runnable() {
			@Override
			public final void run() {

				final Intent intent1 = getIntent();
				Uri url1;
				Uri url2;
				try {
					url1 = intent1.getData();
					WriteLog.write(ImageViewer.this, "url1: " + url1.toString());
				} catch (final Exception e) {
					url1 = null;
				}
				try {
					url2 = (Uri) intent1.getExtras().getParcelable(Intent.EXTRA_STREAM);
					WriteLog.write(ImageViewer.this, "url2: " + url2.toString());
				} catch (final Exception e) {
					url2 = null;
				}

				String url = "";
				if (url1 != null) {
					url = url1.toString();
				} else if (url2 != null) {
					url = url2.toString();
				}

				if (url.equals("") == false) {
					WriteLog.write(ImageViewer.this, "url != \"\" url: " + url);

					if (url.startsWith("file://")) {
						WriteLog.write(ImageViewer.this, "image_set(String " + url + ")");

						urls[0] = url;
						urls[1] = url;

						runOnUiThread(new Runnable() {
							@Override
							public final void run() {
								textView1.setText(urls[0]);
							}
						});
						url = url.replace("file://", "");
						try {
							image_set(url);
						} catch (final Exception e) {
							WriteLog.write(ImageViewer.this, e);
						}
					} else {
						WriteLog.write(ImageViewer.this, "(!url.startsWith(\"file://\"))");

						final int pref_enable_expand_uri_string_length = ListAdapter.getPrefInt(ImageViewer.this, "pref_enable_expand_uri_string_length", "30");
						url = ( url.length() > pref_enable_expand_uri_string_length ) ? url : ( new UrlUtil(ImageViewer.this) ).expand_uri(url);

						urls[1] = url;
						WriteLog.write(ImageViewer.this, "(!url.startsWith(\"file://\")) urls[1]: " + urls[1]);

						final String expanded_url = expand_image_url(url);

						urls[0] = expanded_url;
						WriteLog.write(ImageViewer.this, "(!url.startsWith(\"file://\")) urls[0]: " + urls[0]);

						runOnUiThread(new Runnable() {
							@Override
							public final void run() {
								textView1.setText(Html.fromHtml("<a href=\"" + expanded_url + "\">" + expanded_url + "</a>"));
								WriteLog.write(ImageViewer.this, "(!url.startsWith(\"file://\")) textView1.setText: <a href=\"" + expanded_url + "\">" + expanded_url + "</a>");
							}
						});

						url = getFilesDir() + "/" + ( new UrlUtil(ImageViewer.this) ).get_file_from_web(expanded_url);
						WriteLog.write(ImageViewer.this, "(!url.startsWith(\"file://\")) image_set(String " + url + ")");

						try {
							image_set(url);
						} catch (final Exception e) {
							WriteLog.write(ImageViewer.this, e);
						}
					}
				}
			}
		}).start();
	}

	@Override
	protected final Dialog onCreateDialog(final int id) {
		final Dialog dialog = createDialog(id);

		if (dialog != null) {
			return dialog;
		} else {
			return super.onCreateDialog(id);
		}
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {

		final SubMenu sub1 = menu.addSubMenu(0, R.string.share_url, 0, R.string.share_url).setIcon(android.R.drawable.ic_menu_share);
		sub1.add(0, R.string.share_url, 0, R.string.share_url);
		sub1.add(0, R.string.share_url_thumbnail, 0, R.string.share_url_thumbnail);

		menu.add(0, R.string.exif_show, 0, R.string.exif_show).setIcon(android.R.drawable.ic_menu_info_details);

		menu.add(0, R.string.back, 0, R.string.back).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.string.share_url) {
			if (urls[0].equals("") == false) {
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[0]));
				startActivity(intent);
			}

		} else if (item.getItemId() == R.string.share_url_thumbnail) {
			if (urls[1].equals("") == false) {
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[1]));
				startActivity(intent);
			}

		} else if (item.getItemId() == R.string.exif_show) {
			if (urls[2].equals("") == false) {
				exif_show(urls[2]);
			}

		} else if (item.getItemId() == R.string.back) {
			finish();

		}
		return true;
	}

	@Override
	public final boolean onTouch(final View v, final MotionEvent event) {
		final ImageView view = (ImageView) v;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
			v.performClick();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			}
			break;
		}

		// ズーム
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode != DRAG) {
				final float newDist = spacing(event);
				final float scale = newDist / oldDist;
				final float tmpRatio = curRatio * scale;
				if (( 0.1f < tmpRatio ) && ( tmpRatio < 20f )) {
					curRatio = tmpRatio;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		return true;
	}

	// 2点間の距離を計算
	private final float spacing(final MotionEvent event) {
		try {
			final float x = event.getX(0) - event.getX(1);
			final float y = event.getY(0) - event.getY(1);
			return (float) Math.sqrt(( x * x ) + ( y * y ));
		} catch (final Exception e) {
			return 0.0f;
		}
	}

	private final void toast(final String text) {
		if (!isFinishing()) {
			if (currentThreadIsUiThread()) {
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						Toast.makeText(ImageViewer.this, text, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
}

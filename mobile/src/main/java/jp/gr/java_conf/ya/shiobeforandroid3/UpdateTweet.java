package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.gr.java_conf.ya.shiobeforandroid3.util.CharRefDecode;
import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.CollectionsUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.CsUncaughtExceptionHandler;
import jp.gr.java_conf.ya.shiobeforandroid3.util.FontUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.GeocodeUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HttpsClient;
import jp.gr.java_conf.ya.shiobeforandroid3.util.Morse;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.SntpClientOffset;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringValidator;
import jp.gr.java_conf.ya.shiobeforandroid3.util.UrlUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.GeoLocation;
import twitter4j.PagableResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

@SuppressWarnings("deprecation")
public final class UpdateTweet extends AppCompatActivity implements LocationListener, View.OnClickListener, View.OnFocusChangeListener, View.OnLongClickListener {

    private AutoCompleteTextView editText1;
    private AutoCompleteTextView editText3;

    private boolean mLongPressed;
    private boolean mode_fav;
    private boolean mode_pak;
    private boolean mode_rt;
    private boolean mode_tweet = true;

    private static boolean[] timeout = {true, true};

    private Button button1;
    private Button button2;

    private BroadcastReceiver broadcastReceiver;

    private final CheckNetworkUtil checkNetworkUtil = new CheckNetworkUtil(this);

    private final CollectionsUtil collectionsUtil = new CollectionsUtil();

    private Configuration conf;

    private double altitude;

    private EditText editText2;
    private EditText editText4;
    private EditText editText5;

    // private File imagePath;
    private ArrayList<File> imagePaths = new ArrayList<File>();

    private final class FileCheckFont implements FilenameFilter {
        @Override
        public final boolean accept(final File dir, final String strfilename) {
            if ((strfilename.endsWith(".ttf")) || (strfilename.endsWith(".ttc")) || (strfilename.endsWith(".otf")) || (strfilename.endsWith(".otc")) || (strfilename.endsWith(".zip"))) {
                return true;
            } else {
                return false;
            }
        }
    }

    private final class FileCheckPrefXml implements FilenameFilter {
        @Override
        public final boolean accept(final File dir, final String strfilename) {
            if (strfilename.contains(getPackageName()) && (strfilename.endsWith(".xml") || strfilename.endsWith(".xml.bin"))) {
                return true;
            } else {
                return false;
            }
        }
    }

    private float bearing;
    private float capture_thumbnail_scale;
    private float speed;

    private ImageView imageView1;

    private int capture_thumbnail_height;
    private int capture_thumbnail_retry;
    private int capture_thumbnail_webview_height;
    private int capture_thumbnail_webview_width;
    private int capture_thumbnail_width;
    private static int flagCaptureThumbnail;
    // private static final int NOTIFY_RUNNING = 0;
    private static final int NOTIFY_DOING_SEND = 1;
    private static final int NOTIFY_DONE_TWEET = 2;
    private static final int NOTIFY_TWITTER_EXCEPTION = 4;
    //	private static final int NOTIFY_DONE_ACTION = 8;
    //	private static final int NOTIFY_OPEN_UPDATETWEET = 16;
    public static final int REQUEST_PERMISSION = 1;
    private int pre_key;
    private int pref_timeout_connection;
    private int pref_timeout_so;
    private static int pref_timeout_t4j_connection = 20000;
    private static int pref_timeout_t4j_read = 120000;
    private static final int REQUEST_VOICERECOG = 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;

    private ListAdapter adapter;

    private LocationManager mLocationManager;

    private final class MyPictureListener implements PictureListener {
        @Override
        public final void onNewPicture(final WebView view, final Picture picture) {
            if (flagCaptureThumbnail == 0) {
                adapter.toast(getString(R.string.doing_save_thumbnail));
                capture_webpagethumbnail(capture_thumbnail_height, capture_thumbnail_width, capture_thumbnail_scale);
                flagCaptureThumbnail += 1;
            } else if ((flagCaptureThumbnail < capture_thumbnail_retry) && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)) {
                capture_webpagethumbnail(capture_thumbnail_height, capture_thumbnail_width, capture_thumbnail_scale);
                flagCaptureThumbnail += 1;
            } else if ((flagCaptureThumbnail == capture_thumbnail_retry) && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)) {
                adapter.toast(getString(R.string.done_save_thumbnail));
                flagCaptureThumbnail += 1;
            }
        }
    }

    private final class MyTimerTask implements Runnable {
        @Override
        public final void run() {
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    textView1.setText(simpleDateFormat.format(new Date((System.currentTimeMillis() + adapter.getNtpOffset()))));
                }
            });
        }
    }

    private ScheduledFuture<?> scheduledFuture;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S", Locale.JAPAN);

    private SharedPreferences pref_app;
    private SharedPreferences pref_twtr;

    private static String consumerKey = "";
    private static String consumerSecret = "";
    private String crpKey = "";
    private String inReplyToStatusId = "";
    private String oauthToken = "";
    private String preCapturedUri = "";
    private static String pref_pictureUploadSite = ListAdapter.default_pictureUploadSite;
    private String pref_tl_fontcolor_text_updatetweet = "#ffffff";
    private String pref_tl_fontcolor_text_updatetweet_button_tweet = "#ffffff";
    private String pref_tl_fontcolor_text_updatetweet_button_tweet_over = "#ff0000";
    private String pref_tl_fontcolor_text_updatetweet_over = "#ff0000";
    private String schedule_status_id = "";
    private static final String thumbnailFilename = "thumbnail.jpg";
    private static final String thumbnailPath = Environment.getExternalStorageDirectory().toString() + "/" + thumbnailFilename;
//    private static String twitpicKey = ListAdapter.default_twitpicKey;

    private TextView textView1;
    private TextView textView2;

    private Uri imageUri;

    private UrlUtil urlUtil;

    private WebView webView1;

    private final void button1Clicked() {
        int pref_user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));
        if (pref_user_index_size < ListAdapter.default_user_index_size) {
            pref_user_index_size = ListAdapter.default_user_index_size;
        }

        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        final int index = adapter.checkIndexFromPrefTwtr();
        final int offset = 1;
        final String[] ITEM = new String[pref_user_index_size + offset];
        final String current_name = pref_twtr.getString("screen_name_" + index, "");
        final String current_oauth_token = pref_twtr.getString("oauth_token_" + index, "");
        ITEM[0] = getString(R.string.tweet_current_account);
        String itemname;
        for (int i = 0; i < pref_user_index_size; i++) {
            itemname = pref_twtr.getString("screen_name_" + i, "");

            if (itemname.equals("")) {
                ITEM[i + offset] = "  - ";
            } else if ((itemname.equals(current_name)) && ((pref_twtr.getString("oauth_token_" + i, "")).equals(current_oauth_token))) {
                ITEM[i + offset] = "Current: @" + itemname;
            } else {
                ITEM[i + offset] = " @" + itemname;
            }
        }
        new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.changeaccount).setItems(ITEM, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                final SharedPreferences.Editor editor = pref_twtr.edit();
                if ((which >= offset) && (ITEM[which].equals("  - ") == false)) {
                    editor.putString("index", Integer.toString(which - offset));
                    editor.commit();
                    init_user(which - offset);
                    adapter.toast(getString(R.string.userinit) + " @" + adapter.checkScreennameFromIndex(adapter.checkIndexFromPrefTwtr()));
                } else if (which == 0) {
                    tweet_button();
                }
            }
        }).create().show();
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                webView1.setVisibility(View.GONE);
            }
        });
    }

    private final void capture_webpagethumbnail(final int height, final int width, final float scale) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            Thread.sleep(3000);
        } catch (final InterruptedException e) {
            WriteLog.write(this, e);
        }
        try {
            final Picture picture = webView1.capturePicture();

            final boolean pref_capture_thumbnail_fullpage = pref_app.getBoolean("pref_capture_thumbnail_fullpage", false);

            Bitmap b;
            if (pref_capture_thumbnail_fullpage) {
                try {
                    b = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    WriteLog.write(this, e);
                    System.gc();
                    System.runFinalization();
                    System.gc();
                    adapter.toast(getString(R.string.too_large_thumbnail));
                    b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                }
            } else {
                b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            final Canvas c = new Canvas(b);
            picture.draw(c);
            Bitmap b2;
            try {
                b2 = Bitmap.createScaledBitmap(b, (int) (width * scale), (int) (height * scale), true);
            } catch (OutOfMemoryError e) {
                WriteLog.write(this, e);
                System.gc();
                System.runFinalization();
                System.gc();
                adapter.toast(getString(R.string.too_large_thumbnail));
                b2 = Bitmap.createScaledBitmap(b, (int) (width * scale), (int) (height * scale), true);
            }
            final Paint paint = new Paint();
            if (b2 != null) {
                c.drawBitmap(b2, 0, 0, paint);

                WriteLog.write(this, "thumbnailPath: " + thumbnailPath);
                final FileOutputStream fos = new FileOutputStream(thumbnailPath);
                if (fos != null) {
                    b2.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }
            }

        } catch (final IllegalArgumentException e) {
            WriteLog.write(this, e);
        } catch (final FileNotFoundException e) {
            WriteLog.write(this, e);
        } catch (final IOException e) {
            WriteLog.write(this, e);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }
        try {
            OnScanCompletedListener mScanCompletedListener = new OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    final File imageFile = new File(path);
                    WriteLog.write(UpdateTweet.this, "onScanCompleted imageFile: " + imageFile);
                    if (!imageFile.exists()) {
                        WriteLog.write(UpdateTweet.this, "onScanCompleted (!imageFile.exists())");
                        return;
                    }
                    final File imagePath = imageFile.getAbsoluteFile();
                    imagePaths.add(imagePath);
                    WriteLog.write(UpdateTweet.this, "image_set(final Uri uri) imagePath: " + imagePath);

                    InputStream in;
                    try {
                        in = getContentResolver().openInputStream(uri);
                        final Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                imageView1.setImageBitmap(img);
                                imageView1.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (final FileNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final IOException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                }
            };
            String[] paths = {thumbnailPath};
            String[] mimeTypes = {"image/jpeg"};
            MediaScannerConnection.scanFile(UpdateTweet.this, paths, mimeTypes, mScanCompletedListener);

        } catch (final IllegalArgumentException e) {
            WriteLog.write(this, e);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }
    }

    private final boolean checkLocationinfoException(final EditText editText4, final EditText editText5) {
        return jp.gr.java_conf.ya.shiobeforandroid3.util.CoordsUtil.checkLocationinfoException(UpdateTweet.this, editText4.getText().toString(), editText5.getText().toString(), pref_tl_fontcolor_text_updatetweet, pref_tl_fontcolor_text_updatetweet_over, editText4, editText5);
    }

    private final boolean checkLocationinfoException(final String lat, final String lng) {
        return jp.gr.java_conf.ya.shiobeforandroid3.util.CoordsUtil.checkLocationinfoException(UpdateTweet.this, lat, lng, pref_tl_fontcolor_text_updatetweet, pref_tl_fontcolor_text_updatetweet_over, null, null);
    }

    public void checkLocationPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            init_location();
        }
        // 拒否していた場合
        else {
            requestLocationPermission();
        }
    }

    private final void clearForm() {
        // imagePaths.clear();

        imageView1.setVisibility(View.GONE);
        webView1.setVisibility(View.GONE);

        editText2.setText("");

        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean pref_enable_keep_headerfooter = pref_app.getBoolean("pref_enable_keep_headerfooter", true);
        if (!pref_enable_keep_headerfooter) {
            editText1.setText("");
            editText3.setText("");
        }
    }

    private final void clearForm(final PendingIntent pendingIntent2) {
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                adapter.notification(NOTIFY_DOING_SEND, R.drawable.ic_launcher, getString(R.string.doing_send), getString(R.string.doing_send), getString(R.string.app_name), true, false, Color.TRANSPARENT, false, pendingIntent2, true);

                clearForm();
            }
        });
    }

    private final void copy(final File srcPath, final File destPath) {
        FileChannel srcChannel = null;
        FileChannel destChannel = null;

        try {
            srcChannel = new FileInputStream(srcPath).getChannel();
            destChannel = new FileOutputStream(destPath).getChannel();

            srcChannel.transferTo(0, srcChannel.size(), destChannel);

        } catch (final IOException e) {
            WriteLog.write(this, e);

        } finally {
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e) {
                    WriteLog.write(this, e);
                }
            }
            if (destChannel != null) {
                try {
                    destChannel.close();
                } catch (final IOException e) {
                    WriteLog.write(this, e);
                }
            }
        }
    }

    private final boolean copyFile(final String srcFilePath, final String dstFilePath) {
        final File srcFile = new File(srcFilePath);
        WriteLog.write(this, "copyFile() srcFilePath: " + srcFilePath);
        final File dstFile = new File(dstFilePath);
        WriteLog.write(this, "copyFile() dstFilePath: " + dstFilePath);

        try {
            new File((dstFile.getParentFile()).getPath()).mkdirs();
        } catch (final Exception e) {
            WriteLog.write(this, "copyFile() mkdirs() Exception: " + e.toString());
        }

        InputStream input;
        OutputStream output = null;
        try {
            input = new FileInputStream(srcFile);
        } catch (FileNotFoundException e) {
            input = null;
            WriteLog.write(this, "copyFile() input FileNotFoundException: " + e.toString());
        }
        try {
            output = new FileOutputStream(dstFile);
        } catch (final FileNotFoundException e) {
            try {
                input.close();
            } catch (final IOException e1) {
            }
            input = null;
            WriteLog.write(this, "copyFile() output FileNotFoundException: " + e.toString());
            return false;
        }

        final int DEFAULT_BUFFER_SIZE = 1024 * 4;
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            input.close();
            output.flush();
            output.close();
        } catch (final IOException e) {
            WriteLog.write(this, "copyFile() output.write() IOException: " + e.toString());
            return false;
        } catch (final Exception e) {
            WriteLog.write(this, "copyFile() output.write() Exception: " + e.toString());
            return false;
        }
        return true;
    }

    private static final boolean currentThreadIsUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private final void deleteExif() {
        for (final File imagePath : imagePaths) {
            if (imagePath != null) {
                if ((Environment.getExternalStorageState()).equals(Environment.MEDIA_MOUNTED)) {
                    //				File destPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    //						(imagePath.getName()).replace(".jpg", "").replace(".JPG", "").replace(".jpeg", "")
                    //								.replace(".JPEG", "") + "_noexif.jpg");
                    final File destPath = new File((imagePath.getPath()).replace(".jpg", "").replace(".JPG", "").replace(".jpeg", "").replace(".JPEG", "") + "_noexif.jpg");

                    WriteLog.write(UpdateTweet.this, "deleteExif() imagePath: " + imagePath);
                    WriteLog.write(UpdateTweet.this, "deleteExif() destPath: " + destPath);

                    copy(imagePath, destPath);

                    ExifInterface exifInterface = null;
                    try {
                        exifInterface = new ExifInterface(destPath.getPath());
                    } catch (IOException e) {
                    }

                    WriteLog.write(UpdateTweet.this, "deleteExif() new ExifInterface(destPathStr)");
                    final String tagImagelength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                    WriteLog.write(UpdateTweet.this, "deleteExif() tagImagelength: " + tagImagelength);
                    final String tagImagewidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    WriteLog.write(UpdateTweet.this, "deleteExif() tagImagewidth: " + tagImagewidth);
                    final String tagOrientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                    WriteLog.write(UpdateTweet.this, "deleteExif() tagOrientation: " + tagOrientation);

                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_APERTURE, "1.0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_DATETIME, "2000:01:01 0:00:00");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_FLASH, "0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "50");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, "0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, "2000:01:01");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "0/1.0/1,0/1");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "0/1.0/1,0/1");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "NETWORK");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, "0:00:00");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, tagImagelength);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, tagImagewidth);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_ISO, "50");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_MAKE, "A");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_MODEL, "A");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, tagOrientation);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.setAttribute(ExifInterface.TAG_WHITE_BALANCE, "0");
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    try {
                        exifInterface.saveAttributes();
                        adapter.toast(getString(R.string.done_delete_exif));
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }

                    try {
                        removeElement(imagePaths, imagePath);
                    } catch (Exception e) {
                    }
                    imageView1.setVisibility(View.GONE);

                    final String[] paths = {destPath.getPath()};
                    final String[] mimeTypes = null;
                    scan(paths, mimeTypes);
                }
            }
        }
    }

    private final void draft() {
        final int default_draft_index_size = ListAdapter.default_draft_index_size;
        final String[] draftMenuItem = new String[]{getString(R.string.load_draft), getString(R.string.save_draft)};
        final String[] draftMenuItem2 = new String[default_draft_index_size];
        final String[] draft = new String[default_draft_index_size];
        final String[] draft_pre = new String[default_draft_index_size];
        final String[] draft_suf = new String[default_draft_index_size];
        final String[] draft_p1 = new String[default_draft_index_size];
        final String[] draft_p2 = new String[default_draft_index_size];
        final String[] draft_rep = new String[default_draft_index_size];
        final String[] draft_img = new String[default_draft_index_size];
        for (int i = 0; i < default_draft_index_size; i++) {
            draft[i] = pref_twtr.getString("draft_" + i, "");
            draft_pre[i] = pref_twtr.getString("draft_pre_" + i, "");
            draft_suf[i] = pref_twtr.getString("draft_suf_" + i, "");
            draft_p1[i] = pref_twtr.getString("draft_p1_" + i, "");
            draft_p2[i] = pref_twtr.getString("draft_p2_" + i, "");
            draft_rep[i] = pref_twtr.getString("draft_rep_" + i, "");
            draft_img[i] = pref_twtr.getString("draft_img_" + i, "");
            draftMenuItem2[i] = draft_pre[i] + " " + draft[i] + " " + draft_suf[i] + " " + draft_p1[i] + " " + draft_p2[i] + " " + draft_rep[i] + " " + draft_img[i];
            WriteLog.write(this, "draftMenuItem2[" + i + "]: " + draftMenuItem2[i]);
        }

        new AlertDialog.Builder(this).setTitle(R.string.draft).setItems(draftMenuItem, new DialogInterface.OnClickListener() {
            public final void onClick(final DialogInterface dialog, final int which) {
                if (draftMenuItem[which].equals(getString(R.string.load_draft))) {
                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.load_draft).setItems(draftMenuItem2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            if ((editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString()).equals("")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        editText1.setText(draft_pre[which]);
                                        editText2.setText(draft[which]);
                                        editText3.setText(draft_suf[which]);
                                        editText4.setText(draft_p1[which]);
                                        editText5.setText(draft_p2[which]);
                                        inReplyToStatusId = draft_rep[which];
                                        image_set(draft_img[which]);
                                        adapter.toast(getString(R.string.done_load_draft));
                                    }
                                });
                            } else {
                                new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.overwrite).setMessage(R.string.confirm_overwrite).setPositiveButton(R.string.overwrite, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which2) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                editText1.setText(draft_pre[which]);
                                                editText2.setText(draft[which]);
                                                editText3.setText(draft_suf[which]);
                                                editText4.setText(draft_p1[which]);
                                                editText5.setText(draft_p2[which]);
                                                inReplyToStatusId = draft_rep[which];
                                                image_set(draft_img[which]);
                                                adapter.toast(getString(R.string.done_load_draft));
                                            }
                                        });
                                    }
                                }).setNegativeButton(R.string.confirm_overwrite_add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which2) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                editText1.setText(editText1.getText() + draft_pre[which]);
                                                editText2.setText(editText2.getText() + draft[which]);
                                                editText3.setText(editText3.getText() + draft_suf[which]);
                                                editText4.setText(editText4.getText() + draft_p1[which]);
                                                editText5.setText(editText5.getText() + draft_p2[which]);
                                                inReplyToStatusId = draft_rep[which];
                                                image_set(draft_img[which]);
                                                adapter.toast(getString(R.string.done_load_draft));
                                            }
                                        });
                                    }
                                }).create().show();
                            }
                        }
                    }).create().show();

                } else if (draftMenuItem[which].equals(getString(R.string.save_draft))) {
                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.save_draft).setItems(draftMenuItem2, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            final SharedPreferences.Editor editor = pref_twtr.edit();
                            editor.putString("draft_pre_" + which, editText1.getText().toString());
                            editor.putString("draft_" + which, editText2.getText().toString());
                            editor.putString("draft_suf_" + which, editText3.getText().toString());
                            editor.putString("draft_p1_" + which, editText4.getText().toString());
                            editor.putString("draft_p2_" + which, editText5.getText().toString());
                            editor.putString("draft_rep_" + which, inReplyToStatusId);
                            editor.putString("draft_img_" + which, getImagePathsString());
                            editor.commit();
                            adapter.toast(getString(R.string.done_save_draft));
                        }
                    }).create().show();

                }
            }
        }).create().show();
    }

    //	private void first_run() {
    //		new AlertDialog.Builder(this)
    //				.setTitle(R.string.welcome)
    //				.setMessage(R.string.first_run_dialog_message)
    //				.setPositiveButton("Positive", new DialogInterface.OnClickListener() {
    //					public final void onClick(final DialogInterface dialog, final int which) {
    //						//
    //					}
    //				})
    //				.create().show();
    //	}

    private final void getColors(final int index) {
        final String status = pref_twtr.getString("status_" + Integer.toString(index), "");
        if (isConnected(status)) {
            pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
            try {
                final String screenName = pref_twtr.getString("screen_name_" + Integer.toString(index), "");
                final User user = adapter.getTwitter(index, false).showUser(screenName);

                final String getProfileBackgroundColor = "#" + user.getProfileBackgroundColor();
                final String getProfileLinkColor = "#" + user.getProfileLinkColor();
                //	String getProfileSidebarBorderColor = "#" + user.getProfileSidebarBorderColor();
                //	String getProfileSidebarFillColor = "#" + user.getProfileSidebarFillColor();
                final String getProfileTextColor = "#" + user.getProfileTextColor();

                setColors(getProfileBackgroundColor, getProfileBackgroundColor, "#ffffff", getProfileTextColor, getProfileLinkColor, "#77B255");

                adapter.toast(getString(R.string.done_get_colors));
            } catch (final TwitterException e) {
                adapter.twitterException(e);
                WriteLog.write(this, e);
            } catch (final Exception e) {
                WriteLog.write(this, e);
                adapter.toast(getString(R.string.exception));
            }
        }
    }

    private final void get_intent(Intent receivedIntent) {
        final Intent finalReceivedIntent = receivedIntent;
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);

        new Thread(new Runnable() {
            @Override
            @SuppressWarnings("unused")
            public final void run() {
                try {
                    if (finalReceivedIntent == null) {
                        WriteLog.write(UpdateTweet.this, "(receivedIntent == null)");
                        return;
                    }
                } catch (final Exception e) {
                    WriteLog.write(UpdateTweet.this, e);
                    return;
                }

                // try {
                WriteLog.write(UpdateTweet.this, "get_intent start");
                adapter.toast(getString(R.string.loading));

                if (Intent.ACTION_SEND_MULTIPLE.equals(finalReceivedIntent.getAction()) && finalReceivedIntent.getType() != null) {
                    if ((finalReceivedIntent.getType()).startsWith("image/")) {
                        ArrayList<Uri> imageUris = finalReceivedIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                        if (imageUris != null) {
                            for (Uri imageUri : imageUris) {
                                image_set(imageUri);
                            }
                        }
                    }
                }

                String intentDataStr = "";
                String intentExtraText = "";
                String intentExtraSubjectUtf8 = "";
                String intentStr1 = "";
                String intentStr2 = "";
                String intentStr3 = "";
                String intentStr4 = "";
                String intentStr5 = "";
                String intentStrInReplyToStatusId = "";
                String intentStrSkip = "";
                String intentStrTweetImagePathString = "";
                final String action = finalReceivedIntent.getAction();

                try {
                    if (finalReceivedIntent.getStringExtra("str1").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"str1\").equals(\"\") == false)");
                        intentStr1 = finalReceivedIntent.getStringExtra("str1");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("str2").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"str2\").equals(\"\") == false)");
                        intentStr2 = finalReceivedIntent.getStringExtra("str2");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("str3").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"str3\").equals(\"\") == false)");
                        intentStr3 = finalReceivedIntent.getStringExtra("str3");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("str4").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"str4\").equals(\"\") == false)");
                        intentStr4 = finalReceivedIntent.getStringExtra("str4");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("str5").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"str5\").equals(\"\") == false)");
                        intentStr5 = finalReceivedIntent.getStringExtra("str5");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("inReplyToStatusId").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"inReplyToStatusId\").equals(\"\") == false)");
                        intentStrInReplyToStatusId = finalReceivedIntent.getStringExtra("inReplyToStatusId");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("tweetImagePathString").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"tweetImagePathString\").equals(\"\") == false)");
                        intentStrTweetImagePathString = finalReceivedIntent.getStringExtra("tweetImagePathString");
                    }
                } catch (final Exception e) {
                }
                try {
                    if (finalReceivedIntent.getStringExtra("skip").equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(finalReceivedIntent.getStringExtra(\"skip\").equals(\"\") == false)");
                        intentStrSkip = finalReceivedIntent.getStringExtra("skip");
                    }
                } catch (final Exception e) {
                }

                if (intentStrSkip.equals("1") == true) {
                    //					WriteLog.write(UpdateTweet.this, "(intentStrSkip.equals(\"1\") == true)");
                    //					WriteLog.write(UpdateTweet.this, "intentStr1: " + intentStr1);
                    //					WriteLog.write(UpdateTweet.this, "intentStr2: " + intentStr2);
                    //					WriteLog.write(UpdateTweet.this, "intentStr3: " + intentStr3);
                    //					WriteLog.write(UpdateTweet.this, "intentStr4: " + intentStr4);
                    //					WriteLog.write(UpdateTweet.this, "intentStr5: " + intentStr5);
                } else {
                    WriteLog.write(UpdateTweet.this, "(intentStrSkip.equals(\"1\") == false)");

                    if ((Intent.ACTION_VIEW.equals(action)) || (Intent.ACTION_SEND.equals(action))) {
                        WriteLog.write(UpdateTweet.this, "(Intent.ACTION_VIEW.equals(" + action + "))");

                        // getDataString
                        try {
                            intentDataStr = (finalReceivedIntent.getDataString().equals("")) ? "" : finalReceivedIntent.getDataString();
                        } catch (final Exception e) {
                            WriteLog.write(UpdateTweet.this, "hoge: " + e);
                        }

                        // getExtras
                        final Bundle extras = finalReceivedIntent.getExtras();
                        if (extras != null) {
                            if (pref_app.getBoolean("pref_enable_log", false)) {
                                if (extras.keySet() != null) {
                                    int i = 0;
                                    for (final String key : extras.keySet()) {
                                        try {
                                            WriteLog.write(UpdateTweet.this, "extra: " + Integer.toString(i++) + ": " + key + ": " + extras.get(key));
                                        } catch (final Exception e) {
                                            WriteLog.write(UpdateTweet.this, "hoge: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                        }

                        WriteLog.write(UpdateTweet.this, "Intent.EXTRA_TEXT");
                        try {
                            intentExtraText = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
                        } catch (final Exception e) {
                            intentExtraText = "";
                        }
                        if (intentExtraText.equals("") == false) {
                            WriteLog.write(UpdateTweet.this, "intentExtraText: " + intentExtraText);
                            WriteLog.write(UpdateTweet.this, "extras.getCharSequence(Intent.EXTRA_TEXT)");
                            try {
                                intentDataStr = StringUtil.getTweetString(intentDataStr, new String(intentExtraText.getBytes("UTF8"), "UTF8"));
                                WriteLog.write(UpdateTweet.this, "intentDataStr: " + intentDataStr);
                            } catch (final UnsupportedEncodingException e) {
                                WriteLog.write(UpdateTweet.this, e);
                                intentDataStr = StringUtil.getTweetString(intentDataStr, intentExtraText);
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, "hoge: " + e.getMessage());
                            }
                        }
                        intentExtraText = "";

                        WriteLog.write(UpdateTweet.this, "Intent.EXTRA_SUBJECT");
                        String intentExtraSubject = "";
                        try {
                            intentExtraSubject = (extras.getCharSequence(Intent.EXTRA_SUBJECT)).toString();
                        } catch (final Exception e) {
                            WriteLog.write(UpdateTweet.this, "hoge: " + e.getMessage());
                        }
                        if (intentExtraSubject.equals("") == false) {
                            WriteLog.write(UpdateTweet.this, "intentExtraSubject: " + intentExtraSubject);
                            try {
                                intentExtraSubjectUtf8 = new String(intentExtraSubject.getBytes("UTF8"), "UTF8");
                                WriteLog.write(UpdateTweet.this, "intentExtraSubjectUtf8: " + intentExtraSubjectUtf8);
                            } catch (final UnsupportedEncodingException e) {
                                WriteLog.write(UpdateTweet.this, e);
                            } catch (final Exception e) {
                            }
                        }
                        intentExtraSubject = "";

                        WriteLog.write(UpdateTweet.this, "android.intent.extra.STREAM");
                        String intentExtraStream = "";
                        try {
                            intentExtraStream = extras.get("android.intent.extra.STREAM").toString();
                        } catch (final Exception e) {
                            WriteLog.write(UpdateTweet.this, "hoge: " + e.getMessage());
                        }
                        if (intentExtraStream.startsWith("content://media/")) {
                            intentStrTweetImagePathString = intentExtraStream;
                        } else if (intentExtraStream.startsWith("file:///")) {
                            intentStrTweetImagePathString = intentExtraStream;
                        } else if (intentExtraStream.equals("") == false) {
                            intentDataStr = StringUtil.getTweetString(intentDataStr, intentExtraStream);
                        }
                        intentExtraStream = "";
                    } else {
                        WriteLog.write(UpdateTweet.this, "!( ACTION_SEND or ACTION_VIEW )");
                    }

                    if (intentDataStr.equals("") == false) {
                        WriteLog.write(UpdateTweet.this, "(intentDataStr.equals(\"\") == false)");
                        INTENT:
                        {
                            WriteLog.write(UpdateTweet.this, "INTENT:");
                            INTENT2:
                            {
                                WriteLog.write(UpdateTweet.this, "INTENT2:");
                                // ISBN
                                WriteLog.write(UpdateTweet.this, "INTENT2: before ISBN");
                                if (pref_app.getBoolean("pref_receivedintent_check_isbn", true)) {
                                    final Pattern p_isbn1 = Pattern.compile("^(\\d{13}|\\d{10})$", Pattern.DOTALL);
                                    final Matcher matcher_isbn1 = p_isbn1.matcher(intentDataStr);
                                    if (matcher_isbn1.find()) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: ISBN");
                                        final String group = matcher_isbn1.group(0);
                                        try {
                                            final String bookdata = googlebooksisbn(group.replace("-", ""));
                                            if (bookdata.equals("") == false) {
                                                intentStr2 = bookdata + editText2.getText().toString().replace(group, "");
                                                break INTENT;
                                            }
                                        } catch (final Exception e) {
                                            WriteLog.write(UpdateTweet.this, e);
                                        }
                                    }
                                }

                                // GoogleMap
                                WriteLog.write(UpdateTweet.this, "INTENT2: before GoogleMap");
                                if (pref_app.getBoolean("pref_receivedintent_check_googlemap", true)) {
                                    if ((intentDataStr.indexOf("geo:") > -1) || (intentDataStr.indexOf("http://g.co/maps/") > -1) || (intentDataStr.indexOf("http://goo.gl/maps/") > -1)
                                            || (intentDataStr.indexOf("http://m.google.co") > -1) || (intentDataStr.indexOf("http://maps.google.co") > -1)
                                            || (intentDataStr.indexOf("https://g.co/maps/") > -1) || (intentDataStr.indexOf("https://goo.gl/maps/") > -1)
                                            || (intentDataStr.indexOf("https://m.google.co") > -1) || (intentDataStr.indexOf("https://maps.google.co") > -1)) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap");

                                        final Matcher matcher_pattern_urlHttpHttps = (ListAdapter.pattern_urlHttpHttps).matcher(intentDataStr);
                                        while (matcher_pattern_urlHttpHttps.find()) {
                                            intentDataStr = intentDataStr.replace(matcher_pattern_urlHttpHttps.group(), urlUtil.expand_uri(matcher_pattern_urlHttpHttps.group()));
                                        }
                                        WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap uri_expanded: " + intentDataStr);

                                        if (intentDataStr.contains("geo:")) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap (intentDataStr.contains(\"geo:\"))");
                                            final Pattern pattern_geo = Pattern.compile("geo:.*?([-0-9]{1,3}[.][0-9]{1,})[^0-9]*?,[^0-9]*?([-0-9]{1,4}[.][0-9]{1,})", Pattern.DOTALL);
                                            final Matcher matcher_geo = pattern_geo.matcher(intentDataStr);
                                            if (matcher_geo.find()) {
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_geo.find()");

                                                intentStr2 = matcher_geo.group(0);

                                                final String lat = matcher_geo.group(1);
                                                final String lng = matcher_geo.group(2);

                                                final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = "https://maps.google.com/?q=" + lat + "," + lng;
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public final void run() {
                                                        editText4.setText(lat);
                                                        editText5.setText(lng);
                                                    }
                                                });
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lat: " + lat);
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lng: " + lng);

                                                adapter.toast("intent: GoogleMap URLを受け取りました");

                                                break INTENT;
                                            }
                                        } else if (intentDataStr.contains("cid=")) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap (intentDataStr.contains(\"maps.google.com/?cid=\"))");

                                            final Pattern pattern_googlemaps_cid = Pattern.compile("cid=([0-9]+)" + ListAdapter.patternStr_urlPart, Pattern.DOTALL);
                                            final Matcher matcher_googlemaps_cid = pattern_googlemaps_cid.matcher(intentDataStr);
                                            if (matcher_googlemaps_cid.find()) {
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps_cid.find()");

                                                String jsontext = "";
                                                try {
                                                    jsontext =
                                                            HttpsClient.https2data(UpdateTweet.this, "https://maps.google.com/?" + matcher_googlemaps_cid.group() + "&output=json", pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
                                                } catch (final Exception e) {
                                                }

                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps_cid.find() jsontext:" + jsontext);

                                                if (jsontext.equals("") == false) {
                                                    final Pattern pattern_googlemaps_cid2 =
                                                            Pattern.compile("cid:\"" + matcher_googlemaps_cid.group(1) + "\",latlng:\\{lat:([-0-9]{1,3}[.][0-9]{1,}),lng:([-0-9]{1,4}[.][0-9]{1,})\\}", Pattern.DOTALL);
                                                    final Matcher matcher_googlemaps_cid2 = pattern_googlemaps_cid2.matcher(jsontext);
                                                    if (matcher_googlemaps_cid2.find()) {

                                                        final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);
                                                        if (pref_receivedintent_uri_add == true) {
                                                            intentStr3 = "https://maps.google.com/?" + matcher_googlemaps_cid.group();
                                                        }

                                                        final String lat = matcher_googlemaps_cid2.group(1);
                                                        final String lng = matcher_googlemaps_cid2.group(2);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public final void run() {
                                                                editText4.setText(lat);
                                                                editText5.setText(lng);
                                                            }
                                                        });
                                                        WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps_cid2.find() lat: " + lat);
                                                        WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps_cid2.find() lng: " + lng);

                                                        adapter.toast("intent: GoogleMap URLを受け取りました");

                                                        final Pattern pattern_googlemaps_cid3 = Pattern.compile("name:\"([^\"]+)\"", Pattern.DOTALL);
                                                        final Matcher matcher_googlemaps_cid3 = pattern_googlemaps_cid3.matcher(jsontext);
                                                        if (matcher_googlemaps_cid3.find()) {
                                                            intentStr2 = matcher_googlemaps_cid3.group(1);

                                                        }

                                                        break INTENT;
                                                    }
                                                }
                                            }
                                        } else if (((intentDataStr.contains("/maps?")) || (intentDataStr.contains("/?")))
                                                && ((intentDataStr.contains("ll=")) || (intentDataStr.contains("q=")))) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap (intentDataStr.contains(\"maps.google.com/maps?\"))");
                                            final Pattern pattern_googlemaps = Pattern.compile("=([-0-9]{1,3}[.][0-9]{1,})[^0-9]*?,[^0-9]*?([-0-9]{1,4}[.][0-9]{1,})", Pattern.DOTALL);
                                            final Matcher matcher_googlemaps = pattern_googlemaps.matcher(intentDataStr);
                                            if (matcher_googlemaps.find()) {
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find()");

                                                intentStr2 = get_webpagetitle(intentDataStr);
                                                final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);

                                                final String lat = matcher_googlemaps.group(1);
                                                final String lng = matcher_googlemaps.group(2);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public final void run() {
                                                        editText4.setText(lat);
                                                        editText5.setText(lng);
                                                    }
                                                });
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lat: " + lat);
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lng: " + lng);

                                                adapter.toast("intent: GoogleMap URLを受け取りました");

                                                break INTENT;
                                            }
                                        } else if (intentDataStr.contains("/maps/preview")) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap (intentDataStr.contains(\"maps.google.com/maps/preview\"))");
                                            final Pattern pattern_googlemaps_lat = Pattern.compile("(!3d)([-0-9]{1,3}[.][0-9]{1,})", Pattern.DOTALL);
                                            final Pattern pattern_googlemaps_lng = Pattern.compile("(![24]d)([-0-9]{1,4}[.][0-9]{1,})", Pattern.DOTALL);
                                            final Matcher matcher_googlemaps_lat = pattern_googlemaps_lat.matcher(intentDataStr);
                                            final Matcher matcher_googlemaps_lng = pattern_googlemaps_lng.matcher(intentDataStr);
                                            if (matcher_googlemaps_lat.find()) {
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lat");
                                                final String lat = matcher_googlemaps_lat.group(2);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public final void run() {
                                                        editText4.setText(lat);
                                                    }
                                                });
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lat: " + lat);
                                            }
                                            if (matcher_googlemaps_lng.find()) {
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lng");
                                                final String lng = matcher_googlemaps_lng.group(2);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public final void run() {
                                                        editText5.setText(lng);
                                                    }
                                                });
                                                WriteLog.write(UpdateTweet.this, "INTENT2: GoogleMap matcher_googlemaps.find() lng: " + lng);
                                            }
                                            intentStr2 = get_webpagetitle(intentDataStr);
                                            adapter.toast("intent: GoogleMap URLを受け取りました");
                                            break INTENT;
                                        }
                                    }
                                }

                                // Mapion
                                WriteLog.write(UpdateTweet.this, "INTENT2: before Mapion");
                                if (pref_app.getBoolean("pref_receivedintent_check_mapion", true)) {
                                    if (intentDataStr.indexOf("http://www.mapion.co.jp/m/") > -1) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: Mapion");
                                        final Pattern pattern_mapionurl = Pattern.compile("http://www.mapion.co.jp/m/([.0-9a-zA-Z/]+)_([.0-9a-zA-Z/]+)_[0-9a-zA-Z/]+/?", Pattern.DOTALL);
                                        final Matcher matcher_mapionurl = pattern_mapionurl.matcher(intentDataStr);
                                        if (matcher_mapionurl.find()) {
                                            final String lat = matcher_mapionurl.group(1);
                                            final String lng = matcher_mapionurl.group(2);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    editText4.setText(lat);
                                                    editText5.setText(lng);
                                                }
                                            });
                                            WriteLog.write(UpdateTweet.this, "INTENT2: Mapion matcher_mapionurl.find() lat: " + lat);
                                            WriteLog.write(UpdateTweet.this, "INTENT2: Mapion matcher_mapionurl.find() lng: " + lng);
                                            intentStr2 = get_webpagetitle(matcher_mapionurl.group(0));

                                            final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);
                                            if (pref_receivedintent_uri_add == true) {
                                                intentStr3 = matcher_mapionurl.group();
                                            }

                                            adapter.toast("intent: Mapion URLを受け取りました");
                                            break INTENT;
                                        }
                                    }
                                }

                                // Evernote
                                WriteLog.write(UpdateTweet.this, "INTENT2: before Evernote");
                                if (pref_app.getBoolean("pref_receivedintent_check_evernote", true)) {
                                    if ((intentDataStr.startsWith("http://www.evernote.com/shard/s")) || (intentDataStr.startsWith("https://www.evernote.com/shard/s"))) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: Evernote");
                                        intentStr1 = "Evernote " + intentExtraSubjectUtf8;
                                        intentStr2 = intentDataStr.replace(System.getProperty("line.separator"), " ");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                adapter.toast("intent: Evernote URLを受け取りました");
                                            }
                                        });
                                        break INTENT;
                                    }
                                }

                                // Twitter URL
                                WriteLog.write(UpdateTweet.this, "INTENT2: before Twitter URL");
                                WriteLog.write(UpdateTweet.this, "INTENT2: before Twitter URL");
                                if (pref_app.getBoolean("pref_receivedintent_check_statusuri", true)) {
                                    if ((intentDataStr.startsWith("http://twitter.com/")) || (intentDataStr.startsWith("https://twitter.com/"))) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: Twitter URL");

                                        final Pattern pattern_twitterurl = Pattern.compile("https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+)", Pattern.DOTALL);
                                        final Matcher matcher_twitterurl = pattern_twitterurl.matcher(intentDataStr);
                                        if (matcher_twitterurl.find()) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: Status URI=>Status");
                                            final long statusId = Long.parseLong(matcher_twitterurl.group(3));

                                            try {
                                                final Status status = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).showStatus(statusId);
                                                intentStr2 = status.getText();
                                                intentStr1 = "RT @" + status.getUser().getScreenName();
                                                final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
                                                }
                                                inReplyToStatusId = Long.toString(status.getId());
                                                adapter.toast("intent: Status URLを受け取りました");
                                                break INTENT;
                                            } catch (final TwitterException e) {
                                                adapter.twitterException(e);
                                                WriteLog.write(UpdateTweet.this, e);
                                            } catch (final Exception e) {
                                                WriteLog.write(UpdateTweet.this, e);
                                                adapter.toast(getString(R.string.exception));
                                            }
                                        }

                                        if ((intentDataStr.startsWith("http://twitter.com/")) || (intentDataStr.startsWith("https://twitter.com/"))) {
                                            WriteLog.write(UpdateTweet.this, "INTENT2: Intent=>Tweet");
                                            try {
                                                URI webintentUri = new URI(intentDataStr);
                                                List<NameValuePair> list = URLEncodedUtils.parse(webintentUri, "UTF-8");

                                                for (NameValuePair nameValuePair : list) {
                                                    if (nameValuePair.getName().equals("url")) {
                                                        final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);
                                                        if (pref_receivedintent_uri_add == true) {
                                                            try {
                                                                intentStr3 += " " + URLDecoder.decode(nameValuePair.getValue(), "UTF-8");
                                                            } catch (UnsupportedEncodingException e) {
                                                                intentStr3 += " " + nameValuePair.getValue();
                                                            }
                                                        }
                                                    } else if (nameValuePair.getName().equals("via")) {
                                                        intentStr3 += " via @" + nameValuePair.getValue();
                                                    } else if (nameValuePair.getName().equals("text")) {
                                                        intentStr2 = nameValuePair.getValue();
                                                    } else if (nameValuePair.getName().equals("in_reply_to")) {
                                                        inReplyToStatusId = nameValuePair.getValue();
                                                    } else if (nameValuePair.getName().equals("hashtags")) {
                                                        intentStr3 += " #" + (nameValuePair.getValue()).replace("", " #");
                                                    }
                                                }

                                                adapter.toast("intent: Web Intent URLを受け取りました");
                                                break INTENT;
                                            } catch (URISyntaxException e) {
                                            }
                                        }
                                    }
                                }

                                // URI=>TITLE+URI
                                WriteLog.write(UpdateTweet.this, "INTENT2: before URI=>TITLE+URI");
                                if (pref_app.getBoolean("pref_receivedintent_check_uri", true)) {
                                    if (intentDataStr.startsWith(getString(R.string.httphttps))) {
                                        WriteLog.write(UpdateTweet.this, "INTENT2: URI=>TITLE+URI");
                                        intentStr1 = get_webpagetitle(intentDataStr);
                                        WriteLog.write(UpdateTweet.this, "INTENT2: URI=>TITLE+URI get_webpagetitle()");

                                        intentStr2 = urlUtil.expand_uri(intentDataStr);
                                        adapter.toast("intent: URLを受け取りました");
                                        WriteLog.write(UpdateTweet.this, "INTENT2: URI=>TITLE+URI intentStr1: " + intentStr1);
                                        WriteLog.write(UpdateTweet.this, "INTENT2: URI=>TITLE+URI intentStr2: " + intentStr2);

                                        if (pref_app.getBoolean("pref_receivedintent_uri_webview", false)) {
                                            final String finalIntentDataStr = intentDataStr;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    loadWebpage(finalIntentDataStr);
                                                }
                                            });
                                        }

                                        break INTENT;
                                    }
                                }

                                WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: pref_receivedintent_check_tweet: " + pref_app.getBoolean("pref_receivedintent_check_tweet", true));

                                if (pref_app.getBoolean("pref_receivedintent_check_tweet", true)) {
                                    final boolean pref_receivedintent_uri_add = pref_app.getBoolean("pref_receivedintent_uri_add", false);

                                    // Twitter Official
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: before Twitter Official");
                                    final String extSubj_preffix_twitter = "さんのツイート: ";
                                    final String extSubj_suffix_twitter = "https://twitter.com/";
                                    if ((intentDataStr.contains(extSubj_preffix_twitter)) && (intentDataStr.contains(extSubj_suffix_twitter))) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Twitter Official");

                                        final Pattern pattern_twitter = Pattern.compile("(https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))$", Pattern.DOTALL);
                                        final Matcher matcher_twitter = pattern_twitter.matcher(intentDataStr);
                                        if (matcher_twitter.find()) {
                                            final long statusId = Long.parseLong(matcher_twitter.group(4));

                                            try {
                                                final Status status = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).showStatus(statusId);
                                                intentStr2 = status.getText();
                                                intentStr1 = "RT @" + status.getUser().getScreenName();
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = matcher_twitter.group(1);
                                                }
                                                inReplyToStatusId = matcher_twitter.group(4);
                                                adapter.toast("intent: Twitter OfficialからIntentを受け取りました");
                                                break INTENT;
                                            } catch (final TwitterException e) {
                                                adapter.twitterException(e);
                                                WriteLog.write(UpdateTweet.this, e);
                                            } catch (final Exception e) {
                                                WriteLog.write(UpdateTweet.this, e);
                                                adapter.toast(getString(R.string.exception));
                                            }
                                        }
                                    }

                                    // Plume
                                    WriteLog.write(UpdateTweet.this, "INTENT2: before Plume");
                                    final String extSubj_suffix_plume = "Shared via Plume" + System.getProperty("line.separator") + "http://bit.ly/GetPlume";
                                    if (intentDataStr.endsWith(extSubj_suffix_plume)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Plume");
                                        final Pattern pattern_plume = Pattern.compile("\\s*?(https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))\\s*?", Pattern.DOTALL);
                                        final Matcher matcher_plume = pattern_plume.matcher(intentDataStr);
                                        if (matcher_plume.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Plume matcher_plume.find()");
                                            final String ext022 = intentDataStr.replace(extSubj_suffix_plume, "").replace(matcher_plume.group(0), "");
                                            intentStr2 = ext022.substring(0, ext022.length() - 2);
                                            intentStr1 = "RT @" + matcher_plume.group(3);
                                            if (pref_receivedintent_uri_add == true) {
                                                intentStr3 = matcher_plume.group(1);
                                            }
                                            inReplyToStatusId = matcher_plume.group(4);
                                            adapter.toast("intent: PlumeからIntentを受け取りました");
                                            break INTENT;
                                        }
                                    }

                                    // Seesmic
                                    WriteLog.write(UpdateTweet.this, "INTENT2: before Seesmic");
                                    final String extSubj_suffix_seesmic = "(Seesmicからの送信 http://www.seesmic.com)";
                                    if (intentDataStr.endsWith(extSubj_suffix_seesmic)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Seesmic");
                                        final Pattern pattern_seesmic = Pattern.compile("\\s*?(https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))\\s*?", Pattern.DOTALL);
                                        final Matcher matcher_seesmic = pattern_seesmic.matcher(intentDataStr);
                                        if (matcher_seesmic.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Seesmic matcher_seesmic.find()");
                                            final int indexof_username = intentDataStr.lastIndexOf(matcher_seesmic.group(3) + "):");
                                            final String ext022 =
                                                    intentDataStr.substring(indexof_username + (matcher_seesmic.group(3) + "): ").length()).replace(extSubj_suffix_seesmic, "").replace(matcher_seesmic.group(0), "");
                                            intentStr2 = ext022.substring(0, ext022.length() - 2);
                                            intentStr1 = "RT @" + matcher_seesmic.group(3);
                                            if (pref_receivedintent_uri_add == true) {
                                                intentStr3 = matcher_seesmic.group(1);
                                            }
                                            inReplyToStatusId = matcher_seesmic.group(4);
                                            adapter.toast("intent: SeesmicからIntentを受け取りました");
                                            break INTENT;
                                        }
                                    }

                                    // Tweetcaster
                                    WriteLog.write(UpdateTweet.this, "INTENT2: before Tweetcaster");
                                    final String extSubj_suffix_tweetcaster = getString(R.string.extsubj_suffix_tweetcaster);
                                    if (intentDataStr.endsWith(extSubj_suffix_tweetcaster)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Tweetcaster");
                                        final Pattern pattern_tweetcaster = Pattern.compile("^@([a-zA-Z0-9]+): ", Pattern.DOTALL);
                                        final Matcher matcher_tweetcaster = pattern_tweetcaster.matcher(intentDataStr);
                                        if (matcher_tweetcaster.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Tweetcaster matcher_tweetcaster.find()");
                                            intentStr2 = intentDataStr.replace(System.getProperty("line.separator") + extSubj_suffix_tweetcaster, "").replace(matcher_tweetcaster.group(0), "");
                                            intentStr1 = "RT @" + matcher_tweetcaster.group(1);
                                            // inReplyToStatusId = "";
                                            adapter.toast("intent: TweetcasterからIntentを受け取りました");
                                            break INTENT;
                                        }
                                    }

                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: after Tweetcaster");

                                    // Echofon
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: before Echofon");
                                    final String extSubj_suffix_echofon = "Get the clean and fast Twitter app at http://echofon.com";
                                    if (intentDataStr.endsWith(extSubj_suffix_echofon)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Echofon");
                                        final String ext01_2 =
                                                intentDataStr.replace(System.getProperty("line.separator") + System.getProperty("line.separator") + "--" + System.getProperty("line.separator")
                                                        + extSubj_suffix_echofon, "");
                                        final Pattern pattern_echofon2 = Pattern.compile("^.*?\\(@([0-9a-zA-Z_-]+)\\) tweeted at [0-9\\s:-]+\\n", Pattern.DOTALL);
                                        final Matcher matcher_echofon2 = pattern_echofon2.matcher(ext01_2);
                                        if (matcher_echofon2.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Echofon matcher_echofon2.find()");
                                            intentStr2 = ext01_2.replace(matcher_echofon2.group(0), "");
                                            intentStr1 = "RT @" + matcher_echofon2.group(1);
                                            adapter.toast("intent: EchofonからIntentを受け取りました");
                                            break INTENT;
                                        }
                                    }

                                    // TweetDeck
                                    WriteLog.write(UpdateTweet.this, "INTENT2: before TweetDeck");
                                    final String extSubj_suffix_tweetdeck = "Sent via TweetDeck (www.tweetdeck.com)";
                                    if (intentDataStr.endsWith(extSubj_suffix_tweetdeck)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: TweetDeck");
                                        String ext01_2 = intentDataStr.replace(extSubj_suffix_tweetdeck, "");
                                        final Pattern pattern_tweetdeck = Pattern.compile("\\s*Original Tweet: (https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))\\s*", Pattern.DOTALL);
                                        final Matcher matcher_tweetdeck = pattern_tweetdeck.matcher(intentDataStr);
                                        if (matcher_tweetdeck.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: TweetDeck matcher_tweetdeck.find()");
                                            ext01_2 = ext01_2.replace(extSubj_suffix_tweetdeck, "").replace(matcher_tweetdeck.group(0), "");
                                            final Pattern pattern_tweetdeck2 = Pattern.compile("^([0-9a-zA-Z_-]+): ", Pattern.DOTALL);
                                            final Matcher matcher_tweetdeck2 = pattern_tweetdeck2.matcher(intentDataStr);
                                            if (matcher_tweetdeck2.find()) {
                                                WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: TweetDeck matcher_tweetdeck2.find()");
                                                intentStr2 = ext01_2.replace(matcher_tweetdeck2.group(0), "");
                                                intentStr1 = "RT @" + matcher_tweetdeck2.group(1);
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = matcher_tweetdeck.group(1);
                                                }
                                                inReplyToStatusId = matcher_tweetdeck.group(4);
                                                adapter.toast("intent: TweetDeckからIntentを受け取りました");
                                                break INTENT;
                                            }
                                        }
                                    }

                                    // janetter
                                    WriteLog.write(UpdateTweet.this, "INTENT2: before Janetter");
                                    final String extSubj_suffix_janetter = "Janetter から";
                                    if (intentDataStr.contains(extSubj_suffix_janetter)) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Janetter");

                                        final Pattern pattern_janetter = Pattern.compile("https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+)\\s*?$", Pattern.DOTALL);
                                        final Matcher matcher_janetter = pattern_janetter.matcher(intentDataStr);
                                        if (matcher_janetter.find()) {
                                            final long statusId = Long.parseLong(matcher_janetter.group(3));

                                            try {
                                                final Status status = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).showStatus(statusId);
                                                intentStr2 = status.getText();
                                                intentStr1 = "RT @" + status.getUser().getScreenName();
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = matcher_janetter.group(0);
                                                }
                                                inReplyToStatusId = matcher_janetter.group(3);
                                                adapter.toast("intent: JanetterからIntentを受け取りました");
                                                break INTENT;
                                            } catch (final TwitterException e) {
                                                adapter.twitterException(e);
                                                WriteLog.write(UpdateTweet.this, e);
                                            } catch (final Exception e) {
                                                WriteLog.write(UpdateTweet.this, e);
                                                adapter.toast(getString(R.string.exception));
                                            }
                                        }
                                    }

                                    // 以下、フッタ等がないクライアント

                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_tempname");
                                    final int indexof_space = (intentDataStr.indexOf(" ") > -1) ? intentDataStr.indexOf(" ") : 0;
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: indexof_space: " + indexof_space);
                                    final int indexof_newline =
                                            (intentDataStr.indexOf(System.getProperty("line.separator") + System.getProperty("line.separator")) > -1)
                                                    ? intentDataStr.indexOf(System.getProperty("line.separator") + System.getProperty("line.separator")) : 0;
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: indexof_newline: " + indexof_newline);
                                    final String tempname = intentDataStr.substring(0, indexof_space);
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_tempname tempname:" + tempname);
                                    if (((ListAdapter.pattern_screenname).matcher(tempname)).find()) {
                                        final Pattern p_tempname = Pattern.compile("http://twitter.com/" + tempname + "/status/", Pattern.DOTALL);
                                        final Matcher matcher_tempname = p_tempname.matcher(intentDataStr);
                                        if (matcher_tempname.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_tempname.find()");

                                            final Pattern pattern_twicca = Pattern.compile("[0-9]+月[0-9]+日 [0-9]+時[0-9]+分 .+?から", Pattern.DOTALL);
                                            final Matcher matcher_twicca = pattern_twicca.matcher(intentDataStr);
                                            if (matcher_twicca.find()) {
                                                // twicca
                                                WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_twicca.find()");
                                                final int lastindexof_statusuri = intentDataStr.lastIndexOf("http://twitter.com/" + tempname + "/" + "status/");
                                                WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_twicca.find() lastindexof_statusuri: " + lastindexof_statusuri);
                                                final String ext022 = intentDataStr.substring(indexof_newline + 2, lastindexof_statusuri).replace(matcher_twicca.group(0), "");
                                                intentStr2 = ext022.substring(0, ext022.length() - 3);
                                                intentStr1 = "RT @" + tempname;
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = intentDataStr.substring(lastindexof_statusuri);
                                                }

                                                final Pattern pattern_twicca2 = Pattern.compile("\\s*https?://twitter.com/(#!/)?" + tempname + "/status/([0-9]+)", Pattern.DOTALL);
                                                final Matcher matcher_twicca2 = pattern_twicca2.matcher(intentDataStr);
                                                if (matcher_twicca2.find()) {
                                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_twicca2.find()");
                                                    inReplyToStatusId = matcher_twicca2.group(2);
                                                    adapter.toast("intent: twiccaからIntentを受け取りました");
                                                    break INTENT;
                                                }
                                            }
                                        }
                                    }
                                    final int indexof_colon = (intentDataStr.indexOf(":") > -1) ? intentDataStr.indexOf(":") : 0;
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: indexof_colon: " + indexof_colon);
                                    final String tempname_c = intentDataStr.substring(0, indexof_colon);
                                    WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: tempname_c: " + tempname_c);
                                    if (((ListAdapter.pattern_screenname).matcher(tempname_c)).find()) {
                                        final int lastindexof_statusuri =
                                                (intentDataStr.lastIndexOf("http://twitter.com/" + tempname_c + "/status/") > -1) ? intentDataStr.lastIndexOf("http://twitter.com/" + tempname_c
                                                        + "/status/") : intentDataStr.lastIndexOf("https://twitter.com/" + tempname_c + "/status/");
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: lastindexof_statusuri: " + lastindexof_statusuri);
                                        final Pattern p_tempname_c = Pattern.compile("\\[ (https?://twitter.com/" + tempname_c + "/status/([0-9]+)) \\]$", Pattern.DOTALL);
                                        final Matcher matcher_tempname_c = p_tempname_c.matcher(intentDataStr);
                                        if (matcher_tempname_c.find()) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_tempname_c.find()");

                                            if (lastindexof_statusuri > -1) {
                                                // twiyatsu
                                                WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: (lastindexof_statusuri>-1)");

                                                intentStr2 = intentDataStr.substring(indexof_colon + 1, lastindexof_statusuri - 2);
                                                intentStr1 = "RT @" + tempname_c;
                                                if (pref_receivedintent_uri_add == true) {
                                                    intentStr3 = matcher_tempname_c.group(1);
                                                }
                                                inReplyToStatusId = matcher_tempname_c.group(2);
                                                adapter.toast("intent: ツイやつからIntentを受け取りました");
                                                break INTENT;
                                            }
                                        }
                                    }
                                    final Pattern p_statusurl = Pattern.compile("\\s*?--?\\s*?(https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))\\s*?$", Pattern.DOTALL);
                                    final Matcher matcher_statusurl = p_statusurl.matcher(intentDataStr);
                                    if (matcher_statusurl.find()) {
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: matcher_statusurl.find()");

                                        // Twipple
                                        final String extSubj_twipple = getString(R.string.extsubj_twipple);
                                        if (intentExtraSubjectUtf8.indexOf(extSubj_twipple) > -1) {
                                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Twipple");
                                            intentStr2 =
                                                    intentDataStr.replace("@" + matcher_statusurl.group(3) + ":" + System.getProperty("line.separator") + System.getProperty("line.separator"), "").replace(matcher_statusurl.group(0), "");
                                            intentStr1 = "RT @" + matcher_statusurl.group(3);
                                            if (pref_receivedintent_uri_add == true) {
                                                intentStr3 = matcher_statusurl.group(1);
                                            }
                                            inReplyToStatusId = matcher_statusurl.group(4);
                                            adapter.toast("intent: TwippleからIntentを受け取りました");
                                            break INTENT;
                                        }

                                        // Hootsuite/twitcle
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Hootsuite/twitcle");
                                        intentStr2 = intentDataStr.replace(matcher_statusurl.group(0), "");
                                        intentStr1 = "RT @" + matcher_statusurl.group(3);
                                        if (pref_receivedintent_uri_add == true) {
                                            intentStr3 = matcher_statusurl.group(1);
                                        }
                                        inReplyToStatusId = matcher_statusurl.group(4);
                                        adapter.toast("intent: Hootsuite/twitcleからIntentを受け取りました");
                                        break INTENT;
                                    }

                                    final Pattern p_statusurl2 = Pattern.compile("^(.+?)\\n\\n(https?://twitter.com/(#!/)?([0-9a-zA-Z_-]+)/status/([0-9]+))\\s*?$", Pattern.DOTALL);
                                    final Matcher matcher_statusurl2 = p_statusurl2.matcher(intentDataStr);
                                    if (matcher_statusurl2.find()) {
                                        // Hamooon/TwitRocker2
                                        WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: Hamooon/TwitRocker2");

                                        final long statusId = Long.parseLong(matcher_statusurl2.group(5));

                                        try {
                                            final Status status = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).showStatus(statusId);
                                            intentStr2 = status.getText();
                                            intentStr1 = "RT @" + status.getUser().getScreenName();
                                            if (pref_receivedintent_uri_add == true) {
                                                intentStr3 = matcher_statusurl2.group(2);
                                            }
                                            inReplyToStatusId = matcher_statusurl2.group(5);
                                            adapter.toast("intent: Hamooon/TwitRocker2からIntentを受け取りました");
                                            break INTENT;
                                        } catch (final TwitterException e) {
                                            adapter.twitterException(e);
                                            WriteLog.write(UpdateTweet.this, e);
                                        } catch (final Exception e) {
                                            WriteLog.write(UpdateTweet.this, e);
                                            adapter.toast(getString(R.string.exception));
                                        }
                                    }
                                }

                                // etc

                            } // INTENT2:
                            WriteLog.write(UpdateTweet.this, "get_intent() INTENT2: etc");
                            if (intentStr2.equals("")) {
                                intentStr2 = intentDataStr;
                                adapter.toast("intent: Intentを受け取りました");
                                break INTENT;
                            }
                        } // INTENT:
                    } // if (intentDataStr.equals("") == false)
                } // if (intentStrSkip.equals("1") == false)

                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr1");
                if (intentStr1.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() intentStr1: " + intentStr1);
                    final String finalIntentStr1 = CharRefDecode.decode(intentStr1);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr1: " + finalIntentStr1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            editText1.setText(finalIntentStr1);
                        }
                    });
                    intentStr1 = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr2");
                if (intentStr2.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() intentStr2: " + intentStr2);
                    final String finalIntentStr2 = CharRefDecode.decode(intentStr2);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr2: " + finalIntentStr2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            editText2.setText(finalIntentStr2);
                        }
                    });
                    intentStr2 = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr3");
                if (intentStr3.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() intentStr3: " + intentStr3);
                    final String finalIntentStr3 = CharRefDecode.decode(intentStr3);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr3: " + finalIntentStr3);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            editText3.setText(finalIntentStr3);
                        }
                    });
                    intentStr3 = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr4");
                if (intentStr4.equals("") == false) {
                    final String finalIntentStr4 = CharRefDecode.decode(intentStr4);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr4: " + finalIntentStr4);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            editText4.setText(finalIntentStr4);
                        }
                    });
                    intentStr4 = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr5");
                if (intentStr5.equals("") == false) {
                    final String finalIntentStr5 = CharRefDecode.decode(intentStr5);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStr5: " + finalIntentStr5);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            editText5.setText(finalIntentStr5);
                        }
                    });
                    intentStr5 = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStrInReplyToStatusId");
                if (intentStrInReplyToStatusId.equals("") == false) {
                    final String finalIntentStrInReplyToStatusId = CharRefDecode.decode(intentStrInReplyToStatusId);
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStrInReplyToStatusId: " + finalIntentStrInReplyToStatusId);
                    inReplyToStatusId = finalIntentStrInReplyToStatusId;
                    intentStrInReplyToStatusId = "";
                }
                WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() finalIntentStrTweetImagePathString");
                if (intentStrTweetImagePathString.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "get_intent() runOnUiThread() intentStrTweetImagePathString: " + intentStrTweetImagePathString);

                    final String[] intentStrTweetImagePathStringArray = intentStrTweetImagePathString.split("\n");
                    for (String s : intentStrTweetImagePathStringArray) {
                        if (s.startsWith("file://")) {
                            try {
                                image_set(s);
                                WriteLog.write(UpdateTweet.this, "image_set(String " + s + ")");
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        } else {
                            Uri uri = null;
                            try {
                                uri = Uri.parse(s);
                            } catch (final Exception e) {
                                uri = null;
                                WriteLog.write(UpdateTweet.this, e);
                            }
                            if (uri != null) {
                                try {
                                    image_set(uri);
                                    WriteLog.write(UpdateTweet.this, "image_set(Uri " + s + ")");
                                } catch (final Exception e) {
                                    WriteLog.write(UpdateTweet.this, e);
                                }
                            }
                        }
                    }
                    intentStrTweetImagePathString = "";
                }

                setTextColorOnTextChanged();
            }
        }).start();
        receivedIntent = null;
    }

    private final void get_webpagethumbnail(final String uri, final boolean init) {
        if ((init == false) && (preCapturedUri.equals("") == false)) {
            capture_webpagethumbnail(capture_thumbnail_height, capture_thumbnail_width, capture_thumbnail_scale);
        } else {
            pref_app = PreferenceManager.getDefaultSharedPreferences(this);
            final boolean pref_capture_thumbnail_use_picturelistener = pref_app.getBoolean("pref_capture_thumbnail_use_picturelistener", ListAdapter.default_capture_thumbnail_use_picturelistener);
            int pref_timeout_connection = ListAdapter.getPrefInt(this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
            final int pref_timeout_connection2 = pref_timeout_connection;
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    loadWebpage(uri, pref_capture_thumbnail_use_picturelistener, pref_timeout_connection2);
                }
            });
        }
    }

    private final String get_webpagetitle(final String uri) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        WriteLog.write(this, "uri: " + uri);

        String pref_receivedintent_charset = pref_app.getString("pref_receivedintent_charset", ListAdapter.default_charset);
        String pagesource = HttpsClient.https2data(this, uri, pref_timeout_connection, pref_timeout_so, pref_receivedintent_charset);

        final boolean pref_receivedintent_charset_autodetection = pref_app.getBoolean("pref_receivedintent_charset_autodetection", true);
        if (pref_receivedintent_charset_autodetection) {
            final Pattern pattern_charset = Pattern.compile("charset=[\"']?([a-zA-Z0-9_-]+)[;>\"']", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            final Matcher matcher_charset = pattern_charset.matcher(pagesource);
            if (matcher_charset.find()) {
                pref_receivedintent_charset = matcher_charset.group(1);
                adapter.toast(getString(R.string.charset_autodetection) + ": " + pref_receivedintent_charset);

                pagesource = HttpsClient.https2data(this, uri, pref_timeout_connection, pref_timeout_so, pref_receivedintent_charset);
            } else {
                pref_receivedintent_charset = StringValidator.detectEncode(pagesource);
                adapter.toast(getString(R.string.charset_autodetection) + ": " + pref_receivedintent_charset);

                pagesource = HttpsClient.https2data(this, uri, pref_timeout_connection, pref_timeout_so, pref_receivedintent_charset);
            }
        }

        Pattern pattern_title = Pattern.compile("<title[^>]*?>\\s*?(.+?)\\s*?</title[^>]*?>", Pattern.DOTALL);
        Matcher matcher_title = pattern_title.matcher(pagesource);
        if (matcher_title.find()) {

            if ((matcher_title.group(1)).equals("") == false) {
                WriteLog.write(this, "title[1]: " + matcher_title.group(1));
                return matcher_title.group(1);
            } else {
                pattern_title = Pattern.compile("<h[1-6][^>]*?>([^<]+?)</h[1-6][^>]*?>", Pattern.DOTALL);
                matcher_title = pattern_title.matcher(pagesource);
                if (matcher_title.find()) {
                    if ((matcher_title.group(1)).equals("") == false) {
                        WriteLog.write(this, "title[2]: " + matcher_title.group(1));
                        return matcher_title.group(1);
                    }
                }
            }
        }
        WriteLog.write(this, "title: \"\"");
        return "";
    }

    private final String get_webpagetitle(final WebView webView) {
        return webView.getTitle();
    }

    private final String getImagePathsString() {
        collectionsUtil.removeDuplicate(imagePaths);

        if (imagePaths != null) {
            final StringBuilder sb = new StringBuilder();

            int i = 0;
            final int limit = imagePaths.size() - 1;
            for (File imagePath : imagePaths) {
                sb.append(imagePath.getAbsolutePath());
                if (i < limit) {
                    sb.append("\n");
                }

                i++;
            }

            return sb.toString();
        }
        return "";
    }

    private final String[] getImagePathsStringArray() {
        String[] result = new String[imagePaths.size()];
        int i = 0;
        for (File imagePath : imagePaths) {
            result[i++] = imagePath.getAbsolutePath();
        }
        return result;
    }

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

    private final long getNtpOffset() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_ntp_server = pref_app.getString("pref_ntp_server", ListAdapter.default_ntp_server);
        int pref_timeout_ntp_server = ListAdapter.getPrefInt(this, "pref_timeout_ntp_server", ListAdapter.default_timeout_ntp_server_string);

        final SntpClientOffset client = new SntpClientOffset();
        if (client.requestTime(pref_ntp_server, pref_timeout_ntp_server)) {
            final long offset = client.getClockOffset();
            adapter.setNtpOffset(offset);
            return offset;
        }
        return 0;
    }

    private final String googlebooksisbn(final String isbn) {
        boolean found = false;
        String bookdata = "";
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean pref_uri_shorten_withhttp = pref_app.getBoolean("pref_uri_shorten_withhttp", false);

        WriteLog.write(this, "isbn: " + isbn);

        String json_googlebooksisbn = "";
        try {
            json_googlebooksisbn =
                    HttpsClient.https2data(this, "http://books.google.com/books?jscmd=viewapi&bibkeys=ISBN:" + isbn, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }

        json_googlebooksisbn = json_googlebooksisbn.replaceAll("^var _GBSBookInfo = \\{\"ISBN:" + isbn + "\":", "");
        json_googlebooksisbn = json_googlebooksisbn.replaceAll("\\};$", "");
        WriteLog.write(this, "json_googlebooksisbn: " + json_googlebooksisbn);

        String info_url = "";
        String thumbnail_url = "";
        try {
            final JSONObject jSONObject1 = new JSONObject(json_googlebooksisbn);
            info_url = jSONObject1.getString("info_url");
            WriteLog.write(this, "info_url: " + info_url);
            thumbnail_url = jSONObject1.getString("thumbnail_url").replace("/zoom=\\d/isu", "zoom=1");
            WriteLog.write(this, "thumbnail_url: " + thumbnail_url);
        } catch (final JSONException e) {
            WriteLog.write(this, e);
        }

        String json_googlebooksinfo = "";
        try {
            json_googlebooksinfo = HttpsClient.https2data(this, info_url + "&output=bibtex", pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }
        WriteLog.write(this, "json_googlebooksinfo: " + json_googlebooksinfo);

        final Pattern p_title = Pattern.compile("title=\\{(.*?)\\},?");
        final Matcher m_title = p_title.matcher(json_googlebooksinfo);
        if (m_title.find()) {
            found = true;
            bookdata += getString(R.string.zenkaku_double_parenthese_start) + m_title.group(1) + getString(R.string.zenkaku_double_parenthese_end);
        }

        final Pattern p_author = Pattern.compile("author=\\{(.*?)\\},?");
        final Matcher m_author = p_author.matcher(json_googlebooksinfo);
        if (m_author.find()) {
            found = true;
            bookdata += "(著:" + m_author.group(1);

            final Pattern p_publisher = Pattern.compile("publisher=\\{(.*?)\\},?");
            final Matcher m_publisher = p_publisher.matcher(json_googlebooksinfo);
            if (m_publisher.find()) {
                bookdata += ";" + m_publisher.group(1);
            }

            final Pattern p_year = Pattern.compile("year=\\{(.*?)\\},?");
            final Matcher m_year = p_year.matcher(json_googlebooksinfo);
            if (m_year.find()) {
                bookdata += ";" + m_year.group(1);
            }

            bookdata += ")";
        }

        if (found) {
            bookdata += "を読了。";
        }

        if (info_url.equals("") == false) {
            bookdata += " info:" + urlUtil.uri_shorten(info_url, pref_uri_shorten_withhttp);
        }

        if (thumbnail_url.equals("") == false) {
            bookdata += " image:" + urlUtil.uri_shorten(thumbnail_url, pref_uri_shorten_withhttp);
        }

        if (found) {
            bookdata += " #nowreading";
        }

        WriteLog.write(this, "bookdata: " + bookdata);
        return bookdata;
    }

    private final void image_set(final String path) {
        if (path.equals("")) {
            WriteLog.write(this, "image_set(final String imagePathString) (imagePathString.equals(\"\"))");
            return;
        }

        final File imageFile = new File(path);
        if (!imageFile.exists()) {
            WriteLog.write(UpdateTweet.this, "image_set(final String path " + path + ") !imageFile.exists()");
            return;
        }

        if (imageFile != null) {
            WriteLog.write(UpdateTweet.this, "image_set(final String path " + path + ") imageFile.getAbsolutePath(): " + imageFile.getAbsolutePath());
            try {
                imagePaths.add(imageFile.getAbsoluteFile());
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }
        }

        if (path.equals("") == false) {
            new Thread(new Runnable() {
                @Override
                public final void run() {
                    image_set_part(path);
                }
            }).start();
        }
    }

    private final void image_set(final Uri uri) {
        if (uri == null) {
            WriteLog.write(this, "image_set(final Uri uri) (uri == null)");
            return;
        }

        final String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            final String imagePathString = (uri.getPath() == null) ? "" : uri.getPath();
            try {
                imagePaths.add(new File(imagePathString));
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }
            if (imagePathString.equals("") == false) {
                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        image_set_part(imagePathString);
                    }
                }).start();
            }
        } else if ("content".equals(scheme)) {

            final ContentResolver cr = getContentResolver();
            final String[] columns = {MediaStore.Images.Media.DATA};
            final Cursor c = cr.query(uri, columns, null, null, null);
            File imageFile;
            try {
                c.moveToFirst();

                final String imageFileC = c.getString(0);

                if (imageFileC.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "imageFileC: " + imageFileC);
                    imageFile = new File(c.getString(0));

                    if (imageFile != null) {
                        if (imageFile.exists()) {
                            WriteLog.write(UpdateTweet.this, "image_set(final Uri uri " + uri.toString() + ") imageFile.getAbsoluteFile(): " + imageFile.getAbsoluteFile());
                            try {
                                imagePaths.add(imageFile.getAbsoluteFile());
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        } else {
                            WriteLog.write(UpdateTweet.this, "image_set(final Uri uri " + uri.toString() + ") !imageFile.exists()");
                        }
                    }
                } else {
                    WriteLog.write(UpdateTweet.this, "image_set(final Uri uri " + uri.toString() + ") imageFileC.equals(\"\")");
                    imageFile = null;
                }
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
                imageFile = null;
            }

            final String imagePathString = (imageFile.getAbsoluteFile() == null) ? "" : imageFile.getAbsoluteFile().getAbsolutePath();

            if (imagePathString.equals("") == false) {
                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        image_set_part(imagePathString);
                    }
                }).start();
            }
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
                        }
                    });
                }
            }
        } catch (final OutOfMemoryError oom1) {
            WriteLog.write(UpdateTweet.this, oom1);
            try {
                System.gc();
                System.runFinalization();
                System.gc();
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }
            adapter.toast(getString(R.string.too_large_picture));

            try {
                final Bitmap img = BitmapFactory.decodeFile(imagePathString);
                final Bitmap img2 = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), getMatrix(imagePathString), true);
                if (img2 != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            imageView1.setImageBitmap(img2);
                            imageView1.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } catch (final OutOfMemoryError oom2) {
                pref_app = PreferenceManager.getDefaultSharedPreferences(this);
                final boolean pref_pictureappended_oome_argb4444 = pref_app.getBoolean("pref_pictureappended_oome_argb4444", false);
                final int pref_pictureappended_oome_insamplesize = ListAdapter.getPrefInt(this, "pref_pictureappended_oome_insamplesize", "2");

                try {
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
                            }
                        });
                    }
                } catch (final OutOfMemoryError oom3) {
                    try {
                        final BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
                        // ARGBでそれぞれ0～127段階の色を使用（メモリ対策）
                        bmfOptions.inPreferredConfig = Config.ARGB_4444;

                        bmfOptions.inSampleSize = pref_pictureappended_oome_insamplesize + 1;
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
                                }
                            });
                        }
                    } catch (final OutOfMemoryError oom4) {
                        WriteLog.write(UpdateTweet.this, oom4);
                        adapter.toast(getString(R.string.too_large_picture_resize));
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                } catch (final Exception e) {
                    WriteLog.write(UpdateTweet.this, e);
                }
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }
        } catch (final Exception e) {
            WriteLog.write(UpdateTweet.this, e);
        }
    }

    private final void init_location() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final int pref_locationinfo_mintime = ListAdapter.getPrefInt(this, "pref_locationinfo_mintime", "300000");
        WriteLog.write(UpdateTweet.this, "requestLocationUpdates() pref_locationinfo_mintime: " + pref_locationinfo_mintime);

        new Thread(new Runnable() {
            @Override
            public final void run() {
                try {
                    final Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
                    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (mLocationManager != null) {
                        // final String provider = manager.getBestProvider(criteria, true);
                        boolean provider_flag = false;
                        final List<String> providers = mLocationManager.getProviders(true);
                        for (final String provider : providers) {
                            if ((provider.equals(LocationManager.GPS_PROVIDER)) || (provider.equals(LocationManager.NETWORK_PROVIDER))) {
                                if (mLocationManager.isProviderEnabled(provider)) {
                                    provider_flag = true;
                                }
                            }
                            WriteLog.write(UpdateTweet.this, "requestLocationUpdates() provider: " + provider);
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        if (ContextCompat.checkSelfPermission(UpdateTweet.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            mLocationManager.requestLocationUpdates(provider, pref_locationinfo_mintime, 0, UpdateTweet.this);
                                        } else {
                                            if (Build.VERSION.SDK_INT >= 23)
                                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
                                        }
                                    }
                                });
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        }

                        if (provider_flag == false) {
                            adapter.toast(getString(R.string.open_location_source_settings));
                            try {
                                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            } catch (final ActivityNotFoundException e) {
                                WriteLog.write(UpdateTweet.this, e);
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        }
                    }
                } catch (final IllegalArgumentException e) {
                    WriteLog.write(UpdateTweet.this, e);
                } catch (final RuntimeException e) {
                    WriteLog.write(UpdateTweet.this, e);
                }

            }
        }).start();
    }

    private final void init_short_url_length(final int index) {
        int pref_short_url_length;
        try {
            pref_short_url_length = adapter.getTwitter(index, false).getAPIConfiguration().getShortURLLength();
        } catch (final TwitterException e) {
            pref_short_url_length = Integer.parseInt(ListAdapter.default_short_url_length_string);

            try {
                adapter.twitterException(e);
            } catch (Exception ex) {
                if(adapter != null)
                    adapter.toast(getString(R.string.cannot_access_twitter));
            }
        } catch (final Exception e) {
            pref_short_url_length = Integer.parseInt(ListAdapter.default_short_url_length_string);
            adapter.toast(getString(R.string.exception));
        }
        final String pref_short_url_length_string = Integer.toString(pref_short_url_length);
        if (pref_short_url_length_string.equals(pref_app.getString("pref_short_url_length", "")) == false) {
            final SharedPreferences.Editor editor1 = pref_twtr.edit();
            editor1.putString("pref_short_url_length", pref_short_url_length_string);
            editor1.commit();
            adapter.toast(getString(R.string.short_url_length) + ": " + pref_short_url_length_string);
        }
    }

    // ユーザを変更した直後に使用
    private final void init_user(int index) {
        pref_twtr = getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0
        if (adapter.isConnected(pref_twtr.getString("status_" + index, "")) == false) {
            index = adapter.checkIndexFromPrefTwtr();
        }

        final String screenName = init_user_oauth(index);
        if (screenName.equals("")) {
            WriteLog.write(this, "screenName.equals(\"\")");

            adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
            // finish();
        } else {
            WriteLog.write(this, "!screenName.equals(\"\")");
            init_user_profimage(index);
            WriteLog.write(this, "init_user_profimage(\"" + index + "\")");
            init_user_autocomplete(index);
            WriteLog.write(this, "init_user_autocomplete(\"" + index + "\")");
            init_short_url_length(index);
            WriteLog.write(this, "init_short_url_length(\"" + index + "\")");
        }
    }

    private final void init_user_autocomplete(final int index) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean pref_enable_cooperation_url = pref_app.getBoolean("pref_enable_cooperation_url", false);
        if (pref_enable_cooperation_url) {
            final String pref_cooperation_url = pref_app.getString("pref_cooperation_url", ListAdapter.default_cooperation_url);
            int pref_timeout_connection = ListAdapter.getPrefInt(this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
            int pref_timeout_so = ListAdapter.getPrefInt(this, "pref_timeout_so", ListAdapter.default_timeout_so_string);

            final String app_uri_setting = pref_cooperation_url + "autocomplete_shiobe.php?id=";
            final String screenName = adapter.checkScreennameFromIndex(index);
            final String url1 = app_uri_setting + screenName + "&mode=pre";
            final String url3 = app_uri_setting + screenName + "&mode=tag";

            String text1 = MyCrypt.decrypt(this, oauthToken, HttpsClient.https2data(this, url1, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset));
            final String text3 = MyCrypt.decrypt(this, oauthToken, HttpsClient.https2data(this, url3, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset));

            final boolean pref_enable_header_athome = pref_app.getBoolean("pref_enable_header_athome", false);
            if (pref_enable_header_athome) {
                try {
                    final String nonUsernamePaths = StringUtil.join("@", adapter.getTwitter(index, false).getAPIConfiguration().getNonUsernamePaths());
                    //					WriteLog.write(this, "nonUsernamePaths: " + nonUsernamePaths);
                    text1 = text1.concat(",").concat(nonUsernamePaths);
                    //					WriteLog.write(this, "text1: " + text1);
                } catch (final TwitterException e) {
                    try {
                        adapter.twitterException(e);
                    } catch (Exception ex) {
                        if(adapter != null)
                            adapter.toast(getString(R.string.cannot_access_twitter));
                    }
                } catch (final Exception e) {
                    adapter.toast(getString(R.string.exception));
                }
            }

            if (text1.equals("") == false) {
                final String[] PREFIXS = text1.split(",");
                final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.list_item, PREFIXS);
                editText1.setAdapter(adapter1);
                editText1.setThreshold(1);
            }

            if (text3.equals("") == false) {
                final String[] HASHTAGS = text3.split(",");
                final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.list_item, HASHTAGS);
                editText3.setAdapter(adapter3);
                editText3.setThreshold(1);
            }
        }
    }

    private final String init_user_oauth(final int index) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        pref_twtr = getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0

        //		WriteLog.write(this, "init_user_oauth(): index: " + Integer.toString(index));

        pref_timeout_t4j_connection = ListAdapter.getPrefInt(this, "pref_timeout_t4j_connection", "20000");
        //		WriteLog.write(this, "init_user_oauth(): pref_timeout_t4j_connection: " + pref_timeout_t4j_connection);
        pref_timeout_t4j_read = ListAdapter.getPrefInt(this, "pref_timeout_t4j_read", "120000");
        //		WriteLog.write(this, "init_user_oauth(): pref_timeout_t4j_read: " + pref_timeout_t4j_read);

        pref_pictureUploadSite = pref_app.getString("pref_pictureuploadsite", MediaProvider.TWITTER.toString());
        //		WriteLog.write(this, "init_user_oauth(): pref_pictureUploadSite: " + pref_pictureUploadSite);

        consumerKey = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_key_" + Integer.toString(index), ""));
        consumerSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("consumer_secret_" + Integer.toString(index), ""));
        if (consumerKey.equals("") || consumerSecret.equals("")) {
            //			WriteLog.write(this, "(consumerKey.equals(\"\") || consumerSecret.equals(\"\"))");
            consumerKey = getString(R.string.default_consumerKey);
            consumerSecret = getString(R.string.default_consumerSecret);
        }
        //		WriteLog.write(this, "init_user_oauth(): consumerKey: " + consumerKey);
        //		WriteLog.write(this, "init_user_oauth(): consumerSecret: " + consumerSecret);

        oauthToken = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_" + Integer.toString(index), ""));
        //		WriteLog.write(this, "init_user_oauth(): oauthToken: " + oauthToken);
        //		WriteLog.write(this,
        //				"init_user_oauth(): oauthToken:: " + pref_twtr.getString("oauth_token_" + Integer.toString(index), ""));
        //		if (oauthToken.equals("")) {
        //			WriteLog.write(this, "(oauthToken.equals(\"\"))");SharedPreferences.Editor editor = pref_twtr.edit();
        //			editor.putString("index", "0");editor.remove("consumer_key_" + index);editor.remove("consumer_secret_" + index);
        //			editor.remove("oauth_token_" + index);editor.remove("oauth_token_secret_" + index);editor.remove("profile_image_url_" + index);
        //			editor.remove("screen_name_" + index);editor.remove("status_" + index);editor.commit();
        //		}
        final String oauthTokenSecret = MyCrypt.decrypt(this, crpKey, pref_twtr.getString("oauth_token_secret_" + Integer.toString(index), ""));
        //		WriteLog.write(this, "init_user_oauth(): oauthTokenSecret: " + oauthTokenSecret);
        //		WriteLog.write(this, "init_user_oauth(): oauthTokenSecret:: "
        //				+ pref_twtr.getString("oauth_token_secret_" + Integer.toString(index), ""));

        ConfigurationBuilder confBuilder;
        try {
            confBuilder = new ConfigurationBuilder();
        } catch (final Exception e) {
            WriteLog.write(this, e);
            confBuilder = null;
            adapter.toast(getString(R.string.exception) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
        }
        //		WriteLog.write(this, "init_user_oauth(): confbuilder1: " + confbuilder.toString());

        Twitter twtr = null;
        if (confBuilder != null) {
//			if (pref_pictureUploadSite.equals(MediaProvider.TWITPIC.toString())) {
//				confBuilder.setMediaProvider(pref_pictureUploadSite).setMediaProviderAPIKey(twitpicKey).setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true);
//			} else {
            confBuilder.setMediaProvider(pref_pictureUploadSite).setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read);// .setUseSSL(true);
//			}
            //		WriteLog.write(this, "init_user_oauth(): confbuilder2: " + confbuilder.toString());

            try {
                conf = confBuilder.build();
                //			WriteLog.write(this, "init_user_oauth(): conf: " + conf.toString());

                twtr = new TwitterFactory(conf).getInstance();
                //			WriteLog.write(this, "init_user_oauth(): twtr: " + twtr.toString());
            } catch (final Exception e) {
                conf = null;
                twtr = null;
                WriteLog.write(this, e);
                adapter.toast(getString(R.string.exception) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
            }
        }

        String screenName = "";
        if (twtr != null) {
            try {
                screenName = twtr.getScreenName();
                //				WriteLog.write(this, "screenName: " + screenName);

                pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref_twtr.edit();
                editor.putString("screen_name_" + Integer.toString(index), screenName);
                editor.commit();
            } catch (final TwitterException e) {
                try {
                    adapter.twitterException(e);
                } catch (Exception ex) {
                    adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
                }
            } catch (final Exception e) {
                WriteLog.write(this, e);
                adapter.toast(getString(R.string.exception) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
            }

            if (screenName.equals("") == false) {
                try {
                    User user = twtr.showUser(screenName);
                    final String profile_image_url = user.getProfileImageURL().toString();
                    //					WriteLog.write(this, "profile_image_url: " + profile_image_url);

                    pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref_twtr.edit();
                    editor.putString("profile_image_url_" + Integer.toString(index), profile_image_url);
                    editor.commit();
                } catch (final TwitterException e) {
                    try {
                        adapter.twitterException(e);
                    } catch (Exception ex) {
                        adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
                    }
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                    adapter.toast(getString(R.string.exception) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
                }
            }
        }
        return screenName;
    }

    private final void init_user_profimage(final int index) {
        URL url = null;
        try {
            url = new URL(pref_twtr.getString("profile_image_url_" + index, ""));
        } catch (final MalformedURLException e) {
            WriteLog.write(this, e);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }
        if (url != null) {
            InputStream input;
            try {
                input = url.openStream();
            } catch (final IOException e) {
                input = null;
                WriteLog.write(this, e);
            }
            if (input != null) {
                Drawable drawable;
                try {
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);
                    drawable = new BitmapDrawable(bitmap);
                } catch (OutOfMemoryError e) {
                    WriteLog.write(this, e);
                    System.gc();
                    System.runFinalization();
                    System.gc();
                    adapter.toast(getString(R.string.too_large_picture));
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);
                    drawable = new BitmapDrawable(bitmap);
                } catch (final Exception e) {
                    drawable = null;
                    WriteLog.write(this, e);
                }
                if (drawable != null) {
                    final Drawable finalDrawable = drawable;
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            button1.setBackgroundDrawable(finalDrawable);
                        }
                    });
                }
            }
        }
    }

    private final boolean isConnected(final String shiobeStatus) {
        if ((shiobeStatus != null) && shiobeStatus.equals("available")) {
            return true;
        } else {
            return false;
        }
    }

    private final void loadWebpage(final String uri) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);

        int pref_timeout_connection = ListAdapter.getPrefInt(this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
        final long pref_timeout_connection2 =
                ((uri.startsWith(ListAdapter.app_uri_about)) || (uri.equals(ListAdapter.app_uri_local))) ? (pref_timeout_connection / 5) : pref_timeout_connection;

        timeout[1] = true;
        loadWebpageInit();
        webView1.loadUrl(uri);
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public final void onPageFinished(final WebView view, final String url) {
                preCapturedUri = url;
                timeout[1] = false;

                webView1.setVisibility(View.VISIBLE);
                // webView1.requestFocus(View.FOCUS_DOWN);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public final void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                webView1.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        try {
                            Thread.sleep(pref_timeout_connection2);
                        } catch (final InterruptedException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        }

                        if (timeout[1]) {
                            WriteLog.write(UpdateTweet.this, getString(R.string.timeout));
                            if (url.startsWith(ListAdapter.app_uri_about)) {
                                runOnUiThread(new Runnable() {
                                    public final void run() {
                                        webView1.stopLoading();
                                        webView1.loadUrl(ListAdapter.app_uri_local);
                                        webView1.requestFocus(View.FOCUS_DOWN);
                                    }
                                });
                            } else if (url.equals(ListAdapter.app_uri_local) == false) {
                                adapter.toast(getString(R.string.timeout));
                            }
                        }
                    }
                }).start();
            }

            @Override
            public final void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                if (failingUrl.startsWith(ListAdapter.app_uri_about)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            webView1.stopLoading();
                            webView1.loadUrl(ListAdapter.app_uri_local);
                            webView1.requestFocus(View.FOCUS_DOWN);
                        }
                    });
                } else if (failingUrl.equals(ListAdapter.app_uri_local) == false) {
                    WriteLog.write(UpdateTweet.this, "errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                    adapter.toast("errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                }
            }

            @Override
            public final void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                WriteLog.write(UpdateTweet.this, "error: " + error.toString());
                handler.proceed();
            }
        });

    }

    private final void loadWebpage(final String uri, final boolean capture_thumbnail_use_picturelistener, final long timeout_connection) {
        timeout[0] = true;
        loadWebpageInit();
        if (capture_thumbnail_use_picturelistener) {
            try {
                capture_thumbnail_retry = ListAdapter.getPrefInt(this, "pref_capture_thumbnail_retry", Integer.toString(ListAdapter.default_capture_thumbnail_retry));
                WriteLog.write(UpdateTweet.this, "(pref_capture_thumbnail_use_picturelistener)" + " pref_capture_thumbnail_retry:" + capture_thumbnail_retry + " default_capture_thumbnail_retry:"
                        + ListAdapter.default_capture_thumbnail_retry);
            } catch (final Exception e) {
                capture_thumbnail_retry = ListAdapter.default_capture_thumbnail_retry;
                WriteLog.write(UpdateTweet.this, "(pref_capture_thumbnail_use_picturelistener)" + " NullPointerException pref_capture_thumbnail_retry:" + capture_thumbnail_retry
                        + " default_capture_thumbnail_retry:" + ListAdapter.default_capture_thumbnail_retry);
            }
            webView1.setPictureListener(new MyPictureListener());
        }
        webView1.loadUrl(uri);
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                preCapturedUri = url;
                timeout[0] = false;
                if (!capture_thumbnail_use_picturelistener) {
                    capture_webpagethumbnail(capture_thumbnail_height, capture_thumbnail_width, capture_thumbnail_scale);
                }

                webView1.setVisibility(View.VISIBLE);
                // webView1.requestFocus(View.FOCUS_DOWN);
            }

            @Override
            public final void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                webView1.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        try {
                            Thread.sleep(timeout_connection);
                        } catch (final InterruptedException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        }
                        if (timeout[0]) {
                            WriteLog.write(UpdateTweet.this, getString(R.string.timeout));
                            if (url.startsWith(ListAdapter.app_uri_about)) {
                                runOnUiThread(new Runnable() {
                                    public final void run() {
                                        webView1.stopLoading();
                                        webView1.loadUrl(ListAdapter.app_uri_local);
                                        webView1.requestFocus(View.FOCUS_DOWN);
                                    }
                                });
                            } else if (url.equals(ListAdapter.app_uri_local) == false) {
                                adapter.toast(getString(R.string.timeout));
                            }
                        }
                    }
                }).start();
            }

            @Override
            public final void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                adapter.toast(getString(R.string.network_error) + ": " + description);
                runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        webView1.stopLoading();
                        webView1.loadUrl("about:blank");
                        if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
                            WriteLog.write(UpdateTweet.this, "errorCode: ERROR_FILE_NOT_FOUND description: " + description + " failingUrl: " + failingUrl);
                            adapter.toast(getString(R.string.webview_error_file_not_found));
                        } else {
                            WriteLog.write(UpdateTweet.this, "errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                        }
                    }
                });
            }

            @Override
            public final void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                WriteLog.write(UpdateTweet.this, "error: " + error.toString());
                handler.proceed();
            }
        });
    }

    private final void loadWebpageInit() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_useragent = pref_app.getString("pref_useragent", getString(R.string.useragent_ff));

        // webView1.setVisibility(View.VISIBLE);
        webView1.setVisibility(View.GONE);
        final LayoutParams lp = webView1.getLayoutParams();
        lp.width = capture_thumbnail_webview_width;
        lp.height = capture_thumbnail_webview_height;
        webView1.setLayoutParams(lp);
        webView1.getSettings().setJavaScriptEnabled(true);
        if (pref_useragent.equals("")) {
            webView1.getSettings().setUserAgentString(getString(R.string.useragent_ff));
        } else {
            webView1.getSettings().setUserAgentString(pref_useragent);
        }
        webView1.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView1.getSettings().setBuiltInZoomControls(true);
        webView1.getSettings().setSupportZoom(true);
        try {
            final Field nameField = webView1.getSettings().getClass().getDeclaredField("mBuiltInZoomControls");
            nameField.setAccessible(true);
            nameField.set(webView1.getSettings(), false);
        } catch (final Exception e) {
            WriteLog.write(UpdateTweet.this, e);
            webView1.getSettings().setBuiltInZoomControls(false);
        }
        webView1.getSettings().setLoadWithOverviewMode(true);
        webView1.getSettings().setUseWideViewPort(true);
        webView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        String pref_cooperation_url = pref_app.getString("pref_cooperation_url", ListAdapter.default_cooperation_url);
        pref_cooperation_url += (pref_cooperation_url.endsWith("/")) ? "" : "/";
        webView1.loadUrl(pref_cooperation_url + "shiobeforandroid.php?id=" + ListAdapter.getSha1(StringUtil.join("_", ListAdapter.getPhoneIds())) + "&note="
                + StringUtil.join("__", adapter.getOurScreenNames()));
    }

    @Override
    protected final void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        if ((requestCode == REQUEST_VOICERECOG) && (resultCode == RESULT_OK)) {
            WriteLog.write(this, "REQUEST_VOICERECOG");
            final ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (int i = 0; i < results.size(); i++) {
                editText2.setText(editText2.getText() + results.get(i));
            }
        } else if ((requestCode == REQUEST_GALLERY) && (resultCode == RESULT_OK)) {
            WriteLog.write(this, "REQUEST_GALLERY");
            Uri uri;
            try {
                WriteLog.write(this, "data.getData(): " + data.getData());
                uri = data.getData();
                WriteLog.write(this, "uri: " + uri.toString());
                image_set(uri);
            } catch (final Exception e) {
                uri = null;
                WriteLog.write(UpdateTweet.this, e);
            }
        } else if ((requestCode == REQUEST_CAMERA) && (resultCode == RESULT_OK)) {
            WriteLog.write(this, "REQUEST_CAMERA");
            Uri uri;
            try {
                WriteLog.write(this, "imageUri: " + imageUri);
                if (imageUri.equals("")) {
                    WriteLog.write(this, "data.getData(): " + data.getData());
                    WriteLog.write(this, "data.getExtras().get(\"data\"): " + data.getExtras().get("data"));
                    if (data.getData() != null) {
                        imageUri = data.getData();
                    }
                }
                uri = imageUri;
                WriteLog.write(this, "uri: " + uri.toString());
                image_set(uri);
                WriteLog.write(this, "image_set()");
            } catch (final Exception e) {
                uri = null;
                WriteLog.write(UpdateTweet.this, e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onClick(final View v) {
        if (v.getId() == R.id.button1) {
            button1Clicked();
        } else if (v.getId() == R.id.button2) {
            //
        } else if (v.getId() == R.id.imageView1) {
            sharePicture();
        } else if (v.getId() == R.id.textView1) {
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    textView1.setText(simpleDateFormat.format(new Date((System.currentTimeMillis() + getNtpOffset()))));
                }
            });
        } else if (v.getId() == R.id.textView2) {
            final Status justbeforeTweet = adapter.getjustbefore(adapter.checkIndexFromPrefTwtr());
            if (justbeforeTweet != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        textView2.setText(justbeforeTweet.getText() + " " + adapter.DFu.format(justbeforeTweet.getCreatedAt()));
                    }
                });
            }
        }
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int logi = 0;

        // BugReport
        // Thread.setDefaultUncaughtExceptionHandler(new CsUncaughtExceptionHandler(this.getApplicationContext()));
        WriteLog.write(this, (logi++) + ": BugReport");

        // StrictMode
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        WriteLog.write(this, (logi++) + ": StrictMode");

        // pref_app
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        WriteLog.write(this, (logi++) + ": pref_app");

        // pref_twtr
        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        WriteLog.write(this, (logi++) + ": pref_twtr");

        // 認証
        simpleauth();
        WriteLog.write(this, (logi++) + ": 認証");

        // crpKey
        crpKey = getString(R.string.app_name);
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        crpKey += telephonyManager.getDeviceId();
        crpKey += telephonyManager.getSimSerialNumber();
        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
            crpKey += Long.toString(packageInfo.firstInstallTime);
        } catch (NameNotFoundException e) {
            WriteLog.write(this, e);
        }
        WriteLog.write(this, (logi++) + ": crpKey");

        // adapter
        adapter = new ListAdapter(this, crpKey, null, null);
        WriteLog.write(this, (logi++) + ": adapter");

        // 画面の向き
        setOrientation();
        WriteLog.write(this, (logi++) + ": 画面の向き");

        // View
        setContentView(R.layout.tweet);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        editText1 = (AutoCompleteTextView) this.findViewById(R.id.editText1);
        editText2 = (EditText) this.findViewById(R.id.editText2);
        editText3 = (AutoCompleteTextView) this.findViewById(R.id.editText3);
        editText4 = (EditText) this.findViewById(R.id.editText4);
        editText5 = (EditText) this.findViewById(R.id.editText5);
        editText2.setFocusable(true);
        editText2.setFocusableInTouchMode(true);
        editText2.requestFocusFromTouch();
        imageView1 = (ImageView) this.findViewById(R.id.imageView1);
        textView1 = (TextView) this.findViewById(R.id.textView1);
        textView2 = (TextView) this.findViewById(R.id.textView2);
        webView1 = (WebView) this.findViewById(R.id.webView1);
        WriteLog.write(this, (logi++) + ": View");

        // View色
        final String pref_tl_bgcolor_updatetweet = pref_app.getString("pref_tl_bgcolor_updatetweet", "#000000");
        pref_tl_fontcolor_text_updatetweet = pref_app.getString("pref_tl_fontcolor_text_updatetweet", "#ffffff");
        pref_tl_fontcolor_text_updatetweet_over = pref_app.getString("pref_tl_fontcolor_text_updatetweet_over", "#ff0000");
        pref_tl_fontcolor_text_updatetweet_button_tweet = pref_app.getString("pref_tl_fontcolor_text_updatetweet_button_tweet", "#ffffff");
        pref_tl_fontcolor_text_updatetweet_button_tweet_over = pref_app.getString("pref_tl_fontcolor_text_updatetweet_button_tweet_over", "#ff0000");

        if (pref_tl_bgcolor_updatetweet.equals("") == false) {
            try {
                final LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
                linearLayout1.setBackgroundColor(Color.parseColor(pref_tl_bgcolor_updatetweet));
            } catch (final IllegalArgumentException e) {
            }
        }
        setTextColorOnTextChanged();
        if (pref_tl_fontcolor_text_updatetweet.equals("") == false) {
            try {
                //				button1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_button_tweet));
                button2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_button_tweet));
                //				editText1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                //				editText2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                //				editText3.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                editText4.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                editText5.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                textView1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                textView2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
            } catch (final IllegalArgumentException e) {
            }
        }
        WriteLog.write(this, (logi++) + ": View色");

        // Viewイベント
        registerForContextMenu(editText2);
        editText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public final boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if ((pre_key == KeyEvent.KEYCODE_SHIFT_LEFT) || (pre_key == KeyEvent.KEYCODE_SHIFT_RIGHT)) {
                        WriteLog.write(UpdateTweet.this, "Shift+Enter");
                        tweet_button();
                        pre_key = 0;
                        return true;
                    }
                    if ((pre_key == 113) || (pre_key == KeyEvent.KEYCODE_CTRL_RIGHT)) {
                        WriteLog.write(UpdateTweet.this, "Ctrl+Enter");
                        tweet_button();
                        pre_key = 0;
                        return true;
                    }
                }
                if ((keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) || (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) || (keyCode == KeyEvent.KEYCODE_CTRL_LEFT) || (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)) {
                    pre_key = keyCode;
                    WriteLog.write(UpdateTweet.this, "keyCode: " + keyCode);
                } else {
                    pre_key = 0;
                }
                return false;
            }
        });

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        button1.setOnLongClickListener(this);
        button2.setOnLongClickListener(this);

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public final void afterTextChanged(Editable s) {
                if ((inReplyToStatusId.equals("") == false) && (editText1.getText().toString().equals(""))) {
                    inReplyToStatusId = "";
                    adapter.toast(getString(R.string.clear_in_reply_to_status_id));
                }
                setTextColorOnTextChanged();
            }

            @Override
            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                setTextColorOnTextChanged();
            }
        });
        editText1.setOnFocusChangeListener(this);
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public final void afterTextChanged(final Editable s) {
                setTextColorOnTextChanged();
            }

            @Override
            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                setTextColorOnTextChanged();
            }
        });
        editText2.setOnFocusChangeListener(this);
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public final void afterTextChanged(final Editable s) {
                setTextColorOnTextChanged();
            }

            @Override
            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                setTextColorOnTextChanged();
            }
        });
        editText3.setOnFocusChangeListener(this);
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public final void afterTextChanged(final Editable s) {
                checkLocationinfoException(editText4, editText5);
            }

            @Override
            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                checkLocationinfoException(editText4, editText5);
            }
        });
        editText4.setOnFocusChangeListener(this);
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public final void afterTextChanged(final Editable s) {
                checkLocationinfoException(editText4, editText5);
            }

            @Override
            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                checkLocationinfoException(editText4, editText5);
            }
        });
        editText5.setOnFocusChangeListener(this);
        imageView1.setOnClickListener(this);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        webView1.setVisibility(View.GONE);

        // etc
        new Thread(new Runnable() {
            @Override
            public final void run() {
                int logk = 0;

                // 通知
                final boolean pref_showIcon = pref_app.getBoolean("pref_showIcon", false);
                final boolean pref_showIconWear = pref_app.getBoolean("pref_showIconWear", false);
                if (pref_showIcon == true) {
                    adapter.notificationShowIcon(pref_showIconWear);
                }
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": notificationShowIcon");

                // 初回起動
                if (pref_twtr.getBoolean("first_run", false)) {
                    final SharedPreferences.Editor editor1 = pref_twtr.edit();
                    editor1.putBoolean("first_run", true);
                    editor1.commit();
                    // 初回起動
                    // first_run();
                }
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": first_run");

                // タイムアウト
                pref_timeout_connection = ListAdapter.getPrefInt(UpdateTweet.this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
                pref_timeout_so = ListAdapter.getPrefInt(UpdateTweet.this, "pref_timeout_so", ListAdapter.default_timeout_so_string);
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": timeout");

                // URL展開
                urlUtil = new UrlUtil(UpdateTweet.this);
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": urlUtil");

                // WebViewキャプチャ
                final WindowManager windowManager1 = getWindowManager();
                final Display display1 = windowManager1.getDefaultDisplay();
                final DisplayMetrics displayMetrics1 = new DisplayMetrics();
                display1.getMetrics(displayMetrics1);
                capture_thumbnail_webview_width = displayMetrics1.widthPixels;
                capture_thumbnail_webview_height = displayMetrics1.heightPixels;
                try {
                    if (ListAdapter.getPrefInt(UpdateTweet.this, "pref_capture_thumbnail_webview_height", "0") > 0) {
                        capture_thumbnail_webview_height = ListAdapter.getPrefInt(UpdateTweet.this, "pref_capture_thumbnail_webview_height", "0");
                    }
                    if (ListAdapter.getPrefInt(UpdateTweet.this, "pref_capture_thumbnail_webview_width", "0") > 0) {
                        capture_thumbnail_webview_width = ListAdapter.getPrefInt(UpdateTweet.this, "pref_capture_thumbnail_webview_width", "0");
                    }
                } catch (final NumberFormatException e) {
                    WriteLog.write(UpdateTweet.this, e);
                }
                capture_thumbnail_height = (int) (capture_thumbnail_webview_height / displayMetrics1.scaledDensity);
                capture_thumbnail_width = (int) (capture_thumbnail_webview_width / displayMetrics1.scaledDensity);
                capture_thumbnail_scale = displayMetrics1.scaledDensity;
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": WebView capture");

                // フォント
                final String pref_tl_fontfilename = pref_app.getString("pref_tl_fontfilename", "");
                if (pref_tl_fontfilename.equals("") == false) {
                    final FontUtil fontUtil = new FontUtil();
                    fontUtil.loadFont(pref_tl_fontfilename, UpdateTweet.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            // 99-2-1 フォントを指定
                            fontUtil.setFont(editText1, UpdateTweet.this);
                            fontUtil.setFont(editText2, UpdateTweet.this);
                            fontUtil.setFont(editText3, UpdateTweet.this);
                            fontUtil.setFont(editText4, UpdateTweet.this);
                            fontUtil.setFont(editText5, UpdateTweet.this);
                            fontUtil.setFont(textView1, UpdateTweet.this);
                            fontUtil.setFont(textView2, UpdateTweet.this);
                        }
                    });
                }
                final float pref_tl_fontsize_updatetweet = ListAdapter.getPrefFloat(UpdateTweet.this, "pref_tl_fontsize_updatetweet", "14");
                if (pref_tl_fontsize_updatetweet > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            // 99-2-2 入力欄文字サイズを指定
                            editText1.setTextSize(pref_tl_fontsize_updatetweet);
                            editText2.setTextSize(pref_tl_fontsize_updatetweet);
                            editText3.setTextSize(pref_tl_fontsize_updatetweet);
                            editText4.setTextSize(pref_tl_fontsize_updatetweet);
                            editText5.setTextSize(pref_tl_fontsize_updatetweet);
                            textView1.setTextSize(pref_tl_fontsize_updatetweet);
                            textView2.setTextSize(pref_tl_fontsize_updatetweet);
                        }
                    });
                }
                final float pref_tl_fontsize_updatetweet_button = ListAdapter.getPrefFloat(UpdateTweet.this, "pref_tl_fontsize_updatetweet_button", "12");
                if (pref_tl_fontsize_updatetweet_button > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            // 99-2-3 ボタン文字サイズを指定
                            button1.setTextSize(pref_tl_fontsize_updatetweet_button);
                            button2.setTextSize(pref_tl_fontsize_updatetweet_button);
                        }
                    });
                }
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": fontUtil");

                // 日付・時刻フォーマット
                String pref_show_datetime_format = "yyyy/MM/dd HH:mm:ss.SS";
                try {
                    pref_show_datetime_format = pref_app.getString("pref_show_datetime_format", "yyyy/MM/dd HH:mm:ss.SS");
                    simpleDateFormat = new SimpleDateFormat(pref_show_datetime_format, Locale.JAPAN);
                } catch (final Exception e) {
                }
                int pref_show_datetime_interval = ListAdapter.getPrefInt(UpdateTweet.this, "pref_show_datetime_interval", "500");

                // 要接続
                final boolean connected = checkNetworkUtil.isConnected();
                if (connected) {
                    if ((getNtpOffset() > 1000) || (-1000 > getNtpOffset())) {
                        adapter.toast(getString(R.string.ntp_offset) + ": " + getNtpOffset() + getString(R.string.millisecond));
                    }
                    WriteLog.write(UpdateTweet.this, "99-" + logk + ": simpleDateFormat");
                    // 日付・時刻
                    if (pref_show_datetime_interval > -1) {
                        getNtpOffset();
                        final MyTimerTask timerTask = new MyTimerTask();
                        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(timerTask, 20 * pref_show_datetime_interval, pref_show_datetime_interval, TimeUnit.MILLISECONDS);
                    }
                    WriteLog.write(UpdateTweet.this, "99-" + logk + ": NTP");

                    // 直前のツイート
                    final Status justbeforeTweet = adapter.getjustbefore(adapter.checkIndexFromPrefTwtr());
                    if (justbeforeTweet != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                textView2.setText(justbeforeTweet.getText() + " " + adapter.DFu.format(justbeforeTweet.getCreatedAt()));
                            }
                        });
                    }
                    WriteLog.write(UpdateTweet.this, "99-" + logk + ": justbeforeTweet");
                }

                // 起動音
                final boolean pref_enable_ringtone_onstart = pref_app.getBoolean("pref_enable_ringtone_onstart", true);
                final String pref_ringtone_onstart_updatetweet = pref_app.getString("pref_ringtone_onstart_updatetweet", "");
                if (pref_enable_ringtone_onstart && (pref_ringtone_onstart_updatetweet != null) && (pref_ringtone_onstart_updatetweet.equals("") == false)) {
                    final MediaPlayer mediaPlayer = MediaPlayer.create(UpdateTweet.this, Uri.parse(pref_ringtone_onstart_updatetweet));
                    mediaPlayer.setLooping(false);
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                WriteLog.write(UpdateTweet.this, "99-" + logk + ": ringtone_onstart");

                // Usage
                final String screenName = adapter.checkScreennameFromIndex(adapter.checkIndexFromPrefTwtr());
                if ((!screenName.endsWith("26")) && (!screenName.equals("shiobe4a"))) {
                    WriteLog.writeUsage(UpdateTweet.this, "onCreate");
                }
            }
        }).start();
        WriteLog.write(this, "99: etc");

        // 要接続
        final boolean connected = checkNetworkUtil.isConnected();
        if (connected) {
            int logj = 0;
            try {
                // init_user
                init_user(adapter.checkIndexFromPrefTwtr());
                WriteLog.write(this, logi + "-" + (logj++) + ": init_user");
            } catch (final Exception e) {
            }
        }
        WriteLog.write(this, (logi++) + ": need internet-connection");

        // 一部要接続
        get_intent(getIntent());
        WriteLog.write(this, (logi++) + ": get_intent");
    }

    @Override
    public final void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final int id = v.getId();
        if (id == R.id.editText2) {
            WriteLog.write(this, "(id == R.id.editText2)");

            menu.add(0, R.string.tweet_current_account, 0, R.string.tweet_current_account).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item4) {
                    tweet_button();
                    return true;
                }
            });

            menu.add(0, R.string.paste, 0, R.string.paste).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item4) {
                    try {
                        final ClipboardManager clipboardManager1 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        final int start = editText2.getSelectionStart();
                        final int end = editText2.getSelectionEnd();
                        editText2.getText().replace(Math.min(start, end), Math.max(start, end), clipboardManager1.getText().toString());
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                    return true;
                }
            });

            final SubMenu sub1 = menu.addSubMenu(R.string.clear);
            sub1.add(0, R.string.allform, 0, R.string.allform).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item1) {
                    editText1.setText("");
                    editText2.setText("");
                    editText3.setText("");
                    editText4.setText("");
                    editText5.setText("");
                    imageView1.setVisibility(View.GONE);
                    webView1.setVisibility(View.GONE);
                    setTextColorOnTextChanged();
                    return true;
                }
            });
            sub1.add(0, R.string.prefix, 0, R.string.prefix).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    editText1.setText("");
                    setTextColorOnTextChanged();
                    return true;
                }
            });
            sub1.add(0, R.string.message, 0, R.string.message).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item) {
                    editText2.setText("");
                    setTextColorOnTextChanged();
                    return true;
                }
            });
            sub1.add(0, R.string.suffix, 0, R.string.suffix).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item) {
                    editText3.setText("");
                    setTextColorOnTextChanged();
                    return true;
                }
            });
            sub1.setGroupCheckable(0, true, true);

            final Boolean pref_uri_shorten_withhttp = pref_app.getBoolean("pref_uri_shorten_withhttp", false);
            final String group_suffix = pref_uri_shorten_withhttp ? getString(R.string.with_http) : getString(R.string.without_http);

            final SubMenu sub1b = menu.addSubMenu(R.string.get_webpage_title);
            final SubMenu sub1c = menu.addSubMenu(R.string.uri_show_in_webview);
            final SubMenu sub2 = menu.addSubMenu(R.string.uri_shorten);

            final Matcher matcher1b =
                    ListAdapter.pattern_urlHttpHttps.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
            int i = 0;
            String pregroup1b = "";
            while (matcher1b.find()) {
                final String group = matcher1b.group(0);
                if (group.equals(pregroup1b) == false) {
                    pregroup1b = group;
                    sub1b.add(0, R.string.get_webpage_title + i++, 0, group).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public final boolean onMenuItemClick(final MenuItem item1b) {
                            if (!isFinishing()) {
                                showDialog(R.string.get_webpage_title);
                            }

                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    try {
                                        final String webpagetitle = get_webpagetitle(group);
                                        if (webpagetitle.equals("") == false) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    editText2.setText(StringUtil.getTweetString(webpagetitle, editText2.getText().toString()));

                                                    try {
                                                        dismissDialog(R.string.get_webpage_title);
                                                    } catch (final IllegalArgumentException e) {
                                                    }
                                                }
                                            });
                                        }
                                    } catch (final Exception e) {
                                        WriteLog.write(UpdateTweet.this, e);
                                    }
                                }
                            }).start();
                            return true;
                        }
                    });

                    sub1c.add(0, R.string.uri_show_in_webview + i++, 0, group).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public final boolean onMenuItemClick(final MenuItem item1b) {
                            if (!isFinishing()) {
                                showDialog(R.string.uri_show_in_webview);
                            }

                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    try {
                                        if (group.startsWith(getString(R.string.httphttps)) == true) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    loadWebpage(group);
                                                    webView1.requestFocus(View.FOCUS_DOWN);

                                                    try {
                                                        dismissDialog(R.string.uri_show_in_webview);
                                                    } catch (final IllegalArgumentException e) {
                                                    }
                                                }
                                            });
                                        }
                                    } catch (final Exception e) {
                                        WriteLog.write(UpdateTweet.this, e);
                                    }
                                }
                            }).start();
                            return true;
                        }
                    });

                    sub2.add(0, R.string.uri_shorten + i++, 0, group + "(" + group_suffix + ")").setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public final boolean onMenuItemClick(final MenuItem item2) {
                            try {
                                final String shorturi = urlUtil.uri_shorten(group, pref_uri_shorten_withhttp);
                                if (shorturi.equals("") == false) {
                                    editText1.setText(editText1.getText().toString().replaceAll(group, shorturi));
                                    editText2.setText(editText2.getText().toString().replaceAll(group, shorturi));
                                    editText3.setText(editText3.getText().toString().replaceAll(group, shorturi));
                                }
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                            return true;
                        }
                    });
                }
            }
            sub1b.setGroupCheckable(0, true, true);
            sub1c.setGroupCheckable(0, true, true);
            sub2.setGroupCheckable(0, true, true);

            final SubMenu sub3 = menu.addSubMenu(R.string.uri_expand);
            final Matcher matcher3 =
                    ListAdapter.pattern_urlHttpHttpsShortened.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
            i = 0;
            String pregroup3 = "";
            while (matcher3.find()) {
                final String group = matcher3.group(0);
                if (group.equals(pregroup3) == false) {
                    pregroup3 = group;
                    sub3.add(0, R.string.uri_expand + i++, 0, group).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public final boolean onMenuItemClick(final MenuItem item3) {
                            try {
                                final String longuri = urlUtil.expand_uri(group);
                                if (longuri.equals("") == false) {
                                    editText1.setText(editText1.getText().toString().replaceAll(group, longuri));
                                    editText2.setText(editText2.getText().toString().replaceAll(group, longuri));
                                    editText3.setText(editText3.getText().toString().replaceAll(group, longuri));
                                }
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                            return true;
                        }
                    });
                }
            }
            sub3.setGroupCheckable(0, true, true);

            menu.add(0, R.string.check_in_reply_to_status_id, 0, R.string.check_in_reply_to_status_id).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item4) {
                    if (inReplyToStatusId == null) {
                        adapter.toast("inReplyToStatusId: - ");
                    } else {
                        final EditText editText = new EditText(UpdateTweet.this);
                        editText.setText(inReplyToStatusId);
                        new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.schedule).setView(editText).setMessage(R.string.status_id).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public final void onClick(final DialogInterface dialog, final int which) {
                                inReplyToStatusId = editText.getText().toString();
                            }
                        }).setNeutralButton(R.string.uri_show_in_browser, new DialogInterface.OnClickListener() {
                            public final void onClick(final DialogInterface dialog, final int which) {
                                try {
                                    final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/a/status/" + inReplyToStatusId));
                                    startActivity(intent);
                                } catch (final ActivityNotFoundException e) {
                                    WriteLog.write(UpdateTweet.this, e);
                                } catch (final Exception e) {
                                    WriteLog.write(UpdateTweet.this, e);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public final void onClick(final DialogInterface dialog, final int which) {
                            }
                        }).create().show();
                    }
                    return true;
                }
            });

            final SubMenu sub5 = menu.addSubMenu(R.string.edit_text);
            sub5.add(0, R.string.remove_space_left, 0, R.string.remove_space_left).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item5) {
                    final char[] c = {'\u3000'};
                    final String wspace = new String(c);
                    final Pattern p = Pattern.compile("[" + wspace + " \\t\\r]+\\n");
                    final Matcher m1 = p.matcher(editText1.getText().toString());
                    while (m1.find()) {
                        editText1.setText(m1.replaceAll(System.getProperty("line.separator")));
                    }
                    final Matcher m2 = p.matcher(editText2.getText().toString());
                    while (m2.find()) {
                        editText2.setText(m2.replaceAll(System.getProperty("line.separator")));
                    }
                    final Matcher m3 = p.matcher(editText3.getText().toString());
                    while (m3.find()) {
                        editText3.setText(m3.replaceAll(System.getProperty("line.separator")));
                    }
                    return true;
                }
            });
            sub5.add(0, R.string.remove_space_right, 0, R.string.remove_space_right).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item5) {
                    final char[] c = {'\u3000'};
                    final String wspace = new String(c);
                    final Pattern p = Pattern.compile("\\n[" + wspace + " \\t\\r]+");
                    final Matcher m1 = p.matcher(editText1.getText().toString());
                    while (m1.find()) {
                        editText1.setText(m1.replaceAll(System.getProperty("line.separator")));
                    }
                    final Matcher m2 = p.matcher(editText2.getText().toString());
                    while (m2.find()) {
                        editText2.setText(m2.replaceAll(System.getProperty("line.separator")));
                    }
                    final Matcher m3 = p.matcher(editText3.getText().toString());
                    while (m3.find()) {
                        editText3.setText(m3.replaceAll(System.getProperty("line.separator")));
                    }
                    return true;
                }
            });
            sub5.add(0, R.string.add_quotemark_left, 0, R.string.add_quotemark_left).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item5) {
                    final Pattern p = Pattern.compile("^|\\r|\\n|\\r\\n|" + System.getProperty("line.separator"));
                    final Matcher m1 = p.matcher(editText1.getText().toString());
                    while (m1.find()) {
                        editText1.setText(m1.replaceAll(System.getProperty("line.separator") + "> "));
                    }
                    final Matcher m2 = p.matcher(editText2.getText().toString());
                    while (m2.find()) {
                        editText2.setText(m2.replaceAll(System.getProperty("line.separator") + "> "));
                    }
                    final Matcher m3 = p.matcher(editText3.getText().toString());
                    while (m3.find()) {
                        editText3.setText(m3.replaceAll(System.getProperty("line.separator") + "> "));
                    }
                    return true;
                }
            });

            sub5.add(0, R.string.suddenly_death, 0, R.string.suddenly_death).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item5) {
                    final String orig = editText2.getText().toString();
                    int len = orig.length();
                    final StringBuilder sb = new StringBuilder(140);
                    sb.append(getString(R.string.zenkaku_underbar));
                    sb.append(adapter.repeatStr("人", len));
                    sb.append(getString(R.string.zenkaku_underbar));
                    sb.append("\n");
                    sb.append(getString(R.string.zenkaku_lt));
                    sb.append(orig);
                    sb.append(getString(R.string.zenkaku_gt));
                    sb.append("\n");
                    sb.append(getString(R.string.zenkaku_overline));
                    sb.append(adapter.repeatStr("Y^", len - 1));
                    sb.append("Y");
                    sb.append(getString(R.string.zenkaku_overline));
                    editText2.setText(sb.toString());
                    return true;
                }
            });

            sub5.add(0, R.string.go_home, 0, R.string.go_home).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item5) {
                    final String orig = editText2.getText().toString();
                    int len = orig.length();
                    final StringBuilder sb = new StringBuilder(140);
                    for (int i = 0; i < len; i++) {
                        final char c = orig.charAt(i);
                        if (((c != ' ') && (c != '\u3000'))) {
                            sb.append(c);
                            sb.append(getString(R.string.zenkaku_dakuten));
                        } else {
                            sb.append(getString(R.string.go_home_yada));
                        }
                    }
                    sb.append(getString(R.string.go_home_kaeru));
                    editText2.setText(sb.toString());
                    return true;
                }
            });

            sub5.add(0, R.string.morse, 0, R.string.morse).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item5) {
                    final String[] ITEM1 = {getString(R.string.morse_e2m), getString(R.string.morse_j2m), getString(R.string.morse_m2e), getString(R.string.morse_m2j),};

                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.morse).setItems(ITEM1, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            try {
                                if (ITEM1[which].equals(getString(R.string.morse_e2m))) {
                                    editText3.setText(StringUtil.getTweetString(editText3.getText().toString(), (new Morse()).EStringToEMorses(editText2.getText().toString())));
                                } else if (ITEM1[which].equals(getString(R.string.morse_j2m))) {
                                    editText3.setText(StringUtil.getTweetString(editText3.getText().toString(), (new Morse()).JStringToJMorses(editText2.getText().toString())));
                                } else if (ITEM1[which].equals(getString(R.string.morse_m2e))) {
                                    editText3.setText(StringUtil.getTweetString(editText3.getText().toString(), (new Morse()).EMorsesToEString(editText2.getText().toString())));
                                } else if (ITEM1[which].equals(getString(R.string.morse_m2j))) {
                                    editText3.setText(StringUtil.getTweetString(editText3.getText().toString(), (new Morse()).JMorsesToJString(editText2.getText().toString())));
                                }
                            } catch (final Exception e) {
                            }
                        }
                    }).create().show();
                    return true;
                }
            });

            sub5.setGroupCheckable(0, true, true);

            final SubMenu sub6 = menu.addSubMenu(R.string.autocomplete);
            sub6.add(0, R.string.autocomplete_update, 0, R.string.autocomplete_update).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public final boolean onMenuItemClick(final MenuItem item1) {
                    if (((editText1.getText().toString()).equals("") == false) || ((editText3.getText().toString()).equals("") == false)) {
                        final int pref_timeout_connection = ListAdapter.getPrefInt(UpdateTweet.this, "pref_timeout_connection", ListAdapter.default_timeout_connection_string);
                        final int pref_timeout_so = ListAdapter.getPrefInt(UpdateTweet.this, "pref_timeout_so", ListAdapter.default_timeout_so_string);
                        if (oauthToken.equals("")) {
                            pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                            init_user(adapter.checkIndexFromPrefTwtr());
                        }
                        final String pref_cooperation_url = pref_app.getString("pref_cooperation_url", ListAdapter.default_cooperation_url);

                        if ((editText1.getText().toString()).equals("") == false) {
                            String uri_prelist = "";
                            try {
                                uri_prelist =
                                        pref_cooperation_url + "setting_list_shiobe.php?type=pre&id=" + adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).getScreenName() + "&tok="
                                                + java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweet.this, oauthToken, oauthToken)) + "&text="
                                                + java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweet.this, oauthToken, editText1.getText().toString()));
                            } catch (final TwitterException e) {
                                try {
                                    adapter.twitterException(e);
                                } catch (Exception ex) {
                                    if(adapter != null)
                                        adapter.toast(getString(R.string.cannot_access_twitter));
                                }
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                                adapter.toast(getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + getString(R.string.tryagain_oauth));
                            }
                            final String result_prelist = HttpsClient.https2data(UpdateTweet.this, uri_prelist, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
                            if (result_prelist.startsWith(getString(R.string.done_update_list_header))) {
                                adapter.toast(result_prelist);
                            }
                        }

                        if ((editText3.getText().toString()).equals("") == false) {
                            String uri_taglist = "";
                            try {
                                uri_taglist =
                                        pref_cooperation_url + "setting_list_shiobe.php?type=tag&id=" + adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).getScreenName() + "&tok="
                                                + java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweet.this, oauthToken, oauthToken)) + "&text="
                                                + java.net.URLEncoder.encode(MyCrypt.encrypt(UpdateTweet.this, oauthToken, editText3.getText().toString()));
                            } catch (final TwitterException e) {
                                try {
                                    adapter.twitterException(e);
                                } catch (Exception ex) {
                                    if(adapter != null)
                                        adapter.toast(getString(R.string.cannot_access_twitter));
                                }
                            } catch (final Exception e) {
                                adapter.toast(getString(R.string.exception));
                            }
                            final String result_taglist = HttpsClient.https2data(UpdateTweet.this, uri_taglist, pref_timeout_connection, pref_timeout_so, ListAdapter.default_charset);
                            if (result_taglist.startsWith(getString(R.string.done_update_list_footer))) {
                                adapter.toast(result_taglist);
                            }
                        }

                    }
                    return true;
                }
            });
            sub6.setGroupCheckable(0, true, true);
        }
    }

    @Override
    protected final Dialog onCreateDialog(final int id) {
        final Dialog dialog = adapter.createDialog(id);

        if (dialog != null) {
            return dialog;
        } else {
            return super.onCreateDialog(id);
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(0, R.string.draft, 0, R.string.draft).setIcon(android.R.drawable.ic_menu_edit);

        menu.add(0, R.string.voice, 0, R.string.voice).setIcon(android.R.drawable.ic_btn_speak_now).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, R.string.pictureappended, 0, R.string.pictureappended).setIcon(android.R.drawable.ic_menu_camera).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, R.string.webview_operation, 0, R.string.webview_operation).setIcon(android.R.drawable.ic_menu_camera);

        final SubMenu sub1 = menu.addSubMenu(0, R.string.place, 0, R.string.place).setIcon(android.R.drawable.ic_menu_mylocation);
        sub1.add(0, R.string.enable, 0, R.string.enable);
        sub1.add(0, R.string.disable, 0, R.string.disable);
        sub1.add(0, R.string.share_place, 0, R.string.share_place);
        sub1.add(0, R.string.reversegeocoding, 0, R.string.reversegeocoding);
        sub1.add(0, R.string.tky2jgd, 0, R.string.tky2jgd);
        sub1.add(0, R.string.locationinfo_exception_add, 0, R.string.locationinfo_exception_add);

        final SubMenu sub2 = menu.addSubMenu(0, R.string.share, 0, R.string.share).setIcon(android.R.drawable.ic_menu_share);
        sub2.add(0, R.string.share_search, 0, R.string.share_search);
        sub2.add(0, R.string.share_translate, 0, R.string.share_translate);
        sub2.add(0, R.string.share_telephone, 0, R.string.share_telephone);
        sub2.add(0, R.string.share_picture, 0, R.string.share_picture);
        sub2.add(0, R.string.share_place, 0, R.string.share_shareplace);
        sub2.add(0, R.string.share_isbn, 0, R.string.share_isbn);
        sub2.add(0, R.string.share_send, 0, R.string.share_send);
        sub2.add(0, R.string.share_url, 0, R.string.share_url);

        menu.add(0, R.string.schedule, 0, R.string.schedule).setIcon(android.R.drawable.ic_menu_my_calendar).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, R.string.check_ratelimit, 0, R.string.check_ratelimit).setIcon(android.R.drawable.stat_sys_download);

        menu.add(0, R.string.check_apistatus, 0, R.string.check_apistatus);

        menu.add(0, R.string.deljustbefore, 0, R.string.deljustbefore).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        final SubMenu sub3 = menu.addSubMenu(getString(R.string.settings_all) + "...").setIcon(android.R.drawable.ic_menu_preferences);
        sub3.add(0, R.string.settings, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
        sub3.add(0, R.string.settings_import_export, 0, getString(R.string.settings_import_export) + "...").setIcon(android.R.drawable.stat_notify_sdcard);
        sub3.add(0, R.string.select_tl_fontfilename, 0, getString(R.string.select_tl_fontfilename) + "...").setIcon(android.R.drawable.stat_notify_sdcard);
        sub3.add(0, R.string.get_blockusers, 0, R.string.get_blockusers).setIcon(android.R.drawable.stat_sys_download);
        sub3.add(0, R.string.get_short_url_length, 0, R.string.get_short_url_length).setIcon(android.R.drawable.stat_sys_download);

        final SubMenu sub4 = sub3.addSubMenu(getString(R.string.set_colors) + "...").setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.get_colors, 0, R.string.get_colors).setIcon(android.R.drawable.stat_sys_download);
        sub4.add(0, R.string.set_colors_white_on_black, 0, R.string.set_colors_white_on_black).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_black_on_white, 0, R.string.set_colors_black_on_white).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_white_on_darkgrey, 0, R.string.set_colors_white_on_darkgrey).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_darkgrey_on_black, 0, R.string.set_colors_darkgrey_on_black).setIcon(android.R.drawable.ic_menu_gallery);

        menu.add(0, R.string.copyright, 0, R.string.copyright).setIcon(android.R.drawable.ic_menu_info_details);

        menu.add(0, R.string.back, 0, R.string.back).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    protected final void onDestroy() {
        checkNetworkUtil.autoDisconnect_updatetweet();

        super.onDestroy();
    }

    @Override
    public final void onFocusChange(final View v, final boolean hasFocus) {
        if ((v.getId() == R.id.editText1) || (v.getId() == R.id.editText2) || (v.getId() == R.id.editText3)) {
            setTextColorOnTextChanged();
        } else if ((v.getId() == R.id.editText4) || (v.getId() == R.id.editText5)) {
            checkLocationinfoException(editText4, editText5);
        }
    }

    @Override
    public final void onLocationChanged(final Location location) {
        new Thread(new Runnable() {
            @Override
            public final void run() {
                if ((Double.toString(location.getLatitude()).equals("") == false) && (Double.toString(location.getLongitude()).equals("") == false)) {
                    altitude = location.getAltitude();
                    bearing = location.getBearing();
                    speed = location.getSpeed();

                    pref_app = PreferenceManager.getDefaultSharedPreferences(UpdateTweet.this);
                    final boolean pref_enable_update_locationinfo = pref_app.getBoolean("pref_enable_update_locationinfo", true);
                    if (pref_enable_update_locationinfo) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                editText4.setText(Double.toString(location.getLatitude()));
                                editText5.setText(Double.toString(location.getLongitude()));
                                checkLocationinfoException(editText4, editText5);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public final boolean onLongClick(final View v) {
        if (v.getId() == R.id.button1) {
            tweet_button();
            return true;
        } else if (v.getId() == R.id.button2) {
            tweet_button();
            return true;
        }
        return false;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = pref_app.edit();

        final ArrayList<String> itemList1 = new ArrayList<String>(12);

        final boolean ret = true;

        String pregroup;

        final int user_index_size = ListAdapter.getPrefInt(this, "pref_user_index_size", Integer.toString(ListAdapter.default_user_index_size));
        final String[] ITEM1 = new String[user_index_size + 1];
        int account_num = 0;
        for (int i = 0; i < user_index_size; i++) {
            final String itemname = pref_twtr.getString("screen_name_" + i, "");
            account_num += itemname.equals("") ? 0 : 1;
            ITEM1[i + 1] = itemname.equals("") ? " - " : "@" + itemname;
        }
        ITEM1[0] = (account_num > 0) ? "Current" : "---";

        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
        int idx = -1;
        try {
            idx = Integer.parseInt(pref_twtr.getString("index", "0"));
        } catch (final Exception e) {
            idx = 0;
        }
        final int index = idx;

        if (item.getItemId() == R.string.draft) {
            draft();

        } else if (item.getItemId() == R.string.voice) {
            try {
                final Intent intent_voicerecog = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent_voicerecog.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent_voicerecog.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.voice);
                intent_voicerecog.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                startActivityForResult(intent_voicerecog, REQUEST_VOICERECOG);
            } catch (final ActivityNotFoundException e) {
                WriteLog.write(UpdateTweet.this, e);
                adapter.toast(getString(R.string.notfound_voicerecog));
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }

        } else if (item.getItemId() == R.string.pictureappended) {
            pictureappend();

        } else if (item.getItemId() == R.string.webview_operation) {
            webview_operation();

        } else if (item.getItemId() == R.string.enable) {
            editor.putBoolean("pref_enable_update_locationinfo", true);
            editor.commit();

            if (Build.VERSION.SDK_INT >= 23) {
                checkLocationPermission();
            } else {
                init_location();
            }

        } else if (item.getItemId() == R.string.disable) {
            editor.putBoolean("pref_enable_update_locationinfo", false);
            editor.commit();
            editText4.setText("");
            editText5.setText("");
            if (mLocationManager != null) {
                try {
                    mLocationManager.removeUpdates(this);
                } catch (final Exception e) {
                }
            }

        } else if (item.getItemId() == R.string.share_place) {
            share_place();

        } else if (item.getItemId() == R.string.reversegeocoding) {
            reverse_geocoding();

        } else if (item.getItemId() == R.string.tky2jgd) {
            tky2jgd();

        } else if (item.getItemId() == R.string.locationinfo_exception_add) {
            final String pref_locationinfo_exception_latlng = pref_app.getString("pref_locationinfo_exception_latlng", ListAdapter.default_locationinfo_exception_latlng);
            editor.putString("pref_locationinfo_exception_latlng", pref_locationinfo_exception_latlng + " " + editText4.getText().toString() + "," + editText5.getText().toString());
            editor.commit();

            final EditText editText = new EditText(UpdateTweet.this);
            editText.setText(pref_app.getString("pref_locationinfo_exception_radius", ListAdapter.default_locationinfo_exception_radius));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(UpdateTweet.this).setTitle(getString(R.string.locationinfo_exception) + " " + getString(R.string.locationinfo_exception_radius)).setView(editText).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    final SharedPreferences.Editor editor = pref_app.edit();
                    editor.putString("pref_locationinfo_exception_radius", editText.getText().toString());
                    editor.commit();
                    checkLocationinfoException(editText4, editText5);
                }
            }).show();

        } else if (item.getItemId() == R.string.share_search) {
            new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.search).setPositiveButton(R.string.search_twitter, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()).equals("") == false) {
                        Intent intent_sub2;
                        try {
                            intent_sub2 =
                                    new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/search?q="
                                            + URLEncoder.encode(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()), ListAdapter.default_charset)));
                            startActivity(intent_sub2);
                        } catch (final UnsupportedEncodingException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        } catch (final ActivityNotFoundException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        } catch (final Exception e) {
                            WriteLog.write(UpdateTweet.this, e);
                        }
                    }
                }
            }).setNeutralButton(R.string.search_google, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()).equals("") == false) {
                        try {
                            final Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                            search.putExtra(SearchManager.QUERY, StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
                            startActivity(search);
                        } catch (final ActivityNotFoundException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        } catch (final Exception e) {
                            WriteLog.write(UpdateTweet.this, e);
                        }
                    }
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                }
            }).create().show();

        } else if (item.getItemId() == R.string.share_translate) {
            try {
                final Intent intent_sub4 = new Intent();
                intent_sub4.setAction(Intent.ACTION_VIEW);
                intent_sub4.putExtra("key_text_input", editText2.getText().toString());
                intent_sub4.putExtra("key_text_output", "");
                intent_sub4.putExtra("key_language_from", "ja");
                intent_sub4.putExtra("key_language_to", "en");
                intent_sub4.putExtra("key_suggest_translation", "");
                intent_sub4.putExtra("key_from_floating_window", false);
                intent_sub4.setComponent(new ComponentName("com.google.android.apps.translate", "com.google.android.apps.translate.translation.TranslateActivity"));
                startActivity(intent_sub4);
            } catch (final ActivityNotFoundException e) {
                WriteLog.write(UpdateTweet.this, e);
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }

        } else if (item.getItemId() == R.string.share_telephone) {
            final Pattern pattern_phone = Pattern.compile("(\\+\\d{1,4}[- (]?|\\()?\\d{2,4}[- )]?\\d{2,4}[- ]?\\d{4}", Pattern.DOTALL);
            final Matcher matcher_phone = pattern_phone.matcher(editText2.getText());
            pregroup = "";
            while (matcher_phone.find()) {
                final String group = matcher_phone.group(0);
                if (group.equals(pregroup) == false) {
                    pregroup = group;
                    itemList1.add(group);
                }
            }
            new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.telephone).setItems(itemList1.toArray(new String[itemList1.size()]), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final Intent intent_sub5b = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + itemList1.toArray(new String[itemList1.size()])[which].replaceAll("-", "")));
                        startActivity(intent_sub5b);
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.share_picture) {
            sharePicture();

        } else if (item.getItemId() == R.string.share_isbn) {
            final Pattern p_isbn1 = Pattern.compile("\\d{13}|\\d{10}", Pattern.DOTALL);
            final Pattern p_isbn2 = Pattern.compile("[0-9-]{11}-[0-9X]|\\d{3}-[0-9-]{11}-\\d{1}", Pattern.DOTALL);

            final Matcher matcher_isbn1 = p_isbn1.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
            final Matcher matcher_isbn2 = p_isbn2.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
            pregroup = "";
            while (matcher_isbn1.find()) {
                final String group = matcher_isbn1.group(0);
                if (group.equals(pregroup) == false) {
                    pregroup = group;
                    itemList1.add(group);
                }
            }
            while (matcher_isbn2.find()) {
                final String group = matcher_isbn2.group(0);
                if (group.equals(pregroup) == false) {
                    pregroup = group;
                    itemList1.add(group);
                }
            }
            new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.isbn).setItems(itemList1.toArray(new String[itemList1.size()]), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String bookdata = googlebooksisbn(itemList1.toArray(new String[itemList1.size()])[which].replace("-", ""));
                        WriteLog.write(UpdateTweet.this, "isbn ISBN: " + itemList1.toArray(new String[itemList1.size()])[which] + " data: " + bookdata);
                        if (bookdata.equals("") == false) {
                            editText2.setText(bookdata + editText2.getText().toString().replace(itemList1.toArray(new String[itemList1.size()])[which], ""));
                        }
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.share_send) {
            final String[] ITEM = new String[]{getString(R.string.allform), getString(R.string.prefix), getString(R.string.message), getString(R.string.suffix)};
            new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.send).setItems(ITEM, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final Intent intent_share_send = new Intent();
                        intent_share_send.setAction(Intent.ACTION_SEND);
                        intent_share_send.setType("text/plain");
                        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                        final boolean pref_add_send_extra = pref_twtr.getBoolean("pref_add_send_extra", true);
                        if (pref_add_send_extra) {
                            intent_share_send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + ": @" + adapter.checkScreennameFromIndex(adapter.checkIndexFromPrefTwtr()));
                        }
                        String text = "";
                        if (which == 0) {
                            text = StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString());
                        } else if (which == 1) {
                            text = editText1.getText().toString();
                        } else if (which == 2) {
                            text = editText2.getText().toString();
                        } else if (which == 3) {
                            text = editText3.getText().toString();
                        }

                        final boolean pref_sendintent_text_expanduri = pref_app.getBoolean("pref_sendintent_text_expanduri", true);
                        if (pref_sendintent_text_expanduri) {
                            final Matcher matcher = ListAdapter.pattern_urlHttpHttpsShortened.matcher(text);
                            String pregroup = "";
                            while (matcher.find()) {
                                final String group = matcher.group(0);
                                if (group.equals(pregroup) == false) {
                                    pregroup = group;
                                    final String longuri = urlUtil.expand_uri(group);
                                    if (longuri.equals("") == false) {
                                        text = text.replaceAll(group, longuri);
                                    }
                                }
                            }
                        }

                        intent_share_send.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(intent_share_send);
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.share_url) {
            final Matcher matcher_share_url =
                    ListAdapter.pattern_urlHttpHttps.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
            pregroup = "";
            while (matcher_share_url.find()) {
                final String group = matcher_share_url.group(0);
                if (group.equals(pregroup) == false) {
                    pregroup = group;
                    itemList1.add(group);
                }
            }

            new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.url).setItems(itemList1.toArray(new String[itemList1.size()]), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final Uri uri = Uri.parse(itemList1.toArray(new String[itemList1.size()])[which]);
                        final Intent intent_sub7 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent_sub7);
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }

                }
            }).create().show();

        } else if (item.getItemId() == R.string.schedule) {
            schedule_type();

        } else if (item.getItemId() == R.string.check_ratelimit) {
            adapter.showRateLimits(webView1);

        } else if (item.getItemId() == R.string.check_apistatus) {
            adapter.showApiStatuses(webView1);

        } else if (item.getItemId() == R.string.deljustbefore) {
            adapter.deljustbefore(-1);

        } else if (item.getItemId() == R.string.settings) {
            try {
                final Intent intent2 = new Intent();
                intent2.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Preference");
                startActivity(intent2);
            } catch (final ActivityNotFoundException e) {
                WriteLog.write(UpdateTweet.this, e);
            } catch (final Exception e) {
                WriteLog.write(UpdateTweet.this, e);
            }

        } else if (item.getItemId() == R.string.settings_import_export) {
            final Date date = new Date();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddkkmmss", Locale.JAPAN);
            final String datestr = simpleDateFormat.format(date);

            final String prefXmlPath = Environment.getDataDirectory().getPath() + "/data/" + getPackageName() + "/shared_prefs/" + getPackageName() + "_preferences.xml";
            final String backupPath = Environment.getExternalStorageDirectory().toString() + "/" + getPackageName() + "_preferences.xml";
            final String backupPath_date = Environment.getExternalStorageDirectory().toString() + "/" + getPackageName() + "_preferences_" + datestr + ".xml";
            WriteLog.write(this, "prefXmlPath: " + prefXmlPath);
            WriteLog.write(this, "backupPath: " + backupPath);

            final File dir = new File(Environment.getExternalStorageDirectory().toString() + "/");
            final File[] files = dir.listFiles(new FileCheckPrefXml());
            final String[] ITEM3 = new String[files.length + 2];
            ITEM3[0] = getString(R.string.settings_export);
            ITEM3[1] = getString(R.string.settings_export) + " (日付を付加)";
            for (int i = 0; i < files.length; i++) {
                ITEM3[i + 2] = files[i].getName();
            }

            adapter.toast(getString(R.string.settings_import_export_message));

            new AlertDialog.Builder(this).setTitle(R.string.settings_import_export).setItems(ITEM3, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        if (prefExport(prefXmlPath, backupPath)) {
                            adapter.toast(getString(R.string.settings_export) + ": " + getString(R.string.success));
                        }
                    } else if (which == 1) {
                        if (prefExport(prefXmlPath, backupPath_date)) {
                            adapter.toast(getString(R.string.settings_export) + ": " + getString(R.string.success));
                        }
                    } else {
                        if (prefImport(Environment.getExternalStorageDirectory().toString() + "/" + ITEM3[which])) {
                            adapter.toast(getString(R.string.settings_import) + ": " + getString(R.string.success));
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.select_tl_fontfilename) {
            final File dir = new File(Environment.getExternalStorageDirectory().toString() + "/");
            WriteLog.write(this, dir.getAbsolutePath());
            final File dir2 = new File(Environment.getExternalStorageDirectory().toString() + "/Fonts/");
            WriteLog.write(this, dir2.getAbsolutePath());

            File[] files2a;
            File[] files2b;
            int length = 0;
            try {
                final File[] files2a_ = dir.listFiles();
                WriteLog.write(this, "/");
                for (final File f : files2a_) {
                    WriteLog.write(this, f.getAbsolutePath());
                }

                files2a = dir.listFiles(new FileCheckFont());
                WriteLog.write(this, "/*.ttf");
                for (final File f : files2a) {
                    WriteLog.write(this, f.getAbsolutePath());
                }

                length += files2a.length;
            } catch (final Exception e) {
                files2a = null;
            }

            try {
                final File[] files2b_ = dir2.listFiles();
                WriteLog.write(this, "/Fonts/");
                for (final File f : files2b_) {
                    WriteLog.write(this, f.getAbsolutePath());
                }

                files2b = dir2.listFiles(new FileCheckFont());
                WriteLog.write(this, "/Fonts/*.ttf");
                for (final File f : files2b) {
                    WriteLog.write(this, f.getAbsolutePath());
                }

                length += files2b.length;
            } catch (final Exception e) {
                files2b = null;
            }

            WriteLog.write(this, "length: " + length);
            if (length > 0) {
                final String[] ITEM4 = new String[(length)];

                int k = 0;
                if (files2a != null) {
                    for (int i = 0; i < files2a.length; i++) {
                        ITEM4[k] = files2a[i].getName();
                        k++;
                    }
                }
                if (files2b != null) {
                    for (int j = 0; j < files2b.length; j++) {
                        ITEM4[k] = files2b[j].getName();
                        k++;
                    }
                }

                new AlertDialog.Builder(this).setTitle(R.string.select_tl_fontfilename).setItems(ITEM4, new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(final DialogInterface dialog, final int which) {
                        final SharedPreferences.Editor editor = pref_app.edit();
                        editor.putString("pref_tl_fontfilename", ITEM4[which]);
                        editor.commit();
                        pref_app = PreferenceManager.getDefaultSharedPreferences(UpdateTweet.this);

                        adapter.toast(getString(R.string.select_tl_fontfilename) + ": " + getString(R.string.success) + ": " + ITEM4[which]);
                    }
                }).create().show();
            } else {
                adapter.toast(getString(R.string.select_tl_fontfilename_not_found));
            }

        } else if (item.getItemId() == R.string.get_blockusers) {
            final StringBuilder allBlockedUsersStr = new StringBuilder(32);
            for (int j = 0; j < user_index_size; j++) {
                if (isConnected(pref_twtr.getString("status_" + j, ""))) {
                    try {
                        final PagableResponseList<User> blockedUsers = adapter.getTwitter(j, false).getBlocksList();
                        if (blockedUsers.isEmpty() == false) {
                            final StringBuilder blockedUsersStr = new StringBuilder(32);
                            for (final User user : blockedUsers) {
                                blockedUsersStr.append(user.getScreenName() + ",");
                            }
                            editor.putString("blocked_users_" + Integer.toString(j), "," + blockedUsersStr.toString());
                            allBlockedUsersStr.append(blockedUsersStr.toString());
                        }
                    } catch (final TwitterException e) {
                        try {
                            adapter.twitterException(e);
                        } catch (Exception ex) {
                            if(adapter != null)
                                adapter.toast(getString(R.string.cannot_access_twitter));
                        }
                    } catch (final Exception e) {
                        WriteLog.write(this, e);
                        adapter.toast(getString(R.string.exception));
                    }
                }
            }
            editor.putString("blocked_users", "," + allBlockedUsersStr.toString());
            editor.commit();

        } else if (item.getItemId() == R.string.get_colors) {

            new AlertDialog.Builder(this).setTitle(R.string.login).setItems(ITEM1, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    if (which == 0) {
                        String status = pref_twtr.getString("status_" + Integer.toString(index), "");
                        if (isConnected(status)) {
                            getColors(index);
                        }
                    } else {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(which - 1));
                        editor.commit();
                        String status = pref_twtr.getString("status_" + (which - 1), "");
                        if (isConnected(status)) {
                            getColors(index);
                        }
                    }
                }
            }).create().show();

        } else if (item.getItemId() == R.string.set_colors_white_on_black) {
            setColors("#000000", "#0F1419", "", "#F8F8F8", "#33BFDB", "#77B255");

        } else if (item.getItemId() == R.string.set_colors_black_on_white) {
            setColors("#B7B7B7", "#ffffff", "", "#0F1419", "#0E7AC4", "#77B255");

        } else if (item.getItemId() == R.string.set_colors_white_on_darkgrey) {
            setColors("#B7B7B7", "#383838", "", "#F8F8F8", "#0E7AC4", "#77B255");

        } else if (item.getItemId() == R.string.set_colors_darkgrey_on_black) {
            setColors("#000000", "#0F1419", "#666666", "#7C858E", "#5C82A4", "#7A946A");

        } else if (item.getItemId() == R.string.get_short_url_length) {
            final String status = pref_twtr.getString("status_" + Integer.toString(index), "");
            if (isConnected(status)) {
                final SharedPreferences.Editor editor1 = pref_twtr.edit();
                try {
                    final int pref_short_url_length = adapter.getTwitter(index, false).getAPIConfiguration().getShortURLLength();
                    final String pref_short_url_length_string = Integer.toString(pref_short_url_length);
                    editor1.putString("pref_short_url_length", pref_short_url_length_string);
                    editor1.commit();
                    adapter.toast(getString(R.string.short_url_length) + ": " + pref_short_url_length_string);
                } catch (final TwitterException e) {
                    try {
                        adapter.twitterException(e);
                    } catch (Exception ex) {
                        if(adapter != null)
                            adapter.toast(getString(R.string.cannot_access_twitter));
                    }
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                    adapter.toast(getString(R.string.exception));
                }
            }

        } else if (item.getItemId() == R.string.copyright) {
            new Thread(new Runnable() {
                @Override
                public final void run() {
                    try {
                        final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
                        adapter.toast(getString(R.string.app_name_short) + ": " + getString(R.string.version) + packageInfo.versionName + " (" + packageInfo.versionCode + ")");
                    } catch (final NameNotFoundException e) {
                    }

                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                loadWebpage(ListAdapter.app_uri_about + "?id=" + StringUtil.join("_", ListAdapter.getPhoneIds()) + "&note=" + StringUtil.join("__", adapter.getOurScreenNames()));
                                webView1.requestFocus(View.FOCUS_DOWN);
                            }
                        });
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                }
            }).start();

        } else if (item.getItemId() == R.string.back) {
            finish();

        }
        return ret;
    }

    @Override
    protected final void onPause() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final int pref_show_datetime_interval = ListAdapter.getPrefInt(this, "pref_show_datetime_interval", "500");
        if (pref_show_datetime_interval > -1) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
        }

        if (mLocationManager != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    mLocationManager.removeUpdates(this);
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                }
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    mLocationManager.removeUpdates(this);
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                }
            }

            final boolean pref_enable_show_gpssetting_onexit = pref_app.getBoolean("pref_enable_show_gpssetting_onexit", true);
            if (pref_enable_show_gpssetting_onexit) {
                try {
                    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                } catch (final ActivityNotFoundException e) {
                    WriteLog.write(this, e);
                } catch (final Exception e) {
                    WriteLog.write(this, e);
                }
            }
        }

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (final Exception e) {
                WriteLog.write(this, e);
            }
        }

        super.onPause();
    }

    @Override
    public final void onProviderDisabled(final String provider) {
        adapter.toast("LocationProvider: Disabled");
    }

    @Override
    public final void onProviderEnabled(final String provider) {
        adapter.toast("LocationProvider: Enabled");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init_location();
            } else {
                Toast.makeText(this, getString(R.string.permission_does_not_granted), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected final void onResume() {
        super.onResume();

        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

        checkNetworkUtil.autoConnect(false);

        final boolean pref_enable_updatetweet_auto_inituser = pref_app.getBoolean("pref_enable_updatetweet_auto_inituser", false);
        if (pref_enable_updatetweet_auto_inituser) {

            if (adapter != null) {
                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (final InterruptedException e) {
                            WriteLog.write(UpdateTweet.this, e);
                        }

                        final IntentFilter filter = new IntentFilter();
                        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                        broadcastReceiver = new BroadcastReceiver() {
                            @Override
                            public final void onReceive(final Context context, final Intent intent) {
                                toast(context.getString(R.string.wifistate_changed));
                                try {
                                    init_user(adapter.checkIndexFromPrefTwtr());
                                } catch (final Exception e) {
                                }
                            }
                        };
                        registerReceiver(broadcastReceiver, filter);
                    }
                }).start();
            }
        }
    }

    // BugReport
    protected final void onStart() {
        super.onStart();
        CsUncaughtExceptionHandler.SendBugReport(this);
    }

    @Override
    public final void onStatusChanged(final String provider, final int status, final Bundle extras) {
        String statusString = "Unknown";
        if (status == LocationProvider.AVAILABLE) {
            statusString = "AVAILABLE";
        } else if (status == LocationProvider.OUT_OF_SERVICE) {
            statusString = "OUT OF SERVICE";
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            statusString = "TEMP UNAVAILABLE";
        }
        adapter.toast("LocationProvider: " + statusString);
    }

    private final boolean prefExport(final String backupPath) {
        final File file = new File(backupPath);
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(file));
            final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(this);
            output.writeObject(pref_app.getAll());
            res = true;
        } catch (final FileNotFoundException e) {
        } catch (final IOException e) {
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (final IOException ex) {
            }
        }
        return res;
    }

    private final boolean prefExport(final String prefXmlPath, final String backupPath) {
        final boolean result1 = prefExport(backupPath + ".bin");
        final boolean result2 = copyFile(prefXmlPath, backupPath);
        return result1 && result2;
    }

    private final boolean prefImport(final String backupPath) {
        final File file = new File(backupPath);
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor prefEdit = pref_app.edit();
            prefEdit.clear();
            @SuppressWarnings("unchecked")
            final Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                final Object v = entry.getValue();
                final String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (final FileNotFoundException e) {
        } catch (final IOException e) {
        } catch (final ClassNotFoundException e) {
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (final IOException ex) {
            }
        }
        return res;
    }

    private final void setColors(final String listviewBackgroundColor, final String backgroundColor, final String filterColor, final String fontColor, final String linkFontColor,
                                 final String retweetFontColor) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = pref_app.edit();

        editor.putString("pref_filter_item_inline_img", filterColor);
        editor.putString("pref_filter_item_usericon", filterColor);
        editor.putString("pref_filter_tl_headericon", filterColor);
        editor.putString("pref_header_bgcolor", backgroundColor);
        editor.putString("pref_header_fontcolor", fontColor);
        editor.putString("pref_listview_bgcolor", listviewBackgroundColor);
        editor.putString("pref_tl_bgcolor", backgroundColor);
        editor.putString("pref_tl_bgcolor_buttons", backgroundColor);
        editor.putString("pref_tl_bgcolor_updatetweet", backgroundColor);
        editor.putString("pref_tl_fontcolor_buttons", fontColor);
        editor.putString("pref_tl_fontcolor_createdat", fontColor);
        editor.putString("pref_tl_fontcolor_createdat_retweeted", retweetFontColor);
        editor.putString("pref_tl_fontcolor_createdat_retweetedby", fontColor);
        editor.putString("pref_tl_fontcolor_screenname", fontColor);
        editor.putString("pref_tl_fontcolor_screenname_retweeted", retweetFontColor);
        editor.putString("pref_tl_fontcolor_screenname_retweetedby", fontColor);
        editor.putString("pref_tl_fontcolor_source", fontColor);
        editor.putString("pref_tl_fontcolor_source_retweeted", retweetFontColor);
        editor.putString("pref_tl_fontcolor_source_retweetedby", fontColor);
        editor.putString("pref_tl_fontcolor_statustext", fontColor);
        editor.putString("pref_tl_fontcolor_statustext_hashtag", linkFontColor);
        editor.putString("pref_tl_fontcolor_statustext_location", linkFontColor);
        editor.putString("pref_tl_fontcolor_statustext_retweeted", retweetFontColor);
        editor.putString("pref_tl_fontcolor_statustext_screenname", linkFontColor);
        editor.putString("pref_tl_fontcolor_statustext_uri", linkFontColor);
        editor.putString("pref_tl_fontcolor_text_updatetweet", fontColor);
        editor.putString("pref_tl_fontcolor_text_updatetweet_over", "#ff0000");
        editor.putString("pref_tl_fontcolor_username", fontColor);
        editor.putString("pref_tl_fontcolor_username_retweeted", retweetFontColor);
        editor.putString("pref_userinfo_fontcolor_my_profile", fontColor);

        editor.commit();
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private final void sharePicture() {

        final String[] ITEM1 = getImagePathsStringArray();

        new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.pictureappended).setItems(ITEM1, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                sharePicture(which);
            }
        }).create().show();
    }

    private final void sharePicture(final int pos) {
        try {
            final Intent intent_viewphoto = new Intent();
            intent_viewphoto.setAction(android.content.Intent.ACTION_VIEW);
            intent_viewphoto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Uri imageUri = Uri.fromFile(imagePaths.get(pos));
            final String mimetype = "image/*";
            intent_viewphoto.setDataAndType(imageUri, mimetype);
            intent_viewphoto.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent_viewphoto.addCategory(Intent.CATEGORY_DEFAULT);

            startActivity(intent_viewphoto);
        } catch (final ActivityNotFoundException e) {
            WriteLog.write(this, e);
        } catch (final Exception e) {
            WriteLog.write(this, e);
        }
    }

    private final void pictureappend() {
        final int head = 3;
        final String[] ITEM1 = new String[head + 2 * imagePaths.size()];
        ITEM1[0] = getString(R.string.pictureappended_storage);
        ITEM1[1] = getString(R.string.pictureappended_camera);
        ITEM1[2] = getString(R.string.pictureappended_delete_exif);

        if (imagePaths.size() > 0) {
            WriteLog.write(UpdateTweet.this, "imagePaths.size(): " + imagePaths.size());

            int i = 0;
            collectionsUtil.removeDuplicate(imagePaths);
            for (final File imagePath : imagePaths) {
                if (imagePath != null) {
                    ITEM1[head + (2 * i)] = getString(R.string.share_picture) + ":" + imagePath.getAbsolutePath();
                    ITEM1[head + (2 * i) + 1] = getString(R.string.clear) + ":" + imagePath.getAbsolutePath();
                    i++;
                }
            }
        }

        new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.pictureappended).setItems(ITEM1, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                if (ITEM1[which].equals(getString(R.string.pictureappended_storage))) {
                    try {
                        final Intent intent_selectimage = new Intent();
                        intent_selectimage.setType("image/*");
                        intent_selectimage.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent_selectimage, REQUEST_GALLERY);
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                } else if (ITEM1[which].equals(getString(R.string.pictureappended_camera))) {
                    final String filename = getString(R.string.app_name_short) + System.currentTimeMillis() + ".jpg";
                    final ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        final Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (final ActivityNotFoundException e) {
                        WriteLog.write(UpdateTweet.this, e);
                    } catch (final Exception e) {
                        WriteLog.write(UpdateTweet.this, e);
                    }
                } else if (ITEM1[which].startsWith(getString(R.string.clear) + ":")) {
                    try {
                        imagePaths.remove((which - head) / 2);
                    } catch (Exception e) {
                    }
                    imageView1.setVisibility(View.GONE);
                } else if (ITEM1[which].startsWith(getString(R.string.share_picture) + ":")) {
                    sharePicture((which - head) / 2);
                } else if (ITEM1[which].equals(getString(R.string.pictureappended_delete_exif))) {
                    deleteExif();
                }
            }
        }).create().show();
    }

    private void removeElement(ArrayList<File> list, final File targetElement) {
        final Iterator<File> itr = list.iterator();
        while (itr.hasNext()) {
            final File element = (File) itr.next();
            if ((element.getAbsolutePath()).equals(targetElement.getAbsolutePath())) {
                itr.remove();
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.permission_does_not_granted), Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
        }
    }

    private final void reverse_geocoding() {
        new Thread(new Runnable() {
            @Override
            public final void run() {
                if ((editText4.getText().toString().equals("") == false) && (editText5.getText().toString().equals("") == false)) {
                    if (checkLocationinfoException(editText4.getText().toString(), editText5.getText().toString()) == false) {
                        final ArrayList<String> ITEM =
                                GeocodeUtil.reverseGeoCoding(UpdateTweet.this, Double.parseDouble(editText4.getText().toString()), Double.parseDouble(editText5.getText().toString()));
                        if (ITEM != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public final void run() {
                                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.telephone).setItems(ITEM.toArray(new String[ITEM.size()]), new DialogInterface.OnClickListener() {
                                        @Override
                                        public final void onClick(final DialogInterface dialog, final int which) {
                                            String str = ITEM.get(which);
                                            if (altitude > 0.0) {
                                                str += " " + Double.toString(altitude) + "m";
                                            }
                                            if (bearing > 0.0) {
                                                str += " " + Double.toString(bearing) + "deg";
                                            }
                                            if (speed > 0.0) {
                                                str += " " + Double.toString(speed) + "km/h";
                                            }

                                            if (str.equals("") == false) {
                                                editText2.setText(str);
                                            }
                                        }
                                    }).create().show();
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    private final void scan(final String[] scanFilePaths, final String[] mimeTypes) {
        try {
            WriteLog.write(UpdateTweet.this, "scan(" + scanFilePaths[0] + ", " + mimeTypes[0] + ")");
        } catch (final Exception e) {
            WriteLog.write(UpdateTweet.this, "scan(" + scanFilePaths[0] + ", NULL)");
        }
        try {
            MediaScannerConnection.scanFile(getApplicationContext(), scanFilePaths, mimeTypes, new OnScanCompletedListener() {
                @Override
                public final void onScanCompleted(final String path, final Uri uri) {
                    image_set(path);

                    adapter.toast(getString(R.string.done_mediascannerconnection_scan) + ": " + path);
                    WriteLog.write(UpdateTweet.this, "scan(" + scanFilePaths[0] + ") path: " + path + " uri:" + uri.toString());
                }
            });
        } catch (final Exception e) {
        }
    }

    private final void schedule_del(final int schedule_index) {

        final SharedPreferences.Editor editor = pref_twtr.edit();
        editor.remove("scheduledDateGetTimeInMillisString_" + schedule_index);
        editor.remove("schedule_text_" + schedule_index);
        editor.commit();

        adapter.toast(getString(R.string.done_del_schedule));
    }

    private final void schedule_reserve() {
        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

        final int clockOffsetInt = (int) (getNtpOffset() / 1000);

        final String[] ITEM2 = new String[ListAdapter.default_schedule_index_size];
        for (int i = 0; i < ListAdapter.default_schedule_index_size; i++) {
            String itemname = pref_twtr.getString("schedule_text_" + i, "");

            boolean isAfter = false;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm", Locale.JAPAN);
            if (itemname.equals("") == false) {
                try {
                    Date nowdate = new Date();
                    isAfter = nowdate.after(simpleDateFormat.parse(itemname));
                } catch (final ParseException e) {
                    isAfter = true;
                }
            } else {
                isAfter = true;
            }
            WriteLog.write(this, "schedule_reserve() isAfter[" + i + "]: " + isAfter);

            if (isAfter) {
                final SharedPreferences.Editor editor = pref_twtr.edit();
                editor.remove("scheduledDateGetTimeInMillisString_" + i);
                editor.remove("schedule_text_" + i);
                editor.commit();
                itemname = "";
            }

            if (itemname.equals("")) {
                ITEM2[i] = "  - ";
            } else {
                ITEM2[i] = itemname;
            }
        }
        new AlertDialog.Builder(this).setTitle(R.string.schedule).setItems(ITEM2, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                final int schedule_index = which;
                if (ITEM2[which].equals("  - ")) {
                    WriteLog.write(UpdateTweet.this, "schedule_reserve() (ITEM2[which].equals(\"  - \"))");

                    if (tweetstrlengthUi(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), true) > 0) {
                        final Date now = new Date();
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        final int now_y = cal.get(Calendar.YEAR);
                        final int now_m = cal.get(Calendar.MONTH);
                        final int now_d = cal.get(Calendar.DAY_OF_MONTH);
                        final int now_h = cal.get(Calendar.HOUR_OF_DAY);
                        final int now_i = cal.get(Calendar.MINUTE);
                        WriteLog.write(UpdateTweet.this, "schedule_reserve() (ITEM2[which].equals(\"  - \")) now_y/now_m/now_d now_h:now_i: " + now_y + "/" + now_m + "/" + now_d + " " + now_h + ":"
                                + now_i);

                        new DatePickerDialog(UpdateTweet.this, new DatePickerDialog.OnDateSetListener() {
                            boolean dpdFirst = true;

                            @Override
                            public final void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                if (dpdFirst) {
                                    dpdFirst = false;
                                    final int y = year;
                                    final int m = monthOfYear;
                                    final int d = dayOfMonth;
                                    WriteLog.write(UpdateTweet.this, "schedule_reserve() (ITEM2[which].equals(\"  - \")) y/m/d: " + y + "/" + m + "/" + d);
                                    new TimePickerDialog(UpdateTweet.this, new TimePickerDialog.OnTimeSetListener() {
                                        boolean tpdFirst = true;

                                        @Override
                                        public final void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
                                            if (tpdFirst) {
                                                tpdFirst = false;
                                                WriteLog.write(UpdateTweet.this, "schedule_reserve() (ITEM2[which].equals(\"  - \")) h:m:s: " + hourOfDay + ":" + minute + ":" + -1 * clockOffsetInt);
                                                final Calendar scheduledDate = Calendar.getInstance();
                                                scheduledDate.set(y, m, d, hourOfDay, minute, -1 * clockOffsetInt);
                                                schedule_set(scheduledDate, schedule_index);
                                            }
                                        }
                                    }, now_h, now_i + 1, true).show();
                                }
                            }
                        }, now_y, now_m, now_d).show();
                    }
                } else {
                    WriteLog.write(UpdateTweet.this, "schedule_reserve() (! ITEM2[which].equals(\"  - \"))");

                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.del_schedule).setMessage(R.string.confirm_del).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            schedule_del(schedule_index);
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                        }
                    }).create().show();
                }
            }
        }).create().show();
    }

    private final void schedule_set(final Calendar scheduledDate, final int schedule_index) {
        WriteLog.write(UpdateTweet.this, "schedule_set() scheduledDate: " + scheduledDate.toString() + " schedule_index: " + schedule_index);

        pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);

        String mode = "";
        if (mode_fav) {
            mode += "f";
        }
        if (mode_rt) {
            mode += "r";
        }
        if (mode_pak) {
            mode += "p";
        }
        if (mode_tweet || mode.equals("")) {
            mode += "t";
        }

        WriteLog.write(this, "mode: " + mode);
        WriteLog.write(this, "str1: " + editText1.getText().toString());
        WriteLog.write(this, "str2: " + editText2.getText().toString());
        WriteLog.write(this, "str3: " + editText3.getText().toString());
        WriteLog.write(this, "str4: " + editText4.getText().toString());
        WriteLog.write(this, "str5: " + editText5.getText().toString());

        final Intent intent = new Intent(this, AutoTweet.class);
        intent.setData(Uri.parse("http://shiobe/?" + String.valueOf(scheduledDate.getTimeInMillis())));
        intent.putExtra("mode", mode);
        intent.putExtra("str1", editText1.getText().toString());
        intent.putExtra("str2", editText2.getText().toString());
        intent.putExtra("str3", editText3.getText().toString());
        intent.putExtra("str4", editText4.getText().toString());
        intent.putExtra("str5", editText5.getText().toString());
        intent.putExtra("index", String.valueOf(adapter.checkIndexFromPrefTwtr()));
        intent.putExtra("schedule_index", String.valueOf(schedule_index));
        intent.putExtra("inReplyToStatusId", schedule_status_id);
        intent.putExtra("scheduledDateGetTimeInMillisString", Double.toString(scheduledDate.getTimeInMillis()));
        intent.putExtra("tweetImagePathString", getImagePathsString());

        final PendingIntent sender = PendingIntent.getBroadcast(UpdateTweet.this, schedule_index, intent, 0);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledDate.getTimeInMillis(), sender);

        adapter.toast(getString(R.string.done_set_schedule) + ": " + DateFormat.format("yyyy/MM/dd kk:mm", scheduledDate).toString() + System.getProperty("line.separator") + "[@"
                + adapter.checkScreennameFromIndex(adapter.checkIndexFromPrefTwtr()) + "]");

        final SharedPreferences.Editor editor = pref_twtr.edit();
        editor.putString("scheduledDateGetTimeInMillisString_" + schedule_index, Double.toString(scheduledDate.getTimeInMillis()));
        editor.putString("schedule_text_" + schedule_index, DateFormat.format("yyyy/MM/dd kk:mm", scheduledDate).toString());
        editor.commit();

        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        imageView1.setVisibility(View.GONE);
        webView1.setVisibility(View.GONE);
    }

    private final void schedule_type() {
        final String[] str_items = {getString(R.string.tweet), getString(R.string.fav), getString(R.string.rt), getString(R.string.pak)};
        final boolean[] flags = {mode_tweet, mode_fav, mode_rt, mode_pak};
        new AlertDialog.Builder(this).setTitle(R.string.schedule).setMultiChoiceItems(str_items, flags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                switch (which) {
                    case 0:
                        mode_tweet = isChecked;
                        break;
                    case 1:
                        mode_fav = isChecked;
                        break;
                    case 2:
                        mode_rt = isChecked;
                        break;
                    case 3:
                        mode_pak = isChecked;
                        break;
                }
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if ((mode_tweet == true) && (mode_fav == false) && (mode_rt == false) && (mode_pak == false)) {
                    schedule_reserve();
                } else {
                    final EditText editText = new EditText(UpdateTweet.this);
                    editText.setText(inReplyToStatusId);
                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.schedule).setView(editText).setMessage(R.string.status_id).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public final void onClick(final DialogInterface dialog, final int which) {
                            schedule_status_id = editText.getText().toString();
                            schedule_reserve();
                        }
                    }).create().show();
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    private final void setOrientation() {
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        switch (ListAdapter.getPrefInt(this, "pref_screen_orientation_tweet", "0")) {
            default:
                break;
            case 0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case 3:
                switch (getResources().getConfiguration().orientation) {
                    case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    default:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
                break;
        }
    }

    private final boolean setTextColorOnTextChanged() {
        if (tweetstrlengthUi(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), true) > 140) {
            if (pref_tl_fontcolor_text_updatetweet_over.equals("") == false) {
                try {
                    editText1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                    editText2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                    editText3.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                } catch (final IllegalArgumentException e) {
                }
            }
            if (pref_tl_fontcolor_text_updatetweet_button_tweet_over.equals("") == false) {
                try {
                    button1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_button_tweet_over));
                } catch (final IllegalArgumentException e) {
                }
            }

            return false;
        } else {
            if (pref_tl_fontcolor_text_updatetweet.equals("") == false) {
                try {
                    editText1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                    editText2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                    editText3.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
                } catch (final IllegalArgumentException e) {
                }
            }
            if (pref_tl_fontcolor_text_updatetweet_button_tweet.equals("") == false) {
                try {
                    button1.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_button_tweet));
                } catch (final IllegalArgumentException e) {
                }
            }

            return true;
        }
    }

    @Override
    public final boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            final String pref_hardkey_updatetweet_volumeup = pref_app.getString("pref_hardkey_updatetweet_volumeup", "0");
            if (pref_hardkey_updatetweet_volumeup.equals("0")) {
                tweet_button();
                //trueを返して音量減を無効化する
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            final String pref_hardkey_updatetweet_volumedown = pref_app.getString("pref_hardkey_updatetweet_volumedown", "0");
            if (pref_hardkey_updatetweet_volumedown.equals("0")) {
                tweet_button();
                //trueを返して音量減を無効化する
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getRepeatCount() > 0)
                mLongPressed = true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public final boolean onKeyUp(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLongPressed) {
                if (webView1.getVisibility() != View.GONE) {
                    if (webView1.canGoBack()) {
                        webView1.goBack();
                    } else {
                        webView1.setVisibility(View.GONE);
                    }
                } else {
                    finish();
                }
            } else {
                adapter.toast(getString(R.string.backbutton_singleclick));
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private final void share_place() {
        if ((editText4.getText().equals("") == false) && (editText5.getText().equals("") == false)) {
            try {
                pref_app = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
                int pref_map_zoom = ListAdapter.getPrefInt(this, "pref_map_zoom", "20");

                String geo_uri = "geo:" + editText4.getText() + "," + editText5.getText();
                if ((pref_map_zoom > 0) && (pref_map_zoom < 24)) {
                    geo_uri += "?z=" + pref_map_zoom;
                }

                final Uri uri = Uri.parse(geo_uri);
                final Intent intent_map = new Intent(Intent.ACTION_VIEW, uri);
                WriteLog.write(this, "share_place " + editText4.getText() + "," + editText5.getText());
                startActivity(intent_map);
            } catch (final ActivityNotFoundException e) {
                WriteLog.write(this, e);
            } catch (final Exception e) {
                WriteLog.write(this, e);
            }
        }
    }

    private final void simpleauth() {
        // Password処理
        pref_app = PreferenceManager.getDefaultSharedPreferences(this);
        final String pref_appPassword = pref_app.getString("pref_appPassword", "");
        if (pref_appPassword.equals("") == false) {
            WriteLog.write(this, "(pref_appPassword.equals(\"\") == false)");
            final EditText editView = new EditText(UpdateTweet.this);
            new AlertDialog.Builder(UpdateTweet.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.enter_password).setView(editView).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int whichButton) {
                    if (editView.getText().toString().equals(pref_appPassword) == false) {
                        WriteLog.write(UpdateTweet.this, getString(R.string.wrong_password) + ": " + editView.getText().toString());
                        finish();
                    }
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int whichButton) {
                    WriteLog.write(UpdateTweet.this, getString(R.string.cancelled));
                    finish();
                }
            }).show();
        }
    }

    private final void tky2jgd() {
        final double latJ = Double.parseDouble(editText4.getText().toString());
        final double lngJ = Double.parseDouble(editText5.getText().toString());
        final double latW = (latJ - (latJ * 0.00010695)) + (lngJ * 0.000017464) + 0.0046017;
        final double lngW = (lngJ - (latJ * 0.000046038) - (lngJ * 0.000083043)) + 0.010040;

        final DecimalFormat decimalFormat1 = new DecimalFormat("#.000000");
        final String latW2 = decimalFormat1.format(latW);
        final String lngW2 = decimalFormat1.format(lngW);

        editText4.setText(latW2);
        editText5.setText(lngW2);
    }

    private final void toast(final String text) {
        if (!isFinishing()) {
            if (currentThreadIsUiThread()) {
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        Toast.makeText(UpdateTweet.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private final void tweet(final String indexStr, final String str1, final String str2, final String str3, final String str4, final String str5) throws TwitterException {

        pref_app = PreferenceManager.getDefaultSharedPreferences(this);

        final Intent intent2 = new Intent();
        intent2.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet");
        intent2.setAction(Intent.ACTION_VIEW);
        intent2.putExtra("str1", str1);
        intent2.putExtra("str2", str2);
        intent2.putExtra("str3", str3);
        intent2.putExtra("str4", str4);
        intent2.putExtra("str5", str5);
        intent2.putExtra("inReplyToStatusId", inReplyToStatusId);
        intent2.putExtra("tweetImagePathString", getImagePathsString());
        intent2.putExtra("skip", "1");

        final PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, new Intent(this, jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet.class), 0);
        final PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, 0);

        new Thread(new Runnable() {
            @Override
            public final void run() {
                final String tweetstrOriginal = StringUtil.getTweetString(adapter.getTweetHeader(pref_app, str1), str2, adapter.getTweetfooter(pref_app, str3));
                String tweetstr = tweetstrOriginal;
                if ((tweetstrOriginal.equals("")) && (imagePaths == null)) {
                    adapter.toast(getString(R.string.empty_tweettext));
                    return;
                }

                clearForm(pendingIntent2);

                int index;
                try {
                    index = Integer.parseInt(indexStr);
                } catch (final NumberFormatException e) {
                    index = adapter.checkIndexFromPrefTwtr();
                }
                if (index <= -1) {
                    index = adapter.checkIndexFromPrefTwtr();
                }

                init_user_oauth(index);
                WriteLog.write(UpdateTweet.this, "tweet() userinit_oauth(" + indexStr + ")");

                final long pref_notification_duration_done_tweet =
                        ListAdapter.getPrefInt(UpdateTweet.this, "pref_notification_duration_done_tweet", Integer.toString(ListAdapter.default_notification_duration_done_tweet));

                final boolean pref_enable_notification_done_tweet = pref_app.getBoolean("pref_enable_notification_done_tweet", true);
                final boolean pref_enable_notification_vibration_done_tweet = pref_app.getBoolean("pref_enable_notification_vibration_done_tweet", false);
                final boolean pref_enable_notification_led_done_tweet = pref_app.getBoolean("pref_enable_notification_led_done_tweet", false);
                final boolean pref_enable_notification_twitterexception = pref_app.getBoolean("pref_enable_notification_twitterexception", true);
                final boolean pref_enable_notification_vibration_twitterexception = pref_app.getBoolean("pref_enable_notification_vibration_twitterexception", false);
                final boolean pref_enable_notification_led_twitterexception = pref_app.getBoolean("pref_enable_notification_led_twitterexception", false);
                final int pref_notification_led_color_done_tweet = adapter.getPrefColor("pref_notification_led_color_done_tweet", "#0000ff", ListAdapter.default_notification_led_color_done_tweet);
                final int pref_notification_led_color_twitterexception =
                        adapter.getPrefColor("pref_notification_led_color_twitterexception", "#ffff00", ListAdapter.default_notification_led_color_twitterexception);

                final String screenName = adapter.checkScreennameFromIndex(index);

                final StatusUpdate statusUpdate = new StatusUpdate(tweetstrOriginal);

                String toast_message = "";

                boolean isPictureUploadedToTwitter = false;
                ArrayList<Long> mediaIds = new ArrayList<Long>();
                if (imagePaths != null) {
                    WriteLog.write(UpdateTweet.this, "tweet() (imagePaths != null)");

                    WriteLog.write(UpdateTweet.this, "tweet() imagePaths.size(): " + imagePaths.size());
                    if (imagePaths.size() > 1) {
                        WriteLog.write(UpdateTweet.this, "tweet() imagePaths.size() > 1");
                        for (final File imagePath : imagePaths) {
                            WriteLog.write(UpdateTweet.this, "tweet() imagePaths.size() > 1 imagePath.getAbsolutePath(): " + imagePath.getAbsolutePath());
                            if (imagePath != null) {
                                WriteLog.write(UpdateTweet.this, "tweet() (imagePath != null)");
                                try {
                                    WriteLog.write(UpdateTweet.this, "tweet() try");
                                    UploadedMedia media = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).uploadMedia(imagePath);
                                    mediaIds.add(media.getMediaId());

                                    tweetstr += " " + imagePath.getAbsolutePath();
                                } catch (final TwitterException e) {
                                    WriteLog.write(UpdateTweet.this, e);
                                } catch (final Exception e) {
                                    WriteLog.write(UpdateTweet.this, e);
                                }
                            }
                            WriteLog.write(UpdateTweet.this, "(tweetImagePath != null) tweetstr: " + tweetstr + " tweetstrOriginal: " + tweetstrOriginal);
                            toast_message += System.getProperty("line.separator") + getString(R.string.done_pictureupload) + ": " + imagePath.getAbsolutePath();
                        }
                    } else if (imagePaths.size() == 1) {
                        if (imagePaths.get(0) != null) {
                            WriteLog.write(UpdateTweet.this, "(tweetImagePath != null)");
                            try {
                                WriteLog.write(UpdateTweet.this, "tweet() try");
                                final ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance();
                                pref_pictureUploadSite = pref_app.getString("pref_pictureuploadsite", MediaProvider.TWITTER.toString());
                                final boolean pref_pictureUploadSiteIsTwitter = pref_pictureUploadSite.equals(MediaProvider.TWITTER.toString());
                                if (pref_pictureUploadSiteIsTwitter) {
                                    tweetstr = imageUpload.upload(imagePaths.get(0), tweetstrOriginal);
                                    isPictureUploadedToTwitter = true;
                                } else {
                                    tweetstr += " " + imageUpload.upload(imagePaths.get(0), tweetstrOriginal);
                                }
                                WriteLog.write(UpdateTweet.this, "(tweetImagePath != null) tweetstr: " + tweetstr + " tweetstrOriginal: " + tweetstrOriginal);
                                toast_message += System.getProperty("line.separator") + getString(R.string.failure_pictureupload) + ": " + imagePaths.get(0).getAbsolutePath();
                            } catch (final TwitterException e) {
                                WriteLog.write(UpdateTweet.this, e);
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        }
                    }
                }

                imagePaths.clear();

                if (inReplyToStatusId.equals("") == false) {
                    WriteLog.write(UpdateTweet.this, "inReplyToStatusId: " + inReplyToStatusId);
                    statusUpdate.setInReplyToStatusId(Long.parseLong(inReplyToStatusId));
                }

                if ((str4.length() > 0) && (str5.length() > 0)) {
                    // 位置情報
                    if (checkLocationinfoException(str4, str5) == false) {
                        statusUpdate.location(new GeoLocation(Double.parseDouble(str4), Double.parseDouble(str5)));
                        WriteLog.write(UpdateTweet.this, "tweet() statusUpdate.location() :" + str4 + ", " + str5);
                        toast_message += System.getProperty("line.separator") + getString(R.string.placeinfo) + ": " + str4 + "," + str5;
                    } else {
                        toast_message += System.getProperty("line.separator") + getString(R.string.placeinfo) + ": " + getString(R.string.locationinfo_exception);
                    }
                }
                Status updatedstatus = null;
                try {
                    if (mediaIds.size() > 0) {
                        WriteLog.write(UpdateTweet.this, "tweet() (mediaIds.size() > 0)");

                        long[] mediaIdsArray = new long[mediaIds.size()];
                        int i = 0;
                        for (long id : mediaIds) {
                            mediaIdsArray[i++] = id;
                        }
                        statusUpdate.setMediaIds(mediaIdsArray);
                        WriteLog.write(UpdateTweet.this, "tweet() (mediaIds.size() > 0) statusUpdate.setMediaIds(mediaIdsArray)");
                    }

                    if ((isPictureUploadedToTwitter == false) || (mediaIds.size() > 0)) {
                        updatedstatus = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).updateStatus(statusUpdate);
                        WriteLog.write(UpdateTweet.this, "twitter.updateStatus()");
                        WriteLog.write(UpdateTweet.this, updatedstatus);
                    }

                    final String finalTweetstr = tweetstr;
                    final String finalToast_message = toast_message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            adapter.cancelNotification(NOTIFY_DOING_SEND);

                            if (pref_enable_notification_done_tweet) {
                                adapter.notification(NOTIFY_DONE_TWEET, R.drawable.done, getString(R.string.done_tweet), finalTweetstr + System.getProperty("line.separator") + finalToast_message
                                        + System.getProperty("line.separator") + "[@" + screenName + "]", getString(R.string.app_name), true, false, pref_enable_notification_led_done_tweet
                                        ? pref_notification_led_color_done_tweet : Color.TRANSPARENT, pref_enable_notification_vibration_done_tweet, pendingIntent1, true);

                                new Thread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        try {
                                            Thread.sleep(pref_notification_duration_done_tweet);
                                        } catch (InterruptedException e) {
                                            WriteLog.write(UpdateTweet.this, e);
                                        }
                                        adapter.cancelNotification(NOTIFY_DONE_TWEET);
                                    }
                                }).start();
                            }
                        }
                    });
                } catch (final TwitterException e) {
                    updatedstatus = null;
                    if (e.exceededRateLimitation()) {
                        WriteLog.write(UpdateTweet.this, e);
                        try {
                            if (isPictureUploadedToTwitter == false) {
                                updatedstatus = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(1), false).updateStatus(statusUpdate);
                                WriteLog.write(UpdateTweet.this, "twitter.updateStatus()");
                                WriteLog.write(UpdateTweet.this, updatedstatus);
                            }

                            final String finalTweetstr = tweetstr;
                            final String finalToast_message = toast_message;
                            runOnUiThread(new Runnable() {
                                @Override
                                public final void run() {
                                    adapter.cancelNotification(NOTIFY_DOING_SEND);

                                    if (pref_enable_notification_done_tweet) {
                                        adapter.notification(NOTIFY_DONE_TWEET, R.drawable.done, getString(R.string.done_tweet), finalTweetstr + System.getProperty("line.separator")
                                                + finalToast_message + System.getProperty("line.separator") + "[@" + screenName + "]", getString(R.string.app_name), true, false, pref_enable_notification_led_done_tweet
                                                ? pref_notification_led_color_done_tweet : Color.TRANSPARENT, pref_enable_notification_vibration_done_tweet, pendingIntent1, true);

                                        new Thread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                try {
                                                    Thread.sleep(pref_notification_duration_done_tweet);
                                                } catch (InterruptedException e) {
                                                    WriteLog.write(UpdateTweet.this, e);
                                                }
                                                adapter.cancelNotification(NOTIFY_DONE_TWEET);
                                            }
                                        }).start();
                                    }
                                }
                            });

                        } catch (final TwitterException e1) {
                            WriteLog.write(UpdateTweet.this, e1);
                            updatedstatus = null;
                            runOnUiThread(new Runnable() {
                                @Override
                                public final void run() {
                                    adapter.cancelNotification(NOTIFY_DOING_SEND);

                                    if (pref_enable_notification_twitterexception) {
                                        adapter.notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e1.getErrorMessage() + System.getProperty("line.separator") + "[@"
                                                + screenName + "]", getString(R.string.app_name), false, false, pref_enable_notification_led_twitterexception
                                                ? pref_notification_led_color_twitterexception : Color.TRANSPARENT, pref_enable_notification_vibration_twitterexception, pendingIntent2, true);
                                    }
                                }
                            });
                        }
                    } else {
                        WriteLog.write(UpdateTweet.this, e);
                        updatedstatus = null;
                        runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                adapter.cancelNotification(NOTIFY_DOING_SEND);

                                if (pref_enable_notification_twitterexception) {
                                    adapter.notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getErrorMessage() + System.getProperty("line.separator") + "[@"
                                            + screenName + "]", getString(R.string.app_name), false, false, pref_enable_notification_led_twitterexception
                                            ? pref_notification_led_color_twitterexception : Color.TRANSPARENT, pref_enable_notification_vibration_twitterexception, pendingIntent2, true);
                                }
                            }
                        });
                    }
                }
                if (updatedstatus != null) {
                    WriteLog.write(UpdateTweet.this, "(updatedstatus != null)");

                    final boolean pref_enable_ringtone_ontweet = pref_app.getBoolean("pref_enable_ringtone_ontweet", true);
                    final String pref_ringtone_ontweet_updatetweet = pref_app.getString("pref_ringtone_ontweet_updatetweet", "");
                    if (pref_enable_ringtone_ontweet && (pref_ringtone_ontweet_updatetweet != null) && (pref_ringtone_ontweet_updatetweet.equals("") == false)) {
                        final MediaPlayer mediaPlayer = MediaPlayer.create(UpdateTweet.this, Uri.parse(pref_ringtone_ontweet_updatetweet));
                        mediaPlayer.setLooping(false);
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }

                    final Status justbeforeTweet = updatedstatus;
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            textView2.setText(justbeforeTweet.getText() + " " + adapter.DFu.format(justbeforeTweet.getCreatedAt()));
                        }
                    });

                    final boolean pref_enable_Kiriban = pref_app.getBoolean("pref_enable_Kiriban", false);
                    if (pref_enable_Kiriban == true) {
                        WriteLog.write(UpdateTweet.this, "(pref_enable_Kiriban == true)");
                        // 100,1000,10000,20000,30000,...
                        final String statusCount = String.valueOf(updatedstatus.getUser().getStatusesCount());
                        final String nextStatusCount = String.valueOf(updatedstatus.getUser().getStatusesCount() + 1);
                        Pattern p_statuscount;
                        try {
                            p_statuscount = Pattern.compile("^(99+)|([0-8]+9999)$", Pattern.DOTALL);
                        } catch (final PatternSyntaxException e) {
                            p_statuscount = null;
                            WriteLog.write(UpdateTweet.this, e);
                        }
                        final Matcher matcher_statuscount = p_statuscount.matcher(statusCount);
                        if (matcher_statuscount.find()) {
                            WriteLog.write(UpdateTweet.this, "(matcher_statuscount.find())");
                            final StatusUpdate statusUpdate2 = new StatusUpdate(nextStatusCount + getString(R.string.kiriban_1));
                            try {
                                updatedstatus = adapter.getTwitter(adapter.checkIndexFromPrefTwtr(), false).updateStatus(statusUpdate2);
                                WriteLog.write(UpdateTweet.this, "twitter.updateStatus()");
                            } catch (final TwitterException e) {
                                adapter.twitterException(e);
                                WriteLog.write(UpdateTweet.this, e);
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                                adapter.toast(getString(R.string.exception));
                            }
                        }
                    }
                } else {
                    final Status justbeforeTweet = adapter.getjustbefore(adapter.checkIndexFromPrefTwtr());
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            textView2.setText(justbeforeTweet.getText() + " " + adapter.DFu.format(justbeforeTweet.getCreatedAt()));
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        adapter.cancelNotification(NOTIFY_DOING_SEND);
                    }
                });
            }
        }).start();
        return;
    }

    private final void tweet_button() {
        try {
            pref_twtr = getSharedPreferences("Twitter_setting", MODE_PRIVATE);
            final int index = adapter.checkIndexFromPrefTwtr();

            WriteLog.write(UpdateTweet.this, "imagePaths.size(): " + imagePaths.size());
            tweet(Integer.toString(index), editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), editText4.getText().toString(), editText5.getText().toString());
            setTextColorOnTextChanged();
        } catch (final TwitterException e) {
            WriteLog.write(UpdateTweet.this, e);
        }
    }

    private final int tweetstrlengthUi(final String str1, final String str2, final String str3, final boolean ui) {
        final String str = StringUtil.getTweetString(str1, str2, str3);
        final int strlength = ListAdapter.getStringLength(str);

        if (strlength > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    button1.setText(Integer.toString(strlength));
                    editText1.setHint("");
                    editText2.setHint("");
                    editText3.setHint("");
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    button1.setText("0");
                    editText1.setHint(R.string.prefix);
                    editText2.setHint(R.string.message);
                    editText3.setHint(R.string.suffix);
                }
            });
        }
        return strlength;
    }

    private final void webview_operation() {
        final String[] ITEM1 = new String[6];
        ITEM1[0] = getString(R.string.webview_operation_back_or_gone);
        ITEM1[1] = getString(R.string.webview_operation_next);
        ITEM1[2] = getString(R.string.webview_operation_url);
        ITEM1[3] = getString(R.string.webview_operation_get_title);
        ITEM1[4] = getString(R.string.pictureappended_thumbnail);
        if (webView1.getVisibility() == View.GONE) {
            ITEM1[5] = getString(R.string.webview_operation_visible);
        } else {
            ITEM1[5] = getString(R.string.webview_operation_gone);
        }
        new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.webview_operation).setItems(ITEM1, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                if (ITEM1[which].equals(getString(R.string.webview_operation_back_or_gone))) {
                    if (webView1.canGoBack()) {
                        webView1.goBack();
                    } else {
                        webView1.setVisibility(View.GONE);
                    }
                } else if (ITEM1[which].equals(getString(R.string.webview_operation_next))) {
                    if (webView1.canGoForward()) {
                        webView1.goForward();
                    }
                } else if (ITEM1[which].equals(getString(R.string.webview_operation_url))) {
                    final EditText editText = new EditText(UpdateTweet.this);
                    if (webView1.getUrl().equals("")) {
                        editText.setText(getString(R.string.http));
                    } else {
                        editText.setText(webView1.getUrl());
                    }
                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.webview_operation_url).setView(editText).setMessage(R.string.enter_url).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public final void onClick(final DialogInterface dialog, final int which) {
                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public final void run() {
                                            loadWebpage(editText.getText().toString());
                                            webView1.requestFocus(View.FOCUS_DOWN);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).create().show();
                } else if (ITEM1[which].equals(getString(R.string.webview_operation_get_title))) {
                    if (webView1.getVisibility() == View.VISIBLE) {
                        if (webView1.getUrl().equals("") == false) {
                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    try {
                                        final String webpagetitle = get_webpagetitle(webView1);
                                        if (webpagetitle.equals("") == false) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public final void run() {
                                                    editText2.setText(StringUtil.getTweetString(webpagetitle, editText2.getText().toString()));

                                                    try {
                                                        dismissDialog(R.string.get_webpage_title);
                                                    } catch (final IllegalArgumentException e) {
                                                    }
                                                }
                                            });
                                        }
                                    } catch (final Exception e) {
                                        WriteLog.write(UpdateTweet.this, e);
                                    }
                                }
                            }).start();
                        }
                    }
                } else if (ITEM1[which].equals(getString(R.string.webview_operation_visible))) {
                    webView1.setVisibility(View.VISIBLE);
                } else if (ITEM1[which].equals(getString(R.string.webview_operation_gone))) {
                    webView1.setVisibility(View.GONE);
                } else if (ITEM1[which].equals(getString(R.string.pictureappended_thumbnail))) {
                    new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.pictureappended_thumbnail).setPositiveButton(R.string.pictureappended_thumbnail_new, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            final Matcher matcher_thumbnail =
                                    ListAdapter.pattern_urlHttpHttps.matcher(StringUtil.getTweetString(editText1.getText().toString(), editText2.getText().toString(), editText3.getText().toString()));
                            final ArrayList<String> ITEM11 = new ArrayList<String>(7);
                            String pregroup_thumbnail = "";
                            while (matcher_thumbnail.find()) {
                                final String group = matcher_thumbnail.group(0);
                                if (group.equals(pregroup_thumbnail) == false) {
                                    pregroup_thumbnail = group;
                                    ITEM11.add(group);
                                }
                            }
                            try {
                                new AlertDialog.Builder(UpdateTweet.this).setTitle(R.string.pictureappended).setItems(ITEM11.toArray(new String[ITEM11.size()]), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, final int which) {
                                        adapter.toast(getString(R.string.doing));
                                        flagCaptureThumbnail = 0;
                                        new Thread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                get_webpagethumbnail(ITEM11.get(which).toString(), true);
                                            }
                                        }).start();
                                    }
                                }).create().show();
                            } catch (final ArrayStoreException e) {
                                WriteLog.write(UpdateTweet.this, e);
                            } catch (final Exception e) {
                                WriteLog.write(UpdateTweet.this, e);
                            }
                        }
                    }).setNeutralButton(R.string.pictureappended_thumbnail_preuri, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            if (preCapturedUri.equals("")) {
                                adapter.toast(getString(R.string.pictureappended_thumbnail_preuri_is_null));
                            } else {
                                adapter.toast(getString(R.string.doing));
                                flagCaptureThumbnail = 0;
                                new Thread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        get_webpagethumbnail(preCapturedUri, false);
                                    }
                                }).start();
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                        }
                    }).create().show();
                }
            }
        }).create().show();

    }
}

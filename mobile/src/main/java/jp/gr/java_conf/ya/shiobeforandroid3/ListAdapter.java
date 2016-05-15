package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.twitter.Extractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.java_conf.ya.shiobeforandroid3.util.BinarySearchUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.BitmapLruCache;
import jp.gr.java_conf.ya.shiobeforandroid3.util.CheckNetworkUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.CollectionsUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.FontUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HistoryStack;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HtmlEscape;
import jp.gr.java_conf.ya.shiobeforandroid3.util.HttpsClient;
import jp.gr.java_conf.ya.shiobeforandroid3.util.ImageGetter2;
import jp.gr.java_conf.ya.shiobeforandroid3.util.ImageGetter3;
import jp.gr.java_conf.ya.shiobeforandroid3.util.ListNameComparator;
import jp.gr.java_conf.ya.shiobeforandroid3.util.MyCrypt;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.TlViewLayoutUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.UrlUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.ViewHolderStatus;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserMentionEntity;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.MediaProvider;

// リストアダプタの生成
public class ListAdapter extends BaseAdapter {

    // 依存なし

    private ArrayList<String> sortedOurslistName;

    private BitmapLruCache bitmapLruCache = new BitmapLruCache();

    public static final boolean default_capture_thumbnail_use_picturelistener = false;
    public static final boolean default_enable_httpurlconnection_follow_redirects = false;
    public static final boolean default_enable_htmlescape = true;
    private boolean isFix;
    private boolean pref_check_ratelimit_allacounts;
    private boolean pref_check_ratelimit_detail = true;
    private boolean pref_confirmdialog_fav = true;
    private boolean pref_confirmdialog_pak = true;
    private boolean pref_confirmdialog_rt = true;
    private boolean pref_confirmdialog_userrt = true;
    private boolean pref_enable_confirmdialog_hidden_allaccount = true;
    private boolean pref_enable_expand_uri_fullurl;
    private boolean pref_enable_expand_uri_thirdparty_mobile;
    private boolean pref_enable_expand_uri_thirdparty_tweetmenu_mobile = true;
    private boolean pref_enable_expand_uri_thirdparty_tweetmenu_wifi = true;
    private boolean pref_enable_expand_uri_thirdparty_wifi;
    private boolean pref_enable_expand_uri_twitter_mobile = true;
    private boolean pref_enable_expand_uri_twitter_tweetmenu_mobile = true;
    private boolean pref_enable_expand_uri_twitter_tweetmenu_wifi = true;
    private boolean pref_enable_expand_uri_twitter_wifi = true;
    private boolean pref_enable_htmlescape = true;
    private boolean pref_enable_inline_img_async;
    private boolean pref_enable_inline_img_volley = false;
    private boolean pref_enable_inline_img_volley_cancel = true;
    private boolean pref_enable_inline_img_mobile = false;
    private boolean pref_enable_inline_img_wifi = true;
    private boolean pref_enable_log_statuses;
    private boolean pref_enable_singleline;
    private boolean pref_enable_smoothscroll_load;
    private boolean pref_enable_smoothscroll_search;
    private boolean pref_enable_tl_speedy;
    private boolean pref_enable_tweetmenu_check_favednum = true;
    private boolean pref_enable_tweetmenu_check_retweetednum = true;
    private boolean pref_enable_tweetmenu_check_tweet = true;
    private boolean pref_fav_at_the_same_time_pak = true;
    private boolean pref_hide_item_myicon;
    private boolean pref_hide_item_myname;
    private boolean pref_hide_item_usericon;
    private boolean pref_hide_link_createdat;
    private boolean pref_hide_link_screenname;
    private boolean pref_hide_link_source;
    private boolean pref_hide_tl_headericon;
    private boolean pref_hide_tweet_footer_action;
    private boolean pref_retweet_at_the_same_time_pak;
    private boolean pref_sendintent_text_expanduri = true;
    private boolean pref_setselection_requestfocus;
    private boolean pref_setselection_requestfocusfromtouch;
    private boolean pref_setselection_setfocusable;
    private boolean pref_setselection_setfocusableintouchmode;
    private boolean pref_setselection_setitemchecked;
    private boolean pref_setselection_setselected;
    private boolean pref_setselection_triple;
    private boolean pref_show_profilebannerimage;
    private boolean pref_tl_fontsize_large_screenname = true;
    private boolean pref_tl_fontsize_large_url = true;
    private boolean pref_tl_fontsize_small_action = true;
    private boolean pref_tl_fontsize_small_createdat = true;
    private boolean pref_tl_fontsize_small_old_retweet = true;
    private boolean pref_tl_fontsize_small_source = true;
    private boolean pref_tl_fontsize_small_username = true;
    private boolean pref_tl_load_direction = false;
    private boolean pref_userinfo_show_my_profile = true;
    private boolean preincludedOurs_sortedUserlist;

    private CheckNetworkUtil checkNetworkUtil;

    private final CollectionsUtil collectionsUtil = new CollectionsUtil();

    private Configuration conf;

    private ConfigurationBuilder confbuilder;

    private Context context;
    private static Context staticContext;

    private float dpi = 1.0f, pref_tl_fontsize = 14.0f, pref_tl_imagesize = 56.0f;

    private final FontUtil fontUtil = new FontUtil();

    private final HistoryStack<String> uriStringHistory = new HistoryStack<String>();

    private ImageLoader mImageLoader;

    static final int default_pref_tl_interval = 300;
    static final int default_capture_thumbnail_retry = 4;
    static final int default_draft_index_size = 4;
    static final int default_notification_duration_done_tweet = 5000;
    static final int default_item_num = 2;
    static final int default_locationinfo_mintime = 10000;
    static final int default_notification_led_color_doing_tweet = Color.parseColor("#00ffff");
    static final int default_notification_led_color_done_tweet = Color.parseColor("#0000ff");
    static final int default_notification_led_color_twitterexception = Color.parseColor("#ffff00");
    static final int default_schedule_index_size = 4;
    static final int default_search_unread_mylastfav_timeout = 300;
    static final int default_search_unread_mylasttweet_timeout = 300;
    static final int default_search_unread_mylastfav_count = 20;
    static final int default_search_unread_mylasttweet_count = 20;
    static final int default_short_url_length = 22;
    public static final int default_timeout_connection = 60000;
    static final int default_timeout_ntp_server = 60000;
    public static final int default_timeout_so = 60000;
    public static final int default_user_index_size = 4;
    private int index_pre = -1;
    private int index_pre_s = -1;
    static final int NOTIFY_RUNNING = 0;
    //	private static final int NOTIFY_RUNNING = 0;
    private static final int NOTIFY_DOING_SEND = 1;
    private static final int NOTIFY_DONE_TWEET = 2;
    //	private static final int NOTIFY_EXCEPTION = 3;
    private static final int NOTIFY_TWITTER_EXCEPTION = 4;
    static final int NOTIFY_DONE_ACTION = 8;
    static final int NOTIFY_OPEN_UPDATETWEET = 16;
    private int padding = 1;
    private static final int PLAY = 3; // "車輪の遊び"の遊び
    private int post_max = 127;
    private int pref_fav_site;
    private int pref_enable_expand_uri_string_length = 30;
    private int pref_map_zoom = 20;
    private int pref_mute_time = 600;
    private static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private int pref_search_unread_mylastfav_timeout = 300;
    private int pref_search_unread_mylasttweet_timeout = 300;
    private int pref_search_unread_mylastfav_count = 20;
    private int pref_search_unread_mylasttweet_count = 20;
    static int pref_short_url_length = 22;
    private int pref_timeout_connection;
    private int pref_timeout_so;
    private int pref_timeout_connection_imgtag;
    private int pref_timeout_so_imgtag;
    private int pref_timeout_t4j_connection;
    private int pref_timeout_t4j_read;
    private int pref_tl_iconsize1 = 56, pref_tl_iconsize2 = 28;
    private int pref_tl_retweetedby_multiline = 3;
    private int pref_user_index_offset, pref_user_index_size = default_user_index_size;
    private static int rc = 0;
    private int select_pos;
    private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    private LightingColorFilter lightingColorFilterItemInlineimg;
    private LightingColorFilter lightingColorFilterItemUsericon;
    private LightingColorFilter lightingColorFilterTlHeadericon;

    private List<Status> tweets;

    private ListView listView;

    public static final Locale LOCALE = Locale.JAPAN; // Locale.getDefault();

    private long ntpOffset = 0;

    private final Map<String, String> expandedUris = new HashMap<String, String>();

    private MovementMethod movementmethod;

    private NotificationCompat.Action action;

    static final Pattern pattern_screenname = Pattern.compile("^\\s*@?([a-zA-Z0-9_-]{1,30})\\s*$", Pattern.CASE_INSENSITIVE);

    private PendingIntent pendingIntent;

    private ResponseList<UserList> sortedOurslist;
    private ResponseList<UserList> sortedUserlist;

    private SharedPreferences pref_app;
    private static SharedPreferences staticPref_app;
    private SharedPreferences pref_twtr;

    private static final String API_STATUS_URL = "https://status.io.watchmouse.com/7617";
    static final String app_uri_about = "http://www5.pf-x.net/~shiobe/streaming/shiobeforandroid.php";
    static final String app_uri_local = "file:///android_asset/index.html";
    public static final String BR = "<br />" + System.getProperty("line.separator");
    private String crpKey = "";
    public static final String EXTRA_VOICE_REPLY = "EXTRA_VOICE_REPLY";
    static final String default_background_color = "#000000";
    public static final String default_charset = "UTF-8";
    static final String default_confirmdialog_checkeditems = "";
    public static final String default_cooperation_url = "http://www5.pf-x.net/~shiobe/streaming/";
    static final String default_filter_color = "";
    static final String default_font_color = "#ffffff";
    static final String default_link_font_color = "#0000ff";
    static final String default_listview_background_color = "#333333";
    static final String default_locationinfo_exception_lat = "35.622581";
    public static final String default_locationinfo_exception_lng = "140.103279";
    public static final String default_locationinfo_exception_radius = "1000.0";
    static final String default_notification_duration_done_tweet_string = "5000";
    static final String default_notification_led_color_done_tweet_string = "#0000ff";
    static final String default_ntp_server = "ntp.jst.mfeed.ad.jp";
    static final String default_pictureUploadSite = "TWITTER";
    static final String default_retweet_font_color = "#00ff00";
    static final String default_search_unread_mylastfav_count_string = "20";
    static final String default_search_unread_mylastfav_timeout_string = "300";
    static final String default_search_unread_mylasttweet_count_string = "20";
    static final String default_search_unread_mylasttweet_timeout_string = "300";
    static final String default_short_url_length_string = "22";
    public static final String default_timeout_connection_string = "60000";
    static final String default_timeout_ntp_server_string = "60000";
    public static final String default_timeout_so_string = "60000";
    static final String default_twitpicKey = "4d591f0d449888af9d33687a4db9adee";
    static final String default_update_check_url = "http://192.168.11.3/s4a/";
    private String fontcolor_createdat = "";
    private String fontcolor_screenname = "";
    private String fontcolor_source = "";
    private String fontcolor_statustext = "";
    private String fontcolor_username = "";
    static final String GSI_MAP_URL_FORMAT = "http://cyberjapandata.gsi.go.jp/xyz/std/%d/%d/%d.png";
    private String imgSizeTagpart = " height=\"11\"";
    private String maxId_key = "";
    static final String NL = System.getProperty("line.separator");
    private String oauthToken = "";
    private String oauthTokenSecret = "";
    static final String OSM_MAP_URL_FORMAT = "http://tile.openstreetmap.org/%d/%d/%d.png";
    private static final String patternStr_hashtag = "#([^\\s]+)";
    private static final String patternStr_screenname = "@([a-zA-Z0-9_-]+)";
    static final String patternStr_urlPart = "[\\/\\w\\.\\#\\?\\=\\&\\%\\~\\+\\-\\:\\;]+";
    private String preSearchtweetString = "";
    private String pref_confirmdialog_checkeditems = "";
    private String pref_filter_item_inline_img = "";
    private String pref_filter_item_usericon = "";
    private String pref_filter_tl_headericon = "";
    private String pref_header_fontcolor = "";
    private String pref_mute_screenname = "";
    private String pref_mute_source = "";
    private String pref_mute_text = "";
    private String pref_mute_time_screenname = "";
    private String pref_search_unread_mylastfav_mute_source = "";
    private String pref_search_unread_mylastfav_mute_text = "";
    private String pref_search_unread_mylasttweet_mute_source = "";
    private String pref_search_unread_mylasttweet_mute_text = "";
    private String pref_tl_fontcolor_createdat = "";
    private String pref_tl_fontcolor_createdat_retweeted = "";
    private String pref_tl_fontcolor_createdat_retweetedby = "";
    private String pref_tl_fontcolor_screenname = "";
    private String pref_tl_fontcolor_screenname_retweeted = "";
    private String pref_tl_fontcolor_screenname_retweetedby = "";
    private String pref_tl_fontcolor_source = "";
    private String pref_tl_fontcolor_source_retweeted = "";
    private String pref_tl_fontcolor_source_retweetedby = "";
    private String pref_tl_fontcolor_statustext = "";
    private String pref_tl_fontcolor_statustext_hashtag = "";
    private String pref_tl_fontcolor_statustext_location = "";
    private String pref_tl_fontcolor_statustext_retweeted = "";
    private String pref_tl_fontcolor_statustext_screenname = "";
    private String pref_tl_fontcolor_statustext_uri = "";
    private String pref_tl_fontcolor_text_updatetweet_over = "#ff0000";
    private String pref_tl_fontcolor_username = "";
    private String pref_tl_fontcolor_username_retweeted = "";
    private String pref_tl_imagesize_string = "56";
    private String pref_userinfo_fontcolor_my_profile = "";
    private String screenName = "";
    private static final String SP = "&nbsp;";
    public static final String TWITTER_BASE_URI = "https://twitter.com/";

    private String[] finalOurScreenNames;
    private static final String[] STRINGTODATE_FORMAT_PATTERN = {"HHmm", "HH:mm", "MM/dd", "HHmmss", "HH:mm:ss", "yy/MM/dd", "yyyy/MM/dd", "MM/dd HH:mm", "yyMMdd HHmm", "yyMMdd HHmmss",
            "yy/MM/dd HH:mm", "yyyy/MM/dd HH:mm", "yy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"};

    private Twitter twitter;

    private UrlUtil urlUtil;

    private ViewHolderStatus holder;

    // 依存あり

    public static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", LOCALE);
    final DateFormat DFu = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", LOCALE);
    static final DateFormat DF_DATE = new SimpleDateFormat("yyyyMMdd", LOCALE);
    static final DateFormat DF_TIME = new SimpleDateFormat("HH:mm:ss", LOCALE);

    private static final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    private String BIO_STR_1 = "</font><font color=\"" + default_link_font_color + "\"><a href=\"https://twitter.com/search/%23$1\">#$1</a></font><font color=\"" + default_font_color + "\">";
    private String BIO_STR_2 = "</font><font color=\"" + default_link_font_color + "\"><a href=\"https://twitter.com/$1\">@$1</a></font><font color=\"" + default_font_color + "\">";
    private String pref_tl_bgcolor = default_background_color;
    public static final String default_locationinfo_exception_latlng = default_locationinfo_exception_lat + "," + default_locationinfo_exception_lng;
    static final String default_user_index_size_string = "4";
    private static final String patternStr_urlHttpHttps = "(?:https?://)" + patternStr_urlPart;
    private static final String patternStr_urlTcoHttpHttps = "(?:https?://)?t[.]co/" + patternStr_urlPart;
    private static final String SP4 = new String(new char[4]).replace("\0", SP);
    private static final String SP8 = new String(new char[8]).replace("\0", SP);

    // 依存あり2

    static final Pattern pattern_urlHttpHttps = Pattern.compile(patternStr_urlHttpHttps, Pattern.CASE_INSENSITIVE);
    static final Pattern pattern_urlHttpHttpsShortened = Pattern.compile("(" + patternStr_urlHttpHttps + "|\\w+[.]+\\w{2,}/)" + patternStr_urlPart, Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern_urlTcoHttpHttps = Pattern.compile(patternStr_urlTcoHttpHttps, Pattern.CASE_INSENSITIVE);

    //

    ListAdapter(Context context, String crpKey, ListView listView, List<Status> tweets) {
        this.context = context;
        staticContext = context;
        checkNetworkUtil = new CheckNetworkUtil(context);
        mImageLoader = new ImageLoader(Volley.newRequestQueue(this.context.getApplicationContext()), bitmapLruCache);
        urlUtil = new UrlUtil(context);

        this.crpKey = crpKey;
        this.listView = listView;
        if (tweets != null) {
            this.tweets = tweets;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dpi = metrics.density; // DPIの取得
        padding = (int) (1 * dpi);

        movementmethod = LinkMovementMethod.getInstance();

        //

        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0

        final String pref_tl_fontfilename = pref_app.getString("pref_tl_fontfilename", "");
        if (pref_tl_fontfilename.equals("") == false) {
            fontUtil.loadFont(pref_tl_fontfilename, context);
        }

        //

        // pref_header_bgcolor = pref_app.getString("pref_header_fontcolor", default_background_color);
        // pref_listview_bgcolor = pref_app.getString("pref_listview_bgcolor", default_listview_background_color);
        // pref_tl_bgcolor_buttons = pref_app.getString("pref_tl_bgcolor_buttons", default_background_color);
        // pref_tl_bgcolor_updatetweet = pref_app.getString("pref_tl_bgcolor_updatetweet", default_background_color);
        // pref_tl_fontcolor_buttons = pref_app.getString("pref_tl_fontcolor_buttons", default_background_color);
        // pref_tl_fontcolor_text_updatetweet = pref_app.getString("pref_tl_fontcolor_text_updatetweet", "#ffffff");
        pref_check_ratelimit_allacounts = pref_app.getBoolean("pref_check_ratelimit_allacounts", false);
        pref_check_ratelimit_detail = pref_app.getBoolean("pref_check_ratelimit_detail", true);
        pref_confirmdialog_checkeditems = pref_app.getString("pref_confirmdialog_checkeditems", default_confirmdialog_checkeditems);
        pref_confirmdialog_fav = pref_app.getBoolean("pref_confirmdialog_fav", true);
        pref_confirmdialog_pak = pref_app.getBoolean("pref_confirmdialog_pak", true);
        pref_confirmdialog_rt = pref_app.getBoolean("pref_confirmdialog_rt", true);
        pref_confirmdialog_userrt = pref_app.getBoolean("pref_confirmdialog_userrt", true);
        pref_enable_confirmdialog_hidden_allaccount = pref_app.getBoolean("pref_enable_confirmdialog_hidden_allaccount", true);
        pref_enable_expand_uri_fullurl = pref_app.getBoolean("pref_enable_expand_uri_fullurl", false);
        pref_enable_expand_uri_string_length = getPrefInt("pref_enable_expand_uri_string_length", "30");
        pref_enable_expand_uri_thirdparty_mobile = pref_app.getBoolean("pref_enable_expand_uri_thirdparty_mobile", false);
        pref_enable_expand_uri_thirdparty_tweetmenu_mobile = pref_app.getBoolean("pref_enable_expand_uri_thirdparty_tweetmenu_mobile", true);
        pref_enable_expand_uri_twitter_mobile = pref_app.getBoolean("pref_enable_expand_uri_twitter_mobile", true);
        pref_enable_expand_uri_twitter_tweetmenu_mobile = pref_app.getBoolean("pref_enable_expand_uri_twitter_tweetmenu_mobile", true);
        pref_enable_htmlescape = pref_app.getBoolean("pref_enable_htmlescape", default_enable_htmlescape);
        pref_enable_inline_img_async = pref_app.getBoolean("pref_enable_inline_img_async", false);
        pref_enable_inline_img_mobile = pref_app.getBoolean("pref_enable_inline_img_mobile", false);
        pref_enable_inline_img_volley = pref_app.getBoolean("pref_enable_inline_img_volley", false);
        pref_enable_inline_img_volley_cancel = pref_app.getBoolean("pref_enable_inline_img_volley_cancel", true);
        pref_enable_log_statuses = pref_app.getBoolean("pref_enable_log_statuses", false);
        pref_enable_singleline = pref_app.getBoolean("pref_enable_singleline", false);
        pref_enable_smoothscroll_load = pref_app.getBoolean("pref_enable_smoothscroll_load", false);
        pref_enable_smoothscroll_search = pref_app.getBoolean("pref_enable_smoothscroll_search", false);
        pref_enable_tl_speedy = pref_app.getBoolean("pref_enable_tl_speedy", false);
        pref_enable_tweetmenu_check_favednum = pref_app.getBoolean("pref_enable_tweetmenu_check_favednum", true);
        pref_enable_tweetmenu_check_retweetednum = pref_app.getBoolean("pref_enable_tweetmenu_check_retweetednum", true);
        pref_enable_tweetmenu_check_tweet = pref_app.getBoolean("pref_enable_tweetmenu_check_tweet", true);
        pref_fav_at_the_same_time_pak = pref_app.getBoolean("pref_fav_at_the_same_time_pak", true);
        pref_fav_site = getPrefInt("pref_fav_site", "0");
        pref_filter_item_inline_img = pref_app.getString("pref_filter_item_inline_img", default_filter_color);
        pref_filter_item_usericon = pref_app.getString("pref_filter_item_usericon", default_filter_color);
        pref_filter_tl_headericon = pref_app.getString("pref_filter_tl_headericon", default_filter_color);
        pref_header_fontcolor = pref_app.getString("pref_header_fontcolor", default_font_color);
        pref_hide_item_myicon = pref_app.getBoolean("pref_hide_item_myicon", false);
        pref_hide_item_myname = pref_app.getBoolean("pref_hide_item_myname", false);
        pref_hide_item_usericon = pref_app.getBoolean("pref_hide_item_usericon", false);
        pref_hide_link_createdat = pref_app.getBoolean("pref_hide_link_createdat", false);
        pref_hide_link_screenname = pref_app.getBoolean("pref_hide_link_screenname", false);
        pref_hide_link_source = pref_app.getBoolean("pref_hide_link_source", false);
        pref_hide_tl_headericon = pref_app.getBoolean("pref_hide_tl_headericon", false);
        pref_hide_tweet_footer_action = pref_app.getBoolean("pref_hide_tweet_footer_action", false);
        pref_map_zoom = getPrefInt("pref_map_zoom", "20");
        pref_mute_screenname = "," + pref_app.getString("pref_mute_screenname", "") + "," + pref_twtr.getString("blocked_users", "") + ",";
        pref_mute_source = "," + pref_app.getString("pref_mute_source", "") + ",";
        pref_mute_text = "," + pref_app.getString("pref_mute_text", "") + ",";
        pref_mute_time = getPrefInt("pref_mute_time", "600");
        pref_mute_time_screenname = "," + pref_app.getString("pref_mute_time_screenname", "") + ",";
        pref_retweet_at_the_same_time_pak = pref_app.getBoolean("pref_retweet_at_the_same_time_pak", false);
        pref_search_unread_mylastfav_count = getPrefInt("pref_search_unread_mylastfav_count", default_search_unread_mylastfav_count_string);
        pref_search_unread_mylastfav_timeout = getPrefInt("pref_search_unread_mylastfav_timeout", default_search_unread_mylastfav_timeout_string);
        pref_search_unread_mylasttweet_count = getPrefInt("pref_search_unread_mylasttweet_count", default_search_unread_mylasttweet_count_string);
        pref_search_unread_mylasttweet_timeout = getPrefInt("pref_search_unread_mylasttweet_timeout", default_search_unread_mylasttweet_timeout_string);
        pref_setselection_requestfocus = pref_app.getBoolean("pref_setselection_requestfocus", false);
        pref_setselection_requestfocusfromtouch = pref_app.getBoolean("pref_setselection_requestfocusfromtouch", false);
        pref_setselection_setfocusable = pref_app.getBoolean("pref_setselection_setfocusable", false);
        pref_setselection_setfocusableintouchmode = pref_app.getBoolean("pref_setselection_setfocusableintouchmode", false);
        pref_setselection_setitemchecked = pref_app.getBoolean("pref_setselection_setitemchecked", false);
        pref_setselection_setselected = pref_app.getBoolean("pref_setselection_setselected", false);
        pref_setselection_triple = pref_app.getBoolean("pref_setselection_triple", false);
        pref_short_url_length = getPrefInt("pref_short_url_length", default_short_url_length_string);
        pref_show_profilebannerimage = pref_app.getBoolean("pref_show_profilebannerimage", false);
        pref_timeout_connection = getPrefInt("pref_timeout_connection", default_timeout_connection_string);
        pref_timeout_connection_imgtag = getPrefInt("pref_timeout_connection_imgtag", default_timeout_connection_string);
        pref_timeout_so = getPrefInt("pref_timeout_so", default_timeout_so_string);
        pref_timeout_so_imgtag = getPrefInt("pref_timeout_so_imgtag", default_timeout_so_string);
        pref_timeout_t4j_connection = getPrefInt("pref_timeout_t4j_connection", "20000");
        pref_timeout_t4j_read = getPrefInt("pref_timeout_t4j_read", "120000");
        pref_tl_bgcolor = pref_app.getString("pref_tl_bgcolor", default_background_color);
        pref_tl_fontcolor_createdat = pref_app.getString("pref_tl_fontcolor_createdat", default_font_color);
        pref_tl_fontcolor_createdat_retweeted = pref_app.getString("pref_tl_fontcolor_createdat_retweeted", default_retweet_font_color);
        pref_tl_fontcolor_createdat_retweetedby = pref_app.getString("pref_tl_fontcolor_createdat_retweetedby", default_font_color);
        pref_tl_fontcolor_screenname = pref_app.getString("pref_tl_fontcolor_screenname", default_font_color);
        pref_tl_fontcolor_screenname_retweeted = pref_app.getString("pref_tl_fontcolor_screenname_retweeted", default_retweet_font_color);
        pref_tl_fontcolor_screenname_retweetedby = pref_app.getString("pref_tl_fontcolor_screenname_retweetedby", default_font_color);
        pref_tl_fontcolor_source = pref_app.getString("pref_tl_fontcolor_source", default_font_color);
        pref_tl_fontcolor_source_retweeted = pref_app.getString("pref_tl_fontcolor_source_retweeted", default_retweet_font_color);
        pref_tl_fontcolor_source_retweetedby = pref_app.getString("pref_tl_fontcolor_source_retweetedby", default_font_color);
        pref_tl_fontcolor_statustext = pref_app.getString("pref_tl_fontcolor_statustext", default_font_color);
        pref_tl_fontcolor_statustext_hashtag = pref_app.getString("pref_tl_fontcolor_statustext_hashtag", default_link_font_color);
        pref_tl_fontcolor_statustext_location = pref_app.getString("pref_tl_fontcolor_statustext_location", default_link_font_color);
        pref_tl_fontcolor_statustext_retweeted = pref_app.getString("pref_tl_fontcolor_statustext_retweeted", default_retweet_font_color);
        pref_tl_fontcolor_statustext_screenname = pref_app.getString("pref_tl_fontcolor_statustext_screenname", default_link_font_color);
        pref_tl_fontcolor_statustext_uri = pref_app.getString("pref_tl_fontcolor_statustext_uri", default_link_font_color);
        pref_tl_fontcolor_text_updatetweet_over = pref_app.getString("pref_tl_fontcolor_text_updatetweet_over", "#ff0000");
        pref_tl_fontcolor_username = pref_app.getString("pref_tl_fontcolor_username", default_font_color);
        pref_tl_fontcolor_username_retweeted = pref_app.getString("pref_tl_fontcolor_username_retweeted", default_retweet_font_color);
        pref_tl_fontsize = getPrefFloat("pref_tl_fontsize", "14");
        pref_tl_fontsize_large_screenname = pref_app.getBoolean("pref_tl_fontsize_large_screenname", true);
        pref_tl_fontsize_large_url = pref_app.getBoolean("pref_tl_fontsize_large_url", true);
        pref_tl_fontsize_small_action = pref_app.getBoolean("pref_tl_fontsize_small_action", true);
        pref_tl_fontsize_small_createdat = pref_app.getBoolean("pref_tl_fontsize_small_createdat", true);
        pref_tl_fontsize_small_old_retweet = pref_app.getBoolean("pref_tl_fontsize_small_old_retweet", true);
        pref_tl_fontsize_small_source = pref_app.getBoolean("pref_tl_fontsize_small_source", true);
        pref_tl_fontsize_small_username = pref_app.getBoolean("pref_tl_fontsize_small_username", true);
        pref_tl_imagesize_string = Float.toString(pref_tl_imagesize);
        pref_tl_load_direction = pref_app.getBoolean("pref_tl_load_direction", true);
        pref_tl_retweetedby_multiline = getPrefInt("pref_tl_retweetedby_multiline", "3");
        pref_user_index_offset = getPrefInt("pref_user_index_offset", "0");
        pref_user_index_size = getPrefInt("pref_user_index_size", default_user_index_size_string);
        pref_userinfo_fontcolor_my_profile = pref_app.getString("pref_userinfo_fontcolor_my_profile", default_font_color);
        pref_userinfo_show_my_profile = pref_app.getBoolean("pref_userinfo_show_my_profile", true);

        //

        BIO_STR_1 = "</font><font color=\"" + pref_tl_fontcolor_statustext_hashtag + "\"><a href=\"https://twitter.com/search/%23$1\">#$1</a></font><font color=\"" + pref_header_fontcolor + "\">";
        BIO_STR_2 = "</font><font color=\"" + pref_tl_fontcolor_statustext_screenname + "\"><a href=\"https://twitter.com/$1\">@$1</a></font><font color=\"" + pref_header_fontcolor + "\">";

        imgSizeTagpart = " height=\"" + Float.toString(pref_tl_fontsize * 0.8f * dpi) + "\"";

        lightingColorFilterItemInlineimg = getLightingColorFilter(pref_filter_item_inline_img);
        lightingColorFilterItemUsericon = getLightingColorFilter(pref_filter_item_usericon);
        lightingColorFilterTlHeadericon = getLightingColorFilter(pref_filter_tl_headericon);

        pref_enable_expand_uri_thirdparty_tweetmenu_wifi = pref_enable_tl_speedy ? pref_enable_expand_uri_thirdparty_tweetmenu_mobile : pref_enable_expand_uri_thirdparty_tweetmenu_wifi;
        pref_enable_expand_uri_thirdparty_wifi = pref_enable_tl_speedy ? pref_enable_expand_uri_thirdparty_mobile : pref_app.getBoolean("pref_enable_expand_uri_thirdparty_wifi", false);
        pref_enable_expand_uri_twitter_tweetmenu_wifi = pref_enable_tl_speedy ? pref_enable_expand_uri_twitter_tweetmenu_mobile : pref_enable_expand_uri_twitter_tweetmenu_wifi;
        pref_enable_expand_uri_twitter_wifi = pref_enable_tl_speedy ? pref_enable_expand_uri_twitter_mobile : pref_app.getBoolean("pref_enable_expand_uri_twitter_wifi", true);
        pref_enable_inline_img_wifi = pref_enable_tl_speedy ? pref_enable_inline_img_mobile : pref_app.getBoolean("pref_enable_inline_img_wifi", true);
        pref_tl_iconsize1 = (int) (pref_tl_fontsize * dpi * ((pref_enable_singleline) ? 2.0f : (4.0f * getPrefFloat("pref_tl_iconsize", "1"))));
        pref_tl_iconsize2 = (int) (pref_tl_fontsize * dpi * ((pref_enable_singleline) ? 1.0f : (2.0f * getPrefFloat("pref_tl_iconsize", "1"))));
        pref_tl_imagesize = pref_tl_fontsize * dpi * ((pref_enable_singleline) ? 2.0f : (4.0f * getPrefFloat("pref_tl_imagesize", "1")));

        //

        finalOurScreenNames = getOurScreenNames();

    }

    private static final boolean currentThreadIsUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static final String[] getPhoneIds() {
        final String[] result = {"", "", ""};
        final TelephonyManager manager = (TelephonyManager) staticContext.getSystemService(Context.TELEPHONY_SERVICE);
        result[0] = manager.getDeviceId();
        result[1] = android.os.Build.SERIAL;
        result[2] = android.provider.Settings.Secure.getString(staticContext.getContentResolver(), android.provider.Settings.System.ANDROID_ID);

        return result;
    }

    public static final String getSha1(final String str) {
        final MessageDigest md = newMessageDigest("SHA-1");
        if (md == null) {
            return "";
        } else {
            md.reset();
            md.update(str.getBytes());
            final byte[] hash = md.digest();
            final StringBuffer sb = new StringBuffer();
            final int cnt = hash.length;
            for (int i = 0; i < cnt; i++) {
                sb.append(Integer.toHexString((hash[i] >> 4) & 0x0F));
                sb.append(Integer.toHexString(hash[i] & 0x0F));
            }
            return sb.toString();
        }
    }

    static final int getStringLength(final String str) {
        int count = str.length();

        final Extractor extractor = new Extractor();
        final List<String> urls = extractor.extractURLs(str);
        for (final String url : urls) {
            count -= (url.length() - pref_short_url_length);
            if (url.startsWith(staticContext.getString(R.string.https))) {
                count += 1;
            }
        }
        return count;
    }

    private static final MessageDigest newMessageDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static final Date stringToDate(final String datetime, final int formatType) {
        final boolean isLong = datetime.length() > 10 ? true : false;
        final Date result = stringToDateParse(datetime, formatType, isLong);
        try {
            if (result != null) {
                return result;
            } else if (formatType + 1 < STRINGTODATE_FORMAT_PATTERN.length) {
                return stringToDate(datetime, formatType + 1);
            }
        } catch (Exception e) {
        }
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60);
    }

    private static final Date stringToDateParse(final String datetime, final int formatType, final boolean isLong) {
        final SimpleDateFormat sdf = new SimpleDateFormat(STRINGTODATE_FORMAT_PATTERN[formatType], LOCALE);
        try {
            final Date parsedDate = sdf.parse(datetime);
            if (parsedDate.getYear() == 70) {
                final Calendar cal = new GregorianCalendar(LOCALE);
                cal.setTime(timeTruncatedDate(new Date()).getTime());
                cal.add(Calendar.HOUR_OF_DAY, parsedDate.getHours());
                cal.add(Calendar.MINUTE, parsedDate.getMinutes());
                cal.add(Calendar.SECOND, parsedDate.getSeconds());
                Date date = cal.getTime();
                if (date.after(new Date())) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    date = cal.getTime();
                }
                return date;
            } else {
                return parsedDate;
            }
        } catch (ParseException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static final Calendar timeTruncatedDate(final Date d) {
        final Calendar cal = new GregorianCalendar(LOCALE);
        cal.setTime(d);
        cal.clear(Calendar.AM_PM);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal;
    }

    private final void afterAction(final String key, final String text) {
        playSound(key);

        final boolean pref_enable_notification_vibration_afteraction = pref_app.getBoolean("pref_enable_notification_vibration_" + key, false);
        final boolean pref_enable_notification_led_afteraction = pref_app.getBoolean("pref_enable_notification_led_" + key, false);
        final long pref_notification_duration_afteraction = getPrefInt("pref_notification_duration_" + key, default_notification_duration_done_tweet_string);
        final int pref_notification_led_color_afteraction =
                getPrefColor("pref_notification_led_color_" + key, default_notification_led_color_done_tweet_string, default_notification_led_color_done_tweet);

        notification(NOTIFY_DONE_ACTION, text, pref_enable_notification_led_afteraction ? pref_notification_led_color_afteraction : Color.TRANSPARENT, pref_enable_notification_vibration_afteraction);

        new Thread(new Runnable() {
            @Override
            public final void run() {
                try {
                    Thread.sleep(pref_notification_duration_afteraction);
                } catch (InterruptedException e) {
                    WriteLog.write(context, e);
                }
                cancelNotification(NOTIFY_DONE_ACTION);
            }
        }).start();
    }

    private final String block(final boolean mode, final int index, final String uScreenname) {
        if (mode) {
            try {
                getTwitter(index, false).createBlock(uScreenname);

                return (context.getString(R.string.done_block_create) + ": @" + uScreenname + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final NumberFormatException e) {
                WriteLog.write(context, e);
            } catch (final TwitterException e) {
                twitterException(e);
            }
        } else {
            try {
                getTwitter(index, false).destroyBlock(uScreenname);

                return (context.getString(R.string.done_block_destroy) + ": @" + uScreenname + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final NumberFormatException e) {
                WriteLog.write(context, e);
            } catch (final TwitterException e) {
                twitterException(e);
            }
        }
        return "";
    }

    final void cancelNotification(final int notificationId) {
        if (!((Activity) context).isFinishing()) {
            if (currentThreadIsUiThread()) {
                final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(notificationId);
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.cancel(notificationId);
                    }
                });
            }
        } else {
            try {
                final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(notificationId);
            } catch (final Exception e) {
            }
        }
    }

    private final String checkAllRelationship(final String screenName) {
        try {
            final int sbInitSize = 510; // 4(=default_user_index_size) * 94 * 4/3;
            final StringBuilder relationshipBuilder = new StringBuilder(sbInitSize);

            for (int indx = 0; indx < pref_user_index_size; indx++) {
                if (isConnected(pref_twtr.getString("status_" + indx, ""))) {
                    if (screenName.equals(pref_twtr.getString("screen_name_" + indx, "")) == false) {
                        if (checkUniq(indx)) {
                            try {
                                final Relationship relationship =
                                        getTwitter(checkIndexFromScreenname(pref_twtr.getString("screen_name_" + indx, "")), false).showFriendship(pref_twtr.getString("screen_name_" + indx, ""), screenName);

                                relationshipBuilder.append(SP4 + "@");
                                relationshipBuilder.append(screenName);
                                relationshipBuilder.append(" ");
                                relationshipBuilder.append(relationship.isSourceFollowingTarget() ? "&lt;" : "X");
                                relationshipBuilder.append("=");
                                relationshipBuilder.append(relationship.isSourceBlockingTarget() ? "B" : "");
                                relationshipBuilder.append("=");
                                relationshipBuilder.append(relationship.isSourceFollowedByTarget() ? "&gt;" : "X");
                                relationshipBuilder.append(" @");
                                relationshipBuilder.append(pref_twtr.getString("screen_name_" + indx, ""));

                                if (relationship.isSourceNotificationsEnabled()) {
                                    relationshipBuilder.append(" Noti");
                                }
                                if (relationship.isSourceWantRetweets()) {
                                    relationshipBuilder.append(" RT");
                                }
                                relationshipBuilder.append(BR);
                            } catch (final TwitterException e) {
                                twitterException(e);
                            } catch (final Exception e) {
                                WriteLog.write(context, e);
                            }
                        }
                    }
                }
            }

            return relationshipBuilder.toString();
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    final int checkIndex(final int index, final boolean saveIndex) {
        try {
            for (int i = index; i < pref_user_index_size; i++) {
                if (isConnected(pref_twtr.getString("status_" + i, ""))) {
                    pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
                    if (saveIndex) {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(index));
                        editor.commit();
                    }
                    return i;
                }
            }
            toast(context.getString(R.string.notconnected_all));
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return -1;
    }

    final int checkIndexFromListname(final String listName) {
        try {
            final String listOwner = ((listName.replace("@", "").replace("/lists/", "/")).split("/", 0))[0];
            if (listOwner.equals("") == false) {
                for (int i = pref_user_index_offset; i < pref_user_index_size; i++) {
                    if (isConnected(pref_twtr.getString("status_" + i, ""))) {
                        if (listOwner.equals(pref_twtr.getString("screen_name_" + i, ""))) {
                            return i;
                        }
                    }
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return 0;
    }

    final int checkIndexFromPrefTwtr() {
        try {
            pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
            try {
                return Integer.parseInt(pref_twtr.getString("index", "0"));
            } catch (final NumberFormatException e) {
            } catch (final Exception e) {
            }

            for (int i = pref_user_index_offset; i < pref_user_index_size; i++) {
                if (isConnected(pref_twtr.getString("status_" + i, ""))) {
                    return i;
                }
            }
            toast(context.getString(R.string.notconnected_all));
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return 0;
    }

    final int checkIndexFromPrefTwtr(int retry) {
        try {
            final int idx = checkIndexFromPrefTwtr();

            for (int i = pref_user_index_offset; i < pref_user_index_size; i++) {
                if (i != idx) {
                    if (isConnected(pref_twtr.getString("status_" + i, ""))) {
                        if ((pref_twtr.getString("status_" + Integer.toString(idx), "")).equals(pref_twtr.getString("screen_name_" + i, ""))) {
                            if (retry == 0) {
                                return i;
                            } else {
                                retry--;
                            }
                        }
                    }
                }
            }
            toast(context.getString(R.string.notconnected_all));
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return 0;
    }

    final int checkIndexFromScreenname(String screenName) {
        try {
            screenName = screenName.replace("@", "");
            int idx = 0;
            for (int i = pref_user_index_offset; i < pref_user_index_size; i++) {
                if (isConnected(pref_twtr.getString("status_" + i, ""))) {
                    if (screenName.equals(pref_twtr.getString("screen_name_" + i, ""))) {
                        idx = i;
                    }
                }
            }
            return idx;
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return 0;
    }

    private final boolean checkMute(final Status status) {
        try {
            if ((checkMuteSource(pref_mute_source, status)) || (checkMuteTimeScreenname(status)) || (checkMuteScreenname(status)) || (checkMuteText(pref_mute_text, status))) {
                return true;
            } else {
                return false;
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return false;
    }

    private final boolean checkMuteScreenname(final Status status) {
        try {
            if (pref_mute_screenname.indexOf("," + (status.getUser().getScreenName()) + ",") > -1) {
                return true;
            }

            if (status.isRetweet()) {
                try {
                    if (pref_mute_screenname.indexOf("," + (status.getRetweetedStatus().getUser().getScreenName()) + ",") > -1) {
                        return true;
                    }
                } catch (final Exception e) {
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return false;
    }

    private final boolean checkMuteSource(final String muteSource, final Status status) {
        // pref_mute_source
        // pref_search_unread_mylastfav_mute_source
        // pref_search_unread_mylasttweet_mute_source
        try {
            if (muteSource.equals(",,") == false) {
                if (muteSource.indexOf("," + (status.getSource()).replaceAll("<a[^>]+?>", "").replaceAll("</a[^>]*?>", "") + ",") > -1) {
                    return true;
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return false;
    }

    private final boolean checkMuteText(final String muteText, final Status status) {
        // pref_mute_text
        // pref_search_unread_mylastfav_mute_text
        // pref_search_unread_mylasttweet_mute_text
        try {
            if (muteText.equals(",,") == false) {
                final String[] muteTextArray = muteText.split(",");
                for (final String pref_mute_text_part : muteTextArray) {
                    if ((pref_mute_text_part.equals("") == false) && (status.getText().indexOf(pref_mute_text_part) > -1)) {
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return false;
    }

    private final boolean checkMuteTimeScreenname(final Status status) {
        try {
            if (pref_mute_time > 0) {
                if ((pref_mute_time_screenname.indexOf("," + (status.getUser().getScreenName()) + ",") > -1)
                        && ((System.currentTimeMillis() - status.getCreatedAt().getTime()) > pref_mute_time * 1000)) {
                    return true;
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return false;
    }

    private final String checkPostRateLimit(final int idx, final boolean showScreenname) {
        try {
            final StringBuilder postRateLimitStr = new StringBuilder();
            postRateLimitStr.append("Update" + (showScreenname ? (" (@" + pref_twtr.getString("screen_name_" + idx, "") + ")") : "") + ":" + System.getProperty("line.separator") + "  ");

            final Paging pagingCheckPostRateLimit = new Paging();
            pagingCheckPostRateLimit.setCount(200);
            ResponseList<Status> statuses = getTwitter(idx, false).getUserTimeline(pref_twtr.getString("screen_name_" + idx, ""), pagingCheckPostRateLimit);
            loop1:
            for (int i = 0; i < (statuses.size() - 1); i++) {
                long diff1 = (((statuses.get(i)).getCreatedAt()).getTime()) - (((statuses.get(i + 1)).getCreatedAt()).getTime());
                if (diff1 > (1000 * 60 * 60 * 3)) {
                    Date resumeDate = statuses.get(i).getCreatedAt();
                    Date nowDate = new Date();
                    while ((nowDate.getTime() - resumeDate.getTime()) > (1000 * 60 * 60 * 3)) {
                        resumeDate.setTime(resumeDate.getTime() + (1000 * 60 * 60 * 3));
                    }

                    statuses = getTwitter(idx, false).getUserTimeline(pref_twtr.getString("screen_name_" + idx, ""), pagingCheckPostRateLimit);
                    loop2:
                    for (int j = 0; j < statuses.size(); j++) {
                        long diff2 = (((statuses.get(j)).getCreatedAt()).getTime()) - (resumeDate.getTime());

                        if (diff2 < 0) {
                            postRateLimitStr.append(Integer.toString(post_max - j) + " / " + post_max + " ");
                            break loop2;
                        }
                    }

                    resumeDate.setTime(resumeDate.getTime() + (1000 * 60 * 60 * 3));
                    postRateLimitStr.append("(" + DF.format(resumeDate) + ")");
                    break loop1;
                }
            }
            return postRateLimitStr.toString();
        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final ArrayList<String> checkRateLimit(final int idx, final boolean showScreenname) {
        final ArrayList<String> rateLimitArray = new ArrayList<String>(144);
        try {
            final Map<String, RateLimitStatus> mapRateLimitStatus = getTwitter(idx, false).getRateLimitStatus();
            for (final String endpoint : mapRateLimitStatus.keySet()) {
                RateLimitStatus status = mapRateLimitStatus.get(endpoint);

                rateLimitArray.add(endpoint + (showScreenname ? (" (@" + pref_twtr.getString("screen_name_" + idx, "") + ")") : "") + ":" + System.getProperty("line.separator") + "  "
                        + status.getRemaining() + "/" + status.getLimit() + " (" + DF.format(status.getResetTimeInSeconds() * 1000L) + ")" + System.getProperty("line.separator"));
            }
            Collections.sort(rateLimitArray);
        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return rateLimitArray;
    }

    final String checkScreennameFromIndex(final int index) {
        try {
            return pref_twtr.getString("screen_name_" + index, "");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final boolean checkUniq(final int idx) {
        try {
            for (int j = 0; j < idx; j++) {
                if (isConnected(pref_twtr.getString("status_" + j, ""))) {
                    if (pref_twtr.getString("screen_name_" + idx, "").equals(pref_twtr.getString("screen_name_" + j, ""))) {
                        return false;
                    }
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return true;
    }

    final ProgressDialog createDialog(final int id) {
        try {
            if ((context.getString(id)).equals("") == false) {
                final Context context1 = (((Activity) context).getParent() != null) ? ((Activity) context).getParent() : ((Activity) context);
                final ProgressDialog pDialog = new ProgressDialog(context1);
                try {
                    ((Activity) context1).dismissDialog(id);
                } catch (final Exception e) {
                }

                pDialog.setTitle(id);
                pDialog.setMessage("Loading...");

                return pDialog;
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return null;
    }

    private final String del(final Status tweet, final String screenName) {
        try {
            String tweetScreenName = screenName;
            try {
                tweetScreenName = tweet.getUser().getScreenName();
            } catch (final Exception e) {
            }
            WriteLog.write(context, "tweetScreenName: " + tweetScreenName);

            if (tweetScreenName.equals("") == false) {
                getTwitter(checkIndexFromScreenname(tweetScreenName), false).destroyStatus(tweet.getId());

                return (context.getString(R.string.done_del) + ": @" + tweetScreenName + ": " + tweet.getText() + " " + " [@" + tweetScreenName + "]");
            }
        } catch (final TwitterException e) {
            twitterException(e);
            if (tweet.isRetweet()) {
                try {
                    String tweetScreenName = screenName;
                    try {
                        tweetScreenName = tweet.getRetweetedStatus().getUser().getScreenName();
                    } catch (Exception e1) {
                    }
                    WriteLog.write(context, "tweetScreenName: " + tweetScreenName);

                    if (tweetScreenName.equals("") == false) {
                        getTwitter(checkIndexFromScreenname(tweetScreenName), false).destroyStatus(tweet.getRetweetedStatus().getId());

                        return (context.getString(R.string.done_del) + ": @" + tweetScreenName + ": " + tweet.getRetweetedStatus().getText() + " " + " [@" + tweetScreenName + "]");
                    }
                } catch (final TwitterException e1) {
                    twitterException(e1);
                }
            }
        }
        return "";
    }

    final void deljustbefore(int index) {
        if ((pref_user_index_size <= index) || (index <= -1)) {
            index = checkIndexFromPrefTwtr();
            WriteLog.write(context, "getjustbefore(" + index + ")");
        }
        final Status finalDeljustbeforeTweet = getjustbefore(index);

        if (finalDeljustbeforeTweet != null) {
            WriteLog.write(context, finalDeljustbeforeTweet);
            String screenName = "";
            try {
                screenName = finalDeljustbeforeTweet.getUser().getScreenName();
                WriteLog.write(context, "deljustbefore() finalDeljustbeforeTweet.getUser().getScreenName() screenName: " + screenName);
            } catch (final Exception e) {
                WriteLog.write(context, e);
                screenName = "";
            }
            if (screenName.equals("")) {
                screenName = checkScreennameFromIndex(index);
                WriteLog.write(context, "deljustbefore() checkScreennameFromIndex() screenName: " + screenName);
            }
            final String finalScreenName = screenName;
            new AlertDialog.Builder(context).setTitle(context.getString(R.string.deljustbefore) + " " + Long.toString(finalDeljustbeforeTweet.getId())).setMessage(context.getString(R.string.confirm_del)
                    + ":" + NL + " @" + finalScreenName + ":" + NL + "  " + finalDeljustbeforeTweet.getText() + NL + "  " + DF.format(finalDeljustbeforeTweet.getCreatedAt())).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    //								del(finalDeljustbeforeTweet, finalScreenName);
                    afterAction("onaction_del", del(finalDeljustbeforeTweet, finalScreenName));
                }
            }).setNeutralButton(R.string.other_accounts, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    new AlertDialog.Builder(context).setTitle(R.string.deljustbefore).setItems(finalOurScreenNames, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(final DialogInterface dialog, final int which) {
                            if (finalOurScreenNames[which].equals(" - ") == false) {
                                deljustbefore(checkIndexFromScreenname(finalOurScreenNames[which]));
                            }
                        }
                    }).create().show();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {
                    return;
                }
            }).create().show();
        }
        return;

    }

    private final String fav(final boolean mode, final int index, final Status tweet) {
        if (mode) {
            try {
                String statusUserScreenname = "";
                String statusText = "";
                long statusId = 0;
                if (tweet.isRetweet()) {
                    statusText = tweet.getRetweetedStatus().getText();
                    statusUserScreenname = tweet.getRetweetedStatus().getUser().getScreenName();
                    statusId = tweet.getRetweetedStatus().getId();
                } else {
                    statusText = tweet.getText();
                    statusUserScreenname = tweet.getUser().getScreenName();
                    statusId = tweet.getId();
                }

                getTwitter(index, false).createFavorite(statusId);

                return (context.getString(R.string.done_fav_create) + ": @" + statusUserScreenname + ": " + statusText + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final NumberFormatException e) {
                WriteLog.write(context, e);
            } catch (final TwitterException e) {
                twitterException(e);
            }
        } else {
            try {
                String statusUserScreenname = "";
                String statusText = "";
                long statusId = 0;
                if (tweet.isRetweet()) {
                    statusText = tweet.getRetweetedStatus().getText();
                    statusUserScreenname = tweet.getRetweetedStatus().getUser().getScreenName();
                    statusId = tweet.getRetweetedStatus().getId();
                } else {
                    statusText = tweet.getText();
                    statusUserScreenname = tweet.getUser().getScreenName();
                    statusId = tweet.getId();
                }

                getTwitter(index, false).destroyFavorite(statusId);

                return (context.getString(R.string.done_fav_destroy) + ": @" + statusUserScreenname + ": " + statusText + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final NumberFormatException e) {
                WriteLog.write(context, e);
            } catch (final TwitterException e) {
                twitterException(e);
            }
        }
        return "";
    }

    private final String friendships(final boolean mode, final int index, final String uScreenname) {
        if (mode) {
            try {
                getTwitter(index, false).createFriendship(uScreenname);

                return (context.getString(R.string.done_follow_create) + ": @" + uScreenname + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final TwitterException e) {
                twitterException(e);
            }
        } else {
            try {
                getTwitter(index, false).destroyFriendship(uScreenname);

                return (context.getString(R.string.done_follow_destroy) + ": @" + uScreenname + " " + " [@" + checkScreennameFromIndex(index) + "]");
            } catch (final TwitterException e) {
                twitterException(e);
            }
        }

        return "";
    }

    private final Configuration getConf(final int index) {
        // screenName = pref_twtr.getString("screen_name_" + index, "");
        String consumerKey = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_key_" + index, ""));
        String consumerSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_secret_" + index, ""));
        if (consumerKey.equals("") || consumerSecret.equals("")) {
            consumerKey = context.getString(R.string.default_consumerKey);
            consumerSecret = context.getString(R.string.default_consumerSecret);
        }
        oauthToken = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_" + index, ""));
        oauthTokenSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_secret_" + index, ""));
        confbuilder = new ConfigurationBuilder();
        confbuilder.setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10)
        // .setUseSSL(true)
        ;
        conf = confbuilder.build();
        return conf;
    }

    private final boolean[] getConfirmdialogCheckedItems() {
        if (pref_confirmdialog_checkeditems.equals("")) {
            return new boolean[finalOurScreenNames.length];
        } else {
            boolean[] checkedItems = new boolean[finalOurScreenNames.length];

            int i = 0;
            for (final String sn : finalOurScreenNames) {
                if (("," + pref_confirmdialog_checkeditems + ",").contains("," + sn.replace("@", "") + ",")) {
                    checkedItems[i] = true;
                }

                i++;
            }

            return checkedItems;
        }
    }

    // 項目数の取得
    @Override
    public final int getCount() {
        int size = 0;
        try {
            size = tweets.size();
        } catch (final Exception e) {
        }
        return size;
    }

    private final int getFavCount(final Status tweet) {
        int maxFavcount = 0;

        try {
            final String[] url_fav = {"https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + Long.toString(tweet.getId()), // Twitter
                    "http://twistar.cc/" + tweet.getUser().getScreenName() + "/status/" + Long.toString(tweet.getId()), // twistar.cc
                    "http://favotter.net/status.php?id=" + Long.toString(tweet.getId()), // favotter.net
                    "http://favstar.fm/users/" + tweet.getUser().getScreenName() + "/status/" + Long.toString(tweet.getId()), // favstar.fm
                    "http://aclog.koba789.com/api/tweets/show.json?id=" + Long.toString(tweet.getId())}; // aclog

            final Pattern[] p_fav = {Pattern.compile("お気に入り<strong>([0-9]+)</strong>", Pattern.DOTALL), // Twitter
                    Pattern.compile("faved\\s+?by[^0-9]*?([0-9]+)[^0-9]*?(?:people|person)", Pattern.DOTALL), // twistar.cc
                    Pattern.compile("<span[^>]*?class=\"favotters\"[^>]*?>[^0-9]*?([0-9]+)[^0-9]*?favs by", Pattern.DOTALL), // favotter.net
                    Pattern.compile("<li[^>]*?class=\"fs-total\"[^>]*?>([0-9]+)</li[^>]*?><li[^>]*?class=\"fs-title\"[^>]*?>FAVS</li[^>]*?>", Pattern.DOTALL), // favstar.fm
                    Pattern.compile("\"favorites_count\"[^:]*?:[^0-9]*?([0-9]+),", Pattern.DOTALL)}; // aclog

            if (pref_fav_site == 0) {
                if (tweet.isRetweet()) {
                    return tweet.getRetweetedStatus().getFavoriteCount();
                } else {
                    return tweet.getFavoriteCount();
                }
            } else if (pref_fav_site == 1) {
                for (int i = 0; i < url_fav.length; i++) {
                    final Matcher m_fav = p_fav[i].matcher(HttpsClient.https2data(context, url_fav[i], pref_timeout_connection, pref_timeout_so, default_charset));
                    if (m_fav.find()) {
                        final int favcount = Integer.parseInt(m_fav.group(1));

                        if (maxFavcount < favcount) {
                            maxFavcount = favcount;
                        }
                    }
                }
            } else {
                final Matcher m_fav = p_fav[pref_fav_site - 2].matcher(HttpsClient.https2data(context, url_fav[pref_fav_site - 2], pref_timeout_connection, pref_timeout_so, default_charset));
                if (m_fav.find()) {
                    maxFavcount = Integer.parseInt(m_fav.group(1));
                } else {
                    maxFavcount = 0;
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
            maxFavcount = 0;
        }
        return maxFavcount;
    }

    private final String getFFRatio(final int followersCount, final int friendsCount) {
        final String sharps = new String(new char[(Integer.toString(followersCount)).length() - 1]).replace("\0", "#");
        decimalFormat.applyPattern(sharps + "0.##");

        float ratio;
        try {
            ratio = ((float) followersCount) / ((float) friendsCount);
        } catch (final ArithmeticException e) {
            WriteLog.write(context, e);
            return " - "; // Infinity
        }

        try {
            return decimalFormat.format(ratio);
        } catch (final IllegalArgumentException e) {
            WriteLog.write(context, e);
            return " - "; // Infinity
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final void getIcon(final ImageView imageView, final String url) {
        // WriteLog.write(context, "getIcon(" + Integer.toString(imageView.getId()) + ", " + url + ")");
        if (pref_enable_inline_img_volley) {
            getIconVolley(imageView, url);
        } else {
            getIconMap(imageView, url);
        }
    }

    private final void getIconMap(final ImageView imageView, final String url) {
        if (url != null) {
            if (url.equals("") == false) {
                // キャッシュ内アイコンの取得
                if (bitmapLruCache.containsKey(url)) {
                    try {
                        imageView.setImageBitmap(bitmapLruCache.getBitmap(url));
                    } catch (Exception e) {
                        WriteLog.write(context, e);
                    }
                    return;
                }
                // ネット上アイコンの取得
                imageView.setImageBitmap(null);
                new Thread(new Runnable() {
                    @Override
                    public final void run() {
                        try {
                            final Bitmap icon = BitmapFactory.decodeStream((new URL(url)).openStream());
                            bitmapLruCache.putBitmap(url, icon);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public final void run() {
                                    imageView.setImageBitmap(icon);
                                }
                            });
                        } catch (final Exception e) {
                            WriteLog.write(context, e);
                        }
                    }
                }).start();
            }
        }
    }

    private final void getIconVolley(final ImageView imageView, final String url) {
        new Thread(new Runnable() {
            @Override
            public final void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public final void run() {
                        try {
                            final ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.spinner_background, android.R.drawable.ic_dialog_alert);
                            final ImageContainer imageContainer = mImageLoader.get(url, listener);
                            imageView.setTag(imageContainer);
                        } catch (Exception e) {
                            WriteLog.write(context, e);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public final Object getItem(final int pos) {
        return tweets.get(pos);
    }

    @Override
    public final long getItemId(final int pos) {
        return pos;
    }

    final Status getjustbefore(int index) {
        if ((pref_user_index_size <= index) || (index <= -1))
            index = checkIndexFromPrefTwtr();
        WriteLog.write(context, "getjustbefore(" + index + ")");

        final Twitter twtr = getTwitter(index, false);
        Status deljustbeforeTweet = null;
        final String deljustbeforeScreenname = checkScreennameFromIndex(index); // deljustbeforeScreenname = twtr.getScreenName();

        try {
            WriteLog.write(context, "getjustbefore() twtr.getUserTimeline(deljustbeforeScreenname).get(0)");
            deljustbeforeTweet = twtr.getUserTimeline(deljustbeforeScreenname, new Paging(1, 1)).get(0);
        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        if (deljustbeforeTweet != null) {
            WriteLog.write(context, deljustbeforeTweet);
            return deljustbeforeTweet;
        } else {
            try {
                WriteLog.write(context, "getjustbefore() twtr.lookupUsers(deljustbeforeScreennames).get(0)");
                final String[] deljustbeforeScreennames = {deljustbeforeScreenname};
                final User deljustbeforeUser = twtr.lookupUsers(deljustbeforeScreennames).get(0);
                WriteLog.write(context, deljustbeforeTweet);
                return deljustbeforeUser.getStatus();
            } catch (final TwitterException e1) {
                twitterException(e1);
            } catch (final Exception e1) {
                WriteLog.write(context, e1);
            }
        }
        return null;
    }

    private final LightingColorFilter getLightingColorFilter(final String filterName) {
        if (filterName.equals("") == false) {
            try {
                return new LightingColorFilter(Color.parseColor(filterName), 0);
            } catch (final Exception e) {
            }
        }
        return null;
    }

    final long[] getListMemberLong(final String listOwner, final String listSlug) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        final int index = checkIndexFromScreenname(listOwner);
        final Twitter twitter = getTwitter(index, false);

        ArrayList<Long> queryIds = new ArrayList<Long>();

        try {
            long cursor = -1;
            PagableResponseList<User> users;
            do {
                users = twitter.getUserListMembers(listOwner, listSlug, cursor);
                for (final User list : users) {
                    queryIds.add(list.getId());
                }
            } while ((cursor = users.getNextCursor()) != 0);
        } catch (final TwitterException e) {
            toast(context.getString(R.string.cannot_access_twitter));
        } catch (final Exception e) {
            WriteLog.write(context, e);
            toast(context.getString(R.string.exception));
        }

        long[] result = new long[queryIds.size()];
        int i = 0;
        for (final long l : queryIds) {
            result[i++] = l;
        }

        return result;
    }

    final String[] getListMemberString(final String listOwner, final String listSlug) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        final int index = checkIndexFromScreenname(listOwner);
        final Twitter twitter = getTwitter(index, false);

        ArrayList<String> queryStrs = new ArrayList<String>();

        try {
            long cursor = -1;
            PagableResponseList<User> users;
            do {
                users = twitter.getUserListMembers(listOwner, listSlug, cursor);
                for (final User user : users) {
                    queryStrs.add(user.getScreenName());
                }
            } while ((cursor = users.getNextCursor()) != 0);
        } catch (final TwitterException e) {
            toast(context.getString(R.string.cannot_access_twitter));
        } catch (final Exception e) {
            WriteLog.write(context, e);
            toast(context.getString(R.string.exception));
        }

        return queryStrs.toArray(new String[queryStrs.size()]);
    }

    final long getNtpOffset() {
        return ntpOffset;
    }

    final String[] getOurScreenNames() {
        return getOurScreenNames("", "");
    }

    final String[] getOurScreenNames(final String prefix, final String suffix) {
        final ArrayList<String> ourScreenNames = new ArrayList<String>(default_user_index_size);
        for (int idx = 0; idx < pref_user_index_size; idx++) {
            if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                if (checkUniq(idx)) {
                    ourScreenNames.add(prefix + pref_twtr.getString("screen_name_" + idx, "") + suffix);
                }
            }
        }

        return ourScreenNames.toArray(new String[ourScreenNames.size()]);
    }

    public final int getPrefColor(final String key, final String defaultValueString, final int defaultValue) {
        try {
            return Color.parseColor(pref_app.getString(key, defaultValueString));
        } catch (final Exception e) {
            return defaultValue;
        }
    }

    final float getPrefFloat(final String key, final String defaultValueString) {
        try {
            if (pref_app == null) {
                pref_app = PreferenceManager.getDefaultSharedPreferences(context);
            }
            return Float.parseFloat(pref_app.getString(key, defaultValueString));
        } catch (final Exception e) {
            return Float.parseFloat(defaultValueString);
        }
    }

    public static final float getPrefFloat(final Context context, final String key, final String defaultValueString) {
        try {
            if (staticPref_app == null) {
                staticPref_app = PreferenceManager.getDefaultSharedPreferences((context != null) ? context : staticContext);
            }
            return Float.parseFloat(staticPref_app.getString(key, defaultValueString));
        } catch (final Exception e) {
            return Float.parseFloat(defaultValueString);
        }
    }

    final int getPrefInt(final String key, final String defaultValueString) {
        try {
            if (pref_app == null) {
                pref_app = PreferenceManager.getDefaultSharedPreferences(context);
            }
            return Integer.parseInt(pref_app.getString(key, defaultValueString));
        } catch (final Exception e) {
            return Integer.parseInt(defaultValueString);
        }
    }

    public static final int getPrefInt(final Context context, final String key, final String defaultValueString) {
        try {
            if (staticPref_app == null) {
                staticPref_app = PreferenceManager.getDefaultSharedPreferences((context != null) ? context : staticContext);
            }
            return Integer.parseInt(staticPref_app.getString(key, defaultValueString));
        } catch (final Exception e) {
            return Integer.parseInt(defaultValueString);
        }
    }

    final String getQuery(final String uriString) {
        WriteLog.write(context, "uriString: " + uriString);
        if (uriString.indexOf("search?q=") > -1) {
            return uriString.substring(uriString.indexOf("search?q=") + ("search?q=").length());
        } else if (uriString.indexOf("search\\/") > -1) {
            return uriString.substring(uriString.indexOf("search/") + ("search/").length());
        } else if (uriString.indexOf("?") > -1) {
            return uriString.substring(uriString.indexOf("?") + 1);
        } else {
            return uriString.substring(uriString.lastIndexOf("/") + 1);
        }
    }

    private final String getRateLimits() {
        String messagePostRateLimit = "";
        String messageRateLimit = "";

        if (pref_check_ratelimit_allacounts) {
            final ArrayList<ArrayList<String>> RateLimitArray = new ArrayList<ArrayList<String>>(default_user_index_size);

            for (int idx = pref_user_index_offset; idx < pref_user_index_size; idx++) {
                if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                    if (checkUniq(idx)) {
                        messagePostRateLimit += checkPostRateLimit(idx, true) + System.getProperty("line.separator");
                        if (pref_check_ratelimit_detail) {
                            RateLimitArray.add(checkRateLimit(idx, true));
                        }
                    }
                }
            }

            if (pref_check_ratelimit_detail) {
                for (int i = 0; i < RateLimitArray.get(0).size(); i++) {
                    for (int j = 0; j < RateLimitArray.size(); j++) {
                        messageRateLimit += (RateLimitArray.get(j)).get(i);
                    }
                }
            }

        } else {
            ArrayList<String> RateLimitArray = new ArrayList<String>(144);

            int idx = checkIndexFromPrefTwtr();
            messagePostRateLimit = checkPostRateLimit(idx, false);

            if (pref_check_ratelimit_detail) {
                messageRateLimit = System.getProperty("line.separator");

                RateLimitArray = checkRateLimit(idx, false);
                for (int i = 0; i < RateLimitArray.size(); i++) {
                    messageRateLimit += RateLimitArray.get(i);
                }
            }
        }

        return messagePostRateLimit + System.getProperty("line.separator") + messageRateLimit;
    }

    private final Status getRetweet(final Status status) {
        if (status.isRetweet()) {
            try {
                return status.getRetweetedStatus();
            } catch (final Exception e) {
                return status;
            }
        } else {
            return status;
        }
    }

    private final ResponseList<Status> getRetweetsList(int index, long id) {
        try {
            return getTwitter(index, false).getRetweets(id);
        } catch (final TwitterException e) {
            twitterException(e);
        }
        return null;
    }

    final int getSelectPos() {
        return this.select_pos;
    }

    final int getSelectPos(int i) {
        this.select_pos = this.select_pos + i;
        return this.select_pos;
    }

    final ResponseList<UserList> getSortedOurslist() {
        if (sortedOurslist == null) {
            for (int idx = pref_user_index_offset; idx < pref_user_index_size; idx++) {
                if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                    if (checkUniq(idx)) {
                        final Twitter twitter = getTwitter(idx, false);
                        PagableResponseList<UserList> templists;
                        long cursor = -1L;
                        try {
                            do {
                                templists = twitter.getUserListsOwnerships(twitter.getScreenName(), 1000, cursor);
                                if (templists != null) {
                                    if (sortedOurslist == null) {
                                        try {
                                            sortedOurslist = templists;
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }
                                    } else {
                                        try {
                                            sortedOurslist.addAll(templists);
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }
                                    }
                                }
                                if (templists.hasNext()) {
                                    cursor = templists.getNextCursor();
                                    WriteLog.write(context, "getSortedOurslistName() cursor: " + cursor);
                                } else {
                                    cursor = -1L;
                                }
                            } while (cursor > -1L);
                        } catch (final TwitterException e) {
                            twitterException(e);
                        }
                    }
                }
            }
            try {
                Collections.sort(sortedOurslist, new ListNameComparator());
            } catch (final Exception e) {
            }
            collectionsUtil.removeDuplicate(sortedOurslist);
        }
        return sortedOurslist;
    }

    final ArrayList<String> getSortedOurslistName() {
        if (sortedOurslistName == null) {
            if (sortedOurslist == null) {
                for (int idx = pref_user_index_offset; idx < pref_user_index_size; idx++) {
                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                        if (checkUniq(idx)) {
                            final Twitter twitter = getTwitter(idx, false);
                            PagableResponseList<UserList> templists;
                            long cursor = -1L;
                            try {
                                do {
                                    templists = twitter.getUserListsOwnerships(twitter.getScreenName(), 1000, cursor);
                                    if (templists != null) {
                                        if (sortedOurslistName == null) {
                                            try {
                                                sortedOurslistName = new ArrayList<String>();
                                            } catch (final Exception e) {
                                                WriteLog.write(context, e);
                                            }
                                        }
                                        for (final UserList userList : templists) {
                                            sortedOurslistName.add(userList.getURI().toString());
                                        }
                                    }
                                    if (templists.hasNext()) {
                                        cursor = templists.getNextCursor();
                                        WriteLog.write(context, "getSortedOurslistName() cursor: " + cursor);
                                    } else {
                                        cursor = -1L;
                                    }
                                } while (cursor > -1L);
                            } catch (final TwitterException e) {
                                twitterException(e);
                            }
                        }
                    }
                }
                try {
                    Collections.sort(sortedOurslist, new ListNameComparator());
                } catch (final Exception e) {
                }
                collectionsUtil.removeDuplicate(sortedOurslist);
            } else {
                for (final UserList userList : sortedOurslist) {
                    sortedOurslistName.add(userList.getURI().toString());
                }
            }
        }
        return sortedOurslistName;
    }

    final ResponseList<UserList> getSortedUserlist(boolean includedOurs) {
        if ((sortedUserlist == null) || (includedOurs != preincludedOurs_sortedUserlist)) {
            for (int idx = pref_user_index_offset; idx < pref_user_index_size; idx++) {
                if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                    if (checkUniq(idx)) {
                        final Twitter twitter = getTwitter(idx, false);
                        ResponseList<UserList> templists;
                        int page = 1;
                        try {
                            do {
                                templists = twitter.getUserLists(twitter.getScreenName() + "&page=" + page);
                                if (templists != null) {
                                    if (sortedUserlist == null) {
                                        try {
                                            sortedUserlist = templists;
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }
                                    } else {
                                        try {
                                            sortedUserlist.addAll(templists);
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }
                                    }
                                }
                                page++;
                            } while (templists.size() > 0);
                        } catch (final TwitterException e) {
                            twitterException(e);
                        }
                    }
                }
            }
            try {
                Collections.sort(sortedUserlist, new ListNameComparator());
            } catch (final Exception e) {
            }
            if (!includedOurs) {
                String sNames = ",";
                for (int idx = pref_user_index_offset; idx < pref_user_index_size; idx++) {
                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                        sNames += pref_twtr.getString("screen_name_" + idx, "") + ",";
                    }
                }
                for (Iterator<UserList> iterator = sortedUserlist.iterator(); iterator.hasNext(); ) {
                    final UserList userList = iterator.next();
                    if (sNames.indexOf("," + (userList.getUser().getScreenName()) + ",") > -1) {
                        try {
                            iterator.remove();
                        } catch (final Exception e) {
                        }
                    }
                }
            }
            collectionsUtil.removeDuplicate(sortedUserlist);
        }
        return sortedUserlist;
    }

    final String[] getTlAutoCompleteStringArray() {
        final String[] uriParts = {"#home", "#mention", "#favorite"};
        final String[] ourScreenNames = getOurScreenNames();
        final ArrayList<String> oursListNames = getSortedOurslistName();

        final ArrayList<String> result = new ArrayList<String>(ourScreenNames.length * uriParts.length + 1 + 100);

        for (final String s : uriStringHistoryGetArray()) {
            result.add(s);
        }

        result.add("");

        result.add(TWITTER_BASE_URI + "search?q=");
        if (ourScreenNames != null) {
            try {
                for (final String screenName : ourScreenNames) {
                    for (final String uriPart : uriParts) {
                        result.add(TWITTER_BASE_URI + screenName + uriPart);
                    }
                }
            } catch (final Exception e) {
            }
        }
        if (oursListNames != null) {
            try {
                for (final String listName : oursListNames) {
                    result.add(listName);
                }
            } catch (final Exception e) {
            }
        }

        return result.toArray(new String[result.size()]);
    }

    final String getTweetfooter(final SharedPreferences pref_app, final String str3) {
        final boolean pref_enable_common_footer = pref_app.getBoolean("pref_enable_common_footer", false);
        String pref_common_footer = "";
        if (pref_enable_common_footer) {
            pref_common_footer = pref_app.getString("pref_common_footer", "");
        }
        return str3 + pref_common_footer;
    }

    final String getTweetHeader(final SharedPreferences pref_app, final String str1) {
        final boolean pref_enable_common_header = pref_app.getBoolean("pref_enable_common_header", false);
        String pref_common_header = "";
        if (pref_enable_common_header) {
            pref_common_header = pref_app.getString("pref_common_header", "");
        }
        return pref_common_header + str1;
    }

    private final String[] getTweetMenuItemArray(final Status finalTweet_menu, final Status finalTweet_menu_RetweetedStatus) {

        final User finalTweet_menu_user = finalTweet_menu.getUser();
        final String finalTweet_menu_screenName = finalTweet_menu_user.getScreenName();
        final String finalTweet_menu_RetweetedStatus_screenName = finalTweet_menu_RetweetedStatus.getUser().getScreenName();
        final boolean finalTweet_menu_IsRetweet = finalTweet_menu.isRetweet();

        final boolean myStatus = isMyStatusOrMyRetweet(finalTweet_menu_screenName, finalTweet_menu_RetweetedStatus_screenName);

        final ArrayList<String> ITEM = new ArrayList<String>(18);
        ITEM.add("@" + finalTweet_menu_RetweetedStatus_screenName);
        if (finalTweet_menu_IsRetweet) {
            ITEM.add(" @" + finalTweet_menu_screenName);
        }
        ITEM.add(context.getString(R.string.mention));

        if (!finalTweet_menu_user.isProtected()) {
            if (pref_enable_tweetmenu_check_retweetednum) {
                final long rtcount = finalTweet_menu_RetweetedStatus.getRetweetCount();
                ITEM.add(context.getString(R.string.add_rt)
                        + ((rtcount > 0) ? (" " + context.getString(R.string.rt_by) + " " + Long.toString(rtcount) + context.getString(R.string.rt_users)) : "")
                        + ((finalTweet_menu_RetweetedStatus.isRetweetedByMe()) ? (" " + context.getString(R.string.rt_by_current_account)) : ""));
            } else {
                ITEM.add(context.getString(R.string.add_rt));
            }

            ITEM.add(context.getString(R.string.rt_who_add));

            ITEM.add(context.getString(R.string.add_userrt));
        }

        if (pref_enable_tweetmenu_check_favednum) {
            final long favcount = getFavCount(finalTweet_menu_RetweetedStatus);

            ITEM.add(context.getString(R.string.fav_create)
                    + ((favcount > 0) ? (" " + context.getString(R.string.fav_by) + " " + Long.toString(favcount) + context.getString(R.string.fav_users)) : "")
                    + ((finalTweet_menu_RetweetedStatus.isFavorited()) ? (" " + context.getString(R.string.fav_by_current_account)) : ""));
        } else {
            ITEM.add(context.getString(R.string.fav_create));
        }

        ITEM.add(context.getString(R.string.fav_destroy));

        ITEM.add(context.getString(R.string.fav_who_add));

        if (!finalTweet_menu_user.isProtected()) {
            ITEM.add(context.getString(R.string.add_favrt));

            ITEM.add(context.getString(R.string.add_favuserrt));
        }

        ITEM.add(context.getString(R.string.show_mention));

        ITEM.add(context.getString(R.string.share_link));

        ITEM.add(context.getString(R.string.share_text) + ":" + System.getProperty("line.separator") + finalTweet_menu_RetweetedStatus.getText());

        ITEM.add(context.getString(R.string.pak));

        if (finalTweet_menu_RetweetedStatus.getGeoLocation() != null) {
            ITEM.add(context.getString(R.string.share_place) + finalTweet_menu_RetweetedStatus.getGeoLocation().getLatitude() + "," + finalTweet_menu_RetweetedStatus.getGeoLocation().getLongitude());
        }

        if (myStatus) {
            ITEM.add(context.getString(R.string.del));
        }

        ITEM.add(context.getString(R.string.add_mute));

        // ITEM.add(context.getString(R.string.develop_bookmark));

        return ITEM.toArray(new String[ITEM.size()]);
    }

    final List<Status> getTweets() {
        return tweets;
    }

    final Twitter getTwitter(final int index, final boolean saveIndex) {
        try {
            pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
            if (isConnected(pref_twtr.getString("status_" + index, ""))) {
                if ((twitter == null) || (index != index_pre)) {
                    if (saveIndex) {
                        final SharedPreferences.Editor editor = pref_twtr.edit();
                        editor.putString("index", Integer.toString(index));
                        editor.commit();
                    }
                    conf = getConf(index);
                    if (conf != null)
                        twitter = new TwitterFactory(conf).getInstance();
                    index_pre = index;
                }
                return twitter;
            }
            toast(context.getString(R.string.notconnected));
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return null;
    }

    final TwitterStream getTwitterStream(final int index, final boolean saveIndex) {
        try {
            if (isConnected(pref_twtr.getString("status_" + index, ""))) {
                try {
                    TwitterStream twitterStream = null;
                    if (index != index_pre_s) {
                        if (saveIndex) {
                            pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
                            final SharedPreferences.Editor editor = pref_twtr.edit();
                            editor.putString("index", Integer.toString(index));
                            editor.commit();
                        }
                        conf = getConf(index);
                        if (conf != null) {
                            TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
                            if (twitterStreamFactory != null)
                                twitterStream = twitterStreamFactory.getInstance();
                        }
                        index_pre_s = index;
                    }
                    return twitterStream;
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return null;
    }

    private final User getUser() {
        try {
            final int idx = checkIndexFromPrefTwtr();
            return getUser(idx, getTwitter(idx, false).getScreenName());
        } catch (IllegalStateException e) {
            WriteLog.write(context, e);
        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return null;
    }

    private final User getUser(int index, String screenname) {
        try {
            if (index < 0)
                index = checkIndexFromPrefTwtr();
            if (screenname.equals(""))
                screenname = checkScreennameFromIndex(checkIndexFromPrefTwtr());
            return getTwitter(index, false).showUser(screenname);
        } catch (final TwitterException e) {
            try {
                twitterException(e);
            } catch (Exception ex) {
                toast(context.getString(R.string.cannot_access_twitter));
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return null;
    }

    @Override
    public final View getView(final int pos, View view, final ViewGroup parent) {
        try {

            if (view == null) {
                final LinearLayout layout = new LinearLayout(context);
                try {
                    layout.setBackgroundColor(Color.parseColor(pref_tl_bgcolor));
                } catch (final IllegalArgumentException e) {
                }
                layout.setPadding(padding, padding, padding, padding);
                layout.setGravity(Gravity.TOP);

                final ImageView imageView1 = new ImageView(context);
                final ImageView imageView2 = new ImageView(context);
                final TextView textView = new TextView(context);
                textView.setMovementMethod(movementmethod);
                try {
                    textView.setBackgroundColor(Color.parseColor(pref_tl_bgcolor));
                } catch (final IllegalArgumentException e) {
                }
                textView.setTextSize(pref_tl_fontsize * dpi);
                textView.setPadding(padding, 0, padding, 0);
                fontUtil.setFont(textView, context);

                if (pref_enable_singleline) {
                    layout.addView(imageView1);
                    layout.addView(imageView2);
                    textView.setSingleLine();
                } else {
                    final LinearLayout layout_v = new LinearLayout(context);
                    layout_v.setOrientation(LinearLayout.VERTICAL);
                    layout_v.addView(imageView1);
                    layout_v.addView(imageView2);
                    layout.addView(layout_v);
                }
                layout.addView(textView);
                view = layout;

                holder = new ViewHolderStatus();
                holder.imageView1 = imageView1;
                holder.imageView2 = imageView2;
                holder.textView = textView;
                holder.view = view;

                view.setTag(holder);
            } else {
                holder = (ViewHolderStatus) view.getTag();
            }

            // リクエストのキャンセル処理
            if (pref_enable_inline_img_volley && pref_enable_inline_img_volley_cancel) {
                try {
                    final ImageContainer imageContainer1 = (ImageContainer) holder.imageView1.getTag();
                    if ((imageContainer1 != null) && (imageContainer1.getRequestUrl().equals("") == false)) {
                        imageContainer1.cancelRequest();
                    }
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
                try {
                    final ImageContainer imageContainer2 = (ImageContainer) holder.imageView2.getTag();
                    if ((imageContainer2 != null) && (imageContainer2.getRequestUrl().equals("") == false)) {
                        imageContainer2.cancelRequest();
                    }
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }

            final Status tweet = tweets.get(pos);
            final Status tweet_retweetedStatus = getRetweet(tweet);

            if (pref_enable_log_statuses) {
                WriteLog.write(context, tweet_retweetedStatus);
            }

            String statusText = pref_enable_htmlescape ? HtmlEscape.escape(tweet_retweetedStatus.getText()) : tweet_retweetedStatus.getText();

            if (tweet.isRetweet()) {
                fontcolor_screenname = pref_tl_fontcolor_screenname_retweeted;
                fontcolor_username = pref_tl_fontcolor_username_retweeted;
                fontcolor_statustext = pref_tl_fontcolor_statustext_retweeted;
                fontcolor_createdat = pref_tl_fontcolor_createdat_retweeted;
                fontcolor_source = pref_tl_fontcolor_source_retweeted;

                if (tweet.getURLEntities() != null) {
                    statusText = replaceAllUrl(statusText, fontcolor_statustext, tweet_retweetedStatus.getURLEntities(), true);
                }

                if (tweet.getHashtagEntities() != null) {
                    statusText = replaceAllHashtag(statusText, fontcolor_statustext, tweet_retweetedStatus.getHashtagEntities());
                }

                if (tweet.getUserMentionEntities() != null) {
                    statusText = replaceAllUser(statusText, fontcolor_statustext, tweet_retweetedStatus.getUserMentionEntities());
                }

                if (tweet_retweetedStatus.getURLEntities() != null) {
                    if (tweet.getURLEntities() != null) {
                        if (tweet.getURLEntities().equals(tweet_retweetedStatus.getURLEntities()) == false) {
                            statusText = replaceAllUrl(statusText, fontcolor_statustext, tweet_retweetedStatus.getURLEntities(), true);
                        }
                    } else {
                        statusText = replaceAllUrl(statusText, fontcolor_statustext, tweet_retweetedStatus.getURLEntities(), true);
                    }
                }

                if (tweet_retweetedStatus.getHashtagEntities() != null) {
                    if (tweet.getHashtagEntities() != null) {
                        if (tweet.getHashtagEntities().equals(tweet_retweetedStatus.getHashtagEntities()) == false) {
                            statusText = replaceAllHashtag(statusText, fontcolor_statustext, tweet_retweetedStatus.getHashtagEntities());
                        }
                    } else {
                        statusText = replaceAllHashtag(statusText, fontcolor_statustext, tweet_retweetedStatus.getHashtagEntities());
                    }
                }

                if (tweet_retweetedStatus.getUserMentionEntities() != null) {
                    if (tweet.getUserMentionEntities() != null) {
                        if (tweet.getUserMentionEntities().equals(tweet_retweetedStatus.getUserMentionEntities()) == false) {
                            statusText = replaceAllUser(statusText, fontcolor_statustext, tweet_retweetedStatus.getUserMentionEntities());
                        }
                    } else {
                        statusText = replaceAllUser(statusText, fontcolor_statustext, tweet_retweetedStatus.getUserMentionEntities());
                    }
                }

                if (pref_hide_item_usericon) {
                    holder.imageView1.setImageResource(drawable.btn_default);
                    holder.imageView2.setVisibility(View.GONE);

                    if (lightingColorFilterItemUsericon != null) {
                        try {
                            holder.imageView1.setColorFilter(lightingColorFilterItemUsericon);
                        } catch (final Exception e) {
                        }
                    }
                } else if (pref_hide_item_myicon && ((tweet.getUser().getScreenName().equals(screenName)) || (tweet_retweetedStatus.getUser().getScreenName().equals(screenName)))) {
                    holder.imageView1.setImageResource(drawable.btn_default);
                    holder.imageView2.setVisibility(View.GONE);

                    if (lightingColorFilterItemUsericon != null) {
                        try {
                            holder.imageView1.setColorFilter(lightingColorFilterItemUsericon);
                        } catch (final Exception e) {
                        }
                    }
                } else {
                    getIcon(holder.imageView1, tweet_retweetedStatus.getUser().getProfileImageURL().toString());

                    holder.imageView2.setVisibility(View.VISIBLE);
                    getIcon(holder.imageView2, tweet.getUser().getProfileImageURL().toString());

                    if (lightingColorFilterItemUsericon != null) {
                        try {
                            holder.imageView1.setColorFilter(lightingColorFilterItemUsericon);
                            holder.imageView2.setColorFilter(lightingColorFilterItemUsericon);
                        } catch (final Exception e) {
                        }
                    }
                }
            } else {
                fontcolor_screenname = pref_tl_fontcolor_screenname;
                fontcolor_username = pref_tl_fontcolor_username;
                fontcolor_statustext = pref_tl_fontcolor_statustext;
                fontcolor_createdat = pref_tl_fontcolor_createdat;
                fontcolor_source = pref_tl_fontcolor_source;

                if (tweet.getURLEntities() != null) {
                    statusText = replaceAllUrl(statusText, fontcolor_statustext, tweet.getURLEntities(), true);
                }

                if (tweet.getHashtagEntities() != null) {
                    statusText = replaceAllHashtag(statusText, fontcolor_statustext, tweet.getHashtagEntities());
                }

                if (tweet.getUserMentionEntities() != null) {
                    statusText = replaceAllUser(statusText, fontcolor_statustext, tweet.getUserMentionEntities());
                }

                holder.imageView2.setVisibility(View.GONE);
                if (pref_hide_item_usericon) {
                    holder.imageView1.setImageResource(drawable.btn_default);
                } else if (pref_hide_item_myicon && (tweet.getUser().getScreenName().equals(screenName))) {
                    holder.imageView1.setImageResource(drawable.btn_default);
                } else {
                    getIcon(holder.imageView1, tweet.getUser().getProfileImageURL().toString());

                    if (lightingColorFilterItemUsericon != null) {
                        try {
                            holder.imageView1.setColorFilter(lightingColorFilterItemUsericon);
                        } catch (final Exception e) {
                        }
                    }
                }
            }

            if (pref_enable_inline_img_wifi && (!pref_enable_tl_speedy)) {
                if (tweet_retweetedStatus.getExtendedMediaEntities() != null) {
                    if (tweet_retweetedStatus.getExtendedMediaEntities().length > 1) {
                        statusText = replaceAllMedia(statusText, fontcolor_statustext, tweet_retweetedStatus.getExtendedMediaEntities());
                    } else {
                        statusText = replaceAllMedia(statusText, fontcolor_statustext, tweet_retweetedStatus.getMediaEntities());
                    }
                } else if (tweet_retweetedStatus.getMediaEntities() != null) {
                    statusText = replaceAllMedia(statusText, fontcolor_statustext, tweet_retweetedStatus.getMediaEntities());
                }

                statusText = urlUtil.expand_image_url(statusText, pref_tl_imagesize_string);
            }

            final View[] views = {holder.imageView1, holder.imageView2, holder.textView, holder.view};
            if (checkMute(tweet)) {
                viewMute(views);
                return view;
            } else {
                viewMute(false, holder.imageView1, holder.imageView2, holder.textView, holder.view);
            }

            try {
                final ImageGetter ig =
                        (pref_enable_inline_img_async) ? (new ImageGetter2(holder.textView, context, pref_tl_imagesize))
                                : (new ImageGetter3(holder.textView, context, pref_tl_imagesize, lightingColorFilterItemInlineimg, pref_timeout_connection_imgtag, pref_timeout_so_imgtag));
                holder.textView.setText(Html.fromHtml(getViewStatusTimelineItem(pos, statusText), ig, null));
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }

            try {
                holder.textView.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public final void onClick(final View view) {
                        getViewTextViewOnClick(pos);
                    }
                });
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }

            try {
                holder.imageView1.setOnClickListener(new OnClickListener() {
                    @Override
                    public final void onClick(View v) {
                        getViewImageViewOnClick(pos);
                    }
                });
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }

            try {
                holder.imageView2.setOnClickListener(new OnClickListener() {
                    @Override
                    public final void onClick(View v) {
                        getViewImageViewOnClick(pos);
                    }
                });
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }

        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        return view;
    }

    private void getViewImageViewOnClick(final int pos) {
        try {
            final Status tweet = tweets.get(pos);
            final Status tweet_retweetedStatus = getRetweet(tweet);

            final Status finalTweet_menu = pref_enable_tweetmenu_check_tweet ? (getTwitter(checkIndexFromPrefTwtr(), false).showStatus(tweet.getId())) : (tweet);
            final Status finalTweet_menu_RetweetedStatus = pref_enable_tweetmenu_check_tweet ? (getRetweet(finalTweet_menu)) : (tweet_retweetedStatus);
            final String finalTweet_menu_screenName = finalTweet_menu.getUser().getScreenName();
            final String finalTweet_menu_RetweetedStatus_screenName = finalTweet_menu_RetweetedStatus.getUser().getScreenName();

            final String[] finalITEM = getTweetMenuItemArray(finalTweet_menu, finalTweet_menu_RetweetedStatus);

            new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info).setTitle("@"
                    + finalTweet_menu_screenName
                    + ":"
                    + System.getProperty("line.separator")
                    + " "
                    + finalTweet_menu.getText()
                    + ((finalTweet_menu.isRetweet()) ? ("( @" + finalTweet_menu_RetweetedStatus_screenName + ":" + System.getProperty("line.separator") + " "
                    + finalTweet_menu_RetweetedStatus.getText() + " )") : (""))

            ).setItems(finalITEM, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(final DialogInterface dialog, final int which) {

                    if ((finalITEM[which].startsWith("@")) || (finalITEM[which].startsWith(" @"))) {
                        final String screenName = (finalITEM[which].replace(" @", "")).replace("@", "");
                        startTlUser(checkIndexFromScreenname(screenName), screenName);
                    } else if (finalITEM[which].startsWith(context.getString(R.string.mention))) {
                        startUpdateTweet(finalTweet_menu.getId(), "@" + finalTweet_menu_screenName);
                    } else if (finalITEM[which].startsWith(context.getString(R.string.add_rt))) {
                        if (pref_confirmdialog_rt) {
                            final boolean[] checkedItems = getConfirmdialogCheckedItems();
                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_rt).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            }).setPositiveButton(R.string.confirmdialog_rt, new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface dialog, final int which) {
                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                        if (checkedItems[i] == true) {
                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                afterAction("onaction_rt", rt(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus));
                                            }
                                        }
                                    }
                                }
                            }).create().show();

                        } else {
                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                        if (checkUniq(idx)) {
                                            afterAction("onaction_rt", rt(idx, finalTweet_menu_RetweetedStatus));
                                        }
                                    }
                                }
                            } else {
                                afterAction("onaction_rt", rt(checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus));
                            }
                        }
                    } else if (finalITEM[which].startsWith(context.getString(R.string.rt_who_add))) {
                        if (!((Activity) context).isFinishing()) {
                            ((Activity) context).showDialog(R.string.rt_who_add);
                        }

                        final int retweetCount = finalTweet_menu.getRetweetCount();
                        if (retweetCount > 0) {
                            final ArrayList<String> screenNames = new ArrayList<String>(retweetCount);
                            final ResponseList<Status> retweetedStatusList =
                                    getRetweetsList(checkIndexFromScreenname(finalTweet_menu_RetweetedStatus_screenName), finalTweet_menu_RetweetedStatus.getId());

                            if (retweetedStatusList != null) {
                                for (final Status retweetedStatusListStatus : retweetedStatusList) {
                                    screenNames.add("@" + retweetedStatusListStatus.getUser().getScreenName());
                                }
                                final String[] screenNames2 = screenNames.toArray(new String[screenNames.size()]);
                                new AlertDialog.Builder(context).setTitle(R.string.rt_who_add).setItems(screenNames2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(final DialogInterface dialog, final int which) {
                                        final String screenName = screenNames2[which].replace("@", "");
                                        startTlUser(checkIndexFromScreenname(screenName), screenName);
                                    }
                                }).create().show();
                            }
                            try {
                                ((Activity) context).dismissDialog(R.string.rt_who_add);
                            } catch (final IllegalArgumentException e) {
                            }
                        }
                    } else if (finalITEM[which].startsWith(context.getString(R.string.add_userrt))) {
                        if (pref_confirmdialog_userrt) {
                            final boolean[] checkedItems = getConfirmdialogCheckedItems();

                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_userrt).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            }).setPositiveButton(R.string.confirmdialog_userrt, new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface dialog, final int which) {
                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                        if (checkedItems[i] == true) {
                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                afterAction("onaction_userrt", userrt(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu));
                                            }
                                        }
                                    }
                                }
                            }).create().show();
                        } else {
                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                        if (checkUniq(idx)) {
                                            afterAction("onaction_userrt", userrt(idx, finalTweet_menu));
                                        }
                                    }
                                }
                            } else {
                                afterAction("onaction_userrt", userrt(checkIndexFromPrefTwtr(), finalTweet_menu));
                            }
                        }
                    } else if (finalITEM[which].startsWith(context.getString(R.string.fav_create))) {
                        new Thread(new Runnable() {
                            public final void run() {

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {
                                        if (pref_confirmdialog_fav) {
                                            final boolean[] checkedItems = getConfirmdialogCheckedItems();
                                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_fav_create).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                    checkedItems[which] = isChecked;
                                                }
                                            }).setPositiveButton(R.string.confirmdialog_fav_create, new DialogInterface.OnClickListener() {
                                                @Override
                                                public final void onClick(final DialogInterface dialog, final int which) {
                                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                                        if (checkedItems[i] == true) {
                                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                                afterAction("onaction_fav_create", fav(true, checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus));
                                                            }
                                                        }
                                                    }
                                                }
                                            }).create().show();

                                        } else {
                                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                                        if (checkUniq(idx)) {
                                                            afterAction("onaction_fav_create", fav(true, idx, finalTweet_menu_RetweetedStatus));
                                                        }
                                                    }
                                                }
                                            } else {
                                                afterAction("onaction_fav_create", fav(true, checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus));
                                            }
                                        }

                                    }
                                });
                            }
                        }).start();
                    } else if (finalITEM[which].startsWith(context.getString(R.string.fav_destroy))) {
                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        if (pref_confirmdialog_fav) {
                                            final boolean[] checkedItems = getConfirmdialogCheckedItems();
                                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_fav_destroy).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                    checkedItems[which] = isChecked;
                                                }
                                            }).setPositiveButton(R.string.confirmdialog_fav_destroy, new DialogInterface.OnClickListener() {
                                                @Override
                                                public final void onClick(final DialogInterface dialog, final int which) {
                                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                                        if (checkedItems[i] == true) {
                                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                                afterAction("onaction_fav_create", fav(false, checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus));
                                                            }
                                                        }
                                                    }
                                                }
                                            }).create().show();
                                        } else {
                                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                                        if (checkUniq(idx)) {
                                                            afterAction("onaction_fav_destroy", fav(false, idx, finalTweet_menu_RetweetedStatus));
                                                        }
                                                    }
                                                }
                                            } else {
                                                afterAction("onaction_fav_destroy", fav(false, checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus));
                                            }
                                        }

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.add_favrt))) {
                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {
                                        if (pref_confirmdialog_fav || pref_confirmdialog_rt) {
                                            final boolean[] checkedItems = getConfirmdialogCheckedItems();

                                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_favrt).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                    checkedItems[which] = isChecked;
                                                }
                                            }).setPositiveButton(R.string.confirmdialog_favrt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public final void onClick(final DialogInterface dialog, final int which) {
                                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                                        if (checkedItems[i] == true) {
                                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                                fav(true, checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus);
                                                                final String result = rt(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus);
                                                                afterAction("onaction_favrt", result.replace(context.getString(R.string.done_rt), context.getString(R.string.done_favrt)));
                                                            }
                                                        }
                                                    }
                                                }
                                            }).create().show();
                                        } else {
                                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                                        if (checkUniq(idx)) {
                                                            fav(true, idx, finalTweet_menu_RetweetedStatus);
                                                            final String result = rt(idx, finalTweet_menu_RetweetedStatus);
                                                            afterAction("onaction_favrt", result.replace(context.getString(R.string.done_rt), context.getString(R.string.done_favrt)));
                                                        }
                                                    }
                                                }
                                            } else {
                                                fav(true, checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus);
                                                final String result = rt(checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus);
                                                afterAction("onaction_favrt", result.replace(context.getString(R.string.done_rt), context.getString(R.string.done_favrt)));
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.add_favuserrt))) {
                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        if (pref_confirmdialog_fav || pref_confirmdialog_userrt) {
                                            final boolean[] checkedItems = getConfirmdialogCheckedItems();

                                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_favuserrt).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                    checkedItems[which] = isChecked;
                                                }
                                            }).setPositiveButton(R.string.confirmdialog_favuserrt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public final void onClick(final DialogInterface dialog, final int which) {
                                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                                        if (checkedItems[i] == true) {
                                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                                fav(true, checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus);
                                                                final String result = userrt(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu);
                                                                afterAction("onaction_favuserrt", result.replace(context.getString(R.string.done_userrt), context.getString(R.string.done_favuserrt)));
                                                            }
                                                        }
                                                    }
                                                }
                                            }).create().show();
                                        } else {
                                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                                        if (checkUniq(idx)) {
                                                            fav(true, idx, finalTweet_menu_RetweetedStatus);
                                                            final String result = userrt(idx, finalTweet_menu);
                                                            afterAction("onaction_favrt", result.replace(context.getString(R.string.done_userrt), context.getString(R.string.done_favuserrt)));
                                                        }
                                                    }
                                                }
                                            } else {
                                                fav(true, checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus);
                                                final String result = userrt(checkIndexFromPrefTwtr(), finalTweet_menu);
                                                afterAction("onaction_favrt", result.replace(context.getString(R.string.done_userrt), context.getString(R.string.done_favuserrt)));
                                            }
                                        }

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.fav_who_add))) {
                        if (!((Activity) context).isFinishing()) {
                            ((Activity) context).showDialog(R.string.fav_who_add);
                        }

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        final long favcount = getFavCount(finalTweet_menu_RetweetedStatus);
                                        if (favcount > 0) {
                                            final String[] screenNames = getWhoAddFav(favcount, finalTweet_menu_RetweetedStatus);
                                            if (screenNames != null) {
                                                new AlertDialog.Builder(context).setTitle(R.string.fav_who_add).setItems(screenNames, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public final void onClick(final DialogInterface dialog, final int which) {
                                                        final String screenName = screenNames[which].replace("@", "");
                                                        startTlUser(checkIndexFromScreenname(screenName), screenName);
                                                    }
                                                }).create().show();
                                            }
                                        }
                                        try {
                                            ((Activity) context).dismissDialog(R.string.fav_who_add);
                                        } catch (IllegalArgumentException e) {
                                        }

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.show_mention))) {

                        final Intent intent = new Intent();
                        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.StatusTl");
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.putExtra("statusId", Long.toString(finalTweet_menu.getId())); // not finalTweet_menu2
                        intent.putExtra("userName", finalTweet_menu_screenName);
                        context.startActivity(intent);

                    } else if (finalITEM[which].startsWith(context.getString(R.string.share_link))) {

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        final ArrayList<String> uris = new ArrayList<String>(11);

                                        uris.add(context.getString(R.string.share_tweetlink));

                                        if (finalTweet_menu.getURLEntities() != null) {
                                            for (final URLEntity entity : finalTweet_menu.getURLEntities()) {
                                                uris.add(entity.getURL());
                                                if (pref_enable_expand_uri_twitter_tweetmenu_wifi) {
                                                    uris.add(entity.getExpandedURL());
                                                }
                                                if (pref_enable_expand_uri_thirdparty_tweetmenu_wifi) {
                                                    if (checkNetworkUtil.isConnected()) {
                                                        try {
                                                            final String longuri = urlUtil.expand_uri(entity.getURL());
                                                            if ((longuri.equals("") == false) && (longuri.equals(entity.getURL()) == false)) {
                                                                uris.add(longuri);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (finalTweet_menu.getMediaEntities() != null) {
                                            for (final MediaEntity entity : finalTweet_menu.getMediaEntities()) {
                                                uris.add(entity.getURL());
                                                if (pref_enable_expand_uri_twitter_tweetmenu_wifi) {
                                                    uris.add(entity.getExpandedURL());
                                                }
                                                if (pref_enable_expand_uri_thirdparty_tweetmenu_wifi) {
                                                    if (checkNetworkUtil.isConnected()) {
                                                        try {
                                                            final String longuri = urlUtil.expand_uri(entity.getURL());
                                                            if ((longuri.equals("") == false) && (longuri.equals(entity.getURL()) == false)) {
                                                                uris.add(longuri);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        final Matcher matcher1 = pattern_urlTcoHttpHttps.matcher(finalTweet_menu_RetweetedStatus.getText());
                                        while (matcher1.find()) {
                                            uris.add(matcher1.group(0));
                                            try {
                                                if (pref_enable_expand_uri_thirdparty_tweetmenu_wifi) {
                                                    if (checkNetworkUtil.isConnected()) {
                                                        try {
                                                            final String longuri = urlUtil.expand_uri(matcher1.group(0));
                                                            if ((longuri.equals("") == false) && (longuri.equals(matcher1.group(0)) == false)) {
                                                                uris.add(longuri);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            } catch (final Exception e) {
                                                WriteLog.write(context, e);
                                            }
                                        }

                                        final Matcher matcher2 = pattern_urlHttpHttps.matcher(finalTweet_menu_RetweetedStatus.getText());
                                        while (matcher2.find()) {
                                            uris.add(matcher2.group(0));
                                            try {
                                                if (pref_enable_expand_uri_thirdparty_tweetmenu_wifi) {
                                                    if (checkNetworkUtil.isConnected()) {
                                                        try {
                                                            final String longuri = urlUtil.expand_uri(matcher2.group(0));
                                                            if ((longuri.equals("") == false) && (longuri.equals(matcher2.group(0)) == false)) {
                                                                uris.add(longuri);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            } catch (final Exception e) {
                                                WriteLog.write(context, e);
                                            }
                                        }

                                        collectionsUtil.removeDuplicate(uris);
                                        Collections.sort(uris);

                                        final String[] uris2 = uris.toArray(new String[uris.size()]);
                                        new AlertDialog.Builder(context).setTitle(R.string.share_link).setItems(uris2, new DialogInterface.OnClickListener() {
                                            @Override
                                            public final void onClick(final DialogInterface dialog, final int which) {
                                                if (uris2[which].equals(context.getString(R.string.share_tweetlink))) {
                                                    final String finalStatusUrl =
                                                            "https://twitter.com/" + tweet_retweetedStatus.getUser().getScreenName() + "/status/" + Long.toString(tweet_retweetedStatus.getId());
                                                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalStatusUrl));
                                                    context.startActivity(intent);
                                                } else if (uris2[which].startsWith("http")) {
                                                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uris2[which]));
                                                    context.startActivity(intent);
                                                }
                                            }
                                        }).create().show();

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.share_text))) {

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        String text = finalTweet_menu_RetweetedStatus.getText();

                                        if (pref_sendintent_text_expanduri) {
                                            final Matcher matcher = ListAdapter.pattern_urlHttpHttpsShortened.matcher(text);
                                            String pregroup = "";
                                            while (matcher.find()) {
                                                final String group = matcher.group(0);
                                                if (group.equals(pregroup) == false) {
                                                    pregroup = group;
                                                    if (checkNetworkUtil.isConnected()) {
                                                        try {
                                                            final String longuri = urlUtil.expand_uri(group);
                                                            if ((longuri.equals("") == false) && (longuri.equals(group) == false)) {
                                                                text = text.replaceAll(group, longuri);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        final Intent intent3 = new Intent(Intent.ACTION_SEND);
                                        intent3.setType("text/plain");
                                        intent3.putExtra(Intent.EXTRA_TEXT, text);
                                        context.startActivity(intent3);

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.pak))) {

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {

                                        if (pref_confirmdialog_pak) {
                                            final boolean[] checkedItems = getConfirmdialogCheckedItems();

                                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_pak).setCancelable(true).setMultiChoiceItems(finalOurScreenNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                    checkedItems[which] = isChecked;
                                                }
                                            }).setPositiveButton(R.string.confirmdialog_pak, new DialogInterface.OnClickListener() {
                                                @Override
                                                public final void onClick(final DialogInterface dialog, final int which) {
                                                    for (int i = 0; i < finalOurScreenNames.length; i++) {
                                                        if (checkedItems[i] == true) {
                                                            if ((finalOurScreenNames[i]).equals("") == false) {
                                                                if (pref_fav_at_the_same_time_pak) {
                                                                    fav(true, checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus);
                                                                }
                                                                if (pref_retweet_at_the_same_time_pak) {
                                                                    rt(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus);
                                                                }
                                                                afterAction("onaction_pak", pak(checkIndexFromScreenname(finalOurScreenNames[i]), finalTweet_menu_RetweetedStatus));
                                                            }
                                                        }
                                                    }
                                                }
                                            }).create().show();
                                        } else {
                                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                                        if (checkUniq(idx)) {
                                                            if (pref_fav_at_the_same_time_pak) {
                                                                fav(true, idx, finalTweet_menu_RetweetedStatus);
                                                            }
                                                            if (pref_retweet_at_the_same_time_pak) {
                                                                rt(idx, finalTweet_menu_RetweetedStatus);
                                                            }
                                                            afterAction("onaction_pak", pak(idx, finalTweet_menu_RetweetedStatus));
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (pref_fav_at_the_same_time_pak) {
                                                    fav(true, checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus);
                                                }
                                                if (pref_retweet_at_the_same_time_pak) {
                                                    rt(checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus);
                                                }
                                                afterAction("onaction_pak", pak(checkIndexFromPrefTwtr(), finalTweet_menu_RetweetedStatus));
                                            }
                                        }

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.share_place))) {

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {
                                        try {
                                            final String geoUri =
                                                    StringUtil.getGeoUriString(finalTweet_menu_RetweetedStatus.getGeoLocation().getLatitude(), finalTweet_menu_RetweetedStatus.getGeoLocation().getLongitude(), pref_map_zoom);
                                            final Intent intent_map = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                                            context.startActivity(intent_map);
                                        } catch (final ActivityNotFoundException e) {
                                            WriteLog.write(context, e);
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.del))) {

                        new Thread(new Runnable() {
                            public final void run() {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public final void run() {
                                        new AlertDialog.Builder(context).setTitle(context.getString(R.string.del) + " " + Long.toString(finalTweet_menu.getId()))
                                                // not finalTweet_menu2
                                                .setMessage(context.getString(R.string.confirm_del) + ":" + NL + " @" + finalTweet_menu.getUser() // not finalTweet_menu2
                                                        .getScreenName() + ":" + NL + "  " + finalTweet_menu.getText())
                                                // not finalTweet_menu2
                                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public final void onClick(final DialogInterface dialog, final int which) {
                                                        afterAction("onaction_del", del(finalTweet_menu, "")); // not finalTweet_menu2
                                                    }
                                                }).create().show();

                                    }
                                });
                            }
                        }).start();

                    } else if (finalITEM[which].startsWith(context.getString(R.string.add_mute))) {

                        final String[] ITEM3 = {context.getString(R.string.add_mute_screenname), context.getString(R.string.add_mute_source)};

                        new AlertDialog.Builder(context).setTitle(R.string.add_mute).setItems(ITEM3, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                if (which == 0) {
                                    if (finalTweet_menu.isRetweet()) {
                                        final String[] str_items = {finalTweet_menu_RetweetedStatus_screenName, finalTweet_menu_screenName};
                                        final boolean[] flags = new boolean[2];
                                        new AlertDialog.Builder(context).setTitle(R.string.confirm_add_mute_screenname).setMultiChoiceItems(str_items, flags, new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                                flags[which] = isChecked;
                                            }
                                        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                if (flags[0] || flags[1]) {
                                                    pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                                    final String pref_mute_screenname = pref_app.getString("pref_mute_screenname", "");
                                                    final SharedPreferences.Editor editor = pref_app.edit();
                                                    editor.putString("pref_mute_screenname", pref_mute_screenname + ((pref_mute_screenname.endsWith(",")) ? "" : ",")
                                                            + (flags[0] ? str_items[0] + "," : "") + (flags[1] ? str_items[1] + "," : ""));
                                                    editor.commit();
                                                }
                                            }
                                        }).show();
                                    } else {
                                        new AlertDialog.Builder(context).setTitle(R.string.add_mute_screenname).setMessage(context.getString(R.string.confirm_add_mute_screenname) + ":" + NL + " @"
                                                + finalTweet_menu_RetweetedStatus_screenName).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public final void onClick(final DialogInterface dialog, final int which) {
                                                pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                                final String pref_mute_screenname = pref_app.getString("pref_mute_screenname", "");
                                                final SharedPreferences.Editor editor = pref_app.edit();
                                                editor.putString("pref_mute_screenname", pref_mute_screenname + ((pref_mute_screenname.endsWith(",")) ? "" : ",")
                                                        + finalTweet_menu_RetweetedStatus_screenName + ",");
                                                editor.commit();
                                            }
                                        }).create().show();
                                    }
                                } else if (which == 1) {
                                    new AlertDialog.Builder(context).setTitle(R.string.add_mute_source).setMessage(context.getString(R.string.confirm_add_mute_source) + ":" + NL + " "
                                            + finalTweet_menu_RetweetedStatus.getSource()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public final void onClick(final DialogInterface dialog, final int which) {
                                            pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                            final String pref_mute_source = pref_app.getString("pref_mute_source", "");
                                            final SharedPreferences.Editor editor = pref_app.edit();
                                            editor.putString("pref_mute_source", pref_mute_source + ((pref_mute_source.endsWith(",")) ? "" : ",")
                                                    + finalTweet_menu_RetweetedStatus.getSource().replaceAll("<[^>]+?>", "") + ",");
                                            editor.commit();
                                        }
                                    }).create().show();
                                }
                            }
                        }).create().show();
                    } else if (finalITEM[which].startsWith(context.getString(R.string.develop_bookmark))) {
                        final String[] ITEM3 = {context.getString(R.string.save), context.getString(R.string.load)};

                        new AlertDialog.Builder(context).setTitle(R.string.develop_bookmark).setItems(ITEM3, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                if (which == 0) {
                                    pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                    final SharedPreferences.Editor editor = pref_app.edit();
                                    editor.putString("develop_bookmark", Long.toString(finalTweet_menu.getId())); // not finalTweet_menu2
                                    editor.commit();
                                    pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                } else if (which == 1) {
                                    pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                    final long statusId = Long.parseLong(pref_app.getString("develop_bookmark", "-1"));
                                    int pos = -1;
                                    if (statusId > 0) {
                                        pos = searchTweetPositionById(statusId, true);
                                    }
                                    if (pos > -1) {
                                        listViewSetSelection(pos, false);
                                    }
                                    pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                                }
                            }
                        }).create().show();
                    }
                }
            }).create().show();
        } catch (final TwitterException e) {
            try {
                twitterException(e);
            } catch (Exception ex) {
                toast(context.getString(R.string.cannot_access_twitter));
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
    }

    private void getViewTextViewOnClick(final int pos) {

        WriteLog.write(context, "getViewTextViewOnClick()");

        if (isFix == false) {
            WriteLog.write(context, "getViewTextViewOnClick() !isFix pos: " + pos);
            setSelectPos(pos + 1);
            listViewSetSelection(getSelectPos(), false);
        }
        isFix = !isFix;

        setMaxId(getSelectPos());

    }

    private String getViewStatusTimelineItem(final int pos, final String statusText) {
        final Status tweet = tweets.get(pos);
        final Status tweet_retweetedStatus = getRetweet(tweet);
        final String finalStatusRetweetedbyUserScreenname = (tweet.isRetweet()) ? (tweet.getUser().getScreenName()) : (" - ");
        final String finalStatusUrl = "https://twitter.com/" + tweet_retweetedStatus.getUser().getScreenName() + "/status/" + Long.toString(tweet_retweetedStatus.getId());

        // final int sbInitSize = 2670; // 1997 * 4/3
        final StringBuilder timelineItemBuilder = new StringBuilder(2670);

        timelineItemBuilder.append((pref_tl_fontsize_small_old_retweet && tweet.isRetweet() && (System.currentTimeMillis() - tweet_retweetedStatus.getCreatedAt().getTime() > 2 * 24 * 3600 * 1000))
                ? "<small><small>" : "");
        timelineItemBuilder.append(pref_tl_fontsize_large_screenname ? "<big>" : "");
        timelineItemBuilder.append("<font color=\"");
        timelineItemBuilder.append(fontcolor_screenname);
        timelineItemBuilder.append("\">");
        timelineItemBuilder.append(pref_hide_link_screenname ? "" : "<a href=\"https://twitter.com/" + tweet_retweetedStatus.getUser().getScreenName() + "\">");
        timelineItemBuilder.append("@");
        timelineItemBuilder.append(tweet_retweetedStatus.getUser().getScreenName());
        timelineItemBuilder.append(pref_hide_link_screenname ? "" : "</a>");
        timelineItemBuilder.append("</font>");
        timelineItemBuilder.append(pref_tl_fontsize_large_screenname ? "</big>" : "");
        timelineItemBuilder.append(" ");

        if (!pref_enable_singleline) {
            timelineItemBuilder.append("<font color=\"");
            timelineItemBuilder.append(fontcolor_statustext);
            timelineItemBuilder.append("\">[ </font>");
            timelineItemBuilder.append(pref_tl_fontsize_small_username ? "<small>" : "");
            timelineItemBuilder.append("<font color=\"");
            timelineItemBuilder.append(fontcolor_username);
            timelineItemBuilder.append("\">");
            timelineItemBuilder.append(tweet_retweetedStatus.getUser().getName());
            timelineItemBuilder.append("</font>");
            timelineItemBuilder.append(pref_tl_fontsize_small_username ? "</small>" : "");
            timelineItemBuilder.append("<font color=\"");
            timelineItemBuilder.append(fontcolor_statustext);
            timelineItemBuilder.append("\"> ]</font>");
            timelineItemBuilder.append(BR);
        }
        timelineItemBuilder.append("<font color=\"");
        timelineItemBuilder.append(fontcolor_statustext);
        timelineItemBuilder.append("\">");
        if (pref_enable_singleline) {
            timelineItemBuilder.append(statusText);
        } else {
            timelineItemBuilder.append(statusText.replaceAll("\n", BR));
        }
        timelineItemBuilder.append("</font>");

        if (!pref_enable_singleline) {
            timelineItemBuilder.append(BR);
            timelineItemBuilder.append("<font color=\"");
            timelineItemBuilder.append(fontcolor_createdat);
            timelineItemBuilder.append("\">");
            timelineItemBuilder.append(pref_hide_link_createdat ? "" : "<a href=\"" + finalStatusUrl + "\">");
            timelineItemBuilder.append(pref_tl_fontsize_small_createdat ? "<small>" : "");
            timelineItemBuilder.append(DF.format(tweet_retweetedStatus.getCreatedAt()));
            timelineItemBuilder.append(pref_tl_fontsize_small_createdat ? "</small>" : "");
            timelineItemBuilder.append(pref_hide_link_createdat ? "" : "</a>");
            timelineItemBuilder.append("</font>");
            timelineItemBuilder.append(" ");
            timelineItemBuilder.append(pref_tl_fontsize_small_source ? "<small>" : "");
            timelineItemBuilder.append("<font color=\"");
            timelineItemBuilder.append(fontcolor_source);
            timelineItemBuilder.append("\">");
            timelineItemBuilder.append(pref_hide_link_source ? tweet_retweetedStatus.getSource().replaceAll("</?a[^>]*>", "") : tweet_retweetedStatus.getSource());
            timelineItemBuilder.append("</font>");
            timelineItemBuilder.append(pref_tl_fontsize_small_source ? "</small>" : "");

            if (!pref_hide_tweet_footer_action) {
                timelineItemBuilder.append(pref_tl_fontsize_small_action ? "<small>" : "");
                timelineItemBuilder.append("<font color=\"");
                timelineItemBuilder.append(fontcolor_source);
                timelineItemBuilder.append("\"> <a href=\"https://twitter.com/intent/tweet?in_reply_to=");
                timelineItemBuilder.append(Long.toString(tweet_retweetedStatus.getId()));
                timelineItemBuilder.append("&via=");
                timelineItemBuilder.append(tweet_retweetedStatus.getUser().getScreenName());
                timelineItemBuilder.append("\"><small><img src=\"reply\" ");
                timelineItemBuilder.append(imgSizeTagpart);
                timelineItemBuilder.append(" /></small></a> <a href=\"https://twitter.com/intent/retweet?tweet_id=");
                timelineItemBuilder.append(Long.toString(tweet_retweetedStatus.getId()));
                timelineItemBuilder.append("&via=");
                timelineItemBuilder.append(tweet_retweetedStatus.getUser().getScreenName());
                timelineItemBuilder.append("\"><small><img src=\"");
                timelineItemBuilder.append(tweet_retweetedStatus.isRetweetedByMe() ? "retweet_on" : (tweet_retweetedStatus.isRetweeted() ? "retweet_hover" : "retweet"));
                timelineItemBuilder.append("\" ");
                timelineItemBuilder.append(imgSizeTagpart);
                timelineItemBuilder.append(" /></small></a> <a href=\"https://twitter.com/intent/favorite?tweet_id=");
                timelineItemBuilder.append(Long.toString(tweet_retweetedStatus.getId()));
                timelineItemBuilder.append("\"><small><img src=\"");
                timelineItemBuilder.append(tweet_retweetedStatus.isFavorited() ? "favorite_on" : "favorite");
                timelineItemBuilder.append("\" ");
                timelineItemBuilder.append(imgSizeTagpart);
                timelineItemBuilder.append(" /></small></a></font>");
                timelineItemBuilder.append(pref_tl_fontsize_small_action ? "</small>" : "");
            }
            if (tweet.isRetweet()) {
                timelineItemBuilder.append((pref_tl_retweetedby_multiline > 1) ? BR : "");
                timelineItemBuilder.append(" <font color=\"");
                timelineItemBuilder.append(pref_tl_fontcolor_screenname_retweetedby);
                timelineItemBuilder.append("\"><a href=\"https://twitter.com/");
                timelineItemBuilder.append(finalStatusRetweetedbyUserScreenname);
                timelineItemBuilder.append("\">RTed by @");
                timelineItemBuilder.append(finalStatusRetweetedbyUserScreenname);
                timelineItemBuilder.append("</a>");
                if (tweet_retweetedStatus.getRetweetCount() > 0) {
                    timelineItemBuilder.append(" and ");
                    timelineItemBuilder.append(Long.toString(tweet_retweetedStatus.getRetweetCount()));
                    timelineItemBuilder.append(" users");
                }
                timelineItemBuilder.append("</font>");
                timelineItemBuilder.append((pref_tl_retweetedby_multiline > 2) ? BR : "");
                timelineItemBuilder.append(" ");
                timelineItemBuilder.append(pref_tl_fontsize_small_createdat ? "<small>" : "");
                timelineItemBuilder.append("<font color=\"");
                timelineItemBuilder.append(pref_tl_fontcolor_createdat_retweetedby);
                timelineItemBuilder.append("\">");
                timelineItemBuilder.append(DF.format(tweet.getCreatedAt()));
                timelineItemBuilder.append("</font>");
                timelineItemBuilder.append(pref_tl_fontsize_small_createdat ? "</small>" : "");
                timelineItemBuilder.append(" ");
                timelineItemBuilder.append(pref_tl_fontsize_small_source ? "<small>" : "");
                timelineItemBuilder.append("<font color=\"");
                timelineItemBuilder.append(pref_tl_fontcolor_source_retweetedby);
                timelineItemBuilder.append("\">");
                timelineItemBuilder.append(tweet.getSource());
                timelineItemBuilder.append("</font>");
                timelineItemBuilder.append(pref_tl_fontsize_small_source ? "</small>" : "");
                if (!pref_hide_tweet_footer_action) {
                    timelineItemBuilder.append(pref_tl_fontsize_small_action ? "<small>" : "");
                    timelineItemBuilder.append("<font color=\"");
                    timelineItemBuilder.append(fontcolor_source);
                    timelineItemBuilder.append("\"> <a href=\"https://twitter.com/intent/tweet?in_reply_to=");
                    timelineItemBuilder.append(Long.toString(tweet.getId()));
                    timelineItemBuilder.append("&via=");
                    timelineItemBuilder.append(tweet.getUser().getScreenName());
                    timelineItemBuilder.append("\"><small><img src=\"reply\" ");
                    timelineItemBuilder.append(imgSizeTagpart);
                    timelineItemBuilder.append(" /></small></a> <a href=\"https://twitter.com/intent/retweet?tweet_id=");
                    timelineItemBuilder.append(Long.toString(tweet.getId()));
                    timelineItemBuilder.append("&via=");
                    timelineItemBuilder.append(tweet.getUser().getScreenName());
                    timelineItemBuilder.append("\"><small><img src=\"");
                    timelineItemBuilder.append(tweet.isRetweetedByMe() ? "retweet_on" : (tweet.isRetweeted() ? "retweet_hover" : "retweet"));
                    timelineItemBuilder.append("\" ");
                    timelineItemBuilder.append(imgSizeTagpart);
                    timelineItemBuilder.append(" /></small></a> <a href=\"https://twitter.com/intent/favorite?tweet_id=");
                    timelineItemBuilder.append(Long.toString(tweet.getId()));
                    timelineItemBuilder.append("\"><small><img src=\"");
                    timelineItemBuilder.append(tweet.isFavorited() ? "favorite_on" : "favorite");
                    timelineItemBuilder.append("\" ");
                    timelineItemBuilder.append(imgSizeTagpart);
                    timelineItemBuilder.append(" /></small></a></font>");
                    timelineItemBuilder.append(pref_tl_fontsize_small_action ? "</small>" : "");
                }
            }
            if (tweet.getGeoLocation() != null) {
                timelineItemBuilder.append(BR);
                timelineItemBuilder.append(" <font color=\"");
                timelineItemBuilder.append(pref_tl_fontcolor_statustext_location);
                timelineItemBuilder.append("\"><a href=\"http://maps.google.com/maps?q=");
                timelineItemBuilder.append(tweet.getGeoLocation().getLatitude());
                timelineItemBuilder.append(",+");
                timelineItemBuilder.append(tweet.getGeoLocation().getLongitude());
                timelineItemBuilder.append("\">");
                timelineItemBuilder.append(tweet.getGeoLocation().getLatitude());
                timelineItemBuilder.append(",");
                timelineItemBuilder.append(tweet.getGeoLocation().getLongitude());
                timelineItemBuilder.append("</a></font>");
            }
        }
        timelineItemBuilder.append((pref_tl_fontsize_small_old_retweet && tweet.isRetweet() && (System.currentTimeMillis() - tweet_retweetedStatus.getCreatedAt().getTime() > 2 * 24 * 3600 * 1000))
                ? "</small></small>" : "");
        return timelineItemBuilder.toString();
    }

    private String[] getWhoAddFav(final long favCount, final Status tweet) {
        final ArrayList<String> screenNames = new ArrayList<String>(default_user_index_size);

        try {
            final String[] url_fav = { // URL
                    "https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + Long.toString(tweet.getId()), // Twitter
                    "http://aclog.koba789.com/api/tweets/show.json?id=" + Long.toString(tweet.getId()) // aclog
            };

            final Pattern[] p_fav = {
                    // Twitter
                    Pattern.compile("<a[^>]*?href=\"[^\"]*?/([a-zA-Z0-9_-]+)\"[^>]*?>[^<]*?<img[^>]*?src=\"https://[a|p]bs.twimg.com/[^>]*?>[^<]*?</a>", Pattern.DOTALL)};

            if (pref_fav_site == 5) {
                // aclog
                final String json = HttpsClient.https2data(context, url_fav[1], pref_timeout_connection, pref_timeout_so, default_charset);
                final ArrayList<Long> ids = new ArrayList<Long>(10);
                try {
                    final JSONObject rootObject = new JSONObject(json);
                    final JSONArray favoritersArray = rootObject.getJSONArray("favoriters");
                    for (int i = 0; i < favoritersArray.length(); i++) {
                        try {
                            ids.add(Long.parseLong(favoritersArray.getString(i)));
                        } catch (final Exception e) {
                            WriteLog.write(context, e);
                        }
                    }
                } catch (final JSONException e) {
                    WriteLog.write(context, e);
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
                final List<List<Long>> idsArray = CollectionsUtil.devide(ids, 100);
                for (final List<Long> idsTemp : idsArray) {
                    final long[] idsTempArray = CollectionsUtil.listLong2longarray(idsTemp);
                    final ResponseList<User> users = getTwitter(checkIndexFromPrefTwtr(), false).lookupUsers(idsTempArray);
                    for (final User user : users) {
                        screenNames.add("@" + user.getScreenName());
                    }
                }
            } else {
                // aclog以外
                final Matcher m_fav = p_fav[0].matcher(HttpsClient.https2data(context, url_fav[0], pref_timeout_connection, pref_timeout_so, default_charset));
                while (m_fav.find()) {
                    screenNames.add("@" + m_fav.group(1));
                }
            }

            return screenNames.toArray(new String[screenNames.size()]);
        } catch (final Exception e) {
            WriteLog.write(context, e);
            return null;
        }
    }

    private final String initUserOauth(final int index) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        pref_twtr = context.getSharedPreferences("Twitter_setting", 0); // MODE_PRIVATE == 0

        WriteLog.write(context, "init_user_oauth(): index: " + Integer.toString(index));

        String scrName = checkScreennameFromIndex(index);

        pref_timeout_t4j_connection = getPrefInt("pref_timeout_t4j_connection", "20000");
        WriteLog.write(context, "init_user_oauth(): pref_timeout_t4j_connection: " + pref_timeout_t4j_connection);
        pref_timeout_t4j_read = getPrefInt("pref_timeout_t4j_read", "120000");
        WriteLog.write(context, "init_user_oauth(): pref_timeout_t4j_read: " + pref_timeout_t4j_read);

        String pref_pictureUploadSite = pref_app.getString("pref_pictureuploadsite", MediaProvider.TWITTER.toString());
        WriteLog.write(context, "init_user_oauth(): pref_pictureUploadSite: " + pref_pictureUploadSite);

        String consumerKey = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_key_" + Integer.toString(index), ""));
        String consumerSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("consumer_secret_" + Integer.toString(index), ""));
        if (consumerKey.equals("") || consumerSecret.equals("")) {
            WriteLog.write(context, "(consumerKey.equals(\"\") || consumerSecret.equals(\"\"))");
            consumerKey = context.getString(R.string.default_consumerKey);
            consumerSecret = context.getString(R.string.default_consumerSecret);
        }
        WriteLog.write(context, "init_user_oauth(): consumerKey: " + consumerKey);
        WriteLog.write(context, "init_user_oauth(): consumerSecret: " + consumerSecret);

        oauthToken = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_" + Integer.toString(index), ""));
        WriteLog.write(context, "init_user_oauth(): oauthToken: " + oauthToken);
        WriteLog.write(context, "init_user_oauth(): oauthToken:: " + pref_twtr.getString("oauth_token_" + Integer.toString(index), ""));
        //		if (oauthToken.equals("")) {
        //			WriteLog.write(this, "(oauthToken.equals(\"\"))");SharedPreferences.Editor editor = pref_twtr.edit();
        //			editor.putString("index", "0");editor.remove("consumer_key_" + index);editor.remove("consumer_secret_" + index);
        //			editor.remove("oauth_token_" + index);editor.remove("oauth_token_secret_" + index);editor.remove("profile_image_url_" + index);
        //			editor.remove("screen_name_" + index);editor.remove("status_" + index);editor.commit();
        //		}
        oauthTokenSecret = MyCrypt.decrypt(context, crpKey, pref_twtr.getString("oauth_token_secret_" + Integer.toString(index), ""));
        WriteLog.write(context, "init_user_oauth(): oauthTokenSecret: " + oauthTokenSecret);
        WriteLog.write(context, "init_user_oauth(): oauthTokenSecret:: " + pref_twtr.getString("oauth_token_secret_" + Integer.toString(index), ""));

        try {
            confbuilder = new ConfigurationBuilder();
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        WriteLog.write(context, "init_user_oauth(): confbuilder1: " + confbuilder.toString());

        final String twitpicKey = ListAdapter.default_twitpicKey;
//		if (pref_pictureUploadSite.equals(MediaProvider.TWITPIC.toString())) {
//			confbuilder.setMediaProvider(pref_pictureUploadSite).setMediaProviderAPIKey(twitpicKey).setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read).setHttpRetryCount(3).setHttpRetryIntervalSeconds(10);// .setUseSSL(true)
//		} else {
        confbuilder.setMediaProvider(pref_pictureUploadSite).setOAuthAccessToken(oauthToken).setOAuthAccessTokenSecret(oauthTokenSecret).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setHttpConnectionTimeout(pref_timeout_t4j_connection).setHttpReadTimeout(pref_timeout_t4j_read);// .setUseSSL(true)
//		}
        WriteLog.write(context, "init_user_oauth(): confbuilder2: " + confbuilder.toString());

        Twitter twtr;
        try {
            conf = confbuilder.build();
            WriteLog.write(context, "init_user_oauth(): conf: " + conf.toString());

            twtr = new TwitterFactory(conf).getInstance();
            WriteLog.write(context, "init_user_oauth(): twtr: " + twtr.toString());
        } catch (final Exception e) {
            conf = null;
            twtr = null;
            WriteLog.write(context, e);
            toast(context.getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + context.getString(R.string.tryagain_oauth));
        }

        if (twtr != null) {
            try {
                scrName = twtr.getScreenName();
                WriteLog.write(context, "screenName: " + scrName);

                pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
                final SharedPreferences.Editor editor = pref_twtr.edit();
                editor.putString("screen_name_" + Integer.toString(index), scrName);
                editor.commit();
            } catch (final TwitterException e) {
                try {
                    twitterException(e);
                } catch (Exception ex) {
                    toast(context.getString(R.string.cannot_access_twitter));
                }
            } catch (final Exception e) {
                toast(context.getString(R.string.cannot_access_twitter) + System.getProperty("line.separator") + context.getString(R.string.tryagain_oauth));

                WriteLog.write(context, e);
            }
            try {
                final User user = twtr.showUser(scrName);
                final String profile_image_url = user.getProfileImageURL().toString();
                WriteLog.write(context, "profile_image_url: " + profile_image_url);

                pref_twtr = context.getSharedPreferences("Twitter_setting", 0);
                final SharedPreferences.Editor editor = pref_twtr.edit();
                editor.putString("profile_image_url_" + Integer.toString(index), profile_image_url);
                editor.commit();
            } catch (final TwitterException e) {
                try {
                    twitterException(e);
                } catch (Exception ex) {
                    toast(context.getString(R.string.cannot_access_twitter));
                }
            } catch (final Exception e) {
                toast(context.getString(R.string.exception) + System.getProperty("line.separator") + context.getString(R.string.tryagain_oauth));

                WriteLog.write(context, e);
            }
        }
        screenName = scrName;
        return scrName;
    }

    final boolean isConnected(final String shiobeStatus) {
        if ((shiobeStatus != null) && shiobeStatus.equals("available")) {
            return true;
        } else {
            return false;
        }
    }

    private final boolean isMyStatusOrMyRetweet(final String finalTweet_menu_screenName, final String finalTweet_menu_RetweetedStatus_screenName) {
        for (int idx = 0; idx < pref_user_index_size; idx++) {
            if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                if ((finalTweet_menu_screenName.equals(pref_twtr.getString("screen_name_" + idx, "")))
                        || (finalTweet_menu_RetweetedStatus_screenName.equals(pref_twtr.getString("screen_name_" + idx, "")))) {
                    return true;
                }
            }
        }
        return false;
    }

    private final String listUser(final boolean mode, final long listId, final String listName, final long userId, final String uScreenname) {
        final int idx = checkIndexFromListname(listName);

        if (mode) {
            try {
                getTwitter(idx, false).createUserListMember(listId, userId);

                return (context.getString(R.string.done_list_add) + ": " + listName + ": @" + uScreenname + " [@" + checkScreennameFromIndex(idx) + "]");
            } catch (final TwitterException e) {
                try {
                    twitterException(e);
                } catch (Exception ex) {
                    toast(context.getString(R.string.cannot_access_twitter));
                }
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }
        } else {
            try {
                getTwitter(idx, false).destroyUserListMember(listId, userId);
                WriteLog.write(context, context.getString(R.string.done_list_remove) + ": " + listName + ": @" + uScreenname + " [@" + checkScreennameFromIndex(idx) + "]");

                return (context.getString(R.string.done_list_remove) + ": " + listName + ": @" + uScreenname + " [@" + checkScreennameFromIndex(idx) + "]");
            } catch (final TwitterException e) {
                try {
                    twitterException(e);
                } catch (Exception ex) {
                    toast(context.getString(R.string.cannot_access_twitter));
                }
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }
        }

        return "";
    }

    private final void listViewSetItemCheckedAndSetSelection(final int pos) {
        if (pref_setselection_setitemchecked) {
            try {
                listView.setItemChecked(pos, true);
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }
            WriteLog.write(context, "listViewSetItemCheckedAndSetSelection(" + pos + ") listView.setItemChecked()");
        }

        try {
            notifyDataSetChanged();
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        WriteLog.write(context, "listViewSetItemCheckedAndSetSelection(" + pos + ") notifyDataSetChanged()");

        listView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    listView.setSelection(pos);
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
        });
        WriteLog.write(context, "listViewSetItemCheckedAndSetSelection(" + pos + ") listView.post listView.setSelection(" + pos + ")");
    }

    final void listViewSetSelection(final int pos, final boolean modeLoad) {
        final boolean pref_enable_smoothscroll = modeLoad ? pref_enable_smoothscroll_load : pref_enable_smoothscroll_search;
        if (pref_enable_smoothscroll) {
            WriteLog.write(context, "listViewSetSelection(" + pos + ") pref_enable_smoothscroll");

            try {
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPosition(pos);
                    }
                });
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }
            WriteLog.write(context, "listViewSetSelection(" + pos + ") listView.smoothScrollToPosition(" + pos + ")");
        } else {
            WriteLog.write(context, "listViewSetSelection(" + pos + ") !pref_enable_smoothscroll");

            if (pref_setselection_requestfocus) {
                try {
                    listView.requestFocus();
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
            if (pref_setselection_requestfocusfromtouch) {
                try {
                    listView.requestFocusFromTouch();
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
            if (pref_setselection_setselected) {
                try {
                    listView.setSelected(true);
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
            if (pref_setselection_setfocusable) {
                try {
                    listView.setFocusable(true);
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }
            if (pref_setselection_setfocusableintouchmode) {
                try {
                    listView.setFocusableInTouchMode(true);
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }
            }

            if (pref_setselection_triple) {
                if (pos < PLAY) {
                    listViewSetItemCheckedAndSetSelection(pos + 2);
                    listViewSetItemCheckedAndSetSelection(pos + 1);
                } else {
                    listViewSetItemCheckedAndSetSelection(pos - 2);
                    listViewSetItemCheckedAndSetSelection(pos - 1);
                }
            }
            listViewSetItemCheckedAndSetSelection(pos);
            WriteLog.write(context, "listViewSetSelection(" + pos + ") !pref_enable_smoothscroll listViewSetItemCheckedAndSetSelection(" + pos + ")");
        }

        try {
            notifyDataSetChanged();
            WriteLog.write(context, "listViewSetSelection(" + pos + ") notifyDataSetChanged()");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
    }

    private final void makeShortcut(final Intent shortcutIntent, final String shortcutName) {
        final Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        final Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        context.sendBroadcast(intent);
    }

    final void makeShortcutHomeTl(final String screenName, final boolean str) {
        makeShortcutTl(screenName, screenName + "#home", str);
    }

    final void makeShortcutMentionTl(final String screenName) {
        makeShortcutTl(screenName, screenName + "#mention", false);
    }

    final void makeShortcutSearchTl(final String queryStr, final String screenName, final boolean str) {
        makeShortcutTl(screenName, screenName + "?" + queryStr, str);
    }

    final void makeShortcutTl(final String screenName, final String uriString, final boolean str) {
        makeShortcut(uriStringToTlIntent(screenName, uriString), StringUtil.uriStringToShortcutName(screenName, uriString));
    }

    final void makeShortcutUri(final String uriString) {
        makeShortcut(uriStringToIntent(uriString), StringUtil.uriStringToShortcutName("", uriString));
    }

    final void makeShortcutUserfavTl(final String targetScreenName, final String screenName) {
        makeShortcutTl(screenName, targetScreenName + "#favorite", false);
    }

    final void makeShortcutUserlistTl(final long listId, final String listName, final String screenName, final boolean str) {
        makeShortcutTl(screenName, listName, str);
    }

    final void makeShortcutUserTl(final String targetScreenName, final String screenName) {
        makeShortcutTl(screenName, TWITTER_BASE_URI + targetScreenName, false);
    }

    final void notification(final int notificationId, final int icon, final String title, final String message, final String summary, final boolean autoCancel, final boolean onGoing,
                            final int ledColor, final boolean vibrate, final PendingIntent pendingIntent, final boolean useRemoteInput) {

        if (useRemoteInput) {
            if (action == null) {
                final String pref_develop_updatetweet_androidwear_choice = pref_app.getString("pref_develop_updatetweet_androidwear_choice", "");
                final String[] pref_develop_updatetweet_androidwear_choice_array = pref_develop_updatetweet_androidwear_choice.split(",");
                final Intent intent1 = new Intent(context, WearTweet.class).putExtra("mode", "t");
                final RemoteInput remoteInput = new RemoteInput.Builder(ListAdapter.EXTRA_VOICE_REPLY).setLabel(context.getString(R.string.develop_updatetweet_androidwear_choice_label)) //
                        .setChoices(pref_develop_updatetweet_androidwear_choice_array) //
                        .build();
                final PendingIntent pendingIntent1 = PendingIntent.getActivity(context, rc++, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                action =
                        new NotificationCompat.Action.Builder(R.drawable.reply, context.getString(R.string.develop_updatetweet_androidwear_choice_label), pendingIntent1).addRemoteInput(remoteInput).build();
            }
        }

        if (!((Activity) context).isFinishing()) {
            if (currentThreadIsUiThread()) {
                notificationPart(notificationId, icon, title, message, summary, autoCancel, onGoing, ledColor, vibrate, pendingIntent, useRemoteInput);
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        notificationPart(notificationId, icon, title, message, summary, autoCancel, onGoing, ledColor, vibrate, pendingIntent, useRemoteInput);
                    }
                });
            }
        }
    }

    private final void notification(final int notificationId, final String text, final int ledColor, final boolean vibrate) {
        if (pendingIntent == null) {
            final Intent intent = new Intent(context, UpdateTweet.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notification(notificationId, R.drawable.ic_launcher, context.getString(R.string.app_name_short), text, "", true, false, ledColor, vibrate, pendingIntent, true);
    }

    final void notificationPart(final int notificationId, final int icon, final String title, final String message, final String summary, final boolean autoCancel, final boolean onGoing,
                                final int ledColor, final boolean vibrate, final PendingIntent pendingIntent, final boolean useRemoteInput) {
        final Style style =
                (summary.equals("")) ? (new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title))
                        : (new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title).setSummaryText(summary));
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context).setAutoCancel(autoCancel).setOngoing(onGoing).setSmallIcon(icon).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setStyle(style);

        if (ledColor != Color.TRANSPARENT) {
            notificationBuilder.setLights(ledColor, 500, 500);
        }
        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (useRemoteInput) {
            notificationBuilder.extend(new NotificationCompat.WearableExtender().addAction(action));
        }

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    final void notificationShowIcon(final boolean showiconwear) {
        if (pendingIntent == null) {
            final Intent intent = new Intent(context, UpdateTweet.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notification(NOTIFY_RUNNING, R.drawable.ic_launcher, context.getString(R.string.app_name_short), context.getString(R.string.app_name)
                + context.getString(R.string.pendingintent_sammary_running), "", false, true, Color.TRANSPARENT, false, pendingIntent, false);
        if (showiconwear) {
            notification(NOTIFY_OPEN_UPDATETWEET, R.drawable.ic_launcher, context.getString(R.string.app_name_short), context.getString(R.string.app_name)
                    + context.getString(R.string.pendingintent_sammary_open_updatetweet), "", false, false, Color.TRANSPARENT, false, pendingIntent, true);
        }
    }

    private final String pak(final int index, final Status tweet) {
        try {
            String statusUserScreenname = "";
            String statusText = "";
            long statusId = 0;
            if (tweet.isRetweet()) {
                statusText = tweet.getRetweetedStatus().getText();
                statusUserScreenname = tweet.getRetweetedStatus().getUser().getScreenName();
                statusId = tweet.getRetweetedStatus().getId();
            } else {
                statusText = tweet.getText();
                statusUserScreenname = tweet.getUser().getScreenName();
                statusId = tweet.getId();
            }

            final StatusUpdate statusUpdate = new StatusUpdate(statusText);
            statusUpdate.setInReplyToStatusId(statusId);
            getTwitter(index, false).updateStatus(statusUpdate);

            return (context.getString(R.string.done_pak) + ": @" + statusUserScreenname + ": " + statusText + " " + " [@" + checkScreennameFromIndex(index) + "]");
        } catch (final TwitterException e) {
            try {
                twitterException(e);
            } catch (Exception ex) {
                toast(context.getString(R.string.cannot_access_twitter));
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final void playSound(final String key) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        // onaction_block_create	ブロック
        // onaction_block_destroy	ブロック
        // onaction_del				削除
        // onaction_fav_create		ふぁぼ
        // onaction_fav_destroy		ふぁぼ
        // onaction_favrt			ふぁぼ公
        // onaction_favuserrt		ふぁぼ+非公式リツイート
        // onaction_follow_create	フォロー
        // onaction_follow_remove	リムーブ
        // onaction_list_add		リスト
        // onaction_list_remove		リスト
        // onaction_pak				pak
        // onaction_r4s				R4S
        // onaction_rt				公式リツイート
        // onaction_userrt			非公式リツイート

        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean pref_enable_ringtone_ = pref_app.getBoolean("pref_enable_ringtone_" + (key.split("_"))[0], true);
        final String pref_ringtone_ = pref_app.getString("pref_ringtone_" + key, "");
        if (pref_enable_ringtone_ && (pref_ringtone_ != null) && (pref_ringtone_.equals("") == false)) {
            final MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(pref_ringtone_));
            mediaPlayer.setLooping(false);
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    private final void preloadIcon(final String url) {
        if (pref_enable_inline_img_volley) {
            preloadIconVolley(url);
        } else {
            preloadIconMap(url);
        }
    }

    private final void preloadIconMap(final String url) {
        Bitmap icon;
        try {
            icon = BitmapFactory.decodeStream((new URL(url)).openStream());
        } catch (MalformedURLException e) {
            icon = null;
            WriteLog.write(context, e);
        } catch (IOException e) {
            icon = null;
            WriteLog.write(context, e);
        } catch (OutOfMemoryError e) {
            icon = null;
            WriteLog.write(context, e);
        }
        if (icon != null) {
            try {
                bitmapLruCache.putBitmap(url, icon);
            } catch (Exception e) {
                WriteLog.write(context, e);
            }
        }
    }

    private final void preloadIconVolley(final String url) {
        Bitmap icon;
        try {
            icon = BitmapFactory.decodeStream((new URL(url)).openStream());
        } catch (MalformedURLException e) {
            icon = null;
            WriteLog.write(context, e);
        } catch (IOException e) {
            icon = null;
            WriteLog.write(context, e);
        } catch (OutOfMemoryError e) {
            icon = null;
            WriteLog.write(context, e);
        }
        if (icon != null) {
            try {
                bitmapLruCache.putBitmap(url, icon);
            } catch (Exception e) {
                WriteLog.write(context, e);
            }
        }
    }

    private final void preloadMapPart(final Status status) {
        if (pref_enable_expand_uri_thirdparty_wifi) {
            if (status.getURLEntities() != null) {
                for (final URLEntity urlEntity : status.getURLEntities()) {
                    preloadUri(urlEntity);
                }
            }
        }
        if (pref_enable_inline_img_wifi) {
            if (status.getMediaEntities() != null) {
                for (final MediaEntity mediaEntity : status.getMediaEntities()) {
                    preloadIcon(mediaEntity.getMediaURLHttps());
                }
            }
        }
    }

    final void preloadMaps(final List<Status> statuses) {
        if (checkNetworkUtil.isConnected() == false) {
            return;
        }

        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        new Thread(new Runnable() {
            @Override
            public final void run() {
                try {
                    if (pref_tl_load_direction) { // 下方向
                        for (int i = 0; i < (2 * PLAY); i++) {
                            preloadMapPart(statuses.get(i)); // 0,1
                        }
                        for (int i = (statuses.size() - 1); i >= (statuses.size() - 2 * PLAY); i--) {
                            preloadMapPart(statuses.get(i)); // 9,8
                        }
                        for (int i = (2 * PLAY); i <= (statuses.size() - 1 - 2 * PLAY); i++) {
                            preloadMapPart(statuses.get(i)); // 2,3,4,5,6,7
                        }
                    } else { // 上方向
                        for (int i = (statuses.size() - 1); i >= (statuses.size() - 2 * PLAY); i--) {
                            preloadMapPart(statuses.get(i)); // 9,8
                        }
                        for (int i = 0; i < (2 * PLAY); i++) {
                            preloadMapPart(statuses.get(i)); // 0,1
                        }
                        for (int i = (statuses.size() - 1 - 2 * PLAY); i >= (2 * PLAY); i--) {
                            preloadMapPart(statuses.get(i)); // 7,6,5,4,3,2
                        }
                    }
                } catch (final Exception e) {
                }
            }
        }).start();
    }

    private final void preloadUri(final URLEntity urlEntity) {
        if (!expandedUris.containsKey(urlEntity.getURL())) {
            if (checkNetworkUtil.isConnected()) {
                try {
                    final String longUrl = ((pref_enable_expand_uri_thirdparty_wifi) && (checkNetworkUtil.isConnected())) ? urlUtil.expand_uri(urlEntity.getURL()) : urlEntity.getExpandedURL();
                    if ((longUrl.equals("") == false) && (longUrl.equals(urlEntity.getURL()) == false)) {
                        expandedUris.put(urlEntity.getURL(), longUrl);

                        if (urlEntity.getURL().startsWith(context.getString(R.string.https))) {
                            expandedUris.put((urlEntity.getURL()).replace(context.getString(R.string.https), context.getString(R.string.http)), longUrl);
                        }
                    }
                } catch (final Exception e) {
                }
            }
        }
    }

    private final String r4s(final int index, final String uScreenname) {
        try {
            getTwitter(index, false).reportSpam(uScreenname);

            return (context.getString(R.string.done_r4s) + ": @" + uScreenname + " " + " [@" + checkScreennameFromIndex(index) + "]");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final String readUri(final String shortUrl) {
        // キャッシュ内URLの取得
        if (expandedUris.containsKey(shortUrl)) {
            final String longUrl = expandedUris.get(shortUrl);
            if (longUrl.length() > shortUrl.length()) {
                return longUrl;
            }
        }

        if (checkNetworkUtil.isConnected()) {
            try {
                final String longuri = urlUtil.expand_uri(shortUrl);
                if ((longuri.equals("") == false) && (longuri.equals(shortUrl) == false)) {
                    expandedUris.put(shortUrl, longuri);

                    if (shortUrl.startsWith(context.getString(R.string.https))) {
                        expandedUris.put(shortUrl.replace(context.getString(R.string.https), context.getString(R.string.http)), longuri);
                    }
                }
                return longuri;
            } catch (final Exception e) {
            }
        }
        return shortUrl;
    }

    final String repeatStr(final String s, final int n) {
        final StringBuilder stringBuilder1 = new StringBuilder(n);
        stringBuilder1.append("");
        for (int i = 0; i < n; i++) {
            stringBuilder1.append(s);
        }
        return stringBuilder1.toString();
    }

    private final String replaceAllHashtag(String text, final String fontcolor_statustext, final HashtagEntity[] hashtagEntities) {
        for (final HashtagEntity hashtagEntity : hashtagEntities) {
            text =
                    text.replace("#" + hashtagEntity.getText(), "</font><font color=\"" + pref_tl_fontcolor_statustext_hashtag + "\"><a class=\"t\" href=\"https://twitter.com/search/%23"
                            + hashtagEntity.getText() + "\">#" + hashtagEntity.getText() + "</a></font><font color=\"" + fontcolor_statustext + "\">");
        }

        return text;
    }

    private final String replaceAllMedia(String text, final String fontcolor_statustext, final MediaEntity[] mediaEntities) {
        for (final MediaEntity mediaEntity : mediaEntities) {
            if (text.contains(mediaEntity.getURL())) {
                text =
                        text.replace(mediaEntity.getURL(), "</font>" + "<a href=\"" + mediaEntity.getMediaURLHttps() + "\">" + "<img src=\"" + mediaEntity.getMediaURLHttps() + "\" height=\""
                                + pref_tl_imagesize_string + "\" />" + "</a>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\">"
                                + "<a href=\"" + mediaEntity.getExpandedURL() + "\">" + "twitter" + "</a></font>" + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\""
                                + fontcolor_statustext + "\">");
            } else {
                text +=
                        "</font>" + "<a href=\"" + mediaEntity.getMediaURLHttps() + "\">" + "<img src=\"" + mediaEntity.getMediaURLHttps() + "\" height=\"" + pref_tl_imagesize_string + "\" />"
                                + "</a>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\">" + "<a href=\"" + mediaEntity.getExpandedURL()
                                + "\">" + "twitter" + "</a></font>" + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\"" + fontcolor_statustext + "\">";
            }
        }

        return text;
    }

    private final String replaceAllUrl(String text, final String fontcolor_statustext, final URLEntity[] urlEntities, final boolean flag_enable_tl_speedy) {
        pref_enable_tl_speedy = (flag_enable_tl_speedy) ? pref_app.getBoolean("pref_enable_tl_speedy", false) : false;
        if (pref_enable_tl_speedy) {
            pref_enable_expand_uri_twitter_wifi = pref_enable_expand_uri_twitter_mobile;
            pref_enable_expand_uri_thirdparty_wifi = pref_enable_expand_uri_thirdparty_mobile;

            pref_enable_inline_img_wifi = pref_enable_inline_img_mobile;
        }

        for (final URLEntity urlEntity : urlEntities) {
            if ((pref_enable_expand_uri_thirdparty_wifi && (!pref_enable_tl_speedy)) && ((urlEntity.getExpandedURL()).length() < pref_enable_expand_uri_string_length)) {
                text =
                        text.replace(urlEntity.getURL(), "</font>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\"><a class=\"l\" href=\""
                                + readUri(urlEntity.getExpandedURL()) + "\">" + readUri(urlEntity.getExpandedURL()) + "</a></font>" + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\""
                                + fontcolor_statustext + "\">");
                text =
                        text.replace(urlEntity.getURL(), "</font>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\"><a class=\"l\" href=\""
                                + readUri(urlEntity.getURL()) + "\">" + readUri(urlEntity.getExpandedURL()) + "</a></font>" + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\""
                                + fontcolor_statustext + "\">");

            } else if (pref_enable_expand_uri_twitter_wifi) {
                text =
                        text.replace(urlEntity.getURL(), "</font>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\"><a class=\"l\" href=\""
                                + urlEntity.getExpandedURL() + "\">" + (pref_enable_expand_uri_fullurl ? urlEntity.getExpandedURL() : urlEntity.getDisplayURL()) + "</a></font>"
                                + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\"" + fontcolor_statustext + "\">");
            } else {
                text =
                        text.replace(urlEntity.getURL(), "</font>" + (pref_tl_fontsize_large_url ? "<big>" : "") + "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\"><a class=\"l\" href=\""
                                + urlEntity.getURL() + "\">" + urlEntity.getURL() + "</a></font>" + (pref_tl_fontsize_large_url ? "</big>" : "") + "<font color=\"" + fontcolor_statustext + "\">");
            }
        }

        return text;
    }

    private final String replaceAllUser(String text, final String fontcolor_statustext, final UserMentionEntity[] usermentionEntities) {
        for (final UserMentionEntity userMentionEntity : usermentionEntities) {
            text =
                    text.replace("@" + userMentionEntity.getScreenName(), "</font><font color=\"" + pref_tl_fontcolor_statustext_screenname + "\"><a class=\"u\" href=\"https://twitter.com/"
                            + userMentionEntity.getScreenName() + "\">@" + userMentionEntity.getScreenName() + "</a></font><font color=\"" + fontcolor_statustext + "\">");
        }

        return text;
    }

    private final String rt(final int index, final Status tweet) {
        try {
            String statusUserScreenname = "";
            String statusText = "";
            long statusId = 0;
            if (tweet.isRetweet()) {
                statusText = tweet.getRetweetedStatus().getText();
                statusUserScreenname = tweet.getRetweetedStatus().getUser().getScreenName();
                statusId = tweet.getRetweetedStatus().getId();
            } else {
                statusText = tweet.getText();
                statusUserScreenname = tweet.getUser().getScreenName();
                statusId = tweet.getId();
            }

            getTwitter(index, false).retweetStatus(statusId);

            return (context.getString(R.string.done_rt) + ": @" + statusUserScreenname + ": " + statusText + " " + " [@" + checkScreennameFromIndex(index) + "]");
        } catch (final NumberFormatException e) {
            WriteLog.write(context, e);
        } catch (final TwitterException e) {
            try {
                twitterException(e);
            } catch (Exception ex) {
                toast(context.getString(R.string.cannot_access_twitter));
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    final void searchTweet(final ListView listView) {
        final EditText editText = new EditText(context);
        if (preSearchtweetString.equals("") == false) {
            editText.setText(preSearchtweetString);
        }
        new AlertDialog.Builder(context).setTitle(R.string.search).setView(editText).setCancelable(true).setNegativeButton(R.string.createdat, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                preSearchtweetString = editText.getText().toString();
                setPosSearch(listView, "createdat", editText.getText().toString());
            }
        }).setNeutralButton(R.string.screenname, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                preSearchtweetString = editText.getText().toString();
                setPosSearch(listView, "screenname", editText.getText().toString());
            }
        }).setPositiveButton(R.string.tweettext, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(final DialogInterface dialog, final int which) {
                preSearchtweetString = editText.getText().toString();
                setPosSearch(listView, "search", editText.getText().toString());
            }
        }).create().show();
    }

    private final int searchTweetPositionById(final long statusId, final boolean mode) {
        return BinarySearchUtil.binary_search(statusId, getTweets(), mode);
    }

    private final int searchTweetPositionByTweetCreatedat(final String searchStr) {
        final List<Status> tweets = getTweets();
        for (int pos = (listView.getFirstVisiblePosition() + 1 < tweets.size()) ? listView.getFirstVisiblePosition() + 1 : 0; pos < tweets.size(); pos++) {
            if (tweets.get(pos).getCreatedAt().before(stringToDate(searchStr, 0))) {
                toast(tweets.get(pos).getUser().getScreenName() + ": " + tweets.get(pos).getText());
                return pos;
            }
        }
        return -1;
    }

    private final int searchTweetPositionByTweetScreenname(final String searchStr) {
        final List<Status> tweets = getTweets();
        for (int pos = (listView.getFirstVisiblePosition() + 1 < tweets.size()) ? listView.getFirstVisiblePosition() + 1 : 0; pos < tweets.size(); pos++) {
            if (tweets.get(pos).getUser().getScreenName().indexOf(searchStr) > -1) {
                toast(tweets.get(pos).getUser().getScreenName() + ": " + tweets.get(pos).getText());
                return pos;
            }
        }
        return -1;
    }

    private final int searchTweetPositionByTweetText(final String searchStr) {
        final List<Status> tweets = getTweets();
        for (int pos = (listView.getFirstVisiblePosition() + 1 < tweets.size()) ? listView.getFirstVisiblePosition() + 1 : 0; pos < tweets.size(); pos++) {
            if (tweets.get(pos).getText().indexOf(searchStr) > -1) {
                toast(tweets.get(pos).getUser().getScreenName() + ": " + tweets.get(pos).getText());
                return pos;
            }
        }
        return -1;
    }

    private final int searchTweetPositionOfMyLastfav() {
        final long currentTimeLong = System.currentTimeMillis();
        final long pref_search_unread_mylastfav_timeout_millis = pref_search_unread_mylastfav_timeout * 1000;

        try {
            pref_search_unread_mylastfav_mute_source = "," + pref_app.getString("pref_search_unread_mylastfav_mute_source", "") + ",";
            pref_search_unread_mylastfav_mute_text = "," + pref_app.getString("pref_mute_time_screenname", "") + ",";
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            Twitter twtr = getTwitter(checkIndexFromPrefTwtr(), false);

            long tempId = -1;
            ResponseList<Status> statuses = twtr.getFavorites();
            for (final Status status : statuses) {
                if ((currentTimeLong - status.getCreatedAt().getTime()) > pref_search_unread_mylastfav_timeout_millis) {
                    if ((checkMuteSource(pref_search_unread_mylastfav_mute_source, status) == false) && (checkMuteText(pref_search_unread_mylastfav_mute_text, status) == false)) {
                        return searchTweetPositionById(status.getId(), true);
                    }
                }
                tempId = status.getId();
            }

            final Paging pagingLastfav = new Paging();
            pagingLastfav.setCount(pref_search_unread_mylastfav_count);
            pagingLastfav.setMaxId(tempId);

            statuses = twtr.getFavorites(pagingLastfav);
            for (final Status status : statuses) {
                if ((currentTimeLong - status.getCreatedAt().getTime()) > pref_search_unread_mylastfav_timeout_millis) {
                    if ((checkMuteSource(pref_search_unread_mylastfav_mute_source, status) == false) && (checkMuteText(pref_search_unread_mylastfav_mute_text, status) == false)) {
                        return searchTweetPositionById(status.getId(), true);
                    }
                }
                tempId = status.getId();
            }

        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return -1;
    }

    private final int searchTweetPositionOfMyLasttweet() {
        final long currentTimeLong = System.currentTimeMillis();
        final long pref_search_unread_mylasttweet_timeout_millis = pref_search_unread_mylasttweet_timeout * 1000;

        try {
            pref_search_unread_mylasttweet_mute_source = "," + pref_app.getString("pref_search_unread_mylasttweet_mute_source", "") + ",";
            pref_search_unread_mylasttweet_mute_text = "," + pref_app.getString("pref_mute_time_screenname", "") + ",";
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            final int idx = checkIndexFromPrefTwtr();
            final Twitter twtr = getTwitter(idx, false);
            final User user = getUser(idx, checkScreennameFromIndex(idx));
            final Status userLastStatus = user.getStatus();
            if ((currentTimeLong - userLastStatus.getCreatedAt().getTime()) > pref_search_unread_mylasttweet_timeout_millis) {
                if ((checkMuteSource(pref_search_unread_mylasttweet_mute_source, userLastStatus) == false) && (checkMuteText(pref_search_unread_mylasttweet_mute_text, userLastStatus) == false)) {
                    return searchTweetPositionById(userLastStatus.getId(), true);
                }
            }

            long tempId = -1;
            ResponseList<Status> statuses = twtr.getUserTimeline(twtr.getScreenName());
            for (final Status status : statuses) {
                WriteLog.write(context, "search_tweet_position_of_my_lasttweet() status: " + status.getText());
                if ((currentTimeLong - status.getCreatedAt().getTime()) > pref_search_unread_mylasttweet_timeout_millis) {
                    if ((checkMuteSource(pref_search_unread_mylasttweet_mute_source, status) == false) && (checkMuteText(pref_search_unread_mylasttweet_mute_text, status) == false)) {
                        return searchTweetPositionById(status.getId(), true);
                    }
                }
                tempId = status.getId();
            }

            final Paging pagingLasttweet = new Paging();
            pagingLasttweet.setCount(pref_search_unread_mylasttweet_count);
            pagingLasttweet.setMaxId(tempId);

            statuses = twtr.getUserTimeline(twtr.getScreenName(), pagingLasttweet);
            for (final Status status : statuses) {
                WriteLog.write(context, "search_tweet_position_of_my_lasttweet() status: " + status.getText());
                if ((currentTimeLong - status.getCreatedAt().getTime()) > pref_search_unread_mylasttweet_timeout_millis) {
                    if (checkMuteSource(pref_search_unread_mylasttweet_mute_source, status) == false) {
                        return searchTweetPositionById(status.getId(), true);
                    }
                }
                tempId = status.getId();
            }

        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return -1;
    }

    final void setPosition(final ListView listView, final int statuses_size, final long maxId, final long sinceId) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        final boolean pref_tl_load_direction = pref_app.getBoolean("pref_tl_load_direction", true);

        try {
            if (pref_tl_load_direction) { // 下向き
                if (maxId > -1) {
                    if ((listView.getCount() - statuses_size - default_item_num - PLAY) < listView.getLastVisiblePosition()) { // 下向き 更新(↓) 最下部
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 下向き 更新(↓) 最下部");
                        listViewSetSelection(listView.getCount() - statuses_size - default_item_num, true);
                    } else { // 下向き 更新(↓) 最下部以外
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 下向き 更新(↓) 最下部以外");
                        listViewSetSelection(listView.getFirstVisiblePosition() + statuses_size - 1, true);
                    }
                } else {
                    if (default_item_num >= (listView.getCount() - statuses_size)) { // 下向き 更新(0)
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 下向き 更新(0)");
                        listViewSetSelection(1, true);
                    } else if (PLAY > listView.getFirstVisiblePosition()) { // 下向き 更新(↑) 最上部
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 下向き 更新(↑) 最上部");
                        listViewSetSelection(1, true);
                    } else { // 下向き 更新(↑) 最上部以外
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 下向き 更新(↑) 最上部以外");
                        listViewSetSelection(listView.getFirstVisiblePosition() + statuses_size - 1, true);
                    }
                }
            } else { // 上向き
                if (sinceId > -1) {
                    if (PLAY > listView.getFirstVisiblePosition()) { // 上向き 更新(↑) 最上部
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 上向き 更新(↑) 最上部");
                        listViewSetSelection(statuses_size, true);
                    } else { // 上向き 更新(↑) 最上部以外
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 上向き 更新(↑) 最上部以外");
                        listViewSetSelection(listView.getFirstVisiblePosition() + statuses_size - 1, true);
                    }
                } else {
                    if (default_item_num >= (listView.getCount() - statuses_size)) { // 上向き 更新(0)
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 上向き 更新(0)");
                        listViewSetSelection(statuses_size, true);
                    } else if ((listView.getCount() - statuses_size - default_item_num - PLAY) < listView.getLastVisiblePosition()) { // 上向き 更新(↓) 最下部
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 上向き 更新(↓) 最下部");
                        listViewSetSelection(listView.getCount() - 1, true);
                    } else { // 上向き 更新(↓) 最下部以外
                        WriteLog.write(context, "pref_tl_load_direction: " + Boolean.toString(pref_tl_load_direction) + " 上向き 更新(↓) 最下部以外");
                        listViewSetSelection(listView.getFirstVisiblePosition() + statuses_size - 1, true);
                    }
                }
            }
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        //		try {
        //			notifyDataSetChanged();
        //			WriteLog.write(context, "set_position() notifyDataSetChanged()");
        //		} catch (final Exception e) {
        //			WriteLog.write(context, e);
        //		}
    }

    final void setInfo(String uriString) {

        if (checkNetworkUtil.isConnected() == false) {
            toast(context.getString(R.string.cannot_access_internet));
            return;
        }

        final String TAG = StringUtil.uriStringToTag(uriString, true);
        WriteLog.write(context, "TAG: " + TAG + " uriString: " + uriString);

        if (uriString.startsWith(TWITTER_BASE_URI)) {
            uriString = uriString.replace(TWITTER_BASE_URI, "");
        }

        if (TAG.equals("home")) {
            setUserInfo((uriString.split("#"))[0]);
        } else if (TAG.equals("home(s)")) {
            setUserInfo((uriString.split("#"))[0]);
        } else if (TAG.equals("mention")) {
            setUserInfo((uriString.split("#"))[0]);
        } else if (TAG.equals("search")) {
            setQuerystrInfo(getQuery(uriString));
        } else if (TAG.equals("search(s)")) {
            setQuerystrInfo(getQuery(uriString));
        } else if (TAG.equals("user")) {
            setUserInfo(uriString);
        } else if (TAG.equals("userfav")) {
            setUserInfo((uriString.split("#"))[0]);
        } else if (TAG.equals("userlist")) {
            setUserlistInfo(-1, uriString.replace("/lists/", "/"));
        } else if (TAG.equals("userlist(s)")) {
            setUserlistInfo(-1, uriString.replace("/lists/", "/"));
        }
    }

    private final void setMaxId(final int pos) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean pref_search_unread_timeline = pref_app.getBoolean("pref_search_unread_timeline", true);
        if ((!pref_search_unread_timeline) && (pos == 0)) {
            return;
        }

        final String key = "maxId_" + Integer.toString(checkIndexFromPrefTwtr()) + "_" + maxId_key;
        long thisId = -1;
        try {
            thisId = tweets.get(pos).getId();
        } catch (final Exception e) {
        }
        final long currentMaxId = Long.parseLong(pref_app.getString(key, "-1"));
        if (thisId > currentMaxId) {
            pref_app = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = pref_app.edit();
            editor.putString(key, Long.toString(thisId));
            editor.commit();
        }
    }

    final void setMaxId(final String maxId_key, final int pos) {
        setMaxIdKey(maxId_key);
        setMaxId(pos);
    }

    final void setMaxIdKey(String maxId_key) {
        this.maxId_key = maxId_key;
    }

    final void setNtpOffset(final long offset) {
        ntpOffset = offset;
    }

    private final void setPosSearch(final ListView listView, final String mode, final String searchStr) {
        int pos = -1;
        if (mode.equals("search")) {
            pos = searchTweetPositionByTweetText(searchStr);
        } else if (mode.equals("screenname")) {
            pos = searchTweetPositionByTweetScreenname(searchStr);
        } else if (mode.equals("createdat")) {
            pos = searchTweetPositionByTweetCreatedat(searchStr);
        }
        if (pos > -1) {
            listViewSetSelection(pos + 1, false);
        }
    }

    final void setPosUnread(final ListView listView) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        if (!((Activity) context).isFinishing()) {
            try {
                ((Activity) context).dismissDialog(R.string.move_tl_unread);
            } catch (final Exception e) {
            }
            ((Activity) context).showDialog(R.string.move_tl_unread);
        }

        new Thread(new Runnable() {
            public final void run() {

                final String key = "maxId_" + Integer.toString(checkIndexFromPrefTwtr()) + "_" + maxId_key;
                int pos = -1;
                long statusId = -1;

                final boolean pref_search_unread_timeline = pref_app.getBoolean("pref_search_unread_timeline", true);
                if (pref_search_unread_timeline) {
                    statusId = Long.parseLong(pref_app.getString(key, "-1"));
                }

                if (statusId > -1) {
                    pos = searchTweetPositionById(statusId, false);
                    if (pos > -1) {
                        setPosUnreadPart(pref_app.getString(key, "-1"), searchTweetPositionById(statusId, false));
                        return;
                    }
                }

                final boolean pref_search_unread_mylasttweet = pref_app.getBoolean("pref_search_unread_mylasttweet", true);
                if (pref_search_unread_mylasttweet) {
                    pos = searchTweetPositionOfMyLasttweet();
                    if (pos > -1) {
                        setPosUnreadPart(R.string.unread_mylasttweet, pos);
                        return;
                    }
                }

                final boolean pref_search_unread_mylastfav = pref_app.getBoolean("pref_search_unread_mylastfav", true);
                if (pref_search_unread_mylastfav) {
                    pos = searchTweetPositionOfMyLastfav();
                    if (pos > -1) {
                        setPosUnreadPart(R.string.unread_mylastfav, pos);
                        return;
                    }
                }

                if (!pref_tl_load_direction) {
                    setPosUnreadPart(listView.getCount() - 1);
                    return;
                }

                try {
                    ((Activity) context).dismissDialog(R.string.move_tl_unread);
                } catch (IllegalArgumentException e) {
                }

            }
        }).start();
    }

    private final void setPosUnreadPart(final int pos) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public final void run() {
                try {
                    ((Activity) context).dismissDialog(R.string.move_tl_unread);
                } catch (IllegalArgumentException e) {
                }
                try {
                    listViewSetSelection(pos, false);
                } catch (final Exception e) {
                }
            }
        });
    }

    private final void setPosUnreadPart(final int mode, final int pos) {
        toast(context.getString(mode) + ": " + pos);
        setPosUnreadPart(pos);
    }

    private final void setPosUnreadPart(final String toastMessage, final int pos) {
        toast(toastMessage + ": " + pos);
        setPosUnreadPart(pos);
    }

    final void setQuerystrInfo(String queryStr) {
        final LinearLayout layout1 = (LinearLayout) ((Activity) context).findViewById(TlViewLayoutUtil.ID_LAYOUT1);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        final ImageView imageView = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEW);
        final ImageView imageViewBackground = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEWBACKGROUND);
        final TextView textView = (TextView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_TEXTVIEW);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(WC, WC));

        final User myUser = getUser();
        pref_userinfo_show_my_profile = (myUser.getScreenName().equals("")) ? false : true;

        new Thread(new Runnable() {
            public final void run() {
                if ((pref_hide_tl_headericon) || (pref_hide_item_myicon)) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        public final void run() {
                            imageView.setImageResource(drawable.btn_default);

                            if (lightingColorFilterTlHeadericon != null) {
                                try {
                                    imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                } catch (final Exception e) {
                                }
                            }
                        }
                    });
                } else {
                    if (myUser != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                try {
                                    getIcon(imageView, myUser.getProfileImageURLHttps());

                                    if (pref_show_profilebannerimage) {
                                        final String profileBannerURL = myUser.getProfileBannerURL() == null ? "" : myUser.getProfileBannerURL();

                                        if (profileBannerURL.equals("") == false) {
                                            getIcon(imageViewBackground, profileBannerURL);
                                        } else {
                                            getIcon(imageViewBackground, myUser.getProfileBackgroundImageUrlHttps());
                                        }
                                    } else {
                                        getIcon(imageViewBackground, myUser.getProfileBackgroundImageUrlHttps());
                                    }
                                } catch (final Exception e) {
                                }
                            }
                        });
                    }

                    if (lightingColorFilterTlHeadericon != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                try {
                                    imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                    imageViewBackground.setColorFilter(lightingColorFilterTlHeadericon);
                                } catch (final Exception e) {
                                }
                            }
                        });
                    }
                }
            }
        }).start();

        String queryStr_encoded = "";
        try {
            queryStr_encoded = URLEncoder.encode(queryStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            queryStr_encoded = queryStr;
        }

        try {
            final int sbInitSize = 240; // 174 * 4/3
            final StringBuilder queryinfoBuilder = new StringBuilder(sbInitSize);
            queryinfoBuilder.append("<font color=\"");
            queryinfoBuilder.append(pref_header_fontcolor);
            queryinfoBuilder.append("\">Query: </font><font color=\"");
            queryinfoBuilder.append(pref_header_fontcolor);
            queryinfoBuilder.append("\"><a href=\"https://twitter.com/search/");
            queryinfoBuilder.append(queryStr_encoded);
            queryinfoBuilder.append("\">");
            queryinfoBuilder.append((pref_enable_htmlescape ? HtmlEscape.escape(queryStr) : queryStr));
            queryinfoBuilder.append("</a></font>");

            textView.setText(Html.fromHtml(queryinfoBuilder.toString()));

            imageViewBackground.setOnClickListener(new OnClickListener() {
                @Override
                public final void onClick(View v) {
                    if (pref_show_profilebannerimage) {
                        final String profileBannerURL = myUser.getProfileBannerURL() == null ? "" : myUser.getProfileBannerURL();

                        if (profileBannerURL.equals("") == false) {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileBannerURL));
                            context.startActivity(intent);
                        } else {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myUser.getProfileBackgroundImageUrlHttps()));
                            context.startActivity(intent);
                        }
                    } else {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myUser.getProfileBackgroundImageUrlHttps()));
                        context.startActivity(intent);
                    }
                }
            });
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            notifyDataSetChanged();
            WriteLog.write(context, "setQuerystrInfo() notifyDataSetChanged()");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            ((Activity) context).dismissDialog(R.string.loading);
        } catch (IllegalArgumentException e) {
        }
    }

    final void setSelectPos(final int select_pos) {
        this.select_pos = select_pos;
    }

    final void setTlOptionsMenu(final Menu menu) {
        menu.add(0, R.string.updatetweet_lite, 0, R.string.updatetweet_lite).setIcon(android.R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, R.string.enter_tl_uri, 0, R.string.enter_tl_uri).setIcon(android.R.drawable.ic_popup_sync);
        menu.add(0, R.string.deljustbefore, 0, R.string.deljustbefore).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, R.string.load_tl_up, 0, R.string.load_tl_up).setIcon(android.R.drawable.arrow_up_float);
        menu.add(0, R.string.load_tl_down, 0, R.string.load_tl_down).setIcon(android.R.drawable.arrow_down_float);

        menu.add(0, R.string.move_tl_up, 0, R.string.move_tl_up).setIcon(android.R.drawable.stat_sys_upload);
        menu.add(0, R.string.move_tl_unread, 0, R.string.move_tl_unread).setIcon(android.R.drawable.ic_menu_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, R.string.move_tl_down, 0, R.string.move_tl_down).setIcon(android.R.drawable.stat_sys_download);
        menu.add(0, R.string.search, 0, R.string.search).setIcon(android.R.drawable.ic_menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        final SubMenu sub0 = menu.addSubMenu(R.string.streaming).setIcon(android.R.drawable.ic_media_ff);
        sub0.add(0, R.string.streaming_restart, 0, R.string.streaming_restart).setIcon(android.R.drawable.ic_menu_revert);
        sub0.add(0, R.string.streaming_cleanup, 0, R.string.streaming_cleanup).setIcon(android.R.drawable.ic_popup_sync);
        sub0.add(0, R.string.tts_streamingtl_on, 0, R.string.tts_streamingtl_on).setIcon(android.R.drawable.ic_lock_silent_mode_off);
        sub0.add(0, R.string.tts_streamingtl_off, 0, R.string.tts_streamingtl_off).setIcon(android.R.drawable.ic_lock_silent_mode);
        sub0.setGroupCheckable(0, true, true);

        final SubMenu sub1 = menu.addSubMenu(R.string.tl_repeat).setIcon(android.R.drawable.ic_menu_rotate);
        sub1.add(0, R.string.tl_repeat_on, 0, R.string.tl_repeat_on);
        sub1.add(0, R.string.tl_repeat_off, 0, R.string.tl_repeat_off);
        sub1.setGroupCheckable(0, true, true);
        final SubMenu sub2 = menu.addSubMenu(R.string.tl_speedy).setIcon(android.R.drawable.ic_media_ff);
        sub2.add(0, R.string.tl_speedy_on, 0, R.string.tl_speedy_on);
        sub2.add(0, R.string.tl_speedy_off, 0, R.string.tl_speedy_off);
        sub2.setGroupCheckable(0, true, true);

        menu.add(0, R.string.check_ratelimit, 0, R.string.check_ratelimit).setIcon(android.R.drawable.stat_sys_download);
        menu.add(0, R.string.check_apistatus, 0, R.string.check_apistatus).setIcon(android.R.drawable.stat_sys_download);
        menu.add(0, R.string.make_shortcut, 0, R.string.make_shortcut).setIcon(android.R.drawable.ic_menu_add);

        final SubMenu sub3 = menu.addSubMenu(context.getString(R.string.settings_all) + "...").setIcon(android.R.drawable.ic_menu_preferences);
        sub3.add(0, R.string.settings, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
        sub3.add(0, R.string.settings_import_export, 0, context.getString(R.string.settings_import_export) + "...").setIcon(android.R.drawable.stat_notify_sdcard);
        sub3.add(0, R.string.select_tl_fontfilename, 0, context.getString(R.string.select_tl_fontfilename) + "...").setIcon(android.R.drawable.stat_notify_sdcard);
        sub3.add(0, R.string.get_blockusers, 0, R.string.get_blockusers).setIcon(android.R.drawable.stat_sys_download);
        sub3.add(0, R.string.get_short_url_length, 0, R.string.get_short_url_length).setIcon(android.R.drawable.stat_sys_download);

        final SubMenu sub4 = sub3.addSubMenu(context.getString(R.string.set_colors) + "...").setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.get_colors, 0, R.string.get_colors).setIcon(android.R.drawable.stat_sys_download);
        sub4.add(0, R.string.set_colors_white_on_black, 0, R.string.set_colors_white_on_black).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_black_on_white, 0, R.string.set_colors_black_on_white).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_white_on_darkgrey, 0, R.string.set_colors_white_on_darkgrey).setIcon(android.R.drawable.ic_menu_gallery);
        sub4.add(0, R.string.set_colors_darkgrey_on_black, 0, R.string.set_colors_darkgrey_on_black).setIcon(android.R.drawable.ic_menu_gallery);
        menu.add(0, R.string.copyright, 0, R.string.copyright).setIcon(android.R.drawable.ic_menu_info_details);
    }

    final void setTweets(final List<Status> statuses, final int mode) {
        if (mode == 0) {
            tweets = statuses;
        } else if (mode == 1) {
            try {
                tweets.addAll(statuses);
            } catch (final Exception e) {
                tweets = statuses;
            }
        } else if (mode == 2) {
            try {
                final ArrayList<Status> templist = new ArrayList<Status>(400);
                templist.addAll(statuses);
                templist.addAll(tweets);
                tweets = templist;
            } catch (final Exception e) {
                tweets = statuses;
            }
        }
    }

    final void setUserInfo(final String uScreenname) {
        final LinearLayout layout1 = (LinearLayout) ((Activity) context).findViewById(TlViewLayoutUtil.ID_LAYOUT1);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        final ImageView imageView = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEW);
        final ImageView imageViewBackground = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEWBACKGROUND);
        final TextView textView = (TextView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_TEXTVIEW);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(WC, WC));

        boolean loc_null = false;
        final int idx = checkIndexFromScreenname(uScreenname);
        String loc_encoded = "";
        final User user = getUser(idx, uScreenname);
        final User myUser = getUser();

        if ((user != null) && (myUser != null)) {
            final boolean pref_hide_item_myicon_userEqualsMyuser = (myUser.getScreenName().equals("")) ? false : (pref_hide_item_myicon && (user.getScreenName().equals(myUser.getScreenName())));

            new Thread(new Runnable() {
                public final void run() {
                    if ((pref_hide_tl_headericon) || (pref_hide_item_myicon_userEqualsMyuser)) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                imageView.setImageResource(drawable.btn_default);

                                if (lightingColorFilterTlHeadericon != null) {
                                    try {
                                        imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                    } catch (final Exception e) {
                                        WriteLog.write(context, e);
                                        // toast(context.getString(R.string.exception));
                                    }
                                }
                            }
                        });
                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                getIcon(imageView, user.getProfileImageURLHttps());

                                if (pref_show_profilebannerimage) {
                                    final String profileBannerURL = user.getProfileBannerURL() == null ? "" : user.getProfileBannerURL();

                                    if (profileBannerURL.equals("") == false) {
                                        getIcon(imageViewBackground, profileBannerURL);
                                    } else {
                                        getIcon(imageViewBackground, user.getProfileBackgroundImageUrlHttps());
                                    }
                                } else {
                                    getIcon(imageViewBackground, user.getProfileBackgroundImageUrlHttps());
                                }

                                if (lightingColorFilterTlHeadericon != null) {
                                    try {
                                        imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                        imageViewBackground.setColorFilter(lightingColorFilterTlHeadericon);
                                    } catch (final Exception e) {
                                    }
                                }
                            }
                        });
                    }
                }
            }).start();

            try {
                loc_null = ((user.getLocation()).equals("")) ? true : false;
                if (!loc_null) {
                    loc_encoded = URLEncoder.encode(user.getLocation(), "UTF-8");
                }
            } catch (final UnsupportedEncodingException e) {
                WriteLog.write(context, e);
            } catch (final Exception e) {
                WriteLog.write(context, e);
            }
        }

        try {
            pref_userinfo_show_my_profile = (myUser.getScreenName().equals("")) ? false : true;

            final int sbInitSize = 4815; // 3611 * 4/3
            final StringBuilder userinfoBuilder = new StringBuilder(sbInitSize);
            if ((pref_hide_item_myname == false) || (((user.getScreenName()).equals(myUser.getScreenName())) == false)) {
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">Screen Name: </font>");
                userinfoBuilder.append(pref_tl_fontsize_large_screenname ? "<big>" : "");
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_tl_fontcolor_screenname);
                userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                userinfoBuilder.append(user.getScreenName());
                userinfoBuilder.append("\">@");
                userinfoBuilder.append(user.getScreenName());
                userinfoBuilder.append("(twitter)</a></font>");
                userinfoBuilder.append(pref_tl_fontsize_large_screenname ? "</big>" : "");
                userinfoBuilder.append(SP4);
                userinfoBuilder.append(pref_tl_fontsize_small_username ? "<small>" : "");
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">User Name: ");
                userinfoBuilder.append(user.getName());
                userinfoBuilder.append("</font>");
                userinfoBuilder.append(pref_tl_fontsize_small_username ? "</small>" : "");
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("<small><font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">User Id: ");
                userinfoBuilder.append(user.getId());
                userinfoBuilder.append("</font></small>");
                if (pref_userinfo_show_my_profile) {
                    userinfoBuilder.append(SP4);
                    userinfoBuilder.append("<small><font color=\"");
                    userinfoBuilder.append(pref_header_fontcolor);
                    userinfoBuilder.append("\">[ </font><font color=\"");
                    userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                    userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                    userinfoBuilder.append(myUser.getScreenName());
                    userinfoBuilder.append("\">@");
                    userinfoBuilder.append(myUser.getScreenName());
                    userinfoBuilder.append("</a></font><font color=\"");
                    userinfoBuilder.append(pref_header_fontcolor);
                    userinfoBuilder.append("\"> ]</font></small>");
                }
                userinfoBuilder.append(BR);
            }
            userinfoBuilder.append("<font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\">Statuses: </font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"><a href=\"http://twilog.org/");
            userinfoBuilder.append(user.getScreenName());
            userinfoBuilder.append("\">");
            userinfoBuilder.append(user.getStatusesCount());
            userinfoBuilder.append("(twilog)</a></font>");
            if (pref_userinfo_show_my_profile) {
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("<small><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\">[ ");
                userinfoBuilder.append(myUser.getStatusesCount());
                userinfoBuilder.append(" ]</font></small>");
            }
            userinfoBuilder.append(SP8);
            userinfoBuilder.append("<font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\">Favorites: </font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"><a href=\"https://twitter.com/");
            userinfoBuilder.append(user.getScreenName());
            userinfoBuilder.append("/favorites\">");
            userinfoBuilder.append(user.getFavouritesCount());
            userinfoBuilder.append("</a></font>");
            if (pref_userinfo_show_my_profile) {
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("<small><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\">[ </font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                userinfoBuilder.append(myUser.getScreenName());
                userinfoBuilder.append("/favorites\">");
                userinfoBuilder.append(myUser.getFavouritesCount());
                userinfoBuilder.append("</a></font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"> ]</font></small>");
            }
            userinfoBuilder.append(SP8);
            userinfoBuilder.append("<font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\">Listed: </font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"><a href=\"https://twitter.com/");
            userinfoBuilder.append(user.getScreenName());
            userinfoBuilder.append("/lists/memberships\">");
            userinfoBuilder.append(user.getListedCount());
            userinfoBuilder.append("</a></font>");
            if (pref_userinfo_show_my_profile) {
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("<small><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\">[ </font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                userinfoBuilder.append(myUser.getScreenName());
                userinfoBuilder.append("/lists/memberships\">");
                userinfoBuilder.append(myUser.getListedCount());
                userinfoBuilder.append("</a></font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"> ]</font></small>");
            }
            userinfoBuilder.append(BR);
            userinfoBuilder.append("<font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\">Followers: </font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"><a href=\"https://twitter.com/");
            userinfoBuilder.append(user.getScreenName());
            userinfoBuilder.append("/followers\">");
            userinfoBuilder.append(user.getFollowersCount());
            userinfoBuilder.append("</a></font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"> / Following: </font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\">");
            userinfoBuilder.append("<a href=\"https://twitter.com/");
            userinfoBuilder.append(user.getScreenName());
            userinfoBuilder.append("/following\">");
            userinfoBuilder.append(user.getFriendsCount());
            userinfoBuilder.append("</a></font><font color=\"");
            userinfoBuilder.append(pref_header_fontcolor);
            userinfoBuilder.append("\"> (");
            userinfoBuilder.append(getFFRatio(user.getFollowersCount(), user.getFriendsCount()));
            userinfoBuilder.append(")</font>");
            if (pref_userinfo_show_my_profile) {
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("<small><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\">[ </font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                userinfoBuilder.append(myUser.getScreenName());
                userinfoBuilder.append("/followers\">");
                userinfoBuilder.append(myUser.getFollowersCount());
                userinfoBuilder.append("</a></font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"> / </font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"><a href=\"https://twitter.com/");
                userinfoBuilder.append(myUser.getScreenName());
                userinfoBuilder.append("/following\">");
                userinfoBuilder.append(myUser.getFriendsCount());
                userinfoBuilder.append("</a></font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"> (");
                userinfoBuilder.append(getFFRatio(myUser.getFollowersCount(), myUser.getFriendsCount()));
                userinfoBuilder.append(")</font><font color=\"");
                userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                userinfoBuilder.append("\"> ]</font></small>");
            }

            if (!pref_enable_singleline) {
                userinfoBuilder.append(BR);
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">");
                userinfoBuilder.append(checkAllRelationship(user.getScreenName()));
                userinfoBuilder.append("</font><font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">Location: </font>");
                if (!loc_null) {
                    userinfoBuilder.append("<font color=\"");
                    userinfoBuilder.append(pref_tl_fontcolor_statustext_location);
                    userinfoBuilder.append("\"><a href=\"http://www.geocoding.jp/?q=");
                    userinfoBuilder.append(loc_encoded);
                    userinfoBuilder.append("\">");
                    userinfoBuilder.append(user.getLocation());
                    userinfoBuilder.append("</a></font>");
                }
                userinfoBuilder.append(BR);
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">URL: </font>");
                if (!((user.getURL() == null) ? true : false)) {
                    userinfoBuilder.append((pref_tl_fontsize_large_url ? "<big>" : ""));
                    userinfoBuilder.append("<font color=\"");
                    userinfoBuilder.append(pref_tl_fontcolor_statustext_uri);
                    userinfoBuilder.append("\"><a href=\"");
                    userinfoBuilder.append((checkNetworkUtil.isConnected()) ? (urlUtil.expand_uri(user.getURL())) : (user.getURL()));
                    userinfoBuilder.append("\">");
                    userinfoBuilder.append((checkNetworkUtil.isConnected()) ? (urlUtil.expand_uri(user.getURL())) : (user.getURL()));
                    userinfoBuilder.append("</a></font>");
                    userinfoBuilder.append(pref_tl_fontsize_large_url ? "</big>" : "");
                }
                userinfoBuilder.append(BR);
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">bio: </font>");
                String bio = (pref_enable_htmlescape ? HtmlEscape.escape(user.getDescription()) : user.getDescription()) //
                        .replaceAll(patternStr_hashtag, BIO_STR_1) //
                        .replaceAll(patternStr_screenname, BIO_STR_2) //
                        .replaceAll("\n", BR);
                if (user.getDescriptionURLEntities() != null) {
                    bio = replaceAllUrl(bio, pref_tl_fontcolor_statustext, user.getDescriptionURLEntities(), false);
                }
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">");
                userinfoBuilder.append(bio);
                userinfoBuilder.append("</font>");
                userinfoBuilder.append(BR);

                userinfoBuilder.append(pref_tl_fontsize_small_createdat ? "<small>" : "");
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">CreatedAt: ");
                userinfoBuilder.append(DF.format(user.getCreatedAt()));
                userinfoBuilder.append("</font>");
                userinfoBuilder.append(pref_tl_fontsize_small_createdat ? "</small>" : "");
                if (pref_userinfo_show_my_profile) {
                    userinfoBuilder.append(SP4);
                    userinfoBuilder.append("<small><font color=\"");
                    userinfoBuilder.append(pref_userinfo_fontcolor_my_profile);
                    userinfoBuilder.append("\">[ ");
                    userinfoBuilder.append(DF.format(myUser.getCreatedAt()));
                    userinfoBuilder.append(" ]</font></small>");
                }

                userinfoBuilder.append(BR);
                userinfoBuilder.append("<font color=\"");
                userinfoBuilder.append(pref_header_fontcolor);
                userinfoBuilder.append("\">Lang: ");
                userinfoBuilder.append(user.getLang());
                userinfoBuilder.append(SP4);
                userinfoBuilder.append("Timezone: ");
                userinfoBuilder.append(user.getTimeZone());
                userinfoBuilder.append(" (UTC");
                if (user.getUtcOffset() >= 0) {
                    userinfoBuilder.append("+");
                }
                userinfoBuilder.append((user.getUtcOffset() / 3600));
                userinfoBuilder.append(")</font>");

                final int sbInitSize2 = 60; // 42 * 4/3
                final StringBuilder userinfoBuilder2 = new StringBuilder(sbInitSize2);
                if (user.isGeoEnabled()) {
                    userinfoBuilder2.append(" Geo-enabled");
                }
                if (user.isProtected()) {
                    userinfoBuilder2.append(" Protected");
                }
                if (user.isTranslator()) {
                    userinfoBuilder2.append(" Translator");
                }
                if (user.isVerified()) {
                    userinfoBuilder2.append(" Verified");
                }
                if ((userinfoBuilder2.toString()).equals("") == false) {
                    userinfoBuilder.append(BR);
                    userinfoBuilder.append("<font color=\"");
                    userinfoBuilder.append(pref_header_fontcolor);
                    userinfoBuilder.append("\">");
                    userinfoBuilder.append(userinfoBuilder2.toString());
                    userinfoBuilder.append("</font>");
                }

            }

            textView.setText(Html.fromHtml(userinfoBuilder.toString()));

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] ITEM =
                            {"@" + user.getScreenName(), context.getString(R.string.friendship), context.getString(R.string.list), context.getString(R.string.block), context.getString(R.string.r4s)};
                    new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info).setItems(ITEM, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            pref_app = PreferenceManager.getDefaultSharedPreferences(context);
                            pref_enable_confirmdialog_hidden_allaccount = pref_app.getBoolean("pref_enable_confirmdialog_hidden_allaccount", true);

                            if (which == 0) {
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + user.getScreenName()));
                                context.startActivity(intent);
                            } else if (ITEM[which].equals(context.getString(R.string.friendship))) {
                                showFollowDialog(user.getScreenName());
                            } else if (ITEM[which].equals(context.getString(R.string.list))) {
                                showListDialog(user);
                            } else if (ITEM[which].equals(context.getString(R.string.block))) {
                                showBlockDialog(user.getScreenName());
                            } else if (ITEM[which].equals(context.getString(R.string.r4s))) {
                                showR4sDialog(user.getScreenName());
                            }

                        }
                    }).create().show();
                }
            });

            imageViewBackground.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pref_show_profilebannerimage) {
                        final String profileBannerURL = user.getProfileBannerURL() == null ? "" : user.getProfileBannerURL();

                        if (profileBannerURL.equals("") == false) {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileBannerURL));
                            context.startActivity(intent);
                        } else {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getProfileBackgroundImageUrlHttps()));
                            context.startActivity(intent);
                        }
                    } else {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getProfileBackgroundImageUrlHttps()));
                        context.startActivity(intent);
                    }
                }
            });
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            notifyDataSetChanged();
            WriteLog.write(context, "setUserInfo() notifyDataSetChanged()");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            ((Activity) context).dismissDialog(R.string.loading);
        } catch (IllegalArgumentException e) {
        }
    }

    final long[] setUserlistInfo(final long listId, final String listName) {
        final LinearLayout layout1 = (LinearLayout) ((Activity) context).findViewById(TlViewLayoutUtil.ID_LAYOUT1);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        final ImageView imageView = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEW);
        final ImageView imageViewBackground = (ImageView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_IMAGEVIEWBACKGROUND);
        final TextView textView = (TextView) ((Activity) context).findViewById(TlViewLayoutUtil.ID_TEXTVIEW);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(WC, WC));

        long[] rslt = new long[2];

        final UserList usrlist;
        try {
            if (listId > -1) {
                usrlist = getTwitter(checkIndexFromListname(listName), false).showUserList(listId);
            } else {
                final String[] listOwnerSlug = (listName.replace("/lists/", "/")).split("/", 0);
                final String listOwner = ((listOwnerSlug.length > 1) ? (listOwnerSlug[0]) : checkScreennameFromIndex(checkIndexFromPrefTwtr())).replace("@", "");
                final String listSlug = (listOwnerSlug.length > 1) ? listOwnerSlug[1] : listOwnerSlug[0];

                usrlist = getTwitter(checkIndexFromScreenname(listOwner), false).showUserList(listOwner, listSlug);
            }
        } catch (final Exception e) {
            // usrlist = null;
            WriteLog.write(context, e);

            rslt[0] = -1;
            rslt[1] = -1;
            return rslt;
        }

        final User myUser = getUser();

        if ((usrlist != null) && (myUser != null)) {
            final boolean pref_hide_item_myicon_userEqualsMyuser =
                    (myUser.getScreenName().equals("")) ? false : (pref_hide_item_myicon && (usrlist.getUser().getScreenName().equals(myUser.getScreenName())));

            new Thread(new Runnable() {
                public final void run() {
                    if ((pref_hide_tl_headericon) || (pref_hide_item_myicon_userEqualsMyuser)) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                imageView.setImageResource(drawable.btn_default);

                                if (lightingColorFilterTlHeadericon != null) {
                                    try {
                                        imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                    } catch (final Exception e) {
                                        WriteLog.write(context, e);
                                        // toast(context.getString(R.string.exception));
                                    }
                                }
                            }
                        });
                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public final void run() {
                                getIcon(imageView, usrlist.getUser().getProfileImageURLHttps());

                                if (pref_show_profilebannerimage) {
                                    final String profileBannerURL = usrlist.getUser().getProfileBannerURL() == null ? "" : usrlist.getUser().getProfileBannerURL();

                                    if (profileBannerURL.equals("") == false) {
                                        getIcon(imageViewBackground, profileBannerURL);
                                    } else {
                                        getIcon(imageViewBackground, usrlist.getUser().getProfileBackgroundImageUrlHttps());
                                    }
                                } else {
                                    getIcon(imageViewBackground, usrlist.getUser().getProfileBackgroundImageUrlHttps());
                                }

                                if (lightingColorFilterTlHeadericon != null) {
                                    try {
                                        imageView.setColorFilter(lightingColorFilterTlHeadericon);
                                        imageViewBackground.setColorFilter(lightingColorFilterTlHeadericon);
                                    } catch (final Exception e) {
                                        WriteLog.write(context, e);
                                    }
                                }
                            }
                        });
                    }
                }
            }).start();

            final int sbInitSize = 1266; // 949 * 4/3
            final StringBuilder userlistinfoBuilder = new StringBuilder(sbInitSize);
            userlistinfoBuilder.append("<font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">Full Name: </font><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\"><a href=\"https://twitter.com");
            userlistinfoBuilder.append(usrlist.getURI());
            userlistinfoBuilder.append("\">");
            if (pref_hide_item_myname) {
                screenName = checkScreennameFromIndex(checkIndexFromPrefTwtr());
                userlistinfoBuilder.append((usrlist.getFullName()).replace("@" + screenName, ""));
            } else {
                userlistinfoBuilder.append(usrlist.getFullName());
            }
            userlistinfoBuilder.append("</a></font>");
            userlistinfoBuilder.append(SP4);
            userlistinfoBuilder.append("<small><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">List Id: ");
            userlistinfoBuilder.append(usrlist.getId());
            userlistinfoBuilder.append("</font></small>");
            userlistinfoBuilder.append(BR);
            userlistinfoBuilder.append("<font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">Description: </font><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">");
            userlistinfoBuilder.append((pref_enable_htmlescape ? HtmlEscape.escape(usrlist.getDescription()) : usrlist.getDescription()) //
                    .replaceAll(patternStr_hashtag, BIO_STR_1) //
                    .replaceAll(patternStr_screenname, BIO_STR_2) //
                    .replaceAll("\n", BR));
            userlistinfoBuilder.append("</font>");
            userlistinfoBuilder.append(BR);
            userlistinfoBuilder.append("<font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">Subscribers: </font><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\"><a href=\"https://twitter.com");
            userlistinfoBuilder.append(usrlist.getURI());
            userlistinfoBuilder.append("/subscribers\">");
            userlistinfoBuilder.append(usrlist.getSubscriberCount());
            userlistinfoBuilder.append("</a></font><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\"> / Members: </font><font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\"><a href=\"https://twitter.com");
            userlistinfoBuilder.append(usrlist.getURI());
            userlistinfoBuilder.append("/members\">");
            userlistinfoBuilder.append(usrlist.getMemberCount());
            userlistinfoBuilder.append("</a></font>");
            userlistinfoBuilder.append(BR);
            userlistinfoBuilder.append("<font color=\"");
            userlistinfoBuilder.append(pref_header_fontcolor);
            userlistinfoBuilder.append("\">");
            userlistinfoBuilder.append(((usrlist.isPublic()) ? "Public" : "Private"));
            userlistinfoBuilder.append("</font>");

            textView.setText(Html.fromHtml(userlistinfoBuilder.toString()));

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String screenName = usrlist.getUser().getScreenName();
                    startTlUser(checkIndexFromScreenname(screenName), screenName);
                }
            });

            imageViewBackground.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pref_show_profilebannerimage) {
                        final String profileBannerURL = usrlist.getUser().getProfileBannerURL() == null ? "" : usrlist.getUser().getProfileBannerURL();

                        if (profileBannerURL.equals("") == false) {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileBannerURL));
                            context.startActivity(intent);
                        } else {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usrlist.getUser().getProfileBackgroundImageUrlHttps()));
                            context.startActivity(intent);
                        }
                    } else {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usrlist.getUser().getProfileBackgroundImageUrlHttps()));
                        context.startActivity(intent);
                    }
                }
            });

            rslt[0] = usrlist.getId();
            rslt[1] = usrlist.getMemberCount();
        }

        try {
            notifyDataSetChanged();
            WriteLog.write(context, "setUserlistInfo() notifyDataSetChanged()");
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }

        try {
            ((Activity) context).dismissDialog(R.string.loading);
        } catch (IllegalArgumentException e) {
        }

        return rslt;
    }

    final void showApiStatuses() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API_STATUS_URL));
        context.startActivity(intent);
    }

    final void showApiStatuses(final WebView webView1) {
        if (!((Activity) context).isFinishing()) {
            ((Activity) context).showDialog(R.string.check_apistatus);
        }
        new Thread(new Runnable() {
            @Override
            public final void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        webView1.loadUrl(ListAdapter.API_STATUS_URL);
                        webView1.setVisibility(View.VISIBLE);
                        try {
                            ((Activity) context).dismissDialog(R.string.check_apistatus);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                });
            }
        }).start();
    }

    private final String showBlockDialog(final String uScreenname) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean pref_confirmdialog_block = pref_app.getBoolean("pref_confirmdialog_block", true);

        new Thread(new Runnable() {
            public final void run() {

                ((Activity) context).runOnUiThread(new Runnable() {
                    public final void run() {

                        if (pref_confirmdialog_block) {
                            final ArrayList<String> ourScreenNamesBlock = new ArrayList<String>(default_user_index_size * 2);
                            for (int idx = 0; idx < pref_user_index_size; idx++) {
                                if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                    if (checkUniq(idx)) {
                                        final String itemname = pref_twtr.getString("screen_name_" + idx, "");

                                        try {
                                            final Relationship relationship = getTwitter(checkIndexFromScreenname(itemname), false).showFriendship(itemname, uScreenname);

                                            if (relationship.isSourceBlockingTarget()) {
                                                ourScreenNamesBlock.add("@" + itemname + context.getString(R.string.block_destroy_by));
                                            } else {
                                                ourScreenNamesBlock.add("@" + itemname + context.getString(R.string.block_create_by));
                                            }
                                        } catch (final TwitterException e) {
                                            ourScreenNamesBlock.add("@" + itemname + context.getString(R.string.block_create_by) + " *");
                                            ourScreenNamesBlock.add("@" + itemname + context.getString(R.string.block_destroy_by) + " *");

                                            try {
                                                twitterException(e);
                                            } catch (Exception ex) {
                                                toast(context.getString(R.string.cannot_access_twitter));
                                            }
                                        } catch (final Exception e) {
                                            WriteLog.write(context, e);
                                        }
                                    }
                                }
                            }
                            final boolean[] checkedItems = new boolean[ourScreenNamesBlock.size()];
                            final String[] items = ourScreenNamesBlock.toArray(new String[ourScreenNamesBlock.size()]);

                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_block).setCancelable(true).setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            }).setPositiveButton(R.string.confirmdialog_block, new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface dialog, final int which) {
                                    for (int i = 0; i < ourScreenNamesBlock.size(); i++) {
                                        if (checkedItems[i] == true) {
                                            final int idx = checkIndexFromScreenname(((items[which].split("で"))[0]).replace("@", ""));

                                            if (items[i].contains(context.getString(R.string.block_create))) {
                                                afterAction("onaction_block", block(true, idx, uScreenname));
                                            } else if (items[i].contains(context.getString(R.string.block_destroy))) {
                                                afterAction("onaction_block", block(false, idx, uScreenname));
                                            }
                                        }
                                    }
                                }
                            }).create().show();
                        } else {
                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                        if (checkUniq(idx)) {
                                            afterAction("onaction_block", block(true, idx, uScreenname));
                                        }
                                    }
                                }
                            } else {
                                afterAction("onaction_block", block(true, checkIndexFromPrefTwtr(), uScreenname));
                            }
                        }

                    }
                });

            }
        }).start();

        return "";
    }

    private final String showFollowDialog(final String uScreenname) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        new Thread(new Runnable() {
            public final void run() {

                ((Activity) context).runOnUiThread(new Runnable() {
                    public final void run() {
                        final ArrayList<String> ourScreenNamesFollow = new ArrayList<String>(default_user_index_size * 2);
                        for (int idx = 0; idx < pref_user_index_size; idx++) {
                            if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                if (checkUniq(idx)) {
                                    final String itemname = pref_twtr.getString("screen_name_" + idx, "");

                                    try {
                                        final Relationship relationship = getTwitter(checkIndexFromScreenname(itemname), false).showFriendship(itemname, uScreenname);

                                        if (relationship.isSourceFollowingTarget()) {
                                            ourScreenNamesFollow.add("@" + itemname + context.getString(R.string.follow_destroy_by));
                                        } else {
                                            ourScreenNamesFollow.add("@" + itemname + context.getString(R.string.follow_create_by));
                                        }
                                    } catch (final TwitterException e) {
                                        ourScreenNamesFollow.add("@" + itemname + context.getString(R.string.follow_create_by) + " *");
                                        ourScreenNamesFollow.add("@" + itemname + context.getString(R.string.follow_destroy_by) + " *");

                                        try {
                                            twitterException(e);
                                        } catch (Exception ex) {
                                            toast(context.getString(R.string.cannot_access_twitter));
                                        }
                                    } catch (final Exception e) {
                                        WriteLog.write(context, e);
                                    }
                                }
                            }
                        }
                        final boolean[] checkedItems = new boolean[ourScreenNamesFollow.size()];
                        final String[] items = ourScreenNamesFollow.toArray(new String[ourScreenNamesFollow.size()]);

                        new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_friendship).setCancelable(true).setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                checkedItems[which] = isChecked;
                            }
                        }).setPositiveButton(R.string.confirmdialog_friendship, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                for (int i = 0; i < ourScreenNamesFollow.size(); i++) {
                                    if (checkedItems[i] == true) {
                                        final String sn = ((items[i].split("で"))[0]).replace("@", "");

                                        if (sn.equals(uScreenname) == false) {
                                            final int idx = checkIndexFromScreenname(sn);

                                            if (items[i].contains(context.getString(R.string.follow_create))) {
                                                afterAction("onaction_follow_create", friendships(true, idx, uScreenname));
                                            } else if (items[i].contains(context.getString(R.string.follow_destroy))) {
                                                afterAction("onaction_follow_destroy", friendships(false, idx, uScreenname));
                                            }
                                        }
                                    }
                                }
                            }
                        }).create().show();

                    }
                });

            }
        }).start();

        return "";
    }

    private final String showListDialog(final User finalUsr) {

        if (!((Activity) context).isFinishing()) {
            try {
                ((Activity) context).dismissDialog(R.string.list_user_add);
            } catch (final Exception e) {
            }
            ((Activity) context).showDialog(R.string.list_user_add);
        }
        new Thread(new Runnable() {
            @Override
            public final void run() {

                final ResponseList<UserList> lists = getSortedOurslist();
                final ArrayList<String> listNames = new ArrayList<String>(default_user_index_size * 20);

                try {
                    for (final UserList list : lists) {
                        try {
                            getTwitter(checkIndexFromScreenname(list.getUser().getScreenName()), false).showUserListMembership(list.getId(), finalUsr.getId());

                            //  リストに追加済み
                            listNames.add(list.getFullName() + " " + context.getString(R.string.list_user_add_to) + " *");
                            listNames.add(list.getFullName() + context.getString(R.string.list_user_remove_from));
                        } catch (final TwitterException e) {
                            if (e.getStatusCode() == 404) {
                                // リストに追加されていない
                                listNames.add(list.getFullName() + context.getString(R.string.list_user_add_to));
                                listNames.add(list.getFullName() + " " + context.getString(R.string.list_user_remove_from) + " *");
                            } else if (e.exceededRateLimitation()) {
                                // 規制
                                listNames.add(list.getFullName() + context.getString(R.string.list_user_add_to) + " *");
                                listNames.add(list.getFullName() + context.getString(R.string.list_user_remove_from) + " *");
                            } else {
                                listNames.add(list.getFullName() + context.getString(R.string.list_user_add_to) + " *");
                                listNames.add(list.getFullName() + context.getString(R.string.list_user_remove_from) + " *");
                            }
                        } catch (final Exception e) {
                            WriteLog.write(context, e);
                        }
                    }
                } catch (final Exception e) {
                    WriteLog.write(context, e);
                }

                final boolean[] checkedItems = new boolean[listNames.size()];
                final String[] items = listNames.toArray(new String[listNames.size()]);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {

                        new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_list).setCancelable(true).setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                checkedItems[which] = isChecked;
                            }
                        }).setPositiveButton(R.string.confirmdialog_list, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                for (int i = 0; i < listNames.size(); i++) {
                                    if (checkedItems[i] == true) {
                                        final int i2 = i / 2;
                                        if (items[i].contains(context.getString(R.string.list_user_add_to))) {
                                            afterAction("onaction_follow_create", listUser(true, lists.get(i2).getId(), lists.get(i2).getFullName(), finalUsr.getId(), finalUsr.getScreenName()));
                                        } else if (items[i].contains(context.getString(R.string.list_user_remove_from))) {
                                            afterAction("onaction_follow_destroy", listUser(false, lists.get(i2).getId(), lists.get(i2).getFullName(), finalUsr.getId(), finalUsr.getScreenName()));
                                        }
                                    }
                                }
                            }
                        }).create().show();

                        try {
                            ((Activity) context).dismissDialog(R.string.list_user_add);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                });
            }
        }).start();

        return "";
    }

    private final String showR4sDialog(final String uScreenname) {
        pref_app = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean pref_confirmdialog_r4s = pref_app.getBoolean("pref_confirmdialog_r4s", true);

        new Thread(new Runnable() {
            public final void run() {

                ((Activity) context).runOnUiThread(new Runnable() {
                    public final void run() {
                        final String[] finalOurScreenNamesR4sby = getOurScreenNames("@", context.getString(R.string.r4s_by));
                        final boolean[] checkedItems = getConfirmdialogCheckedItems();

                        if (pref_confirmdialog_r4s) {

                            new AlertDialog.Builder(context).setTitle(R.string.confirmdialog_r4s).setCancelable(true).setMultiChoiceItems(finalOurScreenNamesR4sby, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                public final void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            }).setPositiveButton(R.string.confirmdialog_r4s, new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface dialog, final int which) {
                                    for (int idx = 0; idx < finalOurScreenNamesR4sby.length; idx++) {
                                        if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                            if (checkUniq(idx)) {
                                                if (checkedItems[idx] == true) {
                                                    afterAction("onaction_r4s", r4s(idx, uScreenname));
                                                }
                                            }
                                        }
                                    }
                                }
                            }).create().show();
                        } else {
                            if (pref_enable_confirmdialog_hidden_allaccount) {
                                for (int idx = 0; idx < pref_user_index_size; idx++) {
                                    if (isConnected(pref_twtr.getString("status_" + idx, ""))) {
                                        if (checkUniq(idx)) {
                                            afterAction("onaction_r4s", r4s(idx, uScreenname));
                                        }
                                    }
                                }
                            } else {
                                afterAction("onaction_r4s", r4s(checkIndexFromPrefTwtr(), uScreenname));
                            }
                        }

                    }
                });

            }
        }).start();

        return "";
    }

    final void showRateLimits() {
        if (!((Activity) context).isFinishing()) {
            ((Activity) context).showDialog(R.string.check_ratelimit);
        }

        new Thread(new Runnable() {
            @Override
            public final void run() {

                final String finalMessage = getRateLimits();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        new AlertDialog.Builder(context).setTitle(context.getString(R.string.check_ratelimit) + " [@" + pref_twtr.getString("screen_name_" + checkIndexFromPrefTwtr(), "") + "]").setMessage(finalMessage).setPositiveButton(R.string.ok, null).create().show();

                        try {
                            ((Activity) context).dismissDialog(R.string.check_ratelimit);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                });
            }
        }).start();
    }

    final void showRateLimits(final WebView webView1) {
        if (!((Activity) context).isFinishing()) {
            ((Activity) context).showDialog(R.string.check_ratelimit);
        }
        new Thread(new Runnable() {
            @Override
            public final void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        webView1.loadDataWithBaseURL(null, getRateLimits().replaceAll(System.getProperty("line.separator"), "<br />"), "text/html", "UTF-8", null);
                        webView1.setVisibility(View.VISIBLE);
                        try {
                            ((Activity) context).dismissDialog(R.string.check_ratelimit);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                });
            }
        }).start();
    }

    final void showUpdateTweetLiteDialog(int index) {
        if ((pref_user_index_size <= index) || (index <= -1)) {
            index = checkIndexFromPrefTwtr();
        }

        final int finalIndex = index;
        WriteLog.write(context, "showUpdateTweetLiteDialog(" + index + ") finalIndex: " + finalIndex);
        new Thread(new Runnable() {
            @Override
            public final void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        final EditText editText = new EditText(context);
                        final int defaultTextColor = editText.getTextColors().getDefaultColor();
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public final void afterTextChanged(final Editable s) {
                                if (getStringLength(editText.getText().toString()) > 140) {
                                    try {
                                        editText.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                                    } catch (IllegalArgumentException e) {
                                    }
                                } else {
                                    try {
                                        editText.setTextColor(defaultTextColor);
                                    } catch (IllegalArgumentException e) {
                                    }
                                }
                            }

                            @Override
                            public final void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                            }

                            @Override
                            public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                                if (getStringLength(editText.getText().toString()) > 140) {
                                    try {
                                        editText.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                                    } catch (final IllegalArgumentException e) {
                                    }
                                } else {
                                    try {
                                        editText.setTextColor(defaultTextColor);
                                    } catch (final IllegalArgumentException e) {
                                    }
                                }
                            }
                        });
                        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public final void onFocusChange(final View arg0, final boolean arg1) {
                                if (getStringLength(editText.getText().toString()) > 140) {
                                    try {
                                        editText.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
                                    } catch (final IllegalArgumentException e) {
                                    }
                                } else {
                                    try {
                                        editText.setTextColor(defaultTextColor);
                                    } catch (final IllegalArgumentException e) {
                                    }
                                }
                            }
                        });
                        new AlertDialog.Builder(context).setView(editText).setTitle(R.string.updatetweet_lite).setNegativeButton(R.string.app_name_update, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                final Intent intent = new Intent();
                                intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet");
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.putExtra("str1", "");
                                intent.putExtra("str2", editText.getText().toString());
                                intent.putExtra("str3", "");
                                intent.putExtra("str4", "");
                                intent.putExtra("str5", "");
                                intent.putExtra("inReplyToStatusId", "");
                                intent.putExtra("tweetImagePathString", "");
                                intent.putExtra("skip", "1");
                                context.startActivity(intent);
                                return;
                            }
                        }).setNeutralButton(R.string.other_accounts, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                new AlertDialog.Builder(context).setTitle(R.string.updatetweet_lite).setItems(finalOurScreenNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(final DialogInterface dialog, final int which) {
                                        if (finalOurScreenNames[which].equals(" - ") == false) {
                                            showUpdateTweetLiteDialog(checkIndexFromScreenname(finalOurScreenNames[which]));
                                        }
                                    }
                                }).create().show();
                            }
                        }).setPositiveButton(context.getString(R.string.post) + ": " + checkScreennameFromIndex(finalIndex), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface dialog, final int which) {
                                tweetLite(Integer.toString(finalIndex), editText.getText().toString());
                            }
                        }).create().show();
                    }
                });
            }
        }).start();
        return;

    }

    final void startTl(final int index, final String uriString) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Tl");
        intent.setData(Uri.parse(uriString));
        intent.putExtra("index", index);
        staticContext.startActivity(intent);
    }

    final void startTlFavorite(final int index) {
        startTl(index, ListAdapter.TWITTER_BASE_URI + "favorites");
    }

    final void startTlHome(final int index) {
        startTl(index, ListAdapter.TWITTER_BASE_URI);
    }

    final void startTlMention(final int index) {
        startTl(index, ListAdapter.TWITTER_BASE_URI + "mentions");
    }

    final void startTlUser(final int index) {
        startTl(index, ListAdapter.TWITTER_BASE_URI + checkScreennameFromIndex(index));
    }

    final void startTlUser(final int index, final String screenName) {
        startTl(index, ListAdapter.TWITTER_BASE_URI + screenName);
    }

    final void startUpdateTweet(final long statusId, final String str1) {
        final Intent intent = new Intent();
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet");
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("inReplyToStatusId", Long.toString(statusId));
        intent.putExtra("str1", str1);
        context.startActivity(intent);
    }

    public final String toast(final String text) {
        if (!((Activity) context).isFinishing()) {
            if (currentThreadIsUiThread()) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public final void run() {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return text;
    }

    private final void tweetLite(final String indexStr, final String str) {

        if (pendingIntent == null) {
            final Intent intent = new Intent(context, UpdateTweet.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        final Intent intent2 = new Intent();
        intent2.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.UpdateTweet");
        intent2.setAction(Intent.ACTION_VIEW);
        intent2.putExtra("index", indexStr);
        intent2.putExtra("str1", "");
        intent2.putExtra("str2", str);
        intent2.putExtra("str3", "");
        intent2.putExtra("str4", "");
        intent2.putExtra("str5", "");
        intent2.putExtra("inReplyToStatusId", "");
        intent2.putExtra("tweetImagePathString", "");
        intent2.putExtra("skip", "1");
        final PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        pref_app = PreferenceManager.getDefaultSharedPreferences(context);

        // 共通ヘッダ・フッタ
        final Boolean pref_enable_common_header = pref_app.getBoolean("pref_enable_common_header", false);
        final Boolean pref_enable_common_footer = pref_app.getBoolean("pref_enable_common_footer", false);
        final String pref_common_header = (pref_enable_common_header) ? pref_app.getString("pref_common_header", "") : "";
        final String pref_common_footer = (pref_enable_common_footer) ? pref_app.getString("pref_common_footer", "") : "";

        new Thread(new Runnable() {
            @Override
            public final void run() {
                final String tweetstr = StringUtil.getTweetString(pref_common_header, str, pref_common_footer);
                if (tweetstr.equals("")) {
                    toast(context.getString(R.string.empty_tweettext));
                    return;
                }

                final boolean pref_enable_notification_doing_tweet = pref_app.getBoolean("pref_enable_notification_doing_tweet", true);
                if (pref_enable_notification_doing_tweet) {
                    final boolean pref_enable_notification_vibration_doing_tweet = pref_app.getBoolean("pref_enable_notification_vibration_doing_tweet", false);
                    final boolean pref_enable_notification_led_doing_tweet = pref_app.getBoolean("pref_enable_notification_led_doing_tweet", false);
                    final int pref_notification_led_color_doing_tweet = getPrefColor("pref_notification_led_color_doing_tweet", "#0000ff", default_notification_led_color_doing_tweet);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            notification(NOTIFY_DOING_SEND, R.drawable.ic_launcher, context.getString(R.string.doing_send), context.getString(R.string.doing_send), context.getString(R.string.app_name), true, false, pref_enable_notification_led_doing_tweet
                                    ? pref_notification_led_color_doing_tweet : Color.TRANSPARENT, pref_enable_notification_vibration_doing_tweet, pendingIntent2, true);
                        }
                    });
                }

                int index;
                try {
                    index = Integer.parseInt(indexStr);
                } catch (final NumberFormatException e) {
                    index = checkIndexFromPrefTwtr();
                }
                if (index <= -1) {
                    index = checkIndexFromPrefTwtr();
                }

                initUserOauth(index);
                screenName = checkScreennameFromIndex(index);
                WriteLog.write(context, "initUserOauth(" + indexStr + ") screenName: " + screenName);

                final boolean pref_enable_notification_done_tweet = pref_app.getBoolean("pref_enable_notification_done_tweet", true);
                final boolean pref_enable_notification_twitterexception = pref_app.getBoolean("pref_enable_notification_twitterexception", true);

                final StatusUpdate statusUpdate = new StatusUpdate((tweetstr.length() > 140) ? tweetstr.substring(0, 140) : tweetstr);
                final String finalTweetstr = tweetstr;
                Status updatedstatus;
                try {
                    updatedstatus = getTwitter(index, false).updateStatus(statusUpdate);
                    WriteLog.write(context, "twitter.updateStatus()");

                    if (tweetstr.contains(updatedstatus.getText())) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                cancelNotification(NOTIFY_DOING_SEND);

                                if (pref_enable_notification_done_tweet) {
                                    final boolean pref_enable_notification_vibration_done_tweet = pref_app.getBoolean("pref_enable_notification_vibration_done_tweet", false);
                                    final boolean pref_enable_notification_led_done_tweet = pref_app.getBoolean("pref_enable_notification_led_done_tweet", false);
                                    int pref_notification_led_color_done_tweet;
                                    try {
                                        pref_notification_led_color_done_tweet = Color.parseColor(pref_app.getString("pref_notification_led_color_done_tweet", "#0000ff"));
                                    } catch (final Exception e) {
                                        pref_notification_led_color_done_tweet = default_notification_led_color_done_tweet;
                                        WriteLog.write(context, e);
                                    }

                                    notification(NOTIFY_DONE_TWEET, R.drawable.done, context.getString(R.string.done_tweet), finalTweetstr + System.getProperty("line.separator") + "[@" + screenName
                                            + "]", context.getString(R.string.app_name), true, false, pref_enable_notification_led_done_tweet ? pref_notification_led_color_done_tweet
                                            : Color.TRANSPARENT, pref_enable_notification_vibration_done_tweet, pendingIntent, true);

                                    new Thread(new Runnable() {
                                        @Override
                                        public final void run() {
                                            final int pref_notification_duration_done_tweet = getPrefInt("pref_notification_duration_done_tweet", default_notification_duration_done_tweet_string);
                                            try {
                                                Thread.sleep(pref_notification_duration_done_tweet);
                                            } catch (final InterruptedException e) {
                                                WriteLog.write(context, e);
                                            }
                                            cancelNotification(NOTIFY_DONE_TWEET);
                                        }
                                    }).start();
                                }
                            }
                        });
                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                cancelNotification(NOTIFY_DOING_SEND);
                            }
                        });

                    }
                } catch (final TwitterException e) {
                    updatedstatus = null;
                    if (e.exceededRateLimitation()) {
                        toast(context.getString(R.string.ratelimit_exhausted));
                    } else {
                        updatedstatus = null;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public final void run() {
                                cancelNotification(NOTIFY_DOING_SEND);

                                if (pref_enable_notification_twitterexception) {
                                    final boolean pref_enable_notification_vibration_twitterexception = pref_app.getBoolean("pref_enable_notification_vibration_twitterexception", false);
                                    final boolean pref_enable_notification_led_twitterexception = pref_app.getBoolean("pref_enable_notification_led_twitterexception", false);
                                    int pref_notification_led_color_twitterexception;
                                    try {
                                        pref_notification_led_color_twitterexception = Color.parseColor(pref_app.getString("pref_notification_led_color_twitterexception", "#ffff00"));
                                    } catch (final Exception e) {
                                        pref_notification_led_color_twitterexception = default_notification_led_color_twitterexception;
                                        WriteLog.write(context, e);
                                    }

                                    notification(NOTIFY_TWITTER_EXCEPTION, R.drawable.exception, "TwitterException", e.getErrorMessage() + System.getProperty("line.separator") + "[@" + screenName
                                            + "]", context.getString(R.string.app_name), false, false, pref_enable_notification_led_twitterexception ? pref_notification_led_color_twitterexception
                                            : Color.TRANSPARENT, pref_enable_notification_vibration_twitterexception, pendingIntent2, true);
                                }
                            }
                        });

                        WriteLog.write(context, e);
                    }
                }
                if (updatedstatus != null) {
                    WriteLog.write(context, "(updatedstatus != null)");

                    final boolean pref_enable_ringtone_ontweet = pref_app.getBoolean("pref_enable_ringtone_ontweet", true);
                    final String pref_ringtone_ontweet_updatetweet = pref_app.getString("pref_ringtone_ontweet_updatetweet", "");
                    if (pref_enable_ringtone_ontweet && (pref_ringtone_ontweet_updatetweet != null) && (pref_ringtone_ontweet_updatetweet.equals("") == false)) {
                        final MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(pref_ringtone_ontweet_updatetweet));
                        mediaPlayer.setLooping(false);
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }
            }
        }).start();
    }

    final void twitterException(final TwitterException e) {
        if (e.exceededRateLimitation()) {
            toast(context.getString(R.string.ratelimit_exhausted));
        } else if (e.isCausedByNetworkIssue()) {
            toast(context.getString(R.string.network_issue));
        } else if (e.isErrorMessageAvailable()) {
            toast(context.getString(R.string.api_is_unavailable));
        } else if (e.resourceNotFound()) {
            toast(context.getString(R.string.resource_notfound));
        } else if (e.getErrorCode() == 404) {
            toast(context.getString(R.string.tweet_notfound));
        } else {
            toast(context.getString(R.string.twitterexception) + e.getStatusCode());
            WriteLog.write(context, e);
        }
    }

    final void uriStringHistoryAdd(final String uriString) {
        uriStringHistory.add(uriString);
    }

    final ArrayList<String> uriStringHistoryGetArray() {
        final ArrayList<String> uriStringHistoryArray = uriStringHistory.getArray();
        collectionsUtil.removeDuplicate(uriStringHistoryArray);
        return uriStringHistoryArray;
    }

    final String uriStringHistoryRedo() {
        return uriStringHistory.redo();
    }

    final String uriStringHistoryUndo() {
        return uriStringHistory.undo();
    }

    private final Intent uriStringShortenedToIntent(final String uriStringShortened) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final String TAG = StringUtil.uriStringToTag(uriStringShortened, true);

        if (TAG.equals("home")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI);
        } else if (TAG.equals("home(s)")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI + "(s)");
        } else if (TAG.equals("mention")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI + "mentions");
        } else if (TAG.equals("search")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI + "search/" + ((uriStringShortened.split("\\?"))[1]));
        } else if (TAG.equals("search(s)")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI + "search/" + ((uriStringShortened.split("\\?"))[1]) + "(s)");
        } else if (TAG.equals("user")) {
            return uriStringToTlIntent(uriStringShortened, TWITTER_BASE_URI + uriStringShortened);
        } else if (TAG.equals("userfav")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI + "favorites");
        } else if (TAG.equals("userlist")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI
                    + ((uriStringShortened.contains("/lists/")) ? uriStringShortened : uriStringShortened.replace("/", "/lists/")));
        } else if (TAG.equals("userlist(s)")) {
            return uriStringToTlIntent((uriStringShortened.split("#"))[0], TWITTER_BASE_URI
                    + ((uriStringShortened.contains("/lists/")) ? uriStringShortened : uriStringShortened.replace("/", "/lists/")) + "(s)");
        }
        return intent;
    }

    final Intent uriStringToIntent(final String uriString) {
        if (uriString.startsWith(ListAdapter.TWITTER_BASE_URI)) {
            return uriStringToTlIntent("", uriString);
        } else {
            return uriStringShortenedToIntent(uriString);
        }
    }

    private final Intent uriStringToTlIntent(final String screenName, final String uriString) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Tl");
        intent.setData(Uri.parse(uriString));
        intent.putExtra("index", Integer.toString((screenName.equals("") ? checkIndexFromPrefTwtr() : checkIndexFromScreenname(screenName))));

        return intent;
    }

    private final String userrt(final int index, final Status tweet) {
        WriteLog.write(context, "userrt(" + Integer.toString(index) + ",");
        WriteLog.write(context, tweet);
        try {
            pref_app = PreferenceManager.getDefaultSharedPreferences(context);
            final String pref_userrt_format = pref_app.getString("pref_userrt_format", "RT @%USER%: %TEXT%");
            final boolean pref_enable_userrt_format_via = pref_app.getBoolean("pref_enable_userrt_format_via", false);

            if (tweet.isRetweet()) {
                if (tweet.getRetweetedStatus() != null) {
                    String newStatusText = tweet.getText();
                    if (pref_enable_userrt_format_via) {
                        newStatusText =
                                pref_userrt_format.replace("%USER%", tweet.getUser().getScreenName()).replace("%TEXT%", pref_userrt_format.replace("%USER%", tweet.getRetweetedStatus().getUser().getScreenName()).replace("%TEXT%", tweet.getRetweetedStatus().getText()));
                    } else {
                        newStatusText =
                                pref_userrt_format.replace("%USER%", tweet.getRetweetedStatus().getUser().getScreenName()).replace("%TEXT%", tweet.getRetweetedStatus().getText());
                    }
                    final StatusUpdate statusUpdate = new StatusUpdate((newStatusText.length() > 140) ? newStatusText.substring(0, 140) : newStatusText);
                    WriteLog.write(context, "userrt() 公式RT " + (pref_enable_userrt_format_via ? "" : "!") + "pref_enable_userrt_format_via newStatusText: " + newStatusText);
                    statusUpdate.setInReplyToStatusId(tweet.getRetweetedStatus().getId());
                    getTwitter(index, false).updateStatus(statusUpdate);
                    return (context.getString(R.string.done_userrt) + ": " + newStatusText + " [@" + checkScreennameFromIndex(index) + "]");
                }
            }
            if (((tweet.getText()).contains("RT @")) && (pref_enable_userrt_format_via == false)) {
                final int lastIndexOf = tweet.getText().lastIndexOf("RT @");
                final String newStatusText = (lastIndexOf > -1) ? tweet.getText().substring(lastIndexOf) : tweet.getText();
                final StatusUpdate statusUpdate = new StatusUpdate((newStatusText.length() > 140) ? newStatusText.substring(0, 140) : newStatusText);
                WriteLog.write(context, "userrt() 非公式RT newStatusText: " + newStatusText);
                statusUpdate.setInReplyToStatusId(tweet.getId());
                getTwitter(index, false).updateStatus(statusUpdate);
                return (context.getString(R.string.done_userrt) + ": " + newStatusText + " [@" + checkScreennameFromIndex(index) + "]");
            }
            final String newStatusText = pref_userrt_format.replace("%USER%", tweet.getUser().getScreenName()).replace("%TEXT%", tweet.getText());
            final StatusUpdate statusUpdate = new StatusUpdate((newStatusText.length() > 140) ? newStatusText.substring(0, 140) : newStatusText);
            WriteLog.write(context, "userrt() 通常 newStatusText: " + newStatusText);
            statusUpdate.setInReplyToStatusId(tweet.getId());
            getTwitter(index, false).updateStatus(statusUpdate);
            return (context.getString(R.string.done_userrt) + ": " + newStatusText + " [@" + checkScreennameFromIndex(index) + "]");
        } catch (final TwitterException e) {
            twitterException(e);
        } catch (final Exception e) {
            WriteLog.write(context, e);
        }
        return "";
    }

    private final void viewMute(final boolean mute, final ImageView imageView1, final ImageView imageView2, final TextView textView, final View view) {
        if (mute) {
            final View[] views = {imageView1, imageView2, textView, view};
            viewMute(views);
        } else {
            imageView1.setVisibility(View.VISIBLE);
            // imageView2.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);

            imageView1.setLayoutParams(new LinearLayout.LayoutParams(pref_tl_iconsize1, pref_tl_iconsize1));
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pref_tl_iconsize2, pref_tl_iconsize2);
            layoutParams.setMargins((pref_enable_singleline ? 0 : pref_tl_iconsize2), (pref_enable_singleline ? pref_tl_iconsize2 : 0), 0, 0);
            imageView2.setLayoutParams(layoutParams);
            textView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        }
    }

    private final void viewMute(final View[] views) {
        for (final View view : views) {
            view.setVisibility(View.GONE);
            if (view instanceof ImageView) {
                ((ImageView) view).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            } else if (view instanceof TextView) {
                ((TextView) view).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }
    }
}
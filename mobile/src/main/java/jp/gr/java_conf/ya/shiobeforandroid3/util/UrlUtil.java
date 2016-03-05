package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import jp.gr.java_conf.ya.shiobeforandroid3.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

public final class UrlUtil {

	private Context context;
	private boolean pref_enable_httpurlconnection_follow_redirects;
	private int pref_timeout_connection_expanduri;
	private int pref_timeout_connection_shortenuri;
	private int pref_timeout_so_expanduri;
	private int pref_timeout_so_shortenuri;

	private String pref_tl_fontcolor_statustext_uri = "#0000ff";
	private String fontColorTagpart = "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\">";

	private SharedPreferences pref_app;

	// @formatter:off
	private static final String[] HEX = { "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
			"9A", "9B", "9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8",
			"B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7",
			"D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15",
			"16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33", "34",
			"35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53",
			"54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72",
			"73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", };

	public UrlUtil(final Context context) {
		this.context = context;

		pref_app = PreferenceManager.getDefaultSharedPreferences(context);

		pref_enable_httpurlconnection_follow_redirects = pref_app.getBoolean("pref_enable_httpurlconnection_follow_redirects", ListAdapter.default_enable_httpurlconnection_follow_redirects);
		pref_timeout_connection_expanduri = ListAdapter.getPrefInt(context, "pref_timeout_connection_expanduri", ListAdapter.default_timeout_connection_string);
		pref_timeout_connection_shortenuri = ListAdapter.getPrefInt(context, "pref_timeout_connection_shortenuri", ListAdapter.default_timeout_connection_string);
		pref_timeout_so_expanduri = ListAdapter.getPrefInt(context, "pref_timeout_so_expanduri", ListAdapter.default_timeout_so_string);
		pref_timeout_so_shortenuri = ListAdapter.getPrefInt(context, "pref_timeout_so_shortenuri", ListAdapter.default_timeout_so_string);

		pref_tl_fontcolor_statustext_uri = pref_app.getString("pref_tl_fontcolor_statustext_uri", "#0000ff");
		fontColorTagpart = "<font color=\"" + pref_tl_fontcolor_statustext_uri + "\">";
	}

	private final String encode(final String aUrl) {
		String encoded = "";
		try {
			final byte[] tBytes = aUrl.getBytes("ISO-8859-1");
			final int tLength = tBytes.length;
			final StringBuilder tBuilder = new StringBuilder(tLength * 3);
			for (int tIndex = 0; tIndex < tLength; tIndex++) {
				final int tIntAt = tBytes[tIndex];
				if (tIntAt < 0) {
					tBuilder.append('%');
					tBuilder.append(HEX[tIntAt + 128]);
				} else {
					tBuilder.append((char) tIntAt);
				}
			}
			encoded = tBuilder.toString();
		} catch (UnsupportedEncodingException e) {
			WriteLog.write(context, e);
		}
		return encoded;
	}

	public final String expand_image_url(final String statusText, final String imageSize) {
		final String imgSizeTagpart = " height=\"" + imageSize + "\""; // " width=\"" + imageSize + "\" height=\"" + imageSize + "\"";

		return statusText

		// hatena http
		.replaceAll("<a[^>]*href=\"(https?://f[.]hatena[.]ne[.]jp/(([\\w-])[\\w-]+)/((\\d{8})\\d+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://img.f.hatena.ne.jp/images/fotolife/$3/$2/$5/$4.jpg\"><img src=\"http://img.f.hatena.ne.jp/images/fotolife/$3/$2/$5/$4_120.jpg\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">はてなフォトライフ</a></font>")

		// img.ly https
		// http://img.ly/api
		.replaceAll("<a[^>]*href=\"(https?://img[.]ly/([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://img.ly/show/thumb/$2\"><img src=\"http://img.ly/show/thumb/$2\"" + imgSizeTagpart
				+ " /></a>" + fontColorTagpart + "<a href=\"$1\">img.ly</a></font>")

		// imgur https
		// http://api.imgur.com/models/image
		.replaceAll("<a[^>]*href=\"(https?://imgur[.]com/([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"https://i.imgur.com/$2.jpg\"><img src=\"https://i.imgur.com/$2.jpg\"" + imgSizeTagpart
				+ " /></a>" + fontColorTagpart + "<a href=\"$1\">imgur</a></font>")

		// instagram http
		// http://instagram.com/developer/endpoints/media/#get_media
		.replaceAll("<a[^>]*href=\"(https?://instagr[.]?(?:am|am[.]com)/p/([\\w-]+))/?(?:media/\\?size=(?:t|l))?\"[^>]*>.*?</a[^>]*>", "<a href=\"$1/media/?size=l\"><img src=\"$1/media/?size=t\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">instagram</a></font>")

		// mobypicture http
		// http://developers.mobypicture.com/documentation/1-0/getthumburl/
		.replaceAll("<a[^>]*href=\"(https?://moby[.]to/(\\w+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://moby.to/$2:small\"><img src=\"http://moby.to/$2:small\"" + imgSizeTagpart + " /></a>"
				+ fontColorTagpart + "<a href=\"$1\">mobypicture</a></font>")

		// movapic http
		.replaceAll("<a[^>]*href=\"(https?://movapic[.]com/pic/(\\w+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://image.movapic.com/pic/t_$2.jpeg\"><img src=\"http://image.movapic.com/pic/s_$2.jpeg\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">携帯百景</a></font>")

		// nicovideo http
		// http://dic.nicovideo.jp/a/%E3%83%8B%E3%82%B3%E3%83%8B%E3%82%B3%E5%8B%95%E7%94%BBapi
		.replaceAll("<a[^>]*href=\"(https?://(?:www[.]nicovideo[.]jp/watch/|nico[.]ms/)(?:s|n)m([0-9]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://tn-skr.smilevideo.jp/smile?i=$2\"><img src=\"http://tn-skr.smilevideo.jp/smile?i=$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">ニコ動</a></font>")

		// ow.ly http
		.replaceAll("<a[^>]*href=\"(https?://ow[.]ly/i/(\\w+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://static.ow.ly/photos/thumb/$2.jpg\"><img src=\"http://static.ow.ly/photos/thumb/$2.jpg\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">ow.ly</a></font>")

		// photozou http
		// http://photozou.jp/basic/api_method_photo_info
		.replaceAll("<a[^>]*href=\"(https?://photozou[.]jp/photo/show/\\d+/([\\d]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://photozou.jp/p/img/$2\"><img src=\"http://photozou.jp/p/thumb/$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">フォト蔵</a></font>")

		// plixi http
		// http://support.lockerz.com/entries/350297-Image-From-URL
		.replaceAll("<a[^>]*href=\"(https?://tweetphoto[.]com/\\d+|https?://plixi[.]com/p/\\d+|https?://lockerz[.]com/s/\\d+)\"[^>]*>.*?</a[^>]*>", "<a href=\"http://api.plixi.com/api/tpapi.svc/imagefromurl?size=big&url=$1\"><img src=\"http://api.plixi.com/api/tpapi.svc/imagefromurl?size=thumbnail&url=$1\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">Lockerz</a></font>")

		// tuna.be http
		// http://tuna.be/api/
		.replaceAll("<a[^>]*href=\"(https?://tuna[.]be/t/([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://tuna.be/show/thumb/$2\"><img src=\"http://tuna.be/show/mini/$2\"" + imgSizeTagpart
				+ " /></a>" + fontColorTagpart + "<a href=\"$1\">tuna.be</a></font>")

		// twipple http
		// http://p.twipple.jp/wiki/API_Thumbnail/ja
		.replaceAll("<a[^>]*href=\"(https?://p[.]twipple[.]jp/(?:show/(?:large|thumb)/)?([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://p.twipple.jp/show/large/$2\"><img src=\"http://p.twipple.jp/show/thumb/$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">ついっぷる</a></font>").replaceAll("<a[^>]*href=\"(https?://p[.]twpl[.]jp/(?:show/(?:large|thumb)/)?([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://p.twipple.jp/show/large/$2\"><img src=\"http://p.twipple.jp/show/thumb/$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">ついっぷる</a></font>")

		// twitgoo http
		.replaceAll("<a[^>]*href=\"(https?://twitgoo[.]com/([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://twitgoo.com/$2/img\"><img src=\"http://twitgoo.com/$2/mini\"" + imgSizeTagpart
				+ " /></a>" + fontColorTagpart + "<a href=\"$1\">Twitgoo</a></font>")

		// twitpic https
		// http://dev.twitpic.com/docs/thumbnails/
		.replaceAll("<a[^>]*href=\"(https?://twitpic[.]com/(?:show/(?:mini|thumb)/)?([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"https://twitpic.com/show/thumb/$2\"><img src=\"https://twitpic.com/show/mini/$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">Twitpic</a></font>")

		// yfrog https
		// http://twitter.yfrog.com/page/api#a5
		.replaceAll("<a[^>]*href=\"(https?://(?:twitter.)?yfrog[.]com/([-_0-9a-zA-Z]+)(:[a-zA-Z]+)?)\"[^>]*>.*?</a[^>]*>", "<a href=\"https://yfrog.com/$2:small\"><img src=\"https://yfrog.com/$2:small\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">yfrog</a></font>")

		// youtube https
		// https://developers.google.com/youtube/v3/docs/thumbnails
		// http://no-delay.com/?p=797
		.replaceAll("<a[^>]*href=\"(https?://(?:www[.]youtube[.]com/watch(?:\\?|#!)v=|youtu[.]be/)([\\w_\\-]+)(?:[-_.!~*\\'()a-zA-Z0-9;\\/?:@&=+$,%#]*))\"[^>]*>.*?</a[^>]*>", "<a href=\"https://i.ytimg.com/vi/$2/hqdefault.jpg\"><img src=\"https://i.ytimg.com/vi/$2/default.jpg\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">YouTube</a></font>")

		// via.me http
		// http://via.me/developers/posts#GET-posts/{id}
		// http://mgng.aws.af.cm/misc/viame/
		.replaceAll("<a[^>]*href=\"(https?://via[.]me/([-_0-9a-zA-Z]+))\"[^>]*>.*?</a[^>]*>", "<a href=\"http://mgng.aws.af.cm/misc/viame/?r=thumb&id=$2\"><img src=\"http://mgng.aws.af.cm/misc/viame/?r=thumb&id=$2\""
				+ imgSizeTagpart + " /></a>" + fontColorTagpart + "<a href=\"$1\">via.me</a></font>");

		// etc
		// .replaceAll(
		// "<a[^>]*href=\"((?:https?|ftp)://[-_.!*\"'()a-zA-Z0-9;/?:\\\\@&=+$,%#]+[.](gif|jpeg?|png|tiff?))\"[^>]*>.*?</a[^>]*>",
		// fontColorTagpart+"<a href=\"$1\"><img src=\"$1\" />$1</a></font>");

	}

	public final String expand_uri(String shorturi) {
		if (shorturi.indexOf("http") != 0) {
			shorturi = context.getString(R.string.http) + shorturi;
		}
		String longuri = "";

		URLConnection tURLConnection;
		try {
			tURLConnection = new URL(shorturi).openConnection(Proxy.NO_PROXY);
		} catch (final Exception e) {
			tURLConnection = null;
			WriteLog.write(context, e);
		}
		if (tURLConnection != null) {
			if (!( tURLConnection instanceof HttpURLConnection )) {
				if (shorturi.equals("") == false) {
					return shorturi;
				}
			}
		}
		HttpURLConnection tHttpURLConnection = null;
		String tLocation = "";
		int tResponseCode = -1;

		try {
			tHttpURLConnection = (HttpURLConnection) tURLConnection;
		} catch (final Exception e) {
			tHttpURLConnection = null;
			WriteLog.write(context, e);
		}
		if (tHttpURLConnection != null) {
			try {
				tHttpURLConnection.setConnectTimeout(pref_timeout_connection_expanduri);
				tHttpURLConnection.setReadTimeout(pref_timeout_so_expanduri);
				tHttpURLConnection.setInstanceFollowRedirects(pref_enable_httpurlconnection_follow_redirects);
				tHttpURLConnection.setRequestMethod("HEAD");
			} catch (final ProtocolException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final Exception e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			}
		}
		if (tHttpURLConnection != null) {
			try {
				tHttpURLConnection.connect();
			} catch (final EOFException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final MalformedURLException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final ProtocolException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final UnsupportedEncodingException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final IOException e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			} catch (final Exception e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			}
		}

		if (tHttpURLConnection != null) {
			try {
				tResponseCode = tHttpURLConnection.getResponseCode();
			} catch (final EOFException e) {
				// WriteLog.write(context, e.toString(), 3);
			} catch (final MalformedURLException e) {
				WriteLog.write(context, e);
			} catch (final ProtocolException e) {
				WriteLog.write(context, e);
			} catch (final UnsupportedEncodingException e) {
				WriteLog.write(context, e);
			} catch (final SocketTimeoutException e) {
				WriteLog.write(context, e);
			} catch (final IOException e) {
				WriteLog.write(context, e);
			} catch (final Exception e) {
				WriteLog.write(context, e);
			}
		}

		if (tHttpURLConnection != null) {
			try {
				tLocation = tHttpURLConnection.getHeaderField("Location");
			} catch (final Exception e) {
				tHttpURLConnection = null;
				WriteLog.write(context, e);
			}
		}

		if (tLocation != null) {
			if (tLocation.startsWith("http")) {
				if (( tResponseCode == HttpURLConnection.HTTP_MOVED_PERM ) || ( tResponseCode == HttpURLConnection.HTTP_MOVED_TEMP )) {
					return expand_uri(encode(tLocation));
				}
				longuri = tLocation;
			}
		} else if (tHttpURLConnection != null) {
			longuri = tHttpURLConnection.getURL().toExternalForm();
		} else {
			return shorturi;
		}

		try {
			tHttpURLConnection.disconnect();
		} catch (final Exception e) {
		}

		String longuri_encoded = "";
		longuri_encoded = encode(longuri);
		if (longuri_encoded.equals("")) {
			return shorturi;
		}
		return longuri_encoded;
	}

	public final String get_file_from_web(final String urlString) {
		try {
			final URL url = new URL(urlString);
			final String fileName = getFilenameFromURL(url);
			WriteLog.write(context, "fileName: " + fileName);
			final URLConnection conn = url.openConnection();

			final HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			// httpConn.setInstanceFollowRedirects(false);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			final int response = httpConn.getResponseCode();
			WriteLog.write(context, "response: " + response);

			// Check Response
			if (response != HttpURLConnection.HTTP_OK) {
				WriteLog.write(context, "(response != HttpURLConnection.HTTP_OK)");
				if (( response == HttpURLConnection.HTTP_MOVED_PERM ) || ( response == HttpURLConnection.HTTP_MOVED_TEMP )) {
					WriteLog.write(context, "((response == HttpURLConnection.HTTP_MOVED_PERM) || (response == HttpURLConnection.HTTP_MOVED_TEMP))");
					final String tLocation = httpConn.getHeaderField("Location");
					WriteLog.write(context, "tLocation: " + tLocation);
					return get_file_from_web(( new URL(encode(tLocation)) ).toExternalForm());
				} else {
					return "";
				}
			}
			// int contentLength = httpConn.getContentLength();

			final InputStream in = httpConn.getInputStream();

			final FileOutputStream outStream = context.openFileOutput(fileName, 0);

			final DataInputStream dataInStream = new DataInputStream(in);
			final DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));
			final byte[] b = new byte[4096];
			int readByte = 0; // , totalByte = 0;
			while (-1 != ( readByte = dataInStream.read(b) )) {
				dataOutStream.write(b, 0, readByte);
				// totalByte += readByte;
			}
			dataInStream.close();
			dataOutStream.close();

			return fileName;
		} catch (final IOException e) {
			WriteLog.write(context, e);
		}
		return "";
	}

	private final String getFilenameFromURL(final URL url) {
		final String[] p = url.getFile().split("/");
		final String s = p[p.length - 1];
		WriteLog.write(context, "p[p.length - 1]" + p[p.length - 1]);
		WriteLog.write(context, "p[p.length - 2]" + p[p.length - 2]);
		if (s.indexOf("?") > -1) {
			final String s2 = s.substring(0, s.indexOf("?"));
			if (s2.equals("")) {
				WriteLog.write(context, "(s.equals(\"\"))");
				try {
					return getFilenameFromURL(new URL(url.toString().substring(0, url.toString().lastIndexOf("/"))));
				} catch (MalformedURLException e) {
					WriteLog.write(context, e);
				}
			}
			WriteLog.write(context, "s2: " + s2);
			return s2;
		}
		WriteLog.write(context, "s: " + s);
		return s;
	}

	// @formatter:on

	public final String uri_shorten(String longuri, boolean addhttp) {
		String encoded_longuri = "";
		try {
			encoded_longuri = URLEncoder.encode(longuri, ListAdapter.default_charset);
		} catch (final UnsupportedEncodingException e) {
			WriteLog.write(context, e);
		}
		if (encoded_longuri.equals("")) {
			return longuri;
		}
		String shorturi = "";

		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		final String pref_uri_shorten_site = pref_app.getString("pref_uri_shorten_site", "");

		if (pref_uri_shorten_site.equals("bit.ly")) {
			shorturi = uri_shorten_bitly(encoded_longuri);
		} else if (pref_uri_shorten_site.equals("Google")) {
			shorturi = uri_shorten_google(encoded_longuri);
		} else {
			shorturi = uri_shorten_tinyurl(encoded_longuri);
		}

		if (addhttp == false) {
			shorturi = shorturi.replace(context.getString(R.string.http), "").replace(context.getString(R.string.https), "");
		}
		if (shorturi.equals("")) {
			return longuri;
		}
		return shorturi;
	}

	private final String uri_shorten_bitly(final String encoded_longuri) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		String pref_bitly_apikey = pref_app.getString("pref_bitly_apikey", "");
		String pref_bitly_username = pref_app.getString("pref_bitly_username", "");

		if (pref_bitly_apikey.equals("") || pref_bitly_username.equals("")) {
			Toast.makeText(context, context.getString(R.string.bitly_key_orand_username_is_empty), Toast.LENGTH_SHORT).show();

			return uri_shorten_tinyurl(encoded_longuri);
		}

		String shorturi = "";
		final Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.path("http://api.bit.ly/v3/shorten");
		uriBuilder.appendQueryParameter("login", pref_bitly_username);
		uriBuilder.appendQueryParameter("apiKey", pref_bitly_apikey);
		uriBuilder.appendQueryParameter("longUrl", encoded_longuri);
		uriBuilder.appendQueryParameter("format", "json");
		String uri = Uri.decode(uriBuilder.build().toString());
		String json = "";
		try {
			json = HttpsClient.https2data(context, uri, pref_timeout_connection_shortenuri, pref_timeout_so_shortenuri, ListAdapter.default_charset);
			if (json.equals("") == false) {
				final JSONObject jsonEntity = new JSONObject(json);
				if (jsonEntity != null) {
					final JSONObject jsonResults = jsonEntity.optJSONObject("data");
					if (jsonResults != null) {
						shorturi = jsonResults.optString("url");
					}
				}
			}
		} catch (final JSONException e) {
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}

		return shorturi;
	}

	private final String uri_shorten_google(String encoded_longuri) {
		final String urlString = "https://www.googleapis.com/urlshortener/v1/url";
		try {
			final URL url = new URL(urlString);
			final URLConnection uc = url.openConnection();
			uc.setDoOutput(true);

			uc.setRequestProperty("Content-Type", "application/json");
			final OutputStream os = uc.getOutputStream();

			final String postStr = "{\"longUrl\": \"" + encoded_longuri + "\"}";

			final PrintStream ps = new PrintStream(os);
			ps.print(postStr);
			ps.close();

			final InputStream is = uc.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s;
			final Pattern pattern_uri_shorten_google = Pattern.compile("\"id\":\\s*?\"(.+)\"", Pattern.DOTALL);
			while (( s = reader.readLine() ) != null) {
				final Matcher matcher_uri_shorten_google = pattern_uri_shorten_google.matcher(s);
				if (matcher_uri_shorten_google.find()) {
					reader.close();
					return matcher_uri_shorten_google.group(1);
				}
			}
			reader.close();
		} catch (final IllegalAccessError e) {
			WriteLog.write(context, e);
		} catch (final MalformedURLException e) {
			WriteLog.write(context, e);
		} catch (final IOException e) {
			WriteLog.write(context, e);
		} catch (final PatternSyntaxException e) {
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}
		return "";
	}

	private final String uri_shorten_tinyurl(final String encoded_longuri) {
		return HttpsClient.https2data(context, "https://tinyurl.com/api-create.php?url=" + encoded_longuri, pref_timeout_connection_shortenuri, pref_timeout_so_shortenuri, ListAdapter.default_charset);
	}

	//	private final Drawable getHtmlDrawable(final String url) {
	//		try {
	//			final HttpGet method = new HttpGet(url);
	//			final DefaultHttpClient client = new DefaultHttpClient();
	//			method.setHeader("Connection", "Keep-Alive");
	//			final HttpResponse response = client.execute(method);
	//			final int status = response.getStatusLine().getStatusCode();
	//			if (status == HttpStatus.SC_OK) {
	//				final InputStream is = response.getEntity().getContent();
	//				final Drawable drawable = Drawable.createFromStream(is, "");
	//				is.close();
	//				return drawable;
	//			}
	//		} catch (final OutOfMemoryError e) {
	//			WriteLog.write(context, e);
	//		} catch (final Exception e) {
	//			WriteLog.write(context, e);
	//		}
	//		return null;
	//	}

}

package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.gr.java_conf.ya.shiobeforandroid3.util.FontUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.GeocodeUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.StringUtil;
import jp.gr.java_conf.ya.shiobeforandroid3.util.WriteLog;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

public final class UpdateTweetDrive extends AppCompatActivity implements LocationListener {
	private ListAdapter adapter;
	private boolean startup_flag = true;
	private Dialog alertDialog = null;
	private EditText editText2;
	private EditText editText4;
	private EditText editText5;
	private TableLayout tableLayout1;
	private final FontUtil fontUtil = new FontUtil();
	private GoogleMap map;
	private LocationManager mLocationManager = null;
	private SharedPreferences pref_app;
	private String crpKey = "";

	public static final int REQUEST_PERMISSION = 1;

	private String pref_tl_fontcolor_text_updatetweet = "#ffffff", pref_tl_fontcolor_text_updatetweet_over = "#ff0000";

	private static final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private final boolean checkLocationinfoException(final EditText editText4, final EditText editText5) {
		return jp.gr.java_conf.ya.shiobeforandroid3.util.CoordsUtil.checkLocationinfoException(UpdateTweetDrive.this, editText4.getText().toString(), editText5.getText().toString(), pref_tl_fontcolor_text_updatetweet, pref_tl_fontcolor_text_updatetweet_over, editText4, editText5);
	}

	public void checkLocationPermission() {
		// 既に許可している
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
			init_location();
		}
		// 拒否していた場合
		else{
			requestLocationPermission();
		}
	}

	//普通に戻るボタンを押してもアプリを終了させない
	@Override
	public final boolean dispatchKeyEvent(final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				//trueを返して戻るのを無効化する
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	private final void init_location() {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
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
							if (( provider.equals(LocationManager.GPS_PROVIDER) ) || ( provider.equals(LocationManager.NETWORK_PROVIDER) )) {
								if (mLocationManager.isProviderEnabled(provider)) {
									provider_flag = true;
								}
							}
							WriteLog.write(UpdateTweetDrive.this, "requestLocationUpdates() provider: " + provider);

							final int pref_locationinfo_mintime = ListAdapter.getPrefInt(UpdateTweetDrive.this, "pref_locationinfo_mintime", "300000");
							try {
								new Runnable() {
									@Override
									public final void run() {
										if (ContextCompat.checkSelfPermission(UpdateTweetDrive.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
											mLocationManager.requestLocationUpdates(provider, pref_locationinfo_mintime, 0, UpdateTweetDrive.this);
										} else {
											if(Build.VERSION.SDK_INT >= 23)
												requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
										}
									}
								};
							} catch (final Exception e) {
								WriteLog.write(UpdateTweetDrive.this, e);
							}
						}

						if (startup_flag == true) {
							startup_flag = false;

							if (provider_flag == false) {
								toast(getString(R.string.open_location_source_settings));
								try {
									startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
								} catch (final ActivityNotFoundException e) {
									WriteLog.write(UpdateTweetDrive.this, e);
								} catch (final Exception e) {
									WriteLog.write(UpdateTweetDrive.this, e);
								}
							}
						}
					}
				} catch (final IllegalArgumentException e) {
					WriteLog.write(UpdateTweetDrive.this, e);
				} catch (final RuntimeException e) {
					WriteLog.write(UpdateTweetDrive.this, e);
				}

			}
		}).start();
	}

	private final void moveTo(final Double lat, final Double lng, final float speed, final float bearing) {
		try {
			final CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)) // Sets the center of the map to Mountain View
			.zoom(21 - (float) ( speed / 10 )) // Sets the zoom
			.bearing(bearing) // Sets the orientation of the camera to east
			.tilt((float) ( speed / 2 )) // Sets the tilt of the camera to 30 degrees
			.build(); // Creates a CameraPosition from the builder
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		} catch (final Exception e) {
		}
	}

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		simpleauth();

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

		adapter = new ListAdapter(this, crpKey, null, null);

		setContentView(R.layout.tweet_drive);
		tableLayout1 = (TableLayout) this.findViewById(R.id.tableLayout1);
		editText2 = (EditText) this.findViewById(R.id.editText2);
		editText4 = (EditText) this.findViewById(R.id.editText4);
		editText5 = (EditText) this.findViewById(R.id.editText5);
		editText2.setFocusable(true);
		editText2.setFocusableInTouchMode(true);
		editText2.requestFocusFromTouch();

		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final String pref_tl_fontfilename = pref_app.getString("pref_tl_fontfilename", "");
		if (pref_tl_fontfilename.equals("") == false) {
			try {
				WriteLog.write(this, "pref_tl_fontfilename: " + pref_tl_fontfilename);
				fontUtil.loadFont(pref_tl_fontfilename, this);

				fontUtil.setFont(editText2, this);
				fontUtil.setFont(editText4, this);
				fontUtil.setFont(editText5, this);
			} catch (final Exception e) {
				WriteLog.write(this, e);
			}
		}

		final float pref_tl_fontsize_updatetweet = ListAdapter.getPrefFloat(this, "pref_tl_fontsize_updatetweet", "14");
		editText2.setTextSize(pref_tl_fontsize_updatetweet);
		editText4.setTextSize(pref_tl_fontsize_updatetweet);
		editText5.setTextSize(pref_tl_fontsize_updatetweet);

		final String pref_tl_bgcolor_updatetweet = pref_app.getString("pref_tl_bgcolor_updatetweet", "#000000");
		pref_tl_fontcolor_text_updatetweet = pref_app.getString("pref_tl_fontcolor_text_updatetweet", "#ffffff");
		pref_tl_fontcolor_text_updatetweet_over = pref_app.getString("pref_tl_fontcolor_text_updatetweet_over", "#ff0000");

		if (pref_tl_bgcolor_updatetweet.equals("") == false) {
			try {
				tableLayout1.setBackgroundColor(Color.parseColor(pref_tl_bgcolor_updatetweet));
			} catch (final IllegalArgumentException e) {
			}
		}
		setTextColorOnTextChanged();
		if (pref_tl_fontcolor_text_updatetweet.equals("") == false) {
			try {
				editText4.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
				editText5.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
			} catch (final IllegalArgumentException e) {
			}
		}

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
		editText2.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public final void onFocusChange(final View arg0, final boolean arg1) {
				setTextColorOnTextChanged();
			}
		});

		editText2.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public final boolean onLongClick(final View v) {
				tweet();
				return true;
			}
		});

		map = ( (MapFragment) getFragmentManager().findFragmentById(R.id.map) ).getMap();

		final String pref_map_site = pref_app.getString("pref_map_site", "0");
		if (pref_map_site.equals("0")) {
			try {
				MapsInitializer.initialize(this);
			} catch (final Exception e) {
				toast("You must update Google Google Play Service.");
			}
		} else {
			try {
				map.setMapType(GoogleMap.MAP_TYPE_NONE);

				final TileProvider tileProvider = new UrlTileProvider(256, 256) {
					@Override
					public final synchronized URL getTileUrl(final int x, final int y, final int zoom) {
						// The moon tile coordinate system is reversed.  This is not normal.
						// int reversedY = (1 << zoom) - y - 1;
						// String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY);
						final String s = String.format(Locale.US, ( ( pref_map_site.equals("0") ) ? ListAdapter.OSM_MAP_URL_FORMAT : ListAdapter.GSI_MAP_URL_FORMAT ), zoom, x, y);
						URL url = null;
						try {
							url = new URL(s);
						} catch (final MalformedURLException e) {
							throw new AssertionError(e);
						}
						return url;
					}
				};

				map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			} catch (final Exception e) {
				try {
					MapsInitializer.initialize(this);
				} catch (final Exception e1) {
					toast("You must update Google Maps.");
				}
			}
		}

		moveTo(35.66279, 139.759848, 0.0f, 0.0f);
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, R.string.get_maplocation, 0, R.string.get_maplocation).setIcon(android.R.drawable.ic_menu_mylocation).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, R.string.deljustbefore, 0, R.string.deljustbefore).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, R.string.settings, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);

		menu.add(0, R.string.copyright, 0, R.string.copyright).setIcon(android.R.drawable.ic_menu_info_details);

		menu.add(0, R.string.back, 0, R.string.back).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public final boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			tweet();
			return true;
		}

		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public final void onLocationChanged(final Location location) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean pref_enable_update_locationinfo = pref_app.getBoolean("pref_enable_update_locationinfo", true);

		new Thread(new Runnable() {
			@Override
			public final void run() {
				WriteLog.write(UpdateTweetDrive.this, "Lat: " + Double.toString(location.getLatitude()) + " Lng: " + Double.toString(location.getLongitude()));

				if (( Double.toString(location.getLatitude()).equals("") == false ) && ( Double.toString(location.getLongitude()).equals("") == false )) {
					final double latitude = location.getLatitude();
					final double longitude = location.getLongitude();
					final double altitude = location.getAltitude();
					final float bearing = location.getBearing();
					final float speed = location.getSpeed();
					if (pref_enable_update_locationinfo) {
						runOnUiThread(new Runnable() {
							@Override
							public final void run() {
								moveTo(latitude, longitude, speed, bearing);
								editText4.setText(Double.toString(latitude));
								editText5.setText(Double.toString(longitude));

								if (checkLocationinfoException(editText4, editText5) == false) {
									final ArrayList<String> ITEM = GeocodeUtil.reverseGeoCoding(UpdateTweetDrive.this, latitude, longitude);
									if (ITEM != null) {
										if (!isFinishing()) {
											if (alertDialog != null) {
												if (alertDialog.isShowing()) {
													try {
														alertDialog.cancel();
													} catch (final Exception e) {
													}
												}
											}
										}
										alertDialog =
												new AlertDialog.Builder(UpdateTweetDrive.this).setTitle(R.string.reversegeocoding).setItems(ITEM.toArray(new String[ITEM.size()]), new DialogInterface.OnClickListener() {
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
												}).create();
										alertDialog.show();
									}
								}
							}
						});
					}
				}
			}
		}).start();
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {

		boolean ret = true;
		if (item.getItemId() == R.string.get_maplocation) {

			final CameraPosition cameraPos = map.getCameraPosition();
			final LatLng latLng = cameraPos.target;
			editText4.setText(Double.toString(latLng.latitude));
			editText5.setText(Double.toString(latLng.longitude));

			if (checkLocationinfoException(editText4, editText5) == false) {
				final ArrayList<String> ITEM = GeocodeUtil.reverseGeoCoding(UpdateTweetDrive.this, latLng.latitude, latLng.longitude);
				if (ITEM != null) {
					if (!isFinishing()) {
						if (alertDialog != null) {
							if (alertDialog.isShowing()) {
								try {
									alertDialog.cancel();
								} catch (final Exception e) {
								}
							}
						}
					}
					alertDialog =
							new AlertDialog.Builder(UpdateTweetDrive.this).setTitle(R.string.reversegeocoding).setItems(ITEM.toArray(new String[ITEM.size()]), new DialogInterface.OnClickListener() {
								@Override
								public final void onClick(final DialogInterface dialog, final int which) {
									String str = ITEM.get(which);
									if (str.equals("") == false) {
										editText2.setText(str);
									}
								}
							}).create();
					alertDialog.show();
				}
			}

		} else if (item.getItemId() == R.string.deljustbefore) {
			adapter.deljustbefore(-1);

		} else if (item.getItemId() == R.string.settings) {
			try {
				final Intent intent2 = new Intent();
				intent2.setClassName("jp.gr.java_conf.ya.shiobeforandroid3", "jp.gr.java_conf.ya.shiobeforandroid3.Preference");
				startActivity(intent2);
			} catch (final ActivityNotFoundException e) {
				WriteLog.write(UpdateTweetDrive.this, e);
			} catch (final Exception e) {
				WriteLog.write(UpdateTweetDrive.this, e);
			}

		} else if (item.getItemId() == R.string.copyright) {
			new Thread(new Runnable() {
				@Override
				public final void run() {
					try {
						final PackageInfo packageInfo = getPackageManager().getPackageInfo("jp.gr.java_conf.ya.shiobeforandroid3", PackageManager.GET_META_DATA);
						toast(getString(R.string.app_name_short) + ": " + getString(R.string.version) + packageInfo.versionName + " (" + packageInfo.versionCode + ")");
					} catch (final NameNotFoundException e) {
					}

					toast(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(UpdateTweetDrive.this));

					try {
						final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(ListAdapter.app_uri_about));
						startActivity(intent);
					} catch (final Exception e) {
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
		if (mLocationManager != null) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
				try {
					mLocationManager.removeUpdates(this);
				} catch (final Exception e) {
					WriteLog.write(this, e);
				}
			}
		}

		super.onPause();
	}

	@Override
	public final void onProviderDisabled(final String provider) {
		toast("LocationProvider: Disabled");
	}

	@Override
	public final void onProviderEnabled(final String provider) {
		toast("LocationProvider: Enabled");
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
		if(Build.VERSION.SDK_INT >= 23){
			checkLocationPermission();
		}else{
			init_location();
		}

		super.onResume();
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
		toast("LocationProvider: " + statusString);
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

	private final boolean setTextColorOnTextChanged() {
		if (tweetstrlengthUi("", editText2.getText().toString(), "") > 140) {
			if (pref_tl_fontcolor_text_updatetweet_over.equals("") == false) {
				try {
					editText2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet_over));
				} catch (final IllegalArgumentException e) {
				}
			}

			return false;
		} else {
			if (pref_tl_fontcolor_text_updatetweet.equals("") == false) {
				try {
					editText2.setTextColor(Color.parseColor(pref_tl_fontcolor_text_updatetweet));
				} catch (final IllegalArgumentException e) {
				}
			}

			return true;
		}
	}

	private final void simpleauth() {
		// Password処理
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);
		final String pref_appPassword = pref_app.getString("pref_appPassword", "");
		if (pref_appPassword.equals("") == false) {
			WriteLog.write(this, "(pref_appPassword.equals(\"\") == false)");
			final EditText editView = new EditText(UpdateTweetDrive.this);
			new AlertDialog.Builder(UpdateTweetDrive.this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.enter_password).setView(editView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int whichButton) {
					if (editView.getText().toString().equals(pref_appPassword) == false) {
						WriteLog.write(UpdateTweetDrive.this, getString(R.string.wrong_password) + ": " + editView.getText().toString());
						finish();
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public final void onClick(final DialogInterface dialog, final int whichButton) {
					WriteLog.write(UpdateTweetDrive.this, getString(R.string.cancelled));
					finish();
				}
			}).show();
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
						Toast.makeText(UpdateTweetDrive.this, text, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	private final void tweet() {
		pref_app = PreferenceManager.getDefaultSharedPreferences(this);

		final Intent intent = new Intent(UpdateTweetDrive.this, AutoTweet.class);
		intent.setData(Uri.parse("http://shiobe/?" + String.valueOf(System.currentTimeMillis())));
		intent.putExtra("mode", "t");
		intent.putExtra("str1", adapter.getTweetHeader(pref_app, ""));
		intent.putExtra("str2", editText2.getText().toString());
		intent.putExtra("str3", adapter.getTweetfooter(pref_app, ""));
		intent.putExtra("str4", editText4.getText().toString());
		intent.putExtra("str5", editText5.getText().toString());
		intent.putExtra("index", "-1");
		intent.putExtra("schedule_index", "");
		intent.putExtra("inReplyToStatusId", "");
		intent.putExtra("scheduledDateGetTimeInMillisString", "");
		intent.putExtra("tweetImagePathString", "");
		sendBroadcast(intent);
	}

	private final int tweetstrlengthUi(final String str1, final String str2, final String str3) {
		final String str = StringUtil.getTweetString(str1, str2, str3);
		// final int strlength = tweetstrlength2(str).length();
		final int strlength = ListAdapter.getStringLength(str);

		if (strlength > 0) {
			runOnUiThread(new Runnable() {
				@Override
				public final void run() {
					editText2.setHint("");
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public final void run() {
					editText2.setHint(R.string.message);
				}
			});
		}
		return strlength;
	}
}

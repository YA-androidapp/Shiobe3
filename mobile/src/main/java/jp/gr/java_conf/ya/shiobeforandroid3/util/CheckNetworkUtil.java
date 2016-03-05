package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import jp.gr.java_conf.ya.shiobeforandroid3.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

public final class CheckNetworkUtil {
	private ConnectivityManager connectivityManager;
	private Context context;
	private static int isConnectedCount = 0;
	private SharedPreferences pref_app;

	public CheckNetworkUtil(final Context context) {
		this.context = context;
	}

	private static final boolean currentThreadIsUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	private static final void toast(final boolean isAutoTweet, final Context context, final String text) {
		if (isAutoTweet == false) {
			if (!( (Activity) context ).isFinishing()) {
				if (currentThreadIsUiThread()) {
					Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				} else {
					( (Activity) context ).runOnUiThread(new Runnable() {
						@Override
						public final void run() {
							Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}
	}

	public final int autoConnect(final boolean isAutoTweet) {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		final String pref_network_wifi_ssid_exception = pref_app.getString("pref_network_wifi_ssid_exception", "");

		boolean pref_enable_network_auto_reconnect_wifi = pref_app.getBoolean("pref_enable_network_auto_reconnect_wifi", false);
		boolean pref_enable_network_auto_reconnect_mobile = pref_app.getBoolean("pref_enable_network_auto_reconnect_mobile", false);
		if (isAutoTweet) {
			pref_enable_network_auto_reconnect_wifi = pref_app.getBoolean("pref_enable_network_auto_reconnect_wifi_autotweet", false);
			pref_enable_network_auto_reconnect_mobile = pref_app.getBoolean("pref_enable_network_auto_reconnect_mobile_autotweet", false);
		}
		final boolean pref_enable_networkcheck_wifi_mobile_as_wifi = pref_app.getBoolean("pref_enable_networkcheck_wifi_mobile_as_wifi", false);
		final int pref_networkcheck_wifi_mobile_as_wifi_linkspeed_threshold = ListAdapter.getPrefInt(context, "pref_networkcheck_wifi_mobile_as_wifi_linkspeed_threshold", "27");

		final String[] pref_network_wifi_ssid_exceptions = pref_network_wifi_ssid_exception.split(",");

		connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {

			final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			String ssidStr = "";
			int wifiLinkSpeed = -1;
			if (wifiManager != null) {

				final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo != null) {
					// int ipAddr = wifiInfo.getIpAddress();
					// String ipAddress = ( ( ipAddr >> 0 ) & 0xFF ) + "." + ( ( ipAddr >> 8 ) & 0xFF ) + "." + ( ( ipAddr >> 16 ) & 0xFF ) + "." + ( ( ipAddr >> 24 ) & 0xFF );
					ssidStr = ( networkInfo.getTypeName().equals("WIFI") ) ? " SSID:" + wifiInfo.getSSID() : "";
					wifiLinkSpeed = wifiInfo.getLinkSpeed();

					if (pref_network_wifi_ssid_exception.equals("") == false) {
						if (networkInfo.getTypeName().equals("WIFI")) {
							for (String pref_network_wifi_ssid_exceptions_part : pref_network_wifi_ssid_exceptions) {
								if (( wifiInfo.getSSID() ).equals(pref_network_wifi_ssid_exceptions_part)) {
									wifiManager.setWifiEnabled(false);
									toast(isAutoTweet, context, context.getString(R.string.network_wifi_ssid_exception) + ssidStr);
									return -1;
								}
							}
						}
					}
				}
			}
			if (networkInfo.isConnected()) {
				toast(isAutoTweet, context, networkInfo.getTypeName() + ssidStr);

				if (pref_enable_networkcheck_wifi_mobile_as_wifi && ( wifiLinkSpeed < pref_networkcheck_wifi_mobile_as_wifi_linkspeed_threshold )) {
					return ConnectivityManager.TYPE_MOBILE;
				} else {
					return networkInfo.getType();
				}
			} else if (networkInfo.isConnectedOrConnecting()) {
				toast(isAutoTweet, context, networkInfo.getTypeName() + ssidStr);
				return -1;
			}
		}

		final boolean final_pref_enable_network_auto_reconnect_wifi = pref_enable_network_auto_reconnect_wifi;
		final boolean final_pref_enable_network_auto_reconnect_mobile = pref_enable_network_auto_reconnect_mobile;
		new Thread(new Runnable() {
			@Override
			public final void run() {
				final String result = autoReconnect(final_pref_enable_network_auto_reconnect_wifi, final_pref_enable_network_auto_reconnect_mobile);
				if (result.equals("") == false) {
					toast(isAutoTweet, context, result);
				}
			}
		}).start();

		return -1;
	}

	public final void autoDisconnect_updatetweet() {
		pref_app = PreferenceManager.getDefaultSharedPreferences(context);
		final boolean pref_enable_network_auto_disconnect_wifi = pref_app.getBoolean("pref_enable_network_auto_disconnect_wifi", false);
		final boolean pref_enable_network_auto_disconnect_mobile = pref_app.getBoolean("pref_enable_network_auto_disconnect_mobile", false);

		if (pref_enable_network_auto_disconnect_wifi) {
			setWifiEnabled(false);
		}
		if (pref_enable_network_auto_disconnect_mobile) {
			setMobileDataEnabled(false);
		}
	}

	/*
	 * @param keyManagement 0:None;1:WEP;2:WPA
	 * @param ssid
	 */
	//	private final int setWifiEnabled(final int keyManagement, final String ssid) {
	//		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	//		WifiConfiguration wifiConfiguration = new WifiConfiguration();
	//		wifiConfiguration.SSID = "\"" + ssid + "\"";
	//
	//		if (keyManagement == 2) {
	//			;
	//		} else if (keyManagement == 1) {
	//			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	//		} else {
	//			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	//		}
	//
	//		wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
	//		wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
	//
	//		if (keyManagement == 2) {
	//			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	//		} else if (keyManagement == 1) {
	//			wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
	//			wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
	//		} else {
	//			wifiConfiguration.allowedAuthAlgorithms.clear();
	//		}
	//
	//		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	//		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	//		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
	//		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	//
	//		if (keyManagement == 2) {
	//			wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	//			wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	//			wifiConfiguration.preSharedKey = "\"password\"";
	//		} else if (keyManagement == 1) {
	//			wifiConfiguration.wepKeys[0] = "\"password\"";
	//			wifiConfiguration.wepTxKeyIndex = 0;
	//		} else {
	//			wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	//			wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	//		}
	//
	//		int networkId = wifiManager.addNetwork(wifiConfiguration);
	//		wifiManager.saveConfiguration();
	//		wifiManager.updateNetwork(wifiConfiguration);
	//
	//		wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	//		try {
	//			wifiManager.startScan();
	//
	//			Pattern pattern = Pattern.compile("\"([^\"\\]*(\\.[^\"\\]*)*)\"", Pattern.MULTILINE);
	//			for (ScanResult result : wifiManager.getScanResults()) {
	//				Matcher matcher = pattern.matcher(result.SSID);
	//				String resultSSID = "";
	//				if (matcher.find()) {
	//					resultSSID = matcher.group(1);
	//				} else {
	//					resultSSID = result.SSID;
	//				}
	//				if (resultSSID.equals(ssid)) {
	//					if (networkId > 0) {
	//						for (WifiConfiguration c0 : wifiManager.getConfiguredNetworks()) {
	//							wifiManager.enableNetwork(c0.networkId, false);
	//						}
	//						wifiManager.enableNetwork(networkId, true);
	//					}
	//					break;
	//				}
	//			}
	//		} catch (final Exception e) {
	//		}
	//
	//		return networkId;
	//	}

	public final String autoReconnect(final boolean enable_auto_reconnect_wifi, final boolean enable_auto_reconnect_mobile) {
		boolean flagMobile = false;
		boolean flagWifi = false;
		connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		final NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
		if (networkInfos.length < 1) {
			toast(false, context, context.getString(R.string.cannot_access_internet));
			return context.getString(R.string.cannot_access_internet);
		}

		for (final NetworkInfo networkInfo : networkInfos) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				flagWifi = true;
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				flagMobile = true;
			}
		}
		if (flagWifi && enable_auto_reconnect_wifi) {
			final String result = autoReconnectWifi();
			if (result.equals("") == false) {
				return result;
			}
		}
		if (flagMobile && enable_auto_reconnect_mobile) {
			final String result = autoReconnectMobile();
			if (result.equals("") == false) {
				return result;
			}
		}

		return "";
	}

	private final String autoReconnectMobile() {
		toast(false, context, context.getString(R.string.doing_enable_mobile));

		if (isMobileDataEnabled() == false) {
			setMobileDataEnabled(true);
			for (int i = 0; i < 20; i++) {
				if (isMobileDataEnabled()) {
					return "MOBILE";
				} else {
					toast(false, context, context.getString(R.string.doing_enable_mobile) + ": " + i);
					waitThreeSec();
				}
			}
		}

		return "";
	}

	private final String autoReconnectWifi() {
		toast(false, context, context.getString(R.string.doing_enable_wifi));
		if (isWifiEnabled() == false) {
			setWifiEnabled(true);
			for (int i = 0; i < 20; i++) {
				if (isWifiEnabled()) {
					final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
					final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					final String pref_network_wifi_ssid_exception = pref_app.getString("pref_network_wifi_ssid_exception", "");
					if (( "," + pref_network_wifi_ssid_exception + "," ).contains("," + wifiInfo.getSSID() + ",")) {
						return "";
					}

					return "WIFI";
				} else {
					toast(false, context, context.getString(R.string.doing_enable_wifi) + ": " + i);
					waitThreeSec();
				}
			}
		}

		return "";
	}

	private final Object getConnectivityManagerStub() throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		final Class<?> connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
		final Field serviceField = connectivityManagerClass.getDeclaredField("mService");
		serviceField.setAccessible(true);
		return serviceField.get(connectivityManager);
	}

	private final Class<?> getConnectivityManagerStubClass(final Object connectivityManagerStub) throws ClassNotFoundException {
		return Class.forName(connectivityManagerStub.getClass().getName());
	}

	public final boolean isConnected() {
		if (( connectivityManager == null ) || ( isConnectedCount > 100 )) {
			connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
			isConnectedCount = 0;
		} else {
			isConnectedCount++;
		}
		try {
			final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	private final boolean isMobileDataEnabled() {
		toast(false, context, context.getString(R.string.alert_is_mobile_data_enabled));
		try {
			final Object stub = getConnectivityManagerStub();
			final Class<?> stubClass = getConnectivityManagerStubClass(stub);

			final Method method = stubClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true);
			final Object result = method.invoke(stub);
			return Boolean.TRUE.equals(result);
		} catch (final Exception e) {
		}
		return false;
	}

	public final boolean isWifiEnabled() {
		final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		try {
			if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	private final void setMobileDataEnabled(final boolean enabled) {
		try {
			final Object stub = getConnectivityManagerStub();
			final Class<?> stubClass = getConnectivityManagerStubClass(stub);

			final Method method = stubClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			method.setAccessible(true);
			method.invoke(stub, enabled);
		} catch (final Exception e) {
		}
	}

	private final void setWifiEnabled(final boolean enabled) {
		final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);
	}

	private final void waitThreeSec() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
	}

}

package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.ArrayList;
import java.util.Collections;

import jp.gr.java_conf.ya.shiobeforandroid3.ListAdapter;
import jp.gr.java_conf.ya.shiobeforandroid3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class GeocodeUtil {
	public static ArrayList<String> reverseGeoCoding(Context context, double latitude, double longitude) {

		if (( Math.abs(latitude) >= 0 ) && ( Math.abs(longitude) >= 0 ) && ( Math.abs(latitude) <= 90 ) && ( Math.abs(longitude) <= 180 )) {

			final SharedPreferences pref_app = PreferenceManager.getDefaultSharedPreferences(context);
			final int pref_reversegeocoding_site = ListAdapter.getPrefInt(context, "pref_reversegeocoding_site", "0");
			final String pref_reversegeocoding_yahoo_apikey = pref_app.getString("pref_reversegeocoding_yahoo_apikey", context.getString(R.string.default_yahooKey));

			final ArrayList<String> result = new ArrayList<String>(18);

			if (pref_reversegeocoding_site == 0) {
				final String revGeoCodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + Double.toString(latitude) + "," + Double.toString(longitude) + "&sensor=true&language=ja";
				WriteLog.write(context, "url: " + revGeoCodingUrl);

				final String result_json = HttpsClient.https2data(context, revGeoCodingUrl, ListAdapter.default_timeout_connection, ListAdapter.default_timeout_so, ListAdapter.default_charset);

				String country = "";
				String administrative_area_level_1 = "";
				String locality = "";
				String sublocality_level_1 = "";
				String sublocality_level_2 = "";
				String sublocality_level_3 = "";
				String sublocality_level_4 = "";
				String neighborhood = "";
				String natural_feature = "";
				String park = "";
				String point_of_interest = "";
				String transit_station = "";
				String airport = "";
				String train_station = "";
				String route = "";
				String intersection = "";

				try {
					// JSONオブジェクトの生成(root)
					final JSONObject rootObject = new JSONObject(result_json);

					// "results"階層にある配列を取得
					final JSONArray resultsArray = rootObject.getJSONArray("results");

					// 配列を用意
					final int countResults = resultsArray.length();
					final JSONObject[] componentObject = new JSONObject[countResults];
					for (int i = 0; i < countResults; i++) {
						componentObject[i] = resultsArray.getJSONObject(i);
					}

					// さらに下階層から「country」「administrative_area_level_1」「locality」と
					// 同階層の「formatted_address」を探して地域名を取得
					for (int i = 0; i < componentObject.length; i++) {
						final JSONArray typesArray = componentObject[i].getJSONArray("types");
						for (int j = 0; j < typesArray.length(); j++) {
							final String elem = typesArray.getString(j);
							// 国
							if (elem.equals("country")) {
								country = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "country: " + country);
							}
							// 都道府県
							if (elem.equals("administrative_area_level_1")) {
								administrative_area_level_1 = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "administrative_area_level_1: " + administrative_area_level_1);
							}
							// 区市町村
							if (elem.equals("locality")) {
								locality = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "locality: " + locality);
							}
							// 番地
							if (elem.equals("sublocality_level_1")) {
								sublocality_level_1 = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "sublocality_level_1: " + sublocality_level_1);
							}
							if (elem.equals("sublocality_level_2")) {
								sublocality_level_2 = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "sublocality_level_2: " + sublocality_level_2);
							}
							if (elem.equals("sublocality_level_3")) {
								sublocality_level_3 = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "sublocality_level_3: " + sublocality_level_3);
							}
							if (elem.equals("sublocality_level_4")) {
								sublocality_level_4 = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "sublocality_level_4: " + sublocality_level_4);
							}

							// 地域的な区域(例：国立公園)
							if (elem.equals("neighborhood")) {
								neighborhood = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "neighborhood: " + neighborhood);
							}
							// 有名な地勢(例：富士山)
							if (elem.equals("natural_feature")) {
								natural_feature = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "natural_feature: " + natural_feature);
							}
							// 名前のある公園
							if (elem.equals("park")) {
								park = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "park: " + park);
							}
							// 地域の著名な実在物
							if (elem.equals("point_of_interest")) {
								point_of_interest = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "point_of_interest: " + point_of_interest);
							}
							// 
							if (elem.equals("transit_station")) {
								transit_station = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "transit_station: " + transit_station);
							}
							// 
							if (elem.equals("airport")) {
								airport = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "airport: " + airport);
							}
							// 
							if (elem.equals("train_station")) {
								train_station = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "train_station: " + train_station);
							}
							// 
							if (elem.equals("route")) {
								route = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "route: " + route);
							}
							// 
							if (elem.equals("intersection")) {
								intersection = componentObject[i].getString("formatted_address");
								WriteLog.write(context, "intersection: " + intersection);
							}

						}
					}
				} catch (final JSONException e) {
				}

				if (neighborhood.length() > 0) {
					result.add(neighborhood);
				}
				if (natural_feature.length() > 0) {
					result.add(natural_feature);
				}
				if (park.length() > 0) {
					result.add(park);
				}
				if (point_of_interest.length() > 0) {
					result.add(point_of_interest);
				}
				if (transit_station.length() > 0) {
					result.add(transit_station);
				}
				if (airport.length() > 0) {
					result.add(airport);
				}
				if (train_station.length() > 0) {
					result.add(train_station);
				}
				if (route.length() > 0) {
					result.add(route);
				}
				if (intersection.length() > 0) {
					result.add(intersection);
				}

				if (sublocality_level_4.length() > 0) {
					result.add(sublocality_level_4);
				} else if (sublocality_level_3.length() > 0) {
					result.add(sublocality_level_3);
				} else if (sublocality_level_2.length() > 0) {
					result.add(sublocality_level_2);
				} else if (sublocality_level_1.length() > 0) {
					result.add(sublocality_level_1);
				} else if (locality.length() > 0) {
					result.add(locality);
				} else if (administrative_area_level_1.length() > 0) {
					result.add(administrative_area_level_1);
				} else if (country.length() > 0) {
					result.add(country);
				}

				( new CollectionsUtil() ).removeDuplicate(result);
				Collections.sort(result);

				return result;
			} else if (( pref_reversegeocoding_site == 1 ) && ( pref_reversegeocoding_yahoo_apikey.equals("") == false )) {
				final String revGeoCodingUrl =
						"http://placeinfo.olp.yahooapis.jp/V1/get?output=json&lat=" + Double.toString(latitude) + "&lon=" + Double.toString(longitude) + "&appid=" + pref_reversegeocoding_yahoo_apikey;
				WriteLog.write(context, "url: " + revGeoCodingUrl);

				final String result_json = HttpsClient.https2data(context, revGeoCodingUrl, ListAdapter.default_timeout_connection, ListAdapter.default_timeout_so, ListAdapter.default_charset);
				WriteLog.write(context, "result_json: " + result_json);

				String combined;

				try {
					// JSONオブジェクトの生成(root)
					final JSONObject rootObject = new JSONObject(result_json);
					final JSONObject resultsetObject = rootObject.getJSONObject("ResultSet");
					WriteLog.write(context, "resultsetObject: " + resultsetObject.toString());

					final JSONArray resultArray = resultsetObject.getJSONArray("Result");
					final JSONObject resultObject = resultArray.getJSONObject(0);
					WriteLog.write(context, "resultObject: " + resultObject.toString());

					combined = resultObject.getString("Combined");
					WriteLog.write(context, "combined: " + combined);

				} catch (final JSONException e) {
					WriteLog.write(context, e);
					combined = "";
				}

				if (combined.length() > 0) {
					result.add(combined);
				}

				return result;

			} else { // if (pref_reversegeocoding_site == 2) {

				final String revGeoCodingUrl = "http://www.finds.jp/ws/rgeocode.php?json&lat=" + Double.toString(latitude) + "&lon=" + Double.toString(longitude);
				WriteLog.write(context, "url: " + revGeoCodingUrl);

				final String result_json = HttpsClient.https2data(context, revGeoCodingUrl, ListAdapter.default_timeout_connection, ListAdapter.default_timeout_so, ListAdapter.default_charset);

				String prefecture = "";
				String municipality = "";
				String local = "";

				try {
					// JSONオブジェクトの生成(root)
					final JSONObject rootObject = new JSONObject(result_json);
					final JSONObject resultObject = rootObject.getJSONObject("result");
					WriteLog.write(context, "resultObject: " + resultObject.toString());

					final JSONObject prefectureObject = resultObject.getJSONObject("prefecture");
					WriteLog.write(context, "prefectureObject: " + prefectureObject.toString());

					prefecture = prefectureObject.getString("pname");
					WriteLog.write(context, "prefecture: " + prefecture);

					final JSONObject municipalityObject = resultObject.getJSONObject("municipality");
					WriteLog.write(context, "municipalityObject: " + municipalityObject.toString());

					municipality = municipalityObject.getString("mname");
					WriteLog.write(context, "municipality: " + municipality);

					final JSONArray localArray = resultObject.getJSONArray("local");
					final JSONObject localObject2 = localArray.getJSONObject(0);
					WriteLog.write(context, "localObject2: " + localObject2.toString());

					local = localObject2.getString("section");
					WriteLog.write(context, "local: " + local);

				} catch (final JSONException e) {
					WriteLog.write(context, e);
				}

				String resultStr = "";
				if (prefecture.length() > 0) {
					resultStr += prefecture;
				}
				if (municipality.length() > 0) {
					resultStr += municipality;
				}
				if (local.length() > 0) {
					resultStr += local;
				}
				result.add(resultStr);

				return result;
			}
		}

		return new ArrayList<String>(0);
	}
}
package com.clickeat.restaurant.click_eatrestaurant.utilities;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

public class Const {

	public static String SD_CARD_PATH = Environment
			.getExternalStorageDirectory() + "/" + "LocumNexus";
	public static String MyPREFERENCES = "ClickeatRestaurantPref";



	public static JsonObject getRestaurantUserDetails = new JsonObject();

	public static String PREF_LOGINKEY= "PREF_LOGINKEY";
	public static String INTRO_DONE= "INTRO_DONE";
	public static String PREF_USER_ID = "PREF_USER_ID";
	public static String PREF_USER_ROLE = "PREF_USER_ROLE";
	public static String PREF_USER_MOBILE_NO = "PREF_USER_MOBILE_NO";
	public static String PREF_USER_FULL_NAME = "PREF_USER_FULL_NAME";
	public static String PREF_USER_PROFILE_PIC_URL = "PREF_USER_PROFILE_PIC_URL";
	public static String PREF_PASSWORD = "PREF_PASSWORD";
	public static String PREF_USER_EMAIL = "PREF_USER_EMAIL";
	public static String PREF_USER_TOKEN = "PREF_USER_TOKEN";
	public static String DEVICE_TYPE = "android";





	/*
	 * Cookie and SESSION
	 */
	// connection timeout is set to 30 seconds
	public static int TIMEOUT_CONNECTION = 30000;
	// SOCKET TIMEOUT IS SET TO 30 SECONDS
	public static int TIMEOUT_SOCKET = 30000;

	public static String PREF_SESSION_COOKIE = "sessionid";
	public static String SET_COOKIE_KEY = "Set-Cookie";
	public static String COOKIE_KEY = "Cookie";
	public static String SESSION_COOKIE = "sessionid";

	public static int API_SUCCESS = 0;
	public static int API_FAIL = 1;

	public static String API_RESULT_SUCCESS = "200";
	public static String API_RESULT_FAIL = "400";


	public static DecimalFormat GLOBAL_FORMATTER = new DecimalFormat("#,##0.00");

	/*** BACKEND VARIABLES */

	public static String SERVER_URL_ONLY =//"http://192.168.1.34:3000/";
			"https://test.pivotaltechnology.co.uk/";
	public static String SERVER_URL_API =//"http://192.168.1.34:3000/api/";
			"https://test.pivotaltechnology.co.uk/api/";
	public static String WEBSITE_PIC_URL = //"http://192.168.1.34:3000/";
			"https://test.pivotaltechnology.co.uk/";

	public static JsonObject getUserDetailsJson = new JsonObject();

	public static final String GOOGLE_PLACE_LOG_TAG = "Google Places Autocomplete";
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String OUT_JSON = "/json";
}

package com.nexus.locum.locumnexus.utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;

public class Const {

	public static String SD_CARD_PATH = Environment
			.getExternalStorageDirectory() + "/" + "LocumNexus";
	public static String MyPREFERENCES = "LocumNexusPref";


	public static JsonObject CONST_PROFILE_JSON = new JsonObject();

	public static String PREF_IS_PROFILECOMPLETED = "PREF_IS_PROFILECOMPLETED";


	public static String PREF_LOGINKEY= "PREF_LOGINKEY";
	public static String INTRO_DONE= "INTRO_DONE";
	public static String PREF_USER_ID = "PREF_USER_ID";
	//public static String PREF_FACEBOOK_ID = "PREF_FACEBOOK_ID";
	public static String PREF_USER_MOBILE_NO = "PREF_USER_MOBILE_NO";
	public static String PREF_USER_FULL_NAME = "PREF_USER_FULL_NAME";
	public static String PREF_USER_PROFILE_PIC_URL = "PREF_USER_PROFILE_PIC_URL";
	public static String PREF_PASSWORD = "PREF_PASSWORD";
	public static String PREF_USER_EMAIL = "PREF_USER_EMAIL";
	public static String PREF_USER_TOKEN = "PREF_USER_TOKEN";
	public static String PREF_USER_ROLE = "PREF_USER_ROLE";




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

	public static String SERVER_URL_ONLY ="http://203.109.113.78:9001/"; //"http://54.153.127.215/api/";
	public static String SERVER_URL_API ="http://203.109.113.78:9001/api/"; //"http://54.153.127.215/api/";
	public static String WEBSITE_PIC_URL = "https://203.109.113.78:9001/";// assets/images/";

	public static JsonObject getUserDetailsJson = new JsonObject();

	public static final String GOOGLE_PLACE_LOG_TAG = "Google Places Autocomplete";
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String OUT_JSON = "/json";
}

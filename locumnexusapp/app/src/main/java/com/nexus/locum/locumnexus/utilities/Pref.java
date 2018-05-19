package com.nexus.locum.locumnexus.utilities;

import android.content.Context;
import android.content.SharedPreferences;


public class Pref {

	static SharedPreferences sharedpreferences;
	static SharedPreferences.Editor editor;

	/** set value in preference */
	public static void setValue(Context context, String key, String value) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setValue(Context context, String key, int value) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setValue(Context context, String key, long value) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void setValue(Context context, String key, boolean value) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/** get value in preference */
	public static String getValue(Context context, String key,String defaultValue) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		return sharedpreferences.getString(key, defaultValue);
	}

	public static int getValue(Context context, String key, int defaultValue) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		return sharedpreferences.getInt(key, defaultValue);
	}

	public static long getValue(Context context, String key, long defaultValue) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		return sharedpreferences.getLong(key, defaultValue);
	}

	public static boolean getValue(Context context, String key,	boolean defaultValue) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		return sharedpreferences.getBoolean(key, defaultValue);
	}

	/*public static void setLogValue(Context context, String key,
			ArrayList<LogBean> appointmentBean) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();
		String json = gson.toJson(appointmentBean);
		editor.putString(key, json);
		editor.commit();
	}

	public static ArrayList<LogBean> getLogValue(Context context,
			String key) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();

		String json = sharedpreferences.getString(key, "");
		ArrayList<LogBean> list = gson.fromJson(json,
				new TypeToken<ArrayList<LogBean>>() {
				}.getType());
		String json1 = gson.toJson(list);
		editor.putString(key, json1);
		editor.commit();

		return list;
	}
	
	public static void setContactValue(Context context, String key,
			ArrayList<ContactsBean> appointmentBean) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();
		String json = gson.toJson(appointmentBean);
		editor.putString(key, json);
		editor.commit();
	}

	public static ArrayList<ContactsBean> getContactValue(Context context,
			String key) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();

		String json = sharedpreferences.getString(key, "");
		ArrayList<ContactsBean> list = gson.fromJson(json,
				new TypeToken<ArrayList<ContactsBean>>() {
				}.getType());
		String json1 = gson.toJson(list);
		editor.putString(key, json1);
		editor.commit();

		return list;
	}
	
	public static void setHistoryValue(Context context, String key,
			ArrayList<MedicalHistoryBean> appointmentBean) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();
		String json = gson.toJson(appointmentBean);
		editor.putString(key, json);
		editor.commit();
	}

	public static ArrayList<MedicalHistoryBean> getHistoryValue(Context context,
			String key) {

		sharedpreferences = context.getSharedPreferences(Const.MyPREFERENCES,
				Context.MODE_PRIVATE);

		editor = sharedpreferences.edit();

		Gson gson = new Gson();

		String json = sharedpreferences.getString(key, "");
		ArrayList<MedicalHistoryBean> list = gson.fromJson(json,
				new TypeToken<ArrayList<MedicalHistoryBean>>() {
				}.getType());
		String json1 = gson.toJson(list);
		editor.putString(key, json1);
		editor.commit();

		return list;
	}*/
}

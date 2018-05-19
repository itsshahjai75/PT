package com.clickeat.customer.click_eatcustomer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by pivotech on 6/10/17.
 */

public class SharedData {

    public static ArrayList<String> getURLList(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return new ArrayList<>(preferences.getStringSet("url", new HashSet<String>()));
    }

    public static void setURLList(Context c, ArrayList<String> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("url", new HashSet<String>(value));
        editor.commit();
    }

    public static String getUserId(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("userId", "");
    }

    public static void setUserId(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", value);
        editor.commit();
    }

    public static String getPassword(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("password", "");
    }

    public static void setPassword(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", value);
        editor.commit();
    }

    public static void setEmailId(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", value);
        editor.commit();
    }

    public static String getEmailId(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("email", "");
    }

    public static void setIsRemember(Context c, Boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRemember", value);
        editor.commit();
    }

    public static boolean getIsRemember(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getBoolean("isRemember", false);
    }

    public static void setIsLoggedIn(Context c, Boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", value);
        editor.commit();
    }

    public static boolean getIsLoggedIn(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getBoolean("isLoggedIn", false);
    }

    public static void setSortingType(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sort_by", value);
        editor.commit();
    }

    public static String getSortingType(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("sort_by", "");
    }

    public static String getNotificationToken(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("token", "");
    }

    public static void setNotificationToken(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", value);
        editor.commit();
    }

    public static String getWebToken(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getString("webToken", "");
    }

    public static void setWebToken(Context c, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("webToken", value);
        editor.commit();
    }



}

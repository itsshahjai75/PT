package com.clickeat.restaurant.click_eatrestaurant.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

/**
 * Created by pivotech on 6/10/17.
 */

public class SharedData {


   public static String Token;
    static SharedPreferences CONST_SHAREDPREFERENCES;

    public static String getToken(Context mContext) {
         CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN, "");
    }

    public static void setToken(Context mContext,String token) {
        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_TOKEN, token).apply();
        Token = token;
    }

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


}

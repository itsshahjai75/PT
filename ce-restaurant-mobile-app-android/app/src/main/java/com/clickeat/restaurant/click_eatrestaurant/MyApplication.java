package com.clickeat.restaurant.click_eatrestaurant;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.MenuDetailsModel;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.OGlobalData;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.exception.RegisterForPushNotificationsException;
import com.pushwoosh.function.Callback;
import com.pushwoosh.function.Result;
import com.pushwoosh.notification.LocalNotification;
import com.pushwoosh.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by android on 3/4/18.
 */

public class MyApplication extends MultiDexApplication {

    public static final String LTAG = "PushwooshSample";
    private static OGlobalData globalData = new OGlobalData();
    public static String sDefSystemLanguage;
    private ArrayList<MenuDetailsModel> menuDetailsModels;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify
                .with(new FontAwesomeModule());
        sDefSystemLanguage = Locale.getDefault().getLanguage();

        Pushwoosh.getInstance().registerForPushNotifications(new Callback<String, RegisterForPushNotificationsException>() {
            @Override
            public void process(@NonNull Result<String, RegisterForPushNotificationsException> result) {
                if (result.isSuccess()) {
                    Log.d(LTAG, "Successfully registered for push notifications with token: " + result.getData());
                } else {
                    Log.d(LTAG, "Failed to register for push notifications:u " + result.getException().getMessage());
                }
            }
        });

        Pushwoosh.getInstance().sendTags(Tags.intTag("fav_number", 42));

        LocalNotification notification = new LocalNotification.Builder()
                .setMessage("Local notification")
                .setDelay(5)
                .build();

        //Pushwoosh.getInstance().scheduleLocalNotification(notification);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        sDefSystemLanguage = newConfig.locale.getLanguage();
    }

    public static OGlobalData getGlobalData() {
        return globalData;
    }

    public static void setGlobalData(OGlobalData globalData) {
        MyApplication.globalData = globalData;
    }

    public void addMenuDetailsDataList(List<MenuDetailsModel> classObjs) {
        menuDetailsModels.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                menuDetailsModels.add(classObjs.get(i));
            }
            String strInfo = String.format("add menu details info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<MenuDetailsModel> getMenuDetailsDataList() {
        return menuDetailsModels;
    }

    
}


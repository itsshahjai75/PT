package com.clickeat.customer.click_eatcustomer;

import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.clickeat.customer.click_eatcustomer.DataModel.OGlobalData;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.tags.Tags;

import org.piwik.sdk.Piwik;
import org.piwik.sdk.Tracker;
import org.piwik.sdk.TrackerConfig;
import org.piwik.sdk.extra.DownloadTracker;
import org.piwik.sdk.extra.TrackHelper;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by pivotech on 30/11/17.
 */

public class MyApplication extends MultiDexApplication {
    private static OGlobalData globalData = new OGlobalData();
    public static String sDefSystemLanguage;
    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify
                .with(new FontAwesomeModule());
        sDefSystemLanguage = Locale.getDefault().getLanguage();
        Pushwoosh.getInstance().registerForPushNotifications(result -> {
            if (result.isSuccess()) {
                SharedData.setNotificationToken(getApplicationContext(), result.getData());
                Log.d("push", "Successfully registered for push notifications with token: " + result.getData());
            } else {
                Log.d("push", "Failed to register for push notifications:u " + result.getException().getMessage());
            }
        });
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        initPiwik();
        Pushwoosh.getInstance().sendTags(Tags.intTag("fav_number", 42));

        /*LocalNotification notification = new LocalNotification.Builder()
                .setMessage("Local notification")
                .setDelay(5)
                .build();
        Pushwoosh.getInstance().scheduleLocalNotification(notification);*/

    }

    public synchronized Tracker getTracker() {
        if (tracker == null)
            tracker = Piwik.getInstance(this).newTracker(TrackerConfig.createDefault("https://pivotaltechnology.innocraft.cloud/piwik.php", 1));
        return tracker;
    }

    private void initPiwik() {
        // Print debug output when working on an app.
        Timber.plant(new Timber.DebugTree());

        // When working on an app we don't want to skew tracking results.
        // getPiwik().setDryRun(BuildConfig.DEBUG);

        // If you want to set a specific userID other than the random UUID token, do it NOW to ensure all future actions use that token.
        // Changing it later will track new events as belonging to a different user.
        // String userEmail = ....preferences....getString
        // getTracker().setUserId(userEmail);

        // Track this app install, this will only trigger once per app version.
        // i.e. "http://com.piwik.demo:1/185DECB5CFE28FDB2F45887022D668B4"
        TrackHelper.track().download().identifier(new DownloadTracker.Extra.ApkChecksum(this)).with(getTracker());
        // Alternative:
        // i.e. "http://com.piwik.demo:1/com.android.vending"
        // getTracker().download();
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
}

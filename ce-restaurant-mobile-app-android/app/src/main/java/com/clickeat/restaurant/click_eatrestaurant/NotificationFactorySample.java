package com.clickeat.restaurant.click_eatrestaurant;

/**
 * Created by android on 4/4/18.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clickeat.restaurant.click_eatrestaurant.MyApplication;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pushwoosh.notification.PushMessage;
import com.pushwoosh.notification.PushwooshNotificationFactory;

import java.util.UUID;

public class NotificationFactorySample extends PushwooshNotificationFactory {
    @Override
    public Notification onGenerateNotification(@NonNull PushMessage pushMessage) {
        Log.d(MyApplication.LTAG, "onGenerateNotification: " + pushMessage.toJson().toString());

       Notification notification = super.onGenerateNotification(pushMessage);
        // TODO: customise notification content
        
        return notification;
    }

    @Override
    protected Bitmap getLargeIcon(PushMessage pushMessage) {
        // TODO: set custom large icon for notification

        return super.getLargeIcon(pushMessage);
    }
}
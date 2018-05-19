package com.clickeat.restaurant.click_eatrestaurant;

/**
 * Created by android on 4/4/18.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.util.Log;

import com.clickeat.restaurant.click_eatrestaurant.MyApplication;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.notification.LocalNotification;
import com.pushwoosh.notification.NotificationServiceExtension;
import com.pushwoosh.notification.PushMessage;

import static android.content.Context.MODE_PRIVATE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

public class NotificationServiceSample extends NotificationServiceExtension {
    SharedPreferences CONST_SHAREDPREFERENCES;
    @Override
    public boolean onMessageReceived(final PushMessage message) {
        Log.d(MyApplication.LTAG, "NotificationService.onMessageReceived: " + message.toJson().toString());

        CONST_SHAREDPREFERENCES  = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        // automatic foreground push handling
        if (isAppOnForeground()) {
            /*Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    handlePush(message);
                }
            });

            // this indicates that notification should not be displayed
            return true;*/
        }

        if(!CONST_SHAREDPREFERENCES.getString(Const.PREF_LOGINKEY,"").
                equalsIgnoreCase("yes")){

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    handlePush(message);
                }
            });

            // this indicates that notification should not be displayed
            return true;

        }

        return false;
    }

    @Override
    protected void startActivityForPushMessage(PushMessage message) {
       // super.startActivityForPushMessage(message);

        // TODO: start custom activity if necessary

        //handlePush(message);

        Intent launchIntent  = new Intent(getApplicationContext(), NavigationMainScreen.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // (Optional) pass notification data to Activity
        launchIntent.putExtra(Pushwoosh.PUSH_RECEIVE_EVENT, message.toJson().toString());

        getApplicationContext().startActivity(launchIntent);

    }

    @MainThread
    private void handlePush(PushMessage message) {

        Log.d(MyApplication.LTAG, "NotificationService.handlePush: " + message.toJson().toString());
        // TODO: handle push message



    }
}

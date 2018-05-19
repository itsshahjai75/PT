package com.clickeat.restaurant.click_eatrestaurant;

import android.util.Log;

/**
 * Created by pivotech on 4/10/17.
 */

public class CookieManager {

    public static String xsrfToken;
    public static String authorizationToken;
    public static String cookieAll;

    public static String getXsrfToken() {
        return xsrfToken;
    }

    public static void setXsrfToken(String xsrfToken) {
        Log.d("<>cookiemanager-", " xsrf token in manager "+xsrfToken);
        CookieManager.xsrfToken = xsrfToken;
    }

    public static String getAuthorizationToken() {
        return authorizationToken;
    }

    public static void setAuthorizationToken(String authorizationToken) {
        CookieManager.authorizationToken = authorizationToken;
    }

    public static String getCookieAll() {
        return cookieAll;
    }

    public static void setCookieAll(String cookieAll) {
        Log.d("<>cookiemanager-", " setCookieAll in manager "+cookieAll);
        CookieManager.cookieAll = cookieAll;
    }
}

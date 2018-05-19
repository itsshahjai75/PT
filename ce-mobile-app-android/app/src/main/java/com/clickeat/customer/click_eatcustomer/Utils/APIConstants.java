package com.clickeat.customer.click_eatcustomer.Utils;

/**
 * Created by pivotech on 10/10/17.
 */

public class APIConstants {
//    public static String URL = "http://103.226.185.40:9013";
//    public static String URL = "http://192.168.1.34:3000";
//    public static String URL = "http://192.168.1.27:3000";
    public static String URL = "https://test.pivotaltechnology.co.uk";
//    public static String TOKEN;
    public static Double Latitude;
    public static Double Longitude;
    public static String TOKEN_PARAM_REQUEST_FORM = "app";
    public static String ROLE_PARAM = "user";
    public static String DEVICE_TYPE = "android";

   /* public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        APIConstants.TOKEN = TOKEN;
    }
*/
    public static Double getLatitude() {
        return Latitude;
    }

    public static void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public static Double getLongitude() {
        return Longitude;
    }

    public static void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}

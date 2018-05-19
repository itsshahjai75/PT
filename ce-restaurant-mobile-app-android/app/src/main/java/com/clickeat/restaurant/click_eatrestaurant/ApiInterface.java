package com.clickeat.restaurant.click_eatrestaurant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by pivotech on 28/9/17.
 */

public interface ApiInterface {


    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/users/me")
    Call<JsonElement> getLoginData(
//            @Header("X-XSRF-TOKEN") String xsrfToken,
//            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorizationToken
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/users/sendPasswordMail")
    Call<JsonElement> forgotPassword(
            @Body String emailId
//            @Header("X-XSRF-TOKEN") String xsrfToken,
//            @Header("Content-Type") String contentType,
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/getTables")
    Call<JsonElement> getTableNumberByRestaurantId(
            @Body JsonObject restaurantId);

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/getTimeslotByDate")
    Call<JsonElement> getTimeSlotsByRestaurantId(
            @Body JsonObject restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/bookatables")
    Call<JsonElement> sendBookATableData(
            @Body JsonObject bookDetails
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/getTableGridData")
    Call<JsonElement> getBookingCalendar(
            @Body JsonObject bookingParams
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/getGridDataForAmend")
    Call<JsonElement> getTableGridForAmend(
            @Body JsonObject bookingParams
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/menu-managements/search/menu")
    Call<JsonElement> getMenuDetailsByRestaurantId(
            @Body JsonObject restaurantId
    );

}

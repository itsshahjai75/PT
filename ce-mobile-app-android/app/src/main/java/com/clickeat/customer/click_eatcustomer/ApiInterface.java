package com.clickeat.customer.click_eatcustomer;

import com.clickeat.customer.click_eatcustomer.DataModel.LoginAPI;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantByLocationModel;
import com.clickeat.customer.click_eatcustomer.DataModel.SignUpModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by pivotech on 28/9/17.
 */

public interface ApiInterface {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("auth/local")
    Call<LoginAPI> authenticate(@Body String body);

    @POST("api/users/sendPasswordMail")
    Call<LoginAPI> sendPassword(@Body String email);

    @GET("/")
    Call<ResponseBody> getCSRF();

   /* @Headers({"Content-Type: application/json;charset=utf-8",
            "X-XSRF-TOKEN: m6AoMUCsukVVUxJTanKexn7nIkQQhJJOuOaZ0="
    })*/

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("auth/local")
    Call<JsonElement> getAuthorisation(
            @Body LoginAPI loginData
//            @Header("Content-Type") String contentType
//            @Header("Cookie") String cookie,
//            @Header("X-XSRF-TOKEN") String xsrfToken,
//            @Header("Content-Type") String contentType
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/users/me")
    Call<JsonElement> getLoginData(
//            @Header("X-XSRF-TOKEN") String xsrfToken,
//            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorizationToken
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/cuisine-types/{defaultLanguage}")
    Call<JsonElement> getCuisineList(@Path("defaultLanguage") String defaultLanguage);

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/users/sendPasswordMail")
    Call<JsonElement> forgotPassword(
            @Body String emailId
//            @Header("X-XSRF-TOKEN") String xsrfToken,
//            @Header("Content-Type") String contentType,
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/users")
    Call<JsonElement> getSIgnupAuthorisation(
            @Body SignUpModel signUpData
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/search/restaurantByLocation")
    Call<JsonElement> getRestaurantByLocation(
            @Body RestaurantByLocationModel restaurantData
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/facilities/{defaultLanguage}")
    Call<JsonElement> getFacilitiesList(@Path("defaultLanguage") String defaultLanguage);

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/special-diets/{defaultLanguage}")
    Call<JsonElement> getSpecialDiet(@Path("defaultLanguage") String defaultLanguage);

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/search/restaurant")
    Call<JsonElement> getRestaurantDetailsByRestaurantId(
            @Body JsonObject restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/updateRestaurant/like")
    Call<JsonElement> setRestaurantLike(
            @Body JsonObject user_restaurantId
    );
//    {userId: "5a0589236253bf501c2a842f", restaurantId: "5a4e1d3a382c6d2b2bd20054"}

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/updateRestaurant/dislike")
    Call<JsonElement> setRestaurantDislike(
            @Body JsonObject user_restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/users/favouriteRestaurant")
    Call<JsonElement> setFavouriteRestaurant(
            @Body JsonObject user_restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/users/removeFavouriteRestaurant")
    Call<JsonElement> removeFavouriteRestaurant(
            @Body JsonObject user_restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("api/menu-managements")
    Call<JsonElement> getMenuManagements();

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/menu-managements/search/menu")
    Call<JsonElement> getMenuDetailsByRestaurantId(
            @Body JsonObject restaurantId
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("api/restaurant-details/getTables")
    Call<JsonElement> getTableNumberByRestaurantId(
            @Body JsonObject restaurantId
    );

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
    @POST("api/bookatables/getBookingsByDurationType")
    Call<JsonElement> getBookingsByDurationTYpe(
            @Body JsonObject bookDetails
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/getTableGridData")
    Call<JsonElement> getBookingCalendar(
            @Body JsonObject bookingParams
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/addReview")
    Call<JsonElement> addCustomerReview(
            @Body JsonObject reviewParams
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/push-tokens")
    Call<JsonElement> sendNotificationToken(
            @Body JsonObject notificationAuth
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/getReviewsOfRestaurant/{restaurantId}")
    Call<JsonElement> getAllCustomerReviews(@Path("restaurantId") String restaurantId,
                                            @Body JsonObject reviewFilter);

    @Multipart
    @POST("/api/restaurant-details/addReviewWithImages/{restaurant_id}/{user_id}")
    Call<JsonElement> addReviewWithImages(@Path("restaurant_id") String restaid,
                                          @Path("user_id") String user_id,
                                          @Part MultipartBody.Part[] image,
                                          @Part("restaurant_id") RequestBody restaurantId,
                                          @Part("customer") RequestBody customer,
                                          @Part("reviewDate") RequestBody reviewDate,
                                          @Part("overallRating") RequestBody overallRating,
                                          @Part("locationRating") RequestBody locationRating,
                                          @Part("foodRating") RequestBody foodRating,
                                          @Part("staffRating") RequestBody staffRating,
                                          @Part("serviceRating") RequestBody serviceRating,
                                          @Part("facilitiesRating") RequestBody facilitiesRating,
                                          @Part("positives") RequestBody positives,
                                          @Part("suggestions") RequestBody suggestions);

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/push-tokens/logout")
    Call<JsonElement> notificationLogout(
            @Body JsonObject notificationAuth
    );

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/api/restaurant-details/search/getFavouriteRestaurants")
    Call<JsonElement> getFavouriteRestaurantList(
            @Body JsonObject userId
    );

}

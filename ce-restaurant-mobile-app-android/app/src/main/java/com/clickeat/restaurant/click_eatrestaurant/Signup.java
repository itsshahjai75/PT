package com.clickeat.restaurant.click_eatrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Signup extends AppCompatActivity {
    String userName;
    TextView labelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        labelName = (TextView) findViewById(R.id.txtLabelName);
        getLoginData();
    }

    public void getLoginData() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.SERVER_URL_API)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final Call<JsonElement> mService = mInterfaceService.getLoginData(CookieManager.getAuthorizationToken());
        Log.d("<>login-", " login service ==> " + mService.toString());
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Headers is  ==> " + mService.request().headers().toString());
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");
//        Log.d("<>login-", " headers with token  ==> "+mService.request().header("token").toString()+"");
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response headers ===> " + response.headers());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    Log.d("<>log-", " in response of login ");
                    Log.d("<>log-", " response body for data ==> "+response.body().toString());
                    userName = response.body().getAsJsonObject().get("name").getAsString();
                    labelName.setText("Welcome "+userName);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });

    }
}

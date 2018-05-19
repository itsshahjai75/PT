package com.clickeat.restaurant.click_eatrestaurant.utilities;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jay-Andriod on 05-Apr-17.
 */

public class APICall {


    public static final MediaType JSON = MediaType.parse("application/json");

    static OkHttpClient client = new OkHttpClient();

    static Request.Builder request = new Request.Builder();
        /*client.connectTimeoutMillis();
        client.readTimeoutMillis();*/


    public static String ResponseAPI(Context mContext, String url, String json, String method) throws IOException {

        if(ConnectionDetector.isConnectingToInternet(mContext)) {

            RequestBody body = RequestBody.create(JSON, json);
            Log.d("data send--", "" + json);
            request.url(url)
                    .addHeader("Content-Type", "application/json");


            if (method.equalsIgnoreCase("post")) {
                request.post(body)
                        .build();

            } else if (method.equalsIgnoreCase("get")) {
                request .get()
                        .build();

            } else if (method.equalsIgnoreCase("put")) {
                request.put(body)
                        .build();

            } else if (method.equalsIgnoreCase("delete")) {
                request.delete(body)
                        .build();
            }

            try (Response response = client.newCall(request.build()).execute()) {
                response.code();
                Log.d("Response Code", "" + response.code());
                return response.body().string();
            }
        }else{
         return "noInternet";
        }

    }


    public static String ResponseAPIWithHeader(Context mContext, String url, String token, String json, String method) throws IOException {






            Log.d("data URL---", "" +url);
            Log.d("data token---", "" +token);
            //Log.d("data send--", "" + json);
            request.url(url)
                    .addHeader("Authorization","Bearer "+token)
                    /*.addHeader("Content-Type", "application/json")*/;


            if (method.equalsIgnoreCase("post")) {
                RequestBody body = RequestBody.create(JSON, json);
                request.post(body)
                        .build();

            } else if (method.equalsIgnoreCase("get")) {
                request .get();

            } else if (method.equalsIgnoreCase("put")) {
                RequestBody body = RequestBody.create(JSON, json);
                request.put(body)
                        .build();

            } else if (method.equalsIgnoreCase("delete")) {
                RequestBody body = RequestBody.create(JSON, json);
                request.delete(body)
                        .build();
            }


            try (Response response = client.newCall(request.build()).execute()) {
                response.code();
                Log.d("Response Code for --"+url,""+ response.code());
                return response.body().string();
            }
    }

    public static String POST_MULTIPART(String url,String filename,File file,String service_provider_id) throws IOException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        MediaType MEDIA_TYPE = MediaType.parse("image/*" ); // e.g. "image/png"

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("service_provider_id",service_provider_id)
                .addFormDataPart("cake_image_url","test")
                /*.addFormDataPart("filename",filename)*/ //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE, file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}

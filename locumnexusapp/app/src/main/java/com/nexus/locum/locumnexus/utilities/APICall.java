package com.nexus.locum.locumnexus.utilities;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
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

        if(new ConnectionDetector(mContext).isConnectingToInternet() ==true) {

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

    public static String ResponseWithAuthAPI(Context mContext, String AuthToken, String url, String json, String method) throws IOException {

        if(new ConnectionDetector(mContext).isConnectingToInternet() ==true) {

            RequestBody body = RequestBody.create(JSON, json);
            Log.d("data send--", "" + json);


            if(AuthToken==null){

            }else {
                request.url(url)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer "+AuthToken);
            }

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


    public static String POST_MULTIPART(String url,String filename,File file, String header) throws IOException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        MediaType MEDIA_TYPE = MediaType.parse("/*" ); // e.g. "image/png"

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                /*.addFormDataPart("filename",filename)*/ //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE, file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+header)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}

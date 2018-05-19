package com.clickeat.customer.click_eatcustomer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.DataModel.LoginAPI;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;
import org.piwik.sdk.Tracker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {
    private ImageView imgView;

    private Animation fadeIn;
    private ProgressDialog loadingSpinner;
    private Tracker myTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imgView = (ImageView) findViewById(R.id.imgView);
        /*String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("<>token-", refreshedToken);*/
       /* Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();*/

       /* tracker demo */
// Get the `Tracker` you want to use
//        myTracker = MyApplication.getTracker();
/*
// Track a screen view
        TrackHelper.track().screen("/SplashScreen").title("Splash").with(myTracker);
*/
// Monitor your app installs
//        TrackHelper.track().download().with(myTracker);



        /*fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imgView.setAnimation(fadeIn);*/
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    /*if (SharedData.getIsRemember(SplashScreen.this) == true
                            || SharedData.getUserId(SplashScreen.this) != "") {
                        String txtEmail = SharedData.getEmailId(SplashScreen.this);
                        String txtPassword = SharedData.getPassword(SplashScreen.this);
                        loginProcessWithRetrofit(txtEmail, txtPassword);
                    } else {*/
                    Intent intent = new Intent(SplashScreen.this, PreLoginMainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    }
                }
            }
        };
        timerThread.start();

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void loginProcessWithRetrofit(final String email, final String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSpinner();
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("email", "restaurant@gmail.com");
            paramObject.put("password", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final LoginAPI login = new LoginAPI(email, password, APIConstants.TOKEN_PARAM_REQUEST_FORM);
        Call<JsonElement> mService = mInterfaceService.getAuthorisation(login);
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, final Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            if (loadingSpinner != null)
                                dismissSpinner();
                            String token = null;
                            Log.d("<>log-", " in response of login ");
                            JsonElement mLoginObject = response.body();
                            Log.d("<>log-", " in response of login " + mLoginObject.toString() + "");
                            token = "Bearer ";
                            String token1 = mLoginObject.getAsJsonObject().get("token").getAsString();
                            Log.d("<>log-", " in response token1 ==>  " + token1 + "");
                            token += token1;
                            Log.d("<>log-", " in response token ==>  " + token + "");
                            SharedData.setWebToken(SplashScreen.this, token);
                            if (response.code() == 200) {
                                Intent i = new Intent(SplashScreen.this, PreLoginMainActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        } else if (response.code() == 401) {
                            Log.d("<>log-", " Unauthorized");
                            if (loadingSpinner != null)
                                dismissSpinner();
                            Toast.makeText(SplashScreen.this, R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingSpinner != null)
                            dismissSpinner();
                        Toast.makeText(SplashScreen.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(SplashScreen.this, R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }
}

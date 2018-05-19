package com.nexus.locum.locumnexus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;

import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_LOGINKEY;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_PASSWORD;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_EMAIL;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;
import static com.nexus.locum.locumnexus.utilities.Const.getUserDetailsJson;

public class SplashScreen extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    String str_email,str_password;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        CONST_SHAREDPREFERENCES  = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if(CONST_SHAREDPREFERENCES.getString(PREF_LOGINKEY,"").equalsIgnoreCase("yes")){
                    str_email  = CONST_SHAREDPREFERENCES.getString(PREF_USER_EMAIL,"");
                    str_password = CONST_SHAREDPREFERENCES.getString(PREF_PASSWORD,"");

                    new LoginLocumAPI().execute();

                }else {
                    Intent i = new Intent(SplashScreen.this, LoginLocum.class);
                    startActivity(i);
                    finish();
                }

                /*Intent i = new Intent(SplashScreen.this, LoginLocum.class);
                startActivity(i);
                finish();*/

            }
        }, SPLASH_TIME_OUT);
    }

    String resUserDetails;
    protected ProgressDialog progressDialog;
    private class LoginLocumAPI extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(SplashScreen.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject LoginJson = new JsonObject();
                LoginJson.addProperty("email",str_email);
                LoginJson.addProperty("password",str_password);
                //Log.d("data send--",""+LoginJson.toString());

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_ONLY +"auth/local", LoginJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserDetails=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES UserDetails---", resUserDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();

                if(rootObj.has("errors")){

                }else if(rootObj.has("message")){

                    startActivity(new Intent(SplashScreen.this,LoginLocum.class));
                    finish();
                    //Toast.makeText(SplashScreen.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }else{

                    getUserDetailsJson = null;

                    String token = rootObj.get("token").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_PASSWORD,str_password).apply();
                    startActivity(new Intent(SplashScreen.this,NavigationMainDashboardLocum.class));
                    finish();

                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

}

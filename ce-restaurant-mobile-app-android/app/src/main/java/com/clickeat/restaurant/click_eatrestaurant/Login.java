package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.FloorModel;
import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.clickeat.restaurant.click_eatrestaurant.utilities.SharedData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pushwoosh.PushFcmIntentService;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.PushwooshService;
import com.pushwoosh.internal.platform.service.PushwooshJobService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPI;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_LOGINKEY;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_PASSWORD;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_EMAIL;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ROLE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;

public class Login extends AppCompatActivity {

    @BindView(R.id.edtLoginEmail) AutoCompleteTextView edtEmail;
    @BindView(R.id.edtLoginPassword) EditText edtPassword;
    @BindView(R.id.chkLoginRememberMe) CheckBox chkRemember;
    @BindView(R.id.txtForgotPwd) TextView txtForgot;
    Button btnLogin;
    @BindView(R.id.txtMail) TextView txtMail;
    @BindView(R.id.txtCall) TextView txtPhone;

    public String strtxtEmail,strtxtPassword;

    private ArrayList<String> urlArrayList;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(Login.this);

        CONST_SHAREDPREFERENCES  = getBaseContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        btnLogin= (Button)this.findViewById(R.id.btnLogin);



            urlArrayList = new ArrayList<>();
            urlArrayList = SharedData.getURLList(Login.this);
            Log.d("<>array-", " size of array list ==> " + urlArrayList.size() + "");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, urlArrayList);
            edtEmail.setThreshold(1);
            edtEmail.setAdapter(adapter);
            Typeface roboto = Typeface.createFromAsset(getAssets(),
                    "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
            edtEmail.setTypeface(roboto);
            edtPassword.setTypeface(roboto);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edtEmail.getText().toString().length() == 0) {
                        Toast.makeText(Login.this, R.string.mail_error, Toast.LENGTH_LONG).show();
                        edtEmail.setError(getResources().getString(R.string.mail_error));
//                    getResources().getDrawable(R.drawable.ic_email_white_0padding));
                        return;
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
                        //Validation for Invalid Email Address
                        Toast.makeText(Login.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                        edtEmail.setError(getResources().getString(R.string.invalid_email));
                        return;
                    } else if (edtPassword.getText().toString().length() == 0) {
                        Toast.makeText(Login.this, R.string.password_error
                                , Toast.LENGTH_LONG).show();
                        edtPassword.setError(getResources().getString(R.string.password_error));
                        return;
                    } else {
                        //Validate Successfully.
                        //Log.d("<>login-", " it is in else part");
                        strtxtEmail = edtEmail.getText().toString();
                        strtxtPassword = edtPassword.getText().toString();
                        //loginProcessWithRetrofit(txtEmail, txtPassword);

                        if(ConnectionDetector.isConnectingToInternet(getBaseContext())){

                            LoginUser login = new LoginUser();
                            login.execute();

                        }else{
                            Toast.makeText(Login.this,"Check Internet Connection !",Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });

            txtForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this,Forgotpassword.class));
                    //showDialogForgot();
                }
            });
            txtMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEmailClient();
                }
            });
            txtPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForPermission(android.Manifest.permission.CALL_PHONE, 001);
                }
            });
    }

    private void openEmailClient() {
        final Intent emailLauncher = new Intent(Intent.ACTION_VIEW);
        emailLauncher.setType("message/rfc822");
//        Intent testIntent = new Intent(Intent.ACTION_VIEW);
//        Uri data = Uri.parse("mailto:?subject=" + "blah blah subject" + "&body=" + "blah blah body" + "&to=" + "sendme@me.com");
//        testIntent.setData(data);
//        startActivity(testIntent);
        startActivity(emailLauncher);
        try {
//            startActivity(emailLauncher);
        } catch (ActivityNotFoundException e) {

        }
    }

    private void openDialCall() {
        String number = "tel:" + txtPhone.getText().toString().trim();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Login.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Login.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(Login.this, new String[]{permission}, requestCode);
                openDialCall();
            }
        } else {
            // Toast.makeText(Login.this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            openDialCall();
        }
    }

    String resUserDetails;
    String token;
    protected ProgressDialog progressDialog;
    class LoginUser extends AsyncTask<Object, Void, String> {


        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(Login.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {
                CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,"").clear().commit();
                JsonObject ReqestLoginJson = new JsonObject();
                ReqestLoginJson.addProperty("email",strtxtEmail);
                ReqestLoginJson.addProperty("password",strtxtPassword);
                //Log.d("data ---",""+ReqestLoginJson.toString());

                String responseUSerTitles = ResponseAPI(Login.this,Const.SERVER_URL_ONLY +"auth/local", ReqestLoginJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);

                resUserDetails=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserDetails;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();
                Log.i("login API response---", resUserDetails);

                if(rootObj.has("errors")){

                }else if(rootObj.has("message")){
                    Toast.makeText(Login.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }else{

                    token = rootObj.get("token").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).commit();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").commit();

                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_EMAIL,strtxtEmail).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_PASSWORD,strtxtPassword).apply();

                    Intent i = new Intent(Login.this, NavigationMainScreen.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                    finish();


                    /*if (urlArrayList != null) {
                        if (!(urlArrayList.contains(strtxtEmail))) {
                            //Adding input string into the name array-list
                            urlArrayList.add(strtxtEmail);
                            SharedData.setURLList(Login.this, urlArrayList);
                        }
                    }
                    if (chkRemember.isChecked() == true) {
                        SharedData.setIsRemember(Login.this, true);
                    }*/
                }


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            progressDialog.dismiss();




            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    new GetRestaurantDetails().execute();

                    progressDialog.dismiss();
                }
            }, 5000);*/

        }

    }


    String resUserProfileDetails;
    public class GetRestaurantDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                String responseUSerTitles = ResponseAPIWithHeader(getBaseContext(),
                        Const.SERVER_URL_API +"users/me"
                        ,CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,""),
                        "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserProfileDetails=responseUSerTitles;



            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserProfileDetails;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("user/me API call RESponse---", ""+resUserProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserProfileDetails).getAsJsonObject();

                Const.getRestaurantUserDetails = rootObj;

                String _id= rootObj.get("_id").getAsString();
                CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ID,rootObj.get("_id").getAsString()).apply();

                //String provider= rootObj.get("provider").getAsString();

                String name= rootObj.get("name").getAsString();
                CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_FULL_NAME,rootObj.get("name").getAsString()).apply();


                String email= rootObj.get("email").getAsString();
                CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_EMAIL,rootObj.get("email").getAsString()).apply();

                String mobile_number= rootObj.get("mobile_number").getAsString();
                CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_MOBILE_NO,rootObj.get("mobile_number").getAsString()).apply();


                //  String postcode= rootObj.get("postcode").getAsString();
                // String tnc= rootObj.get("tnc").getAsString();

                String subrole= rootObj.get("subrole").getAsString();
                //   String restaurantStatus= rootObj.get("restaurantStatus").getAsString();

                String __v= rootObj.get("__v").getAsString();
                //  String passwordResetCode= rootObj.get("passwordResetCode").getAsString();

                String role= rootObj.get("role").getAsString();

                CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_ROLE,subrole).apply();

                if(role.equalsIgnoreCase("restaurant")) {

                    if (subrole.equalsIgnoreCase("superadmin")) {

                        CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_EMAIL,strtxtEmail).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_PASSWORD,strtxtPassword).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();

                    } else if (subrole.equalsIgnoreCase("ops_manager") || subrole.equalsIgnoreCase("Ops Manager")) {

                        CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_EMAIL,strtxtEmail).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_PASSWORD,strtxtPassword).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                        CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();



                    } else if (subrole.equalsIgnoreCase("kitchen")) {
                        //startActivity(new Intent(Login.this,Login.class));
                        Toast.makeText(Login.this,"Sorry ! you are not authorised restaurant member !",Toast.LENGTH_LONG).show();

                    }else if (subrole.equalsIgnoreCase("driver")) {
                        //startActivity(new Intent(Login.this,Login.class));
                        Toast.makeText(Login.this,"Sorry ! you are not authorised restaurant member !",Toast.LENGTH_LONG).show();

                    }else{
                        //startActivity(new Intent(Login.this,Login.class));
                        Toast.makeText(Login.this,"Sorry ! you are not authorised restaurant member !",Toast.LENGTH_LONG).show();

                    }



                }else{
                    //startActivity(new Intent(Login.this,Login.class));
                    Toast.makeText(Login.this,"Sorry ! you are not restaurant member !",Toast.LENGTH_LONG).show();
                }


                new RegPushToken().execute();

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private class RegPushToken extends AsyncTask<Object, Void, String> {


        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(Login.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject ReqestLoginJson = new JsonObject();
                ReqestLoginJson.addProperty("token",Pushwoosh.getInstance().getPushToken());
                ReqestLoginJson.addProperty("userId",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                ReqestLoginJson.addProperty("userRole",CONST_SHAREDPREFERENCES.getString(PREF_USER_ROLE,""));
                ReqestLoginJson.addProperty("isLoggedIn",true);
                ReqestLoginJson.addProperty("lastSession", String.valueOf(Calendar.getInstance().getTime()));
                ReqestLoginJson.addProperty("deviceType",Const.DEVICE_TYPE);
                //Log.d("data ---",""+ReqestLoginJson.toString());

                String responseUSerTitles = ResponseAPI(Login.this,Const.SERVER_URL_API +"push-tokens", ReqestLoginJson.toString(),"post");
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
                /*JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();*/

                }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

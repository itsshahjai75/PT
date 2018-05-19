package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.clickeat.restaurant.click_eatrestaurant.utilities.SharedData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPI;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_LOGINKEY;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_PASSWORD;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_EMAIL;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_FULL_NAME;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;

public class Profile extends AppCompatActivity {

    @BindView(R.id.edtLoginEmail)
    AutoCompleteTextView edtEmail;
    @BindView(R.id.edtuserName)
    AutoCompleteTextView edtuserName;

    @BindView(R.id.edtLoginPassword)
    EditText edtPassword;
    @BindView(R.id.chkLoginRememberMe)
    CheckBox chkRemember;
    @BindView(R.id.txtForgotPwd)
    TextView txtForgot;
    Button btnUpdate;
    @BindView(R.id.txtMail) TextView txtMail;
    @BindView(R.id.txtCall) TextView txtPhone;

    public String strtxtEmail,strtxtPassword;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(Profile.this);

        CONST_SHAREDPREFERENCES  = getBaseContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        btnUpdate= (Button)this.findViewById(R.id.btnUpdate);

        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
        edtEmail.setTypeface(roboto);
        edtPassword.setTypeface(roboto);
        edtEmail.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_EMAIL,""));
        edtuserName.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_FULL_NAME,""));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEmail.getText().toString().length() == 0) {
                    Toast.makeText(Profile.this, R.string.mail_error, Toast.LENGTH_LONG).show();
                    edtEmail.setError(getResources().getString(R.string.mail_error));
//                    getResources().getDrawable(R.drawable.ic_email_white_0padding));
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
                    //Validation for Invalid Email Address
                    Toast.makeText(Profile.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                    edtEmail.setError(getResources().getString(R.string.invalid_email));
                    return;
                } else if (edtPassword.getText().toString().length() == 0) {
                    Toast.makeText(Profile.this, R.string.password_error
                            , Toast.LENGTH_LONG).show();
                    edtPassword.setError(getResources().getString(R.string.password_error));
                    return;
                } else {
                    //Validate Successfully.
                    //Log.d("<>login-", " it is in else part");
                    strtxtEmail = edtEmail.getText().toString();
                    strtxtPassword = edtPassword.getText().toString();
                    //loginProcessWithRetrofit(txtEmail, txtPassword);

                    /*if(ConnectionDetector.isConnectingToInternet(getBaseContext())){
                        new LoginUser().execute();
                    }else{
                        Toast.makeText(Login.this,"Check Internet Connection !",Toast.LENGTH_LONG).show();
                    }*/

                }
            }
        });

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,Forgotpassword.class));
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
        if (ContextCompat.checkSelfPermission(Profile.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Profile.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Profile.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(Profile.this, new String[]{permission}, requestCode);
                openDialCall();
            }
        } else {
            // Toast.makeText(Login.this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            openDialCall();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


    /*String resUserDetails;
    protected ProgressDialog progressDialog;
    private class LoginUser extends AsyncTask<Object, Void, String> {


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
                    Toast.makeText(Login.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }else{

                    String token = rootObj.get("token").getAsString();

                    if (urlArrayList != null) {
                        if (!(urlArrayList.contains(strtxtEmail))) {
                            //Adding input string into the name array-list
                            urlArrayList.add(strtxtEmail);
                            SharedData.setURLList(Login.this, urlArrayList);
                        }
                    }
                    if (chkRemember.isChecked() == true) {
                        SharedData.setIsRemember(Login.this, true);
                    }

                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_EMAIL,strtxtEmail).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_PASSWORD,strtxtPassword).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();
                    Intent i = new Intent(Login.this, NavigationMainScreen.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                    finish();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }*/

}

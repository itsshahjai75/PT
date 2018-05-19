package com.clickeat.customer.click_eatcustomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.DataModel.LoginAPI;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail, edtPassword;
    CheckBox chkRemember;
    TextView txtForgot, txtMail, txtPhone;
    Button btnLogin, btnSignup;


    private ArrayList<String> urlArrayList;
    private ProgressDialog loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ButterKnife.bind(Login.this);
        findIds();
        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
        edtEmail.setTypeface(roboto);
        edtPassword.setTypeface(roboto);

      /*  layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>key-", " on touch keyboard");
                hideKeyboard(view);
            }
        });

        layoutLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("<>key-", " on touch keyboard");
                hideKeyboard(view);
                return false;
            }
        });*/

        /*txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>login-", " call ");
            }
        });*/
    }

    public void findIds() {
        edtEmail =  findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        chkRemember = findViewById(R.id.chkLoginRememberMe);
        txtForgot =  findViewById(R.id.txtForgotPwd);
        btnLogin =  findViewById(R.id.btnLogin);
        btnSignup =  findViewById(R.id.btnSignUp);
        txtMail =  (TextView) findViewById(R.id.txtMailLogin);
        txtPhone =  (TextView) findViewById(R.id.txtCallLogin);

        btnLogin.setOnClickListener(this);
        txtForgot.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        txtMail.setOnClickListener(this);
        txtPhone.setOnClickListener(this);

    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        InputMethodManager imm = (InputMethodManager) getSystemService(
//                Activity.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /*for checking the validation*/
    public void checkValidations() {
        if (edtEmail.getText().toString().length() == 0) {
            Toast.makeText(Login.this, R.string.mail_error, Toast.LENGTH_LONG).show();
            edtEmail.setError(getResources().getString(R.string.mail_error));
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
        }
       /* else if(!android.util.Patterns.DOMAIN_NAME.matcher(edtEmail.getText().toString()).matches())
        {
            //Validation for Website Address
            Toast.makeText(getApplicationContext(), “Invalid Website”, Toast.LENGTH_LONG).show();
            et3.setError(“Invalid Website”);
            return;
        }*/
        else {
            //Validate Successfully.
            Log.d("<>login-", " it is in else part");
            String txtEmail = edtEmail.getText().toString();
            String txtPassword = edtPassword.getText().toString();
            loginProcessWithRetrofit(txtEmail, txtPassword);
        }
    }

    private void loginProcessWithRetrofit(final String email, final String password) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final LoginAPI login = new LoginAPI(email, password, APIConstants.TOKEN_PARAM_REQUEST_FORM);
        Call<JsonElement> mService = mInterfaceService.getAuthorisation(login);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    ArrayList<String> favourites = new ArrayList<>();
                    Log.d("<>log-", " in response of login ");
                    String token = null;
                    Log.d("<>log-", " in response of login body" + response.body().toString());
                    JsonElement mLoginObject = response.body();
                    Log.d("<>log-", " in response of login " + mLoginObject.toString() + "");
                    token = "Bearer ";
                    String token1 = mLoginObject.getAsJsonObject().get("token").getAsString();
                    Log.d("<>log-", " in response token1 ==>  " + token1 + "");
                    token += token1;
                    Log.d("<>log-", " in response token ==>  " + token + "");
                    SharedData.setWebToken(Login.this, token);
                    if (response.code() == 200) {

                        if (chkRemember.isChecked() == true) {
                            SharedData.setIsRemember(Login.this, true);
                            SharedData.setEmailId(Login.this, email);
                            SharedData.setPassword(Login.this, password);
                        }
                        SharedData.setEmailId(Login.this, email);
                        SharedData.setPassword(Login.this, password);
                        getLoginData();
                      /*  Intent i = new Intent(Login.this, PreLoginMainActivity.class);
                        i.putExtra("signIn", "true");
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();*/
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Toast.makeText(Login.this, R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(Login.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Log.d("<>is-", " on click ");
        switch (view.getId()) {
            case R.id.btnLogin:
                checkValidations();
                break;
            case R.id.btnSignUp:
                Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.txtCallLogin:
                askForPermission(android.Manifest.permission.CALL_PHONE, 001);
                break;
            case R.id.txtForgotPwd:
                showDialogForgot();
                break;
            case R.id.txtMailLogin:
                Log.d("<>mail-", "in switch");
                sendEmail();
                break;

        }
    }

    private void sendEmail() {
        Log.d("<>mail-", "send email called");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{txtMail.getText().toString() });
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");

        //need this to prompts email client only
        intent.setType("message/rfc822");
        try {
            // the user can choose the email client
            startActivity(Intent.createChooser(intent, "Choose an email client from..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Login.this, "No email client installed.",
                    Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(Login.this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            openDialCall();
        }
    }

    private void showDialogForgot() {
        // custom dialog
        final Dialog dialog = new Dialog(Login.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.setContentView(R.layout.layout_forgot_password);
        dialog.setTitle(getResources().getString(R.string.reset_password));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog.Builder(Login.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            dialog = new AlertDialog.Builder(context);
        }*/
        // set the custom dialog components - text, image and button
        final TextView text = (TextView) dialog.findViewById(R.id.input_username);
        Button dialogButton = (Button) dialog.findViewById(R.id.button_resetPassword);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (text.getText().length() == 0) {
                    text.setError(getString(R.string.enter_email_id));
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text.getText().toString()).matches()) {
                    //Validation for Invalid Email Address
                    Toast.makeText(Login.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                    edtEmail.setError(getResources().getString(R.string.invalid_email));
//                    edtEmail.setError(null);
                    return;
                }
                String emailText = text.getText().toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIConstants.URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
                JSONObject paramObject = new JSONObject();
                try {
                    paramObject.put("email", emailText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Call<JsonElement> mService = mInterfaceService.forgotPassword(paramObject.toString());
                mService.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            Log.d("<>password-", " response =>" + response.body() + "");
                            JsonElement mPasswordObject = response.body();
                            String status = mPasswordObject.getAsJsonObject().get("status").getAsString();
                            if (status.equals("mail_sent"))
                                Toast.makeText(Login.this, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show();
                            else if (status.equals("not_found"))
                                Toast.makeText(Login.this, getString(R.string.main_not_registered), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(Login.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
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

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(Login.this, R.style.AppCompatAlertDialogStyle);
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

    public void getLoginData() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final Call<JsonElement> mService = mInterfaceService.getLoginData(SharedData.getWebToken(Login.this));
        Log.d("<>login-", " login service ==> " + mService.toString());
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    ArrayList<String> favourites = new ArrayList<>();
                    Log.d("<>log-", " response body for data ==> " + response.body().toString());
                    if (chkRemember.isChecked() != true) {
                        MyApplication.getGlobalData().setUserId(response.body().getAsJsonObject().get("_id").getAsString());
                        SharedData.setUserId(Login.this, response.body().getAsJsonObject().get("_id").getAsString());
                    }else{
                        SharedData.setUserId(Login.this, response.body().getAsJsonObject().get("_id").getAsString());
                        SharedData.setIsRemember(Login.this, true);
                    }
                    if (response.body().getAsJsonObject().has("restaurantFacility")) {
                        JsonArray restaurantFavourite = response.body().getAsJsonObject().get("favouriteRestaurants").getAsJsonArray();
                        Log.d("<>log-", " favourites ===> " + restaurantFavourite.toString());
                        for (int rest = 0; rest < restaurantFavourite.size(); rest++) {
                            favourites.add(restaurantFavourite.get(rest).getAsString());
                            Log.d("<>log-", " favourites ===> " + favourites.toString());
                        }
                    }

                    ArrayList<OUser> user = new ArrayList<>();
                    user.add(new OUser(
                            response.body().getAsJsonObject().get("_id").getAsString(),
                            response.body().getAsJsonObject().get("name").getAsString(),
                            response.body().getAsJsonObject().get("email").getAsString(),
                            response.body().getAsJsonObject().get("mobile_number").getAsString(),
                            response.body().getAsJsonObject().get("role").getAsString(),
                            favourites));
                    Log.d("<>login-", " role is ==> "+response.body().getAsJsonObject().get("role").getAsString());
                    MyApplication.getGlobalData().addUserDataList(user);
                    if (response.body().getAsJsonObject().get("role").getAsString().equals(APIConstants.ROLE_PARAM)) {
                        SharedData.setIsLoggedIn(Login.this, true);
                        sendNotificationToken(response.body().getAsJsonObject().get("_id").getAsString(),
                                response.body().getAsJsonObject().get("role").getAsString());
                        Intent i = new Intent(Login.this, PreLoginMainActivity.class);
                        i.putExtra("name", response.body().getAsJsonObject().get("name").getAsString());
                        i.putExtra("signIn", "true");
                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else {
                        Toast.makeText(Login.this, getResources().getString(R.string.permission_issue), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(Login.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sendNotificationToken(String _id, String role) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //                Log.d("<>date-", "tableId ==> " + tableId + " date of string ---> " + date.toString() + "name ==> " + name);
        String dateTime = dateFormat.format(new Date());
        JsonObject noteData = new JsonObject();
        noteData.addProperty("token", SharedData.getNotificationToken(getApplicationContext()));
        noteData.addProperty("userId", _id);
        noteData.addProperty("userRole", role);
        noteData.addProperty("isLoggedIn", true);
        noteData.addProperty("lastSession", dateTime);
        noteData.addProperty("deviceType", APIConstants.DEVICE_TYPE);

        final Call<JsonElement> mService = mInterfaceService.sendNotificationToken(noteData);
        Log.d("<>login-", " login service ==> " + mService.toString());
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Headers is  ==> " + mService.request().headers().toString());
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response headers ===> " + response.headers());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>push-", " notification result ==> " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(Login.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });

    }
}

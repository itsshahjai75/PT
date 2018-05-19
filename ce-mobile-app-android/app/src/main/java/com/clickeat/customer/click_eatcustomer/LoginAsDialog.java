package com.clickeat.customer.click_eatcustomer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

public class LoginAsDialog extends Activity implements View.OnClickListener {
    private EditText edtEmail, edtPassword;
    private CheckBox chkRemember;
    private TextView txtForgot, txtMail, txtPhone;
    private Button btnLogin, btnSignup;
    private RelativeLayout layoutLogin;

    private ArrayList<String> urlArrayList;
    private ProgressDialog loadingSpinner;
    private Button btnDialogLoginClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login_as_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        this.getWindow().setAttributes(params);
        findIds();
        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
        edtEmail.setTypeface(roboto);
        edtPassword.setTypeface(roboto);

        btnLogin.setOnClickListener(this);
        txtForgot.setOnClickListener(this);
        txtMail.setOnClickListener(this);
        txtPhone.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnDialogLoginClose.setOnClickListener(this);

    }

    public void findIds() {
        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        chkRemember = findViewById(R.id.chkLoginRememberMe);
        txtForgot = findViewById(R.id.txtForgotPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        txtMail = findViewById(R.id.txtMail);
        txtPhone = findViewById(R.id.txtCall);
        btnDialogLoginClose = findViewById(R.id.btnDialogLoginClose);
    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        Log.d("<>btn-", "on clicked");
        switch (view.getId()) {
            case R.id.btnLogin:
                checkValidations();
                break;
            case R.id.btnSignUp:
                Intent i = new Intent(LoginAsDialog.this, Signup.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.txtForgotPwd:
                showDialogForgot();
                break;
            case R.id.btnDialogLoginClose:
                this.finish();
                break;
        }
    }

    /*for checking the validation*/
    public void checkValidations() {
        if (edtEmail.getText().toString().length() == 0) {
            Toast.makeText(LoginAsDialog.this, R.string.mail_error, Toast.LENGTH_LONG).show();
            edtEmail.setError(getResources().getString(R.string.mail_error));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            //Validation for Invalid Email Address
            Toast.makeText(LoginAsDialog.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
            edtEmail.setError(getResources().getString(R.string.invalid_email));
            return;
        } else if (edtPassword.getText().toString().length() == 0) {
            Toast.makeText(LoginAsDialog.this, R.string.password_error
                    , Toast.LENGTH_LONG).show();
            edtPassword.setError(getResources().getString(R.string.password_error));
            return;
        } else {
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
//                .client(client)
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
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Headers is  ==> " + mService.request().headers().toString());
        Log.d("<>login-", " contentType is  ==> " + mService.request().body().contentType() + "");
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response headers ===> " + response.headers().toMultimap().get("Set-Cookie"));
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    String token = null;
                    Log.d("<>log-", " in response of login body" + response.body().toString());
                    JsonElement mLoginObject = response.body();
                    token = "Bearer ";
                    String token1 = mLoginObject.getAsJsonObject().get("token").getAsString();
                    token += token1;
                    SharedData.setWebToken(LoginAsDialog.this, token);
                    if (response.code() == 200) {

                        if (chkRemember.isChecked() == true) {
                            SharedData.setIsRemember(LoginAsDialog.this, true);
                            SharedData.setEmailId(LoginAsDialog.this, email);
                            SharedData.setPassword(LoginAsDialog.this, password);
                        }
                        SharedData.setEmailId(LoginAsDialog.this, email);
                        SharedData.setPassword(LoginAsDialog.this, password);
                        getLoginData();
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Toast.makeText(LoginAsDialog.this, R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(LoginAsDialog.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
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
        final Call<JsonElement> mService = mInterfaceService.getLoginData(SharedData.getWebToken(LoginAsDialog.this));
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
                        SharedData.setUserId(LoginAsDialog.this, response.body().getAsJsonObject().get("_id").getAsString());
                    } else {
                        SharedData.setUserId(LoginAsDialog.this, response.body().getAsJsonObject().get("_id").getAsString());
                        SharedData.setIsRemember(LoginAsDialog.this, true);
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
                    MyApplication.getGlobalData().addUserDataList(user);
                    if (response.body().getAsJsonObject().get("role").getAsString().equals(APIConstants.ROLE_PARAM)) {
                        SharedData.setIsLoggedIn(LoginAsDialog.this, true);
                        sendNotificationToken(response.body().getAsJsonObject().get("_id").getAsString(),
                                response.body().getAsJsonObject().get("role").getAsString());
                        Intent returnIntent = getIntent();
                        returnIntent.putExtra("name", response.body().getAsJsonObject().get("name").getAsString());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginAsDialog.this, getResources().getString(R.string.permission_issue), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(LoginAsDialog.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginAsDialog.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showDialogForgot() {
        // custom dialog
        final Dialog dialog = new Dialog(LoginAsDialog.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.setContentView(R.layout.layout_forgot_password);
        dialog.setTitle(getResources().getString(R.string.reset_password));
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
                    Toast.makeText(LoginAsDialog.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(LoginAsDialog.this, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show();
                            else if (status.equals("not_found"))
                                Toast.makeText(LoginAsDialog.this, getString(R.string.main_not_registered), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(LoginAsDialog.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(1200, 850);
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(LoginAsDialog.this, R.style.AppCompatAlertDialogStyle);
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

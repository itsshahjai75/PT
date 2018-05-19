package com.clickeat.customer.click_eatcustomer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.SignUpModel;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Signup extends AppCompatActivity {
    EditText edtName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtPasswordAgain;
    EditText edtPostCode;
    EditText edtMobile;
    TextView txtRegisterLoginLink;
    Spinner spinnerDate;
    Spinner spinnerMonth;
    Button btnRegister;
    CheckBox checkCondition;

    private HashMap<String, Integer> hashmapMonths;
    private ProgressDialog loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findIds();
        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
        edtEmail.setTypeface(roboto);
        edtName.setTypeface(roboto);
        edtPassword.setTypeface(roboto);
        edtPasswordAgain.setTypeface(roboto);
        edtMobile.setTypeface(roboto);
        edtPostCode.setTypeface(roboto);

        txtRegisterLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logLink = new Intent(Signup.this, Login.class);
                startActivity(logLink);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        List listDates = new ArrayList<>();
        hashmapMonths = new HashMap<>();
        listDates.add("Birth Date");
        for (int i = 1; i <= 31; i++) {
            listDates.add(String.valueOf(i));
        }

        String[] listMonths = getResources().getStringArray(R.array.spinner_items_month);
        Log.d("<>size-", " size of =========> " + listMonths.length);
        for (int i = 0; i < listMonths.length; i++) {
            hashmapMonths.put(listMonths[i].toString(), i);
        }

//        spinnerDate.setPrompt("Birth Date");
        ArrayAdapter adapterDate = new ArrayAdapter(Signup.this, R.layout.layout_singup_spinner, listDates);
        adapterDate.setDropDownViewResource(R.layout.layout_checkbox_spinner);
        spinnerDate.setAdapter(adapterDate);

        ArrayAdapter adapter = new ArrayAdapter(Signup.this, R.layout.layout_singup_spinner, listMonths);
        adapter.setDropDownViewResource(R.layout.layout_checkbox_spinner);
        spinnerMonth.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }

    /*for checking the validation*/
    public void checkValidations() {
        if (edtName.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.name_error, Toast.LENGTH_LONG).show();
            edtName.setError(getResources().getString(R.string.name_error));
            return;
        } else if (edtEmail.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.mail_error, Toast.LENGTH_LONG).show();
            edtEmail.setError(getResources().getString(R.string.mail_error));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            //Validation for Invalid Email Address
//            Toast.makeText(Signup.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
            edtEmail.setError(getResources().getString(R.string.invalid_email));
            return;
        } else if (edtMobile.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.mobile_number
//                    , Toast.LENGTH_LONG).show();
            edtMobile.setError(getResources().getString(R.string.mobile_number));
            return;
        } else if (!isValidMobile(edtMobile.getText().toString())) {
            edtMobile.setError(getResources().getString(R.string.mobile_valid));
            return;
        } else if (edtPostCode.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.postcode_error
//                    , Toast.LENGTH_LONG).show();
            edtPostCode.setError(getResources().getString(R.string.postcode_error));
            return;
        } else if (spinnerDate.getSelectedItemPosition() == 0) {
            Toast.makeText(Signup.this, R.string.birth_date_error
                    , Toast.LENGTH_LONG).show();
        } else if (spinnerMonth.getSelectedItemPosition() == 0) {
            Toast.makeText(Signup.this, R.string.birth_month_error
                    , Toast.LENGTH_LONG).show();
        } else if (edtPassword.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.password_error
//                    , Toast.LENGTH_LONG).show();
            edtPassword.setError(getResources().getString(R.string.password_error));
            return;
        } else if (edtPassword.getText().length() < 3) {
//            Toast.makeText(Signup.this, R.string.password_length
//                    , Toast.LENGTH_LONG).show();
            edtPassword.setError(getResources().getString(R.string.password_length));
            return;
        } else if (edtPasswordAgain.getText().toString().length() == 0) {
//            Toast.makeText(Signup.this, R.string.password_error
//                    , Toast.LENGTH_LONG).show();
            edtPasswordAgain.setError(getResources().getString(R.string.password_error));
            return;
        } else if (!edtPasswordAgain.getText().toString().equals(edtPassword.getText().toString())) {
//            Toast.makeText(Signup.this, R.string.password_not_match
//                    , Toast.LENGTH_LONG).show();
            edtPasswordAgain.setError(getResources().getString(R.string.password_not_match));
            return;
        } else if (!checkCondition.isChecked()) {
            Toast toast = Toast.makeText(Signup.this, "Accept Terms and Condition", Toast.LENGTH_LONG);
            View toastView = toast.getView(); // This'll return the default View of the Toast.

            /* And now you can get the TextView of the default View of the Toast. */
            final Typeface roboto = Typeface.createFromAsset(Signup.this.getAssets(),
                    "fonts/Roboto-Light.ttf");
            TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
            toastMessage.setTextSize(12);
            toastMessage.setTypeface(roboto);
            toastMessage.setTextColor(Signup.this.getResources().getColor(R.color.colorWhite));
            toastMessage.setGravity(Gravity.CENTER);
            toast.show();
        } else {
            //Validate Successfully.
            Log.d("<>login-", " it is in else part");
            String txtEmail = edtEmail.getText().toString();
            String txtPassword = edtPassword.getText().toString();
            String txtName = edtName.getText().toString();
            String mobile = edtMobile.getText().toString();
            String txtMobile = mobile;
            String txtPostcode = edtPostCode.getText().toString();
            Integer txtDate = Integer.valueOf(spinnerDate.getSelectedItem().toString());
            Integer txtMonth = Integer.valueOf(hashmapMonths.get(spinnerMonth.getSelectedItem().toString()));
            signUpProcessWithRetrofit(txtEmail, txtPassword, txtName, txtMobile, txtPostcode, txtDate, txtMonth);
        }
    }

    private void signUpProcessWithRetrofit(final String email, final String password, String name, String mobile,
                                           String postcode, Integer birthDate, Integer birthMonth) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        SignUpModel modelData = new SignUpModel(
                email, password, name, mobile, postcode, birthDate, birthMonth, true, APIConstants.ROLE_PARAM, APIConstants.TOKEN_PARAM_REQUEST_FORM
        );
        Call<JsonElement> mService = mInterfaceService.getSIgnupAuthorisation(modelData);
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of login ");
                    String token = null;
                    Log.d("<>log-", " in response of login ");
                    JsonElement mLoginObject = response.body();
                    Log.d("<>log-", " in response of login " + mLoginObject.toString() + "");
                    token = "Bearer ";
                    String token1 = mLoginObject.getAsJsonObject().get("token").getAsString();
                    Log.d("<>log-", " in response token1 ==>  " + token1 + "");
                    token += token1;
                    Log.d("<>log-", " in response token ==>  " + token + "");
                    SharedData.setWebToken(Signup.this, token);
                    if (response.code() == 200) {
                        getLoginData();
                       /* Intent i = new Intent(Signup.this, PreLoginMainActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();*/
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Toast.makeText(Signup.this, R.string.title_something_wrong, Toast.LENGTH_SHORT).show();
                } else if (response.code() == 422) {
                    Log.d("<>log-", " Unprocessable");
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Toast.makeText(Signup.this, R.string.email_already_registered, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(Signup.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
//                txtPhone.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
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

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(Signup.this, R.style.AppCompatAlertDialogStyle);
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

    private void findIds() {
        edtName = findViewById(R.id.edtSignUpName);
        edtEmail = findViewById(R.id.edtSignUpEmail);
        edtPassword = findViewById(R.id.edtSignUpPassword);
        edtPasswordAgain = findViewById(R.id.edtSignUpPasswordAgain);
        edtPostCode = findViewById(R.id.edtSignUpPostCode);
        edtMobile = findViewById(R.id.edtSignUpMobile);
        txtRegisterLoginLink = findViewById(R.id.txtRegisterLogin);
        spinnerDate = findViewById(R.id.spinnerDate);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        btnRegister = findViewById(R.id.btnRegisterSignup);
        checkCondition = findViewById(R.id.checkCondition);
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
        final Call<JsonElement> mService = mInterfaceService.getLoginData(SharedData.getWebToken(Signup.this));
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
                    MyApplication.getGlobalData().setUserId(response.body().getAsJsonObject().get("_id").getAsString());
                    SharedData.setUserId(Signup.this, response.body().getAsJsonObject().get("_id").getAsString());

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
                    Log.d("<>login-", " role is ==> " + response.body().getAsJsonObject().get("role").getAsString());
                    MyApplication.getGlobalData().addUserDataList(user);
                    if (response.body().getAsJsonObject().get("role").getAsString().equals(APIConstants.ROLE_PARAM)) {
                        SharedData.setIsLoggedIn(Signup.this, true);
                        sendNotificationToken(response.body().getAsJsonObject().get("_id").getAsString(),
                                response.body().getAsJsonObject().get("role").getAsString());
                        Intent i = new Intent(Signup.this, PreLoginMainActivity.class);
                        i.putExtra("name", response.body().getAsJsonObject().get("name").getAsString());
                        i.putExtra("signIn", "true");
                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(Signup.this, getResources().getString(R.string.permission_issue), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(Signup.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
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
                Toast.makeText(Signup.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });

    }
}

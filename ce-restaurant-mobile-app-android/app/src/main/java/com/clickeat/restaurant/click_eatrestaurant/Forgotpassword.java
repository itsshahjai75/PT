package com.clickeat.restaurant.click_eatrestaurant;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Forgotpassword extends AppCompatActivity {


    @BindView(R.id.img_key_frg_Pwd)ImageView img_key_frg_Pwd;
    @BindView(R.id.edtfrgPwdEmail)AutoCompleteTextView edtEmail;
    @BindView(R.id.btnresetPassword) Button btnresetPassword;
    @BindView(R.id.txtcontent) TextView txtcontent;

    boolean isMailSent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.forgot_password).substring(0,getString(R.string.forgot_password).length()-1));

        btnresetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMailSent==true) {
                    onBackPressed();
                    finish();
                }else {
                    if (edtEmail.getText().length() == 0) {
                        edtEmail.setError(getString(R.string.enter_email_id));
                        return;
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
                        //Validation for Invalid Email Address
                        Toast.makeText(Forgotpassword.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                        edtEmail.setError(getResources().getString(R.string.invalid_email));
//                    edtEmail.setError(null);
                        return;
                    }
                    String emailText = edtEmail.getText().toString();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Const.SERVER_URL_API)
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
                                if (status.equals("mail_sent")) {
                                    img_key_frg_Pwd.setImageDrawable(ContextCompat.getDrawable(Forgotpassword.this, R.drawable.ic_right_tick));
                                    btnresetPassword.setText(getString(R.string.back_to_login));
                                    isMailSent = true;
                                    Toast.makeText(Forgotpassword.this, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show();
                                } else if (status.equals("not_found")) {
                                    Toast.makeText(Forgotpassword.this, getString(R.string.main_not_registered), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            call.cancel();
                            Toast.makeText(Forgotpassword.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


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


}

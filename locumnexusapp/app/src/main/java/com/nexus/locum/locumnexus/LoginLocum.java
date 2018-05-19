package com.nexus.locum.locumnexus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.utilities.ConnectionDetector;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_LOGINKEY;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;
import static com.nexus.locum.locumnexus.utilities.Const.getUserDetailsJson;

public class LoginLocum extends AppCompatActivity {

    private static final int MY_PERMISSIONS = 1;
    TextInputLayout TIL_emailid ,TIL_password;
    EditText et_email ,et_password;
    TextView tv_register,tv_forgot,tv_emailID,tv_callnumber;
    Button btn_sigin;
    String str_email,str_password,str_email_forgot;

    final Context context = this;


    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_locum);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        checkAndRequestPermissions();

        TIL_emailid = (TextInputLayout)this.findViewById(R.id.TIL_emailid);
        TIL_password = (TextInputLayout)this.findViewById(R.id.TIL_password);

        et_email = (EditText) this.findViewById(R.id.et_email);
        final String email = et_email.getEditableText().toString().trim();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        et_password = (EditText) this.findViewById(R.id.et_password);

        tv_register = (TextView) this.findViewById(R.id.tv_register);
        tv_register.setText(Html.fromHtml("<font color=\"#134374\">" + "Don't have an account? " + "</font>" + "<u><font color=\"#00A75B\" > Sign Up " + "</font></u>"));
        tv_forgot = (TextView)this.findViewById(R.id.tv_forgot);

        tv_emailID = (TextView)this.findViewById(R.id.tv_emailID);
        tv_callnumber = (TextView)this.findViewById(R.id.tv_callnumber);

        tv_emailID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { tv_emailID.getText().toString() });
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
            }
        });


        tv_callnumber.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tv_callnumber.getText().toString()));
                startActivity(intent);
            }
        });

        btn_sigin = (Button) this.findViewById(R.id.btn_sigin);
        btn_sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_email.getText().length()==0 || et_password.getText().length()==0){
                    if(et_email.getText().length()==0){
                        et_email.setError("Enter Email !");
                    }else{
                        et_password.setError("Enter Password !");
                    }
                }else if(!isEmailValid(et_email.getText().toString())){
                    et_email.setError("Enter Valid Email !");
                }/*else if(!isPasswordValid(et_password.getText().toString())){
                    et_password.setError("Password must be 8-15 character\nhas 1 cap&small alphabet\nhas number\nhas special character.");
                }*/else{
                    str_email= et_email.getText().toString();
                    str_password=et_password.getText().toString();

                    if( new ConnectionDetector(getBaseContext()).isConnectingToInternet() ==true){
                        new LoginLocumAPI().execute(et_email.getText().toString(),et_password.getText().toString());
                    }else{Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),"No Internet Connection !", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });

                        snackbar.show();
                        //Toast.makeText(LoginLocum.this,"No internet connection found",Toast.LENGTH_LONG).show();
                    }

                    /*startActivity(new Intent(LoginLocum.this,SignupLocum.class));
                    finish();*/
                }
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginLocum.this,SignupLocum.class));
                //finish();
            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_forgot_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInputEmal = (EditText) promptsView
                        .findViewById(R.id.et_forgot_password_email);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("submit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        if(!isEmailValid(userInputEmal.getText().toString())){
                                            userInputEmal.setError("Enter Valid Email !");

                                        }else{
                                            str_email_forgot = userInputEmal.getText().toString();
                                            new ForgotPasswordAPI().execute();

                                        }
                                    }
                                });
                        /*.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                    }
                                });*/

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();



            }
        });


    }
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,15}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public boolean isPasswordValid(String password) {
        boolean isValid = false;
        String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,15})";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
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
            progressDialog = ProgressDialog.show(LoginLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject LoginJson = new JsonObject();
                LoginJson.addProperty("email",parametros[0].toString());
                LoginJson.addProperty("password",parametros[1].toString());
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
                Log.i("RES Token---", resUserDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();

                if(rootObj.has("errors")){

                }else if(rootObj.has("message")){
                    Toast.makeText(LoginLocum.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }else{

                    getUserDetailsJson = null;

                    String token = rootObj.get("token").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_PASSWORD,str_password).apply();

                    startActivity(new Intent(LoginLocum.this,NavigationMainDashboardLocum.class));
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



    String resForgetpwd;
    private class ForgotPasswordAPI extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(LoginLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject LoginJson = new JsonObject();
                LoginJson.addProperty("email",str_email_forgot);
                //Log.d("data send--",""+LoginJson.toString());

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"users/sendPasswordMail", LoginJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resForgetpwd=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resForgetpwd;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resForgetpwd---", resForgetpwd);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resForgetpwd).getAsJsonObject();

                if(rootObj.has("errors")){

                }else if(rootObj.has("status")
                        && rootObj.get("status").getAsString().equalsIgnoreCase("not_found")){
                    Toast.makeText(LoginLocum.this,rootObj.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                }else if(rootObj.has("status")
                        && rootObj.get("status").getAsString().equalsIgnoreCase("mail_sent")) {
                    Toast.makeText(LoginLocum.this,rootObj.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    private boolean checkAndRequestPermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        int callPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);

        int storagePermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);



        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,

                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS);
            return false;
        }

        return true;
    }


    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }
}

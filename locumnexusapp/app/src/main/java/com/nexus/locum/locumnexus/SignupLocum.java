package com.nexus.locum.locumnexus;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.fragments.SignupStepOne;
import com.nexus.locum.locumnexus.fragments.SignupStepTwo;
import com.nexus.locum.locumnexus.utilities.ConnectionDetector;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nexus.locum.locumnexus.fragments.SignupStepOne.arrayList_prefixName;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_cnfemail;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_email;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_firstName;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_lastName;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_mobileno;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.rg_gender;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.sp_prefixName;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.spinnerArrayAdapter;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.TIL_catTypeNumber;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.et_catTypeNumber;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.et_confirmPassword;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.et_password;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.isTermsAccepted;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rb_basic;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rb_enterprise;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rb_monthly;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rb_yearly;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rg_plan;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.rg_plantype;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.sp_category;
import static com.nexus.locum.locumnexus.fragments.SignupStepTwo.tv_termscondition;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_LOGINKEY;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

public class SignupLocum extends AppCompatActivity {

    JsonObject EmailCheckJson;
    JsonObject SignupJsonRequest = new JsonObject();
    JsonArray rootArrayCategory,rootArrayUserTitle;

    protected ProgressDialog progressDialog;

    boolean isEmailExist = true;

    public static ArrayList<String> arrayList_category = new ArrayList<String>();
    public static ArrayAdapter<String> spinnerCategoryAdapter;


    Button firstFragment, secondFragment,btn_submit;
    LinearLayout ll_stepssignup;
    TextView tv_alreadyaccount,tv_emailID,tv_callnumber;
    int step_no=1;
    public static String str_prifixname ="",str_firstName ="",str_lastName ="",str_gender ="",str_email ="",str_mobile ="",
            str_plan ="",str_plan_type ="",str_category ="",str_categoryNumber ="",str_TitlecatTypeNumber ="",str_password="";

    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_locum);

        CONST_SHAREDPREFERENCES  = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        spinnerCategoryAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.onlytextview,arrayList_category);
        spinnerCategoryAdapter.setDropDownViewResource(R.layout.onlytextview);

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
        if( new ConnectionDetector(getBaseContext()).isConnectingToInternet() ==true){
            new GetUeserTitleGetCategory().execute();
        }else{Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content),"No Internet Connection !", Snackbar.LENGTH_LONG)
                .setAction("Setting", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });

            snackbar.show();
            //Toast.makeText(SignupLocum.this,"No internet connection found",Toast.LENGTH_LONG).show();
        }



        btn_submit = (Button) findViewById(R.id.btn_submit);

        ll_stepssignup = (LinearLayout)this.findViewById(R.id.ll_stepssignup);

        tv_alreadyaccount = (TextView) findViewById(R.id.tv_alreadyaccount);
        tv_alreadyaccount.setText(Html.fromHtml("<font color=\"#134374\">" + "Already have an account? " + "</font>" + "<u><font color=\"#00A75B\" > Log In " + "</font></u>"));

        tv_alreadyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupLocum.this,LoginLocum.class));
                finish();
            }
        });

        // get the reference of Button's
        firstFragment = (Button) findViewById(R.id.firstFragment);
        secondFragment = (Button) findViewById(R.id.secondFragment);

        loadFragment(new SignupStepOne());


        // perform setOnClickListener event on First Button
        firstFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // load First Fragment
                //loadFragment(new SignupStepOne());
            }
        });

        // perform setOnClickListener event on Second Button
        secondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // load Second Fragment
                //loadFragment(new SignupStepTwo());
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if( new ConnectionDetector(getBaseContext()).isConnectingToInternet() ==true) {
                    if (step_no == 1) {
                        if (et_firstName.getText().length() <= 0 || et_lastName.getText().length() <= 0 ||
                                et_email.getText().length() <= 0 || et_cnfemail.getText().length() <= 0 ||
                                et_mobileno.getText().length() <= 0) {

                            if (et_firstName.getText().length() <= 0) {
                                et_firstName.setError("Enter First Name");
                            }
                            if (et_lastName.getText().length() <= 0) {
                                et_lastName.setError("Enter Last Name");
                            }
                            if (et_email.getText().length() <= 0) {
                                et_email.setError("Email id cannot be blank, enter your valid email address");
                            }
                            if (et_cnfemail.getText().length() <= 0) {
                                et_cnfemail.setError("Email id cannot be blank, enter your valid email address");
                            }
                            if (et_mobileno.getText().length() <= 0) {
                                et_mobileno.setError("Enter your contact number");
                            }

                        } else if (!isEmailValid(et_email.getText().toString())) {
                            et_email.setError("Enter your valid email address");
                        } else if (!et_email.getText().toString().equals(et_cnfemail.getText().toString())) {
                            et_cnfemail.setError("Email id cannot be blank, enter your valid email address");
                        } else if (et_mobileno.getText().length() < 10) {
                            et_mobileno.setError("Enter your 10 Digit contact number");
                        } else if (rg_gender.getCheckedRadioButtonId() == -1) {// no radio buttons are checked
                            Toast.makeText(getBaseContext(), "Select appropriate gender", Toast.LENGTH_LONG).show();
                        } else {

                            EmailCheckJson = new JsonObject();
                            EmailCheckJson.addProperty("email", et_email.getText().toString());
                            new CheckEmailExistAPI().execute();

                        }
                        //firstFragment.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        //secondFragment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    } else if (step_no == 2) {

                        if (et_password.getText().length() <= 0 || et_confirmPassword.getText().length() <= 0) {

                            if (et_password.getText().length() <= 0) {
                                et_password.setError("Enter a valid password");
                            } else if (et_confirmPassword.getText().length() <= 0) {
                                et_confirmPassword.setError("Password mismatch, enter a valid password");
                            }

                        } else if (sp_category.getSelectedItemPosition() == 0) {
                            //et_email.setError("Should select category.");
                            ((TextView) sp_category.getSelectedView()).setError("Should select category");
                        } else if (et_password.getText().length() < 6) {
                        /*et_password.setError("Password must be 8-15 character\nhas 1 cap&small alphabet\nhas number\nhas special character.");*/
                            et_password.setError("Enter a valid password ,must be at least 6 digit");
                        }/*else if(!isPasswordValid(et_password.getText().toString())){
                        et_password.setError("Password must be 8-15 character\nhas 1 cap&small alphabet\nhas number\nhas special character.");
                    }*/ else if (!et_password.getText().toString().equals(et_confirmPassword.getText().toString())) {
                            et_confirmPassword.setError("Password mismatch, enter a valid password");
                        } else if (et_catTypeNumber.getText().length() <= 0) {
                            et_catTypeNumber.setError("Enter " + TIL_catTypeNumber.getHint().toString());
                        } else if (TIL_catTypeNumber.getHint().toString().equalsIgnoreCase("NMC Number")
                                && et_catTypeNumber.getText().length() != 8) {
                            et_catTypeNumber.setError("Enter " + TIL_catTypeNumber.getHint().toString() + " Number !");
                        } else if ((TIL_catTypeNumber.getHint().toString().equalsIgnoreCase("GDC Number")
                                || TIL_catTypeNumber.getHint().toString().equalsIgnoreCase("GMC Number"))
                                && et_catTypeNumber.getText().length() != 7) {
                            et_catTypeNumber.setError("Enter " + TIL_catTypeNumber.getHint().toString() + " Number !");
                        } else if (isTermsAccepted != true) {
                            tv_termscondition.setError("Please read and accept our terms and conditions");
                        } else {

                            int selectedIdPlan = rg_plan.getCheckedRadioButtonId();
                            RadioButton radioButtonPlan = (RadioButton) findViewById(selectedIdPlan);
                            // Toast.makeText(getBaseContext(),radioButtonPlan.getText(),Toast.LENGTH_SHORT).show();

                            int selectedIdPlanType = rg_plantype.getCheckedRadioButtonId();
                            RadioButton radioButtonPlanType = (RadioButton) findViewById(selectedIdPlanType);
                            //Toast.makeText(getBaseContext(),radioButtonPlanType.getText(),Toast.LENGTH_SHORT).show();

                            str_password = et_password.getText().toString();
                            str_category = sp_category.getSelectedItem().toString();

                            Log.e("Selectd Category--", str_category);

                            JsonObject CategoryRequestJson = null;
                            for (int a = 0; a < rootArrayCategory.size(); a++) {
                                if (str_category.equalsIgnoreCase(rootArrayCategory.get(a).getAsJsonObject().get("name").getAsString())) {
                                    CategoryRequestJson = rootArrayCategory.get(a).getAsJsonObject();

                                    //Log.e("got CatfromArray--",rootArrayCategory.get(a).getAsJsonObject().get("name").getAsString());
                                }
                            }


                            str_categoryNumber = et_catTypeNumber.getText().toString();
                            str_TitlecatTypeNumber = TIL_catTypeNumber.getHint().toString();


                            str_plan = "";
                            if (rb_basic.isChecked() == true) {
                                str_plan = rb_basic.getText().toString();
                            } else if (rb_enterprise.isChecked() == true) {
                                str_plan = rb_enterprise.getText().toString();
                            }

                            str_plan_type = "";
                            if (rb_monthly.isChecked() == true) {
                                str_plan_type = rb_monthly.getText().toString();
                            } else if (rb_yearly.isChecked() == true) {
                                str_plan_type = rb_yearly.getText().toString();
                            }

                            JsonArray CategoryJsonArrayReq = new JsonArray();
                            CategoryJsonArrayReq.add(CategoryRequestJson);

                            SignupJsonRequest.addProperty("title", str_prifixname.toLowerCase().trim());
                            SignupJsonRequest.addProperty("fname", str_firstName);
                            SignupJsonRequest.addProperty("gender", str_gender.toLowerCase().trim());
                            SignupJsonRequest.addProperty("lname", str_lastName);
                            SignupJsonRequest.addProperty("email", str_email);
                            SignupJsonRequest.addProperty("serialNumber", "1");
                            SignupJsonRequest.addProperty("password", str_password);
                            SignupJsonRequest.addProperty("currencySign", "gbp");
                            SignupJsonRequest.add("category", CategoryJsonArrayReq);


                            //SignupJsonRequest.addProperty("accountant_status", "open");
                            SignupJsonRequest.addProperty("package", str_plan.trim().toLowerCase());
                            SignupJsonRequest.addProperty("trialPlan", str_plan.trim().toLowerCase() + str_plan_type.trim());
                            SignupJsonRequest.addProperty("mobile", str_mobile);
                            SignupJsonRequest.addProperty("role", "locum");
                            SignupJsonRequest.addProperty("tc", "1");
                            //SignupJsonRequest.addProperty("securityQues",str_password);
                            //SignupJsonRequest.addProperty("securityAns","");
                            SignupJsonRequest.addProperty(str_TitlecatTypeNumber.substring(0, 3).toLowerCase() + "Number", str_categoryNumber);
                            //SignupJsonRequest.addProperty("dueDates","gbp");
                            //SignupJsonRequest.addProperty("financial_years",CategoryJsonArrayReq.toString());

                            if (new ConnectionDetector(getBaseContext()).isConnectingToInternet() == true) {

                                new SignUpLocumAPI().execute();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(findViewById(android.R.id.content), "No Internet Connection !", Snackbar.LENGTH_LONG)
                                        .setAction("Setting", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                            }
                                        });

                                snackbar.show();
                                // Toast.makeText(SignupLocum.this,"No internet connection found",Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }else {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "No Internet Connection !", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });

                    snackbar.show();
                    // Toast.makeText(SignupLocum.this,"No internet connection found",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout,fragment, fragment.getClass().getSimpleName().toString());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName().toString());
        fragmentTransaction.commit(); // save the changes
        Log.d("fragment Name---",fragment.getClass().getSimpleName().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {


        FragmentManager fm = getFragmentManager();
        fm.popBackStack();

        //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0 || step_no==1){
            super.onBackPressed();

            str_prifixname ="";str_firstName ="";str_lastName ="";str_gender ="";str_email ="";str_mobile ="";
                    str_plan ="";str_plan_type ="";str_category ="";str_categoryNumber ="";
                    str_TitlecatTypeNumber ="";str_password="";

            finish();
           }else{

            //loadFragment(new SignupStepOne());
            step_no = 1;
            btn_submit.setText("Next");
            ll_stepssignup.setBackgroundResource(R.drawable.step_one);


            if(et_password.getText().length()>0 || et_confirmPassword.getText().length()>0){

                if(et_password.getText().length()>0){
                    str_password = et_password.getText().toString();
                }
                if(et_confirmPassword.getText().length()>0){
                    str_password = et_password.getText().toString();
                }

            }
            if(sp_category.getSelectedItemPosition()>1){
                //et_email.setError("Should select category.");
                str_category = sp_category.getSelectedItem().toString();            }
            if (et_catTypeNumber.getText().length()>0){
                str_categoryNumber = et_catTypeNumber.getText().toString();
            }

            str_plan ="";
            if(rb_basic.isChecked()==true){
                str_plan = rb_basic.getText().toString();
            }else if(rb_enterprise.isChecked()==true){
                str_plan = rb_enterprise.getText().toString();
            }

            str_plan_type = "";
            if(rb_monthly.isChecked()==true){
                str_plan_type=rb_monthly.getText().toString();
            }else if(rb_yearly.isChecked()==true){
                str_plan_type=rb_yearly.getText().toString();
            }

        }
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



    String resUserTitles,resCategory;
    JsonObject CategoryJson = new JsonObject();

    private class GetUeserTitleGetCategory extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(SignupLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"UserTitles", "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserTitles=responseUSerTitles;

                String responseCategory = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"locum_categorys", "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resCategory=responseCategory;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserTitles;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                 Log.i("RES UsertitlesCategy---", resUserTitles+"\n---------------------\n"+resCategory);
                JsonParser parser = new JsonParser();


                arrayList_prefixName.clear();
                rootArrayUserTitle = parser.parse(resUserTitles).getAsJsonArray();



                for(int a=0; a<rootArrayUserTitle.size();a++){
                    String Title = rootArrayUserTitle.get(a).getAsJsonObject().get("title").getAsString();
                    arrayList_prefixName.add(Title);
                    spinnerArrayAdapter.notifyDataSetChanged();

                }

                Collections.sort(arrayList_prefixName, new Comparator<String>() {
                    @Override
                    public int compare(String item, String t1) {
                        return item.compareToIgnoreCase(t1);
                    }

                });

                arrayList_category.clear();
                arrayList_category.add("Select Category");
                rootArrayCategory = parser.parse(resCategory).getAsJsonArray();

                for(int b=0; b<rootArrayCategory.size();b++){
                    String name = rootArrayCategory.get(b).getAsJsonObject().get("name").getAsString();
                    arrayList_category.add(name);
                    spinnerCategoryAdapter.notifyDataSetChanged();

                    CategoryJson.addProperty("_id", rootArrayCategory.get(b).getAsJsonObject().get("_id").getAsString());
                    CategoryJson.addProperty("name", rootArrayCategory.get(b).getAsJsonObject().get("name").getAsString());
                    CategoryJson.addProperty("__v", rootArrayCategory.get(b).getAsJsonObject().get("__v").getAsString());
                }

                Collections.sort(arrayList_category.subList(1,arrayList_category.size()), new Comparator<String>() {
                    @Override
                    public int compare(String item, String t1) {
                        return item.compareToIgnoreCase(t1);
                    }


                });




            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    String resUserSigup;
    private class SignUpLocumAPI extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(SignupLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

               // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"users", SignupJsonRequest.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserSigup=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserSigup;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.e("RES resUserSigup---", resUserSigup);
                JsonParser parser = new JsonParser();
                JsonObject rootObjsignup = parser.parse(resUserSigup).getAsJsonObject();

                if(rootObjsignup.has("errors")){
                    if(rootObjsignup.get("errors").getAsJsonObject().has("email")){

                        getFragmentManager().popBackStack();
                        step_no=1;

                        et_email.setError(rootObjsignup.get("errors").getAsJsonObject()
                                .get("email").getAsJsonObject().get("message").getAsString());
                        Toast.makeText(getBaseContext(),rootObjsignup.get("errors").getAsJsonObject()
                                .get("email").getAsJsonObject().get("message").getAsString(),Toast.LENGTH_LONG).show();
                        btn_submit.setText("Next");
                    }else if(rootObjsignup.get("errors").getAsJsonObject().has("gdcNumber")){
                        et_catTypeNumber.setError(rootObjsignup.get("errors").getAsJsonObject()
                                .get("gdcNumber").getAsJsonObject().get("message").getAsString());
                    }else if(rootObjsignup.get("errors").getAsJsonObject().has("gmcNumber")){
                        et_catTypeNumber.setError(rootObjsignup.get("errors").getAsJsonObject()
                                .get("gmcNumber").getAsJsonObject().get("message").getAsString());
                    }else if(rootObjsignup.get("errors").getAsJsonObject().has("nmcNumber")){
                        et_catTypeNumber.setError(rootObjsignup.get("errors").getAsJsonObject()
                                .get("nmcNumber").getAsJsonObject().get("message").getAsString());
                    }

                    //loadFragment(new SignupStepOne());
                }else{

                    String token = rootObjsignup.get("token").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,token).apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"yes").apply();
                    CONST_SHAREDPREFERENCES.edit().putString(PREF_IS_PROFILECOMPLETED,"no").apply();
                    startActivity(new Intent(SignupLocum.this,NavigationMainDashboardLocum.class));

                    str_prifixname ="";str_firstName ="";str_lastName ="";str_gender ="";str_email ="";str_mobile ="";
                    str_plan ="";str_plan_type ="";str_category ="";str_categoryNumber ="";
                    str_TitlecatTypeNumber ="";str_password="";

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

    String resEmailChecked;
    private class CheckEmailExistAPI extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(SignupLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"users/validateServerError", EmailCheckJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resEmailChecked= responseUSerTitles;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resEmailChecked;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.e("RES resEmailChecked---", resEmailChecked);
                JsonParser parser = new JsonParser();
                JsonObject rootObjEmailexist = parser.parse(resEmailChecked).getAsJsonObject();

                if(rootObjEmailexist.get("email").getAsBoolean()==false){
                    isEmailExist=false;
                    //loadFragment(new SignupStepOne());
                }else{
                    isEmailExist=true;
                }


                if (isEmailExist == false) {
                    loadFragment(new SignupStepTwo());
                    step_no = 2;
                    btn_submit.setText("Sign Up");
                    ll_stepssignup.setBackgroundResource(R.drawable.step_two);

                    int selectedId = rg_gender.getCheckedRadioButtonId();
                    RadioButton radioGenderButton = (RadioButton) findViewById(selectedId);
//                        Toast.makeText(getBaseContext(),radioGenderButton.getText(),Toast.LENGTH_SHORT).show();

                    str_prifixname = sp_prefixName.getSelectedItem().toString();

                    for (int a = 0; a < rootArrayUserTitle.size(); a++) {
                        if (str_prifixname.equalsIgnoreCase(rootArrayUserTitle.get(a).getAsJsonObject().get("title").getAsString())) {
                            str_prifixname = rootArrayUserTitle.get(a).getAsJsonObject().get("value").getAsString();
                        }
                    }

                    str_firstName = et_firstName.getText().toString();
                    str_lastName = et_lastName.getText().toString();
                    str_email = et_email.getText().toString();
                    str_mobile = et_mobileno.getText().toString();
                    str_gender = radioGenderButton.getText().toString();
                } else {
                    et_email.setError("Email id already exists, enter your valid email id.");
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

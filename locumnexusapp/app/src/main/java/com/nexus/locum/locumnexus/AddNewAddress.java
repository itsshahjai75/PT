package com.nexus.locum.locumnexus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.fragments.HomeBottomNavTabFragment;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nexus.locum.locumnexus.fragments.SignupStepOne.arrayList_prefixName;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.et_email;
import static com.nexus.locum.locumnexus.fragments.SignupStepOne.spinnerArrayAdapter;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_LOGINKEY;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

public class AddNewAddress extends AppCompatActivity {

    @BindView(R.id.TIL_country) TextInputLayout TIL_country;
    @BindView(R.id.TIL_housename) TextInputLayout TIL_housename;
    @BindView(R.id.TIL_housenumber) TextInputLayout TIL_housenumber;
    @BindView(R.id.TIL_addressline1) TextInputLayout TIL_addressline1;
    @BindView(R.id.TIL_addressline2) TextInputLayout TIL_addressline2;
    @BindView(R.id.TIL_addressline3) TextInputLayout TIL_addressline3;
    @BindView(R.id.TIL_town) TextInputLayout TIL_town;
    @BindView(R.id.TIL_city) TextInputLayout TIL_city;
    @BindView(R.id.TIL_county) TextInputLayout TIL_county;

    @BindView(R.id.et_housename) EditText et_housename;
    @BindView(R.id.et_housenumber) EditText et_housenumber;
    @BindView(R.id.et_addressline1) EditText et_addressline1;
    @BindView(R.id.et_addressline2) EditText et_addressline2;
    @BindView(R.id.et_addressline3) EditText et_addressline3;
    @BindView(R.id.et_town) EditText et_town;
    @BindView(R.id.et_city) EditText et_city;
    @BindView(R.id.et_county) EditText et_county;
    @BindView(R.id.et_postcode) EditText et_postcode;


    @BindView(R.id.tv_extra) TextView tv_extra;

    @BindView(R.id.rg_addressType) RadioGroup rg_addressType;

    @BindView(R.id.rb_home) RadioButton rb_home;
    @BindView(R.id.rb_billing) RadioButton rb_billing;


    @BindView(R.id.sp_country) Spinner sp_country;
    @BindView(R.id.btn_save) Button btn_save;

    @BindView(R.id.cb_sameas) CheckBox cb_sameas;

    ArrayList<String> arrayList_country = new ArrayList<String>();
    ArrayAdapter<String> spinnerCountryAdapter;

    String str_country;

    JsonObject UpdateAddressJsonRequest = new JsonObject();

    String houseNumber,houseName,line1 ,line2,line3 ,city,country=null ,zipCode,county;

    JsonObject CurrentAddressJson = new JsonObject();
    JsonObject BillingAddressJson = new JsonObject();
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        ButterKnife.bind( this);

        CONST_SHAREDPREFERENCES  = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Address");

        new GetCountry().execute();

        if(getIntent().hasExtra("edit")) {

            CurrentAddressJson = new JsonParser().parse(getIntent().getStringExtra("currentAddressJsonObj")).getAsJsonObject();
            BillingAddressJson = new JsonParser().parse(getIntent().getStringExtra("billingAddressJsonObj")).getAsJsonObject();

            if(getIntent().getStringExtra("edit").equalsIgnoreCase("home")
                    &&CurrentAddressJson.size()>0){

                rb_billing.setEnabled(false);
                rb_billing.setClickable(false);

                houseNumber = CurrentAddressJson.get("houseNumber").getAsString();
                houseName = CurrentAddressJson.get("houseName").getAsString();
                line1 = CurrentAddressJson.get("line1").getAsString();
                line2 = CurrentAddressJson.get("line2").getAsString();
                line3 =CurrentAddressJson.get("line3").getAsString();
                city = CurrentAddressJson.get("city").getAsString();
                country = CurrentAddressJson.get("country").getAsString();
                zipCode = CurrentAddressJson.get("zipCode").getAsString();
                county = CurrentAddressJson.get("county").getAsString();

            }else if(getIntent().getStringExtra("edit").equalsIgnoreCase("billing")
                    &&BillingAddressJson.size()>0){

                rb_home.setEnabled(false);
                rb_home.setClickable(false);

                houseNumber = BillingAddressJson.get("houseNumber").getAsString();
                houseName = BillingAddressJson.get("houseName").getAsString();
                line1 = BillingAddressJson.get("line1").getAsString();
                line2 = BillingAddressJson.get("line2").getAsString();
                line3 =BillingAddressJson.get("line3").getAsString();
                city = BillingAddressJson.get("city").getAsString();
                country = BillingAddressJson.get("country").getAsString();
                zipCode = BillingAddressJson.get("zipCode").getAsString();
                county = BillingAddressJson.get("county").getAsString();

            }

            et_housename.setText(houseName);
            et_housenumber.setText(houseNumber);
            et_addressline1.setText(line1);
            et_addressline2.setText(line2);
            et_addressline3.setText(line2);
            et_town.setText(line3);
            et_city.setText(city);
            et_county.setText(county);
            et_postcode.setText(zipCode);


            if(getIntent().getStringExtra("edit").equalsIgnoreCase("home")){
                rb_home.setChecked(true);
            }else if(getIntent().getStringExtra("edit").equalsIgnoreCase("billing")){
                rb_billing.setChecked(true);
            }

        }else{
             CurrentAddressJson = new JsonParser().parse(getIntent().getStringExtra("currentAddressJsonObj")).getAsJsonObject();
             BillingAddressJson = new JsonParser().parse(getIntent().getStringExtra("billingAddressJsonObj")).getAsJsonObject();
        }

        spinnerCountryAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.onlytextview,arrayList_country);
        spinnerCountryAdapter.setDropDownViewResource(R.layout.onlytextview);
        sp_country.setAdapter(spinnerCountryAdapter);


        rg_addressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rb_home.isChecked()){
                    cb_sameas.setText("Is the billing address the same as house address ?");

                }else if(rb_billing.isChecked()){
                    cb_sameas.setText("Is the house address the same as billing address ?");
                }
            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {

                str_country = sp_country.getSelectedItem().toString();
                JsonObject requestBodyJson = new JsonObject();
                if(sp_country.getSelectedItemPosition()>0) {

                   // requestBodyJson.addProperty("id", Const.CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID, ""));

                    if(et_addressline1.getText().length()<=0){
                        et_addressline1.setError("Enter Address Line1");
                    }else if(et_city.getText().length()<=0){
                        et_city.setError("Enter Address City");
                    }else if(et_postcode.getText().length()<=0){
                        et_postcode.setError("Enter Address Postcode");

                    }else{

                        if(rb_home.isChecked()) {

                            CurrentAddressJson.addProperty("country", str_country);
                            CurrentAddressJson.addProperty("houseName", et_housename.getText().toString());
                            CurrentAddressJson.addProperty("houseNumber", et_housenumber.getText().toString());
                            CurrentAddressJson.addProperty("line1", et_addressline1.getText().toString());
                            CurrentAddressJson.addProperty("line2", et_addressline2.getText().toString());
                            CurrentAddressJson.addProperty("line3", et_addressline3.getText().toString());
                            CurrentAddressJson.addProperty("town", et_town.getText().toString());
                            CurrentAddressJson.addProperty("city", et_city.getText().toString());
                            CurrentAddressJson.addProperty("county", et_county.getText().toString());
                            CurrentAddressJson.addProperty("zipCode", et_postcode.getText().toString());

                            requestBodyJson.add("currentAddress",CurrentAddressJson);

                            CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                    .get(0).getAsJsonObject().add("currentAddress",CurrentAddressJson);

                            if (cb_sameas.isChecked()) {
                                requestBodyJson.add("billingAddress",CurrentAddressJson);
                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(1).getAsJsonObject().add("billingAddress",CurrentAddressJson);

                            }else{
                                requestBodyJson.add("billingAddress",BillingAddressJson);
                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(1).getAsJsonObject().add("billingAddress",BillingAddressJson);

                            }




                        }else if(rb_billing.isChecked()){

                            requestBodyJson.add("currentAddress",CurrentAddressJson);

                            BillingAddressJson.addProperty("country", str_country);
                            BillingAddressJson.addProperty("houseName", et_housename.getText().toString());
                            BillingAddressJson.addProperty("houseNumber", et_housenumber.getText().toString());
                            BillingAddressJson.addProperty("line1", et_addressline1.getText().toString());
                            BillingAddressJson.addProperty("line2", et_addressline2.getText().toString());
                            BillingAddressJson.addProperty("line3", et_addressline3.getText().toString());
                            BillingAddressJson.addProperty("town", et_town.getText().toString());
                            BillingAddressJson.addProperty("city", et_city.getText().toString());
                            BillingAddressJson.addProperty("county", et_county.getText().toString());
                            BillingAddressJson.addProperty("zipCode", et_postcode.getText().toString());

                            requestBodyJson.add("billingAddress",BillingAddressJson);


                            CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                    .get(1).getAsJsonObject().add("billingAddress",BillingAddressJson);


                            if (cb_sameas.isChecked()) {
                                requestBodyJson.add("currentAddress",BillingAddressJson);
                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(0).getAsJsonObject().add("currentAddress",BillingAddressJson);

                            }else{
                                requestBodyJson.add("currentAddress",CurrentAddressJson);
                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(0).getAsJsonObject().add("currentAddress",CurrentAddressJson);
                            }

                        }


                        UpdateAddressJsonRequest = requestBodyJson;

                        new UpdateAddress().execute();

                        Log.e("< Updated Profile JSON >",CONST_PROFILE_JSON.toString());
                    }


                }else{
                    Toast.makeText(AddNewAddress.this,"Select Country !",Toast.LENGTH_LONG).show();
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

    ProgressDialog progressDialog;

    String resgetcountry;
    JsonObject CountryJson = new JsonObject();
    JsonArray rootArrayCountry = new JsonArray();

    private class GetCountry extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(AddNewAddress.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                String responseUSerTitles = ResponseAPI(getBaseContext(), Const.SERVER_URL_API +"Country_lists", "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resgetcountry=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resgetcountry;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES Country_lists---", resgetcountry);
                JsonParser parser = new JsonParser();

                rootArrayCountry = parser.parse(resgetcountry).getAsJsonArray();


                arrayList_country.clear();
                arrayList_country.add("Select Country");

                for(int b=0; b<rootArrayCountry.size();b++){
                    String name = rootArrayCountry.get(b).getAsJsonObject().get("name").getAsString();
                    arrayList_country.add(name);
                    spinnerCountryAdapter.notifyDataSetChanged();


                    CountryJson.addProperty("_id", rootArrayCountry.get(b).getAsJsonObject().get("_id").getAsString());
                    CountryJson.addProperty("name", rootArrayCountry.get(b).getAsJsonObject().get("name").getAsString());
                    CountryJson.addProperty("value", rootArrayCountry.get(b).getAsJsonObject().get("value").getAsString());
                    CountryJson.addProperty("createdBy", rootArrayCountry.get(b).getAsJsonObject().get("createdBy").getAsString());
                    CountryJson.addProperty("__v", rootArrayCountry.get(b).getAsJsonObject().get("__v").getAsString());
                    //CountryJson.addProperty("updatedBy", rootArrayCountry.get(b).getAsJsonObject().get("updatedBy").getAsString());
                }

                Collections.sort(arrayList_country.subList(1,arrayList_country.size()), new Comparator<String>() {
                    @Override
                    public int compare(String item, String t1) {
                        return item.compareToIgnoreCase(t1);
                    }


                });

                if(country!=null){
                    sp_country.setSelection(((ArrayAdapter)sp_country.getAdapter()).getPosition(country));
                }

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    String resUPdateAddress;
    private class UpdateAddress extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(AddNewAddress.this, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseWithAuthAPI(getBaseContext(),CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/currentAddress/saltvalue", UpdateAddressJsonRequest.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUPdateAddress=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdateAddress;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.e("RES resUPdateAddress---", resUPdateAddress);
                JsonParser parser = new JsonParser();
                JsonObject rootObjsignup = parser.parse(resUPdateAddress).getAsJsonObject();

                if(rootObjsignup.has("errors")){

                    //loadFragment(new SignupStepOne());
                }else{

                  //  startActivity(new Intent(AddNewAddress.this,));

                    new GetUserProfileDetails().execute();

                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    /*static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

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

                final OkHttpClient client = new OkHttpClient();

                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", Const.CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));

                MediaType JSON = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(JSON,getProfileDetails.toString());
                Log.e("request body profdet-",getProfileDetails.toString());


                final Request request = new Request.Builder()
                        .url(Const.SERVER_URL_API+"users/getProfile")
                        .post(body)
                        //.addHeader("Content-Type", "application/json")
                        .addHeader("Authorization","Bearer "+ Const.CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,""))
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        Log.w("failure Response", mMessage);
                        //call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response)
                            throws IOException {

                        Log.i("RES enque Code---", ""+response.code());
                        //
                        if (response.isSuccessful()){
                            try {
                                resUSerProfileDetails=response.body().string();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            progressDialog.dismiss();

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();


                finish();

                PersonalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                progressDialog.dismiss();
            }

        }
    }*/

    static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

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


                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));



                String resUSerProfileDetails = ResponseWithAuthAPI(AddNewAddress.this, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/getProfile", getProfileDetails.toString(),"post");



            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();




            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            PersonalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();

        }
    }



}

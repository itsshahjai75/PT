package com.nexus.locum.locumnexus.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.AddNewAddress;
import com.nexus.locum.locumnexus.NavigationMainDashboardLocum;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.customviews.UserTextView;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;
import static com.nexus.locum.locumnexus.utilities.Const.getUserDetailsJson;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressPersonalProfile extends Fragment {

    boolean allowRefresh;
    @BindView(R.id.tv_addNewAddress) UserTextView tv_addNewAddres;
    @BindView(R.id.tv_savedAddress) TextView tv_savedAddress;
    @BindView(R.id.tvstatic) TextView tvstatic;
    @BindView(R.id.tv_addressLine2) TextView tv_addressLine2;
    @BindView(R.id.tv_town) TextView tv_town;
    @BindView(R.id.tv_Country) TextView tv_Country;
    @BindView(R.id.tv_static) TextView tv_static;
    @BindView(R.id.tv_addressLine2Billing) TextView tv_addressLine2Billing;
    @BindView(R.id.tv_townBilling) TextView tv_townBilling;
    @BindView(R.id.tv_CountryBilling) TextView tv_CountryBilling;
    @BindView(R.id.tv_editHomeAddress) TextView tv_editHomeAddress;
    @BindView(R.id.tv_addressLine1) TextView tv_addressLine1;
    @BindView(R.id.tv_addressLine3) TextView tv_addressLine3;
    @BindView(R.id.tv_city) TextView tv_city;
    @BindView(R.id.tv_postcode) TextView tv_postcode;
    @BindView(R.id.tv_editBillingAddress) TextView tv_editBillingAddress;
    @BindView(R.id.tv_addressLine1Billing) TextView tv_addressLine1Billing;
    @BindView(R.id.tv_addressLine3Billing) TextView tv_addressLine3Billing;
    @BindView(R.id.tv_cityBilling) TextView tv_cityBilling;
    @BindView(R.id.tv_postcodeBilling) TextView tv_postcodeBilling;


    @BindView(R.id.cv_homeaddress) CardView cv_homeaddress;
    @BindView(R.id.cv_billing) CardView cv_billing;

    @BindView(R.id.ll_addressHome) LinearLayout ll_addressHome;
    @BindView(R.id.ll_linesHomes) LinearLayout ll_linesHomes;
    @BindView(R.id.ll_addressBilling) LinearLayout ll_addressBilling;
    @BindView(R.id.ll_linesBilling) LinearLayout ll_linesBilling;

    String houseNumberBilling,houseNameBilling,cityBilling ,line1Billing,line2Billing ,
            line3Billing,countryBilling ,zipCodeBilling,countyBilling;
    String houseNumber,houseName ,line1,line2 ,line3,city ,country,zipCode ,county;

    JsonObject UpdateAddressJsonRequest = new JsonObject();
    JsonObject CurrentAddressJson = new JsonObject();
    JsonObject BillingAddressJson = new JsonObject();
    SharedPreferences CONST_SHAREDPREFERENCES;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_address_personal_profile, container, false);

        ButterKnife.bind(this, fragmentView);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


//        getActivity().getSupportFragmentManager().getFragment(savedInstanceState,getContext().getClass().getSimpleName());

        Log.d("JSON Profile--",CONST_PROFILE_JSON.toString());
        JsonArray profileAddressArray = CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray();

        final JsonObject currentAddressJsonObj = profileAddressArray.get(0).getAsJsonObject().get("currentAddress").getAsJsonObject();
        final JsonObject billingAddressJsonObj = profileAddressArray.get(1).getAsJsonObject().get("billingAddress").getAsJsonObject();

        if(!currentAddressJsonObj.get("line1").isJsonNull()
                && currentAddressJsonObj.get("line1").getAsString().length()>0
                && !billingAddressJsonObj.get("line1").isJsonNull()
                && billingAddressJsonObj.get("line1").getAsString().length()>0){
            tv_addNewAddres.setVisibility(View.GONE);
        }else {
            tv_addNewAddres.setVisibility(View.VISIBLE);
        }


        tv_addNewAddres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddNewAddress.class)
                        .putExtra("currentAddressJsonObj",currentAddressJsonObj.toString())
                        .putExtra("billingAddressJsonObj",billingAddressJsonObj.toString()));
            }
        });

        if(currentAddressJsonObj.size()<=0){
            ll_linesHomes.setVisibility(View.VISIBLE);
            ll_addressHome.setVisibility(View.GONE);
        }else{

            Log.e("cuurent Add---",currentAddressJsonObj.toString());

             houseNumber = currentAddressJsonObj.get("houseNumber").getAsString();
             houseName = currentAddressJsonObj.get("houseName").getAsString();
             line1 = currentAddressJsonObj.get("line1").getAsString();
             line2 = currentAddressJsonObj.get("line2").getAsString();
             line3 = currentAddressJsonObj.get("line3").getAsString();
             city = currentAddressJsonObj.get("city").getAsString();
            country = currentAddressJsonObj.get("country").getAsString();
             zipCode = currentAddressJsonObj.get("zipCode").getAsString();
             county = currentAddressJsonObj.get("county").getAsString();

            ll_linesHomes.setVisibility(View.GONE);
            ll_addressHome.setVisibility(View.VISIBLE);

            if(houseName.length()<=0 || houseNumber.length()<=0) {
                tv_addressLine1.setVisibility(View.GONE);
            }else {
                tv_addressLine1.setText(houseName + "\n" + houseNumber);
            }

            if(line1.length()<=0) {
                tv_addressLine2.setVisibility(View.GONE);
            }else {
                tv_addressLine2.setText(line1);
            }

            if(line2.length()<=0 && line3.length()<=0) {
                tv_addressLine3.setVisibility(View.GONE);
            }else {
                tv_addressLine3.setText(line2+"\n"+line3);
            }

            if(county.length()<=0){
                tv_town.setVisibility(View.GONE);
            }else {
                tv_town.setText(county);
            }
            if(city.length()<=0){
                tv_city.setVisibility(View.GONE);
            }else{
                tv_city.setText(", "+city);
            }

            if(country.length()<=0){
                tv_Country.setVisibility(View.GONE);
            }else{
                tv_Country.setText(", "+country);
            }

            if(zipCode.length()<=0){
                tv_postcode.setVisibility(View.GONE);
            }else{
                tv_postcode.setText("Postcode : "+zipCode);
            }
        }

        if(billingAddressJsonObj.size()<=0){

            ll_linesBilling.setVisibility(View.VISIBLE);
            ll_addressBilling.setVisibility(View.GONE);

        }else{

            houseNumberBilling = billingAddressJsonObj.get("houseNumber").getAsString();
            houseNameBilling = billingAddressJsonObj.get("houseName").getAsString();
            cityBilling = billingAddressJsonObj.get("city").getAsString();
            line1Billing = billingAddressJsonObj.get("line1").getAsString();
            line2Billing = billingAddressJsonObj.get("line2").getAsString();
            line3Billing = billingAddressJsonObj.get("line3").getAsString();
            countryBilling = billingAddressJsonObj.get("country").getAsString();
            zipCodeBilling = billingAddressJsonObj.get("zipCode").getAsString();
            countyBilling = billingAddressJsonObj.get("county").getAsString();


            ll_linesBilling.setVisibility(View.GONE);
            ll_addressBilling.setVisibility(View.VISIBLE);


            tv_addressLine1Billing.setText(houseNameBilling+"\n"+houseNumberBilling);
            tv_addressLine2Billing.setText(line1Billing);
            tv_addressLine3Billing.setText(line2Billing+"\n"+line3Billing);
            tv_townBilling.setText(countyBilling);
            tv_cityBilling.setText(", "+cityBilling);
            tv_CountryBilling.setText(", "+countryBilling);
            tv_postcodeBilling.setText("Postcode : "+zipCodeBilling);

            if(houseNameBilling.length()<=0 || houseNumberBilling.length()<=0) {
                tv_addressLine1Billing.setVisibility(View.GONE);
            }else {
                tv_addressLine1Billing.setText(houseNameBilling + "\n" + houseNumberBilling);
            }

            if(line1Billing.length()<=0) {
                tv_addressLine2Billing.setVisibility(View.GONE);
            }else {
                tv_addressLine2Billing.setText(line1Billing);
            }

            if(line2Billing.length()<=0 && line3Billing.length()<=0) {
                tv_addressLine3Billing.setVisibility(View.GONE);
            }else {
                tv_addressLine3Billing.setText(line2Billing+"\n"+line3Billing);
            }

            if(countyBilling.length()<=0){
                tv_townBilling.setVisibility(View.GONE);
            }else {
                tv_townBilling.setText(countyBilling);
            }

            if(cityBilling.length()<=0){
                tv_cityBilling.setVisibility(View.GONE);
            }else{
                tv_cityBilling.setText(", "+cityBilling);
            }

            if(countryBilling.length()<=0){
                tv_CountryBilling.setVisibility(View.GONE);
            }else{
                tv_CountryBilling.setText(", "+countryBilling);
            }

            if(zipCodeBilling.length()<=0){
                tv_postcodeBilling.setVisibility(View.GONE);
            }else{
                tv_postcodeBilling.setText("Postcode : "+zipCodeBilling);
            }
        }


        tv_editHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(getContext(), tv_editHomeAddress);
                //inflating menu from xml resource
                popup.inflate(R.menu.optionmenu_practices_profile_adapter);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                //handle menu1 click
                                // get prompts.xml view

                                startActivity(new Intent(getContext(), AddNewAddress.class)
                                        .putExtra("edit","home")
                                        .putExtra("currentAddressJsonObj",currentAddressJsonObj.toString())
                                        .putExtra("billingAddressJsonObj",billingAddressJsonObj.toString()));
                                break;
                            case R.id.menu_delete:
                                //handle menu2 click
                                JsonObject requestBodyJson = new JsonObject();


                                //CurrentAddressJson= null;
                                CurrentAddressJson.addProperty("country", "");
                                CurrentAddressJson.addProperty("houseName", "");
                                CurrentAddressJson.addProperty("houseNumber", "");
                                CurrentAddressJson.addProperty("line1", "");
                                CurrentAddressJson.addProperty("line2", "");
                                CurrentAddressJson.addProperty("line3", "");
                                CurrentAddressJson.addProperty("town", "");
                                CurrentAddressJson.addProperty("city", "");
                                CurrentAddressJson.addProperty("county", "");
                                CurrentAddressJson.addProperty("zipCode", "");

                                requestBodyJson.add("currentAddress",CurrentAddressJson);


                                BillingAddressJson = billingAddressJsonObj;
                                requestBodyJson.add("billingAddress",BillingAddressJson);


                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(0).getAsJsonObject().add("currentAddress",CurrentAddressJson);

                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(1).getAsJsonObject().add("billingAddress",BillingAddressJson);

                                UpdateAddressJsonRequest = requestBodyJson;

                                new UpdateAddress().execute();



                                break;
                            case R.id.menu_share:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup

                popup.show();
            }
        });

        tv_editBillingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(getContext(), tv_editBillingAddress);
                //inflating menu from xml resource
                popup.inflate(R.menu.optionmenu_practices_profile_adapter);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                //handle menu1 click
                                // get prompts.xml view

                                startActivity(new Intent(getContext(), AddNewAddress.class)
                                        .putExtra("edit","billing")
                                        .putExtra("currentAddressJsonObj",currentAddressJsonObj.toString())
                                        .putExtra("billingAddressJsonObj",billingAddressJsonObj.toString()));
                                break;
                            case R.id.menu_delete:
                                //handle menu2 click


                                JsonObject requestBodyJson = new JsonObject();

                                CurrentAddressJson = currentAddressJsonObj;

                                requestBodyJson.add("currentAddress",CurrentAddressJson);

                                //BillingAddressJson = null;
                                BillingAddressJson.addProperty("country", "");
                                BillingAddressJson.addProperty("houseName", "");
                                BillingAddressJson.addProperty("houseNumber", "");
                                BillingAddressJson.addProperty("line1", "");
                                BillingAddressJson.addProperty("line2", "");
                                BillingAddressJson.addProperty("line3", "");
                                BillingAddressJson.addProperty("town", "");
                                BillingAddressJson.addProperty("city", "");
                                BillingAddressJson.addProperty("county", "");
                                BillingAddressJson.addProperty("zipCode", "");

                                requestBodyJson.add("billingAddress",BillingAddressJson);

                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(0).getAsJsonObject().add("currentAddress",CurrentAddressJson);

                                CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("Address").getAsJsonArray()
                                        .get(1).getAsJsonObject().add("billingAddress",BillingAddressJson);

                                UpdateAddressJsonRequest = requestBodyJson;

                                new UpdateAddress().execute();


                                break;
                            case R.id.menu_share:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup

                popup.show();
            }
        });

        return fragmentView;
    }

    String resUPdateAddress;
    ProgressDialog progressDialog;
    private class UpdateAddress extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(getContext(), "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseWithAuthAPI(getContext(),CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
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

                    Toast.makeText(getContext(),"Address Deleted.",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

   /* static String resUSerProfileDetails;
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



                String USerProfileDetails = ResponseWithAuthAPI(getContext(), CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/getProfile", getProfileDetails.toString(),"post");
                resUSerProfileDetails =USerProfileDetails;


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

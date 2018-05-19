package com.nexus.locum.locumnexus.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import static com.nexus.locum.locumnexus.PersonalProfileActivity.viewPager;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPersonalProfile extends Fragment  {


    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_Name) TextView tv_Name;
    @BindView(R.id.tv_category) TextView tv_category;
    @BindView(R.id.tv_catTypeNumberTitle) TextView tv_catTypeNumberTitle;
    @BindView(R.id.tv_categoryNumber) TextView tv_categoryNumber;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.tv_preferred_contact) TextView tv_preferred_contact;
    @BindView(R.id.tv_britishcitizen) TextView tv_britishcitizen;
    @BindView(R.id.tv_eligible_uk) TextView tv_eligible_uk;
    @BindView(R.id.tv_confirmlist) TextView tv_confirmlist;
    @BindView(R.id.tv_inquiry) TextView tv_inquiry;


    @BindView(R.id.rb_male) RadioButton rb_male;
    @BindView(R.id.rb_female) RadioButton rb_female;
    @BindView(R.id.rb_other) RadioButton rb_other;
    @BindView(R.id.rb_mobile) RadioButton rb_mobile;
    @BindView(R.id.rb_landline) RadioButton rb_landline;
    @BindView(R.id.rb_yes) RadioButton rb_yes;
    @BindView(R.id.rb_no) RadioButton rb_no;
    @BindView(R.id.rb_britishcitizen_yes) RadioButton rb_britishcitizen_yes;
    @BindView(R.id.rb_britishcitizen_no) RadioButton rb_britishcitizen_no;
    @BindView(R.id.rb_eligible_uk_yes) RadioButton rb_eligible_uk_yes;
    @BindView(R.id.rb_eligible_uk_no) RadioButton rb_eligible_uk_no;
    @BindView(R.id.rb_inquiry_yes) RadioButton rb_inquiry_yes;
    @BindView(R.id.rb_inquiry_no) RadioButton rb_inquiry_no;


    @BindView(R.id.rg_gender) RadioGroup rg_gender;
    @BindView(R.id.rg_confirmlist) RadioGroup rg_confirmlist;
    @BindView(R.id.rg_britishcitizen) RadioGroup rg_britishcitizen;
    @BindView(R.id.rg_eligible_uk) RadioGroup rg_eligible_uk;
    @BindView(R.id.rg_preferedContact) RadioGroup rg_preferedContact;
    @BindView(R.id.rg_inquiry_uk) RadioGroup rg_inquiry_uk;

   /* @BindView(R.id.ll_addressHome) LinearLayout ll_addressHome;
    @BindView(R.id.ll_linesHomes) LinearLayout ll_linesHomes;
    @BindView(R.id.ll_addressBilling) LinearLayout ll_addressBilling;
    @BindView(R.id.ll_linesBilling) LinearLayout ll_linesBilling;*/

    @BindView(R.id.TIL_landlineno) TextInputLayout TIL_landlineno;
    @BindView(R.id.TIL_qualification) TextInputLayout TIL_qualification;
    @BindView(R.id.TIL_inquiry) TextInputLayout TIL_inquiry;
    @BindView(R.id.TIL_NINumber) TextInputLayout TIL_NINumber;
    @BindView(R.id.TIL_Qualification) TextInputLayout TIL_Qualification;



    @BindView(R.id.et_mobileno) EditText et_mobileno;
    @BindView(R.id.et_landlineno) EditText et_landlineno;
    @BindView(R.id.et_inquiry) EditText et_inquiry;
    @BindView(R.id.et_NINumber) EditText et_NINumber;
    @BindView(R.id.et_Qualification) EditText et_Qualification;


    @BindView(R.id.btn_save) Button btn_save;
    @BindView(R.id.btn_next) Button btn_next;


    JsonObject UpdateMainRequestJSON = new JsonObject();
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_main_personal_profile, container, false);
        ButterKnife.bind( this, fragmentView);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        new GetQualification().execute();

        rg_britishcitizen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(rb_britishcitizen_yes.isChecked()){
                    rg_eligible_uk.setVisibility(View.GONE);
                }else if(rb_britishcitizen_no.isChecked()){
                    rg_eligible_uk.setVisibility(View.VISIBLE);
                }

            }
        });


        rg_inquiry_uk.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(rb_inquiry_yes.isChecked()){
                    TIL_inquiry.setVisibility(View.VISIBLE);
                }else if(rb_inquiry_no.isChecked()){
                    TIL_inquiry.setVisibility(View.GONE);
                }

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_mobileno.getText().length()<0){
                    et_mobileno.setError("Enter Mobile Number.");
                }else if(et_NINumber.getText().length()<0){
                    et_NINumber.setError("Enter NI Number.");
                }else if (rb_inquiry_yes.isChecked() && et_inquiry.getText().length()<=0){
                    et_inquiry.setError("Required ! ");
                }else if(et_Qualification.getText().length()<=0){
                    et_Qualification.setError("Enter Qualification .");
                }else{

                    JsonArray addReqQualificationJson = new JsonArray();

                    for(int a=0;a<qualificationJSON.size();a++){

                        String label = qualificationJSON.get(a).getAsJsonObject().get("label").getAsString();

                        String[] containsQualification = et_Qualification.getText().toString().split(",");

                        if(Arrays.asList(containsQualification).contains(label)){

                            addReqQualificationJson.add(qualificationJSON.get(a).getAsJsonObject());

                        }
                    }

                    String str_rb_britishcitizen = null;
                    if(rb_britishcitizen_yes.isChecked()){
                        str_rb_britishcitizen = "true";
                    }else if(rb_britishcitizen_no.isChecked()){
                        str_rb_britishcitizen = "false";
                    }

                    String str_rb_workin_UK=null;
                    if(rb_eligible_uk_yes.isChecked()){
                        str_rb_workin_UK = "true";
                    }else if(rb_eligible_uk_no.isChecked()){
                        str_rb_workin_UK = "false";
                    }



                    JsonObject requestBodyJson = new JsonObject();

                    if(rb_mobile.isChecked()){
                        requestBodyJson.addProperty("contactonmobile","mobile");
                    }else{
                        requestBodyJson.addProperty("contactonmobile","work_mobile");
                    }

                    requestBodyJson.addProperty("mobile",et_mobileno.getText().toString());
                    requestBodyJson.addProperty("work_mobile",et_landlineno.getText().toString());
                    requestBodyJson.addProperty("ni_number",et_NINumber.getText().toString());
                    requestBodyJson.addProperty("BritishCitizen",str_rb_britishcitizen);
                    requestBodyJson.addProperty("AbleToWorkInUk",str_rb_workin_UK);
                    requestBodyJson.addProperty("profileSummary",et_inquiry.getText().toString());
                    requestBodyJson.add("qualifications",addReqQualificationJson);

                    UpdateMainRequestJSON .add("main",requestBodyJson);

                    new UpdateMain().execute();

                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem()==0){
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }else{
                    getActivity().onBackPressed();
                }

            }
        });

        Log.d("JSON Profile--",CONST_PROFILE_JSON.toString());
        JsonArray profileMainArray = CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("main").getAsJsonArray();

        JsonObject MainJsonObj = profileMainArray.get(0).getAsJsonObject();

        String fname = MainJsonObj.get("fname").getAsString();
        String lname = MainJsonObj.get("lname").getAsString();
        String title = MainJsonObj.get("title").getAsString();
        String mobile = MainJsonObj.get("mobile").getAsString();
        String email = MainJsonObj.get("email").getAsString();


        String work_mobile = MainJsonObj.has("work_mobile") && !MainJsonObj.get("work_mobile").isJsonNull()
                ? MainJsonObj.get("work_mobile").getAsString():"";
        String ni_number = MainJsonObj.has("ni_number")
                ? MainJsonObj.get("ni_number").getAsString():"";
        String BritishCitizen = MainJsonObj.has("BritishCitizen")
                ? MainJsonObj.get("BritishCitizen").getAsString():"";
        String AbleToWorkInUk = MainJsonObj.has("AbleToWorkInUk")
                ? MainJsonObj.get("AbleToWorkInUk").getAsString():"";
        String pendingEnquiry = MainJsonObj.has("pendingEnquiry")
                ? MainJsonObj.get("pendingEnquiry").getAsString():"";
        String profileSummary = MainJsonObj.has("profileSummary")
                ? MainJsonObj.get("profileSummary").getAsString():"";
        String contactonmobile  = MainJsonObj.has("contactonmobile")
                ? MainJsonObj.get("contactonmobile").getAsString():"";

        String gender = MainJsonObj.get("gender").getAsString();

        JsonArray category = MainJsonObj.get("category").getAsJsonArray();
        String categoryTitle = category.get(0).getAsJsonObject().get("name").getAsString();

        String category_typeNumberTitle,category_typNumber;

        if(MainJsonObj.has("gdcNumber")){
            category_typeNumberTitle="GDC Number";
            tv_catTypeNumberTitle.setText(category_typeNumberTitle);
            category_typNumber=MainJsonObj.get("gdcNumber").getAsString();
            tv_categoryNumber.setText(" : "+category_typNumber);

        }else if(MainJsonObj.has("gmcNumber")){
            category_typeNumberTitle="GMC Number";
            tv_catTypeNumberTitle.setText(category_typeNumberTitle);
            category_typNumber=MainJsonObj.get("gmcNumber").getAsString();
            tv_categoryNumber.setText(" : "+category_typNumber);

        }else if(MainJsonObj.has("nmcNumber")){
            category_typeNumberTitle="NMC Number";
            tv_catTypeNumberTitle.setText(category_typeNumberTitle);
            category_typNumber=MainJsonObj.get("nmcNumber").getAsString();
            tv_categoryNumber.setText(" : "+category_typNumber);
        }else{
            tv_catTypeNumberTitle.setText("NA");
            tv_categoryNumber.setText("NA");
        }


        JsonArray qualificationArray = MainJsonObj.get("qualifications").getAsJsonArray();
        String qualificationString = "";
        for(int a= 0; a<qualificationArray.size();a++) {
            if (qualificationString.length() <= 0) {
                qualificationString = qualificationArray.get(a).getAsJsonObject().get("label").getAsString();
                }else{
                qualificationString = qualificationString + "," + qualificationArray.get(a).getAsJsonObject().get("label").getAsString();
            }

        }

        et_Qualification.setText(qualificationString);

        tv_title.setText(" : "+title.substring(0,1).toUpperCase()+title.substring(1,title.length()));
        tv_Name.setText(" "+fname+" "+lname);
        tv_category.setText(" : "+categoryTitle);
        tv_email.setText(" : "+email);

        rg_gender.setEnabled(false);

        if(gender.equalsIgnoreCase("Male")){
            rb_male.setChecked(true);
        }else if(gender.equalsIgnoreCase("Feale")){
            rb_female.setChecked(true);
        }else if(gender.equalsIgnoreCase("Other")){
            rb_other.setChecked(true);
        }

        et_mobileno.setText(mobile);
        et_landlineno.setText(work_mobile);
        et_NINumber.setText(ni_number);
        et_inquiry.setText(profileSummary);

        if(BritishCitizen.equalsIgnoreCase("true")){
            rb_britishcitizen_yes.setChecked(true);
        }else{
            rb_britishcitizen_no.setChecked(true);
        }

        if(AbleToWorkInUk.equalsIgnoreCase("true")){
            rb_eligible_uk_yes.setChecked(true);
        }else{
            rb_britishcitizen_no.setChecked(true);
        }

        if(pendingEnquiry.equalsIgnoreCase("true")){
            rb_inquiry_yes.setChecked(true);
        }else{
            rb_inquiry_no.setChecked(true);
        }

        if(contactonmobile.equalsIgnoreCase("mobile")){
            rb_mobile.setChecked(true);
        }else{
            rb_landline.setChecked(true);
        }


        et_Qualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectQualificationDialog();
            }
        });


        return fragmentView;

    }

    ArrayList<CharSequence> selectedQualification;
    String[] qualificationLable;

    protected void showSelectQualificationDialog() {

        selectedQualification = new ArrayList<CharSequence>();

        if(et_Qualification.getText().length()>0 && et_Qualification.getText().toString().contains(",")) {
            //split the string using separator, in this case it is ","
            String[] strValues = et_Qualification.getText().toString().split(",");

		/*
		 * Use asList method of Arrays class to convert Java String array to ArrayList
		 */
            selectedQualification = new ArrayList<CharSequence>(Arrays.asList(strValues));
        }

        qualificationLable = multiSelectedOptions.toArray(new String[multiSelectedOptions.size()]);

        boolean[] checkedQualificaiton = new boolean[qualificationLable.length];

        int count = qualificationLable.length;

        for(int i = 0; i < count; i++) {
            checkedQualificaiton[i] = selectedQualification.contains(qualificationLable[i]);
        }

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if(isChecked)

                    selectedQualification.add(qualificationLable[which]);

                else

                    selectedQualification.remove(qualificationLable[which]);

                onChangeSelectedColours();

            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Select Qualification");
        builder.setMultiChoiceItems(qualificationLable, checkedQualificaiton, coloursDialogListener);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    protected void onChangeSelectedColours() {

        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence qualifications : selectedQualification)

            stringBuilder.append(qualifications + ",");

        et_Qualification.setText(stringBuilder.toString());

    }


    String resUPdateAddress;
    private class UpdateMain extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(AddNewAddress.this, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseWithAuthAPI(getContext(),CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/main/saltvalue", UpdateMainRequestJSON.toString(),"post");
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
                    Toast.makeText(getContext(),"Data saved",Toast.LENGTH_LONG).show();
                    new GetUserProfileDetails().execute();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

    static String resQualificaions;
    final List<String> multiSelectedOptions = new ArrayList<String>();
    public static JsonArray qualificationJSON = new JsonArray();

    public class GetQualification extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
           // progressDialog = ProgressDialog.show(activity, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                final OkHttpClient client = new OkHttpClient();

                final Request request = new Request.Builder()
                        .url(Const.SERVER_URL_API+"educations")
                        .get()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization","Bearer "+ CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,""))
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
                                resQualificaions=response.body().string();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resQualificaions;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES basicDetails---", ""+resQualificaions);
                JsonParser parser = new JsonParser();

                JsonArray rootObj = parser.parse(resQualificaions).getAsJsonArray();
                qualificationJSON = rootObj;

                multiSelectedOptions.clear();
                for (int a=0;a<rootObj.size();a++){

                    String _id = rootObj.get(a).getAsJsonObject().get("_id").getAsString();
                    String label = rootObj.get(a).getAsJsonObject().get("label").getAsString();
                    String id = rootObj.get(a).getAsJsonObject().get("id").getAsString();
                    String createdBy = rootObj.get(a).getAsJsonObject().get("createdBy").getAsString();
                    String __v = rootObj.get(a).getAsJsonObject().get("__v").getAsString();
                    multiSelectedOptions.add(label);

                }


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

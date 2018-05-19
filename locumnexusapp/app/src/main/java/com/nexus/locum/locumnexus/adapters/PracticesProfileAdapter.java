package com.nexus.locum.locumnexus.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.AddPreferedPractices;
import com.nexus.locum.locumnexus.LoginLocum;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.customviews.UserTextView;
import com.nexus.locum.locumnexus.modelPOJO.PracticesModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
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

public class PracticesProfileAdapter extends
        RecyclerView.Adapter<PracticesProfileAdapter.MyViewHolder>  {

    private List<PracticesModel> practiceList;

    private Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;
    /**
     * View holder class
     */
   
    public PracticesProfileAdapter(List<PracticesModel> practiceList, Context mContext) {
        this.practiceList = practiceList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final PracticesModel dataModel = practiceList.get(position);

        holder.tvPracticesName.setText(dataModel.getPractice_name().length()>0
                ?dataModel.getPractice_name():"N/A");
        holder.tvPracticesCode.setText(dataModel.getPractice_code().length()>0
                ?dataModel.getPractice_code():"N/A");

        holder.tv_contactNumber.setText(dataModel.getPracticeJson().has("contact_number")
                && dataModel.getPracticeJson().get("contact_number").getAsString().length()>0
                ?dataModel.getPracticeJson().get("contact_number").getAsString():"N/A");

        holder.tv_contactName.setText(dataModel.getPracticeJson().has("contact_name")
                && dataModel.getPracticeJson().get("contact_name").getAsString().length()>0
                ?dataModel.getPracticeJson().get("contact_name").getAsString():"N/A");

        holder.tvcontactEmail.setText(dataModel.getPracticeJson().has("email")
                && dataModel.getPracticeJson().get("email").getAsString().length()>0
                ?dataModel.getPracticeJson().get("email").getAsString():"N/A");

        holder.tvNHSPSEACode.setText(dataModel.getPracticeJson().has("NHSPS_EA_Code")
                && dataModel.getPracticeJson().get("NHSPS_EA_Code").getAsString().length()>0
                ?dataModel.getPracticeJson().get("NHSPS_EA_Code").getAsString():"N/A");


        holder.tv_preferedMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.tv_preferedMore);
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



                                LayoutInflater li = LayoutInflater.from(mContext);
                                View promptsView = li.inflate(R.layout.dialog_edit_practice_contact, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                final TextView tv_title = (TextView) promptsView.findViewById(R.id.tv_title);

                                final EditText et_practices_contactName = (EditText)promptsView.findViewById(R.id.et_practices_contactName);
                                final EditText et_email = (EditText)promptsView.findViewById(R.id.et_email);
                                final EditText et_contactNumber = (EditText)promptsView.findViewById(R.id.et_contactNumber);
                                final EditText et_NHSPSNumber = (EditText)promptsView.findViewById(R.id.et_NHSPSNumber);

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                tv_title.setText(holder.tvPracticesName.getText().toString());

                                if(holder.tv_contactName.getText().length()>0
                                        && !holder.tv_contactName.getText().toString().equalsIgnoreCase("N/A")){
                                    et_practices_contactName.setText(holder.tv_contactName.getText().toString());
                                }


                                if(holder.tv_contactNumber.getText().length()>0
                                        && !holder.tv_contactNumber.getText().toString().equalsIgnoreCase("N/A")){
                                    et_contactNumber.setText(holder.tv_contactNumber.getText().toString());
                                }

                                if(holder.tvcontactEmail.getText().length()>0
                                        && !holder.tvcontactEmail.getText().toString().equalsIgnoreCase("N/A")){
                                    et_email.setText(holder.tvcontactEmail.getText().toString());
                                }

                                if(holder.tvNHSPSEACode.getText().length()>0
                                        && !holder.tvNHSPSEACode.getText().toString().equalsIgnoreCase("N/A")){
                                    et_NHSPSNumber.setText(holder.tvNHSPSEACode.getText().toString());
                                }

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Save",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        // get user input and set it to result
                                                        // edit text

                                                        JsonObject parmsJson = new JsonObject();
                                                        parmsJson.addProperty("contact_name",et_practices_contactName.getText().toString());
                                                        parmsJson.addProperty("email",et_email.getText().toString());
                                                        parmsJson.addProperty("contact_number",et_contactNumber.getText().toString());
                                                        parmsJson.addProperty("NHSPS_EA_Code",et_NHSPSNumber.getText().toString());

                                                        new UpdatePreferredPractice().execute(dataModel.get_id(),parmsJson);

                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();

                                break;
                            case R.id.menu_delete:
                                //handle menu2 click
                                JsonObject parmsJson = new JsonObject();
                                parmsJson.addProperty("contact_name","");
                                parmsJson.addProperty("email","");
                                parmsJson.addProperty("contact_number","");
                                parmsJson.addProperty("NHSPS_EA_Code","");

                                new UpdatePreferredPractice().execute(dataModel.get_id(),parmsJson);

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

        if(dataModel.isISprefer()){
            holder.switch_prefer.setCheckedTogglePosition(0);
        }else {
            holder.switch_prefer.setCheckedTogglePosition(1);
        }

        holder.switch_prefer.setTogglePadding(2,10,2,10);

        holder.switch_prefer.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                // Write your code ...

                if(position==0){


                    JsonObject parmsJson = new JsonObject();
                    parmsJson.addProperty("practiceId",dataModel.get_id());
                    parmsJson.addProperty("timesheet",false);
                    parmsJson.addProperty("prefer",true);

                    JsonObject practiceFlagJson = new JsonObject();
                    practiceFlagJson.add("practiceFlag", parmsJson);


                    new UpdatePrefferedBlockPreferredPractice().execute(practiceFlagJson);

                }else if(position==1){

                    JsonObject parmsJson = new JsonObject();
                    parmsJson.addProperty("practiceId",dataModel.get_id());
                    parmsJson.addProperty("timesheet","false");
                    parmsJson.addProperty("prefer","false");

                    JsonObject practiceFlagJson = new JsonObject();
                    practiceFlagJson.add("practiceFlag", parmsJson);


                    new UpdatePrefferedBlockPreferredPractice().execute(practiceFlagJson);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return practiceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPracticesName,tvPracticesCode ,tv_preferedMore,tv_contactName ,tvcontactEmail,tv_contactNumber ,tvNHSPSEACode;
        ToggleSwitch switch_prefer;

        public MyViewHolder(View view) {
            super(view);
            tvPracticesName = (TextView) view.findViewById(R.id.tvPracticesName);
            tvPracticesCode = (TextView) view.findViewById(R.id.tvPracticesCode);
            tv_preferedMore = (TextView) view.findViewById(R.id.tv_preferedMore);
            tv_contactName = (TextView) view.findViewById(R.id.tv_contactName);
            tvcontactEmail = (TextView) view.findViewById(R.id.tvcontactEmail);
            tv_contactNumber = (TextView) view.findViewById(R.id.tv_contactNumber);
            tvNHSPSEACode = (TextView) view.findViewById(R.id.tvNHSPSEACode);
            switch_prefer = (ToggleSwitch)view.findViewById(R.id.switch_prefer);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_practices_table, parent, false);
        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        return new MyViewHolder(v);
    }


    String resUPdatePractices;
    // ProgressDialog progressDialog;

    private class UpdatePreferredPractice extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                // Log.d("data send--",""+SignupJsonRequest.toString());
                JsonObject UpdatePracticeJsonRequest = (JsonObject) parametros[1];

                Log.d("Update Preffered=",
                        Const.SERVER_URL_API +"users/addPracticeEntities/"
                                +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/"+parametros[0]);

                String responseUSerTitles = ResponseWithAuthAPI(mContext, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/addPracticeEntities/"
                                +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/"+parametros[0], UpdatePracticeJsonRequest.toString(),"put");



                resUPdatePractices=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdatePractices;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            new GetUserProfileDetails().execute();
            super.onPostExecute(result);



            try{
                Log.e("RES resUpdatepractices", resUPdatePractices);

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

    String resUPdatePReferBlockPractices;
    // ProgressDialog progressDialog;

    private class UpdatePrefferedBlockPreferredPractice extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                // Log.d("data send--",""+SignupJsonRequest.toString());
                JsonObject UpdatePracticeJsonRequest = (JsonObject) parametros[0];

                Log.d("Update Preffered=",
                        Const.SERVER_URL_API +"users/"
                                +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/practiceFlag/saltvalue");

                String responseUSerTitles = ResponseWithAuthAPI(mContext, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"
                                +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/practiceFlag/saltvalue", UpdatePracticeJsonRequest.toString(),"post");



                resUPdatePReferBlockPractices=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdatePReferBlockPractices;
        }


        @SuppressLint("LongLogTag")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            //new GetUserProfileDetails().execute();

            try{
                Log.e("RES resUPdatePReferBlockPractices", resUPdatePReferBlockPractices);

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

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



                String USerProfileDetails = ResponseWithAuthAPI(mContext, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
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

            ProfessionalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();
            notifyDataSetChanged();

        }
    }


}
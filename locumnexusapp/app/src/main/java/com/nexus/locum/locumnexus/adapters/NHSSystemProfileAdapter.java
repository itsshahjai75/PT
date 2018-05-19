package com.nexus.locum.locumnexus.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.modelPOJO.NHSSystemModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class NHSSystemProfileAdapter extends
        RecyclerView.Adapter<NHSSystemProfileAdapter.MyViewHolder>  {

    private List<NHSSystemModel> NHSSystemsList;

    private Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;

    /**
     * View holder class
     */

    public NHSSystemProfileAdapter(ArrayList<NHSSystemModel> NHSSystemsList, Context mContext) {
        this.NHSSystemsList = NHSSystemsList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final NHSSystemModel dataModel = NHSSystemsList.get(position);
        holder.tvNHSTitle.setText(dataModel.getLabel());



        if(dataModel.getJsonObj().has("level")) {
            Log.d("json get",dataModel.getJsonObj().get("level").getAsString());

            if (dataModel.getJsonObj().get("level").getAsString().equalsIgnoreCase("basic")) {
                holder.rb_basic.setChecked(true);
            } else if (dataModel.getJsonObj().get("level").getAsString().equalsIgnoreCase("intermediate")) {
                holder.rb_Intermediate.setChecked(true);
            } else if (dataModel.getJsonObj().get("level").getAsString().equalsIgnoreCase("advanced")) {
                holder.rb_Advanced.setChecked(true);
            }
        }


        holder.rg_NHSSystem_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int selectedId = holder.rg_NHSSystem_group.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton)group.findViewById(selectedId);
                String type = radioButton.getText().toString();

                JsonArray array = new JsonArray();

                for(int a=0;a<NHSSystemsList.size();a++){
                    if(NHSSystemsList.get(a).getLabel().equalsIgnoreCase(holder.tvNHSTitle.getText().toString())){
                        NHSSystemsList.get(a).getJsonObj().addProperty("level",type.toLowerCase().toString());
                        break;
                    }
                }

                for(int b=0;b<NHSSystemsList.size();b++) {
                    array.add(NHSSystemsList.get(b).getJsonObj());
                }
                Log.e("all NHS modified",""+NHSSystemsList.toArray().toString());
                new AddNHSsystem().execute(array);

            }
        });


    }

    @Override
    public int getItemCount() {
        return NHSSystemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNHSTitle;
        RadioGroup rg_NHSSystem_group;
        RadioButton rb_basic ,rb_Intermediate,rb_Advanced;

        public MyViewHolder(View view) {
            super(view);
            tvNHSTitle = (TextView) view.findViewById(R.id.tvNHSTitle);
            rg_NHSSystem_group = (RadioGroup)view.findViewById(R.id.rg_NHSSystem_group);
            rb_basic = (RadioButton) view.findViewById(R.id.rb_basic);
            rb_Intermediate = (RadioButton) view.findViewById(R.id.rb_Intermediate);
            rb_Advanced = (RadioButton) view.findViewById(R.id.rb_Advanced);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_nhs_sytem_type, parent, false);

        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        return new MyViewHolder(view);
    }


    String resUPdateNHSSystem;
    // ProgressDialog progressDialog;
    private class AddNHSsystem extends AsyncTask<Object, Void, String> {

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
                JsonObject UpdatePracticeJsonRequest = new JsonObject();
                UpdatePracticeJsonRequest.add("nhsSys", (JsonElement) parametros[0]);

                String responseUSerTitles = ResponseWithAuthAPI(mContext,CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/nhsSys/saltvalue", UpdatePracticeJsonRequest.toString(),"post");

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUPdateNHSSystem=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdateNHSSystem;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            new GetUserProfileDetails().execute();

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
            //notifyDataSetChanged();

        }
    }
}
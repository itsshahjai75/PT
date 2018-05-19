package com.nexus.locum.locumnexus.adapters;
import android.app.ProgressDialog;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.AddNewAddress;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.modelPOJO.PracticesModel;
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

public class AddPreferedPracticesAdapter extends RecyclerView.Adapter<AddPreferedPracticesAdapter.ViewHolder> implements Filterable {

    private ArrayList<PracticesModel> mArrayList;
    private ArrayList<PracticesModel> mFilteredList;
    Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;

    public AddPreferedPracticesAdapter(ArrayList<PracticesModel> arrayList, Context mContext) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.mContext = mContext;
    }

    @Override
    public AddPreferedPracticesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_prefer_practice_table, viewGroup, false);

        CONST_SHAREDPREFERENCES = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddPreferedPracticesAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.tvPracticesName.setText(mFilteredList.get(i).getPractice_name());
        viewHolder.tvPracticesCode.setText(mFilteredList.get(i).getPractice_code());

        JsonArray practiceJsonArray = Const.CONST_PROFILE_JSON.get("professional").getAsJsonObject().get("practices").getAsJsonArray();

        if(practiceJsonArray.size()>0) {
            for (int a = 0; a < practiceJsonArray.size(); a++) {
                String practice_name = practiceJsonArray.get(a).getAsJsonObject().get("practice_name").getAsString();
                if(practice_name.equalsIgnoreCase(mFilteredList.get(i).getContact_name())){
                    mFilteredList.get(i).setISprefer(true);
                    viewHolder.tv_prefere.setBackgroundResource(R.drawable.rounded_corner_primary_filled);
                    viewHolder.tv_prefere.setTextColor(mContext.getResources().getColor(R.color.whiteApp));
                    viewHolder.tv_prefere.setEnabled(false);
                    viewHolder.tv_prefere.setClickable(false);
                }
            }
        }

        viewHolder.tv_prefere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mFilteredList.get(i).isISprefer()){
                    mFilteredList.get(i).setISprefer(true);
                    viewHolder.tv_prefere.setBackgroundResource(R.drawable.rounded_corner_primary_filled);
                    viewHolder.tv_prefere.setTextColor(mContext.getResources().getColor(R.color.whiteApp));
                    new AddPreferedPractices().execute(mFilteredList.get(i).getPracticeJson());

                    notifyDataSetChanged();

                }else{
                    mFilteredList.get(i).setISprefer(false);
                    viewHolder.tv_prefere.setBackgroundResource(R.drawable.rounded_corner_primary_borde);
                    viewHolder.tv_prefere.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<PracticesModel> filteredList = new ArrayList<>();

                    for (PracticesModel androidVersion : mArrayList) {

                        if (androidVersion.getPractice_name().contains(charString)
                                || androidVersion.getPractice_name().contains(charString)
                                || androidVersion.getPractice_code().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;

//=====================================================================================

                /*String filterString = charSequence.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<PracticesModel> list = mArrayListNHSSyste;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();*/


                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<PracticesModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_prefere,tvPracticesName,tvPracticesCode;
        public ViewHolder(View view) {
            super(view);

             tv_prefere = (TextView) view.findViewById(R.id.tv_prefere);
             tvPracticesName = (TextView) view.findViewById(R.id.tvPracticesName);
             tvPracticesCode = (TextView) view.findViewById(R.id.tvPracticesCode);

        }
    }


    String resUPdatePractices;
   // ProgressDialog progressDialog;

    private class AddPreferedPractices extends AsyncTask<Object, Void, String> {

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
                JsonArray arrayJsonPractices = new JsonArray();

                JsonObject preferedPracticeJson = (JsonObject) new JsonParser().parse(parametros[0].toString());
                preferedPracticeJson.addProperty("prefer","true");
                preferedPracticeJson.addProperty("timesheet","true");
                preferedPracticeJson.addProperty("edit","false");

                arrayJsonPractices.add(preferedPracticeJson);

                UpdatePracticeJsonRequest.add("practices",arrayJsonPractices);

                String responseUSerTitles = ResponseWithAuthAPI(mContext,CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/practices/saltvalue", UpdatePracticeJsonRequest.toString(),"post");
               
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
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


/*
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


                ProfessionalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();


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
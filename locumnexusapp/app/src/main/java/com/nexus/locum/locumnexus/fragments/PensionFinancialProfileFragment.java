package com.nexus.locum.locumnexus.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.FinancialProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.adapters.NHSSystemProfileAdapter;
import com.nexus.locum.locumnexus.adapters.PensionFinanceProfileAdapter;
import com.nexus.locum.locumnexus.modelPOJO.NHSSystemModel;
import com.nexus.locum.locumnexus.modelPOJO.PensionFinanceProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;
import com.nexus.locum.locumnexus.utilities.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.adapters.PensionFinanceProfileAdapter.pensionRateList;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class PensionFinancialProfileFragment extends Fragment {

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    EditText et_dob,et_nhsoscode,et_areateam,et_hostlhbNo;
    RecyclerView recycler_view_pensionrate;
    Button btn_save;

    private ArrayList<PensionFinanceProfileModel> mArrayList;
    private PensionFinanceProfileAdapter mAdapter;
    SharedPreferences CONST_SHAREDPREFERENCES;
    public String rateSelectedformProfile = "";

    public PensionFinancialProfileFragment() {
        // Required empty public constructor
    }


    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_pension_financial_profile, container, false);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        et_dob = (EditText)convertView.findViewById(R.id.et_dob);
        et_nhsoscode = (EditText)convertView.findViewById(R.id.et_nhsoscode);
        et_areateam = (EditText)convertView.findViewById(R.id.et_areateam);
        et_hostlhbNo = (EditText)convertView.findViewById(R.id.et_hostlhbNo);
        recycler_view_pensionrate = (RecyclerView) convertView.findViewById(R.id.recycler_view_pensionrate);

        btn_save = (Button) convertView.findViewById(R.id.btn_save);

        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String selectedDate  = Utils.convertDateStringToString(
                                        dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                                        ,"dd-MM-yyyy","dd/MMM/yyyy");
                                et_dob.setText(selectedDate);

                            }
                        }, mYear, mMonth, mDay);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -20);
                Date newDate = cal.getTime();
                datePickerDialog.getDatePicker().setMaxDate(newDate.getTime());
                //datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()-(1000*60*60*24*365*10));
                datePickerDialog.show();
            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_pensionrate.hasFixedSize();
        recycler_view_pensionrate.setItemAnimator(new DefaultItemAnimator());
        recycler_view_pensionrate.setLayoutManager(llm);



        mArrayList = new ArrayList<>();
        mAdapter = new PensionFinanceProfileAdapter(mArrayList,getContext());
        recycler_view_pensionrate.setAdapter(mAdapter);



        JsonObject profileJSON = Const.CONST_PROFILE_JSON;

        JsonObject pensionFinanceJSONObj = profileJSON.get("financial").getAsJsonObject().get("tiredContriPensionRates").getAsJsonObject();

        if(pensionFinanceJSONObj.get("tiredContriPensionRate").getAsJsonObject().size()>0){

            JsonObject tiredContriPensionRate = pensionFinanceJSONObj.get("tiredContriPensionRate").getAsJsonObject();
            Log.e("tiredContriPensionRatetiredContriPensionRate---",tiredContriPensionRate.toString());

            String  __v = Integer.toString(tiredContriPensionRate.get("__v").getAsInt());
            String percentage = Integer.toString(tiredContriPensionRate.get("percentage").getAsInt());

            String toAmount = Integer.toString(tiredContriPensionRate.get("toAmount").getAsInt());
            String fromAmount = Integer.toString(tiredContriPensionRate.get("fromAmount").getAsInt());
            String _id = tiredContriPensionRate.get("_id").getAsString();
            rateSelectedformProfile = percentage;

        }

        if(pensionFinanceJSONObj.has("dateofbirth") ){
            String dateofbirth = pensionFinanceJSONObj.get("dateofbirth").getAsString();
            dateofbirth= Utils.convertDateStringToString(dateofbirth,
                    "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'","dd/MMM/yyyy");
            et_dob.setText(dateofbirth);
        }
        if(pensionFinanceJSONObj.has("NHSPSRefNumber")){
            String NHSPSRefNumber = pensionFinanceJSONObj.get("NHSPSRefNumber").getAsString();
            et_nhsoscode.setText(NHSPSRefNumber);
        }
        if(pensionFinanceJSONObj.has("hostareateam")){
            String hostareateam = pensionFinanceJSONObj.get("hostareateam").getAsString();
            et_areateam.setText(hostareateam);
        }
        if(pensionFinanceJSONObj.has("hostlhbrefnumber")){
            String hostlhbrefnumber = pensionFinanceJSONObj.get("hostlhbrefnumber").getAsString();
            et_hostlhbNo.setText(hostlhbrefnumber);
        }


        new GetPenionRate().execute();



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JsonObject pensionObj  = new JsonObject();

                String  parseDateformate = "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";
                String cureentformateDate  = "dd/MMM/yyyy";

                if(et_dob.getText().length()>0){
                    String getDate =  Utils.convertDateStringToString(et_dob.getText().toString(),cureentformateDate,parseDateformate);
                    pensionObj.addProperty("dateofbirth",getDate);

                }

                if(et_nhsoscode.getText().length()>0){
                    pensionObj.addProperty("NHSPSRefNumber",et_nhsoscode.getText().toString());
                }

                if(et_areateam.getText().length()>0){
                    pensionObj.addProperty("hostareateam",et_areateam.getText().toString());
                }

                if(et_hostlhbNo.getText().length()>0){
                    pensionObj.addProperty("hostlhbrefnumber",et_hostlhbNo.getText().toString());
                }

                for(int a=0;a<pensionRateList.size();a++){

                    if(pensionRateList.get(a).isIschecked()){
                        pensionObj.add("tiredContriPensionRate",pensionRateList.get(a).getMainObj());

                        break;
                    }

                }

                new AddPensionRate().execute(pensionObj);
                Log.e("Sent Obj to pension API",pensionObj.toString());


            }
        });


        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());


        recycler_view_pensionrate.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0
                                : mRecyclerView.getChildAt(0).getTop();

                //sr_awaiting.setEnabled(topRowVerticalPosition >= 0);

                //=========================================================================================================
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    //onHide();

                    btn_save.animate().translationY(btn_save.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    //onShow();
                    btn_save.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

                    controlsVisible = true;
                    scrolledDistance = 0;


                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }

                //======================================================================================================

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                   /* if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            textView_loadmore.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                            textView_loadmore.setVisibility(View.VISIBLE);
                        }
                    }*/

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount /*&& (totalItemCount - visibleItemCount) <= (pastVisiblesItems + visibleThreshold)*/) {
                        // End has been reached
                        // Do something

                       /* if(loading_data!=true) {
                            if(int_from==0){
                                int_from=int_from+11;
                                int_to=int_to+10;
                            }else{
                                int_from=int_from+10;
                                int_to=int_to+10;
                            }
                            if(is_allDataLoaded==false){
                                new GetVenuesListDefault().execute();
                            }
                        }*/
                    }
                }

                //=======================================================================================================
            }

            @Override
            public void onScrollStateChanged(RecyclerView mRecyclerView, int newState) {
                super.onScrollStateChanged(mRecyclerView, newState);
            }
        });



        return convertView;
    }

    static String resGetpentionRates;
    ProgressDialog progressDialog;
    public class GetPenionRate extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(getContext(), "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                String responseUSerNHSsystem = ResponseWithAuthAPI(getContext(),CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"tiered_contribution_rates", "","get");

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGetpentionRates=responseUSerNHSsystem;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGetpentionRates;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            progressDialog.dismiss();

            try{
                Log.i("RES resGetPension---", ""+resGetpentionRates);


                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resGetpentionRates).getAsJsonArray();


                mArrayList.clear();

                mArrayList.add(new PensionFinanceProfileModel("Total Pensionable Income",
                        "",
                        "Contribution",false,null));
                mArrayList.add(new PensionFinanceProfileModel("From ( "+getString(R.string.pound_currency)+" )",
                        "To ( "+getString(R.string.pound_currency)+" )",
                        " Rate %",false,null));


                if(rootObjArray.size()>0) {

                    for (int a = 0; a < rootObjArray.size(); a++) {

                        String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                        String fromAmount = rootObjArray.get(a).getAsJsonObject().get("fromAmount").getAsString();
                        String toAmount = rootObjArray.get(a).getAsJsonObject().get("toAmount").getAsString();

                        String percentage = rootObjArray.get(a).getAsJsonObject().has("percentage")
                                ? rootObjArray.get(a).getAsJsonObject().get("percentage").getAsString() : "";
                        String __v = rootObjArray.get(a).getAsJsonObject().has("__v")
                                ? rootObjArray.get(a).getAsJsonObject().get("__v").getAsString() : "";

                        if(percentage.equalsIgnoreCase(rateSelectedformProfile)){

                            mArrayList.add(new PensionFinanceProfileModel(fromAmount,toAmount,
                                    percentage,true,rootObjArray.get(a).getAsJsonObject()));
                            //mAdapter.notifyDataSetChanged();

                        }else{
                            mArrayList.add(new PensionFinanceProfileModel(fromAmount,toAmount,
                                    percentage,false,rootObjArray.get(a).getAsJsonObject()));
                            //mAdapter.notifyDataSetChanged();

                        }

                        mAdapter.notifyDataSetChanged();

                    }

                }

                Collections.sort(mArrayList.subList(2,mArrayList.size()),
                        new Comparator<PensionFinanceProfileModel>() {

                    @Override
                    public int compare(PensionFinanceProfileModel lhs, PensionFinanceProfileModel rhs) {
                        // TODO Auto-generated method stub

                        return Float.valueOf(lhs.getRate()).compareTo(Float.valueOf(rhs.getRate()));
                    }
                });



            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



        }
    }

    String resUpdatePensionRate;
    // ProgressDialog progressDialog;
    private class AddPensionRate extends AsyncTask<Object, Void, String> {

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
                JsonObject updatePensionRateJSON = new JsonObject();
                updatePensionRateJSON.add("tiredContriPensionRates", (JsonElement) parametros[0]);

                String responseUSerTitles = ResponseWithAuthAPI(getContext(),
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/tiredContriPensionRates/saltvalue", updatePensionRateJSON.toString(),"post");

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdatePensionRate=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUpdatePensionRate;
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
           // new GetPenionRate().execute();

            FinancialProfileActivity.viewPager.getAdapter().notifyDataSetChanged();
            //notifyDataSetChanged();

        }
    }

}

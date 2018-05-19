package com.nexus.locum.locumnexus.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.FinancialProfileActivity;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.SignupLocum;
import com.nexus.locum.locumnexus.adapters.RatesFinancialProfileAdapter;
import com.nexus.locum.locumnexus.adapters.RatesFinancialProfileAdapter;
import com.nexus.locum.locumnexus.modelPOJO.RatesFinancialProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateFinancialProfileFragment extends Fragment {

    @BindView(R.id.recycler_view_addRate)
    RecyclerView recycler_view_addpreferpractices;

    private ArrayList<RatesFinancialProfileModel> mArrayList;
    private RatesFinancialProfileAdapter mAdapter;
    FloatingActionButton fab_add_Rate;
    SharedPreferences CONST_SHAREDPREFERENCES;
    public RateFinancialProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_rate_financial_profile, container, false);

        ButterKnife.bind( this, convertView);
        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        new GetRateTypes().execute();

       spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.onlytextview,arrayList_prefixName);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.onlytextview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_addpreferpractices.hasFixedSize();
        recycler_view_addpreferpractices.setItemAnimator(new DefaultItemAnimator());
        recycler_view_addpreferpractices.setLayoutManager(llm);



        mArrayList = new ArrayList<>();
        mAdapter = new RatesFinancialProfileAdapter(mArrayList,getContext());
        recycler_view_addpreferpractices.setAdapter(mAdapter);

        //new GetNHSSytemDefault().execute();

        fab_add_Rate = (FloatingActionButton)convertView.findViewById(R.id.fab_add_Rate);
        fab_add_Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectQualificationDialog();
            }
        });

        JsonObject financialJSON = CONST_PROFILE_JSON.get("financial").getAsJsonObject();
        JsonArray rates = financialJSON.get("rates").getAsJsonArray();

        for (int a=0;a<rates.size();a++){

            String rateType = rates.get(a).getAsJsonObject().get("rateType").getAsString();
            String amount = rates.get(a).getAsJsonObject().get("amount").getAsString();


            mArrayList.add(new RatesFinancialProfileModel(rateType,amount,rates.get(a).getAsJsonObject()));
            mAdapter.notifyDataSetChanged();
        }



        return convertView;

    }


    public static ArrayList<String> arrayList_prefixName = new ArrayList<String>();
    public static ArrayAdapter<String> spinnerArrayAdapter;

    protected void showSelectQualificationDialog() {


        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        //alert.setTitle("Add Rate");
        // this is set the view from XML inside AlertDialog
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_rate_add_profile, null);
        alert.setView(dialogView);

        final EditText et_amount = (EditText) dialogView.findViewById(R.id.et_amount);
        final Spinner sp_rateTypes = (Spinner) dialogView.findViewById(R.id.sp_rateTypes);


        sp_rateTypes.setAdapter(spinnerArrayAdapter);

        Button btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        Button btn_clear = (Button) dialogView.findViewById(R.id.btn_clear);

        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sp_rateTypes.getSelectedItemPosition()>0) {
                    JsonObject requestRate = new JsonObject();
                    requestRate.addProperty("rateType", sp_rateTypes.getSelectedItem().toString());
                    requestRate.addProperty("amount", et_amount.getText().toString());
                    requestRate.addProperty("isDelete", false);

                    JsonObject ReqMain = new JsonObject();

                    ReqMain.add("sessionRates",requestRate);

                    new AddRateType().execute(ReqMain);

                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(),"Please select rate type.",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSelectQualificationDialog();
            }
        });

        TextView tvClose = (TextView)dialogView.findViewById(R.id.tvClose);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    String resUserTitles,resCategory;
    JsonArray rootArrayRateType = new JsonArray();

    private class GetRateTypes extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(SignupLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                String responseUSerTitles = ResponseAPI(getContext(),Const.SERVER_URL_API +"rate_types", "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserTitles=responseUSerTitles;


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
                Log.i("RES rate_types---", resUserTitles);
                JsonParser parser = new JsonParser();


                arrayList_prefixName.clear();
                rootArrayRateType = parser.parse(resUserTitles).getAsJsonArray();


                arrayList_prefixName.clear();
                arrayList_prefixName.add("Select Rate Type");

                for(int a=0; a<rootArrayRateType.size();a++){
                    String _id = rootArrayRateType.get(a).getAsJsonObject().get("_id").getAsString();
                            String name = rootArrayRateType.get(a).getAsJsonObject().get("name").getAsString();
                    String value = rootArrayRateType.get(a).getAsJsonObject().get("value").getAsString();
                            String createdBy = rootArrayRateType.get(a).getAsJsonObject().get("createdBy").getAsString();
                    String createdDate = rootArrayRateType.get(a).getAsJsonObject().get("createdDate").getAsString();
                            String __v = rootArrayRateType.get(a).getAsJsonObject().get("__v").getAsString();

                    arrayList_prefixName.add(name);
                    spinnerArrayAdapter.notifyDataSetChanged();

                }

                Collections.sort(arrayList_prefixName.subList(1,arrayList_prefixName.size()), new Comparator<String>() {
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

        }
    }


    String resUPdateAddress;
    ProgressDialog progressDialog;
    private class AddRateType extends AsyncTask<Object, Void, String> {

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
                                +"/sessionRates/saltvalue", parametros[0].toString(),"post");
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

                    Toast.makeText(getContext(),"Rate Added.",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
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

            FinancialProfileActivity.viewPager.getAdapter().notifyDataSetChanged();

        }
    }


}
package com.nexus.locum.locumnexus.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.adapters.FinancialYearFinanceProfileAdapter;
import com.nexus.locum.locumnexus.adapters.FinancialYearFinanceProfileAdapter;
import com.nexus.locum.locumnexus.modelPOJO.FinanceYearFinanceProfileModel;
import com.nexus.locum.locumnexus.modelPOJO.PensionFinanceProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinancialYearFinancialProfileFragment extends Fragment {

    RecyclerView recycler_view_financialyear;



    private ArrayList<FinanceYearFinanceProfileModel> mArrayList;
    private FinancialYearFinanceProfileAdapter mAdapter;
    SharedPreferences CONST_SHAREDPREFERENCES;


    public FinancialYearFinancialProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_financial_year_financial_profile, container, false);
        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        recycler_view_financialyear = (RecyclerView) convertView.findViewById(R.id.recycler_view_financialyear);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_financialyear.hasFixedSize();
        recycler_view_financialyear.setItemAnimator(new DefaultItemAnimator());
        recycler_view_financialyear.setLayoutManager(llm);



        mArrayList = new ArrayList<>();
        mAdapter = new FinancialYearFinanceProfileAdapter(mArrayList,getContext());
        recycler_view_financialyear.setAdapter(mAdapter);

       // new GetPenionRate().execute();


        JsonObject profileJSON = Const.CONST_PROFILE_JSON;

        JsonArray financial_years = profileJSON.get("financial").getAsJsonObject().get("financial_years").getAsJsonArray();

        for(int a=0;a<financial_years.size();a++){

           String  taxSubmisionBy  = financial_years.get(a).getAsJsonObject().get("taxSubmisionBy").getAsString();
            String isCurrenttaxReturn = financial_years.get(a).getAsJsonObject().get("isCurrenttaxReturn").getAsString();
            String account_status= financial_years.get(a).getAsJsonObject().get("account_status").getAsString();
            String financialYear = financial_years.get(a).getAsJsonObject().get("financialYear").getAsString();
            String isCurrentYear = financial_years.get(a).getAsJsonObject().get("isCurrentYear").getAsString();
            String end_year = financial_years.get(a).getAsJsonObject().get("end_year").getAsString();
            String start_year= financial_years.get(a).getAsJsonObject().get("start_year").getAsString();
            String _id= financial_years.get(a).getAsJsonObject().get("_id").getAsString();

            mArrayList.add(new FinanceYearFinanceProfileModel(taxSubmisionBy
                    ,isCurrenttaxReturn
                    ,account_status
                    ,financialYear
                    ,isCurrentYear
                    ,end_year
                    ,start_year
                    ,_id
                    ,financial_years.get(a).getAsJsonObject()));
            //mAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();


        }




        return convertView;

    }


   /* static String resGetpentionRates;
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

                        mArrayList.add(new PensionFinanceProfileModel(fromAmount,toAmount,
                                percentage,false,rootObjArray.get(a).getAsJsonObject()));
                        //mAdapter.notifyDataSetChanged();
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
    }*/





}

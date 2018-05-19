package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.adapters.AwaitingBookingsTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.UpcomingBookingsTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

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
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavTabHomeUpcomming extends Fragment {


    RecyclerView recycler_view_UpcomingBookTable;

    Spinner spinnerStatus;
    SwipeRefreshLayout sr_upcomming;

    private ArrayList<AwaitingUpcommingBookTable> dataModels;

    private View m_myFragmentView;

    UpcomingBookingsTableRecyclerAdapter adapter;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_nav_tab_home_upcomming, container, false);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        Log.d("Fragment Upcomming ----", "Loaded");

        sr_upcomming = (SwipeRefreshLayout)m_myFragmentView.findViewById(R.id.sr_upcomming);

        sr_upcomming.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ConnectionDetector.isConnectingToInternet(getContext())){
                 //   new GetBookingDetails().execute();
                }else{
                    Toast.makeText(getContext(),"Check Internet Connection !",Toast.LENGTH_LONG).show();
                }
            }
        });

        if(ConnectionDetector.isConnectingToInternet(getContext())){
          //  new GetBookingDetails().execute();
        }else{
            Toast.makeText(getContext(),"Check Internet Connection !",Toast.LENGTH_LONG).show();
        }


        spinnerStatus = (Spinner)m_myFragmentView.findViewById(R.id.spinnerStatusBookTable);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("<>status-", " spinner item status ===> " + adapterView.getSelectedItem().toString() + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recycler_view_UpcomingBookTable = (RecyclerView)m_myFragmentView.findViewById(R.id.recycler_view_UpcomingBookTable);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_UpcomingBookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_UpcomingBookTable.setItemAnimator(new DefaultItemAnimator());
        recycler_view_UpcomingBookTable.setLayoutManager(llm);
        dataModels = new ArrayList<>();

        adapter = new UpcomingBookingsTableRecyclerAdapter(dataModels, getActivity());
        recycler_view_UpcomingBookTable.setAdapter(adapter);

        return m_myFragmentView;

    }

    static String resUserBookingsDetails;
    ProgressDialog progressDialog;
    public class GetBookingDetails extends AsyncTask<Object, Void, String> {

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

                final OkHttpClient client = new OkHttpClient();

                JsonObject getBookingsJson = new JsonObject();
                getBookingsJson.addProperty("status","all");
                getBookingsJson.addProperty("orderBy",1);
                getBookingsJson.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));



                String responseUpcomming = ResponseAPIWithHeader(getContext(),Const.SERVER_URL_API +"bookatables/getUpcomingTodayBooking"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , getBookingsJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserBookingsDetails=responseUpcomming;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserBookingsDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES upcomingBookings --", ""+resUserBookingsDetails);
                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resUserBookingsDetails).getAsJsonArray();

                dataModels.clear();
                for(int a=0;a<rootObjArray.size();a++){

                    String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                    String bookingDate = rootObjArray.get(a).getAsJsonObject().get("bookingDate").getAsString();
                    String numOfPeople = rootObjArray.get(a).getAsJsonObject().get("numOfPeople").getAsString();

                    JsonArray table_dataArray = rootObjArray.get(a).getAsJsonObject().get("table_data").getAsJsonArray();

                    String tableNum = null,tableCapacity=null,tableId=null,roomNo=null;

                    for(int b=0;b<table_dataArray.size();b++){

                        tableNum = Integer.toString(table_dataArray.get(b).getAsJsonObject().get("tableNum").getAsInt());
                        tableCapacity = Integer.toString(table_dataArray.get(b).getAsJsonObject().get("tableCapacity").getAsInt());
                        tableId = table_dataArray.get(b).getAsJsonObject().get("tableId").getAsString();
                        roomNo = Integer.toString(table_dataArray.get(b).getAsJsonObject().get("roomNo").getAsInt());

                    }

                    String customerName = rootObjArray.get(a).getAsJsonObject().get("customerName").getAsString();
                    String customerEmail = rootObjArray.get(a).getAsJsonObject().get("customerEmail").getAsString();
                    String customerPhone = rootObjArray.get(a).getAsJsonObject().get("customerPhone").getAsString();
                    String bookingStartTime = rootObjArray.get(a).getAsJsonObject().get("bookingStartTime").getAsString();
                    String bookingEndTime = rootObjArray.get(a).getAsJsonObject().get("bookingEndTime").getAsString();
                    String restaurant_id = rootObjArray.get(a).getAsJsonObject().get("restaurant_id").getAsString();
                    String restaurantName = rootObjArray.get(a).getAsJsonObject().get("restaurantName").getAsString();
//                    String userid = rootObjArray.get(a).getAsJsonObject().get("userid").getAsString();
                    String flag = rootObjArray.get(a).getAsJsonObject().get("flag").getAsString();
                    String status = rootObjArray.get(a).getAsJsonObject().get("status").getAsString();
                    String __v = rootObjArray.get(a).getAsJsonObject().get("__v").getAsString();
                    String startTimeslot = rootObjArray.get(a).getAsJsonObject().get("startTimeslot").getAsString();
                    String endTimeslot = rootObjArray.get(a).getAsJsonObject().get("endTimeslot").getAsString();
                    dataModels.add(new AwaitingUpcommingBookTable(_id,bookingStartTime,  customerName, customerEmail,
                            customerPhone,tableNum, numOfPeople,endTimeslot,startTimeslot,table_dataArray,rootObjArray.get(a).getAsJsonObject()));
                }

                adapter.notifyDataSetChanged();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(sr_upcomming.isRefreshing()){
                sr_upcomming.setRefreshing(false);
            }
            progressDialog.dismiss();
        }
    }


}

package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.UnavailableTablesModel;
import com.clickeat.restaurant.click_eatrestaurant.adapters.UnavailableTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.UpcomingBookingsTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.fragments.NavTabHomeUpcomming;
import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

public class UnavailableTableActivity extends AppCompatActivity {

    RecyclerView recycler_view_UpcomingBookTable;

    Spinner spinnerStatus;
    SwipeRefreshLayout sr_upcomming;

    private ArrayList<UnavailableTablesModel> dataModels;
    FloatingActionButton fabAddUnavialableTable;


    UnavailableTableRecyclerAdapter adapter;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unavailable_table);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Unavailable Tables");



        sr_upcomming = (SwipeRefreshLayout)this.findViewById(R.id.sr_upcomming);

        sr_upcomming.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ConnectionDetector.isConnectingToInternet(UnavailableTableActivity.this)){
                    new GetUnAvialableTables().execute();
                }else{
                    Toast.makeText(UnavailableTableActivity.this,"Check Internet Connection !",Toast.LENGTH_LONG).show();
                }
            }
        });




        spinnerStatus = (Spinner)this.findViewById(R.id.spinnerStatusBookTable);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("<>status-", " spinner item status ===> " + adapterView.getSelectedItem().toString() + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recycler_view_UpcomingBookTable = (RecyclerView)this.findViewById(R.id.recycler_view_UpcomingBookTable);

        LinearLayoutManager llm = new LinearLayoutManager(UnavailableTableActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_UpcomingBookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_UpcomingBookTable.setItemAnimator(new DefaultItemAnimator());
        recycler_view_UpcomingBookTable.setLayoutManager(llm);
        dataModels = new ArrayList<>();

        adapter = new UnavailableTableRecyclerAdapter(dataModels, UnavailableTableActivity.this);
        recycler_view_UpcomingBookTable.setAdapter(adapter);

        fabAddUnavialableTable = (FloatingActionButton)this.findViewById(R.id.fabAddUnavialableTable);
        fabAddUnavialableTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UnavailableTableActivity.this,AddUnavailableTableActivity.class));
                finish();
            }
        });

        new GetUnAvialableTables().execute();

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(UnavailableTableActivity.this);
        recycler_view_UpcomingBookTable.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                //sr_awaiting.setEnabled(topRowVerticalPosition >= 0);
//=========================================================================================================
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    //onHide();


                    fabAddUnavialableTable.animate().translationY(fabAddUnavialableTable.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    //onShow();
                    fabAddUnavialableTable.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

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


    }

    static String resUnaviableTables;
    ProgressDialog progressDialog;
    public class GetUnAvialableTables extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(UnavailableTableActivity.this, "Loading", "Please Wait...", true, false);

        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(Object... parametros) {

            try {

            JsonObject mainRequestJSon = new JsonObject();

            JsonObject searchJson = new JsonObject();
            searchJson.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));
            mainRequestJSon.add("search",searchJson);

            JsonObject paramsJson = new JsonObject();
            paramsJson.addProperty("unavailableTables",1);
            mainRequestJSon.add("params",paramsJson);

                /*JsonObject ReqObj = new JsonObject();
                ReqObj.add("search",getBookingsJson);*/


                String responseUnaviableTables = ResponseAPIWithHeader(UnavailableTableActivity.this,
                        Const.SERVER_URL_API +"restaurant-details/search/restaurant"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , mainRequestJSon.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUnaviableTables=responseUnaviableTables;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUnaviableTables;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resUnaviableTables --", ""+resUnaviableTables);
                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resUnaviableTables).getAsJsonArray();

                dataModels.clear();
                for(int a=0;a<rootObjArray.size();a++){

                    String _idRestaurant = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                    JsonArray unavailableTables = rootObjArray.get(a).getAsJsonObject().get("unavailableTables").getAsJsonArray();


                    for(int b=0;b<unavailableTables.size();b++){


                        String _id = unavailableTables.get(b).getAsJsonObject().get("_id").getAsString();
                        String endTime = unavailableTables.get(b).getAsJsonObject().get("endTime").getAsString();
                        String startTime = unavailableTables.get(b).getAsJsonObject().get("startTime").getAsString();
                        String unavailableTableReason = unavailableTables.get(b).getAsJsonObject().get("unavailableTableReason").getAsString();
                        String endTimeSlot = unavailableTables.get(b).getAsJsonObject().get("endTimeSlot").getAsString();
                        String startTimeSlot = unavailableTables.get(b).getAsJsonObject().get("startTimeSlot").getAsString();
                        String endDate = unavailableTables.get(b).getAsJsonObject().get("endDate").getAsString();
                        String startDate = unavailableTables.get(b).getAsJsonObject().get("startDate").getAsString();





                        JsonObject table_data = unavailableTables.get(b).getAsJsonObject().get("table_data").getAsJsonObject();
                        String tableNum = table_data.get("tableNum").getAsString();
                        String tableCapacity = table_data.get("tableCapacity").getAsString();
                        String tableId = table_data.get("tableId").getAsString();
                        String roomNo = table_data.get("roomNo").getAsString();
                        String floorNo = table_data.get("floorNo").getAsString();

                        dataModels.add(new UnavailableTablesModel(_id
                                ,endTime
                                ,startTime
                                ,unavailableTableReason
                                ,endTimeSlot
                                ,startTimeSlot
                                ,endDate
                                ,startDate
                                ,table_data
                                ,unavailableTables.get(a).getAsJsonObject()));


                    }

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


}

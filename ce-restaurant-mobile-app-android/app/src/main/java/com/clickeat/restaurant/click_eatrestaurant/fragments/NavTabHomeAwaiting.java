package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.NavigationMainScreen;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.adapters.AwaitingBookingsTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

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
import static com.clickeat.restaurant.click_eatrestaurant.fragments.NavigationBottomMain.bottomNavigationView;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */

public class NavTabHomeAwaiting extends Fragment {

  //  static Context mContext;



    static RecyclerView recycler_view_BookTable;

    Spinner spinnerStatus;
    static SwipeRefreshLayout sr_awaiting;

    private static ArrayList<AwaitingUpcommingBookTable> dataModels;

    private View m_myFragmentView;
    static TextView tv_nodata;

    static AwaitingBookingsTableRecyclerAdapter adapter;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Fragment Awaitng ----", "Loaded");
        m_myFragmentView = inflater.inflate(R.layout.fragment_nav_tab_home_awaiting, container, false);


        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        tv_nodata = (TextView)m_myFragmentView.findViewById(R.id.tv_nodata);
        recycler_view_BookTable =(RecyclerView)m_myFragmentView.findViewById(R.id.recycler_view_UpcomingBookTable);
        sr_awaiting = (SwipeRefreshLayout)m_myFragmentView.findViewById(R.id.sr_awaiting);

        sr_awaiting.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(ConnectionDetector.isConnectingToInternet(getContext())){
                    new GetAwaitingTableRequest().execute();
                }else{
                    Toast.makeText(getContext(),"Check Internet Connection !",Toast.LENGTH_LONG).show();
                }

            }
        });

        new GetAwaitingTableRequest().execute();
        /*
        if(ConnectionDetector.isConnectingToInternet(getContext())){
            new GetBookingDetails().execute();
        }else{
            Toast.makeText(getContext(),"Check Internet Connection !",Toast.LENGTH_LONG).show();
        }*/

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
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //llm.setStackFromEnd(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_UpcomingBookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_BookTable.setItemAnimator(new DefaultItemAnimator());
        recycler_view_BookTable.setLayoutManager(llm);
        dataModels = new ArrayList<>();

        adapter = new AwaitingBookingsTableRecyclerAdapter(dataModels, getActivity());
        recycler_view_BookTable.hasFixedSize();
        recycler_view_BookTable.setAdapter(adapter);

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_BookTable.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                sr_awaiting.setEnabled(topRowVerticalPosition >= 0);
//=========================================================================================================
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    //onHide();


                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    //onShow();
                    bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

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


        return m_myFragmentView;

    }

    static String resUserBookingsDetails;
    ProgressDialog progressDialog;
    public class GetAwaitingTableRequest extends AsyncTask<Object, Void, String> {

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

                JsonObject getBookingsJson = new JsonObject();
                getBookingsJson.addProperty("status","Awaiting Confirmation");
                getBookingsJson.addProperty("restaurantId",CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));
                getBookingsJson.addProperty("durationType","upcoming");
                getBookingsJson.addProperty("orderBy","ascending");


                String responseAwaiting = ResponseAPIWithHeader(getContext(),
                        Const.SERVER_URL_API +"bookatables/getBookingsByDurationType"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , getBookingsJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserBookingsDetails=responseAwaiting;

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

            progressDialog.dismiss();

            try{
                Log.i("RES Awaiting Body---", ""+resUserBookingsDetails);
                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resUserBookingsDetails).getAsJsonArray();

                if(rootObjArray.size()<=0){
                    tv_nodata.setVisibility(View.VISIBLE);
                    recycler_view_BookTable.setVisibility(View.GONE);
                }else {

                    dataModels.clear();
                    for (int a = 0; a < rootObjArray.size(); a++) {

                        String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                        String bookingDate = rootObjArray.get(a).getAsJsonObject().get("bookingDate").getAsString();
                        String numOfPeople = rootObjArray.get(a).getAsJsonObject().get("numOfPeople").getAsString();

                        JsonArray table_dataArray = rootObjArray.get(a).getAsJsonObject().get("table_data").getAsJsonArray();

                        String tableNum = "",tableCapacity=null,tableId=null,roomNo=null;

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
//                        String userid = rootObjArray.get(a).getAsJsonObject().get("userid").getAsString();
                        String flag = rootObjArray.get(a).getAsJsonObject().get("flag").getAsString();
                        String status = rootObjArray.get(a).getAsJsonObject().get("status").getAsString();
                        String __v = rootObjArray.get(a).getAsJsonObject().get("__v").getAsString();
                        String startTimeslot = rootObjArray.get(a).getAsJsonObject().get("startTimeslot").getAsString();
                        String endTimeslot = rootObjArray.get(a).getAsJsonObject().get("endTimeslot").getAsString();
                        dataModels.add(new AwaitingUpcommingBookTable(_id, bookingStartTime, customerName, customerEmail, customerPhone, tableNum,
                                numOfPeople, endTimeslot, startTimeslot,table_dataArray,rootObjArray.get(a).getAsJsonObject()));
                    }

                    adapter.notifyDataSetChanged();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                progressDialog.dismiss();
            }

            if(sr_awaiting.isRefreshing()){
                sr_awaiting.setRefreshing(false);
            }
        }
    }


}

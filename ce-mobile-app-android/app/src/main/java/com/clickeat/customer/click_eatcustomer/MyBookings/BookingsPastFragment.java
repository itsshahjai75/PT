package com.clickeat.customer.click_eatcustomer.MyBookings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.OBookings;
import com.clickeat.customer.click_eatcustomer.DataModel.OBookingsforPast;
import com.clickeat.customer.click_eatcustomer.DataModel.OTable;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by pivotech on 28/2/18.
 */

public class BookingsPastFragment extends Fragment {

    private View m_myFragmentView;
    private ProgressDialog loadingSpinner;
    private RecyclerView recyclerViewPast;
    private TextView textNoFoundBookingPast;
    private static Boolean isPastAscending;
    private static final String args1 = "isPastAscending";

    public static BookingsPastFragment newInstance(Boolean isPastAscending) {
        BookingsPastFragment fragment = new BookingsPastFragment();
        Bundle args = new Bundle();
        args.putBoolean(args1, isPastAscending);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPastAscending = getArguments().getBoolean(args1);
        Log.d("<>tab-", " in past fragment --> " + isPastAscending);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Bookings");
        m_myFragmentView = inflater.inflate(R.layout.layout_my_booking_past_fragment, container, false);
        recyclerViewPast = (RecyclerView) m_myFragmentView.findViewById(R.id.myBookingPastRecycler);
        textNoFoundBookingPast = m_myFragmentView.findViewById(R.id.textNoFoundBookingPast);

        getBookings(false);
        return m_myFragmentView;
    }

    private void init() {
        List<OBookingsforPast> listBookings = MyApplication.getGlobalData().getBookingsForPastList();
        if (listBookings.size() > 0) {
            textNoFoundBookingPast.setVisibility(View.GONE);
            recyclerViewPast.setVisibility(View.VISIBLE);
            recyclerViewPast.setHasFixedSize(true);
            BookingPastAdapter adapter = new BookingPastAdapter(getActivity(), listBookings);
            recyclerViewPast.setAdapter(adapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            recyclerViewPast.setLayoutManager(llm);
        } else {
            textNoFoundBookingPast.setVisibility(View.VISIBLE);
            recyclerViewPast.setVisibility(View.GONE);
        }
    }

    /*show spinner */
    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    /*dismiss spinner*/
    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }*/

    public void getBookings(Boolean isAscending) {
        Log.d("<>book-", " state of --> "+isAscending);
        if (getActivity() != null) {
            showSpinner();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final JsonObject innerData = new JsonObject();
        String userId = "";
        if (SharedData.getIsRemember(getActivity()) == true)
            userId = SharedData.getUserId(getActivity());
        else
            userId = MyApplication.getGlobalData().getUserId();

        innerData.addProperty("userId", userId);
        innerData.addProperty("durationType", "past");
        if (isAscending)
            innerData.addProperty("orderBy", "descending");
        else
            innerData.addProperty("orderBy", "ascending");
        Call<JsonElement> mService = mInterfaceService.getBookingsByDurationTYpe(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (getActivity() != null) {
                        if (loadingSpinner != null)
                            dismissSpinner();
                    }
                    Log.d("<>log-", " response body ===> " + response.body() + "");
                    JsonArray responseBooking = response.body().getAsJsonArray();
                    if (responseBooking.size() > 0) {
                        List<OBookingsforPast> bookingList = new ArrayList<>();
                        for (int book = 0; book < responseBooking.size(); book++) {
                            String id = "", bookingDate = "", customerName = "", customerPhone = "",
                                    customerEmail = "", bookingStartTime = "", restaurant_id = "",
                                    restaurantName = "", userid = "", bookingEndTime = "", status = "", tableId = "";
                            Integer numOfPeople = 0, floorNo = 0, roomNo = 0, tableCapacity = 0, tableNum = 0;
                            JsonObject bookingDetails = responseBooking.get(book).getAsJsonObject();
                            if (!bookingDetails.isJsonNull()) {
                                if (bookingDetails.has("_id") && !bookingDetails.get("_id").isJsonNull()) {
                                    id = bookingDetails.get("_id").getAsString();
                                }
                                if (bookingDetails.has("bookingDate") && !bookingDetails.get("bookingDate").isJsonNull()) {
                                    bookingDate = bookingDetails.get("bookingDate").getAsString();
                                }
                                if (bookingDetails.has("numOfPeople") && !bookingDetails.get("numOfPeople").isJsonNull()) {
                                    numOfPeople = bookingDetails.get("numOfPeople").getAsInt();
                                }
                                if (bookingDetails.has("customerName") && !bookingDetails.get("customerName").isJsonNull()) {
                                    customerName = bookingDetails.get("customerName").getAsString();
                                }
                                if (bookingDetails.has("customerEmail") && !bookingDetails.get("customerEmail").isJsonNull()) {
                                    customerEmail = bookingDetails.get("customerEmail").getAsString();
                                }
                                if (bookingDetails.has("customerPhone") && !bookingDetails.get("customerPhone").isJsonNull()) {
                                    customerPhone = bookingDetails.get("customerPhone").getAsString();
                                }
                                if (bookingDetails.has("bookingStartTime") && !bookingDetails.get("bookingStartTime").isJsonNull()) {
                                    bookingStartTime = bookingDetails.get("bookingStartTime").getAsString();
                                }
                                if (bookingDetails.has("restaurant_id") && !bookingDetails.get("restaurant_id").isJsonNull()) {
                                    restaurant_id = bookingDetails.get("restaurant_id").getAsString();
                                }
                                if (bookingDetails.has("restaurantName") && !bookingDetails.get("restaurantName").isJsonNull()) {
                                    restaurantName = bookingDetails.get("restaurantName").getAsString();
                                }
                                if (bookingDetails.has("userid") && !bookingDetails.get("userid").isJsonNull()) {
                                    userid = bookingDetails.get("userid").getAsString();
                                }
                                if (bookingDetails.has("bookingEndTime") && !bookingDetails.get("bookingEndTime").isJsonNull()) {
                                    bookingEndTime = bookingDetails.get("bookingEndTime").getAsString();
                                }
                                if (bookingDetails.has("status") && !bookingDetails.get("status").isJsonNull()) {
                                    status = bookingDetails.get("status").getAsString();
                                }
                                List<OTable> tableData = new ArrayList<>();
                                if (bookingDetails.has("table_data") && !bookingDetails.get("table_data").isJsonNull() &&
                                        bookingDetails.get("table_data").isJsonArray()) {
                                    JsonArray table_data = bookingDetails.get("table_data").getAsJsonArray();
                                    if (table_data.size() > 0){

                                        for (int i=0; i < table_data.size(); i++){
                                            if (table_data.get(i).getAsJsonObject().has("floorNo") &&
                                                    !table_data.get(i).getAsJsonObject().get("floorNo").isJsonNull()) {
                                                floorNo = table_data.get(i).getAsJsonObject().get("floorNo").getAsInt();
                                            }
                                            if (table_data.get(i).getAsJsonObject().has("roomNo") &&
                                                    !table_data.get(i).getAsJsonObject().get("roomNo").isJsonNull()) {
                                                roomNo = table_data.get(i).getAsJsonObject().get("roomNo").getAsInt();
                                            }
                                            if (table_data.get(i).getAsJsonObject().has("tableCapacity") &&
                                                    !table_data.get(i).getAsJsonObject().get("tableCapacity").isJsonNull()) {
                                                tableCapacity = table_data.get(i).getAsJsonObject().get("tableCapacity").getAsInt();
                                            }
                                            if (table_data.get(i).getAsJsonObject().has("tableNum") &&
                                                    !table_data.get(i).getAsJsonObject().get("tableNum").isJsonNull()) {
                                                tableNum = table_data.get(i).getAsJsonObject().get("tableNum").getAsInt();
                                            }
                                            if (table_data.get(i).getAsJsonObject().has("tableId") &&
                                                    !table_data.get(i).getAsJsonObject().get("tableId").isJsonNull()) {
                                                tableId = table_data.get(i).getAsJsonObject().get("tableId").getAsString();
                                            }
                                            tableData.add(new OTable(tableNum, tableCapacity, tableId, roomNo, floorNo));
                                        }
                                    }

                                }
                                bookingList.add(new OBookingsforPast(
                                        id,
                                        bookingDate,
                                        numOfPeople,
                                        customerName,
                                        customerPhone,
                                        customerEmail,
                                        bookingStartTime,
                                        restaurant_id,
                                        restaurantName,
                                        userid,
                                        bookingEndTime,
                                        status,
                                        tableData
                                ));

                            }
                        }
                        MyApplication.getGlobalData().addBookingPastList(bookingList);
                        init();
                    } else {
                        init();
                    }

                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (getActivity() != null) {
                        if (loadingSpinner != null)
                            dismissSpinner();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (getActivity() != null) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }
}




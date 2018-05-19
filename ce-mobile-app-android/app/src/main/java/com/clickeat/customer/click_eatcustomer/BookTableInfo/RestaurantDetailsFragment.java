package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.OTableMap;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantByLocationModel;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.LoginAsDialog;
import com.clickeat.customer.click_eatcustomer.MainHome.BookATableFragmentAsDialog;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Search.IOnSearchViewChangeListener;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by pivotech on 9/10/17.
 */

public class RestaurantDetailsFragment extends Fragment implements View.OnClickListener,
        RestaurantDeatilsRecyclerAdapter.CallbackInterface, SearchView.OnQueryTextListener,
        TimeSlotRecyclerAdapter.timeSlotInterface {

    private int FILTER_REQUEST_CODE = 111;
    public static int LOGIN_REQUEST_CODE = 123;
    private View m_myFragmentView;
    RecyclerView recyclerViewRestaurant;
    SwipeRefreshLayout refreshLayout;
    LinearLayout relativeLayoutSortBy, relativeLayoutFilter;
    TextView textSearch, txtNoFound;
    ImageButton imgButtonEditFilter;
    private SearchView searchViewFilter;
    Boolean result;

    private TimeSlotRecyclerAdapter.timeSlotInterface timeSlotInterface;
    private static String TAG = "Details";
    private ArrayList<RestaurantDetailModel> dataModels;
    BottomSheetBehavior behavior;
    private BottomSheetDialog mBottomSheetDialog;
    private Intent extras;
    private ProgressDialog loadingSpinner;
    private String time, people, date, address, cuisineResource;
    private Double longitude, latitude;
    private static final String ARG_PARAM_TIME = "time";
    private static final String ARG_PARAM_PEOPLE = "people";
    private static final String ARG_PARAM_DATE = "date";
    private static final String ARG_PARAM_LATITUDE = "latitude";
    private static final String ARG_PARAM_LONGITUDE = "longitude";
    private static final String ARG_PARAM_ADDRESS = "address";
    private ArrayList<String> cuisines = new ArrayList<>();
    private RestaurantDeatilsRecyclerAdapter recyclerAdapter;
    RestaurantDeatilsRecyclerAdapter.CallbackInterface callbackInterface;
    private SearchView searchView;
    private IOnSearchViewChangeListener mOnSearchViewChangeListener;
    private SearchView mSearchView;
    String resUnavilabletable;

    public static RestaurantDetailsFragment newInstance(String time, String people, String date, Double longitude, Double latitude, String address) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_TIME, time + "");
        args.putString(ARG_PARAM_PEOPLE, people);
        args.putString(ARG_PARAM_DATE, date);
        args.putDouble(ARG_PARAM_LATITUDE, latitude);
        args.putDouble(ARG_PARAM_LONGITUDE, longitude);
        args.putString(ARG_PARAM_ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            time = getArguments().getString(ARG_PARAM_TIME);
            date = getArguments().getString(ARG_PARAM_DATE);
            people = getArguments().getString(ARG_PARAM_PEOPLE);
            latitude = getArguments().getDouble(ARG_PARAM_LATITUDE);
            longitude = getArguments().getDouble(ARG_PARAM_LONGITUDE);
            address = getArguments().getString(ARG_PARAM_ADDRESS);
            cuisines = MyApplication.getGlobalData().getCuisineList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final PreLoginMainActivity activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle("Restaurants");
        m_myFragmentView = inflater.inflate(R.layout.activity_restaurant_details, container, false);
        try {
            callbackInterface = (RestaurantDeatilsRecyclerAdapter.CallbackInterface) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnListItemSelectedListener");
        }

        timeSlotInterface = this;
        APIConstants.setLatitude(latitude);
        APIConstants.setLongitude(longitude);
        SharedData.setSortingType(getActivity(), getResources().getString(R.string.distance));
        findIds();
        SimpleDateFormat input = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM, yyyy");
        cuisines = MyApplication.getGlobalData().getCuisineList();
        cuisineResource = cuisines.size() == 1 ? getString(R.string.one_cuisine) : getString(R.string.pural_selected_cuisines);
        Log.d("<>fragRestaurant-", " time ==> " + time + " date ==> " + date + " people == " +
                people + " latitude ==>  " + latitude + " longitude ==> " + longitude + "cuisines ===> " + cuisines.toString());
        try {
            Date formatedDate = input.parse(date);
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < cuisines.size(); c++) {
                if (c + 1 == cuisines.size())
                    builder.append(cuisines.get(c) + ".");
                else
                    builder.append(cuisines.get(c) + ", ");
            }
//            textSearch.setText(getString(R.string.title_search) + " 0 " + getString(R.string.title_Restaurants) + " " + getString(R.string.search_string_around) + " " + address + " " +
//                    " on " + output.format(formatedDate) + " " + getString(R.string.search_string_at) + " " + time + " " + getString(R.string.search_string_for) + " " + people + " " + getString(R.string.search_string_people));
            textSearch.setText("0 " + getString(R.string.title_Restaurants));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getRestaurantByLocation(longitude, latitude);
        return m_myFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

    }

    public void init() {
        Log.d("<>resta-", " cuisine list in init ==? " + cuisines.toString());
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        double f = Math.random();
                        getRestaurantByLocation(longitude, latitude);
                    }
                }, 6000);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setLayoutManager(llm);
        /*View bottomSheet = m_myFragmentView.findViewById(R.id.bottom_sheet1);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        RecyclerView recyclerView = (RecyclerView) m_myFragmentView.findViewById(R.id.recyclerView_sheet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/

        relativeLayoutSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        imgButtonEditFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>resta-", " cuisine list while click ==? " + cuisines.toString());
                Log.d("<>resta-", " cuisine list while click global cuisine list ==? " + MyApplication.getGlobalData().getCuisineList());
                BookATableFragmentAsDialog dialog = BookATableFragmentAsDialog.newInstance(true, time, people, date, address, cuisines, latitude, longitude);
                dialog.show(getFragmentManager(), "dialog");
            }
        });


        searchViewFilter.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutSortBy.setVisibility(View.GONE);
                relativeLayoutFilter.setVisibility(View.GONE);
                searchViewFilter.setQueryHint(Html.fromHtml("<font color = #D3D3D3 >" + "<small>" + "Search by Restaurant Name" + "</small>" + "</font>"));
               /* AutoCompleteTextView search_text = (AutoCompleteTextView) searchViewFilter.findViewById(searchViewFilter.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
                search_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                search_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_small));*/
               /* searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getActivity().getComponentName()));
                searchView.setMaxWidth(Integer.MAX_VALUE);*/
            }
        });

        searchViewFilter.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                relativeLayoutSortBy.setVisibility(View.VISIBLE);
                relativeLayoutFilter.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void findIds() {
        recyclerViewRestaurant = m_myFragmentView.findViewById(R.id.recycler_view_restaurant);
        refreshLayout = m_myFragmentView.findViewById(R.id.swipe_refresh_layout);
        textSearch = m_myFragmentView.findViewById(R.id.textSearch);
        txtNoFound = m_myFragmentView.findViewById(R.id.txtNoFound);
        relativeLayoutSortBy = m_myFragmentView.findViewById(R.id.relativeLayoutSortBy);
        relativeLayoutFilter = m_myFragmentView.findViewById(R.id.relativeLayoutFilter);
        imgButtonEditFilter = m_myFragmentView.findViewById(R.id.imgButtonEditFilter);
        searchViewFilter = m_myFragmentView.findViewById(R.id.searchViewList);
        searchViewFilter.setOnQueryTextListener(this);
    }

    private void showBottomSheetDialog() {
       /* if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }*/

        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        final ListView lstViewSheet = (ListView) view.findViewById(R.id.lstViewSheet);
        Button btnBottomCancel = (Button) view.findViewById(R.id.btnBottomCancel);
        lstViewSheet.setAdapter(new BottomSheetAdapter(createItems(), new BottomSheetAdapter.ItemListener() {
            @Override
            public void onItemClick(BottomSheetPOJO item) {
                if (mBottomSheetDialog != null) {

                    Log.d("<>bottom-", " BottomSheetAdapter.selected_item " + BottomSheetAdapter.selected_item);
                    Log.d("<>bottom-", "item rank ==> " + item.getmState());

                    /*if (BottomSheetAdapter.selected_item == item.getmState()) {
                        BottomSheetAdapter.selected_item = 0;
                        Log.d("<>bottom-", " in if item click title name ===> " + item.getmTitle());
                    } else {*/
                    BottomSheetAdapter.selected_item = item.getmState();
                    SharedData.setSortingType(getActivity(), item.getmTitle());
//                            lstViewSheet.setItemChecked(BottomSheetAdapter.selected_item, true);
                    Log.d("<>bottom-", " in item click title name ===> " + item.getmTitle());
                    getRestaurantByLocation(longitude, latitude);
//                    }
                    mBottomSheetDialog.dismiss();
                }
            }
        }, getActivity(), lstViewSheet));

        /*List<BottomSheetPOJO> pojo = createItems();
        for (int bot=0 ; bot < pojo.size(); bot++){
            if(SharedData.getSortingType(getActivity()).equals(pojo.get(bot).getmTitle()))
                lstViewSheet.setItemChecked(pojo.get(bot).getmState(), true);
        }*/

        btnBottomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        lstViewSheet.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        /*lstViewSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                SharedData.setSortingType(getActivity(), parent.getAdapter().getItem(position).toString());
                Log.d("<>list-", " in item click title name ===> " + parent.getAdapter().getItem(position).toString());
            }
        });*/

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public List<BottomSheetPOJO> createItems() {

        ArrayList<BottomSheetPOJO> items = new ArrayList<>();
//        items.add(new BottomSheetPOJO(getResources().getString(R.string.sort_by_title), 0));
        items.add(new BottomSheetPOJO(getString(R.string.distance), 0));
        items.add(new BottomSheetPOJO(getResources().getString(R.string.title_popularity), 1));
        items.add(new BottomSheetPOJO(getString(R.string.title_newest_first), 2));
        items.add(new BottomSheetPOJO(getString(R.string.title_price_low_to_high), 3));
        items.add(new BottomSheetPOJO(getString(R.string.title_price_high_to_low), 4));
        items.add(new BottomSheetPOJO(getString(R.string.alphabetically_title), 5));
        return items;
    }

    private void getRestaurantByLocation(Double longitude, final Double latitude) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("email", "restaurant@gmail.com");
            paramObject.put("password", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<String> listCuisine = new ArrayList<>();
        listCuisine = MyApplication.getGlobalData().getCuisineList();
        ArrayList<String> listFacility = new ArrayList<>();
        listFacility = MyApplication.getGlobalData().getFacilitiesList();
        ArrayList<String> listDiets = new ArrayList<>();
        listDiets = MyApplication.getGlobalData().getDietsList();
        ArrayList<String> collectionDeliveryList = new ArrayList<>();
        ArrayList<String> finalCollectionList = new ArrayList<>();
        collectionDeliveryList = MyApplication.getGlobalData().getCollectionDeliveryList();
        if (collectionDeliveryList.contains(getResources().getString(R.string.collection_title)))
            finalCollectionList.add("collection");
        if (collectionDeliveryList.contains(getActivity().getResources().getString(R.string.delivery_title)))
            finalCollectionList.add("delivery");
        Double maxDistance = MyApplication.getGlobalData().getSliderValue() * 1609.34;
        Integer avgPrice = Integer.valueOf(MyApplication.getGlobalData().getAvgPrice());
        JSONObject objSort = new JSONObject();
        try {
            objSort.put("sort_by", SharedData.getSortingType(getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Boolean isVegetarian = MyApplication.getGlobalData().getVeg();
        Boolean isClubMember = MyApplication.getGlobalData().getClubMember();
        final RestaurantByLocationModel data = new RestaurantByLocationModel("en", longitude, latitude,
                maxDistance, 0.000621, listCuisine, finalCollectionList, listFacility,
                objSort, listDiets, isClubMember, avgPrice, isVegetarian);
//        Call<ResponseBody> mService = mInterfaceService.getAuthorisation(login, CookieManager.getCookieAll(), CookieManager.getXsrfToken(), "application/json;charset=utf-8");
        Call<JsonElement> mService = mInterfaceService.getRestaurantByLocation(data);
        Log.d("<>dist-", "latitued ==> " + latitude + " === longitude " + longitude);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body().toString() + "");
        Log.d("<>login-", " Headers is  ==> " + data.toString());
        Log.d("<>login-", " contentType is  ==> " + mService.request().body().contentType() + "");
        Log.d("<>login-", " Body is  ==> " + mService.request().body().toString() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
           /*     try {
//                    Log.d("<>log-", " response error body ===> " + response.errorBody().string().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                Log.d("<>log-", " response headers ===> " + response.headers().toMultimap().get("Set-Cookie"));
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    Log.d("<>log-", " in response of login ");
                    SimpleDateFormat input = new SimpleDateFormat("dd-MMM-yyyy");
                    SimpleDateFormat output = new SimpleDateFormat("dd MMM, yyyy");
                    Date formatedDate = null;
                    try {
                        formatedDate = input.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (response.body().getAsJsonArray().size() == 0) {
                            Toast.makeText(getActivity(), "No restaurant found!!!", Toast.LENGTH_SHORT).show();
                            txtNoFound.setVisibility(View.VISIBLE);
                            textSearch.setText("0 " + getString(R.string.title_Restaurants));
//                            textSearch.setText(getString(R.string.title_search) + " " + 0 + " " + getString(R.string.title_Restaurants) + " " + getString(R.string.search_string_around) + " " + address + " " +
//                                    " on " + output.format(formatedDate) + " " + getString(R.string.search_string_at) + " " + time + " " + getString(R.string.search_string_for) + " " + people + " " + getString(R.string.search_string_people));
                            Log.d("<>tag-", " in init child count of recycler -->" + recyclerViewRestaurant.getChildCount() + "");
                            dataModels = new ArrayList<>();
                            RestaurantDeatilsRecyclerAdapter adapter = new RestaurantDeatilsRecyclerAdapter(dataModels, getActivity(), false, time, date, people, callbackInterface, timeSlotInterface);
                            recyclerViewRestaurant.setAdapter(adapter);
                            if (loadingSpinner != null)
                                dismissSpinner();
                            /*if (recyclerViewRestaurant.getChildCount() > 0)
                                recyclerViewRestaurant.getRecycledViewPool().clear();*/
                            init();
                        } else {
                            Log.d("<>tag-", " on else while body has 0 count");
                            JsonArray arrayMain = response.body().getAsJsonArray();
                            RestaurantDetails restDetails = new RestaurantDetails();
                            dataModels = new ArrayList<>();
                            SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
                            Log.d("<>resta-", " array main size in result ==> " + arrayMain.size() + "");
                            textSearch.setText(arrayMain.size() + " " + getString(R.string.title_Restaurants));
//                            textSearch.setText(getString(R.string.title_search) + " " + arrayMain.size() + " " + getString(R.string.title_Restaurants) + " " + getString(R.string.search_string_around) + " " + address + " " +
//                                    " on " + output.format(formatedDate) + " " + getString(R.string.search_string_at) + " " + time + " " + getString(R.string.search_string_for) + " " + people + " " + getString(R.string.search_string_people));
                            for (int i = 0; i < arrayMain.size(); i++) {
                                if (arrayMain.get(i).getAsJsonObject().has("status") &&
                                        !arrayMain.get(i).getAsJsonObject().get("status").isJsonNull()) {
                                    if (arrayMain.get(i).getAsJsonObject().get("status").getAsString().equals("Published")) {


                                        JsonArray facilitiesDetails = new JsonArray();
                                        Boolean collection = false;
                                        Boolean delivery = false, isMembership = false, isBookTable = false, isTakeaway = false;
                                        Integer collectionDiscount = null, averageCollectionTime = null, averageDeliveryTime = null;
                                        Integer averagePriceForTwo = 0, averagePriceForFour = 0;
                                        List<String> listImages = new ArrayList<>();
                                        Double deliveryCharge = null, deliveryDistance = null;
                                        String addressCity = "";
                              /*  if (arrayMain.get(i).getAsJsonObject().has("facilities")) {
                                    facilitiesDetails = arrayMain.get(i).getAsJsonObject().get("facilities").getAsJsonArray();
                                }*/
                                        JsonObject mainDetails = arrayMain.get(i).getAsJsonObject().get("main_details").getAsJsonObject();
                                        Log.d("<>hygieneRating-", " mainDetails == > " + mainDetails.toString());
                                        JsonArray latlangBounds = arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().getAsJsonObject("restaurantAddress").getAsJsonObject("location").getAsJsonArray("coordinates");
                                        JsonArray list = new JsonArray();
                                        HashMap<String, List<String>> timeTables = new HashMap<>();
                                        HashMap<String, Boolean> isOpenDay = new HashMap<>();
                                        if (arrayMain.get(i).getAsJsonObject().has("calender")) {
                                            JsonArray calendarList = arrayMain.get(i).getAsJsonObject().get("calender").getAsJsonObject().get("dayList").getAsJsonArray();
                                            Log.d("<>log-", " calender list ===> " + calendarList.toString());
                                            for (int cal = 0; cal < calendarList.size(); cal++) {
                                                Log.d("<>log-", " hours ==> " + calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray());
                                                String openTime = null, closeTime = null;
                                                List<String> times = new ArrayList<>();
                                                try {
                                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                    for (int tm = 0; tm < calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().size(); tm++) {
                                                        String dtStart = "", dtEnd = "";
                                                        if (calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().has("opensAt") &&
                                                                !calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("opensAt").isJsonNull()) {
                                                            dtStart = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("opensAt").getAsString();
                                                        }
                                                        Date dateStart = df.parse(dtStart.replaceAll("Z$", "+0000"));
                                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
                                                        Log.d("<>test-", " date start time --> " + dateStart.toString());
                                                        openTime = sdf.format(dateStart);
                                                        if (calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().has("closesAt") &&
                                                                !calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("closesAt").isJsonNull()) {
                                                            dtEnd = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("closesAt").getAsString();
                                                        }
//                                                String dtEnd = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("closesAt").getAsString();
                                                        Date dateEnd = df.parse(dtEnd.replaceAll("Z$", "+0000"));
                                                        closeTime = sdf.format(dateEnd);
                                                        Log.d("<>test-", " date end time --> " + dtEnd);
                                                        String fullTime = new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(openTime))
                                                                + " - " + new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(closeTime));
                                                        times.add(fullTime);
                                                    }
                                            /*String dtStart = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(0).getAsJsonObject().get("opensAt").getAsString();
                                            Date dateStart = df.parse(dtStart.replaceAll("Z$", "+0000"));
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            openTime = sdf.format(dateStart);
                                            String dtEnd = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(0).getAsJsonObject().get("closesAt").getAsString();
                                            Date dateEnd = df.parse(dtEnd.replaceAll("Z$", "+0000"));
                                            closeTime = sdf.format(dateEnd);*/
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                String day = calendarList.get(cal).getAsJsonObject().get("label").getAsString();
                                                Boolean value = calendarList.get(cal).getAsJsonObject().get("value").getAsBoolean();
                                                Log.d("<>test-", " day + value ==> " + day + " ==== " + times.toString());
                                                timeTables.put(day, times);
                                                isOpenDay.put(day, value);
                                            }
                                        }

                                        /* facilities from outside of main */
                                /*if (facilitiesDetails.size() > 0) {
                                    for (int fc = 0; fc < facilitiesDetails.size(); fc++) {
                                        Log.d("<>log-", " facilities ==> " + facilitiesDetails.get(fc).getAsJsonObject().get("en").getAsJsonObject().get("name").getAsString());
                                        String replaceString = facilitiesDetails.get(fc).getAsJsonObject().get("icon").getAsString().replace("-", "_");
                                        facilitiesList.put(facilitiesDetails.get(fc).getAsJsonObject().get("icon").getAsString()
                                                , facilitiesDetails.get(fc).getAsJsonObject().get("en").getAsJsonObject().get("name").getAsString());
                                    }
                                }*/


                                        if (arrayMain.get(i).getAsJsonObject().has("collection_delivery")) {
                                            JsonObject collection_delivery = arrayMain.get(i).getAsJsonObject().get("collection_delivery").getAsJsonObject();
                                            collection = collection_delivery.get("collection").getAsJsonObject().get("value").getAsBoolean();
                                            if (collection_delivery.get("collection").getAsJsonObject().has("collectionDiscount"))
                                                if (!collection_delivery.get("collection").getAsJsonObject().get("collectionDiscount").toString().equals("null")) {
                                                    Log.d("<>1test-", collection_delivery.get("collection").getAsJsonObject().get("collectionDiscount").getAsInt() + "");
                                                    collectionDiscount = collection_delivery.get("collection").getAsJsonObject().get("collectionDiscount").getAsInt();
                                                }
                                            if (collection_delivery.get("collection").getAsJsonObject().has("averageCollectionTime") &&
                                                    !collection_delivery.get("collection").getAsJsonObject().get("averageCollectionTime").toString().equals("null")) {
                                                Log.d("<>1test-", "averageCollectionTime ==> " + collection_delivery.get("collection").getAsJsonObject().get("averageCollectionTime").getAsInt() + "");
                                                averageCollectionTime = collection_delivery.get("collection").getAsJsonObject().get("averageCollectionTime").getAsInt();
                                            }
                                            delivery = collection_delivery.get("delivery").getAsJsonObject().get("value").getAsBoolean();
                                            if (collection_delivery.get("delivery").getAsJsonObject().has("deliveryCharge") &&
                                                    !collection_delivery.get("delivery").getAsJsonObject().get("deliveryCharge").toString().equals("null"))
                                                deliveryCharge = Double.valueOf(String.format("%.2f", collection_delivery.get("delivery").getAsJsonObject().get("deliveryCharge").getAsDouble()));
                                            if (collection_delivery.get("delivery").getAsJsonObject().has("deliveryDistance") &&
                                                    !collection_delivery.get("delivery").getAsJsonObject().get("deliveryDistance").isJsonNull())
                                                deliveryDistance = collection_delivery.get("delivery").getAsJsonObject().get("deliveryDistance").getAsDouble();
                                            if (collection_delivery.get("delivery").getAsJsonObject().has("averageDeliveryTime") &&
                                                    !collection_delivery.get("delivery").getAsJsonObject().get("averageDeliveryTime").isJsonNull())
                                                averageDeliveryTime = collection_delivery.get("delivery").getAsJsonObject().get("averageDeliveryTime").getAsInt();
                                        }
                                        List<String> likedBy = new ArrayList<>();
                                        if (arrayMain.get(i).getAsJsonObject().has("likedBy")) {
                                            JsonArray likedArray = arrayMain.get(i).getAsJsonObject().get("likedBy").getAsJsonArray();
                                            if (likedArray.size() > 0) {
                                                for (int like = 0; like < likedArray.size(); like++) {
                                                    likedBy.add(likedArray.get(like).getAsString());
                                                }
                                            }
                                        }

                                        if (arrayMain.get(i).getAsJsonObject().has("averagePrice") &&
                                                !arrayMain.get(i).getAsJsonObject().get("averagePrice").isJsonNull()) {
//                                    if (arrayMain.get(i).getAsJsonObject().
//                                            get("averagePrice").getAsJsonObject().has("averagePrice") &&
//                                            !arrayMain.get(i).getAsJsonObject().
//                                                    get("averagePrice").getAsJsonObject().get("averagePrice").isJsonNull()) {
                                            JsonObject averagePrice = arrayMain.get(i).getAsJsonObject().
                                                    get("averagePrice").getAsJsonObject();
                                            averagePriceForFour = averagePrice.get("AveragePriceForFourPeople").getAsInt();
                                            averagePriceForTwo = averagePrice.get("AveragePriceForTwoPeople").getAsInt();
//                                    }
                                        }

                                        if (arrayMain.get(i).getAsJsonObject().has("service") &&
                                                !arrayMain.get(i).getAsJsonObject().get("service").isJsonNull()) {
                                            if (arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().has("membership")
                                                    && !arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("membership").isJsonNull())
                                                isMembership = arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("membership").getAsBoolean();
                                            if (arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().has("takeaway")
                                                    && !arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("takeaway").isJsonNull())
                                                isTakeaway = arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("takeaway").getAsBoolean();
                                            if (arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().has("bookatable")
                                                    && !arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("bookatable").isJsonNull())
                                                isBookTable = arrayMain.get(i).getAsJsonObject().get("service").getAsJsonObject().get("bookatable").getAsJsonObject().get("bookatable").getAsBoolean();
                                        }

                                        if (arrayMain.get(i).getAsJsonObject().has("images") &&
                                                !arrayMain.get(i).getAsJsonObject().get("images").isJsonNull()) {
                                            JsonArray images = arrayMain.get(i).getAsJsonObject().get("images").getAsJsonArray();
                                            listImages = new ArrayList<>();
                                            if (images.size() > 0) {
                                                for (int im = 0; im < images.size(); im++) {
                                                    listImages.add(images.get(im).getAsJsonObject().get("url").getAsString());
                                                }
                                            }
                                        }
                                        String logoFull = "";
                                        if (mainDetails.has("restaurantLogoFull") && !mainDetails.get("restaurantLogoFull").isJsonNull()) {
                                            Log.d("<>1-", "logo full ==> " + mainDetails.get("restaurantLogoFull").getAsString());
                                            logoFull = mainDetails.get("restaurantLogoFull").getAsString();
                                        }
                                        Integer numberOfReviews = 0;
                                        if (arrayMain.get(i).getAsJsonObject().has("numberOfReviews") &&
                                                !arrayMain.get(i).getAsJsonObject().get("numberOfReviews").isJsonNull()) {
                                            numberOfReviews = arrayMain.get(i).getAsJsonObject().get("numberOfReviews").getAsInt();
                                        }
                                        String hygieneRating = "", restaurantName = "", about = "";
                                        HashMap<Integer, String> serviceMap = new HashMap<>();
                                        if (mainDetails.has(MyApplication.sDefSystemLanguage) &&
                                                !mainDetails.get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("hygieneRating")) {
                                                hygieneRating = mainDetails.getAsJsonObject("en").get("hygieneRating").getAsString();
                                            }

                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("aboutRestaurant")) {
                                                about = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("aboutRestaurant").getAsString();
                                            }

                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("restaurantName")) {
                                                restaurantName = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("restaurantName").getAsString();
                                            }

                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("facilities") &&
                                                    !mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("facilities").isJsonNull()) {
                                                facilitiesDetails = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("facilities").getAsJsonArray();
                                            }

                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("cuisines") &&
                                                    !mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("cuisines").isJsonNull()) {
                                                list = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("cuisines").getAsJsonArray();
                                            }

                                            if (mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).has("additionalService") &&
                                                    !mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("additionalService").isJsonNull()) {
                                                JsonArray serviceArray = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).get("additionalService").getAsJsonArray();
                                                if (serviceArray.size() > 0) {
                                                    for (int ser = 0; ser < serviceArray.size(); ser++) {
                                                        serviceMap.put(serviceArray.get(ser).getAsJsonObject().get("id").getAsInt(),
                                                                serviceArray.get(ser).getAsJsonObject().get("name").getAsString());
                                                    }
                                                }
                                            }
                                        }
                                        JsonArray tableMapArray = new JsonArray();
                                        if (arrayMain.get(i).getAsJsonObject().has("tableMapImages") &&
                                                !arrayMain.get(i).getAsJsonObject().get("tableMapImages").isJsonNull() &&
                                                arrayMain.get(i).getAsJsonObject().get("tableMapImages").isJsonArray()) {
                                            tableMapArray = arrayMain.get(i).getAsJsonObject().
                                                    get("tableMapImages").getAsJsonArray();
                                        }

                                        List<OTableMap> tableMapImages = new ArrayList<>();
                                        if (tableMapArray.size() > 0) {
                                            for (int tab = 0; tab < tableMapArray.size(); tab++) {
                                                if (tableMapArray.get(tab).isJsonObject()) {
                                                    String floorName = "";
                                                    if (tableMapArray.get(tab).getAsJsonObject().has("floorNo") &&
                                                            !tableMapArray.get(tab).getAsJsonObject().get("floorNo").isJsonNull()) {
                                                        floorName = tableMapArray.get(tab).getAsJsonObject().get("floorNo").getAsString();
                                                    }
                                                    if (tableMapArray.get(tab).getAsJsonObject().has("rooms") &&
                                                            !tableMapArray.get(tab).getAsJsonObject().get("rooms").isJsonNull() &&
                                                            tableMapArray.get(tab).getAsJsonObject().get("rooms").isJsonArray()) {
                                                        JsonArray roomsArray = tableMapArray.get(tab).getAsJsonObject().get("rooms").getAsJsonArray();
                                                        if (roomsArray.size() > 0) {
                                                            for (int rm = 0; rm < roomsArray.size(); rm++) {
                                                                tableMapImages.add(new OTableMap(floorName,
                                                                        roomsArray.get(rm).getAsJsonObject().get("roomNo").getAsString(),
                                                                        roomsArray.get(rm).getAsJsonObject().get("imageUrl").getAsString()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        JsonObject addressDetails = new JsonObject();
                                        StringBuilder addressBuilder = new StringBuilder();
                                        if (arrayMain.get(i).getAsJsonObject().has("address") &&
                                                !arrayMain.get(i).getAsJsonObject().get("address").isJsonNull()) {
                                            if (arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().has("restaurantAddress") &&
                                                    !arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().get("restaurantAddress").isJsonNull()) {
                                                addressDetails = arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().get("restaurantAddress").getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject();
                                                if (addressDetails.has("addressLine1"))
                                                    addressBuilder.append(addressDetails.get("addressLine1").getAsString() + ", ");
                                                if (addressDetails.has("addressLine2"))
                                                    addressBuilder.append(addressDetails.get("addressLine2").getAsString() + ", ");
                                                if (addressDetails.has("city")) {
                                                    addressCity = addressDetails.get("city").getAsString();
                                                    addressBuilder.append(addressDetails.get("city").getAsString() + ", ");
                                                }
                                                if (addressDetails.has("county"))
                                                    addressBuilder.append(addressDetails.get("county").getAsString() + ", ");
                                                if (addressDetails.has("town"))
                                                    addressBuilder.append(addressDetails.get("town").getAsString() + ", ");
                                                if (addressDetails.has("postcode"))
                                                    addressBuilder.append(addressDetails.get("postcode").getAsString());
                                            }
                                        }
                                        HashMap<String, String> facilitiesList = new HashMap<>();
                                        if (facilitiesDetails.size() > 0) {
                                            for (int fc = 0; fc < facilitiesDetails.size(); fc++) {
                                                if (facilitiesDetails.get(fc).isJsonObject()) {
                                                    Log.d("<>log-", " facilities ==> " + facilitiesDetails.get(fc).getAsJsonObject().getAsJsonObject().get("name").getAsString());
                                                    String replaceString = facilitiesDetails.get(fc).getAsJsonObject().get("icon").getAsString().replace("-", "_");
                                                    facilitiesList.put(facilitiesDetails.get(fc).getAsJsonObject().get("icon").getAsString()
                                                            , facilitiesDetails.get(fc).getAsJsonObject().getAsJsonObject().get("name").getAsString());
                                                }
                                            }
                                        }

                                        List<String> cuisineList = new ArrayList<>();
                                        for (int cuisine = 0; cuisine < list.size(); cuisine++) {
                                            cuisineList.add(list.get(cuisine).getAsString());
                                        }

                                        Log.d("<>dist-", arrayMain.get(i).getAsJsonObject().get("dist").getAsDouble() + "");
                                        if (addressDetails != null) {
                                            Log.d("<>log-", " addressDetails details ===?> " + addressDetails + "");
                                            if (isBookTable == true) {
                                                dataModels.add(new RestaurantDetailModel(
                                                        arrayMain.get(i).getAsJsonObject().get("_id").getAsString(),
                                                        arrayMain.get(i).getAsJsonObject().get("restaurant_id").getAsString(),
                                                        restaurantName,
                                                        String.format("%.2f", arrayMain.get(i).getAsJsonObject().get("dist").getAsDouble()),
                                                        logoFull,
                                                        addressBuilder.toString()
                                                /*addressDetails.get("addressLine1").getAsString() + addressDetails.get("addressLine2").getAsString() +
                                                        addressDetails.get("town").getAsString()*/,
                                                        latlangBounds.get(0).getAsString(),
                                                        latlangBounds.get(1).getAsString(),
                                                        hygieneRating,
                                                        cuisineList,
                                                        facilitiesList,
                                                        timeTables,
                                                        isOpenDay,
                                                        collection,
                                                        delivery,
                                                        collectionDiscount,
                                                        averageCollectionTime,
                                                        deliveryCharge,
                                                        deliveryDistance,
                                                        averageDeliveryTime,
                                                        likedBy,
                                                        averagePriceForFour,
                                                        averagePriceForTwo,
                                                        isMembership,
                                                        isTakeaway,
                                                        isBookTable,
                                                        listImages,
                                                        numberOfReviews,
                                                        about,
                                                        serviceMap,
                                                        tableMapImages,
                                                        addressCity
                                                ));
                                            }
                                        }
                                    }
                                }
                            }
                            String myFormat = "dd-MMM-yyyy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                            Boolean isBookToday = true;
                            try {
                                Date bookDate = sdf.parse(date);
                                if (bookDate.after(new Date())) {
                                    Log.d("<>test-", " it is tomorrows date");
                                    isBookToday = false;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (SharedData.getSortingType(getActivity()).equals(getString(R.string.alphabetically_title))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        return restaurantDetailModel.getTitle().toString().compareTo(t1.getTitle().toString());
                                    }
                                });
                            } else if (SharedData.getSortingType(getActivity()).equals(getString(R.string.distance))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        if (restaurantDetailModel.getDistance() == t1.getDistance()) {
                                            return restaurantDetailModel.getTitle().compareTo(t1.getTitle());
                                        } else if (Double.parseDouble(restaurantDetailModel.getDistance()) > Double.parseDouble(t1.getDistance())) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    }
                                });
                            } else if (SharedData.getSortingType(getActivity()).equals(getString(R.string.title_newest_first))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        return t1.get_id().toString().compareTo(restaurantDetailModel.get_id().toString());
                                    }
                                });
                            } else if (SharedData.getSortingType(getActivity()).equals(getString(R.string.title_price_high_to_low))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        return t1.getAveragePriceForTwo().compareTo(restaurantDetailModel.getAveragePriceForTwo());
                                    }
                                });
                            } else if (SharedData.getSortingType(getActivity()).equals(getString(R.string.title_price_low_to_high))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        return restaurantDetailModel.getAveragePriceForTwo().compareTo(t1.getAveragePriceForTwo());
                                    }
                                });
                            } else if (SharedData.getSortingType(getActivity()).equals(getString(R.string.title_popularity))) {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        if (restaurantDetailModel.getLikedBy().size() == t1.getLikedBy().size()) {
                                            return restaurantDetailModel.getTitle().compareTo(t1.getTitle());
                                        } else if (restaurantDetailModel.getLikedBy().size() > t1.getLikedBy().size()) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    }
                                });
                            } else {
                                Collections.sort(dataModels, new Comparator<RestaurantDetailModel>() {
                                    @Override
                                    public int compare(RestaurantDetailModel restaurantDetailModel, RestaurantDetailModel t1) {
                                        if (restaurantDetailModel.getDistance() == t1.getDistance()) {
                                            return restaurantDetailModel.getTitle().compareTo(t1.getTitle());
                                        } else if (Double.parseDouble(restaurantDetailModel.getDistance()) > Double.parseDouble(t1.getDistance())) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    }
                                });
                            }
                            if (loadingSpinner != null)
                                dismissSpinner();
                            recyclerAdapter = new RestaurantDeatilsRecyclerAdapter(dataModels, getActivity(), isBookToday, time, date, people, callbackInterface, timeSlotInterface);
                            recyclerViewRestaurant.setAdapter(recyclerAdapter);
                            txtNoFound.setVisibility(View.GONE);
                            init();
                        }
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                } else if (response.code() == 503) {
                    Toast.makeText(getActivity(), R.string.service_unavilable, Toast.LENGTH_LONG).show();
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        Activity activity = getActivity();
        if (activity != null) {
            loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
            loadingSpinner.setCancelable(false);
            loadingSpinner.show();
        }
    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // some code
        relativeLayoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filter = new Intent(getActivity(), FilterDetails.class);
                getActivity().overridePendingTransition(R.anim.stay, R.anim.slide_up);
                startActivityForResult(filter, FILTER_REQUEST_CODE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        Log.d("<>onAct-", " on Activity returnedData id ===> ");
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("<>filter-", " result ok ");
                getRestaurantByLocation(longitude, latitude);
            } else {
                // You can handle a case where no selection was made if you want
                Log.d("<>filter-", " user cancelled");
                Log.d("<>filter-", " cuisine list ==> " + cuisines.toString());
            }
        } else if (requestCode == LOGIN_REQUEST_CODE) {
            recyclerAdapter.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                Log.d("<>abt-", " result ok ");
                getActivity().findViewById(R.id.menuRight).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.menuLogin).setVisibility(View.GONE);
//                NavigationView navigationView = (NavigationView) activity.findViewById(R.id.yournavigationviewid);
//                navigationView.getMenu().findItem(R.id.youritemid).setVisible(false);
                TextView textUser = (TextView) getActivity().findViewById(R.id.textViewLabel);
                textUser.setVisibility(View.VISIBLE);
                Log.d("<>abt-", " textUser name ===> " + data.getStringExtra("name"));
                textUser.setText(getString(R.string.welcome_title) + " " + data.getStringExtra("name"));
                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.pre_login_nav_view);
                navigationView.getMenu().findItem(R.id.ic_action_change_password).setVisible(true);
                navigationView.getMenu().findItem(R.id.ic_action_notification).setVisible(true);
//                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.pre_drawer_layout);
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                getRestaurantByLocation(longitude, latitude);
            } else {
                // You can handle a case where no selection was made if you want
                Log.d("<>abt-", " user cancelled");
            }
        }
    }


    @Override
    public Boolean onHandleSelection(String restaurantId) {
        Log.d("<>tag-", "on handle in restaurant fragment");
        Intent filter = new Intent(getActivity(), LoginAsDialog.class);
        startActivityForResult(filter, LOGIN_REQUEST_CODE);
        return true;
    }

    @Override
    public void onRestaurantDislike(String text, Integer position) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());

        makeRestaurantDisLike(userId, text, position);
    }

    @Override
    public void onRestaurantLike(String text, Integer position) {
        Log.d("<>mm-", " in restaurant liked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());

        makeRestaurantLike(userId, text, position);
       /* JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("userId", "5ad480f33210f83dcd4682f8");
        bodyJson.addProperty("restaurantId", text);
        new UnavialableTable().execute(bodyJson);*/
    }

    @Override
    public void onAddRestaurantFavourite(String text, Integer position) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());
        makeRestaurantFavourite(userId, text, position);
    }

    @Override
    public void onAddRestaurantFavouriteRemove(String text, Integer position) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());
        makeRestaurantFavouriteRemove(userId, text, position);
    }

    private void makeRestaurantDisLike(String userId, final String restaurantId, Integer position) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject innerData = new JsonObject();
        innerData.addProperty("userId", userId);
        innerData.addProperty("restaurantId", restaurantId);

        Log.d("<>mm-", " final data for search ===> " + innerData.toString());

        Call<JsonElement> mService = mInterfaceService.setRestaurantDislike(innerData);
        Log.d("<>mm-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>mm-", " Request Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>mm-", " response msg ===> " + response.message());
                Log.d("<>mm-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    if (response.code() == 200) {
                        Log.d("<>mm-", " response body ===> " + response.body() + "");
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("disliked")) {
                            Log.d("<>mm-", " disliked");
                            getRestaurantByLocation(longitude, latitude);
                        }
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeRestaurantLike(String userId, String restaurantId, Integer position) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject innerData = new JsonObject();
        innerData.addProperty("userId", userId);
        innerData.addProperty("restaurantId", restaurantId);

        Log.d("<>mm-", " final data for search ===> " + innerData.toString());

        Call<JsonElement> mService = mInterfaceService.setRestaurantLike(innerData);
        Log.d("<>mm-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>mm-", " Request Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>mm-", " response msg ===> " + response.message());
                Log.d("<>mm-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    if (response.code() == 200) {
                        Log.d("<>mm-", " response body ===> " + response.body() + "");
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("liked")) {
                            Log.d("<>mm-", "liekd in restaurant ");
                        }
                        getRestaurantByLocation(longitude, latitude);
//                        recyclerAdapter.notifyItemChanged(position);

                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeRestaurantFavourite(String userId, final String restaurantId, Integer position) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject innerData = new JsonObject();
        innerData.addProperty("userId", userId);
        innerData.addProperty("restaurantId", restaurantId);

        Log.d("<>rest-", " final data for search ===> " + innerData.toString());

        Call<JsonElement> mService = mInterfaceService.setFavouriteRestaurant(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body() + "");
        Log.d("<>login-", " Headers is  ==> " + mService.request().headers().toString());
        Log.d("<>login-", " contentType is  ==> " + mService.request().body().contentType() + "");
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response headers ===> " + response.headers().toMultimap().get("Set-Cookie"));
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of login ");
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("success")) {
                            Log.d("<>log-", "add to favourite in restaurant ");
                            List<OUser> userList = MyApplication.getGlobalData().getUserData();
                            List<String> favourites = new ArrayList<>();
                            for (int user = 0; user < userList.size(); user++) {
                                favourites = userList.get(user).getFavouriteRestaurants();
                            }
                            favourites.add(restaurantId);
                        }
//                        getRestaurantByLocation(longitude, latitude);
                        recyclerAdapter.notifyItemChanged(position);
//                        recyclerAdapter.notifyDataSetChanged();
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeRestaurantFavouriteRemove(String userId, final String restaurantId, Integer position) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject innerData = new JsonObject();
        innerData.addProperty("userId", userId);
        innerData.addProperty("restaurantId", restaurantId);

        Log.d("<>like-", " final data for search ===> " + innerData.toString());

        Call<JsonElement> mService = mInterfaceService.removeFavouriteRestaurant(innerData);
        Log.d("<>like-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>like-", " Request Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>like-", " response msg ===> " + response.message());
                Log.d("<>like-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>like-", " in response of login ");
                    if (response.code() == 200) {
                        Log.d("<>like-", " response body ===> " + response.body() + "");
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("success")) {
                            Log.d("<>like-", "remove to favourite in restaurant ");
                            List<OUser> userList = MyApplication.getGlobalData().getUserData();
                            List<String> favourites = new ArrayList<>();
                            for (int user = 0; user < userList.size(); user++) {
                                favourites = userList.get(user).getFavouriteRestaurants();
                            }
                            Log.d("<>like-", " favourite list == > " + favourites.toString());
                            Log.d("<>like-", " restaurant id  == > " + restaurantId.toString());
                            if (favourites.contains(restaurantId)) {
                                Log.d("<>like-", " favourite inside == > ");
                                favourites.remove(restaurantId);
                                Log.d("<>like-", " user data list == > " + userList.toString());
//                                MyApplication.getGlobalData().addUserDataList(userList);
                            }
                        }
//                        getRestaurantByLocation(longitude, latitude);
                        recyclerAdapter.notifyItemChanged(1);
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pre_login_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #D3D3D3 >" + "<small>" + "Search by Restaurant Name" + "</small>" + "</font>"));
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                recyclerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (recyclerAdapter != null) {
                    recyclerAdapter.getFilter().filter(query);
                    Log.d("<>log-", " on serach view item count in adapter ---> " + recyclerAdapter.getItemCount() + "");
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        textSearch.setText(recyclerAdapter.getItemCount() + " " + getString(R.string.title_Restaurants));
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.getActivity().onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        recyclerAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
       /* String text = newText;
        if (text.length() > 0)
            recyclerAdapter.filter(text);*/

        if (recyclerAdapter != null) {
            recyclerAdapter.getFilter().filter(newText);
            Log.d("<>log-", " on serach view item count in adapter ---> " + recyclerAdapter.getItemCount() + "");
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                textSearch.setText(recyclerAdapter.getItemCount() + " " + getString(R.string.title_Restaurants));
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        return false;
    }

    @Override
    public void onOpenBookATableForm(String restaurantId, String restaurantName, Integer position) {
        Intent filter = new Intent(getActivity(), BookATable.class);
        filter.putExtra("restaurantId", restaurantId);
        filter.putExtra("people", people);
        filter.putExtra("date", date);
        filter.putExtra("name", restaurantName);
        startActivity(filter);
    }

    @Override
    public void onTimeSlotClick(String restaurantId) {
        Log.d("<>tag-", "on handle in restaurant fragment");
        Intent filter = new Intent(getActivity(), LoginAsDialog.class);
        startActivityForResult(filter, LOGIN_REQUEST_CODE);
    }

    ProgressDialog progressDialogtableUnavailable;

    private class UnavialableTable extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialogtableUnavailable = ProgressDialog.show(
                    getActivity(), "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {
                OkHttpClient client = new OkHttpClient();

                Request.Builder request = new Request.Builder();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), parametros[0].toString());
                Log.d("data send--", "" + parametros[0].toString());
                request.url(APIConstants.URL + "/api/restaurant-details/updateRestaurant/like")
                        .addHeader("Content-Type", "application/json");
                request.post(body)
                        .build();

                try (okhttp3.Response response = client.newCall(request.build()).execute()) {
                    response.code();
                    Log.d("Response Code", "" + response.code());
                    resUnavilabletable = response.body().string();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUnavilabletable;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try {
                Log.d("<> RES resUnavilabletable---", resUnavilabletable);
                //if(res)
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialogtableUnavailable.dismiss();
        }
    }
}

package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantByLocationModel;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RestaurantDetails extends AppCompatActivity implements BottomSheetAdapter.ItemListener{
    RecyclerView recyclerViewRestaurant;
    SwipeRefreshLayout refreshLayout;
    RelativeLayout relativeLayoutSortBy, relativeLayoutFilter;
    TextView textSearch, txtNoFound;

    private static String TAG = "Details";
    private ArrayList<RestaurantDetailModel> dataModels;
    private BottomSheetAdapter itemAdapter;
    BottomSheetBehavior behavior;
    private BottomSheetDialog mBottomSheetDialog;
    private Intent extras;
    private ProgressDialog loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        setContentView(R.layout.activity_restaurant_details);
        findIds();
//        ButterKnife.bind(RestaurantDetails.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        extras = getIntent();
        if (extras != null){
            String time = extras.getStringExtra("time");
            String date = extras.getStringExtra("date");
            String people = extras.getStringExtra("people");
            Double longitude = extras.getDoubleExtra("longitude", 0.0);
            Double latitude = extras.getDoubleExtra("latitude", 0.0);
            Log.d(TAG, " logitude ==> "+longitude + " latitude ==> "+latitude+"");
            Log.d(TAG, " time ===> "+time+" date ====> "+date+" people ==> "+people);
            textSearch.setText("You are looking for restaurant at Ahmedabad for "+people+ " people on"+
            " 13th November, 2017 at"+time);
            getRestaurantByLocation(longitude, latitude);
        }
    }

    public void init(){
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        double f = Math.random();
                    }
                }, 6000);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(RestaurantDetails.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setLayoutManager(llm);
        View bottomSheet = findViewById(R.id.bottom_sheet);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_sheet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*itemAdapter = new BottomSheetAdapter(createItems(), this, RestaurantDetails.this);
        recyclerView.setAdapter(itemAdapter);*/

        relativeLayoutSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        relativeLayoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFliterDialog();
            }
        });
    }

    private void findIds(){
        recyclerViewRestaurant = findViewById(R.id.recycler_view_restaurant);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        textSearch = findViewById(R.id.textSearch);
        txtNoFound = findViewById(R.id.txtNoFound);
        relativeLayoutSortBy =findViewById(R.id.relativeLayoutSortBy);
        relativeLayoutFilter = findViewById(R.id.relativeLayoutFilter);
    }
    private void showFliterDialog(){
        Intent i = new Intent(RestaurantDetails.this, FilterDetails.class);
        startActivity(i);
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }

    private void showBottomSheetDialog() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        /*mBottomSheetDialog = new BottomSheetDialog(RestaurantDetails.this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_sheet);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomSheetAdapter(createItems(), new BottomSheetAdapter.ItemListener() {
            @Override
            public void onItemClick(BottomSheetPOJO item) {
                if (mBottomSheetDialog != null) {
                    Log.d("<>log-", " in item click");
                    if (BottomSheetAdapter.selected_item == item.getmState())
                        BottomSheetAdapter.selected_item = 0;
                    else
                        BottomSheetAdapter.selected_item = item.getmState();
                    mBottomSheetDialog.dismiss();
                }
            }
        }, RestaurantDetails.this));

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });*/
    }

    public void showBottomSheet(){
        new BottomSheet.Builder(RestaurantDetails.this, R.style.BottomSheet_StyleDialog)
                .title(R.string.sort_by_title)
                .sheet(R.menu.bottom_sheet_menu)
                .listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.popularity:
                        Log.d("<>bottomsheet-", "popularity !");
                        break;
                    case R.id.priceHtoL:
                        Log.d("<>bottomsheet-", "price High to Low !");
                        break;
                    case R.id.priceLtoH:
                        Log.d("<>bottomsheet-", "price Low to High !");
                        break;
                    case R.id.newest:
                        Log.d("<>bottomsheet-", "Newest First !");
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onItemClick(BottomSheetPOJO item) {
        Log.d("<>===", " =========> in item click");
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public List<BottomSheetPOJO> createItems() {

        ArrayList<BottomSheetPOJO> items = new ArrayList<>();
//        items.add(new BottomSheetPOJO(getResources().getString(R.string.sort_by_title), 0));
//        items.add(new BottomSheetPOJO(getResources().getString(R.string.title_popularity), 2));
//        items.add(new BottomSheetPOJO(getString(R.string.title_price_high_to_low), 3));
//        items.add(new BottomSheetPOJO(getString(R.string.title_price_low_to_high), 4));
//        items.add(new BottomSheetPOJO(getString(R.string.title_newest_first), 5));
        return items;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRestaurantByLocation(Double longitude, Double latitude){
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

        final RestaurantByLocationModel data = new RestaurantByLocationModel("en", longitude, latitude, 8046.7, 0.000621, listCuisine, new ArrayList<String>(), listFacility, new JSONObject(), listDiets, false, 20, false);
//        Call<ResponseBody> mService = mInterfaceService.getAuthorisation(login, CookieManager.getCookieAll(), CookieManager.getXsrfToken(), "application/json;charset=utf-8");
        Call<JsonElement> mService = mInterfaceService.getRestaurantByLocation(data);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body()+"");
        Log.d("<>login-", " Headers is  ==> " + mService.request().headers().toString());
        Log.d("<>login-", " contentType is  ==> " + mService.request().body().contentType() + "");
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");

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
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of login ");
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (response.body().getAsJsonArray().size() == 0){
                            Toast.makeText(RestaurantDetails.this, "No restaurant find !!!", Toast.LENGTH_SHORT).show();
                            txtNoFound.setVisibility(View.VISIBLE);
                            init();
                        }else {
                            JsonArray arrayMain = response.body().getAsJsonArray();
                            RestaurantDetails restDetails = new RestaurantDetails();
                            dataModels = new ArrayList<>();

//                            for (int i=0; i < arrayMain.size(); i++){
//                                JsonObject mainDetails = arrayMain.get(i).getAsJsonObject().get("main_details").getAsJsonObject();
//                                Log.d("<>log-", " main details ===?> "+mainDetails+"");
//                                dataModels.add(new RestaurantDetailModel(mainDetails.get("restaurantName").getAsString(),
//                                        String.format("%.2f", arrayMain.get(i).getAsJsonObject().get("dist").getAsDouble())));
//                            }
                            /*RestaurantDeatilsRecyclerAdapter adapter = new RestaurantDeatilsRecyclerAdapter(dataModels, RestaurantDetails.this, false);
                            recyclerViewRestaurant.setAdapter(adapter);*/
                            txtNoFound.setVisibility(View.GONE);
                            init();
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
                Toast.makeText(RestaurantDetails.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(RestaurantDetails.this, R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }
}

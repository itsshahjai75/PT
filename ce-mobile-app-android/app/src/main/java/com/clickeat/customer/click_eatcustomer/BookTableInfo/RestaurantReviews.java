package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.AllReviews;
import com.clickeat.customer.click_eatcustomer.DataModel.OReview;
import com.clickeat.customer.click_eatcustomer.LoginAsDialog;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestaurantReviews extends Fragment {

    private View m_myFragmentView;
    private String restaurantName, restaurantId, noReviews;
    RecyclerView listReviews;
    private RestaurantReviewsAdapter reviewsAdapter;
    private static final String ARG_PARAM_Name = "name";
    private static final String ARG_PARAM_RESTAURANT_ID = "restaurantId";
    private static final String ARG_PARAM_NO_REVIEWS = "noReviews";
    private static int LOGIN_REQUEST_CODE = 123;
    Button btnRvWrite;
    TextView txtNoReviews, txtRevCount;
    private ProgressDialog loadingSpinner;
    private TextView txtRVOverall, txtRvLocation, txtRvFood, txtRvStaff, txtRvFacilities, txtRvService;
    Spinner spinnerRevSort, spinnerRevLimit;
    private LinearLayout layoutOL, layoutLo, layoutFo, layoutStaff, layoutService, layoutFacilities;

    public static RestaurantReviews newInstance(String restaurantName, String restaurantId, String noReviews) {
        RestaurantReviews fragment = new RestaurantReviews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_Name, restaurantName);
        args.putString(ARG_PARAM_RESTAURANT_ID, restaurantId);
        args.putString(ARG_PARAM_NO_REVIEWS, noReviews);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantName = getArguments().getString(ARG_PARAM_Name);
            restaurantId = getArguments().getString(ARG_PARAM_RESTAURANT_ID);
            noReviews = getArguments().getString(ARG_PARAM_NO_REVIEWS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final PreLoginMainActivity activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle(restaurantName + " Reviews");
        m_myFragmentView = inflater.inflate(R.layout.activity_resturant_reviews, container, false);
//        getActivity().setTitle(restaurantName + " Reviews");
        findIds();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listReviews.getContext(),
                llm.getOrientation());
        listReviews.addItemDecoration(dividerItemDecoration);*/
        listReviews.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        listReviews.setLayoutManager(llm);
        ArrayAdapter limitAdapter = new ArrayAdapter(getActivity(), R.layout.layout_spinner_reviews,
                getResources().getStringArray(R.array.spinner_review_limit));
        spinnerRevLimit.setAdapter(limitAdapter);

        ArrayAdapter shortAdapter = new ArrayAdapter(getActivity(), R.layout.layout_spinner_reviews,
                getResources().getStringArray(R.array.spinner_review_sort));
        spinnerRevSort.setAdapter(shortAdapter);
//        getAllReviews();
        spinnerRevLimit.setSelection(1);
        spinnerRevLimit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    getAllReviews("all");
                else if (position == 1)
                    getAllReviews("lastweek");
                else if (position ==2)
                    getAllReviews("lastmonth");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return m_myFragmentView;
    }

    private void init() {
        txtRevCount.setText(noReviews);
        ArrayList<AllReviews> allReviewsList = new ArrayList<>();
        allReviewsList = MyApplication.getGlobalData().getAllReviews();
        if (allReviewsList.size() > 0) {
            txtNoReviews.setVisibility(View.GONE);
            listReviews.setVisibility(View.VISIBLE);
            layoutFacilities.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getFacilitiesRating())));
            txtRvFacilities.setText(allReviewsList.get(0).getFacilitiesRating());
            layoutFo.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getFoodRating())));
            txtRvFood.setText(allReviewsList.get(0).getFoodRating());
            layoutLo.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getLocationRating())));
            txtRvLocation.setText(allReviewsList.get(0).getLocationRating());
            layoutOL.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getOverallRating())));
            txtRVOverall.setText(allReviewsList.get(0).getOverallRating());
            layoutService.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getServiceRating())));
            txtRvService.setText(allReviewsList.get(0).getServiceRating());
            layoutStaff.setBackground(setBGDrawable(Float.parseFloat(allReviewsList.get(0).getStaffRating())));
            txtRvStaff.setText(allReviewsList.get(0).getStaffRating());
            List<OReview> reviewList = new ArrayList<>();
            reviewList = allReviewsList.get(0).getReviews();
            Collections.sort(reviewList, new Comparator<OReview>() {
                @Override
                public int compare(OReview reviewModel, OReview t1) {
                    if (reviewModel.getReviewDate() == null || t1.getReviewDate() == null)
                        return 0;
                    return t1.getReviewDate().compareTo(reviewModel.getReviewDate());
                }
            });
            reviewsAdapter = new RestaurantReviewsAdapter(getActivity(), reviewList);
            listReviews.setAdapter(reviewsAdapter);
        } else {
            txtNoReviews.setVisibility(View.VISIBLE);
            listReviews.setVisibility(View.GONE);
            layoutFacilities.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRvFacilities.setText("0.0");
            layoutFo.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRvFood.setText("0.0");
            layoutLo.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRvLocation.setText("0.0");
            layoutOL.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRVOverall.setText("0.0");
            layoutService.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRvService.setText("0.0");
            layoutStaff.setBackground(setBGDrawable(Float.parseFloat("0.0")));
            txtRvStaff.setText("0.0");
        }
        btnRvWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "";
                userId = SharedData.getUserId(getActivity());
                if (SharedData.getIsLoggedIn(getActivity()) && !userId.equals("")) {
                    FragmentActivity activity = (FragmentActivity) (getActivity());
                    Fragment fragment = RestaurantWriteReview.newInstance(restaurantName, restaurantId, noReviews);
                    FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Intent filter = new Intent(getActivity(), LoginAsDialog.class);
                    startActivityForResult(filter, LOGIN_REQUEST_CODE);
                }
            }
        });
    }

    private Drawable setBGDrawable(float rating) {
        Drawable myDrawable;
        if (rating <= 1.9) {
            myDrawable = getResources().getDrawable(R.drawable.rating_rad_style);
        } else if (rating >= 2.0 && rating < 3.0) {
            myDrawable = getResources().getDrawable(R.drawable.rating_yellow_style);
        } else {
            myDrawable = getResources().getDrawable(R.drawable.rating_green_style);
        }
        return myDrawable;
    }

    private void findIds() {
        listReviews = m_myFragmentView.findViewById(R.id.listReviews);
        btnRvWrite = m_myFragmentView.findViewById(R.id.btnRvWrite);
        txtRVOverall = m_myFragmentView.findViewById(R.id.txtRVOverall);
        txtRvLocation = m_myFragmentView.findViewById(R.id.txtRvLocation);
        txtRvFood = m_myFragmentView.findViewById(R.id.txtRvFood);
        txtRvStaff = m_myFragmentView.findViewById(R.id.txtRvStaff);
        txtRvFacilities = m_myFragmentView.findViewById(R.id.txtRvFacilities);
        txtRvService = m_myFragmentView.findViewById(R.id.txtRvService);
        spinnerRevSort = m_myFragmentView.findViewById(R.id.spinnerRevSort);
        spinnerRevLimit = m_myFragmentView.findViewById(R.id.spinnerRevLimit);
        layoutOL = m_myFragmentView.findViewById(R.id.layoutOL);
        layoutLo = m_myFragmentView.findViewById(R.id.layoutLo);
        layoutFo = m_myFragmentView.findViewById(R.id.layoutFo);
        layoutStaff = m_myFragmentView.findViewById(R.id.layoutStaff);
        layoutService = m_myFragmentView.findViewById(R.id.layoutService);
        layoutFacilities = m_myFragmentView.findViewById(R.id.layoutFacilities);
        txtNoReviews = m_myFragmentView.findViewById(R.id.txtNoReviews);
        txtRevCount = m_myFragmentView.findViewById(R.id.txtRevCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == LOGIN_REQUEST_CODE) {
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
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,  GravityCompat.END);
                FragmentActivity activity = (FragmentActivity) (getActivity());
                Fragment fragment = RestaurantWriteReview.newInstance(restaurantName, restaurantId, txtNoReviews+"");
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                // You can handle a case where no selection was made if you want
                Log.d("<>abt-", " user cancelled");
            }
        }
    }

    private void getAllReviews(String filterBy) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject reviewFilter = new JsonObject();
        reviewFilter.addProperty("filtertype", filterBy);
        Call<JsonElement> mService = mInterfaceService.getAllCustomerReviews(restaurantId, reviewFilter);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (loadingSpinner != null)
                            dismissSpinner();
                        JsonObject object = response.body().getAsJsonObject();
                        List<AllReviews> allReviews = new ArrayList<>();
                        List<OReview> reviews = new ArrayList<>();
                        if (object.has("reviews") && !object.get("reviews").isJsonNull()
                                && object.get("reviews").isJsonArray()) {
                            JsonArray reviewList = object.get("reviews").getAsJsonArray();
                            if (reviewList.size() > 0) {

                                for (int i = 0; i < reviewList.size(); i++) {
                                    String restaurant_id = "", userId = "", customerName = "", reviewDate = "", suggestions = "", positives = "";
                                    float overallRating = 0f, locationRating = 0f, foodRating = 0f;
                                    float staffRating = 0f, serviceRating = 0f, facilitiesRating = 0f;

                                    if (reviewList.get(i).getAsJsonObject().has("restaurant_id") &&
                                            !reviewList.get(i).getAsJsonObject().get("restaurant_id").isJsonNull()) {
                                        restaurant_id = reviewList.get(i).getAsJsonObject().get("restaurant_id").getAsString();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("userId") &&
                                            !reviewList.get(i).getAsJsonObject().get("userId").isJsonNull()) {
                                        userId = reviewList.get(i).getAsJsonObject().get("userId").getAsString();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("customer") &&
                                            !reviewList.get(i).getAsJsonObject().get("customer").isJsonNull()) {
                                        customerName = reviewList.get(i).getAsJsonObject().get("customer").getAsString();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("reviewDate") &&
                                            !reviewList.get(i).getAsJsonObject().get("reviewDate").isJsonNull()) {
                                        reviewDate = reviewList.get(i).getAsJsonObject().get("reviewDate").getAsString();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("overallRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("overallRating").isJsonNull()) {
                                        overallRating = reviewList.get(i).getAsJsonObject().get("overallRating").getAsFloat();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("locationRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("locationRating").isJsonNull()) {
                                        locationRating = reviewList.get(i).getAsJsonObject().get("locationRating").getAsFloat();
                                    }

                                    if (reviewList.get(i).getAsJsonObject().has("foodRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("foodRating").isJsonNull()) {
                                        foodRating = reviewList.get(i).getAsJsonObject().get("foodRating").getAsFloat();
                                    }
                                    if (reviewList.get(i).getAsJsonObject().has("staffRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("staffRating").isJsonNull()) {
                                        staffRating = reviewList.get(i).getAsJsonObject().get("staffRating").getAsFloat();
                                    }
                                    if (reviewList.get(i).getAsJsonObject().has("serviceRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("serviceRating").isJsonNull()) {
                                        serviceRating = reviewList.get(i).getAsJsonObject().get("serviceRating").getAsFloat();
                                    }
                                    if (reviewList.get(i).getAsJsonObject().has("facilitiesRating") &&
                                            !reviewList.get(i).getAsJsonObject().get("facilitiesRating").isJsonNull()) {
                                        facilitiesRating = reviewList.get(i).getAsJsonObject().get("facilitiesRating").getAsFloat();
                                    }
                                    if (reviewList.get(i).getAsJsonObject().has("suggestions") &&
                                            !reviewList.get(i).getAsJsonObject().get("suggestions").isJsonNull()) {
                                        suggestions = reviewList.get(i).getAsJsonObject().get("suggestions").getAsString();
                                    }
                                    if (reviewList.get(i).getAsJsonObject().has("positives") &&
                                            !reviewList.get(i).getAsJsonObject().get("positives").isJsonNull()) {
                                        positives = reviewList.get(i).getAsJsonObject().get("positives").getAsString();
                                    }
                                    List<String> imagesPath = new ArrayList<>();
                                    if (reviewList.get(i).getAsJsonObject().has("images") &&
                                            !reviewList.get(i).getAsJsonObject().get("images").isJsonNull()) {
                                        JsonArray paths = reviewList.get(i).getAsJsonObject().get("images").getAsJsonArray();
                                        if (paths.size() > 0) {
                                            for (int idx = 0; idx < paths.size(); idx++) {
                                                imagesPath.add(paths.get(idx).getAsString());
                                            }
                                        }
                                    }

                                    reviews.add(new OReview(
                                            restaurant_id,
                                            userId,
                                            customerName,
                                            reviewDate,
                                            suggestions,
                                            positives,
                                            overallRating,
                                            locationRating,
                                            foodRating,
                                            staffRating,
                                            serviceRating,
                                            facilitiesRating,
                                            imagesPath));
                                }
                                if (object.has("averageRatings") && !object.get("averageRatings").isJsonNull()
                                        && object.get("averageRatings").isJsonObject()) {
                                    allReviews.add(new AllReviews(
                                            object.get("averageRatings").getAsJsonObject().get("overallRating").getAsString(),
                                            object.get("averageRatings").getAsJsonObject().get("locationRating").getAsString(),
                                            object.get("averageRatings").getAsJsonObject().get("foodRating").getAsString(),
                                            object.get("averageRatings").getAsJsonObject().get("staffRating").getAsString(),
                                            object.get("averageRatings").getAsJsonObject().get("serviceRating").getAsString(),
                                            object.get("averageRatings").getAsJsonObject().get("facilitiesRating").getAsString(),
                                            reviews
                                    ));
                                }

                            }
                        }

                        MyApplication.getGlobalData().addAllReviews(allReviews);
                        init();
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                } else if (response.code() == 500) {
                    Log.d("<>log-", " Internal Server");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        }
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
}

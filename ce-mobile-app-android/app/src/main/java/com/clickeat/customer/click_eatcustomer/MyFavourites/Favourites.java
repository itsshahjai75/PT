package com.clickeat.customer.click_eatcustomer.MyFavourites;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.BookATable;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.RestaurantDetails;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.TimeSlotRecyclerAdapter;
import com.clickeat.customer.click_eatcustomer.DataModel.OTableMap;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.LoginAsDialog;
import com.clickeat.customer.click_eatcustomer.MainHome.AppLocationService;
import com.clickeat.customer.click_eatcustomer.MainHome.LocationAddress;
import com.clickeat.customer.click_eatcustomer.MyAccounts.MyAccountFragment;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Favourites extends Fragment implements FavouriteRecyclerAdapter.favouriteInterface, TimeSlotRecyclerAdapter.timeSlotInterface {
    private View m_myFragmentView;
    private PreLoginMainActivity activity;
    private RecyclerView recycler_view_favRestaurant;
    private ProgressDialog loadingSpinner;
    private ArrayList<RestaurantDetailModel> dataModels;
    private FavouriteRecyclerAdapter recyclerAdapter;
    private FavouriteRecyclerAdapter.favouriteInterface callbackInterface;
    private TimeSlotRecyclerAdapter.timeSlotInterface timeSlotInterface;
    AppLocationService appLocationService;
    public static int LOGIN_REQUEST_CODE = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;
    private Double currentLat;
    private Double currentLong;
    private Boolean isGPSon = true;

    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle(getString(R.string.my_favourites));
        m_myFragmentView = inflater.inflate(R.layout.layout_my_favourite_fragment, container, false);
        try {
            callbackInterface = (FavouriteRecyclerAdapter.favouriteInterface) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnListItemSelectedListener");
        }
        findIds();
        if (isNetworkConnected()) {
            if (checkLocationPermission()) {
                getLocations();
            } else {
                if (!isLocationEnabled(getActivity()))
                    showSettingsAlert();
            }
        } else {
            Toast.makeText(getActivity(), "Please enable your network", Toast.LENGTH_SHORT).show();
        }

        return m_myFragmentView;
    }


    // Ask for permission (START DIALOG) in Android APIs >= 23.
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Prompt the user once explanation has been shown.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            }
            Log.d("<>log-", " in  check permission if");
            return false;
        } else {
            Log.d("<>log-", " in check permission else");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    isGPSon = true;
                    getLocations();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    isGPSon = false;
                    getRestaurantByUserId();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void findIds() {
        recycler_view_favRestaurant = m_myFragmentView.findViewById(R.id.recycler_view_favRestaurant);
    }

    public void init() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view_favRestaurant.setItemAnimator(new DefaultItemAnimator());
        recycler_view_favRestaurant.setLayoutManager(llm);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void getLocations() {
        // check if GPS enabled
        appLocationService = new AppLocationService(getActivity());
        if (appLocationService.canGetLocation()) {
            Log.d("<>log-", " in get location");
            double latitude = appLocationService.getLatitude();
            double longitude = appLocationService.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getActivity(), new GeocoderLocationHandler());
            // \n is for new line
//            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            Log.d("<>log-", " in else get location");
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (!isLocationEnabled(getActivity()))
                showSettingsAlert();
        }
    }

    /* that will be alert for Location setting */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                getActivity());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Log.d("<>per-", " dined ==> ");
                    }
                });
        alertDialog.show();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    /*::	This function converts decimal degrees to radians						 :*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*::	This function converts radians to decimal degrees						 :*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }


    /* handler class to listen the Location Listener */
    private class GeocoderLocationHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    Log.d("<>fav-", " latitude ==> " + bundle.getDouble("latitude"));
                    Log.d("<>fav-", " longitude ==> " + bundle.getDouble("longitude"));
                    currentLat = bundle.getDouble("latitude");
                    currentLong = bundle.getDouble("longitude");
                    getRestaurantByUserId();
                    break;
                default:
                    locationAddress = null;
            }
        }
    }


    private void getRestaurantByUserId() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("userId", SharedData.getUserId(getActivity()));
        Log.d("<>fav-", " user id ==> " + SharedData.getUserId(getActivity()));
        Call<JsonElement> mService = mInterfaceService.getFavouriteRestaurantList(paramObject);
        Log.d("<>fav-", " URL is  ==> " + mService.request().url().toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>fav-", " response msg ===> " + response.message());
                Log.d("<>fav-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    Log.d("<>log-", " in response of login ");

                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (response.body().getAsJsonArray().size() == 0) {
                            Toast.makeText(getActivity(), "No restaurant found!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("<>tag-", " on else while body has 0 count");
                            JsonArray arrayMain = response.body().getAsJsonArray();
                            RestaurantDetails restDetails = new RestaurantDetails();
                            dataModels = new ArrayList<>();
                            for (int i = 0; i < arrayMain.size(); i++) {
                                JsonArray facilitiesDetails = new JsonArray();
                                Boolean collection = false;
                                Boolean delivery = false, isMembership = false, isBookTable = false, isTakeaway = false;
                                Integer collectionDiscount = null, averageCollectionTime = null, averageDeliveryTime = null;
                                Integer averagePriceForTwo = 0, averagePriceForFour = 0;
                                List<String> listImages = new ArrayList<>();
                                Double deliveryCharge = null, deliveryDistance = null;
                                Double restLatitude = null, restLongitude = null;
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

                                        JsonObject locationObj = arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().get("restaurantAddress").getAsJsonObject().get("location").getAsJsonObject();
                                        if (locationObj.has("coordinates") &&
                                                !locationObj.get("coordinates").isJsonNull()) {
                                            restLatitude = locationObj.get("coordinates").getAsJsonArray().get(1).getAsDouble();
                                            restLongitude = locationObj.get("coordinates").getAsJsonArray().get(0).getAsDouble();
                                        }

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
                                String distance = "";
                                if (currentLat != null) {
                                    Double disMiles = distance(currentLat, currentLong, restLatitude, restLongitude, "M");
                                    Log.d("<>fav-", " miles of dest ===> " + disMiles + "");
                                    distance = String.format("%.2f", disMiles);
                                }

                                if (addressDetails != null) {
                                    Log.d("<>log-", " addressDetails details ===?> " + addressDetails + "");
                                    if (isBookTable == true) {
                                        dataModels.add(new RestaurantDetailModel(
                                                arrayMain.get(i).getAsJsonObject().get("_id").getAsString(),
                                                arrayMain.get(i).getAsJsonObject().get("restaurant_id").getAsString(),
                                                restaurantName,
                                                distance,
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
                            String myFormat = "dd-MMM-yyyy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                            Boolean isBookToday = true;
                            try {
                                Date bookDate = sdf.parse(sdf.format(new Date()));
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
                            recyclerAdapter = new FavouriteRecyclerAdapter(dataModels, getActivity(), callbackInterface, timeSlotInterface, isGPSon, currentLat, currentLong);
                            recycler_view_favRestaurant.setAdapter(recyclerAdapter);
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
    public Boolean onHandleSelection(String text) {
        Intent filter = new Intent(getActivity(), LoginAsDialog.class);
        startActivityForResult(filter, LOGIN_REQUEST_CODE);
        return true;
    }

    @Override
    public void onRestaurantDislike(String text) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());

        makeRestaurantDisLike(userId, text);
    }

    @Override
    public void onRestaurantLike(String text) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());

        makeRestaurantLike(userId, text);
    }

    @Override
    public void onAddRestaurantFavourite(String text) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());
        makeRestaurantFavourite(userId, text);
    }

    @Override
    public void onAddRestaurantFavouriteRemove(String text) {
        Log.d("<>tag-", " in restaurant disliked restaurant fragment");
        String userId = "";
        userId = SharedData.getUserId(getActivity());
        makeRestaurantFavouriteRemove(userId, text);
    }

    @Override
    public void onOpenBookATableForm(String restaurantId, String restaurantName) {
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDate = inFormat.format(new Date());
        Intent filter = new Intent(getActivity(), BookATable.class);
        filter.putExtra("restaurantId", restaurantId);
        filter.putExtra("people", "2");
        filter.putExtra("date", currentDate);
        filter.putExtra("name", restaurantName);
        startActivity(filter);
    }


    private void makeRestaurantDisLike(String userId, final String restaurantId) {
        final Integer[] result = {0};
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

        Call<JsonElement> mService = mInterfaceService.setRestaurantDislike(innerData);
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
                        if (status.equals("disliked")) {
                            Log.d("<>log-", " disliked");
                            getRestaurantByUserId();
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

    private void makeRestaurantLike(String userId, String restaurantId) {
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

        Call<JsonElement> mService = mInterfaceService.setRestaurantLike(innerData);
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
                        if (status.equals("liked")) {
                            Log.d("<>log-", "liekd in restaurant ");
                        }
                        getRestaurantByUserId();
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

    private void makeRestaurantFavourite(String userId, final String restaurantId) {
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
                        getRestaurantByUserId();
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

    private void makeRestaurantFavouriteRemove(String userId, final String restaurantId) {
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

        Call<JsonElement> mService = mInterfaceService.removeFavouriteRestaurant(innerData);
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
                            Log.d("<>log-", "remove to favourite in restaurant ");
                            List<OUser> userList = MyApplication.getGlobalData().getUserData();
                            List<String> favourites = new ArrayList<>();
                            for (int user = 0; user < userList.size(); user++) {
                                favourites = userList.get(user).getFavouriteRestaurants();
                            }
                            if (favourites.contains(restaurantId)) {
                                favourites.remove(restaurantId);
                                MyApplication.getGlobalData().addUserDataList(userList);
                            }
                        }
                        getRestaurantByUserId();
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
    public void onTimeSlotClick(String restaurantId) {
        Log.d("<>tag-", "on handle in restaurant fragment");
        Intent filter = new Intent(getActivity(), LoginAsDialog.class);
        startActivityForResult(filter, LOGIN_REQUEST_CODE);
    }
}

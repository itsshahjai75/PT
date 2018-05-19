package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.DialogFragment;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends android.support.v4.app.DialogFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    private Button btnMapOk;
    private Double longitude, latitude, currentLatitude, currentLongitude;
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private static final String ARG_PARAM_CU_LATITUDE = "currentLatitude";
    private static final String ARG_PARAM_CU_LONITUDE = "currentLongitude";
    private static final String ARG_PARAM_LATITUDE = "latitude";
    private static final String ARG_PARAM_LONGITUDE = "longitude";
    private static final String ARG_PARAM_LATLANG = "latLngs";
    private static final String ARG_PARAM_ID = "_id";
    private static final String ARG_PARAM_TIME = "time";
    private static final String ARG_PARAM_DATE = "bookDate";
    private static final String ARG_PARAM_PEOPLE = "people";
    private static final String ARG_PARAM_BOOK_DATE = "isBookToday";
    private static final String ARG_PARAM_Distance = "distance";
    private String _id, time, bookDate, distance, people = "";
    private View rootView;
    private Boolean isBookToday;
    List<RestaurantDetailModel> data;
    RestaurantDetailModel model;
    SupportMapFragment mapFragment;

    public MapsActivity() {
        // Required empty public constructor
    }

    public static MapsActivity newInstance(Double currentLatitude, Double currentLongitude, Double longitude, Double latitude, ArrayList<LatLng> latLngs) {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM_CU_LATITUDE, currentLatitude);
        args.putDouble(ARG_PARAM_CU_LONITUDE, currentLongitude);
        args.putDouble(ARG_PARAM_LATITUDE, latitude);
        args.putDouble(ARG_PARAM_LONGITUDE, longitude);
        args.putParcelableArrayList(ARG_PARAM_LATLANG, latLngs);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapsActivity newInstance(Double currentLatitude, Double currentLongitude,
                                           Double longitude, Double latitude, ArrayList<LatLng> latLngs, String _id, String time, String date,
                                           String people, Boolean isBookToday, String distance) {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM_CU_LATITUDE, currentLatitude);
        args.putDouble(ARG_PARAM_CU_LONITUDE, currentLongitude);
        args.putDouble(ARG_PARAM_LATITUDE, latitude);
        args.putDouble(ARG_PARAM_LONGITUDE, longitude);
        args.putParcelableArrayList(ARG_PARAM_LATLANG, latLngs);
        args.putString(ARG_PARAM_ID, _id);
        args.putString(ARG_PARAM_TIME, time);
        args.putString(ARG_PARAM_DATE, date);
        args.putString(ARG_PARAM_PEOPLE, people);
        args.putBoolean(ARG_PARAM_BOOK_DATE, isBookToday);
        args.putString(ARG_PARAM_Distance, distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogMapTheme);
        if (getArguments() != null) {
            currentLatitude = getArguments().getDouble(ARG_PARAM_CU_LATITUDE);
            currentLongitude = getArguments().getDouble(ARG_PARAM_CU_LONITUDE);
            latitude = getArguments().getDouble(ARG_PARAM_LATITUDE);
            longitude = getArguments().getDouble(ARG_PARAM_LONGITUDE);
            Log.d("<>loc-", " in intent longitude ===> " + longitude);
            latLngs = getArguments().getParcelableArrayList(ARG_PARAM_LATLANG);
            _id = getArguments().getString(ARG_PARAM_ID);
            time = getArguments().getString(ARG_PARAM_TIME);
            bookDate = getArguments().getString(ARG_PARAM_DATE);
            people = getArguments().getString(ARG_PARAM_PEOPLE);
            isBookToday = getArguments().getBoolean(ARG_PARAM_BOOK_DATE);
            distance = getArguments().getString(ARG_PARAM_Distance);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().setCanceledOnTouchOutside(true);
        rootView = inflater.inflate(R.layout.activity_maps, container, false);

       /* if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_maps, container, false);
        }*/
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_act);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                data = MyApplication.getGlobalData().getRestaurantsFullData();
                // Add a marker in Sydney and move the camera
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_current_map);
                LatLng defaultLocation = new LatLng(currentLatitude, currentLongitude);
//        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN
                mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Origin").icon(icon));

                MarkerPoints.add(new LatLng(currentLatitude, currentLongitude));
                Log.d("<>loc-", " current locaton ==> " + currentLatitude + " ==> longoitude " +
                        currentLongitude);
                if (data != null) {
                    for (int m = 0; m < data.size(); m++) {
                        if (data.get(m).getLongitude().equals(String.valueOf(longitude)) &&
                                data.get(m).getLatitude().equals(String.valueOf(latitude))) {
                            model = data.get(m);
                            // Add new marker to the Google Map Android API V2
                        }
                    }
                }
                // Add new marker to the Google Map Android API V2

                Marker markeDest = mMap.addMarker(new MarkerOptions().position(new LatLng(
                        latitude, longitude
                )));
                MarkerPoints.add(new LatLng(latitude, longitude));
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(MarkerPoints.get(1));

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */

                if (MarkerPoints.size() == 1) {
                    options.icon(icon);
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                mMap.addMarker(options);


      /*  mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Log.d("<>get-", "called hetre ");
                String markerLat = String.valueOf(marker.getPosition().latitude);
                String markerLong = String.valueOf(marker.getPosition().longitude);
                if (String.valueOf(latitude).equals(markerLat) &&
                        String.valueOf(longitude).equals(markerLong)){
                    Log.d("<>get-", "called ts in hetre ");
                    if (data != null){
                        for (int m = 0; m < data.size(); m++){
                            if (data.get(m).getLongitude().equals(String.valueOf(longitude)) &&
                                    data.get(m).getLatitude().equals(String.valueOf(latitude))){
                                RestaurantDetailModel model = data.get(m);
                                // Add new marker to the Google Map Android API V2
                                marker.setTag(model);
                                marker.showInfoWindow();
                            }
                        }
                    }
                }
                return null;
            }
        });*/

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    Log.d("<>map-", " origin ===> " + origin.toString());
                    Log.d("<>map-", " dest ===> " + dest.toString());


                    String url = getUrl(origin, dest);
                    Log.d("<>onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    /*Marker marker1 = mMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .title("Sample2")
                            .snippet("zzzzzzz"));
                    mMap.addMarker(marker1);

                    Marker marker2 = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(14.611335, 120.962160))
                            .title("Sample1")
                            .snippet("sssssss"));
                    allMarkersMap.put(marker2, MainActivity2Activity.class);*/
                    //move map camera
           /* mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
            mMap.getCameraPosition();*/

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(origin);
                    builder.include(dest);
                    LatLngBounds bounds = builder.build();

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
//            mMap.animateCamera(cu);
                    mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy(-1.0f);
                            mMap.animateCamera(zout);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                }

                Log.d("<>loc-", " latlags in map activity ==> " + latLngs.toString());
                for (LatLng point : latLngs) {
                    MarkerOptions options1 = new MarkerOptions();
                    options1.position(point);
                    options1.title("someTitle");
//            options1.snippet("someDesc");
                    options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(options1);
                }

                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                mMap.setInfoWindowAdapter(customInfoWindow);
                markeDest.setTag(model);
                markeDest.showInfoWindow();
                customInfoWindow.getInfoWindow(markeDest);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.d("<>map-", " info window cliked ==" + marker.getTag().toString());
//                getDialog().getWindow().findViewById(R.id.btnMapOk).performClick();

                        marker.hideInfoWindow();
//                        dismissDialog();
                        String _id = model.getRestaurant_id();
                        List<RestaurantDetailModel> ResturantModel = new ArrayList<>();
                        ResturantModel.add(model);
                        MyApplication.getGlobalData().addRestaurantsFullData(ResturantModel);
                        Fragment attachFragment = RestaurantDetailsAboutFragment.newInstance(_id, time,
                                bookDate, people, isBookToday, model.getDistance());
                        android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.PreLoginFrame, attachFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        Log.d("<>marker-", " default in on marker click ");
                        try {
                            Log.d("<>map-", " data of restaurants ==> " + data.toString());
                            RestaurantDetailModel model = null;

                            for (int i = 0; i < data.size(); i++) {
                                Log.d("<>map-", "<>resturant latitude ==> " + data.get(i).getLatitude());
                                Log.d("<>map-", "<>resturant longitude ==> " + data.get(i).getLongitude());
                                Log.d("<>map-", "<>marker latitude ==> " + marker.getPosition().latitude + "");
                                Log.d("<>map-", "<>marker longitude ==> " + marker.getPosition().longitude + "");
                                if (data.get(i).getLatitude().equals(String.valueOf(marker.getPosition().latitude)) &&
                                        data.get(i).getLongitude().equals(String.valueOf(marker.getPosition().longitude))) {
                                    Log.d("<>map-", "<>resturant name ==> " + data.get(i).getTitle());
                                    model = data.get(i);
                                }
                            }
                            List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude,
                                    marker.getPosition().longitude, 1);
                            Log.d("<>marker-", " address ==>  " + addresses.size() + "");
                            String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            //create your custom title
                            String title = address + "\n" + city + "\n" + state;
                            Log.d("<>marker-", " title ==>  " + title + "");
                            marker.setTitle(title);
                            if (model != null) {
                                marker.setTag(model);
                                marker.showInfoWindow();
                                int zoom = (int) mMap.getCameraPosition().zoom;
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude + (double) 90 / Math.pow(2, zoom), marker.getPosition().longitude), zoom);
                                mMap.animateCamera(cu);
                            }
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
            }
        });
        btnMapOk = (Button) rootView.findViewById(R.id.btnMapOk);
        btnMapOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        MarkerPoints = new ArrayList<>();
        return rootView;
    }
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MarkerPoints = new ArrayList<>();

    }*/

    public void dismissDialog() {
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            MapsActivity df = (MapsActivity) prev;
            df.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map_act));
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d("<>map-", " url is ==> " + url);
        return url;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        getDialog().dismiss();
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("<>Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("<>Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("<>downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("<>Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        data = MyApplication.getGlobalData().getRestaurantsFullData();
        // Add a marker in Sydney and move the camera
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ur_here);
        LatLng defaultLocation = new LatLng(currentLatitude, currentLongitude);
//        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Origin").icon(icon));

        MarkerPoints.add(new LatLng(currentLatitude, currentLongitude));
        Log.d("<>loc-", " current locaton ==> " + currentLatitude + " ==> longoitude " +
                currentLongitude);
        if (data != null) {
            for (int m = 0; m < data.size(); m++) {
                if (data.get(m).getLongitude().equals(String.valueOf(longitude)) &&
                        data.get(m).getLatitude().equals(String.valueOf(latitude))) {
                    model = data.get(m);
                    // Add new marker to the Google Map Android API V2
                }
            }
        }
        // Add new marker to the Google Map Android API V2

        Marker markeDest = mMap.addMarker(new MarkerOptions().position(new LatLng(
                latitude, longitude
        )));
        MarkerPoints.add(new LatLng(latitude, longitude));
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(MarkerPoints.get(1));

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */

        if (MarkerPoints.size() == 1) {
            options.icon(icon);
        } else if (MarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        mMap.addMarker(options);


      /*  mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Log.d("<>get-", "called hetre ");
                String markerLat = String.valueOf(marker.getPosition().latitude);
                String markerLong = String.valueOf(marker.getPosition().longitude);
                if (String.valueOf(latitude).equals(markerLat) &&
                        String.valueOf(longitude).equals(markerLong)){
                    Log.d("<>get-", "called ts in hetre ");
                    if (data != null){
                        for (int m = 0; m < data.size(); m++){
                            if (data.get(m).getLongitude().equals(String.valueOf(longitude)) &&
                                    data.get(m).getLatitude().equals(String.valueOf(latitude))){
                                RestaurantDetailModel model = data.get(m);
                                // Add new marker to the Google Map Android API V2
                                marker.setTag(model);
                                marker.showInfoWindow();
                            }
                        }
                    }
                }
                return null;
            }
        });*/

        // Checks, whether start and end locations are captured
        if (MarkerPoints.size() >= 2) {
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            Log.d("<>map-", " origin ===> " + origin.toString());
            Log.d("<>map-", " dest ===> " + dest.toString());


            String url = getUrl(origin, dest);
            Log.d("<>onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
                    /*Marker marker1 = mMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .title("Sample2")
                            .snippet("zzzzzzz"));
                    mMap.addMarker(marker1);

                    Marker marker2 = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(14.611335, 120.962160))
                            .title("Sample1")
                            .snippet("sssssss"));
                    allMarkersMap.put(marker2, MainActivity2Activity.class);*/
            //move map camera
           /* mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
            mMap.getCameraPosition();*/

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(dest);
            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
//            mMap.animateCamera(cu);
            mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    CameraUpdate zout = CameraUpdateFactory.zoomBy(-1.0f);
                    mMap.animateCamera(zout);
                }

                @Override
                public void onCancel() {

                }
            });

        }

        Log.d("<>loc-", " latlags in map activity ==> " + latLngs.toString());
        for (LatLng point : latLngs) {
            MarkerOptions options1 = new MarkerOptions();
            options1.position(point);
            options1.title("someTitle");
//            options1.snippet("someDesc");
            options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(options1);
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
        mMap.setInfoWindowAdapter(customInfoWindow);
        markeDest.setTag(model);
        markeDest.showInfoWindow();
        customInfoWindow.getInfoWindow(markeDest);
       /* mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("<>map-", " info window cliked ==" + marker.getTag().toString());
//                getDialog().getWindow().findViewById(R.id.btnMapOk).performClick();
                marker.hideInfoWindow();
//                getDialog().dismiss();
                String _id = model.getRestaurant_id();
                List<RestaurantDetailModel> ResturantModel = new ArrayList<>();
                ResturantModel.add(model);
                MyApplication.getGlobalData().addRestaurantsFullData(ResturantModel);
                Fragment attachFragment = RestaurantDetailsAboutFragment.newInstance(_id, time,
                        bookDate, people, isBookToday, model.getDistance());
                android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });*/


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                Log.d("<>marker-", " default in on marker click ");
                try {
                    Log.d("<>map-", " data of restaurants ==> " + data.toString());
                    RestaurantDetailModel model = null;

                    for (int i = 0; i < data.size(); i++) {
                        Log.d("<>map-", "<>resturant latitude ==> " + data.get(i).getLatitude());
                        Log.d("<>map-", "<>resturant longitude ==> " + data.get(i).getLongitude());
                        Log.d("<>map-", "<>marker latitude ==> " + marker.getPosition().latitude + "");
                        Log.d("<>map-", "<>marker longitude ==> " + marker.getPosition().longitude + "");
                        if (data.get(i).getLatitude().equals(String.valueOf(marker.getPosition().latitude)) &&
                                data.get(i).getLongitude().equals(String.valueOf(marker.getPosition().longitude))) {
                            Log.d("<>map-", "<>resturant name ==> " + data.get(i).getTitle());
                            model = data.get(i);
                        }
                    }
                    List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude,
                            marker.getPosition().longitude, 1);
                    Log.d("<>marker-", " address ==>  " + addresses.size() + "");
                    String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    //create your custom title
                    String title = address + "\n" + city + "\n" + state;
                    Log.d("<>marker-", " title ==>  " + title + "");
                    marker.setTitle(title);
                    if (model != null) {
                        marker.setTag(model);
                        marker.showInfoWindow();
                        int zoom = (int) mMap.getCameraPosition().zoom;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude + (double) 90 / Math.pow(2, zoom), marker.getPosition().longitude), zoom);
                        mMap.animateCamera(cu);
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("<>onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("<>onPostExecute", "without Polylines drawn");
            }
        }
    }
}

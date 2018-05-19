package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.ORestaurant;
import com.clickeat.customer.click_eatcustomer.DataModel.OTimeTable;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.DataModel.TimeSlotDetails;
import com.clickeat.customer.click_eatcustomer.LoginAsDialog;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by pivotech on 22/11/17.
 */

public class RestaurantDetailsAboutFragment extends Fragment implements TimeSlotRecyclerAdapter.timeSlotInterface {
    private int LOGIN_REQUEST_CODE = 123;
    private View m_myFragmentView;
    private ProgressDialog loadingSpinner;
    private ArrayList<ORestaurant> dataModel;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAboutRestaurantAdapter viewPagerAdapter;
    ImageView img_details_icon, buttonLike, buttonFavourite, buttonReview;
    TextView txt_details_title, txt_details_cuisine_type, txt_details_time, txt_details_address,
            txt_details_hygiene_rating, txt_details_distance, txt_details_collection, txt_details_delivery,
            txt_details_isOpenStatus, txtLikeText, textLogoNot, txtDetailsReview, txt_rate;
    IconTextView img_btn_calendarInfo, img_btn_mapNavigation, img_btn_like;
    LinearLayout aLayoutMap, layoutDelivery, layoutCollection, layoutTimingSlot;
    RecyclerView recyclerTimeSlotDetails;
    RelativeLayout relativeLayoutBookTable;
    private Boolean isBookToday;
    private String _id, time, bookDate, distance;
    private String dayStartTime = "", dayEndTime = "", people = "";
    private static final String ARG_PARAM_ID = "_id";
    private static final String ARG_PARAM_TIME = "time";
    private static final String ARG_PARAM_DATE = "bookDate";
    private static final String ARG_PARAM_PEOPLE = "people";
    private static final String ARG_PARAM_BOOK_DATE = "isBookToday";
    private static final String ARG_PARAM_Distance = "distance";
    private static final String ARG_PARAM_Model = "model";

    private TimeSlotRecyclerAdapter.timeSlotInterface timeSlotInterface;

    public static RestaurantDetailsAboutFragment newInstance(String _id, String time, String date,
                                                             String people, Boolean isBookToday, String distance) {
        RestaurantDetailsAboutFragment fragment = new RestaurantDetailsAboutFragment();
        Bundle args = new Bundle();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        setHasOptionsMenu(true);
        m_myFragmentView = inflater.inflate(R.layout.layout_fragment_about_restaurant, container, false);
        android.support.v7.widget.Toolbar toolbar = m_myFragmentView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // Implemented by activity
            }
        });
        dismissDialog();
        findIds();
        init();
//        getRestaurantById(_id);
        return m_myFragmentView;
    }

    public void dismissDialog() {
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            MapsActivity df = (MapsActivity) prev;
            df.dismiss();
        }
    }

    /* fins the view id */
    private void findIds() {
        img_details_icon = m_myFragmentView.findViewById(R.id.img_details_icon);
        txt_details_title = m_myFragmentView.findViewById(R.id.txt_details_title);
        txt_details_cuisine_type = m_myFragmentView.findViewById(R.id.txt_details_cuisine_type);
        txt_details_time = m_myFragmentView.findViewById(R.id.txt_details_time);
        txt_details_address = m_myFragmentView.findViewById(R.id.txt_details_address);
        txt_details_hygiene_rating = m_myFragmentView.findViewById(R.id.txt_details_hygiene_rating);
        txt_details_distance = m_myFragmentView.findViewById(R.id.txt_details_distance);
        txt_details_delivery = m_myFragmentView.findViewById(R.id.txt_details_delivery);
        txt_details_collection = m_myFragmentView.findViewById(R.id.txt_details_collection);
        txt_details_isOpenStatus = m_myFragmentView.findViewById(R.id.txt_details_isOpenStatus);
        img_btn_mapNavigation = m_myFragmentView.findViewById(R.id.img_btn_mapNavigation);
        img_btn_calendarInfo = m_myFragmentView.findViewById(R.id.img_btn_calendarInfo);
        buttonLike = m_myFragmentView.findViewById(R.id.buttonLike);
        img_btn_like = m_myFragmentView.findViewById(R.id.img_btn_like);
        txtLikeText = m_myFragmentView.findViewById(R.id.txtLikeText);
        aLayoutMap = m_myFragmentView.findViewById(R.id.aLayoutMap);
        layoutDelivery = m_myFragmentView.findViewById(R.id.listLayoutDelivery);
        layoutCollection = m_myFragmentView.findViewById(R.id.listLayoutCollection);
        buttonFavourite = m_myFragmentView.findViewById(R.id.buttonFavourite);
        textLogoNot = m_myFragmentView.findViewById(R.id.textLogoNot);
        layoutTimingSlot = m_myFragmentView.findViewById(R.id.layoutTimingSlot);
        recyclerTimeSlotDetails = m_myFragmentView.findViewById(R.id.recyclerTimeSlotDetails);
        buttonReview = m_myFragmentView.findViewById(R.id.buttonReview);
        txtDetailsReview = m_myFragmentView.findViewById(R.id.txtDetailsReview);
        relativeLayoutBookTable = m_myFragmentView.findViewById(R.id.relativeLayoutBookTable);
        txt_rate = m_myFragmentView.findViewById(R.id.txt_rate);


        final Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        txt_details_title.setTypeface(roboto);
        txt_details_cuisine_type.setTypeface(roboto);
    }

    /* on create load data from API */
    private void init() {
        timeSlotInterface = this;
        StringBuilder timesb = new StringBuilder();
        List<RestaurantDetailModel> models = MyApplication.getGlobalData().getRestaurantsFullData();
        final RestaurantDetailModel restaurant = models.get(0);
        viewPager = (ViewPager) m_myFragmentView.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAboutRestaurantAdapter(getFragmentManager(), restaurant.getRestaurant_id());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) m_myFragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        String imgPath = restaurant.getImage();
        imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
        Log.d("<>image-", " img path ==> " + APIConstants.URL + imgPath);
        if (!imgPath.equals("")) {
            Picasso.with(getActivity())
                    .load(APIConstants.URL + imgPath)
                    .into(img_details_icon);
        } else {
            //get first letter of each String item
            textLogoNot.setVisibility(View.VISIBLE);
            img_details_icon.setVisibility(View.GONE);
            String firstLetter = String.valueOf(restaurant.getTitle().charAt(0));
            textLogoNot.setText(firstLetter);
        }
        txt_details_address.setText(restaurant.getAddress());
        txt_details_title.setText(restaurant.getTitle());
        txt_details_hygiene_rating.setText(restaurant.getHygieneRating().toString());
        txt_details_distance.setText(distance + " Miles");
        txtDetailsReview.setText(restaurant.getNumberOfReviews() + "");
        txt_rate.setText(restaurant.getAveragePriceForTwo() + "");
        List<String> cuisines = new ArrayList<>();
        cuisines = restaurant.getCuisines();
        if (cuisines.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < cuisines.size(); c++) {
                if (c + 1 == cuisines.size())
                    builder.append(cuisines.get(c));
                else
                    builder.append(cuisines.get(c) + ",");
            }
            txt_details_cuisine_type.setText(builder.toString());
        }

        if (restaurant.getDelivery() == true)
            txt_details_delivery.setText(restaurant.getAverageDeliveryTime() + " " + getActivity().getString(R.string.mins_title));
        else
            txt_details_delivery.setText(R.string.no_title);

        if (restaurant.getCollection() == true)
            txt_details_collection.setText(restaurant.getAverageCollectionTime() + " " + getActivity().getString(R.string.mins_title));
        else
            txt_details_collection.setText(R.string.no_title);

        relativeLayoutBookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "";
                userId = SharedData.getUserId(getActivity());
                if (SharedData.getIsLoggedIn(getActivity()) && userId != "") {
                    Intent filter = new Intent(getActivity(), BookATable.class);
                    filter.putExtra("restaurantId", _id);
                    filter.putExtra("people", people);
                    filter.putExtra("date", bookDate);
                    filter.putExtra("name", restaurant.getTitle());
                    startActivity(filter);
                } else {
                    Intent filter = new Intent(getActivity(), LoginAsDialog.class);
                    getActivity().overridePendingTransition(R.anim.stay, R.anim.slide_up);
                    startActivityForResult(filter, LOGIN_REQUEST_CODE);
                }
            }
        });

        layoutCollection.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if (restaurant.getCollection() != false) {
                    Toast toast = Toast.makeText(getActivity(), "Average collection time of " + restaurant.getAverageCollectionTime() + "" +
                            " mins within " + restaurant.getCollectionDiscount() + " % discount of order value.", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                    /* And now you can get the TextView of the default View of the Toast. */
                    final Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                            "fonts/Roboto-Light.ttf");
                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(14);
                    toastMessage.setTypeface(roboto);
                    toastMessage.setTextColor(getResources().getColor(R.color.colorPrimary));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(16);
                    toastView.setBackground(getResources().getDrawable(R.drawable.book_table_btn_style));
                    toast.show();
                }
            }
        });

        layoutDelivery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if (restaurant.getDelivery() != false) {
                    Toast toast = Toast.makeText(getActivity(), "Average delivery time of " + restaurant.getAverageDeliveryTime() + "" +
                            " mins within " + restaurant.getDeliveryDistance() + " miles delivery free from.", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                    /* And now you can get the TextView of the default View of the Toast. */
                    final Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                            "fonts/Roboto-Light.ttf");
                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(14);
                    toastMessage.setTypeface(roboto);
                    toastMessage.setTextColor(getResources().getColor(R.color.colorPrimary));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(16);
                    toastView.setBackground(getResources().getDrawable(R.drawable.book_table_btn_style));
                    toast.show();
                }
            }
        });

        txt_details_hygiene_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://ratings.food.gov.uk/"));
                startActivity(intent);*/
                // Build the custom tab intent and launch the url
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                builder.setShowTitle(true);
                builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_arrow_back));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse("http://ratings.food.gov.uk/"));

                /*Intent hygiene = new Intent(getActivity(), HygieneRatingWeb.class);
                startActivity(hygiene);*/
            }
        });
        String dayOfTheWeek = "";
        HashMap<String, List<String>> timeHashmap = new HashMap<>();
        HashMap<String, Boolean> isOpenDay = new HashMap<>();
        isOpenDay = restaurant.getIsOpenDay();
        timeHashmap = restaurant.getTimeTable();
        final ArrayList<OTimeTable> timesList = new ArrayList<>();
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = inFormat.parse(bookDate);
            SimpleDateFormat sdfDate = new SimpleDateFormat("E");
            dayOfTheWeek = sdfDate.format(date);

            if (timeHashmap.size() > 0) {
                for (Map.Entry<String, Boolean> isOpenDayItem : isOpenDay.entrySet()) {
                    String dayName1 = sdfDate.format(sdfDate.parse(isOpenDayItem.getKey()));
                    if (dayName1.equals(dayOfTheWeek)) {
                        Boolean value = isOpenDayItem.getValue();
                        for (Map.Entry<String, List<String>> map : timeHashmap.entrySet()) {
                            String dayName = sdfDate.format(sdfDate.parse(map.getKey()));
                            StringBuilder sb = new StringBuilder();
                            ArrayList<String> state = new ArrayList<>();
                            if (value) {
                                if (dayName.equals(dayOfTheWeek)) {
                                    for (int tm = 0; tm < map.getValue().size(); tm++) {
                                        String[] separated = map.getValue().get(tm).split("-");
                                        Boolean isOpen = isRestaurantOpen(separated[0], separated[1]);
                                        state.add(isOpen.toString());
                                        if (tm + 1 == map.getValue().size())
                                            sb.append(map.getValue().get(tm));
                                        else
                                            sb.append(map.getValue().get(tm) + ",\n");
                                        if (state.contains("true")) {
                                            txt_details_isOpenStatus.setText(R.string.open_title);
                                            txt_details_isOpenStatus.setTextColor(getActivity().getResources().getColor(R.color.colorGreen));
                                            txt_details_time.setText(sb.toString());
                                        } else {
                                            txt_details_isOpenStatus.setText(R.string.closed_title);
                                            txt_details_isOpenStatus.setTextColor(getResources().getColor(R.color.colorBrown));
                                            txt_details_time.setText("");
                                        }
                                        timesb.append(sb);

                                    }
                                } else {
                                    for (int tm = 0; tm < map.getValue().size(); tm++) {
                                        if (tm + 1 == map.getValue().size())
                                            sb.append(map.getValue().get(tm));
                                        else
                                            sb.append(map.getValue().get(tm) + ",\n");
                                    }
                                }
                            } else {
                                txt_details_isOpenStatus.setText(R.string.closed_title);
                                txt_details_isOpenStatus.setTextColor(getResources().getColor(R.color.colorBrown));
                                txt_details_time.setText("");
                                for (int tm = 0; tm < map.getValue().size(); tm++) {
                                    String[] separated = map.getValue().get(tm).split("-");
                                    if (tm + 1 == map.getValue().size())
                                        sb.append(map.getValue().get(tm));
                                    else
                                        sb.append(map.getValue().get(tm) + ",\n");
                                }
                                timesb.append(sb);
                            }
                            OTimeTable timeTable = new OTimeTable(dayName, sb.toString());
                            timesList.add(timeTable);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentDateandTime = sdf.format(new Date());
        Date date1 = null;
        try {
            /*if (!isBookToday)
                date1 = sdf.parse("00:00");
            else*/
            date1 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        /*if (!isBookToday) {
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
        }*/
        Date startTime = date1;//start
        if (!isBookToday) {
            startTime = calendar.getTime();
        }
        Calendar calendarnew = Calendar.getInstance();
        int year = calendarnew.get(Calendar.YEAR);
        int month = calendarnew.get(Calendar.MONTH);
        int day = calendarnew.get(Calendar.DATE);
        calendarnew.set(year, month, day, 23, 59, 59);
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        Integer addHour = Integer.parseInt(sdf1.format(calendarnew.getTime())) - Integer.parseInt(sdf1.format(calendar.getTime()));
        Log.d("<>tets-", " hpours of day ==> " + Integer.parseInt(sdf1.format(calendarnew.getTime()) + ""));
        Log.d("<>tets-", " now hour ==> " + Integer.parseInt(sdf1.format(calendar.getTime())) + "");
        Log.d("<>tets-", " addHour ==> " + addHour + "");
        calendar.add(Calendar.HOUR, addHour);
        Date endTime = calendar.getTime();///end
        ArrayList<String> times = new ArrayList<String>();
        Calendar now = Calendar.getInstance();
        now.setTime(startTime);
        now.setTime(now.getTime());
        int unroundedMinutes = now.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        now.add(Calendar.MINUTE, mod < 0 ? -mod : (30 - mod));
        int flag = 0;
        while (now.getTime().before(endTime)) {
            if (flag != 0)
                now.add(Calendar.MINUTE, 30);
            times.add(sdf.format(now.getTime()));
            flag++;
        }
        if (times.size() > 0) {
            Log.d("<>abt-", " timse size --> " + times.size() + "");
            List<TimeSlotDetails> timingItems = new ArrayList<>();
            Boolean isSlotEmpty = false;
            for (int slot = 0; slot < times.size(); slot++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 5, 15, 5);
                String slotTime = times.get(slot).toString();
                Log.d("<>abt-", " sb in times ==>" + timesb.toString());
                if (timesb.toString().contains(",")) {
                    String[] separatedList = timesb.toString().split(",\n");
                    Log.d("<>abt-", " separated part ==> " + separatedList.length);
                    for (int s = 0; s < separatedList.length; s++) {
                        String[] separated = separatedList[s].split("-");
                        Log.d("<>abt-", " separated 0 ==> " + separated[0].toString());
                        Log.d("<>abt-", " separated 1 ==> " + separated[1].toString());
                        dayStartTime = separated[0];
                        dayEndTime = separated[1];
                        isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);
                        Log.d("<>slot-", "StartTime -> " + dayStartTime + "  ended time -> " + dayEndTime +
                                "slot --> " + slotTime + "  resulkt => " + isSlotEmpty.toString());
                        if (isSlotEmpty)
                            break;
                    }
                } else {
                    if (timesb.length() > 0) {
                        String[] separated = timesb.toString().split("-");
                        dayStartTime = separated[0];
                        dayEndTime = separated[1];
                        isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);
                        Log.d("<>else-", "StartTime -> " + dayStartTime + "  ended time -> " + dayEndTime +
                                "slot --> " + slotTime + "  resulkt => " + isSlotEmpty.toString());
                    }
                }

//                Boolean isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);

                final TextView text = new TextView(getActivity());
                text.setText(times.get(slot).toString());


                if (isSlotEmpty) {
                    text.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.list_time_slot_style));
                    text.setTextColor(getResources().getColor(R.color.colorWhite));
                    text.setTag("1");
                } else {
                    text.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.closed_list_time_slot_style));
                    text.setTextColor(getResources().getColor(R.color.colorBlack));
                    text.setTag("0");
                }
                Log.d("<>re-", " timing slot size ==> " + text.getText().toString() + "");
                timingItems.add(new TimeSlotDetails(text.getText().toString(), text.getTag().toString(),
                        restaurant.getRestaurant_id(), people, restaurant.getTitle()));
                text.setTextSize(10);
                text.setGravity(Gravity.CENTER);
                text.setPadding(15, 15, 15, 15);
                text.setLayoutParams(params);
                layoutTimingSlot.addView(text);
            }


            /*if (!flagDecoration) {
                holder.recyclerTimeSlot.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
                flagDecoration = true;
            }*/
            TimeSlotRecyclerAdapter groceryAdapter = new TimeSlotRecyclerAdapter(timingItems,
                    getActivity(), recyclerTimeSlotDetails, bookDate, timeSlotInterface);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerTimeSlotDetails.setLayoutManager(horizontalLayoutManager);
            recyclerTimeSlotDetails.setAdapter(groceryAdapter);
            for (int i = 0; i < layoutTimingSlot.getChildCount(); i++) {
                final View child = (TextView) layoutTimingSlot.getChildAt(i);
                Log.d("<>tm-", " child of linear ==> " + child + "");
                if (child.getTag().toString() == "1") {
                    Log.d("<>pos-", " position of timeslot ==> " + i + "");
                    recyclerTimeSlotDetails.scrollToPosition(i);
                    break;
                }
            }
        }
        img_btn_calendarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.layout_time_table_restaurant);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 1100;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);

                Comparator<OTimeTable> dateComparator = new Comparator<OTimeTable>() {
                    @Override
                    public int compare(OTimeTable s1, OTimeTable s2) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("EEE");
                            Date d1 = format.parse(s1.getDay());
                            Date d2 = format.parse(s2.getDay());
                            if (d1.equals(d2)) {
                                return s1.getDay().substring(s1.getDay().indexOf(" ") + 1).
                                        compareTo(s2.getDay().substring(s2.getDay().indexOf(" ") + 1));
                            } else {
                                Calendar cal1 = Calendar.getInstance();
                                Calendar cal2 = Calendar.getInstance();
                                cal1.setTime(d1);
                                cal2.setTime(d2);
                                return cal1.get(Calendar.DAY_OF_WEEK) - cal2.get(Calendar.DAY_OF_WEEK);
                            }
                        } catch (ParseException pe) {
                            throw new RuntimeException(pe);
                        }
                    }
                };
                Collections.sort(timesList, dateComparator);
                ListView listView = (ListView) dialog.findViewById(R.id.listTimes);
                OTimeTableAdapter adapter = new OTimeTableAdapter(getActivity(), timesList);
                listView.setAdapter(adapter);

                Button dialogButton = (Button) dialog.findViewById(R.id.timeOk);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        aLayoutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity = (FragmentActivity) (getActivity());
                FragmentManager fm = activity.getSupportFragmentManager();
                Log.d("<>loc-", " location longitude ==> " + restaurant.getLongitude());
                Log.d("<>loc-", " location latitude ==> " + restaurant.getLatitude());
                MapsActivity dialog = MapsActivity.newInstance(APIConstants.getLatitude(),
                        APIConstants.getLongitude(), Double.parseDouble(restaurant.getLongitude()),
                        Double.parseDouble(restaurant.getLatitude()), new ArrayList<LatLng>(),
                        restaurant.get_id(), time, bookDate, people, isBookToday, distance);
                dialog.show(fm, "dialog");
            }
        });

        img_btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = "";
                userId = SharedData.getUserId(getActivity());
                if (SharedData.getIsLoggedIn(getActivity()) && userId != "" && (!img_btn_like.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " user id ==> " + userId);
                    Log.d("<>like-", " restaurant id ==> " + restaurant.get_id());
                    makeRestaurantLike(userId, restaurant.get_id());
                } else if (SharedData.getIsLoggedIn(getActivity()) && userId != "" && (img_btn_like.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " dislike id ==> " + userId);
                    makeRestaurantDisLike(userId, restaurant.get_id());
                } else {
                    Intent filter = new Intent(getActivity(), LoginAsDialog.class);
                    getActivity().overridePendingTransition(R.anim.stay, R.anim.slide_up);
                    startActivityForResult(filter, LOGIN_REQUEST_CODE);
                }
            }
        });

        List<String> likedBy = new ArrayList<>();
        likedBy = restaurant.getLikedBy();
        txtLikeText.setText(likedBy.size() + "");
        if (likedBy.size() > 0) {
            Log.d("<>abt-", " user id in about ==> " + MyApplication.getGlobalData().getUserId());
            if (SharedData.getIsLoggedIn(getActivity()) == true) {
                if (likedBy.contains(SharedData.getUserId(getActivity()))) {
                    img_btn_like.setText("{fa-heart}");
                    img_btn_like.setTag("1");
                }

            } else {
                if (likedBy.contains(SharedData.getUserId(getActivity()))) {
                    img_btn_like.setText("{fa-heart}");
                    img_btn_like.setTag("1");
                }
            }
        }

        List<OUser> users = new ArrayList<>();
        users = MyApplication.getGlobalData().getUserData();
        for (int i = 0; i < users.size(); i++) {
            List<String> favourites = new ArrayList<>();
            favourites = users.get(i).getFavouriteRestaurants();
            Log.d("<>log-", " user list of restaurant ==? " + favourites.toString());
            if (favourites.size() > 0) {
                Log.d("<>log-", " restaurant id  ==? " + favourites.contains(restaurant.get_id()));
                if (favourites.contains(restaurant.get_id())) {
                    buttonFavourite.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favourites));
                    buttonFavourite.setTag("1");
                }
            }
        }

        buttonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = "";
                userId = SharedData.getUserId(getActivity());
                Log.d("<>like-", " like text  ==> " + buttonFavourite.getTag().toString());
                if (SharedData.getIsLoggedIn(getActivity()) && userId != "" && (!buttonFavourite.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " user id ==> " + userId);
                    makeRestaurantFavourite(userId, restaurant.get_id());
                } else if (SharedData.getIsLoggedIn(getActivity()) && userId != "" && (buttonFavourite.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " dislike id ==> " + userId);
                    makeRestaurantFavouriteRemove(userId, restaurant.get_id());
                } else {
                    Intent filter = new Intent(getActivity(), LoginAsDialog.class);
                    getActivity().overridePendingTransition(R.anim.stay, R.anim.slide_up);
                    startActivityForResult(filter, LOGIN_REQUEST_CODE);
                }
            }

        });

        buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), RestaurantReviews.class);
                intent.putExtra("restaurantName", restaurant.getTitle());
                startActivity(intent);*/
                FragmentActivity activity = (FragmentActivity) (getActivity());
                Fragment fragment = RestaurantReviews.newInstance(restaurant.getTitle(), restaurant.getRestaurant_id(), restaurant.getNumberOfReviews()+"");
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void showHygieneRating() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Title here");
        WebView wv = new WebView(getActivity());
        final ProgressBar progressBar = new ProgressBar(getActivity());
        wv.getSettings().setJavaScriptEnabled(true); // enable javascript
        wv.loadUrl("http://ratings.food.gov.uk/");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                alert.setTitle("Pages Finished");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                alert.setTitle("Pages Started");
            }

        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alert.show();
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
                            img_btn_like.setText("{fa-heart}");
                            img_btn_like.setTag("1");
                        }
                        getRestaurantById(_id);
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

    private Boolean isTimeSloatOpen(String startTime, String endTime, String slot) {
        try {
            String string1 = startTime;
            Log.d("<>dd-", " start time ==> " + string1);
            Date time1 = new SimpleDateFormat("hh:mm a").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String test = "12:00 AM";
            Date testTime = new SimpleDateFormat("hh:mm aa").parse(test);
            Calendar testCalendar = Calendar.getInstance();
            testCalendar.setTime(testTime);
            String string2 = endTime;
            Date time2 = new SimpleDateFormat("hh:mm a").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            Date calen2 = calendar2.getTime();
            Log.d("<>tm-", " compare time -->" + calen2.compareTo(testCalendar.getTime()) + "");
            if (calen2.after(testCalendar.getTime()) || calen2.compareTo(testCalendar.getTime()) == 0)
                calendar2.add(Calendar.DATE, 1);

            String someRandomTime = slot;
            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
//            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            Log.d("<>open-", "calendar1.getTime() " + calendar1.getTime().toString()
                    + "calendar2.getTime()" + calendar2.getTime().toString()
                    + " slot ==? " + x.toString());
           /* if (x.after(calendar1.getTime()) || x.equals(calendar1.getTime()) &&
                    x.before(calendar2.getTime()) || x.equals(calendar2.getTime())) {*/
            if ((x.after(calendar1.getTime()) || x.compareTo(calendar1.getTime()) == 0)
                    && (x.before(calendar2.getTime()) || x.compareTo(calendar2.getTime()) == 0)) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.

                System.out.println(true);
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getRestaurantById(String _id) {
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
        JsonObject finalData = new JsonObject();
        JsonObject innerData = new JsonObject();
        innerData.addProperty("restaurant_id", _id);
        finalData.add("search", innerData);

        Log.d("<>rest-", " final data for search ===> " + finalData.toString());

        Call<JsonElement> mService = mInterfaceService.getRestaurantDetailsByRestaurantId(finalData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body() + "");
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
                        if (response.body().getAsJsonArray().size() == 0) {
                            Toast.makeText(getActivity(), "No restaurant find !!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("<>tag-", " on else while body has 0 count");
                            JsonArray arrayMain = response.body().getAsJsonArray();
                            RestaurantDetails restDetails = new RestaurantDetails();
                            Boolean collection = false, delivery = false;
                            Integer collectionDiscount = null, averageCollectionTime = null, averageDeliveryTime = null;
                            Double deliveryCharge = null, deliveryDistance = null;
                            dataModel = new ArrayList<>();
                            Log.d("<>resta-", " array main size in result ==> " + arrayMain.size() + "");
                            for (int i = 0; i < arrayMain.size(); i++) {
                                JsonArray facilitiesDetails = new JsonArray();
                                /*if (arrayMain.get(i).getAsJsonObject().has("facilities"))
                                    facilitiesDetails = arrayMain.get(i).getAsJsonObject().get("facilities").getAsJsonArray();*/
                                JsonObject mainDetails = arrayMain.get(i).getAsJsonObject().get("main_details").getAsJsonObject();
                                JsonArray latlangBounds = arrayMain.get(i).getAsJsonObject().get("address").getAsJsonObject().getAsJsonObject("restaurantAddress").getAsJsonObject("location").getAsJsonArray("coordinates");
                                JsonArray list = new JsonArray();
                                if (mainDetails.has(MyApplication.sDefSystemLanguage)) {
                                    list = mainDetails.getAsJsonObject(MyApplication.sDefSystemLanguage).getAsJsonArray("cuisines");
                                }
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
                                                String dtStart = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("opensAt").getAsString();
                                                Date dateStart = df.parse(dtStart.replaceAll("Z$", "+0000"));
                                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                                openTime = sdf.format(dateStart);
                                                String dtEnd = calendarList.get(cal).getAsJsonObject().get("hours").getAsJsonArray().get(tm).getAsJsonObject().get("closesAt").getAsString();
                                                Date dateEnd = df.parse(dtEnd.replaceAll("Z$", "+0000"));
                                                closeTime = sdf.format(dateEnd);
                                                String fullTime = new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(openTime))
                                                        + " - " + new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(closeTime));
                                                times.add(fullTime);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        String day = calendarList.get(cal).getAsJsonObject().get("label").getAsString();
                                        Boolean value = calendarList.get(cal).getAsJsonObject().get("value").getAsBoolean();
                                        timeTables.put(day, times);
                                        isOpenDay.put(day, value);
                                    }
                                }
                                List<String> cuisineList = new ArrayList<>();
                                HashMap<String, String> facilitiesList = new HashMap<>();
                                for (int cuisine = 0; cuisine < list.size(); cuisine++) {
                                    cuisineList.add(list.get(cuisine).getAsString());
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
                                Log.d("<>log-", " main details ===?> " + mainDetails + "");
                                Log.d("<>log-", " facilitiesDetails  ===?> " + facilitiesDetails + "");


                                if (arrayMain.get(i).getAsJsonObject().has("collection_delivery")) {
                                    JsonObject collection_delivery = arrayMain.get(i).getAsJsonObject().get("collection_delivery").getAsJsonObject();
                                    collection = collection_delivery.get("collection").getAsJsonObject().get("value").getAsBoolean();
                                    delivery = collection_delivery.get("delivery").getAsJsonObject().get("value").getAsBoolean();
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
                                        deliveryCharge = collection_delivery.get("delivery").getAsJsonObject().get("deliveryCharge").getAsDouble();
                                    if (collection_delivery.get("delivery").getAsJsonObject().has("deliveryDistance") &&
                                            !collection_delivery.get("delivery").getAsJsonObject().get("deliveryDistance").equals("null"))
                                        deliveryDistance = collection_delivery.get("delivery").getAsJsonObject().get("deliveryDistance").getAsDouble();
                                    if (collection_delivery.get("delivery").getAsJsonObject().has("averageDeliveryTime") &&
                                            !collection_delivery.get("delivery").getAsJsonObject().get("averageDeliveryTime").equals("null"))
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
                                String logoFull = "";
                                if (mainDetails.has("restaurantLogoFull") && !mainDetails.get("restaurantLogoFull").isJsonNull()) {
                                    logoFull = mainDetails.get("restaurantLogoFull").getAsString();
                                }
                                String distance = "";
                                if (arrayMain.get(i).getAsJsonObject().has("dist") && !arrayMain.get(i).getAsJsonObject().get("dist").isJsonNull()) {
                                    distance = String.format("%.2f", arrayMain.get(i).getAsJsonObject().get("dist").getAsDouble());
                                }
                                List<String> listImages = new ArrayList<>();
                                if (arrayMain.get(i).getAsJsonObject().has("images") &&
                                        !arrayMain.get(i).getAsJsonObject().get("images").isJsonNull()) {
                                    JsonArray images = arrayMain.get(i).getAsJsonObject().get("images").getAsJsonArray();
                                    listImages = new ArrayList<>();
                                    if (images.size() > 0) {
                                        for (int im = 0; im < images.size(); im++) {
                                            listImages.add(images.get(im).getAsJsonObject().get("thumbnailImages").getAsString());
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
                                        if (addressDetails.has("city"))
                                            addressBuilder.append(addressDetails.get("city").getAsString() + ", ");
                                        if (addressDetails.has("county"))
                                            addressBuilder.append(addressDetails.get("county").getAsString() + ", ");
                                        if (addressDetails.has("town"))
                                            addressBuilder.append(addressDetails.get("town").getAsString() + ", ");
                                        if (addressDetails.has("postcode"))
                                            addressBuilder.append(addressDetails.get("postcode").getAsString());
                                    }
                                }

                                Log.d("<>log-", " addressDetails details ===?> " + addressDetails + "");
                                dataModel.add(new ORestaurant(
                                        arrayMain.get(i).getAsJsonObject().get("_id").getAsString(),
                                        arrayMain.get(i).getAsJsonObject().get("restaurant_id").getAsString(),
                                        restaurantName,
                                        about,
                                        distance,
                                        logoFull,
                                        addressBuilder.toString(),
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
                                        listImages,
                                        serviceMap
                                ));
                            }


                            MyApplication.getGlobalData().addRestaurantDataList(dataModel);
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
                init();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean isRestaurantOpen(String startTime, String endTime) {
        try {
            String string1 = startTime;
            Date time1 = new SimpleDateFormat("hh:mm aa").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = endTime;
            Date time2 = new SimpleDateFormat("hh:mm aa").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
//            calendar2.add(Calendar.DATE, 1);

            String someRandomTime = time;
            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
//            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                System.out.println(true);
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        Log.d("<>onAct-", " on Activity returnedData id ===> ");
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("<>abt-", " result ok ");
                getActivity().findViewById(R.id.menuRight).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.menuLogin).setVisibility(View.GONE);
                getRestaurantById(_id);
//                NavigationView navigationView = (NavigationView) activity.findViewById(R.id.yournavigationviewid);
//                navigationView.getMenu().findItem(R.id.youritemid).setVisible(false);
                TextView textUser = (TextView) getActivity().findViewById(R.id.textViewLabel);
                Log.d("<>abt-", " textUser name ===> " + data.getStringExtra("name"));
                textUser.setVisibility(View.VISIBLE);
                textUser.setText(getString(R.string.welcome_title) + " " + data.getStringExtra("name"));
                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.pre_login_nav_view);
                navigationView.getMenu().findItem(R.id.ic_action_change_password).setVisible(true);
                navigationView.getMenu().findItem(R.id.ic_action_notification).setVisible(true);
//                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.pre_drawer_layout);
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,  GravityCompat.END);
            } else {
                // You can handle a case where no selection was made if you want
                Log.d("<>abt-", " user cancelled");
            }
        }
    }

    private void makeRestaurantDisLike(String userId, String restaurantId) {
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
                            img_btn_like.setText("{fa-heart-o}");
                            img_btn_like.setTag("0");
                        }
                        getRestaurantById(_id);
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
                        init();
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
        final JsonObject innerData = new JsonObject();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pre_login_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
    }

}

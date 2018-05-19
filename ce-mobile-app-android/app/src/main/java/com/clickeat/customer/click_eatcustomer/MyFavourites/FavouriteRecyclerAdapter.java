package com.clickeat.customer.click_eatcustomer.MyFavourites;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.BookTableInfo.FlipAnimation;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.ImageFullscreenView;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.MapsActivity;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.MenuListActivity;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.OTimeTableAdapter;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.RestaurantDetailsAboutFragment;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.RestaurantReviews;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.TableMaps;
import com.clickeat.customer.click_eatcustomer.BookTableInfo.TimeSlotRecyclerAdapter;
import com.clickeat.customer.click_eatcustomer.DataModel.OTimeTable;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.DataModel.TimeSlotDetails;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.android.gms.maps.model.LatLng;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

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

public class FavouriteRecyclerAdapter extends
        RecyclerView.Adapter<FavouriteRecyclerAdapter.MyViewHolder>
        implements Filterable {

    private favouriteInterface mCallback;
    private TimeSlotRecyclerAdapter.timeSlotInterface timeSlotInterface;
    private List<RestaurantDetailModel> countryList;
    private List<RestaurantDetailModel> restaurantList;
    private Context mContext;
    private String dayStartTime = "", dayEndTime = "";
    private float lastX;
    private Integer filpPosition = -1;
    private Boolean flagDecoration = false;
    public static MapsActivity mapDialog;
    private String currentTime = "", currentDate = "";
    private Boolean isGpsOn;
    private Double currentLatitude, currentLongitude;
    //The "x" and "y" position of the "Show Button" on screen.
    Point p;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txt_details_distance, txt_details_cuisine_type,
                txt_details_delivery, txt_details_collection, txt_details_isRestaurantStatus, txt_details_type,
                txtLikeText, txtTableLink, textLogoNot, txt_details_avg_price, txt_rate, txtRestName, txtReviewText;
        ImageView imgLogo, img_details_restaurant, imb_btn_more, img_details_close, img_details_menu_view,
                buttonFavourite, imb_btn_membership, img_order_takeaway, buttonReviewCard, btnRateLike, buttonLike;
        View layoutFront, layoutBack, layoutRoot;
        Button btnBack;
        LinearLayout layoutFacilities, layoutThumblines, layoutTimingSlot, layoutMenu, layoutMap,
                listLayoutDelivery, layoutCollection, boxPhotos, boxFacility, layoutLogo, layoutTable,
                layout_details_tbl_map, layout_details_table_book_back, layout_details_menu_view_back;
        IconTextView img_btn_calendarInfo, img_btn_mapNavigation;
        ListView listMenu;
        HorizontalScrollView timingScroll;
        RecyclerView recyclerTimeSlot, recyclerTimeSlotBack;

        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txt_details_title);
            layoutBack = view.findViewById(R.id.card_view_back);
            layoutFront = view.findViewById(R.id.card_view);
            layoutRoot = view.findViewById(R.id.layoutRoot);
            imb_btn_more = view.findViewById(R.id.imb_btn_flip);
            imgLogo = view.findViewById(R.id.img_details_icon);
            btnBack = view.findViewById(R.id.btnBack);
            img_btn_calendarInfo = view.findViewById(R.id.img_btn_calendarInfo);
            img_details_close = view.findViewById(R.id.img_details_close);
            txt_details_distance = view.findViewById(R.id.txt_details_distance);
            img_btn_mapNavigation = view.findViewById(R.id.img_btn_mapNavigation);
            img_details_restaurant = view.findViewById(R.id.img_details_restaurant);
            txt_details_cuisine_type = view.findViewById(R.id.txt_details_cuisine_type);
            layoutFacilities = view.findViewById(R.id.layoutFacilities);
            layoutThumblines = view.findViewById(R.id.layoutThumblines);
            layoutTimingSlot = view.findViewById(R.id.layoutTimingSlot);
            txt_details_delivery = view.findViewById(R.id.txt_details_delivery);
            txt_details_collection = view.findViewById(R.id.txt_details_collection);
            txt_details_isRestaurantStatus = view.findViewById(R.id.txt_details_isRestaurantStatus);
            txt_details_type = view.findViewById(R.id.txt_details_type);
            layoutMenu = view.findViewById(R.id.layoutMenu);
            img_details_menu_view = view.findViewById(R.id.img_details_menu_view);
            layoutMap = view.findViewById(R.id.layoutMap);
            buttonLike = view.findViewById(R.id.buttonLike);
            txtLikeText = view.findViewById(R.id.txtLikedText);
            listLayoutDelivery = view.findViewById(R.id.layoutDelivery);
            layoutCollection = view.findViewById(R.id.layoutCollection);
            buttonFavourite = view.findViewById(R.id.buttonFavourite);
            timingScroll = view.findViewById(R.id.timingScroll);
            txtTableLink = view.findViewById(R.id.txtTableLink);
            boxPhotos = view.findViewById(R.id.boxPhotos);
            boxFacility = view.findViewById(R.id.boxFacility);
            textLogoNot = view.findViewById(R.id.textLogoNot);
            layoutLogo = view.findViewById(R.id.layoutLogo);
            imb_btn_membership = view.findViewById(R.id.imb_btn_membership);
            txt_details_avg_price = view.findViewById(R.id.txt_details_avg_price);
            img_order_takeaway = view.findViewById(R.id.img_order_takeaway);
            txt_rate = view.findViewById(R.id.txt_rate);
            recyclerTimeSlot = view.findViewById(R.id.recyclerTimeSlot);
            layoutTable = view.findViewById(R.id.layoutTable);
            txtRestName = view.findViewById(R.id.txtRestName);
            buttonReviewCard = view.findViewById(R.id.buttonReviewCard);
            txtReviewText = view.findViewById(R.id.txtReviewText);
            layout_details_tbl_map = view.findViewById(R.id.layout_details_tbl_map);
            layout_details_table_book_back = view.findViewById(R.id.layout_details_table_book_back);
            layout_details_menu_view_back = view.findViewById(R.id.layout_details_menu_view_back);
            btnRateLike = view.findViewById(R.id.btnRateLike);
            recyclerTimeSlotBack = view.findViewById(R.id.recyclerTimeSlotBack);
        }
    }

    public FavouriteRecyclerAdapter(List<RestaurantDetailModel> countryList, Context mContext,
                                    favouriteInterface callbackInterface,
                                    TimeSlotRecyclerAdapter.timeSlotInterface timeSlotInterface,
                                    Boolean isGpsOn, Double currentLatitude, Double currentLongitude) {
        this.countryList = countryList;
        this.restaurantList = countryList;
        this.mContext = mContext;
        this.timeSlotInterface = timeSlotInterface;
        this.isGpsOn = isGpsOn;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;

        try {
            this.mCallback = callbackInterface;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
            Log.e("MyAdapter", "Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RestaurantDetailModel dataModel = countryList.get(position);
//        holder.timingScroll.scrollTo(0, 0); // scroll to application top
        dayEndTime = "";
        dayStartTime = "";


        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MMM-yyyy");
        currentDate = inFormat.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        currentTime = sdf.format(new Date());


        StringBuilder timesb = new StringBuilder();
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome-webfont.ttf");
        holder.txtName.setText(dataModel.getTitle());
        String name = "<font color=#000>" + mContext.getResources().getString(R.string.about) +
                "</font> <font color=#FF4823>" + dataModel.getTitle() + "</font>";
        holder.txtRestName.setText(Html.fromHtml(name));
        if (isGpsOn)
            holder.txt_details_distance.setText(dataModel.getDistance() + " Miles");
        else {
            holder.txt_details_distance.setText(dataModel.getAddressCity());
            holder.img_btn_mapNavigation.setVisibility(View.GONE);
        }

//        holder.txt_details_avg_price.setText(mContext.getResources().getString(R.string.avg_price) + " £" + dataModel.getAveragePriceForTwo());
        holder.txt_details_avg_price.setText(dataModel.getAveragePriceForTwo() + "");
        final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Regular.ttf");
        holder.txtName.setTypeface(roboto);
        String imgPath = dataModel.getImage();
        imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
        Log.d("<>image-", " img path ==> " + APIConstants.URL + imgPath);
        if (!imgPath.equals("")) {
            Picasso.with(mContext)
                    .load(APIConstants.URL + imgPath)
                    .into(holder.imgLogo);
        } else {
            //get first letter of each String item
            holder.textLogoNot.setVisibility(View.VISIBLE);
            holder.imgLogo.setVisibility(View.GONE);
            String firstLetter = String.valueOf(dataModel.getTitle().charAt(0));
            holder.textLogoNot.setText(firstLetter);
            /*TextDrawable drawable = TextDrawable.builder()
                    .buildRect(firstLetter, mContext.getResources().getColor(R.color.colorWhite)); // radius in px
            holder.imgLogo.setImageDrawable(drawable);*/
        }


        if (dataModel.getDelivery() == true)
            holder.txt_details_delivery.setText(dataModel.getAverageDeliveryTime() + " " + mContext.getString(R.string.mins_title));
        else
            holder.txt_details_delivery.setText(R.string.no_title);
        if (dataModel.getCollection() == true)
            holder.txt_details_collection.setText(dataModel.getAverageCollectionTime() + " " + mContext.getString(R.string.mins_title));
        else
            holder.txt_details_collection.setText(R.string.no_title);

        String packageName = mContext.getPackageName();

        List<String> cuisines = new ArrayList<>();
        cuisines = dataModel.getCuisines();
        if (cuisines.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < cuisines.size(); c++) {
                if (c + 1 == cuisines.size())
                    builder.append(cuisines.get(c));
                else
                    builder.append(cuisines.get(c) + ",");
            }
            holder.txt_details_cuisine_type.setText(builder.toString());
        }

        List<String> imagesList = new ArrayList<>();
        imagesList = dataModel.getImagesList();
        if (imagesList.size() > 0) {
            List<String> thumblinesPaths = new ArrayList<>();
            for (int im = 0; im < imagesList.size(); im++) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.linear_facility),
                        mContext.getResources().getDimensionPixelSize(R.dimen.linear_facility));
                layoutParams.setMargins(7, 7, 7, 7);
                ImageView image = new ImageView(mContext);
//                image.setPadding(5, 2, 5, 2);
                image.setLayoutParams(layoutParams);
                String imgPath1 = imagesList.get(im);
//                imgPath1 = imgPath1.startsWith(".") ? imgPath1.substring(1) : imgPath1;
                Picasso.with(mContext)
                        .load(APIConstants.URL + "/" + imgPath1)
                        .into(image);
                holder.layoutThumblines.addView(image);
                thumblinesPaths.add(APIConstants.URL + "/" + imgPath1);
                image.setTag(im);
                List<String> finalImagesList = imagesList;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("<>log-", "position " + v.getTag() + "");
                        Integer tag = Integer.parseInt(v.getTag() + "");
                        Intent i = new Intent(mContext, ImageFullscreenView.class);
                        i.putStringArrayListExtra("thumblines", (ArrayList<String>) thumblinesPaths);
                        i.putExtra("position", tag);
                        mContext.startActivity(i);
                    }
                });
            }
            MyApplication.getGlobalData().addImageThumblinePath(thumblinesPaths);
        } else {
            holder.layoutThumblines.setVisibility(View.GONE);
        }
        HashMap<String, String> facilitiesList = new HashMap<>();
        facilitiesList = dataModel.getFacilities();
        if (facilitiesList.size() > 0) {
            for (Map.Entry<String, String> map : facilitiesList.entrySet()) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.linear_facility),
                        mContext.getResources().getDimensionPixelSize(R.dimen.linear_facility));
//                layoutParams.setMargins(3, 3, 3, 3);
                LinearLayout linear = new LinearLayout(mContext);
                linear.setOrientation(LinearLayout.VERTICAL);
                linear.setGravity(Gravity.CENTER);
//                linear.setBackground(mContext.getResources().getDrawable(R.drawable.menu_list_style));
                TextView text = new TextView(mContext);
                text.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                text.setTypeface(roboto);
                text.setLines(2);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setTextSize(12);
                text.setGravity(Gravity.CENTER);
//                TextView textImg = new TextView(mContext);
                IconTextView textImg = new IconTextView(mContext);
                textImg.setTextSize(15);
                textImg.setTypeface(roboto_regular);
                textImg.setGravity(Gravity.CENTER);
//                textImg.setPadding(2, 2, 2, 2);
                textImg.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                text.setText(map.getValue().toString());
                Log.d("<>log-", " get value from map ==> " + map.getValue());
                textImg.setText("{" + map.getKey() + "}");
                /*int resId = mContext.getResources().getIdentifier(map.getKey(), "string", packageName);
                Log.d("<>log-", " res id ===> " + resId + "");
                if (resId != 0)
                    textImg.setText(mContext.getString(resId));*/
                linear.addView(textImg);
//                linear.addView(text);
                holder.layoutFacilities.addView(linear, layoutParams);
            }
        } else {
            holder.layoutFacilities.setVisibility(View.GONE);
        }
        String dayOfTheWeek = "";
        HashMap<String, List<String>> timeHashmap = new HashMap<>();
        HashMap<String, Boolean> isOpenDay = new HashMap<>();
        isOpenDay = dataModel.getIsOpenDay();
        timeHashmap = dataModel.getTimeTable();
        final ArrayList<OTimeTable> timesList = new ArrayList<>();
        try {
            Date date = inFormat.parse(inFormat.format(new Date()));
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
                                        dayStartTime = separated[0];
                                        dayEndTime = separated[1];
                                        Log.d("<>bb-", "dayStartTime ==> " + dayStartTime + " dayEndTime" + dayEndTime);
                                        Boolean isOpen = isRestaurantOpen(separated[0], separated[1]);
                                        Log.d("<>bb-", "status return ==> " + isOpen + "");
                                        state.add(isOpen.toString());
                                        if (tm + 1 == map.getValue().size())
                                            sb.append(map.getValue().get(tm));
                                        else
                                            sb.append(map.getValue().get(tm) + ",\n");
                                        if (state.contains("true")) {
                                            holder.txt_details_isRestaurantStatus.setText(R.string.open_title);
                                            holder.txt_details_isRestaurantStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                                            holder.txt_details_type.setText(sb.toString());
                                        } else {
                                            holder.txt_details_isRestaurantStatus.setText(R.string.closed_title);
                                            holder.txt_details_isRestaurantStatus.setTextColor(mContext.getResources().getColor(R.color.colorBrown));
                                            holder.txt_details_type.setText("");
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
                                holder.txt_details_isRestaurantStatus.setText(R.string.closed_title);
                                holder.txt_details_isRestaurantStatus.setTextColor(mContext.getResources().getColor(R.color.colorBrown));
                                holder.txt_details_type.setText("");
                                for (int tm = 0; tm < map.getValue().size(); tm++) {
                                    if (dayName.equals(dayOfTheWeek)) {
                                        if (tm + 1 == map.getValue().size())
                                            sb.append(map.getValue().get(tm));
                                        else
                                            sb.append(map.getValue().get(tm) + ",\n");
                                        timesb.append(sb.toString());
                                        String[] separated = map.getValue().get(tm).split("-");
                                        dayStartTime = separated[0];
                                        dayEndTime = separated[1];
                                        Log.d("<>bb-", "in else dayStartTime ==> " + dayStartTime + " dayEndTime" + dayEndTime);
                                    } else {
                                        if (tm + 1 == map.getValue().size())
                                            sb.append(map.getValue().get(tm));
                                        else
                                            sb.append(map.getValue().get(tm) + ",\n");
                                    }
                                }
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


        Date date1 = null;
        try {
            date1 = sdf.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Date startTime = date1;//start
        Calendar calendarnew = Calendar.getInstance();
        int year = calendarnew.get(Calendar.YEAR);
        int month = calendarnew.get(Calendar.MONTH);
        int day = calendarnew.get(Calendar.DATE);
        calendarnew.set(year, month, day, 23, 59, 59);
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        Integer addHour = Integer.parseInt(sdf1.format(calendarnew.getTime())) - Integer.parseInt(sdf1.format(calendar.getTime()));
        calendar.add(Calendar.HOUR, addHour);
        Date endTime = calendar.getTime();///end
        ArrayList<String> times = new ArrayList<String>();
        Calendar now = Calendar.getInstance();
        now.setTime(startTime);
        now.setTime(now.getTime());
        int unroundedMinutes = now.get(Calendar.MINUTE);
        final int mod = unroundedMinutes % 30;
        now.add(Calendar.MINUTE, mod < 0 ? -mod : (30 - mod));
        int flag = 0;
        while (now.getTime().before(endTime)) {
            if (flag != 0)
                now.add(Calendar.MINUTE, 30);
            times.add(sdf.format(now.getTime()));
            flag++;
        }
        if (times.size() > 0) {
            Log.d("<>tm-", " timse size --> " + times.size() + "");
            List<TimeSlotDetails> timingItems = new ArrayList<>();
            Boolean isSlotEmpty = false;
            for (int slot = 0; slot < times.size(); slot++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 5, 15, 5);
                String slotTime = times.get(slot).toString();
                Log.d("<>sb-", " sb in times ==>" + timesb.toString());
                if (timesb.toString().contains(",")) {
                    String[] separatedList = timesb.toString().split(",\n");
                    Log.d("<>sb-", " separated part ==> " + separatedList.length);
                    for (int s = 0; s < separatedList.length; s++) {
                        String[] separated = separatedList[s].split("-");
                        Log.d("<>tt-", " separated 0 ==> " + separated[0].toString());
                        Log.d("<>tt-", " separated 1 ==> " + separated[1].toString());
                        dayStartTime = separated[0];
                        dayEndTime = separated[1];
                        Log.d("<>check-", position + " out status ==> " + holder.txt_details_isRestaurantStatus.getText() + "");
                        if (!holder.txt_details_isRestaurantStatus.getText().toString().equals(
                                mContext.getResources().getString(R.string.closed_title))) {
                            Log.d("<>check-", position + " status ==> " + holder.txt_details_isRestaurantStatus.getText() + "");
                            isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);
                        } else
                            isSlotEmpty = false;
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
                        Log.d("<>check-", position + " out status ==> " + holder.txt_details_isRestaurantStatus.getText() + "");
                        if (!holder.txt_details_isRestaurantStatus.getText().toString().equals(
                                mContext.getResources().getString(R.string.closed_title))) {
                            Log.d("<>check-", position + " status ==> " + holder.txt_details_isRestaurantStatus.getText() + "");
                            isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);
                        } else
                            isSlotEmpty = false;
//                        isSlotEmpty = isTimeSloatOpen(dayStartTime, dayEndTime, slotTime);
                        Log.d("<>slot-", " else StartTime -> " + dayStartTime + "  ended time -> " + dayEndTime +
                                "slot --> " + slotTime + "  resulkt => " + isSlotEmpty.toString());
                    }
                }
                final TextView text = new TextView(mContext);
                text.setText(times.get(slot).toString());

                if (isSlotEmpty) {
                    text.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_time_slot_style));
                    text.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    text.setTag("1");
                } else {
                    text.setBackground(ContextCompat.getDrawable(mContext, R.drawable.closed_list_time_slot_style));
                    text.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                    text.setTag("0");
                }
                Log.d("<>re-", " timing slot size ==> " + text.getText().toString() + "");
                timingItems.add(new TimeSlotDetails(text.getText().toString(), text.getTag().toString(),
                        dataModel.getRestaurant_id(), "2", dataModel.getTitle()));
                text.setTypeface(roboto_regular);
                text.setTextSize(10);
                text.setGravity(Gravity.CENTER);
                text.setPadding(15, 15, 15, 15);
                text.setLayoutParams(params);
                holder.layoutTimingSlot.addView(text);
            }

            TimeSlotRecyclerAdapter groceryAdapter = new TimeSlotRecyclerAdapter(timingItems,
                    mContext, holder.recyclerTimeSlot, currentDate, timeSlotInterface);
            Log.d("<>aaa-", " tmings --> " + timingItems.toString());
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerTimeSlot.setLayoutManager(horizontalLayoutManager);
            holder.recyclerTimeSlot.setAdapter(groceryAdapter);
            for (int i = 0; i < holder.layoutTimingSlot.getChildCount(); i++) {
                final View child = (TextView) holder.layoutTimingSlot.getChildAt(i);
                Log.d("<>tm-", " child of linear ==> " + child + "");
                if (child.getTag().toString() == "1") {
                    Log.d("<>pos-", " position of timeslot ==> " + i + "");
                    holder.recyclerTimeSlot.scrollToPosition(i);
                    break;
                }
            }

            TimeSlotRecyclerAdapter backAdapter = new TimeSlotRecyclerAdapter(timingItems,
                    mContext, holder.recyclerTimeSlotBack, currentDate, timeSlotInterface);
            LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerTimeSlotBack.setLayoutManager(horizontalLayoutManager1);
            holder.recyclerTimeSlotBack.setAdapter(backAdapter);
            for (int i = 0; i < holder.layoutTimingSlot.getChildCount(); i++) {
                final View child = (TextView) holder.layoutTimingSlot.getChildAt(i);
                Log.d("<>tm-", " child of linear ==> " + child + "");
                if (child.getTag().toString() == "1") {
                    Log.d("<>pos-", " position of timeslot ==> " + i + "");
                    holder.recyclerTimeSlotBack.scrollToPosition(i);
                    break;
                }
            }
        }

        holder.img_btn_calendarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.layout_time_table_restaurant);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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


                OTimeTableAdapter adapter = new OTimeTableAdapter(mContext, timesList);
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
        holder.layoutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGpsOn) {
                    String _id = dataModel.getRestaurant_id();
                    MyApplication.getGlobalData().addRestaurantsFullData(countryList);
                    ArrayList<LatLng> points = new ArrayList<>();
                    for (int i = 0; i < countryList.size(); i++) {
                        Log.d("<>loc-", " position of list ==> " + position + "" + " ===> i" + i + "");
                        if (position != i)
                            points.add(new LatLng(Double.parseDouble(countryList.get(i).getLatitude()), Double.parseDouble(countryList.get(i).getLongitude())));
                    }
                    Log.d("<>loc-", " restaurant points " + points.toString());
                    FragmentActivity activity = (FragmentActivity) (mContext);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    Log.d("<>loc-", " location longitude ==> " + dataModel.getLongitude());
                    Log.d("<>loc-", " location latitude ==> " + dataModel.getLatitude());
                    mapDialog = MapsActivity.newInstance(currentLatitude, currentLongitude
                            , Double.parseDouble(dataModel.getLongitude()),
                            Double.parseDouble(dataModel.getLatitude()), points, _id, currentTime,
                            currentDate, "2", true, dataModel.getDistance());
                    mapDialog.show(fm, "dialog");
                }
            }
        });
        final HashMap<String, String> finalFacilitiesList = facilitiesList;
        holder.imb_btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>filp-", " in imb_btn_more button click ");
                filpPosition = position;
                FlipAnimation flipAnimation = new FlipAnimation(holder.layoutFront, holder.layoutBack);
                if (holder.layoutFront.getVisibility() == View.GONE) {
                    flipAnimation.reverse();
                }
                holder.layoutRoot.startAnimation(flipAnimation);
            }
        });

        holder.img_details_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>filp-", " in back button click ");
                FlipAnimation flipAnimation = new FlipAnimation(holder.layoutBack, holder.layoutFront);

                if (holder.layoutBack.getVisibility() == View.GONE) {
                    Log.d("<>filp-", " in back button click in if condition  ");
                    flipAnimation.reverse();
                }
                holder.layoutRoot.startAnimation(flipAnimation);
            }
        });

        holder.layoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _id = dataModel.getRestaurant_id();
                List<RestaurantDetailModel> model = new ArrayList<>();
                model.add(dataModel);
                MyApplication.getGlobalData().addRestaurantsFullData(model);
                Fragment attachFragment = RestaurantDetailsAboutFragment.newInstance(_id, currentTime,
                        currentDate, "2", true, dataModel.getDistance());
                android.support.v4.app.FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _id = dataModel.getRestaurant_id();
                List<RestaurantDetailModel> model = new ArrayList<>();
                model.add(dataModel);
                MyApplication.getGlobalData().addRestaurantsFullData(model);
                Fragment attachFragment = RestaurantDetailsAboutFragment.newInstance(_id, currentTime,
                        currentDate, "2", true, dataModel.getDistance());
                android.support.v4.app.FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        holder.img_details_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>details-", " on click of details about btn");
               /* String _id = dataModel.getRestaurant_id();
                List<RestaurantDetailModel> model = new ArrayList<>();
                model.add(dataModel);
                MyApplication.getGlobalData().addRestaurantsFullData(model);
                Fragment attachFragment = RestaurantDetailsAboutFragment.newInstance(_id, time,
                        bookDate, people, isBookToday, dataModel.getDistance());
                FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
            }
        });

        holder.layoutCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataModel.getCollection() != false) {
                    Toast toast = Toast.makeText(mContext, "Average collection time of " + dataModel.getAverageCollectionTime() + "" +
                            " mins " + dataModel.getCollectionDiscount() + "% discount for Club Member.", Toast.LENGTH_LONG);
//                    within " + dataModel.getCollectionDiscount() + " % discount of order value.
                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                    /* And now you can get the TextView of the default View of the Toast. */
                    final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Roboto-Light.ttf");
                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(14);
                    toastMessage.setTypeface(roboto);
                    toastMessage.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(16);
                    toastView.setBackground(mContext.getResources().getDrawable(R.drawable.collection_delivery_popup_style));
                    toast.show();
                }
            }
        });

        holder.listLayoutDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataModel.getDelivery() != false) {
                    Toast toast = Toast.makeText(mContext, "Average delivery time of " + dataModel.getAverageDeliveryTime() + "" +
                            " mins \n Delivery charge of £" + dataModel.getDeliveryCharge() + ".", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                    /* And now you can get the TextView of the default View of the Toast. */
                    final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Roboto-Light.ttf");
                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(14);
                    toastMessage.setTypeface(roboto);
                    toastMessage.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(16);
                    toastView.setBackground(mContext.getResources().getDrawable(R.drawable.collection_delivery_popup_style));
                    toast.show();
                }
            }
        });

        holder.imb_btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(mContext, "This Restaurant has CE Club Membership.", Toast.LENGTH_LONG);
                View toastView = toast.getView(); // This'll return the default View of the Toast.

                /* And now you can get the TextView of the default View of the Toast. */
                final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Light.ttf");
                TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                toastMessage.setTextSize(14);
                toastMessage.setTypeface(roboto);
                toastMessage.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                toastMessage.setGravity(Gravity.CENTER);
                toastMessage.setCompoundDrawablePadding(16);
                toastView.setBackground(mContext.getResources().getDrawable(R.drawable.collection_delivery_popup_style));
                toast.show();
            }
        });

        holder.layout_details_menu_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(mContext, MenuListActivity.class);
                menu.putExtra("restaurantId", dataModel.getRestaurant_id());
                menu.putExtra("name", dataModel.getTitle());
                mContext.startActivity(menu);
            }
        });

        holder.layoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent menu = new Intent(mContext, MenuListActivity.class);
                menu.putExtra("restaurantId", dataModel.getRestaurant_id());
                menu.putExtra("name", dataModel.getTitle());
                mContext.startActivity(menu);
            }
        });

        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("<>like-", " in button click of like adapter");
                String userId = "";
                userId = SharedData.getUserId(mContext);

                Log.d("<>like-", " like text  ==> " + holder.buttonLike.getTag().toString());
                if (SharedData.getIsLoggedIn(mContext) && userId != "" && (!holder.buttonLike.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " user id ==> " + userId);
                    if (mCallback != null)
                        mCallback.onRestaurantLike(dataModel.get_id());
                } else if (SharedData.getIsLoggedIn(mContext) && userId != "" && (holder.buttonLike.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " dislike id ==> " + userId);
                    if (mCallback != null)
                        mCallback.onRestaurantDislike(dataModel.get_id());
                } else {
                    if (mCallback != null) {
                        Boolean result = mCallback.onHandleSelection(dataModel.get_id());
                        Log.d("<>like-", " result of ===> " + result.toString());
                    }
                }

            }
        });

//        Animation rotation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_logo);
//        holder.imb_btn_membership.startAnimation(rotation);
        flipIt(holder.imb_btn_membership);

        List<String> likedBy = new ArrayList<>();
        likedBy = dataModel.getLikedBy();
        if (likedBy.size() > 0) {
            holder.txtLikeText.setText(likedBy.size() + "");
            if (SharedData.getIsLoggedIn(mContext) == true) {
                if (likedBy.contains(SharedData.getUserId(mContext))) {
                   /* holder.buttonLike.setText("{fa-heart}");
                    holder.buttonLike.setTag(1);*/
                    holder.buttonLike.setBackground(mContext.getDrawable(R.drawable.ic_like));
                    holder.buttonLike.setTag(1);
                }
            } else {
                if (likedBy.contains(SharedData.getUserId(mContext))) {
                  /*  holder.buttonLike.setText("{fa-heart}");
                    holder.buttonLike.setTag(1);*/
                    holder.buttonLike.setBackground(mContext.getDrawable(R.drawable.ic_like));
                    holder.buttonLike.setTag(1);
                }
            }
        } else {
            holder.txtLikeText.setText("0");
            holder.buttonLike.setTag("0");
//            holder.btnRateLike.setText("0");
//            holder.btnRateLike.setTag(0);
        }

        List<OUser> users = new ArrayList<>();
        users = MyApplication.getGlobalData().getUserData();
        for (int i = 0; i < users.size(); i++) {
            List<String> favourites = new ArrayList<>();
            favourites = users.get(i).getFavouriteRestaurants();
            Log.d("<>log-", " user list of restaurant ==? " + favourites.toString());
            if (favourites.size() > 0) {
                Log.d("<>log-", " restaurant id  ==? " + favourites.contains(dataModel.getId()));
                if (favourites.contains(dataModel.getId())) {
                    holder.buttonFavourite.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favourites));
                    holder.buttonFavourite.setTag("1");
                }
            }
        }

        holder.buttonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = "";
                userId = SharedData.getUserId(mContext);

                if (SharedData.getIsLoggedIn(mContext) && userId != "" && (!holder.buttonFavourite.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " user id ==> " + userId);
                    if (mCallback != null)
                        mCallback.onAddRestaurantFavourite(dataModel.getId());
                } else if (SharedData.getIsLoggedIn(mContext) && userId != "" && (holder.buttonFavourite.getTag().toString().equals("1"))) {
                    Log.d("<>like-", " dislike id ==> " + userId);
                    if (mCallback != null)
                        mCallback.onAddRestaurantFavouriteRemove(dataModel.getId());
                } else {
                    if (mCallback != null) {
                        Boolean result = mCallback.onHandleSelection(dataModel.getId());
                        Log.d("<>like-", " result of ===> " + result.toString());
                    }
                }
            }
        });

        Log.d("<>mem-", " membership logo ==>" + dataModel.getMembership() + "");
        if (dataModel.getMembership() == true)
            holder.imb_btn_membership.setVisibility(View.VISIBLE);

        if (dataModel.getTakeaway() == false) {
            holder.img_order_takeaway.setEnabled(false);
            holder.img_order_takeaway.setAlpha(0.5f);
        }

        holder.layoutTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "";
                userId = SharedData.getUserId(mContext);

                if (holder.txt_details_isRestaurantStatus.getText().equals(mContext.getResources().getString(R.string.open_title))) {

                    if (SharedData.getIsLoggedIn(mContext) && userId != "") {
                        if (mCallback != null) {
                            mCallback.onOpenBookATableForm(dataModel.getRestaurant_id(), dataModel.getTitle());
                        }
                    } else {
                        if (mCallback != null) {
                            Boolean result = mCallback.onHandleSelection(dataModel.getId());
                            Log.d("<>like-", " result of ===> " + result.toString());
                        }
                    }
                }
            }
        });

        holder.layout_details_table_book_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "";
                userId = SharedData.getUserId(mContext);

                if (SharedData.getIsLoggedIn(mContext) && userId != "") {
                    if (mCallback != null) {
                        mCallback.onOpenBookATableForm(dataModel.getRestaurant_id(), dataModel.getTitle());
                    }
                } else {
                    if (mCallback != null) {
                        Boolean result = mCallback.onHandleSelection(dataModel.getId());
                        Log.d("<>like-", " result of ===> " + result.toString());
                    }
                }
            }
        });

       /* if (MyApplication.getGlobalData().getOpen()){
            if(!holder.txt_details_isRestaurantStatus.getText().equals("true")){
            }
        }*/

        holder.buttonReviewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*Intent intent = new Intent(mContext, RestaurantReviews.class);
               intent.putExtra("restaurantName", dataModel.getTitle());
               mContext.startActivity(intent);*/
                FragmentActivity activity = (FragmentActivity) (mContext);
                Fragment fragment = RestaurantReviews.newInstance(dataModel.getTitle(), dataModel.getRestaurant_id(), dataModel.getNumberOfReviews()+"");
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        holder.txtReviewText.setText(String.valueOf(dataModel.getNumberOfReviews() + " " + mContext.getString(R.string.review_text)));
//        holder.btnRateLike.setText(String.valueOf(dataModel.getNumberOfReviews()));

        holder.layout_details_tbl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RestaurantDetailModel> model = new ArrayList<>();
                model.add(dataModel);
                MyApplication.getGlobalData().addRestaurantsFullData(model);
                Intent intent = new Intent(mContext, TableMaps.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_final_list_restaurant_details, parent, false);
        return new MyViewHolder(v);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("<>MyAdapter", "onActivityResult");
    }

    private Boolean isRestaurantOpen(String startTime, String endTime) {
        Log.d("<>bb-", "in method ==> " + dayStartTime + " dayEndTime" + dayEndTime + "" +
                "current time " + currentTime);
        try {
            String string1 = startTime;
            Date time1 = new SimpleDateFormat("hh:mm aa").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String test = "12:00 AM";
            Date testTime = new SimpleDateFormat("hh:mm aa").parse(test);
            Calendar testCalendar = Calendar.getInstance();
            testCalendar.setTime(testTime);
            String string2 = endTime;
            Log.d("<>time-", " end time ==> " + endTime);
            Date time2 = new SimpleDateFormat("hh:mm aa").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
           /* Date calen2 = calendar2.getTime();
            if (calen2.after(testCalendar.getTime())) {
                calendar2.add(Calendar.DATE, 1);
            }*/


            String someRandomTime = currentTime;
            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
//            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            Log.d("<>bb-", "calGetTime ==> " + calendar1.getTime().toString() + "\n" +
                    " cal2 time ==> " + calendar2.getTime().toString() + " cal x ==> " + x.toString());
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
            /*Date calen2 = calendar2.getTime();
            Log.d("<>tm-", " compare time -->" + calen2.compareTo(testCalendar.getTime()) + "");
            if (calen2.after(testCalendar.getTime()) || calen2.compareTo(testCalendar.getTime()) == 0)
                calendar2.add(Calendar.DATE, 1);*/

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

    public interface favouriteInterface {

        /**
         * Callback invoked when clicked
         *
         * @param text - the text to pass back
         */
        Boolean onHandleSelection(String text);

        void onRestaurantDislike(String text);

        void onRestaurantLike(String text);

        void onAddRestaurantFavourite(String text);

        void onAddRestaurantFavouriteRemove(String text);

        void onOpenBookATableForm(String restaurantId, String restaurantName);
    }

    private void scrollToRow(HorizontalScrollView scrollView, LinearLayout linearLayout, TextView textViewToShow) {
        Rect textRect = new Rect(); //coordinates to scroll to
        textViewToShow.getHitRect(textRect); //fills textRect with coordinates of TextView relative to its parent (LinearLayout)
        scrollView.requestChildRectangleOnScreen(linearLayout, textRect, false); //ScrollView will make sure, the given textRect is visible
    }

    private void flipIt(final View viewToFlip) {
        ObjectAnimator flip = ObjectAnimator.ofFloat(viewToFlip, "rotationY", 0f, 360f);
        flip.setDuration(3000);
        flip.setStartDelay(0);
        flip.setRepeatMode(ObjectAnimator.RESTART);
        flip.setInterpolator(new AccelerateInterpolator());
        flip.setRepeatCount(Animation.INFINITE);
        flip.start();

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    countryList = restaurantList;
                } else {
                    List<RestaurantDetailModel> filteredList = new ArrayList<>();
                    for (RestaurantDetailModel row : restaurantList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    countryList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = countryList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                countryList = (ArrayList<RestaurantDetailModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Filter Class
    public void filter(String charText) {
        String charString = charText.toString();
        if (charString.isEmpty()) {
            countryList = restaurantList;
        } else {
            List<RestaurantDetailModel> filteredList = new ArrayList<>();
            for (RestaurantDetailModel row : restaurantList) {

                // name match condition. this might differ depending on your requirement
                // here we are looking for name or phone number match
                if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                    filteredList.add(row);
                }
            }

            countryList = filteredList;
        }
        notifyDataSetChanged();
    }
}
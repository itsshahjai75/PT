package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.RemoteController;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.OCuisines;
import com.clickeat.customer.click_eatcustomer.DataModel.OFacilities;
import com.clickeat.customer.click_eatcustomer.DataModel.OSpecialDiet;
import com.clickeat.customer.click_eatcustomer.MyAccounts.MyAccountFragment;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class FilterDetails extends Activity implements View.OnClickListener {
    ImageButton imgBtnDistanceExpand, imgBtnCuisineExpand;
    LinearLayout layoutDistanceExpand, layoutPriceExpand, layoutCuisineExpand, layoutCollectionExpand,
            layoutFacilityExpand, layoutSpecialDietExpand, layoutAvgPriceExpand, layoutQuickExpand;
    ImageButton imgBtnPriceExpand, imgBtnCollectionExpand, imgBtnFacilitiesExpand, imgBtnDietsExpand, imgBtnAvgPriceExpand,
            imgBtnQuickExpand, btnFilterClose;
    Button btnResetFilter, btnApplyFilter;
    TextView txtMin, txtMax, txtMinPrice, txtMaxPrice, txtMinAvgPrice, seekBarAvgPriceIndicator, txtMaxAvgPrice;
    ListView listviewFacilities, listviewCuisines, listviewSpecialDiet;
    LinearLayout layoutCollapseDistance, layoutCollapseCuisine, layoutCollapseCollection, layoutCollapseFacility,
            layoutCollapseDiet, layoutCollapseRating, layoutCollapseOffer, layoutCollapseAvgPrice, layoutCollapseQuick;
    CheckBox checkBoxCollection, checkBoxDelivery, checkBoxVeg, checkBoxOpen, checkBoxDeliveryQuick, checkBoxClubMember, checkBoxMeals;

    boolean isDistancePressed = false, isPricePressed = false, isCuisinePressed = false, isCollectionPressed = false,
            isFacilitiesPressed = false, isDietPressed = false, isAvgPricePressed = false, isQuickPressed = false;
    private ProgressDialog loadingSpinner;
    private ArrayList<String> collectionDeliveryList;
    private ArrayList<String> oldCuisine, oldFacility, oldCollection;
    int previousGroup = -1;
    private IndicatorSeekBar distanceSB, averagePriceSB;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_details);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        findIds();
        getFacilitiesList();
//        ButterKnife.bind(FilterDetails.this);
        layoutCollapseQuick.setOnClickListener(this);
        layoutCollapseCollection.setOnClickListener(this);
        layoutCollapseCuisine.setOnClickListener(this);
        layoutCollapseAvgPrice.setOnClickListener(this);
        layoutCollapseFacility.setOnClickListener(this);
        layoutCollapseDiet.setOnClickListener(this);
        layoutCollapseRating.setOnClickListener(this);
        btnResetFilter.setOnClickListener(this);
        btnApplyFilter.setOnClickListener(this);
        btnFilterClose.setOnClickListener(this);
        layoutCollapseOffer.setOnClickListener(this);
        layoutCollapseDistance.setOnClickListener(this);
        collectionDeliveryList = new ArrayList<>();
        collectionDeliveryList = MyApplication.getGlobalData().getCollectionDeliveryList();
        oldCollection = new ArrayList<>();
        oldCuisine = new ArrayList<>();
        oldFacility = new ArrayList<>();
        oldCollection = MyApplication.getGlobalData().getCollectionDeliveryList();
        oldFacility = MyApplication.getGlobalData().getFacilitiesList();
        oldCuisine = MyApplication.getGlobalData().getCuisineList();
        if (collectionDeliveryList.contains(this.getResources().getString(R.string.collection_title)))
            checkBoxCollection.setChecked(true);
        if (collectionDeliveryList.contains(this.getResources().getString(R.string.delivery_title)))
            checkBoxDelivery.setChecked(true);
        init();

        if (MyApplication.getGlobalData().getVeg())
            checkBoxVeg.setChecked(true);

        if (MyApplication.getGlobalData().getDelivery())
            checkBoxDeliveryQuick.setChecked(true);

        if (MyApplication.getGlobalData().getClubMember())
            checkBoxClubMember.setChecked(true);

        if (MyApplication.getGlobalData().getMealsDeals())
            checkBoxMeals.setChecked(true);

        if (MyApplication.getGlobalData().getOpen())
            checkBoxOpen.setChecked(true);
        listviewFacilities.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(listviewFacilities);

        listviewCuisines.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(listviewCuisines);

        listviewSpecialDiet.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent
                    event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(listviewSpecialDiet);

        checkBoxCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    Log.d("<>resta-", " item checked true==> ");
                    if (!collectionDeliveryList.contains(checkBoxCollection.getText().toString()))
                        collectionDeliveryList.add(checkBoxCollection.getText().toString());
                    checkBoxCollection.setChecked(true);

                } else {
                    // code here
                    Log.d("<>resta-", " item checked false==> ");
                    if (collectionDeliveryList.contains(checkBoxCollection.getText().toString()))
                        collectionDeliveryList.remove(checkBoxCollection.getText().toString());
                }
            }
        });

        checkBoxDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    Log.d("<>resta-", " item checked true==> ");
                    if (!collectionDeliveryList.contains(checkBoxDelivery.getText().toString()))
                        collectionDeliveryList.add(checkBoxDelivery.getText().toString());
                    checkBoxDelivery.setChecked(true);
                } else {
                    // code here
                    Log.d("<>resta-", " item checked false==> ");
                    if (collectionDeliveryList.contains(checkBoxDelivery.getText().toString()))
                        collectionDeliveryList.remove(checkBoxDelivery.getText().toString());
                }
            }
        });

        checkBoxVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    checkBoxVeg.setChecked(true);
                    MyApplication.getGlobalData().setVeg(true);
                } else {
                    // code here
                    checkBoxVeg.setChecked(false);
                    MyApplication.getGlobalData().setVeg(false);
                }
            }
        });

        checkBoxMeals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    checkBoxMeals.setChecked(true);
                    MyApplication.getGlobalData().setMealsDeals(true);
                } else {
                    // code here
                    checkBoxMeals.setChecked(false);
                    MyApplication.getGlobalData().setMealsDeals(false);
                }
            }
        });

        checkBoxClubMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    checkBoxClubMember.setChecked(true);
                    MyApplication.getGlobalData().setClubMember(true);
                } else {
                    // code here
                    checkBoxClubMember.setChecked(false);
                    MyApplication.getGlobalData().setClubMember(false);
                }
            }
        });

        checkBoxOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    checkBoxOpen.setChecked(true);
                    MyApplication.getGlobalData().setOpen(true);
                } else {
                    // code here
                    checkBoxOpen.setChecked(false);
                    MyApplication.getGlobalData().setOpen(false);
                }
            }
        });

        checkBoxDeliveryQuick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do sometheing here
                    checkBoxDeliveryQuick.setChecked(true);
                    MyApplication.getGlobalData().setDelivery(true);
                } else {
                    // code here
                    checkBoxDeliveryQuick.setChecked(false);
                    MyApplication.getGlobalData().setDelivery(false);
                    if (collectionDeliveryList.contains(checkBoxDelivery.getText().toString()))
                        collectionDeliveryList.remove(checkBoxDelivery.getText().toString());
                }
            }
        });

        distanceSB.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                Integer mProgress = seekBar.getProgress();
                MyApplication.getGlobalData().setSliderValue(mProgress);
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Integer mProgress = seekBar.getProgress();
                MyApplication.getGlobalData().setSliderValue(mProgress);
                if (seekBar.getProgress() == 0)
                    distanceSB.getIndicator().hide();
                else if (seekBar.getProgress() == 10)
                    distanceSB.getIndicator().hide();
                else
                    distanceSB.getIndicator().show();
            }
        });

        averagePriceSB.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                Integer mProgress = seekBar.getProgress();
                MyApplication.getGlobalData().setAvgPrice(mProgress);
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Integer mProgress = seekBar.getProgress();
                MyApplication.getGlobalData().setAvgPrice(mProgress);
            }
        });

    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void findIds() {
        imgBtnDistanceExpand = findViewById(R.id.imgBtnDistanceExpand);
        layoutDistanceExpand = findViewById(R.id.layoutDistanceExpand);
        layoutCuisineExpand = findViewById(R.id.layoutCuisineExpand);
        layoutCollectionExpand = findViewById(R.id.layoutCollectionExpand);
        layoutFacilityExpand = findViewById(R.id.layoutFacilityExpand);
        layoutPriceExpand = findViewById(R.id.layoutPriceExpand);
        imgBtnPriceExpand = findViewById(R.id.imgBtnPriceExpand);
        imgBtnCuisineExpand = findViewById(R.id.imgBtnCuisineExpand);
        imgBtnCollectionExpand = findViewById(R.id.imgBtnCollectionExpand);
        imgBtnFacilitiesExpand = findViewById(R.id.imgBtnFacilitiesExpand);
        imgBtnDietsExpand = findViewById(R.id.imgBtnDietsExpand);
        txtMax = findViewById(R.id.txtMax);
        txtMin = findViewById(R.id.txtMin);
        txtMaxPrice = findViewById(R.id.txtMaxPrice);
        txtMinPrice = findViewById(R.id.txtMinPrice);
        listviewFacilities = findViewById(R.id.listviewFacilities);
        listviewCuisines = findViewById(R.id.listviewCuisines);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnResetFilter = findViewById(R.id.btnResetFilter);
        checkBoxCollection = findViewById(R.id.checkBoxCollection);
        checkBoxDelivery = findViewById(R.id.checkBoxDelivery);
        listviewSpecialDiet = findViewById(R.id.listviewSpecialDiet);
        layoutSpecialDietExpand = findViewById(R.id.layoutSpecialDietExpand);
        layoutCollapseDistance = findViewById(R.id.layoutCollapseDistance);
        layoutCollapseCuisine = findViewById(R.id.layoutCollapseCuisine);
        layoutCollapseCollection = findViewById(R.id.layoutCollapseCollection);
        layoutCollapseFacility = findViewById(R.id.layoutCollapseFacility);
        layoutCollapseDiet = findViewById(R.id.layoutCollapseDiet);
        layoutCollapseRating = findViewById(R.id.layoutCollapseRating);
        layoutCollapseOffer = findViewById(R.id.layoutCollapseOffer);
        layoutCollapseAvgPrice = findViewById(R.id.layoutCollapseAvgPrice);
        layoutAvgPriceExpand = findViewById(R.id.layoutAvgPriceExpand);
        imgBtnAvgPriceExpand = findViewById(R.id.imgBtnAvgPriceExpand);
        txtMinAvgPrice = findViewById(R.id.txtMinAvgPrice);
        seekBarAvgPriceIndicator = findViewById(R.id.seekBarAvgPriceIndicator);
        txtMaxAvgPrice = findViewById(R.id.txtMaxAvgPrice);
        layoutCollapseQuick = findViewById(R.id.layoutCollapseQuick);
        layoutQuickExpand = findViewById(R.id.layoutQuickExpand);
        imgBtnQuickExpand = findViewById(R.id.imgBtnQuickExpand);
        checkBoxDeliveryQuick = findViewById(R.id.checkDelivery);
        checkBoxOpen = findViewById(R.id.checkOpen);
        checkBoxVeg = findViewById(R.id.checkPureVeg);
        distanceSB = findViewById(R.id.distanceSB);
        averagePriceSB = findViewById(R.id.averagePriceSB);
        checkBoxClubMember = findViewById(R.id.chk_filter_member);
        checkBoxMeals = findViewById(R.id.chk_filter_meals);
        btnFilterClose = findViewById(R.id.btnFilterClose);
    }

    private void init() {
        Integer mProgress = MyApplication.getGlobalData().getSliderValue();
        Log.d("<>mPro-", " progress is ==> "+mProgress);
        distanceSB.setProgress(mProgress);
        averagePriceSB.setProgress(MyApplication.getGlobalData().getAvgPrice());
        ArrayList<OSpecialDiet> dietList = new ArrayList<>();
        dietList = MyApplication.getGlobalData().getSpecialDietsList();
        ArrayList<OFacilities> facilitiesList = new ArrayList<>();
        facilitiesList = MyApplication.getGlobalData().getOFacilitiesList();
        FilterFacilitiesAdapter adapter = new FilterFacilitiesAdapter(FilterDetails.this, facilitiesList);
        listviewFacilities.setAdapter(adapter);

        FilterSpecialDietsAdapter adapter1 = new FilterSpecialDietsAdapter(FilterDetails.this, dietList);
        listviewSpecialDiet.setAdapter(adapter1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutCollapseDistance:
                if (isDistancePressed) {
                    imgBtnDistanceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutDistanceExpand.setVisibility(View.GONE);
                    layoutPriceExpand.setVisibility(View.GONE);
                    layoutCuisineExpand.setVisibility(View.GONE);
                    distanceSB.setVisibility(View.GONE);
                    distanceSB.getIndicator().forceHide();
                } else {
                    imgBtnDistanceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutDistanceExpand.setVisibility(View.VISIBLE);
                    distanceSB.setVisibility(View.VISIBLE);
                }
                isDistancePressed = !isDistancePressed;
                distanceSB.setProgress(MyApplication.getGlobalData().getSliderValue());
                onExapandItem(1);
                break;
            case R.id.imgBtnPriceExpand:
                if (isPricePressed) {
                    imgBtnPriceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutPriceExpand.setVisibility(View.GONE);
                } else {
                    imgBtnPriceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutPriceExpand.setVisibility(View.VISIBLE);
                }
                isPricePressed = !isPricePressed;
                break;
            case R.id.layoutCollapseCuisine:
                if (isCuisinePressed) {
                    imgBtnCuisineExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutCuisineExpand.setVisibility(View.GONE);
                } else {
                    imgBtnCuisineExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutCuisineExpand.setVisibility(View.VISIBLE);
                    ArrayList<OCuisines> cuisines = new ArrayList<>();
                    cuisines = MyApplication.getGlobalData().getOCuisinesList();
                    ArrayList<String> demo = new ArrayList<>();
                    MyApplication.getGlobalData().addDemo(demo);
                    FilterCuisinesAdapter adapter = new FilterCuisinesAdapter(FilterDetails.this, cuisines);
                    listviewCuisines.setAdapter(adapter);
                }
                isCuisinePressed = !isCuisinePressed;
                onExapandItem(2);
                break;
            case R.id.layoutCollapseCollection:
                if (isCollectionPressed) {
                    imgBtnCollectionExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutCollectionExpand.setVisibility(View.GONE);
                } else {
                    imgBtnCollectionExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutCollectionExpand.setVisibility(View.VISIBLE);
                }
                isCollectionPressed = !isCollectionPressed;
                onExapandItem(3);
                break;
            case R.id.layoutCollapseFacility:
                if (isFacilitiesPressed) {
                    imgBtnFacilitiesExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutFacilityExpand.setVisibility(View.GONE);
                } else {
                    imgBtnFacilitiesExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutFacilityExpand.setVisibility(View.VISIBLE);
                }
                isFacilitiesPressed = !isFacilitiesPressed;
                onExapandItem(4);
                break;
            case R.id.layoutCollapseDiet:
                if (isDietPressed) {
                    imgBtnDietsExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutSpecialDietExpand.setVisibility(View.GONE);
                } else {
                    imgBtnDietsExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutSpecialDietExpand.setVisibility(View.VISIBLE);
                }
                isDietPressed = !isDietPressed;
                onExapandItem(5);
                break;
            case R.id.layoutCollapseRating:
                break;
            case R.id.layoutCollapseOffer:
                break;
            case R.id.btnApplyFilter:
                filterApply();
                break;
            case R.id.btnResetFilter:
                resetFilter();
                break;
            case R.id.layoutCollapseAvgPrice:
                if (isAvgPricePressed) {
                    imgBtnAvgPriceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutAvgPriceExpand.setVisibility(View.GONE);
                    layoutPriceExpand.setVisibility(View.GONE);
                    layoutCuisineExpand.setVisibility(View.GONE);
                    averagePriceSB.setVisibility(View.GONE);
                    averagePriceSB.getIndicator().forceHide();
                } else {
                    imgBtnAvgPriceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutAvgPriceExpand.setVisibility(View.VISIBLE);
                    averagePriceSB.setVisibility(View.VISIBLE);
                }
                isAvgPricePressed = !isAvgPricePressed;
                averagePriceSB.setProgress(MyApplication.getGlobalData().getAvgPrice());
                onExapandItem(6);
                break;
            case R.id.layoutCollapseQuick:
                if (isQuickPressed) {
                    imgBtnQuickExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutQuickExpand.setVisibility(View.GONE);
                } else {
                    imgBtnQuickExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    layoutQuickExpand.setVisibility(View.VISIBLE);
                }
                isQuickPressed = !isQuickPressed;
                onExapandItem(7);
                break;
            case R.id.btnFilterClose:
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                break;
        }
    }

    /* Call this method on Apply button click */
    private void filterApply() {
        List<String> facility = MyApplication.getGlobalData().getFacilitiesList();
        Log.d("<>filter-", " facility list in apply ==> " + facility.toString());
        List<String> cdlist = MyApplication.getGlobalData().getCollectionDeliveryList();
        Log.d("<>filter-", " cdlist list in apply ==> " + cdlist.toString());
        List<String> cuisineList = MyApplication.getGlobalData().getCuisineList();
        Log.d("<>filter-", " cuisine list in apply ==> " + cuisineList.toString());
        Log.d("<>filter-", " slider value ==> " + MyApplication.getGlobalData().getSliderValue() + "");
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /* Call this method on Cancel button click */
    private void resetFilter() {
        List<String> diets = MyApplication.getGlobalData().getDietsList();
        if (diets.size() > 0) {
            List<OSpecialDiet> specialDiets = MyApplication.getGlobalData().getSpecialDietsList();
            for (int spe = 0; spe < specialDiets.size(); spe++) {
                if (diets.contains(specialDiets.get(spe).getName())) {
                    specialDiets.get(spe).setStatus(false);
                }
            }
            MyApplication.getGlobalData().getDietsList().clear();
        }
        List<String> facilities = MyApplication.getGlobalData().getFacilitiesList();
        if (facilities.size() > 0) {
            List<OFacilities> oFacilities = MyApplication.getGlobalData().getOFacilitiesList();
            for (int spe = 0; spe < oFacilities.size(); spe++) {
                if (facilities.contains(oFacilities.get(spe).getName())) {
                    oFacilities.get(spe).setStatus(false);
                }
            }
            MyApplication.getGlobalData().getFacilitiesList().clear();
        }
        List<String> cuisines = MyApplication.getGlobalData().getCuisineList();
        if (cuisines.size() > 0) {
            List<OCuisines> cuisines1 = MyApplication.getGlobalData().getOCuisinesList();
            for (int cui = 0; cui < cuisines1.size(); cui++) {
                if (cuisines.contains(cuisines1.get(cui).getName()))
                    cuisines1.get(cui).setStatus(false);
            }
            MyApplication.getGlobalData().getCuisineList().clear();
        }
        MyApplication.getGlobalData().getDietsList().clear();
        MyApplication.getGlobalData().setMealsDeals(false);
        MyApplication.getGlobalData().setClubMember(false);
        MyApplication.getGlobalData().setSliderValue(5);
        MyApplication.getGlobalData().setAvgPrice(20);
        MyApplication.getGlobalData().setOpen(false);
        MyApplication.getGlobalData().setVeg(false);
        MyApplication.getGlobalData().setDelivery(false);
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
//        MyApplication.getGlobalData().addFacility(oldFacility);
//        MyApplication.getGlobalData().addCollectionDelivery(oldCollection);

    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(FilterDetails.this, R.style.AppCompatAlertDialogStyle);
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

    private void onExapandItem(Integer groupPosition) {
      /*  if (groupPosition != previousGroup)
            layoutCuisineExpand.setVisibility(View.GONE);
        previousGroup = groupPosition;*/
        if (groupPosition != previousGroup) {
            switch (previousGroup) {
                case 1:
                    imgBtnDistanceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutDistanceExpand.setVisibility(View.GONE);
                    distanceSB.getIndicator().forceHide();
                    distanceSB.setVisibility(View.GONE);
                    isDistancePressed = !isDistancePressed;
                    previousGroup = groupPosition;
                    break;
                case 2:
                    imgBtnCuisineExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutCuisineExpand.setVisibility(View.GONE);
                    isCuisinePressed = !isCuisinePressed;
                    previousGroup = groupPosition;
                    break;
                case 3:
                    imgBtnCollectionExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutCollectionExpand.setVisibility(View.GONE);
                    isCollectionPressed = !isCollectionPressed;
                    previousGroup = groupPosition;
                    break;
                case 4:
                    imgBtnFacilitiesExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutFacilityExpand.setVisibility(View.GONE);
                    isFacilitiesPressed = !isFacilitiesPressed;
                    previousGroup = groupPosition;
                    break;
                case 5:
                    imgBtnDietsExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutSpecialDietExpand.setVisibility(View.GONE);
                    isDietPressed = !isDietPressed;
                    previousGroup = groupPosition;
                    break;
                case 6:
                    imgBtnAvgPriceExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutAvgPriceExpand.setVisibility(View.GONE);
                    averagePriceSB.getIndicator().forceHide();
                    averagePriceSB.setVisibility(View.GONE);
                    isAvgPricePressed = !isAvgPricePressed;
                    previousGroup = groupPosition;
                    break;
                case 7:
                    imgBtnQuickExpand.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
                    layoutQuickExpand.setVisibility(View.GONE);
                    isQuickPressed = !isQuickPressed;
                    previousGroup = groupPosition;
                    break;
            }
        }
        previousGroup = groupPosition;
    }

    /* get special diet list from API */
    public void getSpecialDiets() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        Call<JsonElement> mService = mInterfaceService.getSpecialDiet(MyApplication.sDefSystemLanguage);
        Log.d("<>facility-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>facility-", " Headers is  ==> " + mService.request().headers().toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>facility-", " response msg ===> " + response.message());
                Log.d("<>facility-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>facility-", " result from server " + response.body().toString());
                    Log.d("<>facility-", " result from server " + response.body().getAsJsonArray());
                    JsonArray listData = response.body().getAsJsonArray();
                    Log.d("<>facility-", "list data of json ===> " + listData.size() + "");
                    ArrayList<OSpecialDiet> diets = new ArrayList<>();
                    List<String> dietName = new ArrayList<>();
                    for (int i = 0; i < listData.size(); i++) {
                        dietName.add(listData.get(i).getAsJsonObject().getAsJsonObject(MyApplication.sDefSystemLanguage).get("name").getAsString());
                        OSpecialDiet specialDiet = new OSpecialDiet(
                                listData.get(i).getAsJsonObject().getAsJsonObject(MyApplication.sDefSystemLanguage).get("name").getAsString(),
                                "",
//                                listData.get(i).getAsJsonObject().getAsJsonObject(MyApplication.sDefSystemLanguage).get("description").getAsString(),
                                false
                        );
                        diets.add(specialDiet);
                        Log.d("<>diet-", "json ===> " + diets.toString());
//                        MyApplication.getGlobalData().addDiets(dietName);
                        MyApplication.getGlobalData().addOSpecialDiets(diets);

                    }
                } else {
                    Toast.makeText(FilterDetails.this, R.string.title_something_wrong, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Log.d("<>Details-", " on failure of get special diets.");
            }
        });
    }

    /* get facilities list from API */
    public void getFacilitiesList() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        Call<JsonElement> mService = mInterfaceService.getFacilitiesList(MyApplication.sDefSystemLanguage);
        Log.d("<>facility-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>facility-", " Headers is  ==> " + mService.request().headers().toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>facility-", " response msg ===> " + response.message());
                Log.d("<>facility-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>facility-", " result from server " + response.body().toString());
                    Log.d("<>facility-", " result from server " + response.body().getAsJsonArray());
                    JsonArray listData = response.body().getAsJsonArray();
                    Log.d("<>facility-", "list data of json ===> " + listData.size() + "");
                    ArrayList<OFacilities> facilities = new ArrayList<>();
                    for (int i = 0; i < listData.size(); i++) {
                        OFacilities facility = new OFacilities(
                                listData.get(i).getAsJsonObject().getAsJsonObject(MyApplication.sDefSystemLanguage).get("name").getAsString(),
                                listData.get(i).getAsJsonObject().get("icon").getAsString(),
                                false
                        );
                        facilities.add(facility);
                        Log.d("<>facility-", "json ===> " + facilities.toString());
                        MyApplication.getGlobalData().addFacilitiesList(facilities);

                    }
                } else {
                    Toast.makeText(FilterDetails.this, R.string.title_something_wrong, Toast.LENGTH_LONG).show();
                }
                getSpecialDiets();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Log.d("<>Details-", " on failure of get facilities.");
                getSpecialDiets();
            }
        });
    }

}

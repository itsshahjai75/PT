package com.clickeat.restaurant.click_eatrestaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.DishNameDetailModel;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.MenuDetailsModel;
import com.clickeat.restaurant.click_eatrestaurant.adapters.MenuItemAdapter;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MenuManagementCustomerApp extends Activity {
    private ProgressDialog loadingSpinner;
    private String restaurantId, name;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MenuPagerAdapter mPagerAdapter;
    private Button btnMenuClose;
    private TextView txtListStatus, textMenuHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);
        Intent am = getIntent();
        if (am != null) {
            restaurantId = am.getStringExtra("restaurantId");
            name = am.getStringExtra("name");
        }
        mTabLayout = (TabLayout) findViewById(R.id.tabsMenu);
        mViewPager = (ViewPager) findViewById(R.id.viewpagerMenu);
        textMenuHeader = (TextView) findViewById(R.id.textMenuHeader);
        txtListStatus = findViewById(R.id.txtListStatus);
        btnMenuClose = findViewById(R.id.btnMenuClose);
        textMenuHeader.setText(name);
        mPagerAdapter = new MenuPagerAdapter(MenuManagementCustomerApp.this);
        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.setOnPageChangeListener(mTabLayout.);
        mTabLayout.setOnTabSelectedListener(mTabListener);
        mTabLayout.setupWithViewPager(mViewPager);
        getAllMenus(restaurantId);
        Log.d("<>dt-", " restaurant id ==> " + restaurantId);

        btnMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        ArrayList<MenuDetailsModel> menuDataAll = new ArrayList<>();
        menuDataAll = MyApplication.getGlobalData().getMenuDetailsDataList();
        List<String> categoryList = new ArrayList<>();
        List<DishNameDetailModel> menuDetails = new ArrayList<>();
        if (menuDataAll.size() > 0) {
            for (MenuDetailsModel model : menuDataAll) {
                menuDetails = new ArrayList<>();
                mTabLayout.addTab(mTabLayout.newTab().setText(model.getCategoryName()));
                menuDetails = model.getDishnameDetails();
                mPagerAdapter.addTab(model.getCategoryName(), menuDetails);
                /*categoryList.add(model.getCategoryName());
                menuDetails = model.getDishnameDetails();*/
            }
        }
      /*  Log.d("<>dt-", "category list ==> " + categoryList.toString());
        if (categoryList.size() > 0) {
            for (int categ = 0; categ < categoryList.size(); categ++) {

            }
        }*/

       /* ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 1; i <= 3; i++) {
            ActionBar.Tab tab = actionBar.newTab();
            tab.setText("Tab " + i);
            tab.setTabListener(this);
            actionBar.addTab(tab);
        }*/
    }

    private void getAllMenus(final String restaurantId) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.SERVER_URL_ONLY)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject restId = new JsonObject();
        restId.addProperty("restaurant_id", restaurantId);
        JsonObject object = new JsonObject();
        object.add("search", restId);
        Call<JsonElement> mService = mInterfaceService.getMenuDetailsByRestaurantId(object);

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.d("<>log-", " in response of login ");
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        JsonObject menuResponse = response.body().getAsJsonObject();
                        Log.d("<>log-", " status of menu ===> " + menuResponse.getAsJsonObject().get("status").getAsInt() + "");
                        List<MenuDetailsModel> menuModels = new ArrayList<>();
                        if (menuResponse.isJsonObject() && !menuResponse.isJsonNull()) {
                            if (menuResponse.getAsJsonObject().has("status") &&
                                    !menuResponse.getAsJsonObject().get("status").isJsonNull()) {
                                if (menuResponse.getAsJsonObject().get("status").getAsInt() == 1) {
                                    if (menuResponse.getAsJsonObject().has("data") &&
                                            !menuResponse.getAsJsonObject().get("data").isJsonNull()) {
                                        if (menuResponse.getAsJsonObject().get("data").getAsJsonObject().has("menuDetail") &&
                                                !menuResponse.getAsJsonObject().get("data").getAsJsonObject().get("menuDetail").isJsonNull()) {
                                            JsonArray menuData = menuResponse.getAsJsonObject().get("data").getAsJsonObject().get("menuDetail").getAsJsonArray();
                                            Log.d("<>menu-", " menu details --> " + menuData.size());
                                            if (menuData.size() > 0) {
                                                for (int menu = 0; menu < menuData.size(); menu++) {
                                                    List<DishNameDetailModel> dishNameDetailModels = new ArrayList<>();
                                                    String categoryId = "", categoryName = "", categoryStatus = "";
                                                    if (menuData.get(menu).getAsJsonObject().has("CategoryStatus") &&
                                                            !menuData.get(menu).getAsJsonObject().get("CategoryStatus").isJsonNull()) {
                                                        categoryStatus = menuData.get(menu).getAsJsonObject().get("CategoryStatus").getAsString();
                                                        if (categoryStatus.equals("available")) {
                                                            if (menuData.get(menu).getAsJsonObject().has("dishname") &&
                                                                    !menuData.get(menu).getAsJsonObject().get("dishname").isJsonNull()) {
                                                                JsonArray dishData = menuData.get(menu).getAsJsonObject().get("dishname").getAsJsonArray();
                                                                if (dishData.size() > 0) {
                                                                    for (int dish = 0; dish < dishData.size(); dish++) {
                                                                        if (dishData.get(dish).getAsJsonObject().get("status").getAsString().equals("available")) {
                                                                            Log.d("<>menu-", " inside ");
                                                                            String name = "", description = "", cuisine = "", spicelevel = "", isVegetarian = "",
                                                                                    costRegular = "", costLarge = "", costSmall = "", retailRegular = "", retailLarge = "",
                                                                                    retailSmall = "", includeInOffer = "", status = "", caloryRegular = "", endDate = "", currency = "";
                                                                            Boolean isDishData = true;
                                                                            List<String> dietList = new ArrayList<>();
                                                                            List<String> dishSizeList = new ArrayList<>();
                                                                            List<String> allergyList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                                    !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("name") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").isJsonNull()) {
                                                                                    name = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("description") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").isJsonNull()) {
                                                                                    description = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("cuisine") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").isJsonNull()) {
                                                                                    cuisine = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("spicelevel") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").isJsonNull()) {
                                                                                    spicelevel = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").getAsString();
                                                                                }
                                                                            }
                                                                            if (dishData.get(dish).getAsJsonObject().has("vegetarian") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("vegetarian").isJsonNull()) {
                                                                                isVegetarian = dishData.get(dish).getAsJsonObject().get("vegetarian").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("endDate") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("endDate").isJsonNull()) {
                                                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                                                try {
                                                                                    Date dateStart = df.parse(dishData.get(dish).getAsJsonObject().get("endDate").getAsString().replaceAll("Z$", "+0000"));
                                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                                    endDate = sdf.format(dateStart);
                                                                                } catch (ParseException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("status") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("status").isJsonNull()) {
                                                                                status = dishData.get(dish).getAsJsonObject().get("status").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("costprice") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("costprice").isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("Regular") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    costRegular = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("large") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("large").isJsonNull()) {
                                                                                    costLarge = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("large").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("Small") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Small").isJsonNull()) {
                                                                                    costSmall = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Small").getAsString();
                                                                                }
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("currency") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("currency").isJsonNull()) {
                                                                                currency = dishData.get(dish).getAsJsonObject().get("currency").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("retailprice") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("retailprice").isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().has("Regular") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    retailRegular = dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().has("large") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("large").isJsonNull()) {
                                                                                    retailLarge = dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("large").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().has("Small") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("Small").isJsonNull()) {
                                                                                    retailSmall = dishData.get(dish).getAsJsonObject().get("retailprice").getAsJsonObject().get("Small").getAsString();
                                                                                }
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("includeInOffer") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("includeInOffer").isJsonNull()) {
                                                                                includeInOffer = dishData.get(dish).getAsJsonObject().get("includeInOffer").getAsString();
                                                                            }
                                                                            dietList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("Diet") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("Diet").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("Diet").isJsonArray()) {
                                                                                JsonArray dietArray = dishData.get(dish).getAsJsonObject().get("Diet").getAsJsonArray();
                                                                                if (dietArray.size() > 0) {
                                                                                    for (int diet = 0; diet < dietArray.size(); diet++) {
                                                                                        dietList.add(dietArray.get(diet).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            dishSizeList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("dishsizes") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("dishsizes").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("dishsizes").isJsonArray()) {
                                                                                JsonArray dishsizes = dishData.get(dish).getAsJsonObject().get("dishsizes").getAsJsonArray();
                                                                                if (dishsizes.size() > 0) {
                                                                                    for (int size = 0; size < dishsizes.size(); size++) {
                                                                                        dishSizeList.add(dishsizes.get(size).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            allergyList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("Allergy") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("Allergy").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("Allergy").isJsonArray()) {
                                                                                JsonArray allerges = dishData.get(dish).getAsJsonObject().get("Allergy").getAsJsonArray();
                                                                                if (allerges.size() > 0) {
                                                                                    for (int allergy = 0; allergy < allerges.size(); allergy++) {
                                                                                        allergyList.add(allerges.get(allergy).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("calorie") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("calorie").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("calorie").isJsonObject()) {
                                                                                JsonObject calorieList = dishData.get(dish).getAsJsonObject().get("calorie").getAsJsonObject();
                                                                                if (calorieList.getAsJsonObject().has("Regular") &&
                                                                                        !calorieList.getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    caloryRegular = calorieList.getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                            }


                                                                            dishNameDetailModels.add(new DishNameDetailModel(
                                                                                    name,
                                                                                    status,
                                                                                    isVegetarian,
                                                                                    description,
                                                                                    cuisine,
                                                                                    spicelevel,
                                                                                    allergyList,
                                                                                    costRegular,
                                                                                    costSmall,
                                                                                    costLarge,
                                                                                    retailRegular,
                                                                                    retailSmall,
                                                                                    retailLarge,
                                                                                    dishSizeList,
                                                                                    dietList,
                                                                                    includeInOffer,
                                                                                    caloryRegular,
                                                                                    endDate, currency, isDishData
                                                                            ));
                                                                        }
                                                                    }
                                                                }
                                                            } else if (menuData.get(menu).getAsJsonObject().has("drinkname") &&
                                                                    !menuData.get(menu).getAsJsonObject().get("drinkname").isJsonNull()) {
                                                                JsonArray dishData = menuData.get(menu).getAsJsonObject().get("drinkname").getAsJsonArray();
                                                                if (dishData.size() > 0) {
                                                                    for (int dish = 0; dish < dishData.size(); dish++) {
                                                                        if (dishData.get(dish).getAsJsonObject().get("status").getAsString().equals("available")) {
                                                                            Log.d("<>menu-", " inside ");
                                                                            String name = "", description = "", cuisine = "", spicelevel = "", isVegetarian = "",
                                                                                    costRegular = "", costLarge = "", costSmall = "", retailRegular = "", retailLarge = "",
                                                                                    retailSmall = "", includeInOffer = "", status = "", caloryRegular = "", endDate = "", currency = "";
                                                                            Boolean isDishData = false;
                                                                            List<String> dietList = new ArrayList<>();
                                                                            List<String> dishSizeList = new ArrayList<>();
                                                                            List<String> allergyList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                                    !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("name") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").isJsonNull()) {
                                                                                    name = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("description") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").isJsonNull()) {
                                                                                    description = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("cuisine") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").isJsonNull()) {
                                                                                    cuisine = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("spicelevel") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").isJsonNull()) {
                                                                                    spicelevel = dishData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").getAsString();
                                                                                }
                                                                            }
                                                                            if (dishData.get(dish).getAsJsonObject().has("vegetarian") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("vegetarian").isJsonNull()) {
                                                                                isVegetarian = dishData.get(dish).getAsJsonObject().get("vegetarian").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("endDate") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("endDate").isJsonNull()) {
                                                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                                                try {
                                                                                    Date dateStart = df.parse(dishData.get(dish).getAsJsonObject().get("endDate").getAsString().replaceAll("Z$", "+0000"));
                                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                                    endDate = sdf.format(dateStart);
                                                                                } catch (ParseException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("status") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("status").isJsonNull()) {
                                                                                status = dishData.get(dish).getAsJsonObject().get("status").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("costprice") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("costprice").isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("Regular") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    costRegular = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("large") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("large").isJsonNull()) {
                                                                                    costLarge = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("large").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().has("Small") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Small").isJsonNull()) {
                                                                                    costSmall = dishData.get(dish).getAsJsonObject().get("costprice").getAsJsonObject().get("Small").getAsString();
                                                                                }
                                                                            }
                                                                            if (dishData.get(dish).getAsJsonObject().has("currency") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("currency").isJsonNull()) {
                                                                                currency = dishData.get(dish).getAsJsonObject().get("currency").getAsString();
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("sellingPrice") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("sellingPrice").isJsonNull()) {
                                                                                if (dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().has("Regular") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    retailRegular = dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().has("large") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("large").isJsonNull()) {
                                                                                    retailLarge = dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("large").getAsString();
                                                                                }
                                                                                if (dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().has("Small") &&
                                                                                        !dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("Small").isJsonNull()) {
                                                                                    retailSmall = dishData.get(dish).getAsJsonObject().get("sellingPrice").getAsJsonObject().get("Small").getAsString();
                                                                                }
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("includeInOffer") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("includeInOffer").isJsonNull()) {
                                                                                includeInOffer = dishData.get(dish).getAsJsonObject().get("includeInOffer").getAsString();
                                                                            }
                                                                            dietList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("Diet") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("Diet").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("Diet").isJsonArray()) {
                                                                                JsonArray dietArray = dishData.get(dish).getAsJsonObject().get("Diet").getAsJsonArray();
                                                                                if (dietArray.size() > 0) {
                                                                                    for (int diet = 0; diet < dietArray.size(); diet++) {
                                                                                        dietList.add(dietArray.get(diet).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            dishSizeList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("size") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("size").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("size").isJsonArray()) {
                                                                                JsonArray dishsizes = dishData.get(dish).getAsJsonObject().get("size").getAsJsonArray();
                                                                                if (dishsizes.size() > 0) {
                                                                                    for (int size = 0; size < dishsizes.size(); size++) {
                                                                                        dishSizeList.add(dishsizes.get(size).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            allergyList = new ArrayList<>();
                                                                            if (dishData.get(dish).getAsJsonObject().has("volume") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("volume").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("volume").isJsonArray()) {
                                                                                JsonArray allerges = dishData.get(dish).getAsJsonObject().get("volume").getAsJsonArray();
                                                                                if (allerges.size() > 0) {
                                                                                    for (int allergy = 0; allergy < allerges.size(); allergy++) {
                                                                                        allergyList.add(allerges.get(allergy).getAsString());
                                                                                    }
                                                                                }
                                                                            }

                                                                            if (dishData.get(dish).getAsJsonObject().has("calorie") &&
                                                                                    !dishData.get(dish).getAsJsonObject().get("calorie").isJsonNull() &&
                                                                                    dishData.get(dish).getAsJsonObject().get("calorie").isJsonObject()) {
                                                                                JsonObject calorieList = dishData.get(dish).getAsJsonObject().get("calorie").getAsJsonObject();
                                                                                if (calorieList.getAsJsonObject().has("Regular") &&
                                                                                        !calorieList.getAsJsonObject().get("Regular").isJsonNull()) {
                                                                                    caloryRegular = calorieList.getAsJsonObject().get("Regular").getAsString();
                                                                                }
                                                                            }


                                                                            dishNameDetailModels.add(new DishNameDetailModel(
                                                                                    name,
                                                                                    status,
                                                                                    isVegetarian,
                                                                                    description,
                                                                                    cuisine,
                                                                                    spicelevel,
                                                                                    allergyList,
                                                                                    costRegular,
                                                                                    costSmall,
                                                                                    costLarge,
                                                                                    retailRegular,
                                                                                    retailSmall,
                                                                                    retailLarge,
                                                                                    dishSizeList,
                                                                                    dietList,
                                                                                    includeInOffer,
                                                                                    caloryRegular,
                                                                                    endDate,
                                                                                    currency,
                                                                                    isDishData
                                                                            ));
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            if (menuData.get(menu).getAsJsonObject().has("category") &&
                                                                    !menuData.get(menu).getAsJsonObject().get("category").isJsonNull()) {
                                                                categoryId = menuData.get(menu).getAsJsonObject().get("category").getAsJsonObject().get("category_id").getAsString();
                                                                if (menuData.get(menu).getAsJsonObject().get("category").getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                        !menuData.get(menu).getAsJsonObject().get("category").getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                    categoryName = menuData.get(menu).getAsJsonObject().
                                                                            get("category").getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                                }
                                                            }
                                                            menuModels.add(new MenuDetailsModel(
                                                                    categoryId,
                                                                    categoryName,
                                                                    dishNameDetailModels
                                                            ));

                                                        }
                                                    } else if (menuData.get(menu).getAsJsonObject().has("mealStatus") &&
                                                            !menuData.get(menu).getAsJsonObject().get("mealStatus").isJsonNull() &&
                                                            menuData.get(menu).getAsJsonObject().get("mealStatus").getAsString().equals("available")) {
                                                        if (menuData.get(menu).getAsJsonObject().has("meal") &&
                                                                !menuData.get(menu).getAsJsonObject().get("meal").isJsonNull()) {
                                                            JsonArray mealData = menuData.get(menu).getAsJsonObject().get("meal").getAsJsonArray();
                                                            if (mealData.size() > 0) {
                                                                for (int dish = 0; dish < mealData.size(); dish++) {
                                                                    if (mealData.get(dish).getAsJsonObject().get("mealStatus").getAsString().equals("true")) {
                                                                        Log.d("<>menu-", " inside ");
                                                                        String name = "", description = "", cuisine = "", spicelevel = "", isVegetarian = "",
                                                                                costRegular = "", costLarge = "", costSmall = "", retailRegular = "", retailLarge = "",
                                                                                retailSmall = "", includeInOffer = "", status = "", caloryRegular = "", endDate = "", currency = "";
                                                                        Boolean isDishData = false;
                                                                        List<String> dietList = new ArrayList<>();
                                                                        List<String> dishSizeList = new ArrayList<>();
                                                                        List<String> allergyList = new ArrayList<>();
                                                                        if (mealData.get(dish).getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                                !mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                            if (mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("name") &&
                                                                                    !mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").isJsonNull()) {
                                                                                name = mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                                            }
                                                                            if (mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("description") &&
                                                                                    !mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").isJsonNull()) {
                                                                                description = mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").getAsString();
                                                                            }
                                                                            if (mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("cuisine") &&
                                                                                    !mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").isJsonNull()) {
                                                                                cuisine = mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").getAsString();
                                                                            }
                                                                            if (mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("spicelevel") &&
                                                                                    !mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").isJsonNull()) {
                                                                                spicelevel = mealData.get(dish).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").getAsString();
                                                                            }
                                                                        }
                                                                        if (mealData.get(dish).getAsJsonObject().has("vegetarian") &&
                                                                                !mealData.get(dish).getAsJsonObject().get("vegetarian").isJsonNull()) {
                                                                            isVegetarian = mealData.get(dish).getAsJsonObject().get("vegetarian").getAsString();
                                                                        }

                                                                        if (mealData.get(dish).getAsJsonObject().has("endDate") &&
                                                                                !mealData.get(dish).getAsJsonObject().get("endDate").isJsonNull()) {
                                                                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                                            try {
                                                                                Date dateStart = df.parse(mealData.get(dish).getAsJsonObject().get("endDate").getAsString().replaceAll("Z$", "+0000"));
                                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                                endDate = sdf.format(dateStart);
                                                                            } catch (ParseException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                        }

                                                                        if (mealData.get(dish).getAsJsonObject().has("mealStatus") &&
                                                                                !mealData.get(dish).getAsJsonObject().get("mealStatus").isJsonNull()) {
                                                                            status = mealData.get(dish).getAsJsonObject().get("mealStatus").getAsString();
                                                                        }

                                                                        if (mealData.get(dish).getAsJsonObject().has("currency") &&
                                                                                !mealData.get(dish).getAsJsonObject().get("currency").isJsonNull()) {
                                                                            currency = mealData.get(dish).getAsJsonObject().get("currency").getAsString();
                                                                        }

                                                                        if (mealData.get(dish).getAsJsonObject().has("price") &&
                                                                                !mealData.get(dish).getAsJsonObject().get("price").isJsonNull()) {
                                                                            retailRegular = mealData.get(dish).getAsJsonObject().get("price").getAsString();
                                                                        }

                                                                        dietList = new ArrayList<>();
                                                                        dishNameDetailModels.add(new DishNameDetailModel(
                                                                                name,
                                                                                status,
                                                                                isVegetarian,
                                                                                description,
                                                                                cuisine,
                                                                                spicelevel,
                                                                                allergyList,
                                                                                costRegular,
                                                                                costSmall,
                                                                                costLarge,
                                                                                retailRegular,
                                                                                retailSmall,
                                                                                retailLarge,
                                                                                dishSizeList,
                                                                                dietList,
                                                                                includeInOffer,
                                                                                caloryRegular,
                                                                                endDate,
                                                                                currency,
                                                                                isDishData
                                                                        ));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (menuData.get(menu).getAsJsonObject().has("meal") &&
                                                                !menuData.get(menu).getAsJsonObject().get("meal").isJsonNull()) {
                                                            categoryId = menuData.get(menu).getAsJsonObject().get("meal").getAsJsonArray().get(0).getAsJsonObject().get("meal_id").getAsString();
                                                            if (menuData.get(menu).getAsJsonObject().get("meal").getAsJsonArray().get(0).getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                    !menuData.get(menu).getAsJsonObject().get("meal").getAsJsonArray().get(0).getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                categoryName = menuData.get(menu).getAsJsonObject().
                                                                        get("meal").getAsJsonArray().get(0).getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                            }
                                                        }
                                                        menuModels.add(new MenuDetailsModel(
                                                                categoryId,
                                                                categoryName,
                                                                dishNameDetailModels
                                                        ));
                                                    }
                                                }
                                                MyApplication.getGlobalData().addMenuDetailsDataList(menuModels);
                                                init();
                                            }
                                        }
                                    }
                                } else {
                                    txtListStatus.setVisibility(View.VISIBLE);
//                                    Toast.makeText(MenuManagementCustomerApp.this, "No any dishes found!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (loadingSpinner != null)
                            dismissSpinner();
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
                Toast.makeText(MenuManagementCustomerApp.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(MenuManagementCustomerApp.this, R.style.AppCompatAlertDialogStyle);
        }

        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();

    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }

    private final TabLayout.OnTabSelectedListener
            mTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // no-op
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // no-op
        }
    };

    private static class MenuPagerAdapter extends PagerAdapter {
        private final ArrayList<CharSequence> mCheeses = new ArrayList<>();
        private List<DishNameDetailModel> dishNameDetails;
        private Context mContext;

        public MenuPagerAdapter(Context context) {
            mContext = context;
        }

        public void addTab(String title, List<DishNameDetailModel> dishNameDetails) {
            mCheeses.add(title);
            this.dishNameDetails = dishNameDetails;
            notifyDataSetChanged();

        }

        public void removeTab() {
            if (!mCheeses.isEmpty()) {
                mCheeses.remove(mCheeses.size() - 1);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mCheeses.size();
        }

        private class ViewHolder {
            ListView listMenu;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewHolder holder = null;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.layout_menu_listview, container, false);
            if (holder == null) {
                Log.d("<>list-", " come in new holder");
                holder = new ViewHolder();
                holder.listMenu = view.findViewById(R.id.listviewMenu);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) container.getTag();
            }
            List<MenuDetailsModel> model = MyApplication.getGlobalData().getMenuDetailsDataList();
            if (model.size() > 0) {
                for (int i = 0; i < model.size(); i++) {
                    if (mCheeses.get(position).toString().equals(model.get(i).getCategoryName())) {
                        dishNameDetails = model.get(i).getDishnameDetails();
                        MenuItemAdapter adapter = new MenuItemAdapter(mContext, dishNameDetails);
                        holder.listMenu.setAdapter(adapter);
                    }
                }
            }

            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCheeses.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.DishNameDetailModel;
import com.clickeat.customer.click_eatcustomer.DataModel.MenuDetailsModel;
import com.clickeat.customer.click_eatcustomer.DataModel.OMealDeals;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
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

/**
 * Created by pivotech on 22/11/17.
 */

public class FragmentMenuRestaurant extends Fragment {
    private View m_myFragmentView;
    private ProgressDialog loadingSpinner;
    private String restaurantId;
    private TextView textStatusMenu;
    private static final String ARG_PARAM = "param1";
    private ExapandableListMenuAdapter menuAdapter;
    ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    public static FragmentMenuRestaurant newInstance(String param1) {
        FragmentMenuRestaurant fragment = new FragmentMenuRestaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1 + "");
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMenuRestaurant() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_myFragmentView = inflater.inflate(R.layout.layout_menu_restaurant_fragment, container, false);

        findIds();
        getAllMenus(restaurantId);
        return m_myFragmentView;
    }

    private void findIds() {
        expListView = m_myFragmentView.findViewById(R.id.lvExpMenu);
        textStatusMenu = m_myFragmentView.findViewById(R.id.textStatusMenu);
    }

    private void inits() {
        ArrayList<MenuDetailsModel> menuDataAll = new ArrayList<>();
        menuDataAll = MyApplication.getGlobalData().getMenuDetailsDataList();
        menuAdapter = new ExapandableListMenuAdapter(getActivity(), menuDataAll);
        // setting list adapter
        expListView.setAdapter(menuAdapter);
        setListViewHeight(expListView);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Log.d("<>exp-", " in group expand");
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.d("<>exp-", " in grop click");
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void getAllMenus(final String restaurantId) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
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
                        List<OMealDeals> mealDealsList = new ArrayList<>();
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
                                                    } else if (menuData.get(menu).getAsJsonObject().has("menuMealStatus") &&
                                                            !menuData.get(menu).getAsJsonObject().get("menuMealStatus").isJsonNull() &&
                                                            menuData.get(menu).getAsJsonObject().get("menuMealStatus").getAsString().equals("available")) {
                                                        String name = "", description = "", cuisine = "", spicelevel = "", isVegetarian = "",
                                                                costRegular = "", costLarge = "", costSmall = "", retailRegular = "", retailLarge = "",
                                                                retailSmall = "", includeInOffer = "", status = "", caloryRegular = "", endDate = "", currency = "";
                                                        String mealImage = "";
                                                        Boolean isDishData = false;
                                                        if (menuData.get(menu).getAsJsonObject().has("meal") &&
                                                                !menuData.get(menu).getAsJsonObject().get("meal").isJsonNull()) {
                                                            JsonObject mealData = menuData.get(menu).getAsJsonObject().get("meal").getAsJsonObject();
                                                            if (mealData != null) {
                                                                if (mealData.get("mealStatus").getAsString().equals("true")) {
                                                                    Log.d("<>menu-", " inside ");
                                                                    List<String> dietList = new ArrayList<>();
                                                                    List<String> dishSizeList = new ArrayList<>();
                                                                    List<String> allergyList = new ArrayList<>();
                                                                    if (mealData.has(MyApplication.sDefSystemLanguage) &&
                                                                            !mealData.get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                        if (mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("name") &&
                                                                                !mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").isJsonNull()) {
                                                                            name = mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                                        }
                                                                        if (mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("description") &&
                                                                                !mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").isJsonNull()) {
                                                                            description = mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("description").getAsString();
                                                                        }
                                                                        if (mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("cuisine") &&
                                                                                !mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").isJsonNull()) {
                                                                            cuisine = mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("cuisine").getAsString();
                                                                        }
                                                                        if (mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().has("spicelevel") &&
                                                                                !mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").isJsonNull()) {
                                                                            spicelevel = mealData.get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("spicelevel").getAsString();
                                                                        }
                                                                    }
                                                                    if (mealData.has("vegetarian") &&
                                                                            !mealData.get("vegetarian").isJsonNull()) {
                                                                        isVegetarian = mealData.get("vegetarian").getAsString();
                                                                    }

                                                                    if (mealData.has("originalImages") &&
                                                                            !mealData.get("originalImages").isJsonNull()) {
                                                                        mealImage = mealData.get("originalImages").getAsString();
                                                                    }

                                                                    if (mealData.has("endDate") &&
                                                                            !mealData.get("endDate").isJsonNull()) {
                                                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                                                        try {
                                                                            Date dateStart = df.parse(mealData.get("endDate").getAsString().replaceAll("Z$", "+0000"));
                                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                            endDate = sdf.format(dateStart);
                                                                        } catch (ParseException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }

                                                                    if (mealData.has("mealStatus") &&
                                                                            !mealData.get("mealStatus").isJsonNull()) {
                                                                        status = mealData.get("mealStatus").getAsString();
                                                                    }

                                                                    if (mealData.has("currency") &&
                                                                            !mealData.get("currency").isJsonNull()) {
                                                                        currency = mealData.get("currency").getAsString();
                                                                    }

                                                                    if (mealData.has("price") &&
                                                                            !mealData.get("price").isJsonNull()) {
                                                                        retailRegular = mealData.get("price").getAsString();
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
                                                        if (menuData.get(menu).getAsJsonObject().has("meal") &&
                                                                !menuData.get(menu).getAsJsonObject().get("meal").isJsonNull()) {
                                                            categoryId = menuData.get(menu).getAsJsonObject().get("meal").getAsJsonObject().get("meal_id").getAsString();
                                                            if (menuData.get(menu).getAsJsonObject().get("meal").getAsJsonObject().has(MyApplication.sDefSystemLanguage) &&
                                                                    !menuData.get(menu).getAsJsonObject().get("meal").getAsJsonObject().get(MyApplication.sDefSystemLanguage).isJsonNull()) {
                                                                categoryName = menuData.get(menu).getAsJsonObject().
                                                                        get("meal").getAsJsonObject().get(MyApplication.sDefSystemLanguage).getAsJsonObject().get("name").getAsString();
                                                            }
                                                        }
                                                        mealDealsList.add(new OMealDeals(categoryId,
                                                                categoryName,
                                                                description,
                                                                retailRegular,
                                                                currency,
                                                                mealImage));
                                                        menuModels.add(new MenuDetailsModel(
                                                                categoryId,
                                                                categoryName,
                                                                dishNameDetailModels
                                                        ));
                                                    }
                                                }
                                                MyApplication.getGlobalData().addMenuDetailsDataList(menuModels);
                                                MyApplication.getGlobalData().addMealDeals(mealDealsList);
                                                inits();
                                            }
                                        }
                                    }
                                } else {
                                    textStatusMenu.setVisibility(View.VISIBLE);
                                    expListView.setVisibility(View.GONE);
//                                    Toast.makeText(MenuListActivity.this, "No any dishes found!!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
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
}

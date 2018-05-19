package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.MenuMangmentExpandedMenuModel;
import com.clickeat.restaurant.click_eatrestaurant.adapters.MenuManagementExpandableListAdapter;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;

public class MenuManagmentExpandable extends AppCompatActivity {

    MenuManagementExpandableListAdapter mMenuAdapter;
    ExpandableListView expandTandC;
    List<MenuMangmentExpandedMenuModel> listDataHeader;
    HashMap<MenuMangmentExpandedMenuModel, List<String>> listDataChild;
    SharedPreferences CONST_SHAREDPREFERENCES;
    public static String data_id;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_managment_expandable);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Menu Managment");

        new GetAllMenu().execute();

        expandTandC = (ExpandableListView)this.findViewById(R.id.expandTandC);

        expandTandC.setIndicatorBounds(expandTandC.getWidth()-40, expandTandC.getWidth());

        expandTandC.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                return false;
            }
        });
        expandTandC.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");

                return false;
            }
        });

        expandTandC.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup){
                    expandTandC.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            }
        });

        expandTandC.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(groupPosition==0 && childPosition==3){
                    //   startActivity(new Intent(NavigationMainScreen.this, UnavailableTableActivity.class));
                }

                return false;
            }
        });


    }

    JsonObject rootObjGetallMenu;
    static String resGetAllMenu;
    ProgressDialog progressDialog;
    public class GetAllMenu extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(MenuManagmentExpandable.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject getMenuall = new JsonObject();

                getMenuall.addProperty("restaurant_id", CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));

                JsonObject ReqObj = new JsonObject();
                ReqObj.add("search",getMenuall);

                String responseAwaiting = ResponseAPIWithHeader(MenuManagmentExpandable.this,
                        Const.SERVER_URL_API +"menu-managements/search/menu"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , ReqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGetAllMenu=responseAwaiting;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGetAllMenu;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            progressDialog.dismiss();

            try{
                Log.i("RES All Menus---", ""+resGetAllMenu);
                JsonParser parser = new JsonParser();
                rootObjGetallMenu = parser.parse(resGetAllMenu).getAsJsonObject();
                prepareListData();
                // setting list adapter
                mMenuAdapter = new MenuManagementExpandableListAdapter(MenuManagmentExpandable.this, listDataHeader, listDataChild, expandTandC);

                expandTandC.setAdapter(mMenuAdapter);



            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                progressDialog.dismiss();
            }

        }
    }

    private void prepareListData() {

        listDataHeader = new ArrayList<MenuMangmentExpandedMenuModel>();
        listDataChild = new HashMap<MenuMangmentExpandedMenuModel, List<String>>();


        JsonObject GetallMenuJSON = rootObjGetallMenu;
        JsonArray menudetailsArray = GetallMenuJSON.get("data").getAsJsonObject()
                .get("menuDetail").getAsJsonArray();

        data_id = GetallMenuJSON.get("data").getAsJsonObject().get("_id").getAsString();
        for(int a= 0;a<menudetailsArray.size();a++) {


            List<String> heading1 = new ArrayList<String>();

            if (menudetailsArray.get(a).getAsJsonObject().has("meal")) {

                JsonObject mealArrary = menudetailsArray.get(a).getAsJsonObject().get("meal").getAsJsonObject();

                String headerTitleMeal = mealArrary.get("en").getAsJsonObject()
                        .get("name").getAsString();
                String headerID = mealArrary.get("meal_id").getAsString();

                MenuMangmentExpandedMenuModel item1 = new MenuMangmentExpandedMenuModel();
                item1.setIconName(headerTitleMeal);
                item1.setIconImg(R.drawable.ic_arrow_right);
                item1.setHeaderId(headerID);
                item1.setChildId(null);
                item1.setHeaderJson(menudetailsArray.get(a).getAsJsonObject());
                // Adding data header
                listDataHeader.add(item1);



            } else if (menudetailsArray.get(a).getAsJsonObject().has("drinkname")) {

                JsonObject category = menudetailsArray.get(a).getAsJsonObject().get("category").getAsJsonObject();
                String headerTitleDrink = category.get("en").getAsJsonObject()
                        .get("name").getAsString();
                String headerId = category.get("category_id").getAsString();

                MenuMangmentExpandedMenuModel item1 = new MenuMangmentExpandedMenuModel();
                item1.setIconName(headerTitleDrink);
                item1.setIconImg(R.drawable.ic_arrow_right);
                item1.setHeaderId(headerId);
                item1.setChildId(null);
                item1.setHeaderJson(menudetailsArray.get(a).getAsJsonObject());
                // Adding data header
                listDataHeader.add(item1);



                /*for (int aa = 0; aa < drinkArrary.size(); aa++) {

                   *//* String headerTitleDrink = drinkArrary.get(aa).getAsJsonObject()
                            .get("category").getAsJsonArray().get(0).getAsJsonObject()
                            .get("en").getAsJsonObject()
                            .get("name").getAsString();
                    String headerId = drinkArrary.get(aa).getAsJsonObject()
                            .get("category").getAsJsonArray().get(0).getAsJsonObject()
                            .get("category_id").getAsString();*//*


                    break;
                }*/

                JsonArray drinkArrary = menudetailsArray.get(a).getAsJsonObject().get("drinkname").getAsJsonArray();


                for (int bb = 0; bb < drinkArrary.size(); bb++) {
                    String name = drinkArrary.get(bb).getAsJsonObject().get("en")
                            .getAsJsonObject().get("name").getAsString();
                    String childID = drinkArrary.get(bb).getAsJsonObject().get("_id").getAsString();
                    heading1.add(name);
                    listDataHeader.get(a).setChiledJson(drinkArrary.get(bb).getAsJsonObject());
                }


            } else if (menudetailsArray.get(a).getAsJsonObject().has("dishname")) {


                JsonObject category = menudetailsArray.get(a).getAsJsonObject().get("category").getAsJsonObject();
                String headerTitleDrink = category.get("en").getAsJsonObject()
                        .get("name").getAsString();
                String headerId = category.get("category_id").getAsString();

                MenuMangmentExpandedMenuModel item1 = new MenuMangmentExpandedMenuModel();
                item1.setIconName(headerTitleDrink);
                item1.setIconImg(R.drawable.ic_arrow_right);
                item1.setHeaderId(headerId);
                item1.setChildId(null);
                item1.setHeaderJson(menudetailsArray.get(a).getAsJsonObject());
                // Adding data header
                listDataHeader.add(item1);



                /*for (int aa = 0; aa < drinkArrary.size(); aa++) {

                    String headerTitleDrink = drinkArrary.get(aa).getAsJsonObject()
                            .get("category").getAsJsonArray().get(0).getAsJsonObject()
                            .get("en").getAsJsonObject()
                            .get("name").getAsString();

                    MenuMangmentExpandedMenuModel item1 = new MenuMangmentExpandedMenuModel();
                    item1.setIconName(headerTitleDrink);
                    item1.setIconImg(R.drawable.ic_arrow_right);
                    // Adding data header
                    listDataHeader.add(item1);
                    break;
                }*/
                JsonArray drinkArrary = menudetailsArray.get(a).getAsJsonObject().get("dishname").getAsJsonArray();

                for (int bb = 0; bb < drinkArrary.size(); bb++) {
                    String name = drinkArrary.get(bb).getAsJsonObject().get("en")
                            .getAsJsonObject().get("name").getAsString();
                    heading1.add(name);
                    listDataHeader.get(a).setChiledJson(drinkArrary.get(bb).getAsJsonObject());
                }
                // heading1.add("Add new terms .");

            }

            listDataChild.put(listDataHeader.get(a), heading1);

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

}

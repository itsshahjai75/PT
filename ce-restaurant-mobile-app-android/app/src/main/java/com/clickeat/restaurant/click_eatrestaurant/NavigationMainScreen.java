package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.NavRightExpandedMenuModel;
import com.clickeat.restaurant.click_eatrestaurant.adapters.NavRightExpandableListAdapter;
import com.clickeat.restaurant.click_eatrestaurant.customviews.Custom.CustomTypefaceSpan;
import com.clickeat.restaurant.click_eatrestaurant.fragments.FaqFragment;
import com.clickeat.restaurant.click_eatrestaurant.fragments.NavigationBottomMain;
import com.clickeat.restaurant.click_eatrestaurant.utilities.ConnectionDetector;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pushwoosh.Pushwoosh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPI;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_LOGINKEY;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_PASSWORD;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_EMAIL;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_FULL_NAME;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ROLE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.SERVER_URL_API;

public class NavigationMainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static Toolbar toolbar;
    public static TextView toolbar_title;
    FragmentTransaction transaction;

    TextView tvName,tvEmail,tvNameRight;
    ImageView ivlogoapp,ivlogoapp_right;
    DrawerLayout drawer;
    private Boolean drawerFlag = false;

    NavRightExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<NavRightExpandedMenuModel> listDataHeader;
    HashMap<NavRightExpandedMenuModel, List<String>> listDataChild;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main_screen);

        ButterKnife.bind(this);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        //new GetRestaurantDetails().execute();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_hamburger);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        NavigationView drawerright = (NavigationView) findViewById(R.id.nav_viewRight);
        Menu mRight = drawerright.getMenu();
        for (int i=0;i<mRight.size();i++) {
            MenuItem mi = mRight.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }


        View header=navigationView.getHeaderView(0);

        tvName = (TextView)header.findViewById(R.id.tvName);
        tvEmail = (TextView)header.findViewById(R.id.tvEmail);
        ivlogoapp = (ImageView)header.findViewById(R.id.ivlogoapp);
        ivlogoapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar_title.setText(getString(R.string.static_opsmanager));

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, new NavigationBottomMain());
                transaction.commit();

                drawer.closeDrawer(GravityCompat.START);

            }
        });


        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        tvNameRight = (TextView)findViewById(R.id.tvNameRight);
        tvNameRight.setVisibility(View.GONE);
        ivlogoapp_right = (ImageView)findViewById(R.id.ivlogoapp_right);

        ivlogoapp_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                toolbar_title.setText(getString(R.string.static_opsmanager));

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, new NavigationBottomMain());
                transaction.commit();

                drawer.closeDrawer(GravityCompat.START);

            }
        });

        tvName.setText(getString(R.string.welcome)+" "+CONST_SHAREDPREFERENCES.getString(PREF_USER_FULL_NAME,""));
        tvEmail.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_EMAIL,""));

        navigationView.setNavigationItemSelectedListener(this);

        /*transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, new NavigationBottomMain());
        transaction.commit();*/


        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);

        if (drawerright != null) {
            setupDrawerContent(drawerright);
        }

        prepareListData();
        mMenuAdapter = new NavRightExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
        expandableList.setIndicatorBounds(expandableList.getWidth()-40, expandableList.getWidth());

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                if(i==3){
                Intent menu = new Intent(NavigationMainScreen.this, MenuManagmentExpandable.class);
                menu.putExtra("restaurantId", CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));
                menu.putExtra("name", CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_FULL_NAME,""));
                startActivity(menu);

                }
                if(i==4){
                    startActivity(new Intent(NavigationMainScreen.this, Profile.class));
                }
                return false;
            }
        });

        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup){
                    expandableList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
                }
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(groupPosition==0 && childPosition==3){
                    startActivity(new Intent(NavigationMainScreen.this, UnavailableTableActivity.class));
                }

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }else if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }else {
                    drawer.openDrawer(GravityCompat.END);
                }
                return false;
            }
        });


        String token = CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,null);
        String url = SERVER_URL_API +"users/me";
        getAsyncCall(url,token);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<NavRightExpandedMenuModel>();
        listDataChild = new HashMap<NavRightExpandedMenuModel, List<String>>();

        NavRightExpandedMenuModel item1 = new NavRightExpandedMenuModel();
        item1.setIconName("Book a Table");
        item1.setIconImg(R.drawable.ic_booking);
        // Adding data header
        listDataHeader.add(item1);

        NavRightExpandedMenuModel item2 = new NavRightExpandedMenuModel();
        item2.setIconName("Takeaway");
        item2.setIconImg(R.drawable.ic_takeaway);
        listDataHeader.add(item2);

        NavRightExpandedMenuModel item3 = new NavRightExpandedMenuModel();
        item3.setIconName("WalkIn");
        item3.setIconImg(R.drawable.ic_walkin);
        listDataHeader.add(item3);

        NavRightExpandedMenuModel item4 = new NavRightExpandedMenuModel();
        item4.setIconName("Manage Menu");
        item4.setIconImg(R.drawable.ic_nav_right_menu);
        listDataHeader.add(item4);

        NavRightExpandedMenuModel item5 = new NavRightExpandedMenuModel();
        item5.setIconName("Profile");
        item5.setIconImg(R.drawable.ic_profile);
        listDataHeader.add(item5);


        NavRightExpandedMenuModel item6 = new NavRightExpandedMenuModel();
        item6.setIconName("Support");
        item6.setIconImg(R.drawable.ic_nav_right_support);
        listDataHeader.add(item6);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("- View Tables");
        heading1.add("- New Booking");
        heading1.add("- Manage Bookings");
        heading1.add("- Unavailable Table");

        List<String> heading2 = new ArrayList<String>();
        heading2.add("- Manage Takeaways");
        heading2.add("- Track a Takeaway");
        heading2.add("- New Takeaway");
        heading2.add("- Manage Takeaway Drivers");

        List<String> heading3 = new ArrayList<String>();
        heading3.add("- New Walk In");
        heading3.add("- Manage Walk Ins");

        List<String> heading4 = new ArrayList<String>();
        List<String> heading5 = new ArrayList<String>();

        List<String> heading6 = new ArrayList<String>();
        heading6.add("- My Tickets");


        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);
        listDataChild.put(listDataHeader.get(2), heading3);// Header, Child data
        listDataChild.put(listDataHeader.get(3), heading4);// Header, Child data
        listDataChild.put(listDataHeader.get(4), heading5);// Header, Child data
        listDataChild.put(listDataHeader.get(5), heading6);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (drawer.isDrawerOpen(GravityCompat.END)) {
                            drawer.closeDrawer(GravityCompat.END);
                        }else if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }else {
                            drawer.openDrawer(GravityCompat.END);
                        }
                        return true;
                    }
                });
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    TextView tv_cartItemCount;
    ImageView ic_cart_actionbar;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main_screen, menu);

        MenuItem item = menu.findItem(R.id.ic_action_notification);
        MenuItemCompat.setActionView(item, R.layout.notification_badge_layout);
        RelativeLayout notifCount = (RelativeLayout)   MenuItemCompat.getActionView(item);

        tv_cartItemCount = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        ic_cart_actionbar = (ImageView) notifCount.findViewById(R.id.ic_cart_actionbar);
        tv_cartItemCount.setText("99");
        ic_cart_actionbar .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(NavigationMainScreen.this,CartSummaryScreen.class));

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id==R.id.ic_action_user){
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            }else if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else {
                drawer.openDrawer(GravityCompat.END);
            }
        }

        if (id==android.R.id.home){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            }else {
                drawer.openDrawer(GravityCompat.START);
            }
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            // Handle the camera action
            toolbar_title.setText(getString(R.string.static_opsmanager));
            //selectedFragment = new NavigationBottomMain();
            startActivity(new Intent(NavigationMainScreen.this,NavigationMainScreen.class));
            finish();
        } else if (id == R.id.nav_faq) {
            //invalidateOptionsMenu();

            toolbar_title.setText(getString(R.string.menu_title_faq));
            selectedFragment = new FaqFragment();
        } else if (id == R.id.nav_support) {
            //invalidateOptionsMenu();
        } else if (id == R.id.nav_about) {
            //invalidateOptionsMenu();
        } else if (id == R.id.nav_logout) {
           // invalidateOptionsMenu();
            /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().apply();*/

            CONST_SHAREDPREFERENCES.edit().clear().apply();
            CONST_SHAREDPREFERENCES.edit().clear().commit();
            CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"no").apply();
            new LogoutAPI().execute();

            Intent i = new Intent(NavigationMainScreen.this, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();


        }

        if (selectedFragment != null) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, selectedFragment);
            transaction.commit();
        } else {
            item.setChecked(false);
        }


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);

        return true;
    }


    String resLogout;
    private class LogoutAPI extends AsyncTask<Object, Void, String> {


        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(Login.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject ReqestLoginJson = new JsonObject();
                ReqestLoginJson.addProperty("token",Pushwoosh.getInstance().getPushToken());

                String responseUSerTitles = ResponseAPI(NavigationMainScreen.this,Const.SERVER_URL_API +"push-tokens/logout", ReqestLoginJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resLogout=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resLogout;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES Logout---", resLogout);
                /*JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();*/

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    String TAG = "http enquecall--getreso details====";
    public void getAsyncCall(String url, String headerStr){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+headerStr)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    NavigationMainScreen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("user me enque call---",myResponse);
                            new GetRestaurantDetails().execute(myResponse);
                        }
                    });
                }
            }
        });
    }

    String token;
    String resUserProfileDetails;
    public class GetRestaurantDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            token = CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,"");
        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


               /* String responseUSerTitles = ResponseAPIWithHeader(getBaseContext(),
                        Const.SERVER_URL_API +"users/me"
                        ,CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,""),
                        "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserProfileDetails=responseUSerTitles;*/
               resUserProfileDetails= parametros[0].toString();



            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserProfileDetails;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("user/me API call RESponse---", ""+resUserProfileDetails);
                try {
                    JsonParser parser = new JsonParser();
                    JsonObject rootObj = parser.parse(resUserProfileDetails).getAsJsonObject();

                    Const.getRestaurantUserDetails = rootObj;

                    String _id = rootObj.get("_id").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ID, rootObj.get("_id").getAsString()).apply();

                    //String provider= rootObj.get("provider").getAsString();

                    String name = rootObj.get("name").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_FULL_NAME, rootObj.get("name").getAsString()).apply();


                    String email = rootObj.get("email").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_EMAIL, rootObj.get("email").getAsString()).apply();

                    String mobile_number = rootObj.get("mobile_number").getAsString();
                    CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_MOBILE_NO, rootObj.get("mobile_number").getAsString()).apply();


                    //  String postcode= rootObj.get("postcode").getAsString();
                    // String tnc= rootObj.get("tnc").getAsString();

                    String subrole = rootObj.get("subrole").getAsString();
                    //   String restaurantStatus= rootObj.get("restaurantStatus").getAsString();

                    String __v = rootObj.get("__v").getAsString();
                    //  String passwordResetCode= rootObj.get("passwordResetCode").getAsString();

                    String role = rootObj.get("role").getAsString();

                    CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_ROLE, subrole).apply();

                    if (role.equalsIgnoreCase("restaurant")) {

                        if (subrole.equalsIgnoreCase("superadmin")) {

                            CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN, token).apply();
                            CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY, "yes").apply();
                        /*Intent i = new Intent(NavigationMainScreen.this, NavigationMainScreen.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                        finish();*/

                        } else if (subrole.equalsIgnoreCase("ops_manager") || subrole.equalsIgnoreCase("Ops Manager")) {

                            CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN, token).apply();
                            CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY, "yes").apply();
                        /*Intent i = new Intent(NavigationMainScreen.this, NavigationMainScreen.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                        finish();*/


                        } else if (subrole.equalsIgnoreCase("kitchen")) {
                            //startActivity(new Intent(Login.this,Login.class));
                            Toast.makeText(NavigationMainScreen.this, "Sorry ! you are not authorised restaurant member !", Toast.LENGTH_LONG).show();

                        } else if (subrole.equalsIgnoreCase("driver")) {
                            //startActivity(new Intent(Login.this,Login.class));
                            Toast.makeText(NavigationMainScreen.this, "Sorry ! you are not authorised restaurant member !", Toast.LENGTH_LONG).show();

                        } else {
                            //startActivity(new Intent(Login.this,Login.class));
                            Toast.makeText(NavigationMainScreen.this, "Sorry ! you are not authorised restaurant member !", Toast.LENGTH_LONG).show();

                        }


                    } else {
                        //startActivity(new Intent(Login.this,Login.class));
                        Toast.makeText(NavigationMainScreen.this, "Sorry ! you are not restaurant member !", Toast.LENGTH_LONG).show();
                    }

                    tvName.setText(getString(R.string.welcome) + " " + CONST_SHAREDPREFERENCES.getString(PREF_USER_FULL_NAME, ""));
                    tvEmail.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_EMAIL, ""));

                    // new RegPushToken().execute();

                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, new NavigationBottomMain());
                    transaction.commit();

                }catch (Exception exp){
                    exp.printStackTrace();
                }


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    String respushwooshAPI;
    private class RegPushToken extends AsyncTask<Object, Void, String> {


        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(Login.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject ReqestLoginJson = new JsonObject();
                ReqestLoginJson.addProperty("token",Pushwoosh.getInstance().getPushToken());
                ReqestLoginJson.addProperty("userId",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                ReqestLoginJson.addProperty("userRole",CONST_SHAREDPREFERENCES.getString(PREF_USER_ROLE,""));
                ReqestLoginJson.addProperty("isLoggedIn",true);
                ReqestLoginJson.addProperty("lastSession", String.valueOf(Calendar.getInstance().getTime()));
                ReqestLoginJson.addProperty("deviceType",Const.DEVICE_TYPE);
                //Log.d("data ---",""+ReqestLoginJson.toString());

                String responseUSerTitles = ResponseAPI(NavigationMainScreen.this,Const.SERVER_URL_API +"push-tokens", ReqestLoginJson.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                respushwooshAPI=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return respushwooshAPI;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES respushwooshAPI---", respushwooshAPI);
                /*JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserDetails).getAsJsonObject();*/

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}

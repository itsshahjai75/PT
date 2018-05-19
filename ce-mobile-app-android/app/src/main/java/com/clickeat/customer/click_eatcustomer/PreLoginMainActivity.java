package com.clickeat.customer.click_eatcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.MainHome.Home;
import com.clickeat.customer.click_eatcustomer.MyAccounts.MyAccountFragment;
import com.clickeat.customer.click_eatcustomer.MyBookings.MyBookingsMainFragment;
import com.clickeat.customer.click_eatcustomer.MyFavourites.Favourites;
import com.clickeat.customer.click_eatcustomer.Search.CustomTypefaceSpan;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PreLoginMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    RelativeLayout drawerView, drawerView_left;
    RelativeLayout mainView;
    DrawerLayout drawer;
    private TextView textViewTitle, txtUserLabel;
    NavigationView navigationView, navigationView_right;
    ImageButton imgLogin, menuRight, imgSignup;
    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private Boolean isLogin = false, drawerFlag = false;
    String userName = "";
    Boolean result = false;
    private Intent intent;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BOOKINGS = "my_bookings";
    private static final String TAG_FAV = "my_favourites";
    private static final String TAG_FEEDBACK = "feedback";
    private static final String TAG_LOGOUT = "logout";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findIds();
        intent = getIntent();
        if (intent != null) {
            Log.d("<>arc-", " is logged in ==> " + SharedData.getIsLoggedIn(PreLoginMainActivity.this) + "");
            if (intent.hasExtra("signIn")) {
                if (intent.getStringExtra("signIn").equals("true")) {
                    isLogin = SharedData.getIsLoggedIn(PreLoginMainActivity.this);
                    userName = intent.getStringExtra("name");
                    init();
                }
            } else if (SharedData.getIsLoggedIn(PreLoginMainActivity.this)) {
                getLoginData();
            }
        }

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        Menu menuRight_m = navigationView_right.getMenu();
        for (int i = 0; i < menuRight_m.size(); i++) {
            MenuItem mi_menuRight = menuRight_m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi_menuRight.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi_menuRight);
        }

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                    Log.d("<>drawer-", " in false");
                    drawerFlag = false;
                } else {
                    drawer.openDrawer(GravityCompat.END);
                    Log.d("<>drawer-", " in true");
                    drawerFlag = true;
                }
            }
        });


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();

        setupDrawerContent(navigationView_right);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawerFlag = false;
//                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                /* below code is used to move activity with navigation menu */
                if (drawerFlag == true) {
                    Log.d("<>drawer-", " flag is true");
                    mainView.setX(-drawerView.getWidth() * slideOffset);
                } else {
                    Log.d("<>drawer-", " flag is tfalse");
                    mainView.setTranslationX(slideOffset * drawerView.getWidth());
                }
//                if (drawer.isDrawerOpen(GravityCompat.START))
//                    mainView.setTranslationX(slideOffset * drawerView.getWidth());
//                else if (drawer.isDrawerOpen(GravityCompat.END))
//                    mainView.setX(-drawerView.getWidth() * slideOffset);
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
                drawer.setScrimColor(Color.TRANSPARENT);
            }
        };

        drawer.setDrawerListener(mDrawerToggle);
//        drawer.setX(-drawerView.getWidth());
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void findIds() {
        mHandler = new Handler();
        textViewTitle = findViewById(R.id.toolbar_title);
        drawerView = findViewById(R.id.drawerView);
        drawerView_left = findViewById(R.id.drawerView_left);
        mainView = findViewById(R.id.mainView);
        navigationView = findViewById(R.id.pre_login_nav_view);
        navigationView_right = findViewById(R.id.nav_view_right);
        imgLogin = findViewById(R.id.menuLogin);
        imgSignup = findViewById(R.id.menuSignout);
        drawer = findViewById(R.id.pre_drawer_layout);
        View header = navigationView.getHeaderView(0);
        txtUserLabel = header.findViewById(R.id.textViewLabel);
        menuRight = findViewById(R.id.menuRight);

        imgLogin.setOnClickListener(this);
        imgSignup.setOnClickListener(this);
    }

    private void init() {
        if (isLogin) {
            Log.d("<>login-", " is true");
            txtUserLabel.setVisibility(View.VISIBLE);
            drawerView_left.setVisibility(View.VISIBLE);
            menuRight.setVisibility(View.VISIBLE);
//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,  GravityCompat.END);
            imgLogin.setVisibility(View.GONE);
            imgSignup.setVisibility(View.GONE);
            txtUserLabel.setText(getString(R.string.welcome_title) + " " + userName);
            navigationView.getMenu().findItem(R.id.ic_action_change_password).setVisible(true);
            navigationView.getMenu().findItem(R.id.ic_action_notification).setVisible(true);
        } else {
            Log.d("<>login-", " is false");
            drawerView_left.setVisibility(View.GONE);
            txtUserLabel.setVisibility(View.GONE);
            menuRight.setVisibility(View.GONE);
//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,  GravityCompat.END);
            imgLogin.setVisibility(View.VISIBLE);
            imgSignup.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.ic_action_change_password).setVisible(false);
            navigationView.getMenu().findItem(R.id.ic_action_notification).setVisible(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.PreLoginFrame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void setToolbarTitle() {
        textViewTitle.setText(activityTitles[navItemIndex]);
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        Log.d("<>pre-", " ==============> " + getSupportActionBar().getTitle() + "");
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                Home homeFragment = new Home();
                return homeFragment;
            case 1:
                // photos
//                PhotosFragment photosFragment = new PhotosFragment();
//                return photosFragment;
            default:
                return new Home();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pre_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    public void setToolbarTitle(String title) {
        textViewTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pre_login_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        invalidateOptionsMenu();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_right_home) {
            // Handle the camera action
            Log.d("<>frag-", " called home fragment");
            CURRENT_TAG = TAG_HOME;
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.PreLoginFrame, fragment, CURRENT_TAG);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_right_booking) {
            CURRENT_TAG = TAG_BOOKINGS;
            Log.d("<>frag-", " called booking fragment");
            MyBookingsMainFragment main = new MyBookingsMainFragment();
            Fragment fragment = main;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.PreLoginFrame, fragment, CURRENT_TAG);
            fragmentTransaction.commit();

        } else if (id == R.id.ic_action_feedback) {

        } else if (id == R.id.ic_action_logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().commit();
            isLogin = false;
            init();
            /*Intent i = new Intent(PreLoginMainActivity.this, Login.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();*/
        } else if (id == R.id.menu_right_favourite) {
            CURRENT_TAG = TAG_FAV;
            Log.d("<>frag-", " called booking fragment");
            Favourites main = new Favourites();
            Fragment fragment = main;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.PreLoginFrame, fragment, CURRENT_TAG);
            fragmentTransaction.commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pre_drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void getLoginData() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final Call<JsonElement> mService = mInterfaceService.getLoginData(SharedData.getWebToken(PreLoginMainActivity.this));
        Log.d("<>login-", " login service ==> " + mService.toString());
        Log.d("<>login-", " Body is  ==> " + mService.request().body() + "");
//        Log.d("<>login-", " headers with token  ==> "+mService.request().header("token").toString()+"");
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response headers ===> " + response.headers());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    Log.d("<>log-", " in response of login ");
                    Log.d("<>log-", " response body for data ==> " + response.body().toString());
                    ArrayList<String> favourites = new ArrayList<>();
                    userName = response.body().getAsJsonObject().get("name").getAsString();
                    if (SharedData.getIsRemember(PreLoginMainActivity.this) != true) {
                        SharedData.setUserId(PreLoginMainActivity.this, response.body().getAsJsonObject().get("_id").getAsString());
                        MyApplication.getGlobalData().setUserId(response.body().getAsJsonObject().get("_id").getAsString());
                    } else {
                        SharedData.setUserId(PreLoginMainActivity.this, response.body().getAsJsonObject().get("_id").getAsString());
                    }
                    if (response.body().getAsJsonObject().has("restaurantFacility")) {
                        JsonArray restaurantFavourite = response.body().getAsJsonObject().get("favouriteRestaurants").getAsJsonArray();
                        for (int rest = 0; rest < restaurantFavourite.size(); rest++) {
                            favourites.add(restaurantFavourite.get(rest).getAsString());
                        }
                    }
                    ArrayList<OUser> user = new ArrayList<>();
                    user.add(new OUser(
                            response.body().getAsJsonObject().get("_id").getAsString(),
                            response.body().getAsJsonObject().get("name").getAsString(),
                            response.body().getAsJsonObject().get("email").getAsString(),
                            response.body().getAsJsonObject().get("mobile_number").getAsString(),
                            response.body().getAsJsonObject().get("role").getAsString(),
                            favourites));
                    MyApplication.getGlobalData().addUserDataList(user);
                    if (response.body().getAsJsonObject().get("role").getAsString().equals(APIConstants.ROLE_PARAM)) {
                        SharedData.setIsLoggedIn(PreLoginMainActivity.this, true);
                        txtUserLabel.setVisibility(View.VISIBLE);
                        txtUserLabel.setText("Welcome " + userName);
                        isLogin = true;
                        init();
                    } else {
                        init();
                        Toast.makeText(PreLoginMainActivity.this, getResources().getString(R.string.permission_issue), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuLogin:
                Intent intent = new Intent(PreLoginMainActivity.this, Login.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.menuSignout:
                Intent intentSignOut = new Intent(PreLoginMainActivity.this, Signup.class);
                startActivity(intentSignOut);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    public void setUserName(String userName) {
        txtUserLabel = (TextView) findViewById(R.id.textViewLabel);
        txtUserLabel.setVisibility(View.VISIBLE);
        txtUserLabel.setText("Welcome " + userName);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.menu_right_home:
                fragmentClass = Home.class;
                break;
            case R.id.menu_right_booking:
                fragmentClass = MyBookingsMainFragment.class;
                break;
            case R.id.menu_right_favourite:
                fragmentClass = Favourites.class;
                break;
            case R.id.menu_right_details:
                fragmentClass = MyAccountFragment.class;
                break;
            case R.id.ic_action_logout:
                notificationLogoutCall();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                isLogin = false;
                init();
               /* Intent i = new Intent(PreLoginMainActivity.this, Login.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();*/
            default:
                fragmentClass = Home.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
        fragmentTransaction.commit(); // save the changes

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pre_drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
    }

    public void notificationLogoutCall() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        JsonObject noteData = new JsonObject();
        noteData.addProperty("token", SharedData.getNotificationToken(getApplicationContext()));

        final Call<JsonElement> mService = mInterfaceService.notificationLogout(noteData);
        Log.d("<>login-", " login service ==> " + mService.toString());
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                if (response.isSuccessful()) {
                    Log.d("<>push-", " notification result ==> " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                Log.d("<>push-", " failure result ==> ");
            }
        });

    }
}

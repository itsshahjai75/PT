package com.nexus.locum.locumnexus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.customviews.CustomTypefaceSpan;
import com.nexus.locum.locumnexus.fragments.HomeBottomNavTabFragment;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_LOGINKEY;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_EMAIL;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_FULL_NAME;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;
import static com.nexus.locum.locumnexus.utilities.Const.getUserDetailsJson;

public class NavigationMainDashboardLocum extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Toolbar toolbar;
    public static TextView toolbar_title;
    static Activity activity;

    ImageView imageViewLogo;
    TextView tv_UserName,tv_userEmail;

    static ProgressDialog progressDialog;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation_locum);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        activity = this;


        new GetUSerBasicDetails().execute();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);

        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        imageViewLogo = (ImageView)header.findViewById(R.id.imageViewLogo);
        tv_UserName = (TextView)header.findViewById(R.id.tv_UserName);
        tv_userEmail = (TextView)header.findViewById(R.id.tv_userEmail);

        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, new HomeBottomNavTabFragment());
                transaction.commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                }
            }
        });


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
        navigationView.setNavigationItemSelectedListener(this);

    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Proxima Nova Regular.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    TextView tv_cartItemCount;
    ImageView ic_cart_actionbar;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navigation_main_screen, menu);
        /*MenuItem item = menu.findItem(R.id.ic_action_notification);
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
        });*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home_left) {
            invalidateOptionsMenu();
            toolbar_title.setText("Dashboard");
            new HomeBottomNavTabFragment();
        } else if (id == R.id.nav_booking_left) {
            invalidateOptionsMenu();
        } else if (id == R.id.nav_diary_left) {
            invalidateOptionsMenu();
        } else if (id == R.id.nav_timesheet_left) {
            invalidateOptionsMenu();
        } else if (id == R.id.nav_finanace_left) {
            invalidateOptionsMenu();
        } else if (id == R.id.nav_property_left) {
            invalidateOptionsMenu();
        }else if (id == R.id.nav_logout) {

            CONST_SHAREDPREFERENCES.edit().clear().apply();
            CONST_SHAREDPREFERENCES.edit().putString(PREF_USER_TOKEN,"").apply();
            CONST_SHAREDPREFERENCES.edit().putString(PREF_LOGINKEY,"no").apply();


            CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ID,"").apply();
            CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_FULL_NAME,"").apply();
            CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_EMAIL,"").apply();
            CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_MOBILE_NO,"").apply();
            CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ROLE,"").apply();


            getUserDetailsJson = new JsonObject();
            CONST_PROFILE_JSON = new JsonObject();

            Log.e("profile JSON" , CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,"")
                    +"\n============"
                    +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,"")+"============"
                    +CONST_PROFILE_JSON.toString());
            startActivity(new Intent(NavigationMainDashboardLocum.this,LoginLocum.class));
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, selectedFragment);
            transaction.commit();
        } else {
            item.setChecked(false);
        }


        return true;

    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        new GetUSerBasicDetails().execute();
    }
*/

    static String resUserBasicDetails;
    public class GetUSerBasicDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(activity, "Loading", "Please Wait...", true, false);

        }

        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(Object... parametros) {

            try {
                Log.e("basic detail time TOKEN----",CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""));
                String USerProfileDetails = ResponseWithAuthAPI(NavigationMainDashboardLocum.this,
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/basicDetails", "","get");
                resUserBasicDetails =USerProfileDetails;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserBasicDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            String userId = null;

            try{
                Log.i("RES basicDetails---", ""+resUserBasicDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUserBasicDetails).getAsJsonObject();

                getUserDetailsJson = rootObj;

                userId = rootObj.get("userId").getAsString();
                String title = rootObj.get("title").getAsString();
                String fname = rootObj.get("fname").getAsString();
                String lname = rootObj.get("lname").getAsString();
                String email = rootObj.get("email").getAsString();

                JsonArray category = rootObj.get("category").getAsJsonArray();

                for(int a=0;a<category.size();a++) {
                    String __v = category.get(a).getAsJsonObject().get("__v").getAsString();
                    String name =  category.get(a).getAsJsonObject().get("name").getAsString();
                    String _id = category.get(a).getAsJsonObject().get("_id").getAsString();
                }

                String str_package = rootObj.get("package").getAsString();
                String trialPlan = rootObj.get("trialPlan").getAsString();
                String mobile = rootObj.get("mobile").getAsString();
                String role = rootObj.get("role").getAsString();



               CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ID,rootObj.get("userId").getAsString()).apply();
               CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_FULL_NAME,fname+" "+lname).apply();
               CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_EMAIL,email).apply();
               CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_MOBILE_NO,mobile).apply();
               CONST_SHAREDPREFERENCES.edit().putString(Const.PREF_USER_ROLE,role).apply();

                tv_UserName.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_FULL_NAME,""));
                tv_userEmail.setText(CONST_SHAREDPREFERENCES.getString(PREF_USER_EMAIL,""));
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(userId!=null) {
                new GetUserProfileDetails().execute();
            }else{
                Toast.makeText(NavigationMainDashboardLocum.this,"User ID not found !", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));

                String USerProfileDetails = ResponseWithAuthAPI(NavigationMainDashboardLocum.this, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/getProfile", getProfileDetails.toString(),"post");
                resUSerProfileDetails =USerProfileDetails;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            progressDialog.dismiss();

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, new HomeBottomNavTabFragment());
                transaction.commit();

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }


}
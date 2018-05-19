package com.nexus.locum.locumnexus.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.fragments.hometabs.CalendarLocum;
import com.nexus.locum.locumnexus.fragments.hometabs.HomeDashboard;
import com.nexus.locum.locumnexus.fragments.hometabs.Invoices;
import com.nexus.locum.locumnexus.fragments.hometabs.MoreDetailsLocum;
import com.nexus.locum.locumnexus.fragments.hometabs.ProfilelDetailsLocums;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.NavigationMainDashboardLocum.toolbar_title;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ROLE;

public class HomeBottomNavTabFragment extends Fragment {

    @BindView(R.id.navigation)BottomNavigationView bottomNavigationView;

    SharedPreferences CONST_SHAREDPREFERENCES;

    public static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BottomNav", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BottomNav", "Unable to change value of shift mode", e);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ConvertView =  inflater.inflate(R.layout.fragment_home_bottom_nav_tab, container, false);
        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        ButterKnife.bind( this, ConvertView);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,  CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                ?new ProfilelDetailsLocums():new HomeDashboard());
        transaction.commit();

        if( CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            toolbar_title.setText("Profile");
        }else{
            bottomNavigationView.setSelectedItemId(R.id.nav_home_bottom);
            toolbar_title.setText("Dashboard");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.nav_home_bottom:
                                toolbar_title.setText(CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) == false
                                        ? "Profile" : "Dashboard");
                                selectedFragment = CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) == false
                                        ? new ProfilelDetailsLocums() : new HomeDashboard();
                                break;
                            case R.id.nav_profile:
                                toolbar_title.setText("Profile");
                                selectedFragment =  new ProfilelDetailsLocums();
                                break;
                            case R.id.nav_booking:
                                toolbar_title.setText(CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?"Profile":"Bookings");
                                selectedFragment = CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?new ProfilelDetailsLocums():new CalendarLocum();

                                break;
                            case R.id.nav_invoice:
                                toolbar_title.setText(CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?"Profile":"Invoice");
                                selectedFragment = CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?new ProfilelDetailsLocums():new Invoices();

                                break;
                            case R.id.nav_more:
                                toolbar_title.setText(CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?"Profile":"More");
                                selectedFragment = CONST_SHAREDPREFERENCES.getBoolean(PREF_IS_PROFILECOMPLETED,false) ==false
                                        ?new ProfilelDetailsLocums():new MoreDetailsLocum();

                                break;
                        }
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });



        return ConvertView;
    }


}

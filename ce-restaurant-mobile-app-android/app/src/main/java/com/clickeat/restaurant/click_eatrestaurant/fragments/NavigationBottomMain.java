package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.clickeat.restaurant.click_eatrestaurant.NavigationMainScreen;
import com.clickeat.restaurant.click_eatrestaurant.R;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.clickeat.restaurant.click_eatrestaurant.NavigationMainScreen.toolbar_title;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationBottomMain extends Fragment {


   public static  BottomNavigationView bottomNavigationView;

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
        View ConvertView = inflater.inflate(R.layout.fragment_navigation_bottom_main, container, false);
        ButterKnife.bind( this, ConvertView);

        bottomNavigationView = (BottomNavigationView)ConvertView.findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        toolbar_title.setText(getString(R.string.static_opsmanager));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new BotNavTabHome());
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                toolbar_title.setText(getString(R.string.static_opsmanager));
                                selectedFragment = new BotNavTabHome();
                                /*getContext().startActivity(new Intent(getActivity(),NavigationMainScreen.class));
                                getActivity().finish();*/
                                break;
                            case R.id.nav_bookings:
                                toolbar_title.setText(getString(R.string.menu_bookings));
                                selectedFragment = new BotNavTabBookings();
                                break;
                            case R.id.nav_dinein:
                                toolbar_title.setText(getString(R.string.menu_walkin));
                                selectedFragment = new BotNavTabDineIn();
                                break;
                            case R.id.nav_takeaway:
                                toolbar_title.setText(getString(R.string.menu_takeaway));
                                selectedFragment = new BotNavTabTakeaway();
                                break;
                            case R.id.nav_more:
                                toolbar_title.setText(getString(R.string.menu_more));
                                selectedFragment = new BotNavTabMore();
                                break;
                        }
                        if(selectedFragment!=null) {
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment);
                            transaction.commit();
                        }
                        return true;
                    }
                });


        return ConvertView;

    }

}

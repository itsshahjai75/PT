package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by pivotech on 22/11/17.
 */

public class ViewPagerAboutRestaurantAdapter extends FragmentStatePagerAdapter {
    String restaurantId;

    public ViewPagerAboutRestaurantAdapter(FragmentManager fm, String restaurantId) {
        super(fm);
        this.restaurantId = restaurantId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = FragmentAboutRestaurant.newInstance("About page");
        } else if (position == 1) {
            fragment = FragmentMenuRestaurant.newInstance(restaurantId);
        } else if (position == 2) {
            fragment = FragmentMealDealsRestaurant.newInstance(restaurantId);
        } else if (position == 3) {
            fragment = FragmentTableRestaurant.newInstance("Table Map page");
        } else if (position == 4) {
            fragment = FragmentAboutRestaurant.newInstance("Offers page");
        } /*else if (position == 5) {
            fragment =new FragmentFacilitiesRestaurant();
        } else if (position == 6) {
            fragment = FragmentAboutRestaurant.newInstance("Photos page");
        } else if (position == 7) {
            fragment = FragmentAboutRestaurant.newInstance("Reviews page");
        }*/
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "About";
        } else if (position == 1) {
            title = "Menu";
        } else if (position == 2) {
            title = "Meals Deals";
        } else if (position == 3) {
            title = "Table Map";
        } else if (position == 4) {
            title = "Offers";
        } /*else if (position == 4) {
            title = "Meals Deals";
        } else if (position == 5) {
            title = "Facilities";
        } else if (position == 6) {
            title = "Photos";
        } else if (position == 7) {
            title = "Reviews";
        }*/
        return title;
    }
}

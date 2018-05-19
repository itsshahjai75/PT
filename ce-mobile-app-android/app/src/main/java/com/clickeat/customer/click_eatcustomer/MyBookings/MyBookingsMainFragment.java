package com.clickeat.customer.click_eatcustomer.MyBookings;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;

public class MyBookingsMainFragment extends android.support.v4.app.Fragment implements TabLayout.OnTabSelectedListener {

    private View m_myFragmentView;
    private PreLoginMainActivity activity;
    //This is our tablayout
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_arrow_drop_down,
            R.drawable.ic_arrow_drop_down
    };
    private Boolean isUpcomingAscending = true;
    private Boolean isPastAscending = true;
    private  BookingsPastFragment bookingsPastFragment;
    private BookingsUpcomingFragment bookingsUpcomingFragment;

    //This is our viewPager
    private ViewPager viewPager;

    public static MyBookingsMainFragment newInstance() {
        MyBookingsMainFragment fragment = new MyBookingsMainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle("My Bookings");
        m_myFragmentView = inflater.inflate(R.layout.activity_my_bookings, container, false);

        viewPager = (ViewPager) m_myFragmentView.findViewById(R.id.viewpager_booking);
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) m_myFragmentView.findViewById(R.id.tab_layout_booking);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setupTabIcons();
//Creating our pager adapter
        PagerBookingAdapter adapter = new PagerBookingAdapter((getActivity()).getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("<>tab-", " page selected ==> "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
/*
        PagerBookingAdapter pagerAdapter =
                new PagerBookingAdapter((getActivity()).getSupportFragmentManager(), getActivity());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);*/
        // Iterate over all tabs and set the custom view
       /* for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }*/
        return m_myFragmentView;
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_custom_tab, null);
        tabOne.setText("Upcoming");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_custom_tab, null);
        tabTwo.setText("Past");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        Log.d("<>tab-", " selected tab is ==> " + tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.d("<>tab-", " reselected tab is ==> " + tab.getPosition());
        if (tab.getPosition() == 0) {
            if (isUpcomingAscending) {
                TextView tabOne = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab);
                tabOne.setWidth(30);
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ic_arrow_top_down, 0);
                tabOne.setGravity(Gravity.CENTER_VERTICAL);
                bookingsUpcomingFragment.getBookings(isUpcomingAscending);
                isUpcomingAscending = !isUpcomingAscending;
            } else {
                TextView tabOne = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab);
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                bookingsUpcomingFragment.getBookings(isUpcomingAscending);
                isUpcomingAscending = !isUpcomingAscending;
            }
        }
        if (tab.getPosition() == 1) {
            if (isPastAscending) {
                TextView tabOne = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab);
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ic_arrow_top_down, 0);
                bookingsPastFragment.getBookings(isPastAscending);
                isPastAscending = !isPastAscending;
            } else {
                TextView tabOne = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab);
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                bookingsPastFragment.getBookings(isPastAscending);
                isPastAscending = !isPastAscending;
            }
        }
    }

    class PagerBookingAdapter extends FragmentStatePagerAdapter {

        String tabTitles[] = new String[]{"Upcoming", "Past"};
        Context context;
        //integer to count number of tabs
        int tabCount;

        //Constructor to the class
        public PagerBookingAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount = tabCount;
        }
      /*  public PagerBookingAdapter(android.support.v4.app.FragmentManager fm, Context context) {
            super(fm);
            Log.d("<>bbb-", " pager booking called");
            this.context = context;
        }*/

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    bookingsUpcomingFragment = BookingsUpcomingFragment.newInstance(isUpcomingAscending);
                    return bookingsUpcomingFragment;
                case 1:
                    bookingsPastFragment = BookingsPastFragment.newInstance(isPastAscending);
                    return bookingsPastFragment;
                default:
                    return null;
            }
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
        /*@Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Log.d("<>bbb-", "return null");
            switch (position) {
                case 0:
                    Log.d("<>bbb-", "return 0");
                    return BookingsUpcomingFragment.newInstance();
                case 1:
                    Log.d("<>bbb-", "return 1");
                    return BookingsPastFragment.newInstance();
            }
            Log.d("<>bbb-", "return null");
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(getActivity()).inflate(R.layout.layout_custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }*/

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_booking_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_booking_sync:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

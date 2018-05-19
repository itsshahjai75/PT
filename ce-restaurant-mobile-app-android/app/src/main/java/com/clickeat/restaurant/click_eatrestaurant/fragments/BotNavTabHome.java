package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.customviews.UserTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BotNavTabHome extends Fragment {


    private View m_myFragmentView;

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    UserTextView tabAwaiting, tabupcoming;
    MyViewPagerAdapter adapter;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_bot_nav_tab_home, container, false);

        Log.d("Frag BotHome---","Loaded");


        viewPager = (ViewPager)m_myFragmentView.findViewById(R.id.viewpager_bot_tab_home);

        adapter = new MyViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new NavTabHomeAwaiting(),"Awaiting"/*.newInstance(getContext())*/);
        adapter.addFrag(new NavTabHomeUpcomming(),"Upcomming");

        viewPager.setAdapter(adapter);


        tabLayout = (TabLayout)m_myFragmentView.findViewById(R.id.tabsNav_bot_nav_home);
        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.getTabAt(0).select();
        //viewPager.setCurrentItem(0,true);

       /* tabAwaiting = (UserTextView) LayoutInflater.from(getContext()).inflate(R.layout.onlytextview, null);
        tabAwaiting.setText(getString(R.string.tab_awaiting));
        tabAwaiting.setGravity(Gravity.CENTER);
        tabAwaiting.setTextColor(getResources().getColor(R.color.colorPrimary,getActivity().getTheme()));
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.analytics, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabAwaiting);

        tabupcoming = (UserTextView) LayoutInflater.from(getContext()).inflate(R.layout.onlytextview, null);
        tabupcoming.setText(getString(R.string.tab_upcomming));
        tabupcoming.setGravity(Gravity.CENTER);
        tabupcoming.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
        tabLayout.getTabAt(1).setCustomView(tabupcoming);*/

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                /*for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    if (tab.getPosition() == 0) {
                        tabAwaiting.setTextColor(getResources().getColor(R.color.colorPrimary,getActivity().getTheme()));
                        tabupcoming.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
                    } else if (tab.getPosition() == 1) {
                        tabAwaiting.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
                        tabupcoming.setTextColor(getResources().getColor(R.color.colorPrimary,getActivity().getTheme()));
                    }
                }*/

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return m_myFragmentView;

    }

    class MyViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyViewPagerAdapter(FragmentManager manager) {
            super(manager);
            Log.d("fragment adapter---","called");
        }

        @Override
        public Fragment getItem(int position) {

            Log.d("fragment adapter---","get item");
            if (position == 0) {
                return new NavTabHomeAwaiting()/*.newInstance(getContext())*/;
            } else if (position == 1){
                return new NavTabHomeUpcomming();
            }else {
                return new NavTabHomeAwaiting();
            }
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String Title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(Title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position == 0)
                new NavTabHomeAwaiting()/*.newInstance(getContext())*/;
            else if (position == 1)
                new NavTabHomeAwaiting();
        }

    }

}

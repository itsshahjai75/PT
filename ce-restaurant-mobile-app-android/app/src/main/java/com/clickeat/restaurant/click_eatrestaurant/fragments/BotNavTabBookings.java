package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clickeat.restaurant.click_eatrestaurant.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BotNavTabBookings extends Fragment {

    private View m_myFragmentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_bot_nav_tab_bookings, container, false);
        ButterKnife.bind(this, m_myFragmentView);


        return m_myFragmentView;
    }

}

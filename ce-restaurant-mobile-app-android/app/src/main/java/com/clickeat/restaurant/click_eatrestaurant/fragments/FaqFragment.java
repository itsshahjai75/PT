package com.clickeat.restaurant.click_eatrestaurant.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;

import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.UnusedPackage.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaqFragment extends Fragment {

    @BindView(R.id.expandableListViewFaq)ExpandableListView expandableListViewFaq;

    private View m_myFragmentView;

    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_faq, container, false);

        ButterKnife.bind(this, m_myFragmentView);

        ViewTreeObserver vto = expandableListViewFaq.getViewTreeObserver();
        /*vto.addOnGlobalLayoutListener(new      ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                expandableListViewFaq.setIndicatorBounds(expandableListViewFaq.getMeasuredWidth() ,
                        expandableListViewFaq.getMeasuredWidth());
            }
        });*/

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expandableListViewFaq.setAdapter(listAdapter);
        expandableListViewFaq.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    expandableListViewFaq.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        return m_myFragmentView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What is purpose of Login & Password?");
        listDataHeader.add("What if my credentials, email id is blank?");
        listDataHeader.add("What if I have forgotten my username?");

        // Adding child data
        List<String> que1 = new ArrayList<String>();
        que1.add("Login and password are created by Admin to access ClickEat Restaurant application in a secured manner for the data relevant to their business.\n" +
                "\n" +
                "Username and Password are created by Admin during registration.");


        List<String> que2 = new ArrayList<String>();
        que2.add("ClickEat provides maintains a directory of all valid email addresses and password provided by admin in registration. ClickEat provides will not allow unauthorised users or blank information to access any part of an application.\n" +
                "\n" +
                "It is mandatory to provide valid username and password.");
        /*nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");*/

        List<String> que3 = new ArrayList<String>();
        que3.add("ClickEat provides features to assist you with providing your registered email address and password in a secured manner.");
        

        listDataChild.put(listDataHeader.get(0), que1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), que2);
        listDataChild.put(listDataHeader.get(2), que3);
    }


}

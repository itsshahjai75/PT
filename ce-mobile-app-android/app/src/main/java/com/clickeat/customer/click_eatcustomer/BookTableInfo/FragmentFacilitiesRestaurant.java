package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.clickeat.customer.click_eatcustomer.R;

/**
 * Created by pivotech on 22/11/17.
 */

public class FragmentFacilitiesRestaurant extends Fragment {
    private View m_myFragmentView;
    String param1;
    private static final String ARG_PARAM = "param1";

    public static FragmentFacilitiesRestaurant newInstance(String param1) {
        FragmentFacilitiesRestaurant fragment = new FragmentFacilitiesRestaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1 + "");
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentFacilitiesRestaurant() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_myFragmentView = inflater.inflate(R.layout.layout_facilities_fragment, container, false);
        GridView grid = m_myFragmentView.findViewById(R.id.grid_facilities);

        String[] web = {
                "Wi-Fi",
                "Cab",
                "Disability",
                "Plug",
                "Parking"

        };
        String[] imageId = {
                "fa-wifi",
                "fa-taxi",
                "fa-wheelchair",
                "fa-plug",
                "fa-bus"

        };

        CustomGrid adapter = new CustomGrid(getActivity(), web, imageId);
        grid.setAdapter(adapter);
        return m_myFragmentView;
    }
}

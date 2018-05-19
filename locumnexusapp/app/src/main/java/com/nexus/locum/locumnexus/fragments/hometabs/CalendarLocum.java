package com.nexus.locum.locumnexus.fragments.hometabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexus.locum.locumnexus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarLocum extends Fragment {


    public CalendarLocum() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_locum, container, false);
    }

}

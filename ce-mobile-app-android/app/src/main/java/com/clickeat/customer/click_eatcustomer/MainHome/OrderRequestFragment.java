package com.clickeat.customer.click_eatcustomer.MainHome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.clickeat.customer.click_eatcustomer.R;


/**
 * Created by pivotech on 9/10/17.
 */

public class OrderRequestFragment extends Fragment{
    private View m_myFragmentView;

    public static OrderRequestFragment newInstance() {
        OrderRequestFragment fragment = new OrderRequestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_myFragmentView = inflater.inflate(R.layout.layout_order_request, container, false);
        return m_myFragmentView;
    }
}

package com.clickeat.customer.click_eatcustomer.MainHome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;


/**
 * Created by pivotech on 9/10/17.
 */

public class Home extends Fragment {
    BottomNavigationView navigationView;
    private View m_myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        final PreLoginMainActivity activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle(getString(R.string.table_book));
        m_myFragmentView = inflater.inflate(R.layout.layout_fragment_home, container, false);
//        ButterKnife.bind(getActivity(), m_myFragmentView);
        navigationView = (BottomNavigationView) m_myFragmentView.findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_book_table:
                        activity.setToolbarTitle(getString(R.string.table_book));
                        selectedFragment = BookATableFragment.newInstance();
                        break;
                    case R.id.action_take_order:
                        activity.setToolbarTitle(getString(R.string.order_away));
                        selectedFragment = OrderRequestFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, BookATableFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

        return m_myFragmentView;
    }

}

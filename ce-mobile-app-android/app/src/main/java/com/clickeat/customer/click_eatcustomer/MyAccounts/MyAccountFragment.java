package com.clickeat.customer.click_eatcustomer.MyAccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;

/**
 * Created by pivotech on 6/3/18.
 */

public class MyAccountFragment extends Fragment {
    private View m_myFragmentView;
    private PreLoginMainActivity activity;
    private RecyclerView recyclerView;
    private AccountAdapter mAdapter;
    private String[] accountNames;
    private Integer[] accountImage;

    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle(getString(R.string.my_account));
        m_myFragmentView = inflater.inflate(R.layout.layout_my_account_fragment, container, false);

        accountNames = new String[]{
                "Personal",
                "Address",
                "Security",
                "Preferences"
        };

        accountImage = new Integer[]{
                R.drawable.ic_action_personal,
                R.drawable.ic_action_address,
                R.drawable.ic_action_security,
                R.drawable.ic_action_preferences
        };

        recyclerView = (RecyclerView) m_myFragmentView.findViewById(R.id.recycler_view_account);
        AccountAdapter.RecyclerViewClickListener listener = (view, position) -> {
//            Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
            if (position == 0) {
                Intent intentPersonal = new Intent(getActivity(), PersonalForm.class);
                startActivity(intentPersonal);
            } else if (position == 1) {
                Intent intentAddess = new Intent(getActivity(), PersonalAddressList.class);
                startActivity(intentAddess);
            } else if (position == 2) {
                Intent intentSecurity = new Intent(getActivity(), SecurityForm.class);
                startActivity(intentSecurity);
            } else if (position == 3) {
                Intent intentPreference = new Intent(getActivity(), PreferncesForm.class);
                startActivity(intentPreference);
            }
        };
        mAdapter = new AccountAdapter(getActivity(), accountNames, accountImage, listener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return m_myFragmentView;
    }
}

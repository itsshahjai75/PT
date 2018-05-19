package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OMealDeals;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pivotech on 22/11/17.
 */

public class FragmentMealDealsRestaurant extends Fragment {
    private View m_myFragmentView;
    String param1;
    private static final String ARG_PARAM = "param1";
    private RecyclerView rv_meal_deals;
    private TextView statusMeals;
    private MealDealsRecyclerAdapter mealsAdapter;

    public static FragmentMealDealsRestaurant newInstance(String param1) {
        FragmentMealDealsRestaurant fragment = new FragmentMealDealsRestaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1 + "");
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMealDealsRestaurant() {
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
        m_myFragmentView = inflater.inflate(R.layout.layout_meal_deals_fragment, container, false);

        findIds();
        init();
        return m_myFragmentView;
    }

    private void findIds() {
        rv_meal_deals = m_myFragmentView.findViewById(R.id.rv_meal_deals);
        statusMeals = m_myFragmentView.findViewById(R.id.statusMeals);
    }

    private void init(){
        List<OMealDeals> mealDeals = new ArrayList<>();
        mealDeals = MyApplication.getGlobalData().getMealDealList();
        if (mealDeals.size() > 0) {
            statusMeals.setVisibility(View.GONE);
            rv_meal_deals.setVisibility(View.VISIBLE);
            rv_meal_deals.setHasFixedSize(true);
            mealsAdapter = new MealDealsRecyclerAdapter(getActivity(), mealDeals);
            rv_meal_deals.setAdapter(mealsAdapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv_meal_deals.setLayoutManager(llm);
        } else {
            statusMeals.setVisibility(View.VISIBLE);
            rv_meal_deals.setVisibility(View.GONE);
        }

    }


}

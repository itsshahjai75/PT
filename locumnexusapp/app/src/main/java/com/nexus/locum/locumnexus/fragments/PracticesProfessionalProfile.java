package com.nexus.locum.locumnexus.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.nexus.locum.locumnexus.AddPreferedPractices;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.adapters.PracticesProfileAdapter;
import com.nexus.locum.locumnexus.modelPOJO.PracticesModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticesProfessionalProfile extends Fragment {


    @BindView(R.id.recycler_view_BookTable)
    RecyclerView recycler_view_BookTable ;

    private ArrayList<PracticesModel> dataModels;

    private View m_myFragmentView;
    @BindView(R.id.fab_add_prefered_practice) FloatingActionButton fab_add_prefered_practice;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_practices_professional_profile, container, false);

        ButterKnife.bind(this,m_myFragmentView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_BookTable.setItemAnimator(new DefaultItemAnimator());
        recycler_view_BookTable.setLayoutManager(llm);

        dataModels = new ArrayList<>();

        JsonArray practiceJsonArray = Const.CONST_PROFILE_JSON.get("professional").getAsJsonObject()
                .get("practices").getAsJsonArray();

        if(practiceJsonArray.size()>0) {

            for (int a = 0; a < practiceJsonArray.size(); a++) {

                boolean prefer = practiceJsonArray.get(a).getAsJsonObject().get("prefer").getAsBoolean();
                String practice_name = practiceJsonArray.get(a).getAsJsonObject().get("practice_name").getAsString();
                String practice_code = practiceJsonArray.get(a).getAsJsonObject().get("practice_code").getAsString();
                String _id = practiceJsonArray.get(a).getAsJsonObject().get("_id").getAsString();

                String contact_name = practiceJsonArray.get(a).getAsJsonObject().has("contact_name")
                        ? practiceJsonArray.get(a).getAsJsonObject().get("contact_name").getAsString() : "";
                String email = practiceJsonArray.get(a).getAsJsonObject().has("email")
                        ? practiceJsonArray.get(a).getAsJsonObject().get("email").getAsString() : "";
                String contact_number = practiceJsonArray.get(a).getAsJsonObject().has("contact_number")
                        ? practiceJsonArray.get(a).getAsJsonObject().get("contact_number").getAsString() : "";

                dataModels.add(new PracticesModel(prefer, practice_name, practice_code, _id, "",
                        "", "",practiceJsonArray.get(a).getAsJsonObject()));
            }

            PracticesProfileAdapter ca = new PracticesProfileAdapter(dataModels, getActivity());
            recycler_view_BookTable.setAdapter(ca);
        }

        fab_add_prefered_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPreferedPractices.class));
            }
        });

        return m_myFragmentView;
    }

}

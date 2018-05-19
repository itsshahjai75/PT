package com.nexus.locum.locumnexus.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.adapters.ExpandableListAdapter;
import com.nexus.locum.locumnexus.modelPOJO.ExpandedMenuModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class TnCFinancialProfileFragment extends Fragment {


    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandTandC;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;
    SharedPreferences CONST_SHAREDPREFERENCES;
    public TnCFinancialProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tn_cfinancial_profile, container, false);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        expandTandC = (ExpandableListView)view.findViewById(R.id.expandTandC);
        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild, expandTandC);

        // setting list adapter
        expandTandC.setAdapter(mMenuAdapter);



        expandTandC.setIndicatorBounds(expandTandC.getWidth()-40, expandTandC.getWidth());

        expandTandC.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                return false;
            }
        });
        expandTandC.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                if(i==3){
                /*Intent menu = new Intent(NavigationMainScreen.this, MenuManagement.class);
                menu.putExtra("restaurantId", CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_ID,""));
                menu.putExtra("name", CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_FULL_NAME,""));
                startActivity(menu);*/

                }
                if(i==4){
                    //startActivity(new Intent(NavigationMainScreen.this, Profile.class));
                }
                return false;
            }
        });

        expandTandC.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup){
                    expandTandC.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            }
        });

        expandTandC.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(groupPosition==0 && childPosition==3){
                 //   startActivity(new Intent(NavigationMainScreen.this, UnavailableTableActivity.class));
                }

                return false;
            }
        });

        return view;
    }
    
    

    private void prepareListData() {
        
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();


        JsonObject TandCJsonObj = CONST_PROFILE_JSON.getAsJsonObject();
        JsonArray termsandconditions = TandCJsonObj.get("financial").getAsJsonObject()
                .get("termsandconditions").getAsJsonArray();

        for(int a= 0;a<termsandconditions.size();a++){

            String _id = termsandconditions.get(a).getAsJsonObject().get("_id").getAsString();
            String category = termsandconditions.get(a).getAsJsonObject().get("category").getAsString();
            String disable = termsandconditions.get(a).getAsJsonObject().get("disable").getAsString();
            String createdBy = termsandconditions.get(a).getAsJsonObject().get("createdBy").getAsString();
            String __v = termsandconditions.get(a).getAsJsonObject().get("__v").getAsString();
            JsonArray terms = termsandconditions.get(a).getAsJsonObject().get("terms").getAsJsonArray();

            ExpandedMenuModel item1 = new ExpandedMenuModel();
            item1.setIconName(category);
            item1.setIconImg(R.drawable.ic_next);
            // Adding data header
            listDataHeader.add(item1);

            List<String> heading1 = new ArrayList<String>();

            for(int b=0;b<terms.size();b++){
                String id = terms.get(b).getAsJsonObject().has("_id")
                        ?terms.get(b).getAsJsonObject().get("_id").getAsString():"";
                String include = terms.get(b).getAsJsonObject().get("include").getAsString();
                String name = terms.get(b).getAsJsonObject().get("name").getAsString();
                heading1.add(name);

            }
           // heading1.add("Add new terms .");
            listDataChild.put(listDataHeader.get(a), heading1);
        }
    }

}

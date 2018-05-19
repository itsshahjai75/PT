package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import com.clickeat.customer.click_eatcustomer.DataModel.OCuisines;
import com.clickeat.customer.click_eatcustomer.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pivotech on 27/12/17.
 */

public class ExpandableListDataPump {

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cuisine = new ArrayList<>();
        cuisine = MyApplication.getGlobalData().getCuisineList();

        List<String> facilities = new ArrayList<String>();
        facilities = MyApplication.getGlobalData().getFacilitiesList();

        List<String> collection = new ArrayList<String>();
       collection.add("Collection");
       collection.add("Delivery");

        expandableListDetail.put("Cuisine", cuisine);
        expandableListDetail.put("Collection or Delivery", collection);
        expandableListDetail.put("Facilities", facilities);
        return expandableListDetail;
    }
}

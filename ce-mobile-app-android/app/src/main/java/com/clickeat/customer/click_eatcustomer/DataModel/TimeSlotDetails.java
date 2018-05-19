package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 8/1/18.
 */

public class TimeSlotDetails {

    protected String name;
    protected String tag;
    protected String restaurantId;
    protected String noPeople;
    protected String restaurantName;


    public TimeSlotDetails(String name, String tag, String restaurantId, String noPeople, String restaurantName) {
        this.name = name;
        this.tag = tag;
        this.restaurantId = restaurantId;
        this.noPeople = noPeople;
        this.restaurantName = restaurantName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getNoPeople() {
        return noPeople;
    }

    public void setNoPeople(String noPeople) {
        this.noPeople = noPeople;
    }
}

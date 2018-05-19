package com.clickeat.customer.click_eatcustomer.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pivotech on 28/9/17.
 */

public class RestaurantByLocationModel {
    @SerializedName("longitude")
    @Expose
    public Double longitude;
    @SerializedName("latitude")
    @Expose
    public Double latitude;
    @SerializedName("maxDistance")
    @Expose
    public Double maxDistance;
    @SerializedName("distanceMultiplier")
    @Expose
    public Double distanceMultiplier;
    @SerializedName("intrested_cuisine")
    @Expose
    public ArrayList<String> intrested_cuisine;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("collection_delivery")
    @Expose
    public ArrayList<String> collection_delivery;
    @SerializedName("facility")
    @Expose
    public ArrayList<String> facility;
    @SerializedName("getObjectData")
    @Expose
    public JSONObject getObjectData;
    @SerializedName("special_diet")
    @Expose
    public ArrayList<String> special_diet;
    @SerializedName("ceClubMembership")
    @Expose
    public Boolean ceClubMembership;
    @SerializedName("maxAveragePrice")
    @Expose
    public Integer maxAveragePrice;
    @SerializedName("vegetarian")
    @Expose
    public Boolean vegetarian;


    public RestaurantByLocationModel(String language, Double longitude, Double latitude, Double
            maxDistance, Double distanceMultiplier, ArrayList<String> intrested_cuisine,
                                     ArrayList<String> collection_delivery, ArrayList<String> facility,
                                     JSONObject getObjectData, ArrayList<String> special_diet,
                                     Boolean ceClubMembership, Integer maxAveragePrice, Boolean vegetarian) {
        this.language = language;
        this.longitude = longitude;
        this.latitude = latitude;
        this.maxDistance = maxDistance;
        this.distanceMultiplier = distanceMultiplier;
        this.intrested_cuisine = intrested_cuisine;
        this.collection_delivery = collection_delivery;
        this.facility = facility;
        this.getObjectData = getObjectData;
        this.special_diet = special_diet;
        this.ceClubMembership = ceClubMembership;
        this.maxAveragePrice = maxAveragePrice;
        this.vegetarian = vegetarian;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Double getDistanceMultiplier() {
        return distanceMultiplier;
    }

    public void setDistanceMultiplier(Double distanceMultiplier) {
        this.distanceMultiplier = distanceMultiplier;
    }

    public ArrayList<String> getIntrested_cuisine() {
        return intrested_cuisine;
    }

    public void setIntrested_cuisine(ArrayList<String> intrested_cuisine) {
        this.intrested_cuisine = intrested_cuisine;
    }
}

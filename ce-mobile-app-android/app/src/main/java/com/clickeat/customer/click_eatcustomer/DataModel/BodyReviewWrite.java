package com.clickeat.customer.click_eatcustomer.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BodyReviewWrite {
    @SerializedName("restaurant_id")
    @Expose
    public String restaurant_id;
    @SerializedName("customer")
    @Expose
    public String customer;
    @SerializedName("reviewDate")
    @Expose
    public String reviewDate;
    @SerializedName("overallRating")
    @Expose
    public Float overallRating;
    @SerializedName("locationRating")
    @Expose
    public Float locationRating;
    @SerializedName("foodRating")
    @Expose
    public Float foodRating;
    @SerializedName("staffRating")
    @Expose
    public Float staffRating;
    @SerializedName("serviceRating")
    @Expose
    public Float serviceRating;

    @SerializedName("facilitiesRating")
    @Expose
    public Float facilitiesRating;


    public BodyReviewWrite(String restaurant_id, String customer, String reviewDate, Float overallRating, Float locationRating, Float foodRating, Float staffRating, Float serviceRating, Float facilitiesRating) {
        this.restaurant_id = restaurant_id;
        this.customer = customer;
        this.reviewDate = reviewDate;
        this.overallRating = overallRating;
        this.locationRating = locationRating;
        this.foodRating = foodRating;
        this.staffRating = staffRating;
        this.serviceRating = serviceRating;
        this.facilitiesRating = facilitiesRating;
    }


    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Float overallRating) {
        this.overallRating = overallRating;
    }

    public Float getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(Float locationRating) {
        this.locationRating = locationRating;
    }

    public Float getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(Float foodRating) {
        this.foodRating = foodRating;
    }

    public Float getStaffRating() {
        return staffRating;
    }

    public void setStaffRating(Float staffRating) {
        this.staffRating = staffRating;
    }

    public Float getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(Float serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Float getFacilitiesRating() {
        return facilitiesRating;
    }

    public void setFacilitiesRating(Float facilitiesRating) {
        this.facilitiesRating = facilitiesRating;
    }
}

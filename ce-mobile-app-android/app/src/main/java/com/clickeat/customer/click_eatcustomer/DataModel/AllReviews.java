package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.List;

/**
 * Created by pivotech on 21/3/18.
 */

public class AllReviews {
    private String overallRating;
    private String locationRating;
    private String foodRating;
    private String staffRating;
    private String serviceRating;
    private String facilitiesRating;
    private List<OReview> reviews;

    public AllReviews(String overallRating, String locationRating, String foodRating,
                      String staffRating, String serviceRating, String facilitiesRating, List<OReview> reviews) {
        this.overallRating = overallRating;
        this.locationRating = locationRating;
        this.foodRating = foodRating;
        this.staffRating = staffRating;
        this.serviceRating = serviceRating;
        this.facilitiesRating = facilitiesRating;
        this.reviews = reviews;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public String getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(String locationRating) {
        this.locationRating = locationRating;
    }

    public String getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(String foodRating) {
        this.foodRating = foodRating;
    }

    public String getStaffRating() {
        return staffRating;
    }

    public void setStaffRating(String staffRating) {
        this.staffRating = staffRating;
    }

    public String getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(String serviceRating) {
        this.serviceRating = serviceRating;
    }

    public String getFacilitiesRating() {
        return facilitiesRating;
    }

    public void setFacilitiesRating(String facilitiesRating) {
        this.facilitiesRating = facilitiesRating;
    }

    public List<OReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<OReview> reviews) {
        this.reviews = reviews;
    }


}

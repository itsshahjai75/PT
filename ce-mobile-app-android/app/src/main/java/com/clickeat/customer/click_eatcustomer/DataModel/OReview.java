package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.List;

/**
 * Created by pivotech on 21/3/18.
 */

public class OReview {
    private String restaurant_id;
    private String userId;
    private String customerName;
    private String reviewDate;
    private String suggestions;
    private String positives;
    private float overallRating;
    private float locationRating;
    private float foodRating;
    private float staffRating;
    private float serviceRating;
    private float facilitiesRating;
    private List<String> imagesPath;


    public OReview(String restaurant_id, String userId, String customerName, String reviewDate,
                   String suggestions, String positives, float overallRating, float locationRating,
                   float foodRating, float staffRating, float serviceRating, float facilitiesRating,
                   List<String> imagesPath) {
        this.restaurant_id = restaurant_id;
        this.userId = userId;
        this.customerName = customerName;
        this.reviewDate = reviewDate;
        this.suggestions = suggestions;
        this.positives = positives;
        this.overallRating = overallRating;
        this.locationRating = locationRating;
        this.foodRating = foodRating;
        this.staffRating = staffRating;
        this.serviceRating = serviceRating;
        this.facilitiesRating = facilitiesRating;
        this.imagesPath = imagesPath;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getPositives() {
        return positives;
    }

    public void setPositives(String positives) {
        this.positives = positives;
    }

    public float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(float overallRating) {
        this.overallRating = overallRating;
    }

    public float getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(float locationRating) {
        this.locationRating = locationRating;
    }

    public float getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(float foodRating) {
        this.foodRating = foodRating;
    }

    public float getStaffRating() {
        return staffRating;
    }

    public void setStaffRating(float staffRating) {
        this.staffRating = staffRating;
    }

    public float getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(float serviceRating) {
        this.serviceRating = serviceRating;
    }

    public float getFacilitiesRating() {
        return facilitiesRating;
    }

    public void setFacilitiesRating(float facilitiesRating) {
        this.facilitiesRating = facilitiesRating;
    }

    public List<String> getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(List<String> imagesPath) {
        this.imagesPath = imagesPath;
    }
}

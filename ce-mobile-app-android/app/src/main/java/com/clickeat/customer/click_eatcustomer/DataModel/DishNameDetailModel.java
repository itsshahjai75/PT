package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.List;

/**
 * Created by pivotech on 7/2/18.
 */

public class DishNameDetailModel {
    private String dishId;
    private String dishName;
    private String status;
    private String isVegetarian;
    private String description;
    private String cuisine;
    private String spiceLevel;
    private List<String> allergyList;
    private String costPriceRegular;
    private String costPriceSmall;
    private String costPricelarge;
    private String retailPriceRegular;
    private String retailPriceSmall;
    private String retailPricelarge;
    private List<String> dishSize;
    private List<String> diet;
    private String includeInOffer;
    private String calorieRegular;
    private String endDate;
    private String currency;
    private Boolean isDishData;

    public DishNameDetailModel(String dishName, String status, String isVegetarian,
                               String description, String cuisine, String spiceLevel,
                               List<String> allergyList, String costPriceRegular, String costPriceSmall,
                               String costPricelarge, String retailPriceRegular, String retailPriceSmall,
                               String retailPricelarge, List<String> dishSize, List<String> diet, String includeInOffer,
                               String calorieRegular, String endDate, String currency, Boolean isDishData) {
        this.dishName = dishName;
        this.status = status;
        this.isVegetarian = isVegetarian;
        this.description = description;
        this.cuisine = cuisine;
        this.spiceLevel = spiceLevel;
        this.allergyList = allergyList;
        this.costPriceRegular = costPriceRegular;
        this.costPriceSmall = costPriceSmall;
        this.costPricelarge = costPricelarge;
        this.retailPriceRegular = retailPriceRegular;
        this.retailPriceSmall = retailPriceSmall;
        this.retailPricelarge = retailPricelarge;
        this.dishSize = dishSize;
        this.includeInOffer = includeInOffer;
        this.diet = diet;
        this.calorieRegular = calorieRegular;
        this.endDate = endDate;
        this.currency = currency;
        this.isDishData = isDishData;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        status = status;
    }

    public String getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(String isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(String spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public List<String> getAllergyList() {
        return allergyList;
    }

    public void setAllergyList(List<String> allergyList) {
        this.allergyList = allergyList;
    }

    public String getCostPriceRegular() {
        return costPriceRegular;
    }

    public void setCostPriceRegular(String costPriceRegular) {
        this.costPriceRegular = costPriceRegular;
    }

    public String getCostPriceSmall() {
        return costPriceSmall;
    }

    public void setCostPriceSmall(String costPriceSmall) {
        this.costPriceSmall = costPriceSmall;
    }

    public String getCostPricelarge() {
        return costPricelarge;
    }

    public void setCostPricelarge(String costPricelarge) {
        this.costPricelarge = costPricelarge;
    }

    public String getRetailPriceRegular() {
        return retailPriceRegular;
    }

    public void setRetailPriceRegular(String retailPriceRegular) {
        this.retailPriceRegular = retailPriceRegular;
    }

    public String getRetailPriceSmall() {
        return retailPriceSmall;
    }

    public void setRetailPriceSmall(String retailPriceSmall) {
        this.retailPriceSmall = retailPriceSmall;
    }

    public String getRetailPricelarge() {
        return retailPricelarge;
    }

    public void setRetailPricelarge(String retailPricelarge) {
        this.retailPricelarge = retailPricelarge;
    }

    public List<String> getDishSize() {
        return dishSize;
    }

    public void setDishSize(List<String> dishSize) {
        this.dishSize = dishSize;
    }

    public List<String> getDiet() {
        return diet;
    }

    public void setDiet(List<String> diet) {
        this.diet = diet;
    }

    public String getIncludeInOffer() {
        return includeInOffer;
    }

    public void setIncludeInOffer(String includeInOffer) {
        this.includeInOffer = includeInOffer;
    }

    public String getCalorieRegular() {
        return calorieRegular;
    }

    public void setCalorieRegular(String calorieRegular) {
        this.calorieRegular = calorieRegular;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String toString(){
        return "name : " + dishName + "\nstatus : " + status ;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getDishData() {
        return isDishData;
    }

    public void setDishData(Boolean dishData) {
        isDishData = dishData;
    }
}

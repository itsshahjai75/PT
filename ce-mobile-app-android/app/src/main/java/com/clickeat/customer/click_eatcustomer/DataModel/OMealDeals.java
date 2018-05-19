package com.clickeat.customer.click_eatcustomer.DataModel;

public class OMealDeals {
    protected String mealId;
    protected String mealName;
    protected String mealDescription;
    protected String mealPrice;
    protected String mealCurrency;
    protected String mealImage;

    public OMealDeals(String mealId, String mealName, String mealDescription, String mealPrice, String mealCurrency, String mealImage) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealDescription = mealDescription;
        this.mealPrice = mealPrice;
        this.mealCurrency = mealCurrency;
        this.mealImage = mealImage;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public String getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }


    public String getMealCurrency() {
        return mealCurrency;
    }

    public void setMealCurrency(String mealCurrency) {
        this.mealCurrency = mealCurrency;
    }

    public String getMealImage() {
        return mealImage;
    }

    public void setMealImage(String mealImage) {
        this.mealImage = mealImage;
    }
}

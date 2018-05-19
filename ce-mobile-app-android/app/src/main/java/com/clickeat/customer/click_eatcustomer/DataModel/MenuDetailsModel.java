package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.List;

/**
 * Created by pivotech on 7/2/18.
 */

public class MenuDetailsModel {
    private String categoryId;
    private String categoryName;
    private List<DishNameDetailModel> dishNameDetails;


    public MenuDetailsModel(String categoryId, String categoryName, List<DishNameDetailModel> dishNameDetails) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.dishNameDetails = dishNameDetails;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<DishNameDetailModel> getDishnameDetails() {
        return dishNameDetails;
    }

    public void setDishnameDetails(List<DishNameDetailModel> dishNameDetails) {
        this.dishNameDetails = dishNameDetails;
    }
}

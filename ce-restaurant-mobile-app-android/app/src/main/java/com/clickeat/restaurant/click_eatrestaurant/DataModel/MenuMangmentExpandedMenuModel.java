package com.clickeat.restaurant.click_eatrestaurant.DataModel;

import com.google.gson.JsonObject;

/**
 * Created by android on 27/3/18.
 */
public class MenuMangmentExpandedMenuModel {

    String iconName = "";
    int iconImg = -1; // menu icon resource id
    String headerId;
    String childId;
    JsonObject headerJson,chiledJson;

    public String getIconName() {
        return iconName;
    }
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    public int getIconImg() {
        return iconImg;
    }
    public void setIconImg(int iconImg) {
        this.iconImg = iconImg;
    }
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    public String getChildId() {
        return childId;
    }
    public void setChildId(String childId) {
        this.childId = childId;
    }


    /*public MenuMangmentExpandedMenuModel(String iconName, int iconImg, String headerId, String childId, JsonObject headerJson, JsonObject chiledJson) {
        this.iconName = iconName;
        this.iconImg = iconImg;
        this.headerId = headerId;
        this.childId = childId;
        this.headerJson = headerJson;
        this.chiledJson = chiledJson;
    }*/

    public JsonObject getHeaderJson() {
        return headerJson;
    }

    public void setHeaderJson(JsonObject headerJson) {
        this.headerJson = headerJson;
    }

    public JsonObject getChiledJson() {
        return chiledJson;
    }

    public void setChiledJson(JsonObject chiledJson) {
        this.chiledJson = chiledJson;
    }
}
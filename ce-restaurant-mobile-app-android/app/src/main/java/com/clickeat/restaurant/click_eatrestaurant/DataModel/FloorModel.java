package com.clickeat.restaurant.click_eatrestaurant.DataModel;

import com.google.gson.JsonObject;

/**
 * Created by android on 16/4/18.
 */

public class FloorModel {
    String id,name;
    JsonObject json;


    public FloorModel(String id, String name, JsonObject json) {
        this.id = id;
        this.name = name;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonObject getJson() {
        return json;
    }

    public void setJson(JsonObject json) {
        this.json = json;
    }
}

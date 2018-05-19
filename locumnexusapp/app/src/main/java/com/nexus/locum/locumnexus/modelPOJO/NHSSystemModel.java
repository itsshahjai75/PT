package com.nexus.locum.locumnexus.modelPOJO;

import com.google.gson.JsonObject;

/**
 * Created by android on 13/4/18.
 */

public class NHSSystemModel {

    String _id;
    String label;
    String id;
    String createdBy;
    String __v;
    JsonObject JsonObj;

    public NHSSystemModel(String _id, String label, String id, String createdBy, String __v, JsonObject jsonObj) {
        this._id = _id;
        this.label = label;
        this.id = id;
        this.createdBy = createdBy;
        this.__v = __v;
        JsonObj = jsonObj;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public JsonObject getJsonObj() {
        return JsonObj;
    }

    public void setJsonObj(JsonObject jsonObj) {
        JsonObj = jsonObj;
    }
}

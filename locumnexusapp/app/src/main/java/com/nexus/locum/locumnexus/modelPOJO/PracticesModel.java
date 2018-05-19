package com.nexus.locum.locumnexus.modelPOJO;

import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by pivotech on 9/10/17.
 */

public class PracticesModel {

    boolean ISprefer;
    String practice_name;
    String practice_code;
    String _id;

    String contact_name;
    String email;
    String contact_number;
    JsonObject practiceJson;

    public PracticesModel(boolean ISprefer, String practice_name, String practice_code, String _id,
                          String contact_name,
                          String email, String contact_number, JsonObject practiceJson) {
        this.ISprefer = ISprefer;
        this.practice_name = practice_name;
        this.practice_code = practice_code;
        this._id = _id;
        this.contact_name = contact_name;
        this.email = email;
        this.contact_number = contact_number;
        this.practiceJson = practiceJson;
    }

    public boolean isISprefer() {
        return ISprefer;
    }

    public void setISprefer(boolean ISprefer) {
        this.ISprefer = ISprefer;
    }

    public String getPractice_name() {
        return practice_name;
    }

    public void setPractice_name(String practice_name) {
        this.practice_name = practice_name;
    }

    public String getPractice_code() {
        return practice_code;
    }

    public void setPractice_code(String practice_code) {
        this.practice_code = practice_code;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }




    public JsonObject getPracticeJson() {
        return practiceJson;
    }

    public void setPracticeJson(JsonObject practiceJson) {
        this.practiceJson = practiceJson;
    }

}

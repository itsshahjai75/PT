package com.clickeat.customer.click_eatcustomer.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pivotech on 28/9/17.
 */

public class SignUpModel {
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("mobile_number")
    @Expose
    public String mobile_number;
    @SerializedName("postcode")
    @Expose
    public String postcode;
    @SerializedName("birth_date")
    @Expose
    public Integer birth_date;
    @SerializedName("birth_month")
    @Expose
    public Integer birth_month;
    @SerializedName("tnc")
    @Expose
    public Boolean tnc;
    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("requestFrom")
    @Expose
    public String requestFrom;

    public SignUpModel(String email, String password, String name, String mobile_number, String postcode,
                       Integer birth_date, Integer birth_month, Boolean tnc, String role, String requestFrom) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.mobile_number = mobile_number;
        this.postcode = postcode;
        this.birth_date = birth_date;
        this.birth_month = birth_month;
        this.tnc = tnc;
        this.role = role;
        this.requestFrom = requestFrom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Integer getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Integer birth_date) {
        this.birth_date = birth_date;
    }

    public Integer getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(Integer birth_month) {
        this.birth_month = birth_month;
    }

    public Boolean getTnc() {
        return tnc;
    }

    public void setTnc(Boolean tnc) {
        this.tnc = tnc;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }
}

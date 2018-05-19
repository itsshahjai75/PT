package com.clickeat.customer.click_eatcustomer.DataModel;

import android.content.Context;

import java.util.List;

/**
 * Created by pivotech on 13/1/18.
 */

public class OUser {

    protected String id;
    protected String name;
    protected String email;
    protected String role;
    protected String mobile;
    protected List<String> favouriteRestaurants;

    public OUser(String id, String name, String email, String mobile, String role, List<String> favouriteRestaurants) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.mobile = mobile;
        this.favouriteRestaurants = favouriteRestaurants;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getFavouriteRestaurants() {
        return favouriteRestaurants;
    }

    public void setFavouriteRestaurants(List<String> favouriteRestaurants) {
        this.favouriteRestaurants = favouriteRestaurants;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

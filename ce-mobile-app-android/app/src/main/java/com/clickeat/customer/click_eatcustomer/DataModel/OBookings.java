package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pivotech on 28/2/18.
 */

public class OBookings {
    protected String id;
    protected String bookingDate;
    protected Integer numberofPeople;
    protected String customerName;
    protected String customerPhone;
    protected String customerEmail;
    protected String bookingStartTime;
    protected String restaurant_id;
    protected String restaurantName;
    protected String userid;
    protected String bookingEndTime;
    protected String status;
    protected List<OTable> tableList;
   /* protected Integer floorNo;
    protected Integer roomNo;
    protected String tableId;
    protected Integer tableCapacity;
    protected Integer tableNum;*/

    public OBookings(String id, String bookingDate, Integer numberofPeople, String customerName,
                     String customerPhone, String customerEmail, String bookingStartTime,
                     String restaurant_id, String restaurantName, String userid, String bookingEndTime,
                     String status, List<OTable> tableData) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.numberofPeople = numberofPeople;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.bookingStartTime = bookingStartTime;
        this.restaurant_id = restaurant_id;
        this.restaurantName = restaurantName;
        this.userid = userid;
        this.bookingEndTime = bookingEndTime;
        this.status = status;
        this.tableList = tableData;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getNumberofPeople() {
        return numberofPeople;
    }

    public void setNumberofPeople(Integer numberofPeople) {
        this.numberofPeople = numberofPeople;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getBookingStartTime() {
        return bookingStartTime;
    }

    public void setBookingStartTime(String bookingStartTime) {
        this.bookingStartTime = bookingStartTime;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBookingEndTime() {
        return bookingEndTime;
    }

    public void setBookingEndTime(String bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OTable> getTableList() {
        return tableList;
    }

    public void setTableList(List<OTable> tableList) {
        this.tableList = tableList;
    }
}

package com.clickeat.restaurant.click_eatrestaurant.DataModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by pivotech on 9/10/17.
 */

public class AwaitingUpcommingBookTable {

    String bookingId;
    String date;
    String name;
    String email;
    String mobileNo;
    String tableNo;
    String numPeople;
    String bookingTimeTo,bookingTimeFrom;
    JsonArray table_dataJson;
    JsonObject MainObjData;

    public AwaitingUpcommingBookTable(String bookingId, String date, String name, String email, String mobileNo, String tableNo, String numPeople, String bookingTimeTo, String bookingTimeFrom, JsonArray table_dataJson, JsonObject mainObjData) {
        this.bookingId = bookingId;
        this.date = date;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.tableNo = tableNo;
        this.numPeople = numPeople;
        this.bookingTimeTo = bookingTimeTo;
        this.bookingTimeFrom = bookingTimeFrom;
        this.table_dataJson = table_dataJson;
        MainObjData = mainObjData;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(String numPeople) {
        this.numPeople = numPeople;
    }

    public String getBookingTimeTo() {
        return bookingTimeTo;
    }

    public void setBookingTimeTo(String bookingTimeTo) {
        this.bookingTimeTo = bookingTimeTo;
    }

    public String getBookingTimeFrom() {
        return bookingTimeFrom;
    }

    public void setBookingTimeFrom(String bookingTimeFrom) {
        this.bookingTimeFrom = bookingTimeFrom;
    }

    public JsonArray getTable_dataJson() {
        return table_dataJson;
    }

    public void setTable_dataJson(JsonArray table_dataJson) {
        this.table_dataJson = table_dataJson;
    }

    public JsonObject getMainObjData() {
        return MainObjData;
    }

    public void setMainObjData(JsonObject mainObjData) {
        MainObjData = mainObjData;
    }
}

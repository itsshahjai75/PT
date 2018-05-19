package com.clickeat.restaurant.click_eatrestaurant.DataModel;

import com.google.gson.JsonObject;

/**
 * Created by android on 17/4/18.
 */

public class UnavailableTablesModel {

    String _id ;
    String endTime ;
    String startTime ;
    String unavailableTableReason ;
    String endTimeSlot ;
    String startTimeSlot ;
    String endDate ;
    String startDate ;
    JsonObject table_data ;
    JsonObject mainJson;

    public UnavailableTablesModel(String _id, String endTime, String startTime, String unavailableTableReason, String endTimeSlot, String startTimeSlot,
                                  String endDate, String startDate, JsonObject table_data, JsonObject mainJson) {
        this._id = _id;
        this.endTime = endTime;
        this.startTime = startTime;
        this.unavailableTableReason = unavailableTableReason;
        this.endTimeSlot = endTimeSlot;
        this.startTimeSlot = startTimeSlot;
        this.endDate = endDate;
        this.startDate = startDate;
        this.table_data = table_data;
        this.mainJson = mainJson;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUnavailableTableReason() {
        return unavailableTableReason;
    }

    public void setUnavailableTableReason(String unavailableTableReason) {
        this.unavailableTableReason = unavailableTableReason;
    }

    public String getEndTimeSlot() {
        return endTimeSlot;
    }

    public void setEndTimeSlot(String endTimeSlot) {
        this.endTimeSlot = endTimeSlot;
    }

    public String getStartTimeSlot() {
        return startTimeSlot;
    }

    public void setStartTimeSlot(String startTimeSlot) {
        this.startTimeSlot = startTimeSlot;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public JsonObject getTable_data() {
        return table_data;
    }

    public void setTable_data(JsonObject table_data) {
        this.table_data = table_data;
    }

    public JsonObject getMainJson() {
        return mainJson;
    }

    public void setMainJson(JsonObject mainJson) {
        this.mainJson = mainJson;
    }
}

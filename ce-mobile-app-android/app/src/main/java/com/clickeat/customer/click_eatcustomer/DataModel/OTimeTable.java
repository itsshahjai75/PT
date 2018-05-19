package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 29/12/17.
 */

public class OTimeTable {
    protected String day;
    protected String time;

    public OTimeTable(String day, String time) {
        this.day = day;
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

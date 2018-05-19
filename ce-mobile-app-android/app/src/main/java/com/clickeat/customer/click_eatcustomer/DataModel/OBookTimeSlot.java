package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 26/2/18.
 */

public class OBookTimeSlot {
    protected String time;
    protected String status;

    public OBookTimeSlot(String time, String status) {
        this.time = time;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.clickeat.restaurant.click_eatrestaurant.DataModel;

/**
 * Created by pivotech on 21/3/18.
 */

public class TimeSlots {
    private String timeslot;
    private String available;

    public TimeSlots(String timeslot, String available) {
        this.timeslot = timeslot;
        this.available = available;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
